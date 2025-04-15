/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.resource.v1_0;

import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrder;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.Shipment;
import com.liferay.headless.commerce.delivery.order.internal.odate.entity.v1_0.ShipmentEntityModel;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.ShipmentResource;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/shipment.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ShipmentResource.class
)
public class ShipmentResourceImpl extends BaseShipmentResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<Shipment> getPlacedOrderByExternalReferenceCodeShipmentsPage(
			String externalReferenceCode, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return getPlacedOrderShipmentsPage(
			commerceOrder.getCommerceOrderId(), search, filter, pagination,
			sorts);
	}

	@NestedField(parentClass = PlacedOrder.class, value = "shipments")
	@Override
	public Page<Shipment> getPlacedOrderShipmentsPage(
			@NestedFieldId("id") Long placedOrderId, String search,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			placedOrderId);

		if (commerceOrder.isOpen()) {
			throw new NoSuchOrderException();
		}

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceShipment.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK, "itemsCount", "oneLineAddress"),
			searchContext -> {
				searchContext.setAttribute(
					"commerceOrderIds",
					new long[] {commerceOrder.getCommerceOrderId()});
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toShipment(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)),
				GetterUtil.getInteger(document.get("itemsCount")),
				GetterUtil.getString(document.get("oneLineAddress"))));
	}

	private Shipment _toShipment(
			long commerceShipmentId, int itemsCount, String oneLineAddress)
		throws Exception {

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), null,
				_dtoConverterRegistry, commerceShipmentId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser);

		defaultDTOConverterContext.setAttribute("itemsCount", itemsCount);
		defaultDTOConverterContext.setAttribute(
			"oneLineAddress", oneLineAddress);

		return _shipmentDTOConverter.toDTO(defaultDTOConverterContext);
	}

	private static final EntityModel _entityModel = new ShipmentEntityModel();

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.order.internal.dto.v1_0.converter.ShipmentDTOConverter)"
	)
	private DTOConverter<CommerceShipment, Shipment> _shipmentDTOConverter;

}