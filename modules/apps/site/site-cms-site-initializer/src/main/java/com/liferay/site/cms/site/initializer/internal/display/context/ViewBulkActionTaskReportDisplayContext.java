/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Luca Pellizzon
 */
public class ViewBulkActionTaskReportDisplayContext {

	public ViewBulkActionTaskReportDisplayContext(
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_objectDefinitionLocalService = objectDefinitionLocalService;
	}

	public String getAPIURL() {
		return "/o/cms/bulk-action-tasks";
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		try {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					PortalUtil.getCompanyId(_httpServletRequest),
					"BulkActionTask");

			String baseBulkActionTaskReportURL =
				ActionUtil.getBaseBulkActionTaskReportURL(
					objectDefinition.getClassName(),
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY));

			return ListUtil.fromArray(
				new FDSActionDropdownItem(
					baseBulkActionTaskReportURL + "/{id}", "view", "view",
					_language.get(_httpServletRequest, "view"), null, null,
					"link"));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return Collections.emptyList();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewBulkActionTaskReportDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;

}