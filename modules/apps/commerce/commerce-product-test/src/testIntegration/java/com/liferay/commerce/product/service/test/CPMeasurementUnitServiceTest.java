/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.service.CPMeasurementUnitService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CPMeasurementUnitServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		_cpMeasurementUnit = _cpMeasurementUnitService.addCPMeasurementUnit(
			null, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE),
			RandomTestUtil.nextDouble(), true, RandomTestUtil.nextDouble(),
			RandomTestUtil.nextInt(), _serviceContext);
		_role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null, _serviceContext);

		_user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(_user.getUserId(), _role);
	}

	@Test
	public void testAddCPMeasurementUnit() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.addCPMeasurementUnit(
				null, RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE),
				RandomTestUtil.nextDouble(), RandomTestUtil.randomBoolean(),
				RandomTestUtil.nextDouble(), RandomTestUtil.nextInt(),
				_serviceContext);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CPActionKeys.ADD_COMMERCE_PRODUCT_MEASUREMENT_UNIT,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CPConstants.RESOURCE_NAME_PRODUCT,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CPActionKeys.ADD_COMMERCE_PRODUCT_MEASUREMENT_UNIT);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.addCPMeasurementUnit(
				null, RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE),
				RandomTestUtil.nextDouble(), RandomTestUtil.randomBoolean(),
				RandomTestUtil.nextDouble(), RandomTestUtil.nextInt(),
				_serviceContext);
		}
	}

	@Test
	public void testDeleteCPMeasurementUnit() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.deleteCPMeasurementUnit(
				_cpMeasurementUnit.getCPMeasurementUnitId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.DELETE, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_cpMeasurementUnit.getCompanyId(),
			CPMeasurementUnit.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_cpMeasurementUnit.getCPMeasurementUnitId()),
			_role.getRoleId(), new String[] {ActionKeys.DELETE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.deleteCPMeasurementUnit(
				_cpMeasurementUnit.getCPMeasurementUnitId());
		}
	}

	@Test
	public void testFetchCPMeasurementUnit1() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.fetchCPMeasurementUnit(
				_cpMeasurementUnit.getCPMeasurementUnitId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_cpMeasurementUnit.getCompanyId(),
			CPMeasurementUnit.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_cpMeasurementUnit.getCPMeasurementUnitId()),
			_role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.fetchCPMeasurementUnit(
				_cpMeasurementUnit.getCPMeasurementUnitId());
		}
	}

	@Test
	public void testFetchCPMeasurementUnit2() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			try {
				_cpMeasurementUnitService.fetchCPMeasurementUnit(
					_cpMeasurementUnit.getCompanyId(),
					_cpMeasurementUnit.getKey());

				Assert.fail();
			}
			catch (Exception exception) {
				_assertMessage(
					ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
			}
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_cpMeasurementUnit.getCompanyId(),
			CPMeasurementUnit.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_cpMeasurementUnit.getCPMeasurementUnitId()),
			_role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.fetchCPMeasurementUnit(
				_cpMeasurementUnit.getCompanyId(), _cpMeasurementUnit.getKey());
		}
	}

	@Test
	public void testFetchCPMeasurementUnitByExternalReferenceCode()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.
				fetchCPMeasurementUnitByExternalReferenceCode(
					_cpMeasurementUnit.getExternalReferenceCode(),
					_cpMeasurementUnit.getCompanyId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_cpMeasurementUnit.getCompanyId(),
			CPMeasurementUnit.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_cpMeasurementUnit.getCPMeasurementUnitId()),
			_role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.
				fetchCPMeasurementUnitByExternalReferenceCode(
					_cpMeasurementUnit.getExternalReferenceCode(),
					_cpMeasurementUnit.getCompanyId());
		}
	}

	@Test
	public void testFetchPrimaryCPMeasurementUnit1() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.fetchPrimaryCPMeasurementUnit(
				_cpMeasurementUnit.getCompanyId(),
				_cpMeasurementUnit.getType());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_cpMeasurementUnit.getCompanyId(),
			CPMeasurementUnit.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_cpMeasurementUnit.getCPMeasurementUnitId()),
			_role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.fetchPrimaryCPMeasurementUnit(
				_cpMeasurementUnit.getCompanyId(),
				_cpMeasurementUnit.getType());
		}
	}

	@Test
	public void testFetchPrimaryCPMeasurementUnit2() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.fetchPrimaryCPMeasurementUnit(
				_cpMeasurementUnit.getCompanyId(),
				_cpMeasurementUnit.getType());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_cpMeasurementUnit.getCompanyId(),
			CPMeasurementUnit.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_cpMeasurementUnit.getCPMeasurementUnitId()),
			_role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.fetchPrimaryCPMeasurementUnit(
				_cpMeasurementUnit.getCompanyId(),
				_cpMeasurementUnit.getType());
		}
	}

	@Test
	public void testGetCPMeasurementUnit() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.getCPMeasurementUnit(
				_cpMeasurementUnit.getCPMeasurementUnitId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_cpMeasurementUnit.getCompanyId(),
			CPMeasurementUnit.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_cpMeasurementUnit.getCPMeasurementUnitId()),
			_role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.getCPMeasurementUnit(
				_cpMeasurementUnit.getCPMeasurementUnitId());
		}
	}

	@Test
	public void testGetCPMeasurementUnits1() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.getCPMeasurementUnits(
				_cpMeasurementUnit.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CPActionKeys.VIEW_COMMERCE_PRODUCT_MEASUREMENT_UNITS,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CPConstants.RESOURCE_NAME_PRODUCT,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CPActionKeys.VIEW_COMMERCE_PRODUCT_MEASUREMENT_UNITS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.getCPMeasurementUnits(
				_cpMeasurementUnit.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);
		}
	}

	@Test
	public void testGetCPMeasurementUnits2() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.getCPMeasurementUnits(
				_cpMeasurementUnit.getCompanyId(), _cpMeasurementUnit.getType(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CPActionKeys.VIEW_COMMERCE_PRODUCT_MEASUREMENT_UNITS,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CPConstants.RESOURCE_NAME_PRODUCT,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CPActionKeys.VIEW_COMMERCE_PRODUCT_MEASUREMENT_UNITS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.getCPMeasurementUnits(
				_cpMeasurementUnit.getCompanyId(), _cpMeasurementUnit.getType(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}
	}

	@Test
	public void testGetCPMeasurementUnitsCount1() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.getCPMeasurementUnitsCount(
				_cpMeasurementUnit.getCompanyId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CPActionKeys.VIEW_COMMERCE_PRODUCT_MEASUREMENT_UNITS,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CPConstants.RESOURCE_NAME_PRODUCT,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CPActionKeys.VIEW_COMMERCE_PRODUCT_MEASUREMENT_UNITS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.getCPMeasurementUnitsCount(
				_cpMeasurementUnit.getCompanyId());
		}
	}

	@Test
	public void testGetCPMeasurementUnitsCount2() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.getCPMeasurementUnitsCount(
				_cpMeasurementUnit.getCompanyId(),
				_cpMeasurementUnit.getType());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CPActionKeys.VIEW_COMMERCE_PRODUCT_MEASUREMENT_UNITS,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CPConstants.RESOURCE_NAME_PRODUCT,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CPActionKeys.VIEW_COMMERCE_PRODUCT_MEASUREMENT_UNITS);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.getCPMeasurementUnitsCount(
				_cpMeasurementUnit.getCompanyId(),
				_cpMeasurementUnit.getType());
		}
	}

	@Test
	public void testSetPrimary() throws Exception {
		_cpMeasurementUnitService.setPrimary(
			_cpMeasurementUnit.getCPMeasurementUnitId(), false);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.setPrimary(
				_cpMeasurementUnit.getCPMeasurementUnitId(), true);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.UPDATE, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_cpMeasurementUnit.getCompanyId(),
			CPMeasurementUnit.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_cpMeasurementUnit.getCPMeasurementUnitId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.setPrimary(
				_cpMeasurementUnit.getCPMeasurementUnitId(), true);
		}
	}

	@Test
	public void testUpdateCPMeasurementUnit() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			try {
				_cpMeasurementUnitService.updateCPMeasurementUnit(
					_cpMeasurementUnit.getExternalReferenceCode(),
					_cpMeasurementUnit.getCPMeasurementUnitId(),
					_cpMeasurementUnit.getNameMap(),
					_cpMeasurementUnit.getKey(), _cpMeasurementUnit.getRate(),
					_cpMeasurementUnit.isPrimary(),
					_cpMeasurementUnit.getPriority(),
					_cpMeasurementUnit.getType(), _serviceContext);

				Assert.fail();
			}
			catch (Exception exception) {
				_assertMessage(
					ActionKeys.UPDATE, exception.getMessage(),
					_user.getUserId());
			}
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_cpMeasurementUnit.getCompanyId(),
			CPMeasurementUnit.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_cpMeasurementUnit.getCPMeasurementUnitId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_cpMeasurementUnitService.updateCPMeasurementUnit(
				RandomTestUtil.randomString(),
				_cpMeasurementUnit.getCPMeasurementUnitId(),
				_cpMeasurementUnit.getNameMap(),
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE),
				_cpMeasurementUnit.getRate(), _cpMeasurementUnit.isPrimary(),
				RandomTestUtil.nextDouble(), RandomTestUtil.nextInt(),
				_serviceContext);
		}
	}

	private void _assertMessage(String actionId, String message, long userId) {
		Assert.assertTrue(
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", actionId,
					" permission for")));
	}

	private CPMeasurementUnit _cpMeasurementUnit;

	@Inject
	private CPMeasurementUnitService _cpMeasurementUnitService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}