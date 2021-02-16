package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import java.util.logging.Level;

import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.weave.utils.WeaveUtils;

public class ReactorClassMatcherVisitorFactory implements ClassMatchVisitorFactory {

	@Override
	public ClassVisitor newClassMatchVisitor(ClassLoader loader, Class<?> classBeingRedefined, ClassReader reader,ClassVisitor cv, InstrumentationContext context) {
		NewRelic.getAgent().getLogger().log(Level.FINE, "Call to ReactorClassMatcherVisitorFactorynewClassMatchVistor({0},{1},{2},{3},{4})", loader, classBeingRedefined,reader,cv,context);
		return new ClassVisitor(WeaveUtils.ASM_API_LEVEL, cv) {

			@Override
			public void visit(int version, int access, String name, String signature, String superName,String[] interfaces) {
				NewRelic.getAgent().getLogger().log(Level.FINE, "Call to ReactorClassVisitor.visit({0},{1},{2},{3},{4},{5})", version, access,name,signature,superName,interfaces);
				super.visit(version, access, name, signature, superName, interfaces);
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,String[] exceptions) {
				NewRelic.getAgent().getLogger().log(Level.FINE, "Call to ReactorClassVisitor.visitMethod({0},{1},{2},{3},{4},{5})", access,name,descriptor,signature,exceptions);
				return super.visitMethod(access, name, descriptor, signature, exceptions);
			}
			
			
		};
	}

}
