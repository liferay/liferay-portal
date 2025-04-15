/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.servlet.filter;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"before-filter=Session Id Filter", "dispatcher=REQUEST", "enabled=true",
		"init-param.url-regex-ignore-pattern=^/html/.+\\.(css|gif|html|ico|jpg|js|png)(\\?.*)?$",
		"servlet-context-name=",
		"servlet-filter-name=Same Site Lax Cookies SAML Portal Filter",
		"url-pattern=/c/portal/saml/acs", "url-pattern=/c/portal/saml/slo",
		"url-pattern=/c/portal/saml/sso"
	},
	service = Filter.class
)
public class SameSiteLaxCookiesSamlPortalFilter extends BaseSamlPortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!_samlProviderConfigurationHelper.isEnabled() ||
			Objects.equals(httpServletRequest.getMethod(), "GET") ||
			ParamUtil.getBoolean(httpServletRequest, "continue") ||
			(!ParamUtil.getBoolean(httpServletRequest, "noscript") &&
			 (httpServletRequest.getSession(false) != null))) {

			return false;
		}

		return true;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_enabled = MapUtil.getBoolean(properties, "enabled");
	}

	@Override
	protected void doProcessFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		httpServletResponse.setContentType("text/html");

		PrintWriter printWriter = httpServletResponse.getWriter();

		if (ParamUtil.getBoolean(httpServletRequest, "noscript")) {
			printWriter.write(
				StringBundler.concat(
					"<!DOCTYPE html>\n\n<html><body>",
					ResourceBundleUtil.getString(
						ResourceBundleUtil.getBundle(
							_portal.getLocale(httpServletRequest), getClass()),
						"your-browser-must-support-javascript-to-proceed"),
					"</body></html>"));

			printWriter.close();

			return;
		}

		StringBundler sb = new StringBundler(7 + (5 * _PARAMS.length));

		sb.append("<!DOCTYPE html>\n\n");
		sb.append("<html><body onload=\"document.forms[0].submit()\">");
		sb.append("<form action=\"?continue=true\" method=\"post\"");
		sb.append("name=\"fm\">");

		for (String param : _PARAMS) {
			_processParameter(httpServletRequest, sb, param);
		}

		sb.append("<noscript><meta http-equiv=\"refresh\" ");
		sb.append("content=\"0;URL='?noscript=true'\"/>");
		sb.append("</noscript></form></body></html>");

		printWriter.write(sb.toString());

		printWriter.close();
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private void _processParameter(
		HttpServletRequest httpServletRequest, StringBundler sb,
		String paramName) {

		String paramValue = ParamUtil.getString(httpServletRequest, paramName);

		if (Validator.isNotNull(paramValue)) {
			sb.append("<input type=\"hidden\" name=");
			sb.append(paramName);
			sb.append(" value=\"");
			sb.append(HtmlUtil.escapeAttribute(paramValue));
			sb.append("\"/>");
		}
	}

	private static final String[] _PARAMS = {
		"RelayState", "SAMLRequest", "SAMLResponse", "entityId"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		SameSiteLaxCookiesSamlPortalFilter.class);

	private boolean _enabled = true;

	@Reference
	private Portal _portal;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}