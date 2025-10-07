/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryPinLocalService;
import com.liferay.headless.asset.library.client.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.client.dto.v1_0.MimeTypeLimit;
import com.liferay.headless.asset.library.client.dto.v1_0.Settings;
import com.liferay.headless.asset.library.client.pagination.Page;
import com.liferay.headless.asset.library.client.pagination.Pagination;
import com.liferay.headless.asset.library.client.permission.Permission;
import com.liferay.headless.asset.library.client.problem.Problem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@FeatureFlag("LPD-17564")
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
	@Test
	public void testGetAssetLibrariesPage() throws Exception {
		super.testGetAssetLibrariesPage();

		Page<AssetLibrary> page = assetLibraryResource.getAssetLibrariesPage(
			null, null, "type eq 'Space'", Pagination.of(1, 10), null);

		long originalTotalCount = page.getTotalCount();

		AssetLibrary randomAssetLibrary = randomAssetLibrary();

		randomAssetLibrary.setType(AssetLibrary.Type.SPACE);

		AssetLibrary assetLibrary = testGetAssetLibrariesPage_addAssetLibrary(
			randomAssetLibrary);

		page = assetLibraryResource.getAssetLibrariesPage(
			null, null, "type eq 'Space'", Pagination.of(1, 10), null);

		Assert.assertEquals(originalTotalCount + 1, page.getTotalCount());

		assetLibraryResource.deleteAssetLibrary(assetLibrary.getId());
	}

	@Override
	@Test
	public void testPatchAssetLibrary() throws Exception {
		super.testPatchAssetLibrary();

		String[] availableLanguageIds = _getAvailableLanguageIds(
			LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY);
		String defaultLanguageId = _language.getLanguageId(LocaleUtil.US);
		String logoColor = RandomTestUtil.randomString();
		MimeTypeLimit[] mimeTypeLimits = {
			new MimeTypeLimit() {
				{
					maximumSize = 1234;
					mimeType = "application/pdf";
				}
			}
		};
		boolean sharingEnabled = true;
		boolean trashEnabled = true;
		int trashEntriesMaxAge = RandomTestUtil.randomInt();
		boolean useCustomLanguages = true;

		AssetLibrary assetLibrary = _postAssetLibraryWithSettings(
			true, availableLanguageIds, defaultLanguageId, logoColor,
			mimeTypeLimits, sharingEnabled, trashEnabled, trashEntriesMaxAge,
			useCustomLanguages);

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		assetLibrary.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(
							new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});
						setRoleExternalReferenceCode(
							role.getExternalReferenceCode());
						setRoleName(role.getName());
						setRoleType(role.getTypeLabel());
					}
				}
			});

		assetLibrary = assetLibraryResource.patchAssetLibrary(
			assetLibrary.getId(), assetLibrary);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				TestPropsValues.getCompanyId(), DepotEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(assetLibrary.getId()), role.getRoleId());

		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.UPDATE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));

		boolean autoTaggingEnabled = false;

		Settings settings = new Settings();

		settings.setAutoTaggingEnabled(autoTaggingEnabled);
		settings.setTrashEnabled(trashEnabled);
		settings.setTrashEntriesMaxAge(trashEntriesMaxAge);

		assetLibrary.setSettings(settings);

		assetLibrary = assetLibraryResource.patchAssetLibrary(
			assetLibrary.getId(), assetLibrary);

		_assertSettings(
			assetLibrary, autoTaggingEnabled, availableLanguageIds,
			defaultLanguageId, logoColor, mimeTypeLimits, sharingEnabled,
			trashEnabled, trashEntriesMaxAge, useCustomLanguages);

		settings = new Settings();

		settings.setMimeTypeLimits(new MimeTypeLimit[0]);

		assetLibrary.setSettings(settings);

		assetLibrary = assetLibraryResource.patchAssetLibrary(
			assetLibrary.getId(), assetLibrary);

		_assertSettings(
			assetLibrary, autoTaggingEnabled, availableLanguageIds,
			defaultLanguageId, logoColor, new MimeTypeLimit[0], sharingEnabled,
			trashEnabled, trashEntriesMaxAge, useCustomLanguages);
	}

	@Override
	@Test
	public void testPostAssetLibrary() throws Exception {
		super.testPostAssetLibrary();

		_testPostAssetLibrary(
			new MimeTypeLimit[] {
				new MimeTypeLimit() {
					{
						maximumSize = 1234;
						mimeType = "application/pdf";
					}
				}
			});
		_testPostAssetLibrary(new MimeTypeLimit[0]);

		AssetLibrary randomAssetLibrary = randomAssetLibrary();

		randomAssetLibrary.setSettings(new Settings());
		randomAssetLibrary.setType(AssetLibrary.Type.SPACE);

		AssetLibrary postedAssetLibrary = assetLibraryResource.postAssetLibrary(
			randomAssetLibrary);

		Settings settings = postedAssetLibrary.getSettings();

		Assert.assertEquals("outline-0", settings.getLogoColor());
		Assert.assertTrue(settings.getSharingEnabled());
		Assert.assertTrue(settings.getTrashEnabled());

		Assert.assertEquals(
			AssetLibrary.Type.SPACE, postedAssetLibrary.getType());
	}

	@Override
	@Test
	public void testPutAssetLibraryByExternalReferenceCode() throws Exception {
		super.testPutAssetLibraryByExternalReferenceCode();

		_testPutAssetLibraryByExternalReferenceCode(
			new MimeTypeLimit[] {
				new MimeTypeLimit() {
					{
						maximumSize = 1234;
						mimeType = "application/pdf";
					}
				}
			});
		_testPutAssetLibraryByExternalReferenceCode(null);
	}

	@Override
	protected void assertValid(AssetLibrary assetLibrary) throws Exception {
		DepotEntry originalTestDepotEntry = testDepotEntry;
		Group originalTestGroup = testGroup;

		DepotEntry depotEntry = _depotEntryLocalService.getDepotEntry(
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

	protected AssetLibrary randomAssetLibrary() throws Exception {
		AssetLibrary assetLibrary = super.randomAssetLibrary();

		assetLibrary.setSettings(
			new Settings() {
				{
					autoTaggingEnabled = false;
					logoColor = "color-1";
					sharingEnabled = false;
					useCustomLanguages = false;
				}
			});

		return assetLibrary;
	}

	protected AssetLibrary randomAssetLibraryWithTrashEnabled()
		throws Exception {

		AssetLibrary assetLibrary = super.randomAssetLibrary();

		assetLibrary.setSettings(
			new Settings() {
				{
					autoTaggingEnabled = false;
					logoColor = "color-1";
					sharingEnabled = false;
					trashEnabled = true;
					trashEntriesMaxAge = RandomTestUtil.randomInt();
					useCustomLanguages = false;
				}
			});

		return assetLibrary;
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

	@Override
	protected AssetLibrary
			testDeleteAssetLibraryByExternalReferenceCode_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary
			testDeleteAssetLibraryByExternalReferenceCodePin_addAssetLibrary()
		throws Exception {

		return testDeleteAssetLibraryPin_addAssetLibrary();
	}

	@Override
	protected AssetLibrary testDeleteAssetLibraryPin_addAssetLibrary()
		throws Exception {

		AssetLibrary assetLibrary = _addAssetLibrary();

		return assetLibraryResource.putAssetLibraryPin(assetLibrary.getId());
	}

	@Override
	protected AssetLibrary testGetAssetLibrariesPage_addAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		return assetLibraryResource.postAssetLibrary(assetLibrary);
	}

	@Override
	protected AssetLibrary testGetAssetLibrariesPinnedByMePage_addAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		assetLibrary = assetLibraryResource.postAssetLibrary(assetLibrary);

		return assetLibraryResource.putAssetLibraryPin(assetLibrary.getId());
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
	protected AssetLibrary
			testGetAssetLibraryByExternalReferenceCodePermissionsPage_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary testGetAssetLibraryPermissionsPage_addAssetLibrary()
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
	protected AssetLibrary testPostAssetLibrary_addPermissionsAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		return permissionsAssetLibraryResource.postAssetLibrary(assetLibrary);
	}

	@Override
	protected AssetLibrary
			testPutAssetLibraryByExternalReferenceCode_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary
			testPutAssetLibraryByExternalReferenceCodePermissionsPage_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary
			testPutAssetLibraryByExternalReferenceCodePin_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary
		testPutAssetLibraryByExternalReferenceCodePin_getAssetLibrary(
			String externalReferenceCode) {

		try {
			Group group = _groupLocalService.getGroupByExternalReferenceCode(
				externalReferenceCode, testCompany.getCompanyId());

			DepotEntry depotEntry = _depotEntryLocalService.getGroupDepotEntry(
				group.getGroupId());

			return testPutAssetLibraryPin_getAssetLibrary(
				depotEntry.getDepotEntryId());
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	protected AssetLibrary testPutAssetLibraryPermissionsPage_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary testPutAssetLibraryPin_addAssetLibrary()
		throws Exception {

		return _addAssetLibrary();
	}

	@Override
	protected AssetLibrary testPutAssetLibraryPin_getAssetLibrary(
		Long assetLibraryId) {

		try {
			User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

			Assert.assertNotNull(
				_depotEntryPinLocalService.getDepotEntryPin(
					user.getUserId(), assetLibraryId));

			return assetLibraryResource.getAssetLibrary(assetLibraryId);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private AssetLibrary _addAssetLibrary() throws Exception {
		return assetLibraryResource.postAssetLibrary(randomAssetLibrary());
	}

	private void _assertSettings(
		AssetLibrary assetLibrary, boolean expectedAutoTaggingEnabled,
		String[] expectedAvailableLanguageIds, String expectedDefaultLanguageId,
		String expectedLogoColor, MimeTypeLimit[] expectedMimeTypeLimits,
		boolean expectedSharingEnabled, boolean expectedTrashEnabled,
		int expectedTrashEntriesMaxAge, boolean expectedUseCustomLanguages) {

		Settings settings = assetLibrary.getSettings();

		Assert.assertEquals(
			expectedAutoTaggingEnabled, settings.getAutoTaggingEnabled());
		Assert.assertEquals(
			expectedDefaultLanguageId, settings.getDefaultLanguageId());
		Assert.assertEquals(
			expectedAvailableLanguageIds, settings.getAvailableLanguageIds());
		Assert.assertEquals(expectedLogoColor, settings.getLogoColor());

		MimeTypeLimit[] mimeTypeLimits = settings.getMimeTypeLimits();

		if (ArrayUtil.isEmpty(expectedMimeTypeLimits)) {
			Assert.assertEquals(
				Arrays.toString(mimeTypeLimits), 0, mimeTypeLimits.length);
		}
		else {
			Assert.assertEquals(
				Arrays.toString(mimeTypeLimits), expectedMimeTypeLimits.length,
				mimeTypeLimits.length);
			Assert.assertEquals(expectedMimeTypeLimits[0], mimeTypeLimits[0]);
		}

		Assert.assertEquals(
			expectedSharingEnabled, settings.getSharingEnabled());
		Assert.assertEquals(expectedTrashEnabled, settings.getTrashEnabled());
		Assert.assertEquals(
			expectedTrashEntriesMaxAge, (int)settings.getTrashEntriesMaxAge());
		Assert.assertEquals(
			expectedUseCustomLanguages, settings.getUseCustomLanguages());
	}

	private String[] _getAvailableLanguageIds(Locale... locales) {
		return TransformUtil.transformToArray(
			ListUtil.fromArray(locales),
			(Locale locale) -> _language.getLanguageId(locale), String.class);
	}

	private AssetLibrary _postAssetLibraryWithSettings(
			boolean autoTaggingEnabled, String[] availableLanguageIds,
			String defaultLanguageId, String logoColor,
			MimeTypeLimit[] mimeTypeLimits, boolean sharingEnabled,
			boolean trashEnabled, int trashEntriesMaxAge,
			boolean useCustomLanguages)
		throws Exception {

		AssetLibrary assetLibrary = randomAssetLibrary();

		Settings settings = new Settings();

		settings.setAutoTaggingEnabled(autoTaggingEnabled);
		settings.setAvailableLanguageIds(availableLanguageIds);
		settings.setDefaultLanguageId(defaultLanguageId);
		settings.setLogoColor(logoColor);
		settings.setMimeTypeLimits(mimeTypeLimits);
		settings.setSharingEnabled(sharingEnabled);
		settings.setTrashEnabled(trashEnabled);
		settings.setTrashEntriesMaxAge(trashEntriesMaxAge);
		settings.setUseCustomLanguages(useCustomLanguages);

		assetLibrary.setSettings(settings);

		return assetLibraryResource.postAssetLibrary(assetLibrary);
	}

	private void _testPostAssetLibrary(MimeTypeLimit[] mimeTypeLimits)
		throws Exception {

		boolean autoTaggingEnabled = true;
		String[] availableLanguageIds = _getAvailableLanguageIds(
			LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY);
		String defaultLanguageId = _language.getLanguageId(LocaleUtil.US);
		String logoColor = RandomTestUtil.randomString();
		boolean sharingEnabled = true;
		boolean useCustomLanguages = true;
		boolean trashEnabled = true;
		int trashEntriesMaxAge = RandomTestUtil.randomInt();

		AssetLibrary assetLibrary = _postAssetLibraryWithSettings(
			autoTaggingEnabled, availableLanguageIds, defaultLanguageId,
			logoColor, mimeTypeLimits, sharingEnabled, trashEnabled,
			trashEntriesMaxAge, useCustomLanguages);

		_assertSettings(
			assetLibrary, autoTaggingEnabled, availableLanguageIds,
			defaultLanguageId, logoColor, mimeTypeLimits, sharingEnabled,
			trashEnabled, trashEntriesMaxAge, useCustomLanguages);
	}

	private void _testPutAssetLibraryByExternalReferenceCode(
			MimeTypeLimit[] mimeTypeLimits)
		throws Exception {

		AssetLibrary assetLibrary = _postAssetLibraryWithSettings(
			true,
			_getAvailableLanguageIds(
				LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.GERMANY),
			_language.getLanguageId(LocaleUtil.US),
			RandomTestUtil.randomString(), mimeTypeLimits, true, true,
			RandomTestUtil.randomInt(), true);

		String defaultLanguageId = _language.getLanguageId(LocaleUtil.SPAIN);

		assetLibrary.setName_i18n(
			Collections.singletonMap(
				defaultLanguageId, RandomTestUtil.randomString()));

		Settings settings = new Settings();

		boolean autoTaggingEnabled = true;

		settings.setAutoTaggingEnabled(autoTaggingEnabled);

		String[] availableLanguageIds = _getAvailableLanguageIds(
			LocaleUtil.SPAIN);

		settings.setAvailableLanguageIds(availableLanguageIds);

		settings.setDefaultLanguageId(defaultLanguageId);

		boolean trashEnabled = true;

		settings.setTrashEnabled(trashEnabled);

		int trashEntriesMaxAge = RandomTestUtil.randomInt();

		settings.setTrashEntriesMaxAge(trashEntriesMaxAge);

		boolean useCustomLanguages = true;

		settings.setUseCustomLanguages(useCustomLanguages);

		assetLibrary.setSettings(settings);

		assetLibrary =
			assetLibraryResource.putAssetLibraryByExternalReferenceCode(
				assetLibrary.getExternalReferenceCode(), assetLibrary);

		_assertSettings(
			assetLibrary, autoTaggingEnabled, availableLanguageIds,
			defaultLanguageId, "outline-0", new MimeTypeLimit[0], true,
			trashEnabled, trashEntriesMaxAge, useCustomLanguages);
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DepotEntryPinLocalService _depotEntryPinLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}