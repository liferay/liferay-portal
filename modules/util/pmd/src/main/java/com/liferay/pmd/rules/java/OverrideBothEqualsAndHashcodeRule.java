/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.pmd.rules.java;

import java.lang.reflect.Field;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 * @author Shuyang Zhou
 */
public class OverrideBothEqualsAndHashcodeRule
	extends net.sourceforge.pmd.lang.java.rule.basic.
				OverrideBothEqualsAndHashcodeRule {

	@Override
	public Object visit(ASTImplementsList astImplementsList, Object data) {
		for (int i = 0; i < astImplementsList.jjtGetNumChildren(); i++) {
			Node node = astImplementsList.jjtGetChild(i);

			if (!(node instanceof ASTClassOrInterfaceType)) {
				continue;
			}

			ASTClassOrInterfaceType astClassOrInterfaceType =
				(ASTClassOrInterfaceType)node;

			if ((astClassOrInterfaceType.getType() != null) &&
				node.hasImageEqualTo("Comparable")) {

				try {
					_implementsComparableField.set(this, true);
				}
				catch (ReflectiveOperationException
							reflectiveOperationException) {

					throw new RuntimeException(reflectiveOperationException);
				}

				return data;
			}
		}

		return visit((JavaNode)astImplementsList, data);
	}

	private static final Field _implementsComparableField;

	static {
		Class<?> clazz = OverrideBothEqualsAndHashcodeRule.class;

		clazz = clazz.getSuperclass();

		try {
			_implementsComparableField = clazz.getDeclaredField(
				"implementsComparable");

			_implementsComparableField.setAccessible(true);
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

}