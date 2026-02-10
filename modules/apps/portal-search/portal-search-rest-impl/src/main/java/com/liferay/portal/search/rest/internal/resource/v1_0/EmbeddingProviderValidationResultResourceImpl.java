/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.ml.embedding.EmbeddingProviderStatus;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingRetriever;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderConfiguration;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderValidationResult;
import com.liferay.portal.search.rest.resource.v1_0.EmbeddingProviderValidationResultResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Petteri Karttunen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/embedding-provider-validation-result.properties",
	scope = ServiceScope.PROTOTYPE,
	service = EmbeddingProviderValidationResultResource.class
)
public class EmbeddingProviderValidationResultResourceImpl
	extends BaseEmbeddingProviderValidationResultResourceImpl {

	@Override
	public EmbeddingProviderValidationResult
		postEmbeddingValidateProviderConfiguration(
			EmbeddingProviderConfiguration embeddingProviderConfiguration) {

		try {
			EmbeddingProviderStatus embeddingProviderStatus =
				_textEmbeddingRetriever.getEmbeddingProviderStatus(
					embeddingProviderConfiguration.toString());

			return new EmbeddingProviderValidationResult() {
				{
					setErrorMessage(
						() -> {
							if (Validator.isBlank(
									embeddingProviderStatus.
										getErrorMessage())) {

								return null;
							}

							return embeddingProviderStatus.getErrorMessage();
						});
					setExpectedDimensions(
						() -> {
							if (!Validator.isBlank(
									embeddingProviderStatus.
										getErrorMessage())) {

								return null;
							}

							return embeddingProviderStatus.
								getEmbeddingVectorDimensions();
						});
				}
			};
		}
		catch (Exception exception) {
			return new EmbeddingProviderValidationResult() {
				{
					setErrorMessage(exception::getMessage);
				}
			};
		}
	}

	@Reference
	private TextEmbeddingRetriever _textEmbeddingRetriever;

}