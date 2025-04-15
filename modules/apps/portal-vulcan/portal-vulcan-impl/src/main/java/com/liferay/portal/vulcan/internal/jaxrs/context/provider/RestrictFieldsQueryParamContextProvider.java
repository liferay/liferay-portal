/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider;

import com.liferay.portal.vulcan.fields.RestrictFieldsQueryParam;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.ext.Provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Javier Gamarra
 */
@Provider
public class RestrictFieldsQueryParamContextProvider
	implements ContextProvider<RestrictFieldsQueryParam> {

	@Override
	public RestrictFieldsQueryParam createContext(Message message) {
		HttpServletRequest httpServletRequest =
			ContextProviderUtil.getHttpServletRequest(message);

		String restrictFields = httpServletRequest.getParameter(
			"restrictFields");

		if (restrictFields == null) {
			return () -> null;
		}

		if (restrictFields.isEmpty()) {
			return Collections::emptySet;
		}

		return () -> new HashSet<>(Arrays.asList(restrictFields.split(",")));
	}

}