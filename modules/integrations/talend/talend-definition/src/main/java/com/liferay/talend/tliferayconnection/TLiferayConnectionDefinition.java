/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.tliferayconnection;

import com.liferay.talend.DefaultRuntimableRuntime;
import com.liferay.talend.LiferayDefinition;
import com.liferay.talend.properties.connection.LiferayConnectionProperties;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * @author Zoltán Takács
 */
public class TLiferayConnectionDefinition extends LiferayDefinition {

	public static final String COMPONENT_NAME = "tLiferayConnection";

	public TLiferayConnectionDefinition() {
		super(COMPONENT_NAME, ExecutionEngine.DI);
	}

	@Override
	public Class<? extends ComponentProperties> getPropertyClass() {
		return LiferayConnectionProperties.class;
	}

	@Override
	public Property<?>[] getReturnProperties() {
		return new Property[] {RETURN_ERROR_MESSAGE_PROP};
	}

	@Override
	public RuntimeInfo getRuntimeInfo(
		ExecutionEngine executionEngine,
		ComponentProperties componentProperties,
		ConnectorTopology connectorTopology) {

		assertConnectorTopologyCompatibility(connectorTopology);
		assertEngineCompatibility(executionEngine);

		return getCommonRuntimeInfo(DefaultRuntimableRuntime.class.getName());
	}

	@Override
	public Set<ConnectorTopology> getSupportedConnectorTopologies() {
		return EnumSet.of(ConnectorTopology.NONE);
	}

	@Override
	public boolean isStartable() {
		return true;
	}

}