/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.unit.BatchEngineUnitConfiguration;
import com.liferay.batch.engine.unit.BatchEngineUnitMetaInfo;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.batch.engine.unit.BundleBatchEngineUnit;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.DBUpgrader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.net.URL;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.commons.lang.time.StopWatch;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class BatchEngineBundleTrackerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_originalStopWatch = ReflectionTestUtil.getAndSetFieldValue(
			DBUpgrader.class, "_stopWatch", null);
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			DBUpgrader.class, "_stopWatch", _originalStopWatch);
	}

	@Before
	public void setUp() {
		_bundle = FrameworkUtil.getBundle(BatchEngineBundleTrackerTest.class);

		_bundleContext = _bundle.getBundleContext();
	}

	@Test
	public void testProcessBatchEngineBundle() throws Exception {
		_testProcessBatchEngineBundle(null, "batch1", "/batch1/export.json");
		_testProcessBatchEngineBundle(null, "batch2");
		_testProcessBatchEngineBundle(
			null, "batch3", "/batch3/batch1/export.json",
			"/batch3/batch2/export.json");
		_testProcessBatchEngineBundle(
			null, "batch4", "/batch4/batch1/export.json",
			"/batch4/batch2/export.json", "/batch4/batch2/batch3/export.json");
		_testProcessBatchEngineBundle(
			null, "batch5", "/batch5/data.batch-engine-data.json");
		_testProcessBatchEngineBundle(
			null, "batch6", "/batch6/1data.batch-engine-data.json",
			"/batch6/2data.batch-engine-data.json");
		_testProcessBatchEngineBundle(null, "batch7", "/batch7/export.json");
		_testProcessBatchEngineBundle(
			null, "batch8", "/batch8/1data.batch-engine-data.json",
			"/batch8/2data.batch-engine-data.json",
			"/batch8/10data.batch-engine-data.json");
		_testProcessBatchEngineBundle(
			null, "batch9", "/batch9/data.batch-engine-data.json");

		_company = CompanyTestUtil.addCompany(true);

		List<User> users = _userLocalService.getUsersByRoleName(
			_company.getCompanyId(), RoleConstants.ADMINISTRATOR, 0, 1);

		User user = users.get(0);

		user.setScreenName(RandomTestUtil.randomString());

		_userLocalService.updateUser(user);

		_testProcessBatchEngineBundle(
			null, "batch9", "/batch9/data.batch-engine-data.json",
			"/batch9/data.batch-engine-data.json");
	}

	@Test
	public void testProcessBatchEngineBundleOnUpgrade() throws Exception {
		boolean upgradeClient = ReflectionTestUtil.getAndSetFieldValue(
			DBUpgrader.class, "_upgradeClient", false);

		try {
			ReflectionTestUtil.setFieldValue(
				DBUpgrader.class, "_upgradeClient", true);

			_testProcessBatchEngineBundle(null, "batch1");
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				DBUpgrader.class, "_upgradeClient", upgradeClient);
		}
	}

	@Test
	@TestInfo("LPD-61755")
	public void testProcessBatchEngineBundleUsesActiveAdministratorUser()
		throws Exception {

		AtomicReference<BatchEngineImportTask> atomicReference =
			new AtomicReference<>();

		_testProcessBatchEngineBundle(
			atomicReference::set, "batch11",
			"/batch11/data.batch-engine-data.json");

		BatchEngineImportTask batchEngineImportTask = atomicReference.get();

		long userId = batchEngineImportTask.getUserId();

		User user = _userLocalService.getUser(userId);

		Assert.assertTrue(user.isActive());

		int status = user.getStatus();

		user = _userLocalService.updateStatus(
			user, WorkflowConstants.STATUS_INACTIVE, new ServiceContext());

		Assert.assertFalse(user.isActive());

		User adminUser = UserTestUtil.addCompanyAdminUser(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Assert.assertTrue(adminUser.isActive());

		_testProcessBatchEngineBundle(
			atomicReference::set, "batch11",
			"/batch11/data.batch-engine-data.json");

		batchEngineImportTask = atomicReference.get();

		user = _userLocalService.getUser(batchEngineImportTask.getUserId());

		Assert.assertTrue(user.isActive());
		Assert.assertTrue(
			ListUtil.exists(
				_roleLocalService.getUserRoles(user.getUserId()),
				role -> RoleConstants.ADMINISTRATOR.equals(role.getName())));

		_userLocalService.updateStatus(userId, status, new ServiceContext());
	}

	@Test
	public void testProcessBatchEngineBundleVirtualInstanceId()
		throws Exception {

		String webId = "batch10.liferay.virtual.instance.id";

		Company company = CompanyLocalServiceUtil.addCompany(
			null, webId, webId, webId, 0, true, true, null, null, null, null,
			null, null);

		_testProcessBatchEngineBundle(
			batchEngineImportTask -> Assert.assertEquals(
				batchEngineImportTask.getCompanyId(), company.getCompanyId()),
			"batch10", "/batch10/data.batch-engine-data.json");
	}

	private String _getDataFileName(
		BatchEngineImportTask batchEngineImportTask) {

		return batchEngineImportTask.getParameterValue("dataFileName");
	}

	private void _testProcessBatchEngineBundle(
			Consumer<BatchEngineImportTask> consumer, String dirName,
			String... expectedDataFileNames)
		throws Exception {

		ComponentDescriptionDTO componentDescriptionDTO1 =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(
					_batchEngineImportTaskExecutor.getClass()),
				ClassUtil.getClassName(_batchEngineImportTaskExecutor));

		Promise<Void> promise = _serviceComponentRuntime.disableComponent(
			componentDescriptionDTO1);

		promise.getValue();

		ComponentDescriptionDTO componentDescriptionDTO2 =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(_batchEngineUnitReader.getClass()),
				ClassUtil.getClassName(_batchEngineUnitReader));

		promise = _serviceComponentRuntime.disableComponent(
			componentDescriptionDTO2);

		promise.getValue();

		List<String> processedDataFileNames = new CopyOnWriteArrayList<>();

		ServiceRegistration<BatchEngineImportTaskExecutor>
			serviceRegistration1 = _bundleContext.registerService(
				BatchEngineImportTaskExecutor.class,
				new BatchEngineImportTaskExecutor() {

					@Override
					public void execute(
						BatchEngineImportTask batchEngineImportTask) {

						if (consumer != null) {
							consumer.accept(batchEngineImportTask);
						}

						String dataFileName = _getDataFileName(
							batchEngineImportTask);

						if (dataFileName != null) {
							processedDataFileNames.add(dataFileName);
						}
					}

					@Override
					public void execute(
						BatchEngineImportTask batchEngineImportTask,
						BatchEngineTaskItemDelegate<?>
							batchEngineTaskItemDelegate,
						boolean checkPermissions) {

						if (consumer != null) {
							consumer.accept(batchEngineImportTask);
						}

						String dataFileName = _getDataFileName(
							batchEngineImportTask);

						if (dataFileName != null) {
							processedDataFileNames.add(dataFileName);
						}
					}

				},
				null);

		ServiceRegistration<BatchEngineUnitReader> serviceRegistration2 =
			_bundleContext.registerService(
				BatchEngineUnitReader.class,
				bundle -> TransformUtil.transform(
					_batchEngineUnitReader.getBatchEngineUnits(bundle),
					batchEngineUnit -> {
						if (batchEngineUnit instanceof BundleBatchEngineUnit) {
							return new BundleBatchEngineUnitWrapper(
								(BundleBatchEngineUnit)batchEngineUnit,
								dirName);
						}

						return batchEngineUnit;
					}),
				null);

		Bundle bundle = _bundleContext.installBundle(
			RandomTestUtil.randomString(), _toInputStream(dirName));

		try {
			bundle.start();

			Thread.sleep(2000);

			Assert.assertEquals(
				Arrays.asList(expectedDataFileNames), processedDataFileNames);

			bundle.stop();

			bundle.start();

			Thread.sleep(2000);

			Assert.assertEquals(
				processedDataFileNames.toString(), expectedDataFileNames.length,
				processedDataFileNames.size());
		}
		finally {
			bundle.uninstall();

			serviceRegistration1.unregister();
			serviceRegistration2.unregister();

			promise = _serviceComponentRuntime.enableComponent(
				componentDescriptionDTO1);

			promise.getValue();

			promise = _serviceComponentRuntime.enableComponent(
				componentDescriptionDTO2);

			promise.getValue();
		}
	}

	private InputStream _toInputStream(String dirName) throws Exception {
		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		String basePath = StringBundler.concat(
			"com/liferay/batch/engine/internal/test/dependencies/", dirName,
			StringPool.SLASH);

		Enumeration<URL> enumeration = _bundle.findEntries(basePath, "*", true);

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				String urlPath = url.getPath();

				if (urlPath.endsWith(StringPool.SLASH)) {
					continue;
				}

				String zipPath = urlPath.substring(basePath.length());

				if (zipPath.startsWith(StringPool.SLASH)) {
					zipPath = zipPath.substring(1);
				}

				try (InputStream inputStream = url.openStream()) {
					zipWriter.addEntry(zipPath, inputStream);
				}
			}
		}

		return new FileInputStream(zipWriter.getFile());
	}

	private static StopWatch _originalStopWatch;

	@Inject
	private BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	private Bundle _bundle;
	private BundleContext _bundleContext;

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

	private class BundleBatchEngineUnitWrapper
		implements BundleBatchEngineUnit {

		public BundleBatchEngineUnitWrapper(
			BundleBatchEngineUnit bundleBatchEngineUnit, String dirName) {

			_bundleBatchEngineUnit = bundleBatchEngineUnit;
			_dirName = dirName;
		}

		@Override
		public BatchEngineUnitConfiguration getBatchEngineUnitConfiguration()
			throws IOException {

			BatchEngineUnitConfiguration batchEngineUnitConfiguration =
				_bundleBatchEngineUnit.getBatchEngineUnitConfiguration();

			if (!StringUtil.startsWith(
					_bundleBatchEngineUnit.getFileName(), _dirName)) {

				return batchEngineUnitConfiguration;
			}

			Map<String, Serializable> parameters =
				batchEngineUnitConfiguration.getParameters();

			if (parameters == null) {
				parameters = new HashMap<>();
			}

			parameters.put(
				"dataFileName", _bundleBatchEngineUnit.getDataFileName());

			batchEngineUnitConfiguration.setParameters(parameters);

			return batchEngineUnitConfiguration;
		}

		@Override
		public BatchEngineUnitMetaInfo getBatchEngineUnitMetaInfo()
			throws IOException {

			return _bundleBatchEngineUnit.getBatchEngineUnitMetaInfo();
		}

		@Override
		public Bundle getBundle() {
			return _bundleBatchEngineUnit.getBundle();
		}

		@Override
		public InputStream getConfigurationInputStream() throws IOException {
			return _bundleBatchEngineUnit.getConfigurationInputStream();
		}

		@Override
		public String getDataFileName() {
			return _bundleBatchEngineUnit.getDataFileName();
		}

		@Override
		public InputStream getDataInputStream() throws IOException {
			return _bundleBatchEngineUnit.getDataInputStream();
		}

		@Override
		public String getFileName() {
			return _bundleBatchEngineUnit.getFileName();
		}

		@Override
		public boolean isValid() {
			return _bundleBatchEngineUnit.isValid();
		}

		private final BundleBatchEngineUnit _bundleBatchEngineUnit;
		private final String _dirName;

	}

}