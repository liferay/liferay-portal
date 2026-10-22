/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.exportimport.lifecycle;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleEvent;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleListener;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.TransientValue;
import com.liferay.staging.StagingGroupHelper;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

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

		List<Serializable> attributes =
			exportImportLifecycleEvent.getAttributes();

		PortletDataContext portletDataContext = _getPortletDataContext(
			attributes);

		if (portletDataContext == null) {
			return;
		}

		for (Serializable attribute : attributes) {
			if (!(attribute instanceof TransientValue)) {
				continue;
			}

			TransientValue<?> transientValue = (TransientValue<?>)attribute;

			Object value = transientValue.getValue();

			if (!(value instanceof ExternalReferenceCodeModel) ||
				!(value instanceof GroupedModel) ||
				!(value instanceof StagedModel)) {

				continue;
			}

			GroupedModel groupedModel = (GroupedModel)value;

			Map<Long, Long> groupIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					Group.class);

			long groupId = MapUtil.getLong(
				groupIds, groupedModel.getGroupId(), groupedModel.getGroupId());

			if (_stagingGroupHelper.isCompanyGroup(
					portletDataContext.getCompanyId(), groupId)) {

				groupId = 0;
			}

			ExternalReferenceCodeModel externalReferenceCodeModel =
				(ExternalReferenceCodeModel)value;

			_emptyModelManager.resolveMissingReference(
				_getClassName((StagedModel)value),
				portletDataContext.getCompanyId(),
				externalReferenceCodeModel.getExternalReferenceCode(), groupId);
		}
	}

	private String _getClassName(StagedModel stagedModel) {
		if (stagedModel instanceof FileEntry) {
			return FileEntry.class.getName();
		}

		return stagedModel.getModelClassName();
	}

	private PortletDataContext _getPortletDataContext(
		List<Serializable> attributes) {

		for (Serializable attribute : attributes) {
			if (attribute instanceof PortletDataContext) {
				return (PortletDataContext)attribute;
			}
		}

		return null;
	}

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}