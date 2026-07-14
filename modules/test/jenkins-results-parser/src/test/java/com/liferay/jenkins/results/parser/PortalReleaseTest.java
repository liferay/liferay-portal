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

		buildProperties.setProperty(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

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
			"portal_version", RandomTestUtil.randomString()
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
			"portal_version", RandomTestUtil.randomString()
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

}