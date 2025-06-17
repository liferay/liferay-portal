/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.index;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.ccr.CrossClusterReplicationHelper;
import com.liferay.portal.search.index.ConcurrentReindexManager;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.text.SimpleDateFormat;

import java.util.Date;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 * @author Petteri Karttunen
 */
@Component(service = ConcurrentReindexManager.class)
public class CompanyConcurrentReindexManager
	implements ConcurrentReindexManager {

	@Override
	public void createNextIndex(long companyId) throws Exception {
		if (companyId == CompanyConstants.SYSTEM) {
			return;
		}

		String indexNameNext = _createIndexNameNext(companyId);

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient();

		if (_companyIndexHelper.hasIndex(
				indexNameNext, openSearchClient.indices())) {

			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info("Creating next index " + indexNameNext);
		}

		_companyIndexHelper.createIndex(
			companyId, indexNameNext, openSearchClient.indices());

		_companyLocalService.updateIndexNameNext(companyId, indexNameNext);
	}

	@Override
	public void deleteNextIndex(long companyId) {
		Company company = _companyLocalService.fetchCompany(companyId);

		if (company == null) {
			return;
		}

		String indexName = company.getIndexNameNext();

		if (!Validator.isBlank(indexName)) {
			OpenSearchClient openSearchClient =
				_openSearchConnectionManager.getOpenSearchClient();

			if (_log.isInfoEnabled()) {
				_log.info("Deleting next index " + indexName);
			}

			_companyIndexHelper.deleteIndex(
				companyId, indexName, openSearchClient.indices(), false);
		}
	}

	@Override
	public void replaceCurrentIndexWithNextIndex(long companyId)
		throws Exception {

		if (companyId == CompanyConstants.SYSTEM) {
			return;
		}

		String baseIndexName = _indexNameBuilder.getIndexName(companyId);
		Company company = _companyLocalService.getCompany(companyId);

		CrossClusterReplicationHelper crossClusterReplicationHelper =
			_crossClusterReplicationHelperSnapshot.get();

		if (crossClusterReplicationHelper != null) {
			if (!Validator.isBlank(company.getIndexNameCurrent())) {
				crossClusterReplicationHelper.unfollow(
					company.getIndexNameCurrent());
			}
			else {
				crossClusterReplicationHelper.unfollow(baseIndexName);
			}
		}

		_updateAliases(baseIndexName, company);

		_companyLocalService.updateIndexNames(
			companyId, company.getIndexNameNext(), null);

		if (crossClusterReplicationHelper != null) {
			crossClusterReplicationHelper.follow(company.getIndexNameNext());
		}
	}

	private String _createIndexNameNext(long companyId) {
		String baseIndexName = _indexNameBuilder.getIndexName(companyId);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss");

		String timeStampSuffix = simpleDateFormat.format(new Date());

		return baseIndexName + "-" + timeStampSuffix;
	}

	private void _updateAliases(String baseIndexName, Company company)
		throws Exception {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient();

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		AliasesFactory aliasesFactory = new AliasesFactory(
			_companyIndexHelper, openSearchIndicesClient);

		aliasesFactory.updateConcurrentReindexingAliases(
			baseIndexName, company, openSearchIndicesClient);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyConcurrentReindexManager.class);

	private static final Snapshot<CrossClusterReplicationHelper>
		_crossClusterReplicationHelperSnapshot = new Snapshot(
			CompanyConcurrentReindexManager.class,
			CrossClusterReplicationHelper.class, null, true);

	@Reference
	private CompanyIndexHelper _companyIndexHelper;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private OpenSearchConnectionManager _openSearchConnectionManager;

}