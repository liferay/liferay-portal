/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

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

		buildProperties.setProperty("build.properties.seeded", "true");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"bundles_base_url", _BUNDLES_BASE_URL
		).put(
			"portal_version", RandomTestUtil.randomString()
		);

		for (String urlFieldName : _URL_FIELD_NAMES) {
			jsonObject.put(
				urlFieldName, _BUNDLES_BASE_URL + "/" + urlFieldName);
		}

		PortalRelease portalRelease = new PortalRelease(jsonObject);

		JSONObject portalReleaseJSONObject = portalRelease.getJSONObject();

		for (String urlFieldName : _URL_FIELD_NAMES) {
			testEquals(
				_BUNDLES_BASE_URL + "/" + urlFieldName,
				portalReleaseJSONObject.optString(urlFieldName, null));
		}

		JSONObject unsetJSONObject = new JSONObject();

		unsetJSONObject.put(
			_URL_FIELD_NAME_TOMCAT,
			_BUNDLES_BASE_URL + "/" + _URL_FIELD_NAME_TOMCAT
		).put(
			"bundles_base_url", _BUNDLES_BASE_URL
		).put(
			"portal_version", RandomTestUtil.randomString()
		);

		PortalRelease unsetPortalRelease = new PortalRelease(unsetJSONObject);

		JSONObject unsetPortalReleaseJSONObject =
			unsetPortalRelease.getJSONObject();

		for (String urlFieldName : _URL_FIELD_NAMES) {
			if (urlFieldName.equals(_URL_FIELD_NAME_TOMCAT)) {
				continue;
			}

			testEquals(
				null,
				unsetPortalReleaseJSONObject.optString(urlFieldName, null));
		}
	}

	private static final String _BUNDLES_BASE_URL =
		"https://release.liferay.com/portal/7.4.13-ga1";

	private static final String _URL_FIELD_NAME_TOMCAT =
		"portal_bundle_tomcat_url_string";

	private static final String[] _URL_FIELD_NAMES = {
		"plugins_war_zip_url_string", "portal_bundle_glassfish_url_string",
		"portal_bundle_jboss_url_string", "portal_bundle_tomcat_url_string",
		"portal_bundle_wildfly_url_string",
		"portal_dependencies_zip_url_string", "portal_osgi_zip_url_string",
		"portal_sql_zip_url_string", "portal_tools_zip_url_string",
		"portal_war_url_string"
	};

}