/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.object.definition.util.ObjectDefinitionThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = ModelListener.class)
public class ObjectFieldModelListener extends BaseModelListener<ObjectField> {

	@Override
	public void onAfterCreate(ObjectField objectField)
		throws ModelListenerException {

		if (objectField.isRequired()) {
			_reindexObjectEntries(objectField);
		}
	}

	@Override
	public void onAfterRemove(ObjectField objectField)
		throws ModelListenerException {

		if (objectField.isRequired() &&
			!ObjectDefinitionThreadLocal.isDeleteObjectDefinitionId(
				objectField.getObjectDefinitionId())) {

			_reindexObjectEntries(objectField);
		}
	}

	@Override
	public void onAfterUpdate(
			ObjectField originalObjectField, ObjectField objectField)
		throws ModelListenerException {

		if (originalObjectField.isRequired() != objectField.isRequired()) {
			_reindexObjectEntries(objectField);
		}
	}

	private ObjectDefinition _getRootObjectDefinition(ObjectField objectField) {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectField.getObjectDefinitionId());

		if ((objectDefinition == null) ||
			!objectDefinition.isRootDescendantNode()) {

			return objectDefinition;
		}

		return _objectDefinitionLocalService.fetchObjectDefinition(
			objectDefinition.getRootObjectDefinitionId());
	}

	private void _reindexObjectEntries(ObjectField objectField) {
		if (!FeatureFlagManagerUtil.isEnabled(
				objectField.getCompanyId(), "LPD-82226")) {

			return;
		}

		ObjectDefinition objectDefinition = _getRootObjectDefinition(
			objectField);

		if ((objectDefinition == null) || !objectDefinition.isApproved() ||
			!objectDefinition.isCMS()) {

			return;
		}

		try {
			_indexWriterHelper.reindex(
				objectField.getUserId(), "reindex",
				new long[] {objectDefinition.getCompanyId()},
				objectDefinition.getClassName(),
				HashMapBuilder.<String, Serializable>put(
					ReindexBackgroundTaskConstants.EXECUTION_MODE, "sync"
				).build());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to reindex the object entries of " +
						objectDefinition.getClassName(),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectFieldModelListener.class);

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}