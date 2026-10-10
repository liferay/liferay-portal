/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.URL;

import java.util.Collections;
import java.util.Properties;

import org.json.JSONObject;

import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class PortalReleaseTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetJSONObject() {
		mockEnvironment(Collections.<String, String>emptyMap());

		Properties buildProperties = new Properties();

		String portalVersion = RandomTestUtil.randomString();

		for (String propertyName : _BUILD_PROPERTY_NAMES) {
			buildProperties.setProperty(
				propertyName + "[" + portalVersion + "]",
				"https://" + RandomTestUtil.randomString());
		}

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		String bundlesBaseURL = "https://release.liferay.com/portal/7.4.13-ga1";

		String[] urlFieldNames = {
			"plugins_war_zip_url_string", "portal_bundle_glassfish_url_string",
			"portal_bundle_jboss_url_string", "portal_bundle_tomcat_url_string",
			"portal_bundle_wildfly_url_string",
			"portal_dependencies_zip_url_string", "portal_osgi_zip_url_string",
			"portal_sql_zip_url_string", "portal_tools_zip_url_string",
			"portal_war_url_string"
		};

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"bundles_base_url", bundlesBaseURL
		).put(
			"portal_version", portalVersion
		);

		for (String urlFieldName : urlFieldNames) {
			jsonObject.put(urlFieldName, bundlesBaseURL + "/" + urlFieldName);
		}

		PortalRelease portalRelease = new PortalRelease(jsonObject);

		JSONObject portalReleaseJSONObject = portalRelease.getJSONObject();

		for (String urlFieldName : urlFieldNames) {
			testEquals(
				bundlesBaseURL + "/" + urlFieldName,
				portalReleaseJSONObject.optString(urlFieldName, null));
		}

		String tomcatURLFieldName = "portal_bundle_tomcat_url_string";

		JSONObject unsetJSONObject = new JSONObject();

		unsetJSONObject.put(
			tomcatURLFieldName, bundlesBaseURL + "/" + tomcatURLFieldName
		).put(
			"bundles_base_url", bundlesBaseURL
		).put(
			"portal_version", portalVersion
		);

		PortalRelease unsetPortalRelease = new PortalRelease(unsetJSONObject);

		JSONObject unsetPortalReleaseJSONObject =
			unsetPortalRelease.getJSONObject();

		for (String urlFieldName : urlFieldNames) {
			if (urlFieldName.equals(tomcatURLFieldName)) {
				continue;
			}

			testEquals(
				null,
				unsetPortalReleaseJSONObject.optString(urlFieldName, null));
		}
	}

	@Test
	public void testGetPortalVersion() throws Exception {
		_testGetPortalVersion(
			"2026.q2.3", "liferay-dxp-tomcat-2026.q2.3-1780905368.7z");
		_testGetPortalVersion(
			"7.0.10.17-sp17",
			"liferay-dxp-digital-enterprise-tomcat-7.0.10.17-sp17.7z");
		_testGetPortalVersion(
			"7.4.13-ga1", "liferay-dxp-tomcat-7.4.13-ga1-20211020105546063.7z");
		_testGetPortalVersion(
			"7.4.13-u154", "7.4.13-u154-1791054167/liferay-dxp-tomcat.7z");
		_testGetPortalVersion(
			"7.4.13-u154", "liferay-dxp-tomcat-7.4.13-u154-1791054167.7z");
		_testGetPortalVersion(
			"7.4.13-u154",
			"liferay-dxp-tomcat-7.4.13-u154-cms-standalone-1791273379.7z");
		_testGetPortalVersion(
			"7.4.13.u1", "liferay-dxp-tomcat-7.4.13.u1-20211221182705869.7z");
	}

	@Test
	public void testInitializeURLs() throws Exception {
		Properties buildProperties = new Properties();

		for (String propertyName : _BUILD_PROPERTY_NAMES) {
			buildProperties.setProperty(
				propertyName + "[7.4.13]",
				"https://" + RandomTestUtil.randomString());
		}

		String tomcatURLString = "https://" + RandomTestUtil.randomString();

		buildProperties.setProperty(
			"portal.bundle.tomcat[7.4.13.u154]", tomcatURLString);

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		PortalRelease portalRelease = new PortalRelease(
			new URL(
				JenkinsResultsParserUtil.combine(
					"https://", RandomTestUtil.randomString(),
					"/liferay-dxp-tomcat-7.4.13-u154-1791054167.7z")));

		testEquals(
			tomcatURLString,
			String.valueOf(portalRelease.getPortalBundleTomcatURL()));

		JSONObject portalReleaseJSONObject = portalRelease.getJSONObject();

		for (String urlFieldName : _NON_TOMCAT_URL_FIELD_NAMES) {
			testEquals(
				null, portalReleaseJSONObject.optString(urlFieldName, null));
		}
	}

	@Test
	public void testIsStandaloneBundleURL() throws Exception {
		_testIsStandaloneBundleURL(
			false,
			"liferay-dxp-osgi-7.4.13-u154-cms-standalone-1791273379.zip");
		_testIsStandaloneBundleURL(
			false, "liferay-dxp-tomcat-2026.q2.3-1780905368.7z");
		_testIsStandaloneBundleURL(
			false, "liferay-dxp-tomcat-7.4.13-u154-1791054167.7z");
		_testIsStandaloneBundleURL(
			false,
			"liferay-dxp-tomcat-7.4.13-u154-cms-standalone-1791273379.7z.MD5");
		_testIsStandaloneBundleURL(
			false, "liferay-dxp-tomcat-7.4.13-u154-cms-standalone.7z");
		_testIsStandaloneBundleURL(
			true,
			"liferay-dxp-tomcat-7.4.13-u154-cms-standalone-1791273379.7z");
		_testIsStandaloneBundleURL(
			true,
			"liferay-dxp-tomcat-7.4.13-u154-cms-standalone-1791273379.tar.gz");
		_testIsStandaloneBundleURL(
			true,
			"liferay-dxp-tomcat-7.4.13-u154-cms-standalone-1791273379.zip");
	}

	@Test
	public void testSetStandalone() throws Exception {
		Properties buildProperties = new Properties();

		for (String propertyName : _BUILD_PROPERTY_NAMES) {
			buildProperties.setProperty(
				propertyName + "[7.4.13]",
				"https://" + RandomTestUtil.randomString());
		}

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"bundles_base_url", "https://" + RandomTestUtil.randomString()
		).put(
			"portal_version", "7.4.13"
		);

		for (String urlFieldName : _NON_TOMCAT_URL_FIELD_NAMES) {
			jsonObject.put(
				urlFieldName, "https://" + RandomTestUtil.randomString());
		}

		PortalRelease portalRelease = new PortalRelease(jsonObject);

		portalRelease.setPortalBundleTomcatURL(
			new URL(
				JenkinsResultsParserUtil.combine(
					"https://", RandomTestUtil.randomString(),
					"/liferay-dxp-tomcat-7.4.13-u154-cms-standalone-",
					"1791273379.7z")));
		portalRelease.setStandalone(true);

		JSONObject portalReleaseJSONObject = portalRelease.getJSONObject();

		PortalRelease jsonPortalRelease = new PortalRelease(
			portalReleaseJSONObject);

		testEquals(
			portalRelease.getPortalBundleTomcatURL(),
			jsonPortalRelease.getPortalBundleTomcatURL());
		testEquals(true, jsonPortalRelease.isStandalone());

		JSONObject jsonPortalReleaseJSONObject =
			jsonPortalRelease.getJSONObject();

		for (String urlFieldName : _NON_TOMCAT_URL_FIELD_NAMES) {
			testEquals(
				null,
				jsonPortalReleaseJSONObject.optString(urlFieldName, null));
			testEquals(
				null, portalReleaseJSONObject.optString(urlFieldName, null));
		}

		jsonPortalRelease.setStandalone(false);

		testEquals(false, jsonPortalRelease.isStandalone());

		JSONObject unsetPortalReleaseJSONObject =
			jsonPortalRelease.getJSONObject();

		for (String urlFieldName : _NON_TOMCAT_URL_FIELD_NAMES) {
			testEquals(
				null,
				unsetPortalReleaseJSONObject.optString(urlFieldName, null));
		}
	}

	private void _testGetPortalVersion(
			String expectedPortalVersion, String urlPath)
		throws Exception {

		testEquals(
			expectedPortalVersion,
			PortalRelease.getPortalVersion(
				new URL(
					JenkinsResultsParserUtil.combine(
						"https://", RandomTestUtil.randomString(), "/",
						urlPath))));
	}

	private void _testIsStandaloneBundleURL(boolean expected, String fileName)
		throws Exception {

		testEquals(
			expected,
			PortalRelease.isStandaloneBundleURL(
				new URL(
					JenkinsResultsParserUtil.combine(
						"https://", RandomTestUtil.randomString(), "/",
						fileName))));
	}

	private static final String[] _BUILD_PROPERTY_NAMES = {
		"plugins.war.zip.url", "portal.bundle.glassfish", "portal.bundle.jboss",
		"portal.bundle.tomcat", "portal.bundle.wildfly",
		"portal.dependencies.zip.url", "portal.osgi.zip.url",
		"portal.sql.zip.url", "portal.tools.zip.url", "portal.war.url"
	};

	private static final String[] _NON_TOMCAT_URL_FIELD_NAMES = {
		"plugins_war_zip_url_string", "portal_bundle_glassfish_url_string",
		"portal_bundle_jboss_url_string", "portal_bundle_wildfly_url_string",
		"portal_dependencies_zip_url_string", "portal_osgi_zip_url_string",
		"portal_sql_zip_url_string", "portal_tools_zip_url_string",
		"portal_war_url_string"
	};

}