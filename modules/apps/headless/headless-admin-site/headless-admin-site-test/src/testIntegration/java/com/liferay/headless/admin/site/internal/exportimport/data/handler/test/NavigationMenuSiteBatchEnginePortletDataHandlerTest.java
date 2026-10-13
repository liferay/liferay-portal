/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseSiteBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.admin.site.dto.v1_0.NavigationMenu;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.util.Date;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@RunWith(Arquillian.class)
public class NavigationMenuSiteBatchEnginePortletDataHandlerTest
	extends BaseSiteBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_siteNavigationMenuLocalService.addSiteNavigationMenu(
			externalReferenceCode, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_siteNavigationMenuLocalService.deleteSiteNavigationMenu(
			_getSiteNavigationMenu(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu = _getSiteNavigationMenu(
			externalReferenceCode, groupId);

		return siteNavigationMenu.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return NavigationMenu.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu = _getSiteNavigationMenu(
			externalReferenceCode, groupId);

		return siteNavigationMenu.getSiteNavigationMenuId();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (siteNavigationMenu == null) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean supportsComments() {
		return false;
	}

	@Override
	protected boolean supportsEmptyEntries() {
		return false;
	}

	@Override
	protected boolean supportsPermissions() {
		return true;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu = _getSiteNavigationMenu(
			externalReferenceCode, groupId);

		siteNavigationMenu.setUserId(userId);

		_siteNavigationMenuLocalService.updateSiteNavigationMenu(
			siteNavigationMenu);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu = _getSiteNavigationMenu(
			externalReferenceCode, groupId);

		_siteNavigationMenuLocalService.updateSiteNavigationMenu(
			siteNavigationMenu.getUserId(),
			siteNavigationMenu.getSiteNavigationMenuId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				siteNavigationMenu.getGroupId(),
				siteNavigationMenu.getUserId()));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu = _getSiteNavigationMenu(
			externalReferenceCode, groupId);

		siteNavigationMenu.setModifiedDate(modifiedDate);

		_siteNavigationMenuLocalService.updateSiteNavigationMenu(
			siteNavigationMenu);
	}

	private SiteNavigationMenu _getSiteNavigationMenu(
			String externalReferenceCode, long groupId)
		throws Exception {

		return _siteNavigationMenuLocalService.
			getSiteNavigationMenuByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}