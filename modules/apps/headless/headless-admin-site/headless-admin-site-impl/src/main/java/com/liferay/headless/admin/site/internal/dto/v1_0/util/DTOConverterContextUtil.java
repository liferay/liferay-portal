/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.UriInfo;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class DTOConverterContextUtil {

	public static DTOConverterContext getDTOConverterContext(
		AcceptLanguage acceptLanguage,
		DTOConverterRegistry dtoConverterRegistry,
		HttpServletRequest httpServletRequest, Object id, UriInfo uriInfo,
		User user) {

		return getDTOConverterContext(
			acceptLanguage, Collections.emptyMap(), dtoConverterRegistry,
			httpServletRequest, id, uriInfo, user);
	}

	public static DTOConverterContext getDTOConverterContext(
		AcceptLanguage acceptLanguage, Map<String, Map<String, String>> actions,
		Map<String, Object> attributesMap,
		DTOConverterRegistry dtoConverterRegistry,
		HttpServletRequest httpServletRequest, Object id, UriInfo uriInfo,
		User user) {

		return _getDTOConverterContext(
			acceptLanguage.isAcceptAllLanguages(), actions, attributesMap,
			dtoConverterRegistry, httpServletRequest, id,
			acceptLanguage.getPreferredLocale(), uriInfo, user);
	}

	public static DTOConverterContext getDTOConverterContext(
		AcceptLanguage acceptLanguage, Map<String, Object> attributesMap,
		DTOConverterRegistry dtoConverterRegistry,
		HttpServletRequest httpServletRequest, Object id, UriInfo uriInfo,
		User user) {

		return getDTOConverterContext(
			acceptLanguage.isAcceptAllLanguages(), attributesMap,
			dtoConverterRegistry, httpServletRequest, id,
			acceptLanguage.getPreferredLocale(), uriInfo, user);
	}

	public static DTOConverterContext getDTOConverterContext(
		boolean acceptAllLanguages, Map<String, Object> attributesMap,
		DTOConverterRegistry dtoConverterRegistry,
		HttpServletRequest httpServletRequest, Object id, Locale locale,
		UriInfo uriInfo, User user) {

		return _getDTOConverterContext(
			acceptAllLanguages, null, attributesMap, dtoConverterRegistry,
			httpServletRequest, id, locale, uriInfo, user);
	}

	private static DTOConverterContext _getDTOConverterContext(
		boolean acceptAllLanguages, Map<String, Map<String, String>> actions,
		Map<String, Object> attributesMap,
		DTOConverterRegistry dtoConverterRegistry,
		HttpServletRequest httpServletRequest, Object id, Locale locale,
		UriInfo uriInfo, User user) {

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				acceptAllLanguages, actions, dtoConverterRegistry,
				httpServletRequest, id, locale, uriInfo, user);

		for (Map.Entry<String, Object> entry : attributesMap.entrySet()) {
			defaultDTOConverterContext.setAttribute(
				entry.getKey(), entry.getValue());
		}

		return defaultDTOConverterContext;
	}

}