/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.service.base.CPOptionValueServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

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
		"json.web.service.context.path=CPOptionValue"
	},
	service = AopService.class
)
public class CPOptionValueServiceImpl extends CPOptionValueServiceBaseImpl {

	@Override
	public CPOptionValue addCPOptionValue(
			long cpOptionId, Map<Locale, String> nameMap, double priority,
			String key, ServiceContext serviceContext)
		throws PortalException {

		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionId, ActionKeys.UPDATE);

		return cpOptionValueLocalService.addCPOptionValue(
			cpOptionId, nameMap, priority, key, serviceContext);
	}

	@Override
	public CPOptionValue addOrUpdateCPOptionValue(
			String externalReferenceCode, long cpOptionId,
			Map<Locale, String> nameMap, double priority, String key,
			ServiceContext serviceContext)
		throws PortalException {

		CPOptionValue cpOptionValue = cpOptionValuePersistence.fetchByERC_C(
			externalReferenceCode, serviceContext.getCompanyId());

		if (cpOptionValue == null) {
			_cpOptionModelResourcePermission.check(
				getPermissionChecker(), cpOptionId, ActionKeys.UPDATE);
		}

		return cpOptionValueLocalService.addOrUpdateCPOptionValue(
			externalReferenceCode, cpOptionId, nameMap, priority, key,
			serviceContext);
	}

	@Override
	public void deleteCPOptionValue(long cpOptionValueId)
		throws PortalException {

		CPOptionValue cpOptionValue = cpOptionValuePersistence.findByPrimaryKey(
			cpOptionValueId);

		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionValue.getCPOptionId(),
			ActionKeys.UPDATE);

		cpOptionValueLocalService.deleteCPOptionValue(cpOptionValue);
	}

	@Override
	public CPOptionValue fetchCPOptionValue(long cpOptionValueId)
		throws PortalException {

		CPOptionValue cpOptionValue =
			cpOptionValuePersistence.fetchByPrimaryKey(cpOptionValueId);

		if (cpOptionValue != null) {
			_cpOptionModelResourcePermission.check(
				getPermissionChecker(), cpOptionValue.getCPOptionId(),
				ActionKeys.VIEW);
		}

		return cpOptionValue;
	}

	@Override
	public CPOptionValue fetchCPOptionValueByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CPOptionValue cpOptionValue = cpOptionValuePersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (cpOptionValue != null) {
			_cpOptionModelResourcePermission.check(
				getPermissionChecker(), cpOptionValue.getCPOptionId(),
				ActionKeys.VIEW);
		}

		return cpOptionValue;
	}

	@Override
	public CPOptionValue getCPOptionValue(long cpOptionValueId)
		throws PortalException {

		CPOptionValue cpOptionValue = cpOptionValuePersistence.findByPrimaryKey(
			cpOptionValueId);

		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionValue.getCPOptionId(),
			ActionKeys.VIEW);

		return cpOptionValue;
	}

	@Override
	public List<CPOptionValue> getCPOptionValues(
			long cpOptionId, int start, int end)
		throws PortalException {

		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionId, ActionKeys.VIEW);

		return cpOptionValuePersistence.findByCPOptionId(
			cpOptionId, start, end);
	}

	@Override
	public int getCPOptionValuesCount(long cpOptionId) throws PortalException {
		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionId, ActionKeys.VIEW);

		return cpOptionValuePersistence.countByCPOptionId(cpOptionId);
	}

	@Override
	public BaseModelSearchResult<CPOptionValue> searchCPOptionValues(
			long companyId, long cpOptionId, String keywords, int start,
			int end, Sort[] sorts)
		throws PortalException {

		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionId, ActionKeys.VIEW);

		return cpOptionValueLocalService.searchCPOptionValues(
			companyId, cpOptionId, keywords, start, end, sorts);
	}

	@Override
	public int searchCPOptionValuesCount(
			long companyId, long cpOptionId, String keywords)
		throws PortalException {

		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionId, ActionKeys.VIEW);

		return cpOptionValueLocalService.searchCPOptionValuesCount(
			companyId, cpOptionId, keywords);
	}

	@Override
	public CPOptionValue updateCPOptionValue(
			long cpOptionValueId, Map<Locale, String> nameMap, double priority,
			String key, ServiceContext serviceContext)
		throws PortalException {

		CPOptionValue cpOptionValue = cpOptionValueService.getCPOptionValue(
			cpOptionValueId);

		_cpOptionModelResourcePermission.check(
			getPermissionChecker(), cpOptionValue.getCPOptionId(),
			ActionKeys.VIEW);

		return cpOptionValueLocalService.updateCPOptionValue(
			cpOptionValue.getCPOptionValueId(), nameMap, priority, key,
			serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPOption)"
	)
	private ModelResourcePermission<CPOption> _cpOptionModelResourcePermission;

}