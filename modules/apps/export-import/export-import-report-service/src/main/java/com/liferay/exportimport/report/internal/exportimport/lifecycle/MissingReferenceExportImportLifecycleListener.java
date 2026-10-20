/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.exportimport.lifecycle;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleEvent;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleListener;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.StagedGroupedModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.TransientValue;
import com.liferay.staging.StagingGroupHelper;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = ExportImportLifecycleListener.class)
public class MissingReferenceExportImportLifecycleListener
	implements ExportImportLifecycleListener {

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public void onExportImportLifecycleEvent(
			ExportImportLifecycleEvent exportImportLifecycleEvent)
		throws Exception {

		if (exportImportLifecycleEvent.getCode() !=
				ExportImportLifecycleConstants.
					EVENT_STAGED_MODEL_IMPORT_SUCCEEDED) {

			return;
		}

		for (Serializable attribute :
				exportImportLifecycleEvent.getAttributes()) {

			if (!(attribute instanceof TransientValue)) {
				continue;
			}

			TransientValue<?> transientValue = (TransientValue<?>)attribute;

			Object value = transientValue.getValue();

			if (!(value instanceof ExternalReferenceCodeModel) ||
				!(value instanceof StagedGroupedModel)) {

				continue;
			}

			ExternalReferenceCodeModel externalReferenceCodeModel =
				(ExternalReferenceCodeModel)value;

			StagedGroupedModel stagedGroupedModel = (StagedGroupedModel)value;

			long groupId = stagedGroupedModel.getGroupId();

			if (_stagingGroupHelper.isCompanyGroup(
					stagedGroupedModel.getCompanyId(), groupId)) {

				groupId = 0;
			}

			_emptyModelManager.resolveMissingReference(
				_getClassName(stagedGroupedModel),
				stagedGroupedModel.getCompanyId(),
				externalReferenceCodeModel.getExternalReferenceCode(), groupId);
		}
	}

	private String _getClassName(StagedModel stagedModel) {
		if (stagedModel instanceof FileEntry) {
			return FileEntry.class.getName();
		}

		return stagedModel.getModelClassName();
	}

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}