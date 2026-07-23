/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.object.deployer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.deployer.ObjectDefinitionDeployer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionDeployerImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_group = _depotEntry.getGroup();
	}

	@Test
	public void testDeploy() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", TestPropsValues.getCompanyId());

		_objectDefinitionDeployer.deploy(objectDefinition);

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				objectDefinition.getClassName());

		Class<?> clazz = modelResourcePermission.getClass();

		Assert.assertEquals(
			"CMSDefaultPermissionObjectEntryModelResourcePermission",
			clazz.getSimpleName());

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				role.getRoleId(), ActionKeys.VIEW));

		role = RoleTestUtil.addRole(RoleConstants.TYPE_DEPOT);

		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			ObjectEntry objectEntry = CMSDefaultPermissionUtil.fetchObjectEntry(
				_group.getCompanyId(), _group.getCreatorUserId(),
				_group.getExternalReferenceCode(), DepotEntry.class.getName(),
				_filterFactory);

			Assert.assertFalse(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.DELETE));
			Assert.assertFalse(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.PERMISSIONS));
			Assert.assertFalse(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.UPDATE));
			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.VIEW));
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), DepotEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_depotEntry.getDepotEntryId()), role.getRoleId(),
			new String[] {ActionKeys.PERMISSIONS});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			ObjectEntry objectEntry = CMSDefaultPermissionUtil.fetchObjectEntry(
				_group.getCompanyId(), _group.getCreatorUserId(),
				_group.getExternalReferenceCode(), DepotEntry.class.getName(),
				_filterFactory);

			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.DELETE));
			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.PERMISSIONS));
			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.UPDATE));
			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.VIEW));
		}

		ObjectEntryFolder rootObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_group.getGroupId(), _group.getCompanyId());

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), _group.getGroupId(),
				_group.getCreatorUserId(),
				rootObjectEntryFolder.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		role = RoleTestUtil.addRole(RoleConstants.TYPE_DEPOT);
		user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		permissionChecker = PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			ObjectEntry objectEntry = CMSDefaultPermissionUtil.fetchObjectEntry(
				objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolder.class.getName(), _filterFactory);

			Assert.assertFalse(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.DELETE));
			Assert.assertFalse(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.PERMISSIONS));
			Assert.assertFalse(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.UPDATE));
			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.VIEW));
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), ObjectEntryFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
			role.getRoleId(), new String[] {ActionKeys.PERMISSIONS});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			ObjectEntry objectEntry = CMSDefaultPermissionUtil.fetchObjectEntry(
				objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolder.class.getName(), _filterFactory);

			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.DELETE));
			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.PERMISSIONS));
			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.UPDATE));
			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, objectEntry, ActionKeys.VIEW));
		}
	}

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.object.deployer.ObjectDefinitionDeployerImpl"
	)
	private ObjectDefinitionDeployer _objectDefinitionDeployer;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}