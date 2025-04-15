/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = CTDisplayRenderer.class)
public class LayoutSetCTDisplayRenderer
	extends BaseCTDisplayRenderer<LayoutSet> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, LayoutSet layoutSet)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = layoutSet.getGroup();

		if (!GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), group,
				ActionKeys.UPDATE)) {

			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, LayoutAdminPortletKeys.GROUP_PAGES,
				0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout_set"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"groupId", layoutSet.getGroupId()
		).setParameter(
			"privateLayout", layoutSet.isPrivateLayout()
		).buildString();
	}

	@Override
	public Class<LayoutSet> getModelClass() {
		return LayoutSet.class;
	}

	@Override
	public String getTitle(Locale locale, LayoutSet layoutSet)
		throws PortalException {

		Group group = layoutSet.getGroup();

		if (!group.isLayoutSetPrototype() && !group.isLayoutPrototype()) {
			return group.getLayoutRootNodeName(
				layoutSet.isPrivateLayout(), locale);
		}

		return _language.get(locale, "page-set");
	}

	@Override
	public String getTypeName(Locale locale) {
		return _language.get(locale, "page-set");
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}