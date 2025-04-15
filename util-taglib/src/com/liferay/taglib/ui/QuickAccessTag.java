/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.servlet.taglib.ui.QuickAccessEntry;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class QuickAccessTag extends IncludeTag {

	public String getContentId() {
		return _contentId;
	}

	public void setContentId(String contentId) {
		_contentId = contentId;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		List<QuickAccessEntry> quickAccessEntries =
			(List<QuickAccessEntry>)httpServletRequest.getAttribute(
				WebKeys.PORTLET_QUICK_ACCESS_ENTRIES);

		httpServletRequest.setAttribute(
			"liferay-ui:quick-access:contentId", _contentId);
		httpServletRequest.setAttribute(
			"liferay-ui:quick-access:quickAccessEntries", quickAccessEntries);
	}

	private static final String _PAGE = "/html/taglib/ui/quick_access/page.jsp";

	private String _contentId;

}