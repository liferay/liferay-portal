/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.service.CPDefinitionLinkService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.RelatedProduct;
import com.liferay.headless.commerce.admin.catalog.internal.util.DateConfigUtil;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Calendar;

/**
 * @author Alessio Antonio Rendina
 */
public class RelatedProductUtil {

	public static CPDefinitionLink addOrUpdateCPDefinitionLink(
			CPDefinitionLinkService cpDefinitionLinkService,
			CPDefinitionService cpDefinitionService,
			RelatedProduct relatedProduct, long cpDefinitionId,
			ServiceContext serviceContext)
		throws PortalException {

		CPDefinition cpDefinition = null;

		if (Validator.isNotNull(
				relatedProduct.getProductExternalReferenceCode())) {

			cpDefinition =
				ProductUtil.getCPDefinitionByCProductExternalReferenceCode(
					serviceContext.getCompanyId(), cpDefinitionService,
					relatedProduct.getProductExternalReferenceCode(),
					serviceContext.getScopeGroupId(),
					relatedProduct.getProductType());
		}
		else {
			cpDefinition = cpDefinitionService.fetchCPDefinitionByCProductId(
				relatedProduct.getProductId(), false);

			if (cpDefinition == null) {
				throw new NoSuchCPDefinitionException(
					"Unable to find product with ID " +
						relatedProduct.getProductId());
			}
		}

		CPDefinitionLink cpDefinitionLink =
			cpDefinitionLinkService.fetchCPDefinitionLink(
				cpDefinitionId, cpDefinition.getCProductId(),
				relatedProduct.getType());

		if (relatedProduct.getId() != null) {
			cpDefinitionLink = cpDefinitionLinkService.fetchCPDefinitionLink(
				relatedProduct.getId());
		}

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		DateConfig displayDateConfig = new DateConfig(displayCalendar);

		if (cpDefinitionLink == null) {
			return cpDefinitionLinkService.addCPDefinitionLink(
				cpDefinitionId, cpDefinition.getCProductId(),
				displayDateConfig.getMonth(), displayDateConfig.getDay(),
				displayDateConfig.getYear(), displayDateConfig.getHour(),
				displayDateConfig.getMinute(), 0, 0, 0, 0, 0, true,
				GetterUtil.get(relatedProduct.getPriority(), 0D),
				relatedProduct.getType(), serviceContext);
		}

		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		boolean neverExpire = true;

		if (cpDefinitionLink.getExpirationDate() != null) {
			expirationCalendar = DateConfigUtil.convertDateToCalendar(
				cpDefinitionLink.getExpirationDate());

			neverExpire = false;
		}

		DateConfig expirationDateConfig = new DateConfig(expirationCalendar);

		return cpDefinitionLinkService.updateCPDefinitionLink(
			cpDefinitionLink.getCPDefinitionLinkId(),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			neverExpire,
			GetterUtil.get(
				relatedProduct.getPriority(), cpDefinitionLink.getPriority()),
			serviceContext);
	}

}