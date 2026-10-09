/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.model.listener;

import com.liferay.changeset.model.ChangesetCollection;
import com.liferay.changeset.model.ChangesetEntry;
import com.liferay.changeset.service.ChangesetCollectionLocalService;
import com.liferay.changeset.service.ChangesetEntryLocalService;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.model.listener.StagingModelListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(service = ModelListener.class)
public class JournalArticleStagingModelListener
	extends BaseModelListener<JournalArticle> {

	@Override
	public void onAfterCreate(JournalArticle journalArticle)
		throws ModelListenerException {

		if (!_isJournalArticleStaged(journalArticle)) {
			return;
		}

		_stagingModelListener.onAfterCreate(journalArticle);

		_addJournalArticleResourceToChangesetCollection(journalArticle);
	}

	@Override
	public void onAfterRemove(JournalArticle journalArticle)
		throws ModelListenerException {

		if (!_isJournalArticleStaged(journalArticle)) {
			return;
		}

		_stagingModelListener.onAfterRemove(journalArticle);

		_cleanUpJournalArticleResourceFromChangesetCollection(journalArticle);
	}

	@Override
	public void onAfterUpdate(
			JournalArticle originalJournalArticle,
			JournalArticle journalArticle)
		throws ModelListenerException {

		if (!_isJournalArticleStaged(journalArticle)) {
			return;
		}

		_stagingModelListener.onAfterUpdate(journalArticle);

		_addJournalArticleResourceToChangesetCollection(journalArticle);

		// Updates are happening when moving to trash, so clean up after add

		_cleanUpJournalArticleResourceFromChangesetCollection(journalArticle);
	}

	private void _addJournalArticleResourceToChangesetCollection(
		JournalArticle journalArticle) {

		if (journalArticle.getStatus() == WorkflowConstants.STATUS_IN_TRASH) {
			return;
		}

		try {
			ChangesetCollection changesetCollection =
				_changesetCollectionLocalService.fetchOrAddChangesetCollection(
					journalArticle.getGroupId(),
					StagingConstants.
						RANGE_FROM_LAST_PUBLISH_DATE_CHANGESET_NAME);

			long journalArticleClassNameId =
				_classNameLocalService.getClassNameId(
					journalArticle.getModelClass());

			ChangesetEntry changesetEntry =
				_changesetEntryLocalService.fetchChangesetEntry(
					changesetCollection.getChangesetCollectionId(),
					journalArticleClassNameId, journalArticle.getId());

			if (changesetEntry == null) {
				return;
			}

			JournalArticleResource journalArticleResource =
				journalArticle.getArticleResource();

			long journalArticleResourceClassNameId =
				_classNameLocalService.getClassNameId(
					journalArticleResource.getModelClass());

			_changesetEntryLocalService.fetchOrAddChangesetEntry(
				changesetCollection.getChangesetCollectionId(),
				journalArticleResourceClassNameId,
				journalArticleResource.getPrimaryKey());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to process journal article resource for article " +
						journalArticle.getArticleId(),
					portalException);
			}
		}
	}

	private void _cleanUpJournalArticleResourceFromChangesetCollection(
		JournalArticle journalArticle) {

		try {
			ChangesetCollection changesetCollection =
				_changesetCollectionLocalService.fetchOrAddChangesetCollection(
					journalArticle.getGroupId(),
					StagingConstants.
						RANGE_FROM_LAST_PUBLISH_DATE_CHANGESET_NAME);

			List<JournalArticle> journalArticleResourceArticles =
				_journalArticleLocalService.getArticlesByResourcePrimKey(
					journalArticle.getResourcePrimKey());

			Set<Long> classPKs = new HashSet<>();

			StagedModelDataHandler<JournalArticle> stagedModelDataHandler =
				(StagedModelDataHandler<JournalArticle>)
					StagedModelDataHandlerRegistryUtil.
						getStagedModelDataHandler(
							JournalArticle.class.getName());

			for (JournalArticle journalArticleResourceArticle :
					journalArticleResourceArticles) {

				if (!ArrayUtil.contains(
						stagedModelDataHandler.getExportableStatuses(),
						journalArticleResourceArticle.getStatus())) {

					continue;
				}

				classPKs.add(journalArticleResourceArticle.getId());
			}

			long journalArticleClassNameId =
				_classNameLocalService.getClassNameId(
					journalArticle.getModelClass());

			long journalArticleChangetEntriesCount =
				_changesetEntryLocalService.getChangesetEntriesCount(
					changesetCollection.getChangesetCollectionId(),
					journalArticleClassNameId, classPKs);

			if (journalArticleChangetEntriesCount == 0) {
				long journalArticleResourceClassNameId =
					_classNameLocalService.getClassNameId(
						JournalArticleResource.class);

				_changesetEntryLocalService.deleteEntry(
					changesetCollection.getChangesetCollectionId(),
					journalArticleResourceClassNameId,
					journalArticle.getResourcePrimKey());
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to process journal article resource for article " +
						journalArticle.getArticleId(),
					portalException);
			}
		}
	}

	private boolean _isJournalArticleStaged(JournalArticle journalArticle) {
		Group group = _groupLocalService.fetchGroup(
			journalArticle.getGroupId());

		if (group == null) {
			return false;
		}

		return _stagingGroupHelper.isStagingGroup(group);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleStagingModelListener.class);

	@Reference
	private ChangesetCollectionLocalService _changesetCollectionLocalService;

	@Reference
	private ChangesetEntryLocalService _changesetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	@Reference
	private StagingModelListener<JournalArticle> _stagingModelListener;

}