/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Adolfo Pérez
 */
public class DLFileEntryTypeDetailsDisplayContext {

	public DLFileEntryTypeDetailsDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public DLFileEntryType getDLFileEntryType() throws PortalException {
		long fileEntryTypeId = ParamUtil.getLong(
			_httpServletRequest, "fileEntryTypeId");

		if (fileEntryTypeId == 0) {
			return null;
		}

		return DLFileEntryTypeServiceUtil.getFileEntryType(fileEntryTypeId);
	}

	public boolean isForeignDLFileEntryType() throws PortalException {
		DDMStructure ddmStructure = _getDDMStructure();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((ddmStructure != null) &&
			(ddmStructure.getGroupId() != themeDisplay.getScopeGroupId())) {

			return true;
		}

		return false;
	}

	private DDMStructure _getDDMStructure() throws PortalException {
		DLFileEntryType dlFileEntryType = getDLFileEntryType();

		if ((dlFileEntryType == null) ||
			(dlFileEntryType.getDataDefinitionId() == 0)) {

			return null;
		}

		return DDMStructureServiceUtil.getStructure(
			dlFileEntryType.getDataDefinitionId());
	}

	private final HttpServletRequest _httpServletRequest;

}