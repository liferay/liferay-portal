/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.exception.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.lar.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alvaro Saugar
 */
@RunWith(Arquillian.class)
public class ImportStagedModelExceptionHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void test() throws Exception {
		_test(0L, "company", _companyGroup);
		_test(_group.getGroupId(), "site", _group);
	}

	private void _test(long expectedGroupId, String expectedScope, Group group)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ImportStagedModelExceptionHandlerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String errorMessage = RandomTestUtil.randomString();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				StagedModelDataHandler.class,
				new TestStagedModelDataHandler(errorMessage),
				MapUtil.singletonDictionary("companyId", group.getCompanyId()));

		PortletDataContext portletDataContext =
			ExportImportTestUtil.getImportPortletDataContext(
				group.getGroupId());

		long exportImportProcessId = RandomTestUtil.randomLong();

		portletDataContext.setExportImportProcessId(
			String.valueOf(exportImportProcessId));

		long exportImportReportEntriesCount =
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount();

		String externalReferenceCode = RandomTestUtil.randomString();
		long classPK = RandomTestUtil.randomLong();

		try {
			ExportImportThreadLocal.setExportImportConfigurationId(classPK);
			ExportImportThreadLocal.setPortletImportInProcess(true);

			TransactionInvokerUtil.invoke(
				TransactionConfig.Factory.create(
					Propagation.REQUIRED, new Class<?>[] {Exception.class}),
				() -> {
					StagedModelDataHandlerUtil.importStagedModel(
						portletDataContext,
						new TestStagedModel(externalReferenceCode, classPK));

					return null;
				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			PortletDataException portletDataException =
				(PortletDataException)throwable;

			Assert.assertEquals(
				errorMessage, portletDataException.getMessage());
		}
		finally {
			ExportImportThreadLocal.setPortletImportInProcess(false);

			serviceRegistration.unregister();
		}

		Assert.assertEquals(
			exportImportReportEntriesCount + 1,
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount());

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(), exportImportProcessId);

		Assert.assertEquals(
			exportImportReportEntries.toString(), 1,
			exportImportReportEntries.size());

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntries.get(0);

		Assert.assertEquals(
			expectedGroupId, exportImportReportEntry.getGroupId());
		Assert.assertEquals(
			TestPropsValues.getCompanyId(),
			exportImportReportEntry.getCompanyId());
		Assert.assertEquals(
			exportImportProcessId,
			exportImportReportEntry.getExportImportConfigurationId());
		Assert.assertEquals(
			externalReferenceCode,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			_classNameLocalService.getClassNameId(TestStagedModel.class),
			exportImportReportEntry.getClassNameId());
		Assert.assertEquals(classPK, exportImportReportEntry.getClassPK());
		Assert.assertEquals(
			errorMessage, exportImportReportEntry.getErrorMessage());
		Assert.assertNotNull(exportImportReportEntry.getErrorStacktrace());
		Assert.assertEquals(
			TestStagedModel.class.getName(),
			exportImportReportEntry.getModelName());
		Assert.assertEquals(
			ExportImportReportEntryConstants.ORIGIN_STAGING,
			exportImportReportEntry.getOrigin());
		Assert.assertEquals(expectedScope, exportImportReportEntry.getScope());
		Assert.assertEquals(
			group.getExternalReferenceCode(),
			exportImportReportEntry.getScopeKey());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private Group _companyGroup;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private class TestStagedModel
		implements ExternalReferenceCodeModel, StagedModel {

		public TestStagedModel(String externalReferenceCode, long primaryKey) {
			_externalReferenceCode = externalReferenceCode;
			_primaryKey = primaryKey;
		}

		@Override
		public Object clone() {
			return null;
		}

		@Override
		public long getCompanyId() {
			return 0;
		}

		@Override
		public Date getCreateDate() {
			return null;
		}

		@Override
		public ExpandoBridge getExpandoBridge() {
			return null;
		}

		@Override
		public String getExternalReferenceCode() {
			return _externalReferenceCode;
		}

		@Override
		public Class<?> getModelClass() {
			return TestStagedModel.class;
		}

		@Override
		public String getModelClassName() {
			return TestStagedModel.class.getName();
		}

		@Override
		public Date getModifiedDate() {
			return null;
		}

		@Override
		public Serializable getPrimaryKeyObj() {
			return _primaryKey;
		}

		@Override
		public StagedModelType getStagedModelType() {
			return new StagedModelType(
				PortalUtil.getClassNameId(TestStagedModel.class.getName()),
				PortalUtil.getClassNameId(TestStagedModel.class.getName()));
		}

		@Override
		public String getUuid() {
			return null;
		}

		@Override
		public void setCompanyId(long companyId) {
		}

		@Override
		public void setCreateDate(Date date) {
		}

		@Override
		public void setExternalReferenceCode(String externalReferenceCode) {
		}

		@Override
		public void setModifiedDate(Date date) {
		}

		@Override
		public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		}

		@Override
		public void setUuid(String uuid) {
		}

		private final String _externalReferenceCode;
		private final long _primaryKey;

	}

	private class TestStagedModelDataHandler
		extends BaseStagedModelDataHandler<TestStagedModel> {

		public static final String[] CLASS_NAMES = {
			TestStagedModel.class.getName()
		};

		public TestStagedModelDataHandler(String errorMessage) {
			_errorMessage = errorMessage;
		}

		@Override
		public void deleteStagedModel(
				String uuid, long groupId, String className, String extraData)
			throws PortalException {
		}

		@Override
		public void deleteStagedModel(TestStagedModel stagedModel)
			throws PortalException {
		}

		@Override
		public List<TestStagedModel> fetchStagedModelsByUuidAndCompanyId(
			String uuid, long companyId) {

			return null;
		}

		@Override
		public String[] getClassNames() {
			return CLASS_NAMES;
		}

		@Override
		public void importStagedModel(
				PortletDataContext portletDataContext,
				TestStagedModel stagedModel)
			throws PortletDataException {

			throw new PortletDataException(_errorMessage);
		}

		@Override
		protected void doExportStagedModel(
				PortletDataContext portletDataContext,
				TestStagedModel stagedModel)
			throws Exception {
		}

		@Override
		protected void doImportStagedModel(
				PortletDataContext portletDataContext,
				TestStagedModel stagedModel)
			throws Exception {
		}

		private final String _errorMessage;

	}

}