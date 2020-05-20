package com.baek.app.searchbook.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class ApplicationConfig {

    @Bean(name = "kakaoTemplate")
    public RestTemplate kakaoTemplate() {
        return new RestTemplate() {{
            new HttpComponentsClientHttpRequestFactory().setHttpClient(
                    HttpClientBuilder.create()
                            .setConnectionManager(new PoolingHttpClientConnectionManager() {{
                                setDefaultMaxPerRoute(10);
                                setMaxTotal(30);
                            }}).build()
            );
        }};
    }

    @Bean(name = "naverTemplate")
    public RestTemplate naverTemplate() {
        return new RestTemplate() {{
            new HttpComponentsClientHttpRequestFactory().setHttpClient(
                    HttpClientBuilder.create()
                            .setConnectionManager(new PoolingHttpClientConnectionManager() {{
                                setDefaultMaxPerRoute(10);
                                setMaxTotal(30);
                            }}).build()
            );
        }};
    }

}
