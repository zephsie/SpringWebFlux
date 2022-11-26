package com.example.reactive.chapter2.subject;

import com.example.reactive.chapter2.observer.Observer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcreteSubject implements Subject<String> {
    private final Set<Observer<String>> observers = new CopyOnWriteArraySet<>();

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void registerObserver(Observer<String> observer) {
        observers.add(observer);
    }

    public void unregisterObserver(Observer<String> observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String event) {
        observers.forEach(observer -> executorService.submit(() -> observer.observe(event)));
    }
}