/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.rest.client.dto.v1_0.AssetLibrary;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag(value = "LPD-41307"), @FeatureFlag("LPD-57655"),
		@FeatureFlag("LPD-85946")
	}
)
@RunWith(Arquillian.class)
public class AssetLibraryResourceTest extends BaseAssetLibraryResourceTestCase {

	@Override
	@Test
	public void testGetExportPreviewAssetLibrariesPage() throws Exception {
		super.testGetExportPreviewAssetLibrariesPage();

		_testGetExportPreviewAssetLibrariesPage();
		_testGetExportPreviewAssetLibrariesPageWithFilter();
		_testGetExportPreviewAssetLibrariesPageWithInactiveAssetLibrary();
		_testGetExportPreviewAssetLibrariesPageWithSearch();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalReferenceCode"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"descriptiveName", "type"};
	}

	@Override
	protected AssetLibrary
			testGetExportPreviewAssetLibrariesPage_addAssetLibrary(
				AssetLibrary assetLibrary)
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		Group group = depotEntry.getGroup();

		assetLibrary.setExternalReferenceCode(group.getExternalReferenceCode());

		return assetLibrary;
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			type, ServiceContextTestUtil.getServiceContext());

		_groups.add(depotEntry.getGroup());

		return depotEntry;
	}

	private AssetLibrary _getExportPreviewAssetLibrary(
			String externalReferenceCode)
		throws Exception {

		return _getExportPreviewAssetLibrary(externalReferenceCode, null);
	}

	private AssetLibrary _getExportPreviewAssetLibrary(
			String externalReferenceCode, String filterString)
		throws Exception {

		Page<AssetLibrary> assetLibrariesPage =
			assetLibraryResource.getExportPreviewAssetLibrariesPage(
				null, filterString, null, null);

		assetLibrariesPage =
			assetLibraryResource.getExportPreviewAssetLibrariesPage(
				null, filterString,
				Pagination.of(1, (int)assetLibrariesPage.getTotalCount()),
				null);

		for (AssetLibrary assetLibrary : assetLibrariesPage.getItems()) {
			if (externalReferenceCode.equals(
					assetLibrary.getExternalReferenceCode())) {

				return assetLibrary;
			}
		}

		return null;
	}

	private void _testGetExportPreviewAssetLibrariesPage() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		Group group = depotEntry.getGroup();

		AssetLibrary assetLibrary = _getExportPreviewAssetLibrary(
			group.getExternalReferenceCode());

		Assert.assertEquals(
			group.getDescriptiveName(), assetLibrary.getDescriptiveName());
		Assert.assertEquals(AssetLibrary.Type.SPACE, assetLibrary.getType());

		depotEntry = _addDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY);

		group = depotEntry.getGroup();

		assetLibrary = _getExportPreviewAssetLibrary(
			group.getExternalReferenceCode());

		Assert.assertEquals(
			AssetLibrary.Type.ASSET_LIBRARY, assetLibrary.getType());

		group = GroupTestUtil.addGroup();

		_groups.add(group);

		Assert.assertNull(
			_getExportPreviewAssetLibrary(group.getExternalReferenceCode()));
	}

	private void _testGetExportPreviewAssetLibrariesPageWithFilter()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		Group assetLibraryGroup = depotEntry.getGroup();

		depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		Group spaceGroup = depotEntry.getGroup();

		Assert.assertNull(
			_getExportPreviewAssetLibrary(
				assetLibraryGroup.getExternalReferenceCode(),
				"type eq 'Space'"));
		Assert.assertNotNull(
			_getExportPreviewAssetLibrary(
				spaceGroup.getExternalReferenceCode(), "type eq 'Space'"));

		Assert.assertNotNull(
			_getExportPreviewAssetLibrary(
				assetLibraryGroup.getExternalReferenceCode(),
				"type eq 'AssetLibrary'"));
		Assert.assertNull(
			_getExportPreviewAssetLibrary(
				spaceGroup.getExternalReferenceCode(),
				"type eq 'AssetLibrary'"));
	}

	private void _testGetExportPreviewAssetLibrariesPageWithInactiveAssetLibrary()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		Group group = depotEntry.getGroup();

		group.setActive(false);

		group = _groupLocalService.updateGroup(group);

		Assert.assertNull(
			_getExportPreviewAssetLibrary(group.getExternalReferenceCode()));
	}

	private void _testGetExportPreviewAssetLibrariesPageWithSearch()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_addDepotEntry(DepotConstants.TYPE_SPACE);

		Group group = depotEntry.getGroup();

		Page<AssetLibrary> assetLibrariesPage =
			assetLibraryResource.getExportPreviewAssetLibrariesPage(
				group.getDescriptiveName(), null, Pagination.of(1, 10), null);

		Assert.assertEquals(1, assetLibrariesPage.getTotalCount());

		List<AssetLibrary> assetLibraries =
			(List<AssetLibrary>)assetLibrariesPage.getItems();

		AssetLibrary assetLibrary = assetLibraries.get(0);

		Assert.assertEquals(
			group.getExternalReferenceCode(),
			assetLibrary.getExternalReferenceCode());
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

}