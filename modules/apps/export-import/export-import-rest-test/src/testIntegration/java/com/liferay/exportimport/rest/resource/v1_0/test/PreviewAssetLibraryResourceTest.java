/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewAssetLibrary;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.portal.kernel.model.Group;
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
public class PreviewAssetLibraryResourceTest
	extends BasePreviewAssetLibraryResourceTestCase {

	@Override
	@Test
	public void testGetExportPreviewPreviewAssetLibrariesPage()
		throws Exception {

		super.testGetExportPreviewPreviewAssetLibrariesPage();

		_testGetExportPreviewPreviewAssetLibrariesPage();
		_testGetExportPreviewPreviewAssetLibrariesPageWithSearch();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalReferenceCode"};
	}

	@Override
	protected PreviewAssetLibrary
			testGetExportPreviewPreviewAssetLibrariesPage_addPreviewAssetLibrary(
				PreviewAssetLibrary previewAssetLibrary)
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		Group group = depotEntry.getGroup();

		previewAssetLibrary.setExternalReferenceCode(
			group.getExternalReferenceCode());

		return previewAssetLibrary;
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

	private PreviewAssetLibrary _getExportPreviewAssetLibrary(
			String externalReferenceCode)
		throws Exception {

		Page<PreviewAssetLibrary> page =
			previewAssetLibraryResource.
				getExportPreviewPreviewAssetLibrariesPage(null, null, null);

		page =
			previewAssetLibraryResource.
				getExportPreviewPreviewAssetLibrariesPage(
					null, Pagination.of(1, (int)page.getTotalCount()), null);

		for (PreviewAssetLibrary previewAssetLibrary : page.getItems()) {
			if (externalReferenceCode.equals(
					previewAssetLibrary.getExternalReferenceCode())) {

				return previewAssetLibrary;
			}
		}

		return null;
	}

	private void _testGetExportPreviewPreviewAssetLibrariesPage()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		Group group = depotEntry.getGroup();

		PreviewAssetLibrary previewAssetLibrary = _getExportPreviewAssetLibrary(
			group.getExternalReferenceCode());

		Assert.assertEquals(
			group.getDescriptiveName(),
			previewAssetLibrary.getDescriptiveName());
		Assert.assertEquals(
			PreviewAssetLibrary.Type.SPACE, previewAssetLibrary.getType());

		depotEntry = _addDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY);

		group = depotEntry.getGroup();

		Assert.assertNull(
			_getExportPreviewAssetLibrary(group.getExternalReferenceCode()));

		group = GroupTestUtil.addGroup();

		_groups.add(group);

		Assert.assertNull(
			_getExportPreviewAssetLibrary(group.getExternalReferenceCode()));
	}

	private void _testGetExportPreviewPreviewAssetLibrariesPageWithSearch()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_addDepotEntry(DepotConstants.TYPE_SPACE);

		Group group = depotEntry.getGroup();

		Page<PreviewAssetLibrary> page =
			previewAssetLibraryResource.
				getExportPreviewPreviewAssetLibrariesPage(
					group.getDescriptiveName(), Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		List<PreviewAssetLibrary> previewAssetLibraries =
			(List<PreviewAssetLibrary>)page.getItems();

		PreviewAssetLibrary previewAssetLibrary = previewAssetLibraries.get(0);

		Assert.assertEquals(
			group.getExternalReferenceCode(),
			previewAssetLibrary.getExternalReferenceCode());
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

}