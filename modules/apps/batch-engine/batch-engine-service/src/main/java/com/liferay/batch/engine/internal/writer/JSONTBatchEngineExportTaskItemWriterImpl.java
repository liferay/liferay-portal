/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.writer;

import com.fasterxml.jackson.databind.ObjectWriter;

import com.liferay.batch.engine.unit.BatchEngineUnitConfiguration;
import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.petra.string.StringPool;

import java.io.IOException;
import java.io.OutputStream;

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

		_unsyncPrintWriter = new UnsyncPrintWriter(outputStream);

		_unsyncPrintWriter.write(
			"{\"actions\":\n{\"createBatch\": {\"href\": \"");

		String endpoint =
			"/o/headless-batch-engine/v1.0/import-task/" +
				batchEngineUnitConfiguration.getClassName();

		_unsyncPrintWriter.write(endpoint);

		_unsyncPrintWriter.write(
			"\", \"method\": \"POST\"}, \"deleteBatch\": {\"href\": \"");
		_unsyncPrintWriter.write(endpoint);
		_unsyncPrintWriter.write(
			"\", \"method\": \"DELETE\"}, \"updateBatch\": {\"href\": \"");
		_unsyncPrintWriter.write(endpoint);
		_unsyncPrintWriter.write(
			"\", \"method\": \"PUT\"}},\n\"configuration\":\n");
		_unsyncPrintWriter.write(
			_objectWriter.writeValueAsString(batchEngineUnitConfiguration));
		_unsyncPrintWriter.write(",\n\"items\": [");
	}

	@Override
	public void close() throws IOException {
		_unsyncPrintWriter.write("]\n}");

		_unsyncPrintWriter.flush();

		_unsyncPrintWriter.close();
	}

	@Override
	public void write(Collection<?> items) throws Exception {
		if (_itemsStarted) {
			_unsyncPrintWriter.write(StringPool.COMMA);
		}

		Iterator<?> iterator = items.iterator();

		while (iterator.hasNext()) {
			Object item = iterator.next();

			_unsyncPrintWriter.write(_objectWriter.writeValueAsString(item));

			if (!iterator.hasNext()) {
				continue;
			}

			_unsyncPrintWriter.write(StringPool.COMMA);
		}

		if (!_itemsStarted) {
			_itemsStarted = true;
		}
	}

	private boolean _itemsStarted;
	private final ObjectWriter _objectWriter;
	private final UnsyncPrintWriter _unsyncPrintWriter;

}