/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.portlet.action;

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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

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

		String successMessageKey = "your-request-completed-successfully";

		String apiKey = ParamUtil.getString(actionRequest, "apiKey");

		if (Validator.isNotNull(apiKey)) {
			try {
				aiCreatorOpenAIClient.validateAPIKey(apiKey);

				successMessageKey = "your-api-key-was-successfully-added";
			}
			catch (AICreatorOpenAIClientException
						aiCreatorOpenAIClientException) {

				SessionErrors.add(
					actionRequest, aiCreatorOpenAIClientException.getClass(),
					aiCreatorOpenAIClientException);

				hideDefaultErrorMessage(actionRequest);

				sendRedirect(
					actionRequest, actionResponse,
					_getRedirect(actionRequest, true, themeDisplay));

				return;
			}
		}

		saveAICreatorOpenAIConfiguration(
			apiKey, ParamUtil.getBoolean(actionRequest, "enableChatGPT"),
			ParamUtil.getBoolean(actionRequest, "enableDALLE"), themeDisplay);

		SessionMessages.add(
			actionRequest, "requestProcessed",
			language.get(themeDisplay.getLocale(), successMessageKey));

		sendRedirect(
			actionRequest, actionResponse,
			_getRedirect(actionRequest, false, themeDisplay));
	}

	protected abstract void saveAICreatorOpenAIConfiguration(
			String apiKey, boolean enableChatGPT, boolean enableDALLE,
			ThemeDisplay themeDisplay)
		throws ConfigurationException;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected volatile AICreatorOpenAIClient aiCreatorOpenAIClient;

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

		redirect = HttpComponentsUtil.removeParameter(
			redirect, namespace + "apiKey");
		redirect = HttpComponentsUtil.removeParameter(
			redirect, namespace + "enableOpenAI");

		if (!addParameters) {
			return redirect;
		}

		String apiKey = ParamUtil.getString(actionRequest, "apiKey", null);

		if (apiKey != null) {
			redirect = HttpComponentsUtil.addParameter(
				redirect, namespace + "apiKey", apiKey);
		}

		return HttpComponentsUtil.addParameter(
			redirect, namespace + "enableOpenAI",
			ParamUtil.getBoolean(actionRequest, "enableOpenAI"));
	}

}