/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.manager.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.AgentDefinition;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

/**
 * @author Feliphe Marinho
 * @author João Victor Alves
 */
public interface AgentDefinitionManager {

	public void deleteAgentDefinition(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode)
		throws Exception;

	public AgentDefinition getAgentDefinition(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode)
		throws Exception;

	public Page<AgentDefinition> getAgentDefinitionsPage(
			long companyId, DTOConverterContext dtoConverterContext,
			String filterString, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception;

	public AgentDefinition patchAgentDefinitionUpdateActive(
			boolean active, long companyId,
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode)
		throws Exception;

	public AgentDefinition postAgentDefinitionCopy(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode)
		throws Exception;

	public AgentDefinition postAgentDefinitionDraft(
			long companyId, DTOConverterContext dtoConverterContext)
		throws Exception;

}