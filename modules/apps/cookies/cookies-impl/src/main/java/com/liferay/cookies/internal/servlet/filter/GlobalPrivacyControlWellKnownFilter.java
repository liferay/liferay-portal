/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.servlet.filter;

import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Moura
 */
@Component(
	property = {
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Global Privacy Control Well-Known Filter",
		"url-pattern=/.well-known/gpc.json"
	},
	service = Filter.class
)
public class GlobalPrivacyControlWellKnownFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (!Objects.equals(httpServletRequest.getMethod(), "GET")) {
			httpServletResponse.setHeader("Allow", "GET");
			httpServletResponse.sendError(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return;
		}

		long companyId = CompanyThreadLocal.getCompanyId();

		boolean enabled =
			_cookiesConfigurationProvider.
				isCookiesPreferenceHandlingGlobalPrivacyControlEnabled(
					ExtendedObjectClassDefinition.Scope.COMPANY, companyId);

		JSONObject jsonObject = JSONUtil.put("gpc", enabled);

		if (!enabled) {
			_writeJSON(httpServletResponse, jsonObject.toString());

			return;
		}

		long modifiedDate =
			_cookiesConfigurationProvider.
				getCookiesPreferenceHandlingModifiedDate(
					ExtendedObjectClassDefinition.Scope.COMPANY, companyId);

		if (modifiedDate > 0) {
			Instant instant = Instant.ofEpochMilli(modifiedDate);

			ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);

			LocalDate localDate = zonedDateTime.toLocalDate();

			jsonObject.put(
				"lastUpdate",
				localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
		}

		_writeJSON(httpServletResponse, jsonObject.toString());
	}

	private void _writeJSON(
			HttpServletResponse httpServletResponse, String json)
		throws Exception {

		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		ServletResponseUtil.write(httpServletResponse, json);

		httpServletResponse.flushBuffer();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GlobalPrivacyControlWellKnownFilter.class);

	@Reference
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

}