/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.kaleo.runtime.node;

import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.KaleoNodeSettingUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.VariablesUtil;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fabian Bouché
 * @author Iliyan Peychev
 */
@Component(service = NodeExecutor.class)
public class HTTPRequestNodeExecutor extends BaseNodeExecutor {

	@Override
	public NodeType getNodeType() {
		return NodeType.HTTP_REQUEST;
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

		Map<String, Serializable> workflowContext =
			executionContext.getWorkflowContext();

		try {
			KaleoInstanceToken kaleoInstanceToken =
				executionContext.getKaleoInstanceToken();

			if (Validator.isNull(
					GetterUtil.getString(
						workflowContext.get("aiHubCellLiferayDXPURL")))) {

				workflowContext.put(
					"aiHubCellLiferayDXPURL",
					_getAIHubCellLiferayDXPURL(workflowContext));
			}

			Http.Options options = new Http.Options();

			Company company = _companyLocalService.getCompany(
				kaleoInstanceToken.getCompanyId());

			String userToken = EncryptorUtil.decrypt(
				company.getKeyObj(),
				GetterUtil.getString(workflowContext.get("userToken")));

			options.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + userToken);

			String requestBody = VariablesUtil.applyInputVariables(
				executionContext, "requestBody", kaleoNodeSettingValues);

			if (Validator.isNotNull(requestBody)) {
				options.setBody(
					requestBody, ContentTypes.APPLICATION_JSON, "UTF-8");
				options.addHeader(
					HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
			}

			options.setLocation(
				VariablesUtil.applyInputVariables(
					executionContext, "url", kaleoNodeSettingValues));
			options.setMethod(
				Http.Method.valueOf(
					GetterUtil.getString(
						kaleoNodeSettingValues.get("httpMethod"))));
			options.setTimeout(
				GetterUtil.getInteger(
					kaleoNodeSettingValues.get("timeout"), 10000));

			String responseBody = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if ((response.getResponseCode() < 200) ||
				(response.getResponseCode() >= 300)) {

				throw new PortalException(
					StringBundler.concat(
						"HTTP request node \"", currentKaleoNode.getName(),
						"\" failed with response code ",
						response.getResponseCode()));
			}

			VariablesUtil.applyOutputVariable(
				kaleoNodeSettingValues, responseBody, workflowContext);
		}
		catch (Exception exception) {
			if (exception instanceof PortalException portalException) {
				throw portalException;
			}

			throw new PortalException(exception);
		}

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

	@Override
	protected void doExit(
		KaleoNode currentKaleoNode, ExecutionContext executionContext,
		List<PathElement> remainingPathElements) {
	}

	private String _getAIHubCellLiferayDXPURL(
			Map<String, Serializable> workflowContext)
		throws PortalException {

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.getOAuth2Application(
				GetterUtil.getLong(workflowContext.get("oAuth2ApplicationId")));

		return oAuth2Application.getHomePageURL();
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private Http _http;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}