/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.frontend.js.audiences.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.frontend.js.audiences.AudiencesDefinition;
import com.liferay.frontend.js.audiences.AudiencesDefinitionProvider;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AudiencesDefinitionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-85746"))
	@Test
	public void testGetAudiencesDefinition() throws Exception {
		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.addAudiencesEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_getCriteriaJSON(_REGISTERED_CUSTOM_ATTRIBUTE),
				RandomTestUtil.randomString());

		AudiencesDefinition audiencesDefinition =
			_audiencesDefinitionProvider.getAudiencesDefinition(
				TestPropsValues.getCompanyId());

		String content = audiencesDefinition.getContent();

		JSONObject audiencesEntryJSONObject = JSONFactoryUtil.createJSONObject(
			audiencesEntry.getJSON());

		JSONObject expectedContentJSONObject = JSONUtil.put(
			"audiences",
			JSONUtil.putAll(
				audiencesEntryJSONObject.put(
					"id", audiencesEntry.getExternalReferenceCode())));

		ObjectMapper objectMapper = new ObjectMapper();

		Assert.assertEquals(
			objectMapper.readTree(expectedContentJSONObject.toString()),
			objectMapper.readTree(content));

		Assert.assertEquals(
			HashedFilesUtil.computeHash(content),
			audiencesDefinition.getHash());

		AudiencesEntry unregisteredAudiencesEntry =
			_audiencesEntryLocalService.addAudiencesEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_getCriteriaJSON(_REGISTERED_CUSTOM_ATTRIBUTE),
				RandomTestUtil.randomString());

		unregisteredAudiencesEntry.setJSON(
			JSONUtil.put(
				"conjunction", "AND"
			).put(
				"rules",
				JSONUtil.putAll(
					JSONUtil.put(
						"conjunction", "OR"
					).put(
						"rules",
						JSONUtil.putAll(
							_getRuleJSONObject(_UNREGISTERED_CUSTOM_ATTRIBUTE))
					))
			).toString());

		_audiencesEntryLocalService.updateAudiencesEntry(
			unregisteredAudiencesEntry);

		audiencesDefinition =
			_audiencesDefinitionProvider.getAudiencesDefinition(
				TestPropsValues.getCompanyId());

		content = audiencesDefinition.getContent();

		Assert.assertEquals(
			objectMapper.readTree(expectedContentJSONObject.toString()),
			objectMapper.readTree(content));
	}

	private String _getCriteriaJSON(String attribute) {
		return JSONUtil.put(
			"conjunction", "AND"
		).put(
			"rules", JSONUtil.putAll(_getRuleJSONObject(attribute))
		).toString();
	}

	private JSONObject _getRuleJSONObject(String attribute) {
		return JSONUtil.put(
			"attribute", attribute
		).put(
			"operator", "eq"
		).put(
			"value", true
		);
	}

	private static final String _REGISTERED_CUSTOM_ATTRIBUTE =
		"custom:/o/frontend-js-audiences-web/__liferay__" +
			"/custom-attributes.js#signed_in";

	private static final String _UNREGISTERED_CUSTOM_ATTRIBUTE =
		"custom:data:text/javascript,export function run(){return true}#run";

	@Inject
	private AudiencesDefinitionProvider _audiencesDefinitionProvider;

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

}