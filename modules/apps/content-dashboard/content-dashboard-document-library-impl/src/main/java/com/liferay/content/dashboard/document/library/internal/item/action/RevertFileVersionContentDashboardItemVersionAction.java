/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.action;

import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Mikel Lorza
 */
public class RevertFileVersionContentDashboardItemVersionAction
	implements ContentDashboardItemVersionAction {

	public RevertFileVersionContentDashboardItemVersionAction(
		FileVersion fileVersion, HttpServletRequest httpServletRequest,
		Language language, Portal portal,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		_fileVersion = fileVersion;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;
		_requestBackedPortletURLFactory = requestBackedPortletURLFactory;
	}

	@Override
	public String getIcon() {
		return "revert";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "revert");
	}

	@Override
	public String getName() {
		return "revert";
	}

	@Override
	public String getURL() {
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(
				(PortletResponse)_httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE));

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		return PortletURLBuilder.create(
			_requestBackedPortletURLFactory.createActionURL(
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)
		).setActionName(
			"/document_library/edit_file_entry"
		).setCMD(
			Constants.REVERT
		).setRedirect(
			portletURL
		).setBackURL(
			portletURL.toString()
		).setParameter(
			"fileEntryId", _fileVersion.getFileEntryId()
		).setParameter(
			"version", _fileVersion.getVersion()
		).buildString();
	}

	private final FileVersion _fileVersion;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;
	private final RequestBackedPortletURLFactory
		_requestBackedPortletURLFactory;

}