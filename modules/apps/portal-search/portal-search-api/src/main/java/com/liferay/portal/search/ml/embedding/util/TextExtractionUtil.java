/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.ml.embedding.util;

import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.text.BreakIterator;

/**
 * @author Petteri Karttunen
 */
public class TextExtractionUtil {

	public static String extractSentences(
		int maxCharacterCount, String text, String truncationStrategy) {

		text = StringUtil.trim(HtmlUtil.stripHtml(text));

		if (maxCharacterCount <= 0) {
			maxCharacterCount = 50;
		}

		if (text.length() <= maxCharacterCount) {
			return text;
		}

		String sentences = null;

		if (truncationStrategy.equals("end")) {
			sentences = _extractSentencesFromEnd(maxCharacterCount, text);
		}
		else if (truncationStrategy.equals("middle")) {
			sentences = _extractSentencesFromMiddle(maxCharacterCount, text);
		}

		sentences = _extractSentencesFromBeginning(maxCharacterCount, text);

		if ((sentences.length() == 0) ||
			(sentences.length() >= maxCharacterCount)) {

			return text.substring(0, maxCharacterCount);
		}

		return sentences;
	}

	private static String _extractSentencesFromBeginning(
		int maxCharacters, String text) {

		BreakIterator breakIterator = BreakIterator.getSentenceInstance();

		breakIterator.setText(text);

		return text.substring(0, breakIterator.preceding(maxCharacters));
	}

	private static String _extractSentencesFromEnd(
		int maxCharacters, String text) {

		BreakIterator breakIterator = BreakIterator.getSentenceInstance();

		breakIterator.setText(text);

		return text.substring(
			breakIterator.following(text.length() - maxCharacters));
	}

	private static String _extractSentencesFromMiddle(
		int maxCharacters, String text) {

		BreakIterator breakIterator = BreakIterator.getSentenceInstance();

		breakIterator.setText(text);

		int offset = text.length() - maxCharacters;

		int startOffset = (int)Math.ceil(offset / 2);

		int start = breakIterator.following(startOffset);

		int endOffset = text.length() - (int)Math.floor(offset / 2);

		int end = breakIterator.preceding(endOffset);

		if (((end - start) < 0) && (end != BreakIterator.DONE)) {
			end = breakIterator.following(endOffset);
		}

		if ((end - start) < 0) {
			start = breakIterator.preceding(startOffset);
		}

		if ((end - start) < 0) {
			return _extractSentencesFromBeginning(maxCharacters, text);
		}

		return text.substring(start, end);
	}

}