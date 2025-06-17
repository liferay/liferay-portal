/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.index;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationObserver;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.index.util.IndexFactoryCompanyIdRegistryUtil;

import java.io.Closeable;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
public class IndexFactory
	implements Closeable, OpenSearchConfigurationObserver {

	public IndexFactory(
		CompanyIndexHelper companyIndexHelper,
		CompanyLocalService companyLocalService,
		OpenSearchConfigurationWrapper openSearchConfigurationWrapper,
		OpenSearchConnectionManager openSearchConnectionManager) {

		_companyIndexHelper = companyIndexHelper;
		_companyLocalService = companyLocalService;
		_openSearchConfigurationWrapper = openSearchConfigurationWrapper;
		_openSearchConnectionManager = openSearchConnectionManager;

		_openSearchConfigurationWrapper.register(this);

		_initializeCompanyIndexes();
	}

	@Override
	public void close() {
		_openSearchConfigurationWrapper.unregister(this);
	}

	@Override
	public int compareTo(
		OpenSearchConfigurationObserver openSearchConfigurationObserver) {

		return _openSearchConfigurationWrapper.compare(
			this, openSearchConfigurationObserver);
	}

	public boolean deleteIndex(
		long companyId, OpenSearchIndicesClient openSearchIndicesClient) {

		String indexName = _companyIndexHelper.getIndexName(companyId);

		Company company = _companyLocalService.fetchCompany(companyId);

		if ((company != null) &&
			!Validator.isBlank(company.getIndexNameCurrent())) {

			indexName = company.getIndexNameCurrent();
		}

		if (!_companyIndexHelper.hasIndex(indexName, openSearchIndicesClient)) {
			return false;
		}

		_companyIndexHelper.deleteIndex(
			companyId, indexName, openSearchIndicesClient, true);

		return true;
	}

	@Override
	public int getPriority() {
		return 3;
	}

	public boolean initializeIndex(
		long companyId, OpenSearchIndicesClient openSearchIndicesClient) {

		String indexName = _companyIndexHelper.getIndexName(companyId);

		if (_companyIndexHelper.hasIndex(indexName, openSearchIndicesClient)) {
			if ((companyId != CompanyConstants.SYSTEM) &&
				FeatureFlagManagerUtil.isEnabled(companyId, "LPD-7822")) {

				_companyIndexHelper.updateIndex(
					companyId, indexName, openSearchIndicesClient);
			}

			return false;
		}

		_companyIndexHelper.createIndex(
			companyId, indexName, openSearchIndicesClient);

		return true;
	}

	@Override
	public void onOpenSearchConfigurationUpdate() {
		_initializeCompanyIndexes();
	}

	public synchronized void registerCompanyId(long companyId) {
		IndexFactoryCompanyIdRegistryUtil.registerCompanyId(companyId);
	}

	public synchronized void unregisterCompanyId(long companyId) {
		IndexFactoryCompanyIdRegistryUtil.unregisterCompanyId(companyId);
	}

	private synchronized void _initializeCompanyIndexes() {
		_companyLocalService.forEachCompanyId(
			companyId -> _initializeIndex(companyId),
			IndexFactoryCompanyIdRegistryUtil.getCompanyIds());
	}

	private void _initializeIndex(long companyId) {
		try {
			OpenSearchClient openSearchClient =
				_openSearchConnectionManager.getOpenSearchClient();

			initializeIndex(companyId, openSearchClient.indices());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to initialize index for company " + companyId,
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(IndexFactory.class);

	private final CompanyIndexHelper _companyIndexHelper;
	private final CompanyLocalService _companyLocalService;
	private final OpenSearchConfigurationWrapper
		_openSearchConfigurationWrapper;
	private final OpenSearchConnectionManager _openSearchConnectionManager;

}