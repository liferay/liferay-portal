/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.headless.asset.library.client.dto.v1_0.ConnectedSite;
import com.liferay.headless.asset.library.client.problem.Problem;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class ConnectedSiteResourceTest
	extends BaseConnectedSiteResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

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

	@FeatureFlag("LPD-57283")
	@Override
	@Test
	public void testPutAssetLibraryConnectedSite() throws Exception {
		super.testPutAssetLibraryConnectedSite();

		_testPutAssetLibraryConnectedSiteReturnsStagingType();
		_testPutAssetLibraryConnectedSiteReturnsTypeDesignLibrary();
		_testPutAssetLibraryConnectedSiteReturnsTypeSite();
		_testPutAssetLibraryConnectedSiteReturnsTypeSiteTemplate();
		_testPutAssetLibraryConnectedSiteWithDDMStructuresAvailable();
		_testPutAssetLibraryConnectedSiteWithUnacceptedDepotEntryType();
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
			testDeleteAssetLibraryConnectedSite_addConnectedSite()
		throws Exception {

		return connectedSiteResource.putAssetLibraryConnectedSite(
			testDeleteAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode(),
			_testConnectedSite.getExternalReferenceCode(), new ConnectedSite());
	}

	@Override
	protected String
			testDeleteAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected ConnectedSite testGetAssetLibraryConnectedSite_addConnectedSite()
		throws Exception {

		return connectedSiteResource.putAssetLibraryConnectedSite(
			testGetAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode(),
			_testConnectedSite.getExternalReferenceCode(), new ConnectedSite());
	}

	@Override
	protected String
			testGetAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected ConnectedSite
			testGetAssetLibraryConnectedSitesPage_addConnectedSite(
				String assetLibraryExternalReferenceCode,
				ConnectedSite connectedSite)
		throws Exception {

		return connectedSiteResource.putAssetLibraryConnectedSite(
			assetLibraryExternalReferenceCode,
			connectedSite.getExternalReferenceCode(), connectedSite);
	}

	@Override
	protected String
			testGetAssetLibraryConnectedSitesPage_getAssetLibraryExternalReferenceCode()
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
	protected String
			testPutAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	private ConnectedSite _addConnectedSite() throws Exception {
		Group group = GroupTestUtil.addGroup();

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

	private DepotEntry _addDepotEntry(int type) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, type, ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _assertUnacceptedDepotEntryType(
			Group connectedSiteGroup, DepotEntry depotEntry)
		throws Exception {

		Group group = depotEntry.getGroup();

		AssertUtils.assertFailure(
			Problem.ProblemException.class, "This connection is not allowed.",
			() -> connectedSiteResource.putAssetLibraryConnectedSite(
				group.getExternalReferenceCode(),
				connectedSiteGroup.getExternalReferenceCode(),
				new ConnectedSite()));
	}

	private void _enableLocalStaging(Group group) throws Exception {
		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), group, true, false,
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId()));
	}

	private void _testPutAssetLibraryConnectedSiteReturnsStagingType()
		throws Exception {

		Group assetLibraryGroup = testDepotEntry.getGroup();

		String assetLibraryExternalReferenceCode =
			assetLibraryGroup.getExternalReferenceCode();

		Group group = GroupTestUtil.addGroup();

		ConnectedSite connectedSite =
			connectedSiteResource.putAssetLibraryConnectedSite(
				assetLibraryExternalReferenceCode,
				group.getExternalReferenceCode(), new ConnectedSite());

		Assert.assertNull(connectedSite.getStagingType());

		_enableLocalStaging(group);

		ConnectedSite liveConnectedSite =
			connectedSiteResource.getAssetLibraryConnectedSite(
				assetLibraryExternalReferenceCode,
				group.getExternalReferenceCode());

		Assert.assertEquals(
			ConnectedSite.StagingType.LIVE, liveConnectedSite.getStagingType());

		Group stagingGroup = group.getStagingGroup();

		ConnectedSite stagingConnectedSite =
			connectedSiteResource.getAssetLibraryConnectedSite(
				assetLibraryExternalReferenceCode,
				stagingGroup.getExternalReferenceCode());

		Assert.assertEquals(
			ConnectedSite.StagingType.STAGING,
			stagingConnectedSite.getStagingType());
	}

	private void _testPutAssetLibraryConnectedSiteReturnsTypeDesignLibrary()
		throws Exception {

		DepotEntry designLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);

		Group designLibraryGroup = designLibraryDepotEntry.getGroup();

		ConnectedSite connectedSite =
			connectedSiteResource.putAssetLibraryConnectedSite(
				testDepotEntryGroup.getExternalReferenceCode(),
				designLibraryGroup.getExternalReferenceCode(),
				new ConnectedSite());

		Assert.assertEquals(
			ConnectedSite.Type.DESIGN_LIBRARY, connectedSite.getType());

		DepotEntry spaceDepotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		Group spaceGroup = spaceDepotEntry.getGroup();

		connectedSite = connectedSiteResource.putAssetLibraryConnectedSite(
			spaceGroup.getExternalReferenceCode(),
			designLibraryGroup.getExternalReferenceCode(), new ConnectedSite());

		Assert.assertEquals(
			ConnectedSite.Type.DESIGN_LIBRARY, connectedSite.getType());
	}

	private void _testPutAssetLibraryConnectedSiteReturnsTypeSite()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		ConnectedSite connectedSite =
			connectedSiteResource.putAssetLibraryConnectedSite(
				group.getExternalReferenceCode(),
				_testConnectedSite.getExternalReferenceCode(),
				new ConnectedSite());

		Assert.assertEquals(ConnectedSite.Type.SITE, connectedSite.getType());
	}

	private void _testPutAssetLibraryConnectedSiteReturnsTypeSiteTemplate()
		throws Exception {

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.addLayoutSetPrototype(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				new HashMap<>(), true, true, new ServiceContext());

		Group group1 = testDepotEntry.getGroup();
		Group group2 = layoutSetPrototype.getGroup();

		ConnectedSite connectedSite =
			connectedSiteResource.putAssetLibraryConnectedSite(
				group1.getExternalReferenceCode(),
				group2.getExternalReferenceCode(), new ConnectedSite());

		Assert.assertEquals(
			ConnectedSite.Type.SITE_TEMPLATE, connectedSite.getType());
	}

	private void _testPutAssetLibraryConnectedSiteWithDDMStructuresAvailable()
		throws Exception {

		Group group = testDepotEntry.getGroup();

		ConnectedSite connectedSite =
			connectedSiteResource.putAssetLibraryConnectedSite(
				group.getExternalReferenceCode(),
				_testConnectedSite.getExternalReferenceCode(),
				new ConnectedSite() {
					{
						ddmStructuresAvailable = true;
					}
				});

		Assert.assertTrue(connectedSite.getDdmStructuresAvailable());

		connectedSite = connectedSiteResource.putAssetLibraryConnectedSite(
			group.getExternalReferenceCode(),
			_testConnectedSite.getExternalReferenceCode(),
			new ConnectedSite() {
				{
					ddmStructuresAvailable = false;
				}
			});

		Assert.assertFalse(connectedSite.getDdmStructuresAvailable());
	}

	private void _testPutAssetLibraryConnectedSiteWithUnacceptedDepotEntryType()
		throws Exception {

		DepotEntry assetLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);
		DepotEntry spaceDepotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_assertUnacceptedDepotEntryType(
			assetLibraryDepotEntry.getGroup(), spaceDepotEntry);
		_assertUnacceptedDepotEntryType(
			spaceDepotEntry.getGroup(), assetLibraryDepotEntry);

		DepotEntry designLibraryDepotEntry1 = _addDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);
		DepotEntry designLibraryDepotEntry2 = _addDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);

		_assertUnacceptedDepotEntryType(
			designLibraryDepotEntry2.getGroup(), designLibraryDepotEntry1);

		DepotEntry projectDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_PROJECT);

		_assertUnacceptedDepotEntryType(
			designLibraryDepotEntry1.getGroup(), projectDepotEntry);
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

	private ConnectedSite _testConnectedSite;

}