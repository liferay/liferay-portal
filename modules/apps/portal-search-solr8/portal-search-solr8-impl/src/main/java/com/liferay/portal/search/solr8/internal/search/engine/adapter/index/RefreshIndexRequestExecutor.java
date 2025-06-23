/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.search.engine.adapter.index;

import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexResponse;
import com.liferay.portal.search.solr8.internal.connection.SolrClientManager;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrException;

/**
 * @author Bryan Engler
 */
public class RefreshIndexRequestExecutor {

	public RefreshIndexRequestExecutor(SolrClientManager solrClientManager) {
		_solrClientManager = solrClientManager;
	}

	public RefreshIndexResponse execute(
		RefreshIndexRequest refreshIndexRequest) {

		String[] indexNames = refreshIndexRequest.getIndexNames();

		SolrClient solrClient = _solrClientManager.getSolrClient();

		try {
			solrClient.commit(indexNames[0]);

			return new RefreshIndexResponse();
		}
		catch (Exception exception) {
			if (exception instanceof SolrException) {
				SolrException solrException = (SolrException)exception;

				throw solrException;
			}

			throw new RuntimeException(exception);
		}
	}

	private final SolrClientManager _solrClientManager;

}