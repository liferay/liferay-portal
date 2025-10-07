/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.exception.CommerceShippingMethodEngineKeyException;
import com.liferay.commerce.exception.CommerceShippingMethodNameException;
import com.liferay.commerce.model.CommerceAddressRestriction;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.service.CommerceAddressRestrictionLocalService;
import com.liferay.commerce.service.base.CommerceShippingMethodLocalServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

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
	property = "model.class.name=com.liferay.commerce.model.CommerceShippingMethod",
	service = AopService.class
)
public class CommerceShippingMethodLocalServiceImpl
	extends CommerceShippingMethodLocalServiceBaseImpl {

	@Override
	public CommerceAddressRestriction addCommerceAddressRestriction(
			long userId, long groupId, long commerceShippingMethodId,
			long countryId)
		throws PortalException {

		return _commerceAddressRestrictionLocalService.
			addCommerceAddressRestriction(
				userId, groupId, CommerceShippingMethod.class.getName(),
				commerceShippingMethodId, countryId);
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

		return commerceShippingMethodLocalService.addCommerceAddressRestriction(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			commerceShippingMethodId, countryId);
	}

	@Override
	public CommerceShippingMethod addCommerceShippingMethod(
			long userId, long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active,
			String engineKey, File imageFile, double priority,
			String trackingURL)
		throws PortalException {

		// Commerce shipping method

		User user = _userLocalService.getUser(userId);

		if ((imageFile != null) && !imageFile.exists()) {
			imageFile = null;
		}

		_validate(nameMap, engineKey);

		long commerceShippingMethodId = counterLocalService.increment();

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.create(commerceShippingMethodId);

		commerceShippingMethod.setGroupId(groupId);
		commerceShippingMethod.setCompanyId(user.getCompanyId());
		commerceShippingMethod.setUserId(user.getUserId());
		commerceShippingMethod.setUserName(user.getFullName());
		commerceShippingMethod.setNameMap(nameMap);
		commerceShippingMethod.setDescriptionMap(descriptionMap);

		if (imageFile != null) {
			commerceShippingMethod.setImageId(counterLocalService.increment());
		}

		commerceShippingMethod.setActive(active);
		commerceShippingMethod.setEngineKey(engineKey);
		commerceShippingMethod.setPriority(priority);
		commerceShippingMethod.setTrackingURL(trackingURL);

		commerceShippingMethod = commerceShippingMethodPersistence.update(
			commerceShippingMethod);

		// Image

		if (imageFile != null) {
			_imageLocalService.updateImage(
				commerceShippingMethod.getCompanyId(),
				commerceShippingMethod.getImageId(), imageFile);
		}

		return commerceShippingMethod;
	}

	@Override
	public void deleteCommerceAddressRestriction(
			long commerceAddressRestrictionId)
		throws PortalException {

		_commerceAddressRestrictionLocalService.
			deleteCommerceAddressRestriction(commerceAddressRestrictionId);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceShippingMethod deleteCommerceShippingMethod(
			CommerceShippingMethod commerceShippingMethod)
		throws PortalException {

		// Commerce shipping method

		commerceShippingMethodPersistence.remove(commerceShippingMethod);

		// Image

		if (commerceShippingMethod.getImageId() > 0) {
			_imageLocalService.deleteImage(commerceShippingMethod.getImageId());
		}

		// Commerce address restrictions

		_commerceAddressRestrictionLocalService.
			deleteCommerceAddressRestrictions(
				CommerceShippingMethod.class.getName(),
				commerceShippingMethod.getCommerceShippingMethodId());

		return commerceShippingMethod;
	}

	@Override
	public CommerceShippingMethod deleteCommerceShippingMethod(
			long commerceShippingMethodId)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.findByPrimaryKey(
				commerceShippingMethodId);

		return commerceShippingMethodLocalService.deleteCommerceShippingMethod(
			commerceShippingMethod);
	}

	@Override
	public void deleteCommerceShippingMethods(long groupId)
		throws PortalException {

		List<CommerceShippingMethod> commerceShippingMethods =
			commerceShippingMethodPersistence.findByGroupId(groupId);

		for (CommerceShippingMethod commerceShippingMethod :
				commerceShippingMethods) {

			commerceShippingMethodLocalService.deleteCommerceShippingMethod(
				commerceShippingMethod);
		}
	}

	@Override
	public CommerceShippingMethod fetchCommerceShippingMethod(
		long groupId, String engineKey) {

		return commerceShippingMethodPersistence.fetchByG_E(groupId, engineKey);
	}

	@Override
	public List<CommerceAddressRestriction> getCommerceAddressRestrictions(
		long commerceShippingMethodId, int start, int end,
		OrderByComparator<CommerceAddressRestriction> orderByComparator) {

		return _commerceAddressRestrictionLocalService.
			getCommerceAddressRestrictions(
				CommerceShippingMethod.class.getName(),
				commerceShippingMethodId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceAddressRestrictionsCount(
		long commerceShippingMethodId) {

		return _commerceAddressRestrictionLocalService.
			getCommerceAddressRestrictionsCount(
				CommerceShippingMethod.class.getName(),
				commerceShippingMethodId);
	}

	@Override
	public List<CommerceShippingMethod> getCommerceShippingMethods(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return commerceShippingMethodPersistence.findByG_A(
			groupId, active, start, end, orderByComparator);
	}

	@Override
	public List<CommerceShippingMethod> getCommerceShippingMethods(
		long groupId, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return commerceShippingMethodPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceShippingMethod> getCommerceShippingMethods(
		long groupId, long countryId, boolean active) {

		return TransformUtil.transform(
			commerceShippingMethodPersistence.findByG_A(groupId, active),
			commerceShippingMethod -> {
				boolean restricted =
					_commerceAddressRestrictionLocalService.
						isCommerceAddressRestricted(
							CommerceShippingMethod.class.getName(),
							commerceShippingMethod.
								getCommerceShippingMethodId(),
							countryId);

				if (!restricted) {
					return commerceShippingMethod;
				}

				return null;
			});
	}

	@Override
	public int getCommerceShippingMethodsCount(long groupId) {
		return commerceShippingMethodPersistence.countByGroupId(groupId);
	}

	@Override
	public int getCommerceShippingMethodsCount(long groupId, boolean active) {
		return commerceShippingMethodPersistence.countByG_A(groupId, active);
	}

	@Override
	public CommerceShippingMethod setActive(
			long commerceShippingMethodId, boolean active)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.findByPrimaryKey(
				commerceShippingMethodId);

		commerceShippingMethod.setActive(active);

		return commerceShippingMethodPersistence.update(commerceShippingMethod);
	}

	@Override
	public CommerceShippingMethod updateCommerceShippingMethod(
			long commerceShippingMethodId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, File imageFile,
			double priority, String trackingURL)
		throws PortalException {

		// Commerce shipping method

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethodPersistence.findByPrimaryKey(
				commerceShippingMethodId);

		if ((imageFile != null) && !imageFile.exists()) {
			imageFile = null;
		}

		commerceShippingMethod.setNameMap(nameMap);
		commerceShippingMethod.setDescriptionMap(descriptionMap);

		if ((imageFile != null) && (commerceShippingMethod.getImageId() <= 0)) {
			commerceShippingMethod.setImageId(counterLocalService.increment());
		}

		commerceShippingMethod.setActive(active);
		commerceShippingMethod.setPriority(priority);
		commerceShippingMethod.setTrackingURL(trackingURL);

		commerceShippingMethod = commerceShippingMethodPersistence.update(
			commerceShippingMethod);

		// Image

		if (imageFile != null) {
			_imageLocalService.updateImage(
				commerceShippingMethod.getCompanyId(),
				commerceShippingMethod.getImageId(), imageFile);
		}

		return commerceShippingMethod;
	}

	private void _validate(Map<Locale, String> nameMap, String engineKey)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		String name = nameMap.get(locale);

		if (Validator.isNull(name)) {
			throw new CommerceShippingMethodNameException();
		}

		if (Validator.isNull(engineKey)) {
			throw new CommerceShippingMethodEngineKeyException();
		}
	}

	@Reference
	private CommerceAddressRestrictionLocalService
		_commerceAddressRestrictionLocalService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private UserLocalService _userLocalService;

}