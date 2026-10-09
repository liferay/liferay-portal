/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.suggest;

import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.suggest.NGramHolder;
import com.liferay.portal.kernel.search.suggest.NGramHolderBuilder;

import java.io.StringReader;

import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = NGramHolderBuilder.class)
public class NGramHolderBuilderImpl implements NGramHolderBuilder {

	@Override
	public NGramHolder buildNGramHolder(String input) throws SearchException {
		return buildNGramHolder(
			input, getNGramMinLength(input.length()),
			getNGramMaxLength(input.length()));
	}

	@Override
	public NGramHolder buildNGramHolder(String input, int nGramMaxLength)
		throws SearchException {

		if (nGramMaxLength <= 0) {
			nGramMaxLength = getNGramMaxLength(input.length());
		}

		return buildNGramHolder(
			input, getNGramMinLength(input.length()), nGramMaxLength);
	}

	@Override
	public NGramHolder buildNGramHolder(
			String input, int nGramMinLength, int nGramMaxLength)
		throws SearchException {

		try (NGramTokenizer nGramTokenizer = createNGramTokenizer(
				nGramMinLength, nGramMaxLength)) {

			nGramTokenizer.setReader(new StringReader(input));

			nGramTokenizer.reset();

			NGramHolder nGramHolder = new NGramHolder();

			CharTermAttribute charTermAttribute = nGramTokenizer.getAttribute(
				CharTermAttribute.class);

			OffsetAttribute offsetAttribute = nGramTokenizer.getAttribute(
				OffsetAttribute.class);

			while (nGramTokenizer.incrementToken()) {
				String nGram = charTermAttribute.toString();

				int currentNGramLength = charTermAttribute.length();

				if ((currentNGramLength < nGramMinLength) ||
					(currentNGramLength > nGramMaxLength)) {

					continue;
				}

				if (offsetAttribute.startOffset() == 0) {
					nGramHolder.addNGramStart(currentNGramLength, nGram);
				}
				else if (offsetAttribute.endOffset() == input.length()) {
					nGramHolder.addNGramEnd(currentNGramLength, nGram);
				}
				else {
					nGramHolder.addNGram(currentNGramLength, nGram);
				}
			}

			return nGramHolder;
		}
		catch (Exception exception) {
			throw new SearchException(exception);
		}
	}

	protected NGramTokenizer createNGramTokenizer(
		int nGramMinLength, int nGramMaxLength) {

		return new NGramTokenizer(nGramMinLength, nGramMaxLength) {

			@Override
			protected boolean isTokenChar(int c) {
				return !Character.isWhitespace(c);
			}

		};
	}

	protected int getNGramMaxLength(int length) {
		if (length > 5) {
			return 4;
		}
		else if (length == 5) {
			return 3;
		}

		return 2;
	}

	protected int getNGramMinLength(int length) {
		if (length > 5) {
			return 3;
		}
		else if (length == 5) {
			return 2;
		}

		return 1;
	}

}