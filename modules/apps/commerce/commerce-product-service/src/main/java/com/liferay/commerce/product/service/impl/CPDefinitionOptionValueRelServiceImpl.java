/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.base.CPDefinitionOptionValueRelServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Igor Beslic
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPDefinitionOptionValueRel"
	},
	service = AopService.class
)
public class CPDefinitionOptionValueRelServiceImpl
	extends CPDefinitionOptionValueRelServiceBaseImpl {

	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, long cpInstanceId, String key,
			Map<Locale, String> nameMap, boolean preselected,
			BigDecimal deltaPrice, double priority, BigDecimal quantity,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionValueRelLocalService.
			addCPDefinitionOptionValueRel(
				cpDefinitionOptionRelId, cpInstanceId, key, nameMap,
				preselected, deltaPrice, priority, quantity, unitOfMeasureKey,
				serviceContext);
	}

	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, String key,
			Map<Locale, String> nameMap, double priority,
			ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionValueRelLocalService.
			addCPDefinitionOptionValueRel(
				cpDefinitionOptionRelId, key, nameMap, priority,
				serviceContext);
	}

	@Override
	public CPDefinitionOptionValueRel deleteCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionValueRelLocalService.
			deleteCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);
	}

	@Override
	public CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelLocalService.
				fetchCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);

		if (cpDefinitionOptionValueRel != null) {
			CPDefinitionOptionRel cpDefinitionOptionRel =
				_cpDefinitionOptionRelPersistence.findByPrimaryKey(
					cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

			_checkCommerceCatalog(
				cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);
		}

		return cpDefinitionOptionValueRel;
	}

	@Override
	public CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, String key)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelLocalService.
				fetchCPDefinitionOptionValueRel(cpDefinitionOptionRelId, key);

		if (cpDefinitionOptionValueRel != null) {
			CPDefinitionOptionRel cpDefinitionOptionRel =
				_cpDefinitionOptionRelPersistence.findByPrimaryKey(
					cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

			_checkCommerceCatalog(
				cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);
		}

		return cpDefinitionOptionValueRel;
	}

	@Override
	public CPDefinitionOptionValueRel
			fetchCPDefinitionOptionValueRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelLocalService.
				fetchCPDefinitionOptionValueRelByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (cpDefinitionOptionValueRel != null) {
			CPDefinitionOptionRel cpDefinitionOptionRel =
				_cpDefinitionOptionRelPersistence.findByPrimaryKey(
					cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

			_checkCommerceCatalog(
				cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);
		}

		return cpDefinitionOptionValueRel;
	}

	@Override
	public CPDefinitionOptionValueRel getCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);
	}

	@Override
	public List<CPDefinitionOptionValueRel> getCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId, int start, int end)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRels(cpDefinitionOptionRelId, start, end);
	}

	@Override
	public List<CPDefinitionOptionValueRel> getCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId, int start, int end,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRels(
				cpDefinitionOptionRelId, start, end, orderByComparator);
	}

	@Override
	public List<CPDefinitionOptionValueRel> getCPDefinitionOptionValueRels(
			long groupId, String key, int start, int end)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(groupId);

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalog, ActionKeys.VIEW);

		return cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRels(key, start, end);
	}

	@Override
	public int getCPDefinitionOptionValueRelsCount(long cpDefinitionOptionRelId)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRelsCount(cpDefinitionOptionRelId);
	}

	@Override
	public CPDefinitionOptionValueRel resetCPInstanceCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.UPDATE);

		return cpDefinitionOptionValueRelLocalService.
			resetCPInstanceCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRelId);
	}

	@Override
	public BaseModelSearchResult<CPDefinitionOptionValueRel>
			searchCPDefinitionOptionValueRels(
				long companyId, long groupId, long cpDefinitionOptionRelId,
				String keywords, int start, int end, Sort[] sorts)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionValueRelLocalService.
			searchCPDefinitionOptionValueRels(
				companyId, groupId, cpDefinitionOptionRelId, keywords, start,
				end, sorts);
	}

	@Override
	public int searchCPDefinitionOptionValueRelsCount(
			long companyId, long groupId, long cpDefinitionOptionRelId,
			String keywords)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionValueRelLocalService.
			searchCPDefinitionOptionValueRelsCount(
				companyId, groupId, cpDefinitionOptionRelId, keywords);
	}

	@Override
	public CPDefinitionOptionValueRel updateCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId, long cpInstanceId, String key,
			Map<Locale, String> nameMap, boolean preselected, BigDecimal price,
			double priority, BigDecimal quantity, String unitOfMeasureKey,
			ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.UPDATE);

		return cpDefinitionOptionValueRelLocalService.
			updateCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRelId, cpInstanceId, key, nameMap,
				preselected, price, priority, quantity, unitOfMeasureKey,
				serviceContext);
	}

	@Override
	public CPDefinitionOptionValueRel
			updateCPDefinitionOptionValueRelPreselected(
				long cpDefinitionOptionValueRelId, boolean preselected)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.UPDATE);

		return cpDefinitionOptionValueRelLocalService.
			updateCPDefinitionOptionValueRelPreselected(
				cpDefinitionOptionValueRelId, preselected);
	}

	@Override
	public CPDefinitionOptionValueRel updateExternalReferenceCode(
			long cpDefinitionOptionValueRelId, String externalReferenceCode)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.UPDATE);

		return cpDefinitionOptionValueRelLocalService.
			updateExternalReferenceCode(
				cpDefinitionOptionValueRelId, externalReferenceCode);
	}

	private void _checkCommerceCatalog(long cpDefinitionId, String actionId)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionPersistence.fetchByPrimaryKey(
			cpDefinitionId);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException();
		}

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(
				cpDefinition.getGroupId());

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
	private CPDefinitionOptionRelPersistence _cpDefinitionOptionRelPersistence;

	@Reference
	private CPDefinitionPersistence _cpDefinitionPersistence;

}