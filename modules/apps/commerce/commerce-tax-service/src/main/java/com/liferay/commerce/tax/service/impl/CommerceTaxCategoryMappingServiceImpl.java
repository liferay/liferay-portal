/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service.impl;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.model.CommerceTaxCategoryMapping;
import com.liferay.commerce.tax.service.base.CommerceTaxCategoryMappingServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Ivica Cardic
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceTaxCategoryMapping"
	},
	service = AopService.class
)
public class CommerceTaxCategoryMappingServiceImpl
	extends CommerceTaxCategoryMappingServiceBaseImpl {

	@Override
	public CommerceTaxCategoryMapping addCommerceTaxCategoryMapping(
			String externalReferenceCode, long groupId,
			long commerceTaxMethodId, long cpTaxCategoryId)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxCategoryMappingLocalService.
			addCommerceTaxCategoryMapping(
				externalReferenceCode, getUserId(), groupId,
				commerceTaxMethodId, cpTaxCategoryId);
	}

	@Override
	public void deleteCommerceTaxCategoryMapping(
			long commerceTaxCategoryMappingId)
		throws PortalException {

		CommerceTaxCategoryMapping commerceTaxCategoryMapping =
			commerceTaxCategoryMappingPersistence.findByPrimaryKey(
				commerceTaxCategoryMappingId);

		_checkCommerceChannel(commerceTaxCategoryMapping.getGroupId());

		commerceTaxCategoryMappingLocalService.deleteCommerceTaxCategoryMapping(
			commerceTaxCategoryMapping);
	}

	@Override
	public CommerceTaxCategoryMapping fetchCommerceTaxCategoryMapping(
			long commerceTaxCategoryMappingId)
		throws PortalException {

		CommerceTaxCategoryMapping commerceTaxFixedRate =
			commerceTaxCategoryMappingPersistence.fetchByPrimaryKey(
				commerceTaxCategoryMappingId);

		if (commerceTaxFixedRate != null) {
			_checkCommerceChannel(commerceTaxFixedRate.getGroupId());
		}

		return commerceTaxFixedRate;
	}

	@Override
	public int getCommerceTaxCategoryMappingCount(
			long groupId, long commerceTaxMethodId)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxCategoryMappingPersistence.countByCommerceTaxMethodId(
			commerceTaxMethodId);
	}

	@Override
	public List<CommerceTaxCategoryMapping> getCommerceTaxCategoryMappings(
			long groupId, long commerceTaxMethodId, int start, int end,
			OrderByComparator<CommerceTaxCategoryMapping> orderByComparator)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxCategoryMappingPersistence.findByCommerceTaxMethodId(
			commerceTaxMethodId, start, end, orderByComparator);
	}

	@Override
	public CommerceTaxCategoryMapping updateExternalReferenceCode(
			long commerceTaxCategoryMappingId, String externalReferenceCode)
		throws PortalException {

		CommerceTaxCategoryMapping commerceTaxCategoryMapping =
			commerceTaxCategoryMappingPersistence.findByPrimaryKey(
				commerceTaxCategoryMappingId);

		_checkCommerceChannel(commerceTaxCategoryMapping.getGroupId());

		return commerceTaxCategoryMappingLocalService.
			updateExternalReferenceCode(
				commerceTaxCategoryMappingId, externalReferenceCode);
	}

	private void _checkCommerceChannel(long groupId) throws PortalException {
		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(groupId);

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannel, ActionKeys.UPDATE);
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

}