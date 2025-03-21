/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.exception.NoSuchOrderNoteException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderNote;
import com.liferay.commerce.service.CommerceOrderNoteService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderNote;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderNoteResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/order-note.properties",
	scope = ServiceScope.PROTOTYPE, service = OrderNoteResource.class
)
public class OrderNoteResourceImpl extends BaseOrderNoteResourceImpl {

	@Override
	public Response deleteOrderNoteByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceOrderNote commerceOrderNote =
			_commerceOrderNoteService.
				fetchCommerceOrderNoteByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderNote == null) {
			throw new NoSuchOrderNoteException(
				"Unable to find order note with external reference code " +
					externalReferenceCode);
		}

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Page<OrderNote> getOrderByExternalReferenceCodeOrderNotesPage(
			String externalReferenceCode, Pagination pagination)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		List<CommerceOrderNote> commerceOrderNotes =
			_commerceOrderNoteService.getCommerceOrderNotes(
				commerceOrder.getCommerceOrderId(),
				pagination.getStartPosition(), pagination.getEndPosition());

		int totalCount = _commerceOrderNoteService.getCommerceOrderNotesCount(
			commerceOrder.getCommerceOrderId());

		return Page.of(
			_toOrderNotes(commerceOrderNotes), pagination, totalCount);
	}

	@Override
	public Page<OrderNote> getOrderIdOrderNotesPage(
			Long id, Pagination pagination)
		throws Exception {

		List<CommerceOrderNote> commerceOrderNotes =
			_commerceOrderNoteService.getCommerceOrderNotes(
				id, pagination.getStartPosition(), pagination.getEndPosition());

		int totalCount = _commerceOrderNoteService.getCommerceOrderNotesCount(
			id);

		return Page.of(
			_toOrderNotes(commerceOrderNotes), pagination, totalCount);
	}

	@Override
	public OrderNote getOrderNote(Long id) throws Exception {
		return _orderNoteDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				GetterUtil.getLong(id),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Override
	public OrderNote getOrderNoteByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceOrderNote commerceOrderNote =
			_commerceOrderNoteService.
				fetchCommerceOrderNoteByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderNote == null) {
			throw new NoSuchOrderNoteException(
				"Unable to find order note with external reference code " +
					externalReferenceCode);
		}

		return _orderNoteDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceOrderNote.getCommerceOrderNoteId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Override
	public Response patchOrderNote(Long id, OrderNote orderNote)
		throws Exception {

		_updateOrderNote(
			_commerceOrderNoteService.getCommerceOrderNote(id), orderNote);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchOrderNoteByExternalReferenceCode(
			String externalReferenceCode, OrderNote orderNote)
		throws Exception {

		CommerceOrderNote commerceOrderNote =
			_commerceOrderNoteService.
				fetchCommerceOrderNoteByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderNote == null) {
			throw new NoSuchOrderNoteException(
				"Unable to find order note with external reference code " +
					externalReferenceCode);
		}

		_updateOrderNote(commerceOrderNote, orderNote);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public OrderNote postOrderByExternalReferenceCodeOrderNote(
			String externalReferenceCode, OrderNote orderNote)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return _addOrUpdateOrderNote(commerceOrder, orderNote);
	}

	@Override
	public OrderNote postOrderIdOrderNote(Long id, OrderNote orderNote)
		throws Exception {

		return _addOrUpdateOrderNote(
			_commerceOrderService.getCommerceOrder(id), orderNote);
	}

	private OrderNote _addOrUpdateOrderNote(
			CommerceOrder commerceOrder, OrderNote orderNote)
		throws Exception {

		CommerceOrderNote commerceOrderNote =
			_commerceOrderNoteService.addOrUpdateCommerceOrderNote(
				orderNote.getExternalReferenceCode(),
				GetterUtil.get(orderNote.getId(), 0L),
				commerceOrder.getCommerceOrderId(), orderNote.getContent(),
				GetterUtil.get(orderNote.getRestricted(), false),
				_serviceContextHelper.getServiceContext(
					commerceOrder.getGroupId()));

		return _orderNoteDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceOrderNote.getCommerceOrderNoteId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<OrderNote> _toOrderNotes(
			List<CommerceOrderNote> commerceOrderNotes)
		throws Exception {

		return transform(
			commerceOrderNotes,
			commerceOrderNote -> _orderNoteDTOConverter.toDTO(
				new DefaultDTOConverterContext(
					commerceOrderNote.getCommerceOrderNoteId(),
					contextAcceptLanguage.getPreferredLocale())));
	}

	private OrderNote _updateOrderNote(
			CommerceOrderNote commerceOrderNote, OrderNote orderNote)
		throws Exception {

		commerceOrderNote = _commerceOrderNoteService.updateCommerceOrderNote(
			commerceOrderNote.getCommerceOrderNoteId(),
			GetterUtil.get(
				orderNote.getContent(), commerceOrderNote.getContent()),
			GetterUtil.get(
				orderNote.getRestricted(), commerceOrderNote.isRestricted()));

		return _orderNoteDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceOrderNote.getCommerceOrderNoteId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Reference
	private CommerceOrderNoteService _commerceOrderNoteService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter.OrderNoteDTOConverter)"
	)
	private DTOConverter<CommerceOrderNote, OrderNote> _orderNoteDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}