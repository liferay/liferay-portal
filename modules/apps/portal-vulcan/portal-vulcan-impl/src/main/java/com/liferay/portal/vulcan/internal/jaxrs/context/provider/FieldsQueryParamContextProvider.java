/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider;

import com.liferay.portal.vulcan.fields.FieldsQueryParam;
import com.liferay.portal.vulcan.util.FieldsUtil;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.ext.Provider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Alejandro Hernández
 */
@Provider
public class FieldsQueryParamContextProvider
	implements ContextProvider<FieldsQueryParam> {

	@Override
	public FieldsQueryParam createContext(Message message) {
		HttpServletRequest httpServletRequest =
			ContextProviderUtil.getHttpServletRequest(message);

		String fieldNamesString = httpServletRequest.getParameter("fields");

		if (fieldNamesString == null) {
			return () -> null;
		}

		if (fieldNamesString.isEmpty()) {
			return Collections::emptySet;
		}

		Set<String> expandedFieldNames = new HashSet<>();

		for (String fieldName : fieldNamesString.split(",")) {
			expandedFieldNames.addAll(FieldsUtil.expand(fieldName));
		}

		return () -> expandedFieldNames;
	}

}