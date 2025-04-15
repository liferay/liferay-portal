/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.recent.bloggers.web.internal.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.recent.bloggers.constants.RecentBloggersPortletKeys;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
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
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
public class RecentBloggersExportImportPortletPreferencesProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layout,
			RecentBloggersPortletKeys.RECENT_BLOGGERS, "column-1",
			new HashMap<String, String[]>());

		_organization = OrganizationTestUtil.addOrganization();

		_portletDataContextExport =
			ExportImportTestUtil.getExportPortletDataContext(
				_group.getGroupId());

		_portletDataContextExport.setPortletId(
			RecentBloggersPortletKeys.RECENT_BLOGGERS);

		_portletDataContextImport =
			ExportImportTestUtil.getImportPortletDataContext(
				_group.getGroupId());

		_portletDataContextImport.setPortletId(
			RecentBloggersPortletKeys.RECENT_BLOGGERS);
	}

	@Test
	public void testProcessOrganizationId() throws Exception {
		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				_layout, RecentBloggersPortletKeys.RECENT_BLOGGERS);

		portletPreferences.setValue(
			"organizationId",
			String.valueOf(_organization.getOrganizationId()));

		portletPreferences.store();

		PortletPreferences exportedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processExportPortletPreferences(
					_portletDataContextExport, portletPreferences);

		String exportedOrganizationId = exportedPortletPreferences.getValue(
			"organizationId", "");

		Assert.assertEquals(_organization.getUuid(), exportedOrganizationId);

		// Update organization to have a different primary key. We will swap
		// to the new one and verify it.

		OrganizationLocalServiceUtil.deleteOrganization(
			_organization.getOrganizationId());

		_organization = OrganizationTestUtil.addOrganization();

		_organization.setUuid(exportedOrganizationId);

		_organization = OrganizationLocalServiceUtil.updateOrganization(
			_organization);

		// Test the import

		PortletPreferences importedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processImportPortletPreferences(
					_portletDataContextImport, exportedPortletPreferences);

		String importedOrganizationId = importedPortletPreferences.getValue(
			"organizationId", "");

		Assert.assertEquals(
			_organization.getOrganizationId(),
			GetterUtil.getLong(importedOrganizationId));
	}

	@Inject(
		filter = "jakarta.portlet.name=" + RecentBloggersPortletKeys.RECENT_BLOGGERS
	)
	private ExportImportPortletPreferencesProcessor
		_exportImportPortletPreferencesProcessor;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@DeleteAfterTestRun
	private Organization _organization;

	private PortletDataContext _portletDataContextExport;
	private PortletDataContext _portletDataContextImport;

}