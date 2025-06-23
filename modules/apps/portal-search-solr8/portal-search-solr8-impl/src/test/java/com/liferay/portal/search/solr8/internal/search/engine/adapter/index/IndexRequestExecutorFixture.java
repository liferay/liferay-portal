/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.engine.adapter.index.IndexRequestExecutor;
import com.liferay.portal.search.solr8.internal.connection.SolrClientManager;

/**
 * @author Bryan Engler
 */
public class IndexRequestExecutorFixture {

	public IndexRequestExecutor getIndexRequestExecutor() {
		return _indexRequestExecutor;
	}

	public void setUp() {
		_indexRequestExecutor = createIndexRequestExecutor(_solrClientManager);
	}

	protected IndexRequestExecutor createIndexRequestExecutor(
		SolrClientManager solrClientManager) {

		SolrIndexRequestExecutor solrIndexRequestExecutor =
			new SolrIndexRequestExecutor();

		ReflectionTestUtil.setFieldValue(
			solrIndexRequestExecutor, "_refreshIndexRequestExecutor",
			new RefreshIndexRequestExecutor(solrClientManager));

		return solrIndexRequestExecutor;
	}

	protected void setSolrClientManager(SolrClientManager solrClientManager) {
		_solrClientManager = solrClientManager;
	}

	private IndexRequestExecutor _indexRequestExecutor;
	private SolrClientManager _solrClientManager;

}