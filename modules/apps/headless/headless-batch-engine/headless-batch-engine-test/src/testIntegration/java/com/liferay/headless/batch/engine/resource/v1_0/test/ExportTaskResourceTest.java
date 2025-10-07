/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.batch.engine.client.dto.v1_0.ExportTask;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker;
import com.liferay.headless.batch.engine.client.resource.v1_0.ExportTaskResource;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateRegistry;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipInputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Modify the value of _testableClassNames to test specific class names.
 *
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class ExportTaskResourceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		if (!_testableClassNames.isEmpty()) {
			return;
		}

		long companyId = TestPropsValues.getCompanyId();

		_testableClassNames = ListUtil.sort(
			TransformUtil.transform(
				_vulcanBatchEngineTaskItemDelegateRegistry.getEntityClassNames(
					companyId),
				className -> {
					if (!_vulcanBatchEngineTaskItemDelegateRegistry.
							isBatchPlannerExportEnabled(companyId, className) ||
						!_vulcanBatchEngineTaskItemDelegateRegistry.
							isBatchPlannerImportEnabled(companyId, className)) {

						return null;
					}

					VulcanBatchEngineTaskItemDelegate
						vulcanBatchEngineTaskItemDelegate =
							_vulcanBatchEngineTaskItemDelegateRegistry.
								getVulcanBatchEngineTaskItemDelegate(
									companyId, className);

					Set<String> availableCreateStrategies =
						vulcanBatchEngineTaskItemDelegate.
							getAvailableCreateStrategies();

					if ((availableCreateStrategies == null) ||
						!availableCreateStrategies.contains("UPSERT") ||
						_untestableDTOClassNames.contains(className) ||
						StringUtil.startsWith(
							className,
							"com.liferay.object.rest.dto.v1_0.ObjectEntry#" +
								"C_")) {

						return null;
					}

					return className;
				}));
	}

	@Before
	public void setUp() throws Exception {
		_logCaptures.add(
			LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineExportTaskExecutorImpl",
				LoggerTestUtil.ERROR));
		_logCaptures.add(
			LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR));

		_contentExportTaskResource = ExportTaskResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).header(
			HttpHeaders.ACCEPT, ContentTypes.APPLICATION_OCTET_STREAM
		).build();

		_exportTaskResource = ExportTaskResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).header(
			HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON
		).parameter(
			"siteId", String.valueOf(TestPropsValues.getGroupId())
		).build();

		_importTaskResource = ImportTaskResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).header(
			HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON
		).header(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON
		).parameter(
			"siteId", String.valueOf(TestPropsValues.getGroupId())
		).build();
	}

	@After
	public void tearDown() {
		_logCaptures.forEach(LogCapture::close);
	}

	@Ignore
	@Test
	public void testPostExportTask() {
		Assert.assertFalse(_testableClassNames.isEmpty());

		StringBundler sb = new StringBundler();

		for (String className : _testableClassNames) {
			try {
				if (_log.isInfoEnabled()) {
					_log.info("Testing " + className);
				}

				_testPostExportTask(className);
			}
			catch (Throwable throwable) {
				sb.append(className);
				sb.append(": ");
				sb.append(throwable.getMessage());
				sb.append("\n");
			}
		}

		if (sb.length() > 0) {
			throw new AssertionError(sb.toString());
		}
	}

	private void _assertExecuteStatusEquals(
			ExportTask.ExecuteStatus expectedExecuteStatus,
			ExportTask exportTask, ExportTaskResource exportTaskResource)
		throws Exception {

		String externalReferenceCode = exportTask.getExternalReferenceCode();

		while (true) {
			exportTask =
				exportTaskResource.getExportTaskByExternalReferenceCode(
					externalReferenceCode);

			ExportTask.ExecuteStatus executeStatus =
				exportTask.getExecuteStatus();

			if ((executeStatus == ExportTask.ExecuteStatus.COMPLETED) ||
				(executeStatus == ExportTask.ExecuteStatus.FAILED)) {

				if (expectedExecuteStatus != executeStatus) {
					throw new AssertionError(exportTask.getErrorMessage());
				}

				break;
			}
		}
	}

	private Map<String, String> _splitClassName(String className) {
		Map<String, String> classNamePartsMap = new HashMap<>();

		if (className.contains("#")) {
			String[] classNameParts = className.split("#");

			classNamePartsMap.put("className", classNameParts[0]);
			classNamePartsMap.put("taskItemDelegateName", classNameParts[1]);
		}
		else {
			classNamePartsMap.put("className", className);
		}

		return classNamePartsMap;
	}

	private void _testPostExportTask(String className) throws Exception {
		Map<String, String> classNamePartsMap = _splitClassName(className);

		ExportTask exportTask = _exportTaskResource.postExportTask(
			classNamePartsMap.get("className"), "jsont", null, null, null, null,
			classNamePartsMap.get("taskItemDelegateName"));

		String externalReferenceCode = exportTask.getExternalReferenceCode();

		_assertExecuteStatusEquals(
			ExportTask.ExecuteStatus.COMPLETED, exportTask,
			_exportTaskResource);

		String json = null;

		HttpInvoker.HttpResponse httpResponse =
			_contentExportTaskResource.
				getExportTaskByExternalReferenceCodeContentHttpResponse(
					externalReferenceCode);

		try (InputStream inputStream = new UnsyncByteArrayInputStream(
				httpResponse.getBinaryContent())) {

			ZipInputStream zipInputStream = new ZipInputStream(inputStream);

			zipInputStream.getNextEntry();

			json = StringUtil.read(zipInputStream);
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(json);

		JSONObject actionsJSONObject = jsonObject.getJSONObject("actions");

		Assert.assertNotNull(actionsJSONObject);

		JSONObject createBatchJSONObject = actionsJSONObject.getJSONObject(
			"createBatch");

		Assert.assertNotNull(createBatchJSONObject);

		Assert.assertNotNull(createBatchJSONObject.getString("href"));

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		ImportTask importTask = _importTaskResource.postImportTask(
			classNamePartsMap.get("className"), null, null, null, "UPSERT",
			null, null, null, classNamePartsMap.get("taskItemDelegateName"),
			itemsJSONArray);

		externalReferenceCode = importTask.getExternalReferenceCode();

		while (true) {
			importTask =
				_importTaskResource.getImportTaskByExternalReferenceCode(
					externalReferenceCode);

			if (Objects.equals(
					importTask.getExecuteStatusAsString(), "COMPLETED")) {

				break;
			}
			else if (Objects.equals(
						importTask.getExecuteStatusAsString(), "FAILED")) {

				throw new AssertionError(
					StringBundler.concat(
						"Unable to import task for ",
						classNamePartsMap.get("className"), ":\n",
						importTask.getErrorMessage()));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportTaskResourceTest.class);

	/**
	 * Modify the value of _testableClassNames to test specific class names.
	 */
	private static Collection<String> _testableClassNames = Arrays.asList(
		//"com.liferay.data.engine.rest.dto.v2_0.DataDefinition",
		//"com.liferay.data.engine.rest.dto.v2_0.DataDefinitionFieldLink"
	);

	private static final List<String> _untestableDTOClassNames = Arrays.asList(
		"com.liferay.headless.admin.user.dto.v1_0.PostalAddress",
		"com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship",
		"com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory",
		"com.liferay.headless.delivery.dto.v1_0.WikiPage");

	@Inject
	private static VulcanBatchEngineTaskItemDelegateRegistry
		_vulcanBatchEngineTaskItemDelegateRegistry;

	private ExportTaskResource _contentExportTaskResource;
	private ExportTaskResource _exportTaskResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Http _http;

	private ImportTaskResource _importTaskResource;

	@Inject
	private JSONFactory _jsonFactory;

	private final List<LogCapture> _logCaptures = new ArrayList<>();

}