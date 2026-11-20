/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.EventAttributeDefinition;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupEventAttributeDefinitionsPageResponse;
import com.liferay.osb.faro.rest.resource.v1_0.EventAttributeDefinitionResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Caio Pinheiro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/event-attribute-definition.properties",
	scope = ServiceScope.PROTOTYPE,
	service = EventAttributeDefinitionResource.class
)
public class EventAttributeDefinitionResourceImpl
	extends BaseEventAttributeDefinitionResourceImpl {

	@Override
	public Page<EventAttributeDefinition>
			getWorkspaceGroupEventAttributeDefinitionsPage(
				Long groupId, String attributeType, String eventDefinitionId,
				String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		if (Validator.isNull(attributeType)) {
			attributeType = "ALL";
		}

		GetWorkspaceGroupEventAttributeDefinitionsPageResponse
			getWorkspaceGroupEventAttributeDefinitionsPageResponse =
				_faroGraphQLClient.execute(
					GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
						class,
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					"getWorkspaceGroupEventAttributeDefinitionsPage",
					HashMapBuilder.<String, Object>put(
						"eventDefinitionId", eventDefinitionId
					).put(
						"keyword", search
					).put(
						"page", FaroPaginationUtil.getGraphQLPage(pagination)
					).put(
						"size", FaroPaginationUtil.getDelta(pagination)
					).put(
						"sort",
						FaroPaginationUtil.toGraphQLSort(
							new Sort("name", Sort.STRING_TYPE, false), sorts)
					).put(
						"type", attributeType
					).build());

		GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
			EventAttributeDefinitionBag eventAttributeDefinitionBag =
				getWorkspaceGroupEventAttributeDefinitionsPageResponse.
					getEventAttributeDefinitionBag();

		if (eventAttributeDefinitionBag == null) {
			return Page.of(Collections.emptyList(), pagination, 0);
		}

		Integer total = eventAttributeDefinitionBag.getTotal();

		if (total == null) {
			total = 0;
		}

		return Page.of(
			transform(
				eventAttributeDefinitionBag.getEventAttributeDefinitions(),
				eventAttributeDefinition ->
					_eventAttributeDefinitionDTOConverter.toDTO(
						new FaroDTOConverterContext(
							contextAcceptLanguage.isAcceptAllLanguages(),
							eventAttributeDefinition.getId(),
							contextAcceptLanguage.getPreferredLocale()),
						eventAttributeDefinition)),
			pagination, total);
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.EventAttributeDefinitionDTOConverter)"
	)
	private DTOConverter
		<GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
			EventAttributeDefinition,
		 EventAttributeDefinition> _eventAttributeDefinitionDTOConverter;

	@Reference
	private FaroGraphQLClient _faroGraphQLClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}