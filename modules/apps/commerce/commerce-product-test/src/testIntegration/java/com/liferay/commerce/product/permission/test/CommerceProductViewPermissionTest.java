/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.permission.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gianmarco Brunialti Masera
 */
@FeatureFlag("LPD-109588")
@RunWith(Arquillian.class)
public class CommerceProductViewPermissionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), commerceCurrency.getCode());

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(),
			TestPropsValues.getUserId(), commerceCurrency.getCode());

		_cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), _cpDefinition.getCPDefinitionId(),
			_commerceChannel.getCommerceChannelId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_cpDefinitionLocalService.updateCPDefinitionChannelFilter(
			_cpDefinition.getCPDefinitionId(), true);

		_guestPermissionChecker = PermissionCheckerFactoryUtil.create(
			_userLocalService.getGuestUser(_group.getCompanyId()));
	}

	@Test
	public void testContains() throws Exception {
		Assert.assertTrue(
			_commerceProductViewPermission.contains(
				_guestPermissionChecker,
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
				_cpDefinition.getCPDefinitionId()));
		Assert.assertTrue(
			_commerceProductViewPermission.contains(
				_guestPermissionChecker,
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
				_commerceChannel.getGroupId(),
				_cpDefinition.getCPDefinitionId()));

		CommerceTestUtil.runWithGuestBrowsingDisabled(
			_commerceChannel.getGroupId(),
			() -> {
				Assert.assertFalse(
					_commerceProductViewPermission.contains(
						_guestPermissionChecker,
						AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
						_cpDefinition.getCPDefinitionId()));
				Assert.assertFalse(
					_commerceProductViewPermission.contains(
						_guestPermissionChecker,
						AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
						_commerceChannel.getGroupId(),
						_cpDefinition.getCPDefinitionId()));
				Assert.assertFalse(
					_commerceProductViewPermission.contains(
						_guestPermissionChecker,
						AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
						_group.getGroupId(),
						_cpDefinition.getCPDefinitionId()));

				PermissionChecker permissionChecker =
					PermissionCheckerFactoryUtil.create(UserTestUtil.addUser());

				Assert.assertTrue(
					_commerceProductViewPermission.contains(
						permissionChecker,
						AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
						_cpDefinition.getCPDefinitionId()));
				Assert.assertTrue(
					_commerceProductViewPermission.contains(
						permissionChecker,
						AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
						_commerceChannel.getGroupId(),
						_cpDefinition.getCPDefinitionId()));
			});
	}

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Inject
	private CommerceProductViewPermission _commerceProductViewPermission;

	private CPDefinition _cpDefinition;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	private Group _group;
	private PermissionChecker _guestPermissionChecker;

	@Inject
	private UserLocalService _userLocalService;

}