/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime;

import com.liferay.talend.runtime.reader.LiferayReader;
import com.liferay.talend.tliferayinput.TLiferayInputProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.container.RuntimeContainer;

/**
 * @author Zoltán Takács
 */
public class LiferaySource extends LiferaySourceOrSink implements Source {

	@Override
	public Reader<?> createReader(RuntimeContainer runtimeContainer) {
		if (_logger.isDebugEnabled()) {
			_logger.debug(
				"Creating reader for fetching data from the datastore");
		}

		Object componentData = runtimeContainer.getComponentData(
			runtimeContainer.getCurrentComponentId(),
			"COMPONENT_RUNTIME_PROPERTIES");

		if (!(componentData instanceof TLiferayInputProperties)) {
			throw new IllegalArgumentException(
				String.format(
					"Unable to locate %s in given runtime container",
					TLiferayInputProperties.class));
		}

		return new LiferayReader(this, (TLiferayInputProperties)componentData);
	}

	@Override
	protected String getLiferayConnectionPropertiesPath() {
		return "resource." + super.getLiferayConnectionPropertiesPath();
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		LiferaySource.class);

	private static final long serialVersionUID = 7966201253956643887L;

}