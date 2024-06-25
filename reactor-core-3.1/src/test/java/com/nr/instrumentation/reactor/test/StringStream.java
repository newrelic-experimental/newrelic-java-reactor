package com.nr.instrumentation.reactor.test;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class StringStream implements Stream<String> {

	@Override
	public Iterator<String> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Spliterator<String> spliterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isParallel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Stream<String> sequential() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<String> parallel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<String> unordered() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<String> onClose(Runnable closeHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Stream<String> filter(Predicate<? super String> predicate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> Stream<R> map(Function<? super String, ? extends R> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super String> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super String> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super String> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super String, ? extends Stream<? extends R>> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntStream flatMapToInt(Function<? super String, ? extends IntStream> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongStream flatMapToLong(Function<? super String, ? extends LongStream> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super String, ? extends DoubleStream> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<String> distinct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<String> sorted() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<String> sorted(Comparator<? super String> comparator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<String> peek(Consumer<? super String> action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<String> limit(long maxSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<String> skip(long n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forEach(Consumer<? super String> action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forEachOrdered(Consumer<? super String> action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String reduce(String identity, BinaryOperator<String> accumulator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<String> reduce(BinaryOperator<String> accumulator) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super String, U> accumulator, BinaryOperator<U> combiner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super String> accumulator, BiConsumer<R, R> combiner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R, A> R collect(Collector<? super String, A, R> collector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<String> min(Comparator<? super String> comparator) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<String> max(Comparator<? super String> comparator) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean anyMatch(Predicate<? super String> predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allMatch(Predicate<? super String> predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean noneMatch(Predicate<? super String> predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<String> findFirst() {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<String> findAny() {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

}
