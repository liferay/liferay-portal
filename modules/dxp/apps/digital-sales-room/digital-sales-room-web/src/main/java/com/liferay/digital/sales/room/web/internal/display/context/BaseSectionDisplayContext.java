/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
public abstract class BaseSectionDisplayContext {

	public BaseSectionDisplayContext(
		Map<String, Object> configurationMap,
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition,
		ObjectEntryService objectEntryService) {

		this.configurationMap = configurationMap;
		this.httpServletRequest = httpServletRequest;
		this.objectDefinition = objectDefinition;

		_objectEntryService = objectEntryService;

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public abstract String getAPIURL();

	public List<DropdownItem> getBulkActionDropdownItems() {
		return null;
	}

	public CreationMenu getCreationMenu() throws Exception {
		return null;
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return null;
	}

	protected boolean hasAddObjectEntryPortletResourcePermission()
		throws Exception {

		return _objectEntryService.hasPortletResourcePermission(
			themeDisplay.getScopeGroupId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);
	}

	protected final Map<String, Object> configurationMap;
	protected final HttpServletRequest httpServletRequest;
	protected final ObjectDefinition objectDefinition;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.digital.sales.room.web)"
	)
	protected ServletContext servletContext;

	protected final ThemeDisplay themeDisplay;

	private final ObjectEntryService _objectEntryService;

}