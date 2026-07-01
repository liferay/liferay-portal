/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.resource.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.AgentDefinition;
import com.liferay.ai.hub.rest.manager.v1_0.AgentDefinitionManager;
import com.liferay.ai.hub.rest.resource.v1_0.AgentDefinitionResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Feliphe Marinho
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/agent-definition.properties",
	scope = ServiceScope.PROTOTYPE, service = AgentDefinitionResource.class
)
public class AgentDefinitionResourceImpl
	extends BaseAgentDefinitionResourceImpl {

	@Override
	public void deleteAgentDefinitionByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		_agentDefinitionManager.deleteAgentDefinition(
			contextCompany.getCompanyId(), _createDTOConverterContext(),
			externalReferenceCode);
	}

	@Override
	public Page<AgentDefinition> getAgentDefinitionsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		return _agentDefinitionManager.getAgentDefinitionsPage(
			contextCompany.getCompanyId(), _createDTOConverterContext(),
			ParamUtil.getString(contextHttpServletRequest, "filter"),
			pagination, search, sorts);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public AgentDefinition
			patchAgentDefinitionByExternalReferenceCodeUpdateActive(
				String externalReferenceCode, Boolean active)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		return _agentDefinitionManager.patchAgentDefinitionUpdateActive(
			active, contextCompany.getCompanyId(), _createDTOConverterContext(),
			externalReferenceCode);
	}

	@Override
	public AgentDefinition postAgentDefinitionByExternalReferenceCodeCopy(
			String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		return _agentDefinitionManager.postAgentDefinitionCopy(
			contextCompany.getCompanyId(), _createDTOConverterContext(),
			externalReferenceCode);
	}

	@Override
	public AgentDefinition postAgentDefinitionDraft() throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		return _agentDefinitionManager.postAgentDefinitionDraft(
			contextCompany.getCompanyId(), _createDTOConverterContext());
	}

	private DTOConverterContext _createDTOConverterContext() {
		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(), null,
			_dtoConverterRegistry, contextHttpServletRequest, null,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	@Reference
	private AgentDefinitionManager _agentDefinitionManager;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(target = "(entity.model.name=AgentDefinition)")
	private EntityModel _entityModel;

}