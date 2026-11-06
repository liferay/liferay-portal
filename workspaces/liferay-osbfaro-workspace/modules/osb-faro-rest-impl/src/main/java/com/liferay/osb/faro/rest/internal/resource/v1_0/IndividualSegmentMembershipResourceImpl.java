/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembership;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.internal.helper.IndividualSegmentHelper;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentMembershipResource;
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
	properties = "OSGI-INF/liferay/rest/v1_0/individual-segment-membership.properties",
	scope = ServiceScope.PROTOTYPE,
	service = IndividualSegmentMembershipResource.class
)
public class IndividualSegmentMembershipResourceImpl
	extends BaseIndividualSegmentMembershipResourceImpl {

	@Override
	public Page<IndividualSegmentMembership>
			getWorkspaceGroupIndividualSegmentMembershipsPage(
				Long groupId, String individualSegmentId, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.getFaroProjectByGroupId(groupId);

		_individualSegmentHelper.getIndividualSegment(
			faroProject, individualSegmentId);

		Results
			<com.liferay.osb.faro.engine.client.model.
				IndividualSegmentMembership> results =
					_contactsEngineClient.getIndividualSegmentMemberships(
						faroProject, individualSegmentId,
						FaroPaginationUtil.getCur(pagination),
						FaroPaginationUtil.getDelta(pagination),
						FaroPaginationUtil.toOrderByFields(sorts));

		return Page.of(
			transform(
				results.getItems(),
				individualSegmentMembership ->
					_individualSegmentMembershipDTOConverter.toDTO(
						new FaroDTOConverterContext(
							contextAcceptLanguage.isAcceptAllLanguages(),
							individualSegmentMembership.getIndividualId(),
							contextAcceptLanguage.getPreferredLocale()),
						individualSegmentMembership)),
			pagination, results.getTotal());
	}

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private IndividualSegmentHelper _individualSegmentHelper;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.IndividualSegmentMembershipDTOConverter)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership,
		 IndividualSegmentMembership> _individualSegmentMembershipDTOConverter;

}

// LIFERAY-REST-BUILDER-HASH:1196346268