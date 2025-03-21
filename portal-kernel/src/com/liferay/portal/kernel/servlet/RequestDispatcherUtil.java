/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Enumeration;

/**
 * @author Shuyang Zhou
 */
public class RequestDispatcherUtil {

	public static BufferCacheServletResponse getBufferCacheServletResponse(
			RequestDispatcher requestDispatcher,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		BufferCacheServletResponse bufferCacheServletResponse =
			new LastModifiedCacheServletResponse(httpServletResponse);

		requestDispatcher.include(
			new HttpServletRequestWrapper(httpServletRequest) {

				@Override
				public String getContextPath() {
					return (String)getAttribute(
						RequestDispatcher.INCLUDE_CONTEXT_PATH);
				}

				@Override
				public long getDateHeader(String name) {
					if (name.equals(HttpHeaders.IF_MODIFIED_SINCE)) {
						return -1;
					}

					return super.getDateHeader(name);
				}

				@Override
				public String getHeader(String name) {
					if (name.equals(HttpHeaders.IF_MODIFIED_SINCE) ||
						name.equals(HttpHeaders.IF_NONE_MATCH) ||
						name.equals(HttpHeaders.LAST_MODIFIED)) {

						return null;
					}

					return super.getHeader(name);
				}

				@Override
				public Enumeration<String> getHeaders(String name) {
					if (name.equals(HttpHeaders.IF_MODIFIED_SINCE) ||
						name.equals(HttpHeaders.IF_NONE_MATCH) ||
						name.equals(HttpHeaders.LAST_MODIFIED)) {

						return null;
					}

					return super.getHeaders(name);
				}

				@Override
				public String getMethod() {
					return HttpMethods.GET;
				}

				@Override
				public String getPathInfo() {
					return (String)getAttribute(
						RequestDispatcher.INCLUDE_PATH_INFO);
				}

				@Override
				public String getQueryString() {
					return (String)getAttribute(
						RequestDispatcher.INCLUDE_QUERY_STRING);
				}

				@Override
				public String getRequestURI() {
					return (String)getAttribute(
						RequestDispatcher.INCLUDE_REQUEST_URI);
				}

				@Override
				public String getServletPath() {
					return (String)getAttribute(
						RequestDispatcher.INCLUDE_SERVLET_PATH);
				}

			},
			bufferCacheServletResponse);

		return bufferCacheServletResponse;
	}

	public static ObjectValuePair<String, Long>
			getContentAndLastModifiedTimeObjectValuePair(
				RequestDispatcher requestDispatcher,
				HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse)
		throws Exception {

		BufferCacheServletResponse bufferCacheServletResponse =
			getBufferCacheServletResponse(
				requestDispatcher, httpServletRequest, httpServletResponse);

		return new ObjectValuePair<>(
			bufferCacheServletResponse.getString(),
			GetterUtil.getLong(
				bufferCacheServletResponse.getHeader(HttpHeaders.LAST_MODIFIED),
				-1));
	}

	public static String getEffectivePath(
		HttpServletRequest httpServletRequest) {

		DispatcherType dispatcherType = httpServletRequest.getDispatcherType();

		if (dispatcherType.equals(DispatcherType.FORWARD)) {
			return (String)httpServletRequest.getAttribute(
				RequestDispatcher.FORWARD_SERVLET_PATH);
		}
		else if (dispatcherType.equals(DispatcherType.INCLUDE)) {
			return (String)httpServletRequest.getAttribute(
				RequestDispatcher.INCLUDE_SERVLET_PATH);
		}

		return httpServletRequest.getServletPath();
	}

	public static long getLastModifiedTime(
			RequestDispatcher requestDispatcher,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new LastModifiedCacheServletResponse(httpServletResponse);

		requestDispatcher.include(
			new HttpServletRequestWrapper(httpServletRequest) {

				@Override
				public String getMethod() {
					return HttpMethods.HEAD;
				}

			},
			metaInfoCacheServletResponse);

		return GetterUtil.getLong(
			metaInfoCacheServletResponse.getHeader(HttpHeaders.LAST_MODIFIED),
			-1);
	}

	private static class LastModifiedCacheServletResponse
		extends BufferCacheServletResponse {

		public LastModifiedCacheServletResponse(
			HttpServletResponse httpServletResponse) {

			super(httpServletResponse);
		}

		@Override
		public void addDateHeader(String name, long value) {
			if (StringUtil.equalsIgnoreCase(name, HttpHeaders.LAST_MODIFIED)) {
				_lastModified = String.valueOf(value);

				return;
			}

			super.addDateHeader(name, value);
		}

		@Override
		public void addHeader(String name, String value) {
			if (StringUtil.equalsIgnoreCase(name, HttpHeaders.LAST_MODIFIED)) {
				_lastModified = value;

				return;
			}

			super.addHeader(name, value);
		}

		@Override
		public String getHeader(String name) {
			if (StringUtil.equalsIgnoreCase(name, HttpHeaders.LAST_MODIFIED)) {
				return _lastModified;
			}

			return super.getHeader(name);
		}

		@Override
		public void setDateHeader(String name, long value) {
			if (StringUtil.equalsIgnoreCase(name, HttpHeaders.LAST_MODIFIED)) {
				_lastModified = String.valueOf(value);

				return;
			}

			super.setDateHeader(name, value);
		}

		@Override
		public void setHeader(String name, String value) {
			if (StringUtil.equalsIgnoreCase(name, HttpHeaders.LAST_MODIFIED)) {
				_lastModified = value;

				return;
			}

			super.setHeader(name, value);
		}

		private String _lastModified;

	}

}