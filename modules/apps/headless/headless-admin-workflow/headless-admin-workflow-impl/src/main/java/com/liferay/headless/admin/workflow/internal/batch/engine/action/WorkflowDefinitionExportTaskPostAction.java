/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.batch.engine.action;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.action.ExportTaskPostAction;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinition;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionFileException;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.definition.parser.WorkflowModelParser;
import com.liferay.portal.workflow.kaleo.definition.util.WorkflowDefinitionContentUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Leite
 */
@Component(service = ExportTaskPostAction.class)
public class WorkflowDefinitionExportTaskPostAction
	implements ExportTaskPostAction {

	@Override
	public void run(
			BatchEngineExportTask batchEngineExportTask,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			Object item)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				batchEngineExportTask.getCompanyId(), "LPD-49856") ||
			!ExportImportThreadLocal.isExportInProcess() ||
			!(item instanceof WorkflowDefinition)) {

			return;
		}

		WorkflowDefinition workflowDefinition = (WorkflowDefinition)item;

		String content = workflowDefinition.getContent();

		if (Validator.isNull(content)) {
			return;
		}

		try {
			_workflowModelParser.setValidate(false);

			Definition definition = _workflowModelParser.parse(
				batchEngineExportTask.getCompanyId(),
				new UnsyncByteArrayInputStream(content.getBytes()));

			workflowDefinition.setContent(
				() -> WorkflowDefinitionContentUtil.toJSON(
					definition.getContent()));
		}
		catch (WorkflowDefinitionFileException
					workflowDefinitionFileException) {

			if (_log.isDebugEnabled()) {
				_log.debug(workflowDefinitionFileException);
			}
		}
		finally {
			_workflowModelParser.setValidate(true);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowDefinitionExportTaskPostAction.class);

	@Reference
	private WorkflowModelParser _workflowModelParser;

}