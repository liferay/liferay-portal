/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload.servlet.request.filter.internal;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.upload.LiferayInputStream;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Upload Servlet Request Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class UploadServletRequestFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		String contentType = httpServletRequest.getHeader(
			HttpHeaders.CONTENT_TYPE);

		if ((contentType != null) &&
			contentType.startsWith(ContentTypes.MULTIPART_FORM_DATA)) {

			return true;
		}

		return false;
	}

	@Override
	public void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		String portletId = ParamUtil.getString(httpServletRequest, "p_p_id");

		int fileSizeThreshold = 0;
		String location = null;

		if (Validator.isNotNull(portletId)) {
			Portlet portlet = _portletLocalService.getPortletById(
				_portal.getCompanyId(httpServletRequest), portletId);

			if (portlet != null) {
				FilterConfig filterConfig = getFilterConfig();

				InvokerPortlet invokerPortlet =
					PortletInstanceFactoryUtil.create(
						portlet, filterConfig.getServletContext());

				LiferayPortletConfig liferayPortletConfig =
					(LiferayPortletConfig)invokerPortlet.getPortletConfig();

				if (liferayPortletConfig.isCopyRequestParameters() ||
					!liferayPortletConfig.isWARFile()) {

					httpServletRequest.setAttribute(
						LiferayInputStream.COPY_MULTIPART_STREAM_TO_FILE,
						Boolean.FALSE);
				}

				fileSizeThreshold = portlet.getMultipartFileSizeThreshold();
				location = portlet.getMultipartLocation();
			}
		}

		UploadServletRequest uploadServletRequest =
			_portal.getUploadServletRequest(
				httpServletRequest, fileSizeThreshold, location);

		try {
			processFilter(
				UploadServletRequestFilter.class.getName(),
				uploadServletRequest, httpServletResponse, filterChain);
		}
		finally {
			uploadServletRequest.cleanUp();
		}
	}

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

}