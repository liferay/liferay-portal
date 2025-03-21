/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.contacts.constants.ContactsPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "jakarta.portlet.name=" + ContactsPortletKeys.CONCTACTS_CENTER,
	service = AssetRendererFactory.class
)
public class UserAssetRendererFactory extends BaseAssetRendererFactory<User> {

	public static final String TYPE = "user";

	public UserAssetRendererFactory() {
		setSearchable(true);
		setSelectable(false);
	}

	@Override
	public AssetRenderer<User> getAssetRenderer(long classPK, int type)
		throws PortalException {

		User user = _userLocalService.getUserById(classPK);

		UserAssetRenderer userAssetRenderer = new UserAssetRenderer(user);

		userAssetRenderer.setAssetRendererType(type);
		userAssetRenderer.setServletContext(_servletContext);

		return userAssetRenderer;
	}

	@Override
	public AssetRenderer<User> getAssetRenderer(long groupId, String urlTitle)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		User user = _userLocalService.getUserByScreenName(
			group.getCompanyId(), urlTitle);

		return new UserAssetRenderer(user);
	}

	@Override
	public String getClassName() {
		return User.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "user";
	}

	@Override
	public String getPortletId() {
		return ContactsPortletKeys.CONCTACTS_CENTER;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return UserPermissionUtil.contains(
			permissionChecker, classPK, actionId);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.contacts.web)")
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

}