/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.portlet.action;

import com.liferay.ai.creator.openai.web.internal.constants.AICreatorOpenAIPortletKeys;
import com.liferay.ai.creator.openai.web.internal.constants.AICreatorOpenAIWebKeys;
import com.liferay.ai.creator.openai.web.internal.exception.AICreatorOpenAIClientException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletSession;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AICreatorOpenAIPortletKeys.AI_CREATOR_OPENAI,
		"mvc.command.name=/ai_creator_openai/get_generations"
	},
	service = MVCResourceCommand.class
)
public class GetGenerationsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!aiCreatorOpenAIConfigurationManager.
				isAICreatorOpenAIImageGenerationGroupEnabled(
					themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId())) {

			addDisabledConfigurationErrorMessage(
				resourceRequest, resourceResponse);

			return;
		}

		String apiKey =
			aiCreatorOpenAIConfigurationManager.getAICreatorOpenAIGroupAPIKey(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId());

		if (Validator.isNull(apiKey)) {
			addInvalidAPIKeyErrorMessage(resourceRequest, resourceResponse);

			return;
		}

		String prompt = ParamUtil.getString(resourceRequest, "prompt");

		if (Validator.isNull(prompt)) {
			addRequiredFieldErrorMessage(
				resourceRequest, resourceResponse, "prompt");

			return;
		}

		try {
			String[] generations = aiCreatorOpenAIClient.getGenerations(
				apiKey,
				aiCreatorOpenAIConfigurationManager.
					getAICreatorOpenAIGroupImageModel(
						themeDisplay.getCompanyId(),
						themeDisplay.getScopeGroupId()),
				prompt, ParamUtil.getString(resourceRequest, "size"),
				ParamUtil.getInteger(resourceRequest, "numberOfImages", 1));

			PortletSession portletSession = resourceRequest.getPortletSession();

			portletSession.setAttribute(
				AICreatorOpenAIWebKeys.AI_CREATOR_OPENAI_GENERATIONS,
				SetUtil.fromArray(generations));

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"generations", JSONUtil.put("content", generations)));
		}
		catch (AICreatorOpenAIClientException aiCreatorOpenAIClientException) {
			handleAICreatorOpenAIClientExceptionMessages(
				resourceRequest, resourceResponse,
				aiCreatorOpenAIClientException.getGenerationsLocalizedMessage(
					themeDisplay.getLocale()));
		}
	}

}