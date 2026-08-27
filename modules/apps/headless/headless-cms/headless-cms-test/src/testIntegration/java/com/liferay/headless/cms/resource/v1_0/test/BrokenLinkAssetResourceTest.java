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
import com.liferay.headless.cms.client.resource.v1_0.BrokenLinkAssetResource;
import com.liferay.headless.cms.resource.v1_0.test.util.CMSFreeTierTestUtil;
import com.liferay.headless.cms.resource.v1_0.test.util.CMSOutboundLinkTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
		_testGetBrokenLinkAssetsPageWithExpiredAssetInAnotherSpace();
		_testGetBrokenLinkAssetsPageWithExpiredAssetInHiddenSpace();
		_testGetBrokenLinkAssetsPageWithFreeTier();
		_testGetBrokenLinkAssetsPageWithoutUpdatePermission();
		_testGetBrokenLinkAssetsPageWithRelationshipReference();
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

	private ObjectDefinition _addCMSObjectDefinition() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, false, false, ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(_buildRichTextObjectField()),
				objectFolder.getObjectFolderId(),
				ObjectDefinitionConstants.SCOPE_DEPOT,
				TestPropsValues.getUserId());

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS, "true");

		_objectDefinitions.add(objectDefinition);

		return objectDefinition;
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
			DepotEntry depotEntry, ObjectDefinition objectDefinition,
			Map<String, Serializable> values)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", depotEntry.getGroupId(),
					depotEntry.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), "en_US", values,
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addObjectEntry(
			String content, DepotEntry depotEntry,
			ObjectDefinition objectDefinition, String title)
		throws Exception {

		return _addObjectEntry(
			depotEntry, objectDefinition,
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
			).build());
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

	private ObjectField _buildRichTextObjectField() throws Exception {
		return new RichTextObjectFieldBuilder(
		).indexed(
			true
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			"content"
		).userId(
			TestPropsValues.getUserId()
		).build();
	}

	private ObjectDefinition _getBasicWebContentObjectDefinition()
		throws Exception {

		return _objectDefinitionLocalService.
			getObjectDefinitionByExternalReferenceCode(
				"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());
	}

	private BrokenLinkAsset _getSingleBrokenLinkAsset(
			BrokenLinkAssetResource brokenLinkAssetResource,
			DepotEntry depotEntry)
		throws Exception {

		Page<BrokenLinkAsset> brokenLinkAssetsPage =
			brokenLinkAssetResource.getBrokenLinkAssetsPage(
				depotEntry.getDepotEntryId(), null, null, null);

		Assert.assertEquals(1, brokenLinkAssetsPage.getTotalCount());

		List<BrokenLinkAsset> brokenLinkAssets =
			(List<BrokenLinkAsset>)brokenLinkAssetsPage.getItems();

		return brokenLinkAssets.get(0);
	}

	private BrokenLinkAssetResource _getSpaceMemberBrokenLinkAssetResource(
			DepotEntry depotEntry)
		throws Exception {

		String password = RandomTestUtil.randomString();

		User spaceMemberUser = UserTestUtil.addUser(testCompany, password);

		_users.add(spaceMemberUser);

		_userLocalService.addGroupUsers(
			depotEntry.getGroupId(), new long[] {spaceMemberUser.getUserId()});

		return BrokenLinkAssetResource.builder(
		).authentication(
			spaceMemberUser.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
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

		BrokenLinkAsset brokenLinkAsset = _getSingleBrokenLinkAsset(
			brokenLinkAssetResource, depotEntry);

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

	private void _testGetBrokenLinkAssetsPageWithExpiredAssetInAnotherSpace()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry = _addSpaceDepotEntry(serviceContext);
		DepotEntry otherDepotEntry = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getBasicWebContentObjectDefinition();

		ObjectEntry expiredObjectEntry = _addExpiredObjectEntry(
			otherDepotEntry, objectDefinition, serviceContext);

		String referencingTitle = RandomTestUtil.randomString();

		_addObjectEntry(
			CMSOutboundLinkTestUtil.getImageHTML(
				expiredObjectEntry.getExternalReferenceCode()),
			depotEntry, objectDefinition, referencingTitle);

		BrokenLinkAsset brokenLinkAsset = _getSingleBrokenLinkAsset(
			brokenLinkAssetResource, depotEntry);

		Assert.assertEquals(
			1, GetterUtil.getInteger(brokenLinkAsset.getBrokenLinksCount()));
		Assert.assertEquals(referencingTitle, brokenLinkAsset.getTitle());
	}

	private void _testGetBrokenLinkAssetsPageWithExpiredAssetInHiddenSpace()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry = _addSpaceDepotEntry(serviceContext);
		DepotEntry hiddenDepotEntry = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getBasicWebContentObjectDefinition();

		ObjectEntry expiredObjectEntry = _addExpiredObjectEntry(
			depotEntry, objectDefinition, serviceContext);
		ObjectEntry hiddenExpiredObjectEntry = _addExpiredObjectEntry(
			hiddenDepotEntry, objectDefinition, serviceContext);

		String imageHTML = CMSOutboundLinkTestUtil.getImageHTML(
			expiredObjectEntry.getExternalReferenceCode());
		String hiddenImageHTML = CMSOutboundLinkTestUtil.getImageHTML(
			hiddenExpiredObjectEntry.getExternalReferenceCode());

		String referencingTitle = RandomTestUtil.randomString();

		_addObjectEntry(
			imageHTML + hiddenImageHTML, depotEntry, objectDefinition,
			referencingTitle);

		BrokenLinkAsset brokenLinkAsset = _getSingleBrokenLinkAsset(
			brokenLinkAssetResource, depotEntry);

		Assert.assertEquals(
			2, GetterUtil.getInteger(brokenLinkAsset.getBrokenLinksCount()));
		Assert.assertEquals(referencingTitle, brokenLinkAsset.getTitle());

		BrokenLinkAsset spaceMemberBrokenLinkAsset = _getSingleBrokenLinkAsset(
			_getSpaceMemberBrokenLinkAssetResource(depotEntry), depotEntry);

		Assert.assertEquals(
			1,
			GetterUtil.getInteger(
				spaceMemberBrokenLinkAsset.getBrokenLinksCount()));
		Assert.assertEquals(
			expiredObjectEntry.getTitleValue("en_US", true),
			spaceMemberBrokenLinkAsset.getBrokenLinkTitle());
		Assert.assertEquals(
			referencingTitle, spaceMemberBrokenLinkAsset.getTitle());
	}

	private void _testGetBrokenLinkAssetsPageWithFreeTier() throws Exception {
		try (AutoCloseable autoCloseable = CMSFreeTierTestUtil.withFreeTier()) {
			assertHttpResponseStatusCode(
				400,
				brokenLinkAssetResource.getBrokenLinkAssetsPageHttpResponse(
					null, null, Pagination.of(1, 20), null));
		}

		assertHttpResponseStatusCode(
			200,
			brokenLinkAssetResource.getBrokenLinkAssetsPageHttpResponse(
				null, null, Pagination.of(1, 20), null));
	}

	private void _testGetBrokenLinkAssetsPageWithoutUpdatePermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getBasicWebContentObjectDefinition();

		ObjectEntry expiredObjectEntry = _addExpiredObjectEntry(
			depotEntry, objectDefinition, serviceContext);

		_addObjectEntry(
			CMSOutboundLinkTestUtil.getImageHTML(
				expiredObjectEntry.getExternalReferenceCode()),
			depotEntry, objectDefinition, RandomTestUtil.randomString());

		BrokenLinkAsset brokenLinkAsset = _getSingleBrokenLinkAsset(
			brokenLinkAssetResource, depotEntry);

		Map<String, Map<String, String>> actions = brokenLinkAsset.getActions();

		Assert.assertNotNull(actions.get("update"));

		BrokenLinkAsset spaceMemberBrokenLinkAsset = _getSingleBrokenLinkAsset(
			_getSpaceMemberBrokenLinkAssetResource(depotEntry), depotEntry);

		Map<String, Map<String, String>> spaceMemberActions =
			spaceMemberBrokenLinkAsset.getActions();

		Assert.assertNull(spaceMemberActions.get("update"));
	}

	private void _testGetBrokenLinkAssetsPageWithRelationshipReference()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry = _addSpaceDepotEntry(serviceContext);

		ObjectDefinition objectDefinition =
			_getBasicWebContentObjectDefinition();

		ObjectEntry expiredObjectEntry = _addExpiredObjectEntry(
			depotEntry, objectDefinition, serviceContext);

		ObjectDefinition referencingObjectDefinition =
			_addCMSObjectDefinition();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition,
				referencingObjectDefinition);

		_addObjectEntry(
			depotEntry, referencingObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				ObjectRelationshipUtil.getObjectRelationshipFieldName(
					objectDefinition, objectRelationship.getName()),
				expiredObjectEntry.getObjectEntryId()
			).build());

		BrokenLinkAsset brokenLinkAsset = _getSingleBrokenLinkAsset(
			brokenLinkAssetResource, depotEntry);

		Assert.assertEquals(
			1, GetterUtil.getInteger(brokenLinkAsset.getBrokenLinksCount()));
		Assert.assertEquals(
			expiredObjectEntry.getTitleValue("en_US", true),
			brokenLinkAsset.getBrokenLinkTitle());
		Assert.assertEquals(
			referencingObjectDefinition.getExternalReferenceCode(),
			brokenLinkAsset.getObjectDefinitionExternalReferenceCode());
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}