/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.text.embeddings.configuration;

import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderConfiguration;

/**
 * @author Petteri Karttunen
 */
public interface TextEmbeddingProvider {

	public Double[] getEmbedding(
		EmbeddingProviderConfiguration embeddingProviderConfiguration,
		String text);

	public String getProviderName();

}