/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.license.util.App;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public abstract class BasePropertiesUtilLicenseTestCase
	extends BaseLicenseTestCase {

	@Test
	public void test() throws Exception {
		Field propertiesField = findField("properties.field");

		Class<?> clazz = propertiesField.getDeclaringClass();

		Set<String> keys = new HashSet<>();

		for (Field field : clazz.getDeclaredFields()) {
			if (!Modifier.isPublic(field.getModifiers())) {
				continue;
			}

			String key = (String)field.get(null);

			if (key.contains(".app.product.")) {
				for (App app : getApps()) {
					keys.add(key + StringPool.PERIOD + app.toString());
				}
			}
			else {
				keys.add(key);
			}
		}

		Properties properties = (Properties)propertiesField.get(null);

		Assert.assertEquals(keys, properties.stringPropertyNames());
	}

	protected abstract App[] getApps();

}