/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.writer;

import com.liferay.talend.exception.BaseComponentException;
import com.liferay.talend.properties.output.LiferayOutputProperties;
import com.liferay.talend.runtime.LiferaySink;

import java.util.Map;

import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.container.RuntimeContainer;

/**
 * @author Zoltán Takács
 */
public class LiferayWriteOperation implements WriteOperation<Result> {

	public LiferayWriteOperation(
		LiferaySink liferaySink,
		LiferayOutputProperties liferayOutputProperties) {

		_liferaySink = liferaySink;
		_liferayOutputProperties = liferayOutputProperties;
	}

	@Override
	public LiferayWriter createWriter(RuntimeContainer runtimeContainer) {
		return new LiferayWriter(this, _liferayOutputProperties);
	}

	@Override
	public Map<String, Object> finalize(
		Iterable<Result> writerResults, RuntimeContainer runtimeContainer) {

		return Result.accumulateAndReturnMap(writerResults);
	}

	@Override
	public LiferaySink getSink() {
		return _liferaySink;
	}

	@Override
	public void initialize(RuntimeContainer runtimeContainer) {
		Object componentData = runtimeContainer.getComponentData(
			runtimeContainer.getCurrentComponentId(),
			"COMPONENT_RUNTIME_PROPERTIES");

		if (!(componentData instanceof LiferayOutputProperties)) {
			throw new BaseComponentException(
				"Unable to initialize write operation without Liferay output " +
					"properties",
				1);
		}

		_liferayOutputProperties = (LiferayOutputProperties)componentData;
	}

	private LiferayOutputProperties _liferayOutputProperties;
	private final LiferaySink _liferaySink;

}