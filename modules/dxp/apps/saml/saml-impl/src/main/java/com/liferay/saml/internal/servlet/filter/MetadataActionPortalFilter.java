/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.servlet.filter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.saml.helper.SamlHttpRequestHelper;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christopher Kian
 */
@Component(
	property = {
		"after-filter=Virtual Host Filter", "dispatcher=REQUEST",
		"enabled=true", "servlet-context-name=",
		"servlet-filter-name=Metadata Action SAML Portal Filter",
		"url-pattern=/c/portal/saml/metadata"
	},
	service = Filter.class
)
public class MetadataActionPortalFilter extends BaseSamlPortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return _samlProviderConfigurationHelper.isEnabled();
	}

	@Override
	protected void doProcessFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		httpServletResponse.setContentType(ContentTypes.TEXT_XML);

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.print(
			_samlHttpRequestHelper.getEntityDescriptorString(
				httpServletRequest));
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MetadataActionPortalFilter.class);

	@Reference
	private SamlHttpRequestHelper _samlHttpRequestHelper;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}