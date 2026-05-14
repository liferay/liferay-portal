/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.metrics.integration.helper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.metrics.model.AddNodeRequest;
import com.liferay.portal.workflow.metrics.model.AddProcessRequest;
import com.liferay.portal.workflow.metrics.model.AddTaskRequest;
import com.liferay.portal.workflow.metrics.model.AddTransitionRequest;
import com.liferay.portal.workflow.metrics.model.Assignment;
import com.liferay.portal.workflow.metrics.model.DeleteProcessRequest;
import com.liferay.portal.workflow.metrics.model.DeleteTransitionRequest;
import com.liferay.portal.workflow.metrics.model.UpdateProcessRequest;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Felipe Lorenz
 */
public interface IndexerHelper {

	public AddNodeRequest createAddNodeRequest(
		KaleoDefinitionVersion kaleoDefinitionVersion, KaleoNode kaleoNode);

	public AddNodeRequest createAddNodeRequest(
		KaleoDefinitionVersion kaleoDefinitionVersion, KaleoTask kaleoTask);

	public AddProcessRequest createAddProcessRequest(
		long companyId, KaleoDefinition kaleoDefinition);

	public AddTaskRequest createAddTaskRequest(
		KaleoInstance kaleoInstance,
		KaleoTaskInstanceToken kaleoTaskInstanceToken, String processVersion);

	public AddTransitionRequest createAddTransitionRequest(
			KaleoTransition kaleoTransition, String processVersion)
		throws PortalException;

	public Map<Locale, String> createAssetTitleLocalizationMap(
		String className, long classPK, long groupId);

	public Map<Locale, String> createAssetTypeLocalizationMap(
		String className, long groupId);

	public DeleteProcessRequest createDeleteProcessRequest(
		KaleoDefinition kaleoDefinition);

	public DeleteTransitionRequest createDeleteTransitionRequest(
		KaleoTransition kaleoTransition);

	public UpdateProcessRequest createUpdateProcessRequest(
		KaleoDefinition kaleoDefinition);

	public List<Assignment> toAssignments(
		List<KaleoTaskAssignmentInstance> kaleoTaskAssignmentInstances);

}