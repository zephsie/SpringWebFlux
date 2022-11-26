package com.example.reactive.chapter2.test;

import com.example.reactive.chapter2.subject.ConcreteSubject;
import com.example.reactive.chapter2.subject.Subject;

public class Main {
    public static void main(String[] args) {
        Subject<String> subject = new ConcreteSubject();

        subject.registerObserver(event -> System.out.println("Observer A: " + event));
        subject.registerObserver(event -> System.out.println("Observer B: " + event));
        subject.registerObserver(event -> System.out.println("Observer C: " + event));

        subject.notifyObservers("Hello World!");
    }
}