/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service.impl;

import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.tax.exception.DuplicateCommerceTaxCategoryMappingExternalReferenceCodeException;
import com.liferay.commerce.tax.model.CommerceTaxCategoryMapping;
import com.liferay.commerce.tax.service.base.CommerceTaxCategoryMappingLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Ivica Cardic
 */
@Component(
	property = "model.class.name=com.liferay.commerce.tax.model.CommerceTaxCategoryMapping",
	service = AopService.class
)
public class CommerceTaxCategoryMappingLocalServiceImpl
	extends CommerceTaxCategoryMappingLocalServiceBaseImpl {

	@Override
	public CommerceTaxCategoryMapping addCommerceTaxCategoryMapping(
			String externalReferenceCode, long userId, long groupId,
			long commerceTaxMethodId, long cpTaxCategoryId)
		throws PortalException {

		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryLocalService.getCPTaxCategory(cpTaxCategoryId);

		_validate(commerceTaxMethodId, cpTaxCategory.getCPTaxCategoryId());

		CommerceTaxCategoryMapping commerceTaxCategoryMapping =
			commerceTaxCategoryMappingPersistence.create(
				counterLocalService.increment());

		commerceTaxCategoryMapping.setExternalReferenceCode(
			externalReferenceCode);
		commerceTaxCategoryMapping.setGroupId(groupId);

		User user = _userLocalService.getUser(userId);

		commerceTaxCategoryMapping.setCompanyId(user.getCompanyId());
		commerceTaxCategoryMapping.setUserId(user.getUserId());
		commerceTaxCategoryMapping.setUserName(user.getFullName());

		commerceTaxCategoryMapping.setCommerceTaxMethodId(commerceTaxMethodId);
		commerceTaxCategoryMapping.setCPTaxCategoryId(
			cpTaxCategory.getCPTaxCategoryId());

		return commerceTaxCategoryMappingPersistence.update(
			commerceTaxCategoryMapping);
	}

	@Override
	public CommerceTaxCategoryMapping fetchCommerceTaxCategoryMapping(
		long commerceTaxMethodId, long cpTaxCategoryId) {

		return commerceTaxCategoryMappingPersistence.fetchByC_C(
			commerceTaxMethodId, cpTaxCategoryId);
	}

	@Override
	public CommerceTaxCategoryMapping updateExternalReferenceCode(
			long commerceTaxCategoryMappingId, String externalReferenceCode)
		throws PortalException {

		CommerceTaxCategoryMapping commerceTaxCategoryMapping =
			commerceTaxCategoryMappingPersistence.findByPrimaryKey(
				commerceTaxCategoryMappingId);

		commerceTaxCategoryMapping.setExternalReferenceCode(
			externalReferenceCode);

		return commerceTaxCategoryMappingPersistence.update(
			commerceTaxCategoryMapping);
	}

	private void _validate(long commerceTaxMethodId, long cpTaxCategoryId)
		throws PortalException {

		int count = commerceTaxCategoryMappingPersistence.countByC_C(
			commerceTaxMethodId, cpTaxCategoryId);

		if (count > 0) {
			throw new DuplicateCommerceTaxCategoryMappingExternalReferenceCodeException();
		}
	}

	@Reference
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}