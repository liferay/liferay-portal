/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet.shared.task.helper;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(service = PortletSharedRequestHelper.class)
public class PortletSharedRequestHelperImpl
	implements PortletSharedRequestHelper {

	@Override
	public <T> T getAttribute(String name, RenderRequest renderRequest) {
		return getAttribute(name, _getSharedHttpServletRequest(renderRequest));
	}

	@Override
	public String getCompleteURL(RenderRequest renderRequest) {
		return _getCompleteOriginalURL(
			_portal.getHttpServletRequest(renderRequest));
	}

	@Override
	public String getParameter(String name, RenderRequest renderRequest) {
		HttpServletRequest httpServletRequest = _getSharedHttpServletRequest(
			renderRequest);

		String parameterValue = StringUtil.trim(
			httpServletRequest.getParameter(name));

		if (Validator.isBlank(parameterValue)) {
			return null;
		}

		return parameterValue;
	}

	@Override
	public String[] getParameterValues(
		String name, RenderRequest renderRequest) {

		HttpServletRequest httpServletRequest = _getSharedHttpServletRequest(
			renderRequest);

		String[] parameterValues = httpServletRequest.getParameterValues(name);

		if (ArrayUtil.isEmpty(parameterValues)) {
			return null;
		}

		return parameterValues;
	}

	@Override
	public void setAttribute(
		String name, Object attributeValue, RenderRequest renderRequest) {

		HttpServletRequest httpServletRequest = _getSharedHttpServletRequest(
			renderRequest);

		httpServletRequest.setAttribute(name, attributeValue);
	}

	@SuppressWarnings("unchecked")
	protected <T> T getAttribute(
		String name, HttpServletRequest httpServletRequest) {

		return (T)httpServletRequest.getAttribute(name);
	}

	private String _getCompleteOriginalURL(
		HttpServletRequest httpServletRequest) {

		String requestURL = null;
		String queryString = null;

		if (HttpComponentsUtil.isForwarded(httpServletRequest)) {
			requestURL = _portal.getAbsoluteURL(
				httpServletRequest,
				(String)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_SERVLET_FORWARD_REQUEST_URI));

			queryString = (String)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_SERVLET_FORWARD_QUERY_STRING);
		}
		else {
			requestURL = _portal.getAbsoluteURL(
				httpServletRequest,
				HttpComponentsUtil.getPath(
					String.valueOf(httpServletRequest.getRequestURL())));

			queryString = httpServletRequest.getQueryString();
		}

		StringBuffer sb = new StringBuffer();

		sb.append(requestURL);

		if (queryString != null) {
			sb.append(StringPool.QUESTION);
			sb.append(queryString);
		}

		String proxyPath = _portal.getPathProxy();

		if (Validator.isNotNull(proxyPath)) {
			int x =
				sb.indexOf(Http.PROTOCOL_DELIMITER) +
					Http.PROTOCOL_DELIMITER.length();

			int y = sb.indexOf(StringPool.SLASH, x);

			sb.insert(y, proxyPath);
		}

		String completeURL = sb.toString();

		if (httpServletRequest.isRequestedSessionIdFromURL()) {
			HttpSession httpSession = httpServletRequest.getSession();

			String sessionId = httpSession.getId();

			completeURL = _portal.getURLWithSessionId(completeURL, sessionId);
		}

		if (_log.isWarnEnabled() && completeURL.contains("?&")) {
			_log.warn("Invalid URL " + completeURL);
		}

		return completeURL;
	}

	private HttpServletRequest _getSharedHttpServletRequest(
		RenderRequest renderRequest) {

		return _portal.getOriginalServletRequest(
			_portal.getHttpServletRequest(renderRequest));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletSharedRequestHelperImpl.class);

	@Reference
	private Portal _portal;

}