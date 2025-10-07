/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.object.client.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.client.dto.v1_0.Status;
import com.liferay.headless.object.client.pagination.Page;
import com.liferay.headless.object.client.pagination.Pagination;
import com.liferay.headless.object.client.problem.Problem;
import com.liferay.headless.object.resource.v1_0.ObjectEntryFolderResource;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class ObjectEntryFolderResourceTest
	extends BaseObjectEntryFolderResourceTestCase {

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_objectEntryFolderResource.setContextAcceptLanguage(
			new AcceptLanguage() {

				@Override
				public List<Locale> getLocales() {
					return Arrays.asList(LocaleUtil.getDefault());
				}

				@Override
				public String getPreferredLanguageId() {
					return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
				}

				@Override
				public Locale getPreferredLocale() {
					return LocaleUtil.getDefault();
				}

			});
		_objectEntryFolderResource.setContextUser(TestPropsValues.getUser());

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

	@FeatureFlag("LPD-17564")
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

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		User user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		PrincipalThreadLocal.setName(user.getUserId());

		_addResourcePermission(ActionKeys.VIEW, user.getUserId());

		ObjectEntryFolder objectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe_addObjectEntryFolder();

		_objectEntryFolderResource.setContextUser(user);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(),
				" must have SUBSCRIBE permission for ",
				com.liferay.object.model.ObjectEntryFolder.class.getName(),
				StringPool.SPACE, objectEntryFolder.getId()),
			() ->
				_objectEntryFolderResource.
					postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe(
						objectEntryFolder.getScopeKey(),
						objectEntryFolder.getExternalReferenceCode()));

		_addResourcePermission(ActionKeys.SUBSCRIBE, user.getUserId());

		_objectEntryFolderResource.
			postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe(
				objectEntryFolder.getScopeKey(),
				objectEntryFolder.getExternalReferenceCode());

		Assert.assertTrue(
			_subscriptionLocalService.isSubscribed(
				TestPropsValues.getCompanyId(), user.getUserId(),
				com.liferay.object.model.ObjectEntryFolder.class.getName(),
				objectEntryFolder.getId()));

		PermissionThreadLocal.setPermissionChecker(originalPermissionChecker);
		PrincipalThreadLocal.setName(originalName);
	}

	@Override
	@Test
	public void testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe()
		throws Exception {

		super.
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		User user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		PrincipalThreadLocal.setName(user.getUserId());

		_addResourcePermission(ActionKeys.VIEW, user.getUserId());

		ObjectEntryFolder objectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe_addObjectEntryFolder();

		_subscriptionLocalService.addSubscription(
			user.getUserId(), _testDepotEntry.getGroupId(),
			com.liferay.object.model.ObjectEntryFolder.class.getName(),
			objectEntryFolder.getId());

		_objectEntryFolderResource.setContextUser(user);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(),
				" must have SUBSCRIBE permission for ",
				com.liferay.object.model.ObjectEntryFolder.class.getName(),
				StringPool.SPACE, objectEntryFolder.getId()),
			() ->
				_objectEntryFolderResource.
					postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe(
						objectEntryFolder.getScopeKey(),
						objectEntryFolder.getExternalReferenceCode()));

		Assert.assertTrue(
			_subscriptionLocalService.isSubscribed(
				TestPropsValues.getCompanyId(), user.getUserId(),
				com.liferay.object.model.ObjectEntryFolder.class.getName(),
				objectEntryFolder.getId()));

		_addResourcePermission(ActionKeys.SUBSCRIBE, user.getUserId());

		_objectEntryFolderResource.
			postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe(
				objectEntryFolder.getScopeKey(),
				objectEntryFolder.getExternalReferenceCode());

		Assert.assertFalse(
			_subscriptionLocalService.isSubscribed(
				TestPropsValues.getCompanyId(), user.getUserId(),
				com.liferay.object.model.ObjectEntryFolder.class.getName(),
				objectEntryFolder.getId()));

		PermissionThreadLocal.setPermissionChecker(originalPermissionChecker);
		PrincipalThreadLocal.setName(originalName);
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

	private Map<String, String> _getActionValue(String href, String method) {
		return HashMapBuilder.put(
			"href", href
		).put(
			"method", method
		).build();
	}

	private Map<String, Map<String, String>> _getExpectedActions(
		long objectEntryFolderId, boolean sharingEnabled) {

		String href =
			"http://localhost:8080/o/headless-object/v1.0" +
				"/object-entry-folders/" + objectEntryFolderId;

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete", _getActionValue(href, "DELETE")
		).put(
			"get", _getActionValue(href, "GET")
		).put(
			"permissions", _getActionValue(href + "/permissions", "GET")
		).put(
			"share",
			() -> {
				if (sharingEnabled) {
					return _getActionValue(href, "GET");
				}

				return null;
			}
		).put(
			"update", _getActionValue(href, "PATCH")
		).build();
	}

	private void _testGetObjectEntryFolderActions(boolean sharingEnabled)
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testGetObjectEntryFolder_addObjectEntryFolder();

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.getObjectEntryFolder(
				postObjectEntryFolder.getId());

		Assert.assertEquals(
			_getExpectedActions(getObjectEntryFolder.getId(), sharingEnabled),
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

		com.liferay.headless.object.dto.v1_0.ObjectEntryFolder
			objectEntryFolder =
				new com.liferay.headless.object.dto.v1_0.ObjectEntryFolder() {
					{
						dateCreated = RandomTestUtil.nextDate();
						dateModified = RandomTestUtil.nextDate();
						description = RandomTestUtil.randomString();
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						id = RandomTestUtil.randomLong();
						label = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						numberOfObjectEntries = RandomTestUtil.randomInt();
						numberOfObjectEntryFolders = RandomTestUtil.randomInt();
						parentObjectEntryFolderExternalReferenceCode =
							RandomTestUtil.randomString();
						title = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
					}
				};

		Assert.assertNull(
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					objectEntryFolder.
						getParentObjectEntryFolderExternalReferenceCode(),
					_testDepotEntry.getGroupId(),
					_testDepotEntry.getCompanyId()));

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			objectEntryFolder =
				_objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
					String.valueOf(_testDepotEntry.getGroupId()),
					objectEntryFolder);
		}

		com.liferay.object.model.ObjectEntryFolder
			serviceBuilderParentObjectEntryFolder =
				_objectEntryFolderLocalService.
					getObjectEntryFolderByExternalReferenceCode(
						objectEntryFolder.
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

		com.liferay.headless.object.dto.v1_0.ObjectEntryFolder
			objectEntryFolder =
				new com.liferay.headless.object.dto.v1_0.ObjectEntryFolder() {
					{
						dateCreated = RandomTestUtil.nextDate();
						dateModified = RandomTestUtil.nextDate();
						description = RandomTestUtil.randomString();
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						id = RandomTestUtil.randomLong();
						label = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						numberOfObjectEntries = RandomTestUtil.randomInt();
						numberOfObjectEntryFolders = RandomTestUtil.randomInt();
						parentObjectEntryFolderExternalReferenceCode =
							RandomTestUtil.randomString();
						title = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
					}
				};

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			_objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
				String.valueOf(_testDepotEntry.getGroupId()),
				objectEntryFolder);

			com.liferay.object.model.ObjectEntryFolder
				serviceBuilderParentObjectEntryFolder =
					_objectEntryFolderLocalService.
						getObjectEntryFolderByExternalReferenceCode(
							objectEntryFolder.
								getParentObjectEntryFolderExternalReferenceCode(),
							_testDepotEntry.getGroupId(),
							_testDepotEntry.getCompanyId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY,
				serviceBuilderParentObjectEntryFolder.getStatus());

			_objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					String.valueOf(_testDepotEntry.getGroupId()),
					objectEntryFolder.
						getParentObjectEntryFolderExternalReferenceCode(),
					new com.liferay.headless.object.dto.v1_0.
						ObjectEntryFolder() {

						{
							dateCreated = RandomTestUtil.nextDate();
							dateModified = RandomTestUtil.nextDate();
							description = RandomTestUtil.randomString();
							externalReferenceCode =
								objectEntryFolder.
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
						objectEntryFolder.
							getParentObjectEntryFolderExternalReferenceCode(),
						_testDepotEntry.getGroupId(),
						_testDepotEntry.getCompanyId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED,
				serviceBuilderParentObjectEntryFolder.getStatus());
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
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryFolderResource _objectEntryFolderResource;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SubscriptionLocalService _subscriptionLocalService;

	@DeleteAfterTestRun
	private DepotEntry _testDepotEntry;

	private Group _testDepotEntryGroup;

}