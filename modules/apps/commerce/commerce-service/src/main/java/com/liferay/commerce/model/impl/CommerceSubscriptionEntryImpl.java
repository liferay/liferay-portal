/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model.impl;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.service.CommerceOrderItemLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceSubscriptionEntryImpl
	extends CommerceSubscriptionEntryBaseImpl {

	@Override
	public CPDefinition fetchCPDefinition() throws PortalException {
		CommerceOrderItem commerceOrderItem = fetchCommerceOrderItem();

		if (commerceOrderItem == null) {
			return null;
		}

		return commerceOrderItem.getCPDefinition();
	}

	@Override
	public CPInstance fetchCPInstance() {
		CommerceOrderItem commerceOrderItem = fetchCommerceOrderItem();

		if (commerceOrderItem == null) {
			return null;
		}

		return commerceOrderItem.fetchCPInstance();
	}

	@Override
	public CommerceOrderItem fetchCommerceOrderItem() {
		return CommerceOrderItemLocalServiceUtil.fetchCommerceOrderItem(
			getCommerceOrderItemId());
	}

	@Override
	public long getCPDefinitionId() throws PortalException {
		CPDefinition cpDefinition = fetchCPDefinition();

		if (cpDefinition == null) {
			return 0;
		}

		return cpDefinition.getCPDefinitionId();
	}

	@Override
	public long getCPInstanceId() {
		CPInstance cpInstance = fetchCPInstance();

		if (cpInstance == null) {
			return 0;
		}

		return cpInstance.getCPInstanceId();
	}

	@Override
	public UnicodeProperties
		getDeliverySubscriptionTypeSettingsUnicodeProperties() {

		if (_deliverySubscriptionTypeSettingsUnicodeProperties == null) {
			_deliverySubscriptionTypeSettingsUnicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).fastLoad(
					getDeliverySubscriptionTypeSettings()
				).build();
		}

		return _deliverySubscriptionTypeSettingsUnicodeProperties;
	}

	@Override
	public UnicodeProperties getSubscriptionTypeSettingsUnicodeProperties() {
		if (_subscriptionTypeSettingsUnicodeProperties == null) {
			_subscriptionTypeSettingsUnicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).fastLoad(
					getSubscriptionTypeSettings()
				).build();
		}

		return _subscriptionTypeSettingsUnicodeProperties;
	}

	@Override
	public void setDeliverySubscriptionTypeSettingsUnicodeProperties(
		UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties) {

		_deliverySubscriptionTypeSettingsUnicodeProperties =
			deliverySubscriptionTypeSettingsUnicodeProperties;

		if (_deliverySubscriptionTypeSettingsUnicodeProperties == null) {
			_deliverySubscriptionTypeSettingsUnicodeProperties =
				new UnicodeProperties();
		}

		super.setSubscriptionTypeSettings(
			_deliverySubscriptionTypeSettingsUnicodeProperties.toString());
	}

	@Override
	public void setSubscriptionTypeSettings(String subscriptionTypeSettings) {
		super.setSubscriptionTypeSettings(subscriptionTypeSettings);

		_subscriptionTypeSettingsUnicodeProperties = null;
	}

	@Override
	public void setSubscriptionTypeSettingsUnicodeProperties(
		UnicodeProperties subscriptionTypeSettingsUnicodeProperties) {

		_subscriptionTypeSettingsUnicodeProperties =
			subscriptionTypeSettingsUnicodeProperties;

		if (_subscriptionTypeSettingsUnicodeProperties == null) {
			_subscriptionTypeSettingsUnicodeProperties =
				new UnicodeProperties();
		}

		super.setSubscriptionTypeSettings(
			_subscriptionTypeSettingsUnicodeProperties.toString());
	}

	private UnicodeProperties
		_deliverySubscriptionTypeSettingsUnicodeProperties;
	private UnicodeProperties _subscriptionTypeSettingsUnicodeProperties;

}