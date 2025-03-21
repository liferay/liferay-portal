/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.etag;

import com.liferay.portal.kernel.servlet.RestrictedByteBufferCacheServletResponse;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Eduardo Lundgren
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Shuyang Zhou
 */
public class ETagFilter extends BasePortalFilter {

	public static final String SKIP_FILTER =
		ETagFilter.class.getName() + "#SKIP_FILTER";

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (ParamUtil.getBoolean(httpServletRequest, _ETAG, true) &&
			!isAlreadyFiltered(httpServletRequest)) {

			return true;
		}

		return false;
	}

	protected boolean isAlreadyFiltered(HttpServletRequest httpServletRequest) {
		if (httpServletRequest.getAttribute(SKIP_FILTER) != null) {
			return true;
		}

		return false;
	}

	protected boolean isEligibleForETag(int status) {
		if ((status >= HttpServletResponse.SC_OK) &&
			(status < HttpServletResponse.SC_MULTIPLE_CHOICES)) {

			return true;
		}

		return false;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		httpServletRequest.setAttribute(SKIP_FILTER, Boolean.TRUE);

		RestrictedByteBufferCacheServletResponse
			restrictedByteBufferCacheServletResponse =
				new RestrictedByteBufferCacheServletResponse(
					httpServletResponse, PropsValues.ETAG_RESPONSE_SIZE_MAX);

		processFilter(
			ETagFilter.class.getName(), httpServletRequest,
			restrictedByteBufferCacheServletResponse, filterChain);

		if (httpServletRequest.isAsyncSupported() &&
			httpServletRequest.isAsyncStarted()) {

			AsyncContext asyncContext = httpServletRequest.getAsyncContext();

			asyncContext.addListener(
				new ETagFilterAsyncListener(
					asyncContext, httpServletRequest, httpServletResponse,
					restrictedByteBufferCacheServletResponse));
		}
		else {
			_postProcessETag(
				httpServletRequest, httpServletResponse,
				restrictedByteBufferCacheServletResponse);
		}
	}

	private void _postProcessETag(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			RestrictedByteBufferCacheServletResponse
				restrictedByteBufferCacheServletResponse)
		throws IOException {

		if (!restrictedByteBufferCacheServletResponse.isOverflowed() &&
			(!isEligibleForETag(
				restrictedByteBufferCacheServletResponse.getStatus()) ||
			 !ETagUtil.processETag(
				 httpServletRequest, httpServletResponse,
				 restrictedByteBufferCacheServletResponse.getByteBuffer()))) {

			restrictedByteBufferCacheServletResponse.flushCache();
		}
	}

	private static final String _ETAG = "etag";

	private class ETagFilterAsyncListener implements AsyncListener {

		@Override
		public void onComplete(AsyncEvent event) throws IOException {
			_postProcessETag(
				_httpServletRequest, _httpServletResponse,
				_restrictedByteBufferCacheServletResponse);
		}

		@Override
		public void onError(AsyncEvent event) {
		}

		@Override
		public void onStartAsync(AsyncEvent event) {
			_asyncContext.addListener(this);
		}

		@Override
		public void onTimeout(AsyncEvent event) {
		}

		private ETagFilterAsyncListener(
			AsyncContext asyncContext, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			RestrictedByteBufferCacheServletResponse
				restrictedByteBufferCacheServletResponse) {

			_asyncContext = asyncContext;
			_httpServletRequest = httpServletRequest;
			_httpServletResponse = httpServletResponse;
			_restrictedByteBufferCacheServletResponse =
				restrictedByteBufferCacheServletResponse;
		}

		private final AsyncContext _asyncContext;
		private final HttpServletRequest _httpServletRequest;
		private final HttpServletResponse _httpServletResponse;
		private final RestrictedByteBufferCacheServletResponse
			_restrictedByteBufferCacheServletResponse;

	}

}