/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.client;

import java.util.Locale;

/**
 * @author Lourdes Fernández Besada
 * @author Roberto Díaz
 */
public interface AICreatorOpenAIClient {

	public String getCompletion(
			String apiKey, String content, Locale locale, String tone,
			int words)
		throws Exception;

	public String[] getGenerations(
			String apiKey, String model, int numberOfImages, String prompt,
			String size)
		throws Exception;

	public void validateAPIKey(String apiKey, String model) throws Exception;

}