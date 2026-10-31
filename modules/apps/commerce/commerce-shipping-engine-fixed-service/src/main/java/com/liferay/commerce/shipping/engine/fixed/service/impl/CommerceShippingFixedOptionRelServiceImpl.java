/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service.impl;

import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionRel;
import com.liferay.commerce.shipping.engine.fixed.service.base.CommerceShippingFixedOptionRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceShippingFixedOptionRel"
	},
	service = AopService.class
)
public class CommerceShippingFixedOptionRelServiceImpl
	extends CommerceShippingFixedOptionRelServiceBaseImpl {

	@Override
	public CommerceShippingFixedOptionRel addCommerceShippingFixedOptionRel(
			long groupId, long commerceInventoryWarehouseId,
			long commerceShippingFixedOptionId, long commerceShippingMethodId,
			long countryId, long regionId, BigDecimal fixedPrice,
			double ratePercentage, BigDecimal rateUnitWeightPrice,
			double weightFrom, double weightTo, String zip)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceShippingFixedOptionRelLocalService.
			addCommerceShippingFixedOptionRel(
				getUserId(), groupId, commerceInventoryWarehouseId,
				commerceShippingFixedOptionId, commerceShippingMethodId,
				countryId, regionId, fixedPrice, ratePercentage,
				rateUnitWeightPrice, weightFrom, weightTo, zip);
	}

	@Override
	public void deleteCommerceShippingFixedOptionRel(
			long commerceShippingFixedOptionRelId)
		throws PortalException {

		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel =
			commerceShippingFixedOptionRelPersistence.findByPrimaryKey(
				commerceShippingFixedOptionRelId);

		_checkCommerceChannel(commerceShippingFixedOptionRel.getGroupId());

		commerceShippingFixedOptionRelLocalService.
			deleteCommerceShippingFixedOptionRel(
				commerceShippingFixedOptionRel);
	}

	@Override
	public CommerceShippingFixedOptionRel fetchCommerceShippingFixedOptionRel(
			long commerceShippingFixedOptionRelId)
		throws PortalException {

		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel =
			commerceShippingFixedOptionRelPersistence.fetchByPrimaryKey(
				commerceShippingFixedOptionRelId);

		if (commerceShippingFixedOptionRel != null) {
			_checkCommerceChannel(commerceShippingFixedOptionRel.getGroupId());
		}

		return commerceShippingFixedOptionRel;
	}

	@Override
	public List<CommerceShippingFixedOptionRel>
			getCommerceShippingFixedOptionRels(
				long commerceShippingFixedOptionId,
				long commerceShippingMethodId, int start, int end,
				OrderByComparator<CommerceShippingFixedOptionRel>
					orderByComparator)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodService.getCommerceShippingMethod(
				commerceShippingMethodId);

		return commerceShippingFixedOptionRelPersistence.findByC_C(
			commerceShippingFixedOptionId,
			commerceShippingMethod.getCommerceShippingMethodId(), start, end,
			orderByComparator);
	}

	@Override
	public int getCommerceShippingFixedOptionRelsCount(
			long commerceShippingFixedOptionId, long commerceShippingMethodId)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodService.getCommerceShippingMethod(
				commerceShippingMethodId);

		return commerceShippingFixedOptionRelPersistence.countByC_C(
			commerceShippingFixedOptionId,
			commerceShippingMethod.getCommerceShippingMethodId());
	}

	@Override
	public List<CommerceShippingFixedOptionRel>
			getCommerceShippingMethodFixedOptionRels(
				long commerceShippingMethodId, int start, int end,
				OrderByComparator<CommerceShippingFixedOptionRel>
					orderByComparator)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodService.getCommerceShippingMethod(
				commerceShippingMethodId);

		return commerceShippingFixedOptionRelPersistence.
			findByCommerceShippingMethodId(
				commerceShippingMethod.getCommerceShippingMethodId(), start,
				end, orderByComparator);
	}

	@Override
	public int getCommerceShippingMethodFixedOptionRelsCount(
			long commerceShippingMethodId)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodService.getCommerceShippingMethod(
				commerceShippingMethodId);

		return commerceShippingFixedOptionRelPersistence.
			countByCommerceShippingMethodId(
				commerceShippingMethod.getCommerceShippingMethodId());
	}

	@Override
	public CommerceShippingFixedOptionRel updateCommerceShippingFixedOptionRel(
			long commerceShippingFixedOptionRelId,
			long commerceInventoryWarehouseId, long countryId, long regionId,
			BigDecimal fixedPrice, double ratePercentage,
			BigDecimal rateUnitWeightPrice, double weightFrom, double weightTo,
			String zip)
		throws PortalException {

		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel =
			commerceShippingFixedOptionRelPersistence.findByPrimaryKey(
				commerceShippingFixedOptionRelId);

		_checkCommerceChannel(commerceShippingFixedOptionRel.getGroupId());

		return commerceShippingFixedOptionRelLocalService.
			updateCommerceShippingFixedOptionRel(
				commerceShippingFixedOptionRelId, commerceInventoryWarehouseId,
				countryId, regionId, fixedPrice, ratePercentage,
				rateUnitWeightPrice, weightFrom, weightTo, zip);
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

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

}