/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.impl;

import com.liferay.commerce.model.CommerceAddressRestriction;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.base.CommercePaymentMethodGroupRelServiceBaseImpl;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressRestrictionLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePaymentMethodGroupRel"
	},
	service = AopService.class
)
public class CommercePaymentMethodGroupRelServiceImpl
	extends CommercePaymentMethodGroupRelServiceBaseImpl {

	@Override
	public CommerceAddressRestriction addCommerceAddressRestriction(
			long groupId, long classPK, long countryId)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelLocalService.
			addCommerceAddressRestriction(
				getUserId(), groupId, classPK, countryId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceAddressRestriction addCommerceAddressRestriction(
			long classPK, long countryId, ServiceContext serviceContext)
		throws PortalException {

		return commercePaymentMethodGroupRelService.
			addCommerceAddressRestriction(
				serviceContext.getScopeGroupId(), classPK, countryId);
	}

	@Override
	public CommercePaymentMethodGroupRel addCommercePaymentMethodGroupRel(
			long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, File imageFile,
			String paymentIntegrationKey, double priority, String typeSettings)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelLocalService.
			addCommercePaymentMethodGroupRel(
				getUserId(), groupId, nameMap, descriptionMap, active,
				imageFile, paymentIntegrationKey, priority, typeSettings);
	}

	@Override
	public void deleteCommerceAddressRestriction(
			long commerceAddressRestrictionId)
		throws PortalException {

		CommerceAddressRestriction commerceAddressRestriction =
			_commerceAddressRestrictionLocalService.
				fetchCommerceAddressRestriction(commerceAddressRestrictionId);

		if (commerceAddressRestriction != null) {
			_checkCommerceChannel(commerceAddressRestriction.getGroupId());
		}

		commercePaymentMethodGroupRelLocalService.
			deleteCommerceAddressRestriction(commerceAddressRestrictionId);
	}

	@Override
	public void deleteCommerceAddressRestrictions(
			long commercePaymentMethodGroupRelId)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			commercePaymentMethodGroupRelPersistence.findByPrimaryKey(
				commercePaymentMethodGroupRelId);

		_checkCommerceChannel(commercePaymentMethodGroupRel.getGroupId());

		_commerceAddressRestrictionLocalService.
			deleteCommerceAddressRestrictions(
				CommercePaymentMethodGroupRel.class.getName(),
				commercePaymentMethodGroupRel.
					getCommercePaymentMethodGroupRelId());
	}

	@Override
	public void deleteCommercePaymentMethodGroupRel(
			long commercePaymentMethodGroupRelId)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			commercePaymentMethodGroupRelPersistence.findByPrimaryKey(
				commercePaymentMethodGroupRelId);

		_checkCommerceChannel(commercePaymentMethodGroupRel.getGroupId());

		commercePaymentMethodGroupRelLocalService.
			deleteCommercePaymentMethodGroupRel(commercePaymentMethodGroupRel);
	}

	@Override
	public CommercePaymentMethodGroupRel fetchCommercePaymentMethodGroupRel(
			long commercePaymentMethodGroupRelId)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			commercePaymentMethodGroupRelPersistence.fetchByPrimaryKey(
				commercePaymentMethodGroupRelId);

		if (commercePaymentMethodGroupRel != null) {
			_checkCommerceChannel(commercePaymentMethodGroupRel.getGroupId());
		}

		return commercePaymentMethodGroupRel;
	}

	@Override
	public CommercePaymentMethodGroupRel fetchCommercePaymentMethodGroupRel(
			long groupId, String engineKey)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			commercePaymentMethodGroupRelPersistence.fetchByG_P(
				groupId, engineKey);

		if (commercePaymentMethodGroupRel != null) {
			_checkCommerceChannel(commercePaymentMethodGroupRel.getGroupId());
		}

		return commercePaymentMethodGroupRel;
	}

	@Override
	public List<CommerceAddressRestriction> getCommerceAddressRestrictions(
			long classPK, int start, int end,
			OrderByComparator<CommerceAddressRestriction> orderByComparator)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			commercePaymentMethodGroupRelPersistence.fetchByPrimaryKey(classPK);

		if (commercePaymentMethodGroupRel == null) {
			return Collections.emptyList();
		}

		_checkCommerceChannel(commercePaymentMethodGroupRel.getGroupId());

		return commercePaymentMethodGroupRelLocalService.
			getCommerceAddressRestrictions(
				classPK, start, end, orderByComparator);
	}

	@Override
	public int getCommerceAddressRestrictionsCount(long classPK)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			commercePaymentMethodGroupRelPersistence.fetchByPrimaryKey(classPK);

		if (commercePaymentMethodGroupRel == null) {
			return 0;
		}

		_checkCommerceChannel(commercePaymentMethodGroupRel.getGroupId());

		return commercePaymentMethodGroupRelLocalService.
			getCommerceAddressRestrictionsCount(classPK);
	}

	@Override
	public CommercePaymentMethodGroupRel getCommercePaymentMethodGroupRel(
			long commercePaymentMethodGroupRelId)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			commercePaymentMethodGroupRelPersistence.findByPrimaryKey(
				commercePaymentMethodGroupRelId);

		_checkCommerceChannel(commercePaymentMethodGroupRel.getGroupId());

		return commercePaymentMethodGroupRel;
	}

	@Override
	public CommercePaymentMethodGroupRel getCommercePaymentMethodGroupRel(
			long groupId, String engineKey)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelPersistence.findByG_P(
			groupId, engineKey);
	}

	@Override
	public List<CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(long groupId)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelPersistence.findByGroupId(groupId);
	}

	@Override
	public List<CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(long groupId, boolean active)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRels(groupId, active);
	}

	@Override
	public List<CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(
				long groupId, boolean active, int start, int end)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRels(groupId, active, start, end);
	}

	@Override
	public List<CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(
				long groupId, boolean active, int start, int end,
				OrderByComparator<CommercePaymentMethodGroupRel>
					orderByComparator)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelPersistence.findByG_A(
			groupId, active, start, end, orderByComparator);
	}

	@Override
	public List<CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(
				long groupId, int start, int end,
				OrderByComparator<CommercePaymentMethodGroupRel>
					orderByComparator)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRels(
				groupId, start, end, orderByComparator);
	}

	@Override
	public List<CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(
				long groupId, long countryId, boolean active)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRels(groupId, countryId, active);
	}

	@Override
	public int getCommercePaymentMethodGroupRelsCount(long groupId)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelPersistence.countByGroupId(groupId);
	}

	@Override
	public int getCommercePaymentMethodGroupRelsCount(
			long groupId, boolean active)
		throws PortalException {

		_checkCommerceChannel(groupId);

		return commercePaymentMethodGroupRelPersistence.countByG_A(
			groupId, active);
	}

	@Override
	public CommercePaymentMethodGroupRel setActive(
			long commercePaymentMethodGroupRelId, boolean active)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			commercePaymentMethodGroupRelPersistence.fetchByPrimaryKey(
				commercePaymentMethodGroupRelId);

		if (commercePaymentMethodGroupRel != null) {
			_checkCommerceChannel(commercePaymentMethodGroupRel.getGroupId());
		}

		return commercePaymentMethodGroupRelLocalService.setActive(
			commercePaymentMethodGroupRelId, active);
	}

	@Override
	public CommercePaymentMethodGroupRel updateCommercePaymentMethodGroupRel(
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel)
		throws PortalException {

		_checkCommerceChannel(commercePaymentMethodGroupRel.getGroupId());

		return commercePaymentMethodGroupRelLocalService.
			updateCommercePaymentMethodGroupRel(commercePaymentMethodGroupRel);
	}

	@Override
	public CommercePaymentMethodGroupRel updateCommercePaymentMethodGroupRel(
			long commercePaymentMethodGroupRelId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, File imageFile, double priority,
			boolean active)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			commercePaymentMethodGroupRelPersistence.findByPrimaryKey(
				commercePaymentMethodGroupRelId);

		_checkCommerceChannel(commercePaymentMethodGroupRel.getGroupId());

		return commercePaymentMethodGroupRelLocalService.
			updateCommercePaymentMethodGroupRel(
				commercePaymentMethodGroupRel.
					getCommercePaymentMethodGroupRelId(),
				nameMap, descriptionMap, imageFile, priority, active);
	}

	private void _checkCommerceChannel(long groupId) throws PortalException {
		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(groupId);

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannel, ActionKeys.UPDATE);
	}

	@Reference
	private CommerceAddressRestrictionLocalService
		_commerceAddressRestrictionLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

}