/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.internal.resource.v1_0;

import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.exception.NoSuchShipmentException;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.commerce.service.CommerceShipmentService;
import com.liferay.headless.commerce.admin.shipment.dto.v1_0.Shipment;
import com.liferay.headless.commerce.admin.shipment.dto.v1_0.ShipmentItem;
import com.liferay.headless.commerce.admin.shipment.dto.v1_0.ShippingAddress;
import com.liferay.headless.commerce.admin.shipment.internal.util.v1_0.ShipmentItemUtil;
import com.liferay.headless.commerce.admin.shipment.internal.util.v1_0.ShippingAddressUtil;
import com.liferay.headless.commerce.admin.shipment.resource.v1_0.ShipmentResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/shipment.properties",
	scope = ServiceScope.PROTOTYPE, service = ShipmentResource.class
)
public class ShipmentResourceImpl extends BaseShipmentResourceImpl {

	@Override
	public void deleteShipment(Long shipmentId) throws Exception {
		_commerceShipmentService.deleteCommerceShipment(
			shipmentId, Boolean.FALSE);
	}

	@Override
	public void deleteShipmentByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceShipment commerceShipment =
			_commerceShipmentService.
				fetchCommerceShipmentByExternalReferenceCode(
					contextCompany.getCompanyId(), externalReferenceCode);

		if (commerceShipment == null) {
			throw new NoSuchShipmentException(
				"Unable to find shipment with external reference code " +
					externalReferenceCode);
		}

		_commerceShipmentService.deleteCommerceShipment(
			commerceShipment.getCommerceShipmentId(), Boolean.FALSE);
	}

	@Override
	public Shipment getShipment(Long shipmentId) throws Exception {
		return _toShipment(shipmentId);
	}

	@Override
	public Shipment getShipmentByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceShipment commerceShipment =
			_commerceShipmentService.
				fetchCommerceShipmentByExternalReferenceCode(
					contextCompany.getCompanyId(), externalReferenceCode);

		if (commerceShipment == null) {
			throw new NoSuchShipmentException(
				"Unable to find shipment with external reference code " +
					externalReferenceCode);
		}

		return _toShipment(commerceShipment);
	}

	@Override
	public Page<Shipment> getShipmentsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceShipment.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"status", WorkflowConstants.STATUS_ANY);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toShipment(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public Shipment patchShipment(Long shipmentId, Shipment shipment)
		throws Exception {

		CommerceShipment commerceShipment =
			_commerceShipmentService.getCommerceShipment(shipmentId);

		_updateCommerceShipment(commerceShipment, shipment);

		if (!Validator.isBlank(shipment.getExternalReferenceCode())) {
			_commerceShipmentService.updateExternalReferenceCode(
				shipmentId, shipment.getExternalReferenceCode());
		}

		_updateNestedResources(commerceShipment, shipment);

		return _toShipment(shipmentId);
	}

	@Override
	public Shipment patchShipmentByExternalReferenceCode(
			String externalReferenceCode, Shipment shipment)
		throws Exception {

		CommerceShipment commerceShipment =
			_commerceShipmentService.
				fetchCommerceShipmentByExternalReferenceCode(
					contextCompany.getCompanyId(), externalReferenceCode);

		if (commerceShipment == null) {
			throw new NoSuchShipmentException(
				"Unable to find shipment with external reference code " +
					externalReferenceCode);
		}

		_updateCommerceShipment(commerceShipment, shipment);

		_updateNestedResources(commerceShipment, shipment);

		return _toShipment(commerceShipment);
	}

	@Override
	public Shipment postShipment(Shipment shipment) throws Exception {
		CommerceOrder commerceOrder = _getCommerceOrder(shipment);

		CommerceShipment commerceShipment =
			_commerceShipmentService.addCommerceShipment(
				shipment.getExternalReferenceCode(), commerceOrder.getGroupId(),
				commerceOrder.getCommerceAccountId(),
				commerceOrder.getShippingAddressId(),
				commerceOrder.getCommerceShippingMethodId(),
				commerceOrder.getShippingOptionName(),
				_serviceContextHelper.getServiceContext(contextUser));

		_updateCommerceShipment(commerceShipment, shipment);

		_updateNestedResources(commerceShipment, shipment);

		return _toShipment(commerceShipment.getCommerceShipmentId());
	}

	@Override
	public Shipment postShipmentByExternalReferenceCodeStatusDelivered(
			String externalReferenceCode)
		throws Exception {

		CommerceShipment commerceShipment =
			_commerceShipmentService.
				fetchCommerceShipmentByExternalReferenceCode(
					contextCompany.getCompanyId(), externalReferenceCode);

		if (commerceShipment == null) {
			throw new NoSuchShipmentException(
				"Unable to find shipment with external reference code " +
					externalReferenceCode);
		}

		return _toShipment(
			_commerceShipmentService.updateStatus(
				commerceShipment.getCommerceShipmentId(),
				CommerceShipmentConstants.SHIPMENT_STATUS_DELIVERED));
	}

	@Override
	public Shipment postShipmentByExternalReferenceCodeStatusFinishProcessing(
			String externalReferenceCode)
		throws Exception {

		CommerceShipment commerceShipment =
			_commerceShipmentService.
				fetchCommerceShipmentByExternalReferenceCode(
					contextCompany.getCompanyId(), externalReferenceCode);

		if (commerceShipment == null) {
			throw new NoSuchShipmentException(
				"Unable to find shipment with external reference code " +
					externalReferenceCode);
		}

		return _toShipment(
			_commerceShipmentService.updateStatus(
				commerceShipment.getCommerceShipmentId(),
				CommerceShipmentConstants.SHIPMENT_STATUS_READY_TO_BE_SHIPPED));
	}

	@Override
	public Shipment postShipmentByExternalReferenceCodeStatusShipped(
			String externalReferenceCode)
		throws Exception {

		CommerceShipment commerceShipment =
			_commerceShipmentService.
				fetchCommerceShipmentByExternalReferenceCode(
					contextCompany.getCompanyId(), externalReferenceCode);

		if (commerceShipment == null) {
			throw new NoSuchShipmentException(
				"Unable to find shipment with external reference code " +
					externalReferenceCode);
		}

		return _toShipment(
			_commerceShipmentService.updateStatus(
				commerceShipment.getCommerceShipmentId(),
				CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED));
	}

	@Override
	public Shipment postShipmentStatusDelivered(Long shipmentId)
		throws Exception {

		return _toShipment(
			_commerceShipmentService.updateStatus(
				shipmentId,
				CommerceShipmentConstants.SHIPMENT_STATUS_DELIVERED));
	}

	@Override
	public Shipment postShipmentStatusFinishProcessing(Long shipmentId)
		throws Exception {

		return _toShipment(
			_commerceShipmentService.updateStatus(
				shipmentId,
				CommerceShipmentConstants.SHIPMENT_STATUS_READY_TO_BE_SHIPPED));
	}

	@Override
	public Shipment postShipmentStatusShipped(Long shipmentId)
		throws Exception {

		return _toShipment(
			_commerceShipmentService.updateStatus(
				shipmentId, CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED));
	}

	@Override
	public Shipment putShipmentByExternalReferenceCode(
			String externalReferenceCode, Shipment shipment)
		throws Exception {

		CommerceShipment commerceShipment =
			_commerceShipmentService.
				fetchCommerceShipmentByExternalReferenceCode(
					contextCompany.getCompanyId(), externalReferenceCode);

		if (commerceShipment == null) {
			CommerceOrder commerceOrder = _getCommerceOrder(shipment);

			commerceShipment = _commerceShipmentService.addCommerceShipment(
				externalReferenceCode, commerceOrder.getGroupId(),
				commerceOrder.getCommerceAccountId(),
				commerceOrder.getShippingAddressId(),
				commerceOrder.getCommerceShippingMethodId(),
				commerceOrder.getShippingOptionName(),
				_serviceContextHelper.getServiceContext(contextUser));
		}

		_updateCommerceShipment(commerceShipment, shipment);

		_updateNestedResources(commerceShipment, shipment);

		return _toShipment(commerceShipment);
	}

	private Map<String, Map<String, String>> _getActions(
		CommerceShipment commerceShipment) {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"DELETE", commerceShipment.getCommerceShipmentId(),
				"deleteShipment", commerceShipment.getUserId(),
				"com.liferay.commerce.model.CommerceShipment",
				commerceShipment.getGroupId())
		).put(
			"get",
			addAction(
				"VIEW", commerceShipment.getCommerceShipmentId(), "getShipment",
				commerceShipment.getUserId(),
				"com.liferay.commerce.model.CommerceShipment",
				commerceShipment.getGroupId())
		).put(
			"update",
			addAction(
				"UPDATE", commerceShipment.getCommerceShipmentId(),
				"patchShipment", commerceShipment.getUserId(),
				"com.liferay.commerce.model.CommerceShipment",
				commerceShipment.getGroupId())
		).build();
	}

	private CommerceOrder _getCommerceOrder(Shipment shipment)
		throws Exception {

		long orderId = GetterUtil.getLong(shipment.getOrderId());

		if (orderId > 0) {
			return _commerceOrderService.getCommerceOrder(orderId);
		}

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				shipment.getOrderExternalReferenceCode(),
				contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					shipment.getOrderExternalReferenceCode());
		}

		return commerceOrder;
	}

	private Shipment _toShipment(CommerceShipment commerceShipment)
		throws Exception {

		return _toShipment(commerceShipment.getCommerceShipmentId());
	}

	private Shipment _toShipment(long commerceShipmentId) throws Exception {
		CommerceShipment commerceShipment =
			_commerceShipmentService.getCommerceShipment(commerceShipmentId);

		return _shipmentDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commerceShipment), _dtoConverterRegistry,
				commerceShipmentId, contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser));
	}

	private void _updateCommerceShipment(
			CommerceShipment commerceShipment, Shipment shipment)
		throws Exception {

		int expectedDay = 0;
		int expectedMonth = 0;
		int expectedYear = 0;
		int expectedHour = 0;
		int expectedMinute = 0;

		Date expectedDate = shipment.getExpectedDate();

		if (expectedDate != null) {
			Calendar calendar = CalendarFactoryUtil.getCalendar(
				expectedDate.getTime());

			expectedDay = calendar.get(Calendar.DAY_OF_MONTH);
			expectedMonth = calendar.get(Calendar.MONTH);
			expectedYear = calendar.get(Calendar.YEAR);
			expectedHour = calendar.get(Calendar.HOUR_OF_DAY);
			expectedMinute = calendar.get(Calendar.MINUTE);
		}

		int shippingDay = 0;
		int shippingMonth = 0;
		int shippingYear = 0;
		int shippingHour = 0;
		int shippingMinute = 0;

		Date shippingDate = shipment.getShippingDate();

		if (shippingDate != null) {
			Calendar calendar = CalendarFactoryUtil.getCalendar(
				shippingDate.getTime());

			shippingDay = calendar.get(Calendar.DAY_OF_MONTH);
			shippingMonth = calendar.get(Calendar.MONTH);
			shippingYear = calendar.get(Calendar.YEAR);
			shippingHour = calendar.get(Calendar.HOUR_OF_DAY);
			shippingMinute = calendar.get(Calendar.MINUTE);
		}

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			contextUser);

		Map<String, Serializable> expandoBridgeAttributes =
			CustomFieldsUtil.toMap(
				CommerceShipment.class.getName(), contextCompany.getCompanyId(),
				shipment.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale());

		if (expandoBridgeAttributes == null) {
			expandoBridgeAttributes = new HashMap<>();
		}

		serviceContext.setExpandoBridgeAttributes(expandoBridgeAttributes);

		_commerceShipmentService.updateCommerceShipment(
			commerceShipment.getCommerceShipmentId(),
			GetterUtil.get(
				shipment.getShippingMethodId(),
				commerceShipment.getCommerceShippingMethodId()),
			GetterUtil.get(
				shipment.getCarrier(), commerceShipment.getCarrier()),
			expectedMonth, expectedDay, expectedYear, expectedHour,
			expectedMinute, shippingMonth, shippingDay, shippingYear,
			shippingHour, shippingMinute,
			GetterUtil.get(
				shipment.getTrackingNumber(),
				commerceShipment.getTrackingNumber()),
			GetterUtil.get(
				shipment.getTrackingURL(), commerceShipment.getTrackingURL()),
			commerceShipment.getStatus(), serviceContext);
	}

	private CommerceShipment _updateNestedResources(
			CommerceShipment commerceShipment, Shipment shipment)
		throws Exception {

		// Shipping address

		ShippingAddress shippingAddress = shipment.getShippingAddress();

		if (shippingAddress != null) {
			ShippingAddressUtil.updateShippingAddress(
				_commerceAddressService, _commerceShipmentService,
				commerceShipment, _countryService, _regionService,
				shippingAddress, _serviceContextHelper);
		}

		// Shipment items

		ShipmentItem[] shipmentItems = shipment.getShipmentItems();

		if (shipmentItems != null) {
			_commerceShipmentItemService.deleteCommerceShipmentItems(
				commerceShipment.getCommerceShipmentId(), true);

			for (ShipmentItem shipmentItem : shipmentItems) {
				ShipmentItemUtil.addOrUpdateShipmentItem(
					_commerceInventoryWarehouseService,
					_commerceOrderItemService, commerceShipment,
					_commerceShipmentItemService,
					shipmentItem.getExternalReferenceCode(),
					_serviceContextHelper, shipmentItem);
			}
		}

		return commerceShipment;
	}

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceShipmentItemService _commerceShipmentItemService;

	@Reference
	private CommerceShipmentService _commerceShipmentService;

	@Reference
	private CountryService _countryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private RegionService _regionService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.shipment.internal.dto.v1_0.converter.ShipmentDTOConverter)"
	)
	private DTOConverter<CommerceShipment, Shipment> _shipmentDTOConverter;

}