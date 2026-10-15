/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.exception.PIMConnectorException;
import com.liferay.site.pim.site.initializer.internal.link.VariantPIMLinkType;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorFieldMappingsUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
public abstract class BasePIMConnector implements PIMConnector {

	@Override
	public String getName(Locale locale) {
		return language.get(locale, getKey());
	}

	protected Map<String, List<ObjectEntry>> getPIMFieldMappingObjectEntriesMap(
			ObjectEntry pimConnectorObjectEntry)
		throws Exception {

		Map<String, List<ObjectEntry>> objectEntriesMap = new HashMap<>();

		for (ObjectEntry objectEntry :
				PIMConnectorFieldMappingsUtil.getObjectEntries(
					pimConnectorObjectEntry)) {

			List<ObjectEntry> objectEntries = objectEntriesMap.computeIfAbsent(
				MapUtil.getString(objectEntry.getValues(), "channelFieldName"),
				key -> new ArrayList<>());

			objectEntries.add(objectEntry);
		}

		_validate(objectEntriesMap);

		return objectEntriesMap;
	}

	protected Map<String, List<ObjectEntry>> getPIMProductObjectEntriesMap(
			long companyId)
		throws Exception {

		Map<String, List<ObjectEntry>> objectEntriesMap = new LinkedHashMap<>();

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK,
					companyId);
		Map<Long, Map<String, Map<String, Serializable>>> valuesMaps =
			new HashMap<>();

		for (ObjectEntry objectEntry : _getPIMProductObjectEntries(companyId)) {
			long groupId = objectEntry.getGroupId();

			Map<String, Map<String, Serializable>> valuesMap = valuesMaps.get(
				groupId);

			if (valuesMap == null) {
				valuesMap = _getVariantPIMLinkValuesMap(
					companyId, groupId, objectDefinition);

				valuesMaps.put(groupId, valuesMap);
			}

			String externalReferenceCode =
				objectEntry.getExternalReferenceCode();

			Map<String, Serializable> values = valuesMap.getOrDefault(
				externalReferenceCode, Collections.emptyMap());

			List<ObjectEntry> objectEntries = objectEntriesMap.computeIfAbsent(
				MapUtil.getString(values, "clusterKey", externalReferenceCode),
				key -> new ArrayList<>());

			if (MapUtil.getBoolean(values, "primary")) {
				objectEntries.add(0, objectEntry);
			}
			else {
				objectEntries.add(objectEntry);
			}
		}

		return objectEntriesMap;
	}

	@Reference
	protected ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	protected FilterFactory<Predicate> filterFactory;

	@Reference
	protected Language language;

	@Reference
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Reference
	protected ObjectEntryLocalService objectEntryLocalService;

	@Reference
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Reference
	protected Searcher searcher;

	@Reference
	protected Sorts sorts;

	private List<ObjectEntry> _getPIMProductObjectEntries(long companyId) {
		List<ObjectEntry> objectEntries = new ArrayList<>();

		long entryClassPK = 0;

		while (true) {
			List<Document> documents = _search(companyId, entryClassPK);

			if (documents.isEmpty()) {
				break;
			}

			objectEntries.addAll(
				TransformUtil.transform(
					documents,
					document -> objectEntryLocalService.fetchObjectEntry(
						GetterUtil.getLong(
							document.getLong(Field.ENTRY_CLASS_PK)))));

			if (documents.size() < _SEARCH_SIZE) {
				break;
			}

			Document document = documents.get(documents.size() - 1);

			entryClassPK = GetterUtil.getLong(
				document.getLong(Field.ENTRY_CLASS_PK));
		}

		return objectEntries;
	}

	private Map<String, Map<String, Serializable>> _getVariantPIMLinkValuesMap(
			long companyId, long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		Map<String, Map<String, Serializable>> valuesMap = new HashMap<>();

		if (objectDefinition == null) {
			return valuesMap;
		}

		for (Map<String, Serializable> values :
				objectEntryLocalService.getValuesList(
					groupId, companyId, 0,
					objectDefinition.getObjectDefinitionId(),
					filterFactory.create(
						StringBundler.concat(
							"type eq '", VariantPIMLinkType.TYPE, "'"),
						objectDefinition),
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			if (Validator.isNull(MapUtil.getString(values, "clusterKey"))) {
				continue;
			}

			valuesMap.put(
				MapUtil.getString(values, "sourceClassExternalReferenceCode"),
				values);
		}

		return valuesMap;
	}

	private List<Document> _search(long companyId, long entryClassPK) {
		SearchResponse searchResponse = searcher.search(
			searchRequestBuilderFactory.builder(
			).addComplexQueryPart(
				complexQueryPartBuilderFactory.builder(
				).occur(
					"filter"
				).query(
					QueriesUtil.term("cms_section", "products")
				).build()
			).addComplexQueryPart(
				complexQueryPartBuilderFactory.builder(
				).occur(
					"filter"
				).query(
					QueriesUtil.rangeTerm(
						Field.ENTRY_CLASS_PK, false, true, entryClassPK,
						Long.MAX_VALUE)
				).build()
			).addComplexQueryPart(
				complexQueryPartBuilderFactory.builder(
				).occur(
					"filter"
				).query(
					QueriesUtil.term(
						Field.STATUS, WorkflowConstants.STATUS_APPROVED)
				).build()
			).companyId(
				companyId
			).emptySearchEnabled(
				true
			).size(
				_SEARCH_SIZE
			).sorts(
				sorts.field(Field.ENTRY_CLASS_PK, SortOrder.ASC)
			).build());

		return searchResponse.getDocuments();
	}

	private void _validate(Map<String, List<ObjectEntry>> objectEntriesMap)
		throws Exception {

		for (PIMConnectorChannelField pimConnectorChannelField :
				getPIMConnectorChannelFields()) {

			if (!pimConnectorChannelField.isRequired()) {
				continue;
			}

			List<ObjectEntry> objectEntries = ListUtil.filter(
				objectEntriesMap.getOrDefault(
					pimConnectorChannelField.getName(),
					Collections.emptyList()),
				objectEntry -> {
					Map<String, Serializable> values = objectEntry.getValues();

					return Validator.isNotNull(
						MapUtil.getString(values, "sourceFieldName")) ||
						   Validator.isNotNull(
							   MapUtil.getString(values, "value"));
				});

			if (objectEntries.isEmpty()) {
				throw new PIMConnectorException(
					"a-required-channel-field-is-not-mapped");
			}
		}
	}

	private static final int _SEARCH_SIZE = 1000;

}