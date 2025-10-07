/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.helper;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Iván Zaera
 */
public class DLVisualizationHelper {

	public DLVisualizationHelper(DLRequestHelper dlRequestHelper) {
		_dlRequestHelper = dlRequestHelper;
	}

	public String getDisplayStyle() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String[] displayViews = dlPortletInstanceSettings.getDisplayViews();

		String displayStyle = ParamUtil.getString(
			_dlRequestHelper.getRequest(), "displayStyle");

		if (Validator.isNull(displayStyle)) {
			PortalPreferences portalPreferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					_dlRequestHelper.getLiferayPortletRequest());

			displayStyle = portalPreferences.getValue(
				DLPortletKeys.DOCUMENT_LIBRARY, "display-style",
				PropsValues.DL_DEFAULT_DISPLAY_VIEW);
		}

		if (!ArrayUtil.contains(displayViews, displayStyle)) {
			displayStyle = displayViews[0];
		}

		return displayStyle;
	}

	public boolean isAddFolderButtonVisible() {
		String portletName = _dlRequestHelper.getPortletName();

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY) ||
			portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {

			return true;
		}

		return false;
	}

	public boolean isMountFolderVisible() {
		return ParamUtil.getBoolean(
			_dlRequestHelper.getRequest(), "showMountFolder", true);
	}

	public boolean isShowMinimalActionsButton() {
		String portletName = _dlRequestHelper.getPortletName();

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY) ||
			portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {

			return true;
		}

		return ParamUtil.getBoolean(
			_dlRequestHelper.getRequest(), "showMinimalActionButtons");
	}

	private final DLRequestHelper _dlRequestHelper;

}