/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CommerceChannelServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			commerceCurrency.getCode());

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testAddOrUpdateCommerceChannel() throws Exception {
		_addResourcePermission(
			CPConstants.RESOURCE_NAME_CHANNEL,
			CPActionKeys.ADD_COMMERCE_CHANNEL);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commerceChannelService.addOrUpdateCommerceChannel(
				_commerceChannel.getExternalReferenceCode(),
				_commerceChannel.getAccountEntryId(),
				_commerceChannel.getSiteGroupId(),
				RandomTestUtil.randomString(), _commerceChannel.getType(), null,
				_commerceChannel.getCommerceCurrencyCode(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					StringBundler.concat(
						"User ", _user.getUserId(), " must have ",
						ActionKeys.UPDATE, " permission for")));
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				_commerceChannel.getCommerceChannelId());

		Assert.assertEquals(
			_commerceChannel.getName(), commerceChannel.getName());

		_addResourcePermission(
			CommerceChannel.class.getName(), ActionKeys.UPDATE);

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			null, TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			String name = RandomTestUtil.randomString();

			commerceChannel =
				_commerceChannelService.addOrUpdateCommerceChannel(
					_commerceChannel.getExternalReferenceCode(),
					accountEntry.getAccountEntryId(),
					_commerceChannel.getSiteGroupId(), name,
					_commerceChannel.getType(), null,
					_commerceChannel.getCommerceCurrencyCode(),
					ServiceContextTestUtil.getServiceContext());

			Assert.assertEquals(
				_commerceChannel.getAccountEntryId(),
				commerceChannel.getAccountEntryId());
			Assert.assertEquals(name, commerceChannel.getName());
		}
	}

	private void _addResourcePermission(String name, String actionId)
		throws Exception {

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), name,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			actionId);
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceChannelService _commerceChannelService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}