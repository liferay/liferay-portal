/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSubscriptionConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

/**
 * @author Alessio Antonio Rendina
 */
public class ProductSubscriptionConfigurationUtil {

	public static CPDefinition updateCPDefinitionSubscriptionInfo(
			CPDefinitionService cpDefinitionService,
			ProductSubscriptionConfiguration productSubscriptionConfiguration,
			CPDefinition cpDefinition)
		throws PortalException {

		String deliverySubscriptionTypeValue = null;

		ProductSubscriptionConfiguration.DeliverySubscriptionType
			deliverySubscriptionType =
				productSubscriptionConfiguration.getDeliverySubscriptionType();

		if (deliverySubscriptionType != null) {
			deliverySubscriptionTypeValue = deliverySubscriptionType.getValue();
		}

		String subscriptionTypeValue = null;

		ProductSubscriptionConfiguration.SubscriptionType subscriptionType =
			productSubscriptionConfiguration.getSubscriptionType();

		if (subscriptionType != null) {
			subscriptionTypeValue = subscriptionType.getValue();
		}

		return cpDefinitionService.updateSubscriptionInfo(
			cpDefinition.getCPDefinitionId(),
			GetterUtil.get(
				productSubscriptionConfiguration.getEnable(),
				cpDefinition.isSubscriptionEnabled()),
			GetterUtil.get(
				productSubscriptionConfiguration.getLength(),
				cpDefinition.getSubscriptionLength()),
			subscriptionTypeValue,
			UnicodePropertiesBuilder.create(
				productSubscriptionConfiguration.getSubscriptionTypeSettings(),
				true
			).build(),
			GetterUtil.get(
				productSubscriptionConfiguration.getNumberOfLength(),
				cpDefinition.getMaxSubscriptionCycles()),
			GetterUtil.get(
				productSubscriptionConfiguration.
					getDeliverySubscriptionEnable(),
				cpDefinition.isDeliverySubscriptionEnabled()),
			GetterUtil.get(
				productSubscriptionConfiguration.
					getDeliverySubscriptionLength(),
				cpDefinition.getDeliverySubscriptionLength()),
			deliverySubscriptionTypeValue,
			UnicodePropertiesBuilder.create(
				productSubscriptionConfiguration.
					getDeliverySubscriptionTypeSettings(),
				true
			).build(),
			GetterUtil.get(
				productSubscriptionConfiguration.
					getDeliverySubscriptionNumberOfLength(),
				cpDefinition.getDeliveryMaxSubscriptionCycles()));
	}

}