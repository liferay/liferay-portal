/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Mario Leandro
 * @author Thiago Buarque
 */
public class EditDesignLibraryDisplayContext {

	public EditDesignLibraryDisplayContext(
		DepotEntry depotEntry, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_depotEntry = depotEntry;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public Map<String, Object> getProps() throws PortalException {
		Group group = _depotEntry.getGroup();

		return HashMapBuilder.<String, Object>put(
			"backURL", _getBackURL()
		).put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"groupId", group.getGroupId()
		).put(
			"portletId", _getThemeDisplay().getPpid()
		).build();
	}

	public void setPortletDisplay(
			PortletDisplay portletDisplay, RenderResponse renderResponse)
		throws PortalException {

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(_getBackURL());

		Group group = _depotEntry.getGroup();

		renderResponse.setTitle(
			LanguageUtil.format(
				_httpServletRequest, "x-settings",
				group.getName(_getThemeDisplay().getLocale()), false));
	}

	private String _getBackURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/design_library/view_resources_design_library"
		).setParameter(
			"designLibraryEntryId", _depotEntry.getDepotEntryId()
		).buildString();
	}

	private ThemeDisplay _getThemeDisplay() {
		return (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	private final DepotEntry _depotEntry;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}