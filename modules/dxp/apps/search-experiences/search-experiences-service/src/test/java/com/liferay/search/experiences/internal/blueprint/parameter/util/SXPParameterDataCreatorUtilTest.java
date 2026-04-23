/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.parameter.util;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.search.experiences.blueprint.parameter.SXPParameter;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributor;
import com.liferay.search.experiences.internal.blueprint.parameter.SXPParameterData;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.Parameter;
import com.liferay.search.experiences.rest.dto.v1_0.ParameterConfiguration;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jason Myatt
 */
public class SXPParameterDataCreatorUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Map<String, Parameter> parameterMap =
			HashMapBuilder.<String, Parameter>put(
				_PARAMETER_NAME,
				() -> {
					Parameter parameter = new Parameter();

					parameter.setType(_parameterType);

					return parameter;
				}
			).build();

		ParameterConfiguration parameterConfiguration =
			new ParameterConfiguration();

		parameterConfiguration.setParameters(parameterMap);

		Configuration configuration = new Configuration();

		configuration.setParameterConfiguration(parameterConfiguration);

		_sxpBlueprint.setConfiguration(configuration);
	}

	@Test
	public void testCreateWithArrayParameter() {
		String[] arrayOfStrings = {_PARAMETER_VALUE_1, _PARAMETER_VALUE_2};

		_searchContext.setAttribute(_PARAMETER_NAME, arrayOfStrings);

		_assertParameters();
	}

	@Test
	public void testCreateWithListParameter() {
		List<String> requestAttributes = new ArrayList<>();

		requestAttributes.add(_PARAMETER_VALUE_1);

		requestAttributes.add(_PARAMETER_VALUE_2);

		_searchContext.setAttribute(
			_PARAMETER_NAME, (Serializable)requestAttributes);

		_assertParameters();
	}

	private void _assertParameters() {
		SXPParameterData sxpParameterData = SXPParameterDataCreatorUtil.create(
			_runtimeException::addSuppressed, _searchContext, _sxpBlueprint,
			new SXPParameterContributor[0]);

		SXPParameter sxpParameter = sxpParameterData.getSXPParameterByName(
			_PARAMETER_NAME);

		String[] generatedArray = (String[])sxpParameter.getValue();

		Assert.assertEquals(
			Arrays.toString(generatedArray), 2, generatedArray.length);
		Assert.assertEquals(_PARAMETER_VALUE_1, generatedArray[0]);
		Assert.assertEquals(_PARAMETER_VALUE_2, generatedArray[1]);
	}

	private static final String _PARAMETER_NAME = RandomTestUtil.randomString();

	private static final String _PARAMETER_VALUE_1 =
		RandomTestUtil.randomString();

	private static final String _PARAMETER_VALUE_2 =
		RandomTestUtil.randomString();

	private final Parameter.Type _parameterType = Parameter.Type.STRING_ARRAY;
	private final RuntimeException _runtimeException = new RuntimeException();
	private final SearchContext _searchContext = new SearchContext();
	private final SXPBlueprint _sxpBlueprint = new SXPBlueprint();

}