package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import java.util.Collection;
import java.util.Collections;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;

/**
 * Class matcher that matches any class that doesn't belong to reactor.core or its subpackages
 * 
 * @author dhilpipre
 *
 */
public class ReactorClassMatcher extends ClassMatcher {

	@Override
	public Collection<String> getClassNames() {
		return Collections.emptyList();	
	}

	@Override
	public boolean isMatch(Class<?> clazz) {
		if(clazz.isAnnotation()) return false;
		
		Package classPackage = clazz.getPackage();
		boolean b = !classPackage.getName().startsWith("reactor.core") && !classPackage.getName().startsWith("reactor.util");
		return b;
	}

	@Override
	public boolean isMatch(ClassLoader loader, ClassReader cr) {
        if (loader == null) {
            loader = AgentBridge.getAgent().getClass().getClassLoader();
        }
 		String className = cr.getClassName();
 		
		boolean b = !className.startsWith("reactor/core") && !className.startsWith("reactor/util");
		return b;
	}

}
