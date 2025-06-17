/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.action;

import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Cristina González
 */
public class PreviewFileEntryContentDashboardItemAction
	implements ContentDashboardItemAction {

	public PreviewFileEntryContentDashboardItemAction(
		DLURLHelper dlURLHelper, FileEntry fileEntry,
		HttpServletRequest httpServletRequest, Language language,
		Portal portal) {

		_dlURLHelper = dlURLHelper;
		_fileEntry = fileEntry;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;
	}

	@Override
	public String getIcon() {
		return "preview";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "preview");
	}

	@Override
	public String getName() {
		return "preview";
	}

	@Override
	public Type getType() {
		return Type.PREVIEW;
	}

	@Override
	public String getURL() {
		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		if (portletRequest == null) {
			return null;
		}

		try {
			String backURL = ParamUtil.getString(
				_httpServletRequest, "backURL");

			String portletNamespace = _portal.getPortletNamespace(
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);

			return HttpComponentsUtil.addParameter(
				_dlURLHelper.getFileEntryControlPanelLink(
					portletRequest, _fileEntry.getFileEntryId()),
				portletNamespace + "redirect", backURL);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	@Override
	public String getURL(Locale locale) {
		return getURL();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PreviewFileEntryContentDashboardItemAction.class);

	private final DLURLHelper _dlURLHelper;
	private final FileEntry _fileEntry;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;

}