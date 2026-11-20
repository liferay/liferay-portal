/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service.impl;

import com.liferay.commerce.product.exception.NoSuchCPTaxCategoryException;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate;
import com.liferay.commerce.tax.engine.fixed.service.base.CommerceTaxFixedRateServiceBaseImpl;
import com.liferay.commerce.tax.exception.NoSuchTaxMethodException;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalService;
import com.liferay.petra.string.StringBundler;
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
		"json.web.service.context.path=CommerceTaxFixedRate"
	},
	service = AopService.class
)
public class CommerceTaxFixedRateServiceImpl
	extends CommerceTaxFixedRateServiceBaseImpl {

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceTaxFixedRate addCommerceTaxFixedRate(
			long commerceTaxMethodId, long cpTaxCategoryId, double rate,
			ServiceContext serviceContext)
		throws PortalException {

		return commerceTaxFixedRateService.addCommerceTaxFixedRate(
			serviceContext.getScopeGroupId(), commerceTaxMethodId,
			cpTaxCategoryId, rate);
	}

	@Override
	public CommerceTaxFixedRate addCommerceTaxFixedRate(
			long groupId, long commerceTaxMethodId, long cpTaxCategoryId,
			double rate)
		throws PortalException {

		CommerceTaxMethod commerceTaxMethod =
			_commerceTaxMethodLocalService.getCommerceTaxMethod(
				commerceTaxMethodId);

		_checkPermission(groupId, commerceTaxMethod);

		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryLocalService.getCPTaxCategory(cpTaxCategoryId);

		if (commerceTaxMethod.getCompanyId() != cpTaxCategory.getCompanyId()) {
			throw new NoSuchCPTaxCategoryException(
				"No CPTaxCategory exists with the primary key " +
					cpTaxCategoryId);
		}

		return commerceTaxFixedRateLocalService.addCommerceTaxFixedRate(
			getUserId(), commerceTaxMethod.getGroupId(), commerceTaxMethodId,
			cpTaxCategoryId, rate);
	}

	@Override
	public void deleteCommerceTaxFixedRate(long commerceTaxFixedRateId)
		throws PortalException {

		CommerceTaxFixedRate commerceTaxFixedRate =
			commerceTaxFixedRatePersistence.findByPrimaryKey(
				commerceTaxFixedRateId);

		_checkPermission(
			0,
			_commerceTaxMethodLocalService.getCommerceTaxMethod(
				commerceTaxFixedRate.getCommerceTaxMethodId()));

		commerceTaxFixedRateLocalService.deleteCommerceTaxFixedRate(
			commerceTaxFixedRate);
	}

	@Override
	public CommerceTaxFixedRate fetchCommerceTaxFixedRate(
			long commerceTaxFixedRateId)
		throws PortalException {

		CommerceTaxFixedRate commerceTaxFixedRate =
			commerceTaxFixedRatePersistence.fetchByPrimaryKey(
				commerceTaxFixedRateId);

		if (commerceTaxFixedRate != null) {
			_checkPermission(
				0,
				_commerceTaxMethodLocalService.getCommerceTaxMethod(
					commerceTaxFixedRate.getCommerceTaxMethodId()));
		}

		return commerceTaxFixedRate;
	}

	@Override
	public CommerceTaxFixedRate fetchCommerceTaxFixedRate(
			long cpTaxCategoryId, long commerceTaxMethodId)
		throws PortalException {

		CommerceTaxFixedRate commerceTaxFixedRate =
			commerceTaxFixedRatePersistence.fetchByC_C(
				cpTaxCategoryId, commerceTaxMethodId);

		if (commerceTaxFixedRate != null) {
			_checkPermission(
				0,
				_commerceTaxMethodLocalService.getCommerceTaxMethod(
					commerceTaxFixedRate.getCommerceTaxMethodId()));
		}

		return commerceTaxFixedRate;
	}

	@Override
	public List<CommerceTaxFixedRate> getCommerceTaxFixedRates(
			long groupId, long commerceTaxMethodId, int start, int end,
			OrderByComparator<CommerceTaxFixedRate> orderByComparator)
		throws PortalException {

		_checkPermission(
			groupId,
			_commerceTaxMethodLocalService.getCommerceTaxMethod(
				commerceTaxMethodId));

		return commerceTaxFixedRatePersistence.findByCommerceTaxMethodId(
			commerceTaxMethodId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceTaxFixedRatesCount(
			long groupId, long commerceTaxMethodId)
		throws PortalException {

		_checkPermission(
			groupId,
			_commerceTaxMethodLocalService.getCommerceTaxMethod(
				commerceTaxMethodId));

		return commerceTaxFixedRatePersistence.countByCommerceTaxMethodId(
			commerceTaxMethodId);
	}

	@Override
	public CommerceTaxFixedRate updateCommerceTaxFixedRate(
			long commerceTaxFixedRateId, double rate)
		throws PortalException {

		CommerceTaxFixedRate commerceTaxFixedRate =
			commerceTaxFixedRatePersistence.findByPrimaryKey(
				commerceTaxFixedRateId);

		_checkPermission(
			0,
			_commerceTaxMethodLocalService.getCommerceTaxMethod(
				commerceTaxFixedRate.getCommerceTaxMethodId()));

		return commerceTaxFixedRateLocalService.updateCommerceTaxFixedRate(
			commerceTaxFixedRateId, rate);
	}

	private void _checkPermission(
			long groupId, CommerceTaxMethod commerceTaxMethod)
		throws PortalException {

		if ((groupId > 0) && (commerceTaxMethod.getGroupId() != groupId)) {
			throw new NoSuchTaxMethodException(
				StringBundler.concat(
					"No CommerceTaxMethod exists with the primary key ",
					commerceTaxMethod.getCommerceTaxMethodId(), " in group ",
					groupId));
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceTaxMethod.getGroupId());

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
	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

	@Reference
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

}