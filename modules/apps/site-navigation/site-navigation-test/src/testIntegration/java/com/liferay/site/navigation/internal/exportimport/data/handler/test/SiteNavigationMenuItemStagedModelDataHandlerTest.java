/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.test.util.SiteNavigationMenuItemTestUtil;
import com.liferay.site.navigation.test.util.SiteNavigationMenuTestUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class SiteNavigationMenuItemStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testImportedExpandoValue() throws Exception {
		String customFieldName = RandomTestUtil.randomString();

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			ExpandoTestUtil.addTable(
				PortalUtil.getClassNameId(SiteNavigationMenuItem.class),
				ExpandoTableConstants.DEFAULT_TABLE_NAME),
			customFieldName, ExpandoColumnConstants.STRING);

		String customFieldValue = RandomTestUtil.randomString();

		SiteNavigationMenuItem siteNavigationMenuItem =
			(SiteNavigationMenuItem)addStagedModel(
				stagingGroup, new HashMap<>());

		siteNavigationMenuItem = _updateSiteNavigationMenuItemExpandoValue(
			customFieldValue, expandoColumn.getName(), siteNavigationMenuItem);

		_exportSiteNavigationMenuItem(siteNavigationMenuItem);

		SiteNavigationMenuItem importedSiteNavigationMenuItem =
			_importSiteNavigationMenuItem(siteNavigationMenuItem);

		ExpandoBridge expandoBridge =
			importedSiteNavigationMenuItem.getExpandoBridge();

		Assert.assertEquals(
			customFieldValue, expandoBridge.getAttribute(customFieldName));

		String updatedCustomFieldValue = RandomTestUtil.randomString();

		siteNavigationMenuItem = _updateSiteNavigationMenuItemExpandoValue(
			updatedCustomFieldValue, expandoColumn.getName(),
			siteNavigationMenuItem);

		_exportSiteNavigationMenuItem(siteNavigationMenuItem);

		importedSiteNavigationMenuItem = _importSiteNavigationMenuItem(
			siteNavigationMenuItem);

		expandoBridge = importedSiteNavigationMenuItem.getExpandoBridge();

		Assert.assertEquals(
			updatedCustomFieldValue,
			expandoBridge.getAttribute(customFieldName));
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuTestUtil.addSiteNavigationMenu(group);

		return SiteNavigationMenuItemTestUtil.addSiteNavigationMenuItem(
			siteNavigationMenu);
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return _siteNavigationMenuItemLocalService.
			getSiteNavigationMenuItemByUuidAndGroupId(uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return SiteNavigationMenuItem.class;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		SiteNavigationMenuItem siteNavigationMenuItem =
			(SiteNavigationMenuItem)stagedModel;
		SiteNavigationMenuItem importedSiteNavigationMenuItem =
			(SiteNavigationMenuItem)importedStagedModel;

		Assert.assertEquals(
			siteNavigationMenuItem.getName(),
			importedSiteNavigationMenuItem.getName());
		Assert.assertEquals(
			siteNavigationMenuItem.getType(),
			importedSiteNavigationMenuItem.getType());
		Assert.assertEquals(
			siteNavigationMenuItem.getOrder(),
			importedSiteNavigationMenuItem.getOrder());
		Assert.assertEquals(
			siteNavigationMenuItem.getTypeSettings(),
			importedSiteNavigationMenuItem.getTypeSettings());
	}

	private void _exportSiteNavigationMenuItem(
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws Exception {

		initExport();

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, siteNavigationMenuItem);
	}

	private SiteNavigationMenuItem _importSiteNavigationMenuItem(
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws Exception {

		initImport();

		SiteNavigationMenuItem exportedSiteNavigationMenuItem =
			(SiteNavigationMenuItem)readExportedStagedModel(
				siteNavigationMenuItem);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedSiteNavigationMenuItem);

		return (SiteNavigationMenuItem)getStagedModel(
			siteNavigationMenuItem.getUuid(), liveGroup);
	}

	private SiteNavigationMenuItem _updateSiteNavigationMenuItemExpandoValue(
			String customFieldValue, String expandoColumnName,
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			HashMapBuilder.<String, Serializable>put(
				expandoColumnName, customFieldValue
			).build());

		return _siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
			siteNavigationMenuItem.getUserId(),
			siteNavigationMenuItem.getSiteNavigationMenuItemId(),
			siteNavigationMenuItem.getTypeSettings(), serviceContext);
	}

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

}