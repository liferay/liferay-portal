/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.base.CPOptionServiceBaseImpl;
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

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPOption"
	},
	service = AopService.class
)
public class CPOptionServiceImpl extends CPOptionServiceBaseImpl {

	@Override
	public CPOption addCPOption(
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key, ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_cpOptionModelResourcePermission.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CPActionKeys.ADD_COMMERCE_PRODUCT_OPTION);

		return cpOptionLocalService.addCPOption(
			null, getUserId(), nameMap, descriptionMap, commerceOptionTypeKey,
			facetable, required, skuContributor, key, serviceContext);
	}

	@Override
	public CPOption addOrUpdateCPOption(
			String externalReferenceCode, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String commerceOptionTypeKey,
			boolean facetable, boolean required, boolean skuContributor,
			String key, ServiceContext serviceContext)
		throws PortalException {

		CPOption cpOption = cpOptionPersistence.fetchByERC_C(
			externalReferenceCode, serviceContext.getCompanyId());

		if (cpOption == null) {
			PortletResourcePermission portletResourcePermission =
				_cpOptionModelResourcePermission.getPortletResourcePermission();

			portletResourcePermission.check(
				getPermissionChecker(), null,
				CPActionKeys.ADD_COMMERCE_PRODUCT_OPTION);
		}

		return cpOptionLocalService.addOrUpdateCPOption(
			externalReferenceCode, getUserId(), nameMap, descriptionMap,
			commerceOptionTypeKey, facetable, required, skuContributor, key,
			serviceContext);
	}

	@Override
	public void deleteCPOption(long cpOptionId) throws PortalException {
		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionId, ActionKeys.DELETE);

		cpOptionLocalService.deleteCPOption(cpOptionId);
	}

	@Override
	public CPOption fetchCPOption(long cpOptionId) throws PortalException {
		CPOption cpOption = cpOptionPersistence.fetchByPrimaryKey(cpOptionId);

		if (cpOption != null) {
			_cpOptionModelResourcePermission.check(
				getPermissionChecker(), cpOption, ActionKeys.VIEW);
		}

		return cpOption;
	}

	@Override
	public CPOption fetchCPOption(long companyId, String key)
		throws PortalException {

		CPOption cpOption = cpOptionPersistence.fetchByC_K(companyId, key);

		if (cpOption != null) {
			_cpOptionModelResourcePermission.check(
				getPermissionChecker(), cpOption, ActionKeys.VIEW);
		}

		return cpOption;
	}

	@Override
	public CPOption fetchCPOptionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CPOption cpOption = cpOptionPersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (cpOption != null) {
			_cpOptionModelResourcePermission.check(
				getPermissionChecker(), cpOption, ActionKeys.VIEW);
		}

		return cpOption;
	}

	@Override
	public List<CPOption> findCPOptionByCompanyId(
			long companyId, int start, int end,
			OrderByComparator<CPOption> orderByComparator)
		throws PortalException {

		return cpOptionPersistence.filterFindByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public CPOption getCPOption(long cpOptionId) throws PortalException {
		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionId, ActionKeys.VIEW);

		return cpOptionPersistence.findByPrimaryKey(cpOptionId);
	}

	@Override
	public BaseModelSearchResult<CPOption> searchCPOptions(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		return cpOptionLocalService.searchCPOptions(
			companyId, keywords, start, end, sort);
	}

	@Override
	public CPOption updateCPOption(
			long cpOptionId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String commerceOptionTypeKey,
			boolean facetable, boolean required, boolean skuContributor,
			String key, ServiceContext serviceContext)
		throws PortalException {

		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionId, ActionKeys.UPDATE);

		return cpOptionLocalService.updateCPOption(
			cpOptionId, nameMap, descriptionMap, commerceOptionTypeKey,
			facetable, required, skuContributor, key, serviceContext);
	}

	@Override
	public CPOption updateCPOptionExternalReferenceCode(
			String externalReferenceCode, long cpOptionId)
		throws PortalException {

		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionId, ActionKeys.UPDATE);

		return cpOptionLocalService.updateCPOptionExternalReferenceCode(
			externalReferenceCode, cpOptionId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPOption)"
	)
	private ModelResourcePermission<CPOption> _cpOptionModelResourcePermission;

}