/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.tliferayinput;

import com.liferay.talend.LiferayDefinition;
import com.liferay.talend.properties.input.LiferayInputProperties;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * @author Zoltán Takács
 * @author Ivica Cardic
 */
public class TLiferayInputDefinition extends LiferayDefinition {

	public static final String COMPONENT_NAME = "tLiferayInput";

	public TLiferayInputDefinition() {
		super(COMPONENT_NAME, ExecutionEngine.DI);
	}

	@Override
	public Class<? extends ComponentProperties>[]
		getNestedCompatibleComponentPropertiesClass() {

		return concatPropertiesClasses(
			super.getNestedCompatibleComponentPropertiesClass(),
			(Class<? extends ComponentProperties>[])new Class<?>[] {
				LiferayInputProperties.class
			});
	}

	@Override
	public Class<? extends ComponentProperties> getPropertyClass() {
		return TLiferayInputProperties.class;
	}

	public Property<?>[] getReturnProperties() {
		return new Property[] {
			RETURN_ERROR_MESSAGE_PROP, RETURN_TOTAL_RECORD_COUNT_PROP
		};
	}

	@Override
	public RuntimeInfo getRuntimeInfo(
		ExecutionEngine executionEngine,
		ComponentProperties componentProperties,
		ConnectorTopology connectorTopology) {

		if (connectorTopology != ConnectorTopology.OUTGOING) {
			return null;
		}

		assertEngineCompatibility(executionEngine);

		return getCommonRuntimeInfo(SOURCE_CLASS_NAME);
	}

	@Override
	public Set<ConnectorTopology> getSupportedConnectorTopologies() {
		return EnumSet.of(ConnectorTopology.OUTGOING);
	}

	@Override
	public boolean isStartable() {
		return true;
	}

}