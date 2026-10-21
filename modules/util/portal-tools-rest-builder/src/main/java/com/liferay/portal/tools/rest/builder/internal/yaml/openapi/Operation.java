/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.yaml.openapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Peter Shin
 */
public class Operation {

	public String getDescription() {
		return _description;
	}

	public String getFeatureFlag() {
		return _featureFlag;
	}

	public String getOperationId() {
		return _operationId;
	}

	public List<Parameter> getParameters() {
		return _parameters;
	}

	public RequestBody getRequestBody() {
		return _requestBody;
	}

	public Map<ResponseCode, Response> getResponses() {
		return _responses;
	}

	public List<String> getTags() {
		return _tags;
	}

	public boolean isCalculatedOperationId() {
		return _calculatedOperationId;
	}

	public boolean isDeprecated() {
		return _deprecated;
	}

	public void setCalculatedOperationId(boolean calculatedOperationId) {
		_calculatedOperationId = calculatedOperationId;
	}

	public void setDeprecated(boolean deprecated) {
		_deprecated = deprecated;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setFeatureFlag(String featureFlag) {
		_featureFlag = featureFlag;
	}

	public void setOperationId(String operationId) {
		_operationId = operationId;
	}

	public void setParameters(List<Parameter> parameters) {
		_parameters = parameters;
	}

	public void setRequestBody(RequestBody requestBody) {
		_requestBody = requestBody;
	}

	public void setResponses(Map<ResponseCode, Response> responses) {
		_responses = responses;
	}

	public void setTags(List<String> tags) {
		_tags = tags;
	}

	private boolean _calculatedOperationId;
	private boolean _deprecated;
	private String _description;
	private String _featureFlag;
	private String _operationId;
	private List<Parameter> _parameters = new ArrayList<>();
	private RequestBody _requestBody;
	private Map<ResponseCode, Response> _responses;
	private List<String> _tags = new ArrayList<>();

}