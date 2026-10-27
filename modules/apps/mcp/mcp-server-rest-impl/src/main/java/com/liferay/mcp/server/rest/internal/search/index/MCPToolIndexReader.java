/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index;

import com.ibm.icu.text.RuleBasedNumberFormat;

import com.liferay.mcp.server.rest.dto.v1_0.Prerequisite;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSearchResult;
import com.liferay.mcp.server.rest.internal.search.index.constants.MCPToolConstants;
import com.liferay.mcp.server.rest.internal.search.index.util.IntentUtil;
import com.liferay.mcp.server.rest.internal.search.index.util.WordUtil;
import com.liferay.mcp.server.rest.internal.util.OpenAPIUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.aggregation.metrics.TopHitsAggregation;
import com.liferay.portal.search.aggregation.metrics.TopHitsAggregationResult;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.FunctionScoreQuery;
import com.liferay.portal.search.query.MatchQuery;
import com.liferay.portal.search.query.MultiMatchQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.TermQuery;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.query.function.CombineFunction;
import com.liferay.portal.search.query.function.score.ScoreFunctions;
import com.liferay.portal.search.script.Scripts;
import com.liferay.portal.search.sort.ScoreSort;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;

import java.text.ParsePosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = MCPToolIndexReader.class)
public class MCPToolIndexReader {

	public List<ToolSearchResult> search(
		Set<String> allowedToolKeys, long companyId,
		boolean includePrerequisites, String search, int size) {

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(
				_getSearchSearchRequest(
					allowedToolKeys, companyId, search, size));

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		return _toToolSearchResults(
			allowedToolKeys, companyId, includePrerequisites, searchHitsList);
	}

	private Query _addAllowedToolKeys(
		Set<String> allowedToolKeys, Query query) {

		if (allowedToolKeys == null) {
			return query;
		}

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		TermsQuery termsQuery = QueriesUtil.terms(MCPToolFields.UID);

		termsQuery.addValues(allowedToolKeys.toArray());

		booleanQuery.addFilterQueryClauses(termsQuery);

		booleanQuery.addMustQueryClauses(query);

		return booleanQuery;
	}

	private void _demote(
		Query filterQuery, FunctionScoreQuery functionScoreQuery,
		float weight) {

		functionScoreQuery.addFilterQueryScoreFunctionHolder(
			filterQuery,
			_scoreFunctions.script(
				Scripts.INSTANCE.inline("painless", String.valueOf(weight))));
	}

	private void _demoteOperationVariants(
		FunctionScoreQuery functionScoreQuery, Set<String> intents,
		float weight, String... operationVariants) {

		TermsQuery termsQuery = QueriesUtil.terms(
			MCPToolFields.OPERATION_VARIANT);

		for (String operationVariant : operationVariants) {
			if (_isRequestedOperationVariant(intents, operationVariant)) {
				continue;
			}

			termsQuery.addValue(operationVariant);
		}

		if (termsQuery.isEmpty()) {
			return;
		}

		_demote(termsQuery, functionScoreQuery, weight);
	}

	private Query _getBaseQuery(
		boolean batch, String entityPhrase, String search) {

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addShouldQueryClauses(
			_getMultiMatchQuery(search),
			QueriesUtil.matchPhrase(
				MCPToolFields.EXPANSION + ".phrase", search));

		if (batch) {
			TermQuery termQuery = QueriesUtil.term(
				MCPToolFields.OPERATION_VARIANT,
				MCPToolConstants.OPERATION_VARIANT_BATCH);

			termQuery.setBoost(3.0F);

			booleanQuery.addShouldQueryClauses(termQuery);
		}

		if (entityPhrase == null) {
			return booleanQuery;
		}

		MatchQuery matchQuery = QueriesUtil.match(
			MCPToolFields.ENTITY_NAME + ".split", entityPhrase);

		matchQuery.setBoost(2.0F);

		return booleanQuery.addShouldQueryClauses(matchQuery);
	}

	private Map<String, Document> _getCollectionToolDocuments(
		Set<String> allowedToolKeys, Set<String> collections, long companyId) {

		if (collections.isEmpty()) {
			return Collections.emptyMap();
		}

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(
				_getCollectionToolsSearchSearchRequest(
					allowedToolKeys, collections, companyId));

		Map<String, AggregationResult> aggregationResultsMap =
			searchSearchResponse.getAggregationResultsMap();

		return _toCollectionToolDocuments(
			(TermsAggregationResult)aggregationResultsMap.get(
				MCPToolFields.COLLECTION));
	}

	private SearchSearchRequest _getCollectionToolsSearchSearchRequest(
		Set<String> allowedToolKeys, Set<String> collections, long companyId) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		TermsAggregation termsAggregation = _aggregations.terms(
			MCPToolFields.COLLECTION, MCPToolFields.COLLECTION);

		TopHitsAggregation topHitsAggregation = _aggregations.topHits(
			_TOP_HITS_AGGREGATION_NAME);

		topHitsAggregation.addSortFields(
			_sorts.field(MCPToolFields.DEPRECATED, SortOrder.ASC),
			_sorts.field(MCPToolFields.PATH_PARAMETER_COUNT, SortOrder.ASC),
			_sorts.field(MCPToolFields.PATH_PART_COUNT, SortOrder.ASC),
			_sorts.field(MCPToolFields.TOOL_SET_SIZE, SortOrder.DESC),
			_sorts.field(MCPToolFields.PATH_LENGTH, SortOrder.ASC),
			_sorts.field(MCPToolFields.UID, SortOrder.ASC));
		topHitsAggregation.setFetchSource(true);
		topHitsAggregation.setSize(1);

		termsAggregation.addChildAggregation(topHitsAggregation);

		termsAggregation.setSize(collections.size());

		TermsQuery termsQuery = QueriesUtil.terms(MCPToolFields.COLLECTION);

		termsQuery.addValues(collections.toArray());

		searchSearchRequest.addAggregation(termsAggregation);

		searchSearchRequest.setIndexNames(
			_mcpToolIndexCreator.getIndexName(companyId));
		searchSearchRequest.setQuery(
			_addAllowedToolKeys(allowedToolKeys, termsQuery));
		searchSearchRequest.setSize(0);

		return searchSearchRequest;
	}

	private String _getDescription(Document document) {
		String description = document.getString(MCPToolFields.DESCRIPTION);

		String toolName = document.getString(MCPToolFields.TOOL_NAME);

		if (Validator.isBlank(description)) {
			description = WordUtil.toPhrase(toolName);
		}

		String operationHint;

		if (Objects.equals(
				document.getString(MCPToolFields.OPERATION_VARIANT),
				MCPToolConstants.OPERATION_VARIANT_BATCH)) {

			operationHint = StringBundler.concat(
				"Takes many entities in one call, so use it rather than ",
				"calling the single entity operation repeatedly. It answers ",
				"202 with an import task, not a result: nothing is written ",
				"yet and 202 does not mean success. Before reporting the work ",
				"as done, read executeStatus and failedItems by invoking ",
				"getImportTask in headless-batch-engine-v1.0, passing the ID ",
				"it returned.");
		}
		else {
			operationHint = _operationHints.get(toolName);
		}

		if (operationHint == null) {
			return description;
		}

		return description + StringPool.SPACE + operationHint;
	}

	private String _getEntityPhrase(boolean association, String[] words) {
		if (association) {
			return null;
		}

		String entityPhrase = null;

		List<String> entityWords = new ArrayList<>();

		for (String word : words) {
			if (_boundaryWords.contains(word)) {
				break;
			}

			if (word.equals("of") && !entityWords.isEmpty()) {
				entityPhrase = StringUtil.merge(entityWords, StringPool.SPACE);

				entityWords.clear();
			}
			else if (!_ignoredWords.contains(word) &&
					 !IntentUtil.isVerb(word)) {

				entityWords.add(word);
			}
		}

		if (!entityWords.isEmpty()) {
			entityPhrase = StringUtil.merge(entityWords, StringPool.SPACE);
		}

		return entityPhrase;
	}

	private MultiMatchQuery _getMultiMatchQuery(String search) {
		MultiMatchQuery multiMatchQuery = QueriesUtil.multiMatch(
			search,
			LinkedHashMapBuilder.put(
				MCPToolFields.DESCRIPTION, 1.0F
			).put(
				MCPToolFields.ENTITY_NAME + ".split", 4.0F
			).put(
				MCPToolFields.EXPANSION, 1.5F
			).put(
				MCPToolFields.PARAMETER_NAMES, 0.25F
			).put(
				MCPToolFields.PATH, 2.0F
			).put(
				MCPToolFields.SCHEMA_PROPERTY_NAMES, 0.75F
			).put(
				MCPToolFields.TOOL_NAME, 5.0F
			).put(
				MCPToolFields.TOOL_NAME + ".split", 2.0F
			).build());

		multiMatchQuery.setType(MultiMatchQuery.Type.CROSS_FIELDS);

		return multiMatchQuery;
	}

	private String _getObjectPhrase(Set<String> intents) {
		if (intents.contains(IntentUtil.INTENT_CREATE)) {
			return _OBJECT_DEFINITION_PHRASE;
		}

		if (intents.contains(IntentUtil.INTENT_LIST) ||
			intents.contains(IntentUtil.INTENT_READ)) {

			return _OBJECT_ENTRY_PHRASE;
		}

		return _OBJECT_DEFINITION_PHRASE + StringPool.SPACE +
			_OBJECT_ENTRY_PHRASE;
	}

	private Query _getOperationVariantQuery(String... operationVariants) {
		TermsQuery termsQuery = QueriesUtil.terms(
			MCPToolFields.OPERATION_VARIANT);

		termsQuery.addValues((Object[])operationVariants);

		return termsQuery;
	}

	private Query _getOtherIntentsQuery(String verb) {
		TermsQuery termsQuery = QueriesUtil.terms(MCPToolFields.INTENT);

		Set<String> otherIntents = IntentUtil.getOtherIntents(verb);

		termsQuery.addValues(otherIntents.toArray());

		return termsQuery;
	}

	private Map<String, String> _getParameterCollections(Document document) {
		Map<String, String> parameterCollections = new LinkedHashMap<>();

		String[] pathParts = StringUtil.split(
			document.getString(MCPToolFields.PATH), CharPool.SLASH);

		for (int i = 1; i < (pathParts.length - 1); i++) {
			String pathParameter = OpenAPIUtil.getPathParameter(pathParts[i]);

			if (pathParameter == null) {
				continue;
			}

			String pathPart = pathParts[i - 1];

			if ((i >= 2) && StringUtil.startsWith(pathPart, "by-")) {
				pathPart = pathParts[i - 2];
			}

			parameterCollections.put(
				pathParameter, WordUtil.normalize(pathPart));
		}

		for (String requiredPropertyCollection :
				document.getStrings(
					MCPToolFields.REQUIRED_PROPERTY_COLLECTIONS)) {

			String[] parts = StringUtil.split(
				requiredPropertyCollection, CharPool.POUND);

			if (parts.length != 2) {
				continue;
			}

			parameterCollections.put(parts[0], parts[1]);
		}

		return parameterCollections;
	}

	private String _getParameterNote(String parameter) {
		String note = _parameterHints.get(parameter);

		if (note != null) {
			return note;
		}

		if (StringUtil.endsWith(parameter, "ExternalReferenceCode")) {
			return "A generated code rather than a name. Read it from the " +
				"externalReferenceCode field of the listing named here.";
		}

		return null;
	}

	private Prerequisite[] _getPrerequisites(
		Set<String> allowedToolKeys, long companyId, Document document) {

		Map<String, String> parameterCollections = _getParameterCollections(
			document);

		List<Prerequisite> prerequisites = _toPrerequisites(
			_getCollectionToolDocuments(
				allowedToolKeys, new HashSet<>(parameterCollections.values()),
				companyId),
			parameterCollections);

		if (prerequisites.isEmpty()) {
			return null;
		}

		return prerequisites.toArray(new Prerequisite[0]);
	}

	private Query _getQuery(String search) {
		String[] words = WordUtil.toWords(search);

		String verb = IntentUtil.getVerb(words);

		Set<String> intents = IntentUtil.getIntents(verb);

		boolean association = _hasAssociationWords(words);
		boolean batch = _isBatch(intents, verb, words);

		FunctionScoreQuery functionScoreQuery = QueriesUtil.functionScore(
			_getBaseQuery(
				batch, _getEntityPhrase(association, words),
				_replaceEntityWords(intents, search)));

		functionScoreQuery.setCombineFunction(CombineFunction.MULTIPLY);
		functionScoreQuery.setScoreMode(FunctionScoreQuery.ScoreMode.MULTIPLY);

		_demote(
			QueriesUtil.term(MCPToolFields.DEPRECATED, true),
			functionScoreQuery, 0.8F);
		_demoteOperationVariants(
			functionScoreQuery, intents, 0.3F,
			MCPToolConstants.OPERATION_VARIANT_EXPORT);
		_demoteOperationVariants(
			functionScoreQuery, intents, 0.3F,
			_OPERATION_VARIANTS_RARELY_WANTED);
		_demoteOperationVariants(
			functionScoreQuery, intents, 0.8F,
			_OPERATION_VARIANTS_SECONDARY_ACTION);

		if (association) {
			BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

			booleanQuery.addMustQueryClauses(
				_getOperationVariantQuery(
					MCPToolConstants.OPERATION_VARIANT_TRAVERSAL),
				QueriesUtil.term(MCPToolFields.METHOD, "get"));

			_demote(booleanQuery, functionScoreQuery, 0.7F);
		}

		if (!batch) {
			_demote(
				_getOperationVariantQuery(
					MCPToolConstants.OPERATION_VARIANT_BATCH),
				functionScoreQuery, 0.3F);
		}

		if (!intents.isEmpty()) {
			_demote(_getOtherIntentsQuery(verb), functionScoreQuery, 0.5F);
		}

		return functionScoreQuery;
	}

	private SearchSearchRequest _getSearchSearchRequest(
		Set<String> allowedToolKeys, long companyId, String search, int size) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(
			_mcpToolIndexCreator.getIndexName(companyId));
		searchSearchRequest.setQuery(
			_addAllowedToolKeys(allowedToolKeys, _getQuery(search)));
		searchSearchRequest.setSize(size);

		_setSorts(searchSearchRequest);

		return searchSearchRequest;
	}

	private boolean _hasAssociationWords(String[] words) {
		for (String word : words) {
			if (_associationWords.contains(word)) {
				return true;
			}

			String preposition = _associationVerbPrepositions.get(word);

			if ((preposition != null) &&
				ArrayUtil.contains(words, preposition)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isBatch(Set<String> intents, String verb, String[] words) {
		if (intents.contains(IntentUtil.INTENT_LIST) ||
			intents.contains(IntentUtil.INTENT_READ)) {

			return false;
		}

		if (_batchVerbs.contains(verb)) {
			return true;
		}

		for (int i = 0; i < words.length; i++) {
			String word = words[i];

			if (_batchWords.contains(word) ||
				(_isQuantityExpression(i, words) &&
				 (_parseQuantity(word) >= 10))) {

				return true;
			}
		}

		return false;
	}

	private boolean _isQuantityExpression(int index, String[] words) {
		if ((index > 0) && !IntentUtil.isVerb(words[index - 1])) {
			return false;
		}

		for (int i = index + 1; i < words.length; i++) {
			String word = words[i];

			if (_boundaryWords.contains(word)) {
				return false;
			}

			if (WordUtil.isPlural(word)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isRequestedOperationVariant(
		Set<String> intents, String operationVariant) {

		Set<String> operationVariantIntents = _operationVariantIntents.get(
			operationVariant);

		if (operationVariantIntents == null) {
			return false;
		}

		return !Collections.disjoint(intents, operationVariantIntents);
	}

	private int _parseQuantity(String word) {
		ParsePosition parsePosition = new ParsePosition(0);

		Number number;

		synchronized (_ruleBasedNumberFormat) {
			number = _ruleBasedNumberFormat.parse(word, parsePosition);
		}

		if (parsePosition.getIndex() < word.length()) {
			return 0;
		}

		return number.intValue();
	}

	private String _replaceEntityWords(Set<String> intents, String search) {
		Matcher matcher = _entityWordsPattern.matcher(search);

		return matcher.replaceAll(_getObjectPhrase(intents));
	}

	private void _setSorts(SearchSearchRequest searchSearchRequest) {
		ScoreSort scoreSort = _sorts.score();

		scoreSort.setSortOrder(SortOrder.DESC);

		searchSearchRequest.addSorts(
			scoreSort,
			_sorts.field(MCPToolFields.PATH_PART_COUNT, SortOrder.ASC),
			_sorts.field(MCPToolFields.UID, SortOrder.ASC));
	}

	private Map<String, Document> _toCollectionToolDocuments(
		TermsAggregationResult termsAggregationResult) {

		Map<String, Document> collectionToolDocuments = new HashMap<>();

		for (Bucket bucket : termsAggregationResult.getBuckets()) {
			TopHitsAggregationResult topHitsAggregationResult =
				(TopHitsAggregationResult)bucket.getChildAggregationResult(
					_TOP_HITS_AGGREGATION_NAME);

			SearchHits searchHits = topHitsAggregationResult.getSearchHits();

			List<SearchHit> searchHitsList = searchHits.getSearchHits();

			if (searchHitsList.isEmpty()) {
				continue;
			}

			SearchHit searchHit = searchHitsList.get(0);

			collectionToolDocuments.put(
				bucket.getKey(), searchHit.getDocument());
		}

		return collectionToolDocuments;
	}

	private List<Prerequisite> _toPrerequisites(
		Map<String, Document> collectionToolDocuments,
		Map<String, String> parameterCollections) {

		List<Prerequisite> prerequisites = new ArrayList<>();

		for (Map.Entry<String, String> entry :
				parameterCollections.entrySet()) {

			String parameter = entry.getKey();

			Document collectionToolDocument = collectionToolDocuments.get(
				entry.getValue());

			String note = _getParameterNote(parameter);

			if ((collectionToolDocument == null) && (note == null)) {
				continue;
			}

			Prerequisite prerequisite = new Prerequisite();

			prerequisite.setNote(() -> note);
			prerequisite.setParameter(() -> parameter);

			if (collectionToolDocument != null) {
				prerequisite.setToolName(
					() -> collectionToolDocument.getString(
						MCPToolFields.TOOL_NAME));
				prerequisite.setToolSetName(
					() -> collectionToolDocument.getString(
						MCPToolFields.TOOL_SET_NAME));
			}

			prerequisites.add(prerequisite);
		}

		return prerequisites;
	}

	private ToolSearchResult _toToolSearchResult(Document document) {
		return new ToolSearchResult() {
			{
				setDescription(() -> _getDescription(document));
				setToolName(() -> document.getString(MCPToolFields.TOOL_NAME));
				setToolSetName(
					() -> document.getString(MCPToolFields.TOOL_SET_NAME));
			}
		};
	}

	private List<ToolSearchResult> _toToolSearchResults(
		Set<String> allowedToolKeys, long companyId,
		boolean includePrerequisites, List<SearchHit> searchHitsList) {

		List<ToolSearchResult> toolSearchResults = TransformUtil.transform(
			searchHitsList,
			searchHit -> _toToolSearchResult(searchHit.getDocument()));

		if (!includePrerequisites || toolSearchResults.isEmpty()) {
			return toolSearchResults;
		}

		SearchHit searchHit = searchHitsList.get(0);

		Document document = searchHit.getDocument();

		ToolSearchResult toolSearchResult = toolSearchResults.get(0);

		toolSearchResult.setPrerequisites(
			() -> _getPrerequisites(allowedToolKeys, companyId, document));

		return toolSearchResults;
	}

	private static final String _OBJECT_DEFINITION_PHRASE = "object definition";

	private static final String _OBJECT_ENTRY_PHRASE = "object entry";

	private static final String[] _OPERATION_VARIANTS_RARELY_WANTED = {
		MCPToolConstants.OPERATION_VARIANT_HISTORY,
		MCPToolConstants.OPERATION_VARIANT_KEYED,
		MCPToolConstants.OPERATION_VARIANT_NESTED,
		MCPToolConstants.OPERATION_VARIANT_OPENAPI,
		MCPToolConstants.OPERATION_VARIANT_PERMISSIONS,
		MCPToolConstants.OPERATION_VARIANT_PREVIEW,
		MCPToolConstants.OPERATION_VARIANT_RATING,
		MCPToolConstants.OPERATION_VARIANT_SUBSCRIPTION
	};

	private static final String[] _OPERATION_VARIANTS_SECONDARY_ACTION = {
		MCPToolConstants.OPERATION_VARIANT_APPROVED,
		MCPToolConstants.OPERATION_VARIANT_COPY,
		MCPToolConstants.OPERATION_VARIANT_EXPIRE,
		MCPToolConstants.OPERATION_VARIANT_MOVE,
		MCPToolConstants.OPERATION_VARIANT_RESTORE,
		MCPToolConstants.OPERATION_VARIANT_TRANSLATION,
		MCPToolConstants.OPERATION_VARIANT_VALIDATE
	};

	private static final String _TOP_HITS_AGGREGATION_NAME = "topHits";

	private static final Map<String, String> _associationVerbPrepositions =
		HashMapBuilder.put(
			"add", "to"
		).put(
			"added", "to"
		).put(
			"adds", "to"
		).put(
			"put", "to"
		).put(
			"remove", "from"
		).put(
			"removed", "from"
		).put(
			"removes", "from"
		).build();
	private static final Set<String> _associationWords = SetUtil.fromArray(
		"assign", "assigned", "assigning", "associate", "associated",
		"association", "attach", "attached", "detach", "disassociate", "link",
		"linked", "relate", "related", "unassign", "unlink");
	private static final Set<String> _batchVerbs = SetUtil.fromArray("import");
	private static final Set<String> _batchWords = SetUtil.fromArray(
		"batch", "batches", "bulk", "many");
	private static final Set<String> _boundaryWords = SetUtil.fromArray(
		"and", "as", "at", "belonging", "by", "for", "from", "in", "inside",
		"into", "on", "onto", "that", "to", "under", "using", "via", "whose",
		"with", "within");
	private static final Pattern _entityWordsPattern = Pattern.compile(
		"\\b(entities|entity)\\b", Pattern.CASE_INSENSITIVE);
	private static final Set<String> _ignoredWords = SetUtil.fromArray(
		"a", "an", "data", "detail", "details", "entities", "entity", "entries",
		"entry", "info", "information", "item", "items", "object", "objects",
		"of", "record", "records", "row", "rows", "the", "value", "values");
	private static final Map<String, String> _operationHints =
		HashMapBuilder.put(
			"postObjectDefinition",
			StringBundler.concat(
				"Once published, this object definition exposes its entries ",
				"as a tool set named after the definition. To create an ",
				"entry, search for the definition's name, as in \"create a ",
				"pet store\", not \"create an object entry\".")
		).put(
			"postObjectDefinitionPublish",
			StringBundler.concat(
				"Publishing this object definition registers a tool set for ",
				"its entries, named after the definition. To create an entry, ",
				"search for the definition's name, as in \"create a pet ",
				"store\", not \"create an object entry\".")
		).build();
	private static final Map<String, Set<String>> _operationVariantIntents =
		HashMapBuilder.<String, Set<String>>put(
			MCPToolConstants.OPERATION_VARIANT_COPY,
			SetUtil.fromArray(IntentUtil.INTENT_COPY)
		).put(
			MCPToolConstants.OPERATION_VARIANT_EXPIRE,
			SetUtil.fromArray(IntentUtil.INTENT_UNPUBLISH)
		).put(
			MCPToolConstants.OPERATION_VARIANT_EXPORT,
			SetUtil.fromArray(IntentUtil.INTENT_EXPORT)
		).put(
			MCPToolConstants.OPERATION_VARIANT_MOVE,
			SetUtil.fromArray(IntentUtil.INTENT_MOVE)
		).put(
			MCPToolConstants.OPERATION_VARIANT_RESTORE,
			SetUtil.fromArray(IntentUtil.INTENT_RESTORE)
		).put(
			MCPToolConstants.OPERATION_VARIANT_SUBSCRIPTION,
			SetUtil.fromArray(
				IntentUtil.INTENT_SUBSCRIBE, IntentUtil.INTENT_UNSUBSCRIBE)
		).put(
			MCPToolConstants.OPERATION_VARIANT_TRANSLATION,
			SetUtil.fromArray(IntentUtil.INTENT_TRANSLATE)
		).put(
			MCPToolConstants.OPERATION_VARIANT_VALIDATE,
			SetUtil.fromArray(IntentUtil.INTENT_VALIDATE)
		).build();
	private static final Map<String, String> _parameterHints =
		HashMapBuilder.put(
			"assetLibraryId",
			StringBundler.concat(
				"The asset library's key, its numeric ID or its external ",
				"reference code. The key is case sensitive.")
		).put(
			"siteId",
			StringBundler.concat(
				"The site's key, its numeric ID or its external reference ",
				"code. The key is usually the site's name and is case ",
				"sensitive, so try it before listing sites.")
		).build();
	private static final RuleBasedNumberFormat _ruleBasedNumberFormat =
		new RuleBasedNumberFormat(
			LocaleUtil.ENGLISH, RuleBasedNumberFormat.SPELLOUT);

	@Reference
	private Aggregations _aggregations;

	@Reference
	private MCPToolIndexCreator _mcpToolIndexCreator;

	@Reference
	private ScoreFunctions _scoreFunctions;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private Sorts _sorts;

}