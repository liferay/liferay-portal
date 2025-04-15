/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.jaxrs.exception.mapper;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.workflow.RequiredWorkflowDefinitionException;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.Workflow)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.Workflow.RequiredWorkflowDefinitionExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class RequiredWorkflowDefinitionExceptionMapper
	extends BaseExceptionMapper<RequiredWorkflowDefinitionException> {

	@Override
	protected Problem getProblem(
		RequiredWorkflowDefinitionException
			requiredWorkflowDefinitionException) {

		return new Problem(
			Response.Status.BAD_REQUEST,
			_language.format(
				ResourceBundleUtil.getModuleAndPortalResourceBundle(
					_acceptLanguage.getPreferredLocale(),
					RequiredWorkflowDefinitionExceptionMapper.class),
				_getMessageKey(requiredWorkflowDefinitionException),
				_getMessageArguments(requiredWorkflowDefinitionException)));
	}

	private Object[] _getMessageArguments(
		RequiredWorkflowDefinitionException
			requiredWorkflowDefinitionException) {

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			requiredWorkflowDefinitionException.getWorkflowDefinitionLinks();

		if (workflowDefinitionLinks.isEmpty()) {
			return new Object[0];
		}
		else if (workflowDefinitionLinks.size() == 1) {
			WorkflowDefinitionLink workflowDefinitionLink =
				workflowDefinitionLinks.get(0);

			return new Object[] {
				_getModelResource(workflowDefinitionLink.getClassName())
			};
		}
		else if (workflowDefinitionLinks.size() == 2) {
			WorkflowDefinitionLink workflowDefinitionLink1 =
				workflowDefinitionLinks.get(0);
			WorkflowDefinitionLink workflowDefinitionLink2 =
				workflowDefinitionLinks.get(1);

			return new Object[] {
				_getModelResource(workflowDefinitionLink1.getClassName()),
				_getModelResource(workflowDefinitionLink2.getClassName())
			};
		}

		WorkflowDefinitionLink workflowDefinitionLink1 =
			workflowDefinitionLinks.get(0);
		WorkflowDefinitionLink workflowDefinitionLink2 =
			workflowDefinitionLinks.get(1);

		return new Object[] {
			_getModelResource(workflowDefinitionLink1.getClassName()),
			_getModelResource(workflowDefinitionLink2.getClassName()),
			workflowDefinitionLinks.size() - 2
		};
	}

	private String _getMessageKey(
		RequiredWorkflowDefinitionException
			requiredWorkflowDefinitionException) {

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			requiredWorkflowDefinitionException.getWorkflowDefinitionLinks();

		if (workflowDefinitionLinks.isEmpty()) {
			return StringPool.BLANK;
		}
		else if (workflowDefinitionLinks.size() == 1) {
			return "workflow-is-in-use.-remove-its-assignment-to-x";
		}
		else if (workflowDefinitionLinks.size() == 2) {
			return "workflow-is-in-use.-remove-its-assignment-to-x-and-x";
		}

		return "workflow-is-in-use.-remove-its-assignment-to-x-x-and-x-more";
	}

	private String _getModelResource(String className) {
		return ResourceActionsUtil.getModelResource(
			_acceptLanguage.getPreferredLocale(), className);
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}