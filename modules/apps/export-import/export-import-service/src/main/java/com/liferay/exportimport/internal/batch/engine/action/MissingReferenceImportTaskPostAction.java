/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.batch.engine.action;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.action.ImportTaskPostAction;
import com.liferay.batch.engine.context.ImportTaskContext;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.exportimport.internal.lar.ExportImportDescriptorThreadLocal;
import com.liferay.exportimport.internal.lar.PortletDataContextThreadLocal;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.staging.StagingGroupHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = ImportTaskPostAction.class)
public class MissingReferenceImportTaskPostAction
	implements ImportTaskPostAction {

	@Override
	public void run(
		BatchEngineImportTask batchEngineImportTask,
		BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
		ImportTaskContext importTaskContext, Object item,
		Object persistedItem) {

		if (!ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor<?>
			exportImportDescriptor =
				ExportImportDescriptorThreadLocal.getExportImportDescriptor();

		if (exportImportDescriptor == null) {
			return;
		}

		PortletDataContext portletDataContext =
			PortletDataContextThreadLocal.getPortletDataContext();

		if (portletDataContext == null) {
			return;
		}

		String externalReferenceCode = _beanProperties.getStringSilent(
			persistedItem, "externalReferenceCode");

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		long groupId = portletDataContext.getScopeGroupId();

		if (_stagingGroupHelper.isCompanyGroup(
				portletDataContext.getCompanyId(), groupId)) {

			groupId = 0;
		}

		_emptyModelManager.resolveMissingReference(
			exportImportDescriptor.getModelClassName(),
			portletDataContext.getCompanyId(), externalReferenceCode, groupId);
	}

	@Reference
	private BeanProperties _beanProperties;

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}