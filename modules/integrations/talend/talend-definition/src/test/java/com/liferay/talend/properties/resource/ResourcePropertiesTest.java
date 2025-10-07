/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.properties.resource;

import com.liferay.talend.BasePropertiesTestCase;

import org.junit.Assert;
import org.junit.Test;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.property.StringProperty;

/**
 * @author Igor Beslic
 */
public class ResourcePropertiesTest extends BasePropertiesTestCase {

	@Test
	public void testValidateOpenAPIModule() throws Exception {
		ComponentProperties componentProperties =
			getDefaultInitializedComponentPropertiesInstance(
				LiferayResourceProperties.class);

		LiferayResourceProperties liferayResourceProperties =
			(LiferayResourceProperties)componentProperties;

		StringProperty endpointStringProperty =
			liferayResourceProperties.endpoint;

		endpointStringProperty.setValue("/test/by-externalReferenceCode");

		_assertEndpointUrlEquals(
			"/o/headless-liferay/v1.0/test/by-externalReferenceCode",
			"/headless-liferay/v1.0", liferayResourceProperties);
		_assertEndpointUrlEquals(
			"/o/headless-liferay/v1.0/test/by-externalReferenceCode",
			"/headless-liferay/v1.0/", liferayResourceProperties);
		_assertEndpointUrlEquals(
			"/o/headless-liferay/v1.0/test/by-externalReferenceCode",
			"headless-liferay/v1.0", liferayResourceProperties);
		_assertEndpointUrlEquals(
			"/o/headless-liferay/v1.0/test/by-externalReferenceCode",
			"headless-liferay/v1.0/", liferayResourceProperties);
	}

	private void _assertEndpointUrlEquals(
		String expected, String openAPIModule,
		LiferayResourceProperties liferayResourceProperties) {

		StringProperty openAPIModuleStringProperty =
			liferayResourceProperties.openAPIModule;

		openAPIModuleStringProperty.setValue(openAPIModule);

		liferayResourceProperties.validateOpenAPIModule();

		Assert.assertEquals(
			expected, liferayResourceProperties.getEndpointUrl());
	}

}