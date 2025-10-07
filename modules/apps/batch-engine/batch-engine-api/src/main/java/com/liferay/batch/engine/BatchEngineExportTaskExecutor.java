/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine;

import com.liferay.batch.engine.model.BatchEngineExportTask;

import java.io.InputStream;

/**
 * @author Ivica Cardic
 */
public interface BatchEngineExportTaskExecutor {

	public void execute(BatchEngineExportTask batchEngineExportTask);

	public Result execute(
		BatchEngineExportTask batchEngineExportTask, Settings settings);

	public interface Result {

		public BatchEngineExportTask getBatchEngineExportTask();

		public InputStream getInputStream();

	}

	public interface Settings {

		public default int getMaxItems() {
			return Integer.MAX_VALUE;
		}

		public boolean isCompressContent();

		public boolean isPersist();

	}

}