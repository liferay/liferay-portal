/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.tliferayconnection;

import com.liferay.talend.DefaultRuntimableRuntime;
import com.liferay.talend.properties.connection.LiferayConnectionProperties;

import java.util.Set;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * @author Zoltán Takács
 */
public class TLiferayConnectionDefinitionTest {

	@Before
	public void setUp() {
		_tLiferayConnectionDefinition = new TLiferayConnectionDefinition();

		_tLiferayConnectionProperties = new LiferayConnectionProperties(
			"connection");

		_tLiferayConnectionProperties.init();
	}

	@Test
	public void testGetRuntimeInfoWrongTopology() {
		expectedException.expect(TalendRuntimeException.class);
		expectedException.expectMessage(
			"WRONG_CONNECTOR:{component=tLiferayConnection}");

		_tLiferayConnectionDefinition.getRuntimeInfo(
			ExecutionEngine.DI, null, ConnectorTopology.INCOMING);
	}

	@Test
	public void testRuntimeInfo() {
		RuntimeInfo runtimeInfo = _tLiferayConnectionDefinition.getRuntimeInfo(
			ExecutionEngine.DI, _tLiferayConnectionProperties,
			ConnectorTopology.NONE);

		Assert.assertNotNull(runtimeInfo.getMavenUrlDependencies());
		Assert.assertEquals(
			runtimeInfo.getRuntimeClassName(),
			DefaultRuntimableRuntime.class.getName());

		Assert.assertThat(
			runtimeInfo, CoreMatchers.instanceOf(JarRuntimeInfo.class));

		JarRuntimeInfo jarRuntimeInfo = (JarRuntimeInfo)runtimeInfo;

		Assert.assertNotNull(jarRuntimeInfo.getDepTxtPath());
		Assert.assertNotNull(jarRuntimeInfo.getJarUrl());
	}

	@Test
	public void testStartable() {
		Assert.assertTrue(_tLiferayConnectionDefinition.isStartable());
	}

	@Test
	public void testSupportedConnectorTopologies() {
		Set<ConnectorTopology> topologySet =
			_tLiferayConnectionDefinition.getSupportedConnectorTopologies();

		Assert.assertThat(
			topologySet, CoreMatchers.hasItems(ConnectorTopology.NONE));
	}

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	private TLiferayConnectionDefinition _tLiferayConnectionDefinition;
	private LiferayConnectionProperties _tLiferayConnectionProperties;

}