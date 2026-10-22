/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.servlet.filter;

import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.upload.LiferayInputStream;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jaime León
 */
@Component(
	property = {
		"before-filter=Upload Servlet Request Filter", "dispatcher=REQUEST",
		"servlet-context-name=",
		"servlet-filter-name=Import Preview Upload Filter",
		"url-pattern=/o/export-import/*"
	},
	service = Filter.class
)
public class ImportPreviewUploadFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		String requestURI = httpServletRequest.getRequestURI();

		return requestURI.endsWith("/import-preview");
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		httpServletRequest.setAttribute(
			LiferayInputStream.COPY_MULTIPART_STREAM_TO_FILE, Boolean.FALSE);

		processFilter(
			ImportPreviewUploadFilter.class.getName(), httpServletRequest,
			httpServletResponse, filterChain);
	}

}