/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.highlight;

import com.liferay.portal.kernel.search.highlight.HighlightUtil;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

/**
 * @author Michael C. Han
 */
public class HighlighterTranslator {

	public void translate(
		SearchSourceBuilder searchSourceBuilder, String[] highlightFieldNames,
		boolean highlightRequireFieldMatch, int highlightFragmentSize,
		int highlightSnippetSize, boolean luceneSyntax) {

		HighlightBuilder highlightBuilder = new HighlightBuilder();

		for (String highlightFieldName : highlightFieldNames) {
			highlightBuilder.field(
				highlightFieldName, highlightFragmentSize,
				highlightSnippetSize);
		}

		highlightBuilder.postTags(HighlightUtil.HIGHLIGHT_TAG_CLOSE);
		highlightBuilder.preTags(HighlightUtil.HIGHLIGHT_TAG_OPEN);

		if (luceneSyntax) {
			highlightRequireFieldMatch = false;
		}

		highlightBuilder.requireFieldMatch(highlightRequireFieldMatch);

		searchSourceBuilder.highlighter(highlightBuilder);
	}

}