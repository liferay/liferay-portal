/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service.impl;

import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.base.CommerceShippingFixedOptionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceShippingFixedOption"
	},
	service = AopService.class
)
public class CommerceShippingFixedOptionServiceImpl
	extends CommerceShippingFixedOptionServiceBaseImpl {

	@Override
	public CommerceShippingFixedOption addCommerceShippingFixedOption(
			long groupId, long commerceShippingMethodId, BigDecimal amount,
			Map<Locale, String> descriptionMap, String key,
			Map<Locale, String> nameMap, double priority)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceShippingFixedOptionLocalService.
			addCommerceShippingFixedOption(
				getUserId(), groupId, commerceShippingMethodId, amount,
				descriptionMap, key, nameMap, priority);
	}

	@Override
	public void deleteCommerceShippingFixedOption(
			long commerceShippingFixedOptionId)
		throws PortalException {

		CommerceShippingFixedOption commerceShippingFixedOption =
			commerceShippingFixedOptionPersistence.findByPrimaryKey(
				commerceShippingFixedOptionId);

		_checkCommerceChannel(commerceShippingFixedOption.getGroupId());

		commerceShippingFixedOptionLocalService.
			deleteCommerceShippingFixedOption(commerceShippingFixedOption);
	}

	@Override
	public CommerceShippingFixedOption fetchCommerceShippingFixedOption(
			long commerceShippingFixedOptionId)
		throws PortalException {

		CommerceShippingFixedOption commerceShippingFixedOption =
			commerceShippingFixedOptionPersistence.fetchByPrimaryKey(
				commerceShippingFixedOptionId);

		if (commerceShippingFixedOption != null) {
			_checkCommerceChannel(commerceShippingFixedOption.getGroupId());
		}

		return commerceShippingFixedOption;
	}

	@Override
	public CommerceShippingFixedOption fetchCommerceShippingFixedOption(
			long companyId, String key)
		throws PortalException {

		CommerceShippingFixedOption commerceShippingFixedOption =
			commerceShippingFixedOptionPersistence.fetchByC_K(companyId, key);

		if (commerceShippingFixedOption != null) {
			_checkCommerceChannel(commerceShippingFixedOption.getGroupId());
		}

		return commerceShippingFixedOption;
	}

	@Override
	public List<CommerceShippingFixedOption> getCommerceShippingFixedOptions(
			long commerceShippingMethodId, int start, int end)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodService.getCommerceShippingMethod(
				commerceShippingMethodId);

		_checkCommerceChannel(commerceShippingMethod.getGroupId());

		return commerceShippingFixedOptionPersistence.
			findByCommerceShippingMethodId(
				commerceShippingMethod.getCommerceShippingMethodId(), start,
				end);
	}

	@Override
	public List<CommerceShippingFixedOption> getCommerceShippingFixedOptions(
			long commerceShippingMethodId, int start, int end,
			OrderByComparator<CommerceShippingFixedOption> orderByComparator)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodService.getCommerceShippingMethod(
				commerceShippingMethodId);

		_checkCommerceChannel(commerceShippingMethod.getGroupId());

		return commerceShippingFixedOptionPersistence.
			findByCommerceShippingMethodId(
				commerceShippingMethod.getCommerceShippingMethodId(), start,
				end, orderByComparator);
	}

	@Override
	public List<CommerceShippingFixedOption> getCommerceShippingFixedOptions(
			long companyId, long groupId, long commerceShippingMethodId,
			String keywords, int start, int end)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceShippingFixedOptionLocalService.
			getCommerceShippingFixedOptions(
				companyId, groupId, commerceShippingMethodId, keywords, start,
				end);
	}

	@Override
	public int getCommerceShippingFixedOptionsCount(
			long commerceShippingMethodId)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodService.getCommerceShippingMethod(
				commerceShippingMethodId);

		_checkCommerceChannel(commerceShippingMethod.getGroupId());

		return commerceShippingFixedOptionPersistence.
			countByCommerceShippingMethodId(
				commerceShippingMethod.getCommerceShippingMethodId());
	}

	@Override
	public long getCommerceShippingFixedOptionsCount(
			long companyId, long groupId, long commerceShippingMethodId,
			String keywords)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commerceShippingFixedOptionLocalService.
			getCommerceShippingFixedOptionsCount(
				companyId, groupId, commerceShippingMethodId, keywords);
	}

	@Override
	public CommerceShippingFixedOption updateCommerceShippingFixedOption(
			long commerceShippingFixedOptionId, BigDecimal amount,
			Map<Locale, String> descriptionMap, String key,
			Map<Locale, String> nameMap, double priority)
		throws PortalException {

		CommerceShippingFixedOption commerceShippingFixedOption =
			commerceShippingFixedOptionPersistence.findByPrimaryKey(
				commerceShippingFixedOptionId);

		_checkCommerceChannel(commerceShippingFixedOption.getGroupId());

		return commerceShippingFixedOptionLocalService.
			updateCommerceShippingFixedOption(
				commerceShippingFixedOptionId, amount, descriptionMap, key,
				nameMap, priority);
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