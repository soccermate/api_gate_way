package com.example.grad_api_gateway.filters;

import com.example.grad_api_gateway.filters.utils.Logger;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
    private static final String LOGGER = "logger";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {




        return chain.filter(exchange.mutate()
                .request(getMutatedRequest(exchange.getRequest()))
                .response(getMutatedResponse(exchange.getResponse()))
                .build());
    }

    private ServerHttpRequestDecorator getMutatedRequest(ServerHttpRequest request)
    {

        return new ServerHttpRequestDecorator(request){
            @Override
            public Flux<DataBuffer> getBody() {
                Logger logger = new Logger(request);

                if(Logger.isLoggable(request))
                    return super.getBody()
                            .doOnNext(dataBuffer -> {
                                logger.appendBody(dataBuffer.asByteBuffer());
                            })
                            .doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release)
                            .doOnComplete(()->{
                                logger.log();
                            });

                return super.getBody()
                        .doOnComplete(() -> {
                            logger.appendBody("cannot log request body due to unsupported content type");
                            logger.log();
                        });
            }
        };
    }

    private ServerHttpResponseDecorator getMutatedResponse(ServerHttpResponse response)
    {
        return new ServerHttpResponseDecorator(response){

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body)
            {
                Logger logger = new Logger(response);


                return getDelegate().writeWith(appendLoggingLogic(body, logger));


            }
        };
    }


    private Publisher<? extends DataBuffer> appendLoggingLogic(Publisher<? extends DataBuffer> dataBuffer, Logger logger)
    {
        if(dataBuffer == null)
        {
            return null;
        }

        return Flux.from(dataBuffer)
                .doOnNext((db) ->{
                    logger.appendBody(db.asByteBuffer());
                })
                .doOnComplete(()->{
                    logger.log();
                })
                .doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);   //cast to PooledDataBuffer.class and call the DataBufferUtils#release method
    }

    /*
    //this is another way to log response
    private Mono<? extends DataBuffer> join(Publisher<? extends DataBuffer> dataBuffer)
    {
        if(dataBuffer == null)
        {
            return null;
        }

        return Flux.from(dataBuffer)
                .collectList()
                .map(dss -> {
                    return DefaultDataBufferFactory.sharedInstance.join(dss);
                })
                .doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
    }

     */

    @Override
    public int getOrder() {
        return -2;
    }


}
