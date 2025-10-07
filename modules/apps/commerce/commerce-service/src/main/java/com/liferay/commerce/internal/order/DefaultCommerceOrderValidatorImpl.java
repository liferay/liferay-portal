/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.order;

import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderValidator;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.product.discovery.CPConfigurationListDiscovery;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"commerce.order.validator.key=" + DefaultCommerceOrderValidatorImpl.KEY,
		"commerce.order.validator.priority:Integer=10"
	},
	service = CommerceOrderValidator.class
)
public class DefaultCommerceOrderValidatorImpl
	implements CommerceOrderValidator {

	public static final String KEY = "default";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public CommerceOrderValidatorResult validate(
			Locale locale, CommerceOrder commerceOrder, CPInstance cpInstance,
			String json, BigDecimal quantity, boolean child)
		throws PortalException {

		if (cpInstance == null) {
			return new CommerceOrderValidatorResult(false);
		}

		if (!commerceOrder.isOpen()) {
			return new CommerceOrderValidatorResult(
				false,
				_getLocalizedMessage(
					locale, "this-order-has-already-been-checked-out", null));
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceOrder.getGroupId());

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				cpInstance.getCompanyId(), cpInstance.getGroupId(),
				commerceOrder.getCommerceAccountId(),
				commerceChannel.getCommerceChannelId(),
				commerceOrder.getCommerceOrderTypeId());

		long cpConfigurationListId =
			cpConfigurationList.getCPConfigurationListId();

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
				_classNameLocalService.getClassNameId(CPDefinition.class),
				cpInstance.getCPDefinitionId(), cpConfigurationListId);

		if (cpConfigurationEntry == null) {
			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			cpConfigurationEntry =
				cpDefinition.fetchMasterCPConfigurationEntry();
		}

		CPDefinitionInventoryEngine cpDefinitionInventoryEngine =
			_cpDefinitionInventoryEngineRegistry.getCPDefinitionInventoryEngine(
				cpConfigurationEntry.getCPDefinitionInventoryEngine());

		BigDecimal minOrderQuantity =
			cpDefinitionInventoryEngine.getMinOrderQuantity(
				cpConfigurationListId, cpInstance);

		if (BigDecimalUtil.lt(quantity, minOrderQuantity)) {
			return new CommerceOrderValidatorResult(
				false,
				_getLocalizedMessage(
					locale, "the-minimum-quantity-is-x",
					new Object[] {minOrderQuantity}));
		}

		BigDecimal maxOrderQuantity =
			cpDefinitionInventoryEngine.getMaxOrderQuantity(
				cpConfigurationListId, cpInstance);

		if (BigDecimalUtil.gt(maxOrderQuantity, BigDecimal.ZERO) &&
			BigDecimalUtil.gt(quantity, maxOrderQuantity)) {

			return new CommerceOrderValidatorResult(
				false,
				_getLocalizedMessage(
					locale, "the-maximum-quantity-is-x",
					new Object[] {maxOrderQuantity}));
		}

		List<BigDecimal> allowedOrderQuantities = TransformUtil.transformToList(
			cpDefinitionInventoryEngine.getAllowedOrderQuantities(
				cpConfigurationListId, cpInstance),
			allowedOrderQuantity -> {
				BigDecimal allowedOrderQuantityBigDecimal = BigDecimal.valueOf(
					GetterUtil.getDouble(allowedOrderQuantity));

				return allowedOrderQuantityBigDecimal.stripTrailingZeros();
			});

		if (!allowedOrderQuantities.isEmpty() &&
			!allowedOrderQuantities.contains(quantity.stripTrailingZeros())) {

			return new CommerceOrderValidatorResult(
				false,
				_getLocalizedMessage(
					locale, "the-specified-quantity-is-not-allowed", null));
		}

		BigDecimal multipleOrderQuantity =
			cpDefinitionInventoryEngine.getMultipleOrderQuantity(
				cpConfigurationListId, cpInstance);

		if (!BigDecimalUtil.eq(
				quantity.remainder(multipleOrderQuantity), BigDecimal.ZERO)) {

			return new CommerceOrderValidatorResult(
				false,
				_getLocalizedMessage(
					locale, "the-specified-quantity-is-not-a-multiple-of-x",
					new Object[] {multipleOrderQuantity}));
		}

		return new CommerceOrderValidatorResult(true);
	}

	@Override
	public CommerceOrderValidatorResult validate(
			Locale locale, CommerceOrderItem commerceOrderItem)
		throws PortalException {

		CPInstance cpInstance = commerceOrderItem.fetchCPInstance();

		if (cpInstance == null) {
			return new CommerceOrderValidatorResult(false);
		}

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceOrder.getGroupId());

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListDiscovery.getCPConfigurationList(
				cpInstance.getCompanyId(), cpInstance.getGroupId(),
				commerceOrder.getCommerceAccountId(),
				commerceChannel.getCommerceChannelId(),
				commerceOrder.getCommerceOrderTypeId());

		long cpConfigurationListId =
			cpConfigurationList.getCPConfigurationListId();

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
				_classNameLocalService.getClassNameId(CPDefinition.class),
				cpInstance.getCPDefinitionId(), cpConfigurationListId);

		if (cpConfigurationEntry == null) {
			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			cpConfigurationEntry =
				cpDefinition.fetchMasterCPConfigurationEntry();
		}

		CPDefinitionInventoryEngine cpDefinitionInventoryEngine =
			_cpDefinitionInventoryEngineRegistry.getCPDefinitionInventoryEngine(
				cpConfigurationEntry.getCPDefinitionInventoryEngine());

		BigDecimal minOrderQuantity =
			cpDefinitionInventoryEngine.getMinOrderQuantity(
				cpConfigurationListId, cpInstance);

		BigDecimal quantity = commerceOrderItem.getQuantity();

		if (BigDecimalUtil.lt(quantity, minOrderQuantity)) {
			return new CommerceOrderValidatorResult(
				commerceOrderItem.getCommerceOrderItemId(), false,
				_getLocalizedMessage(
					locale, "the-minimum-quantity-is-x",
					new Object[] {minOrderQuantity}));
		}

		BigDecimal maxOrderQuantity =
			cpDefinitionInventoryEngine.getMaxOrderQuantity(
				cpConfigurationListId, cpInstance);

		if (BigDecimalUtil.gt(maxOrderQuantity, BigDecimal.ZERO) &&
			BigDecimalUtil.gt(quantity, maxOrderQuantity)) {

			return new CommerceOrderValidatorResult(
				commerceOrderItem.getCommerceOrderItemId(), false,
				_getLocalizedMessage(
					locale, "the-maximum-quantity-is-x",
					new Object[] {maxOrderQuantity}));
		}

		List<BigDecimal> allowedOrderQuantities = TransformUtil.transformToList(
			cpDefinitionInventoryEngine.getAllowedOrderQuantities(
				cpConfigurationListId, cpInstance),
			allowedOrderQuantity -> {
				BigDecimal allowedOrderQuantityBigDecimal = BigDecimal.valueOf(
					GetterUtil.getDouble(allowedOrderQuantity));

				return allowedOrderQuantityBigDecimal.stripTrailingZeros();
			});

		if (!allowedOrderQuantities.isEmpty() &&
			!allowedOrderQuantities.contains(quantity.stripTrailingZeros())) {

			return new CommerceOrderValidatorResult(
				commerceOrderItem.getCommerceOrderItemId(), false,
				_getLocalizedMessage(
					locale, "the-specified-quantity-is-not-allowed", null));
		}

		BigDecimal multipleOrderQuantity =
			cpDefinitionInventoryEngine.getMultipleOrderQuantity(
				cpConfigurationListId, cpInstance);

		if (!BigDecimalUtil.eq(
				quantity.remainder(multipleOrderQuantity), BigDecimal.ZERO)) {

			return new CommerceOrderValidatorResult(
				false,
				_getLocalizedMessage(
					locale, "the-specified-quantity-is-not-a-multiple-of-x",
					new Object[] {multipleOrderQuantity}));
		}

		return new CommerceOrderValidatorResult(true);
	}

	private String _getLocalizedMessage(
		Locale locale, String key, Object[] arguments) {

		if (locale == null) {
			return key;
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		if (arguments == null) {
			return _language.get(resourceBundle, key);
		}

		return _language.format(resourceBundle, key, arguments);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Reference
	private CPConfigurationListDiscovery _cpConfigurationListDiscovery;

	@Reference
	private CPDefinitionInventoryEngineRegistry
		_cpDefinitionInventoryEngineRegistry;

	@Reference
	private Language _language;

}