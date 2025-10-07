/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.AddressService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class AddressServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetOrAddEmptyAddress() throws Exception {
		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			RoleTestUtil.addResourcePermission(
				role, User.class.getName(), ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				ActionKeys.UPDATE);

			User user1 = UserTestUtil.addUser();

			UserLocalServiceUtil.addRoleUser(
				role.getRoleId(), user1.getUserId());

			User user2 = UserTestUtil.addUser();

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user1, PermissionCheckerFactoryUtil.create(user1))) {

				Address address = _addressService.getOrAddEmptyAddress(
					RandomTestUtil.randomString(), Contact.class.getName(),
					user2.getContactId());

				Assert.assertNotNull(address);
			}

			// Without permissions

			user1 = UserTestUtil.addUser();

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user1, PermissionCheckerFactoryUtil.create(user1))) {

				_addressService.getOrAddEmptyAddress(
					RandomTestUtil.randomString(), Contact.class.getName(),
					user2.getContactId());

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertNotNull(principalException);
			}
		}
	}

	@Inject
	private static AddressService _addressService;

}