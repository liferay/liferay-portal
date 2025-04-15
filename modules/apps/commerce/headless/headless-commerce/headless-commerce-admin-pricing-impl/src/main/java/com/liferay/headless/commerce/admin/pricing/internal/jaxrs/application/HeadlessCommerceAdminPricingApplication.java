/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.jaxrs.application;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;

/**
 * @author Zoltán Takács
 * @generated
 */
@Component(
	property = {
		"liferay.jackson=false",
		"osgi.jaxrs.application.base=/headless-commerce-admin-pricing",
		"osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.Vulcan)",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Pricing"
	},
	service = Application.class
)
@Generated("")
public class HeadlessCommerceAdminPricingApplication extends Application {
}