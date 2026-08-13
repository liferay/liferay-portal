/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.test.util.AccountEntryArgs;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.account.service.test.util.UserRoleTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class AccountEntryServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetOrAddEmptyAccountEntry() throws Exception {
		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			User user = UserTestUtil.addUser();

			UserRoleTestUtil.addResourcePermission(
				AccountActionKeys.ADD_ACCOUNT_ENTRY, PortletKeys.PORTAL,
				user.getUserId());

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				AccountEntry accountEntry =
					_accountEntryService.getOrAddEmptyAccountEntry(
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);

				Assert.assertNotNull(accountEntry);
			}

			// Without permissions

			user = UserTestUtil.addUser();

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				_accountEntryService.getOrAddEmptyAccountEntry(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertNotNull(principalException);
			}
		}
	}

	@Test
	public void testUpdateInvalidAddressId() throws Exception {
		AccountEntry accountEntry1 = AccountEntryTestUtil.addAccountEntry();

		Address address = _addressLocalService.addAddress(
			null, accountEntry1.getUserId(), AccountEntry.class.getName(),
			accountEntry1.getAccountEntryId(), 0,
			_listTypeLocalService.getListTypeId(
				accountEntry1.getCompanyId(), "personal",
				ListTypeConstants.CONTACT_ADDRESS),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			false, RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString(), null, null, null, null, "1234567890",
			ServiceContextTestUtil.getServiceContext());

		Company company = CompanyTestUtil.addCompany();

		User user = UserTestUtil.addCompanyAdminUser(company);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			AccountEntry accountEntry2 = AccountEntryTestUtil.addAccountEntry(
				AccountEntryArgs.withOwner(user));

			accountEntry2.setDefaultBillingAddressId(address.getAddressId());
			accountEntry2.setDefaultShippingAddressId(address.getAddressId());

			_accountEntryService.updateAccountEntry(accountEntry2);

			Assert.fail();
		}
		catch (Exception exception) {
			String message = exception.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + user.getUserId() + " must have VIEW permission"));
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}
	}

	@Inject
	private AccountEntryService _accountEntryService;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

}