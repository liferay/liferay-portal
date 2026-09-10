/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.resource.v1_0;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.internal.util.ToolSetUtil;
import com.liferay.mcp.server.rest.resource.v1_0.ToolResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.vulcan.fields.NestedField;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/tool.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ToolResource.class
)
public class ToolResourceImpl extends BaseToolResourceImpl {

	@NestedField(parentClass = Tool.class, value = "outputSchema")
	public Map<String, ?> getToolOutputSchema(
		@PathParam("toolSetName") String toolSetName,
		@PathParam("toolName") String toolName) {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-63311")) {

			throw new UnsupportedOperationException();
		}

		return ToolSetUtil.getToolOutputSchema(
			contextHttpServletRequest, toolName, toolSetName);
	}

	@Override
	public Tool getToolSetToolSetNameTool(String toolSetName, String toolName) {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-63311")) {

			throw new UnsupportedOperationException();
		}

		return ToolSetUtil.getTool(
			contextHttpServletRequest, toolName, toolSetName);
	}

	@Override
	public Response postToolSetToolSetNameToolInvokeObject(
			String toolSetName, String toolName, Object object)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-63311")) {

			throw new UnsupportedOperationException();
		}

		return ToolSetUtil.invokeTool(
			null, contextHttpServletRequest, object, toolName, toolSetName);
	}

}