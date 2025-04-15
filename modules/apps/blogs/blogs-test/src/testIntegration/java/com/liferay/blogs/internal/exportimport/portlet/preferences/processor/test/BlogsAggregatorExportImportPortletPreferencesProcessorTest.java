/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.GroupUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;
import java.util.Map;

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
public class BlogsAggregatorExportImportPortletPreferencesProcessorTest {

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
			BlogsPortletKeys.BLOGS_AGGREGATOR, "column-1",
			new HashMap<String, String[]>());

		_organization = OrganizationTestUtil.addOrganization();

		_portletDataContextExport =
			ExportImportTestUtil.getExportPortletDataContext(
				_group.getGroupId());

		_portletDataContextExport.setPortletId(
			BlogsPortletKeys.BLOGS_AGGREGATOR);

		_portletDataContextImport =
			ExportImportTestUtil.getImportPortletDataContext(
				_group.getGroupId());

		_portletDataContextImport.setPortletId(
			BlogsPortletKeys.BLOGS_AGGREGATOR);
	}

	@Test
	public void testOrganizationMissingRefValidation() throws Exception {
		StagedModelDataHandler<?> stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				Organization.class.getName());

		Portlet portlet = _portletLocalService.getPortletById(
			_portletDataContextExport.getCompanyId(),
			BlogsPortletKeys.BLOGS_AGGREGATOR);

		_portletDataContextImport.addReferenceElement(
			portlet, _portletDataContextExport.getExportDataRootElement(),
			_organization, PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);

		Assert.assertTrue(
			stagedModelDataHandler.validateReference(
				_portletDataContextImport,
				_portletDataContextImport.getMissingReferenceElement(
					_organization)));

		Group remoteLiveGroup = GroupTestUtil.addGroup();

		try {
			_enableRemoteStaging(remoteLiveGroup, _group);

			remoteLiveGroup = GroupLocalServiceUtil.getGroup(
				remoteLiveGroup.getGroupId());

			Map<String, String[]> parameters =
				ExportImportConfigurationParameterMapFactoryUtil.
					buildFullPublishParameterMap();

			StagingUtil.publishLayouts(
				TestPropsValues.getUserId(), _group.getGroupId(),
				remoteLiveGroup.getGroupId(), false, parameters);

			Assert.assertEquals(1, remoteLiveGroup.getPublicLayoutsPageCount());
		}
		finally {
			GroupTestUtil.deleteGroup(remoteLiveGroup);
		}
	}

	@Test
	public void testProcessOrganizationId() throws Exception {
		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				_layout, BlogsPortletKeys.BLOGS_AGGREGATOR);

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

	private void _enableRemoteStaging(
			Group remoteLiveGroup, Group remoteStagingGroup)
		throws Exception {

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET",
					"F0E1D2C3B4A5968778695A4B3C2D1E0F");
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET_HEX", true)) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);
			serviceContext.setScopeGroupId(remoteStagingGroup.getGroupId());

			_setStagingAttribute(
				serviceContext, PortletDataHandlerKeys.PORTLET_DATA_ALL, false);
			_setStagingAttribute(
				serviceContext, PortletDataHandlerKeys.PORTLET_SETUP_ALL,
				false);

			UserTestUtil.setUser(TestPropsValues.getUser());

			StagingLocalServiceUtil.enableRemoteStaging(
				TestPropsValues.getUserId(), remoteStagingGroup, false, false,
				"localhost", PortalUtil.getPortalServerPort(false),
				PortalUtil.getPathContext(), false,
				remoteLiveGroup.getGroupId(), serviceContext);

			GroupUtil.clearCache();
		}
	}

	private void _setStagingAttribute(
		ServiceContext serviceContext, String key, Object value) {

		serviceContext.setAttribute(
			StagingConstants.STAGED_PREFIX + key + StringPool.DOUBLE_DASH,
			String.valueOf(value));
	}

	@Inject(
		filter = "jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_AGGREGATOR
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

	@Inject
	private PortletLocalService _portletLocalService;

}