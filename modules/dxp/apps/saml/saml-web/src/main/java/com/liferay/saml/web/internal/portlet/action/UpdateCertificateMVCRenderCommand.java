/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.runtime.certificate.CertificateTool;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/admin/update_certificate"
	},
	service = MVCRenderCommand.class
)
public class UpdateCertificateMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			SamlWebKeys.SAML_CERTIFICATE_TOOL, _certificateTool);

		return "/admin/update_certificate.jsp";
	}

	@Reference
	private CertificateTool _certificateTool;

}