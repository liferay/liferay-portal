/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.engine;

import com.liferay.petra.reflect.AnnotationLocator;
import com.liferay.portal.kernel.template.TemplateRestricted;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Method;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tina Tian
 */
public class TemplateRestrictedUtil {

	public static Set<String> getRestrictedMethodNames(
		Class<?> clazz, Set<String> restrictedMethodNames) {

		Set<String> methodNames = _classValue.get(clazz);

		if ((methodNames == null) || (restrictedMethodNames == null)) {
			return methodNames;
		}

		if (methodNames.isEmpty()) {
			return restrictedMethodNames;
		}

		methodNames = new HashSet<>(methodNames);

		methodNames.addAll(restrictedMethodNames);

		return methodNames;
	}

	public static boolean isRestricted(Class<?> clazz) {
		if (_classValue.get(clazz) == null) {
			return true;
		}

		return false;
	}

	private static final ClassValue<Set<String>> _classValue =
		new ClassValue<>() {

			@Override
			protected Set<String> computeValue(Class<?> clazz) {
				TemplateRestricted templateRestricted =
					AnnotationLocator.locate(clazz, TemplateRestricted.class);

				if (templateRestricted != null) {
					return null;
				}

				Set<String> methodNames = new HashSet<>();

				for (Method method : clazz.getMethods()) {
					templateRestricted = AnnotationLocator.locate(
						method, clazz, TemplateRestricted.class);

					if (templateRestricted == null) {
						continue;
					}

					methodNames.add(StringUtil.toLowerCase(method.getName()));
				}

				if (methodNames.isEmpty()) {
					return Collections.emptySet();
				}

				return Collections.unmodifiableSet(methodNames);
			}

		};

}