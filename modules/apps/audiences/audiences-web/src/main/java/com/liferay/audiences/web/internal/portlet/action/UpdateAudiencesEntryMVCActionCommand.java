/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.portlet.action;

import com.liferay.audiences.constants.AudiencesPortletKeys;
import com.liferay.audiences.exception.AudiencesEntryJSONAttributeException;
import com.liferay.audiences.exception.AudiencesEntryJSONException;
import com.liferay.audiences.exception.AudiencesEntryNameException;
import com.liferay.audiences.exception.DuplicateAudiencesEntryExternalReferenceCodeException;
import com.liferay.audiences.exception.NoSuchAudiencesEntryException;
import com.liferay.audiences.service.AudiencesEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

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
public class UpdateAudiencesEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
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

			MultiSessionMessages.add(
				actionRequest, "requestProcessed", StringPool.BLANK);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, _jsonFactory.createJSONObject());
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			hideDefaultErrorMessage(actionRequest);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"error", _getErrorJSONObject(exception, themeDisplay)));
		}
	}

	private JSONObject _getErrorJSONObject(
		Exception exception, ThemeDisplay themeDisplay) {

		if (exception instanceof AudiencesEntryJSONAttributeException) {
			return JSONUtil.put(
				"other",
				_language.get(
					themeDisplay.getLocale(),
					"you-have-entered-an-invalid-custom-attribute"));
		}
		else if (exception instanceof AudiencesEntryJSONException) {
			return JSONUtil.put(
				"other",
				_language.get(
					themeDisplay.getLocale(), "you-have-entered-invalid-json"));
		}
		else if (exception instanceof AudiencesEntryNameException) {
			return JSONUtil.put(
				"name",
				_language.get(
					themeDisplay.getLocale(), "please-enter-a-valid-name"));
		}
		else if (exception instanceof
					DuplicateAudiencesEntryExternalReferenceCodeException) {

			return JSONUtil.put(
				"externalReferenceCode",
				_language.get(
					themeDisplay.getLocale(),
					"please-enter-a-unique-external-reference-code"));
		}
		else if (exception instanceof NoSuchAudiencesEntryException) {
			return JSONUtil.put(
				"other",
				_language.get(
					themeDisplay.getLocale(),
					"the-audiences-could-not-be-found"));
		}
		else if (exception instanceof PrincipalException) {
			return JSONUtil.put(
				"other",
				_language.get(
					themeDisplay.getLocale(),
					"you-do-not-have-the-required-permissions"));
		}

		if (_log.isDebugEnabled()) {
			_log.debug(exception);
		}

		return JSONUtil.put(
			"other",
			_language.get(
				themeDisplay.getLocale(), "an-unexpected-error-occurred"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateAudiencesEntryMVCActionCommand.class);

	@Reference
	private AudiencesEntryService _audiencesEntryService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}