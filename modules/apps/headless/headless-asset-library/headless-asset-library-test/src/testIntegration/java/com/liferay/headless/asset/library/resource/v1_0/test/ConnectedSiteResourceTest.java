/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.asset.library.client.dto.v1_0.ConnectedSite;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class ConnectedSiteResourceTest
	extends BaseConnectedSiteResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_testConnectedSite = _addConnectedSite();
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() {
	}

	@Override
	protected ConnectedSite randomConnectedSite() throws Exception {
		return _addConnectedSite();
	}

	@Override
	protected ConnectedSite randomIrrelevantConnectedSite() throws Exception {
		return _addConnectedSite();
	}

	@Override
	protected ConnectedSite
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode_addConnectedSite()
		throws Exception {

		return connectedSiteResource.putAssetLibraryConnectedSite(
			testDepotEntry.getGroupId(), _testConnectedSite.getId(),
			new ConnectedSite());
	}

	@Override
	protected String
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected ConnectedSite
			testDeleteAssetLibraryConnectedSite_addConnectedSite()
		throws Exception {

		return connectedSiteResource.putAssetLibraryConnectedSite(
			testDepotEntry.getGroupId(), _testConnectedSite.getId(),
			new ConnectedSite());
	}

	@Override
	protected Long testDeleteAssetLibraryConnectedSite_getAssetLibraryId() {
		return testDepotEntry.getGroupId();
	}

	@Override
	protected ConnectedSite
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode_addConnectedSite()
		throws Exception {

		return connectedSiteResource.putAssetLibraryConnectedSite(
			testDepotEntry.getGroupId(), _testConnectedSite.getId(),
			new ConnectedSite());
	}

	@Override
	protected String
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected ConnectedSite
			testGetAssetLibraryByExternalReferenceCodeConnectedSitesPage_addConnectedSite(
				String externalReferenceCode, ConnectedSite connectedSite)
		throws Exception {

		return connectedSiteResource.putAssetLibraryConnectedSite(
			testDepotEntry.getGroupId(), connectedSite.getId(),
			new ConnectedSite());
	}

	@Override
	protected String
			testGetAssetLibraryByExternalReferenceCodeConnectedSitesPage_getExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected ConnectedSite testGetAssetLibraryConnectedSite_addConnectedSite()
		throws Exception {

		return connectedSiteResource.putAssetLibraryConnectedSite(
			testDepotEntry.getGroupId(), _testConnectedSite.getId(),
			new ConnectedSite());
	}

	@Override
	protected Long testGetAssetLibraryConnectedSite_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getGroupId();
	}

	@Override
	protected ConnectedSite
			testGetAssetLibraryConnectedSitesPage_addConnectedSite(
				Long assetLibraryId, ConnectedSite connectedSite)
		throws Exception {

		return connectedSiteResource.putAssetLibraryConnectedSite(
			assetLibraryId, connectedSite.getId(), new ConnectedSite());
	}

	@Override
	protected ConnectedSite
		testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode_addConnectedSite() {

		return _testConnectedSite;
	}

	@Override
	protected String
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeConnectedSiteByExternalReferenceCodeConnectedSiteExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected ConnectedSite
		testPutAssetLibraryConnectedSite_addConnectedSite() {

		return _testConnectedSite;
	}

	@Override
	protected Long testPutAssetLibraryConnectedSite_getAssetLibraryId() {
		return testDepotEntry.getGroupId();
	}

	private ConnectedSite _addConnectedSite() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return new ConnectedSite() {
			{
				externalReferenceCode = group.getExternalReferenceCode();
				id = group.getGroupId();
				name = group.getName();
				name_i18n = LocalizedMapUtil.getI18nMap(
					true, group.getNameMap());
				searchable = false;
			}
		};
	}

	@DeleteAfterTestRun
	private List<Group> _groups = new ArrayList<>();

	private ConnectedSite _testConnectedSite;

}