/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.internal.jaxrs.application;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;

/**
 * @author José Abelenda
 * @generated
 */
@Component(
	property = {
		"liferay.jackson=false",
		"osgi.jaxrs.application.base=/digital-signature-rest",
		"osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.Vulcan)",
		"osgi.jaxrs.name=Liferay.Digital.Signature.REST"
	},
	service = Application.class
)
@Generated("")
public class DigitalSignatureRESTApplication extends Application {
}