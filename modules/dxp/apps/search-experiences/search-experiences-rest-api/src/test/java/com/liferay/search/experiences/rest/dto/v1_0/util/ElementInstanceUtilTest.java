/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0.util;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.search.experiences.rest.dto.v1_0.ElementInstance;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Selena Aungst
 */
public class ElementInstanceUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testUnpackCustomSXPElementRecomputesConfigurationEntry()
		throws Exception {

		ElementInstance elementInstance = ElementInstanceUtil.unpack(
			ElementInstance.unsafeToDTO(
				JSONUtil.put(
					"configurationEntry",
					_createConfigurationJSONObject(_INCORRECT_VALUE)
				).put(
					"sxpElement", _createSXPElementJSONObject(_INCORRECT_VALUE)
				).put(
					"uiConfigurationValues",
					JSONUtil.put(
						"sxpElement",
						String.valueOf(
							_createSXPElementJSONObject(_CORRECT_VALUE)))
				).toString()));

		String configurationEntry = String.valueOf(
			elementInstance.getConfigurationEntry());

		Assert.assertTrue(
			configurationEntry, configurationEntry.contains(_CORRECT_VALUE));
		Assert.assertFalse(
			configurationEntry, configurationEntry.contains(_INCORRECT_VALUE));
	}

	private JSONObject _createClauseJSONObject(String value) {
		return JSONUtil.put(
			"context", "query"
		).put(
			"occur", "filter"
		).put(
			"query",
			JSONUtil.put(
				"terms",
				JSONUtil.put(
					"groupAssetCategoryExternalReferenceCodes",
					JSONUtil.putAll(value)))
		);
	}

	private JSONObject _createConfigurationJSONObject(String value) {
		return JSONUtil.put(
			"queryConfiguration",
			JSONUtil.put(
				"queryEntries",
				JSONUtil.putAll(
					JSONUtil.put(
						"clauses",
						JSONUtil.putAll(_createClauseJSONObject(value))))));
	}

	private JSONObject _createSXPElementJSONObject(String value) {
		return JSONUtil.put(
			"elementDefinition",
			JSONUtil.put(
				"category", "filter"
			).put(
				"configuration", _createConfigurationJSONObject(value)
			).put(
				"icon", "filter"
			)
		).put(
			"title_i18n", JSONUtil.put("en_US", "Filter by Category")
		);
	}

	private static final String _CORRECT_VALUE = RandomTestUtil.randomString();

	private static final String _INCORRECT_VALUE =
		RandomTestUtil.randomString();

}