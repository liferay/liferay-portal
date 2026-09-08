/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.COREntryService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRule;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Status;
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
		"dto.class.name=com.liferay.commerce.order.rule.model.COREntry"
	},
	service = DTOConverter.class
)
public class OrderRuleDTOConverter
	implements DTOConverter<COREntry, OrderRule> {

	@Override
	public String getContentType() {
		return OrderRule.class.getSimpleName();
	}

	@Override
	public OrderRule toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		COREntry corEntry = _corEntryService.getCOREntry(
			(Long)dtoConverterContext.getId());

		return new OrderRule() {
			{
				setActions(dtoConverterContext::getActions);
				setActive(corEntry::isActive);
				setDescription(corEntry::getDescription);
				setDisplayDate(corEntry::getDisplayDate);
				setExpirationDate(corEntry::getExpirationDate);
				setExternalReferenceCode(corEntry::getExternalReferenceCode);
				setId(corEntry::getCOREntryId);
				setName(corEntry::getName);
				setType(corEntry::getType);
				setTypeSettings(corEntry::getTypeSettings);
				setWorkflowStatusInfo(
					() -> _toStatus(
						WorkflowConstants.getStatusLabel(corEntry.getStatus()),
						_language.get(
							LanguageResources.getResourceBundle(
								dtoConverterContext.getLocale()),
							WorkflowConstants.getStatusLabel(
								corEntry.getStatus())),
						corEntry.getStatus()));
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
	private COREntryService _corEntryService;

	@Reference
	private Language _language;

}