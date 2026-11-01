/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service.impl;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionQualifier;
import com.liferay.commerce.shipping.engine.fixed.service.base.CommerceShippingFixedOptionQualifierServiceBaseImpl;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceShippingFixedOptionQualifier"
	},
	service = AopService.class
)
public class CommerceShippingFixedOptionQualifierServiceImpl
	extends CommerceShippingFixedOptionQualifierServiceBaseImpl {

	@Override
	public CommerceShippingFixedOptionQualifier
			addCommerceShippingFixedOptionQualifier(
				String className, long classPK,
				long commerceShippingFixedOptionId)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		return commerceShippingFixedOptionQualifierLocalService.
			addCommerceShippingFixedOptionQualifier(
				getUserId(), className, classPK, commerceShippingFixedOptionId);
	}

	@Override
	public void deleteCommerceShippingFixedOptionQualifier(
			long commerceShippingFixedOptionQualifierId)
		throws PortalException {

		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier =
				commerceShippingFixedOptionQualifierPersistence.
					findByPrimaryKey(commerceShippingFixedOptionQualifierId);

		_checkCommerceChannel(
			commerceShippingFixedOptionQualifier.
				getCommerceShippingFixedOptionId());

		commerceShippingFixedOptionQualifierLocalService.
			deleteCommerceShippingFixedOptionQualifier(
				commerceShippingFixedOptionQualifier);
	}

	@Override
	public void deleteCommerceShippingFixedOptionQualifiers(
			long commerceShippingFixedOptionId)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		commerceShippingFixedOptionQualifierLocalService.
			deleteCommerceShippingFixedOptionQualifiers(
				commerceShippingFixedOptionId);
	}

	@Override
	public void deleteCommerceShippingFixedOptionQualifiers(
			String className, long commerceShippingFixedOptionId)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		commerceShippingFixedOptionQualifierLocalService.
			deleteCommerceShippingFixedOptionQualifiers(
				className, commerceShippingFixedOptionId);
	}

	@Override
	public CommerceShippingFixedOptionQualifier
			fetchCommerceShippingFixedOptionQualifier(
				String className, long classPK,
				long commerceShippingFixedOptionId)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		return commerceShippingFixedOptionQualifierPersistence.fetchByC_C_C(
			_classNameLocalService.getClassNameId(className), classPK,
			commerceShippingFixedOptionId);
	}

	@Override
	public List<CommerceShippingFixedOptionQualifier>
			getCommerceOrderTypeCommerceShippingFixedOptionQualifiers(
				long commerceShippingFixedOptionId, String keywords, int start,
				int end)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		return commerceShippingFixedOptionQualifierLocalService.
			getCommerceOrderTypeCommerceShippingFixedOptionQualifiers(
				commerceShippingFixedOptionId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCommerceShippingFixedOptionQualifiersCount(
			long commerceShippingFixedOptionId, String keywords)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		return commerceShippingFixedOptionQualifierLocalService.
			getCommerceOrderTypeCommerceShippingFixedOptionQualifiersCount(
				commerceShippingFixedOptionId, keywords);
	}

	@Override
	public CommerceShippingFixedOptionQualifier
			getCommerceShippingFixedOptionQualifier(
				long commerceShippingFixedOptionQualifierId)
		throws PortalException {

		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier =
				commerceShippingFixedOptionQualifierPersistence.
					findByPrimaryKey(commerceShippingFixedOptionQualifierId);

		_checkCommerceChannel(
			commerceShippingFixedOptionQualifier.
				getCommerceShippingFixedOptionId());

		return commerceShippingFixedOptionQualifier;
	}

	@Override
	public List<CommerceShippingFixedOptionQualifier>
			getCommerceShippingFixedOptionQualifiers(
				long commerceShippingFixedOptionId)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		return commerceShippingFixedOptionQualifierPersistence.
			findByCommerceShippingFixedOptionId(commerceShippingFixedOptionId);
	}

	@Override
	public List<CommerceShippingFixedOptionQualifier>
			getCommerceShippingFixedOptionQualifiers(
				long commerceShippingFixedOptionId, int start, int end,
				OrderByComparator<CommerceShippingFixedOptionQualifier>
					orderByComparator)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		return commerceShippingFixedOptionQualifierPersistence.
			findByCommerceShippingFixedOptionId(
				commerceShippingFixedOptionId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceShippingFixedOptionQualifiersCount(
			long commerceShippingFixedOptionId)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		return commerceShippingFixedOptionQualifierPersistence.
			countByCommerceShippingFixedOptionId(commerceShippingFixedOptionId);
	}

	@Override
	public List<CommerceShippingFixedOptionQualifier>
			getCommerceTermEntryCommerceShippingFixedOptionQualifiers(
				long commerceShippingFixedOptionId, String keywords, int start,
				int end)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		return commerceShippingFixedOptionQualifierLocalService.
			getCommerceTermEntryCommerceShippingFixedOptionQualifiers(
				commerceShippingFixedOptionId, keywords, start, end);
	}

	@Override
	public int getCommerceTermEntryCommerceShippingFixedOptionQualifiersCount(
			long commerceShippingFixedOptionId, String keywords)
		throws PortalException {

		_checkCommerceChannel(commerceShippingFixedOptionId);

		return commerceShippingFixedOptionQualifierLocalService.
			getCommerceTermEntryCommerceShippingFixedOptionQualifiersCount(
				commerceShippingFixedOptionId, keywords);
	}

	private void _checkCommerceChannel(long commerceShippingFixedOptionId)
		throws PortalException {

		CommerceShippingFixedOption commerceShippingFixedOption =
			_commerceShippingFixedOptionPersistence.findByPrimaryKey(
				commerceShippingFixedOptionId);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceShippingFixedOption.getGroupId());

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannel, ActionKeys.UPDATE);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

	@Reference
	private CommerceShippingFixedOptionPersistence
		_commerceShippingFixedOptionPersistence;

}