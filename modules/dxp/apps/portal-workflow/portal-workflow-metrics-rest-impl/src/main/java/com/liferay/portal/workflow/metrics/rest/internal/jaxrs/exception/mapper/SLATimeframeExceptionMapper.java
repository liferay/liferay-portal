/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.jaxrs.exception.mapper;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.workflow.metrics.exception.WorkflowMetricsSLADefinitionTimeframeException;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.GenericError;

import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Portal.Workflow.Metrics.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Portal.Workflow.Metrics.REST.SLATimeframeExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class SLATimeframeExceptionMapper
	extends BaseSLAExceptionMapper
		<WorkflowMetricsSLADefinitionTimeframeException> {

	@Override
	public List<GenericError> toGenericErrors(
		WorkflowMetricsSLADefinitionTimeframeException
			workflowMetricsSLADefinitionTimeframeException) {

		List<GenericError> genericErrors = TransformUtil.transform(
			workflowMetricsSLADefinitionTimeframeException.getFieldNames(),
			fieldName -> {
				GenericError genericError = new GenericError();

				genericError.setFieldName(() -> fieldName);
				genericError.setMessage(
					() -> getMessage("selected-option-is-no-longer-available"));

				return genericError;
			});

		genericErrors.add(
			new GenericError() {
				{
					setMessage(
						() -> SLATimeframeExceptionMapper.this.getMessage(
							"the-time-frame-options-changed-in-the-workflow-" +
								"definition"));
				}
			});

		return genericErrors;
	}

}