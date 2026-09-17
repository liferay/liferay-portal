/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportURLRewriterTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRewrite() {
		StaticSiteExportURLRewriter staticSiteExportURLRewriter =
			new StaticSiteExportURLRewriter(
				HashMapBuilder.put(
					"/es/web/site/about", "es/about.html"
				).put(
					"/web/site", "home.html"
				).put(
					"/web/site/about", "about.html"
				).build(),
				"localhost",
				HashMapBuilder.put(
					"/o/react-web/react.js", "o/react-web/react.js"
				).put(
					"/o/theme/css/clay.css?languageId=en_US",
					"o/theme/css/clay.1a2b.css"
				).put(
					"/o/theme/images/logo.png", "o/theme/images/logo.png"
				).build());

		StaticSiteExportDocument staticSiteExportDocument =
			new StaticSiteExportDocument(
				StringBundler.concat(
					"<html><head>",
					"<script type=\"importmap\">{\"imports\": {\"react\": ",
					"\"/o/react-web/react.js\"}}</script>",
					"<link href=\"http://localhost:8080/web/site/about\" ",
					"rel=\"canonical\">",
					"<link href=\"http://localhost:8080/es/web/site/about\" ",
					"hreflang=\"es-ES\" rel=\"alternate\">",
					"<link href=\"/o/theme/css/clay.css?languageId=en_US\" ",
					"rel=\"stylesheet\"></head><body>",
					"<a href=\"http://localhost:8080/web/site\">Home</a>",
					"<a href=\"https://liferay.com\">Liferay</a>",
					"<img src=\"/o/theme/images/logo.png\" ",
					"srcset=\"/o/theme/images/logo.png 1x\"></body></html>"),
				new JSONFactoryImpl());

		staticSiteExportURLRewriter.rewrite(staticSiteExportDocument);

		String html = staticSiteExportDocument.getHTML();

		Assert.assertThat(
			html, CoreMatchers.containsString("href=\"/about.html\""));
		Assert.assertThat(
			html, CoreMatchers.containsString("href=\"/es/about.html\""));
		Assert.assertThat(
			html,
			CoreMatchers.containsString("href=\"/o/theme/css/clay.1a2b.css\""));
		Assert.assertThat(
			html, CoreMatchers.containsString("href=\"/home.html\""));
		Assert.assertThat(
			html, CoreMatchers.containsString("href=\"https://liferay.com\""));
		Assert.assertThat(
			html,
			CoreMatchers.containsString(
				"srcset=\"/o/theme/images/logo.png 1x\""));
		Assert.assertThat(
			html,
			CoreMatchers.containsString("\"react\":\"/o/react-web/react.js\""));
		Assert.assertThat(
			html,
			CoreMatchers.not(CoreMatchers.containsString("localhost:8080")));

		Assert.assertEquals(
			"@import \"/o/theme/css/clay.1a2b.css\"; a { background: " +
				"url(/o/theme/images/logo.png); }",
			staticSiteExportURLRewriter.rewriteCSS(
				"@import \"/o/theme/css/clay.css?languageId=en_US\"; a { " +
					"background: url(/o/theme/images/logo.png); }"));
	}

}