/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.service.impl;

import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.wish.list.exception.GuestWishListItemMaxAllowedException;
import com.liferay.commerce.wish.list.internal.configuration.CommerceWishListConfiguration;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.model.CommerceWishListItem;
import com.liferay.commerce.wish.list.service.base.CommerceWishListItemLocalServiceBaseImpl;
import com.liferay.commerce.wish.list.service.persistence.CommerceWishListPersistence;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	configurationPid = "com.liferay.commerce.wish.list.internal.configuration.CommerceWishListConfiguration",
	property = "model.class.name=com.liferay.commerce.wish.list.model.CommerceWishListItem",
	service = AopService.class
)
public class CommerceWishListItemLocalServiceImpl
	extends CommerceWishListItemLocalServiceBaseImpl {

	public CommerceWishListItem addCommerceWishListItem(
			long userId, long commerceWishListId, String cpInstanceUuid,
			long cProductId, String json)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		CommerceWishList commerceWishList =
			_commerceWishListPersistence.findByPrimaryKey(commerceWishListId);

		_validate(
			commerceWishListId, cpInstanceUuid, cProductId, user.getUserId());

		CommerceWishListItem commerceWishListItem =
			commerceWishListItemPersistence.create(
				counterLocalService.increment());

		commerceWishListItem.setGroupId(commerceWishList.getGroupId());
		commerceWishListItem.setCompanyId(user.getCompanyId());
		commerceWishListItem.setUserId(user.getUserId());
		commerceWishListItem.setUserName(user.getFullName());
		commerceWishListItem.setCommerceWishListId(commerceWishListId);
		commerceWishListItem.setCPInstanceUuid(cpInstanceUuid);
		commerceWishListItem.setCProductId(cProductId);
		commerceWishListItem.setJson(json);

		return commerceWishListItemPersistence.update(commerceWishListItem);
	}

	public CommerceWishListItem addOrUpdateCommerceWishListItem(
			long userId, long commerceWishListId, String cpInstanceUuid,
			long cProductId, String json)
		throws PortalException {

		CommerceWishListItem commerceWishListItem =
			commerceWishListItemPersistence.fetchByCW_CPI_CP(
				commerceWishListId, cpInstanceUuid, cProductId);

		if (commerceWishListItem == null) {
			return commerceWishListItemLocalService.addCommerceWishListItem(
				userId, commerceWishListId, cpInstanceUuid, cProductId, json);
		}

		return commerceWishListItemLocalService.updateCommerceWishListItem(
			commerceWishListId, cpInstanceUuid, cProductId, json);
	}

	@Override
	public void deleteCommerceWishListItems(long commerceWishListId) {
		commerceWishListItemPersistence.removeByCommerceWishListId(
			commerceWishListId);
	}

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	@Override
	public void deleteCommerceWishListItemsByCPDefinitionId(
		long cpDefinitionId) {

		CPDefinition cpDefinition = _cpDefinitionLocalService.fetchCPDefinition(
			cpDefinitionId);

		if (cpDefinition != null) {
			commerceWishListItemPersistence.removeByCProductId(
				cpDefinition.getCProductId());
		}
	}

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	@Override
	public void deleteCommerceWishListItemsByCPInstanceId(long cpInstanceId) {
		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			cpInstanceId);

		if (cpInstance != null) {
			commerceWishListItemPersistence.removeByCPInstanceUuid(
				cpInstance.getCPInstanceUuid());
		}
	}

	@Override
	public CommerceWishListItem fetchCommerceWishListItem(
		long commerceWishListId, String cpInstanceUuid, long cProductId) {

		return commerceWishListItemPersistence.fetchByCW_CPI_CP(
			commerceWishListId, cpInstanceUuid, cProductId);
	}

	@Override
	public CommerceWishListItem getCommerceWishListItem(
			long commerceWishListId, String cpInstanceUuid, long cProductId)
		throws PortalException {

		return commerceWishListItemPersistence.findByCW_CPI_CP(
			commerceWishListId, cpInstanceUuid, cProductId);
	}

	@Override
	public int getCommerceWishListItemByContainsCPInstanceCount(
		long commerceWishListId, String cpInstanceUuid) {

		return commerceWishListItemPersistence.countByCW_CPI(
			commerceWishListId, cpInstanceUuid);
	}

	@Override
	public int getCommerceWishListItemByContainsCProductCount(
		long commerceWishListId, long cProductId) {

		return commerceWishListItemPersistence.countByCW_CP(
			commerceWishListId, cProductId);
	}

	@Override
	public List<CommerceWishListItem> getCommerceWishListItems(
		long commerceWishListId, int start, int end,
		OrderByComparator<CommerceWishListItem> orderByComparator) {

		return commerceWishListItemPersistence.findByCommerceWishListId(
			commerceWishListId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceWishListItemsCount(long commerceWishListId) {
		return commerceWishListItemPersistence.countByCommerceWishListId(
			commerceWishListId);
	}

	@Override
	public CommerceWishListItem updateCommerceWishListItem(
			long commerceWishListId, String cpInstanceUuid, long cProductId,
			String json)
		throws PortalException {

		CommerceWishListItem commerceWishListItem = getCommerceWishListItem(
			commerceWishListId, cpInstanceUuid, cProductId);

		_validate(
			commerceWishListId, cpInstanceUuid, cProductId,
			commerceWishListItem.getUserId());

		commerceWishListItem.setJson(json);

		return commerceWishListItemPersistence.update(commerceWishListItem);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_commerceWishListConfiguration = ConfigurableUtil.createConfigurable(
			CommerceWishListConfiguration.class, properties);
	}

	private void _validate(
			long commerceWishListId, String cpInstanceUuid, long cProductId,
			long userId)
		throws PortalException {

		if (userId == 0) {
			int count =
				commerceWishListItemPersistence.countByCommerceWishListId(
					commerceWishListId);

			if (count >=
					_commerceWishListConfiguration.
						guestWishListItemMaxAllowed()) {

				throw new GuestWishListItemMaxAllowedException();
			}
		}

		if (Validator.isNotNull(cpInstanceUuid)) {
			CPInstance cpInstance = _cpInstanceLocalService.getCProductInstance(
				cProductId, cpInstanceUuid);

			if (cpInstance == null) {
				CProduct cProduct = _cProductLocalService.getCProduct(
					cProductId);

				throw new NoSuchCPInstanceException(
					StringBundler.concat(
						"CPInstance ", cpInstanceUuid,
						" belongs to a different CPDefinition than ",
						cProduct.getPublishedCPDefinitionId()));
			}
		}
	}

	private CommerceWishListConfiguration _commerceWishListConfiguration;

	@Reference
	private CommerceWishListPersistence _commerceWishListPersistence;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private UserLocalService _userLocalService;

}