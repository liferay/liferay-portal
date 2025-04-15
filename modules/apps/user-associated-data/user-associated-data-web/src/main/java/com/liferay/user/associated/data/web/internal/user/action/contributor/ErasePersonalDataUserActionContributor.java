/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.user.action.contributor;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.users.admin.user.action.contributor.UserActionContributor;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = UserActionContributor.class)
public class ErasePersonalDataUserActionContributor
	extends BaseUADUserActionContributor {

	@Override
	public String getConfirmationMessage(PortletRequest portletRequest) {
		return _language.get(
			getResourceBundle(getLocale(portletRequest)),
			"the-user-must-be-deactivated-before-starting-the-data-erasure-" +
				"process.-are-you-sure-you-want-to-deactivate-the-user");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse,
		User user, User selectedUser) {

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			portletRequest, UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
			PortletRequest.ACTION_PHASE);

		liferayPortletURL.setParameter(
			ActionRequest.ACTION_NAME,
			"/user_associated_data/erase_personal_data");
		liferayPortletURL.setParameter(
			"p_u_i_d", String.valueOf(selectedUser.getUserId()));

		return liferayPortletURL.toString();
	}

	@Override
	public boolean isShowConfirmationMessage(User selUser) {
		return selUser.isActive();
	}

	@Override
	protected String getKey() {
		return "delete-personal-data";
	}

	@Override
	protected String getMVCRenderCommandName() {
		return null;
	}

	@Reference
	private Language _language;

}