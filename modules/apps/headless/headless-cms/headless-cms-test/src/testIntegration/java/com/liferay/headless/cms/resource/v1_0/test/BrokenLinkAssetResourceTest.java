/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.cms.client.dto.v1_0.BrokenLinkAsset;
import com.liferay.headless.cms.client.pagination.Page;
import com.liferay.headless.cms.client.pagination.Pagination;
import com.liferay.headless.cms.resource.v1_0.test.util.CMSOutboundLinkTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@FeatureFlag("LPD-82226")
@RunWith(Arquillian.class)
public class BrokenLinkAssetResourceTest
	extends BaseBrokenLinkAssetResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Override
	@Test
	public void testEscapeRegexInStringFields() throws Exception {
	}

	@Override
	@Test
	public void testGetBrokenLinkAssetsPage() throws Exception {
		_testGetBrokenLinkAssetsPage(RandomTestUtil.randomString());
		_testGetBrokenLinkAssetsPage(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_testGetBrokenLinkAssetsPageWithDuplicateTitles();
	}

	@Override
	@Test
	public void testGetBrokenLinkAssetsPageWithPagination() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getBasicWebContentObjectDefinition();

		ObjectEntry expiredObjectEntry = _addExpiredObjectEntry(
			depotEntry, objectDefinition, serviceContext);

		String imageHTML = CMSOutboundLinkTestUtil.getImageHTML(
			expiredObjectEntry.getExternalReferenceCode());

		for (int i = 0; i < 3; i++) {
			_addObjectEntry(
				imageHTML, depotEntry, objectDefinition,
				RandomTestUtil.randomString());
		}

		Page<BrokenLinkAsset> brokenLinkAssetsPage =
			brokenLinkAssetResource.getBrokenLinkAssetsPage(
				depotEntry.getDepotEntryId(), null, Pagination.of(1, 2), null);

		Assert.assertEquals(3, brokenLinkAssetsPage.getTotalCount());

		List<BrokenLinkAsset> brokenLinkAssets =
			(List<BrokenLinkAsset>)brokenLinkAssetsPage.getItems();

		Assert.assertEquals(
			brokenLinkAssets.toString(), 2, brokenLinkAssets.size());

		brokenLinkAssetsPage = brokenLinkAssetResource.getBrokenLinkAssetsPage(
			depotEntry.getDepotEntryId(), null, Pagination.of(2, 2), null);

		Assert.assertEquals(3, brokenLinkAssetsPage.getTotalCount());

		brokenLinkAssets =
			(List<BrokenLinkAsset>)brokenLinkAssetsPage.getItems();

		Assert.assertEquals(
			brokenLinkAssets.toString(), 1, brokenLinkAssets.size());
	}

	@Override
	@Test
	public void testGetBrokenLinkAssetsPageWithSortDateTime() throws Exception {
	}

	@Override
	@Test
	public void testGetBrokenLinkAssetsPageWithSortDouble() throws Exception {
	}

	@Override
	@Test
	public void testGetBrokenLinkAssetsPageWithSortInteger() throws Exception {
	}

	@Override
	@Test
	public void testGetBrokenLinkAssetsPageWithSortString() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getBasicWebContentObjectDefinition();

		ObjectEntry expiredObjectEntry = _addExpiredObjectEntry(
			depotEntry, objectDefinition, serviceContext);

		String imageHTML = CMSOutboundLinkTestUtil.getImageHTML(
			expiredObjectEntry.getExternalReferenceCode());

		_addObjectEntry(imageHTML, depotEntry, objectDefinition, "aaa");
		_addObjectEntry(imageHTML, depotEntry, objectDefinition, "bbb");

		_assertTitleOrder(depotEntry, "aaa", "bbb", "title:asc");
		_assertTitleOrder(depotEntry, "bbb", "aaa", "title:desc");
	}

	private ObjectEntry _addExpiredObjectEntry(
			DepotEntry depotEntry, ObjectDefinition objectDefinition,
			ServiceContext serviceContext)
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(
			RandomTestUtil.randomString(), depotEntry, objectDefinition,
			RandomTestUtil.randomString());

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			WorkflowConstants.STATUS_EXPIRED, serviceContext);

		return objectEntry;
	}

	private ObjectEntry _addObjectEntry(
			String content, DepotEntry depotEntry,
			ObjectDefinition objectDefinition, String title)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", depotEntry.getGroupId(),
					depotEntry.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"content_i18n",
				HashMapBuilder.put(
					"en_US", content
				).build()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", title
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private DepotEntry _addSpaceDepotEntry(ServiceContext serviceContext)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE, serviceContext);

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _assertTitleOrder(
			DepotEntry depotEntry, String expectedFirstTitle,
			String expectedSecondTitle, String sortString)
		throws Exception {

		Page<BrokenLinkAsset> brokenLinkAssetsPage =
			brokenLinkAssetResource.getBrokenLinkAssetsPage(
				depotEntry.getDepotEntryId(), null, null, sortString);

		List<BrokenLinkAsset> brokenLinkAssets =
			(List<BrokenLinkAsset>)brokenLinkAssetsPage.getItems();

		Assert.assertEquals(
			brokenLinkAssets.toString(), 2, brokenLinkAssets.size());

		BrokenLinkAsset firstBrokenLinkAsset = brokenLinkAssets.get(0);

		Assert.assertEquals(
			expectedFirstTitle, firstBrokenLinkAsset.getTitle());

		BrokenLinkAsset secondBrokenLinkAsset = brokenLinkAssets.get(1);

		Assert.assertEquals(
			expectedSecondTitle, secondBrokenLinkAsset.getTitle());
	}

	private ObjectDefinition _getBasicWebContentObjectDefinition()
		throws Exception {

		return _objectDefinitionLocalService.
			getObjectDefinitionByExternalReferenceCode(
				"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());
	}

	private void _testGetBrokenLinkAssetsPage(String... targetTitles)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getBasicWebContentObjectDefinition();

		StringBundler sb = new StringBundler(targetTitles.length);

		for (String targetTitle : targetTitles) {
			ObjectEntry targetObjectEntry = _addObjectEntry(
				RandomTestUtil.randomString(), depotEntry, objectDefinition,
				targetTitle);

			sb.append(
				CMSOutboundLinkTestUtil.getImageHTML(
					targetObjectEntry.getExternalReferenceCode()));

			_objectEntryLocalService.updateStatus(
				TestPropsValues.getUserId(),
				targetObjectEntry.getObjectEntryId(),
				WorkflowConstants.STATUS_EXPIRED, serviceContext);
		}

		String referencingTitle = RandomTestUtil.randomString();

		_addObjectEntry(
			sb.toString(), depotEntry, objectDefinition, referencingTitle);

		Page<BrokenLinkAsset> brokenLinkAssetsPage =
			brokenLinkAssetResource.getBrokenLinkAssetsPage(
				depotEntry.getDepotEntryId(), null, null, null);

		Assert.assertEquals(1, brokenLinkAssetsPage.getTotalCount());

		List<BrokenLinkAsset> brokenLinkAssets =
			(List<BrokenLinkAsset>)brokenLinkAssetsPage.getItems();

		BrokenLinkAsset brokenLinkAsset = brokenLinkAssets.get(0);

		Assert.assertEquals(
			targetTitles.length,
			GetterUtil.getInteger(brokenLinkAsset.getBrokenLinksCount()));
		Assert.assertEquals(
			"L_CMS_BASIC_WEB_CONTENT",
			brokenLinkAsset.getObjectDefinitionExternalReferenceCode());
		Assert.assertEquals(referencingTitle, brokenLinkAsset.getTitle());

		if (targetTitles.length == 1) {
			Assert.assertEquals(
				targetTitles[0], brokenLinkAsset.getBrokenLinkTitle());
		}
	}

	private void _testGetBrokenLinkAssetsPageWithDuplicateTitles()
		throws Exception {

		String targetTitle = RandomTestUtil.randomString();

		_testGetBrokenLinkAssetsPage(targetTitle, targetTitle);
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}