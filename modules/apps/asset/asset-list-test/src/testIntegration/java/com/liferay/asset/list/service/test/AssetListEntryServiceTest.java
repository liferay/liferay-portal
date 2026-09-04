/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.exception.AssetListEntryTitleException;
import com.liferay.asset.list.exception.DuplicateAssetListEntryTitleException;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.asset.list.util.comparator.AssetListEntryCreateDateComparator;
import com.liferay.asset.list.util.comparator.AssetListEntryTitleComparator;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.asset.test.util.asset.renderer.factory.TestAssetRendererFactory;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class AssetListEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddAssetListEntry() throws PortalException {
		AssetListEntry assetListEntry = _addAssetListEntry("Asset List Title");

		Assert.assertNotNull(
			_assetListEntryService.fetchAssetListEntry(
				assetListEntry.getAssetListEntryId()));

		Assert.assertEquals("Asset List Title", assetListEntry.getTitle());
	}

	@Test(expected = AssetListEntryTitleException.class)
	public void testAddAssetListEntryWithEmptyTitle() throws PortalException {
		_addAssetListEntry(StringPool.BLANK);
	}

	@Test(expected = AssetListEntryTitleException.class)
	public void testAddAssetListEntryWithInvalidLength()
		throws PortalException {

		_addAssetListEntry(RandomTestUtil.randomString(76));
	}

	@Test(expected = AssetListEntryTitleException.class)
	public void testAddAssetListEntryWithNullTitle() throws PortalException {
		_addAssetListEntry(null);
	}

	@Test(expected = DuplicateAssetListEntryTitleException.class)
	public void testAddDuplicateAssetListEntry() throws PortalException {
		_addAssetListEntry("Asset List Title");

		_addAssetListEntry("Asset List Title");
	}

	@Test
	public void testAssetEntrySelectionAllowsSameAssetEntryForDifferentSegmentsEntries()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetListEntry assetListEntry =
			_assetListEntryService.addAssetListEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, serviceContext);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_group.getGroupId(), assetListEntry,
			segmentsEntry.getSegmentsEntryId());

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		_assetListEntryService.addAssetEntrySelection(
			assetListEntry.getAssetListEntryId(), assetEntry.getEntryId(), 0,
			serviceContext);

		_assetListEntryService.addAssetEntrySelection(
			assetListEntry.getAssetListEntryId(), assetEntry.getEntryId(),
			segmentsEntry.getSegmentsEntryId(), serviceContext);

		Assert.assertEquals(
			2,
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRelsCount(
					assetListEntry.getAssetListEntryId()));
	}

	@Test
	public void testAssetEntrySelectionDontAddAssetEntryRelIfExist()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetListEntry assetListEntry =
			_assetListEntryService.addAssetListEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, serviceContext);

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		_assetListEntryService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry.getEntryId()}, 0, serviceContext);

		int assetListEntriesAssetEntryRelsCount =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRelsCount(
					assetListEntry.getAssetListEntryId());

		_assetListEntryService.addAssetEntrySelection(
			assetListEntry.getAssetListEntryId(), assetEntry.getEntryId(), 0,
			serviceContext);

		Assert.assertEquals(
			assetListEntriesAssetEntryRelsCount,
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRelsCount(
					assetListEntry.getAssetListEntryId()));
	}

	@Test
	public void testAssetEntrySelectionDontDuplicateAssetEntryRel()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetListEntry assetListEntry =
			_assetListEntryService.addAssetListEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, serviceContext);

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		_assetListEntryService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry.getEntryId(), assetEntry.getEntryId()}, 0,
			serviceContext);

		Assert.assertEquals(
			1,
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRelsCount(
					assetListEntry.getAssetListEntryId()));
	}

	@Test
	public void testAssetEntrySelectionRetainsOrder() throws PortalException {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetListEntry assetListEntry =
			_assetListEntryService.addAssetListEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, serviceContext);

		AssetEntry assetEntry1 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());
		AssetEntry assetEntry2 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());
		AssetEntry assetEntry3 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		long[] expectedAssetEntryIds = {
			assetEntry2.getEntryId(), assetEntry1.getEntryId(),
			assetEntry3.getEntryId()
		};

		_assetListEntryService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(), expectedAssetEntryIds,
			SegmentsEntryConstants.ID_DEFAULT, serviceContext);

		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					assetListEntry.getAssetListEntryId(),
					SegmentsEntryConstants.ID_DEFAULT, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		Assert.assertEquals(
			assetListEntryAssetEntryRels.toString(),
			expectedAssetEntryIds.length, assetListEntryAssetEntryRels.size());

		for (int i = 0; i < expectedAssetEntryIds.length; i++) {
			AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
				assetListEntryAssetEntryRels.get(i);

			Assert.assertEquals(
				assetListEntryAssetEntryRels.toString(),
				expectedAssetEntryIds[i],
				assetListEntryAssetEntryRel.getAssetEntryId());
		}
	}

	@Test
	public void testAssetListEntryKey() throws PortalException {
		AssetListEntry assetListEntry = _addAssetListEntry("Asset List Title");

		Assert.assertEquals(
			"asset-list-title", assetListEntry.getAssetListEntryKey());
	}

	@Test
	public void testDeleteAssetListEntries() throws PortalException {
		AssetListEntry assetListEntry1 = _addAssetListEntry(
			"Asset List Title 1");
		AssetListEntry assetListEntry2 = _addAssetListEntry(
			"Asset List Title 2");

		_assetListEntryService.deleteAssetListEntries(
			new long[] {
				assetListEntry1.getAssetListEntryId(),
				assetListEntry2.getAssetListEntryId()
			});

		Assert.assertNull(
			_assetListEntryService.fetchAssetListEntry(
				assetListEntry1.getAssetListEntryId()));

		Assert.assertNull(
			_assetListEntryService.fetchAssetListEntry(
				assetListEntry2.getAssetListEntryId()));
	}

	@Test
	public void testDeleteAssetListEntry() throws PortalException {
		AssetListEntry assetListEntry = _addAssetListEntry("Asset List Title");

		_assetListEntryService.deleteAssetListEntry(
			assetListEntry.getAssetListEntryId());

		Assert.assertNull(
			_assetListEntryService.fetchAssetListEntry(
				assetListEntry.getAssetListEntryId()));
	}

	@Test
	public void testFetchAndGetAssetListEntryByExternalReferenceCode()
		throws Exception {

		AssetListEntry assetListEntry = _addAssetListEntry(
			RandomTestUtil.randomString());

		Assert.assertEquals(
			assetListEntry,
			_assetListEntryService.fetchAssetListEntryByExternalReferenceCode(
				assetListEntry.getExternalReferenceCode(),
				_group.getGroupId()));
		Assert.assertEquals(
			assetListEntry,
			_assetListEntryService.getAssetListEntryByExternalReferenceCode(
				assetListEntry.getExternalReferenceCode(),
				_group.getGroupId()));

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, AssetListEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(assetListEntry.getAssetListEntryId()),
			ActionKeys.VIEW);
		RoleTestUtil.removeResourcePermission(
			RoleConstants.SITE_MEMBER, AssetListEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(assetListEntry.getAssetListEntryId()),
			ActionKeys.VIEW);

		User user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_assetListEntryService.fetchAssetListEntryByExternalReferenceCode(
				assetListEntry.getExternalReferenceCode(), _group.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(
					"User " + user.getUserId() +
						" must have VIEW permission for"));
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_assetListEntryService.getAssetListEntryByExternalReferenceCode(
				assetListEntry.getExternalReferenceCode(), _group.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(
					"User " + user.getUserId() +
						" must have VIEW permission for"));
		}

		_userLocalService.deleteUser(user);
	}

	@Test
	public void testGetAssetListEntriesByGroup() throws PortalException {
		List<AssetListEntry> originalAssetListEntries =
			_assetListEntryService.getAssetListEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		AssetListEntry assetListEntry1 = _addAssetListEntry(
			"Asset List Title 1");

		AssetListEntry assetListEntry2 = _addAssetListEntry(
			"Asset List Title 2");

		List<AssetListEntry> actualAssetListEntries =
			_assetListEntryService.getAssetListEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			actualAssetListEntries.toString(), actualAssetListEntries.size(),
			originalAssetListEntries.size() + 2);

		Assert.assertTrue(actualAssetListEntries.contains(assetListEntry1));

		Assert.assertTrue(actualAssetListEntries.contains(assetListEntry2));
	}

	@Test
	public void testGetAssetListEntriesByOrderByDateComparator()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetListEntry assetListEntry =
			_assetListEntryService.addAssetListEntry(
				RandomTestUtil.randomString(), _group.getGroupId(), "Test Name",
				0, serviceContext);

		_assetListEntryService.addAssetListEntry(
			RandomTestUtil.randomString(), _group.getGroupId(), "A Test Name",
			0, serviceContext);

		_assetListEntryService.addAssetListEntry(
			RandomTestUtil.randomString(), _group.getGroupId(), "B Test name",
			0, serviceContext);

		OrderByComparator<AssetListEntry> orderByComparator =
			AssetListEntryCreateDateComparator.getInstance(true);

		List<AssetListEntry> assetListEntries =
			_assetListEntryService.getAssetListEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				orderByComparator);

		AssetListEntry firstAssetListEntry = assetListEntries.get(0);

		Assert.assertEquals(assetListEntry, firstAssetListEntry);

		orderByComparator = AssetListEntryCreateDateComparator.getInstance(
			false);

		assetListEntries = _assetListEntryService.getAssetListEntries(
			_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			orderByComparator);

		AssetListEntry lastAssetListEntry = assetListEntries.get(
			assetListEntries.size() - 1);

		Assert.assertEquals(lastAssetListEntry, assetListEntry);
	}

	@Test
	public void testGetAssetListEntriesByOrderByTitleComparator()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetListEntry assetListEntry =
			_assetListEntryService.addAssetListEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				"AA Asset List Entry", 0, serviceContext);

		_assetListEntryService.addAssetListEntry(
			RandomTestUtil.randomString(), _group.getGroupId(),
			"AB Asset List Entry", 0, serviceContext);

		_assetListEntryService.addAssetListEntry(
			RandomTestUtil.randomString(), _group.getGroupId(),
			"AC Asset List Entry", 0, serviceContext);

		OrderByComparator<AssetListEntry> orderByComparator =
			AssetListEntryTitleComparator.getInstance(true);

		List<AssetListEntry> assetListEntries =
			_assetListEntryService.getAssetListEntries(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				orderByComparator);

		AssetListEntry firstAssetListEntry = assetListEntries.get(0);

		Assert.assertEquals(assetListEntry, firstAssetListEntry);

		orderByComparator = AssetListEntryTitleComparator.getInstance(false);

		assetListEntries = _assetListEntryService.getAssetListEntries(
			_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			orderByComparator);

		AssetListEntry lastAssetListEntry = assetListEntries.get(
			assetListEntries.size() - 1);

		Assert.assertEquals(lastAssetListEntry, assetListEntry);
	}

	@Test
	public void testGetAssetListEntriesCountByGroup() throws PortalException {
		int originalAssetListEntriesCount =
			_assetListEntryService.getAssetListEntriesCount(
				_group.getGroupId());

		_addAssetListEntry("Asset List Title 1");

		int actualAssetListEntriesCount =
			_assetListEntryService.getAssetListEntriesCount(
				_group.getGroupId());

		Assert.assertEquals(
			actualAssetListEntriesCount, originalAssetListEntriesCount + 1);
	}

	@Test
	public void testGetAssetListEntriesWithNoAssetEntryTypes()
		throws PortalException {

		AssetListEntry assetListEntry = _addAssetListEntry("Asset List Title");

		long[] groupIds = {_group.getGroupId()};

		List<AssetListEntry> assetListEntries =
			_assetListEntryService.getAssetListEntries(
				groupIds, new String[] {assetListEntry.getAssetEntryType()},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			assetListEntries.toString(), 1, assetListEntries.size());

		assetListEntries = _assetListEntryService.getAssetListEntries(
			groupIds, new String[0], QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);

		Assert.assertTrue(
			assetListEntries.toString(), assetListEntries.isEmpty());

		Assert.assertEquals(
			0,
			_assetListEntryService.getAssetListEntriesCount(
				groupIds, new String[0]));

		assetListEntries = _assetListEntryService.getAssetListEntries(
			groupIds, "Asset List", new String[0], QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertTrue(
			assetListEntries.toString(), assetListEntries.isEmpty());

		Assert.assertEquals(
			0,
			_assetListEntryService.getAssetListEntriesCount(
				groupIds, "Asset List", new String[0]));
	}

	@Test
	public void testManualAssetEntryTypeAssetListEntry()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetListEntry assetListEntry =
			_assetListEntryService.addAssetListEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				"Manual Asset List Entry",
				AssetListEntryTypeConstants.TYPE_MANUAL, serviceContext);

		AssetEntry assetEntry1 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		_assetListEntryService.addAssetEntrySelection(
			assetListEntry.getAssetListEntryId(), assetEntry1.getEntryId(), 0,
			serviceContext);

		assetListEntry = _assetListEntryService.fetchAssetListEntry(
			assetListEntry.getAssetListEntryId());

		Assert.assertEquals(
			assetListEntry.getAssetEntryType(),
			TestAssetRendererFactory.class.getName());

		AssetEntry assetEntry2 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null, DLFileEntry.class.getName());

		_assetListEntryService.addAssetEntrySelection(
			assetListEntry.getAssetListEntryId(), assetEntry2.getEntryId(), 0,
			serviceContext);

		assetListEntry = _assetListEntryService.fetchAssetListEntry(
			assetListEntry.getAssetListEntryId());

		Assert.assertEquals(
			assetListEntry.getAssetEntryType(),
			TestAssetRendererFactory.class.getName());

		_assetListEntryService.deleteAssetEntrySelection(
			assetListEntry.getAssetListEntryId(), 0, 1);

		assetListEntry = _assetListEntryService.fetchAssetListEntry(
			assetListEntry.getAssetListEntryId());

		Assert.assertEquals(
			assetListEntry.getAssetEntryType(),
			TestAssetRendererFactory.class.getName());
	}

	@Test
	public void testUpdateAssetListEntry() throws PortalException {
		AssetListEntry assetListEntry = _addAssetListEntry("Asset List Title");

		assetListEntry = _assetListEntryService.updateAssetListEntry(
			assetListEntry.getAssetListEntryId(), "New Asset List Title");

		Assert.assertEquals("New Asset List Title", assetListEntry.getTitle());
	}

	private AssetListEntry _addAssetListEntry(String title)
		throws PortalException {

		return _assetListEntryService.addAssetListEntry(
			RandomTestUtil.randomString(), _group.getGroupId(), title, 0,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Inject
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

	@Inject
	private AssetListEntryService _assetListEntryService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private UserLocalService _userLocalService;

}