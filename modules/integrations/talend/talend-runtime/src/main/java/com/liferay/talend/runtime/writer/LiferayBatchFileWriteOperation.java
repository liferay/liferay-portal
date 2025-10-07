/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.writer;

import com.liferay.talend.runtime.LiferayBatchFileSink;

import java.util.Map;

import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;

/**
 * @author Igor Beslic
 */
public class LiferayBatchFileWriteOperation implements WriteOperation<Result> {

	public LiferayBatchFileWriteOperation(
		LiferayBatchFileSink liferayBatchFileSink) {

		_liferayBatchFileSink = liferayBatchFileSink;
	}

	@Override
	public Writer<Result> createWriter(RuntimeContainer runtimeContainer) {
		return new LiferayBatchFileWriter(this, runtimeContainer);
	}

	@Override
	public Map<String, Object> finalize(
		Iterable<Result> iterable, RuntimeContainer runtimeContainer) {

		return Result.accumulateAndReturnMap(iterable);
	}

	@Override
	public Sink getSink() {
		return _liferayBatchFileSink;
	}

	@Override
	public void initialize(RuntimeContainer runtimeContainer) {
	}

	private final LiferayBatchFileSink _liferayBatchFileSink;

}