package com.example.reactive.chapter2.subject;

import com.example.reactive.chapter2.observer.Observer;

public interface Subject<T> {
    void registerObserver(Observer<T> observer);

    void unregisterObserver(Observer<T> observer);

    void notifyObservers(T event);
}