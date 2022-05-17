package com.joaovictor.libraryapi.config;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library API")
                        .description("API do projeto de Controle de aluguel de livros")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Projeto no GitHub")
                        .url("https://github.com/jvfranco"));
    }

//    @Bean
//    public Docket docket() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.joaovictor.libraryapi.api.resource"))
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(apiInfo());
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("Library API")
//                .description("API do projeto de Controle de aluguel de livros")
//                .version("1.0")
//                .contact(contact())
//                .build();
//    }
//
//    private Contact contact() {
//        return new Contact("Joao Victor", "https://github.com/jvfranco", "joaovictor@email.com");
//    }
}
