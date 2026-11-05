/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;

/**
 * @author Feliphe Marinho
 * @author Gabriel Albuquerque
 */
public class ObjectEntryBatchReindexer {

	public ObjectEntryBatchReindexer(
		ObjectEntryLocalService objectEntryLocalService,
		ObjectDefinition objectDefinition,
		ObjectField accountEntryRestrictedObjectField) {

		_objectEntryLocalService = objectEntryLocalService;
		_objectDefinition = objectDefinition;
		_accountEntryRestrictedObjectField = accountEntryRestrictedObjectField;
	}

	public String getClassName() {
		return _objectDefinition.getClassName();
	}

	public long getObjectEntriesCount(long accountEntryId, long companyId) {
		DynamicQuery dynamicQuery = _objectEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("companyId", companyId));

		_addCriteria(accountEntryId, dynamicQuery);

		return _objectEntryLocalService.dynamicQueryCount(dynamicQuery);
	}

	public void reindex(
		IndexerDocumentBuilder indexerDocumentBuilder, long accountEntryId,
		long companyId) {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_objectEntryLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> _addCriteria(accountEntryId, dynamicQuery));
		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			indexerDocumentBuilder::getDocument);

		indexableActionableDynamicQuery.performActions();
	}

	private void _addCriteria(long accountEntryId, DynamicQuery dynamicQuery) {
		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectDefinitionId",
				_objectDefinition.getObjectDefinitionId()));
		dynamicQuery.add(
			RestrictionsFactoryUtil.eqProperty(
				"objectEntryId", "headObjectEntryId"));
		dynamicQuery.add(
			RestrictionsFactoryUtil.sqlRestriction(
				StringBundler.concat(
					"this_.objectEntryId in (select ",
					_objectDefinition.getPKObjectFieldDBColumnName(), " from ",
					_accountEntryRestrictedObjectField.getDBTableName(),
					" where ",
					_accountEntryRestrictedObjectField.getDBColumnName(), " = ",
					accountEntryId, ")")));
	}

	private final ObjectField _accountEntryRestrictedObjectField;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryLocalService _objectEntryLocalService;

}