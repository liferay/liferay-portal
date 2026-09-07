/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderType;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Status;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.model.CommerceOrderType"
	},
	service = DTOConverter.class
)
public class OrderTypeDTOConverter
	implements DTOConverter<CommerceOrderType, OrderType> {

	@Override
	public String getContentType() {
		return OrderType.class.getSimpleName();
	}

	@Override
	public OrderType toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.getCommerceOrderType(
				(Long)dtoConverterContext.getId());

		return new OrderType() {
			{
				setActions(dtoConverterContext::getActions);
				setActive(commerceOrderType::isActive);
				setCustomFields(
					() -> {
						ExpandoBridge expandoBridge =
							commerceOrderType.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						commerceOrderType.getDescriptionMap()));
				setDisplayDate(commerceOrderType::getDisplayDate);
				setDisplayOrder(commerceOrderType::getDisplayOrder);
				setExpirationDate(commerceOrderType::getExpirationDate);
				setExternalReferenceCode(
					commerceOrderType::getExternalReferenceCode);
				setId(commerceOrderType::getCommerceOrderTypeId);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						commerceOrderType.getNameMap()));
				setWorkflowStatusInfo(
					() -> _toStatus(
						WorkflowConstants.getStatusLabel(
							commerceOrderType.getStatus()),
						_language.get(
							LanguageResources.getResourceBundle(
								dtoConverterContext.getLocale()),
							WorkflowConstants.getStatusLabel(
								commerceOrderType.getStatus())),
						commerceOrderType.getStatus()));
			}
		};
	}

	private Status _toStatus(
		String orderTypeStatusLabel, String orderTypeStatusLabelI18n,
		int statusCode) {

		return new Status() {
			{
				setCode(() -> statusCode);
				setLabel(() -> orderTypeStatusLabel);
				setLabel_i18n(() -> orderTypeStatusLabelI18n);
			}
		};
	}

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private Language _language;

}