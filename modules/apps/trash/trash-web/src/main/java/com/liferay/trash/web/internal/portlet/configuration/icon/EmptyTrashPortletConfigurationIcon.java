/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.configuration.icon.BaseJSPPortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.constants.TrashPortletKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides an implementation of <code>PortletConfigurationIcon</code> (in
 * <code>com.liferay.portal.kernel</code>) to render the <i>Empty the Recycle
 * Bin</i> menu item for the Options menu.
 *
 * @author Eudaldo Alonso
 */
@Component(
	property = "jakarta.portlet.name=" + TrashPortletKeys.TRASH,
	service = PortletConfigurationIcon.class
)
public class EmptyTrashPortletConfigurationIcon
	extends BaseJSPPortletConfigurationIcon {

	@Override
	public Map<String, Object> getContext(PortletRequest portletRequest) {
		return HashMapBuilder.<String, Object>put(
			"action", getNamespace(portletRequest) + "emptyTrash"
		).put(
			"emptyTrashURL", _getEmptyTrashURL(portletRequest)
		).put(
			"globalAction", true
		).build();
	}

	@Override
	public String getIconCssClass() {
		return "restore";
	}

	@Override
	public String getJspPath() {
		return "/configuration/icon/empty_trash.jsp";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "empty-trash");
	}

	@Override
	public double getWeight() {
		return 100.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		if (!CTCollectionThreadLocal.isProductionMode()) {
			return false;
		}

		String keywords = ParamUtil.getString(portletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			return false;
		}

		return true;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private String _getEmptyTrashURL(PortletRequest portletRequest) {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, TrashPortletKeys.TRASH,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"emptyTrash"
		).setParameter(
			"groupId",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)portletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getScopeGroupId();
			}
		).buildString();
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.trash.web)")
	private ServletContext _servletContext;

}