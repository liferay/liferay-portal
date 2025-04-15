/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.taglib.servlet.taglib;

import com.liferay.document.library.taglib.internal.display.context.DLViewFileVersionDisplayContextUtil;
import com.liferay.document.library.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Alejandro Tardín
 */
public class MimeTypeStickerTag extends IncludeTag {

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public FileVersion getFileVersion() {
		return _fileVersion;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setFileVersion(FileVersion fileVersion) {
		_fileVersion = fileVersion;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cssClass = null;
		_fileVersion = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-document-library:mime-type-sticker:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-document-library:mime-type-sticker:" +
				"dlViewFileVersionDisplayContext",
			DLViewFileVersionDisplayContextUtil.
				getDLViewFileVersionDisplayContext(
					httpServletRequest,
					(HttpServletResponse)pageContext.getResponse(),
					_fileVersion));
	}

	private static final String _PAGE = "/mime_type_sticker/page.jsp";

	private String _cssClass;
	private FileVersion _fileVersion;

}