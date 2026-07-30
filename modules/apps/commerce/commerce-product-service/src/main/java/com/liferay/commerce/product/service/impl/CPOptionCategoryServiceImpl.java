/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.service.base.CPOptionCategoryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPOptionCategory"
	},
	service = AopService.class
)
public class CPOptionCategoryServiceImpl
	extends CPOptionCategoryServiceBaseImpl {

	@Override
	public CPOptionCategory addCPOptionCategory(
			String externalReferenceCode, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, double priority, String key,
			ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_cpOptionCategoryModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CPActionKeys.ADD_COMMERCE_PRODUCT_OPTION_CATEGORY);

		return cpOptionCategoryLocalService.addCPOptionCategory(
			externalReferenceCode, getUserId(), titleMap, descriptionMap,
			priority, key, serviceContext);
	}

	@Override
	public CPOptionCategory addOrUpdateCPOptionCategory(
			String externalReferenceCode, long cpOptionCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			double priority, String key, ServiceContext serviceContext)
		throws PortalException {

		CPOptionCategory cpOptionCategory =
			cpOptionCategoryLocalService.
				fetchCPOptionCategoryByExternalReferenceCode(
					externalReferenceCode, serviceContext.getCompanyId());

		if ((cpOptionCategory == null) && (cpOptionCategoryId > 0)) {
			cpOptionCategory = cpOptionCategoryPersistence.fetchByPrimaryKey(
				cpOptionCategoryId);
		}

		if (cpOptionCategory == null) {
			PortletResourcePermission portletResourcePermission =
				_cpOptionCategoryModelResourcePermission.
					getPortletResourcePermission();

			portletResourcePermission.check(
				getPermissionChecker(), null,
				CPActionKeys.ADD_COMMERCE_PRODUCT_OPTION_CATEGORY);
		}
		else {
			_cpOptionCategoryModelResourcePermission.check(
				getPermissionChecker(),
				cpOptionCategory.getCPOptionCategoryId(), ActionKeys.UPDATE);
		}

		return cpOptionCategoryLocalService.addOrUpdateCPOptionCategory(
			externalReferenceCode, getUserId(), cpOptionCategoryId, titleMap,
			descriptionMap, priority, key, serviceContext);
	}

	@Override
	public void deleteCPOptionCategory(long cpOptionCategoryId)
		throws PortalException {

		_cpOptionCategoryModelResourcePermission.check(
			getPermissionChecker(), cpOptionCategoryId, ActionKeys.DELETE);

		cpOptionCategoryLocalService.deleteCPOptionCategory(cpOptionCategoryId);
	}

	@Override
	public CPOptionCategory fetchCPOptionCategory(long cpOptionCategoryId)
		throws PortalException {

		CPOptionCategory cpOptionCategory =
			cpOptionCategoryPersistence.fetchByPrimaryKey(cpOptionCategoryId);

		if (cpOptionCategory != null) {
			_cpOptionCategoryModelResourcePermission.check(
				getPermissionChecker(), cpOptionCategory, ActionKeys.VIEW);
		}

		return cpOptionCategory;
	}

	@Override
	public CPOptionCategory fetchCPOptionCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CPOptionCategory cpOptionCategory =
			cpOptionCategoryLocalService.
				fetchCPOptionCategoryByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (cpOptionCategory != null) {
			_cpOptionCategoryModelResourcePermission.check(
				getPermissionChecker(),
				cpOptionCategory.getCPOptionCategoryId(), ActionKeys.VIEW);
		}

		return cpOptionCategory;
	}

	@Override
	public CPOptionCategory getCPOptionCategory(long cpOptionCategoryId)
		throws PortalException {

		_cpOptionCategoryModelResourcePermission.check(
			getPermissionChecker(), cpOptionCategoryId, ActionKeys.VIEW);

		return cpOptionCategoryPersistence.findByPrimaryKey(cpOptionCategoryId);
	}

	@Override
	public CPOptionCategory getCPOptionCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CPOptionCategory cpOptionCategory =
			cpOptionCategoryLocalService.
				getCPOptionCategoryByExternalReferenceCode(
					externalReferenceCode, companyId);

		_cpOptionCategoryModelResourcePermission.check(
			getPermissionChecker(), cpOptionCategory.getCPOptionCategoryId(),
			ActionKeys.VIEW);

		return cpOptionCategory;
	}

	@Override
	public BaseModelSearchResult<CPOptionCategory> searchCPOptionCategories(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		return cpOptionCategoryLocalService.searchCPOptionCategories(
			companyId, keywords, start, end, sort);
	}

	@Override
	public CPOptionCategory updateCPOptionCategory(
			String externalReferenceCode, long cpOptionCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			double priority, String key)
		throws PortalException {

		_cpOptionCategoryModelResourcePermission.check(
			getPermissionChecker(), cpOptionCategoryId, ActionKeys.UPDATE);

		return cpOptionCategoryLocalService.updateCPOptionCategory(
			externalReferenceCode, cpOptionCategoryId, titleMap, descriptionMap,
			priority, key);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPOptionCategory)"
	)
	private ModelResourcePermission<CPOptionCategory>
		_cpOptionCategoryModelResourcePermission;

}