/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.sidecar.agent;

import java.lang.instrument.ClassFileTransformer;

import java.security.ProtectionDomain;

import java.util.Map;
import java.util.function.Function;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Dante Wang
 */
public class SidecarClassFileTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(
		ClassLoader loader, String className, Class<?> classBeingRedefined,
		ProtectionDomain protectionDomain, byte[] classFileBuffer) {

		Map.Entry<String, Function<MethodVisitor, MethodVisitor>> entry =
			_methodVisitorFunctions.get(className);

		if (entry == null) {
			return null;
		}

		return ClassModificationUtil.getModifiedClassBytes(
			classFileBuffer, entry.getKey(), entry.getValue());
	}

	private static final String _DEFAULT_MODULES_FOLDER_NAME = "modules";

	private static final String _SIDECAR_MODULES_FOLDER_NAME =
		"liferay-sidecar-modules";

	private static final Map
		<String, Map.Entry<String, Function<MethodVisitor, MethodVisitor>>>
			_methodVisitorFunctions;

	private static final Function<MethodVisitor, MethodVisitor>
		_wipingLogicMethodVisitorFunction =
			methodVisitor -> new MethodVisitor(Opcodes.ASM7) {

				@Override
				public void visitCode() {
					methodVisitor.visitCode();
					methodVisitor.visitInsn(Opcodes.RETURN);
				}

				@Override
				public void visitMaxs(int maxStack, int maxLocals) {
					methodVisitor.visitMaxs(0, 0);
				}

			};

	static {
		_methodVisitorFunctions = Map.of(
			"org/elasticsearch/entitlement/bootstrap/EntitlementBootstrap",
			Map.entry("bootstrap", _wipingLogicMethodVisitorFunction),
			"org/elasticsearch/nativeaccess/PosixNativeAccess",
			Map.entry(
				"definitelyRunningAsRoot",
				methodVisitor -> new MethodVisitor(Opcodes.ASM7) {

					@Override
					public void visitCode() {
						methodVisitor.visitCode();
						methodVisitor.visitInsn(Opcodes.ICONST_0);
						methodVisitor.visitInsn(Opcodes.IRETURN);
					}

					@Override
					public void visitMaxs(int maxStack, int maxLocals) {
						methodVisitor.visitMaxs(0, 0);
					}

				}),
			"org/elasticsearch/bootstrap/Bootstrap",
			Map.entry("sendCliMarker", _wipingLogicMethodVisitorFunction),
			"org/elasticsearch/bootstrap/Elasticsearch",
			Map.entry(
				"startCliMonitorThread", _wipingLogicMethodVisitorFunction),
			"org/elasticsearch/bootstrap/Elasticsearch$EntitlementSelfTester",
			Map.entry("entitlementSelfTest", _wipingLogicMethodVisitorFunction),
			"org/elasticsearch/common/settings/KeyStoreWrapper",
			Map.entry("save", _wipingLogicMethodVisitorFunction),
			"org/elasticsearch/bootstrap/Security",
			Map.entry("configure", _wipingLogicMethodVisitorFunction),
			"org/elasticsearch/bootstrap/Spawner",
			Map.entry(
				"spawnNativeControllers", _wipingLogicMethodVisitorFunction),
			"org/elasticsearch/env/Environment",
			Map.entry(
				"<init>",
				methodVisitor -> new MethodVisitor(
					Opcodes.ASM7, methodVisitor) {

					@Override
					public void visitLdcInsn(Object value) {
						if ((value instanceof String) &&
							value.equals(_DEFAULT_MODULES_FOLDER_NAME)) {

							methodVisitor.visitLdcInsn(
								_SIDECAR_MODULES_FOLDER_NAME);
						}
						else {
							methodVisitor.visitLdcInsn(value);
						}
					}

				}));
	}

}