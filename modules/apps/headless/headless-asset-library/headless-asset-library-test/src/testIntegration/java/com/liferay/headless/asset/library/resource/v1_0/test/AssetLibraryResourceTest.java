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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@FeatureFlags("LPD-17564")
@RunWith(Arquillian.class)
public class AssetLibraryResourceTest extends BaseAssetLibraryResourceTestCase {

	@Override
	@Test
	public void testDeleteAssetLibrary() throws Exception {
		super.testDeleteAssetLibrary();

		// Nonexistent asset library ID

		long assetLibraryId = RandomTestUtil.randomLong();

		try {
			assetLibraryResource.deleteAssetLibrary(assetLibraryId);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	protected void assertValid(AssetLibrary assetLibrary) throws Exception {
		DepotEntry originalTestDepotEntry = testDepotEntry;
		Group originalTestGroup = testGroup;

		DepotEntry depotEntry = _depotEntryLocalService.getGroupDepotEntry(
			assetLibrary.getId());

		testDepotEntry = depotEntry;
		testGroup = depotEntry.getGroup();

		super.assertValid(assetLibrary);

		testDepotEntry = originalTestDepotEntry;
		testGroup = originalTestGroup;
	}

	@Override
	protected Collection<EntityField> getEntityFields() throws Exception {
		return new ArrayList<>();
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

		return _addAssetLibrary();
	}

	protected AssetLibrary
			testDeleteAssetLibraryByExternalReferenceCode_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary testGetAssetLibrariesPage_addAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		return assetLibraryResource.postAssetLibrary(assetLibrary);
	}

	@Override
	protected AssetLibrary testGetAssetLibrary_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary
			testGetAssetLibraryByExternalReferenceCode_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary testPatchAssetLibrary_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary
			testPatchAssetLibraryByExternalReferenceCode_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary testPostAssetLibrary_addAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		return assetLibraryResource.postAssetLibrary(assetLibrary);
	}

	@Override
	protected AssetLibrary
			testPutAssetLibraryByExternalReferenceCode_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	private AssetLibrary _addAssetLibrary() throws Exception {
		return assetLibraryResource.postAssetLibrary(randomAssetLibrary());
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}