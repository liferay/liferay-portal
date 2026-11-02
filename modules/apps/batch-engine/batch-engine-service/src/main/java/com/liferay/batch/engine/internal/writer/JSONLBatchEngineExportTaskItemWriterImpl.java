/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.writer;

import com.fasterxml.jackson.databind.ObjectWriter;

import com.liferay.petra.string.StringPool;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.charset.StandardCharsets;

import java.util.Collection;
import java.util.List;

/**
 * @author Ivica Cardic
 */
public class JSONLBatchEngineExportTaskItemWriterImpl
	implements BatchEngineExportTaskItemWriter {

	public JSONLBatchEngineExportTaskItemWriterImpl(
		List<String> includeFieldNames, OutputStream outputStream) {

		_objectWriter = ObjectWriterFactory.getObjectWriter(includeFieldNames);
		_writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
	}

	@Override
	public void close() throws IOException {
		_writer.close();
	}

	@Override
	public void write(Collection<?> items) throws Exception {
		for (Object item : items) {
			_writer.write(_objectWriter.writeValueAsString(item));
			_writer.write(StringPool.NEW_LINE);
		}
	}

	private final ObjectWriter _objectWriter;
	private final Writer _writer;

}