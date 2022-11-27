package com.example.reactive.chapter2.test;

import com.example.reactive.chapter2.subject.ConcreteSubject;
import com.example.reactive.chapter2.subject.Subject;
import com.example.reactive.util.PersonGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Subject<String> subject = new ConcreteSubject();

        subject.registerObserver(event -> System.out.println("Observer A: " + event));
        subject.registerObserver(event -> System.out.println("Observer B: " + event));
        subject.registerObserver(event -> System.out.println("Observer C: " + event));

        subject.notifyObservers("Hello World!");

        Observable<String> observable1 = Observable.create(
                sub -> {
                    sub.onNext("Hello from Observable 1");
                    sub.onCompleted();
                }
        );

        Subscriber<String> subscriber = new Subscriber<>() {
            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onCompleted() {
                System.out.println("Done!");
            }

            @Override
            public void onError(Throwable e) {
                System.err.println("Error: " + e.getMessage());
            }
        };

        observable1.subscribe(subscriber);

        CountDownLatch latch = new CountDownLatch(10);

        Subscription subscription = Observable.interval(100, TimeUnit.MILLISECONDS)
                .subscribe(i -> {
                    System.out.println("Received: " + i);
                    latch.countDown();
                });

        latch.await();

        subscription.unsubscribe();

        Observable.zip(
                Observable.just("Hello"),
                Observable.just("World"),
                (s1, s2) -> s1 + " " + s2
        ).subscribe(System.out::println);

        Flux.just("Hello", "World")
                .map(s -> s + "!")
                .subscribe(System.out::println);

        Mono<Integer> mono = Mono.just(1);

        Flux<Integer> flux = Flux.just(1, 2, 3);

        Flux<Integer> fluxFromMono = mono.flux();

        Mono<Boolean> monoFromFlux = flux.any(i -> i == 2);

        Mono<Integer> integerMono = flux.elementAt(1);

        Flux.range(1, 10)
                .filter(i -> i % 2 == 0)
                .map(i -> i * 2)
                .subscribe(System.out::println);

        PersonGenerator personGenerator = new PersonGenerator();

        Flux.fromIterable(Flux.range(1, 10)
                        .map(i -> personGenerator.createRandomPersonDTO())
                        .toIterable())
                .subscribe(personDTO -> System.out.println(personDTO.getName()));

        Flux.<String>generate(sink -> sink.next("Hello"))
                .delayElements(Duration.ofMillis(1000))
                .take(10)
                .subscribe(System.out::println);

        Flux.<String>generate(sink -> sink.next("Hello"))
                .take(10);

        Flux.generate(
                () -> 666,
                (state, sink) -> {
                    sink.next("State: " + state);

                    if (state == 669) {
                        sink.complete();
                    }

                    return state + 1;
                }
        ).subscribe(System.out::println);

        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }

            sink.complete();
        }).subscribe(System.out::println);

        Flux.push(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }

            sink.complete();
        }).subscribe(System.out::println);

        Flux<String> first = Flux.just("Hello1", "Hello2", "Hello3");
        Flux<String> second = Flux.just("World1", "World2", "World3");

        Flux.zip(first, second, (s1, s2) -> s1 + " " + s2)
                .subscribe(System.out::println);

        Flux<String> first2 = Flux.just("Hello1", "Hello2", "Hello3");
        Flux<String> second2 = Flux.just("World1", "World2", "World3");

        Flux.merge(first2, second2)
                .subscribe(System.out::println);

        Flux<String> sum = Flux.just("Hello1", "Hello2", "Hello3")
                .zipWith(Flux.just("World1", "World2", "World3"), (s1, s2) -> s1 + " " + s2);

        sum
                .delayElements(Duration.ofMillis(3000))
                .timeout(Duration.ofMillis(2000))
                .onErrorResume(e -> Flux.just("Error"))
                .subscribe(System.out::println);

        Flux<String> sum2 = Flux.just("Hello1", "Hello2", "Hello3")
                .zipWith(Flux.just("World1", "World2", "World3"), (s1, s2) -> s1 + " " + s2);

        sum2
                .delayElements(Duration.ofMillis(100))
                .timeout(Duration.ofMillis(2000))
                .subscribe(
                        System.out::println,
                        (e) -> System.out.println("Error"),
                        () -> System.out.println("Done")
                );
    }
}