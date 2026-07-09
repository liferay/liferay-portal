/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.dto.v1_0.Individual;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/individual.properties",
	scope = ServiceScope.PROTOTYPE, service = IndividualResource.class
)
public class IndividualResourceImpl extends BaseIndividualResourceImpl {

	@Override
	public Page<Individual> getWorkspaceGroupChannelIndividualsPage(
			Long groupId, String channelId, String accountId,
			Boolean includeAnonymousUsers, String individualSegmentId,
			String interestName, Pagination pagination, Sort[] sorts)
		throws Exception {

		Results<com.liferay.osb.faro.engine.client.model.Individual> results =
			_contactsEngineClient.getIndividuals(
				_faroProjectLocalService.getFaroProjectByGroupId(groupId),
				accountId, null, null, channelId, null, null, null,
				(includeAnonymousUsers != null) && includeAnonymousUsers,
				individualSegmentId, null, interestName, null, null, null, null,
				null, FaroPaginationUtil.getCur(pagination),
				FaroPaginationUtil.getDelta(pagination),
				FaroPaginationUtil.toOrderByFields(sorts));

		return Page.of(
			transform(
				results.getItems(),
				individual -> _individualDTOConverter.toDTO(
					new FaroDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						individual.getId(),
						contextAcceptLanguage.getPreferredLocale()),
					individual)),
			pagination, results.getTotal());
	}

	@Override
	public Individual getWorkspaceGroupIndividual(
			Long groupId, String individualId, String channelId)
		throws Exception {

		return _individualDTOConverter.toDTO(
			new FaroDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), individualId,
				contextAcceptLanguage.getPreferredLocale()),
			_contactsEngineClient.getIndividual(
				_faroProjectLocalService.getFaroProjectByGroupId(groupId),
				individualId, channelId));
	}

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.IndividualDTOConverter)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.Individual, Individual>
			_individualDTOConverter;

}

// LIFERAY-REST-BUILDER-HASH:1353815917