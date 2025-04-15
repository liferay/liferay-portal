/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.handler;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTRequiredModelException;
import com.liferay.portal.kernel.exception.LayoutNameException;
import com.liferay.portal.kernel.exception.LayoutTypeException;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.LayoutTypeControllerTracker;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ResourceBundle;

/**
 * @author Jürgen Kappler
 */
public class LayoutExceptionRequestHandlerUtil {

	public static void handleException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			Exception exception)
		throws Exception {

		if ((exception instanceof ModelListenerException) &&
			(exception.getCause() instanceof PortalException)) {

			_handlePortalException(
				actionRequest, actionResponse,
				(PortalException)exception.getCause());
		}
		else if (exception instanceof PortalException) {
			_handlePortalException(
				actionRequest, actionResponse, (PortalException)exception);
		}
		else if (exception.getCause() instanceof CTRequiredModelException) {
			SessionMessages.add(
				actionRequest,
				PortalUtil.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);

			JSONObject jsonObject = JSONUtil.put(
				"errorMessage",
				LanguageUtil.get(
					PortalUtil.getLocale(actionRequest),
					"item-cannot-be-deleted-because-it-is-being-modified-in-" +
						"one-or-more-publications"));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);

			return;
		}

		throw exception;
	}

	private static String _handleLayoutTypeException(
		ActionRequest actionRequest, int exceptionType, String layoutType) {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (exceptionType == LayoutTypeException.FIRST_LAYOUT_PERMISSION) {
			return LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-first-page-should-be-visible-for-guest-users");
		}

		String errorMessage = "pages-of-type-x-cannot-be-selected";

		if (exceptionType == LayoutTypeException.FIRST_LAYOUT) {
			errorMessage = "the-first-page-cannot-be-of-type-x";
		}

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(layoutType);

		ResourceBundle layoutTypeResourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", themeDisplay.getLocale(),
			layoutTypeController.getClass());

		return LanguageUtil.format(
			themeDisplay.getRequest(), errorMessage,
			LanguageUtil.get(
				layoutTypeResourceBundle, "layout.types." + layoutType));
	}

	private static void _handlePortalException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			PortalException portalException)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(portalException);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String errorMessage = null;

		if (portalException instanceof AssetCategoryException) {
			AssetCategoryException assetCategoryException =
				(AssetCategoryException)portalException;

			AssetVocabulary assetVocabulary =
				assetCategoryException.getVocabulary();

			String assetVocabularyTitle = StringPool.BLANK;

			if (assetVocabulary != null) {
				assetVocabularyTitle = assetVocabulary.getTitle(
					themeDisplay.getLocale());
			}

			if (assetCategoryException.getType() ==
					AssetCategoryException.AT_LEAST_ONE_CATEGORY) {

				errorMessage = LanguageUtil.format(
					themeDisplay.getRequest(),
					"please-select-at-least-one-category-for-x",
					assetVocabularyTitle);
			}
			else if (assetCategoryException.getType() ==
						AssetCategoryException.TOO_MANY_CATEGORIES) {

				errorMessage = LanguageUtil.format(
					themeDisplay.getRequest(),
					"you-cannot-select-more-than-one-category-for-x",
					assetVocabularyTitle);
			}
		}
		else if (portalException instanceof
					DuplicateFriendlyURLEntryException) {

			errorMessage = LanguageUtil.get(
				themeDisplay.getRequest(),
				"the-friendly-url-is-already-in-use.-please-enter-a-unique-" +
					"friendly-url");
		}
		else if (portalException instanceof LayoutNameException) {
			LayoutNameException layoutNameException =
				(LayoutNameException)portalException;

			if (layoutNameException.getType() == LayoutNameException.TOO_LONG) {
				errorMessage = LanguageUtil.format(
					themeDisplay.getRequest(),
					"page-name-cannot-exceed-x-characters",
					ModelHintsUtil.getMaxLength(
						Layout.class.getName(), "friendlyURL"));
			}
			else {
				errorMessage = LanguageUtil.get(
					themeDisplay.getRequest(),
					"please-enter-a-valid-name-for-the-page");
			}
		}
		else if (portalException instanceof LayoutTypeException) {
			LayoutTypeException layoutTypeException =
				(LayoutTypeException)portalException;

			if ((layoutTypeException.getType() ==
					LayoutTypeException.FIRST_LAYOUT) ||
				(layoutTypeException.getType() ==
					LayoutTypeException.FIRST_LAYOUT_PERMISSION) ||
				(layoutTypeException.getType() ==
					LayoutTypeException.NOT_INSTANCEABLE)) {

				errorMessage = _handleLayoutTypeException(
					actionRequest, layoutTypeException.getType(),
					layoutTypeException.getLayoutType());
			}
		}
		else if (portalException instanceof PrincipalException) {
			errorMessage = LanguageUtil.get(
				themeDisplay.getRequest(),
				"you-do-not-have-the-required-permissions");
		}

		if (Validator.isNull(errorMessage)) {
			errorMessage = LanguageUtil.get(
				themeDisplay.getRequest(), "an-unexpected-error-occurred");

			_log.error(portalException);
		}

		JSONObject jsonObject = JSONUtil.put("errorMessage", errorMessage);

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutExceptionRequestHandlerUtil.class);

}