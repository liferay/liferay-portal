/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index;

import com.liferay.mcp.server.rest.dto.v1_0.Prerequisite;
import com.liferay.mcp.server.rest.internal.search.index.constants.MCPToolConstants;
import com.liferay.mcp.server.rest.internal.search.index.util.IntentUtil;
import com.liferay.mcp.server.rest.internal.search.index.util.WordUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.FunctionScoreQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.TermQuery;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.query.function.score.ScoreFunctions;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class MCPToolIndexReaderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_mcpToolIndexReader, "_scoreFunctions",
			Mockito.mock(ScoreFunctions.class));
	}

	@Test
	public void testAddAllowedToolKeys() {
		Query query = QueriesUtil.booleanQuery();

		Assert.assertSame(query, _addAllowedToolKeys(null, query));

		BooleanQuery booleanQuery = (BooleanQuery)_addAllowedToolKeys(
			SetUtil.fromArray("headless-delivery-v1.0#postSiteBlogPosting"),
			query);

		List<Query> filterQueryClauses = booleanQuery.getFilterQueryClauses();

		TermsQuery termsQuery = (TermsQuery)filterQueryClauses.get(0);

		Assert.assertEquals(MCPToolFields.UID, termsQuery.getField());
		Assert.assertArrayEquals(
			new String[] {"headless-delivery-v1.0#postSiteBlogPosting"},
			termsQuery.getValues());

		List<Query> mustQueryClauses = booleanQuery.getMustQueryClauses();

		Assert.assertSame(query, mustQueryClauses.get(0));
	}

	@Test
	public void testGetEntityPhrase() {
		Assert.assertEquals(
			"blog posting",
			_getEntityPhrase("create a blog posting in a site"));
	}

	@Test
	public void testGetEntityPhraseWhenBoundaryWordFollowsOfPhrase() {
		Assert.assertEquals(
			"site", _getEntityPhrase("list entries of a site in a folder"));
	}

	@Test
	public void testGetEntityPhraseWhenOfPhraseEndsWithArticle() {
		Assert.assertEquals(
			"blog postings", _getEntityPhrase("list the blog postings of the"));
	}

	@Test
	public void testGetEntityPhraseWhenOfPhraseNamesEntity() {
		Assert.assertEquals(
			"site", _getEntityPhrase("list the blog entries of a site"));
	}

	@Test
	public void testGetEntityPhraseWhenOnlyIgnoredWordsFollowVerb() {
		Assert.assertNull(_getEntityPhrase("create an entity"));
		Assert.assertNull(_getEntityPhrase("delete an object"));
		Assert.assertNull(_getEntityPhrase("get the entry"));
		Assert.assertNull(_getEntityPhrase("list the entities"));
	}

	@Test
	public void testGetParameterCollections() {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setString(
			MCPToolFields.PATH,
			"/o/headless-delivery/v1.0/sites/by-external-reference-code" +
				"/{siteExternalReferenceCode}/blog-postings/{blogPostingId}");
		documentBuilder.setStrings(
			MCPToolFields.REQUIRED_PROPERTY_COLLECTIONS,
			"contentStructureId#contentstructures");

		Map<String, String> parameterCollections = ReflectionTestUtil.invoke(
			_mcpToolIndexReader, "_getParameterCollections",
			new Class<?>[] {Document.class}, documentBuilder.build());

		Assert.assertEquals(
			LinkedHashMapBuilder.put(
				"siteExternalReferenceCode", "sites"
			).put(
				"contentStructureId", "contentstructures"
			).build(),
			parameterCollections);
	}

	@Test
	public void testGetQuery() {
		_assertDemoted(
			MCPToolConstants.OPERATION_VARIANT_BATCH, "create a blog posting");
		_assertDemoted(
			MCPToolConstants.OPERATION_VARIANT_EXPORT, "create a blog posting");
		_assertDemoted(
			MCPToolConstants.OPERATION_VARIANT_SUBSCRIPTION,
			"create a blog posting");
	}

	@Test
	public void testGetQueryWhenSearchAsksForBatch() {
		_assertNotDemoted(
			MCPToolConstants.OPERATION_VARIANT_BATCH,
			"create blog postings in bulk");
	}

	@Test
	public void testGetQueryWhenSearchAsksForCopy() {
		_assertNotDemoted(
			MCPToolConstants.OPERATION_VARIANT_COPY, "copy a site page");
		_assertDemoted(
			MCPToolConstants.OPERATION_VARIANT_MOVE, "copy a site page");
	}

	@Test
	public void testGetQueryWhenSearchAsksForExport() {
		_assertNotDemoted(
			MCPToolConstants.OPERATION_VARIANT_EXPORT, "export blog postings");
	}

	@Test
	public void testGetQueryWhenSearchAsksForImport() {
		_assertNotDemoted(
			MCPToolConstants.OPERATION_VARIANT_BATCH, "import user accounts");
	}

	@Test
	public void testGetQueryWhenSearchAsksForSubscription() {
		_assertNotDemoted(
			MCPToolConstants.OPERATION_VARIANT_SUBSCRIPTION,
			"unsubscribe from a blog posting");
		_assertDemoted(
			MCPToolConstants.OPERATION_VARIANT_RATING,
			"unsubscribe from a blog posting");
	}

	@Test
	public void testGetQueryWhenSearchIsAssociation() {
		Assert.assertNull(_getTraversalDemotionQuery("create a blog posting"));

		BooleanQuery booleanQuery = _getTraversalDemotionQuery(
			"assign a role to a user");

		List<Query> mustQueryClauses = booleanQuery.getMustQueryClauses();

		TermsQuery termsQuery = (TermsQuery)mustQueryClauses.get(0);

		Assert.assertArrayEquals(
			new String[] {MCPToolConstants.OPERATION_VARIANT_TRAVERSAL},
			termsQuery.getValues());

		TermQuery termQuery = (TermQuery)mustQueryClauses.get(1);

		Assert.assertEquals(MCPToolFields.METHOD, termQuery.getField());
		Assert.assertEquals("get", termQuery.getValue());
	}

	@Test
	public void testHasAssociationWords() {
		Assert.assertFalse(_hasAssociationWords("add a user"));
		Assert.assertTrue(_hasAssociationWords("add a user to a site"));
		Assert.assertTrue(_hasAssociationWords("assign a role to a user"));
	}

	@Test
	public void testIsBatch() {
		Assert.assertTrue(_isBatch("create 50 blog postings"));
		Assert.assertTrue(_isBatch("delete 20 user accounts"));
		Assert.assertFalse(_isBatch("delete five users"));
		Assert.assertFalse(_isBatch("delete import task"));
		Assert.assertFalse(_isBatch("delete tenant users"));
		Assert.assertTrue(_isBatch("delete twenty users at once"));
		Assert.assertTrue(_isBatch("delete users in bulk"));
	}

	@Test
	public void testIsBatchWhenNumberIsIdentifier() {
		Assert.assertFalse(_isBatch("delete site 20123 details"));
		Assert.assertFalse(_isBatch("update blog posting 40567"));
	}

	@Test
	public void testIsBatchWhenSearchIsListing() {
		Assert.assertFalse(_isBatch("list 20 users of a site"));
		Assert.assertFalse(_isBatch("list users in bulk"));
	}

	@Test
	public void testIsBatchWhenVerbIsImport() {
		Assert.assertTrue(_isBatch("import 500 users"));
		Assert.assertTrue(_isBatch("import users"));
	}

	@Test
	public void testToPrerequisites() {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setString(MCPToolFields.TOOL_NAME, "getSitesPage");
		documentBuilder.setString(
			MCPToolFields.TOOL_SET_NAME, "headless-admin-site-v1.0");

		List<Prerequisite> prerequisites = ReflectionTestUtil.invoke(
			_mcpToolIndexReader, "_toPrerequisites",
			new Class<?>[] {Map.class, Map.class},
			Collections.singletonMap("sites", documentBuilder.build()),
			LinkedHashMapBuilder.put(
				"siteId", "sites"
			).put(
				"processExternalReferenceCode", "processes"
			).put(
				"processId", "processes"
			).build());

		Assert.assertEquals(prerequisites.toString(), 2, prerequisites.size());

		Prerequisite prerequisite = prerequisites.get(0);

		Assert.assertNotNull(prerequisite.getNote());
		Assert.assertEquals("siteId", prerequisite.getParameter());
		Assert.assertEquals("getSitesPage", prerequisite.getToolName());
		Assert.assertEquals(
			"headless-admin-site-v1.0", prerequisite.getToolSetName());

		prerequisite = prerequisites.get(1);

		Assert.assertNotNull(prerequisite.getNote());
		Assert.assertEquals(
			"processExternalReferenceCode", prerequisite.getParameter());
		Assert.assertNull(prerequisite.getToolName());
		Assert.assertNull(prerequisite.getToolSetName());
	}

	private Query _addAllowedToolKeys(
		Set<String> allowedToolKeys, Query query) {

		return ReflectionTestUtil.invoke(
			_mcpToolIndexReader, "_addAllowedToolKeys",
			new Class<?>[] {Set.class, Query.class}, allowedToolKeys, query);
	}

	private void _assertDemoted(String operationVariant, String search) {
		Set<String> demotedOperationVariants = _getDemotedOperationVariants(
			search);

		Assert.assertTrue(demotedOperationVariants.contains(operationVariant));
	}

	private void _assertNotDemoted(String operationVariant, String search) {
		Set<String> demotedOperationVariants = _getDemotedOperationVariants(
			search);

		Assert.assertFalse(demotedOperationVariants.contains(operationVariant));
	}

	private Set<String> _getDemotedOperationVariants(String search) {
		Set<String> demotedOperationVariants = new HashSet<>();

		for (Query filterQuery : _getDemotionFilterQueries(search)) {
			if (!(filterQuery instanceof TermsQuery)) {
				continue;
			}

			TermsQuery termsQuery = (TermsQuery)filterQuery;

			if (!Objects.equals(
					termsQuery.getField(), MCPToolFields.OPERATION_VARIANT)) {

				continue;
			}

			Collections.addAll(
				demotedOperationVariants, termsQuery.getValues());
		}

		return demotedOperationVariants;
	}

	private List<Query> _getDemotionFilterQueries(String search) {
		FunctionScoreQuery functionScoreQuery = ReflectionTestUtil.invoke(
			_mcpToolIndexReader, "_getQuery", new Class<?>[] {String.class},
			search);

		return TransformUtil.transform(
			functionScoreQuery.getFilterQueryScoreFunctionHolders(),
			FunctionScoreQuery.FilterQueryScoreFunctionHolder::getFilterQuery);
	}

	private String _getEntityPhrase(String search) {
		String[] words = WordUtil.toWords(search);

		return ReflectionTestUtil.invoke(
			_mcpToolIndexReader, "_getEntityPhrase",
			new Class<?>[] {boolean.class, String[].class},
			_hasAssociationWords(search), words);
	}

	private BooleanQuery _getTraversalDemotionQuery(String search) {
		for (Query filterQuery : _getDemotionFilterQueries(search)) {
			if (filterQuery instanceof BooleanQuery) {
				return (BooleanQuery)filterQuery;
			}
		}

		return null;
	}

	private boolean _hasAssociationWords(String search) {
		return ReflectionTestUtil.invoke(
			_mcpToolIndexReader, "_hasAssociationWords",
			new Class<?>[] {String[].class}, (Object)WordUtil.toWords(search));
	}

	private boolean _isBatch(String search) {
		String[] words = WordUtil.toWords(search);

		String verb = IntentUtil.getVerb(words);

		return ReflectionTestUtil.invoke(
			_mcpToolIndexReader, "_isBatch",
			new Class<?>[] {Set.class, String.class, String[].class},
			IntentUtil.getIntents(verb), verb, words);
	}

	private final MCPToolIndexReader _mcpToolIndexReader =
		new MCPToolIndexReader();

}