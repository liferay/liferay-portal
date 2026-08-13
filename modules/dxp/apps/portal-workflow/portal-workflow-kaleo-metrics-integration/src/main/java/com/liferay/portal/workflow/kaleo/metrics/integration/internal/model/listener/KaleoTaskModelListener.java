/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.metrics.integration.internal.model.listener;

import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.workflow.kaleo.metrics.integration.helper.IndexerHelper;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.metrics.model.DeleteNodeRequest;
import com.liferay.portal.workflow.metrics.search.index.NodeWorkflowMetricsIndexer;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(service = ModelListener.class)
public class KaleoTaskModelListener extends BaseKaleoModelListener<KaleoTask> {

	@Override
	public void onAfterCreate(KaleoTask kaleoTask) {
		KaleoDefinitionVersion kaleoDefinitionVersion =
			getKaleoDefinitionVersion(kaleoTask.getKaleoDefinitionVersionId());

		if (Objects.isNull(kaleoDefinitionVersion)) {
			return;
		}

		_nodeWorkflowMetricsIndexer.addNode(
			_indexerHelper.createAddNodeRequest(
				kaleoDefinitionVersion, kaleoTask));
	}

	@Override
	public void onAfterRemove(KaleoTask kaleoTask) {
		DeleteNodeRequest.Builder builder = new DeleteNodeRequest.Builder();

		_nodeWorkflowMetricsIndexer.deleteNode(
			builder.companyId(
				kaleoTask.getCompanyId()
			).nodeId(
				kaleoTask.getKaleoTaskId()
			).build());
	}

	@Reference
	private IndexerHelper _indexerHelper;

	@Reference
	private NodeWorkflowMetricsIndexer _nodeWorkflowMetricsIndexer;

}