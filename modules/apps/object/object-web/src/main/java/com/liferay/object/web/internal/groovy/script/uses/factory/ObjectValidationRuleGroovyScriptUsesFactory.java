/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.groovy.script.uses.factory;

import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.definition.groovy.script.use.ObjectDefinitionGroovyScriptUseSourceURLFactory;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.web.internal.object.definitions.constants.ObjectDefinitionsScreenNavigationEntryConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.script.management.groovy.script.use.GroovyScriptUse;
import com.liferay.portal.security.script.management.groovy.script.uses.factory.GroovyScriptUsesFactory;

import jakarta.portlet.ResourceRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = GroovyScriptUsesFactory.class)
public class ObjectValidationRuleGroovyScriptUsesFactory
	implements GroovyScriptUsesFactory {

	@Override
	public List<GroovyScriptUse> create(ResourceRequest resourceRequest) {
		return TransformUtil.transform(
			_objectValidationRuleLocalService.getObjectValidationRules(
				true, ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY),
			objectValidationRule -> {
				Company company = _companyLocalService.getCompany(
					objectValidationRule.getCompanyId());

				return new GroovyScriptUse(
					company.getWebId(),
					objectValidationRule.getName(resourceRequest.getLocale()),
					ObjectDefinitionGroovyScriptUseSourceURLFactory.create(
						company, objectValidationRule.getObjectDefinitionId(),
						_portal,
						ObjectDefinitionsScreenNavigationEntryConstants.
							CATEGORY_KEY_VALIDATIONS));
			});
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Reference
	private Portal _portal;

}