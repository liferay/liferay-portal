/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.kaleo.runtime.node;

import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.KaleoNodeSettingUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.OAuth2ApplicationHomePageURLResolverUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.VariablesUtil;
import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.graph.PathElement;
import com.liferay.portal.workflow.kaleo.runtime.node.BaseNodeExecutor;
import com.liferay.portal.workflow.kaleo.runtime.node.NodeExecutor;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Iliyan Peychev
 */
@Component(service = NodeExecutor.class)
public class ServiceNodeExecutor extends BaseNodeExecutor {

	@Override
	public NodeType getNodeType() {
		return NodeType.SERVICE;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ServiceNodeDelegate.class, null,
			(serviceReference, emitter) -> {
				ServiceNodeDelegate serviceNodeDelegate =
					bundleContext.getService(serviceReference);

				if (serviceNodeDelegate != null) {
					emitter.emit(serviceNodeDelegate.getKey());
				}
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Override
	protected boolean doEnter(
		KaleoNode currentKaleoNode, ExecutionContext executionContext) {

		return true;
	}

	@Override
	protected void doExecute(
			KaleoNode currentKaleoNode, ExecutionContext executionContext,
			List<PathElement> remainingPathElements)
		throws PortalException {

		Map<String, String> kaleoNodeSettingValues =
			KaleoNodeSettingUtil.getKaleoNodeSettingValuesMap(
				currentKaleoNode.getKaleoNodeId());

		String javaDelegate = kaleoNodeSettingValues.get("javaDelegate");

		ServiceNodeDelegate serviceNodeDelegate = _serviceTrackerMap.getService(
			javaDelegate);

		if (serviceNodeDelegate == null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"No service node delegate is registered for \"",
					javaDelegate, "\""));
		}

		Map<String, Serializable> workflowContext =
			executionContext.getWorkflowContext();

		workflowContext.put(
			"aiHubCellLiferayDXPURL",
			OAuth2ApplicationHomePageURLResolverUtil.resolve(
				MapUtil.getLong(workflowContext, "oAuth2ApplicationId")));

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		workflowContext.put(
			"workflowInstanceId", kaleoInstanceToken.getKaleoInstanceId());

		try {
			JSONArray jsonArray = VariablesUtil.getVariablesJSONArray(
				"outputVariables", kaleoNodeSettingValues);

			if ((jsonArray != null) && (jsonArray.length() > 0)) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);

				workflowContext.put(
					jsonObject.getString("name"),
					serviceNodeDelegate.execute(
						VariablesUtil.getInputVariables(
							kaleoNodeSettingValues, workflowContext),
						workflowContext));
			}
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to execute service node delegate \"", javaDelegate,
					"\" on node \"", currentKaleoNode.getName(), "\""),
				exception);

			throw new PortalException(exception);
		}
	}

	@Override
	protected void doExit(
			KaleoNode currentKaleoNode, ExecutionContext executionContext,
			List<PathElement> remainingPathElements)
		throws PortalException {

		KaleoTransition kaleoTransition = null;

		if (Validator.isNull(executionContext.getTransitionName())) {
			kaleoTransition = currentKaleoNode.getDefaultKaleoTransition();
		}
		else {
			kaleoTransition = currentKaleoNode.getKaleoTransition(
				executionContext.getTransitionName());
		}

		remainingPathElements.add(
			new PathElement(
				currentKaleoNode, kaleoTransition.getTargetKaleoNode(),
				new ExecutionContext(
					executionContext.getKaleoInstanceToken(),
					executionContext.getWorkflowContext(),
					executionContext.getServiceContext())));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceNodeExecutor.class);

	private ServiceTrackerMap<String, ServiceNodeDelegate> _serviceTrackerMap;

}