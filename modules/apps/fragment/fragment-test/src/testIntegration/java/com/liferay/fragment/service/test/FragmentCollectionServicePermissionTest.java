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
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.After;
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

		_user = UserTestUtil.addGroupUser(_group, RoleConstants.POWER_USER);
	}

	@After
	public void tearDown() throws Exception {
		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		ResourcePermissionLocalServiceUtil.removeResourcePermissions(
			TestPropsValues.getCompanyId(), "com.liferay.fragment",
			ResourceConstants.SCOPE_GROUP, role.getRoleId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddFragmentCollectionWithFragmentCollectionKeyWithoutPermissions()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.addFragmentCollection(
			null, _group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			serviceContext);
	}

	@Test
	public void testAddFragmentCollectionWithFragmentCollectionKeyWithPermissions()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.addFragmentCollection(
			null, _group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddFragmentCollectionWithoutPermissions() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.addFragmentCollection(
			null, _group.getGroupId(), RandomTestUtil.randomString(),
			StringPool.BLANK, serviceContext);
	}

	@Test
	public void testAddFragmentCollectionWithPermissions() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.addFragmentCollection(
			null, _group.getGroupId(), RandomTestUtil.randomString(),
			StringPool.BLANK, serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteFragmentCollectionsWithoutPermissions()
		throws Exception {

		FragmentCollection fragmentCollection1 =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());
		FragmentCollection fragmentCollection2 =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		long[] fragmentCollectionIds = {
			fragmentCollection1.getFragmentCollectionId(),
			fragmentCollection2.getFragmentCollectionId()
		};

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.deleteFragmentCollections(
			fragmentCollectionIds);
	}

	@Test
	public void testDeleteFragmentCollectionsWithPermissions()
		throws Exception {

		FragmentCollection fragmentCollection1 =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());
		FragmentCollection fragmentCollection2 =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		long[] fragmentCollectionIds = {
			fragmentCollection1.getFragmentCollectionId(),
			fragmentCollection2.getFragmentCollectionId()
		};

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.deleteFragmentCollections(
			fragmentCollectionIds);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteFragmentCollectionWithoutPermissions()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.deleteFragmentCollection(
			fragmentCollection.getFragmentCollectionId());
	}

	@Test
	public void testDeleteFragmentCollectionWithPermissions() throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.deleteFragmentCollection(
			fragmentCollection.getFragmentCollectionId());
	}

	@Test
	public void testFetchFragmentCollectionWithPermissions() throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.fetchFragmentCollection(
			fragmentCollection.getFragmentCollectionId());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	@TestInfo("LPD-88395")
	public void testGetFragmentCollectionByExternalReferenceCodeWithoutPermissions()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.getFragmentCollectionByExternalReferenceCode(
			fragmentCollection.getExternalReferenceCode(), _group.getGroupId());
	}

	@Test
	@TestInfo("LPD-88395")
	public void testGetFragmentCollectionByExternalReferenceCodeWithPermissions()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.getFragmentCollectionByExternalReferenceCode(
			fragmentCollection.getExternalReferenceCode(), _group.getGroupId());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testGetTempFileNamesWithoutPermissions() throws Exception {
		UserTestUtil.setUser(_user);

		_fragmentCollectionService.getTempFileNames(
			_group.getGroupId(), StringPool.BLANK);
	}

	@Test
	public void testGetTempFileNamesWithPermissions() throws Exception {
		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.getTempFileNames(
			_group.getGroupId(), StringPool.BLANK);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdateFragmentCollectionWithoutPermissions()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.updateFragmentCollection(
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Test
	public void testUpdateFragmentCollectionWithPermissions() throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentCollectionService.updateFragmentCollection(
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private void _setRolePermissions(String permissionType) throws Exception {
		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			TestPropsValues.getCompanyId(), "com.liferay.fragment",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			role.getRoleId(), permissionType);
	}

	@Inject
	private FragmentCollectionService _fragmentCollectionService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}