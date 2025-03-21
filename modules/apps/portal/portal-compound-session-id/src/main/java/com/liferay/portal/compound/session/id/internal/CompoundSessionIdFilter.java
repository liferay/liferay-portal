/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.compound.session.id.internal;

import com.liferay.portal.kernel.servlet.WrapHttpServletRequestFilter;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * <p>
 * See https://issues.liferay.com/browse/LPS-18587.
 * </p>
 *
 * @author Michael C. Han
 */
@Component(
	enabled = false,
	property = {
		"dispatcher=ERROR", "dispatcher=FORWARD", "dispatcher=INCLUDE",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Compound Session Id Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class CompoundSessionIdFilter
	extends BasePortalFilter implements WrapHttpServletRequestFilter {

	@Override
	public HttpServletRequest getWrappedHttpServletRequest(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		HttpServletRequest wrappedHttpServletRequest = httpServletRequest;

		while (wrappedHttpServletRequest instanceof HttpServletRequestWrapper) {
			if (wrappedHttpServletRequest instanceof
					CompoundSessionIdServletRequest) {

				return httpServletRequest;
			}

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)wrappedHttpServletRequest;

			wrappedHttpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		return new CompoundSessionIdServletRequest(httpServletRequest);
	}

}