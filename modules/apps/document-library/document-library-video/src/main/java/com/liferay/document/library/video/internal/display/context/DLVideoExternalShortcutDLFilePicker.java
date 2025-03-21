/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.display.context;

import com.liferay.document.library.display.context.DLFilePicker;
import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.internal.constants.DLVideoConstants;
import com.liferay.document.library.video.internal.constants.DLVideoWebKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Iván Zaera
 * @author Sergio González
 * @author Alejandro Tardín
 */
public class DLVideoExternalShortcutDLFilePicker implements DLFilePicker {

	public DLVideoExternalShortcutDLFilePicker(
		DLVideoExternalShortcut dlVideoExternalShortcut,
		String onFilePickCallback, ServletContext servletContext) {

		_dlVideoExternalShortcut = dlVideoExternalShortcut;
		_onFilePickCallback = onFilePickCallback;
		_servletContext = servletContext;
	}

	@Override
	public String getCurrentIconURL() {
		return StringPool.BLANK;
	}

	@Override
	public String getCurrentTitle() {
		if (_dlVideoExternalShortcut != null) {
			return GetterUtil.getString(
				_dlVideoExternalShortcut.getTitle(),
				_dlVideoExternalShortcut.getURL());
		}

		return StringPool.BLANK;
	}

	@Override
	public String getDescriptionFieldName() {
		return DLVideoConstants.DDM_FIELD_NAME_DESCRIPTION;
	}

	@Override
	public String getIconFieldName() {
		return null;
	}

	@Override
	public String getJavaScript() {
		return null;
	}

	@Override
	public String getJavaScriptModuleName() {
		return null;
	}

	@Override
	public String getOnClickCallback() {
		return null;
	}

	@Override
	public String getTitleFieldName() {
		return DLVideoConstants.DDM_FIELD_NAME_TITLE;
	}

	@Override
	public boolean isCustomizedFileButtonVisible() {
		return false;
	}

	@Override
	public void renderFilePicker(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/file_picker.jsp");

		httpServletRequest.setAttribute(
			DLVideoExternalShortcut.class.getName(), _dlVideoExternalShortcut);
		httpServletRequest.setAttribute(
			DLVideoWebKeys.ON_FILE_PICK_CALLBACK, _onFilePickCallback);

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	private final DLVideoExternalShortcut _dlVideoExternalShortcut;
	private final String _onFilePickCallback;
	private final ServletContext _servletContext;

}