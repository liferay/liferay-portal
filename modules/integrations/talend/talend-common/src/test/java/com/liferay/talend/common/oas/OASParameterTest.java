/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.oas;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Igor Beslic
 */
public class OASParameterTest {

	@Test
	public void testConstructor() {
		OASParameter oasParameter = new OASParameter("testParameter", "query");

		Assert.assertEquals(
			"OpenAPI Specification parameter name value", "testParameter",
			oasParameter.getName());

		Assert.assertFalse(
			"OpenAPI Specification parameter is required",
			oasParameter.isRequired());

		Assert.assertFalse(
			"OpenAPI Specification parameter is path",
			oasParameter.isLocationPath());

		oasParameter.setRequired(true);

		Assert.assertTrue(
			"OpenAPI Specification parameter is required",
			oasParameter.isRequired());

		oasParameter.setLocation(OASParameter.Location.PATH);

		Assert.assertTrue(
			"OpenAPI Specification parameter is path",
			oasParameter.isLocationPath());
	}

	@Test
	public void testLocation() {
		OASParameter oasParameter = new OASParameter("testParameter", "path");

		Assert.assertEquals(
			"OpenAPI parameter has location in path",
			OASParameter.Location.PATH, oasParameter.getLocation());

		Assert.assertTrue(oasParameter.isLocationPath());

		oasParameter = new OASParameter("testParameter", "query");

		Assert.assertEquals(
			"OpenAPI parameter has location in query",
			OASParameter.Location.QUERY, oasParameter.getLocation());

		Assert.assertFalse(oasParameter.isLocationPath());
	}

}