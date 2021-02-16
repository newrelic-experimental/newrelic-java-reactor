package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import java.lang.reflect.Method;

import com.newrelic.agent.deps.org.objectweb.asm.Type;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

public class Test {

	public static void main(String[] args) {
		Test test = new Test();
		try {
			test.process();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void process() throws NoSuchMethodException, SecurityException {
		Class<?> monoClass = Mono.class;
		ReactorClassMatcher classMatcher = new ReactorClassMatcher();
		
		boolean b = classMatcher.isMatch(monoClass);
		System.out.println("Matcher result for Mono is : "+b);
		
		Class<?> fluxClass = Flux.class;
		b = classMatcher.isMatch(fluxClass);
		System.out.println("Matcher result for Flux is : "+b);
		
		Class<?> testClass = getClass();
		b = classMatcher.isMatch(testClass);
		System.out.println("Matcher result for Test is : "+b);
		
		ReactorReturnTypeMatcher  returnMatcher = new ReactorReturnTypeMatcher();
		
		Method method = testClass.getDeclaredMethod("createMono", new Class<?>[] {});
		
		
		Type methodType = Type.getType(method);
		
		
		b = returnMatcher.matches(0, "Test", methodType.getDescriptor(), null);
		System.out.println("Matcher result for createMono is : "+b);
		
		Method method2 = testClass.getDeclaredMethod("createProcessor", new Class<?>[] {});
		
		
		Type methodType2 = Type.getType(method2);
		
		
		b = returnMatcher.matches(0, "Test", methodType2.getDescriptor(), null);
		System.out.println("Matcher result for createProcessor is : "+b);

		Method method3 = testClass.getDeclaredMethod("createFlux", new Class<?>[] {});
		
		
		Type methodType3 = Type.getType(method3);
		
		
		b = returnMatcher.matches(0, "Test", methodType3.getDescriptor(), null);
		System.out.println("Matcher result for createFlux is : "+b);
	}
	
	public Mono<String> createMono() {
		Mono<String> mono = Mono.just("Hello");
		return mono;
	}
	
	public MonoProcessor<String> createProcessor() {
		Mono<String> mono = Mono.just("Hello");
		return mono.toProcessor();
	}
	
	public Flux<String> createFlux() {
		Flux<String> flux = Flux.just("Hello"," ","World");
		return flux;
	}
	
	
}
