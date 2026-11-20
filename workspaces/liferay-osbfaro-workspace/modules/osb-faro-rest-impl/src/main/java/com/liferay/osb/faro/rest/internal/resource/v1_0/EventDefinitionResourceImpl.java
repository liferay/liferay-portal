/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.EventDefinition;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupEventDefinitionsPageResponse;
import com.liferay.osb.faro.rest.resource.v1_0.EventDefinitionResource;
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
	properties = "OSGI-INF/liferay/rest/v1_0/event-definition.properties",
	scope = ServiceScope.PROTOTYPE, service = EventDefinitionResource.class
)
public class EventDefinitionResourceImpl
	extends BaseEventDefinitionResourceImpl {

	@Override
	public Page<EventDefinition> getWorkspaceGroupEventDefinitionsPage(
			Long groupId, Boolean blocked, String eventType, Boolean hidden,
			String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		if (Validator.isNull(eventType)) {
			eventType = "ALL";
		}

		GetWorkspaceGroupEventDefinitionsPageResponse
			getWorkspaceGroupEventDefinitionsPageResponse =
				_faroGraphQLClient.execute(
					GetWorkspaceGroupEventDefinitionsPageResponse.class,
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					"getWorkspaceGroupEventDefinitionsPage",
					HashMapBuilder.<String, Object>put(
						"blocked", blocked
					).put(
						"eventType", eventType
					).put(
						"hidden", hidden
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
					).build());

		GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinitionBag
			eventDefinitionBag =
				getWorkspaceGroupEventDefinitionsPageResponse.
					getEventDefinitionBag();

		if (eventDefinitionBag == null) {
			return Page.of(Collections.emptyList(), pagination, 0);
		}

		Integer total = eventDefinitionBag.getTotal();

		if (total == null) {
			total = 0;
		}

		return Page.of(
			transform(
				eventDefinitionBag.getEventDefinitions(),
				eventDefinition -> _eventDefinitionDTOConverter.toDTO(
					new FaroDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						eventDefinition.getId(),
						contextAcceptLanguage.getPreferredLocale()),
					eventDefinition)),
			pagination, total);
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.EventDefinitionDTOConverter)"
	)
	private DTOConverter
		<GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinition,
		 EventDefinition> _eventDefinitionDTOConverter;

	@Reference
	private FaroGraphQLClient _faroGraphQLClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}