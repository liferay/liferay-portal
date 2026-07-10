/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.test.util;

import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.io.File;

import java.util.Objects;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Stefano Motta
 */
public class BatchEngineTestUtil {

	public static void processBatchEngineUnits(
		String bundleSymbolicName, Class<?> clazz, String[] fileNames) {

		BatchEngineUnitProcessor batchEngineUnitProcessor =
			_batchEngineUnitProcessorSnapshot.get();
		BatchEngineUnitReader batchEngineUnitReader =
			_batchEngineUnitReaderSnapshot.get();

		Bundle testBundle = FrameworkUtil.getBundle(clazz);

		BundleContext bundleContext = testBundle.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			if (!Objects.equals(bundle.getSymbolicName(), bundleSymbolicName)) {
				continue;
			}

			for (String fileName : fileNames) {
				_deleteFile(bundle, fileName);
			}

			try {
				batchEngineUnitProcessor.processBatchEngineUnits(
					batchEngineUnitReader.getBatchEngineUnits(bundle));
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}
	}

	private static void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			fileName + ".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
		}
	}

	private static final Snapshot<BatchEngineUnitProcessor>
		_batchEngineUnitProcessorSnapshot = new Snapshot<>(
			BatchEngineTestUtil.class, BatchEngineUnitProcessor.class);
	private static final Snapshot<BatchEngineUnitReader>
		_batchEngineUnitReaderSnapshot = new Snapshot<>(
			BatchEngineTestUtil.class, BatchEngineUnitReader.class);

}