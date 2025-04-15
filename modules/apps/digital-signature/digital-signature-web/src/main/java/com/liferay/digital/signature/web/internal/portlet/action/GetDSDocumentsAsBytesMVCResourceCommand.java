/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet.action;

import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.manager.DSDocumentManager;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Nicolas Moura
 * @author Victor Trajano
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DigitalSignaturePortletKeys.DIGITAL_SIGNATURE,
		"mvc.command.name=/digital_signature/get_ds_documents_as_bytes"
	},
	service = MVCResourceCommand.class
)
public class GetDSDocumentsAsBytesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String dsEnvelopeId = ParamUtil.getString(
			resourceRequest, "dsEnvelopeId");

		byte[] dsDocumentsAsBytes = _dsDocumentManager.getDSDocumentsAsBytes(
			themeDisplay.getCompanyId(), themeDisplay.getSiteGroupId(),
			dsEnvelopeId);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, dsEnvelopeId + ".zip",
			dsDocumentsAsBytes, ContentTypes.APPLICATION_ZIP);
	}

	@Reference
	private DSDocumentManager _dsDocumentManager;

}