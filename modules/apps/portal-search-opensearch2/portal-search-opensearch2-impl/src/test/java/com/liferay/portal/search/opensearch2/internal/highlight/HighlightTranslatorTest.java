/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.highlight;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.highlight.FieldConfig;
import com.liferay.portal.search.highlight.FieldConfigBuilder;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.internal.highlight.FieldConfigImpl;
import com.liferay.portal.search.internal.highlight.HighlightImpl;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.query.OpenSearchQueryVisitor;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.StringQuery;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.opensearch.client.opensearch._types.query_dsl.Query.Kind;
import org.opensearch.client.opensearch.core.search.BoundaryScanner;
import org.opensearch.client.opensearch.core.search.BuiltinHighlighterType;
import org.opensearch.client.opensearch.core.search.HighlightField;
import org.opensearch.client.opensearch.core.search.HighlighterEncoder;
import org.opensearch.client.opensearch.core.search.HighlighterFragmenter;
import org.opensearch.client.opensearch.core.search.HighlighterOrder;
import org.opensearch.client.opensearch.core.search.HighlighterType;

/**
 * @author Bryan Engler
 * @author Petteri Karttunen
 */
public class HighlightTranslatorTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static final OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Before
	public void setUp() {
		_highlightPrototype = _createHighlightPrototype();
	}

	@Test
	public void testBoundaryScannerTypeChars() {
		_highlightPrototype._boundaryScannerType = "chars";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testBoundaryScannerTypeInvalid() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(
			"Invalid boundary scanner type invalid");

		_highlightPrototype._boundaryScannerType = "invalid";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testBoundaryScannerTypeSentence() {
		_highlightPrototype._boundaryScannerType = "sentence";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testBoundaryScannerTypeWord() {
		_highlightPrototype._boundaryScannerType = "word";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testFieldConfigs() {
		List<FieldConfig> fieldConfigs = new ArrayList<>();

		fieldConfigs.add(
			_buildFieldConfig(_createFieldConfigPrototype("description")));
		fieldConfigs.add(
			_buildFieldConfig(_createFieldConfigPrototype("title")));

		_highlightPrototype._fieldConfigs = fieldConfigs;

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testHighlightQuery() {
		_highlightPrototype._highlightQuery = new StringQuery("title:test");

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testNullValues() {
		_highlightPrototype = _createHighlightPrototypeWithNullValues();

		List<FieldConfig> fieldConfigs = new ArrayList<>();

		fieldConfigs.add(_buildFieldConfig(_createFieldConfigPrototype("abc")));

		_highlightPrototype._fieldConfigs = fieldConfigs;

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testOrderScore() {
		_highlightPrototype._order = "score";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testTagSchemaInvalid() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Invalid tags schema");

		_highlightPrototype._tagsSchema = "invalid";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testTagSchemaStyled() {
		_highlightPrototype._tagsSchema = "styled";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testTranslate() {
		_assertTranslation(_highlightPrototype);
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	protected BoundaryScanner getBoundaryScanner(String boundaryScannerType) {
		if (boundaryScannerType == null) {
			return null;
		}

		return BoundaryScanner.valueOf(
			StringUtils.capitalize(boundaryScannerType));
	}

	protected HighlighterOrder getOrder(String order) {
		if (order != null) {
			return HighlighterOrder.valueOf(StringUtils.capitalize(order));
		}

		return null;
	}

	protected Boolean getUseExplicitFieldOrder(Boolean useExplicitFieldOrder) {
		if (useExplicitFieldOrder != null) {
			return useExplicitFieldOrder;
		}

		return false;
	}

	protected class FieldConfigPrototype {

		public FieldConfigPrototype(
			char[] boundaryChars, Integer boundaryMaxScan,
			String boundaryScannerLocale, String boundaryScannerType,
			String fieldName, Boolean forceSource, String fragmenter,
			Integer fragmentSize, String highlighterType,
			Boolean highlightFilter, Query highlightQuery,
			String[] matchedFields, Integer noMatchSize, Integer numFragments,
			String order, Integer phraseLimit, String[] postTags,
			String[] preTags, Boolean requireFieldMatch) {

			_boundaryChars = boundaryChars;
			_boundaryMaxScan = boundaryMaxScan;
			_boundaryScannerLocale = boundaryScannerLocale;
			_boundaryScannerType = boundaryScannerType;
			_fieldName = fieldName;
			_forceSource = forceSource;
			_fragmenter = fragmenter;
			_fragmentSize = fragmentSize;
			_highlighterType = highlighterType;
			_highlightFilter = highlightFilter;
			_highlightQuery = highlightQuery;
			_matchedFields = matchedFields;
			_noMatchSize = noMatchSize;
			_numFragments = numFragments;
			_order = order;
			_phraseLimit = phraseLimit;
			_postTags = postTags;
			_preTags = preTags;
			_requireFieldMatch = requireFieldMatch;
		}

		private char[] _boundaryChars;
		private Integer _boundaryMaxScan;
		private String _boundaryScannerLocale;
		private String _boundaryScannerType;
		private final String _fieldName;
		private Boolean _forceSource;
		private Integer _fragmentSize;
		private String _fragmenter;
		private Boolean _highlightFilter;
		private Query _highlightQuery;
		private String _highlighterType;
		private final String[] _matchedFields;
		private Integer _noMatchSize;
		private final Integer _numFragments;
		private String _order;
		private Integer _phraseLimit;
		private String[] _postTags;
		private String[] _preTags;
		private Boolean _requireFieldMatch;

	}

	protected class HighlightPrototype {

		public HighlightPrototype(
			char[] boundaryChars, Integer boundaryMaxScan,
			String boundaryScannerLocale, String boundaryScannerType,
			String encoder, List<FieldConfig> fieldConfigs, Boolean forceSource,
			String fragmenter, Integer fragmentSize, String highlighterType,
			Boolean highlightFilter, Query highlightQuery, Integer noMatchSize,
			Integer numOfFragments, String order, Integer phraseLimit,
			String[] postTags, String[] preTags, Boolean requireFieldMatch,
			String tagsSchema, Boolean useExplicitFieldOrder) {

			_boundaryChars = boundaryChars;
			_boundaryMaxScan = boundaryMaxScan;
			_boundaryScannerLocale = boundaryScannerLocale;
			_boundaryScannerType = boundaryScannerType;
			_encoder = encoder;
			_fieldConfigs = fieldConfigs;
			_forceSource = forceSource;
			_fragmenter = fragmenter;
			_fragmentSize = fragmentSize;
			_highlighterType = highlighterType;
			_highlightFilter = highlightFilter;
			_highlightQuery = highlightQuery;
			_noMatchSize = noMatchSize;
			_numOfFragments = numOfFragments;
			_order = order;
			_phraseLimit = phraseLimit;
			_postTags = postTags;
			_preTags = preTags;
			_requireFieldMatch = requireFieldMatch;
			_tagsSchema = tagsSchema;
			_useExplicitFieldOrder = useExplicitFieldOrder;
		}

		private char[] _boundaryChars;
		private Integer _boundaryMaxScan;
		private String _boundaryScannerLocale;
		private String _boundaryScannerType;
		private final String _encoder;
		private List<FieldConfig> _fieldConfigs;
		private Boolean _forceSource;
		private Integer _fragmentSize;
		private String _fragmenter;
		private Boolean _highlightFilter;
		private Query _highlightQuery;
		private String _highlighterType;
		private Integer _noMatchSize;
		private final Integer _numOfFragments;
		private String _order;
		private Integer _phraseLimit;
		private String[] _postTags;
		private String[] _preTags;
		private Boolean _requireFieldMatch;
		private String _tagsSchema;
		private final Boolean _useExplicitFieldOrder;

	}

	private void _assertField(
		HighlightField highlightField, FieldConfig fieldConfig) {

		Assert.assertEquals(
			highlightField.boundaryChars(),
			_getBoundaryChars(fieldConfig.getBoundaryChars()));

		Assert.assertEquals(
			GetterUtil.getInteger(highlightField.boundaryMaxScan(), -1),
			GetterUtil.getInteger(fieldConfig.getBoundaryMaxScan(), -1));

		Assert.assertEquals(
			highlightField.boundaryScannerLocale(),
			fieldConfig.getBoundaryScannerLocale());

		Assert.assertEquals(
			highlightField.boundaryScanner(),
			getBoundaryScanner(fieldConfig.getBoundaryScannerType()));

		Assert.assertEquals(
			highlightField.forceSource(), fieldConfig.getForceSource());

		Assert.assertEquals(
			_getHighlighterFragmenter(highlightField.fragmenter()),
			fieldConfig.getFragmenter());

		Assert.assertEquals(
			highlightField.fragmentSize(), fieldConfig.getFragmentSize());

		Assert.assertEquals(
			_getHighlighterType(highlightField.type()),
			fieldConfig.getHighlighterType());

		Assert.assertEquals(
			_getQueryKind(highlightField.highlightQuery()),
			_getQueryKind(fieldConfig.getHighlightQuery()));

		Assert.assertEquals(
			highlightField.noMatchSize(), fieldConfig.getNoMatchSize());

		Assert.assertEquals(
			highlightField.numberOfFragments(), fieldConfig.getNumFragments());

		Assert.assertEquals(
			highlightField.order(), getOrder(fieldConfig.getOrder()));

		Assert.assertEquals(
			highlightField.phraseLimit(), fieldConfig.getPhraseLimit());

		Assert.assertArrayEquals(
			ArrayUtil.toStringArray(highlightField.postTags()),
			fieldConfig.getPostTags());

		Assert.assertArrayEquals(
			ArrayUtil.toStringArray(highlightField.preTags()),
			fieldConfig.getPreTags());

		Assert.assertEquals(
			highlightField.requireFieldMatch(),
			fieldConfig.getRequireFieldMatch());
	}

	private void _assertFields(
		org.opensearch.client.opensearch.core.search.Highlight
			openSearchHighlight,
		Highlight highlight) {

		Map<String, HighlightField> fields = openSearchHighlight.fields();
		List<FieldConfig> fieldConfigs = highlight.getFieldConfigs();

		Assert.assertEquals(
			fieldConfigs.toString(), fields.size(), fieldConfigs.size());

		Map<String, FieldConfig> fieldConfigMap = new HashMap<>();

		for (FieldConfig fieldConfig : fieldConfigs) {
			fieldConfigMap.put(fieldConfig.getFieldName(), fieldConfig);
		}

		for (Map.Entry<String, HighlightField> entry : fields.entrySet()) {
			FieldConfig fieldConfig = fieldConfigMap.get(entry.getKey());

			_assertField(entry.getValue(), fieldConfig);
		}
	}

	private void _assertHighlightBuilder(
		org.opensearch.client.opensearch.core.search.Highlight
			openSearchHighlight,
		Highlight highlight) {

		Assert.assertEquals(
			openSearchHighlight.boundaryChars(),
			_getBoundaryChars(highlight.getBoundaryChars()));

		Assert.assertEquals(
			openSearchHighlight.boundaryMaxScan(),
			highlight.getBoundaryMaxScan());

		Assert.assertEquals(
			openSearchHighlight.boundaryScannerLocale(),
			highlight.getBoundaryScannerLocale());

		Assert.assertEquals(
			openSearchHighlight.boundaryScanner(),
			getBoundaryScanner(highlight.getBoundaryScannerType()));

		Assert.assertEquals(
			_getHighlighterEncoder(openSearchHighlight.encoder()),
			highlight.getEncoder());

		_assertFields(openSearchHighlight, highlight);

		Assert.assertEquals(
			_getHighlighterFragmenter(openSearchHighlight.fragmenter()),
			highlight.getFragmenter());

		Assert.assertEquals(
			openSearchHighlight.fragmentSize(), highlight.getFragmentSize());

		Assert.assertEquals(
			_getHighlighterType(openSearchHighlight.type()),
			highlight.getHighlighterType());

		Assert.assertEquals(
			_getQueryKind(openSearchHighlight.highlightQuery()),
			_getQueryKind(highlight.getHighlightQuery()));

		Assert.assertEquals(
			openSearchHighlight.noMatchSize(), highlight.getNoMatchSize());

		Assert.assertEquals(
			openSearchHighlight.numberOfFragments(),
			highlight.getNumOfFragments());

		Assert.assertEquals(
			openSearchHighlight.order(), getOrder(highlight.getOrder()));

		Assert.assertEquals(
			openSearchHighlight.requireFieldMatch(),
			highlight.getRequireFieldMatch());

		_assertTags(openSearchHighlight, highlight);
	}

	private void _assertTags(
		org.opensearch.client.opensearch.core.search.Highlight
			openSearchHighlight,
		Highlight highlight) {

		String tagsSchema = highlight.getTagsSchema();

		if (tagsSchema == null) {
			String[] postTags = new String[0];

			if (highlight.getPostTags() != null) {
				postTags = highlight.getPostTags();
			}

			Assert.assertArrayEquals(
				ArrayUtil.toStringArray(openSearchHighlight.postTags()),
				postTags);

			String[] preTags = new String[0];

			if (highlight.getPostTags() != null) {
				preTags = highlight.getPreTags();
			}

			Assert.assertArrayEquals(
				ArrayUtil.toStringArray(openSearchHighlight.preTags()),
				preTags);
		}
		else if (tagsSchema.equals("styled")) {
			Assert.assertArrayEquals(
				ArrayUtil.toStringArray(openSearchHighlight.postTags()),
				new String[] {"post"});

			Assert.assertArrayEquals(
				ArrayUtil.toStringArray(openSearchHighlight.preTags()),
				new String[] {"pre"});
		}
	}

	private void _assertTranslation(HighlightPrototype highlightPrototype) {
		Highlight highlight = _buildHighlight(highlightPrototype);

		org.opensearch.client.opensearch.core.search.Highlight
			openSearchHighlight = _highlightTranslator.translate(highlight);

		_assertHighlightBuilder(openSearchHighlight, highlight);
	}

	private FieldConfig _buildFieldConfig(
		FieldConfigPrototype fieldConfigPrototype) {

		FieldConfigImpl.FieldConfigBuilderImpl fieldConfigBuilderImpl =
			new FieldConfigImpl.FieldConfigBuilderImpl(
				fieldConfigPrototype._fieldName);

		return fieldConfigBuilderImpl.boundaryChars(
			fieldConfigPrototype._boundaryChars
		).boundaryMaxScan(
			fieldConfigPrototype._boundaryMaxScan
		).boundaryScannerLocale(
			fieldConfigPrototype._boundaryScannerLocale
		).boundaryScannerType(
			fieldConfigPrototype._boundaryScannerType
		).forceSource(
			fieldConfigPrototype._forceSource
		).fragmenter(
			fieldConfigPrototype._fragmenter
		).fragmentSize(
			fieldConfigPrototype._fragmentSize
		).highlighterType(
			fieldConfigPrototype._highlighterType
		).highlightFilter(
			fieldConfigPrototype._highlightFilter
		).highlightQuery(
			fieldConfigPrototype._highlightQuery
		).matchedFields(
			fieldConfigPrototype._matchedFields
		).noMatchSize(
			fieldConfigPrototype._noMatchSize
		).numFragments(
			fieldConfigPrototype._numFragments
		).order(
			fieldConfigPrototype._order
		).phraseLimit(
			fieldConfigPrototype._phraseLimit
		).postTags(
			fieldConfigPrototype._postTags
		).preTags(
			fieldConfigPrototype._preTags
		).requireFieldMatch(
			fieldConfigPrototype._requireFieldMatch
		).build();
	}

	private Highlight _buildHighlight(HighlightPrototype highlightPrototype) {
		HighlightImpl.HighlightBuilderImpl openSearchHighlightImpl =
			new HighlightImpl.HighlightBuilderImpl();

		return openSearchHighlightImpl.boundaryChars(
			highlightPrototype._boundaryChars
		).boundaryMaxScan(
			highlightPrototype._boundaryMaxScan
		).boundaryScannerLocale(
			highlightPrototype._boundaryScannerLocale
		).boundaryScannerType(
			highlightPrototype._boundaryScannerType
		).encoder(
			highlightPrototype._encoder
		).fieldConfigs(
			(highlightPrototype._fieldConfigs == null) ?
				Collections.emptyList() : highlightPrototype._fieldConfigs
		).forceSource(
			highlightPrototype._forceSource
		).fragmenter(
			highlightPrototype._fragmenter
		).fragmentSize(
			highlightPrototype._fragmentSize
		).highlighterType(
			highlightPrototype._highlighterType
		).highlightFilter(
			highlightPrototype._highlightFilter
		).highlightQuery(
			highlightPrototype._highlightQuery
		).numOfFragments(
			highlightPrototype._numOfFragments
		).noMatchSize(
			highlightPrototype._noMatchSize
		).order(
			highlightPrototype._order
		).phraseLimit(
			highlightPrototype._phraseLimit
		).postTags(
			highlightPrototype._postTags
		).preTags(
			highlightPrototype._preTags
		).requireFieldMatch(
			highlightPrototype._requireFieldMatch
		).tagsSchema(
			highlightPrototype._tagsSchema
		).useExplicitFieldOrder(
			highlightPrototype._useExplicitFieldOrder
		).build();
	}

	private FieldConfigPrototype _createFieldConfigPrototype(String fieldName) {
		char[] boundaryChars = {'a', 'b'};
		Integer boundaryMaxScan = 2;
		String boundaryScannerLocale = "locale";
		String boundaryScannerType = "word";
		Boolean forceSource = true;
		String fragmenter = "simple";
		Integer fragmentSize = 3;
		String highlighterType = "plain";
		Boolean highlightFilter = true;
		Query highlightQuery = new StringQuery("title:test");
		String[] matchedFields = null;
		Integer noMatchSize = 4;
		Integer numFragments = 5;
		String order = "score";
		Integer phraseLimit = 6;
		String[] postTags = {"post"};
		String[] preTags = {"pre"};
		Boolean requireFieldMatch = true;

		return new FieldConfigPrototype(
			boundaryChars, boundaryMaxScan, boundaryScannerLocale,
			boundaryScannerType, fieldName, forceSource, fragmenter,
			fragmentSize, highlighterType, highlightFilter, highlightQuery,
			matchedFields, noMatchSize, numFragments, order, phraseLimit,
			postTags, preTags, requireFieldMatch);
	}

	private HighlightPrototype _createHighlightPrototype() {
		FieldConfigBuilder fieldConfigBuilder =
			new FieldConfigImpl.FieldConfigBuilderImpl("abc");

		char[] boundaryChars = {'a', 'b'};
		Integer boundaryMaxScan = 2;
		String boundaryScannerLocale = "locale";
		String boundaryScannerType = null;
		String encoder = "default";
		List<FieldConfig> fieldConfigs = ListUtil.toList(
			fieldConfigBuilder.build());
		Boolean forceSource = true;
		String fragmenter = "simple";
		Integer fragmentSize = 3;
		String highlighterType = "plain";
		Boolean highlightFilter = true;
		Query highlightQuery = null;
		Integer noMatchSize = 4;
		Integer numOfFragments = 5;
		String order = null;
		Integer phraseLimit = 6;
		String[] postTags = {"post"};
		String[] preTags = {"pre"};
		Boolean requireFieldMatch = true;
		String tagsSchema = null;
		Boolean useExplicitFieldOrder = true;

		return new HighlightPrototype(
			boundaryChars, boundaryMaxScan, boundaryScannerLocale,
			boundaryScannerType, encoder, fieldConfigs, forceSource, fragmenter,
			fragmentSize, highlighterType, highlightFilter, highlightQuery,
			noMatchSize, numOfFragments, order, phraseLimit, postTags, preTags,
			requireFieldMatch, tagsSchema, useExplicitFieldOrder);
	}

	private HighlightPrototype _createHighlightPrototypeWithNullValues() {
		char[] boundaryChars = null;
		Integer boundaryMaxScan = null;
		String boundaryScannerLocale = null;
		String boundaryScannerType = null;
		String encoder = null;
		List<FieldConfig> fieldConfigs = null;
		Boolean forceSource = null;
		String fragmenter = null;
		Integer fragmentSize = null;
		String highlighterType = null;
		Boolean highlightFilter = null;
		Query highlightQuery = null;
		Integer noMatchSize = null;
		Integer numOfFragments = null;
		String order = null;
		Integer phraseLimit = null;
		String[] postTags = null;
		String[] preTags = null;
		Boolean requireFieldMatch = null;
		String tagsSchema = null;
		Boolean useExplicitFieldOrder = null;

		return new HighlightPrototype(
			boundaryChars, boundaryMaxScan, boundaryScannerLocale,
			boundaryScannerType, encoder, fieldConfigs, forceSource, fragmenter,
			fragmentSize, highlighterType, highlightFilter, highlightQuery,
			noMatchSize, numOfFragments, order, phraseLimit, postTags, preTags,
			requireFieldMatch, tagsSchema, useExplicitFieldOrder);
	}

	private String _getBoundaryChars(char[] boundaryChars) {
		if (ArrayUtil.isEmpty(boundaryChars)) {
			return null;
		}

		return String.valueOf(boundaryChars);
	}

	private String _getHighlighterEncoder(
		HighlighterEncoder highlighterEncoder) {

		if (highlighterEncoder == null) {
			return null;
		}

		return highlighterEncoder.jsonValue();
	}

	private String _getHighlighterFragmenter(
		HighlighterFragmenter highlighterFragmenter) {

		if (highlighterFragmenter == null) {
			return null;
		}

		return highlighterFragmenter.jsonValue();
	}

	private String _getHighlighterType(HighlighterType highlighterType) {
		if ((highlighterType == null) || !highlighterType.isBuiltin()) {
			return null;
		}

		BuiltinHighlighterType builtinHighlighterType =
			highlighterType.builtin();

		return builtinHighlighterType.jsonValue();
	}

	private Object _getQueryKind(
		org.opensearch.client.opensearch._types.query_dsl.Query
			openSearchQuery) {

		if (openSearchQuery == null) {
			return null;
		}

		Kind kind = openSearchQuery._kind();

		return kind.jsonValue();
	}

	private String _getQueryKind(Query query) {
		if (query == null) {
			return null;
		}

		org.opensearch.client.opensearch._types.query_dsl.Query
			openSearchQuery =
				new org.opensearch.client.opensearch._types.query_dsl.Query(
					OpenSearchQueryVisitor.INSTANCE.translate(query));

		Kind kind = openSearchQuery._kind();

		return kind.jsonValue();
	}

	private HighlightPrototype _highlightPrototype;
	private final HighlightTranslator _highlightTranslator =
		new HighlightTranslator();

}