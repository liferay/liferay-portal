/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.node.delegate;

import com.liferay.ai.hub.workflow.node.ServiceNodeDelegate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Leite
 */
@Component(service = ServiceNodeDelegate.class)
public class ComposeContentEntriesOutputServiceNodeDelegate
	implements ServiceNodeDelegate {

	@Override
	public String execute(
			Map<String, String> inputVariables,
			Map<String, Serializable> workflowContext)
		throws Exception {

		String contentEntriesPayload = inputVariables.get(
			"contentEntriesPayload");

		if (Validator.isNull(contentEntriesPayload)) {
			String output =
				"I could not generate any content. Please try again.";

			workflowContext.put("output", output);

			return output;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			contentEntriesPayload);

		if (jsonArray.length() == 0) {
			String output = _getOutput(inputVariables);

			workflowContext.put("output", output);

			return output;
		}

		StringBundler sb = new StringBundler();

		for (int i = 0; i < jsonArray.length(); i++) {
			if (i > 0) {
				sb.append("\n");
			}

			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONObject propertiesJSONObject = jsonObject.getJSONObject(
				"properties");

			sb.append("- [");
			sb.append(propertiesJSONObject.getString("title"));
			sb.append("](");
			sb.append(
				MapUtil.getString(workflowContext, "aiHubCellLiferayDXPURL"));
			sb.append("/c/cms/edit_content_item?externalReferenceCode=");
			sb.append(
				URLCodec.encodeURL(
					jsonObject.getString("externalReferenceCode")));
			sb.append("&groupId=");
			sb.append(URLCodec.encodeURL(inputVariables.get("spaceId")));
			sb.append("&objectDefinitionName=");
			sb.append(
				URLCodec.encodeURL(inputVariables.get("objectDefinitionName")));
			sb.append(")");
		}

		String output = sb.toString();

		workflowContext.put("output", output);

		return output;
	}

	@Override
	public String getKey() {
		return "javaDelegate#composeContentEntriesOutput";
	}

	private String _getOutput(Map<String, String> inputVariables) {
		if (Validator.isNull(inputVariables.get("objectDefinitionName")) ||
			Validator.isNull(inputVariables.get("spaceId"))) {

			return StringBundler.concat(
				"I can only generate content when a destination space and a ",
				"content type are selected. Please open the AI Assistant from ",
				"a content section.");
		}

		return StringBundler.concat(
			"I can only generate ", inputVariables.get("objectDefinitionName"),
			" content here. To generate a different content type, open the AI ",
			"Assistant from that content type's section.");
	}

	@Reference
	private JSONFactory _jsonFactory;

}