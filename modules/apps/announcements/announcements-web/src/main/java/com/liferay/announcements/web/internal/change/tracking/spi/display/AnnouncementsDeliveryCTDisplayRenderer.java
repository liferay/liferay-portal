/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.change.tracking.spi.display;

import com.liferay.announcements.kernel.model.AnnouncementsDelivery;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(service = CTDisplayRenderer.class)
public class AnnouncementsDeliveryCTDisplayRenderer
	extends BaseCTDisplayRenderer<AnnouncementsDelivery> {

	@Override
	public String getEditURL(
		HttpServletRequest httpServletRequest,
		AnnouncementsDelivery announcementsDelivery) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!UserPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				announcementsDelivery.getUserId(), ActionKeys.UPDATE)) {

			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, UsersAdminPortletKeys.USERS_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/users_admin/edit_user"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"p_u_i_d", announcementsDelivery.getUserId()
		).setParameter(
			"screenNavigationCategoryKey",
			UserScreenNavigationEntryConstants.CATEGORY_KEY_PREFERENCES
		).setParameter(
			"screenNavigationEntryKey",
			UserScreenNavigationEntryConstants.
				ENTRY_KEY_ALERTS_AND_ANNOUNCEMENTS_DELIVERY
		).buildString();
	}

	@Override
	public Class<AnnouncementsDelivery> getModelClass() {
		return AnnouncementsDelivery.class;
	}

	@Override
	public String getTitle(
			Locale locale, AnnouncementsDelivery announcementsDelivery)
		throws PortalException {

		return _language.get(locale, announcementsDelivery.getType());
	}

	@Override
	public String getTypeName(Locale locale) {
		return _language.get(locale, "alerts-and-announcements-delivery");
	}

	@Override
	public boolean isHideable(AnnouncementsDelivery announcementsDelivery) {
		return !announcementsDelivery.isEmail();
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<AnnouncementsDelivery> displayBuilder) {

		AnnouncementsDelivery announcementsDelivery = displayBuilder.getModel();

		displayBuilder.display(
			"user",
			() -> {
				User user = _userLocalService.getUser(
					announcementsDelivery.getUserId());

				return user.getFullName();
			}
		).display(
			"type", announcementsDelivery.getType()
		).display(
			"email", announcementsDelivery.isEmail()
		).display(
			"sms", announcementsDelivery.isSms()
		).display(
			"website", announcementsDelivery.isWebsite()
		);
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}