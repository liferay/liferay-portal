/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.util;

import com.liferay.headless.batch.engine.client.dto.v1_0.ExportTask;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.serdes.v1_0.ExportTaskSerDes;
import com.liferay.headless.batch.engine.client.serdes.v1_0.ImportTaskSerDes;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Map;

import org.junit.Assert;

/**
 * @author Alberto Javier Moreno Lage
 */
public class ExportImportTaskUtil {

	public static ExportTask postExportTask(
			String className, String expectedExecuteStatus,
			Map<String, String> parameters)
		throws Exception {

		ExportTask exportTask = ExportTaskSerDes.toDTO(
			HTTPTestUtil.invokeToString(
				null,
				StringBundler.concat(
					"headless-batch-engine/v1.0/export-task/", className,
					"/JSON", _getQueryString(parameters)),
				Http.Method.POST));

		return _pollExportTask(
			exportTask.getExternalReferenceCode(), expectedExecuteStatus);
	}

	public static ImportTask postImportTask(
			String body, String className, String expectedExecuteStatus,
			Map<String, String> parameters)
		throws Exception {

		ImportTask importTask = ImportTaskSerDes.toDTO(
			HTTPTestUtil.invokeToString(
				body,
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task/", className,
					_getQueryString(parameters)),
				Http.Method.POST));

		return _pollImportTask(
			importTask.getExternalReferenceCode(), expectedExecuteStatus);
	}

	public static ImportTask putImportTask(
			String body, String className, String expectedExecuteStatus,
			Map<String, String> parameters)
		throws Exception {

		ImportTask importTask = ImportTaskSerDes.toDTO(
			HTTPTestUtil.invokeToString(
				body,
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task/", className,
					_getQueryString(parameters)),
				Http.Method.PUT));

		return _pollImportTask(
			importTask.getExternalReferenceCode(), expectedExecuteStatus);
	}

	private static String _getQueryString(Map<String, String> parameters) {
		StringBundler sb = new StringBundler();

		if (MapUtil.isNotEmpty(parameters)) {
			sb.append("?");
		}

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}

			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append("&");
		}

		return sb.toString();
	}

	private static ExportTask _pollExportTask(
			String externalReferenceCode, String expectedExecuteStatus)
		throws Exception {

		while (true) {
			ExportTask exportTask = ExportTaskSerDes.toDTO(
				HTTPTestUtil.invokeToString(
					null,
					"headless-batch-engine/v1.0/export-task/by-external-" +
						"reference-code/" + externalReferenceCode,
					Http.Method.GET));

			if (StringUtil.equals(
					exportTask.getExecuteStatusAsString(), "COMPLETED") ||
				StringUtil.equals(
					exportTask.getExecuteStatusAsString(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus,
					exportTask.getExecuteStatusAsString());

				return exportTask;
			}
		}
	}

	private static ImportTask _pollImportTask(
			String externalReferenceCode, String expectedExecuteStatus)
		throws Exception {

		while (true) {
			ImportTask importTask = ImportTaskSerDes.toDTO(
				HTTPTestUtil.invokeToString(
					null,
					"headless-batch-engine/v1.0/import-task/by-external-" +
						"reference-code/" + externalReferenceCode,
					Http.Method.GET));

			if (StringUtil.equals(
					importTask.getExecuteStatusAsString(), "COMPLETED") ||
				StringUtil.equals(
					importTask.getExecuteStatusAsString(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus,
					importTask.getExecuteStatusAsString());

				return importTask;
			}
		}
	}

}