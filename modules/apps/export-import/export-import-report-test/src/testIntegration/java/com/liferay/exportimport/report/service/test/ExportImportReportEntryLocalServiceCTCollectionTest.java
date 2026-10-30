/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class ExportImportReportEntryLocalServiceCTCollectionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(Propagation.REQUIRED));

	@Test
	@TestInfo("LPD-113082")
	public void testResolveEmptyExportImportReportEntries() throws Exception {
		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long companyId = TestPropsValues.getCompanyId();
		long groupId = TestPropsValues.getGroupId();

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, RandomTestUtil.randomLong(),
					ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
					RandomTestUtil.randomString());

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, companyId, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_exportImportReportEntryLocalService.
				resolveEmptyExportImportReportEntries(
					groupId, companyId, classExternalReferenceCode,
					classNameId);
		}

		exportImportReportEntry =
			_exportImportReportEntryLocalService.getExportImportReportEntry(
				exportImportReportEntry.getExportImportReportEntryId());

		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_RESOLVED,
			exportImportReportEntry.getStatus());

		_ctCollectionLocalService.deleteCTCollection(ctCollection);
	}

	@Test
	@TestInfo("LPD-113082")
	public void testResolveMissingReferenceExportImportReportEntries()
		throws Exception {

		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long companyId = TestPropsValues.getCompanyId();
		long groupId = TestPropsValues.getGroupId();

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					0, RandomTestUtil.randomLong(),
					ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE,
					null, null, RandomTestUtil.randomString());

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, companyId, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_exportImportReportEntryLocalService.
				resolveMissingReferenceExportImportReportEntries(
					groupId, companyId, classExternalReferenceCode,
					classNameId);
		}

		exportImportReportEntry =
			_exportImportReportEntryLocalService.getExportImportReportEntry(
				exportImportReportEntry.getExportImportReportEntryId());

		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_RESOLVED,
			exportImportReportEntry.getStatus());

		_ctCollectionLocalService.deleteCTCollection(ctCollection);
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

}