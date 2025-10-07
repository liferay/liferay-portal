/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.lang.reflect.Proxy;

/**
 * @author Michael Hashimoto
 */
public class TestBatchFactory {

	public static TestBatch newTestBatch(
		BatchBuildData batchBuildData, Workspace workspace) {

		TestBatch testBatch = null;

		if (batchBuildData instanceof PortalBatchBuildData) {
			PortalBatchBuildData portalBatchBuildData =
				(PortalBatchBuildData)batchBuildData;

			String batchName = batchBuildData.getBatchName();

			if (batchName.contains("functional")) {
				testBatch = new FunctionalPortalTestBatch(
					portalBatchBuildData, workspace);
			}
			else if (batchName.startsWith("integration") ||
					 batchName.startsWith("modules-integration") ||
					 batchName.startsWith("modules-unit") ||
					 batchName.startsWith("unit")) {

				testBatch = new JunitPortalTestBatch(
					portalBatchBuildData, workspace);
			}
			else if (batchName.startsWith("js-unit") ||
					 batchName.startsWith("modules-compile") ||
					 batchName.startsWith("modules-semantic-versioning") ||
					 batchName.startsWith("rest-builder") ||
					 batchName.startsWith("service-builder")) {

				testBatch = new ModulesPortalTestBatch(
					portalBatchBuildData, workspace);
			}
			else if (batchName.startsWith("playwright-js")) {
				return new PlaywrightPortalTestBatch(
					portalBatchBuildData, workspace);
			}
			else {
				testBatch = new DefaultPortalTestBatch(
					portalBatchBuildData, workspace);
			}
		}

		if (testBatch == null) {
			throw new RuntimeException("Unsupported batch");
		}

		return (TestBatch)Proxy.newProxyInstance(
			TestBatch.class.getClassLoader(), new Class<?>[] {TestBatch.class},
			new MethodLogger(testBatch));
	}

}