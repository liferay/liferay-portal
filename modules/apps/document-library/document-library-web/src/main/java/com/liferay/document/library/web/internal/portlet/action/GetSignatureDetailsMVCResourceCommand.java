/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.digital.signature.request.DSRequestDetail;
import com.liferay.digital.signature.request.DSRequestManager;
import com.liferay.digital.signature.request.DSRequestRecipientDetail;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Kim
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"mvc.command.name=/document_library/get_signature_details"
	},
	service = MVCResourceCommand.class
)
public class GetSignatureDetailsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DSRequestDetail dsRequestDetail = _dsRequestManager.getRequestDetail(
			themeDisplay.getCompanyId(),
			ParamUtil.getLong(resourceRequest, "fileEntryId"));

		if (dsRequestDetail == null) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONObject());

			return;
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"createDate", _toTime(dsRequestDetail.getCreateDate())
			).put(
				"emailSubject", dsRequestDetail.getEmailSubject()
			).put(
				"expirationDate", _toTime(dsRequestDetail.getExpirationDate())
			).put(
				"providerRequestId", dsRequestDetail.getProviderRequestId()
			).put(
				"recipients",
				JSONUtil.toJSONArray(
					dsRequestDetail.getRecipientDetails(),
					this::_toRecipientJSONObject)
			).put(
				"requesterEmailAddress",
				dsRequestDetail.getRequesterEmailAddress()
			).put(
				"requesterName", dsRequestDetail.getRequesterName()
			).put(
				"requestStatus", dsRequestDetail.getRequestStatus()
			).put(
				"statusDate", _toTime(dsRequestDetail.getStatusDate())
			));
	}

	private JSONObject _toRecipientJSONObject(
		DSRequestRecipientDetail dsRequestRecipientDetail) {

		return JSONUtil.put(
			"emailAddress", dsRequestRecipientDetail.getEmailAddress()
		).put(
			"name", dsRequestRecipientDetail.getName()
		).put(
			"requestRecipientStatus",
			dsRequestRecipientDetail.getRequestRecipientStatus()
		).put(
			"sentDate", _toTime(dsRequestRecipientDetail.getSentDate())
		).put(
			"statusDate", _toTime(dsRequestRecipientDetail.getStatusDate())
		);
	}

	private Long _toTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private JSONFactory _jsonFactory;

}