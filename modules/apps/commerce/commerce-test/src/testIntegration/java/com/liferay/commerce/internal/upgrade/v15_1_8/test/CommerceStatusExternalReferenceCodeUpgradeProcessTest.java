/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_1_8.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matyas Wollner
 */
@RunWith(Arquillian.class)
public class CommerceStatusExternalReferenceCodeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		Group group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), commerceCurrency.getCode());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			TestPropsValues.getUserId(), commerceChannel.getGroupId(),
			commerceCurrency.getCommerceCurrencyId());

		CPInstance cpInstance = CPTestUtil.addCPInstance(group.getGroupId());

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(
			cpInstance.getCPDefinition());

		CommerceOrderItem commerceOrderItem =
			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, commerceOrderItem.getStatus());

		CommerceShippingMethod commerceShippingMethod =
			CommerceTestUtil.addCommerceShippingMethod(
				TestPropsValues.getUserId(), group.getGroupId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			commerceShippingMethod.getStatus());

		long commerceOrderItemId = commerceOrderItem.getCommerceOrderItemId();
		long commerceShippingMethodId =
			commerceShippingMethod.getCommerceShippingMethodId();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"update CommerceOrderItem set status = ? where " +
					"commerceOrderItemId = ?");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"update CommerceShippingMethod set externalReferenceCode = " +
					"NULL, status = ? where commerceShippingMethodId = ?")) {

			preparedStatement1.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement1.setLong(2, commerceOrderItemId);

			preparedStatement1.executeUpdate();

			preparedStatement2.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement2.setLong(2, commerceShippingMethodId);

			preparedStatement2.executeUpdate();
		}

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.commerce.internal.upgrade.v15_1_8." +
				"CommerceStatusExternalReferenceCodeUpgradeProcess");

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();

		commerceOrderItem = _commerceOrderItemLocalService.getCommerceOrderItem(
			commerceOrderItemId);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, commerceOrderItem.getStatus());

		commerceShippingMethod =
			_commerceShippingMethodLocalService.getCommerceShippingMethod(
				commerceShippingMethodId);

		Assert.assertEquals(
			String.valueOf(commerceShippingMethodId),
			commerceShippingMethod.getExternalReferenceCode());
		Assert.assertEquals(
			commerceShippingMethod,
			_commerceShippingMethodLocalService.
				fetchCommerceShippingMethodByExternalReferenceCode(
					String.valueOf(commerceShippingMethodId),
					commerceShippingMethod.getCompanyId()));
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			commerceShippingMethod.getStatus());
	}

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.internal.upgrade.registry.CommerceServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}