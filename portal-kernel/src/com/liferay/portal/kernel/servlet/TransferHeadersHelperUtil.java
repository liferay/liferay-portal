/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Tina Tian
 */
public class TransferHeadersHelperUtil {

	public static RequestDispatcher getTransferHeadersRequestDispatcher(
		RequestDispatcher requestDispatcher) {

		return new TransferHeadersRequestDispatcher(requestDispatcher);
	}

	public static void transferHeaders(
		Map<String, Object[]> headers,
		HttpServletResponse httpServletResponse) {

		boolean transferringHeaders = _transferringHeaders.get();

		_transferringHeaders.set(true);

		try {
			for (Map.Entry<String, Object[]> entry : headers.entrySet()) {
				String name = entry.getKey();
				Object[] values = entry.getValue();

				if (values instanceof Cookie[]) {
					Cookie[] cookies = (Cookie[])values;

					for (Cookie cookie : cookies) {
						CookiesManagerUtil.addCookie(
							cookie, null, httpServletResponse);
					}
				}
				else if (values instanceof Integer[]) {
					Integer[] intValues = (Integer[])values;

					for (int value : intValues) {
						if (httpServletResponse.containsHeader(name)) {
							httpServletResponse.addIntHeader(name, value);
						}
						else {
							httpServletResponse.setIntHeader(name, value);
						}
					}
				}
				else if (values instanceof Long[]) {
					Long[] dateValues = (Long[])values;

					for (long value : dateValues) {
						if (httpServletResponse.containsHeader(name)) {
							httpServletResponse.addDateHeader(name, value);
						}
						else {
							httpServletResponse.setDateHeader(name, value);
						}
					}
				}
				else if (values instanceof String[]) {
					String[] stringValues = (String[])values;

					for (String value : stringValues) {
						if (httpServletResponse.containsHeader(name)) {
							httpServletResponse.addHeader(name, value);
						}
						else {
							httpServletResponse.setHeader(name, value);
						}
					}
				}
			}
		}
		finally {
			_transferringHeaders.set(transferringHeaders);
		}
	}

	private static final ThreadLocal<Boolean> _transferringHeaders =
		new CentralizedThreadLocal<>(
			TransferHeadersHelperUtil.class + "._transferringHeaders",
			() -> false);

	private static class HeaderAction<T> {

		public String getName() {
			return _name;
		}

		public T getValue() {
			return _value;
		}

		public boolean isOverride() {
			return _override;
		}

		private HeaderAction(String name, T value, boolean override) {
			_name = name;
			_value = value;
			_override = override;
		}

		private final String _name;
		private final boolean _override;
		private final T _value;

	}

	private static class TransferHeadersRequestDispatcher
		implements RequestDispatcher {

		@Override
		public void forward(
				ServletRequest servletRequest, ServletResponse servletResponse)
			throws IOException, ServletException {

			_requestDispatcher.forward(servletRequest, servletResponse);
		}

		@Override
		public void include(
				ServletRequest servletRequest, ServletResponse servletResponse)
			throws IOException, ServletException {

			TransferHeadersServletResponse transferHeadersServletResponse =
				new TransferHeadersServletResponse(
					(HttpServletResponse)servletResponse);

			try {
				_requestDispatcher.include(
					servletRequest, transferHeadersServletResponse);
			}
			finally {
				transferHeadersServletResponse.transferHeaders();
			}
		}

		private TransferHeadersRequestDispatcher(
			RequestDispatcher requestDispatcher) {

			_requestDispatcher = requestDispatcher;
		}

		private final RequestDispatcher _requestDispatcher;

	}

	private static class TransferHeadersServletResponse
		extends HttpServletResponseWrapper {

		@Override
		public void addCookie(Cookie cookie) {
			if (_transferringHeaders.get()) {
				_headerActions.add(
					new HeaderAction<>(cookie.getName(), cookie, false));

				return;
			}

			CookiesManagerUtil.addCookie(cookie, null, _httpServletResponse);
		}

		@Override
		public void addDateHeader(String name, long value) {
			if (_transferringHeaders.get()) {
				_headerActions.add(new HeaderAction<>(name, value, false));

				return;
			}

			_httpServletResponse.addDateHeader(name, value);
		}

		@Override
		public void addHeader(String name, String value) {
			if (_transferringHeaders.get()) {
				_headerActions.add(new HeaderAction<>(name, value, false));

				return;
			}

			_httpServletResponse.addHeader(name, value);
		}

		@Override
		public void addIntHeader(String name, int value) {
			if (_transferringHeaders.get()) {
				_headerActions.add(new HeaderAction<>(name, value, false));

				return;
			}

			_httpServletResponse.addIntHeader(name, value);
		}

		@Override
		public void setDateHeader(String name, long value) {
			if (_transferringHeaders.get()) {
				_headerActions.add(new HeaderAction<>(name, value, true));

				return;
			}

			_httpServletResponse.setDateHeader(name, value);
		}

		@Override
		public void setHeader(String name, String value) {
			if (_transferringHeaders.get()) {
				_headerActions.add(new HeaderAction<>(name, value, true));

				return;
			}

			_httpServletResponse.setHeader(name, value);
		}

		@Override
		public void setIntHeader(String name, int value) {
			if (_transferringHeaders.get()) {
				_headerActions.add(new HeaderAction<>(name, value, true));

				return;
			}

			_httpServletResponse.setIntHeader(name, value);
		}

		public void transferHeaders() {
			boolean transferringHeaders = _transferringHeaders.get();

			_transferringHeaders.set(true);

			try {
				for (HeaderAction<?> headerAction : _headerActions) {
					Object value = headerAction.getValue();

					if (value instanceof Long) {
						if (headerAction.isOverride()) {
							_httpServletResponse.setDateHeader(
								headerAction.getName(), (Long)value);
						}
						else {
							_httpServletResponse.addDateHeader(
								headerAction.getName(), (Long)value);
						}
					}
					else if (value instanceof Cookie) {
						CookiesManagerUtil.addCookie(
							(Cookie)value, null, _httpServletResponse);
					}
					else if (value instanceof Integer) {
						if (headerAction.isOverride()) {
							_httpServletResponse.setIntHeader(
								headerAction.getName(), (Integer)value);
						}
						else {
							_httpServletResponse.addIntHeader(
								headerAction.getName(), (Integer)value);
						}
					}
					else {
						if (headerAction.isOverride()) {
							_httpServletResponse.setHeader(
								headerAction.getName(), (String)value);
						}
						else {
							_httpServletResponse.addHeader(
								headerAction.getName(), (String)value);
						}
					}
				}
			}
			finally {
				_transferringHeaders.set(transferringHeaders);
			}
		}

		private TransferHeadersServletResponse(
			HttpServletResponse httpServletResponse) {

			super(httpServletResponse);

			_httpServletResponse = httpServletResponse;
		}

		private final List<HeaderAction<?>> _headerActions = new ArrayList<>();
		private final HttpServletResponse _httpServletResponse;

	}

}