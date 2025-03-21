/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.handler;

import com.liferay.layout.utility.page.exception.LayoutUtilityPageEntryNameException;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutUtilityPageEntryPortalExceptionRequestHandlerUtil {

	public static void handlePortalException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			PortalException portalException)
		throws Exception {

		_handlePortalException(actionRequest, actionResponse, portalException);
	}

	private static JSONObject _createErrorJSONObject(
		ActionRequest actionRequest, PortalException portalException) {

		if (_log.isDebugEnabled()) {
			_log.debug(portalException);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String errorMessage = null;

		if (portalException instanceof
				LayoutUtilityPageEntryNameException.MustNotBeNull) {

			errorMessage = LanguageUtil.get(
				themeDisplay.getLocale(), "name-must-not-be-empty");
		}
		else if (portalException instanceof
					LayoutUtilityPageEntryNameException.MustNotBeDuplicate) {

			String name = ParamUtil.getString(actionRequest, "name");

			errorMessage = LanguageUtil.format(
				themeDisplay.getLocale(),
				"there-is-already-a-utility-page-with-the-name-x",
				new String[] {name});
		}
		else if (portalException instanceof
					LayoutUtilityPageEntryNameException.
						MustNotContainInvalidCharacters) {

			LayoutUtilityPageEntryNameException.MustNotContainInvalidCharacters
				layoutUtilityPageEntryNameException =
					(LayoutUtilityPageEntryNameException.
						MustNotContainInvalidCharacters)portalException;

			errorMessage = LanguageUtil.format(
				themeDisplay.getLocale(),
				"name-cannot-contain-the-following-invalid-character-x",
				layoutUtilityPageEntryNameException.character);
		}
		else if (portalException instanceof
					LayoutUtilityPageEntryNameException.
						MustNotExceedMaximumSize) {

			int nameMaxLength = ModelHintsUtil.getMaxLength(
				LayoutUtilityPageEntry.class.getName(), "name");

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

	private static void _handlePortalException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			PortalException portalException)
		throws Exception {

		JSONObject errorJSONObject = _createErrorJSONObject(
			actionRequest, portalException);

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, errorJSONObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutUtilityPageEntryPortalExceptionRequestHandlerUtil.class);

}