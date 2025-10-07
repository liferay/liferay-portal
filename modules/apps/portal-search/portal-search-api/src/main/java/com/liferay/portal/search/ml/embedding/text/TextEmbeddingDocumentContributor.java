/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.ml.embedding.text;

import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Document;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Petteri Karttunen
 */
@ProviderType
public interface TextEmbeddingDocumentContributor {

	public <T extends BaseModel<T>> void contribute(
		Document document, String languageId, T model, String text);

	public <T extends BaseModel<T>> void contribute(
		Document document, T model, String text);

	public <T extends BaseModel<T>> List<String> getLanguageIds(T model);

}