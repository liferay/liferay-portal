/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChangeAggregation;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChangeMetric;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.IndividualSegmentMembershipChangeMetricUtil;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentMembershipChangeMetricResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/individual-segment-membership-change-metric.properties",
	scope = ServiceScope.PROTOTYPE,
	service = IndividualSegmentMembershipChangeMetricResource.class
)
public class IndividualSegmentMembershipChangeMetricResourceImpl
	extends BaseIndividualSegmentMembershipChangeMetricResourceImpl {

	@Override
	public IndividualSegmentMembershipChangeMetric
			getWorkspaceGroupIndividualSegmentMembershipChangeMetric(
				Long groupId, String individualSegmentId, String rangeKey)
		throws Exception {

		int days = _getDays(rangeKey);

		Results<IndividualSegmentMembershipChangeAggregation> results =
			_contactsEngineClient.
				getIndividualSegmentMembershipChangeAggregations(
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					individualSegmentId, "day", (2 * days) - 1);

		return IndividualSegmentMembershipChangeMetricUtil.
			toIndividualSegmentMembershipChangeMetric(days, results.getItems());
	}

	private int _getDays(String rangeKey) {
		if (Validator.isNull(rangeKey)) {
			return TimeRange.LAST_30_DAYS.getRangeKey();
		}

		TimeRange timeRange = TimeRange.valueOf(rangeKey);

		return Math.max(1, timeRange.getRangeKey());
	}

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}