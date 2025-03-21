/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.friendly.url.configuration.manager.FriendlyURLSeparatorConfigurationManager;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.impl.LayoutLocalServiceHelper;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/instance_settings/friendly_url_separator_save_company_configuration"
	},
	service = MVCActionCommand.class
)
public class FriendlyURLSeparatorSaveCompanyConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin(themeDisplay.getCompanyId())) {
			PrincipalException principalException =
				new PrincipalException.MustBeCompanyAdmin(
					permissionChecker.getUserId());

			throw new PortletException(principalException);
		}

		JSONObject fieldsValidationErrorsJSONObject =
			_jsonFactory.createJSONObject();

		String friendlyURLSeparators = _getFriendlyURLSeparators(
			actionRequest, fieldsValidationErrorsJSONObject, themeDisplay);

		if (fieldsValidationErrorsJSONObject.length() == 0) {
			_friendlyURLSeparatorConfigurationManager.
				updateFriendlyURLSeparatorCompanyConfiguration(
					themeDisplay.getCompanyId(), friendlyURLSeparators);

			addSuccessMessage(actionRequest, actionResponse);
		}
		else {
			hideDefaultSuccessMessage(actionRequest);
		}

		sendRedirect(
			actionRequest, actionResponse,
			_getRedirect(
				actionRequest, fieldsValidationErrorsJSONObject, themeDisplay));
	}

	private String _getFriendlyURLSeparators(
		ActionRequest actionRequest,
		JSONObject fieldsValidationErrorsJSONObject,
		ThemeDisplay themeDisplay) {

		JSONObject friendlyURLSeparatorsJSONObject =
			_jsonFactory.createJSONObject();

		List<String> friendlyURLSeparators = new ArrayList<>();
		String namespace = _portal.getPortletNamespace(themeDisplay.getPpid());

		for (FriendlyURLResolver friendlyURLResolver :
				FriendlyURLResolverRegistryUtil.
					getFriendlyURLResolversAsCollection()) {

			if (!friendlyURLResolver.isURLSeparatorConfigurable()) {
				continue;
			}

			friendlyURLSeparatorsJSONObject.put(
				friendlyURLResolver.getKey(),
				() -> {
					String friendlyURLSeparator = ParamUtil.getString(
						actionRequest, friendlyURLResolver.getKey());

					if (Validator.isNull(friendlyURLSeparator)) {
						fieldsValidationErrorsJSONObject.put(
							namespace + friendlyURLResolver.getKey(),
							_language.get(
								themeDisplay.getLocale(),
								"friendly-url-separator-error-cannot-be-" +
									"empty"));

						return null;
					}

					friendlyURLSeparator =
						_friendlyURLNormalizer.normalizeWithPeriodsAndSlashes(
							friendlyURLSeparator);

					friendlyURLSeparator =
						StringPool.SLASH + friendlyURLSeparator +
							StringPool.SLASH;

					if (friendlyURLSeparators.contains(friendlyURLSeparator)) {
						fieldsValidationErrorsJSONObject.put(
							namespace + friendlyURLResolver.getKey(),
							_language.get(
								themeDisplay.getLocale(),
								"friendly-url-separator-error-other-asset-" +
									"type-may-use-this-prefix"));

						return null;
					}

					friendlyURLSeparators.add(friendlyURLSeparator);

					_validateURLSeparator(
						fieldsValidationErrorsJSONObject,
						friendlyURLResolver.getKey(), themeDisplay,
						friendlyURLSeparator);

					if (fieldsValidationErrorsJSONObject.length() > 0) {
						return null;
					}

					return friendlyURLSeparator;
				});
		}

		return friendlyURLSeparatorsJSONObject.toString();
	}

	private String _getRedirect(
		ActionRequest actionRequest,
		JSONObject fieldsValidationErrorsJSONObject,
		ThemeDisplay themeDisplay) {

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNull(redirect)) {
			return redirect;
		}

		String namespace = _portal.getPortletNamespace(themeDisplay.getPpid());

		redirect = HttpComponentsUtil.removeParameter(
			redirect, namespace + "errors");

		boolean validSeparators = false;

		if (fieldsValidationErrorsJSONObject.length() == 0) {
			validSeparators = true;
		}

		if (!validSeparators) {
			redirect = HttpComponentsUtil.addParameter(
				redirect, namespace + "errors",
				JSONUtil.put(
					"errorMessage",
					_language.get(
						themeDisplay.getLocale(),
						"friendly-url-separator-error-changes-could-not-be-" +
							"saved")
				).put(
					"fields", fieldsValidationErrorsJSONObject
				).toString());
		}

		for (FriendlyURLResolver friendlyURLResolver :
				FriendlyURLResolverRegistryUtil.
					getFriendlyURLResolversAsCollection()) {

			if (!friendlyURLResolver.isURLSeparatorConfigurable()) {
				continue;
			}

			redirect = HttpComponentsUtil.removeParameter(
				redirect, namespace + friendlyURLResolver.getKey());

			if (!validSeparators) {
				redirect = HttpComponentsUtil.addParameter(
					redirect, namespace + friendlyURLResolver.getKey(),
					ParamUtil.getString(
						actionRequest, friendlyURLResolver.getKey()));
			}
		}

		return redirect;
	}

	private void _validateURLSeparator(
		JSONObject fieldsValidationErrorsJSONObject, String key,
		ThemeDisplay themeDisplay, String urlSeparator) {

		String namespace = _portal.getPortletNamespace(themeDisplay.getPpid());

		if (urlSeparator.length() < 3) {
			fieldsValidationErrorsJSONObject.put(
				namespace + key,
				_language.format(
					themeDisplay.getLocale(),
					"friendly-url-separator-error-should-have-at-least-x-" +
						"characters",
					3));

			return;
		}

		if (urlSeparator.length() > 255) {
			fieldsValidationErrorsJSONObject.put(
				namespace + key,
				_language.format(
					themeDisplay.getLocale(),
					"friendly-url-separator-error-should-have-at-most-x-" +
						"characters",
					255));

			return;
		}

		if (urlSeparator.contains(Portal.FRIENDLY_URL_SEPARATOR)) {
			fieldsValidationErrorsJSONObject.put(
				namespace + key,
				_language.get(
					themeDisplay.getLocale(),
					"friendly-url-separator-error-invalid-characters"));

			return;
		}

		if (Validator.isNumber(
				urlSeparator.substring(1, urlSeparator.length() - 1))) {

			fieldsValidationErrorsJSONObject.put(
				namespace + key,
				_language.get(
					themeDisplay.getLocale(),
					"friendly-url-separator-error-can-not-be-a-number"));
		}

		String friendlyURL = urlSeparator.substring(
			0, urlSeparator.length() - 1);

		try {
			_layoutLocalServiceHelper.validateFriendlyURLKeyword(friendlyURL);
		}
		catch (LayoutFriendlyURLException layoutFriendlyURLException) {
			String keywordConflict =
				layoutFriendlyURLException.getKeywordConflict();

			if (!keywordConflict.endsWith(StringPool.SLASH)) {
				keywordConflict = keywordConflict + StringPool.SLASH;
			}

			FriendlyURLResolver friendlyURLResolver1 =
				FriendlyURLResolverRegistryUtil.
					getFriendlyURLResolverByDefaultURLSeparator(
						keywordConflict);

			FriendlyURLResolver friendlyURLResolver2 =
				FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
					keywordConflict);

			if (((friendlyURLResolver1 == null) &&
				 (friendlyURLResolver2 == null)) ||
				((friendlyURLResolver1 != null) &&
				 Objects.equals(
					 friendlyURLResolver1.getDefaultURLSeparator(),
					 keywordConflict) &&
				 !Objects.equals(friendlyURLResolver1.getKey(), key)) ||
				((friendlyURLResolver2 != null) &&
				 Objects.equals(
					 friendlyURLResolver2.getURLSeparator(), keywordConflict) &&
				 !Objects.equals(friendlyURLResolver2.getKey(), key))) {

				fieldsValidationErrorsJSONObject.put(
					namespace + key,
					_language.get(
						themeDisplay.getLocale(),
						"friendly-url-separator-error-other-asset-type-may-" +
							"use-this-prefix"));
			}
		}

		int count1 = _layoutFriendlyURLLocalService.getLayoutFriendlyURLsCount(
			themeDisplay.getCompanyId(), friendlyURL);
		int count2 = _layoutFriendlyURLLocalService.getLayoutFriendlyURLsCount(
			themeDisplay.getCompanyId(), urlSeparator + CharPool.PERCENT);

		if ((count1 > 0) || (count2 > 0)) {
			fieldsValidationErrorsJSONObject.put(
				namespace + key,
				_language.get(
					themeDisplay.getLocale(),
					"friendly-url-separator-error-other-asset-type-may-use-" +
						"this-prefix"));
		}
	}

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private FriendlyURLSeparatorConfigurationManager
		_friendlyURLSeparatorConfigurationManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

	@Reference
	private LayoutLocalServiceHelper _layoutLocalServiceHelper;

	@Reference
	private Portal _portal;

}