/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.order;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderValidator;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	property = {
		"commerce.order.validator.key=" + VersionCommerceOrderValidatorImpl.KEY,
		"commerce.order.validator.priority:Integer=30"
	},
	service = CommerceOrderValidator.class
)
public class VersionCommerceOrderValidatorImpl
	implements CommerceOrderValidator {

	public static final String KEY = "version";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public CommerceOrderValidatorResult validate(
			Locale locale, CommerceOrder commerceOrder, CPInstance cpInstance,
			String json, BigDecimal quantity, boolean child)
		throws PortalException {

		return new CommerceOrderValidatorResult(true);
	}

	@Override
	public CommerceOrderValidatorResult validate(
			Locale locale, CommerceOrderItem commerceOrderItem)
		throws PortalException {

		if ((commerceOrderItem.getCPInstanceId() != 0) &&
			(commerceOrderItem.getCProductId() != 0)) {

			CPInstance cpInstance = commerceOrderItem.fetchCPInstance();

			CProduct cProduct = commerceOrderItem.fetchCProduct();

			if ((cpInstance != null) && (cProduct != null) &&
				(cpInstance.getCPDefinitionId() !=
					cProduct.getPublishedCPDefinitionId()) &&
				(cProduct.getPublishedCPDefinitionId() != 0)) {

				boolean instanceUpdated = _updateInstance(
					commerceOrderItem, cProduct);

				if (instanceUpdated) {
					return new CommerceOrderValidatorResult(
						commerceOrderItem.getCommerceOrderItemId(), false,
						_getLocalizedMessage(
							locale,
							"this-product-will-be-automatically-updated-to-a-" +
								"newer-version"));
				}

				return new CommerceOrderValidatorResult(
					commerceOrderItem.getCommerceOrderItemId(), false,
					_getLocalizedMessage(
						locale,
						"there-is-a-newer-version-of-this-product-available"));
			}
		}

		return new CommerceOrderValidatorResult(true);
	}

	protected void setCPInstanceLocalService(
		CPInstanceLocalService cpInstanceLocalService) {

		_cpInstanceLocalService = cpInstanceLocalService;
	}

	protected void setCommerceOrderItemLocalService(
		CommerceOrderItemLocalService commerceOrderItemLocalService) {

		_commerceOrderItemLocalService = commerceOrderItemLocalService;
	}

	private String _getLocalizedMessage(Locale locale, String key) {
		if (locale == null) {
			return key;
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, key);
	}

	private boolean _updateInstance(
			CommerceOrderItem commerceOrderItem, CProduct cProduct)
		throws PortalException {

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cProduct.getPublishedCPDefinitionId(), commerceOrderItem.getSku());

		if (cpInstance != null) {
			commerceOrderItem.setCPInstanceId(cpInstance.getCPInstanceId());

			_commerceOrderItemLocalService.updateCommerceOrderItem(
				commerceOrderItem);

			return true;
		}

		return false;
	}

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Language _language;

}