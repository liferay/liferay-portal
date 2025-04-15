/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider;

import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.pagination.provider.PaginationProvider;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Zoltán Takács
 */
@Provider
public class PaginationContextProvider implements ContextProvider<Pagination> {

	public PaginationContextProvider(
		PaginationProvider paginationProvider, Portal portal) {

		_paginationProvider = paginationProvider;
		_portal = portal;
	}

	@Override
	public Pagination createContext(Message message) {
		HttpServletRequest httpServletRequest =
			ContextProviderUtil.getHttpServletRequest(message);

		return _paginationProvider.getPagination(
			_portal.getCompanyId(httpServletRequest),
			_getIntegerValue(httpServletRequest, "page"),
			_getIntegerValue(httpServletRequest, "pageSize"));
	}

	private Integer _getIntegerValue(
		HttpServletRequest httpServletRequest, String key) {

		String value = httpServletRequest.getParameter(key);

		if (Validator.isNotNull(value)) {
			return Integer.valueOf(value);
		}

		return null;
	}

	private final PaginationProvider _paginationProvider;
	private final Portal _portal;

}