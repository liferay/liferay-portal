/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.absoluteredirects;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.TryFilter;
import com.liferay.portal.kernel.servlet.WrapHttpServletResponseFilter;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>
 * This filter is used to ensure that all redirects are absolute. It should not
 * be disabled because it also sets the company ID in the request so that
 * subsequent calls in the thread have the company ID properly set. This filter
 * should also always be the first filter in the list of filters.
 * </p>
 *
 * @author Minhchau Dang
 * @author Brian Wing Shun Chan
 */
public class AbsoluteRedirectsFilter
	extends BasePortalFilter
	implements TryFilter, WrapHttpServletResponseFilter {

	@Override
	public Object doFilterTry(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (httpServletRequest.getCharacterEncoding() == null) {
			httpServletRequest.setCharacterEncoding(StringPool.UTF8);
		}

		//response.setContentType(ContentTypes.TEXT_HTML_UTF8);

		// Company ID needs to always be called here so that it is properly set
		// in subsequent calls

		try {
			PortalInstances.getCompanyId(
				httpServletRequest, PropsValues.VIRTUAL_HOSTS_STRICT_ACCESS);
		}
		catch (NoSuchVirtualHostException noSuchVirtualHostException) {
			_log.error(noSuchVirtualHostException);

			httpServletRequest.setAttribute(
				WebKeys.UNKNOWN_VIRTUAL_HOST, Boolean.TRUE);

			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return null;
		}

		PortalUtil.getCurrentCompleteURL(httpServletRequest);
		PortalUtil.getCurrentURL(httpServletRequest);

		HttpSession httpSession = httpServletRequest.getSession();

		Boolean httpsInitial = (Boolean)httpSession.getAttribute(
			WebKeys.HTTPS_INITIAL);

		if (httpsInitial == null) {
			httpsInitial = Boolean.valueOf(httpServletRequest.isSecure());

			httpSession.setAttribute(WebKeys.HTTPS_INITIAL, httpsInitial);

			if (_log.isDebugEnabled()) {
				_log.debug("Setting httpsInitial to " + httpsInitial);
			}
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		return null;
	}

	@Override
	public HttpServletResponse getWrappedHttpServletResponse(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return new AbsoluteRedirectsResponse(
			httpServletRequest, httpServletResponse);
	}

	@Override
	public boolean isFilterEnabled() {
		return _FILTER_ENABLED;
	}

	private static final boolean _FILTER_ENABLED = true;

	private static final Log _log = LogFactoryUtil.getLog(
		AbsoluteRedirectsFilter.class);

}