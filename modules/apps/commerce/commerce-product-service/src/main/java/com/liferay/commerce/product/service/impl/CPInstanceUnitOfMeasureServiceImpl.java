/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.base.CPInstanceUnitOfMeasureServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPInstanceUnitOfMeasure"
	},
	service = AopService.class
)
public class CPInstanceUnitOfMeasureServiceImpl
	extends CPInstanceUnitOfMeasureServiceBaseImpl {

	@Override
	public CPInstanceUnitOfMeasure addCPInstanceUnitOfMeasure(
			long cpInstanceId, boolean active,
			BigDecimal incrementalOrderQuantity, String key,
			Map<Locale, String> nameMap, int precision,
			BigDecimal pricingQuantity, boolean primary, double priority,
			BigDecimal rate, String sku)
		throws PortalException {

		_checkCommerceCatalog(cpInstanceId, ActionKeys.UPDATE);

		return cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
			getUserId(), cpInstanceId, active, incrementalOrderQuantity, key,
			nameMap, precision, pricingQuantity, primary, priority, rate, sku);
	}

	@Override
	public CPInstanceUnitOfMeasure addOrUpdateCPInstanceUnitOfMeasure(
			long cpInstanceId, boolean active,
			BigDecimal incrementalOrderQuantity, String key,
			Map<Locale, String> nameMap, int precision,
			BigDecimal pricingQuantity, boolean primary, double priority,
			BigDecimal rate, String sku)
		throws PortalException {

		_checkCommerceCatalog(cpInstanceId, ActionKeys.UPDATE);

		return cpInstanceUnitOfMeasureLocalService.
			addOrUpdateCPInstanceUnitOfMeasure(
				getUserId(), cpInstanceId, active, incrementalOrderQuantity,
				key, nameMap, precision, pricingQuantity, primary, priority,
				rate, sku);
	}

	@Override
	public CPInstanceUnitOfMeasure deleteCPInstanceUnitOfMeasure(
			long cpInstanceUnitOfMeasureId)
		throws PortalException {

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			cpInstanceUnitOfMeasurePersistence.findByPrimaryKey(
				cpInstanceUnitOfMeasureId);

		_checkCommerceCatalog(
			cpInstanceUnitOfMeasure.getCPInstanceId(), ActionKeys.UPDATE);

		return cpInstanceUnitOfMeasureLocalService.
			deleteCPInstanceUnitOfMeasure(cpInstanceUnitOfMeasure);
	}

	@Override
	public CPInstanceUnitOfMeasure fetchCPInstanceUnitOfMeasure(
			long cpInstanceUnitOfMeasureId)
		throws PortalException {

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			cpInstanceUnitOfMeasurePersistence.fetchByPrimaryKey(
				cpInstanceUnitOfMeasureId);

		if (cpInstanceUnitOfMeasure != null) {
			_checkCommerceCatalog(
				cpInstanceUnitOfMeasure.getCPInstanceId(), ActionKeys.VIEW);
		}

		return cpInstanceUnitOfMeasure;
	}

	@Override
	public CPInstanceUnitOfMeasure fetchCPInstanceUnitOfMeasure(
			long cpInstanceId, String key)
		throws PortalException {

		_checkCommerceCatalog(cpInstanceId, ActionKeys.VIEW);

		return cpInstanceUnitOfMeasurePersistence.fetchByC_K(cpInstanceId, key);
	}

	@Override
	public CPInstanceUnitOfMeasure fetchPrimaryCPInstanceUnitOfMeasure(
			long cpInstanceId)
		throws PortalException {

		_checkCommerceCatalog(cpInstanceId, ActionKeys.VIEW);

		return cpInstanceUnitOfMeasureLocalService.
			fetchPrimaryCPInstanceUnitOfMeasure(cpInstanceId);
	}

	@Override
	public List<CPInstanceUnitOfMeasure> getActiveCPInstanceUnitOfMeasures(
			long cpInstanceId)
		throws PortalException {

		_checkCommerceCatalog(cpInstanceId, ActionKeys.VIEW);

		return cpInstanceUnitOfMeasureLocalService.
			getActiveCPInstanceUnitOfMeasures(cpInstanceId);
	}

	@Override
	public int getActiveCPInstanceUnitOfMeasuresCount(long cpInstanceId)
		throws PortalException {

		_checkCommerceCatalog(cpInstanceId, ActionKeys.VIEW);

		return cpInstanceUnitOfMeasureLocalService.
			getActiveCPInstanceUnitOfMeasuresCount(cpInstanceId);
	}

	@Override
	public CPInstanceUnitOfMeasure getCPInstanceUnitOfMeasure(
			long cpInstanceUnitOfMeasureId)
		throws PortalException {

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			cpInstanceUnitOfMeasurePersistence.findByPrimaryKey(
				cpInstanceUnitOfMeasureId);

		_checkCommerceCatalog(
			cpInstanceUnitOfMeasure.getCPInstanceId(), ActionKeys.VIEW);

		return cpInstanceUnitOfMeasure;
	}

	@Override
	public CPInstanceUnitOfMeasure getCPInstanceUnitOfMeasure(
			long cpInstanceId, String key)
		throws PortalException {

		_checkCommerceCatalog(cpInstanceId, ActionKeys.VIEW);

		return cpInstanceUnitOfMeasurePersistence.findByC_K(cpInstanceId, key);
	}

	@Override
	public List<CPInstanceUnitOfMeasure> getCPInstanceUnitOfMeasures(
			long cpInstanceId, int start, int end,
			OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator)
		throws PortalException {

		_checkCommerceCatalog(cpInstanceId, ActionKeys.VIEW);

		return cpInstanceUnitOfMeasurePersistence.findByCPInstanceId(
			cpInstanceId, start, end, orderByComparator);
	}

	@Override
	public int getCPInstanceUnitOfMeasuresCount(long cpInstanceId)
		throws PortalException {

		_checkCommerceCatalog(cpInstanceId, ActionKeys.VIEW);

		return cpInstanceUnitOfMeasureLocalService.
			getCPInstanceUnitOfMeasuresCount(cpInstanceId);
	}

	@Override
	public CPInstanceUnitOfMeasure updateCPInstanceUnitOfMeasure(
			long cpInstanceUnitOfMeasureId, long cpInstanceId, boolean active,
			BigDecimal incrementalOrderQuantity, String key,
			Map<Locale, String> nameMap, int precision,
			BigDecimal pricingQuantity, boolean primary, double priority,
			BigDecimal rate, String sku)
		throws PortalException {

		_checkCommerceCatalog(cpInstanceId, ActionKeys.UPDATE);

		return cpInstanceUnitOfMeasureLocalService.
			updateCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasureId, cpInstanceId, active,
				incrementalOrderQuantity, key, nameMap, precision,
				pricingQuantity, primary, priority, rate, sku);
	}

	private void _checkCommerceCatalog(long cpInstanceId, String actionId)
		throws PortalException {

		CPInstance cpInstance = _cpInstancePersistence.findByPrimaryKey(
			cpInstanceId);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(
				cpInstance.getGroupId());

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

	@Reference
	private CPInstancePersistence _cpInstancePersistence;

}