/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayDispatchTargets;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Dante Wang
 */
public class RequestDispatcherImpl implements RequestDispatcher {

	public RequestDispatcherImpl(
		LiferayDispatchTargets liferayDispatchTargets, String path) {

		_liferayDispatchTargets = liferayDispatchTargets;
		_path = path;
	}

	@Override
	public void forward(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		_liferayDispatchTargets.doDispatch(
			(HttpServletRequest)servletRequest,
			(HttpServletResponse)servletResponse, _path,
			DispatcherType.FORWARD);
	}

	@Override
	public void include(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		_liferayDispatchTargets.doDispatch(
			(HttpServletRequest)servletRequest,
			(HttpServletResponse)servletResponse, _path,
			DispatcherType.INCLUDE);
	}

	@Override
	public String toString() {
		String value = _toString;

		if (value == null) {
			value = StringBundler.concat(
				_SIMPLE_NAME, CharPool.OPEN_BRACKET, _path,
				StringPool.COMMA_AND_SPACE, _liferayDispatchTargets,
				CharPool.CLOSE_BRACKET);

			_toString = value;
		}

		return value;
	}

	private static final String _SIMPLE_NAME =
		RequestDispatcherImpl.class.getSimpleName();

	private final LiferayDispatchTargets _liferayDispatchTargets;
	private final String _path;
	private String _toString;

}