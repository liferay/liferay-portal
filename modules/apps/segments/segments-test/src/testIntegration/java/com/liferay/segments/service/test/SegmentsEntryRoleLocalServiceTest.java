/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.exception.RoleAssignmentException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.exception.NoSuchEntryException;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRole;
import com.liferay.segments.service.SegmentsEntryRoleLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class SegmentsEntryRoleLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_serviceContext = ServiceContextTestUtil.getServiceContext();
	}

	@Test
	public void testAddSegmentsEntryRole() throws Exception {
		_testAddSegmentsEntryRole();
		_testAddSegmentsEntryRoleWithInvalidRoleId();
		_testAddSegmentsEntryRoleWithInvalidSegmentsEntryId();
	}

	@Test
	public void testDeleteSegmentsEntryRole() throws Exception {
		_testDeleteSegmentsEntryRole();
		_testDeleteSegmentsEntryRolesByRoleId();
		_testDeleteSegmentsEntryRolesBySegmentsEntryId();
	}

	@Test
	public void testGetSegmentsEntryRoles() throws Exception {
		_testGetSegmentsEntryRoles();
	}

	@Test
	public void testSetSegmentsEntrySiteRoles() throws Exception {
		_testSetSegmentsEntrySiteRoles();
		_testSetSegmentsEntrySiteRolesWithExcludedRole();
		_testSetSegmentsEntrySiteRolesWithRegularRole();
	}

	private Role _addRole(int type) throws Exception {
		Role role = RoleTestUtil.addRole(type);

		_roles.add(role);

		return role;
	}

	private SegmentsEntry _addSegmentsEntry() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			TestPropsValues.getGroupId());

		_segmentsEntries.add(segmentsEntry);

		return segmentsEntry;
	}

	private void _testAddSegmentsEntryRole() throws Exception {
		Role role = _addRole(RoleConstants.TYPE_REGULAR);
		SegmentsEntry segmentsEntry = _addSegmentsEntry();

		SegmentsEntryRole segmentsEntryRole =
			_segmentsEntryRoleLocalService.addSegmentsEntryRole(
				segmentsEntry.getSegmentsEntryId(), role.getRoleId(),
				_serviceContext);

		Assert.assertNotNull(segmentsEntryRole);

		List<SegmentsEntryRole> segmentsEntryRoles =
			_segmentsEntryRoleLocalService.getSegmentsEntryRoles(
				segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRoles.toString(), 1, segmentsEntryRoles.size());

		Assert.assertEquals(role.getRoleId(), segmentsEntryRole.getRoleId());
		Assert.assertEquals(
			segmentsEntry.getSegmentsEntryId(),
			segmentsEntryRole.getSegmentsEntryId());
	}

	private void _testAddSegmentsEntryRoleWithInvalidRoleId() throws Exception {
		SegmentsEntry segmentsEntry = _addSegmentsEntry();

		Assert.assertThrows(
			NoSuchRoleException.class,
			() -> _segmentsEntryRoleLocalService.addSegmentsEntryRole(
				segmentsEntry.getSegmentsEntryId(), 0L, _serviceContext));

		List<SegmentsEntryRole> segmentsEntryRoles =
			_segmentsEntryRoleLocalService.getSegmentsEntryRoles(
				segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRoles.toString(), 0, segmentsEntryRoles.size());
	}

	private void _testAddSegmentsEntryRoleWithInvalidSegmentsEntryId()
		throws Exception {

		Role role = _addRole(RoleConstants.TYPE_REGULAR);

		Assert.assertThrows(
			NoSuchEntryException.class,
			() -> _segmentsEntryRoleLocalService.addSegmentsEntryRole(
				0L, role.getRoleId(), _serviceContext));
	}

	private void _testDeleteSegmentsEntryRole() throws Exception {
		Role role = _addRole(RoleConstants.TYPE_REGULAR);
		SegmentsEntry segmentsEntry = _addSegmentsEntry();

		_segmentsEntryRoleLocalService.addSegmentsEntryRole(
			segmentsEntry.getSegmentsEntryId(), role.getRoleId(),
			_serviceContext);

		_segmentsEntryRoleLocalService.deleteSegmentsEntryRole(
			segmentsEntry.getSegmentsEntryId(), role.getRoleId());

		List<SegmentsEntryRole> segmentsEntryRoles =
			_segmentsEntryRoleLocalService.getSegmentsEntryRoles(
				segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRoles.toString(), 0, segmentsEntryRoles.size());
	}

	private void _testDeleteSegmentsEntryRolesByRoleId() throws Exception {
		Role role = _addRole(RoleConstants.TYPE_REGULAR);
		SegmentsEntry segmentsEntry = _addSegmentsEntry();

		_segmentsEntryRoleLocalService.addSegmentsEntryRole(
			segmentsEntry.getSegmentsEntryId(), role.getRoleId(),
			_serviceContext);

		_segmentsEntryRoleLocalService.deleteSegmentsEntryRolesByRoleId(
			role.getRoleId());

		Assert.assertEquals(
			0,
			_segmentsEntryRoleLocalService.getSegmentsEntryRolesCountByRoleId(
				role.getRoleId()));
	}

	private void _testDeleteSegmentsEntryRolesBySegmentsEntryId()
		throws Exception {

		Role role = _addRole(RoleConstants.TYPE_REGULAR);
		SegmentsEntry segmentsEntry = _addSegmentsEntry();

		_segmentsEntryRoleLocalService.addSegmentsEntryRole(
			segmentsEntry.getSegmentsEntryId(), role.getRoleId(),
			_serviceContext);

		_segmentsEntryRoleLocalService.deleteSegmentsEntryRoles(
			segmentsEntry.getSegmentsEntryId());

		List<SegmentsEntryRole> segmentsEntryRoles =
			_segmentsEntryRoleLocalService.getSegmentsEntryRoles(
				segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRoles.toString(), 0, segmentsEntryRoles.size());
	}

	private void _testGetSegmentsEntryRoles() throws Exception {
		Role role = _addRole(RoleConstants.TYPE_REGULAR);
		SegmentsEntry segmentsEntry = _addSegmentsEntry();

		SegmentsEntryRole segmentsEntryRole =
			_segmentsEntryRoleLocalService.addSegmentsEntryRole(
				segmentsEntry.getSegmentsEntryId(), role.getRoleId(),
				_serviceContext);

		List<SegmentsEntryRole> segmentsEntryRoles =
			_segmentsEntryRoleLocalService.getSegmentsEntryRoles(
				segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRoles.toString(), 1, segmentsEntryRoles.size());
		Assert.assertEquals(segmentsEntryRole, segmentsEntryRoles.get(0));
	}

	private void _testSetSegmentsEntrySiteRoles() throws Exception {
		SegmentsEntry segmentsEntry = _addSegmentsEntry();

		List<Long> actualRoleIdsList = ListUtil.fromArray(
			segmentsEntry.getRoleIds());

		Assert.assertEquals(
			actualRoleIdsList.toString(), 0, actualRoleIdsList.size());

		List<Role> siteRoles = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			siteRoles.add(_addRole(RoleConstants.TYPE_SITE));
		}

		long[] expectedRoleIds = ListUtil.toLongArray(
			siteRoles, Role.ROLE_ID_ACCESSOR);

		_segmentsEntryRoleLocalService.setSegmentsEntrySiteRoles(
			segmentsEntry.getSegmentsEntryId(), expectedRoleIds,
			_serviceContext);

		List<Long> expectedRoleIdsList = ListUtil.fromArray(expectedRoleIds);

		actualRoleIdsList = ListUtil.fromArray(segmentsEntry.getRoleIds());

		Assert.assertEquals(
			actualRoleIdsList.toString(), expectedRoleIdsList.size(),
			actualRoleIdsList.size());

		Assert.assertTrue(expectedRoleIdsList.containsAll(actualRoleIdsList));

		_segmentsEntryRoleLocalService.setSegmentsEntrySiteRoles(
			segmentsEntry.getSegmentsEntryId(), new long[0], _serviceContext);

		actualRoleIdsList = ListUtil.fromArray(segmentsEntry.getRoleIds());

		Assert.assertEquals(
			actualRoleIdsList.toString(), 0, actualRoleIdsList.size());
	}

	private void _testSetSegmentsEntrySiteRolesWithExcludedRole()
		throws Exception {

		SegmentsEntry segmentsEntry = _addSegmentsEntry();

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

		Assert.assertThrows(
			RoleAssignmentException.class,
			() -> _segmentsEntryRoleLocalService.setSegmentsEntrySiteRoles(
				segmentsEntry.getSegmentsEntryId(),
				new long[] {role.getRoleId()}, _serviceContext));

		List<SegmentsEntryRole> segmentsEntryRoles =
			_segmentsEntryRoleLocalService.getSegmentsEntryRoles(
				segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRoles.toString(), 0, segmentsEntryRoles.size());
	}

	private void _testSetSegmentsEntrySiteRolesWithRegularRole()
		throws Exception {

		Role regularRole = _addRole(RoleConstants.TYPE_REGULAR);
		SegmentsEntry segmentsEntry = _addSegmentsEntry();
		Role siteRole = _addRole(RoleConstants.TYPE_SITE);

		Assert.assertThrows(
			RoleAssignmentException.class,
			() -> _segmentsEntryRoleLocalService.setSegmentsEntrySiteRoles(
				segmentsEntry.getSegmentsEntryId(),
				new long[] {regularRole.getRoleId(), siteRole.getRoleId()},
				_serviceContext));

		List<SegmentsEntryRole> segmentsEntryRoles =
			_segmentsEntryRoleLocalService.getSegmentsEntryRoles(
				segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRoles.toString(), 0, segmentsEntryRoles.size());
	}

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private final List<Role> _roles = new ArrayList<>();

	@DeleteAfterTestRun
	private final List<SegmentsEntry> _segmentsEntries = new ArrayList<>();

	@Inject
	private SegmentsEntryRoleLocalService _segmentsEntryRoleLocalService;

	private ServiceContext _serviceContext;

}