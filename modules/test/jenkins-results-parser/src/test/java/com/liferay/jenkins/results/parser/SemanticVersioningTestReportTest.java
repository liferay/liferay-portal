/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class SemanticVersioningTestReportTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestClassName() {
		testEquals(
			"com.liferay.semantic.versioning.SemanticVersioningTest." +
				"testSemanticVersioning[/apps/account/account-api]",
			_getTestClassName(
				"com.liferay.semantic.versioning.SemanticVersioningTest",
				"com.liferay.semantic.versioning.SemanticVersioningTest." +
					"testSemanticVersioning[/apps/account/account-api]"));
		testEquals(
			"com.liferay.semantic.versioning.SemanticVersioningTest." +
				"testSemanticVersioning[/apps/account/account-api]",
			_getTestClassName(
				null,
				"com.liferay.semantic.versioning.SemanticVersioningTest." +
					"testSemanticVersioning[/apps/account/account-api]"));
		testEquals(
			"modules.apps.commerce",
			_getTestClassName(
				"modules.apps.commerce", "modules.apps.commerce"));
	}

	private String _getTestClassName(String testClassName, String testName) {
		JSONObject jsonObject = new JSONObject(
		).put(
			"name", testName
		);

		if (testClassName != null) {
			jsonObject.put("testClassName", testClassName);
		}

		SemanticVersioningTestReport semanticVersioningTestReport =
			new SemanticVersioningTestReport(null, jsonObject);

		return semanticVersioningTestReport.getTestClassName();
	}

}