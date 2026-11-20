/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CountryTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tancredi Covioli
 */
@RunWith(Arquillian.class)
public class CountryServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testAddCountry() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_country = _countryService.addCountry(
				null, CountryTestUtil.randomA2(TestPropsValues.getCompanyId()),
				CountryTestUtil.randomA3(TestPropsValues.getCompanyId()), true,
				RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
				RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessagePortal(
				ActionKeys.ADD_COUNTRY, principalException.getMessage(),
				_user.getUserId());
		}

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), PortletKeys.PORTAL,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			ActionKeys.ADD_COUNTRY);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_country = _countryService.addCountry(
				null, CountryTestUtil.randomA2(TestPropsValues.getCompanyId()),
				CountryTestUtil.randomA3(TestPropsValues.getCompanyId()), true,
				RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
				RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
				ServiceContextTestUtil.getServiceContext());
		}
	}

	@Test
	public void testDeleteCountry() throws Exception {
		_country = _countryLocalService.addCountry(
			null, CountryTestUtil.randomA2(TestPropsValues.getCompanyId()),
			CountryTestUtil.randomA3(TestPropsValues.getCompanyId()), true,
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			ServiceContextTestUtil.getServiceContext());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_countryService.deleteCountry(_country.getCountryId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.DELETE, principalException.getMessage(),
				_user.getUserId());
		}

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), Country.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			ActionKeys.DELETE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_countryService.deleteCountry(_country.getCountryId());
		}
	}

	@Test
	public void testUpdateActive() throws Exception {
		_country = _countryLocalService.addCountry(
			null, CountryTestUtil.randomA2(TestPropsValues.getCompanyId()),
			CountryTestUtil.randomA3(TestPropsValues.getCompanyId()), true,
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			ServiceContextTestUtil.getServiceContext());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_country = _countryService.updateActive(
				_country.getCountryId(), false);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), Country.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_country = _countryService.updateActive(
				_country.getCountryId(), false);
		}
	}

	@Test
	public void testUpdateCountry() throws Exception {
		_country = _countryLocalService.addCountry(
			null, CountryTestUtil.randomA2(TestPropsValues.getCompanyId()),
			CountryTestUtil.randomA3(TestPropsValues.getCompanyId()), true,
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			ServiceContextTestUtil.getServiceContext());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_country = _countryService.updateCountry(
				_country.getExternalReferenceCode(), _country.getCountryId(),
				_country.getA2(), _country.getA3(), _country.isActive(),
				_country.isBillingAllowed(), _country.getIdd(),
				RandomTestUtil.randomString(), _country.getNumber(),
				_country.getPosition(), _country.isShippingAllowed(),
				_country.isSubjectToVAT());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), Country.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_country = _countryService.updateCountry(
				_country.getExternalReferenceCode(), _country.getCountryId(),
				_country.getA2(), _country.getA3(), _country.isActive(),
				_country.isBillingAllowed(), _country.getIdd(),
				RandomTestUtil.randomString(), _country.getNumber(),
				_country.getPosition(), _country.isShippingAllowed(),
				_country.isSubjectToVAT());
		}
	}

	@Test
	public void testUpdateGroupFilterEnabled() throws Exception {
		_country = _countryLocalService.addCountry(
			null, CountryTestUtil.randomA2(TestPropsValues.getCompanyId()),
			CountryTestUtil.randomA3(TestPropsValues.getCompanyId()), true,
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			ServiceContextTestUtil.getServiceContext());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_country = _countryService.updateGroupFilterEnabled(
				_country.getCountryId(), true);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), Country.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_country = _countryService.updateGroupFilterEnabled(
				_country.getCountryId(), true);
		}
	}

	private void _assertMessage(String actionId, String message, long userId) {
		Assert.assertTrue(
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", actionId,
					" permission for")));
	}

	private void _assertMessagePortal(
		String actionKey, String message, long userId) {

		Assert.assertTrue(
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", PortletKeys.PORTAL, ", ",
					actionKey, " permission for null")));
	}

	@DeleteAfterTestRun
	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private CountryService _countryService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}