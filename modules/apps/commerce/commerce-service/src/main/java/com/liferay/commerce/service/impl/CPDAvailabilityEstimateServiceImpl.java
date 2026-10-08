/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.model.CPDAvailabilityEstimate;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.service.base.CPDAvailabilityEstimateServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPDAvailabilityEstimate"
	},
	service = AopService.class
)
public class CPDAvailabilityEstimateServiceImpl
	extends CPDAvailabilityEstimateServiceBaseImpl {

	@Override
	public CPDAvailabilityEstimate fetchCPDAvailabilityEstimateByCPDefinitionId(
			long cpDefinitionId)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		CPDefinition cpDefinition = cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		return cpdAvailabilityEstimateLocalService.
			fetchCPDAvailabilityEstimateByCProductId(
				cpDefinition.getCProductId());
	}

	@Override
	public CPDAvailabilityEstimate updateCPDAvailabilityEstimate(
			long cpdAvailabilityEstimateId, long cpDefinitionId,
			long commerceAvailabilityEstimateId)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.UPDATE);

		CPDefinition cpDefinition = cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		return cpdAvailabilityEstimateLocalService.
			updateCPDAvailabilityEstimateByCProductId(
				getUserId(), cpdAvailabilityEstimateId,
				cpDefinition.getCProductId(), commerceAvailabilityEstimateId);
	}

	@Reference
	protected CommerceCatalogLocalService commerceCatalogLocalService;

	@Reference
	protected CPDefinitionLocalService cpDefinitionLocalService;

	private void _checkCommerceCatalog(long cpDefinitionId, String actionId)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionLocalService.fetchCPDefinition(
			cpDefinitionId);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException();
		}

		CommerceCatalog commerceCatalog =
			commerceCatalogLocalService.fetchCommerceCatalogByGroupId(
				cpDefinition.getGroupId());

		if (commerceCatalog == null) {
			throw new PrincipalException();
		}

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalog, actionId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

}