/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.model.CommerceAddressRestriction;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.service.base.CommerceAddressRestrictionLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CommerceAddressRestriction",
	service = AopService.class
)
public class CommerceAddressRestrictionLocalServiceImpl
	extends CommerceAddressRestrictionLocalServiceBaseImpl {

	@Override
	public CommerceAddressRestriction addCommerceAddressRestriction(
			long userId, long groupId, String className, long classPK,
			long countryId)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long commerceAddressRestrictionId = counterLocalService.increment();

		CommerceAddressRestriction commerceAddressRestriction =
			commerceAddressRestrictionPersistence.create(
				commerceAddressRestrictionId);

		commerceAddressRestriction.setGroupId(groupId);
		commerceAddressRestriction.setCompanyId(user.getCompanyId());
		commerceAddressRestriction.setUserId(user.getUserId());
		commerceAddressRestriction.setUserName(user.getFullName());
		commerceAddressRestriction.setClassName(className);
		commerceAddressRestriction.setClassPK(classPK);
		commerceAddressRestriction.setCountryId(countryId);

		return commerceAddressRestrictionPersistence.update(
			commerceAddressRestriction);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public CommerceAddressRestriction addCommerceAddressRestriction(
			String className, long classPK, long countryId,
			ServiceContext serviceContext)
		throws PortalException {

		return commerceAddressRestrictionLocalService.
			addCommerceAddressRestriction(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				className, classPK, countryId);
	}

	@Override
	public void deleteCommerceAddressRestrictions(
		String className, long classPK) {

		commerceAddressRestrictionPersistence.removeByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public CommerceAddressRestriction fetchCommerceAddressRestriction(
		String className, long classPK, long countryId) {

		return commerceAddressRestrictionPersistence.fetchByC_C_C(
			_classNameLocalService.getClassNameId(className), classPK,
			countryId);
	}

	@Override
	public List<CommerceAddressRestriction> getCommerceAddressRestrictions(
		String className, long classPK, int start, int end,
		OrderByComparator<CommerceAddressRestriction> orderByComparator) {

		return commerceAddressRestrictionPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), classPK, start,
			end, orderByComparator);
	}

	@Override
	public int getCommerceAddressRestrictionsCount(
		String className, long classPK) {

		return commerceAddressRestrictionPersistence.countByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public boolean isCommerceAddressRestricted(
		String className, long classPK, long countryId) {

		CommerceAddressRestriction commerceAddressRestriction =
			commerceAddressRestrictionLocalService.
				fetchCommerceAddressRestriction(className, classPK, countryId);

		if (commerceAddressRestriction != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isCommerceShippingMethodRestricted(
		long commerceShippingMethodId, long countryId) {

		return isCommerceAddressRestricted(
			CommerceShippingMethod.class.getName(), commerceShippingMethodId,
			countryId);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private UserLocalService _userLocalService;

}