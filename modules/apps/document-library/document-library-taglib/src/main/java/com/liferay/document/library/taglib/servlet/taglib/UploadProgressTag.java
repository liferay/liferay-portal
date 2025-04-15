/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.taglib.servlet.taglib;

import com.liferay.document.library.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Brian Wing Shun Chan
 * @author Keith R. Davis
 * @author Iliyan Peychev
 */
public class UploadProgressTag extends IncludeTag {

	public String getId() {
		return _id;
	}

	public String getMessage() {
		return _message;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setMessage(String message) {
		_message = message;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_id = null;
		_message = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-document-library:upload-progress:id", _id);
		httpServletRequest.setAttribute(
			"liferay-document-library:upload-progress:message", _message);
	}

	private static final String _PAGE = "/upload_progress/page.jsp";

	private String _id;
	private String _message;

}