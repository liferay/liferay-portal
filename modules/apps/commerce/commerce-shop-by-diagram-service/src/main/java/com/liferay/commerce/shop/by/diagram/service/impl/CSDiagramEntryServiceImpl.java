/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.impl;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.base.CSDiagramEntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CSDiagramEntry"
	},
	service = AopService.class
)
public class CSDiagramEntryServiceImpl extends CSDiagramEntryServiceBaseImpl {

	@Override
	public CSDiagramEntry addCSDiagramEntry(
			long cpDefinitionId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			ServiceContext serviceContext)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramEntryLocalService.addCSDiagramEntry(
			getUserId(), cpDefinitionId, cpInstanceId, cProductId, diagram,
			quantity, sequence, sku, serviceContext);
	}

	@Override
	public void deleteCSDiagramEntries(long cpDefinitionId)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		csDiagramEntryLocalService.deleteCSDiagramEntries(cpDefinitionId);
	}

	@Override
	public void deleteCSDiagramEntry(CSDiagramEntry csDiagramEntry)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), csDiagramEntry.getCPDefinitionId(),
			ActionKeys.UPDATE);

		csDiagramEntryLocalService.deleteCSDiagramEntry(csDiagramEntry);
	}

	@Override
	public CSDiagramEntry fetchCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramEntryPersistence.fetchByCPDI_S(
			cpDefinitionId, sequence);
	}

	@Override
	public List<CSDiagramEntry> getCProductCSDiagramEntries(
			long cProductId, int start, int end,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws PortalException {

		CProduct cProduct = _cProductLocalService.getCProduct(cProductId);

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cProduct.getPublishedCPDefinitionId(),
			ActionKeys.VIEW);

		return csDiagramEntryPersistence.findByCProductId(
			cProductId, start, end, orderByComparator);
	}

	@Override
	public int getCProductCSDiagramEntriesCount(long cProductId)
		throws PortalException {

		CProduct cProduct = _cProductLocalService.getCProduct(cProductId);

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cProduct.getPublishedCPDefinitionId(),
			ActionKeys.VIEW);

		return csDiagramEntryPersistence.countByCProductId(cProductId);
	}

	@Override
	public List<CSDiagramEntry> getCSDiagramEntries(
			long cpDefinitionId, int start, int end)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramEntryPersistence.findByCPDefinitionId(
			cpDefinitionId, start, end);
	}

	@Override
	public int getCSDiagramEntriesCount(long cpDefinitionId)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramEntryPersistence.countByCPDefinitionId(cpDefinitionId);
	}

	@Override
	public CSDiagramEntry getCSDiagramEntry(long csDiagramEntryId)
		throws PortalException {

		CSDiagramEntry csDiagramEntry =
			csDiagramEntryPersistence.findByPrimaryKey(csDiagramEntryId);

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), csDiagramEntry.getCPDefinitionId(),
			ActionKeys.UPDATE);

		return csDiagramEntry;
	}

	@Override
	public CSDiagramEntry getCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramEntryLocalService.getCSDiagramEntry(
			cpDefinitionId, sequence);
	}

	@Override
	public CSDiagramEntry updateCSDiagramEntry(
			long csDiagramEntryId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			ServiceContext serviceContext)
		throws PortalException {

		CSDiagramEntry csDiagramEntry =
			csDiagramEntryPersistence.findByPrimaryKey(csDiagramEntryId);

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), csDiagramEntry.getCPDefinitionId(),
			ActionKeys.UPDATE);

		return csDiagramEntryLocalService.updateCSDiagramEntry(
			csDiagramEntryId, cpInstanceId, cProductId, diagram, quantity,
			sequence, sku, serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPDefinition)"
	)
	private ModelResourcePermission<CPDefinition>
		_cpDefinitionModelResourcePermission;

	@Reference
	private CProductLocalService _cProductLocalService;

}