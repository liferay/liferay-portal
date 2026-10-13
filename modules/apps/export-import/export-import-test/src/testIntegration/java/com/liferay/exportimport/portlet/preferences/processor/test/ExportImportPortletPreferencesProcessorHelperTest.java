/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabor Komaromi
 */
@RunWith(Arquillian.class)
public class ExportImportPortletPreferencesProcessorHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-109596")
	public void testGetGroupExportPortletPreferencesExternalReferenceCodeWithCompanyGroup()
		throws Exception {

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			Assert.assertEquals(
				_companyGroup.getExternalReferenceCode(),
				_exportImportPortletPreferencesProcessorHelper.
					getGroupExportPortletPreferencesExternalReferenceCode(
						TestPropsValues.getCompanyId(),
						_companyGroup.getExternalReferenceCode()));
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
	}

	@Test
	@TestInfo("LPD-109596")
	public void testGetGroupExportPortletPreferencesExternalReferenceCodeWithCompanyGroupWithoutStaging()
		throws Exception {

		Assert.assertEquals(
			_companyGroup.getExternalReferenceCode(),
			_exportImportPortletPreferencesProcessorHelper.
				getGroupExportPortletPreferencesExternalReferenceCode(
					TestPropsValues.getCompanyId(),
					_companyGroup.getExternalReferenceCode()));
	}

	@Test
	@TestInfo("LPD-109596")
	public void testGetGroupExportPortletPreferencesExternalReferenceCodeWithSiteGroup()
		throws Exception {

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			Assert.assertEquals(
				_group.getExternalReferenceCode(),
				_exportImportPortletPreferencesProcessorHelper.
					getGroupExportPortletPreferencesExternalReferenceCode(
						TestPropsValues.getCompanyId(),
						_group.getExternalReferenceCode()));
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
	}

	@Test
	@TestInfo("LPD-109596")
	public void testGetGroupImportPortletPreferencesExternalReferenceCode()
		throws Exception {

		String sourceCompanyGroupExternalReferenceCode =
			RandomTestUtil.randomString();

		PortletDataContext portletDataContext = _getImportPortletDataContext(
			sourceCompanyGroupExternalReferenceCode);

		Assert.assertEquals(
			_companyGroup.getExternalReferenceCode(),
			_exportImportPortletPreferencesProcessorHelper.
				getGroupImportPortletPreferencesExternalReferenceCode(
					portletDataContext,
					sourceCompanyGroupExternalReferenceCode));
		Assert.assertEquals(
			_group.getExternalReferenceCode(),
			_exportImportPortletPreferencesProcessorHelper.
				getGroupImportPortletPreferencesExternalReferenceCode(
					portletDataContext, _group.getExternalReferenceCode()));
	}

	@Test
	@TestInfo("LPD-109596")
	public void testGetGroupImportPortletPreferencesExternalReferenceCodeWithoutPortletDataContext()
		throws Exception {

		String sourceCompanyGroupExternalReferenceCode =
			RandomTestUtil.randomString();

		Assert.assertEquals(
			sourceCompanyGroupExternalReferenceCode,
			_exportImportPortletPreferencesProcessorHelper.
				getGroupImportPortletPreferencesExternalReferenceCode(
					TestPropsValues.getCompanyId(),
					sourceCompanyGroupExternalReferenceCode));
	}

	@Test
	@TestInfo("LPD-109596")
	public void testUpdateGroupExportPortletPreferencesExternalReferenceCode()
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromDefaultXML(null);

		String preferenceKey = RandomTestUtil.randomString();

		portletPreferences.setValue(
			preferenceKey, _companyGroup.getExternalReferenceCode());

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			_exportImportPortletPreferencesProcessorHelper.
				updateGroupExportPortletPreferencesExternalReferenceCode(
					TestPropsValues.getCompanyId(), preferenceKey,
					portletPreferences);
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}

		Assert.assertEquals(
			_companyGroup.getExternalReferenceCode(),
			portletPreferences.getValue(preferenceKey, null));
	}

	@Test
	@TestInfo("LPD-109596")
	public void testUpdateGroupImportPortletPreferencesExternalReferenceCode()
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromDefaultXML(null);

		String preferenceKey = RandomTestUtil.randomString();
		String sourceCompanyGroupExternalReferenceCode =
			RandomTestUtil.randomString();

		portletPreferences.setValue(
			preferenceKey, sourceCompanyGroupExternalReferenceCode);

		_exportImportPortletPreferencesProcessorHelper.
			updateGroupImportPortletPreferencesExternalReferenceCode(
				_getImportPortletDataContext(
					sourceCompanyGroupExternalReferenceCode),
				preferenceKey, portletPreferences);

		Assert.assertEquals(
			_companyGroup.getExternalReferenceCode(),
			portletPreferences.getValue(preferenceKey, null));
	}

	private PortletDataContext _getImportPortletDataContext(
			String sourceCompanyGroupExternalReferenceCode)
		throws Exception {

		return ExportImportTestUtil.getImportPortletDataContext(
			TestPropsValues.getCompanyId(), _group.getGroupId(),
			new HashMap<>(), sourceCompanyGroupExternalReferenceCode);
	}

	private Group _companyGroup;

	@Inject
	private ExportImportPortletPreferencesProcessorHelper
		_exportImportPortletPreferencesProcessorHelper;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

}