/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.internal.resource.v1_0;

import com.liferay.digital.signature.rest.resource.v1_0.SignatureRequestResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author José Abelenda
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/signature-request.properties",
	scope = ServiceScope.PROTOTYPE, service = SignatureRequestResource.class
)
public class SignatureRequestResourceImpl
	extends BaseSignatureRequestResourceImpl {
}