/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.ml.embedding.text;

import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.configuration.SemanticSearchConfiguration;
import com.liferay.portal.search.configuration.SemanticSearchConfigurationProvider;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingDocumentContributor;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingRetriever;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = TextEmbeddingDocumentContributor.class)
public class TextEmbeddingDocumentContributorImpl
	implements TextEmbeddingDocumentContributor {

	@Override
	public <T extends BaseModel<T>> void contribute(
		Document document, String languageId, T model, String text) {

		if (Validator.isBlank(text)) {
			return;
		}

		EmbeddingProviderConfiguration embeddingProviderConfiguration =
			getEmbeddingProviderConfiguration(model);

		if (embeddingProviderConfiguration == null) {
			return;
		}

		List<String> languageIds = Arrays.asList(
			embeddingProviderConfiguration.getLanguageIds());

		if (!languageIds.contains(languageId)) {
			return;
		}

		Double[] textEmbedding = _textEmbeddingRetriever.getTextEmbedding(
			embeddingProviderConfiguration.getProviderName(), text);

		if (textEmbedding.length == 0) {
			return;
		}

		_addTextEmbeddingField(document, languageId, textEmbedding);
	}

	@Override
	public <T extends BaseModel<T>> void contribute(
		Document document, T model, String text) {

		if (Validator.isBlank(text)) {
			return;
		}

		EmbeddingProviderConfiguration embeddingProviderConfiguration =
			getEmbeddingProviderConfiguration(model);

		if (embeddingProviderConfiguration == null) {
			return;
		}

		Double[] textEmbedding = _textEmbeddingRetriever.getTextEmbedding(
			embeddingProviderConfiguration.getProviderName(), text);

		if (textEmbedding.length == 0) {
			return;
		}

		List<String> languageIds = Arrays.asList(
			embeddingProviderConfiguration.getLanguageIds());

		for (Locale locale :
				_language.getAvailableLocales(_getGroupId(model))) {

			String languageId = LocaleUtil.toLanguageId(locale);

			if (!languageIds.contains(languageId)) {
				continue;
			}

			_addTextEmbeddingField(document, languageId, textEmbedding);
		}
	}

	@Override
	public <T extends BaseModel<T>> List<String> getLanguageIds(T model) {
		EmbeddingProviderConfiguration embeddingProviderConfiguration =
			getEmbeddingProviderConfiguration(model);

		if (embeddingProviderConfiguration == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(embeddingProviderConfiguration.getLanguageIds());
	}

	protected <T extends BaseModel<T>> EmbeddingProviderConfiguration
		getEmbeddingProviderConfiguration(T model) {

		if (!_isSupportedSearchEngine() || !isIndexableStatus(model)) {
			return null;
		}

		long companyId = _getCompanyId(model);

		if (companyId == 0) {
			return null;
		}

		SemanticSearchConfiguration semanticSearchConfiguration =
			semanticSearchConfigurationProvider.getCompanyConfiguration(
				companyId);

		if (!semanticSearchConfiguration.textEmbeddingsEnabled()) {
			return null;
		}

		Class<?> clazz = model.getModelClass();

		String modelClassName = clazz.getName();

		if (model instanceof ObjectEntry) {
			ObjectEntry objectEntry = (ObjectEntry)model;

			modelClassName = objectEntry.getModelClassName();
		}

		try {
			for (String textEmbeddingProviderConfigurationJSON :
					semanticSearchConfiguration.
						textEmbeddingProviderConfigurationJSONs()) {

				EmbeddingProviderConfiguration embeddingProviderConfiguration =
					EmbeddingProviderConfiguration.unsafeToDTO(
						textEmbeddingProviderConfigurationJSON);

				if (!ArrayUtil.contains(
						embeddingProviderConfiguration.getModelClassNames(),
						modelClassName)) {

					continue;
				}

				if (ArrayUtil.isNotEmpty(
						embeddingProviderConfiguration.getLanguageIds())) {

					return embeddingProviderConfiguration;
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	protected String getTextEmbeddingFieldName(
		int dimensions, String languageId) {

		return StringBundler.concat(
			"text_embedding_", dimensions, StringPool.UNDERLINE, languageId);
	}

	protected <T extends BaseModel<T>> boolean isIndexableStatus(T model) {
		if (model instanceof WorkflowedModel) {
			WorkflowedModel workflowedModel = (WorkflowedModel)model;

			if (workflowedModel.getStatus() ==
					WorkflowConstants.STATUS_APPROVED) {

				return true;
			}

			return false;
		}

		return true;
	}

	@Reference
	protected SemanticSearchConfigurationProvider
		semanticSearchConfigurationProvider;

	private void _addTextEmbeddingField(
		Document document, String languageId, Double[] textEmbedding) {

		Field field = new Field(
			getTextEmbeddingFieldName(textEmbedding.length, languageId));

		field.setNumeric(true);
		field.setNumericClass(Double.class);
		field.setTokenized(false);
		field.setValues(ArrayUtil.toStringArray(textEmbedding));

		document.add(field);
	}

	private <T extends BaseModel<T>> long _getCompanyId(T model) {
		if (model instanceof AuditedModel) {
			AuditedModel companyModel = (AuditedModel)model;

			return companyModel.getCompanyId();
		}
		else if (model instanceof ShardedModel) {
			ShardedModel shardedModel = (ShardedModel)model;

			return shardedModel.getCompanyId();
		}
		else if (model instanceof StagedModel) {
			StagedModel stagedModel = (StagedModel)model;

			return stagedModel.getCompanyId();
		}

		return CompanyThreadLocal.getCompanyId();
	}

	private <T extends BaseModel<T>> long _getGroupId(T model) {
		if (model instanceof GroupedModel) {
			GroupedModel groupedModel = (GroupedModel)model;

			return groupedModel.getGroupId();
		}

		return 0;
	}

	private boolean _isSupportedSearchEngine() {
		return !Objects.equals(
			_searchEngineInformation.getVendorString(), "Solr");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TextEmbeddingDocumentContributorImpl.class);

	@Reference
	private Language _language;

	@Reference
	private SearchEngineInformation _searchEngineInformation;

	@Reference
	private TextEmbeddingRetriever _textEmbeddingRetriever;

}