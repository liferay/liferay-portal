/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class GetCollectionVariationsMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_assetListEntry = _assetListEntryLocalService.addAssetListEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);
	}

	@FeatureFlag("LPD-109242")
	@Test
	public void testDoServeResourceWithAudienceVariations() throws Exception {
		AudiencesEntry audiencesEntry1 = _addAudiencesEntry();

		AudiencesEntry audiencesEntry2 = _addAudiencesEntry();

		_addAssetListEntryAudienceEntryRel(
			audiencesEntry2.getExternalReferenceCode(), 1);

		_addAssetListEntryAudienceEntryRel(
			audiencesEntry1.getExternalReferenceCode(), 0);

		String audienceEntryERC = RandomTestUtil.randomString();

		_addAssetListEntryAudienceEntryRel(audienceEntryERC, 2);

		JSONArray jsonArray = _getCollectionVariationsJSONArray();

		Assert.assertEquals(4, jsonArray.length());
		Assert.assertEquals(audiencesEntry1.getName(), jsonArray.getString(0));
		Assert.assertEquals(audiencesEntry2.getName(), jsonArray.getString(1));
		Assert.assertEquals(audienceEntryERC, jsonArray.getString(2));
		Assert.assertEquals(
			SegmentsEntryConstants.getDefaultSegmentsEntryName(LocaleUtil.US),
			jsonArray.getString(3));
	}

	@FeatureFlag(enable = false, value = "LPD-109242")
	@Test
	public void testDoServeResourceWithSegmentsVariations() throws Exception {
		AudiencesEntry audiencesEntry = _addAudiencesEntry();

		_addAssetListEntryAudienceEntryRel(
			audiencesEntry.getExternalReferenceCode(), 0);

		SegmentsEntry segmentsEntry = _addAssetListEntrySegmentsEntryRel();

		JSONArray jsonArray = _getCollectionVariationsJSONArray();

		Assert.assertEquals(2, jsonArray.length());
		Assert.assertEquals(
			SegmentsEntryConstants.getDefaultSegmentsEntryName(LocaleUtil.US),
			jsonArray.getString(0));
		Assert.assertEquals(
			segmentsEntry.getName(LocaleUtil.US), jsonArray.getString(1));
	}

	@FeatureFlag("LPD-109242")
	@Test
	public void testDoServeResourceWithoutAudienceVariations()
		throws Exception {

		_addAssetListEntrySegmentsEntryRel();

		JSONArray jsonArray = _getCollectionVariationsJSONArray();

		Assert.assertEquals(0, jsonArray.length());
	}

	private void _addAssetListEntryAudienceEntryRel(
			String audienceEntryERC, int priority)
		throws Exception {

		_assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_assetListEntry.getAssetListEntryId(), audienceEntryERC,
				priority, StringPool.BLANK, _serviceContext);
	}

	private SegmentsEntry _addAssetListEntrySegmentsEntryRel()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_assetListEntrySegmentsEntryRelLocalService.
			addAssetListEntrySegmentsEntryRel(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_assetListEntry.getAssetListEntryId(),
				segmentsEntry.getSegmentsEntryId(), StringPool.BLANK,
				_serviceContext);

		return segmentsEntry;
	}

	private AudiencesEntry _addAudiencesEntry() throws Exception {
		return _audiencesEntryLocalService.addAudiencesEntry(
			null, TestPropsValues.getUserId(), StringPool.BLANK,
			RandomTestUtil.randomString(), null);
	}

	private JSONArray _getCollectionVariationsJSONArray() throws Exception {
		MockLiferayResourceRequest resourceRequest =
			new MockLiferayResourceRequest();

		resourceRequest.addParameter(
			"classPK", String.valueOf(_assetListEntry.getAssetListEntryId()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);

		resourceRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		MockLiferayResourceResponse resourceResponse =
			new MockLiferayResourceResponse();

		ReflectionTestUtil.invoke(
			_mvcResourceCommand, "doServeResource",
			new Class<?>[] {ResourceRequest.class, ResourceResponse.class},
			resourceRequest, resourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)resourceResponse.getPortletOutputStream();

		return JSONFactoryUtil.createJSONArray(
			byteArrayOutputStream.toString());
	}

	private AssetListEntry _assetListEntry;

	@Inject
	private AssetListEntryAudienceEntryRelLocalService
		_assetListEntryAudienceEntryRelLocalService;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/get_collection_variations"
	)
	private MVCResourceCommand _mvcResourceCommand;

	private ServiceContext _serviceContext;

}