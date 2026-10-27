/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.headless.cms.client.dto.v1_0.UnlinkedAsset;
import com.liferay.headless.cms.client.pagination.Page;
import com.liferay.headless.cms.resource.v1_0.test.util.CMSOutboundLinkTestUtil;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@FeatureFlag("LPD-82226")
@RunWith(Arquillian.class)
public class UnlinkedAssetResourceTest
	extends BaseUnlinkedAssetResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testEscapeRegexInStringFields() throws Exception {
	}

	@Override
	@Test
	@TestInfo("LPD-105769")
	public void testGetUnlinkedAssetsPage() throws Exception {
		_testGetUnlinkedAssetsPageWithAssetLink();
		_testGetUnlinkedAssetsPageWithLayoutClassedModelUsage();
		_testGetUnlinkedAssetsPageWithManyToManyRelationship();
		_testGetUnlinkedAssetsPageWithNavigationMenu();
		_testGetUnlinkedAssetsPageWithOutboundLink();
	}

	@Override
	@Test
	public void testGetUnlinkedAssetsPageWithPagination() throws Exception {
	}

	@Override
	@Test
	public void testGetUnlinkedAssetsPageWithSortDateTime() throws Exception {
	}

	@Override
	@Test
	public void testGetUnlinkedAssetsPageWithSortDouble() throws Exception {
	}

	@Override
	@Test
	public void testGetUnlinkedAssetsPageWithSortInteger() throws Exception {
	}

	@Override
	@Test
	public void testGetUnlinkedAssetsPageWithSortString() throws Exception {
	}

	private ObjectDefinition _addCMSObjectDefinition() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, false, true, ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"title"
					).build()),
				objectFolder.getObjectFolderId(),
				ObjectDefinitionConstants.SCOPE_DEPOT,
				TestPropsValues.getUserId());

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS, "true");

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), "title");

		return _objectDefinitionLocalService.updateTitleObjectFieldId(
			objectDefinition.getObjectDefinitionId(),
			objectField.getObjectFieldId());
	}

	private ObjectEntry _addObjectEntry(
			DepotEntry depotEntry, ObjectDefinition objectDefinition)
		throws Exception {

		return _addObjectEntry(
			depotEntry, objectDefinition, Collections.emptyMap());
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
			depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"title", RandomTestUtil.randomString()
			).putAll(
				values
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private DepotEntry _addSpaceDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertUnlinkedAssetsCount(
			DepotEntry depotEntry, long expectedTotalCount)
		throws Exception {

		Page<UnlinkedAsset> unlinkedAssetsPage =
			unlinkedAssetResource.getUnlinkedAssetsPage(
				depotEntry.getDepotEntryId(), null, null, null);

		Assert.assertEquals(
			expectedTotalCount, unlinkedAssetsPage.getTotalCount());
	}

	private long _getAssetEntryId(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		return assetEntry.getEntryId();
	}

	private void _testGetUnlinkedAssetsPageWithAssetLink() throws Exception {
		DepotEntry depotEntry = _addSpaceDepotEntry();

		ObjectDefinition objectDefinition = _addCMSObjectDefinition();

		ObjectEntry objectEntry1 = _addObjectEntry(
			depotEntry, objectDefinition);
		ObjectEntry objectEntry2 = _addObjectEntry(
			depotEntry, objectDefinition);

		_addObjectEntry(depotEntry, objectDefinition);

		_assertUnlinkedAssetsCount(depotEntry, 3);

		_assetLinkLocalService.addLink(
			TestPropsValues.getUserId(),
			_getAssetEntryId(objectDefinition, objectEntry1),
			_getAssetEntryId(objectDefinition, objectEntry2),
			AssetLinkConstants.TYPE_RELATED, 0);

		_assertUnlinkedAssetsCount(depotEntry, 1);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private void _testGetUnlinkedAssetsPageWithLayoutClassedModelUsage()
		throws Exception {

		DepotEntry depotEntry = _addSpaceDepotEntry();

		ObjectDefinition objectDefinition = _addCMSObjectDefinition();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry, objectDefinition);

		_addObjectEntry(depotEntry, objectDefinition);

		_assertUnlinkedAssetsCount(depotEntry, 2);

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			testGroup.getGroupId(), objectEntry.getExternalReferenceCode(),
			_portal.getClassNameId(DLFileEntry.class),
			objectEntry.getObjectEntryId(), RandomTestUtil.randomString(),
			_portal.getClassNameId(FragmentEntryLink.class), layout.getPlid(),
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		_assertUnlinkedAssetsCount(depotEntry, 2);

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			testGroup.getGroupId(), objectEntry.getExternalReferenceCode(),
			_portal.getClassNameId(objectDefinition.getClassName()),
			objectEntry.getObjectEntryId(), RandomTestUtil.randomString(),
			_portal.getClassNameId(FragmentEntryLink.class), layout.getPlid(),
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		_assertUnlinkedAssetsCount(depotEntry, 1);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private void _testGetUnlinkedAssetsPageWithManyToManyRelationship()
		throws Exception {

		DepotEntry depotEntry = _addSpaceDepotEntry();

		ObjectDefinition objectDefinition = _addCMSObjectDefinition();

		ObjectEntry objectEntry1 = _addObjectEntry(
			depotEntry, objectDefinition);
		ObjectEntry objectEntry2 = _addObjectEntry(
			depotEntry, objectDefinition);

		_addObjectEntry(depotEntry, objectDefinition);

		_assertUnlinkedAssetsCount(depotEntry, 3);

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition,
				objectDefinition,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
				StringUtil.randomId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			objectEntry1.getObjectEntryId(), objectEntry2.getObjectEntryId(),
			objectRelationship, TestPropsValues.getUserId());

		_assertUnlinkedAssetsCount(depotEntry, 1);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private void _testGetUnlinkedAssetsPageWithNavigationMenu()
		throws Exception {

		DepotEntry depotEntry = _addSpaceDepotEntry();

		ObjectDefinition objectDefinition = _addCMSObjectDefinition();

		ObjectEntry objectEntry = _addObjectEntry(depotEntry, objectDefinition);

		_addObjectEntry(depotEntry, objectDefinition);

		_assertUnlinkedAssetsCount(depotEntry, 2);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			siteNavigationMenu.getSiteNavigationMenuId(), 0,
			objectDefinition.getClassName(),
			UnicodePropertiesBuilder.create(
				true
			).put(
				"externalReferenceCode", objectEntry.getExternalReferenceCode()
			).buildString(),
			serviceContext);

		_assertUnlinkedAssetsCount(depotEntry, 1);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	private void _testGetUnlinkedAssetsPageWithOutboundLink() throws Exception {
		DepotEntry depotEntry = _addSpaceDepotEntry();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());

		ObjectEntry objectEntry = _addObjectEntry(
			depotEntry, objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build());

		_addObjectEntry(
			depotEntry, objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build());

		_assertUnlinkedAssetsCount(depotEntry, 2);

		_addObjectEntry(
			depotEntry, objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"content_i18n",
				HashMapBuilder.put(
					"en_US",
					CMSOutboundLinkTestUtil.getImageHTML(
						objectEntry.getExternalReferenceCode())
				).build()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build());

		_assertUnlinkedAssetsCount(depotEntry, 2);

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetLinkLocalService _assetLinkLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}