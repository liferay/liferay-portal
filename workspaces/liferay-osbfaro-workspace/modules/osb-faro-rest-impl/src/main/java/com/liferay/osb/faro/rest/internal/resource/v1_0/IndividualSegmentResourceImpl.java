/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroChannelTable;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegment;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.internal.helper.IndividualSegmentHelper;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentResource;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public Page<IndividualSegment>
			getWorkspaceGroupIndividualIndividualSegmentsPage(
				Long groupId, String individualId, String channelId,
				String search, String status, Pagination pagination)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.getFaroProjectByGroupId(groupId);

		List<String> excludedChannelIds = Collections.emptyList();

		if (Validator.isNull(channelId)) {
			excludedChannelIds = _getExcludedChannelIds(groupId);
		}

		Results<com.liferay.osb.faro.engine.client.model.IndividualSegment>
			results = _contactsEngineClient.getIndividualIndividualSegments(
				faroProject, channelId, excludedChannelIds, individualId,
				search, status, FaroPaginationUtil.getCur(pagination),
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
			_individualSegmentHelper.getIndividualSegment(
				_faroProjectLocalService.getFaroProjectByGroupId(groupId),
				individualSegmentId));
	}

	private List<String> _getExcludedChannelIds(long groupId) {
		Set<String> accessibleChannelIds = new HashSet<>(
			transform(
				_faroChannelLocalService.search(
					groupId, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				FaroChannel::getChannelId));

		return transform(
			_faroChannelLocalService.<List<FaroChannel>>dslQuery(
				DSLQueryFactoryUtil.select(
					FaroChannelTable.INSTANCE
				).from(
					FaroChannelTable.INSTANCE
				).where(
					FaroChannelTable.INSTANCE.workspaceGroupId.eq(groupId)
				)),
			faroChannel -> {
				if (accessibleChannelIds.contains(faroChannel.getChannelId())) {
					return null;
				}

				return faroChannel.getChannelId();
			});
	}

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.IndividualSegmentDTOConverter)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.IndividualSegment,
		 IndividualSegment> _individualSegmentDTOConverter;

	@Reference
	private IndividualSegmentHelper _individualSegmentHelper;

}

// LIFERAY-REST-BUILDER-HASH:979115265