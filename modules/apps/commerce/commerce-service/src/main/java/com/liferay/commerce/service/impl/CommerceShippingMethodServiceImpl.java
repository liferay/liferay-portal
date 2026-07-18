/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.model.CommerceAddressRestriction;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressRestrictionLocalService;
import com.liferay.commerce.service.base.CommerceShippingMethodServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceShippingMethod"
	},
	service = AopService.class
)
public class CommerceShippingMethodServiceImpl
	extends CommerceShippingMethodServiceBaseImpl {

	@Override
	public CommerceAddressRestriction addCommerceAddressRestriction(
			long groupId, long commerceShippingMethodId, long countryId)
		throws PortalException {

		_checkCommerceChannel(ActionKeys.UPDATE, groupId);

		return commerceShippingMethodLocalService.addCommerceAddressRestriction(
			getUserId(), groupId, commerceShippingMethodId, countryId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceAddressRestriction addCommerceAddressRestriction(
			long commerceShippingMethodId, long countryId,
			ServiceContext serviceContext)
		throws PortalException {

		return commerceShippingMethodService.addCommerceAddressRestriction(
			serviceContext.getScopeGroupId(), commerceShippingMethodId,
			countryId);
	}

	@Override
	public CommerceShippingMethod addCommerceShippingMethod(
			long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active,
			String engineKey, File imageFile, double priority,
			String trackingURL)
		throws PortalException {

		_checkCommerceChannel(ActionKeys.UPDATE, groupId);

		return commerceShippingMethodLocalService.addCommerceShippingMethod(
			getUserId(), groupId, nameMap, descriptionMap, active, engineKey,
			imageFile, priority, trackingURL);
	}

	@Override
	public CommerceShippingMethod createCommerceShippingMethod(
			long commerceShippingMethodId)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.fetchByPrimaryKey(
				commerceShippingMethodId);

		if (commerceShippingMethod != null) {
			_checkCommerceChannel(
				ActionKeys.UPDATE, commerceShippingMethod.getGroupId());
		}

		return commerceShippingMethodLocalService.createCommerceShippingMethod(
			commerceShippingMethodId);
	}

	@Override
	public void deleteCommerceAddressRestriction(
			long commerceAddressRestrictionId)
		throws PortalException {

		CommerceAddressRestriction commerceAddressRestriction =
			_commerceAddressRestrictionLocalService.
				getCommerceAddressRestriction(commerceAddressRestrictionId);

		_checkCommerceChannel(
			ActionKeys.UPDATE, commerceAddressRestriction.getGroupId());

		commerceShippingMethodLocalService.deleteCommerceAddressRestriction(
			commerceAddressRestrictionId);
	}

	@Override
	public void deleteCommerceAddressRestrictions(long commerceShippingMethodId)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.findByPrimaryKey(
				commerceShippingMethodId);

		_checkCommerceChannel(
			ActionKeys.UPDATE, commerceShippingMethod.getGroupId());

		_commerceAddressRestrictionLocalService.
			deleteCommerceAddressRestrictions(
				CommerceShippingMethod.class.getName(),
				commerceShippingMethodId);
	}

	@Override
	public void deleteCommerceShippingMethod(long commerceShippingMethodId)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.findByPrimaryKey(
				commerceShippingMethodId);

		_checkCommerceChannel(
			ActionKeys.UPDATE, commerceShippingMethod.getGroupId());

		commerceShippingMethodLocalService.deleteCommerceShippingMethod(
			commerceShippingMethod);
	}

	@Override
	public CommerceShippingMethod fetchCommerceShippingMethod(
			long groupId, String engineKey)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.fetchByG_E(groupId, engineKey);

		if (commerceShippingMethod != null) {
			_checkCommerceChannel(
				ActionKeys.VIEW, commerceShippingMethod.getGroupId());
		}

		return commerceShippingMethod;
	}

	@Override
	public List<CommerceAddressRestriction> getCommerceAddressRestrictions(
			long commerceShippingMethodId, int start, int end,
			OrderByComparator<CommerceAddressRestriction> orderByComparator)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.findByPrimaryKey(
				commerceShippingMethodId);

		_checkCommerceChannel(
			ActionKeys.VIEW, commerceShippingMethod.getGroupId());

		return commerceShippingMethodLocalService.
			getCommerceAddressRestrictions(
				commerceShippingMethodId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceAddressRestrictionsCount(
			long commerceShippingMethodId)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.findByPrimaryKey(
				commerceShippingMethodId);

		_checkCommerceChannel(
			ActionKeys.VIEW, commerceShippingMethod.getGroupId());

		return commerceShippingMethodLocalService.
			getCommerceAddressRestrictionsCount(commerceShippingMethodId);
	}

	@Override
	public CommerceShippingMethod getCommerceShippingMethod(
			long commerceShippingMethodId)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.findByPrimaryKey(
				commerceShippingMethodId);

		_checkCommerceChannel(
			ActionKeys.VIEW, commerceShippingMethod.getGroupId());

		return commerceShippingMethod;
	}

	@Override
	public List<CommerceShippingMethod> getCommerceShippingMethods(
			long groupId, boolean active, int start, int end,
			OrderByComparator<CommerceShippingMethod> orderByComparator)
		throws PortalException {

		_checkCommerceChannel(ActionKeys.VIEW, groupId);

		return commerceShippingMethodPersistence.findByG_A(
			groupId, active, start, end, orderByComparator);
	}

	@Override
	public List<CommerceShippingMethod> getCommerceShippingMethods(
			long groupId, int start, int end,
			OrderByComparator<CommerceShippingMethod> orderByComparator)
		throws PortalException {

		_checkCommerceChannel(ActionKeys.VIEW, groupId);

		return commerceShippingMethodPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceShippingMethod> getCommerceShippingMethods(
			long groupId, long countryId, boolean active)
		throws PortalException {

		_checkCommerceChannel(ActionKeys.VIEW, groupId);

		return commerceShippingMethodLocalService.getCommerceShippingMethods(
			groupId, countryId, active);
	}

	@Override
	public int getCommerceShippingMethodsCount(long groupId)
		throws PortalException {

		_checkCommerceChannel(ActionKeys.VIEW, groupId);

		return commerceShippingMethodLocalService.
			getCommerceShippingMethodsCount(groupId);
	}

	@Override
	public CommerceShippingMethod setActive(
			long commerceShippingMethodId, boolean active)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.fetchByPrimaryKey(
				commerceShippingMethodId);

		if (commerceShippingMethod != null) {
			_checkCommerceChannel(
				ActionKeys.UPDATE, commerceShippingMethod.getGroupId());
		}

		return commerceShippingMethodLocalService.setActive(
			commerceShippingMethodId, active);
	}

	@Override
	public CommerceShippingMethod updateCommerceShippingMethod(
			CommerceShippingMethod commerceShippingMethod)
		throws PortalException {

		_checkCommerceChannel(
			ActionKeys.UPDATE, commerceShippingMethod.getGroupId());

		return commerceShippingMethodLocalService.updateCommerceShippingMethod(
			commerceShippingMethod);
	}

	@Override
	public CommerceShippingMethod updateCommerceShippingMethod(
			long commerceShippingMethodId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, File imageFile,
			double priority, String trackingURL)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.findByPrimaryKey(
				commerceShippingMethodId);

		_checkCommerceChannel(
			ActionKeys.UPDATE, commerceShippingMethod.getGroupId());

		return commerceShippingMethodLocalService.updateCommerceShippingMethod(
			commerceShippingMethod.getCommerceShippingMethodId(), nameMap,
			descriptionMap, active, imageFile, priority, trackingURL);
	}

	private void _checkCommerceChannel(String actionId, long groupId)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(groupId);

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannel, actionId);
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