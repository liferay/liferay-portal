/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.frontend.js.audiences.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.model.AudiencesEntryGroupRel;
import com.liferay.audiences.service.AudiencesEntryGroupRelLocalService;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.frontend.js.audiences.AudiencesDefinition;
import com.liferay.frontend.js.audiences.AudiencesDefinitionProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Objects;
import java.util.Set;

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
				RandomTestUtil.randomString(), null);

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
				RandomTestUtil.randomString(), null);

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

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-85746"))
	@Test
	@TestInfo("LPD-105965")
	public void testGetAudiencesDefinitionAfterGroupRemoval() throws Exception {
		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.addAudiencesEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_getCriteriaJSON(_REGISTERED_CUSTOM_ATTRIBUTE),
				RandomTestUtil.randomString(), null);

		Group group = GroupTestUtil.addGroup();

		_addAudiencesEntryGroupRel(audiencesEntry, group);

		audiencesEntry = _audiencesEntryLocalService.updateAudiencesEntry(
			audiencesEntry);

		_multiVMPool.removePortalCache(AudiencesEntry.class.getName());

		JSONObject jsonObject = _getAudienceJSONObject(audiencesEntry);

		Assert.assertTrue(jsonObject.toString(), jsonObject.has("scope"));

		_groupLocalService.deleteGroup(group);

		jsonObject = _getAudienceJSONObject(audiencesEntry);

		Assert.assertFalse(jsonObject.toString(), jsonObject.has("scope"));
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-85746"))
	@Test
	@TestInfo("LPD-105673")
	public void testGetAudiencesDefinitionWithScope() throws Exception {
		AudiencesEntry audiencesEntry1 =
			_audiencesEntryLocalService.addAudiencesEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_getCriteriaJSON(_REGISTERED_CUSTOM_ATTRIBUTE),
				RandomTestUtil.randomString(), null);

		Group group1 = GroupTestUtil.addGroup();

		_addAudiencesEntryGroupRel(audiencesEntry1, group1);

		AudiencesEntry audiencesEntry2 =
			_audiencesEntryLocalService.addAudiencesEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_getCriteriaJSON(_REGISTERED_CUSTOM_ATTRIBUTE),
				RandomTestUtil.randomString(), null);

		Group group2 = GroupTestUtil.addGroup();

		_addAudiencesEntryGroupRel(audiencesEntry1, group2);

		audiencesEntry1 = _audiencesEntryLocalService.updateAudiencesEntry(
			audiencesEntry1);

		JSONObject jsonObject1 = _getAudienceJSONObject(audiencesEntry1);

		JSONArray scopeJSONArray = jsonObject1.getJSONArray("scope");

		Assert.assertEquals(
			scopeJSONArray.toString(), 2, scopeJSONArray.length());

		Set<Long> groupIds = JSONUtil.toLongSet(scopeJSONArray);

		Assert.assertTrue(
			groupIds.toString(), groupIds.contains(group1.getGroupId()));
		Assert.assertTrue(
			groupIds.toString(), groupIds.contains(group2.getGroupId()));

		JSONObject jsonObject2 = _getAudienceJSONObject(audiencesEntry2);

		Assert.assertFalse(jsonObject2.toString(), jsonObject2.has("scope"));
	}

	private AudiencesEntryGroupRel _addAudiencesEntryGroupRel(
			AudiencesEntry audiencesEntry, Group group)
		throws Exception {

		AudiencesEntryGroupRel audiencesEntryGroupRel =
			_audiencesEntryGroupRelLocalService.createAudiencesEntryGroupRel(
				_counterLocalService.increment());

		audiencesEntryGroupRel.setCompanyId(audiencesEntry.getCompanyId());
		audiencesEntryGroupRel.setUserId(TestPropsValues.getUserId());
		audiencesEntryGroupRel.setAudienceEntryERC(
			audiencesEntry.getExternalReferenceCode());
		audiencesEntryGroupRel.setGroupERC(group.getExternalReferenceCode());

		return _audiencesEntryGroupRelLocalService.addAudiencesEntryGroupRel(
			audiencesEntryGroupRel);
	}

	private JSONObject _getAudienceJSONObject(AudiencesEntry audiencesEntry)
		throws Exception {

		AudiencesDefinition audiencesDefinition =
			_audiencesDefinitionProvider.getAudiencesDefinition(
				TestPropsValues.getCompanyId());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			audiencesDefinition.getContent());

		JSONArray jsonArray = jsonObject.getJSONArray("audiences");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject audienceJSONObject = jsonArray.getJSONObject(i);

			if (Objects.equals(
					audiencesEntry.getExternalReferenceCode(),
					audienceJSONObject.getString("id"))) {

				return audienceJSONObject;
			}
		}

		throw new AssertionError(
			StringBundler.concat(
				"Audience ", audiencesEntry.getExternalReferenceCode(),
				" is absent from ", jsonObject));
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
	private AudiencesEntryGroupRelLocalService
		_audiencesEntryGroupRelLocalService;

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

}