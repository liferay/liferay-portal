/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Alejandro Tardín
 */
public class DLAdminNavigationDisplayContext {

	public DLAdminNavigationDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_currentURLObj = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);

		_httpServletRequest = liferayPortletRequest.getHttpServletRequest();

		_dlRequestHelper = new DLRequestHelper(_httpServletRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<NavigationItem> getNavigationItems() {
		String navigation = ParamUtil.getString(
			_httpServletRequest, "navigation");

		return NavigationItemListBuilder.add(
			navigationItem -> _populateDocumentLibraryNavigationItem(
				navigationItem, navigation)
		).add(
			() -> DLPortletKeys.DOCUMENT_LIBRARY_ADMIN.equals(
				_dlRequestHelper.getPortletName()),
			navigationItem -> _populateFileEntryTypesNavigationItem(
				navigationItem, navigation)
		).add(
			() -> DLPortletKeys.DOCUMENT_LIBRARY_ADMIN.equals(
				_dlRequestHelper.getPortletName()),
			navigationItem -> _populateMetadataSetsNavigationItem(
				navigationItem, navigation)
		).build();
	}

	private void _populateDocumentLibraryNavigationItem(
		NavigationItem navigationItem, String navigation) {

		if (!navigation.equals("file_entry_types") &&
			!navigation.equals("file_entry_metadata_sets")) {

			navigationItem.setActive(true);
		}

		navigationItem.setHref(
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCRenderCommandName(
				"/document_library/view"
			).buildString());
		navigationItem.setLabel(
			LanguageUtil.get(
				_liferayPortletRequest.getHttpServletRequest(), "files"));
	}

	private void _populateFileEntryTypesNavigationItem(
		NavigationItem navigationItem, String navigation) {

		navigationItem.setActive(navigation.equals("file_entry_types"));
		navigationItem.setHref(
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setNavigation(
				"file_entry_types"
			).buildString());
		navigationItem.setLabel(
			LanguageUtil.get(
				_liferayPortletRequest.getHttpServletRequest(),
				"document-types"));
	}

	private void _populateMetadataSetsNavigationItem(
		NavigationItem navigationItem, String navigation) {

		if (navigation.equals("file_entry_metadata_sets")) {
			navigationItem.setActive(true);
		}

		navigationItem.setHref(
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setNavigation(
				"file_entry_metadata_sets"
			).setParameter(
				"groupId", _themeDisplay.getScopeGroupId()
			).buildString());
		navigationItem.setLabel(
			LanguageUtil.get(
				_liferayPortletRequest.getHttpServletRequest(),
				"metadata-sets"));
	}

	private final PortletURL _currentURLObj;
	private final DLRequestHelper _dlRequestHelper;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}