/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.display.context;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public abstract class BaseAICreatorOpenAIConfigurationDisplayContext {

	public BaseAICreatorOpenAIConfigurationDisplayContext(
		HttpServletRequest httpServletRequest) {

		this.httpServletRequest = httpServletRequest;
	}

	public String getAPIKey() throws ConfigurationException {
		String apiKey = ParamUtil.getString(httpServletRequest, "apiKey", null);

		if (apiKey != null) {
			return apiKey;
		}

		return getAICreatorOpenAIAPIKey();
	}

	public String getImageModel() throws ConfigurationException {
		String imageModel = ParamUtil.getString(
			httpServletRequest, "imageModel", null);

		if (imageModel != null) {
			return imageModel;
		}

		return getAICreatorOpenAIImageModel();
	}

	public String getImageSizes() throws ConfigurationException {
		String imageSizes = ParamUtil.getString(
			httpServletRequest, "imageSizes", null);

		if (imageSizes != null) {
			return imageSizes;
		}

		return getAICreatorOpenAIImageSizes();
	}

	public boolean isChatGPTEnabled() throws ConfigurationException {
		String enabled = ParamUtil.getString(
			httpServletRequest, "enableChatGPT", null);

		if (enabled != null) {
			return GetterUtil.getBoolean(enabled);
		}

		return isAICreatorChatGPTEnabled();
	}

	public boolean isOpenAIImageGenerationEnabled()
		throws ConfigurationException {

		String enabled = ParamUtil.getString(
			httpServletRequest, "enableOpenAIImageGeneration", null);

		if (enabled != null) {
			return GetterUtil.getBoolean(enabled);
		}

		return isAICreatorOpenAIImageGenerationEnabled();
	}

	protected abstract String getAICreatorOpenAIAPIKey()
		throws ConfigurationException;

	protected abstract String getAICreatorOpenAIImageModel()
		throws ConfigurationException;

	protected abstract String getAICreatorOpenAIImageSizes()
		throws ConfigurationException;

	protected abstract boolean isAICreatorChatGPTEnabled()
		throws ConfigurationException;

	protected abstract boolean isAICreatorOpenAIImageGenerationEnabled()
		throws ConfigurationException;

	protected final HttpServletRequest httpServletRequest;

}