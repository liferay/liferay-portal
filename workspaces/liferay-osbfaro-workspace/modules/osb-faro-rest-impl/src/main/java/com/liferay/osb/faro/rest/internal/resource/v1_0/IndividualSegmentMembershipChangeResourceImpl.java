/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentMembershipChangeResource;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
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
				Long groupId, String individualSegmentId, String rangeEnd,
				String rangeKey, String rangeStart, String search,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		if (Validator.isNull(rangeEnd) != Validator.isNull(rangeStart)) {
			throw new IllegalArgumentException(
				"\"rangeEnd\" and \"rangeStart\" must be set together");
		}

		return Page.of(Collections.emptyList(), pagination, 0);
	}

}