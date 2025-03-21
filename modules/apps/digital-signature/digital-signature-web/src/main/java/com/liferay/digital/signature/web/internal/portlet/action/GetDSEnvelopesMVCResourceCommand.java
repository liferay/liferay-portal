/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet.action;

import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

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
		"mvc.command.name=/digital_signature/get_ds_envelopes"
	},
	service = MVCResourceCommand.class
)
public class GetDSEnvelopesMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Page<DSEnvelope> page = _dsEnvelopeManager.getDSEnvelopesPage(
			themeDisplay.getCompanyId(), themeDisplay.getSiteGroupId(),
			ParamUtil.getString(resourceRequest, "from_date", "2000-01-01"),
			StringUtil.replace(
				ParamUtil.getString(resourceRequest, "keywords"),
				CharPool.SPACE, CharPool.PLUS),
			StringUtil.removeSubstring(
				ParamUtil.getString(resourceRequest, "sort", "desc"),
				"createdLocalDateTime:"),
			Pagination.of(
				ParamUtil.getInteger(resourceRequest, "page"),
				ParamUtil.getInteger(resourceRequest, "pageSize")),
			ParamUtil.getString(resourceRequest, "status"));

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, page.toString());
	}

	@Reference
	private DSEnvelopeManager _dsEnvelopeManager;

}