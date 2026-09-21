/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportResourceHarvesterTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testHarvestJS() {
		StaticSiteExportDocument staticSiteExportDocument =
			new StaticSiteExportDocument(
				"<script type=\"importmap\">{\"imports\": {\"react\": " +
					"\"/o/react-web/react.js\", \"@clayui/\": " +
						"\"/o/clay-web/\"}}</script>",
				new JSONFactoryImpl());

		Map<String, String> importMapPrefixes =
			staticSiteExportDocument.getImportMapPrefixes();

		Assert.assertEquals(
			importMapPrefixes.toString(),
			HashMapBuilder.put(
				"@clayui/", "/o/clay-web/"
			).build(),
			importMapPrefixes);

		Set<String> urls = _staticSiteExportResourceHarvester.harvestJS(
			importMapPrefixes,
			StringBundler.concat(
				"import a from './a.js'; /* import b from './b.js'; */ ",
				"import('../c.js'); import d from '@clayui/button/index.js'; ",
				"fetch('/o/my-web/data.json'); new URL('./d.css', ",
				"import.meta.url);"),
			"/o/my-web/js/main.js");

		Assert.assertEquals(urls.toString(), 5, urls.size());
		Assert.assertTrue(urls.toString(), urls.contains("/o/my-web/js/a.js"));
		Assert.assertTrue(urls.toString(), urls.contains("/o/my-web/c.js"));
		Assert.assertTrue(
			urls.toString(), urls.contains("/o/clay-web/button/index.js"));
		Assert.assertTrue(
			urls.toString(), urls.contains("/o/my-web/data.json"));
		Assert.assertTrue(urls.toString(), urls.contains("/o/my-web/js/d.css"));
	}

	private final StaticSiteExportResourceHarvester
		_staticSiteExportResourceHarvester =
			new StaticSiteExportResourceHarvester();

}