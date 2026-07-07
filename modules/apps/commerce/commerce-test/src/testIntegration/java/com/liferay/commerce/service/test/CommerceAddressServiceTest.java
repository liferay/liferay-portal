/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
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
 * @author Tancredi Covioli
 */
@RunWith(Arquillian.class)
public class CommerceAddressServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_accountEntry = CommerceAccountTestUtil.getPersonAccountEntry(
			TestPropsValues.getUserId());

		_country = _countryLocalService.fetchCountryByNumber(
			TestPropsValues.getCompanyId(), "000");

		if (_country == null) {
			_country = _countryLocalService.addCountry(
				null, "ZZ", "ZZZ", true, true, null,
				RandomTestUtil.randomString(), "000",
				RandomTestUtil.randomDouble(), true, false, false,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));
		}

		_commerceAddress = _addCommerceAddress(
			_accountEntry.getAccountEntryId(), _country.getCountryId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, AccountEntry.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			AccountActionKeys.MANAGE_ADDRESSES);

		_user = UserTestUtil.addUser(TestPropsValues.getCompanyId());

		_userLocalService.addRoleUser(role.getRoleId(), _user.getUserId());
	}

	@Test
	public void testAddCommerceAddress() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_addCommerceAddress(
				_accountEntry.getAccountEntryId(), _country.getCountryId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertAccountEntryPermission(principalException);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_addCommerceAddress(
				_accountEntry.getAccountEntryId(), _country.getCountryId());
		}

		AccountEntry accountEntry = _addGuestAccountEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_addCommerceAddress(
				accountEntry.getAccountEntryId(), _country.getCountryId());
		}
	}

	@Test
	public void testDeleteCommerceAddress() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_commerceAddressService.deleteCommerceAddress(
				_commerceAddress.getCommerceAddressId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertAccountEntryPermission(principalException);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commerceAddressService.deleteCommerceAddress(
				_commerceAddress.getCommerceAddressId());
		}

		AccountEntry accountEntry = _addGuestAccountEntry();

		CommerceAddress commerceAddress = _addCommerceAddress(
			accountEntry.getAccountEntryId(), _country.getCountryId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_commerceAddressService.deleteCommerceAddress(
				commerceAddress.getCommerceAddressId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertEquals(
				accountEntry.getAccountEntryId(),
				principalException.resourceId);
		}
	}

	@Test
	public void testFetchCommerceAddress() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_commerceAddressService.fetchCommerceAddress(
				_commerceAddress.getCommerceAddressId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertAccountEntryPermission(principalException);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commerceAddressService.fetchCommerceAddress(
				_commerceAddress.getCommerceAddressId());
		}

		AccountEntry accountEntry = _addGuestAccountEntry();

		CommerceAddress commerceAddress = _addCommerceAddress(
			accountEntry.getAccountEntryId(), _country.getCountryId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_commerceAddressService.fetchCommerceAddress(
				commerceAddress.getCommerceAddressId());
		}
	}

	@Test
	public void testFetchCommerceAddressByExternalReferenceCode()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_commerceAddressService.fetchCommerceAddressByExternalReferenceCode(
				_commerceAddress.getExternalReferenceCode(),
				_commerceAddress.getCompanyId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertAccountEntryPermission(principalException);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commerceAddressService.fetchCommerceAddressByExternalReferenceCode(
				_commerceAddress.getExternalReferenceCode(),
				_commerceAddress.getCompanyId());
		}

		AccountEntry accountEntry = _addGuestAccountEntry();

		CommerceAddress commerceAddress = _addCommerceAddress(
			accountEntry.getAccountEntryId(), _country.getCountryId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_commerceAddressService.fetchCommerceAddressByExternalReferenceCode(
				commerceAddress.getExternalReferenceCode(),
				commerceAddress.getCompanyId());
		}
	}

	@Test
	public void testGetCommerceAddress() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_commerceAddressService.getCommerceAddress(
				_commerceAddress.getCommerceAddressId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertAccountEntryPermission(principalException);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commerceAddressService.getCommerceAddress(
				_commerceAddress.getCommerceAddressId());
		}

		User user = UserTestUtil.addUser(TestPropsValues.getCompanyId());

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(), new long[] {user.getUserId()},
				null,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		CommerceAddress commerceAddress = _addCommerceAddress(
			accountEntry.getAccountEntryId(), _country.getCountryId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_commerceAddressService.getCommerceAddress(
				commerceAddress.getCommerceAddressId());
		}

		AccountEntry guestAccountEntry = _addGuestAccountEntry();

		CommerceAddress guestCommerceAddress = _addCommerceAddress(
			guestAccountEntry.getAccountEntryId(), _country.getCountryId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_commerceAddressService.getCommerceAddress(
				guestCommerceAddress.getCommerceAddressId());
		}
	}

	@Test
	public void testUpdateCommerceAddress() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_updateCommerceAddress(
				RandomTestUtil.randomString(), _commerceAddress);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertAccountEntryPermission(principalException);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_updateCommerceAddress(
				RandomTestUtil.randomString(), _commerceAddress);
		}

		AccountEntry accountEntry = _addGuestAccountEntry();

		CommerceAddress commerceAddress = _addCommerceAddress(
			accountEntry.getAccountEntryId(), _country.getCountryId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addUser(TestPropsValues.getCompanyId()))) {

			_updateCommerceAddress(
				RandomTestUtil.randomString(), commerceAddress);
		}
	}

	private CommerceAddress _addCommerceAddress(
			long accountEntryId, long countryId)
		throws Exception {

		return _commerceAddressService.addCommerceAddress(
			StringPool.BLANK, AccountEntry.class.getName(), accountEntryId,
			countryId, 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK,
			CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	private AccountEntry _addGuestAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null, StringPool.BLANK, null,
			null, AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	private void _assertAccountEntryPermission(
		PrincipalException.MustHavePermission principalException) {

		Assert.assertEquals(
			_accountEntry.getAccountEntryId(), principalException.resourceId);
		Assert.assertEquals(
			AccountEntry.class.getName(), principalException.resourceName);
	}

	private CommerceAddress _updateCommerceAddress(
			String city, CommerceAddress commerceAddress)
		throws Exception {

		return _commerceAddressService.updateCommerceAddress(
			commerceAddress.getExternalReferenceCode(),
			commerceAddress.getCommerceAddressId(), _country.getCountryId(), 0,
			city, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			commerceAddress.getSubtype(),
			CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private CommerceAddress _commerceAddress;

	@Inject
	private CommerceAddressService _commerceAddressService;

	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}