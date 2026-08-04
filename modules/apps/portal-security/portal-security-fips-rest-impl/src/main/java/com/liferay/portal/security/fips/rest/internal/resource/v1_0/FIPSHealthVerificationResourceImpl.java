/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.resource.v1_0;

import com.liferay.portal.security.fips.rest.resource.v1_0.FIPSHealthVerificationResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Lucas Miranda
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/fips-health-verification.properties",
	scope = ServiceScope.PROTOTYPE,
	service = FIPSHealthVerificationResource.class
)
public class FIPSHealthVerificationResourceImpl
	extends BaseFIPSHealthVerificationResourceImpl {
}