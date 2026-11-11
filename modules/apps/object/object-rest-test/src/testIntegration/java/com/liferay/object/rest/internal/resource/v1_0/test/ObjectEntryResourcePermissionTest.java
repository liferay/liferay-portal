/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 * @author Magdalena Jedraszak
 * @author Daniel Raposo
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourcePermissionTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetObjectEntriesPageActions() throws Exception {
		JSONObject actionsJSONObject1 =
			_getObjectEntriesPageActionsJSONObject();

		Assert.assertFalse(actionsJSONObject1.isNull("create"));
		Assert.assertFalse(actionsJSONObject1.isNull("createBatch"));
		Assert.assertFalse(actionsJSONObject1.isNull("deleteBatch"));
		Assert.assertFalse(actionsJSONObject1.isNull("get"));
		Assert.assertFalse(actionsJSONObject1.isNull("updateBatch"));

		HTTPTestUtil.customize(
		).withGuest(
		).apply(
			() -> Assert.assertTrue(
				JSONUtil.isEmpty(_getObjectEntriesPageActionsJSONObject()))
		);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		ObjectDefinition objectDefinition = getObjectDefinition4();

		addResourcePermission(
			ActionKeys.VIEW, objectDefinition.getClassName(), role);
		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			objectDefinition.getResourceName(), role);

		String password = RandomTestUtil.randomString();

		User user = addUser(
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE),
			password);

		roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				JSONObject actionsJSONObject2 =
					_getObjectEntriesPageActionsJSONObject();

				Assert.assertFalse(actionsJSONObject2.isNull("create"));
				Assert.assertFalse(actionsJSONObject2.isNull("createBatch"));
				Assert.assertTrue(actionsJSONObject2.isNull("deleteBatch"));
				Assert.assertFalse(actionsJSONObject2.isNull("get"));
				Assert.assertTrue(actionsJSONObject2.isNull("updateBatch"));
			}
		);

		addResourcePermission(
			ActionKeys.DELETE, objectDefinition.getClassName(), role);
		addResourcePermission(
			ActionKeys.UPDATE, objectDefinition.getClassName(), role);

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				JSONObject actionsJSONObject3 =
					_getObjectEntriesPageActionsJSONObject();

				Assert.assertFalse(actionsJSONObject3.isNull("create"));
				Assert.assertFalse(actionsJSONObject3.isNull("createBatch"));
				Assert.assertFalse(actionsJSONObject3.isNull("deleteBatch"));
				Assert.assertFalse(actionsJSONObject3.isNull("get"));
				Assert.assertFalse(actionsJSONObject3.isNull("updateBatch"));
			}
		);
	}

	@Test
	@TestInfo("LPD-62553")
	public void testGetObjectEntryActions() throws Exception {
		_testGetObjectEntryActions(true);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						group.getCompanyId(),
						"com.liferay.sharing.internal.configuration." +
							"SharingCompanyConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			_testGetObjectEntryActions(false);
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		groupLocalService.updateGroup(
			group.getGroupId(),
			UnicodePropertiesBuilder.create(
				typeSettingsUnicodeProperties, true
			).put(
				"sharingEnabled", false
			).buildString());

		try {
			_testGetObjectEntryActions(false);
		}
		finally {
			groupLocalService.updateGroup(
				group.getGroupId(), typeSettingsUnicodeProperties.toString());
		}

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.sharing.internal.configuration." +
						"SharingSystemConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", false
					).build())) {

			_testGetObjectEntryActions(false);
		}
	}

	@Test
	@TestInfo("LPD-83639")
	public void testGetObjectEntryShareAction() throws Exception {

		// With asset library administrator role

		DepotEntry depotEntry = depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(depotEntry.getGroupId());

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
			depotEntry, serviceContext);

		ObjectEntry objectEntry = _addObjectEntry(
			depotEntry, objectDefinition, objectEntryFolder, serviceContext,
			depotEntry.getUserId());

		String password = RandomTestUtil.randomString();

		User user = addUser(
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE),
			password);

		Role role = roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		UserGroupRole userGroupRole =
			userGroupRoleLocalService.addUserGroupRole(
				user.getUserId(), depotEntry.getGroupId(), role.getRoleId());

		JSONObject actionsJSONObject = _getObjectEntryActionsJSONObject(
			objectDefinition, objectEntry, password, user);

		Assert.assertTrue(actionsJSONObject.has("share"));

		userGroupRoleLocalService.deleteUserGroupRole(userGroupRole);

		// With asset library content reviewer role

		role = roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		userGroupRole = userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), depotEntry.getGroupId(), role.getRoleId());

		actionsJSONObject = _getObjectEntryActionsJSONObject(
			objectDefinition, objectEntry, password, user);

		Assert.assertFalse(actionsJSONObject.has("share"));

		// With asset library content reviewer role and shared object entry

		SharingEntry sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, user.getUserId(),
			classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), depotEntry.getGroupId(), true,
			List.of(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				depotEntry.getGroupId(), TestPropsValues.getUserId()));

		actionsJSONObject = _getObjectEntryActionsJSONObject(
			objectDefinition, objectEntry, password, user);

		Assert.assertTrue(actionsJSONObject.has("share"));

		_sharingEntryLocalService.deleteSharingEntry(
			sharingEntry.getSharingEntryId());

		userGroupRoleLocalService.deleteUserGroupRole(userGroupRole);

		// With CMS administrator role

		role = roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		actionsJSONObject = _getObjectEntryActionsJSONObject(
			objectDefinition, objectEntry, password, user);

		Assert.assertTrue(actionsJSONObject.has("share"));

		roleLocalService.deleteUserRole(user.getUserId(), role.getRoleId());

		// With user as creator

		ObjectEntry userObjectEntry = _addObjectEntry(
			depotEntry, objectDefinition, objectEntryFolder,
			ServiceContextTestUtil.getServiceContext(
				depotEntry.getGroupId(), user.getUserId()),
			user.getUserId());

		actionsJSONObject = _getObjectEntryActionsJSONObject(
			objectDefinition, userObjectEntry, password, user);

		Assert.assertTrue(actionsJSONObject.has("share"));

		// Without role

		actionsJSONObject = _getObjectEntryActionsJSONObject(
			objectDefinition, objectEntry, password, user);

		Assert.assertFalse(actionsJSONObject.has("share"));
	}

	@Test
	@TestInfo("LPD-110103")
	public void testPatchPostPutCustomObjectEntryWithPermissions()
		throws Exception {

		// Invalid permissions

		JSONArray invalidPermissionsJSONArray = JSONUtil.putAll(
			getPermissionsJSONObject(
				new String[] {ActionKeys.DELETE},
				RandomTestUtil.randomString()));

		assertNotFound(
			_postCustomObjectEntryWithPermissions(
				true, invalidPermissionsJSONArray));
		assertNotFound(
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true,
				_postCustomObjectEntryWithPermissions(true, null),
				invalidPermissionsJSONArray));
		assertNotFound(
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PUT, true,
				_postCustomObjectEntryWithPermissions(true, null),
				invalidPermissionsJSONArray));
		assertNotFound(
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true,
				_postCustomObjectEntryWithPermissions(true, null),
				invalidPermissionsJSONArray));
		assertNotFound(
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PUT, true,
				_postCustomObjectEntryWithPermissions(true, null),
				invalidPermissionsJSONArray));

		// No permissions in the request body

		JSONObject objectEntryJSONObject =
			_postCustomObjectEntryWithPermissions(true, null);

		ObjectDefinition objectDefinition = getObjectDefinition1();

		_assertPermissions(
			JSONUtil.putAll(getOwnerPermissionsJSONObject(objectDefinition)),
			objectEntryJSONObject);
		_assertPermissions(
			JSONUtil.putAll(getOwnerPermissionsJSONObject(objectDefinition)),
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject, null));
		_assertPermissions(
			JSONUtil.putAll(getOwnerPermissionsJSONObject(objectDefinition)),
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject, null));
		_assertPermissions(
			JSONUtil.putAll(getOwnerPermissionsJSONObject(objectDefinition)),
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject, null));
		_assertPermissions(
			JSONUtil.putAll(getOwnerPermissionsJSONObject(objectDefinition)),
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject, null));

		// Permissions with another object entry ID

		JSONObject otherObjectEntryJSONObject =
			_postCustomObjectEntryWithPermissions(true, null);

		String objectEntryJSON = JSONUtil.put(
			OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
		).put(
			"id", otherObjectEntryJSONObject.getLong("id")
		).put(
			"permissions",
			JSONUtil.putAll(
				getPermissionsJSONObject(new String[0], RoleConstants.OWNER))
		).toString();

		_assertPermissions(
			JSONFactoryUtil.createJSONArray(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON,
				objectDefinition.getRESTContextPath() +
					"?nestedFields=permissions",
				Http.Method.POST));

		String byExternalReferenceCodeEndpoint = StringBundler.concat(
			objectDefinition.getRESTContextPath(),
			"/by-external-reference-code/",
			objectEntryJSONObject.getString("externalReferenceCode"),
			"?nestedFields=permissions");

		_assertPermissions(
			JSONFactoryUtil.createJSONArray(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON, byExternalReferenceCodeEndpoint,
				Http.Method.PATCH));
		_assertPermissions(
			JSONFactoryUtil.createJSONArray(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON, byExternalReferenceCodeEndpoint,
				Http.Method.PUT));

		String endpoint = StringBundler.concat(
			objectDefinition.getRESTContextPath(), StringPool.SLASH,
			objectEntryJSONObject.getLong("id"), "?nestedFields=permissions");

		_assertPermissions(
			JSONFactoryUtil.createJSONArray(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON, endpoint, Http.Method.PATCH));
		_assertPermissions(
			JSONFactoryUtil.createJSONArray(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON, endpoint, Http.Method.PUT));

		_assertPermissions(
			JSONUtil.putAll(getOwnerPermissionsJSONObject(objectDefinition)),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					objectDefinition.getRESTContextPath(), StringPool.SLASH,
					otherObjectEntryJSONObject.getLong("id"),
					"?nestedFields=permissions"),
				Http.Method.GET));

		// Permissions with different roles

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addResourcePermission(
			ActionKeys.DELETE, objectDefinition.getClassName(), role1);

		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		objectEntryJSONObject = _postCustomObjectEntryWithPermissions(
			true,
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.PERMISSIONS}, role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					role2.getName())));

		_assertPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS},
					role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					role2.getName())),
			objectEntryJSONObject);

		Role role3 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_assertPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.UPDATE},
					role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role3.getName())),
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject,
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE}, role1.getName()),
					getPermissionsJSONObject(
						new String[] {ActionKeys.DELETE}, role3.getName()))));
		_assertPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.UPDATE},
					role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.VIEW},
					role3.getName())),
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject,
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE}, role1.getName()),
					getPermissionsJSONObject(
						new String[] {ActionKeys.DELETE, ActionKeys.VIEW},
						role3.getName()))));
		_assertPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.VIEW}, role3.getName())),
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject,
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.VIEW}, role3.getName()))));
		_assertPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.UPDATE}, role3.getName())),
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject,
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE}, role3.getName()))));

		// Permissions with empty list

		JSONArray companyPermissionsJSONArray = JSONUtil.putAll(
			getPermissionsJSONObject(
				new String[] {ActionKeys.DELETE}, role1.getName()));

		_assertPermissions(
			companyPermissionsJSONArray,
			_postCustomObjectEntryWithPermissions(
				true, JSONFactoryUtil.createJSONArray()));
		_assertPermissions(
			companyPermissionsJSONArray,
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject,
				JSONFactoryUtil.createJSONArray()));
		_assertPermissions(
			companyPermissionsJSONArray,
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject,
				JSONFactoryUtil.createJSONArray()));
		_assertPermissions(
			companyPermissionsJSONArray,
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PATCH, true, objectEntryJSONObject,
				JSONFactoryUtil.createJSONArray()));
		_assertPermissions(
			companyPermissionsJSONArray,
			_patchPutCustomObjectEntryWithPermissions(
				Http.Method.PUT, true, objectEntryJSONObject,
				JSONFactoryUtil.createJSONArray()));

		// Permissions without nested fields

		objectEntryJSONObject = _postCustomObjectEntryWithPermissions(
			false, null);

		Assert.assertNull(objectEntryJSONObject.get("permissions"));

		objectEntryJSONObject =
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PATCH, false, objectEntryJSONObject, null);

		Assert.assertNull(objectEntryJSONObject.get("permissions"));

		objectEntryJSONObject =
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method.PUT, false, objectEntryJSONObject, null);

		Assert.assertNull(objectEntryJSONObject.get("permissions"));

		objectEntryJSONObject = _patchPutCustomObjectEntryWithPermissions(
			Http.Method.PATCH, false, objectEntryJSONObject, null);

		Assert.assertNull(objectEntryJSONObject.get("permissions"));

		objectEntryJSONObject = _patchPutCustomObjectEntryWithPermissions(
			Http.Method.PUT, false, objectEntryJSONObject, null);

		Assert.assertNull(objectEntryJSONObject.get("permissions"));
	}

	@Test
	public void testPostCustomObjectEntryWithObjectActions() throws Exception {
		_testPostCustomObjectEntryWithObjectActions(getObjectDefinition1());
		_testPostCustomObjectEntryWithObjectActions(
			getSiteScopedObjectDefinition1());
	}

	@Test
	public void testPutCustomObjectEntryPermissionsPage() throws Exception {

		// Invalid permissions

		JSONObject objectEntryJSONObject =
			_postCustomObjectEntryWithPermissions(true, null);

		JSONObject jsonObject = _putCustomObjectEntryPermissionsPage(
			objectEntryJSONObject.getLong("id"),
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE},
					RandomTestUtil.randomString())));

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		// Permissions with different roles

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		ObjectDefinition objectDefinition = getObjectDefinition1();

		addResourcePermission(
			ActionKeys.DELETE, objectDefinition.getClassName(), role1);

		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		objectEntryJSONObject = _postCustomObjectEntryWithPermissions(
			true,
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.PERMISSIONS}, role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					role2.getName())));

		Role role3 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_assertPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.UPDATE},
					role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role3.getName())),
			_putCustomObjectEntryPermissionsPage(
				objectEntryJSONObject.getLong("id"),
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE}, role1.getName()),
					getPermissionsJSONObject(
						new String[] {ActionKeys.DELETE}, role3.getName()))));
		_assertPermissions(
			JSONUtil.putAll(
				getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role1.getName()),
				getPermissionsJSONObject(
					new String[] {ActionKeys.VIEW}, role3.getName())),
			_putCustomObjectEntryPermissionsPage(
				objectEntryJSONObject.getLong("id"),
				JSONUtil.putAll(
					getPermissionsJSONObject(
						new String[] {ActionKeys.VIEW}, role3.getName()))));

		// Permissions with empty list

		JSONArray companyPermissionsJSONArray = JSONUtil.putAll(
			getPermissionsJSONObject(
				new String[] {ActionKeys.DELETE}, role1.getName()));

		_assertPermissions(
			companyPermissionsJSONArray,
			_putCustomObjectEntryPermissionsPage(
				objectEntryJSONObject.getLong("id"),
				JSONFactoryUtil.createJSONArray()));
	}

	@Test
	public void testPutCustomObjectEntryPermissionsPageAsOwner()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		ObjectDefinition objectDefinition = getObjectDefinition1();

		addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			objectDefinition.getResourceName(), role);

		String password = RandomTestUtil.randomString();

		User user = addUser(
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE),
			password);

		roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"externalReferenceCode", RandomTestUtil.randomString()
					).toString(),
					objectDefinition.getRESTContextPath(), Http.Method.POST);

				jsonObject = _putCustomObjectEntryPermissionsPage(
					jsonObject.getLong("id"),
					JSONUtil.putAll(
						getPermissionsJSONObject(
							new String[0], role.getName())));

				Assert.assertNotEquals(
					"FORBIDDEN", jsonObject.getString("status"));
			}
		);
	}

	private ObjectEntry _addObjectEntry(
			DepotEntry depotEntry, ObjectDefinition objectDefinition,
			ObjectEntryFolder objectEntryFolder, ServiceContext serviceContext,
			long userId)
		throws Exception {

		return objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), userId,
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			serviceContext);
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			DepotEntry depotEntry, ServiceContext serviceContext)
		throws Exception {

		ObjectEntryFolder parentObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					depotEntry.getGroupId(), depotEntry.getCompanyId());

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			null, depotEntry.getGroupId(), depotEntry.getUserId(),
			parentObjectEntryFolder.getObjectEntryFolderId(),
			RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			RandomTestUtil.randomString(), serviceContext);
	}

	private void _assertPermissions(
			JSONArray expectedPermissionsJSONArray, JSONObject jsonObject)
		throws Exception {

		JSONArray actualPermissionsJSONArray = jsonObject.getJSONArray(
			"permissions");

		if (actualPermissionsJSONArray == null) {
			actualPermissionsJSONArray = jsonObject.getJSONArray("items");
		}

		JSONAssert.assertEquals(
			String.valueOf(expectedPermissionsJSONArray),
			String.valueOf(actualPermissionsJSONArray),
			JSONCompareMode.LENIENT);
	}

	private Map<String, String> _getActionValue(String href, String method) {
		return HashMapBuilder.put(
			"href", href
		).put(
			"method", method
		).build();
	}

	private Map<String, Map<String, String>> _getExpectedActions(
			String externalReferenceCode, String objectEntryFolderId,
			String objectEntryId, boolean sharingEnabled)
		throws Exception {

		ObjectDefinition siteScopedObjectDefinition =
			getSiteScopedObjectDefinition1();

		String href = StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false), "/o",
			siteScopedObjectDefinition.getRESTContextPath(), StringPool.SLASH,
			objectEntryId);
		String scopedEndpoint = StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false), "/o",
			siteScopedObjectDefinition.getRESTContextPath(), "/scopes/",
			group.getGroupId());

		return HashMapBuilder.<String, Map<String, String>>put(
			"copy",
			_getActionValue(
				StringBundler.concat(
					href, "/by-object-entry-folder-id/{objectEntryFolderId}",
					"/copy"),
				"POST")
		).put(
			"copy-replace",
			_getActionValue(
				StringBundler.concat(
					href, "/by-object-entry-folder-id/{objectEntryFolderId}",
					"/copy-replace"),
				"POST")
		).put(
			"delete", _getActionValue(href, "DELETE")
		).put(
			"duplicate",
			_getActionValue(
				StringBundler.concat(
					href, "/by-object-entry-folder-id/", objectEntryFolderId,
					"/copy"),
				"POST")
		).put(
			"expire",
			_getActionValue(
				StringBundler.concat(
					scopedEndpoint, "/by-external-reference-code/",
					externalReferenceCode, "/expire"),
				"POST")
		).put(
			"get", _getActionValue(href, "GET")
		).put(
			"get-by-scope", _getActionValue(scopedEndpoint, "GET")
		).put(
			"move",
			_getActionValue(
				StringBundler.concat(
					href, "/by-object-entry-folder-id/{objectEntryFolderId}",
					"/move"),
				"POST")
		).put(
			"move-replace",
			_getActionValue(
				StringBundler.concat(
					href, "/by-object-entry-folder-id/{objectEntryFolderId}",
					"/move-replace"),
				"POST")
		).put(
			"permissions", _getActionValue(href + "/permissions", "GET")
		).put(
			"replace", _getActionValue(href, "PUT")
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
		).put(
			"versions",
			_getActionValue(
				StringBundler.concat(
					scopedEndpoint, "/by-external-reference-code/",
					externalReferenceCode, "/versions"),
				"GET")
		).build();
	}

	private JSONObject _getObjectEntriesPageActionsJSONObject()
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition4();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath(), Http.Method.GET);

		return jsonObject.getJSONObject("actions");
	}

	private JSONObject _getObjectEntryActionsJSONObject(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String password, User user)
		throws Exception {

		AtomicReference<JSONObject> atomicReference = new AtomicReference<>();

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				JSONObject jsonObject = getObjectEntryJSONObject(
					objectDefinition,
					String.valueOf(objectEntry.getObjectEntryId()));

				atomicReference.set(jsonObject.getJSONObject("actions"));
			}
		);

		return atomicReference.get();
	}

	private JSONObject
			_patchPutByExternalReferenceCodeCustomObjectEntryWithPermissions(
				Http.Method httpMethod, boolean nestedFields,
				JSONObject objectEntryJSONObject,
				JSONArray permissionsJSONArray)
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition1();

		String endpoint =
			objectDefinition.getRESTContextPath() +
				"/by-external-reference-code/" +
					objectEntryJSONObject.getString("externalReferenceCode");

		if (nestedFields) {
			endpoint += "?nestedFields=permissions";
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"permissions", permissionsJSONArray
			).toString(),
			endpoint, httpMethod);
	}

	private JSONObject _patchPutCustomObjectEntryWithPermissions(
			Http.Method httpMethod, boolean nestedFields,
			JSONObject objectEntryJSONObject, JSONArray permissionsJSONArray)
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition1();

		String endpoint =
			objectDefinition.getRESTContextPath() + StringPool.SLASH +
				objectEntryJSONObject.getLong("id");

		if (nestedFields) {
			endpoint += "?nestedFields=permissions";
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"permissions", permissionsJSONArray
			).toString(),
			endpoint, httpMethod);
	}

	private JSONObject _postCustomObjectEntryWithPermissions(
			boolean nestedFields, JSONArray permissionsJSONArray)
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition1();

		String endpoint = objectDefinition.getRESTContextPath();

		if (nestedFields) {
			endpoint += "?nestedFields=permissions";
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"permissions", permissionsJSONArray
			).toString(),
			endpoint, Http.Method.POST);
	}

	private JSONObject _putCustomObjectEntryPermissionsPage(
			long objectEntryId, JSONArray permissionsJSONArray)
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition1();

		return HTTPTestUtil.invokeToJSONObject(
			permissionsJSONArray.toString(),
			StringBundler.concat(
				objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntryId, "/permissions"),
			Http.Method.PUT);
	}

	private void _testGetObjectEntryActions(boolean sharingEnabled)
		throws Exception {

		JSONObject jsonObject = postObjectEntry(
			group.getGroupId(), getSiteScopedObjectDefinition1());

		jsonObject = getObjectEntryJSONObject(
			getSiteScopedObjectDefinition1(), jsonObject.getString("id"));

		JSONObject actionsJSONObject = jsonObject.getJSONObject("actions");

		Assert.assertEquals(
			_getExpectedActions(
				jsonObject.getString("externalReferenceCode"),
				jsonObject.getString("objectEntryFolderId"),
				jsonObject.getString("id"), sharingEnabled),
			actionsJSONObject.toMap());
	}

	private void _testPostCustomObjectEntryWithObjectActions(
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectAction objectAction = _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"secret", "standalone"
			).put(
				"url", "https://standalone.com"
			).build(),
			false);

		JSONObject jsonObject = postObjectEntry(testGroupId, objectDefinition);

		JSONObject actionsJSONObject = jsonObject.getJSONObject("actions");

		JSONObject actionJSONObject = actionsJSONObject.getJSONObject(
			objectAction.getName());

		Assert.assertEquals(
			StringBundler.concat(
				"http://localhost:", PortalUtil.getPortalServerPort(false),
				"/o", getEndpoint(objectDefinition, testGroupId),
				"/by-external-reference-code/",
				jsonObject.getString("externalReferenceCode"),
				"/object-actions/", objectAction.getName()),
			actionJSONObject.getString("href"));
		Assert.assertEquals("PUT", actionJSONObject.getString("method"));
	}

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

}