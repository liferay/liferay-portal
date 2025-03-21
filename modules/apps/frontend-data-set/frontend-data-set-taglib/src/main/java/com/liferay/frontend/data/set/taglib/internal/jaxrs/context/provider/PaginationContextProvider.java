/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.taglib.internal.jaxrs.context.provider;

import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Marco Leo
 */
@Provider
public class PaginationContextProvider
	implements ContextProvider<FDSPagination> {

	@Override
	public FDSPagination createContext(Message message) {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)message.getContextualProperty("HTTP.REQUEST");

		int page = ParamUtil.getInteger(httpServletRequest, "page", 1);
		int pageSize = ParamUtil.getInteger(httpServletRequest, "pageSize", 20);

		return new FDSPaginationImpl(page, pageSize);
	}

	private class FDSPaginationImpl implements FDSPagination {

		public FDSPaginationImpl(int page, int pageSize) {
			_page = page;
			_pageSize = pageSize;
		}

		@Override
		public int getEndPosition() {
			return _page * _pageSize;
		}

		@Override
		public int getPage() {
			return _page;
		}

		@Override
		public int getPageSize() {
			return _pageSize;
		}

		@Override
		public int getStartPosition() {
			return (_page - 1) * _pageSize;
		}

		private final int _page;
		private final int _pageSize;

	}

}