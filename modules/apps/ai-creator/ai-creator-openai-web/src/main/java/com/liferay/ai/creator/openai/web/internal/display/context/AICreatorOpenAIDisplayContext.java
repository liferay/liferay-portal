/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.display.context;

import com.liferay.ai.creator.openai.web.internal.constants.AICreatorOpenAIPortletKeys;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 * @author Roberto Díaz
 * @author Ambrín Chaudhary
 */
public class AICreatorOpenAIDisplayContext {

	public AICreatorOpenAIDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public Map<String, Object> getCompletionProps() {
		return HashMapBuilder.<String, Object>put(
			"getCompletionURL",
			() -> {
				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						_httpServletRequest);

				return ResourceURLBuilder.createResourceURL(
					(ResourceURL)
						requestBackedPortletURLFactory.createResourceURL(
							AICreatorOpenAIPortletKeys.AI_CREATOR_OPENAI)
				).setResourceID(
					"/ai_creator_openai/get_completion"
				).buildString();
			}
		).put(
			"learnResources",
			LearnMessageUtil.getReactDataJSONObject("ai-creator-openai-web")
		).build();
	}

	public Map<String, Object> getGenerationsProps() {
		return HashMapBuilder.<String, Object>put(
			"eventName",
			ParamUtil.getString(_httpServletRequest, "selectEventName")
		).put(
			"getGenerationsURL",
			() -> {
				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						_httpServletRequest);

				return ResourceURLBuilder.createResourceURL(
					(ResourceURL)
						requestBackedPortletURLFactory.createResourceURL(
							AICreatorOpenAIPortletKeys.AI_CREATOR_OPENAI)
				).setResourceID(
					"/ai_creator_openai/get_generations"
				).buildString();
			}
		).put(
			"learnResources",
			LearnMessageUtil.getReactDataJSONObject("ai-creator-openai-web")
		).put(
			"uploadGenerationsURL",
			() -> {
				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						_httpServletRequest);

				return PortletURLBuilder.create(
					requestBackedPortletURLFactory.createActionURL(
						AICreatorOpenAIPortletKeys.AI_CREATOR_OPENAI)
				).setActionName(
					"/ai_creator_openai/upload_generations"
				).setParameter(
					"fileEntryTypeId",
					ParamUtil.getLong(_httpServletRequest, "fileEntryTypeId")
				).setParameter(
					"folderId",
					ParamUtil.getLong(_httpServletRequest, "folderId")
				).setParameter(
					"repositoryId",
					ParamUtil.getLong(_httpServletRequest, "repositoryId")
				).buildString();
			}
		).build();
	}

	public boolean isGenerations() {
		if (_generations != null) {
			return _generations;
		}

		_generations = ParamUtil.getBoolean(_httpServletRequest, "generations");

		return _generations;
	}

	private Boolean _generations;
	private final HttpServletRequest _httpServletRequest;

}