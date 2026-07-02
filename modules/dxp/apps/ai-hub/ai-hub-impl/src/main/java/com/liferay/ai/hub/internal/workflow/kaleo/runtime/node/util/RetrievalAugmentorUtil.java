/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util;

import com.liferay.ai.hub.internal.langchain4j.rag.content.injector.DefaultContentInjector;
import com.liferay.ai.hub.internal.langchain4j.rag.content.retriever.ElasticsearchContentRetriever;
import com.liferay.ai.hub.internal.langchain4j.rag.content.retriever.LiferayWebSearchContentRetriever;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.highlight.FieldConfigBuilderFactory;
import com.liferay.portal.search.highlight.HighlightBuilderFactory;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;

import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 */
public class RetrievalAugmentorUtil {

	public static RetrievalAugmentor createRetrievalAugmentor(
		long companyId, DTOConverterRegistry dtoConverterRegistry,
		FieldConfigBuilderFactory fieldConfigBuilderFactory,
		HighlightBuilderFactory highlightBuilderFactory,
		Map<String, String> kaleoNodeSettingValues, Locale locale,
		ObjectEntryManager objectEntryManager,
		SearchEngineAdapter searchEngineAdapter, long userId,
		Map<String, Serializable> workflowContext, long workflowInstanceId) {

		List<ContentRetriever> contentRetrievers = new ArrayList<>();

		ContentRetriever contentRetriever =
			_createElasticsearchContentRetriever(
				companyId, dtoConverterRegistry, fieldConfigBuilderFactory,
				highlightBuilderFactory, locale, objectEntryManager,
				searchEngineAdapter, userId, workflowContext,
				workflowInstanceId);

		if (contentRetriever != null) {
			contentRetrievers.add(contentRetriever);
		}

		contentRetriever = _createLiferayWebSearchContentRetriever(
			companyId, kaleoNodeSettingValues, userId, workflowContext,
			workflowInstanceId);

		if (contentRetriever != null) {
			contentRetrievers.add(contentRetriever);
		}

		if (contentRetrievers.isEmpty()) {
			return null;
		}

		return DefaultRetrievalAugmentor.builder(
		).contentInjector(
			new DefaultContentInjector(List.of("url"))
		).queryRouter(
			new DefaultQueryRouter(contentRetrievers)
		).build();
	}

	private static ContentRetriever _createElasticsearchContentRetriever(
		long companyId, DTOConverterRegistry dtoConverterRegistry,
		FieldConfigBuilderFactory fieldConfigBuilderFactory,
		HighlightBuilderFactory highlightBuilderFactory, Locale locale,
		ObjectEntryManager objectEntryManager,
		SearchEngineAdapter searchEngineAdapter, long userId,
		Map<String, Serializable> workflowContext, long workflowInstanceId) {

		NestedFieldsContext nestedFieldsContext =
			NestedFieldsContextThreadLocal.getAndSetNestedFieldsContext(
				new NestedFieldsContext(
					1, List.of("agentDefinitionsToContentRetrievers")));

		try {
			String agentDefinitionExternalReferenceCode = GetterUtil.getString(
				workflowContext.get("agentDefinitionExternalReferenceCode"));

			if (Validator.isNull(agentDefinitionExternalReferenceCode)) {
				return null;
			}

			ObjectEntry agentDefinitionObjectEntry =
				objectEntryManager.getObjectEntry(
					companyId,
					new DefaultDTOConverterContext(
						false, Map.of(), dtoConverterRegistry, null, locale,
						null, UserLocalServiceUtil.getUserById(userId)),
					agentDefinitionExternalReferenceCode,
					ObjectDefinitionLocalServiceUtil.
						fetchObjectDefinitionByExternalReferenceCode(
							"L_AI_HUB_AGENT_DEFINITION", companyId),
					null);

			ObjectEntry[] contentRetrieversObjectEntries =
				(ObjectEntry[])agentDefinitionObjectEntry.getPropertyValue(
					"agentDefinitionsToContentRetrievers");

			if (ArrayUtil.isEmpty(contentRetrieversObjectEntries)) {
				return null;
			}

			return new ElasticsearchContentRetriever(
				fieldConfigBuilderFactory, highlightBuilderFactory,
				TransformUtil.transform(
					contentRetrieversObjectEntries,
					contentRetriever -> GetterUtil.getString(
						contentRetriever.getPropertyValue("indexName")),
					String.class),
				searchEngineAdapter, userId, workflowInstanceId);
		}
		catch (Exception exception) {
			_log.error(exception);

			return null;
		}
		finally {
			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				nestedFieldsContext);
		}
	}

	private static ContentRetriever _createLiferayWebSearchContentRetriever(
		long companyId, Map<String, String> kaleoNodeSettingValues, long userId,
		Map<String, Serializable> workflowContext, long workflowInstanceId) {

		if (kaleoNodeSettingValues.get("rag") == null) {
			return null;
		}

		try {
			JSONObject ragJSONObject = JSONFactoryUtil.createJSONObject(
				kaleoNodeSettingValues.get("rag"));

			JSONObject contentRetrieverJSONObject = ragJSONObject.getJSONObject(
				"contentRetriever");

			if (Objects.equals(
					contentRetrieverJSONObject.getString("key"), "liferay")) {

				Company company = CompanyLocalServiceUtil.getCompany(companyId);

				return new LiferayWebSearchContentRetriever(
					contentRetrieverJSONObject.getString(
						"blueprintExternalReferenceCode"),
					GetterUtil.getLong(
						workflowContext.get("oAuth2ApplicationId")),
					userId,
					EncryptorUtil.decrypt(
						company.getKeyObj(),
						(String)workflowContext.get("userToken")),
					workflowInstanceId);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RetrievalAugmentorUtil.class);

}