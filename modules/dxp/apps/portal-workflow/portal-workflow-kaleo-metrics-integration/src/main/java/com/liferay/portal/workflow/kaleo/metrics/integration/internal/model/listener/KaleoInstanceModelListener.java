/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.metrics.integration.internal.model.listener;

import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.workflow.kaleo.metrics.integration.helper.IndexerHelper;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.metrics.search.index.InstanceWorkflowMetricsIndexer;

import java.time.Duration;

import java.util.Date;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(service = ModelListener.class)
public class KaleoInstanceModelListener
	extends BaseKaleoModelListener<KaleoInstance> {

	@Override
	public void onAfterCreate(KaleoInstance kaleoInstance) {
		KaleoDefinitionVersion kaleoDefinitionVersion =
			getKaleoDefinitionVersion(
				kaleoInstance.getKaleoDefinitionVersionId());

		if (Objects.isNull(kaleoDefinitionVersion)) {
			return;
		}

		_instanceWorkflowMetricsIndexer.addInstance(
			_indexerHelper.createAssetTitleLocalizationMap(
				kaleoInstance.getClassName(), kaleoInstance.getClassPK(),
				kaleoInstance.getGroupId()),
			_indexerHelper.createAssetTypeLocalizationMap(
				kaleoInstance.getClassName(), kaleoInstance.getGroupId()),
			kaleoInstance.getClassName(), kaleoInstance.getClassPK(),
			kaleoInstance.getCompanyId(), null, kaleoInstance.getCreateDate(),
			kaleoInstance.getKaleoInstanceId(), kaleoInstance.getModifiedDate(),
			kaleoInstance.getKaleoDefinitionId(),
			kaleoDefinitionVersion.getVersion(), kaleoInstance.getUserId(),
			kaleoInstance.getUserName());
	}

	@Override
	public void onAfterRemove(KaleoInstance kaleoInstance) {
		_instanceWorkflowMetricsIndexer.deleteInstance(
			kaleoInstance.getCompanyId(), kaleoInstance.getKaleoInstanceId());
	}

	@Override
	public void onBeforeUpdate(
		KaleoInstance originalKaleoInstance, KaleoInstance kaleoInstance) {

		KaleoInstance currentKaleoInstance =
			_kaleoInstanceLocalService.fetchKaleoInstance(
				kaleoInstance.getKaleoInstanceId());

		if (!currentKaleoInstance.isCompleted() &&
			kaleoInstance.isCompleted()) {

			Date createDate = kaleoInstance.getCreateDate();
			Date completionDate = kaleoInstance.getCompletionDate();

			Duration duration = Duration.between(
				createDate.toInstant(), completionDate.toInstant());

			_instanceWorkflowMetricsIndexer.completeInstance(
				kaleoInstance.getCompanyId(), kaleoInstance.getCompletionDate(),
				duration.toMillis(), kaleoInstance.getKaleoInstanceId(),
				kaleoInstance.getModifiedDate());
		}
		else {
			_instanceWorkflowMetricsIndexer.updateInstance(
				kaleoInstance.isActive(),
				_indexerHelper.createAssetTitleLocalizationMap(
					kaleoInstance.getClassName(), kaleoInstance.getClassPK(),
					kaleoInstance.getGroupId()),
				_indexerHelper.createAssetTypeLocalizationMap(
					kaleoInstance.getClassName(), kaleoInstance.getGroupId()),
				kaleoInstance.getCompanyId(),
				kaleoInstance.getKaleoInstanceId(),
				kaleoInstance.getModifiedDate());
		}
	}

	@Reference
	private IndexerHelper _indexerHelper;

	@Reference
	private InstanceWorkflowMetricsIndexer _instanceWorkflowMetricsIndexer;

	@Reference
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

}