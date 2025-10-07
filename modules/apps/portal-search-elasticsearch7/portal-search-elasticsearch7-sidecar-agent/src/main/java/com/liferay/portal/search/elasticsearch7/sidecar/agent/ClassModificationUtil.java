/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.sidecar.agent;

import java.util.function.Function;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Tina Tian
 * @author André de Oliveira
 */
public class ClassModificationUtil {

	public static byte[] getModifiedClassBytes(
		byte[] classBytes, String methodName,
		Function<MethodVisitor, MethodVisitor> methodVisitorFunction) {

		ClassReader classReader = new ClassReader(classBytes);

		ClassWriter classWriter = new ClassWriter(
			classReader, ClassWriter.COMPUTE_MAXS);

		classReader.accept(
			new ClassVisitor(Opcodes.ASM7, classWriter) {

				@Override
				public MethodVisitor visitMethod(
					int access, String name, String description,
					String signature, String[] exceptions) {

					MethodVisitor methodVisitor = super.visitMethod(
						access, name, description, signature, exceptions);

					if (!name.equals(methodName)) {
						return methodVisitor;
					}

					return methodVisitorFunction.apply(methodVisitor);
				}

			},
			0);

		return classWriter.toByteArray();
	}

}