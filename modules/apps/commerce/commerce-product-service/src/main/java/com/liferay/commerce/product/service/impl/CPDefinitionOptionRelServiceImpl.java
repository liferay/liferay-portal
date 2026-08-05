/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.base.CPDefinitionOptionRelServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionPersistence;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

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
		"json.web.service.context.path=CPDefinitionOptionRel"
	},
	service = AopService.class
)
public class CPDefinitionOptionRelServiceImpl
	extends CPDefinitionOptionRelServiceBaseImpl {

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String commerceOptionTypeKey,
			double priority, boolean facetable, boolean required,
			boolean skuContributor, boolean importOptionValue,
			ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, importOptionValue, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String commerceOptionTypeKey,
			String infoItemServiceKey, double priority,
			boolean definedExternally, boolean facetable, boolean required,
			boolean skuContributor, boolean importOptionValue, String priceType,
			String typeSettings, ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, infoItemServiceKey, priority,
			definedExternally, facetable, required, skuContributor,
			importOptionValue, priceType, typeSettings, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId, ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, true, serviceContext);
	}

	@Override
	public void deleteCPDefinitionOptionRel(long cpDefinitionOptionRelId)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.UPDATE);

		cpDefinitionOptionRelLocalService.deleteCPDefinitionOptionRel(
			cpDefinitionOptionRel);
	}

	@Override
	public CPDefinitionOptionRel fetchCPDefinitionOptionRel(
			long cpDefinitionOptionRelId)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRelPersistence.fetchByPrimaryKey(
				cpDefinitionOptionRelId);

		if (cpDefinitionOptionRel != null) {
			_checkCommerceCatalog(
				cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);
		}

		return cpDefinitionOptionRel;
	}

	@Override
	public CPDefinitionOptionRel fetchCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionOptionRelPersistence.fetchByC_C(
			cpDefinitionId, cpOptionId);
	}

	@Override
	public CPDefinitionOptionRel
			fetchCPDefinitionOptionRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRelLocalService.
				fetchCPDefinitionOptionRelByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (cpDefinitionOptionRel != null) {
			_checkCommerceCatalog(
				cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);
		}

		return cpDefinitionOptionRel;
	}

	@Override
	public CPDefinitionOptionRel getCPDefinitionOptionRel(
			long cpDefinitionOptionRelId)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionRel;
	}

	@Override
	public CPDefinitionOptionRel
			getCPDefinitionOptionRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRelLocalService.
				getCPDefinitionOptionRelByExternalReferenceCode(
					externalReferenceCode, companyId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionRel;
	}

	@Override
	public Map<Long, List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				long cpDefinitionId, String json)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				cpDefinitionId, json);
	}

	@Override
	public Map<String, List<String>>
			getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
				long cpInstanceId)
		throws PortalException {

		CPInstance cpInstance = _cpInstancePersistence.findByPrimaryKey(
			cpInstanceId);

		_checkCommerceCatalog(cpInstance.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
				cpInstanceId);
	}

	@Override
	public List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
			long cpDefinitionId, boolean skuContributor)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
			cpDefinitionId, skuContributor);
	}

	@Override
	public List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
			long cpDefinitionId, int start, int end)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionOptionRelPersistence.findByCPDefinitionId(
			cpDefinitionId, start, end);
	}

	@Override
	public List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
			long cpDefinitionId, int start, int end,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionOptionRelPersistence.findByCPDefinitionId(
			cpDefinitionId, start, end, orderByComparator);
	}

	@Override
	public int getCPDefinitionOptionRelsCount(long cpDefinitionId)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionOptionRelPersistence.countByCPDefinitionId(
			cpDefinitionId);
	}

	@Override
	public int getCPDefinitionOptionRelsCount(
			long cpDefinitionId, boolean skuContributor)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionOptionRelPersistence.countByC_SC(
			cpDefinitionId, skuContributor);
	}

	@Override
	public BaseModelSearchResult<CPDefinitionOptionRel>
			searchCPDefinitionOptionRels(
				long companyId, long groupId, long cpDefinitionId,
				String keywords, int start, int end, Sort[] sorts)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionOptionRelLocalService.searchCPDefinitionOptionRels(
			companyId, groupId, cpDefinitionId, keywords, start, end, sorts);
	}

	@Override
	public int searchCPDefinitionOptionRelsCount(
			long companyId, long groupId, long cpDefinitionId, String keywords)
		throws PortalException {

		_checkCommerceCatalog(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionOptionRelLocalService.
			searchCPDefinitionOptionRelsCount(
				companyId, groupId, cpDefinitionId, keywords);
	}

	@Override
	public CPDefinitionOptionRel updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, long cpOptionId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor,
			ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.UPDATE);

		return cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(), cpOptionId,
			nameMap, descriptionMap, commerceOptionTypeKey, priority, facetable,
			required, skuContributor, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, long cpOptionId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			String commerceOptionTypeKey, String infoItemServiceKey,
			double priority, boolean definedExternally, boolean facetable,
			boolean required, boolean skuContributor, String priceType,
			String typeSettings, ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.UPDATE);

		return cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(), cpOptionId,
			nameMap, descriptionMap, commerceOptionTypeKey, infoItemServiceKey,
			priority, definedExternally, facetable, required, skuContributor,
			priceType, typeSettings, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel updateExternalReferenceCode(
			long cpDefinitionOptionRelId, String externalReferenceCode)
		throws PortalException {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRelPersistence.findByPrimaryKey(
				cpDefinitionOptionRelId);

		_checkCommerceCatalog(
			cpDefinitionOptionRel.getCPDefinitionId(), ActionKeys.UPDATE);

		return cpDefinitionOptionRelLocalService.updateExternalReferenceCode(
			cpDefinitionOptionRelId, externalReferenceCode);
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
	private CPDefinitionPersistence _cpDefinitionPersistence;

	@Reference
	private CPInstancePersistence _cpInstancePersistence;

}