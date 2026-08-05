/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.base.CPTaxCategoryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPTaxCategory"
	},
	service = AopService.class
)
public class CPTaxCategoryServiceImpl extends CPTaxCategoryServiceBaseImpl {

	@Override
	public CPTaxCategory addCPTaxCategory(
			String externalReferenceCode, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CPActionKeys.ADD_COMMERCE_PRODUCT_TAX_CATEGORY);

		return cpTaxCategoryLocalService.addCPTaxCategory(
			externalReferenceCode, nameMap, descriptionMap, serviceContext);
	}

	@Override
	public int countCPTaxCategoriesByCompanyId(long companyId, String keyword)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CPActionKeys.VIEW_COMMERCE_PRODUCT_TAX_CATEGORIES);

		return cpTaxCategoryLocalService.countCPTaxCategoriesByCompanyId(
			companyId, keyword);
	}

	@Override
	public void deleteCPTaxCategory(long cpTaxCategoryId)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), cpTaxCategoryId, ActionKeys.DELETE);

		cpTaxCategoryLocalService.deleteCPTaxCategory(cpTaxCategoryId);
	}

	@Override
	public CPTaxCategory fetchCPTaxCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CPTaxCategory cpTaxCategory =
			cpTaxCategoryLocalService.fetchCPTaxCategoryByExternalReferenceCode(
				externalReferenceCode, companyId);

		if (cpTaxCategory != null) {
			_modelResourcePermission.check(
				getPermissionChecker(), cpTaxCategory, ActionKeys.VIEW);
		}

		return cpTaxCategory;
	}

	@Override
	public List<CPTaxCategory> findCPTaxCategoriesByCompanyId(
			long companyId, String keyword, int start, int end)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CPActionKeys.VIEW_COMMERCE_PRODUCT_TAX_CATEGORIES);

		return cpTaxCategoryLocalService.findCPTaxCategoriesByCompanyId(
			companyId, keyword, start, end);
	}

	@Override
	public List<CPTaxCategory> getCPTaxCategories(long companyId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CPActionKeys.VIEW_COMMERCE_PRODUCT_TAX_CATEGORIES);

		return cpTaxCategoryPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<CPTaxCategory> getCPTaxCategories(
			long companyId, int start, int end,
			OrderByComparator<CPTaxCategory> orderByComparator)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CPActionKeys.VIEW_COMMERCE_PRODUCT_TAX_CATEGORIES);

		return cpTaxCategoryPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCPTaxCategoriesCount(long companyId) throws PortalException {
		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CPActionKeys.VIEW_COMMERCE_PRODUCT_TAX_CATEGORIES);

		return cpTaxCategoryPersistence.countByCompanyId(companyId);
	}

	@Override
	public CPTaxCategory getCPTaxCategory(long cpTaxCategoryId)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), cpTaxCategoryId, ActionKeys.VIEW);

		return cpTaxCategoryPersistence.findByPrimaryKey(cpTaxCategoryId);
	}

	@Override
	public CPTaxCategory getCPTaxCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CPTaxCategory cpTaxCategory =
			cpTaxCategoryLocalService.getCPTaxCategoryByExternalReferenceCode(
				externalReferenceCode, companyId);

		_modelResourcePermission.check(
			getPermissionChecker(), cpTaxCategory, ActionKeys.VIEW);

		return cpTaxCategory;
	}

	@Override
	public BaseModelSearchResult<CPTaxCategory> searchCPTaxCategories(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			CPActionKeys.VIEW_COMMERCE_PRODUCT_TAX_CATEGORIES);

		return cpTaxCategoryLocalService.searchCPTaxCategories(
			companyId, keywords, start, end, sort);
	}

	@Override
	public CPTaxCategory updateCPTaxCategory(
			String externalReferenceCode, long cpTaxCategoryId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), cpTaxCategoryId, ActionKeys.UPDATE);

		return cpTaxCategoryLocalService.updateCPTaxCategory(
			externalReferenceCode, cpTaxCategoryId, nameMap, descriptionMap);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.commerce.product.model.CPTaxCategory)"
	)
	private volatile ModelResourcePermission<CPTaxCategory>
		_modelResourcePermission;

	@Reference(target = "(resource.name=" + CPConstants.RESOURCE_NAME_TAX + ")")
	private PortletResourcePermission _portletResourcePermission;

}