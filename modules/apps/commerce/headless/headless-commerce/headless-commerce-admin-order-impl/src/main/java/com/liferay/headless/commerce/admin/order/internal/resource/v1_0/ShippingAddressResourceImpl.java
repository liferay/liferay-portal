/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.dto.v1_0.ShippingAddress;
import com.liferay.headless.commerce.admin.order.internal.util.v1_0.ShippingAddressUtil;
import com.liferay.headless.commerce.admin.order.resource.v1_0.ShippingAddressResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;

import jakarta.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/shipping-address.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ShippingAddressResource.class
)
public class ShippingAddressResourceImpl
	extends BaseShippingAddressResourceImpl {

	@Override
	public ShippingAddress getOrderByExternalReferenceCodeShippingAddress(
			String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		CommerceAddress commerceAddress =
			_commerceAddressService.getCommerceAddress(
				commerceOrder.getShippingAddressId());

		return _shippingAddressDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceAddress.getCommerceAddressId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@NestedField(parentClass = Order.class, value = "shippingAddress")
	@Override
	public ShippingAddress getOrderIdShippingAddress(Long id) throws Exception {
		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			id);

		CommerceAddress commerceAddress =
			_commerceAddressService.fetchCommerceAddress(
				commerceOrder.getShippingAddressId());

		if (commerceAddress == null) {
			return new ShippingAddress();
		}

		return _shippingAddressDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceAddress.getCommerceAddressId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@NestedField(parentClass = OrderItem.class, value = "shippingAddress")
	@Override
	public ShippingAddress getOrderItemShippingAddress(Long id)
		throws Exception {

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(id);

		if (commerceOrderItem.getShippingAddressId() == 0) {
			return new ShippingAddress();
		}

		return _shippingAddressDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceOrderItem.getShippingAddressId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Override
	public Response patchOrderByExternalReferenceCodeShippingAddress(
			String externalReferenceCode, ShippingAddress shippingAddress)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		ShippingAddressUtil.addOrUpdateShippingAddress(
			_commerceAddressService, commerceOrder, _commerceOrderService,
			_countryService, shippingAddress,
			_serviceContextHelper.getServiceContext());

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Response patchOrderIdShippingAddress(
			Long id, ShippingAddress shippingAddress)
		throws Exception {

		ShippingAddressUtil.addOrUpdateShippingAddress(
			_commerceAddressService, _commerceOrderService.getCommerceOrder(id),
			_commerceOrderService, _countryService, shippingAddress,
			_serviceContextHelper.getServiceContext());

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CountryService _countryService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter.ShippingAddressDTOConverter)"
	)
	private DTOConverter<CommerceAddress, ShippingAddress>
		_shippingAddressDTOConverter;

}