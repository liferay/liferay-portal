/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.generator.internal.artifact;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.Objects;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.osgi.framework.Constants;

/**
 * @author Gregory Amerson
 */
public class ArtifactURLUtilTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static TemporaryFolder temporaryFolder = new TemporaryFolder();

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		URL.setURLStreamHandlerFactory(
			protocol -> {
				if (!Objects.equals(protocol, "webbundle")) {
					return null;
				}

				return new URLStreamHandler() {

					protected URLConnection openConnection(URL url)
						throws IOException {

						return new URLConnection(url) {

							public void connect() throws IOException {
							}

						};
					}

				};
			});
	}

	@Test
	public void testClientExtensionURLWithMalformedConfigFallsBack()
		throws Exception {

		String query = _transformClientExtensionConfigToQuery(
			RandomTestUtil.randomString(), "malformedconfig.zip");

		Assert.assertTrue(
			query.contains(Constants.BUNDLE_SYMBOLICNAME + "=malformedconfig"));
		Assert.assertTrue(query.contains("Web-ContextPath=/malformedconfig"));
	}

	@Test
	public void testClientExtensionURLWithStringConfigValueFallsBack()
		throws Exception {

		JSONObject jsonObject = JSONUtil.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		String query = _transformClientExtensionConfigToQuery(
			jsonObject.toString(), "nonobjectconfig.zip");

		Assert.assertTrue(
			query.contains(Constants.BUNDLE_SYMBOLICNAME + "=nonobjectconfig"));
		Assert.assertTrue(query.contains("Web-ContextPath=/nonobjectconfig"));
	}

	@Test
	public void testClientExtensionURLWithVersionContainsExpectedSymbolicName()
		throws Exception {

		File file = temporaryFolder.newFile("clientextension-1.0.0.zip");

		URI uri = file.toURI();

		URL url = ArtifactURLUtil.transform(uri.toURL());

		String query = url.getQuery();

		Assert.assertFalse(
			query.contains(
				Constants.BUNDLE_SYMBOLICNAME + "=clientextension-1.0.0"));
		Assert.assertTrue(
			query.contains(Constants.BUNDLE_SYMBOLICNAME + "=clientextension"));
	}

	@Test
	public void testClientExtensionURLWithVersionUsesConfigWebContextPath()
		throws Exception {

		JSONObject jsonObject = JSONUtil.put(
			RandomTestUtil.randomString(),
			JSONUtil.put("webContextPath", "/liferay-sample-global-js"));

		String query = _transformClientExtensionConfigToQuery(
			jsonObject.toString(),
			"liferay-sample-global-js-1.0.0-SNAPSHOT.zip");

		Assert.assertTrue(
			query.contains("Web-ContextPath=/liferay-sample-global-js&"));
		Assert.assertFalse(
			query.contains(
				"Web-ContextPath=/liferay-sample-global-js-1.0.0-SNAPSHOT"));
	}

	@Test
	public void testClientExtensionURLWithWebContextPathWithoutLeadingSlash()
		throws Exception {

		JSONObject jsonObject = JSONUtil.put(
			RandomTestUtil.randomString(),
			JSONUtil.put("webContextPath", "no-slash"));

		String query = _transformClientExtensionConfigToQuery(
			jsonObject.toString(), "noleadingslash.zip");

		Assert.assertTrue(query.contains("Web-ContextPath=/no-slash"));
		Assert.assertFalse(query.contains("Web-ContextPath=//no-slash"));
		Assert.assertFalse(query.contains("Web-ContextPath=no-slash"));
	}

	@Test
	public void testClientExtensionURLWithoutVersionContainsExpectedSymbolicName()
		throws Exception {

		File file = temporaryFolder.newFile("clientextension.zip");

		URI uri = file.toURI();

		URL url = ArtifactURLUtil.transform(uri.toURL());

		String query = url.getQuery();

		Assert.assertTrue(
			query.contains(Constants.BUNDLE_SYMBOLICNAME + "=clientextension"));
	}

	@Test
	public void testWarURLContainsExpectedSymbolicName() throws Exception {
		String uriString = _getURIString(
			"dependencies/classic-theme.autodeployed.war");

		URI uri = new URI(uriString);

		URL url = ArtifactURLUtil.transform(uri.toURL());

		String query = url.getQuery();

		Assert.assertTrue(
			query.contains(
				Constants.BUNDLE_SYMBOLICNAME + "=classic-theme.autodeployed"));
	}

	private String _getURIString(String fileName) throws Exception {
		URL url = ArtifactURLUtilTest.class.getResource(fileName);

		URI uri = url.toURI();

		return uri.toASCIIString();
	}

	private String _transformClientExtensionConfigToQuery(
			String configJSON, String zipName)
		throws Exception {

		File dir = temporaryFolder.newFolder();

		try {
			File configFile = new File(
				dir, "liferay-sample-global-js.client-extension-config.json");

			Files.write(
				configFile.toPath(),
				configJSON.getBytes(StandardCharsets.UTF_8));

			File zipFile = temporaryFolder.newFile(zipName);

			ZipTestUtil.zipDirToFile(dir, zipFile);

			URI uri = zipFile.toURI();

			URL url = ArtifactURLUtil.transform(uri.toURL());

			return url.getQuery();
		}
		finally {
			FileUtil.delete(dir);
		}
	}

}