/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.user.action.contributor;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.user.associated.data.anonymizer.UADAnonymousUserProvider;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.users.admin.user.action.contributor.BaseUserActionContributor;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
public abstract class BaseUADUserActionContributor
	extends BaseUserActionContributor {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return LanguageUtil.get(
			getResourceBundle(getLocale(portletRequest)), getKey());
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse,
		User user, User selectedUser) {

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			portletRequest, UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
			PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter(
			"p_u_i_d", String.valueOf(selectedUser.getUserId()));
		liferayPortletURL.setParameter(
			"mvcRenderCommandName", getMVCRenderCommandName());

		return liferayPortletURL.toString();
	}

	@Override
	public boolean isShow(
		PortletRequest portletRequest, User user, User selectedUser) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (Objects.equals(user, selectedUser) ||
			!permissionChecker.isCompanyAdmin() ||
			uadAnonymousUserProvider.isAnonymousUser(selectedUser)) {

			return false;
		}

		return true;
	}

	protected abstract String getKey();

	protected abstract String getMVCRenderCommandName();

	@Reference
	protected UADAnonymousUserProvider uadAnonymousUserProvider;

}