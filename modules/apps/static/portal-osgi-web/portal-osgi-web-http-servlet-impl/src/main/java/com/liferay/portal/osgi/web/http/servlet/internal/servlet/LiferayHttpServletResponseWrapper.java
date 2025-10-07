/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;

/**
 * @author Dante Wang
 */
public class LiferayHttpServletResponseWrapper
	extends HttpServletResponseWrapper {

	public static LiferayHttpServletResponseWrapper find(
		HttpServletResponse httpServletResponse) {

		while (httpServletResponse instanceof
					HttpServletResponseWrapper httpServletResponseWrapper) {

			if (httpServletResponseWrapper instanceof
					LiferayHttpServletResponseWrapper
						liferayHttpServletResponseWrapper) {

				return liferayHttpServletResponseWrapper;
			}

			httpServletResponse =
				(HttpServletResponse)httpServletResponseWrapper.getResponse();
		}

		return null;
	}

	public LiferayHttpServletResponseWrapper(
		HttpServletResponse httpServletResponse) {

		super(httpServletResponse);
	}

	@Override
	public void flushBuffer() throws IOException {
		if (_status != -1) {
			HttpServletResponse httpServletResponse =
				(HttpServletResponse)getResponse();

			httpServletResponse.sendError(_status, getMessage());
		}

		super.flushBuffer();
	}

	public int getInternalStatus() {
		return _status;
	}

	public String getMessage() {
		return _message;
	}

	@Override
	public int getStatus() {
		if (_status != -1) {
			return _status;
		}

		return super.getStatus();
	}

	@Override
	public boolean isCommitted() {
		if (_status != -1) {
			return true;
		}

		return super.isCommitted();
	}

	@Override
	public void sendError(int status) {
		_status = status;
	}

	@Override
	public void sendError(int status, String message) {
		_status = status;
		_message = message;
	}

	private String _message;
	private int _status = -1;

}