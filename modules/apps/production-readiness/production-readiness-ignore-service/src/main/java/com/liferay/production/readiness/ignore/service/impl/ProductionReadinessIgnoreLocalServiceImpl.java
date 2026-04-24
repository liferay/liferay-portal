/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore;
import com.liferay.production.readiness.ignore.service.base.ProductionReadinessIgnoreLocalServiceBaseImpl;

import com.liferay.portal.kernel.service.RoleLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore",
	service = AopService.class
)
public class ProductionReadinessIgnoreLocalServiceImpl
	extends ProductionReadinessIgnoreLocalServiceBaseImpl {

	public ProductionReadinessIgnore addProductionReadinessIgnore(
			long userId, long companyId, String ruleKey, String reason)
		throws PortalException {

		_checkPermission();

		User user = userLocalService.getUser(userId);

		ProductionReadinessIgnore productionReadinessIgnore =
			productionReadinessIgnorePersistence.fetchByC_R(
				companyId, ruleKey);

		if (productionReadinessIgnore == null) {
			long productionReadinessIgnoreId = counterLocalService.increment();

			productionReadinessIgnore =
				productionReadinessIgnorePersistence.create(
					productionReadinessIgnoreId);

			productionReadinessIgnore.setCompanyId(companyId);
			productionReadinessIgnore.setRuleKey(ruleKey);
		}

		productionReadinessIgnore.setUserId(userId);
		productionReadinessIgnore.setUserName(user.getFullName());
		productionReadinessIgnore.setReason(reason);

		return productionReadinessIgnorePersistence.update(
			productionReadinessIgnore);
	}

	public void deleteProductionReadinessIgnore(long companyId, String ruleKey)
		throws PortalException {

		_checkPermission();

		ProductionReadinessIgnore productionReadinessIgnore =
			productionReadinessIgnorePersistence.fetchByC_R(
				companyId, ruleKey);

		if (productionReadinessIgnore != null) {
			productionReadinessIgnorePersistence.remove(
				productionReadinessIgnore);
		}
	}

	public ProductionReadinessIgnore fetchProductionReadinessIgnore(
		long companyId, String ruleKey) {

		return productionReadinessIgnorePersistence.fetchByC_R(
			companyId, ruleKey);
	}

	public List<ProductionReadinessIgnore> getProductionReadinessIgnores(
		long companyId) {

		return productionReadinessIgnorePersistence.findByCompanyId(companyId);
	}

	private void _checkPermission() throws PortalException {
		if (!_roleLocalService.hasUserRole(
				PrincipalThreadLocal.getUserId(),
				CompanyThreadLocal.getCompanyId(), RoleConstants.ADMINISTRATOR,
				true)) {

			throw new PrincipalException.MustBeCompanyAdmin(
				PrincipalThreadLocal.getUserId());
		}
	}

	@Reference
	private RoleLocalService _roleLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1760571727