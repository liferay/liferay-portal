/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Dante Wang
 */
public class HookHotDeployListenerPropertiesTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testPropsValuesProperties() throws Exception {
		Set<String> supportedPropertyNames = new HashSet<>(
			Arrays.asList(HookHotDeployListener.SUPPORTED_PROPERTIES));

		for (Field field : HookHotDeployListener.class.getFields()) {
			String fieldName = field.getName();

			if (!fieldName.startsWith("_PROPS_VALUES_") ||
				fieldName.equals("_PROPS_VALUES_OBSOLETE")) {

				continue;
			}

			String[] propertyNames = (String[])field.get(null);

			for (String propertyName : propertyNames) {
				String propsValuesFieldName = StringUtil.replace(
					StringUtil.toUpperCase(propertyName), CharPool.PERIOD,
					CharPool.UNDERLINE);

				Field propsValuesField = PropsValues.class.getField(
					propsValuesFieldName);

				Assert.assertTrue(supportedPropertyNames.remove(propertyName));
				Assert.assertTrue(
					Modifier.isFinal(propsValuesField.getModifiers()));
			}

			Assert.assertTrue(
				supportedPropertyNames.toString(),
				supportedPropertyNames.isEmpty());
		}
	}

	@Test
	public void testSupportedProperties() {
		List<String> obsoleteProperties = new ArrayList<>();

		for (String supportedProperty :
				HookHotDeployListener.SUPPORTED_PROPERTIES) {

			String propertyName = supportedProperty;

			int index = propertyName.indexOf(".*");

			if (index > 0) {
				propertyName = propertyName.substring(0, index);
			}

			String fieldName = StringUtil.replace(
				StringUtil.toUpperCase(propertyName), CharPool.PERIOD,
				CharPool.UNDERLINE);

			try {
				HookHotDeployListener.class.getField(fieldName);
			}
			catch (NoSuchFieldException noSuchFieldException) {
				obsoleteProperties.add(supportedProperty);
			}
		}

		Assert.assertTrue(
			obsoleteProperties.toString(), obsoleteProperties.isEmpty());
	}

}