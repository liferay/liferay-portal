/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.writer;

import com.fasterxml.jackson.databind.ObjectWriter;

import com.liferay.batch.engine.unit.BatchEngineUnitConfiguration;
import com.liferay.petra.string.StringPool;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.charset.StandardCharsets;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Igor Beslic
 */
public class JSONTBatchEngineExportTaskItemWriterImpl
	implements BatchEngineExportTaskItemWriter {

	public JSONTBatchEngineExportTaskItemWriterImpl(
			BatchEngineUnitConfiguration batchEngineUnitConfiguration,
			List<String> includeFieldNames, OutputStream outputStream)
		throws IOException {

		_objectWriter = ObjectWriterFactory.getObjectWriter(includeFieldNames);

		_writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

		_writer.write("{\"actions\":\n{\"createBatch\": {\"href\": \"");

		String endpoint =
			"/o/headless-batch-engine/v1.0/import-task/" +
				batchEngineUnitConfiguration.getClassName();

		_writer.write(endpoint);

		_writer.write(
			"\", \"method\": \"POST\"}, \"deleteBatch\": {\"href\": \"");
		_writer.write(endpoint);
		_writer.write(
			"\", \"method\": \"DELETE\"}, \"updateBatch\": {\"href\": \"");
		_writer.write(endpoint);
		_writer.write("\", \"method\": \"PUT\"}},\n\"configuration\":\n");
		_writer.write(
			_objectWriter.writeValueAsString(batchEngineUnitConfiguration));
		_writer.write(",\n\"items\": [");
	}

	@Override
	public void close() throws IOException {
		_writer.write("]\n}");

		_writer.flush();

		_writer.close();
	}

	@Override
	public void write(Collection<?> items) throws Exception {
		if (_itemsStarted) {
			_writer.write(StringPool.COMMA);
		}

		Iterator<?> iterator = items.iterator();

		while (iterator.hasNext()) {
			Object item = iterator.next();

			_writer.write(_objectWriter.writeValueAsString(item));

			if (!iterator.hasNext()) {
				continue;
			}

			_writer.write(StringPool.COMMA);
		}

		if (!_itemsStarted) {
			_itemsStarted = true;
		}
	}

	private boolean _itemsStarted;
	private final ObjectWriter _objectWriter;
	private final Writer _writer;

}