package com.nr.instrumentation.reactor;

/**
 * Used to initialize ReactorUtils which needs to be started when the application starts
 * 
 * @author dhilpipre
 *
 */
public class NRReactorPremain {
	
	public static void premain(String[] args) {
		if(!ReactorUtils.initialized) {
			ReactorUtils.initialize();
		}
	}

}
