/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class FragmentCollectionServicePermissionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR,
			"com.liferay.fragment", ResourceConstants.SCOPE_GROUP,
			String.valueOf(_group.getGroupId()),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		_user = UserTestUtil.addGroupUser(_group, RoleConstants.POWER_USER);
	}

	@Test
	public void testAddFragmentCollection() throws Exception {
		UserTestUtil.setUser(_user);

		try {
			_fragmentCollectionService.addFragmentCollection(
				null, _group.getGroupId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), StringPool.BLANK, false,
				ServiceContextTestUtil.getServiceContext(
					_group, _user.getUserId()));

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
		}

		try {
			_fragmentCollectionService.addFragmentCollection(
				null, _group.getGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(
					_group, _user.getUserId()));

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
		}

		_userLocalService.addRoleUser(_role.getRoleId(), _user.getUserId());

		_fragmentCollectionService.addFragmentCollection(
			null, _group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		_fragmentCollectionService.addFragmentCollection(
			null, _group.getGroupId(), RandomTestUtil.randomString(),
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));
	}

	@Test
	public void testDeleteFragmentCollection() throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		UserTestUtil.setUser(_user);

		try {
			_fragmentCollectionService.deleteFragmentCollection(
				fragmentCollection.getFragmentCollectionId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
		}

		_userLocalService.addRoleUser(_role.getRoleId(), _user.getUserId());

		_fragmentCollectionService.deleteFragmentCollection(
			fragmentCollection.getFragmentCollectionId());
	}

	@Test
	public void testDeleteFragmentCollections() throws Exception {
		FragmentCollection fragmentCollection1 =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());
		FragmentCollection fragmentCollection2 =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		long[] fragmentCollectionIds = {
			fragmentCollection1.getFragmentCollectionId(),
			fragmentCollection2.getFragmentCollectionId()
		};

		UserTestUtil.setUser(_user);

		try {
			_fragmentCollectionService.deleteFragmentCollections(
				fragmentCollectionIds);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
		}

		_userLocalService.addRoleUser(_role.getRoleId(), _user.getUserId());

		_fragmentCollectionService.deleteFragmentCollections(
			fragmentCollectionIds);
	}

	@Test
	public void testFetchFragmentCollection() throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		UserTestUtil.setUser(_user);

		_userLocalService.addRoleUser(_role.getRoleId(), _user.getUserId());

		_fragmentCollectionService.fetchFragmentCollection(
			fragmentCollection.getFragmentCollectionId());
	}

	@Test
	@TestInfo("LPD-88395")
	public void testGetFragmentCollectionByExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		UserTestUtil.setUser(_user);

		try {
			_fragmentCollectionService.
				getFragmentCollectionByExternalReferenceCode(
					fragmentCollection.getExternalReferenceCode(),
					_group.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
		}

		_userLocalService.addRoleUser(_role.getRoleId(), _user.getUserId());

		_fragmentCollectionService.getFragmentCollectionByExternalReferenceCode(
			fragmentCollection.getExternalReferenceCode(), _group.getGroupId());
	}

	@Test
	public void testGetTempFileNames() throws Exception {
		UserTestUtil.setUser(_user);

		try {
			_fragmentCollectionService.getTempFileNames(
				_group.getGroupId(), StringPool.BLANK);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
		}

		_userLocalService.addRoleUser(_role.getRoleId(), _user.getUserId());

		_fragmentCollectionService.getTempFileNames(
			_group.getGroupId(), StringPool.BLANK);
	}

	@Test
	public void testUpdateFragmentCollection() throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		UserTestUtil.setUser(_user);

		try {
			_fragmentCollectionService.updateFragmentCollection(
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
		}

		_userLocalService.addRoleUser(_role.getRoleId(), _user.getUserId());

		_fragmentCollectionService.updateFragmentCollection(
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Inject
	private FragmentCollectionService _fragmentCollectionService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}