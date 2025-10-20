/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.highlight;

import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.highlight.FieldConfig;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.opensearch2.internal.util.SetterUtil;
import com.liferay.portal.search.query.QueryTranslator;

import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryVariant;
import org.opensearch.client.opensearch.core.search.BoundaryScanner;
import org.opensearch.client.opensearch.core.search.BuiltinHighlighterType;
import org.opensearch.client.opensearch.core.search.HighlightField;
import org.opensearch.client.opensearch.core.search.HighlighterEncoder;
import org.opensearch.client.opensearch.core.search.HighlighterFragmenter;
import org.opensearch.client.opensearch.core.search.HighlighterOrder;
import org.opensearch.client.opensearch.core.search.HighlighterTagsSchema;
import org.opensearch.client.opensearch.core.search.HighlighterType;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
public class HighlightTranslator {

	public org.opensearch.client.opensearch.core.search.Highlight translate(
		Highlight highlight, QueryTranslator<QueryVariant> queryTranslator) {

		org.opensearch.client.opensearch.core.search.Highlight.Builder builder =
			new org.opensearch.client.opensearch.core.search.Highlight.
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
				_translateFieldConfigs(fieldConfig, queryTranslator));
		}

		if (highlight.getFragmenter() != null) {
			builder.fragmenter(_translateFragmenter(highlight.getFragmenter()));
		}

		SetterUtil.setNotNullInteger(
			builder::fragmentSize, highlight.getFragmentSize());

		if (highlight.getHighlightQuery() != null) {
			builder.highlightQuery(
				new Query(
					queryTranslator.translate(highlight.getHighlightQuery())));
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

	public org.opensearch.client.opensearch.core.search.Highlight translate(
		String[] highlightFieldNames, int highlightFragmentSize,
		boolean highlightRequireFieldMatch, int numberOfFragments) {

		if (ArrayUtil.isEmpty(highlightFieldNames)) {
			return null;
		}

		org.opensearch.client.opensearch.core.search.Highlight.Builder builder =
			new org.opensearch.client.opensearch.core.search.Highlight.
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

	private HighlightField _translateFieldConfigs(
		FieldConfig fieldConfig,
		QueryTranslator<QueryVariant> queryTranslator) {

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
					queryTranslator.translate(
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
		BuiltinHighlighterType builtinHighlighterType = null;

		if (highlighterType.equals("FastVector")) {
			builtinHighlighterType = BuiltinHighlighterType.FastVector;
		}
		else if (highlighterType.equals("plain")) {
			builtinHighlighterType = BuiltinHighlighterType.Plain;
		}
		else if (highlighterType.equals("unified")) {
			builtinHighlighterType = BuiltinHighlighterType.Unified;
		}

		if (builtinHighlighterType != null) {
			HighlighterType.Builder highlighterTypeBuilder =
				new HighlighterType.Builder();

			return highlighterTypeBuilder.builtin(
				builtinHighlighterType
			).build();
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