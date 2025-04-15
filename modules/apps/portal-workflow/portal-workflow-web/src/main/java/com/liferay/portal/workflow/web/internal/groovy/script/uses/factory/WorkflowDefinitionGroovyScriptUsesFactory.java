/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.groovy.script.uses.factory;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.script.management.groovy.script.use.GroovyScriptUse;
import com.liferay.portal.security.script.management.groovy.script.uses.factory.GroovyScriptUsesFactory;
import com.liferay.portal.workflow.definition.groovy.script.use.WorkflowDefinitionGroovyScriptUseDetector;
import com.liferay.portal.workflow.definition.groovy.script.use.WorkflowDefinitionGroovyScriptUseSourceURLFactory;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.portal.workflow.portlet.tab.WorkflowPortletTabRegistry;

import jakarta.portlet.ResourceRequest;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = GroovyScriptUsesFactory.class)
public class WorkflowDefinitionGroovyScriptUsesFactory
	implements GroovyScriptUsesFactory {

	@Override
	public List<GroovyScriptUse> create(ResourceRequest resourceRequest)
		throws Exception {

		return TransformUtil.transform(
			_workflowDefinitionManager.getActiveWorkflowDefinitions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			workflowDefinition -> {
				if (!WorkflowDefinitionGroovyScriptUseDetector.detect(
						workflowDefinition.getContent(), _jsonFactory) ||
					Objects.equals(
						workflowDefinition.getName(),
						"message-boards-user-stats-moderation")) {

					return null;
				}

				Company company = _companyLocalService.getCompany(
					workflowDefinition.getCompanyId());

				Locale locale = resourceRequest.getLocale();

				return new GroovyScriptUse(
					company.getWebId(),
					workflowDefinition.getTitle(locale.getLanguage()),
					WorkflowDefinitionGroovyScriptUseSourceURLFactory.create(
						company, _portal, workflowDefinition.getName(),
						workflowDefinition.getVersion(),
						_workflowPortletTabRegistry));
			});
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Reference
	private WorkflowPortletTabRegistry _workflowPortletTabRegistry;

}