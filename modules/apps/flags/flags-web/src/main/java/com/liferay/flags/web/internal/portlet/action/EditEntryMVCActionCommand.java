/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.flags.web.internal.portlet.action;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.flags.service.FlagsEntryService;
import com.liferay.flags.web.internal.constants.FlagsPortletKeys;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.captcha.CaptchaTextException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 * @author Peter Fellwock
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FlagsPortletKeys.FLAGS,
		"mvc.command.name=/flags/edit_entry"
	},
	service = MVCActionCommand.class
)
public class EditEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			CaptchaUtil.check(actionRequest);

			String className = ParamUtil.getString(actionRequest, "className");
			long classPK = ParamUtil.getLong(actionRequest, "classPK");
			String reporterEmailAddress = ParamUtil.getString(
				actionRequest, "reporterEmailAddress");
			long reportedUserId = ParamUtil.getLong(
				actionRequest, "reportedUserId");
			String contentTitle = ParamUtil.getString(
				actionRequest, "contentTitle");
			String contentURL = ParamUtil.getString(
				actionRequest, "contentURL");
			String reason = ParamUtil.getString(actionRequest, "reason");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				"com.liferay.portlet.flags.model.FlagsEntry", actionRequest);

			_flagsEntryService.addEntry(
				className, classPK, reporterEmailAddress, reportedUserId,
				contentTitle, contentURL, reason, serviceContext);
		}
		catch (CaptchaException captchaException) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			jsonObject.put(
				"error",
				_language.get(
					themeDisplay.getRequest(),
					_getCaptchaExceptionErrorMessageKey(captchaException)));
		}
		catch (Exception exception) {
			_log.error(exception);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			jsonObject.put(
				"error",
				_language.get(
					themeDisplay.getRequest(), "an-unexpected-error-occurred"));
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private String _getCaptchaExceptionErrorMessageKey(
		CaptchaException captchaException) {

		if (captchaException instanceof CaptchaTextException) {
			return "text-verification-failed";
		}

		return "captcha-verification-failed";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditEntryMVCActionCommand.class);

	@Reference
	private FlagsEntryService _flagsEntryService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}