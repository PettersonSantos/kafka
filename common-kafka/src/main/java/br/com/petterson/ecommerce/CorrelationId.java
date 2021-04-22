package br.com.petterson.ecommerce;

import java.util.UUID;

public class CorrelationId {
    private final String id;

    public CorrelationId(String title) {
        this.id = title + "(" + UUID.randomUUID().toString() + ")";
    }

    @Override
    public String toString() {
        return "CorrelationId{" +
                "id='" + id + '\'' +
                '}';
    }

    public CorrelationId continueWith(String title){
        return new CorrelationId(id + "-" + title);
    }
}
