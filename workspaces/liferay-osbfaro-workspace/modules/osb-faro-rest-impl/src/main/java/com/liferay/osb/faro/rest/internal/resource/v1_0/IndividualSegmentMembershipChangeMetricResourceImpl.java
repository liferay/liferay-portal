/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentMembershipChangeMetricResource;

import org.osgi.service.component.annotations.Component;
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
}