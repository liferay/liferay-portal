/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.suggest;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.suggest.NGramHolder;
import com.liferay.portal.kernel.search.suggest.NGramHolderBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.solr.client.solrj.SolrQuery;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = NGramQueryBuilder.class)
public class NGramQueryBuilderImpl implements NGramQueryBuilder {

	@Override
	public SolrQuery getNGramQuery(String input) throws SearchException {
		SolrQuery solrQuery = new SolrQuery();

		NGramHolderBuilder nGramHolderBuilder =
			_nGramHolderBuilderSnapshot.get();

		if (nGramHolderBuilder == null) {
			return solrQuery;
		}

		StringBundler sb = new StringBundler();

		NGramHolder nGramHolder = nGramHolderBuilder.buildNGramHolder(input);

		Map<String, List<String>> nGrams = nGramHolder.getNGrams();

		addNGramsListQuery(sb, nGrams);

		if (!nGrams.isEmpty()) {
			addOrQuerySeparator(sb);
		}

		Map<String, String> nGramEnds = nGramHolder.getNGramEnds();

		addNGramsQuery(sb, nGramEnds);

		if (!nGramEnds.isEmpty()) {
			addOrQuerySeparator(sb);
		}

		Map<String, String> nGramStarts = nGramHolder.getNGramStarts();

		addNGramsQuery(sb, nGramStarts);

		if (!nGramStarts.isEmpty()) {
			addOrQuerySeparator(sb);
		}

		addQuery(sb, Field.SPELL_CHECK_WORD, input);

		solrQuery.setQuery(sb.toString());

		return solrQuery;
	}

	protected void addNGramsListQuery(
		StringBundler sb, Map<String, List<String>> nGrams) {

		Set<Map.Entry<String, List<String>>> set = nGrams.entrySet();

		Iterator<Map.Entry<String, List<String>>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, List<String>> entry = iterator.next();

			String fieldName = entry.getKey();

			List<String> fieldValues = entry.getValue();

			Iterator<String> fieldValuesIterator = fieldValues.iterator();

			while (fieldValuesIterator.hasNext()) {
				addQuery(sb, fieldName, fieldValuesIterator.next());

				if (!fieldValuesIterator.hasNext() && !iterator.hasNext()) {
					continue;
				}

				addOrQuerySeparator(sb);
			}
		}
	}

	protected void addNGramsQuery(
		StringBundler sb, Map<String, String> nGrams) {

		Set<Map.Entry<String, String>> set = nGrams.entrySet();

		Iterator<Map.Entry<String, String>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();

			String fieldName = entry.getKey();
			String fieldValue = entry.getValue();

			addQuery(sb, fieldName, fieldValue);

			if (!iterator.hasNext()) {
				continue;
			}

			addOrQuerySeparator(sb);
		}
	}

	protected void addOrQuerySeparator(StringBundler sb) {
		sb.append(_OR_QUERY_SEPARATOR);
	}

	protected void addQuery(StringBundler sb, String name, String value) {
		sb.append(name);
		sb.append(StringPool.COLON);

		value = QueryParser.escape(value);

		value = _defuseUpperCaseLuceneBooleanOperators(value);

		value = _encloseMultiword(
			value, StringPool.OPEN_PARENTHESIS, StringPool.CLOSE_PARENTHESIS);

		sb.append(value);
	}

	private String _defuseUpperCaseLuceneBooleanOperators(String value) {
		return StringUtil.toLowerCase(value);
	}

	private String _encloseMultiword(String value, String open, String close) {
		if (value.indexOf(CharPool.SPACE) == -1) {
			return value;
		}

		return open + value + close;
	}

	private static final String _OR_QUERY_SEPARATOR = " OR ";

	private static final Snapshot<NGramHolderBuilder>
		_nGramHolderBuilderSnapshot = new Snapshot<>(
			NGramQueryBuilderImpl.class, NGramHolderBuilder.class, null, true);

}