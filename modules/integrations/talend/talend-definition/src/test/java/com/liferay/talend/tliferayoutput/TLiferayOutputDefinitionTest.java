/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.tliferayoutput;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * @author Zoltán Takács
 */
public class TLiferayOutputDefinitionTest {

	@Before
	public void setUp() {
		_tLiferayOutputDefinition = new TLiferayOutputDefinition();
	}

	@Test
	public void testGetFamilies() {
		String[] actualFamilies = _tLiferayOutputDefinition.getFamilies();

		MatcherAssert.assertThat(
			Arrays.asList(actualFamilies),
			Matchers.contains("Business/Liferay"));
	}

	@Test
	public void testGetPropertyClass() {
		Class<?> propertyClass = _tLiferayOutputDefinition.getPropertyClass();

		MatcherAssert.assertThat(
			propertyClass.getCanonicalName(),
			Matchers.equalTo(
				"com.liferay.talend.tliferayoutput.TLiferayOutputProperties"));
	}

	@Test
	public void testGetReturnProperties() {
		List<Property<?>> propertyList = Arrays.asList(
			_tLiferayOutputDefinition.getReturnProperties());

		MatcherAssert.assertThat(propertyList, Matchers.hasSize(4));

		Assert.assertTrue(
			propertyList.contains(
				ComponentDefinition.RETURN_ERROR_MESSAGE_PROP));
		Assert.assertTrue(
			propertyList.contains(
				ComponentDefinition.RETURN_REJECT_RECORD_COUNT_PROP));
		Assert.assertTrue(
			propertyList.contains(
				ComponentDefinition.RETURN_SUCCESS_RECORD_COUNT_PROP));
		Assert.assertTrue(
			propertyList.contains(
				ComponentDefinition.RETURN_TOTAL_RECORD_COUNT_PROP));
	}

	@Test
	public void testGetRuntimeInfoForIncomingTopology() {
		RuntimeInfo runtimeInfo = _tLiferayOutputDefinition.getRuntimeInfo(
			ExecutionEngine.DI, null, ConnectorTopology.INCOMING);

		MatcherAssert.assertThat(
			runtimeInfo.getRuntimeClassName(),
			Matchers.equalTo("com.liferay.talend.runtime.LiferaySink"));
	}

	@Test
	public void testGetRuntimeInfoWrongEngine() {
		expectedException.expect(TalendRuntimeException.class);
		expectedException.expectMessage(
			"WRONG_EXECUTION_ENGINE:{component=tLiferayOutput, " +
				"requested=DI_SPARK_STREAMING, available=[DI]}");

		_tLiferayOutputDefinition.getRuntimeInfo(
			ExecutionEngine.DI_SPARK_STREAMING, null,
			ConnectorTopology.INCOMING);
	}

	@Ignore
	@Test
	public void testGetRuntimeInfoWrongTopology() {
		expectedException.expect(TalendRuntimeException.class);
		expectedException.expectMessage(
			"WRONG_CONNECTOR:{component=tLiferayOutput}");

		_tLiferayOutputDefinition.getRuntimeInfo(
			ExecutionEngine.DI, null, ConnectorTopology.OUTGOING);
	}

	@Test
	public void testGetSupportedConnectorTopologies() {
		Set<ConnectorTopology> connectorTopologies =
			_tLiferayOutputDefinition.getSupportedConnectorTopologies();

		MatcherAssert.assertThat(
			connectorTopologies,
			Matchers.contains(
				ConnectorTopology.INCOMING,
				ConnectorTopology.INCOMING_AND_OUTGOING));
		MatcherAssert.assertThat(
			connectorTopologies,
			Matchers.not(
				Matchers.contains(
					ConnectorTopology.OUTGOING, ConnectorTopology.NONE)));
	}

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	private TLiferayOutputDefinition _tLiferayOutputDefinition;

}