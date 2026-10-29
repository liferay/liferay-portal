/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.configuration;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class FreeMarkerEngineConfigurationTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRestrictedClasses() throws Exception {
		for (String restrictedClassName :
				_freeMarkerEngineConfiguration.restrictedClasses()) {

			if (restrictedClassName.endsWith(StringPool.STAR)) {
				continue;
			}

			Class.forName(
				restrictedClassName, false,
				FreeMarkerEngineConfigurationTest.class.getClassLoader());
		}
	}

	@Test
	public void testRestrictedMethods() throws Exception {
		for (String restrictedMethodName :
				_freeMarkerEngineConfiguration.restrictedMethods()) {

			int index = restrictedMethodName.indexOf(CharPool.POUND);

			Class<?> clazz = Class.forName(
				restrictedMethodName.substring(0, index), false,
				FreeMarkerEngineConfigurationTest.class.getClassLoader());
			String methodName = restrictedMethodName.substring(index + 1);

			Assert.assertTrue(
				restrictedMethodName,
				ArrayUtil.exists(
					clazz.getMethods(),
					method -> methodName.equals(method.getName())));
		}
	}

	private static final FreeMarkerEngineConfiguration
		_freeMarkerEngineConfiguration = ConfigurableUtil.createConfigurable(
			FreeMarkerEngineConfiguration.class, Collections.emptyMap());

}