/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.impl;

import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.commerce.payment.service.base.CommercePaymentMethodGroupRelQualifierServiceBaseImpl;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelPersistence;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
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
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePaymentMethodGroupRelQualifier"
	},
	service = AopService.class
)
public class CommercePaymentMethodGroupRelQualifierServiceImpl
	extends CommercePaymentMethodGroupRelQualifierServiceBaseImpl {

	@Override
	public CommercePaymentMethodGroupRelQualifier
			addCommercePaymentMethodGroupRelQualifier(
				String className, long classPK,
				long commercePaymentMethodGroupRelId)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		return commercePaymentMethodGroupRelQualifierLocalService.
			addCommercePaymentMethodGroupRelQualifier(
				getUserId(), className, classPK,
				commercePaymentMethodGroupRelId);
	}

	@Override
	public void deleteCommercePaymentMethodGroupRelQualifier(
			long commercePaymentMethodGroupRelQualifierId)
		throws PortalException {

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier =
				commercePaymentMethodGroupRelQualifierPersistence.
					findByPrimaryKey(commercePaymentMethodGroupRelQualifierId);

		_checkCommerceChannel(
			commercePaymentMethodGroupRelQualifier.
				getCommercePaymentMethodGroupRelId());

		commercePaymentMethodGroupRelQualifierLocalService.
			deleteCommercePaymentMethodGroupRelQualifier(
				commercePaymentMethodGroupRelQualifier);
	}

	@Override
	public void deleteCommercePaymentMethodGroupRelQualifiers(
			String className, long commercePaymentMethodGroupRelId)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		commercePaymentMethodGroupRelQualifierLocalService.
			deleteCommercePaymentMethodGroupRelQualifiers(
				className, commercePaymentMethodGroupRelId);
	}

	@Override
	public void
			deleteCommercePaymentMethodGroupRelQualifiersByCommercePaymentMethodGroupRelId(
				long commercePaymentMethodGroupRelId)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		commercePaymentMethodGroupRelQualifierLocalService.
			deleteCommercePaymentMethodGroupRelQualifiers(
				commercePaymentMethodGroupRelId);
	}

	@Override
	public CommercePaymentMethodGroupRelQualifier
			fetchCommercePaymentMethodGroupRelQualifier(
				String className, long classPK,
				long commercePaymentMethodGroupRelId)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		return commercePaymentMethodGroupRelQualifierPersistence.fetchByC_C_C(
			_classNameLocalService.getClassNameId(className), classPK,
			commercePaymentMethodGroupRelId);
	}

	@Override
	public List<CommercePaymentMethodGroupRelQualifier>
			getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiers(
				long commercePaymentMethodGroupRelId, String keywords,
				int start, int end)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		return commercePaymentMethodGroupRelQualifierLocalService.
			getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiers(
				commercePaymentMethodGroupRelId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiersCount(
			long commercePaymentMethodGroupRelId, String keywords)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		return commercePaymentMethodGroupRelQualifierLocalService.
			getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiersCount(
				commercePaymentMethodGroupRelId, keywords);
	}

	@Override
	public CommercePaymentMethodGroupRelQualifier
			getCommercePaymentMethodGroupRelQualifier(
				long commercePaymentMethodGroupRelQualifierId)
		throws PortalException {

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier =
				commercePaymentMethodGroupRelQualifierPersistence.
					findByPrimaryKey(commercePaymentMethodGroupRelQualifierId);

		_checkCommerceChannel(
			commercePaymentMethodGroupRelQualifier.
				getCommercePaymentMethodGroupRelId());

		return commercePaymentMethodGroupRelQualifier;
	}

	@Override
	public List<CommercePaymentMethodGroupRelQualifier>
			getCommercePaymentMethodGroupRelQualifiers(
				long commercePaymentMethodGroupRelId, int start, int end,
				OrderByComparator<CommercePaymentMethodGroupRelQualifier>
					orderByComparator)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		return commercePaymentMethodGroupRelQualifierPersistence.
			findByCommercePaymentMethodGroupRelId(
				commercePaymentMethodGroupRelId, start, end, orderByComparator);
	}

	@Override
	public List<CommercePaymentMethodGroupRelQualifier>
			getCommercePaymentMethodGroupRelQualifiers(
				String className, long commercePaymentMethodGroupRelId)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		return commercePaymentMethodGroupRelQualifierLocalService.
			getCommercePaymentMethodGroupRelQualifiers(
				className, commercePaymentMethodGroupRelId);
	}

	@Override
	public int getCommercePaymentMethodGroupRelQualifiersCount(
			long commercePaymentMethodGroupRelId)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		return commercePaymentMethodGroupRelQualifierPersistence.
			countByCommercePaymentMethodGroupRelId(
				commercePaymentMethodGroupRelId);
	}

	@Override
	public List<CommercePaymentMethodGroupRelQualifier>
			getCommerceTermEntryCommercePaymentMethodGroupRelQualifiers(
				long commercePaymentMethodGroupRelId, String keywords,
				int start, int end)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		return commercePaymentMethodGroupRelQualifierLocalService.
			getCommerceTermEntryCommercePaymentMethodGroupRelQualifiers(
				commercePaymentMethodGroupRelId, keywords, start, end);
	}

	@Override
	public int getCommerceTermEntryCommercePaymentMethodGroupRelQualifiersCount(
			long commercePaymentMethodGroupRelId, String keywords)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRelId);

		return commercePaymentMethodGroupRelQualifierLocalService.
			getCommerceTermEntryCommercePaymentMethodGroupRelQualifiersCount(
				commercePaymentMethodGroupRelId, keywords);
	}

	private void _checkCommerceChannel(long commercePaymentMethodGroupRelId)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelPersistence.findByPrimaryKey(
				commercePaymentMethodGroupRelId);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commercePaymentMethodGroupRel.getGroupId());

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
	private CommercePaymentMethodGroupRelPersistence
		_commercePaymentMethodGroupRelPersistence;

}