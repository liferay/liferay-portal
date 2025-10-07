/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotActionKeys;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class DepotEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddDepotEntryWithoutPermissions() throws Exception {
		DepotTestUtil.withRegularUser((user, role) -> _addDepotEntry(user));
	}

	@Test
	public void testAddDepotEntryWithPermissions() throws Exception {
		DepotTestUtil.withRegularUser(
			(user, role) -> {
				RoleTestUtil.addResourcePermission(
					role, DepotConstants.RESOURCE_NAME,
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					DepotActionKeys.ADD_DEPOT_ENTRY);

				Assert.assertNotNull(_addDepotEntry(user));
			});
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteDepotEntryWithoutPermissions() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUser());

		DepotTestUtil.withRegularUser(
			(user, role) -> _depotEntryService.deleteDepotEntry(
				depotEntry.getDepotEntryId()));
	}

	@Test
	public void testDeleteDepotEntryWithPermissions() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUser());

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				RoleTestUtil.addResourcePermission(
					role, DepotEntry.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.DELETE);

				Assert.assertNotNull(
					_depotEntryService.deleteDepotEntry(
						depotEntry.getDepotEntryId()));

				_depotEntries.remove(depotEntry);
			});
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testGetDepotEntryWithoutPermissions() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUser());

		DepotTestUtil.withRegularUser(
			(user, role) -> _depotEntryService.getDepotEntry(
				depotEntry.getDepotEntryId()));
	}

	@Test
	public void testGetDepotEntryWithPermissions() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUser());

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				RoleTestUtil.addResourcePermission(
					role, DepotEntry.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.VIEW);

				Assert.assertNotNull(
					_depotEntryService.getDepotEntry(
						depotEntry.getDepotEntryId()));
			});
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testGetGroupDepotEntryWithoutPermissions() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUser());

		DepotTestUtil.withRegularUser(
			(user, role) -> _depotEntryService.getGroupDepotEntry(
				depotEntry.getGroupId()));
	}

	@Test
	public void testGetGroupDepotEntryWithPermissions() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUser());

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				RoleTestUtil.addResourcePermission(
					role, DepotEntry.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.VIEW);

				Assert.assertNotNull(
					_depotEntryService.getGroupDepotEntry(
						depotEntry.getGroupId()));
			});
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdateDepotEntryWithoutPermissions() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUser());

		DepotTestUtil.withRegularUser(
			(user, role) -> _updateDepotEntry(depotEntry, user));
	}

	@Test
	public void testUpdateDepotEntryWithPermissions() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUser());

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				RoleTestUtil.addResourcePermission(
					role, DepotEntry.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.UPDATE);

				Assert.assertNotNull(_updateDepotEntry(depotEntry, user));
			});
	}

	private DepotEntry _addDepotEntry(User user) throws Exception {
		DepotEntry depotEntry = _depotEntryService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), user.getUserId()));

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private DepotEntry _updateDepotEntry(DepotEntry depotEntry, User user)
		throws PortalException {

		return _depotEntryService.updateDepotEntry(
			depotEntry.getDepotEntryId(),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.emptyMap(), new UnicodeProperties(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), user.getUserId()));
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryService _depotEntryService;

}