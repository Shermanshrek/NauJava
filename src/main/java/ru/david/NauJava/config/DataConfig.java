package ru.david.NauJava.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.david.NauJava.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataConfig {

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public List<Transaction> transactionContainer(){
        return new ArrayList<>();
    }
}
