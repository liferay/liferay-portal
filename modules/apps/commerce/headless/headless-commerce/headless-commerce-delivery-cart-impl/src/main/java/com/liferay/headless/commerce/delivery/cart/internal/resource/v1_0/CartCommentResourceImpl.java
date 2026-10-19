/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0;

import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.exception.NoSuchOrderNoteException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderNote;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.service.CommerceOrderNoteService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.CartComment;
import com.liferay.headless.commerce.delivery.cart.internal.util.v1_0.GuestCommerceOrderUtil;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartCommentResource;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/cart-comment.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = CartCommentResource.class
)
public class CartCommentResourceImpl extends BaseCartCommentResourceImpl {

	@Override
	public void deleteCartComment(Long commentId) throws Exception {
		CommerceOrderNote commerceOrderNote =
			_commerceOrderNoteService.getCommerceOrderNote(commentId);

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			_commerceOrderService.getCommerceOrder(
				commerceOrderNote.getCommerceOrderId()),
			_commerceOrderHttpHelper, contextHttpServletRequest);

		_commerceOrderNoteService.deleteCommerceOrderNote(commentId);
	}

	@Override
	public void deleteCartCommentByExternalReferenceCode(
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

		deleteCartComment(commerceOrderNote.getCommerceOrderNoteId());
	}

	@Override
	public Page<CartComment> getCartByExternalReferenceCodeCommentsPage(
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

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		return Page.of(
			_toOrderNotes(
				_commerceOrderNoteService.getCommerceOrderNotes(
					commerceOrder.getCommerceOrderId(), false,
					pagination.getStartPosition(),
					pagination.getEndPosition())),
			pagination,
			_commerceOrderNoteService.getCommerceOrderNotesCount(
				commerceOrder.getCommerceOrderId(), false));
	}

	@Override
	public CartComment getCartComment(Long commentId) throws Exception {
		CommerceOrderNote commerceOrderNote =
			_commerceOrderNoteService.getCommerceOrderNote(commentId);

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			_commerceOrderService.getCommerceOrder(
				commerceOrderNote.getCommerceOrderId()),
			_commerceOrderHttpHelper, contextHttpServletRequest);

		return _toOrderNote(GetterUtil.getLong(commentId));
	}

	@Override
	public CartComment getCartCommentByExternalReferenceCode(
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

		return getCartComment(commerceOrderNote.getCommerceOrderNoteId());
	}

	@NestedField(parentClass = Cart.class, value = "notes")
	@Override
	public Page<CartComment> getCartCommentsPage(
			@NestedFieldId("id") Long cartId, Pagination pagination)
		throws Exception {

		PortletResourcePermission portletResourcePermission =
			_modelResourcePermission.getPortletResourcePermission();

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		if (portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commerceOrder.getGroupId(),
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_NOTES)) {

			return Page.of(
				_toOrderNotes(
					_commerceOrderNoteService.getCommerceOrderNotes(
						cartId, pagination.getStartPosition(),
						pagination.getEndPosition())),
				pagination,
				_commerceOrderNoteService.getCommerceOrderNotesCount(
					cartId, false));
		}

		return Page.of(
			_toOrderNotes(
				_commerceOrderNoteService.getCommerceOrderNotes(
					cartId, false, pagination.getStartPosition(),
					pagination.getEndPosition())),
			pagination,
			_commerceOrderNoteService.getCommerceOrderNotesCount(
				cartId, false));
	}

	@Override
	public CartComment postCartByExternalReferenceCodeComment(
			String externalReferenceCode, CartComment cartComment)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		return _addOrUpdateOrderNote(commerceOrder, cartComment);
	}

	@Override
	public CartComment postCartComment(Long cartId, CartComment cartComment)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		return _addOrUpdateOrderNote(commerceOrder, cartComment);
	}

	@Override
	public CartComment putCartComment(Long commentId, CartComment cartComment)
		throws Exception {

		CommerceOrderNote commerceOrderNote =
			_commerceOrderNoteService.getCommerceOrderNote(commentId);

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderNote.getCommerceOrderId());

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		cartComment.setId(() -> commentId);

		return _addOrUpdateOrderNote(commerceOrder, cartComment);
	}

	@Override
	public CartComment putCartCommentByExternalReferenceCode(
			String externalReferenceCode, CartComment cartComment)
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

		return putCartComment(
			commerceOrderNote.getCommerceOrderNoteId(), cartComment);
	}

	private CartComment _addOrUpdateOrderNote(
			CommerceOrder commerceOrder, CartComment cartComment)
		throws Exception {

		CommerceOrderNote commerceOrderNote =
			_commerceOrderNoteService.addOrUpdateCommerceOrderNote(
				null, GetterUtil.get(cartComment.getId(), 0L),
				commerceOrder.getCommerceOrderId(), cartComment.getContent(),
				GetterUtil.get(cartComment.getRestricted(), false),
				_serviceContextHelper.getServiceContext(
					commerceOrder.getGroupId()));

		return _toOrderNote(commerceOrderNote.getCommerceOrderNoteId());
	}

	private CartComment _toOrderNote(Long commerceOrderNoteId)
		throws Exception {

		return _noteDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceOrderNoteId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<CartComment> _toOrderNotes(
			List<CommerceOrderNote> commerceOrderNotes)
		throws Exception {

		return transform(
			commerceOrderNotes,
			commerceOrderNote -> _toOrderNote(
				commerceOrderNote.getCommerceOrderNoteId()));
	}

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderNoteService _commerceOrderNoteService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder> _modelResourcePermission;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter.NoteDTOConverter)"
	)
	private DTOConverter<CommerceOrderNote, CartComment> _noteDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}