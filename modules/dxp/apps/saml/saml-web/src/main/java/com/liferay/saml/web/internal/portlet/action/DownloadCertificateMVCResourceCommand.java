/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.runtime.metadata.LocalEntityManager;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/admin/download_certificate"
	},
	service = MVCResourceCommand.class
)
public class DownloadCertificateMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		LocalEntityManager.CertificateUsage certificateUsage =
			LocalEntityManager.CertificateUsage.valueOf(
				ParamUtil.getString(resourceRequest, "certificateUsage"));

		String encodedCertificate =
			_localEntityManager.getEncodedLocalEntityCertificate(
				certificateUsage);

		if (Validator.isNull(encodedCertificate)) {
			return;
		}

		String content = StringBundler.concat(
			"-----BEGIN CERTIFICATE-----\r\n", encodedCertificate,
			"\r\n-----END CERTIFICATE-----");

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse,
			StringBundler.concat(
				_localEntityManager.getLocalEntityId(), StringPool.DASH,
				certificateUsage.name(), ".pem"),
			content.getBytes(), ContentTypes.TEXT_PLAIN);
	}

	@Reference
	private LocalEntityManager _localEntityManager;

}