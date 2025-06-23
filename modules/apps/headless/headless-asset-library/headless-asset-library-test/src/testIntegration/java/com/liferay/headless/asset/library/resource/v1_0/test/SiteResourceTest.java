/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.asset.library.client.dto.v1_0.Site;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class SiteResourceTest extends BaseSiteResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_testSite = _addSite();
	}

	@Override
	protected Site randomIrrelevantSite() throws Exception {
		return _addSite();
	}

	@Override
	protected Site randomSite() throws Exception {
		return _addSite();
	}

	@Override
	protected Site
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_addSite()
		throws Exception {

		return siteResource.putAssetLibrarySite(
			testDepotEntry.getGroupId(), _testSite.getId());
	}

	@Override
	protected String
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected Site testDeleteAssetLibrarySite_addSite() throws Exception {
		return siteResource.putAssetLibrarySite(
			testDepotEntry.getGroupId(), _testSite.getId());
	}

	@Override
	protected Long testDeleteAssetLibrarySite_getAssetLibraryId() {
		return testDepotEntry.getGroupId();
	}

	@Override
	protected Site
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_addSite()
		throws Exception {

		return siteResource.putAssetLibrarySite(
			testDepotEntry.getGroupId(), _testSite.getId());
	}

	@Override
	protected String
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected Site testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
			String externalReferenceCode, Site site)
		throws Exception {

		return siteResource.putAssetLibrarySite(
			testDepotEntry.getGroupId(), site.getId());
	}

	@Override
	protected String
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected Site testGetAssetLibrarySite_addSite() throws Exception {
		return siteResource.putAssetLibrarySite(
			testDepotEntry.getGroupId(), _testSite.getId());
	}

	@Override
	protected Long testGetAssetLibrarySite_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getGroupId();
	}

	@Override
	protected Site testGetAssetLibrarySitesPage_addSite(
			Long assetLibraryId, Site site)
		throws Exception {

		return siteResource.putAssetLibrarySite(assetLibraryId, site.getId());
	}

	@Override
	protected Site
		testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_addSite() {

		return _testSite;
	}

	@Override
	protected String
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected Site testPutAssetLibrarySite_addSite() {
		return _testSite;
	}

	@Override
	protected Long testPutAssetLibrarySite_getAssetLibraryId() {
		return testDepotEntry.getGroupId();
	}

	private Site _addSite() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return new Site() {
			{
				setExternalReferenceCode(group.getExternalReferenceCode());
				setId(group.getGroupId());
				setName(group.getName());
				setName_i18n(
					LocalizedMapUtil.getI18nMap(true, group.getNameMap()));
				setSearchable(false);
			}
		};
	}

	@DeleteAfterTestRun
	private List<Group> _groups = new ArrayList<>();

	private Site _testSite;

}