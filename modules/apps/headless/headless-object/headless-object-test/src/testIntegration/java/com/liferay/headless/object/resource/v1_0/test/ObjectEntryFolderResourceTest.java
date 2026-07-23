/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.object.client.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.client.dto.v1_0.Status;
import com.liferay.headless.object.client.pagination.Page;
import com.liferay.headless.object.client.pagination.Pagination;
import com.liferay.headless.object.client.problem.Problem;
import com.liferay.headless.object.client.resource.v1_0.ObjectEntryFolderResource;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalServiceWrapper;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.test.rule.Inject;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class ObjectEntryFolderResourceTest
	extends BaseObjectEntryFolderResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseObjectEntryFolderResourceTestCase.setUpClass();
		CMSTestUtil.getOrAddGroup(ObjectEntryFolderResourceTest.class);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_testDepotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testGroup.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});

		_testDepotEntryGroup = _groupLocalService.getGroup(
			_testDepotEntry.getGroupId());

		_updateGroup(false);
	}

	@Override
	@Test
	public void testGetObjectEntryFolder() throws Exception {
		super.testGetObjectEntryFolder();

		_testGetObjectEntryFolderActionsWithCompanySharingDisabled();
		_testGetObjectEntryFolderActionsWithGroupSharingDisabled();
		_testGetObjectEntryFolderActionsWithSharingEnabled();
		_testGetObjectEntryFolderActionsWithSystemSharingDisabled();
		_testGetObjectEntryFolderActionsWithoutUpdatePermission();
		_testGetObjectEntryFolderShareAction();
	}

	@Override
	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPage() throws Exception {
		super.testGetScopeScopeKeyObjectEntryFoldersPage();

		_testGetScopeScopeKeyObjectEntryFoldersPageWithGroupKey();
	}

	@Override
	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String scopeKey =
			testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey();

		ObjectEntryFolder objectEntryFolder1 = randomObjectEntryFolder();

		objectEntryFolder1 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, objectEntryFolder1);

		EntityField titleEntityField = new StringEntityField(
			"title", locale -> Field.getSortableFieldName(Field.TITLE));

		for (EntityField entityField : entityFields) {
			Page<ObjectEntryFolder> page =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null,
						StringBundler.concat(
							getFilterString(
								entityField, "between", objectEntryFolder1),
							" and ",
							getFilterString(
								titleEntityField, "contains",
								objectEntryFolder1)),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectEntryFolder1),
				(List<ObjectEntryFolder>)page.getItems());
		}
	}

	@Override
	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithFilterStringEquals()
		throws Exception {

		super.
			testGetScopeScopeKeyObjectEntryFoldersPageWithFilterStringEquals();

		_testGetScopeScopeKeyObjectEntryFoldersPageWithFilterStringEqualsFolderIdAndTitle();
	}

	@Override
	@Test
	public void testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		super.testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode();

		_testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithGroupKey();
	}

	@Override
	@Test
	public void testPostScopeScopeKeyObjectEntryFolder() throws Exception {
		super.testPostScopeScopeKeyObjectEntryFolder();

		_testPostScopeScopeKeyObjectEntryFolderStatus();
		_testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderByExternalReferenceCode();
		_testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderByObjectEntryFolderId();
		_testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderDataMismatch();
		_testPostScopeScopeKeyObjectEntryFolderWithMissingParentObjectEntryFolderReference();
		_testPostScopeScopeKeyObjectEntryFolderWithNonexistentParentObjectEntryFolderByExternalReferenceCode();
		_testPostScopeScopeKeyObjectEntryFolderWithNonexistentParentObjectEntryFolderByObjectEntryFolderId();
	}

	@Override
	@Test
	public void testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore()
		throws Exception {

		super.
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore();

		_updateGroup(true);

		ObjectEntryFolder postObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore_addObjectEntryFolder(
				randomObjectEntryFolder());

		objectEntryFolderResource.
			deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
				String.valueOf(_testDepotEntry.getGroupId()),
				postObjectEntryFolder.getExternalReferenceCode());

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.getObjectEntryFolder(
				postObjectEntryFolder.getId());

		assertEquals(postObjectEntryFolder, getObjectEntryFolder);
		assertValid(getObjectEntryFolder);

		Map<String, Map<String, String>> actions =
			getObjectEntryFolder.getActions();

		Assert.assertTrue(actions.containsKey("restore"));

		Assert.assertNotNull(getObjectEntryFolder.getRemovedBy());
		Assert.assertNotNull(getObjectEntryFolder.getRemovedDate());

		Status status = getObjectEntryFolder.getStatus();

		Assert.assertEquals(
			Integer.valueOf(WorkflowConstants.STATUS_IN_TRASH),
			status.getCode());

		postObjectEntryFolder =
			objectEntryFolderResource.
				postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore(
					String.valueOf(_testDepotEntry.getGroupId()),
					postObjectEntryFolder.getExternalReferenceCode());

		assertEquals(getObjectEntryFolder, postObjectEntryFolder);
		assertValid(postObjectEntryFolder);

		actions = postObjectEntryFolder.getActions();

		Assert.assertFalse(actions.containsKey("restore"));

		Assert.assertNull(postObjectEntryFolder.getRemovedBy());
		Assert.assertNull(postObjectEntryFolder.getRemovedDate());

		status = postObjectEntryFolder.getStatus();

		Assert.assertEquals(
			Integer.valueOf(WorkflowConstants.STATUS_APPROVED),
			status.getCode());
	}

	@Override
	@Test
	public void testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe()
		throws Exception {

		super.
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe();

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {TestPropsValues.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		_addResourcePermission(ActionKeys.VIEW, user.getUserId());

		ObjectEntryFolder objectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe_addObjectEntryFolder();

		ObjectEntryFolderResource objectEntryFolderResource =
			_getObjectEntryFolderResource(user.getEmailAddress(), password);

		_assertProblemException(
			() ->
				objectEntryFolderResource.
					postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe(
						objectEntryFolder.getScopeKey(),
						objectEntryFolder.getExternalReferenceCode()));

		_addResourcePermission(ActionKeys.SUBSCRIBE, user.getUserId());

		objectEntryFolderResource.
			postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe(
				objectEntryFolder.getScopeKey(),
				objectEntryFolder.getExternalReferenceCode());

		Assert.assertTrue(
			_subscriptionLocalService.isSubscribed(
				TestPropsValues.getCompanyId(), user.getUserId(),
				com.liferay.object.model.ObjectEntryFolder.class.getName(),
				objectEntryFolder.getId()));
	}

	@Override
	@Test
	public void testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe()
		throws Exception {

		super.
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe();

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {TestPropsValues.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		_addResourcePermission(ActionKeys.VIEW, user.getUserId());

		ObjectEntryFolder objectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe_addObjectEntryFolder();

		_subscriptionLocalService.addSubscription(
			user.getUserId(), _testDepotEntry.getGroupId(),
			com.liferay.object.model.ObjectEntryFolder.class.getName(),
			objectEntryFolder.getId());

		ObjectEntryFolderResource objectEntryFolderResource =
			_getObjectEntryFolderResource(user.getEmailAddress(), password);

		_assertProblemException(
			() ->
				objectEntryFolderResource.
					postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe(
						objectEntryFolder.getScopeKey(),
						objectEntryFolder.getExternalReferenceCode()));

		Assert.assertTrue(
			_subscriptionLocalService.isSubscribed(
				TestPropsValues.getCompanyId(), user.getUserId(),
				com.liferay.object.model.ObjectEntryFolder.class.getName(),
				objectEntryFolder.getId()));

		_addResourcePermission(ActionKeys.SUBSCRIBE, user.getUserId());

		objectEntryFolderResource.
			postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe(
				objectEntryFolder.getScopeKey(),
				objectEntryFolder.getExternalReferenceCode());

		Assert.assertFalse(
			_subscriptionLocalService.isSubscribed(
				TestPropsValues.getCompanyId(), user.getUserId(),
				com.liferay.object.model.ObjectEntryFolder.class.getName(),
				objectEntryFolder.getId()));
	}

	@Override
	@Test
	public void testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		super.testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode();

		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderByExternalReferenceCode();
		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderByObjectEntryFolderId();
		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderDataMismatch();
		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithMissingParentObjectEntryFolderReference();
		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithNonexistentParentObjectEntryFolderByExternalReferenceCode();
		_testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithNonexistentParentObjectEntryFolderByObjectEntryFolderId();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "label", "title"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"folderId"};
	}

	@Override
	protected ObjectEntryFolder randomObjectEntryFolder() throws Exception {
		return new ObjectEntryFolder() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = RandomTestUtil.randomString();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				label = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfObjectEntries = RandomTestUtil.randomInt();
				numberOfObjectEntryFolders = RandomTestUtil.randomInt();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected ObjectEntryFolder
			testDeleteObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected String
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	@Override
	protected ObjectEntryFolder testGetObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testGetObjectEntryFolderPermissionsPage_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected String
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	@Override
	protected ObjectEntryFolder
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				String scopeKey, ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			scopeKey, objectEntryFolder);
	}

	@Override
	protected String testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey() {
		return String.valueOf(_testDepotEntry.getGroupId());
	}

	@Override
	protected String
			testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	@Override
	protected String
			testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	@Override
	protected ObjectEntryFolder
			testGraphQLObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testPatchObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testPostObjectEntryFolderByParentObjectEntryFolderCopy_addObjectEntryFolder(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()), objectEntryFolder);
	}

	@Override
	protected ObjectEntryFolder
			testPostObjectEntryFolderByParentObjectEntryFolderCopyReplace_addObjectEntryFolder(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()), objectEntryFolder);
	}

	@Override
	protected ObjectEntryFolder
			testPostObjectEntryFolderByParentObjectEntryFolderMove_addObjectEntryFolder(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()), objectEntryFolder);
	}

	@Override
	protected ObjectEntryFolder
			testPostObjectEntryFolderByParentObjectEntryFolderMoveReplace_addObjectEntryFolder(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()), objectEntryFolder);
	}

	@Override
	protected ObjectEntryFolder
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()), objectEntryFolder);
	}

	@Override
	protected ObjectEntryFolder
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore_addObjectEntryFolder(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()), objectEntryFolder);
	}

	@Override
	protected ObjectEntryFolder
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected String
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	@Override
	protected ObjectEntryFolder
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected String
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	@Override
	protected ObjectEntryFolder testPutObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testPutObjectEntryFolderPermissionsPage_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		return objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
			String.valueOf(_testDepotEntry.getGroupId()),
			randomObjectEntryFolder());
	}

	@Override
	protected ObjectEntryFolder
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_createObjectEntryFolder()
		throws Exception {

		ObjectEntryFolder objectEntryFolder = randomObjectEntryFolder();

		objectEntryFolder.setScopeKey(
			String.valueOf(_testDepotEntry.getGroupId()));

		return objectEntryFolder;
	}

	@Override
	protected String
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return objectEntryFolder.getScopeKey();
	}

	private com.liferay.object.model.ObjectEntryFolder _addObjectEntryFolder(
			DepotEntry depotEntry, ServiceContext serviceContext, long userId)
		throws Exception {

		com.liferay.object.model.ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					depotEntry.getGroupId(), depotEntry.getCompanyId());

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			null, depotEntry.getGroupId(), userId,
			objectEntryFolder.getObjectEntryFolderId(),
			RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			RandomTestUtil.randomString(), serviceContext);
	}

	private void _addResourcePermission(String actionId, long userId)
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addUserRole(userId, role);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			com.liferay.object.model.ObjectEntryFolder.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			actionId);
	}

	private void _assertProblemException(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private JSONObject _getActionsJSONObject(
			com.liferay.object.model.ObjectEntryFolder objectEntryFolder,
			String password, User user)
		throws Exception {

		AtomicReference<JSONObject> atomicReference = new AtomicReference<>();

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				JSONObject responseJSONObject = HTTPTestUtil.invokeToJSONObject(
					null,
					"headless-object/v1.0/object-entry-folders/" +
						objectEntryFolder.getObjectEntryFolderId(),
					Http.Method.GET);

				atomicReference.set(
					responseJSONObject.getJSONObject("actions"));
			}
		);

		return atomicReference.get();
	}

	private Map<String, String> _getActionValue(String href, String method) {
		return HashMapBuilder.put(
			"href", href
		).put(
			"method", method
		).build();
	}

	private Map<String, Map<String, String>> _getExpectedActions(
		long objectEntryFolderId, long parentObjectEntryFolderId,
		boolean sharingEnabled) {

		String href1 =
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/headless-object/v1.0";

		String href2 = href1 + "/object-entry-folders/" + objectEntryFolderId;

		String href3 =
			href2 +
				"/by-parent-object-entry-folder-id/{parentObjectEntryFolderId}";

		return HashMapBuilder.<String, Map<String, String>>put(
			"copy", _getActionValue(href3 + "/copy", "POST")
		).put(
			"copy-replace", _getActionValue(href3 + "/copy-replace", "POST")
		).put(
			"delete", _getActionValue(href2, "DELETE")
		).put(
			"duplicate",
			_getActionValue(
				StringBundler.concat(
					href2, "/by-parent-object-entry-folder-id/",
					parentObjectEntryFolderId, "/copy"),
				"POST")
		).put(
			"get", _getActionValue(href2, "GET")
		).put(
			"get-by-scope",
			_getActionValue(
				StringBundler.concat(
					href1, "/scopes/",
					testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey(),
					"/object-entry-folders"),
				"GET")
		).put(
			"move", _getActionValue(href3 + "/move", "POST")
		).put(
			"move-replace", _getActionValue(href3 + "/move-replace", "POST")
		).put(
			"permissions", _getActionValue(href2 + "/permissions", "GET")
		).put(
			"share",
			() -> {
				if (sharingEnabled) {
					return _getActionValue(href2, "GET");
				}

				return null;
			}
		).put(
			"update", _getActionValue(href2, "PATCH")
		).build();
	}

	private ObjectEntryFolderResource _getObjectEntryFolderResource(
			String login, String password)
		throws Exception {

		return ObjectEntryFolderResource.builder(
		).authentication(
			login, password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private ServiceRegistration<?> _registerServiceWrapperService() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		return bundleContext.registerService(
			ServiceWrapper.class,
			new ObjectEntryFolderLocalServiceWrapper(
				_objectEntryFolderLocalService) {

				@Override
				public com.liferay.object.model.ObjectEntryFolder
						getOrAddEmptyObjectEntryFolder(
							String externalReferenceCode, long groupId,
							long companyId, long userId,
							ServiceContext serviceContext)
					throws PortalException {

					try (SafeCloseable safeCloseable =
							LazyReferencingThreadLocal.
								setEnabledWithSafeCloseable(true)) {

						return super.getOrAddEmptyObjectEntryFolder(
							externalReferenceCode, groupId, companyId, userId,
							serviceContext);
					}
				}

			},
			HashMapDictionaryBuilder.<String, Object>put(
				"service.ranking", Integer.MAX_VALUE
			).put(
				"service.wrapper.class",
				ObjectEntryFolderLocalService.class.getName()
			).build());
	}

	private void _testGetObjectEntryFolderActions(boolean sharingEnabled)
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testGetObjectEntryFolder_addObjectEntryFolder();

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.getObjectEntryFolder(
				postObjectEntryFolder.getId());

		Assert.assertEquals(
			_getExpectedActions(
				getObjectEntryFolder.getId(),
				GetterUtil.getLong(
					getObjectEntryFolder.getParentObjectEntryFolderId()),
				sharingEnabled),
			getObjectEntryFolder.getActions());
	}

	@TestInfo("LPD-62553")
	private void _testGetObjectEntryFolderActionsWithCompanySharingDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_testDepotEntryGroup.getCompanyId(),
						"com.liferay.sharing.internal.configuration." +
							"SharingCompanyConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			_testGetObjectEntryFolderActions(false);
		}
	}

	@TestInfo("LPD-62553")
	private void _testGetObjectEntryFolderActionsWithGroupSharingDisabled()
		throws Exception {

		UnicodeProperties originalUnicodeProperties =
			_testDepotEntryGroup.getTypeSettingsProperties();

		_groupLocalService.updateGroup(
			_testDepotEntryGroup.getGroupId(),
			UnicodePropertiesBuilder.create(
				originalUnicodeProperties, true
			).put(
				"sharingEnabled", false
			).buildString());

		try {
			_testGetObjectEntryFolderActions(false);
		}
		finally {
			_groupLocalService.updateGroup(
				_testDepotEntryGroup.getGroupId(),
				originalUnicodeProperties.toString());
		}
	}

	@TestInfo("LPD-62553")
	private void _testGetObjectEntryFolderActionsWithoutUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addUser(
			testCompany.getCompanyId(), testCompany.getUserId(), "test",
			"UserServiceTest." + RandomTestUtil.nextLong() + "@liferay.com",
			StringPool.BLANK, LocaleUtil.getDefault(), "UserServiceTest",
			"UserServiceTest", null, null);

		_addResourcePermission(ActionKeys.VIEW, user.getUserId());

		ObjectEntryFolderResource objectEntryFolderResource =
			ObjectEntryFolderResource.builder(
			).authentication(
				user.getEmailAddress(), "test"
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		ObjectEntryFolder postObjectEntryFolder =
			testGetObjectEntryFolder_addObjectEntryFolder();

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderResource.getObjectEntryFolder(
				postObjectEntryFolder.getId());

		Map<String, Map<String, String>> actions =
			objectEntryFolder.getActions();

		Assert.assertFalse(actions.containsKey("share"));
	}

	@TestInfo("LPD-62553")
	private void _testGetObjectEntryFolderActionsWithSharingEnabled()
		throws Exception {

		_testGetObjectEntryFolderActions(true);
	}

	@TestInfo("LPD-62553")
	private void _testGetObjectEntryFolderActionsWithSystemSharingDisabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.sharing.internal.configuration." +
						"SharingSystemConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", false
					).build())) {

			_testGetObjectEntryFolderActions(false);
		}
	}

	@TestInfo("LPD-83639")
	private void _testGetObjectEntryFolderShareAction() throws Exception {

		// With asset library administrator role

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE,
			new ServiceContext() {
				{
					setCompanyId(testGroup.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		user.setEmailAddressVerified(true);

		user = UserLocalServiceUtil.updateUser(user);

		com.liferay.object.model.ObjectEntryFolder objectEntryFolder =
			_addObjectEntryFolder(
				depotEntry,
				ServiceContextTestUtil.getServiceContext(
					depotEntry.getGroupId()),
				depotEntry.getUserId());

		Role role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		UserGroupRole userGroupRole =
			_userGroupRoleLocalService.addUserGroupRole(
				user.getUserId(), depotEntry.getGroupId(), role.getRoleId());

		JSONObject jsonObject = _getActionsJSONObject(
			objectEntryFolder, password, user);

		Assert.assertTrue(jsonObject.has("share"));

		_userGroupRoleLocalService.deleteUserGroupRole(userGroupRole);

		// With asset library content reviewer role

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		userGroupRole = _userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), depotEntry.getGroupId(), role.getRoleId());

		jsonObject = _getActionsJSONObject(objectEntryFolder, password, user);

		Assert.assertFalse(jsonObject.has("share"));

		// With asset library content reviewer role and shared object entry
		// folder

		SharingEntry sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, user.getUserId(),
			_classNameLocalService.getClassNameId(
				objectEntryFolder.getModelClassName()),
			objectEntryFolder.getObjectEntryFolderId(), depotEntry.getGroupId(),
			true, List.of(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				depotEntry.getGroupId(), TestPropsValues.getUserId()));

		jsonObject = _getActionsJSONObject(objectEntryFolder, password, user);

		_sharingEntryLocalService.deleteSharingEntry(
			sharingEntry.getSharingEntryId());

		Assert.assertTrue(jsonObject.has("share"));

		_userGroupRoleLocalService.deleteUserGroupRole(userGroupRole);

		// With CMS administrator role

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		_roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		jsonObject = _getActionsJSONObject(objectEntryFolder, password, user);

		Assert.assertTrue(jsonObject.has("share"));

		_roleLocalService.deleteUserRole(user.getUserId(), role.getRoleId());

		// With user as creator

		com.liferay.object.model.ObjectEntryFolder objectEntryFolder2 =
			_addObjectEntryFolder(
				depotEntry,
				ServiceContextTestUtil.getServiceContext(
					depotEntry.getGroupId(), user.getUserId()),
				user.getUserId());

		jsonObject = _getActionsJSONObject(objectEntryFolder2, password, user);

		Assert.assertTrue(jsonObject.has("share"));

		// Without role

		jsonObject = _getActionsJSONObject(objectEntryFolder, password, user);

		Assert.assertFalse(jsonObject.has("share"));
	}

	private void _testGetScopeScopeKeyObjectEntryFoldersPageWithFilterStringEqualsFolderIdAndTitle()
		throws Exception {

		ObjectEntryFolder objectEntryFolder1 = randomObjectEntryFolder();

		String scopeKey =
			testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey();

		objectEntryFolder1 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, objectEntryFolder1);

		ObjectEntryFolder objectEntryFolder2 = randomObjectEntryFolder();

		objectEntryFolder2.setParentObjectEntryFolderId(
			objectEntryFolder1.getId());

		objectEntryFolder2 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, objectEntryFolder2);

		ObjectEntryFolder objectEntryFolder3 = randomObjectEntryFolder();

		objectEntryFolder3.setParentObjectEntryFolderId(
			objectEntryFolder1.getId());

		testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
			scopeKey, objectEntryFolder3);

		Page<ObjectEntryFolder> page =
			objectEntryFolderResource.getScopeScopeKeyObjectEntryFoldersPage(
				scopeKey, null, null, null,
				"folderId eq " + objectEntryFolder1.getId(),
				Pagination.of(1, 10), null);

		Assert.assertEquals(2, page.getTotalCount());

		page = objectEntryFolderResource.getScopeScopeKeyObjectEntryFoldersPage(
			scopeKey, null, null, null,
			StringBundler.concat(
				"folderId eq ", objectEntryFolder1.getId(), " and title eq '",
				objectEntryFolder2.getTitle(), "'"),
			Pagination.of(1, 10), null);

		assertEquals(
			Collections.singletonList(objectEntryFolder2),
			(List<ObjectEntryFolder>)page.getItems());
	}

	private void _testGetScopeScopeKeyObjectEntryFoldersPageWithGroupKey()
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				String.valueOf(_testDepotEntry.getGroupId()),
				randomObjectEntryFolder());

		Page<ObjectEntryFolder> page =
			objectEntryFolderResource.getScopeScopeKeyObjectEntryFoldersPage(
				_testDepotEntryGroup.getGroupKey(), null, null, null, null,
				Pagination.of(1, 10), null);

		assertContains(
			objectEntryFolder, (List<ObjectEntryFolder>)page.getItems());

		objectEntryFolderResource.deleteObjectEntryFolder(
			objectEntryFolder.getId());
	}

	private void _testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithGroupKey()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomPatchObjectEntryFolder =
			randomPatchObjectEntryFolder();

		ObjectEntryFolder patchObjectEntryFolder =
			objectEntryFolderResource.
				patchScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					_testDepotEntryGroup.getGroupKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomPatchObjectEntryFolder);

		ObjectEntryFolder expectedPatchObjectEntryFolder =
			postObjectEntryFolder.clone();

		BeanTestUtil.copyProperties(
			randomPatchObjectEntryFolder, expectedPatchObjectEntryFolder);

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					_testDepotEntryGroup.getGroupKey(),
					patchObjectEntryFolder.getExternalReferenceCode());

		assertEquals(expectedPatchObjectEntryFolder, getObjectEntryFolder);
		assertValid(getObjectEntryFolder);
	}

	private void _testPostScopeScopeKeyObjectEntryFolderStatus()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder postObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

		assertValid(postObjectEntryFolder);

		Status status = postObjectEntryFolder.getStatus();

		Assert.assertEquals(
			Integer.valueOf(WorkflowConstants.STATUS_APPROVED),
			status.getCode());
	}

	private void _testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			parentObjectEntryFolder.getExternalReferenceCode());

		ObjectEntryFolder postObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, postObjectEntryFolder);
		assertValid(postObjectEntryFolder);

		Assert.assertEquals(
			postObjectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode(),
			parentObjectEntryFolder.getExternalReferenceCode());
		Assert.assertEquals(
			postObjectEntryFolder.getParentObjectEntryFolderId(),
			parentObjectEntryFolder.getId());
	}

	private void _testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderByObjectEntryFolderId()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			parentObjectEntryFolder.getId());

		ObjectEntryFolder postObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, postObjectEntryFolder);
		assertValid(postObjectEntryFolder);

		Assert.assertEquals(
			postObjectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode(),
			parentObjectEntryFolder.getExternalReferenceCode());
		Assert.assertEquals(
			postObjectEntryFolder.getParentObjectEntryFolderId(),
			parentObjectEntryFolder.getId());
	}

	private void _testPostScopeScopeKeyObjectEntryFolderWithExistingParentObjectEntryFolderDataMismatch()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			parentObjectEntryFolder.getExternalReferenceCode());

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			RandomTestUtil.randomLong());

		try {
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@TestInfo("LPD-56833")
	private void _testPostScopeScopeKeyObjectEntryFolderWithMissingParentObjectEntryFolderReference()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			RandomTestUtil.randomString());

		Assert.assertNull(
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					randomObjectEntryFolder.
						getParentObjectEntryFolderExternalReferenceCode(),
					_testDepotEntry.getGroupId(),
					_testDepotEntry.getCompanyId()));

		ServiceRegistration<?> serviceRegistration =
			_registerServiceWrapperService();

		try {
			randomObjectEntryFolder =
				objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
					String.valueOf(_testDepotEntry.getGroupId()),
					randomObjectEntryFolder);
		}
		finally {
			serviceRegistration.unregister();
		}

		com.liferay.object.model.ObjectEntryFolder
			serviceBuilderParentObjectEntryFolder =
				_objectEntryFolderLocalService.
					getObjectEntryFolderByExternalReferenceCode(
						randomObjectEntryFolder.
							getParentObjectEntryFolderExternalReferenceCode(),
						_testDepotEntry.getGroupId(),
						_testDepotEntry.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY,
			serviceBuilderParentObjectEntryFolder.getStatus());

		_objectEntryFolderLocalService.deleteObjectEntryFolder(
			serviceBuilderParentObjectEntryFolder);
	}

	private void _testPostScopeScopeKeyObjectEntryFolderWithNonexistentParentObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			RandomTestUtil.randomString());

		try {
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testPostScopeScopeKeyObjectEntryFolderWithNonexistentParentObjectEntryFolderByObjectEntryFolderId()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			RandomTestUtil.randomLong());

		try {
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			parentObjectEntryFolder.getExternalReferenceCode());

		ObjectEntryFolder putObjectEntryFolder =
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, putObjectEntryFolder);
		Assert.assertEquals(
			parentObjectEntryFolder.getExternalReferenceCode(),
			putObjectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode());
		assertValid(putObjectEntryFolder);
	}

	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderByObjectEntryFolderId()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			parentObjectEntryFolder.getId());

		ObjectEntryFolder putObjectEntryFolder =
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, putObjectEntryFolder);
		Assert.assertEquals(
			parentObjectEntryFolder.getExternalReferenceCode(),
			putObjectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode());
		assertValid(putObjectEntryFolder);
	}

	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithExistingParentObjectEntryFolderDataMismatch()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder parentObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder());

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			parentObjectEntryFolder.getExternalReferenceCode());

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			RandomTestUtil.randomLong());

		try {
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@TestInfo("LPD-56833")
	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithMissingParentObjectEntryFolderReference()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			RandomTestUtil.randomString());

		ServiceRegistration<?> serviceRegistration =
			_registerServiceWrapperService();

		try {
			objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
				String.valueOf(_testDepotEntry.getGroupId()),
				randomObjectEntryFolder);

			com.liferay.object.model.ObjectEntryFolder
				serviceBuilderParentObjectEntryFolder =
					_objectEntryFolderLocalService.
						getObjectEntryFolderByExternalReferenceCode(
							randomObjectEntryFolder.
								getParentObjectEntryFolderExternalReferenceCode(),
							_testDepotEntry.getGroupId(),
							_testDepotEntry.getCompanyId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY,
				serviceBuilderParentObjectEntryFolder.getStatus());

			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					String.valueOf(_testDepotEntry.getGroupId()),
					randomObjectEntryFolder.
						getParentObjectEntryFolderExternalReferenceCode(),
					new ObjectEntryFolder() {
						{
							dateCreated = RandomTestUtil.nextDate();
							dateModified = RandomTestUtil.nextDate();
							description = RandomTestUtil.randomString();
							externalReferenceCode =
								randomObjectEntryFolder.
									getParentObjectEntryFolderExternalReferenceCode();
							id = RandomTestUtil.randomLong();
							label = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
							numberOfObjectEntries = RandomTestUtil.randomInt();
							numberOfObjectEntryFolders =
								RandomTestUtil.randomInt();
							title = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
						}
					});

			serviceBuilderParentObjectEntryFolder =
				_objectEntryFolderLocalService.
					getObjectEntryFolderByExternalReferenceCode(
						randomObjectEntryFolder.
							getParentObjectEntryFolderExternalReferenceCode(),
						_testDepotEntry.getGroupId(),
						_testDepotEntry.getCompanyId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED,
				serviceBuilderParentObjectEntryFolder.getStatus());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithNonexistentParentObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		String parentObjectEntryFolderExternalReferenceCode =
			RandomTestUtil.randomString();

		randomObjectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			parentObjectEntryFolderExternalReferenceCode);

		ObjectEntryFolder putObjectEntryFolder =
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, putObjectEntryFolder);
		Assert.assertEquals(
			parentObjectEntryFolderExternalReferenceCode,
			putObjectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode());
		assertValid(putObjectEntryFolder);

		ObjectEntryFolder parentObjectEntryFolder =
			objectEntryFolderResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					putObjectEntryFolder.getScopeKey(),
					parentObjectEntryFolderExternalReferenceCode);

		Assert.assertEquals(
			parentObjectEntryFolderExternalReferenceCode,
			parentObjectEntryFolder.getExternalReferenceCode());
		assertValid(parentObjectEntryFolder);
	}

	private void _testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeWithNonexistentParentObjectEntryFolderByObjectEntryFolderId()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		randomObjectEntryFolder.setParentObjectEntryFolderId(
			RandomTestUtil.randomLong());

		try {
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _updateGroup(boolean trashEnabled) throws Exception {
		UnicodeProperties unicodeProperties =
			_testDepotEntryGroup.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			"trashEnabled", String.valueOf(trashEnabled));

		_testDepotEntryGroup = _groupLocalService.updateGroup(
			_testDepotEntryGroup.getGroupId(), unicodeProperties.toString());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@Inject
	private SubscriptionLocalService _subscriptionLocalService;

	@DeleteAfterTestRun
	private DepotEntry _testDepotEntry;

	private Group _testDepotEntryGroup;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}