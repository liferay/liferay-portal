/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.asset.library.client.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.client.problem.Problem;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@FeatureFlags("LPD-32649")
@RunWith(Arquillian.class)
public class AssetLibraryResourceTest extends BaseAssetLibraryResourceTestCase {

	@Override
	@Test
	public void testDeleteAssetLibrary() throws Exception {
		super.testDeleteAssetLibrary();

		// Nonexistent assetLibrary ID

		long siteId = RandomTestUtil.randomLong();

		try {
			assetLibraryResource.deleteAssetLibrary(siteId);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	@Test
	public void testDeleteAssetLibraryLinkToSite() throws Exception {
		AssetLibrary assetLibrary =
			testDeleteAssetLibraryLinkToSite_addAssetLibrary();

		_assertLinkedSites(assetLibrary);

		assetLibrary = assetLibraryResource.deleteAssetLibraryLinkToSite(
			assetLibrary.getId(), testGroup.getGroupId());

		Assert.assertTrue(ArrayUtil.isEmpty(assetLibrary.getLinkedSiteIds()));
		Assert.assertTrue(
			ArrayUtil.isEmpty(
				assetLibrary.getLinkedSitesExternalReferenceCodes()));
	}

	@Override
	@Test
	public void testPatchAssetLibrary() throws Exception {
		super.testPatchAssetLibrary();
	}

	@Override
	@Test
	public void testPostAssetLibraryLinkToSite() throws Exception {
		AssetLibrary assetLibrary =
			testPostAssetLibraryLinkToSite_addAssetLibrary(
				randomAssetLibrary());

		_assertLinkedSites(assetLibrary);
	}

	@Override
	protected AssetLibrary randomPatchAssetLibrary() throws Exception {
		AssetLibrary assetLibrary = randomAssetLibrary();

		assetLibrary.setName(RandomTestUtil.randomString());

		return assetLibrary;
	}

	@Override
	protected AssetLibrary testDeleteAssetLibrary_addAssetLibrary()
		throws Exception {

		return _addRandomAssetLibrary();
	}

	@Override
	protected AssetLibrary testDeleteAssetLibraryLinkToSite_addAssetLibrary()
		throws Exception {

		AssetLibrary assetLibrary = _addRandomAssetLibrary();

		DepotEntry depotEntry = _depotEntryLocalService.getGroupDepotEntry(
			assetLibrary.getId());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), testGroup.getGroupId());

		return assetLibraryResource.getAssetLibrary(assetLibrary.getId());
	}

	@Override
	protected AssetLibrary testGetAssetLibrary_addAssetLibrary()
		throws Exception {

		return _addRandomAssetLibrary();
	}

	@Override
	protected AssetLibrary testPatchAssetLibrary_addAssetLibrary()
		throws Exception {

		return _addRandomAssetLibrary();
	}

	@Override
	protected AssetLibrary testPostAssetLibrary_addAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		return assetLibraryResource.postAssetLibrary(assetLibrary);
	}

	@Override
	protected AssetLibrary testPostAssetLibraryLinkToSite_addAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		assetLibrary = assetLibraryResource.postAssetLibrary(assetLibrary);

		DepotEntry depotEntry = _depotEntryLocalService.getGroupDepotEntry(
			assetLibrary.getId());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), testGroup.getGroupId());

		return assetLibraryResource.getAssetLibrary(assetLibrary.getId());
	}

	private AssetLibrary _addRandomAssetLibrary() throws Exception {
		return assetLibraryResource.postAssetLibrary(randomAssetLibrary());
	}

	private void _assertLinkedSites(AssetLibrary assetLibrary) {
		Long[] linkedSiteIds = assetLibrary.getLinkedSiteIds();
		String[] linkedSitesExternalReferenceCodes =
			assetLibrary.getLinkedSitesExternalReferenceCodes();

		Assert.assertEquals(testGroup.getGroupId(), (long)linkedSiteIds[0]);
		Assert.assertEquals(
			testGroup.getExternalReferenceCode(),
			linkedSitesExternalReferenceCodes[0]);
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

}