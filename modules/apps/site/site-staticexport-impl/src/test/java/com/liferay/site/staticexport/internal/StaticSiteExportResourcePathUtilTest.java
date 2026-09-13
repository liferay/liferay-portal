/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportResourcePathUtilTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetPath() {
		Assert.assertEquals(
			"o/classic-theme/css/clay.css",
			StaticSiteExportResourcePathUtil.getPath(
				"/o/classic-theme/css/clay.css"));

		String path = StaticSiteExportResourcePathUtil.getPath(
			"/o/classic-theme/css/clay.css?browserId=other&languageId=en_US");

		Assert.assertTrue(path, path.startsWith("o/classic-theme/css/clay."));
		Assert.assertTrue(path, path.endsWith(".css"));
		Assert.assertNotEquals("o/classic-theme/css/clay.css", path);
		Assert.assertNotEquals(
			path,
			StaticSiteExportResourcePathUtil.getPath(
				"/o/classic-theme/css/clay.css?languageId=es_ES"));

		Assert.assertEquals(
			"documents/1/2/emblem.svg/4bca3313-75ce.svg",
			StaticSiteExportResourcePathUtil.getPath(
				"/documents/1/2/emblem.svg/4bca3313-75ce"));

		path = StaticSiteExportResourcePathUtil.getPath(
			"/combo?browserId=other&minifierType=js&/o/a/a.js&/o/b/b.js");

		Assert.assertTrue(path, path.startsWith("combo."));
		Assert.assertFalse(path, path.contains("?"));
	}

}