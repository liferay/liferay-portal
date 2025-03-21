/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alejandro Tardín
 */
public class DLSelectRestrictedFileEntryTypesDisplayContext
	extends DLViewFileEntryTypesDisplayContext {

	public DLSelectRestrictedFileEntryTypesDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		super(httpServletRequest, renderRequest, renderResponse);
	}

	public String getFormActionURL() {
		return getSearchActionURL();
	}

	@Override
	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/document_library/select_restricted_file_entry_type.jsp"
		).setParameter(
			"includeBasicFileEntryType", true
		).buildPortletURL();
	}

}