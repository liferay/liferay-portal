/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.dto.v1_0.converter;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderNote;
import com.liferay.commerce.service.CommerceOrderNoteService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderComment;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderComment"
	},
	service = DTOConverter.class
)
public class PlacedOrderCommentDTOConverter
	implements DTOConverter<CommerceOrderNote, PlacedOrderComment> {

	@Override
	public String getContentType() {
		return PlacedOrderComment.class.getSimpleName();
	}

	@Override
	public PlacedOrderComment toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceOrderNote commerceOrderNote =
			_commerceOrderNoteService.getCommerceOrderNote(
				(Long)dtoConverterContext.getId());

		return new PlacedOrderComment() {
			{
				setAuthor(commerceOrderNote::getUserName);
				setContent(commerceOrderNote::getContent);
				setExternalReferenceCode(
					commerceOrderNote::getExternalReferenceCode);
				setId(commerceOrderNote::getCommerceOrderNoteId);
				setOrderId(
					() -> {
						CommerceOrder commerceOrder =
							_commerceOrderService.getCommerceOrder(
								commerceOrderNote.getCommerceOrderId());

						return commerceOrder.getCommerceOrderId();
					});
				setRestricted(commerceOrderNote::isRestricted);
			}
		};
	}

	@Reference
	private CommerceOrderNoteService _commerceOrderNoteService;

	@Reference
	private CommerceOrderService _commerceOrderService;

}