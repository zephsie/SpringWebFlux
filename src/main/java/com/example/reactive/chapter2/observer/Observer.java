package com.example.reactive.chapter2.observer;

public interface Observer<T> {
    void observe(T event);
}