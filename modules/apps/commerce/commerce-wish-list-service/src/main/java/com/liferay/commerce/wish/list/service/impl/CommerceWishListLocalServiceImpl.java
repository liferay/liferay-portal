/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.service.impl;

import com.liferay.commerce.wish.list.exception.CommerceWishListNameException;
import com.liferay.commerce.wish.list.exception.GuestWishListMaxAllowedException;
import com.liferay.commerce.wish.list.exception.RequiredCommerceWishListException;
import com.liferay.commerce.wish.list.internal.configuration.CommerceWishListConfiguration;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.model.CommerceWishListItem;
import com.liferay.commerce.wish.list.service.CommerceWishListItemLocalService;
import com.liferay.commerce.wish.list.service.base.CommerceWishListLocalServiceBaseImpl;
import com.liferay.commerce.wish.list.service.persistence.CommerceWishListItemPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	configurationPid = "com.liferay.commerce.wish.list.internal.configuration.CommerceWishListConfiguration",
	property = "model.class.name=com.liferay.commerce.wish.list.model.CommerceWishList",
	service = AopService.class
)
public class CommerceWishListLocalServiceImpl
	extends CommerceWishListLocalServiceBaseImpl {

	@Override
	public CommerceWishList addCommerceWishList(
			long userId, long groupId, String name, boolean defaultWishList)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		if (user.isGuestUser()) {
			_validateGuestWishLists();
		}

		_validate(0, groupId, user.getUserId(), name, defaultWishList);

		long commerceWishListId = counterLocalService.increment();

		CommerceWishList commerceWishList = commerceWishListPersistence.create(
			commerceWishListId);

		commerceWishList.setGroupId(groupId);
		commerceWishList.setCompanyId(user.getCompanyId());
		commerceWishList.setUserId(user.getUserId());
		commerceWishList.setUserName(user.getFullName());
		commerceWishList.setName(name);
		commerceWishList.setDefaultWishList(defaultWishList);

		return commerceWishListPersistence.update(commerceWishList);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceWishList deleteCommerceWishList(
			CommerceWishList commerceWishList)
		throws PortalException {

		if (commerceWishList.isDefaultWishList()) {
			throw new RequiredCommerceWishListException();
		}

		commerceWishListPersistence.remove(commerceWishList);

		_commerceWishListItemLocalService.deleteCommerceWishListItems(
			commerceWishList.getCommerceWishListId());

		return commerceWishList;
	}

	@Override
	public CommerceWishList deleteCommerceWishList(long commerceWishListId)
		throws PortalException {

		CommerceWishList commerceWishList =
			commerceWishListPersistence.findByPrimaryKey(commerceWishListId);

		return commerceWishListLocalService.deleteCommerceWishList(
			commerceWishList);
	}

	@Override
	public void deleteCommerceWishLists(long userId, Date date) {
		commerceWishListPersistence.removeByU_LtC(userId, date);
	}

	@Override
	public void deleteCommerceWishListsByGroupId(long groupId) {
		List<CommerceWishList> commerceWishLists =
			commerceWishListPersistence.findByGroupId(groupId);

		for (CommerceWishList commerceWishList : commerceWishLists) {
			commerceWishListLocalService.forceDeleteCommerceWishList(
				commerceWishList);
		}
	}

	@Override
	public void deleteCommerceWishListsByUserId(long userId) {
		List<CommerceWishList> commerceWishLists =
			commerceWishListPersistence.findByUserId(userId);

		for (CommerceWishList commerceWishList : commerceWishLists) {
			commerceWishListLocalService.forceDeleteCommerceWishList(
				commerceWishList);
		}
	}

	@Override
	public CommerceWishList fetchCommerceWishList(
		long userId, long groupId, boolean defaultWishList,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return commerceWishListPersistence.fetchByG_U_D_First(
			groupId, userId, defaultWishList, orderByComparator);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceWishList forceDeleteCommerceWishList(
		CommerceWishList commerceWishList) {

		commerceWishListPersistence.remove(commerceWishList);

		_commerceWishListItemLocalService.deleteCommerceWishListItems(
			commerceWishList.getCommerceWishListId());

		return commerceWishList;
	}

	@Override
	public List<CommerceWishList> getCommerceWishLists(
		long groupId, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return commerceWishListPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceWishList> getCommerceWishLists(
		long userId, long groupId, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return commerceWishListPersistence.findByG_U(
			groupId, userId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceWishListsCount(long groupId) {
		return commerceWishListPersistence.countByGroupId(groupId);
	}

	@Override
	public int getCommerceWishListsCount(long userId, long groupId) {
		return commerceWishListPersistence.countByG_U(groupId, userId);
	}

	@Override
	@ThreadLocalCachable
	public CommerceWishList getDefaultCommerceWishList(
			long userId, long groupId, String guestUuid)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CommerceWishList guestCommerceWishList = null;

		if (Validator.isNotNull(guestUuid)) {
			guestCommerceWishList =
				commerceWishListLocalService.
					fetchCommerceWishListByUuidAndGroupId(guestUuid, groupId);

			if ((guestCommerceWishList != null) &&
				!guestCommerceWishList.isGuestWishList()) {

				guestCommerceWishList = null;
			}
		}

		if (user.isGuestUser()) {
			return guestCommerceWishList;
		}

		CommerceWishList commerceWishList =
			commerceWishListPersistence.fetchByG_U_D_First(
				groupId, userId, true, null);

		if (commerceWishList == null) {
			commerceWishList = commerceWishListPersistence.fetchByG_U_D_First(
				groupId, userId, false, null);

			if (commerceWishList != null) {
				commerceWishList.setDefaultWishList(true);

				commerceWishList = commerceWishListPersistence.update(
					commerceWishList);
			}
		}

		if (guestCommerceWishList != null) {
			_mergeCommerceWishList(
				guestCommerceWishList.getCommerceWishListId(),
				commerceWishList.getCommerceWishListId());
		}

		return commerceWishList;
	}

	@Override
	public CommerceWishList updateCommerceWishList(
			long commerceWishListId, String name, boolean defaultWishList)
		throws PortalException {

		CommerceWishList commerceWishList =
			commerceWishListPersistence.findByPrimaryKey(commerceWishListId);

		_validate(
			commerceWishList.getCommerceWishListId(),
			commerceWishList.getGroupId(), commerceWishList.getUserId(), name,
			defaultWishList);

		commerceWishList.setName(name);
		commerceWishList.setDefaultWishList(defaultWishList);

		return commerceWishListPersistence.update(commerceWishList);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_commerceWishListConfiguration = ConfigurableUtil.createConfigurable(
			CommerceWishListConfiguration.class, properties);
	}

	private void _mergeCommerceWishList(
			long fromCommerceWishListId, long toCommerceWishListId)
		throws PortalException {

		// Commerce wish list items

		List<CommerceWishListItem> fromCommerceWishListItems =
			_commerceWishListItemPersistence.findByCommerceWishListId(
				fromCommerceWishListId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		List<CommerceWishListItem> toCommerceWishListItems =
			_commerceWishListItemPersistence.findByCommerceWishListId(
				toCommerceWishListId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		for (CommerceWishListItem fromCommerceWishListItem :
				fromCommerceWishListItems) {

			String json = fromCommerceWishListItem.getJson();

			boolean found = false;

			for (CommerceWishListItem toCommerceWishListItem :
					toCommerceWishListItems) {

				if ((fromCommerceWishListItem.getCProductId() ==
						toCommerceWishListItem.getCProductId()) &&
					Objects.equals(
						fromCommerceWishListItem.getCPInstanceUuid(),
						toCommerceWishListItem.getCPInstanceUuid()) &&
					Objects.equals(json, toCommerceWishListItem.getJson())) {

					found = true;

					break;
				}
			}

			if (!found) {
				_commerceWishListItemLocalService.addCommerceWishListItem(
					fromCommerceWishListItem.getUserId(), toCommerceWishListId,
					fromCommerceWishListItem.getCPInstanceUuid(),
					fromCommerceWishListItem.getCProductId(), json);
			}
		}

		// Commerce wish list

		commerceWishListLocalService.deleteCommerceWishList(
			fromCommerceWishListId);
	}

	private void _validate(
			long commerceWishListId, long groupId, long userId, String name,
			boolean defaultWishList)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new CommerceWishListNameException();
		}

		if (defaultWishList) {
			List<CommerceWishList> commerceWishLists =
				commerceWishListPersistence.findByG_U_D(groupId, userId, true);

			for (CommerceWishList commerceWishList : commerceWishLists) {
				if (commerceWishList.getCommerceWishListId() !=
						commerceWishListId) {

					commerceWishList.setDefaultWishList(false);

					commerceWishListPersistence.update(commerceWishList);
				}
			}
		}
	}

	private void _validateGuestWishLists() throws PortalException {
		int count = commerceWishListPersistence.countByUserId(
			UserConstants.USER_ID_DEFAULT);

		if (count >= _commerceWishListConfiguration.guestWishListMaxAllowed()) {
			throw new GuestWishListMaxAllowedException();
		}
	}

	private CommerceWishListConfiguration _commerceWishListConfiguration;

	@Reference
	private CommerceWishListItemLocalService _commerceWishListItemLocalService;

	@Reference
	private CommerceWishListItemPersistence _commerceWishListItemPersistence;

	@Reference
	private UserLocalService _userLocalService;

}