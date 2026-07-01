/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.node;

import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoNodeSetting;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.graph.PathElement;
import com.liferay.portal.workflow.kaleo.runtime.node.BaseNodeExecutor;
import com.liferay.portal.workflow.kaleo.runtime.node.NodeExecutor;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeSettingLocalService;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = NodeExecutor.class)
public class AIHubAgentNodeExecutor extends BaseNodeExecutor {

	@Override
	public NodeType getNodeType() {
		return NodeType.AI_HUB_AGENT;
	}

	@Activate
	protected void activate() {
		_noticeableExecutorService = _portalExecutorManager.getPortalExecutor(
			AIHubAgentNodeExecutor.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_noticeableExecutorService.shutdown();
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

		_noticeableExecutorService.submit(
			new CompanyInheritableThreadLocalCallable<>(
				() -> {
					try {
						_postAgentInstance(currentKaleoNode, executionContext);
					}
					catch (Exception exception) {
						_log.error(
							StringBundler.concat(
								"Unable to complete AI hub agent node \"",
								currentKaleoNode.getName(), "\""),
							exception);
					}

					return null;
				}));
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
				null, kaleoTransition.getTargetKaleoNode(),
				new ExecutionContext(
					executionContext.getKaleoInstanceToken(),
					executionContext.getWorkflowContext(),
					executionContext.getServiceContext())));
	}

	private JSONObject _getResponseBodyJSONObject(Http.Options options)
		throws Exception {

		String json = _http.URLtoString(options);

		Http.Response response = options.getResponse();

		if ((response.getResponseCode() < 200) ||
			(response.getResponseCode() >= 300)) {

			throw new PortalException(
				StringBundler.concat(
					"Request to ", options.getLocation(),
					" returned response code ", response.getResponseCode(),
					" with body ", json));
		}

		return _jsonFactory.createJSONObject(json);
	}

	private void _postAgentInstance(
			KaleoNode currentKaleoNode, ExecutionContext executionContext)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON);

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		JSONObject authorizationTokenJSONObject = _postAuthorizationToken(
			_companyLocalService.getCompany(kaleoInstanceToken.getCompanyId()));

		options.addHeader(
			HttpHeaders.AUTHORIZATION,
			"Bearer " + authorizationTokenJSONObject.getString("accessToken"));

		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		options.addHeader(
			"Liferay-AI-Hub-Cell-On-Behalf-Of",
			authorizationTokenJSONObject.getString("userToken"));
		options.addHeader("X-Liferay-Transaction-Disabled", "true");

		Map<String, String> kaleoNodeSettingValues = new HashMap<>();

		for (KaleoNodeSetting kaleoNodeSetting :
				_kaleoNodeSettingLocalService.getKaleoNodeSettings(
					currentKaleoNode.getKaleoNodeId())) {

			kaleoNodeSettingValues.put(
				kaleoNodeSetting.getName(), kaleoNodeSetting.getValue());
		}

		Map<String, Serializable> workflowContext =
			executionContext.getWorkflowContext();

		options.setBody(
			JSONUtil.put(
				"agentDefinitionExternalReferenceCode",
				kaleoNodeSettingValues.get(
					"agentDefinitionExternalReferenceCode")
			).put(
				"asynchronous", false
			).put(
				"context",
				() -> {
					JSONObject contextJSONObject =
						_jsonFactory.createJSONObject();

					for (Map.Entry<String, Serializable> entry :
							workflowContext.entrySet()) {

						Serializable value = entry.getValue();

						if (value instanceof Boolean ||
							value instanceof Number ||
							value instanceof String) {

							contextJSONObject.put(entry.getKey(), value);
						}
					}

					return contextJSONObject;
				}
			).toString(),
			ContentTypes.APPLICATION_JSON, StringPool.UTF8);

		options.setLocation(
			authorizationTokenJSONObject.getString("serviceURL") +
				"/o/ai-hub/v1.0/agent-instances");
		options.setMethod(Http.Method.POST);
		options.setTimeout(
			GetterUtil.getInteger(
				kaleoNodeSettingValues.get("timeout"), 120000));

		JSONObject responseBodyJSONObject = _getResponseBodyJSONObject(options);

		Iterator<String> iterator = responseBodyJSONObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			workflowContext.put(
				key, (Serializable)responseBodyJSONObject.get(key));
		}

		KaleoTransition kaleoTransition =
			currentKaleoNode.getDefaultKaleoTransition();

		_workflowNodeManager.completeWorkflowNode(
			kaleoInstanceToken.getCompanyId(), kaleoInstanceToken.getUserId(),
			kaleoInstanceToken.getKaleoInstanceTokenId(),
			kaleoTransition.getName(), workflowContext, false);
	}

	private JSONObject _postAuthorizationToken(Company company)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON);
		options.setLocation(
			company.getPortalURL(0) +
				"/o/ai-hub-cell/v1.0/authorization-tokens");
		options.setMethod(Http.Method.POST);
		options.setTimeout(10000);

		return _getResponseBodyJSONObject(options);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AIHubAgentNodeExecutor.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private KaleoNodeSettingLocalService _kaleoNodeSettingLocalService;

	private NoticeableExecutorService _noticeableExecutorService;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	@Reference
	private WorkflowNodeManager _workflowNodeManager;

}