/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegment;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
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
	properties = "OSGI-INF/liferay/rest/v1_0/individual-segment.properties",
	scope = ServiceScope.PROTOTYPE, service = IndividualSegmentResource.class
)
public class IndividualSegmentResourceImpl
	extends BaseIndividualSegmentResourceImpl {

	@Override
	public Page<IndividualSegment>
			getWorkspaceGroupChannelIndividualSegmentsPage(
				Long groupId, String channelId, String name, String search,
				String status, Pagination pagination)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.getFaroProjectByGroupId(groupId);

		Results<com.liferay.osb.faro.engine.client.model.IndividualSegment>
			results = _contactsEngineClient.getIndividualSegments(
				faroProject, channelId, null, search, null, name, null, null,
				null, status, FaroPaginationUtil.getCur(pagination),
				FaroPaginationUtil.getDelta(pagination), null);

		return Page.of(
			transform(
				results.getItems(),
				individualSegment -> _individualSegmentDTOConverter.toDTO(
					new FaroDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						individualSegment.getId(),
						contextAcceptLanguage.getPreferredLocale()),
					individualSegment)),
			pagination, results.getTotal());
	}

	@Override
	public IndividualSegment getWorkspaceGroupIndividualSegment(
			Long groupId, String individualSegmentId)
		throws Exception {

		return _individualSegmentDTOConverter.toDTO(
			new FaroDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				individualSegmentId,
				contextAcceptLanguage.getPreferredLocale()),
			_contactsEngineClient.getIndividualSegment(
				_faroProjectLocalService.getFaroProjectByGroupId(groupId),
				individualSegmentId, false));
	}

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.IndividualSegmentDTOConverter)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.IndividualSegment,
		 IndividualSegment> _individualSegmentDTOConverter;

}

// LIFERAY-REST-BUILDER-HASH:979115265