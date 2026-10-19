/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@RunWith(Arquillian.class)
public class PortletDataContextExpandoTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_originalSiteDefaultLocale = LocaleThreadLocal.getSiteDefaultLocale();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setSiteDefaultLocale(_originalSiteDefaultLocale);
	}

	@Test
	public void test() throws Exception {
		LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.GERMANY);

		PortletDataContext portletDataContext =
			ExportImportTestUtil.getImportPortletDataContext(
				_group.getGroupId());

		portletDataContext.setZipWriter(
			(ZipWriter)portletDataContext.getZipReader());

		ExportImportConfiguration exportImportConfiguration =
			_addDraftExportImportConfiguration();

		portletDataContext.setExportImportProcessId(
			String.valueOf(
				exportImportConfiguration.getExportImportConfigurationId()));

		String expandoPath = _addExpandoZipEntry(
			portletDataContext,
			HashMapBuilder.<String, Serializable>put(
				"greeting",
				(Serializable)HashMapBuilder.<Locale, String>put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build()
			).build());

		Element element = SAXReaderUtil.createElement("staged-model");

		element.addAttribute("expando-path", expandoPath);

		try {
			portletDataContext.createServiceContext(
				element,
				new TestStagedModel(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomLong()));

			List<ExportImportReportEntry> exportImportReportEntries =
				_exportImportReportEntryLocalService.
					getExportImportReportEntries(
						TestPropsValues.getCompanyId(),
						exportImportConfiguration.
							getExportImportConfigurationId());

			Assert.assertEquals(
				exportImportReportEntries.toString(), 1,
				exportImportReportEntries.size());

			ExportImportReportEntry exportImportReportEntry =
				exportImportReportEntries.get(0);

			Assert.assertEquals(
				ExportImportReportEntryConstants.TYPE_WARNING,
				exportImportReportEntry.getType());

			String errorMessage = exportImportReportEntry.getErrorMessage();

			Assert.assertTrue(errorMessage, errorMessage.contains("greeting"));
		}
		finally {
			_exportImportConfigurationLocalService.
				deleteExportImportConfiguration(exportImportConfiguration);
		}
	}

	private ExportImportConfiguration _addDraftExportImportConfiguration()
		throws Exception {

		return _exportImportConfigurationLocalService.
			addDraftExportImportConfiguration(
				_group.getCreatorUserId(), RandomTestUtil.randomString(),
				ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
				Collections.emptyMap());
	}

	private String _addExpandoZipEntry(
		PortletDataContext portletDataContext,
		Map<String, Serializable> expandoBridgeAttributes) {

		String expandoPath =
			"/expando/test-" + RandomTestUtil.randomString() + ".xml";

		portletDataContext.addZipEntry(expandoPath, expandoBridgeAttributes);

		return expandoPath;
	}

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Locale _originalSiteDefaultLocale;

	private static class TestStagedModel
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

}