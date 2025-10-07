/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime;

import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;

/**
 * @author Igor Beslic
 */
public class LiferayMockRuntimeContainer implements RuntimeContainer {

	public LiferayMockRuntimeContainer(
		ComponentProperties componentProperties) {

		_componentProperties = componentProperties;
	}

	@Override
	public Object getComponentData(String componentId, String key) {
		return _componentProperties;
	}

	@Override
	public String getCurrentComponentId() {
		return _componentProperties.getName();
	}

	@Override
	public Object getGlobalData(String key) {
		return null;
	}

	@Override
	public void setComponentData(String componentId, String key, Object data) {
	}

	private final ComponentProperties _componentProperties;

}