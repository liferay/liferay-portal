/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.base.CPConfigurationEntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPConfigurationEntry"
	},
	service = AopService.class
)
public class CPConfigurationEntryServiceImpl
	extends CPConfigurationEntryServiceBaseImpl {

	@Override
	public CPConfigurationEntry addCPConfigurationEntry(
			String externalReferenceCode, long groupId, long classNameId,
			long classPK, long cpConfigurationListId, long cpTaxCategoryId,
			String allowedOrderQuantities, boolean backOrders,
			long commerceAvailabilityEstimateId,
			String cpDefinitionInventoryEngine, double depth,
			boolean displayAvailability, boolean displayStockQuantity,
			boolean freeShipping, double height, String lowStockActivity,
			BigDecimal maxOrderQuantity, BigDecimal minOrderQuantity,
			BigDecimal minStockQuantity, BigDecimal multipleOrderQuantity,
			boolean purchasable, boolean shippable, double shippingExtraPrice,
			boolean shipSeparately, boolean taxExempt, double weight,
			double width)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.UPDATE);

		return cpConfigurationEntryLocalService.addCPConfigurationEntry(
			externalReferenceCode, getUserId(), groupId, classNameId, classPK,
			cpConfigurationListId, cpTaxCategoryId, allowedOrderQuantities,
			backOrders, commerceAvailabilityEstimateId,
			cpDefinitionInventoryEngine, depth, displayAvailability,
			displayStockQuantity, freeShipping, height, lowStockActivity,
			maxOrderQuantity, minOrderQuantity, minStockQuantity,
			multipleOrderQuantity, purchasable, shippable, shippingExtraPrice,
			shipSeparately, taxExempt, weight, width);
	}

	@Override
	public void deleteCPConfigurationEntry(long cpConfigurationEntryId)
		throws PortalException {

		CPConfigurationEntry cpConfigurationEntry =
			cpConfigurationEntryLocalService.getCPConfigurationEntry(
				cpConfigurationEntryId);

		_checkCommerceCatalog(
			cpConfigurationEntry.getGroupId(), ActionKeys.UPDATE);

		cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			cpConfigurationEntry);
	}

	@Override
	public CPConfigurationEntry getCPConfigurationEntry(
			long cpConfigurationEntryId)
		throws PortalException {

		CPConfigurationEntry cpConfigurationEntry =
			cpConfigurationEntryLocalService.getCPConfigurationEntry(
				cpConfigurationEntryId);

		_checkCommerceCatalog(
			cpConfigurationEntry.getGroupId(), ActionKeys.VIEW);

		return cpConfigurationEntry;
	}

	@Override
	public CPConfigurationEntry getCPConfigurationEntry(
			long classNameId, long classPK, long cpConfigurationListId)
		throws PortalException {

		CPConfigurationEntry cpConfigurationEntry =
			cpConfigurationEntryLocalService.getCPConfigurationEntry(
				classNameId, classPK, cpConfigurationListId);

		_checkCommerceCatalog(
			cpConfigurationEntry.getGroupId(), ActionKeys.VIEW);

		return cpConfigurationEntry;
	}

	@Override
	public CPConfigurationEntry getCPConfigurationEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CPConfigurationEntry cpConfigurationEntry =
			cpConfigurationEntryLocalService.
				getCPConfigurationEntryByExternalReferenceCode(
					externalReferenceCode, companyId);

		_checkCommerceCatalog(
			cpConfigurationEntry.getGroupId(), ActionKeys.VIEW);

		return cpConfigurationEntry;
	}

	@Override
	public CPConfigurationEntry updateCPConfigurationEntry(
			String externalReferenceCode, long cpConfigurationEntryId,
			long cpTaxCategoryId, String allowedOrderQuantities,
			boolean backOrders, long commerceAvailabilityEstimateId,
			String cpDefinitionInventoryEngine, double depth,
			boolean displayAvailability, boolean displayStockQuantity,
			boolean freeShipping, double height, String lowStockActivity,
			BigDecimal maxOrderQuantity, BigDecimal minOrderQuantity,
			BigDecimal minStockQuantity, BigDecimal multipleOrderQuantity,
			boolean purchasable, boolean shippable, double shippingExtraPrice,
			boolean shipSeparately, boolean taxExempt, double weight,
			double width)
		throws PortalException {

		CPConfigurationEntry cpConfigurationEntry =
			cpConfigurationEntryLocalService.getCPConfigurationEntry(
				cpConfigurationEntryId);

		_checkCommerceCatalog(
			cpConfigurationEntry.getGroupId(), ActionKeys.UPDATE);

		return cpConfigurationEntryLocalService.updateCPConfigurationEntry(
			externalReferenceCode, cpConfigurationEntryId, cpTaxCategoryId,
			allowedOrderQuantities, backOrders, commerceAvailabilityEstimateId,
			cpDefinitionInventoryEngine, depth, displayAvailability,
			displayStockQuantity, freeShipping, height, lowStockActivity,
			maxOrderQuantity, minOrderQuantity, minStockQuantity,
			multipleOrderQuantity, purchasable, shippable, shippingExtraPrice,
			shipSeparately, taxExempt, weight, width);
	}

	private void _checkCommerceCatalog(long groupId, String actionId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(groupId);

		if (commerceCatalog == null) {
			throw new PrincipalException();
		}

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalog, actionId);
	}

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

}