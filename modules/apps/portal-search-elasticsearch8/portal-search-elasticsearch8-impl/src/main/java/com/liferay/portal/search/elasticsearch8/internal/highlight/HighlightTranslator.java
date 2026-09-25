/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.highlight;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.BoundaryScanner;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.HighlighterEncoder;
import co.elastic.clients.elasticsearch.core.search.HighlighterFragmenter;
import co.elastic.clients.elasticsearch.core.search.HighlighterOrder;
import co.elastic.clients.elasticsearch.core.search.HighlighterTagsSchema;
import co.elastic.clients.elasticsearch.core.search.HighlighterType;

import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.elasticsearch8.internal.query.ElasticsearchQueryVisitor;
import com.liferay.portal.search.elasticsearch8.internal.util.SetterUtil;
import com.liferay.portal.search.highlight.FieldConfig;
import com.liferay.portal.search.highlight.Highlight;

/**
 * @author Michael C. Han
 */
public class HighlightTranslator {

	public co.elastic.clients.elasticsearch.core.search.Highlight translate(
		Highlight highlight) {

		return translate(null, highlight);
	}

	public co.elastic.clients.elasticsearch.core.search.Highlight translate(
		Query defaultHighlightQuery, Highlight highlight) {

		co.elastic.clients.elasticsearch.core.search.Highlight.Builder builder =
			new co.elastic.clients.elasticsearch.core.search.Highlight.
				Builder();

		if (ArrayUtil.isNotEmpty(highlight.getBoundaryChars())) {
			builder.boundaryChars(String.valueOf(highlight.getBoundaryChars()));
		}

		SetterUtil.setNotNullInteger(
			builder::boundaryMaxScan, highlight.getBoundaryMaxScan());

		if (highlight.getBoundaryScannerType() != null) {
			builder.boundaryScanner(
				_translateBoundaryScannerType(
					highlight.getBoundaryScannerType()));
		}

		SetterUtil.setNotBlankString(
			builder::boundaryScannerLocale,
			highlight.getBoundaryScannerLocale());

		if (highlight.getEncoder() != null) {
			builder.encoder(_translateEncoder(highlight.getEncoder()));
		}

		for (FieldConfig fieldConfig : highlight.getFieldConfigs()) {
			builder.fields(
				fieldConfig.getFieldName(),
				_translateFieldConfigs(fieldConfig));
		}

		if (highlight.getFragmenter() != null) {
			builder.fragmenter(_translateFragmenter(highlight.getFragmenter()));
		}

		SetterUtil.setNotNullInteger(
			builder::fragmentSize, highlight.getFragmentSize());

		if (highlight.getHighlightQuery() != null) {
			builder.highlightQuery(
				new Query(
					ElasticsearchQueryVisitor.INSTANCE.translate(
						highlight.getHighlightQuery())));
		}
		else if (defaultHighlightQuery != null) {
			builder.highlightQuery(defaultHighlightQuery);
		}

		SetterUtil.setNotNullInteger(
			builder::noMatchSize, highlight.getNoMatchSize());
		SetterUtil.setNotNullInteger(
			builder::numberOfFragments, highlight.getNumOfFragments());

		if (highlight.getOrder() != null) {
			builder.order(_translateOrder(highlight.getOrder()));
		}

		SetterUtil.setNotNullEmptyStringArrayAsList(
			builder::postTags, highlight.getPostTags());
		SetterUtil.setNotNullEmptyStringArrayAsList(
			builder::preTags, highlight.getPreTags());
		SetterUtil.setNotNullBoolean(
			builder::requireFieldMatch, highlight.getRequireFieldMatch());

		if (highlight.getTagsSchema() != null) {
			builder.tagsSchema(_translateTagsSchema(highlight.getTagsSchema()));
		}

		if (highlight.getHighlighterType() != null) {
			builder.type(
				_translateHighlighterType(highlight.getHighlighterType()));
		}

		return builder.build();
	}

	public co.elastic.clients.elasticsearch.core.search.Highlight translate(
		String[] highlightFieldNames, int highlightFragmentSize,
		boolean highlightRequireFieldMatch, int numberOfFragments) {

		return translate(
			highlightFieldNames, highlightFragmentSize, null,
			highlightRequireFieldMatch, numberOfFragments);
	}

	public co.elastic.clients.elasticsearch.core.search.Highlight translate(
		String[] highlightFieldNames, int highlightFragmentSize,
		Query highlightQuery, boolean highlightRequireFieldMatch,
		int numberOfFragments) {

		if (ArrayUtil.isEmpty(highlightFieldNames)) {
			return null;
		}

		co.elastic.clients.elasticsearch.core.search.Highlight.Builder builder =
			new co.elastic.clients.elasticsearch.core.search.Highlight.
				Builder();

		for (String highlightFieldName : highlightFieldNames) {
			builder.fields(
				highlightFieldName,
				HighlightField.of(
					highlightField -> highlightField.fragmentSize(
						highlightFragmentSize
					).numberOfFragments(
						numberOfFragments
					)));
		}

		if (highlightQuery != null) {
			builder.highlightQuery(highlightQuery);
		}

		builder.postTags(HighlightUtil.HIGHLIGHT_TAG_CLOSE);
		builder.preTags(HighlightUtil.HIGHLIGHT_TAG_OPEN);

		builder.requireFieldMatch(highlightRequireFieldMatch);

		return builder.build();
	}

	private BoundaryScanner _translateBoundaryScannerType(
		String boundaryScannerType) {

		if (boundaryScannerType.equals("chars")) {
			return BoundaryScanner.Chars;
		}
		else if (boundaryScannerType.equals("sentence")) {
			return BoundaryScanner.Sentence;
		}
		else if (boundaryScannerType.equals("word")) {
			return BoundaryScanner.Word;
		}

		throw new IllegalArgumentException(
			"Invalid boundary scanner type " + boundaryScannerType);
	}

	private HighlighterEncoder _translateEncoder(String encoder) {
		if (encoder.equals("default")) {
			return HighlighterEncoder.Default;
		}
		else if (encoder.equals("html")) {
			return HighlighterEncoder.Html;
		}

		throw new IllegalArgumentException(
			"Invalid highlight encoder scanner for " + encoder);
	}

	private HighlightField _translateFieldConfigs(FieldConfig fieldConfig) {
		HighlightField.Builder builder = new HighlightField.Builder();

		if (ArrayUtil.isNotEmpty(fieldConfig.getBoundaryChars())) {
			builder.boundaryChars(
				String.valueOf(fieldConfig.getBoundaryChars()));
		}

		SetterUtil.setNotNullInteger(
			builder::boundaryMaxScan, fieldConfig.getBoundaryMaxScan());

		if (fieldConfig.getBoundaryScannerType() != null) {
			builder.boundaryScanner(
				_translateBoundaryScannerType(
					fieldConfig.getBoundaryScannerType()));
		}

		SetterUtil.setNotBlankString(
			builder::boundaryScannerLocale,
			fieldConfig.getBoundaryScannerLocale());

		SetterUtil.setNotNullBoolean(
			builder::forceSource, fieldConfig.getForceSource());

		if (fieldConfig.getFragmenter() != null) {
			builder.fragmenter(
				_translateFragmenter(fieldConfig.getFragmenter()));
		}

		SetterUtil.setNotNullInteger(
			builder::fragmentOffset, fieldConfig.getFragmentOffset());
		SetterUtil.setNotNullInteger(
			builder::fragmentSize, fieldConfig.getFragmentSize());

		if (fieldConfig.getHighlightQuery() != null) {
			builder.highlightQuery(
				new Query(
					ElasticsearchQueryVisitor.INSTANCE.translate(
						fieldConfig.getHighlightQuery())));
		}

		SetterUtil.setNotNullEmptyStringArrayAsList(
			builder::matchedFields, fieldConfig.getMatchedFields());
		SetterUtil.setNotNullInteger(
			builder::noMatchSize, fieldConfig.getNoMatchSize());
		SetterUtil.setNotNullInteger(
			builder::numberOfFragments, fieldConfig.getNumFragments());

		if (fieldConfig.getOrder() != null) {
			builder.order(_translateOrder(fieldConfig.getOrder()));
		}

		SetterUtil.setNotNullInteger(
			builder::phraseLimit, fieldConfig.getPhraseLimit());
		SetterUtil.setNotNullEmptyStringArrayAsList(
			builder::postTags, fieldConfig.getPostTags());
		SetterUtil.setNotNullEmptyStringArrayAsList(
			builder::preTags, fieldConfig.getPreTags());
		SetterUtil.setNotNullBoolean(
			builder::requireFieldMatch, fieldConfig.getRequireFieldMatch());

		if (fieldConfig.getHighlighterType() != null) {
			builder.type(
				_translateHighlighterType(fieldConfig.getHighlighterType()));
		}

		return builder.build();
	}

	private HighlighterFragmenter _translateFragmenter(String fragmenter) {
		if (fragmenter.equals("simple")) {
			return HighlighterFragmenter.Simple;
		}
		else if (fragmenter.equals("span")) {
			return HighlighterFragmenter.Span;
		}

		throw new IllegalArgumentException(
			"No available highlight fragmenter for " + fragmenter);
	}

	private HighlighterType _translateHighlighterType(String highlighterType) {
		if (highlighterType.equals("FastVector")) {
			return HighlighterType.FastVector;
		}
		else if (highlighterType.equals("plain")) {
			return HighlighterType.Plain;
		}
		else if (highlighterType.equals("unified")) {
			return HighlighterType.Unified;
		}

		throw new IllegalArgumentException(
			"Invalid highlighter type " + highlighterType);
	}

	private HighlighterOrder _translateOrder(String order) {
		if (order.equals("score")) {
			return HighlighterOrder.Score;
		}

		throw new IllegalArgumentException(
			"Invalid highlighter order " + order);
	}

	private HighlighterTagsSchema _translateTagsSchema(String tagsSchema) {
		if (tagsSchema.equals("styled")) {
			return HighlighterTagsSchema.Styled;
		}

		throw new IllegalArgumentException("Invalid tags schema " + tagsSchema);
	}

}