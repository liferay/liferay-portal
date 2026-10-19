/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.portlet.action;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.ai.creator.openai.web.internal.client.AICreatorOpenAIClient;
import com.liferay.ai.creator.openai.web.internal.exception.AICreatorOpenAIClientException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Lourdes Fernández Besada
 */
public abstract class BaseSaveConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	protected abstract void checkPermission(ThemeDisplay themeDisplay)
		throws PortalException, PortletException;

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		checkPermission(themeDisplay);

		String imageSizes = ParamUtil.getString(actionRequest, "imageSizes");

		if (!_isValidImageSizes(imageSizes)) {
			SessionErrors.add(actionRequest, "imageSizesInvalid");

			hideDefaultErrorMessage(actionRequest);

			sendRedirect(
				actionRequest, actionResponse,
				_getRedirect(actionRequest, true, themeDisplay));

			return;
		}

		String successMessageKey = "your-request-completed-successfully";

		String apiKey = ParamUtil.getString(actionRequest, "apiKey");
		boolean enableOpenAIImageGeneration = ParamUtil.getBoolean(
			actionRequest, "enableOpenAIImageGeneration");
		String imageModel = ParamUtil.getString(actionRequest, "imageModel");

		if (Validator.isNotNull(apiKey)) {
			String model = null;

			if (enableOpenAIImageGeneration) {
				model = imageModel;

				if (Validator.isNull(model)) {
					model =
						aiCreatorOpenAIConfigurationManager.
							getAICreatorOpenAICompanyImageModel(
								themeDisplay.getCompanyId());
				}
			}

			if (!_validateAPIKey(
					actionRequest, actionResponse, apiKey, model,
					themeDisplay)) {

				return;
			}

			successMessageKey = "your-api-key-was-successfully-added";
		}
		else if (enableOpenAIImageGeneration &&
				 Validator.isNotNull(imageModel)) {

			String inheritedAPIKey = getInheritedAPIKey(themeDisplay);

			if (Validator.isNotNull(inheritedAPIKey) &&
				!_validateAPIKey(
					actionRequest, actionResponse, inheritedAPIKey, imageModel,
					themeDisplay)) {

				return;
			}
		}

		saveAICreatorOpenAIConfiguration(
			apiKey, ParamUtil.getBoolean(actionRequest, "enableChatGPT"),
			enableOpenAIImageGeneration, imageModel, imageSizes, themeDisplay);

		SessionMessages.add(
			actionRequest, "requestProcessed",
			language.get(themeDisplay.getLocale(), successMessageKey));

		sendRedirect(
			actionRequest, actionResponse,
			_getRedirect(actionRequest, false, themeDisplay));
	}

	protected abstract String getInheritedAPIKey(ThemeDisplay themeDisplay)
		throws ConfigurationException;

	protected abstract void saveAICreatorOpenAIConfiguration(
			String apiKey, boolean enableChatGPT,
			boolean enableOpenAIImageGeneration, String imageModel,
			String imageSizes, ThemeDisplay themeDisplay)
		throws ConfigurationException;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected volatile AICreatorOpenAIClient aiCreatorOpenAIClient;

	@Reference
	protected AICreatorOpenAIConfigurationManager
		aiCreatorOpenAIConfigurationManager;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	private String _getRedirect(
		ActionRequest actionRequest, boolean addParameters,
		ThemeDisplay themeDisplay) {

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNull(redirect)) {
			return redirect;
		}

		String namespace = portal.getPortletNamespace(themeDisplay.getPpid());

		for (String name : _PARAMETER_NAMES) {
			redirect = HttpComponentsUtil.removeParameter(
				redirect, namespace + name);
		}

		redirect = HttpComponentsUtil.removeParameter(
			redirect, namespace + "enableOpenAI");

		if (!addParameters) {
			return redirect;
		}

		for (String name : _PARAMETER_NAMES) {
			String value = ParamUtil.getString(actionRequest, name, null);

			if (value == null) {
				continue;
			}

			redirect = HttpComponentsUtil.addParameter(
				redirect, namespace + name, value);
		}

		return HttpComponentsUtil.addParameter(
			redirect, namespace + "enableOpenAI",
			ParamUtil.getBoolean(actionRequest, "enableOpenAI"));
	}

	private boolean _isValidImageSizes(String imageSizes) {
		for (String imageSize : StringUtil.split(imageSizes)) {
			Matcher matcher = _imageSizePattern.matcher(imageSize);

			if (!matcher.matches()) {
				return false;
			}
		}

		return true;
	}

	private boolean _validateAPIKey(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String apiKey, String model, ThemeDisplay themeDisplay)
		throws Exception {

		try {
			aiCreatorOpenAIClient.validateAPIKey(apiKey, model);

			return true;
		}
		catch (AICreatorOpenAIClientException aiCreatorOpenAIClientException) {
			SessionErrors.add(
				actionRequest, aiCreatorOpenAIClientException.getClass(),
				aiCreatorOpenAIClientException);

			hideDefaultErrorMessage(actionRequest);

			sendRedirect(
				actionRequest, actionResponse,
				_getRedirect(actionRequest, true, themeDisplay));

			return false;
		}
	}

	private static final String[] _PARAMETER_NAMES = {
		"apiKey", "imageModel", "imageSizes"
	};

	private static final Pattern _imageSizePattern = Pattern.compile(
		"\\d+x\\d+");

}