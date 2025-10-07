/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.indexer;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollectionModel;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.batch.BatchIndexingHelper;
import com.liferay.portal.search.index.IndexStatusManager;
import com.liferay.portal.search.index.UpdateDocumentIndexWriter;
import com.liferay.portal.search.indexer.BaseModelRetriever;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.indexer.IndexerWriter;
import com.liferay.portal.search.internal.index.contributor.helper.ModelIndexerWriterDocumentHelperImpl;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.permission.SearchPermissionIndexWriter;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.util.Collection;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class IndexerWriterImpl<T extends BaseModel<?>>
	implements IndexerWriter<T> {

	public IndexerWriterImpl(
		ModelSearchSettings modelSearchSettings,
		BaseModelRetriever baseModelRetriever,
		BatchIndexingHelper batchIndexingHelper,
		CTCollectionLocalService ctCollectionLocalService,
		ModelIndexerWriterContributor<T> modelIndexerWriterContributor,
		IndexerDocumentBuilder indexerDocumentBuilder,
		SearchPermissionIndexWriter searchPermissionIndexWriter,
		UpdateDocumentIndexWriter updateDocumentIndexWriter,
		IndexStatusManager indexStatusManager,
		IndexWriterHelper indexWriterHelper, UIDFactory uidFactory) {

		_modelSearchSettings = modelSearchSettings;
		_baseModelRetriever = baseModelRetriever;
		_batchIndexingHelper = batchIndexingHelper;
		_ctCollectionLocalService = ctCollectionLocalService;
		_modelIndexerWriterContributor = modelIndexerWriterContributor;
		_indexerDocumentBuilder = indexerDocumentBuilder;
		_searchPermissionIndexWriter = searchPermissionIndexWriter;
		_updateDocumentIndexWriter = updateDocumentIndexWriter;
		_indexStatusManager = indexStatusManager;
		_indexWriterHelper = indexWriterHelper;
		_uidFactory = uidFactory;
	}

	@Override
	public void delete(long companyId, String uid) {
		if (!isEnabled()) {
			return;
		}

		try {
			_indexWriterHelper.deleteDocument(companyId, uid, false);
		}
		catch (SearchException searchException) {
			throw new RuntimeException(searchException);
		}
	}

	@Override
	public void delete(T baseModel) {
		if (baseModel == null) {
			return;
		}

		delete(
			_modelIndexerWriterContributor.getCompanyId(baseModel),
			_uidFactory.getUID(baseModel));

		_modelIndexerWriterContributor.modelDeleted(baseModel);
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		BatchIndexingActionable batchIndexingActionable =
			_modelIndexerWriterContributor.getBatchIndexingActionable();

		batchIndexingActionable.setInterval(
			_batchIndexingHelper.getBulkSize(
				_modelSearchSettings.getClassName()));

		return batchIndexingActionable;
	}

	@Override
	public boolean isEnabled() {
		if (_indexerEnabled == null) {
			String indexerEnabled = PropsUtil.get(
				PropsKeys.INDEXER_ENABLED,
				new Filter(_modelSearchSettings.getClassName()));

			_indexerEnabled = GetterUtil.getBoolean(indexerEnabled, true);

			return _indexerEnabled;
		}

		if (_indexStatusManager.isIndexReadOnly() ||
			_indexStatusManager.isIndexReadOnly(
				_modelSearchSettings.getClassName()) ||
			!_indexerEnabled) {

			return false;
		}

		return true;
	}

	@Override
	public void reindex(Collection<T> baseModels) {
		if (!isEnabled() || (baseModels == null) || baseModels.isEmpty()) {
			return;
		}

		for (T baseModel : baseModels) {
			reindex(baseModel);
		}
	}

	@Override
	public void reindex(long classPK) {
		if (!isEnabled() || (classPK <= 0)) {
			return;
		}

		BaseModel<?> baseModel = _baseModelRetriever.fetchBaseModel(
			_modelSearchSettings.getClassName(), classPK);

		if (baseModel == null) {
			return;
		}

		reindex((T)baseModel);
	}

	@Override
	public void reindex(String[] ids) {
		if (!isEnabled() || ArrayUtil.isEmpty(ids)) {
			return;
		}

		long[] companyIds = new long[ids.length];

		for (int i = 0; i < ids.length; i++) {
			companyIds[i] = GetterUtil.getLong(ids[i]);
		}

		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				for (long ctCollectionId : _getCTCollectionIds(companyId)) {
					try (SafeCloseable safeCloseable1 =
							CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(
								CTSQLModeThreadLocal.CTSQLMode.CT_ONLY);
						SafeCloseable safeCloseable2 =
							CTCollectionThreadLocal.
								setCTCollectionIdWithSafeCloseable(
									ctCollectionId)) {

						BatchIndexingActionable batchIndexingActionable =
							getBatchIndexingActionable();

						batchIndexingActionable.setCompanyId(companyId);

						_modelIndexerWriterContributor.customize(
							batchIndexingActionable,
							new ModelIndexerWriterDocumentHelperImpl(
								_modelSearchSettings.getClassName(),
								_indexerDocumentBuilder));

						try {
							batchIndexingActionable.performActions();
						}
						catch (Exception exception) {
							if (_log.isWarnEnabled()) {
								_log.warn(
									StringBundler.concat(
										"Unable to reindex ",
										_modelSearchSettings.getClassName(),
										" for change tracking collection ID ",
										ctCollectionId, " and company ID ",
										companyId),
									exception);
							}
						}
					}
				}
			},
			companyIds);
	}

	@Override
	public void reindex(T baseModel) {
		reindex(baseModel, true);
	}

	@Override
	public void reindex(T baseModel, boolean notify) {
		if (!isEnabled() || (baseModel == null)) {
			return;
		}

		IndexerWriterMode indexerWriterMode = _getIndexerWriterMode(baseModel);

		if ((indexerWriterMode == IndexerWriterMode.UPDATE) ||
			(indexerWriterMode == IndexerWriterMode.PARTIAL_UPDATE)) {

			Document document = _indexerDocumentBuilder.getDocument(baseModel);

			_updateDocumentIndexWriter.updateDocument(
				_modelIndexerWriterContributor.getCompanyId(baseModel),
				document);
		}
		else if (indexerWriterMode == IndexerWriterMode.DELETE) {
			long companyId = _modelIndexerWriterContributor.getCompanyId(
				baseModel);
			String uid = _indexerDocumentBuilder.getDocumentUID(baseModel);

			delete(companyId, uid);
		}
		else if (indexerWriterMode == IndexerWriterMode.SKIP) {
			if (_log.isDebugEnabled()) {
				_log.debug("Skipping model " + baseModel);
			}
		}

		if (notify) {
			_modelIndexerWriterContributor.modelIndexed(baseModel);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		_indexerEnabled = enabled;
	}

	@Override
	public void updatePermissionFields(T baseModel) {
		_searchPermissionIndexWriter.updatePermissionFields(
			baseModel, _modelIndexerWriterContributor.getCompanyId(baseModel),
			false);
	}

	private List<Long> _getCTCollectionIds(long companyId) {
		List<Long> ctCollectionIds = ListUtil.toList(
			_ctCollectionLocalService.getCTCollections(
				companyId,
				new int[] {
					WorkflowConstants.STATUS_DRAFT,
					WorkflowConstants.STATUS_SCHEDULED
				},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			CTCollectionModel::getCtCollectionId);

		ctCollectionIds.add(CTConstants.CT_COLLECTION_ID_PRODUCTION);

		return ctCollectionIds;
	}

	private IndexerWriterMode _getIndexerWriterMode(T baseModel) {
		IndexerWriterMode indexerWriterMode =
			_modelIndexerWriterContributor.getIndexerWriterMode(baseModel);

		if (indexerWriterMode != null) {
			return indexerWriterMode;
		}

		if (baseModel instanceof TrashedModel &&
			baseModel instanceof WorkflowedModel) {

			TrashedModel trashedModel = (TrashedModel)baseModel;
			WorkflowedModel workflowedModel = (WorkflowedModel)baseModel;

			if (!workflowedModel.isApproved() && !trashedModel.isInTrash()) {
				return IndexerWriterMode.SKIP;
			}
		}

		return IndexerWriterMode.UPDATE;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexerWriterImpl.class);

	private final BaseModelRetriever _baseModelRetriever;
	private final BatchIndexingHelper _batchIndexingHelper;
	private final CTCollectionLocalService _ctCollectionLocalService;
	private final IndexerDocumentBuilder _indexerDocumentBuilder;
	private Boolean _indexerEnabled;
	private final IndexStatusManager _indexStatusManager;
	private final IndexWriterHelper _indexWriterHelper;
	private final ModelIndexerWriterContributor<T>
		_modelIndexerWriterContributor;
	private final ModelSearchSettings _modelSearchSettings;
	private final SearchPermissionIndexWriter _searchPermissionIndexWriter;
	private final UIDFactory _uidFactory;
	private final UpdateDocumentIndexWriter _updateDocumentIndexWriter;

}