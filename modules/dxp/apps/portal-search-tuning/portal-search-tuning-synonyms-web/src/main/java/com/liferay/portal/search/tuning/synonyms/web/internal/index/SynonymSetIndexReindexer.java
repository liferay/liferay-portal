/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.index;

import com.liferay.json.storage.service.JSONStorageEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSenderUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.SyncReindexManager;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = IndexReindexer.class)
public class SynonymSetIndexReindexer implements IndexReindexer {

	@Override
	public void reindex(long companyId, ExecutionMode executionMode)
		throws Exception {

		if (!searchCapabilities.isSynonymsSupported() ||
			(companyId == CompanyConstants.SYSTEM)) {

			return;
		}

		SynonymSetIndexName synonymSetIndexName =
			synonymSetIndexNameBuilder.getSynonymSetIndexName(companyId);

		Date date = null;

		if (_isExecuteSyncReindex(executionMode)) {
			date = new Date();

			Thread.sleep(1000);
		}
		else {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Deleting and creating index " +
						synonymSetIndexName.getIndexName());
			}

			try {
				synchronized (synonymSetIndexNameBuilder) {
					_synonymSetIndexCreator.deleteIfExists(synonymSetIndexName);

					_synonymSetIndexCreator.create(synonymSetIndexName);
				}
			}
			catch (RuntimeException runtimeException) {
				_log.error(
					"Unable to delete or create index " +
						synonymSetIndexName.getIndexName(),
					runtimeException);

				return;
			}
		}

		List<Long> classPKs = jsonStorageEntryLocalService.getClassPKs(
			companyId, classNameLocalService.getClassNameId(SynonymSet.class),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int sendStatusInterval = Math.max(100, classPKs.size() / 20);

		for (int i = 0; i < classPKs.size(); i++) {
			_synonymSetIndexWriter.create(
				synonymSetIndexName, _buildSynonymSet(classPKs.get(i)));

			if ((i % sendStatusInterval) == 0) {
				ReindexStatusMessageSenderUtil.sendStatusMessage(
					SynonymSetIndexReindexer.class.getName(), i + 1,
					classPKs.size());
			}
		}

		if (_isExecuteSyncReindex(executionMode)) {
			SyncReindexManager syncReindexManager =
				_syncReindexManagerSnapshot.get();

			syncReindexManager.deleteStaleDocuments(
				synonymSetIndexName.getIndexName(), date,
				Collections.emptySet());
		}
	}

	@Activate
	protected void activate() {
		_synonymSetIndexCreator = new SynonymSetIndexCreator(
			_searchEngineAdapter);
		_synonymSetIndexWriter = new SynonymSetIndexWriter(
			_searchEngineAdapter);
	}

	@Reference
	protected ClassNameLocalService classNameLocalService;

	@Reference
	protected JSONStorageEntryLocalService jsonStorageEntryLocalService;

	@Reference
	protected SearchCapabilities searchCapabilities;

	@Reference
	protected SynonymSetIndexNameBuilder synonymSetIndexNameBuilder;

	private SynonymSet _buildSynonymSet(long classPK) {
		JSONObject jsonObject = jsonStorageEntryLocalService.getJSONObject(
			classNameLocalService.getClassNameId(SynonymSet.class), classPK);

		SynonymSet.SynonymSetBuilder synonymSetBuilder =
			new SynonymSet.SynonymSetBuilder();

		synonymSetBuilder.synonymSetDocumentId(
			jsonObject.getString("synonymSetDocumentId")
		).synonyms(
			jsonObject.getString("synonyms")
		);

		return synonymSetBuilder.build();
	}

	private boolean _isExecuteSyncReindex(ExecutionMode executionMode) {
		if ((_syncReindexManagerSnapshot.get() != null) &&
			(executionMode != null) &&
			executionMode.equals(ExecutionMode.SYNC)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SynonymSetIndexReindexer.class);

	private static final Snapshot<SyncReindexManager>
		_syncReindexManagerSnapshot = new Snapshot<>(
			SynonymSetIndexReindexer.class, SyncReindexManager.class, null,
			true);

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	private SynonymSetIndexCreator _synonymSetIndexCreator;
	private SynonymSetIndexWriter _synonymSetIndexWriter;

}