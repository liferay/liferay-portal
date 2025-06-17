/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.index;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.ccr.CrossClusterReplicationHelper;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.index.ConcurrentReindexManager;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetadata;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
@Component(service = ConcurrentReindexManager.class)
public class CompanyConcurrentReindexManager
	implements ConcurrentReindexManager {

	@Override
	public void createNextIndex(long companyId) throws Exception {
		if (companyId == CompanyConstants.SYSTEM) {
			return;
		}

		String baseIndexName = _indexNameBuilder.getIndexName(companyId);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		String timeStampSuffix = dateFormat.format(new Date());

		String newIndexName = baseIndexName + "-" + timeStampSuffix;

		RestHighLevelClient restHighLevelClient =
			_elasticsearchConnectionManager.getRestHighLevelClient();

		if (_companyIndexHelper.hasIndex(
				newIndexName, restHighLevelClient.indices())) {

			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info("Creating next index " + newIndexName);
		}

		_companyIndexHelper.createIndex(
			companyId, newIndexName, restHighLevelClient.indices());

		_companyLocalService.updateIndexNameNext(companyId, newIndexName);
	}

	@Override
	public void deleteNextIndex(long companyId) {
		Company company = _companyLocalService.fetchCompany(companyId);

		if (company == null) {
			return;
		}

		String indexName = company.getIndexNameNext();

		if (!Validator.isBlank(indexName)) {
			RestHighLevelClient restHighLevelClient =
				_elasticsearchConnectionManager.getRestHighLevelClient();

			if (_log.isInfoEnabled()) {
				_log.info("Deleting next index " + indexName);
			}

			_companyIndexHelper.deleteIndex(
				companyId, indexName, restHighLevelClient.indices(), false);
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

		RestHighLevelClient restHighLevelClient =
			_elasticsearchConnectionManager.getRestHighLevelClient();

		IndicesClient indicesClient = restHighLevelClient.indices();

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

		_updateAliases(baseIndexName, company, indicesClient);

		_companyLocalService.updateIndexNames(
			companyId, company.getIndexNameNext(), null);

		if (crossClusterReplicationHelper != null) {
			crossClusterReplicationHelper.follow(company.getIndexNameNext());
		}
	}

	private Set<String> _getBaseIndexAliasIndexNames(
			String baseIndexName, IndicesClient indicesClient)
		throws Exception {

		Set<String> baseIndexAliasIndexNames = new HashSet<>();

		GetAliasesResponse getAliasesResponse = indicesClient.getAlias(
			new GetAliasesRequest(baseIndexName), RequestOptions.DEFAULT);

		Map<String, Set<AliasMetadata>> aliases =
			getAliasesResponse.getAliases();

		if (MapUtil.isNotEmpty(aliases)) {
			baseIndexAliasIndexNames.addAll(aliases.keySet());
		}

		return baseIndexAliasIndexNames;
	}

	private Set<String> _getRemoveIndexNames(
			String baseIndexName, IndicesClient indicesClient)
		throws Exception {

		Set<String> removeIndexNames = _getBaseIndexAliasIndexNames(
			baseIndexName, indicesClient);

		if (removeIndexNames.isEmpty() &&
			_companyIndexHelper.hasIndex(baseIndexName, indicesClient)) {

			removeIndexNames.add(baseIndexName);
		}

		return removeIndexNames;
	}

	private void _updateAliases(
			String baseIndexName, Company company, IndicesClient indicesClient)
		throws Exception {

		IndicesAliasesRequest indicesAliasesRequest =
			new IndicesAliasesRequest();

		Set<String> removeIndexNames = _getRemoveIndexNames(
			baseIndexName, indicesClient);

		if (!removeIndexNames.isEmpty()) {
			if (_log.isInfoEnabled()) {
				_log.info("Removing indexes " + removeIndexNames);
			}

			indicesAliasesRequest.addAliasAction(
				new IndicesAliasesRequest.AliasActions(
					IndicesAliasesRequest.AliasActions.Type.REMOVE_INDEX
				).indices(
					ArrayUtil.toStringArray(removeIndexNames)
				));
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Adding alias ", baseIndexName, " for index ",
					company.getIndexNameNext()));
		}

		indicesAliasesRequest.addAliasAction(
			new IndicesAliasesRequest.AliasActions(
				IndicesAliasesRequest.AliasActions.Type.ADD
			).alias(
				baseIndexName
			).index(
				company.getIndexNameNext()
			));

		indicesClient.updateAliases(
			indicesAliasesRequest, RequestOptions.DEFAULT);
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
	private ElasticsearchConnectionManager _elasticsearchConnectionManager;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

}