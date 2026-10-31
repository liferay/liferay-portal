/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service.impl;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.service.base.CommerceTaxFixedRateAddressRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceTaxFixedRateAddressRel"
	},
	service = AopService.class
)
public class CommerceTaxFixedRateAddressRelServiceImpl
	extends CommerceTaxFixedRateAddressRelServiceBaseImpl {

	@Override
	public CommerceTaxFixedRateAddressRel addCommerceTaxFixedRateAddressRel(
			long groupId, long commerceTaxMethodId, long cpTaxCategoryId,
			long countryId, long regionId, String zip, double rate)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxFixedRateAddressRelLocalService.
			addCommerceTaxFixedRateAddressRel(
				getUserId(), groupId, commerceTaxMethodId, cpTaxCategoryId,
				countryId, regionId, zip, rate);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceTaxFixedRateAddressRel addCommerceTaxFixedRateAddressRel(
			long commerceTaxMethodId, long cpTaxCategoryId, long countryId,
			long regionId, String zip, double rate,
			ServiceContext serviceContext)
		throws PortalException {

		return commerceTaxFixedRateAddressRelService.
			addCommerceTaxFixedRateAddressRel(
				serviceContext.getScopeGroupId(), commerceTaxMethodId,
				cpTaxCategoryId, countryId, regionId, zip, rate);
	}

	@Override
	public void deleteCommerceTaxFixedRateAddressRel(
			long commerceTaxFixedRateAddressRelId)
		throws PortalException {

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			commerceTaxFixedRateAddressRelPersistence.findByPrimaryKey(
				commerceTaxFixedRateAddressRelId);

		_checkCommerceChannel(commerceTaxFixedRateAddressRel.getGroupId());

		commerceTaxFixedRateAddressRelLocalService.
			deleteCommerceTaxFixedRateAddressRel(
				commerceTaxFixedRateAddressRel);
	}

	@Override
	public CommerceTaxFixedRateAddressRel fetchCommerceTaxFixedRateAddressRel(
			long commerceTaxFixedRateAddressRelId)
		throws PortalException {

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			commerceTaxFixedRateAddressRelPersistence.fetchByPrimaryKey(
				commerceTaxFixedRateAddressRelId);

		if (commerceTaxFixedRateAddressRel != null) {
			_checkCommerceChannel(commerceTaxFixedRateAddressRel.getGroupId());
		}

		return commerceTaxFixedRateAddressRel;
	}

	@Override
	public List<CommerceTaxFixedRateAddressRel>
			getCommerceTaxMethodFixedRateAddressRels(
				long groupId, long commerceTaxMethodId, int start, int end,
				OrderByComparator<CommerceTaxFixedRateAddressRel>
					orderByComparator)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxFixedRateAddressRelPersistence.
			findByCommerceTaxMethodId(
				commerceTaxMethodId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceTaxMethodFixedRateAddressRelsCount(
			long groupId, long commerceTaxMethodId)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceTaxFixedRateAddressRelPersistence.
			countByCommerceTaxMethodId(commerceTaxMethodId);
	}

	@Override
	public CommerceTaxFixedRateAddressRel updateCommerceTaxFixedRateAddressRel(
			long commerceTaxFixedRateAddressRelId, long countryId,
			long regionId, String zip, double rate)
		throws PortalException {

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			commerceTaxFixedRateAddressRelPersistence.findByPrimaryKey(
				commerceTaxFixedRateAddressRelId);

		_checkCommerceChannel(commerceTaxFixedRateAddressRel.getGroupId());

		return commerceTaxFixedRateAddressRelLocalService.
			updateCommerceTaxFixedRateAddressRel(
				commerceTaxFixedRateAddressRelId, countryId, regionId, zip,
				rate);
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