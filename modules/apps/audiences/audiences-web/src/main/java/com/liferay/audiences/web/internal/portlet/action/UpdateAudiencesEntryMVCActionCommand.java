/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.portlet.action;

import com.liferay.audiences.constants.AudiencesPortletKeys;
import com.liferay.audiences.exception.AudiencesEntryJSONException;
import com.liferay.audiences.exception.AudiencesEntryNameException;
import com.liferay.audiences.exception.DuplicateAudiencesEntryExternalReferenceCodeException;
import com.liferay.audiences.exception.NoSuchAudiencesEntryException;
import com.liferay.audiences.service.AudiencesEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AudiencesPortletKeys.AUDIENCES,
		"mvc.command.name=/audiences/update_audiences_entry"
	},
	service = MVCActionCommand.class
)
public class UpdateAudiencesEntryMVCActionCommand implements MVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			return _processAction(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private String _getErrorFieldName(Exception exception) {
		if (exception instanceof AudiencesEntryNameException) {
			return "name";
		}

		if (exception instanceof
				DuplicateAudiencesEntryExternalReferenceCodeException) {

			return "externalReferenceCode";
		}

		return null;
	}

	private String _getErrorMessageKey(Exception exception) {
		if (exception instanceof AudiencesEntryJSONException) {
			return "you-have-entered-invalid-json";
		}

		if (exception instanceof AudiencesEntryNameException) {
			return "please-enter-a-valid-name";
		}

		if (exception instanceof
				DuplicateAudiencesEntryExternalReferenceCodeException) {

			return "please-enter-a-unique-external-reference-code";
		}

		if (exception instanceof NoSuchAudiencesEntryException) {
			return "the-audiences-could-not-be-found";
		}

		if (exception instanceof PrincipalException) {
			return "you-do-not-have-the-required-permissions";
		}

		return null;
	}

	private boolean _processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long audiencesEntryId = ParamUtil.getLong(
			actionRequest, "audiencesEntryId");

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");
		String json = ParamUtil.getString(actionRequest, "json");
		String name = ParamUtil.getString(actionRequest, "name");

		try {
			if (audiencesEntryId <= 0) {
				_audiencesEntryService.addAudiencesEntry(
					externalReferenceCode, json, name);
			}
			else {
				_audiencesEntryService.updateAudiencesEntry(
					audiencesEntryId, externalReferenceCode, json, name);
			}
		}
		catch (Exception exception) {
			String errorMessageKey = _getErrorMessageKey(exception);

			if (errorMessageKey == null) {
				throw exception;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"errorField", _getErrorFieldName(exception)
				).put(
					"errorMessage",
					_language.get(themeDisplay.getLocale(), errorMessageKey)
				));

			return false;
		}

		MultiSessionMessages.add(
			actionRequest, "requestProcessed", StringPool.BLANK);

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, _jsonFactory.createJSONObject());

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateAudiencesEntryMVCActionCommand.class);

	@Reference
	private AudiencesEntryService _audiencesEntryService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}