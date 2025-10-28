/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.exception.NoSuchObjectEntryFolderException;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectEntryFolderServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_adminUser = TestPropsValues.getUser();
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() throws Exception {
		_setUser(_adminUser);
	}

	@Test
	public void testGetOrAddEmptyObjectEntryFolder() throws Exception {

		// Lazy referencing disabled

		_setUser(_adminUser);

		String externalReferenceCode = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			NoSuchObjectEntryFolderException.class,
			StringBundler.concat(
				"No ObjectEntryFolder exists with the key {",
				"externalReferenceCode=", externalReferenceCode, ", groupId=",
				_group.getGroupId(), ", companyId=",
				TestPropsValues.getCompanyId(), "}"),
			() -> _objectEntryFolderService.getOrAddEmptyObjectEntryFolder(
				externalReferenceCode, _group.getGroupId(),
				TestPropsValues.getCompanyId(),
				ServiceContextTestUtil.getServiceContext()));

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			_objectEntryFolder =
				_objectEntryFolderService.getOrAddEmptyObjectEntryFolder(
					RandomTestUtil.randomString(), _group.getGroupId(),
					TestPropsValues.getCompanyId(),
					ServiceContextTestUtil.getServiceContext());

			// Without permissions

			User user = UserTestUtil.addUser();

			_setUser(user);

			AssertUtils.assertFailure(
				PrincipalException.MustHavePermission.class,
				StringBundler.concat(
					"User ", user.getUserId(),
					" must have ADD_OBJECT_ENTRY_FOLDER permission for ",
					ObjectConstants.RESOURCE_NAME_OBJECT_ENTRY_FOLDER, " ",
					_group.getGroupId()),
				() -> _objectEntryFolderService.getOrAddEmptyObjectEntryFolder(
					RandomTestUtil.randomString(), _group.getGroupId(),
					TestPropsValues.getCompanyId(),
					ServiceContextTestUtil.getServiceContext()));

			// Without permissions, existing object entry folder

			AssertUtils.assertFailure(
				PrincipalException.MustHavePermission.class,
				StringBundler.concat(
					"User ", user.getUserId(),
					" must have VIEW permission for ",
					"com.liferay.object.model.ObjectEntryFolder ",
					_objectEntryFolder.getObjectEntryFolderId()),
				() -> _objectEntryFolderService.getOrAddEmptyObjectEntryFolder(
					_objectEntryFolder.getExternalReferenceCode(),
					_objectEntryFolder.getGroupId(),
					_objectEntryFolder.getCompanyId(),
					ServiceContextTestUtil.getServiceContext()));
		}
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
	)
	@Test
	public void testMoveObjectEntryFolderTrash() throws Exception {
		try {
			_testMoveObjectEntryFolderTrash(_adminUser, _user);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertTrue(
				StringUtil.startsWith(
					principalException.getMessage(),
					"User " + _user.getUserId() +
						" must have DELETE permission for"));
		}

		_testMoveObjectEntryFolderTrash(_adminUser, _adminUser);
		_testMoveObjectEntryFolderTrash(_user, _user);
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
	)
	@Test
	public void testRestoreObjectEntryFolderFromTrash() throws Exception {
		try {
			_testRestoreObjectEntryFolderFromTrash(_adminUser, _user);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertTrue(
				StringUtil.startsWith(
					principalException.getMessage(),
					"User " + _user.getUserId() +
						" must have DELETE permission for"));
		}

		_testRestoreObjectEntryFolderFromTrash(_adminUser, _adminUser);
		_testRestoreObjectEntryFolderFromTrash(_user, _user);
	}

	private void _setUser(User user) throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	private void _testMoveObjectEntryFolderTrash(User ownerUser, User user)
		throws Exception {

		_setUser(ownerUser);

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				_group.getGroupId(), ownerUser.getUserId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		_setUser(user);

		_objectEntryFolderService.moveObjectEntryFolderToTrash(
			objectEntryFolder, ServiceContextTestUtil.getServiceContext());
	}

	private void _testRestoreObjectEntryFolderFromTrash(
			User ownerUser, User user)
		throws Exception {

		_setUser(ownerUser);

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				_group.getGroupId(), ownerUser.getUserId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		objectEntryFolder =
			_objectEntryFolderLocalService.moveObjectEntryFolderToTrash(
				ownerUser.getUserId(), objectEntryFolder,
				ServiceContextTestUtil.getServiceContext());

		_setUser(user);

		_objectEntryFolderService.restoreObjectEntryFolderFromTrash(
			objectEntryFolder, ServiceContextTestUtil.getServiceContext());
	}

	private User _adminUser;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private ObjectEntryFolder _objectEntryFolder;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryFolderService _objectEntryFolderService;

	private User _user;

}