/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.service.impl;

import com.liferay.commerce.wish.list.constants.CommerceWishListActionKeys;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.service.base.CommerceWishListServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceWishList"
	},
	service = AopService.class
)
public class CommerceWishListServiceImpl
	extends CommerceWishListServiceBaseImpl {

	@Override
	public CommerceWishList addCommerceWishList(
			long groupId, String name, boolean defaultWishList)
		throws PortalException {

		_checkPortletResourcePermission(
			groupId, CommerceWishListActionKeys.ADD_COMMERCE_WISH_LIST);

		return commerceWishListLocalService.addCommerceWishList(
			getUserId(), groupId, name, defaultWishList);
	}

	@Override
	public void deleteCommerceWishList(long commerceWishListId)
		throws PortalException {

		_commerceWishListModelResourcePermission.check(
			getPermissionChecker(), commerceWishListId, ActionKeys.DELETE);

		commerceWishListLocalService.deleteCommerceWishList(commerceWishListId);
	}

	@Override
	public CommerceWishList fetchCommerceWishList(
			long groupId, boolean defaultWishList,
			OrderByComparator<CommerceWishList> orderByComparator)
		throws PortalException {

		CommerceWishList commerceWishList =
			commerceWishListPersistence.fetchByG_U_D_First(
				groupId, getUserId(), defaultWishList, orderByComparator);

		if (commerceWishList != null) {
			_commerceWishListModelResourcePermission.check(
				getPermissionChecker(), commerceWishList, ActionKeys.VIEW);
		}

		return commerceWishList;
	}

	@Override
	public CommerceWishList getCommerceWishList(long commerceWishListId)
		throws PortalException {

		_commerceWishListModelResourcePermission.check(
			getPermissionChecker(), commerceWishListId, ActionKeys.VIEW);

		return commerceWishListPersistence.findByPrimaryKey(commerceWishListId);
	}

	@Override
	public List<CommerceWishList> getCommerceWishLists(
			long groupId, int start, int end,
			OrderByComparator<CommerceWishList> orderByComparator)
		throws PortalException {

		_checkPortletResourcePermission(
			groupId, CommerceWishListActionKeys.VIEW_COMMERCE_WISH_LISTS);

		return commerceWishListPersistence.findByG_U(
			groupId, getUserId(), start, end, orderByComparator);
	}

	@Override
	public int getCommerceWishListsCount(long groupId) throws PortalException {
		_checkPortletResourcePermission(
			groupId, CommerceWishListActionKeys.VIEW_COMMERCE_WISH_LISTS);

		return commerceWishListPersistence.countByG_U(groupId, getUserId());
	}

	@Override
	public CommerceWishList getDefaultCommerceWishList(long groupId)
		throws PortalException {

		CommerceWishList commerceWishList =
			commerceWishListLocalService.getDefaultCommerceWishList(
				getUserId(), groupId, null);

		if (commerceWishList != null) {
			_commerceWishListModelResourcePermission.check(
				getPermissionChecker(), commerceWishList, ActionKeys.VIEW);
		}

		return commerceWishList;
	}

	@Override
	public CommerceWishList updateCommerceWishList(
			long commerceWishListId, String name, boolean defaultWishList)
		throws PortalException {

		_commerceWishListModelResourcePermission.check(
			getPermissionChecker(), commerceWishListId, ActionKeys.UPDATE);

		return commerceWishListLocalService.updateCommerceWishList(
			commerceWishListId, name, defaultWishList);
	}

	private void _checkPortletResourcePermission(long groupId, String actionId)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceWishListModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), groupId, actionId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.wish.list.model.CommerceWishList)"
	)
	private ModelResourcePermission<CommerceWishList>
		_commerceWishListModelResourcePermission;

}