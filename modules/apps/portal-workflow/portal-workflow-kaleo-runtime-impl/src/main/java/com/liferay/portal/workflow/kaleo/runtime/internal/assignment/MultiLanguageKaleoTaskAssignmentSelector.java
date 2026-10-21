/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.assignment;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.assignment.BaseKaleoTaskAssignmentSelector;
import com.liferay.portal.workflow.kaleo.runtime.assignment.KaleoTaskAssignmentSelector;
import com.liferay.portal.workflow.kaleo.runtime.assignment.ScriptingAssigneeSelector;
import com.liferay.portal.workflow.kaleo.runtime.internal.cache.KaleoTaskScriptedAssignmentCache;
import com.liferay.portal.workflow.kaleo.runtime.internal.configuration.WorkflowTaskScriptConfiguration;
import com.liferay.portal.workflow.kaleo.runtime.internal.util.ServiceSelectorUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;

import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "assignee.class.name=SCRIPT",
	service = KaleoTaskAssignmentSelector.class
)
public class MultiLanguageKaleoTaskAssignmentSelector
	extends BaseKaleoTaskAssignmentSelector {

	@Override
	public Collection<KaleoTaskAssignment> getKaleoTaskAssignments(
			KaleoTaskAssignment kaleoTaskAssignment,
			ExecutionContext executionContext)
		throws PortalException {

		ScriptingAssigneeSelector scriptingAssigneeSelector =
			ServiceSelectorUtil.getServiceByScriptLanguage(
				StringUtil.trim(kaleoTaskAssignment.getAssigneeScript()),
				kaleoTaskAssignment.getAssigneeScriptLanguage(),
				_serviceTrackerMap);

		if (scriptingAssigneeSelector == null) {
			throw new IllegalArgumentException(
				"No task assignment selector found for " +
					kaleoTaskAssignment.toString());
		}

		Collection<KaleoTaskAssignment> kaleoTaskAssignments = null;

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			executionContext.getKaleoTaskInstanceToken();

		WorkflowTaskScriptConfiguration workflowTaskScriptConfiguration =
			_configurationProvider.getSystemConfiguration(
				WorkflowTaskScriptConfiguration.class);

		int scriptedAssignmentCacheExpirationTime =
			workflowTaskScriptConfiguration.
				scriptedAssignmentCacheExpirationTime();

		if (scriptedAssignmentCacheExpirationTime > 0) {
			kaleoTaskAssignments =
				_kaleoTaskScriptedAssignmentCache.getKaleoTaskAssignments(
					kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());
		}

		if (kaleoTaskAssignments == null) {
			kaleoTaskAssignments = getKaleoTaskAssignments(
				scriptingAssigneeSelector.getAssignees(
					executionContext, kaleoTaskAssignment));

			if (scriptedAssignmentCacheExpirationTime > 0) {
				_kaleoTaskScriptedAssignmentCache.putKaleoTaskAssignments(
					kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId(),
					kaleoTaskAssignments,
					scriptedAssignmentCacheExpirationTime * 60);
			}
		}

		_kaleoInstanceLocalService.updateKaleoInstance(
			kaleoTaskInstanceToken.getKaleoInstanceId(),
			executionContext.getWorkflowContext());

		return kaleoTaskAssignments;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, ScriptingAssigneeSelector.class,
			"scripting.language");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Reference
	private KaleoTaskScriptedAssignmentCache _kaleoTaskScriptedAssignmentCache;

	private ServiceTrackerMap<String, List<ScriptingAssigneeSelector>>
		_serviceTrackerMap;

}