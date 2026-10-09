/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.friendly.url.configuration.manager.FriendlyURLSeparatorConfigurationManager;
import com.liferay.friendly.url.separator.util.FriendlyURLSeparatorUtil;
import com.liferay.petra.string.StringPool;
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
					getFriendlyURLResolversAsCollection(
						themeDisplay.getCompanyId())) {

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

					String message = FriendlyURLSeparatorUtil.validate(
						themeDisplay.getCompanyId(),
						friendlyURLResolver.getKey(), friendlyURLSeparator,
						friendlyURLSeparators, _layoutLocalServiceHelper,
						themeDisplay.getLocale());

					if (message != null) {
						fieldsValidationErrorsJSONObject.put(
							namespace + friendlyURLResolver.getKey(), message);

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
					getFriendlyURLResolversAsCollection(
						themeDisplay.getCompanyId())) {

			if (!friendlyURLResolver.isURLSeparatorConfigurable()) {
				continue;
			}

			redirect = HttpComponentsUtil.removeParameter(
				redirect, namespace + friendlyURLResolver.getKey());

			if (validSeparators) {
				continue;
			}

			redirect = HttpComponentsUtil.addParameter(
				redirect, namespace + friendlyURLResolver.getKey(),
				ParamUtil.getString(
					actionRequest, friendlyURLResolver.getKey()));
		}

		return redirect;
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
	private LayoutLocalServiceHelper _layoutLocalServiceHelper;

	@Reference
	private Portal _portal;

}