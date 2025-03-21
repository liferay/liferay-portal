/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.struts;

import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.saml.helper.SamlHttpRequestHelper;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	property = "path=/portal/saml/metadata", service = StrutsAction.class
)
public class MetadataAction extends BaseSamlStrutsAction {

	@Override
	public boolean isEnabled() {
		return _samlProviderConfigurationHelper.isEnabled();
	}

	@Override
	protected String doExecute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletResponse.setContentType(ContentTypes.TEXT_XML);

		PrintWriter printWriter = httpServletResponse.getWriter();

		String metadata = _samlHttpRequestHelper.getEntityDescriptorString(
			httpServletRequest);

		printWriter.print(metadata);

		return null;
	}

	@Reference
	private SamlHttpRequestHelper _samlHttpRequestHelper;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}