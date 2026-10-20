/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.rest.client.dto.v1_0.Site;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag(value = "LPD-57655"), @FeatureFlag("LPD-85946")
	}
)
@RunWith(Arquillian.class)
public class SiteResourceTest extends BaseSiteResourceTestCase {

	@Override
	@Test
	public void testGetExportPreviewSitesPage() throws Exception {
		super.testGetExportPreviewSitesPage();

		_testGetExportPreviewSitesPage();
		_testGetExportPreviewSitesPageWithInactiveSite();
		_testGetExportPreviewSitesPageWithSearch();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalReferenceCode"};
	}

	@Override
	protected Site testGetExportPreviewSitesPage_addSite(Site site)
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		site.setExternalReferenceCode(group.getExternalReferenceCode());

		return site;
	}

	private Site _getExportPreviewSite(String externalReferenceCode)
		throws Exception {

		Page<Site> page = siteResource.getExportPreviewSitesPage(
			null, null, null);

		page = siteResource.getExportPreviewSitesPage(
			null, Pagination.of(1, (int)page.getTotalCount()), null);

		for (Site site : page.getItems()) {
			if (externalReferenceCode.equals(site.getExternalReferenceCode())) {
				return site;
			}
		}

		return null;
	}

	private void _testGetExportPreviewSitesPage() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Group childGroup = GroupTestUtil.addGroup(group.getGroupId());

		Site site = _getExportPreviewSite(group.getExternalReferenceCode());

		Assert.assertEquals(Integer.valueOf(1), site.getChildSitesCount());
		Assert.assertEquals(
			group.getDescriptiveName(), site.getDescriptiveName());

		Site childSite = _getExportPreviewSite(
			childGroup.getExternalReferenceCode());

		Assert.assertEquals(
			site.getPath() + " / " + childGroup.getDescriptiveName(),
			childSite.getPath());
	}

	private void _testGetExportPreviewSitesPageWithInactiveSite()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		group.setActive(false);

		group = _groupLocalService.updateGroup(group);

		Assert.assertNull(
			_getExportPreviewSite(group.getExternalReferenceCode()));
	}

	private void _testGetExportPreviewSitesPageWithSearch() throws Exception {
		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.addGroup();

		Page<Site> page = siteResource.getExportPreviewSitesPage(
			group.getDescriptiveName(), Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		List<Site> sites = (List<Site>)page.getItems();

		Site site = sites.get(0);

		Assert.assertEquals(
			group.getExternalReferenceCode(), site.getExternalReferenceCode());
	}

	@Inject
	private GroupLocalService _groupLocalService;

}