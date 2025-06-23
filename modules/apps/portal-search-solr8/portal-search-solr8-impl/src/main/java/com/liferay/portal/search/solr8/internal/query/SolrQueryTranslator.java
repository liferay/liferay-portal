/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.query;

import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.query.QueryTranslator;

/**
 * @author André de Oliveira
 * @author Miguel Angelo Caldas Gallindo
 */
public class SolrQueryTranslator
	extends BaseQueryVisitor implements QueryTranslator<String> {

	@Override
	public String translate(Query query, SearchContext searchContext) {
		org.apache.lucene.search.Query luceneQuery = query.accept(this);

		if (luceneQuery != null) {
			return luceneQuery.toString();
		}

		return null;
	}

}