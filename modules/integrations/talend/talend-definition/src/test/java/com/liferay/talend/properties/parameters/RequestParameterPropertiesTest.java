/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.properties.parameters;

import com.liferay.talend.common.oas.OASParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

import org.talend.daikon.properties.property.Property;

/**
 * @author Igor Beslic
 */
public class RequestParameterPropertiesTest {

	@Test
	public void testAddProperties() {
		RequestParameterProperties requestParameterProperties =
			new RequestParameterProperties("request");

		requestParameterProperties.setupProperties();

		requestParameterProperties.addParameters(_testOASParameters);

		Assert.assertTrue(
			"Request parameter properties is empty",
			requestParameterProperties.isEmpty());

		_setRandomRequestParameterValues(requestParameterProperties);

		Assert.assertFalse(
			"Request parameter properties is not empty",
			requestParameterProperties.isEmpty());

		List<RequestParameter> requestParameters =
			requestParameterProperties.getRequestParameters();

		Assert.assertEquals(
			"Request parameter properties size", _testOASParameters.size(),
			requestParameters.size());

		for (RequestParameter requestParameter : requestParameters) {
			_assertOASParameterExists(requestParameter);
		}

		requestParameterProperties.addParameters(_ignoredOASParameters);

		Property<List<String>> parameterNameColumn =
			requestParameterProperties.parameterNameColumn;

		List<String> parameterNames = parameterNameColumn.getValue();

		Assert.assertEquals(
			"Request parameter properties available parameter names size",
			_testOASParameters.size(), parameterNames.size());
	}

	@Test
	public void testConstructor() {
		RequestParameterProperties requestParameterProperties =
			new RequestParameterProperties("request");

		requestParameterProperties.setupProperties();

		Assert.assertTrue(
			"Request parameter properties is empty",
			requestParameterProperties.isEmpty());

		Assert.assertEquals(
			"Request parameter properties name", "request",
			requestParameterProperties.getName());
	}

	@Test
	public void testRemoveProperties() {
		RequestParameterProperties requestParameterProperties =
			new RequestParameterProperties("request");

		requestParameterProperties.setupProperties();

		requestParameterProperties.addParameters(_testOASParameters);

		_setRandomRequestParameterValues(requestParameterProperties);

		Assert.assertFalse(
			"Request parameter properties is not empty",
			requestParameterProperties.isEmpty());

		requestParameterProperties.removeAll();

		Assert.assertTrue(
			"Request parameter properties is empty after removeAll",
			requestParameterProperties.isEmpty());

		requestParameterProperties.addParameters(_testOASParameters);

		_setRandomRequestParameterValues(requestParameterProperties);

		List<RequestParameter> requestParameters =
			requestParameterProperties.getRequestParameters();

		Assert.assertEquals(
			"Request parameter properties size after new addition",
			_testOASParameters.size(), requestParameters.size());
	}

	private void _assertOASParameterExists(RequestParameter requestParameter) {
		for (OASParameter oasParameter : _testOASParameters) {
			String oasParameterName = oasParameter.getName();

			if (Objects.equals(requestParameter.getName(), oasParameterName)) {
				Assert.assertEquals(
					oasParameter.isLocationPath(),
					requestParameter.isPathLocation());

				return;
			}
		}

		throw new RuntimeException(
			"Request parameter not found in OAS parameters: " +
				requestParameter.getName());
	}

	private void _setRandomRequestParameterValues(
		RequestParameterProperties requestParameterProperties) {

		Property<List<String>> parameterNameColumn =
			requestParameterProperties.parameterNameColumn;

		List<String> parameterNames = parameterNameColumn.getValue();

		List<String> parameterValues = new ArrayList<>();

		for (int i = 0; i < parameterNames.size(); i++) {
			parameterValues.add(i, String.format("Value of %d", i));
		}

		Property<List<String>> parameterValueColumn =
			requestParameterProperties.parameterValueColumn;

		parameterValueColumn.setValue(parameterValues);
	}

	private static final List<OASParameter> _ignoredOASParameters =
		new ArrayList<OASParameter>() {
			{
				add(new OASParameter("page", "query"));
				add(new OASParameter("pageSize", "query"));
			}
		};

	private static final List<OASParameter> _testOASParameters =
		new ArrayList<OASParameter>() {
			{
				add(new OASParameter("optionalQuery", "query"));
				add(new OASParameter("requiredPath", "path"));

				OASParameter oasParameter = new OASParameter(
					"requiredQuery", "query");

				oasParameter.setRequired(true);

				add(oasParameter);
			}
		};

}