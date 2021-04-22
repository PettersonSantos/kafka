package br.com.petterson.ecommerce.consumer;

public interface ServiceFactory<T> {
    ConsumerService<T> create() throws Exception;
}
