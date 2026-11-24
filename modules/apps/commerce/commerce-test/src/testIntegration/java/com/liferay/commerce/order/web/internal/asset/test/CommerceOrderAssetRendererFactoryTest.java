/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.asset.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.test.TestInfo;
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
public class CommerceOrderAssetRendererFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-117098")
	public void testHasPermission() throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			TestPropsValues.getGroupId(), commerceCurrency.getCode());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				TestPropsValues.getUserId(), commerceChannel.getGroupId(),
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST,
				commerceCurrency.getCode(), 0);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		Assert.assertTrue(
			_assetRendererFactory.hasPermission(
				permissionChecker, commerceOrder.getCommerceOrderId(),
				ActionKeys.DELETE));
		Assert.assertTrue(
			_assetRendererFactory.hasPermission(
				permissionChecker, commerceOrder.getCommerceOrderId(),
				ActionKeys.UPDATE));
		Assert.assertTrue(
			_assetRendererFactory.hasPermission(
				permissionChecker, commerceOrder.getCommerceOrderId(),
				ActionKeys.VIEW));

		permissionChecker = PermissionCheckerFactoryUtil.create(
			UserTestUtil.addUser());

		Assert.assertFalse(
			_assetRendererFactory.hasPermission(
				permissionChecker, commerceOrder.getCommerceOrderId(),
				ActionKeys.DELETE));
		Assert.assertFalse(
			_assetRendererFactory.hasPermission(
				permissionChecker, commerceOrder.getCommerceOrderId(),
				ActionKeys.UPDATE));
		Assert.assertFalse(
			_assetRendererFactory.hasPermission(
				permissionChecker, commerceOrder.getCommerceOrderId(),
				ActionKeys.VIEW));
	}

	@Inject(
		filter = "component.name=com.liferay.commerce.order.web.internal.asset.CommerceOrderAssetRendererFactory"
	)
	private AssetRendererFactory<?> _assetRendererFactory;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

}