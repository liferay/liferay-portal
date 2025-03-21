/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.web.internal.display.context.SearchResultPreferences;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.document.DocumentFormPermissionChecker;
import com.liferay.portal.search.web.internal.document.DocumentFormPermissionCheckerImpl;

import jakarta.portlet.PortletPreferences;

/**
 * @author André de Oliveira
 */
public class SearchPortletSearchResultPreferences
	implements SearchResultPreferences {

	public SearchPortletSearchResultPreferences(
		PortletPreferences portletPreferences,
		ThemeDisplaySupplier themeDisplaySupplier) {

		_portletPreferences = portletPreferences;

		_documentFormPermissionChecker = new DocumentFormPermissionCheckerImpl(
			themeDisplaySupplier.getThemeDisplay());
	}

	@Override
	public String getFieldsToDisplay() {
		return StringPool.BLANK;
	}

	@Override
	public boolean isDisplayResultsInDocumentForm() {
		if (_displayResultsInDocumentForm != null) {
			return _displayResultsInDocumentForm;
		}

		if (_documentFormPermissionChecker.hasPermission()) {
			_displayResultsInDocumentForm = GetterUtil.getBoolean(
				_portletPreferences.getValue(
					"displayResultsInDocumentForm", null));
		}
		else {
			_displayResultsInDocumentForm = false;
		}

		return _displayResultsInDocumentForm;
	}

	@Override
	public boolean isHighlightEnabled() {
		if (_highlightEnabled != null) {
			return _highlightEnabled;
		}

		_highlightEnabled = GetterUtil.getBoolean(
			_portletPreferences.getValue("highlightEnabled", null), true);

		return _highlightEnabled;
	}

	@Override
	public boolean isViewInContext() {
		if (_viewInContext != null) {
			return _viewInContext;
		}

		_viewInContext = GetterUtil.getBoolean(
			_portletPreferences.getValue("viewInContext", null), true);

		return _viewInContext;
	}

	private Boolean _displayResultsInDocumentForm;
	private final DocumentFormPermissionChecker _documentFormPermissionChecker;
	private Boolean _highlightEnabled;
	private final PortletPreferences _portletPreferences;
	private Boolean _viewInContext;

}