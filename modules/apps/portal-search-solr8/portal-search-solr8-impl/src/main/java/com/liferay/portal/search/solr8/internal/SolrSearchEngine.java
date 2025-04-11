/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchEngine;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(property = "search.engine.impl=Solr", service = SearchEngine.class)
public class SolrSearchEngine implements SearchEngine {

	@Override
	public synchronized String backup(long companyId, String backupName) {
		return StringPool.BLANK;
	}

	@Override
	public IndexWriter getIndexWriter() {
		return _indexWriter;
	}

	@Override
	public String getVendor() {
		return "Solr";
	}

	@Override
	public IndexSearcher getIndexSearcher() {
		return _indexSearcher;
	}

	@Override
	public void initialize(long companyId) {
	}

	@Override
	public synchronized void removeBackup(long companyId, String backupName) {
	}

	@Override
	public void removeCompany(long companyId) {
	}

	@Override
	public synchronized void restore(long companyId, String backupName) {
	}

	@Reference(target = "(search.engine.impl=Solr)")
	private IndexSearcher _indexSearcher;

	@Reference(target = "(search.engine.impl=Solr)")
	private IndexWriter _indexWriter;

}