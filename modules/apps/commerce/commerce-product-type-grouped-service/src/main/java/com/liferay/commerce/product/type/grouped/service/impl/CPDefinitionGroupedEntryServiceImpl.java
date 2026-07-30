/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.service.impl;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.service.base.CPDefinitionGroupedEntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPDefinitionGroupedEntry"
	},
	service = AopService.class
)
public class CPDefinitionGroupedEntryServiceImpl
	extends CPDefinitionGroupedEntryServiceBaseImpl {

	@Override
	public void addCPDefinitionGroupedEntries(
			long cpDefinitionId, long[] entryCPDefinitionIds,
			ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.UPDATE);

		for (long entryCPDefinitionId : entryCPDefinitionIds) {
			_checkCommerceCatalog(entryCPDefinitionId, ActionKeys.VIEW);

			CPDefinition cpDefinition =
				cpDefinitionLocalService.getCPDefinition(entryCPDefinitionId);

			cpDefinitionGroupedEntryLocalService.addCPDefinitionGroupedEntry(
				cpDefinitionId, cpDefinition.getCProductId(), 0, 1,
				serviceContext);
		}
	}

	@Override
	public CPDefinitionGroupedEntry addCPDefinitionGroupedEntry(
			long cpDefinitionId, long entryCProductId, double priority,
			int quantity, ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.UPDATE);

		CProduct entryCProduct = cProductLocalService.getCProduct(
			entryCProductId);

		_checkCommerceCatalog(
			entryCProduct.getPublishedCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionGroupedEntryLocalService.addCPDefinitionGroupedEntry(
			cpDefinitionId, entryCProductId, priority, quantity,
			serviceContext);
	}

	@Override
	public CPDefinitionGroupedEntry deleteCPDefinitionGroupedEntry(
			long cpDefinitionGroupedEntryId)
		throws PortalException {

		CPDefinitionGroupedEntry cpDefinitionGroupedEntry =
			cpDefinitionGroupedEntryPersistence.findByPrimaryKey(
				cpDefinitionGroupedEntryId);

		_checkCommerceCatalog(
			cpDefinitionGroupedEntry.getCPDefinitionId(), ActionKeys.UPDATE);

		return cpDefinitionGroupedEntryLocalService.
			deleteCPDefinitionGroupedEntry(cpDefinitionGroupedEntry);
	}

	@Override
	public List<CPDefinitionGroupedEntry> getCPDefinitionGroupedEntries(
			long cpDefinitionId, int start, int end,
			OrderByComparator<CPDefinitionGroupedEntry> orderByComparator)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionGroupedEntryLocalService.
			getCPDefinitionGroupedEntries(
				cpDefinitionId, start, end, orderByComparator);
	}

	@Override
	public List<CPDefinitionGroupedEntry> getCPDefinitionGroupedEntries(
			long companyId, long cpDefinitionId, String keywords, int start,
			int end, Sort sort)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionGroupedEntryLocalService.
			getCPDefinitionGroupedEntries(
				companyId, cpDefinitionId, keywords, start, end, sort);
	}

	@Override
	public int getCPDefinitionGroupedEntriesCount(long cpDefinitionId)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionGroupedEntryLocalService.
			getCPDefinitionGroupedEntriesCount(cpDefinitionId);
	}

	@Override
	public int getCPDefinitionGroupedEntriesCount(
			long companyId, long cpDefinitionId, String keywords)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionGroupedEntryLocalService.
			getCPDefinitionGroupedEntriesCount(
				companyId, cpDefinitionId, keywords);
	}

	@Override
	public CPDefinitionGroupedEntry getCPDefinitionGroupedEntry(
			long cpDefinitionGroupedEntryId)
		throws PortalException {

		CPDefinitionGroupedEntry cpDefinitionGroupedEntry =
			cpDefinitionGroupedEntryPersistence.findByPrimaryKey(
				cpDefinitionGroupedEntryId);

		if (cpDefinitionGroupedEntry != null) {
			_checkCommerceCatalog(
				cpDefinitionGroupedEntry.getEntryCPDefinitionId(),
				ActionKeys.VIEW);
		}

		return cpDefinitionGroupedEntry;
	}

	@Override
	public List<CPDefinitionGroupedEntry>
			getEntryCProductCPDefinitionGroupedEntries(
				long entryCProductId, int start, int end,
				OrderByComparator<CPDefinitionGroupedEntry> orderByComparator)
		throws PortalException {

		CProduct cProduct = cProductLocalService.getCProduct(entryCProductId);

		_checkCommerceCatalog(
			cProduct.getPublishedCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionGroupedEntryPersistence.findByEntryCProductId(
			entryCProductId, start, end, orderByComparator);
	}

	@Override
	public int getEntryCProductCPDefinitionGroupedEntriesCount(
			long entryCProductId)
		throws PortalException {

		CProduct cProduct = cProductLocalService.getCProduct(entryCProductId);

		_checkCommerceCatalog(
			cProduct.getPublishedCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionGroupedEntryPersistence.countByEntryCProductId(
			entryCProductId);
	}

	@Override
	public CPDefinitionGroupedEntry updateCPDefinitionGroupedEntry(
			long cpDefinitionGroupedEntryId, double priority, int quantity)
		throws PortalException {

		CPDefinitionGroupedEntry cpDefinitionGroupedEntry =
			cpDefinitionGroupedEntryPersistence.findByPrimaryKey(
				cpDefinitionGroupedEntryId);

		_checkCommerceCatalog(
			cpDefinitionGroupedEntry.getEntryCPDefinitionId(),
			ActionKeys.UPDATE);

		return cpDefinitionGroupedEntryLocalService.
			updateCPDefinitionGroupedEntry(
				cpDefinitionGroupedEntryId, priority, quantity);
	}

	@Reference
	protected CommerceCatalogLocalService commerceCatalogLocalService;

	@Reference
	protected CPDefinitionLocalService cpDefinitionLocalService;

	@Reference
	protected CProductLocalService cProductLocalService;

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