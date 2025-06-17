/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.jakarta.ee.transformer;

import com.liferay.portal.tools.jakarta.ee.transformer.function.ClassRemapperBiFunction;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class TransformerAgent {

	public static final Map<String, String> replacementDashDotMap =
		new LinkedHashMap<>();
	public static final Map<String, String> replacementSlashMap =
		new LinkedHashMap<>();
	public static final Map<String, String> reverseReplacementDashDotMap =
		new HashMap<>();

	public static void premain(
		String agentArguments, Instrumentation instrumentation) {

		instrumentation.addTransformer(
			new ClassFileTransformer() {

				@Override
				public byte[] transform(
						ClassLoader classLoader, String className,
						Class<?> classBeingRedefined,
						ProtectionDomain protectionDomain,
						byte[] classfileBuffer)
					throws IllegalClassFormatException {

					if (className.startsWith("org/glowroot/agent/")) {
						return classfileBuffer;
					}

					CodeSource codeSource = protectionDomain.getCodeSource();

					return ClassRemapperBiFunction.INSTANCE.apply(
						"ClassFileTransformer#" +
							String.valueOf(codeSource.getLocation()) + "^" +
								className,
						classfileBuffer);
				}

			});
	}

	public static String replace(
		Map<String, String> replacementMap, String value) {

		for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
			value = value.replace(entry.getKey(), entry.getValue());
		}

		return value;
	}

	private static final Set<String> _preservedSubpackageNames = new HashSet<>(
		Arrays.asList("annotation.processing", "transaction.xa"));
	private static final Set<String> _subpackageNames = new HashSet<>(
		Arrays.asList(
			"activation", "annotation", "batch", "decorator", "ejb", "el",
			"enterprise", "faces", "inject", "interceptor", "jms", "json",
			"jws", "mail", "mvc", "persistence", "portlet", "resource",
			"security.auth.message", "security.enterprise", "security.jacc",
			"servlet", "transaction", "validation", "websocket", "ws.rs",
			"xml.bind", "xml.soap", "xml.ws"));

	static {
		_subpackageNames.forEach(
			subpackageName -> {
				String javaxPackage = "javax." + subpackageName;
				String jakartaPackage = "jakarta." + subpackageName;

				replacementDashDotMap.put(
					javaxPackage.replace('.', '-'),
					jakartaPackage.replace('.', '-'));
				replacementDashDotMap.put(javaxPackage, jakartaPackage);
				replacementSlashMap.put(
					javaxPackage.replace('.', '/'),
					jakartaPackage.replace('.', '/'));
				reverseReplacementDashDotMap.put(
					jakartaPackage.replace('.', '-'),
					javaxPackage.replace('.', '-'));
				reverseReplacementDashDotMap.put(jakartaPackage, javaxPackage);
			});

		// Order matters, preserved subpackage names need to be put into
		// replacement map later

		_preservedSubpackageNames.forEach(
			preservedSubpackageName -> {
				String preservedJavaxPackage =
					"javax." + preservedSubpackageName;
				String preservedJakartaPackage =
					"jakarta." + preservedSubpackageName;

				replacementDashDotMap.put(
					preservedJakartaPackage.replace('.', '-'),
					preservedJavaxPackage.replace('.', '-'));
				replacementDashDotMap.put(
					preservedJakartaPackage, preservedJavaxPackage);
				replacementSlashMap.put(
					preservedJakartaPackage.replace('.', '/'),
					preservedJavaxPackage.replace('.', '/'));
			});

		replacementDashDotMap.put(
			"X-JAVAX-PORTLET-NAMESPACED-RESPONSE",
			"X-JAKARTA-PORTLET-NAMESPACED-RESPONSE");
	}

}