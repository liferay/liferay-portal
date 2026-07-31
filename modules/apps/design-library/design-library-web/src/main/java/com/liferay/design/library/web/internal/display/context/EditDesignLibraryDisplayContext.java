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
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Mario Leandro
 * @author Thiago Buarque
 */
public class EditDesignLibraryDisplayContext
	extends BaseDesignLibraryDisplayContext {

	public EditDesignLibraryDisplayContext(
		DepotEntry depotEntry, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(depotEntry, httpServletRequest);

		_liferayPortletResponse = liferayPortletResponse;
	}

	public Map<String, Object> getProps() throws PortalException {
		Group group = getGroup();

		return HashMapBuilder.<String, Object>put(
			"backURL", _getBackURL()
		).put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"groupId", group.getGroupId()
		).put(
			"portletId", themeDisplay.getPpid()
		).build();
	}

	public void setPortletDisplay(
			PortletDisplay portletDisplay, RenderResponse renderResponse)
		throws PortalException {

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(_getBackURL());

		Group group = getGroup();

		renderResponse.setTitle(
			LanguageUtil.format(
				httpServletRequest, "x-settings",
				group.getName(themeDisplay.getLocale()), false));
	}

	private String _getBackURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/design_library/view_resources_design_library"
		).setParameter(
			"designLibraryEntryId", depotEntry.getDepotEntryId()
		).buildString();
	}

	private final LiferayPortletResponse _liferayPortletResponse;

}