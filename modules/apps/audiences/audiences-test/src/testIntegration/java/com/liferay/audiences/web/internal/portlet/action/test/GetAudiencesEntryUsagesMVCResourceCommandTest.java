/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class GetAudiencesEntryUsagesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-107094")
	public void testServeResource() throws Exception {
		AudiencesEntry audiencesEntry1 = _addAudiencesEntry();

		_assertVariationsCounts(audiencesEntry1, 0, 0);

		AudiencesEntry audiencesEntry2 = _addAudiencesEntry();

		_addAssetListEntryAudienceEntryRel(audiencesEntry1);
		_addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			audiencesEntry1);
		_addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			audiencesEntry1);
		_addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
			audiencesEntry2);

		_assertVariationsCounts(audiencesEntry1, 1, 2);
		_assertVariationsCounts(audiencesEntry2, 0, 1);
	}

	private void _addAssetListEntryAudienceEntryRel(
			AudiencesEntry audiencesEntry)
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		_assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				assetListEntry.getAssetListEntryId(),
				audiencesEntry.getExternalReferenceCode(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));
	}

	private AudiencesEntry _addAudiencesEntry() throws Exception {
		return _audiencesEntryLocalService.addAudiencesEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			JSONUtil.put(
				"conjunction", "AND"
			).put(
				"rules",
				JSONUtil.putAll(
					JSONUtil.put(
						"attribute", "url"
					).put(
						"operator", "eq"
					).put(
						"value", RandomTestUtil.randomString()
					))
			).toString(),
			RandomTestUtil.randomString(), null);
	}

	private void
			_addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				AudiencesEntry audiencesEntry)
		throws Exception {

		_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
			addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				audiencesEntry.getExternalReferenceCode(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));
	}

	private void _assertVariationsCounts(
			AudiencesEntry audiencesEntry,
			int expectedCollectionVariationsCount,
			int expectedElementVariationsCount)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setParameter(
			"audiencesEntryId",
			String.valueOf(audiencesEntry.getAudiencesEntryId()));

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			byteArrayOutputStream.toString());

		Assert.assertEquals(
			expectedCollectionVariationsCount,
			jsonObject.getInt("collectionVariationsCount"));
		Assert.assertEquals(
			expectedElementVariationsCount,
			jsonObject.getInt("elementVariationsCount"));
	}

	@Inject
	private AssetListEntryAudienceEntryRelLocalService
		_assetListEntryAudienceEntryRelLocalService;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;

	@Inject(filter = "mvc.command.name=/audiences/get_audiences_entry_usages")
	private MVCResourceCommand _mvcResourceCommand;

}