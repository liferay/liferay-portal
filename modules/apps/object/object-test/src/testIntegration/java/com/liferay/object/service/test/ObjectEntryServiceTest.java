/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.configuration.ObjectConfiguration;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.ObjectEntryCountException;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Edge;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.Tree;
import com.liferay.object.tree.constants.TreeConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marco Leo
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-34594")}
)
@RunWith(Arquillian.class)
public class ObjectEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_adminUser = TestPropsValues.getUser();
		_group = GroupTestUtil.addGroup();
		_guestUser = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			false, ObjectDefinitionTestUtil.getRandomName(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					"First Name", "firstName", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					"Last Name", "lastName", false)),
			ObjectDefinitionConstants.SCOPE_SITE, TestPropsValues.getUserId());

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		_tree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA", "AB"}
			).put(
				"AA", new String[] {"AAA", "AAB"}
			).put(
				"AB", new String[0]
			).put(
				"AAA", new String[0]
			).put(
				"AAB", new String[0]
			).build());

		_rootObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_A");

		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() throws Exception {
		_setUser(_adminUser);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AB", "C_AAA", "C_AAB"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testAddObjectEntry() throws Exception {
		_setUser(_adminUser);

		Assert.assertNotNull(
			_objectEntryService.addObjectEntry(
				_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"firstName", RandomStringUtils.randomAlphabetic(5)
				).build(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), _adminUser.getUserId())));

		_setUser(_guestUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _guestUser.getUserId(), " must have ADD_OBJECT_ENTRY ",
				"permission for ", _objectDefinition.getResourceName(), " ",
				_group.getGroupId()),
			() -> _objectEntryService.addObjectEntry(
				_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null, Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _guestUser.getUserId())));

		_setUser(_user);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ADD_OBJECT_ENTRY ",
				"permission for ", _objectDefinition.getResourceName(), " ",
				_group.getGroupId()),
			() -> _objectEntryService.addObjectEntry(
				_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null, Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), _user.getUserId())));

		_setUser(_guestUser);

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), _objectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			guestRole.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY);

		Assert.assertNotNull(
			_objectEntryService.addObjectEntry(
				_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"firstName", RandomStringUtils.randomAlphabetic(5)
				).build(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), _guestUser.getUserId())));

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			guestRole.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY);

		_setUser(_user);

		Assert.assertNotNull(
			_objectEntryService.addObjectEntry(
				_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"firstName", RandomStringUtils.randomAlphabetic(5)
				).build(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), _guestUser.getUserId())));

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), StringUtil.randomString()
				).build(),
				StringUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), ObjectEntryFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
			role.getRoleId(), new String[] {ActionKeys.ADD_ENTRY});

		_setUser(_user);

		Assert.assertNotNull(
			_objectEntryService.addObjectEntry(
				_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
				objectEntryFolder.getObjectEntryFolderId(), null,
				HashMapBuilder.<String, Serializable>put(
					"firstName", RandomStringUtils.randomAlphabetic(5)
				).build(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _guestUser.getUserId())));

		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(), ObjectEntryFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
			role.getRoleId(), ActionKeys.ADD_ENTRY);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" must have ADD_ENTRY permission for ",
				ObjectEntryFolder.class.getName(), " ",
				objectEntryFolder.getObjectEntryFolderId()),
			() -> _objectEntryService.addObjectEntry(
				_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
				objectEntryFolder.getObjectEntryFolderId(), null,
				HashMapBuilder.<String, Serializable>put(
					"firstName", RandomStringUtils.randomAlphabetic(5)
				).build(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), _guestUser.getUserId())));
	}

	@Test
	public void testAddObjectEntryHierarchy() throws Exception {

		// Root company permissions must be inherited

		_setUser(_user);

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		Iterator<Node> iterator = _tree.iterator();

		Node rootNode = iterator.next();

		Map<Long, ObjectEntry> objectEntries =
			HashMapBuilder.<Long, ObjectEntry>put(
				rootNode.getPrimaryKey(),
				_objectEntryService.addObjectEntry(
					0, rootNode.getPrimaryKey(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null, Collections.emptyMap(),
					ServiceContextTestUtil.getServiceContext(
						TestPropsValues.getGroupId(), _adminUser.getUserId()))
			).build();

		while (iterator.hasNext()) {
			Node node = iterator.next();

			objectEntries.put(
				node.getPrimaryKey(),
				_objectEntryService.addObjectEntry(
					0, node.getPrimaryKey(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null,
					HashMapBuilder.<String, Serializable>put(
						() -> {
							Edge edge = node.getEdge();

							ObjectRelationship objectRelationship =
								_objectRelationshipLocalService.
									getObjectRelationship(
										edge.getObjectRelationshipId());

							ObjectField objectField =
								_objectFieldLocalService.getObjectField(
									objectRelationship.getObjectFieldId2());

							return objectField.getName();
						},
						() -> {
							Node parentNode = node.getParentNode();

							ObjectEntry objectEntry = objectEntries.get(
								parentNode.getPrimaryKey());

							return objectEntry.getObjectEntryId();
						}
					).build(),
					ServiceContextTestUtil.getServiceContext(
						TestPropsValues.getGroupId(), _adminUser.getUserId())));
		}

		// User can add an object entry to descendant object definitions
		// with the update permission

		_setUser(_adminUser);

		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_rootObjectDefinition.getObjectDefinitionId()),
			role.getRoleId(), new String[] {ActionKeys.UPDATE});

		_setUser(_user);

		iterator = _tree.iterator();

		while (iterator.hasNext()) {
			Node node = iterator.next();

			if (node.isRoot()) {
				continue;
			}

			Assert.assertNotNull(
				_objectEntryService.addObjectEntry(
					0, node.getPrimaryKey(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null,
					HashMapBuilder.<String, Serializable>put(
						() -> {
							Edge edge = node.getEdge();

							ObjectRelationship objectRelationship =
								_objectRelationshipLocalService.
									getObjectRelationship(
										edge.getObjectRelationshipId());

							ObjectField objectField =
								_objectFieldLocalService.getObjectField(
									objectRelationship.getObjectFieldId2());

							return objectField.getName();
						},
						() -> {
							Node parentNode = node.getParentNode();

							ObjectEntry objectEntry = objectEntries.get(
								parentNode.getPrimaryKey());

							return objectEntry.getObjectEntryId();
						}
					).build(),
					ServiceContextTestUtil.getServiceContext(
						TestPropsValues.getGroupId(), _user.getUserId())));
		}

		// User cannot add an object entry to a root object definition
		// with the update permission

		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" must have ADD_OBJECT_ENTRY permission for ",
				_rootObjectDefinition.getResourceName(), " "),
			() -> _objectEntryService.addObjectEntry(
				0, _rootObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null, Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), _user.getUserId())));
	}

	@Test
	public void testDeleteObjectEntry() throws Exception {
		try {
			_testDeleteObjectEntry(_adminUser, _user);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _user.getUserId() +
						" must have DELETE permission for"));
		}

		_testDeleteObjectEntry(_adminUser, _adminUser);
		_testDeleteObjectEntry(_user, _user);
	}

	@Test
	public void testDeleteObjectEntryHierarchy() throws Exception {

		// Root company permissions must be inherited

		Node objectDefinitionRootNode = _tree.getRootNode();

		Tree tree = TreeTestUtil.createObjectEntryTree(
			"1", _objectDefinitionLocalService, _objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			objectDefinitionRootNode.getPrimaryKey());

		_setUser(_user);

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.DELETE);

		TreeTestUtil.forEachNodeObjectEntry(
			tree.iterator(TreeConstants.ITERATOR_TYPE_POST_ORDER),
			_objectEntryLocalService,
			objectEntry -> Assert.assertNotNull(
				_objectEntryService.deleteObjectEntry(
					objectEntry.getObjectEntryId())));

		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.DELETE);

		// Root individual permissions must be inherited

		tree = TreeTestUtil.createObjectEntryTree(
			"1", _objectDefinitionLocalService, _objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			objectDefinitionRootNode.getPrimaryKey());

		Node objectEntryRootNode = tree.getRootNode();

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryRootNode.getPrimaryKey()),
			role.getRoleId(), new String[] {ActionKeys.DELETE});

		TreeTestUtil.forEachNodeObjectEntry(
			tree.iterator(TreeConstants.ITERATOR_TYPE_POST_ORDER),
			_objectEntryLocalService,
			objectEntry -> Assert.assertNotNull(
				_objectEntryService.deleteObjectEntry(
					objectEntry.getObjectEntryId())));

		// User can delete an object entry of a descendant object definition
		// with the update permission

		_setUser(_adminUser);

		tree = TreeTestUtil.createObjectEntryTree(
			"1", _objectDefinitionLocalService, _objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			_rootObjectDefinition.getRootObjectDefinitionId());

		objectEntryRootNode = tree.getRootNode();

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryRootNode.getPrimaryKey()),
			role.getRoleId(), new String[] {ActionKeys.UPDATE});

		_setUser(_user);

		TreeTestUtil.forEachNodeObjectEntry(
			tree.iterator(TreeConstants.ITERATOR_TYPE_POST_ORDER),
			_objectEntryLocalService,
			objectEntry -> {
				if (objectEntry.getRootObjectEntryId() ==
						objectEntry.getObjectEntryId()) {

					return;
				}

				Assert.assertNotNull(
					_objectEntryService.deleteObjectEntry(
						objectEntry.getObjectEntryId()));
			});

		long rootObjectEntryId = objectEntryRootNode.getPrimaryKey();

		// User cannot delete an object entry of a root object definition
		// with the update permission

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have DELETE permission for ",
				_rootObjectDefinition.getClassName(), " ", rootObjectEntryId),
			() -> _objectEntryService.deleteObjectEntry(rootObjectEntryId));
	}

	@Test
	public void testGetObjectEntry() throws Exception {
		_setUser(_adminUser);

		ObjectEntry adminObjectEntry = _addObjectEntry(_adminUser);

		Assert.assertNotNull(
			_objectEntryService.getObjectEntry(
				adminObjectEntry.getObjectEntryId()));

		_setUser(_user);

		ObjectEntry userObjectEntry = _addObjectEntry(_user);

		Assert.assertNotNull(
			_objectEntryService.getObjectEntry(
				userObjectEntry.getObjectEntryId()));

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have VIEW permission for ",
				_objectDefinition.getClassName(), " ",
				adminObjectEntry.getObjectEntryId()),
			() -> _objectEntryService.getObjectEntry(
				adminObjectEntry.getObjectEntryId()));

		_setUser(_guestUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _guestUser.getUserId(), " must have VIEW permission ",
				"for ", _objectDefinition.getClassName(), " ",
				adminObjectEntry.getObjectEntryId()),
			() -> _objectEntryService.getObjectEntry(
				adminObjectEntry.getObjectEntryId()));

		ObjectEntry guestUserObjectEntry = _addObjectEntry(_guestUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _guestUser.getUserId(), " must have VIEW permission ",
				"for ", _objectDefinition.getClassName(), " ",
				guestUserObjectEntry.getObjectEntryId()),
			() -> _objectEntryService.getObjectEntry(
				guestUserObjectEntry.getObjectEntryId()));

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), _objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			guestRole.getRoleId(), ActionKeys.VIEW);

		Assert.assertNotNull(
			_objectEntryService.getObjectEntry(
				adminObjectEntry.getObjectEntryId()));
	}

	@Test
	public void testGetObjectEntryHierarchy() throws Exception {

		// Root company permissions must be inherited

		Node objectDefinitionRootNode = _tree.getRootNode();

		Tree tree = TreeTestUtil.createObjectEntryTree(
			"1", _objectDefinitionLocalService, _objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			objectDefinitionRootNode.getPrimaryKey());

		_setUser(_user);

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);

		TreeTestUtil.forEachNodeObjectEntry(
			tree.iterator(), _objectEntryLocalService,
			objectEntry -> Assert.assertNotNull(
				_objectEntryService.getObjectEntry(
					objectEntry.getObjectEntryId())));

		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);

		// Root individual permissions must be inherited

		Node objectEntryRootNode = tree.getRootNode();

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryRootNode.getPrimaryKey()),
			role.getRoleId(), new String[] {ActionKeys.VIEW});

		TreeTestUtil.forEachNodeObjectEntry(
			tree.iterator(), _objectEntryLocalService,
			objectEntry -> Assert.assertNotNull(
				_objectEntryService.getObjectEntry(
					objectEntry.getObjectEntryId())));

		// User can view an object entry of a descendant object definition
		// with the update permission

		_setUser(_adminUser);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			_rootObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryRootNode.getPrimaryKey()),
			role.getRoleId(), new String[] {ActionKeys.UPDATE});

		_setUser(_user);

		TreeTestUtil.forEachNodeObjectEntry(
			tree.iterator(), _objectEntryLocalService,
			objectEntry -> {
				if (objectEntry.getRootObjectEntryId() ==
						objectEntry.getObjectEntryId()) {

					return;
				}

				Assert.assertNotNull(
					_objectEntryService.getObjectEntry(
						objectEntry.getObjectEntryId()));
			});

		long rootObjectEntryId = objectEntryRootNode.getPrimaryKey();

		// User cannot view an object entry of a root object definition
		// with the update permission

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have VIEW permission for ",
				_rootObjectDefinition.getClassName(), " ", rootObjectEntryId),
			() -> _objectEntryService.getObjectEntry(rootObjectEntryId));
	}

	@Test
	public void testGetOrDeleteObjectEntryWithAccountEntryRestricted()
		throws Exception {

		_objectDefinition.setAccountEntryRestricted(true);

		ObjectDefinition accountEntryObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(),
				AccountEntry.class.getSimpleName());

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				accountEntryObjectDefinition.getObjectDefinitionId(),
				_objectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"relationship", false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_objectDefinition.setAccountEntryRestrictedObjectFieldId(
			objectRelationship.getObjectFieldId2());

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT, "account", null,
			null, null, null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"r_relationship_accountEntryId",
				accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		_setUser(_user);

		_resourcePermissionLocalService.addModelResourcePermissions(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			_user.getUserId(), _objectDefinition.getClassName(),
			String.valueOf(objectEntry.getObjectEntryId()),
			ModelPermissionsFactory.create(
				HashMapBuilder.put(
					RoleConstants.USER,
					new String[] {ActionKeys.DELETE, ActionKeys.VIEW}
				).build(),
				_objectDefinition.getClassName()));

		Assert.assertNotNull(
			_objectEntryService.getObjectEntry(objectEntry.getObjectEntryId()));

		_objectEntryService.deleteObjectEntry(objectEntry.getObjectEntryId());

		_accountEntryLocalService.deleteAccountEntry(accountEntry);
	}

	@Test
	public void testSearchObjectEntries() throws Exception {
		_setUser(_adminUser);

		ObjectEntry objectEntry1 = _addObjectEntry(_adminUser);
		ObjectEntry objectEntry2 = _addObjectEntry(_adminUser);

		BaseModelSearchResult<ObjectEntry> baseModelSearchResult =
			_objectEntryLocalService.searchObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), null, 0, 20);

		Assert.assertEquals(2, baseModelSearchResult.getLength());

		_setUser(_user);

		baseModelSearchResult = _objectEntryLocalService.searchObjectEntries(
			0, _objectDefinition.getObjectDefinitionId(), null, 0, 20);

		Assert.assertEquals(0, baseModelSearchResult.getLength());

		_objectEntryLocalService.deleteObjectEntry(objectEntry1);
		_objectEntryLocalService.deleteObjectEntry(objectEntry2);
	}

	@Test
	public void testValidateMaximumNumberOfGuestUserObjectEntriesPerObjectDefinitionPerDay()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			ObjectConfiguration.class, TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"duration", 1
			).put(
				"maximumFileSizeForGuestUsers", 25
			).put(
				"maximumNumberOfGuestUserObjectEntriesPerObjectDefinition", 1
			).put(
				"timeScale", "days"
			).build());

		_configurationProvider.saveSystemConfiguration(
			ObjectConfiguration.class,
			HashMapDictionaryBuilder.<String, Object>put(
				"duration", 1
			).put(
				"maximumFileSizeForGuestUsers", 25
			).put(
				"maximumNumberOfGuestUserObjectEntriesPerObjectDefinition", 10
			).put(
				"timeScale", "days"
			).build());

		_setUser(_adminUser);

		_objectEntryService.addObjectEntry(
			_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), _adminUser.getUserId()));

		_setUser(_guestUser);

		_addResourcePermissionToGuestUser();

		try {
			ObjectEntry objectEntry = _objectEntryService.addObjectEntry(
				_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null, Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), _guestUser.getUserId()));

			Assert.assertNotNull(objectEntry);

			AssertUtils.assertFailure(
				ObjectEntryCountException.class,
				StringBundler.concat(
					"The limit of guest entries for ",
					_objectDefinition.getLabel(
						_objectDefinition.getDefaultLanguageId()),
					" has been reached and will no longer be accepted"),
				() -> _objectEntryService.addObjectEntry(
					_group.getGroupId(),
					_objectDefinition.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null, Collections.emptyMap(),
					ServiceContextTestUtil.getServiceContext(
						TestPropsValues.getGroupId(), _guestUser.getUserId())));

			_assertUserNotificationEventsCount();

			objectEntry.setCreateDate(
				Date.from(
					LocalDate.now(
					).minusDays(
						1
					).atStartOfDay(
						ZoneId.systemDefault()
					).toInstant()));

			_objectEntryLocalService.updateObjectEntry(objectEntry);

			Assert.assertNotNull(
				_objectEntryService.addObjectEntry(
					_group.getGroupId(),
					_objectDefinition.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null, Collections.emptyMap(),
					ServiceContextTestUtil.getServiceContext(
						TestPropsValues.getGroupId(), _guestUser.getUserId())));

			AssertUtils.assertFailure(
				ObjectEntryCountException.class,
				StringBundler.concat(
					"The limit of guest entries for ",
					_objectDefinition.getLabel(
						_objectDefinition.getDefaultLanguageId()),
					" has been reached and will no longer be accepted"),
				() -> _objectEntryService.addObjectEntry(
					_group.getGroupId(),
					_objectDefinition.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null, Collections.emptyMap(),
					ServiceContextTestUtil.getServiceContext(
						TestPropsValues.getGroupId(), _guestUser.getUserId())));

			_assertUserNotificationEventsCount();
		}
		finally {
			_configurationProvider.deleteCompanyConfiguration(
				ObjectConfiguration.class, TestPropsValues.getCompanyId());
			_configurationProvider.deleteSystemConfiguration(
				ObjectConfiguration.class);
		}
	}

	@Test
	public void testValidateMaximumNumberOfGuestUserObjectEntriesPerObjectDefinitionPerWeek()
		throws Exception {

		_setUser(_guestUser);

		_configurationProvider.saveSystemConfiguration(
			ObjectConfiguration.class,
			HashMapDictionaryBuilder.<String, Object>put(
				"duration", 2
			).put(
				"maximumFileSizeForGuestUsers", 25
			).put(
				"maximumNumberOfGuestUserObjectEntriesPerObjectDefinition", 1
			).put(
				"timeScale", "weeks"
			).build());

		_addResourcePermissionToGuestUser();

		ObjectEntry objectEntry = _objectEntryService.addObjectEntry(
			_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), _guestUser.getUserId()));

		objectEntry.setCreateDate(
			Date.from(
				LocalDate.now(
				).minusDays(
					14
				).atStartOfDay(
					ZoneId.systemDefault()
				).toInstant()));

		_objectEntryLocalService.updateObjectEntry(objectEntry);

		try {
			Assert.assertNotNull(
				_objectEntryService.addObjectEntry(
					_group.getGroupId(),
					_objectDefinition.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null, Collections.emptyMap(),
					ServiceContextTestUtil.getServiceContext(
						TestPropsValues.getGroupId(), _guestUser.getUserId())));

			AssertUtils.assertFailure(
				ObjectEntryCountException.class,
				StringBundler.concat(
					"The limit of guest entries for ",
					_objectDefinition.getLabel(
						_objectDefinition.getDefaultLanguageId()),
					" has been reached and will no longer be accepted"),
				() -> _objectEntryService.addObjectEntry(
					_group.getGroupId(),
					_objectDefinition.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null, Collections.emptyMap(),
					ServiceContextTestUtil.getServiceContext(
						TestPropsValues.getGroupId(), _guestUser.getUserId())));

			_assertUserNotificationEventsCount();
		}
		finally {
			_configurationProvider.deleteSystemConfiguration(
				ObjectConfiguration.class);
		}
	}

	private ObjectEntry _addObjectEntry(User user) throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			user.getUserId(), _group.getGroupId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomStringUtils.randomAlphabetic(5)
			).put(
				"LastName", RandomStringUtils.randomAlphabetic(5)
			).build(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), user.getUserId()));
	}

	private void _addResourcePermissionToGuestUser() throws Exception {
		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), _objectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			guestRole.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY);
	}

	private void _assertUserNotificationEventsCount() throws PortalException {
		String portletId =
			_objectDefinition.isUnmodifiableSystemObject() ? StringPool.BLANK :
				_objectDefinition.getPortletId();

		Role role = _roleLocalService.getRole(
			_objectDefinition.getCompanyId(), RoleConstants.ADMINISTRATOR);

		long[] userIds = _userLocalService.getRoleUserIds(role.getRoleId());

		for (long userId : userIds) {
			int count = 0;

			List<UserNotificationEvent> userNotificationEvents =
				_userNotificationLocalService.getUserNotificationEvents(
					userId, portletId,
					LocalDate.now(
					).atStartOfDay(
						ZoneId.systemDefault()
					).toInstant(
					).getEpochSecond(),
					true);

			for (UserNotificationEvent userNotificationEvent :
					userNotificationEvents) {

				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					userNotificationEvent.getPayload());

				if (jsonObject.has("exceedsObjectEntryLimit")) {
					count++;
				}
			}

			Assert.assertEquals(1, count);
		}
	}

	private void _setUser(User user) throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	private void _testDeleteObjectEntry(User ownerUser, User user)
		throws Exception {

		ObjectEntry deleteObjectEntry = null;
		ObjectEntry objectEntry = null;

		try {
			_setUser(user);

			objectEntry = _addObjectEntry(ownerUser);

			deleteObjectEntry = _objectEntryService.deleteObjectEntry(
				objectEntry.getObjectEntryId());
		}
		finally {
			if (deleteObjectEntry == null) {
				_objectEntryLocalService.deleteObjectEntry(objectEntry);
			}
		}
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	private User _adminUser;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@DeleteAfterTestRun
	private Group _group;

	private User _guestUser;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryService _objectEntryService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private PermissionChecker _originalPermissionChecker;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ObjectDefinition _rootObjectDefinition;
	private Tree _tree;
	private User _user;

	@Inject(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@Inject
	private UserNotificationEventLocalService _userNotificationLocalService;

}