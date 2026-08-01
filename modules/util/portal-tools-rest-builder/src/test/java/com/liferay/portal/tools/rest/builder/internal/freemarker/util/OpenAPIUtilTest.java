/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.freemarker.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.tools.rest.builder.internal.yaml.config.ConfigYAML;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Adolfo Pérez
 */
public class OpenAPIUtilTest {

	@Test
	public void testFormatPlural() {
		_testFormatPlural(15, "car10s", "car10");
		_testFormatPlural(15, "category1s", "category1");
		_testFormatPlural(16, "123", "123");
		_testFormatPlural(16, "batches", "batch");
		_testFormatPlural(16, "boxes", "box");
		_testFormatPlural(16, "boys", "boy");
		_testFormatPlural(16, "buses", "bus");
		_testFormatPlural(16, "cars", "car");
		_testFormatPlural(16, "cars10", "car10");
		_testFormatPlural(16, "categories", "category");
		_testFormatPlural(16, "categories1", "category1");
		_testFormatPlural(16, "days", "day");
		_testFormatPlural(16, "dishes", "dish");
		_testFormatPlural(16, "guys", "guy");
		_testFormatPlural(16, "keys", "key");
		_testFormatPlural(16, "quizes", "quiz");
		_testFormatPlural(16, StringPool.BLANK, StringPool.BLANK);
		_testFormatPlural(16, null, null);
	}

	@Test
	public void testFormatSingular() {
		_testFormatSingular(1, "clas", "class");
		_testFormatSingular(1, "status1", "status1");
		_testFormatSingular(15, "cars10", "cars10");
		_testFormatSingular(15, "categories1", "categories1");
		_testFormatSingular(15, "warehous", "warehouses");
		_testFormatSingular(15, "warehouses1", "warehouses1");
		_testFormatSingular(16, "123", "123");
		_testFormatSingular(16, "base", "bases");
		_testFormatSingular(16, "box", "boxes");
		_testFormatSingular(16, "bus", "buses");
		_testFormatSingular(16, "car", "car");
		_testFormatSingular(16, "car", "cars");
		_testFormatSingular(16, "car10", "cars10");
		_testFormatSingular(16, "category", "categories");
		_testFormatSingular(16, "category1", "categories1");
		_testFormatSingular(16, "clause", "clauses");
		_testFormatSingular(16, "key", "keys");
		_testFormatSingular(16, "status", "status");
		_testFormatSingular(16, "status1", "status1");
		_testFormatSingular(16, "warehouse", "warehouses");
		_testFormatSingular(16, "warehouse1", "warehouses1");
		_testFormatSingular(16, StringPool.BLANK, StringPool.BLANK);
		_testFormatSingular(16, null, null);
		_testFormatSingular(6, "class", "class");
		_testFormatSingular(6, "statu", "status");
	}

	private void _testFormatPlural(
		int compatibilityVersion, String expected, String s) {

		ConfigYAML configYAML = new ConfigYAML();

		configYAML.setCompatibilityVersion(compatibilityVersion);

		Assert.assertEquals(expected, OpenAPIUtil.formatPlural(configYAML, s));
	}

	private void _testFormatSingular(
		int compatibilityVersion, String expected, String s) {

		ConfigYAML configYAML = new ConfigYAML();

		configYAML.setCompatibilityVersion(compatibilityVersion);

		Assert.assertEquals(
			expected, OpenAPIUtil.formatSingular(configYAML, s));
	}

}