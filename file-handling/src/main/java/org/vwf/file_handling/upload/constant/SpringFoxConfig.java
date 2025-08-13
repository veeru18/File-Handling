package org.vwf.file_handling.upload.constant;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
@EnableWebMvc
public class SpringFoxConfig implements WebMvcConfigurer {

    @Bean
    public Docket adminApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .paths(PathSelectors.ant("/**"))
                .apis(RequestHandlerSelectors.any())
                .build().apiInfo(apiDetails())
                .groupName("ADMIN_USER");
    }

    private ApiInfo apiDetails() {
        return new ApiInfo("VFile Handling",
                "VFile Handling API details",
                "1.0", "www.wecodee.com",
                contact(),
                "", "", Collections.emptyList());
    }

    private Contact contact() {
        return new Contact(
                "Veeresh",
                "https://github.com/veeru18",
                "veeresh.ta@gmail.com");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}

