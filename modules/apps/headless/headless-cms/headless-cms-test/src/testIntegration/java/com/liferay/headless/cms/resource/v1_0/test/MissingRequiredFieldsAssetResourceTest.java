/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.cms.client.dto.v1_0.AssetStatistics;
import com.liferay.headless.cms.client.dto.v1_0.MissingRequiredFieldsAsset;
import com.liferay.headless.cms.client.pagination.Page;
import com.liferay.headless.cms.client.pagination.Pagination;
import com.liferay.headless.cms.client.resource.v1_0.AssetStatisticsResource;
import com.liferay.headless.cms.client.resource.v1_0.MissingRequiredFieldsAssetResource;
import com.liferay.headless.cms.resource.v1_0.test.util.CMSFreeTierTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
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
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
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
 * @author Mikel Lorza
 */
@FeatureFlag("LPD-82226")
@RunWith(Arquillian.class)
public class MissingRequiredFieldsAssetResourceTest
	extends BaseMissingRequiredFieldsAssetResourceTestCase {

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
	public void testGetMissingRequiredFieldsAssetsPage() throws Exception {
		DepotEntry depotEntry = _addSpaceDepotEntry();

		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);

		ObjectEntry objectEntry = _addObjectEntry(
			depotEntry, objectDefinition, RandomTestUtil.randomString());

		_assertMissingRequiredFieldsAssetsCount(depotEntry, 0);

		_addRequiredObjectField(objectDefinition);

		Page<MissingRequiredFieldsAsset> missingRequiredFieldsAssetsPage =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					depotEntry.getDepotEntryId(), null, null, null);

		Assert.assertEquals(1, missingRequiredFieldsAssetsPage.getTotalCount());

		List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets =
			(List<MissingRequiredFieldsAsset>)
				missingRequiredFieldsAssetsPage.getItems();

		MissingRequiredFieldsAsset missingRequiredFieldsAsset =
			missingRequiredFieldsAssets.get(0);

		Assert.assertEquals(
			Long.valueOf(objectEntry.getObjectEntryId()),
			missingRequiredFieldsAsset.getId());
		Assert.assertEquals(
			Long.valueOf(1),
			missingRequiredFieldsAsset.getMissingRequiredFieldsCount());
		Assert.assertEquals(
			objectDefinition.getExternalReferenceCode(),
			missingRequiredFieldsAsset.
				getObjectDefinitionExternalReferenceCode());
		Assert.assertEquals(
			objectEntry.getTitleValue(), missingRequiredFieldsAsset.getTitle());
		Assert.assertTrue(
			missingRequiredFieldsAsset.getHref(),
			StringUtil.endsWith(
				missingRequiredFieldsAsset.getHref(),
				"&objectEntryId=" + objectEntry.getObjectEntryId()));

		Map<String, Map<String, String>> actions =
			missingRequiredFieldsAsset.getActions();

		Assert.assertNotNull(actions.get("update"));

		MissingRequiredFieldsAssetResource
			spaceMemberMissingRequiredFieldsAssetResource =
				_getSpaceMemberMissingRequiredFieldsAssetResource(depotEntry);

		missingRequiredFieldsAssetsPage =
			spaceMemberMissingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					depotEntry.getDepotEntryId(), null, null, null);

		missingRequiredFieldsAssets =
			(List<MissingRequiredFieldsAsset>)
				missingRequiredFieldsAssetsPage.getItems();

		missingRequiredFieldsAsset = missingRequiredFieldsAssets.get(0);

		actions = missingRequiredFieldsAsset.getActions();

		Assert.assertNull(actions.get("update"));

		_assertMissingRequiredFieldsAssetsCount(_addSpaceDepotEntry(), 0);

		_testGetMissingRequiredFieldsAssetsPageWithFiles();
		_testGetMissingRequiredFieldsAssetsPageWithFreeTier();
		_testGetMissingRequiredFieldsAssetsPageWithSearch();
		_testGetMissingRequiredFieldsAssetsPageWithStatuses();
	}

	@Override
	@Test
	public void testGetMissingRequiredFieldsAssetsPageWithPagination()
		throws Exception {

		DepotEntry depotEntry = _addSpaceDepotEntry();

		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);

		for (int i = 0; i < 3; i++) {
			_addObjectEntry(
				depotEntry, objectDefinition, RandomTestUtil.randomString());
		}

		_addRequiredObjectField(objectDefinition);

		Page<MissingRequiredFieldsAsset> missingRequiredFieldsAssetsPage =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					depotEntry.getDepotEntryId(), null, Pagination.of(1, 2),
					null);

		Assert.assertEquals(3, missingRequiredFieldsAssetsPage.getTotalCount());

		List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets =
			(List<MissingRequiredFieldsAsset>)
				missingRequiredFieldsAssetsPage.getItems();

		Assert.assertEquals(
			missingRequiredFieldsAssets.toString(), 2,
			missingRequiredFieldsAssets.size());

		missingRequiredFieldsAssetsPage =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					depotEntry.getDepotEntryId(), null, Pagination.of(2, 2),
					null);

		missingRequiredFieldsAssets =
			(List<MissingRequiredFieldsAsset>)
				missingRequiredFieldsAssetsPage.getItems();

		Assert.assertEquals(
			missingRequiredFieldsAssets.toString(), 1,
			missingRequiredFieldsAssets.size());
	}

	@Override
	@Test
	public void testGetMissingRequiredFieldsAssetsPageWithSortDateTime()
		throws Exception {
	}

	@Override
	@Test
	public void testGetMissingRequiredFieldsAssetsPageWithSortDouble()
		throws Exception {
	}

	@Override
	@Test
	public void testGetMissingRequiredFieldsAssetsPageWithSortInteger()
		throws Exception {
	}

	@Override
	@Test
	public void testGetMissingRequiredFieldsAssetsPageWithSortString()
		throws Exception {

		DepotEntry depotEntry = _addSpaceDepotEntry();

		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);

		_addObjectEntry(depotEntry, objectDefinition, "aaa");
		_addObjectEntry(depotEntry, objectDefinition, "bbb");

		_addRequiredObjectField(objectDefinition);

		_assertTitleOrder(depotEntry, "aaa", "bbb", "title:asc");
		_assertTitleOrder(depotEntry, "bbb", "aaa", "title:desc");
	}

	private ObjectDefinition _addCMSObjectDefinition(
			String objectFolderExternalReferenceCode)
		throws Exception {

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				objectFolderExternalReferenceCode,
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

		_objectDefinitions.add(objectDefinition);

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
			DepotEntry depotEntry, ObjectDefinition objectDefinition,
			String title)
		throws Exception {

		return _addObjectEntry(
			depotEntry, objectDefinition,
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS, title,
			WorkflowConstants.ACTION_PUBLISH);
	}

	private ObjectEntry _addObjectEntry(
			DepotEntry depotEntry, ObjectDefinition objectDefinition,
			String objectEntryFolderExternalReferenceCode, String title,
			int workflowAction)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					objectEntryFolderExternalReferenceCode,
					depotEntry.getGroupId(), depotEntry.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"title", title
			).build(),
			_getServiceContext(workflowAction));
	}

	private void _addRequiredObjectField(ObjectDefinition objectDefinition)
		throws Exception {

		_objectFieldLocalService.addCustomObjectField(
			null, TestPropsValues.getUserId(), 0,
			objectDefinition.getObjectDefinitionId(),
			ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
			ObjectFieldConstants.DB_TYPE_INTEGER, null, false, false, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			false, "integer", null, null, true, false, Collections.emptyList());
	}

	private DepotEntry _addSpaceDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _assertMissingRequiredFieldsAssetsCount(
			DepotEntry depotEntry, long expectedTotalCount)
		throws Exception {

		Page<MissingRequiredFieldsAsset> missingRequiredFieldsAssetsPage =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					depotEntry.getDepotEntryId(), null, null, null);

		Assert.assertEquals(
			expectedTotalCount,
			missingRequiredFieldsAssetsPage.getTotalCount());
	}

	private void _assertTitleOrder(
			DepotEntry depotEntry, String expectedFirstTitle,
			String expectedSecondTitle, String sortString)
		throws Exception {

		Page<MissingRequiredFieldsAsset> missingRequiredFieldsAssetsPage =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					depotEntry.getDepotEntryId(), null, null, sortString);

		List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets =
			(List<MissingRequiredFieldsAsset>)
				missingRequiredFieldsAssetsPage.getItems();

		Assert.assertEquals(
			missingRequiredFieldsAssets.toString(), 2,
			missingRequiredFieldsAssets.size());

		MissingRequiredFieldsAsset firstMissingRequiredFieldsAsset =
			missingRequiredFieldsAssets.get(0);

		Assert.assertEquals(
			expectedFirstTitle, firstMissingRequiredFieldsAsset.getTitle());

		MissingRequiredFieldsAsset secondMissingRequiredFieldsAsset =
			missingRequiredFieldsAssets.get(1);

		Assert.assertEquals(
			expectedSecondTitle, secondMissingRequiredFieldsAsset.getTitle());
	}

	private ServiceContext _getServiceContext(int workflowAction)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(workflowAction);

		return serviceContext;
	}

	private MissingRequiredFieldsAssetResource
			_getSpaceMemberMissingRequiredFieldsAssetResource(
				DepotEntry depotEntry)
		throws Exception {

		String password = RandomTestUtil.randomString();

		User spaceMemberUser = UserTestUtil.addUser(testCompany, password);

		_users.add(spaceMemberUser);

		_userLocalService.addGroupUsers(
			depotEntry.getGroupId(), new long[] {spaceMemberUser.getUserId()});

		return MissingRequiredFieldsAssetResource.builder(
		).authentication(
			spaceMemberUser.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _testGetMissingRequiredFieldsAssetsPageWithFiles()
		throws Exception {

		DepotEntry depotEntry = _addSpaceDepotEntry();
		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES);

		ObjectEntry objectEntry = _addObjectEntry(
			depotEntry, objectDefinition,
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
			RandomTestUtil.randomString(), WorkflowConstants.ACTION_PUBLISH);

		_addRequiredObjectField(objectDefinition);

		Page<MissingRequiredFieldsAsset> missingRequiredFieldsAssetsPage =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					depotEntry.getDepotEntryId(), null, null, null);

		List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets =
			(List<MissingRequiredFieldsAsset>)
				missingRequiredFieldsAssetsPage.getItems();

		Assert.assertEquals(
			missingRequiredFieldsAssets.toString(), 1,
			missingRequiredFieldsAssets.size());

		MissingRequiredFieldsAsset missingRequiredFieldsAsset =
			missingRequiredFieldsAssets.get(0);

		Assert.assertEquals(
			Long.valueOf(objectEntry.getObjectEntryId()),
			missingRequiredFieldsAsset.getId());
		Assert.assertEquals(
			objectDefinition.getExternalReferenceCode(),
			missingRequiredFieldsAsset.
				getObjectDefinitionExternalReferenceCode());
	}

	private void _testGetMissingRequiredFieldsAssetsPageWithFreeTier()
		throws Exception {

		try (AutoCloseable autoCloseable = CMSFreeTierTestUtil.withFreeTier()) {
			assertHttpResponseStatusCode(
				400,
				missingRequiredFieldsAssetResource.
					getMissingRequiredFieldsAssetsPageHttpResponse(
						null, null, Pagination.of(1, 20), null));
		}

		assertHttpResponseStatusCode(
			200,
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPageHttpResponse(
					null, null, Pagination.of(1, 20), null));
	}

	private void _testGetMissingRequiredFieldsAssetsPageWithSearch()
		throws Exception {

		DepotEntry depotEntry = _addSpaceDepotEntry();
		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);
		String title = RandomTestUtil.randomString();

		_addObjectEntry(depotEntry, objectDefinition, title);

		_addObjectEntry(
			depotEntry, objectDefinition, RandomTestUtil.randomString());

		_addRequiredObjectField(objectDefinition);

		Page<MissingRequiredFieldsAsset> missingRequiredFieldsAssetsPage =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					depotEntry.getDepotEntryId(), title, null, null);

		List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets =
			(List<MissingRequiredFieldsAsset>)
				missingRequiredFieldsAssetsPage.getItems();

		Assert.assertEquals(
			missingRequiredFieldsAssets.toString(), 1,
			missingRequiredFieldsAssets.size());

		MissingRequiredFieldsAsset missingRequiredFieldsAsset =
			missingRequiredFieldsAssets.get(0);

		Assert.assertEquals(title, missingRequiredFieldsAsset.getTitle());
	}

	private void _testGetMissingRequiredFieldsAssetsPageWithStatuses()
		throws Exception {

		DepotEntry depotEntry = _addSpaceDepotEntry();
		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);

		ObjectEntry approvedObjectEntry = _addObjectEntry(
			depotEntry, objectDefinition, RandomTestUtil.randomString());

		_addObjectEntry(
			depotEntry, objectDefinition,
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
			RandomTestUtil.randomString(), WorkflowConstants.ACTION_SAVE_DRAFT);

		ObjectEntry draftOverApprovedObjectEntry = _addObjectEntry(
			depotEntry, objectDefinition, RandomTestUtil.randomString());
		ObjectEntry expiredObjectEntry = _addObjectEntry(
			depotEntry, objectDefinition, RandomTestUtil.randomString());
		ObjectEntry inTrashObjectEntry = _addObjectEntry(
			depotEntry, objectDefinition, RandomTestUtil.randomString());

		_addRequiredObjectField(objectDefinition);

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(),
			draftOverApprovedObjectEntry.getObjectEntryId(),
			draftOverApprovedObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"integer", RandomTestUtil.randomInt()
			).put(
				"title", RandomTestUtil.randomString()
			).build(),
			_getServiceContext(WorkflowConstants.ACTION_SAVE_DRAFT));
		_objectEntryLocalService.expireObjectEntry(
			TestPropsValues.getUserId(), expiredObjectEntry.getObjectEntryId(),
			ServiceContextTestUtil.getServiceContext());
		_objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(), inTrashObjectEntry,
			ServiceContextTestUtil.getServiceContext());

		Page<MissingRequiredFieldsAsset> missingRequiredFieldsAssetsPage =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					depotEntry.getDepotEntryId(), null, null, null);

		Assert.assertEquals(
			SetUtil.fromArray(
				approvedObjectEntry.getObjectEntryId(),
				draftOverApprovedObjectEntry.getObjectEntryId()),
			SetUtil.fromCollection(
				TransformUtil.transform(
					missingRequiredFieldsAssetsPage.getItems(),
					MissingRequiredFieldsAsset::getId)));

		User adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		AssetStatisticsResource assetStatisticsResource =
			AssetStatisticsResource.builder(
			).authentication(
				adminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		AssetStatistics assetStatistics =
			assetStatisticsResource.getAssetStatistics(
				depotEntry.getDepotEntryId());

		Assert.assertEquals(
			missingRequiredFieldsAssetsPage.getTotalCount(),
			GetterUtil.getLong(
				assetStatistics.getMissingRequiredFieldsCount()));
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@DeleteAfterTestRun
	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}