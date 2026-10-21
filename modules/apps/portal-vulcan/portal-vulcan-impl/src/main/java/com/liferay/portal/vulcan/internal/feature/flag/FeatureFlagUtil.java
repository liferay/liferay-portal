/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.feature.flag;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.feature.flag.FeatureFlag;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daniel Raposo
 * @author Carlos Correa
 */
public class FeatureFlagUtil {

	public static String getFeatureFlagKey(Class<?> clazz) {
		while (clazz != null) {
			FeatureFlag featureFlag = clazz.getAnnotation(FeatureFlag.class);

			if (featureFlag != null) {
				return featureFlag.value();
			}

			clazz = clazz.getEnclosingClass();
		}

		return null;
	}

	public static String getFeatureFlagKey(Method method) {
		Map<Method, String> featureFlagKeys = _featureFlagKeysClassValue.get(
			method.getDeclaringClass());

		String featureFlagKey = featureFlagKeys.computeIfAbsent(
			method, FeatureFlagUtil::_getFeatureFlagKey);

		if (featureFlagKey.isEmpty()) {
			return null;
		}

		return featureFlagKey;
	}

	public static boolean isEnabled(long companyId, Class<?> clazz) {
		return _isEnabled(companyId, getFeatureFlagKey(clazz));
	}

	public static boolean isEnabled(long companyId, Method method) {
		return _isEnabled(companyId, getFeatureFlagKey(method));
	}

	private static String _getFeatureFlagKey(Method method) {
		FeatureFlag featureFlag = method.getAnnotation(FeatureFlag.class);

		if (featureFlag != null) {
			return featureFlag.value();
		}

		Class<?> declaringClass = method.getDeclaringClass();

		Class<?> superclass = declaringClass.getSuperclass();

		while (superclass != null) {
			for (Method declaredMethod : superclass.getDeclaredMethods()) {
				if (!Objects.equals(
						declaredMethod.getName(), method.getName()) ||
					!Arrays.equals(
						declaredMethod.getParameterTypes(),
						method.getParameterTypes())) {

					continue;
				}

				featureFlag = declaredMethod.getAnnotation(FeatureFlag.class);

				if (featureFlag != null) {
					return featureFlag.value();
				}
			}

			superclass = superclass.getSuperclass();
		}

		return GetterUtil.getString(getFeatureFlagKey(declaringClass));
	}

	private static boolean _isEnabled(long companyId, String featureFlagKey) {
		if (featureFlagKey == null) {
			return true;
		}

		return FeatureFlagManagerUtil.isEnabled(companyId, featureFlagKey);
	}

	private static final ClassValue<Map<Method, String>>
		_featureFlagKeysClassValue = new ClassValue<Map<Method, String>>() {

			@Override
			protected Map<Method, String> computeValue(Class<?> clazz) {
				return new ConcurrentHashMap<>();
			}

		};

}