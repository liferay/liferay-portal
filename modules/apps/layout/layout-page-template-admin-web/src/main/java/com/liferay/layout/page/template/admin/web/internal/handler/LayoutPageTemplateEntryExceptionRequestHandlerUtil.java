/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.handler;

import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryNameException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

/**
 * @author Jürgen Kappler
 */
public class LayoutPageTemplateEntryExceptionRequestHandlerUtil {

	public static JSONObject createErrorJSONObject(
		ActionRequest actionRequest, PortalException portalException) {

		if (_log.isDebugEnabled()) {
			_log.debug(portalException);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String errorMessage = null;

		if (portalException instanceof
				LayoutPageTemplateEntryNameException.MustNotBeDuplicate) {

			errorMessage = LanguageUtil.get(
				themeDisplay.getLocale(),
				"a-page-template-entry-with-that-name-already-exists");
		}
		else if (portalException instanceof
					LayoutPageTemplateEntryNameException.MustNotBeNull) {

			errorMessage = LanguageUtil.get(
				themeDisplay.getLocale(), "name-must-not-be-empty");
		}
		else if (portalException instanceof
					LayoutPageTemplateEntryNameException.
						MustNotContainInvalidCharacters) {

			LayoutPageTemplateEntryNameException.MustNotContainInvalidCharacters
				lptene =
					(LayoutPageTemplateEntryNameException.
						MustNotContainInvalidCharacters)portalException;

			errorMessage = LanguageUtil.format(
				themeDisplay.getLocale(),
				"name-cannot-contain-the-following-invalid-character-x",
				lptene.character);
		}
		else if (portalException instanceof
					LayoutPageTemplateEntryNameException.
						MustNotExceedMaximumSize) {

			int nameMaxLength = ModelHintsUtil.getMaxLength(
				LayoutPageTemplateEntry.class.getName(), "name");

			errorMessage = LanguageUtil.format(
				themeDisplay.getLocale(),
				"please-enter-a-name-with-fewer-than-x-characters",
				nameMaxLength);
		}

		if (Validator.isNull(errorMessage)) {
			errorMessage = LanguageUtil.get(
				themeDisplay.getLocale(), "an-unexpected-error-occurred");

			_log.error(portalException);
		}

		return JSONUtil.put("error", errorMessage);
	}

	public static void handlePortalException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			PortalException portalException)
		throws Exception {

		JSONObject errorJSONObject = createErrorJSONObject(
			actionRequest, portalException);

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, errorJSONObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateEntryExceptionRequestHandlerUtil.class);

}