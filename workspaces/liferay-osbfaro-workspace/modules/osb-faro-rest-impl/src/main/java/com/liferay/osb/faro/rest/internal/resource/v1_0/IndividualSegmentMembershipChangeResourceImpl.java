/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentMembershipChangeResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/individual-segment-membership-change.properties",
	scope = ServiceScope.PROTOTYPE,
	service = IndividualSegmentMembershipChangeResource.class
)
public class IndividualSegmentMembershipChangeResourceImpl
	extends BaseIndividualSegmentMembershipChangeResourceImpl {

	@Override
	public Page<IndividualSegmentMembershipChange>
			getWorkspaceGroupIndividualSegmentMembershipChangesPage(
				Long groupId, String individualSegmentId, String operation,
				String rangeEnd, String rangeKey, String rangeStart,
				String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		List<OrderByField> orderByFields = FaroPaginationUtil.toOrderByFields(
			sorts);

		if (orderByFields == null) {
			orderByFields = Collections.singletonList(
				new OrderByField("dateChanged", "desc"));
		}

		Results
			<com.liferay.osb.faro.engine.client.model.
				IndividualSegmentMembershipChange> results =
					_contactsEngineClient.getIndividualSegmentMembershipChanges(
						_faroProjectLocalService.getFaroProjectByGroupId(
							groupId),
						individualSegmentId, search,
						DateRangeUtil.getStartDate(rangeKey, rangeStart),
						DateRangeUtil.getEndDate(rangeKey, rangeEnd),
						FaroPaginationUtil.getCur(pagination),
						FaroPaginationUtil.getDelta(pagination), orderByFields);

		List
			<com.liferay.osb.faro.engine.client.model.
				IndividualSegmentMembershipChange>
					individualSegmentMembershipChanges = results.getItems();

		if (Validator.isNotNull(operation)) {
			individualSegmentMembershipChanges = ListUtil.filter(
				individualSegmentMembershipChanges,
				individualSegmentMembershipChange -> Objects.equals(
					individualSegmentMembershipChange.getOperation(),
					operation));
		}

		return Page.of(
			transform(
				individualSegmentMembershipChanges,
				individualSegmentMembershipChange ->
					_individualSegmentMembershipChangeDTOConverter.toDTO(
						new FaroDTOConverterContext(
							contextAcceptLanguage.isAcceptAllLanguages(),
							individualSegmentMembershipChange.getId(),
							contextAcceptLanguage.getPreferredLocale()),
						individualSegmentMembershipChange)),
			pagination, results.getTotal());
	}

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference(
		target = "(dto.class.name=com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChange)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.
			IndividualSegmentMembershipChange,
		 IndividualSegmentMembershipChange>
			_individualSegmentMembershipChangeDTOConverter;

}