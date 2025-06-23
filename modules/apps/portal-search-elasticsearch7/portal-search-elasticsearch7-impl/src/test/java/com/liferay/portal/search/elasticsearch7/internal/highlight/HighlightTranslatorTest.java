/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.highlight;

import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.highlight.FieldConfig;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.internal.highlight.FieldConfigImpl;
import com.liferay.portal.search.internal.highlight.HighlightImpl;
import com.liferay.portal.search.internal.query.StringQueryImpl;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Bryan Engler
 */
public class HighlightTranslatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_elasticsearchQueryTranslator = new ElasticsearchQueryTranslator();
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
		expectedException.expectMessage("No enum constant");

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
			_buildFieldConfig(_createFieldConfigPrototype("title")));

		fieldConfigs.add(
			_buildFieldConfig(_createFieldConfigPrototype("description")));

		_highlightPrototype._fieldConfigs = fieldConfigs;

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testHighlightQuery() {
		_highlightPrototype._highlightQuery = new StringQueryImpl("title:test");

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testNullValues() {
		_highlightPrototype = _createHighlightPrototypeWithNullValues();

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testOrderNone() {
		_highlightPrototype._order = "none";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testOrderOther() {
		_highlightPrototype._order = "other";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testOrderScore() {
		_highlightPrototype._order = "score";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testTagSchemaDefault() {
		_highlightPrototype._tagsSchema = "default";

		_assertTranslation(_highlightPrototype);
	}

	@Test
	public void testTagSchemaInvalid() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Unknown tag schema [invalid]");

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

	protected HighlightBuilder.BoundaryScannerType getBoundaryScannerType(
		String boundaryScannerType) {

		if (boundaryScannerType == null) {
			return null;
		}

		return HighlightBuilder.BoundaryScannerType.fromString(
			boundaryScannerType);
	}

	protected HighlightBuilder.Order getOrder(String order) {
		if (order != null) {
			return HighlightBuilder.Order.fromString(order);
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
		private String _fragmenter;
		private Integer _fragmentSize;
		private String _highlighterType;
		private Boolean _highlightFilter;
		private Query _highlightQuery;
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
		private String _fragmenter;
		private Integer _fragmentSize;
		private String _highlighterType;
		private Boolean _highlightFilter;
		private Query _highlightQuery;
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
		HighlightBuilder.Field field, FieldConfig fieldConfig) {

		Assert.assertEquals(
			field.boundaryChars(), fieldConfig.getBoundaryChars());

		Assert.assertEquals(
			field.boundaryMaxScan(), fieldConfig.getBoundaryMaxScan());

		Assert.assertEquals(
			field.boundaryScannerLocale(),
			_getLocale(fieldConfig.getBoundaryScannerLocale()));

		Assert.assertEquals(
			field.boundaryScannerType(),
			getBoundaryScannerType(fieldConfig.getBoundaryScannerType()));

		Assert.assertEquals(field.forceSource(), fieldConfig.getForceSource());

		Assert.assertEquals(field.fragmenter(), fieldConfig.getFragmenter());

		Assert.assertEquals(
			field.fragmentSize(), fieldConfig.getFragmentSize());

		Assert.assertEquals(
			field.highlighterType(), fieldConfig.getHighlighterType());

		Assert.assertEquals(
			field.highlightFilter(), fieldConfig.getHighlightFilter());

		Assert.assertEquals(
			field.highlightQuery(),
			_getQueryBuilder(fieldConfig.getHighlightQuery()));

		Assert.assertEquals(field.noMatchSize(), fieldConfig.getNoMatchSize());

		Assert.assertEquals(
			field.numOfFragments(), fieldConfig.getNumFragments());

		Assert.assertEquals(field.order(), getOrder(fieldConfig.getOrder()));

		Assert.assertEquals(field.phraseLimit(), fieldConfig.getPhraseLimit());

		Assert.assertArrayEquals(field.postTags(), fieldConfig.getPostTags());

		Assert.assertArrayEquals(field.preTags(), fieldConfig.getPreTags());

		Assert.assertEquals(
			field.requireFieldMatch(), fieldConfig.getRequireFieldMatch());
	}

	private void _assertFields(
		HighlightBuilder highlightBuilder, Highlight highlight) {

		List<HighlightBuilder.Field> fields = highlightBuilder.fields();
		List<FieldConfig> fieldConfigs = highlight.getFieldConfigs();

		Assert.assertEquals(
			fieldConfigs.toString(), fields.size(), fieldConfigs.size());

		Map<String, FieldConfig> fieldConfigMap = new HashMap<>();

		for (FieldConfig fieldConfig : fieldConfigs) {
			fieldConfigMap.put(fieldConfig.getFieldName(), fieldConfig);
		}

		for (HighlightBuilder.Field field : fields) {
			FieldConfig fieldConfig = fieldConfigMap.get(field.name());

			_assertField(field, fieldConfig);
		}
	}

	private void _assertHighlightBuilder(
		HighlightBuilder highlightBuilder, Highlight highlight) {

		Assert.assertEquals(
			highlightBuilder.boundaryChars(), highlight.getBoundaryChars());

		Assert.assertEquals(
			highlightBuilder.boundaryMaxScan(), highlight.getBoundaryMaxScan());

		Assert.assertEquals(
			highlightBuilder.boundaryScannerLocale(),
			_getLocale(highlight.getBoundaryScannerLocale()));

		Assert.assertEquals(
			highlightBuilder.boundaryScannerType(),
			getBoundaryScannerType(highlight.getBoundaryScannerType()));

		Assert.assertEquals(highlightBuilder.encoder(), highlight.getEncoder());

		_assertFields(highlightBuilder, highlight);

		Assert.assertEquals(
			highlightBuilder.forceSource(), highlight.getForceSource());

		Assert.assertEquals(
			highlightBuilder.fragmenter(), highlight.getFragmenter());

		Assert.assertEquals(
			highlightBuilder.fragmentSize(), highlight.getFragmentSize());

		Assert.assertEquals(
			highlightBuilder.highlighterType(), highlight.getHighlighterType());

		Assert.assertEquals(
			highlightBuilder.highlightFilter(), highlight.getHighlightFilter());

		Assert.assertEquals(
			highlightBuilder.highlightQuery(),
			_getQueryBuilder(highlight.getHighlightQuery()));

		Assert.assertEquals(
			highlightBuilder.noMatchSize(), highlight.getNoMatchSize());

		Assert.assertEquals(
			highlightBuilder.numOfFragments(), highlight.getNumOfFragments());

		Assert.assertEquals(
			highlightBuilder.order(), getOrder(highlight.getOrder()));

		Assert.assertEquals(
			highlightBuilder.phraseLimit(), highlight.getPhraseLimit());

		Assert.assertEquals(
			highlightBuilder.requireFieldMatch(),
			highlight.getRequireFieldMatch());

		_assertTags(highlightBuilder, highlight);

		Assert.assertEquals(
			highlightBuilder.useExplicitFieldOrder(),
			getUseExplicitFieldOrder(highlight.getUseExplicitFieldOrder()));
	}

	private void _assertTags(
		HighlightBuilder highlightBuilder, Highlight highlight) {

		String tagsSchema = highlight.getTagsSchema();

		if (tagsSchema == null) {
			Assert.assertArrayEquals(
				highlightBuilder.postTags(), highlight.getPostTags());

			Assert.assertArrayEquals(
				highlightBuilder.preTags(), highlight.getPreTags());
		}
		else if (tagsSchema.equals("default")) {
			Assert.assertArrayEquals(
				highlightBuilder.postTags(), new String[] {"</em>"});

			Assert.assertArrayEquals(
				highlightBuilder.preTags(), new String[] {"<em>"});
		}
		else if (tagsSchema.equals("styled")) {
			Assert.assertArrayEquals(
				highlightBuilder.postTags(),
				HighlightBuilder.DEFAULT_STYLED_POST_TAGS);

			Assert.assertArrayEquals(
				highlightBuilder.preTags(),
				HighlightBuilder.DEFAULT_STYLED_PRE_TAG);
		}
	}

	private void _assertTranslation(HighlightPrototype highlightPrototype) {
		Highlight highlight = _buildHighlight(highlightPrototype);

		HighlightBuilder highlightBuilder = _highlightTranslator.translate(
			highlight, _elasticsearchQueryTranslator);

		_assertHighlightBuilder(highlightBuilder, highlight);
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
		HighlightImpl.HighlightBuilderImpl highlightBuilderImpl =
			new HighlightImpl.HighlightBuilderImpl();

		return highlightBuilderImpl.boundaryChars(
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
		String fragmenter = "fragmenter";
		Integer fragmentSize = 3;
		String highlighterType = "highlighterType";
		Boolean highlightFilter = true;
		Query highlightQuery = new StringQueryImpl("title:test");
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
		char[] boundaryChars = {'a', 'b'};
		Integer boundaryMaxScan = 2;
		String boundaryScannerLocale = "locale";
		String boundaryScannerType = null;
		String encoder = "encoder";
		List<FieldConfig> fieldConfigs = null;
		Boolean forceSource = true;
		String fragmenter = "fragmenter";
		Integer fragmentSize = 3;
		String highlighterType = "highlighterType";
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

	private Locale _getLocale(String boundaryScannerLocale) {
		if (boundaryScannerLocale != null) {
			return Locale.forLanguageTag(boundaryScannerLocale);
		}

		return null;
	}

	private QueryBuilder _getQueryBuilder(Query query) {
		if (query != null) {
			return _elasticsearchQueryTranslator.translate(query);
		}

		return null;
	}

	private ElasticsearchQueryTranslator _elasticsearchQueryTranslator;
	private HighlightPrototype _highlightPrototype;
	private final HighlightTranslator _highlightTranslator =
		new HighlightTranslator();

}