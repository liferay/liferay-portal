/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.index.contributor;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntryModelIndexerWriterContributor
	implements ModelIndexerWriterContributor<ObjectEntry> {

	public ObjectEntryModelIndexerWriterContributor(
		DynamicQueryBatchIndexingActionableFactory
			dynamicQueryBatchIndexingActionableFactory,
		long objectDefinitionId,
		ObjectEntryLocalService objectEntryLocalService) {

		_dynamicQueryBatchIndexingActionableFactory =
			dynamicQueryBatchIndexingActionableFactory;
		_objectDefinitionId = objectDefinitionId;
		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	public void customize(
		BatchIndexingActionable batchIndexingActionable,
		ModelIndexerWriterDocumentHelper modelIndexerWriterDocumentHelper) {

		batchIndexingActionable.setAddCriteriaMethod(
			dynamicQuery -> {
				Property objectDefinitionIdProperty =
					PropertyFactoryUtil.forName("objectDefinitionId");

				dynamicQuery.add(
					objectDefinitionIdProperty.eq(_objectDefinitionId));
			});
		batchIndexingActionable.setPerformActionMethod(
			(ObjectEntry objectEntry) -> batchIndexingActionable.addDocuments(
				modelIndexerWriterDocumentHelper.getDocument(objectEntry)));
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		return _dynamicQueryBatchIndexingActionableFactory.
			getBatchIndexingActionable(
				_objectEntryLocalService.getIndexableActionableDynamicQuery());
	}

	@Override
	public long getCompanyId(ObjectEntry objectEntry) {
		return objectEntry.getCompanyId();
	}

	@Override
	public IndexerWriterMode getIndexerWriterMode(ObjectEntry objectEntry) {
		return IndexerWriterMode.UPDATE;
	}

	private final DynamicQueryBatchIndexingActionableFactory
		_dynamicQueryBatchIndexingActionableFactory;
	private final Long _objectDefinitionId;
	private final ObjectEntryLocalService _objectEntryLocalService;

}