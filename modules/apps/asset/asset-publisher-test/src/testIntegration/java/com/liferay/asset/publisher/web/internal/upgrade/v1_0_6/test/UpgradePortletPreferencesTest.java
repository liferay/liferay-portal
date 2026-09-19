/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.upgrade.v1_0_6.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portlet.display.template.test.util.BaseUpgradePortletPreferencesTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class UpgradePortletPreferencesTest
	extends BaseUpgradePortletPreferencesTestCase {

	@Test
	public void testUpgradeWithDifferentGroupAssetListEntry() throws Exception {
		Group guestGroup = groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		AssetListEntry assetListEntry = _addAssetListEntry(guestGroup);

		try {
			testUpgrade(
				HashMapBuilder.put(
					"assetListEntryExternalReferenceCode",
					assetListEntry.getExternalReferenceCode()
				).put(
					"assetListEntryGroupExternalReferenceCode",
					guestGroup.getExternalReferenceCode()
				).put(
					"assetListEntryId",
					String.valueOf(assetListEntry.getAssetListEntryId())
				).build(),
				HashMapBuilder.put(
					"assetListEntryId",
					String.valueOf(assetListEntry.getAssetListEntryId())
				).build());
		}
		finally {
			_assetListEntryLocalService.deleteAssetListEntry(assetListEntry);
		}
	}

	@Test
	@TestInfo("LPD-51051")
	public void testUpgradeWithDifferentGroupAssetListEntryAndPreferencesPlidShared()
		throws Exception {

		Group guestGroup = groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		AssetListEntry assetListEntry = _addAssetListEntry(guestGroup);

		try {
			testUpgrade(
				HashMapBuilder.put(
					"assetListEntryExternalReferenceCode",
					assetListEntry.getExternalReferenceCode()
				).put(
					"assetListEntryGroupExternalReferenceCode",
					guestGroup.getExternalReferenceCode()
				).put(
					"assetListEntryId",
					String.valueOf(assetListEntry.getAssetListEntryId())
				).build(),
				HashMapBuilder.put(
					"assetListEntryId",
					String.valueOf(assetListEntry.getAssetListEntryId())
				).build(),
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				PortletKeys.PREFS_PLID_SHARED);
		}
		finally {
			_assetListEntryLocalService.deleteAssetListEntry(assetListEntry);
		}
	}

	@Test
	public void testUpgradeWithGlobalGroupAssetListEntry() throws Exception {
		Group companyGroup = groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		AssetListEntry assetListEntry = _addAssetListEntry(companyGroup);

		try {
			testUpgrade(
				HashMapBuilder.put(
					"assetListEntryExternalReferenceCode",
					assetListEntry.getExternalReferenceCode()
				).put(
					"assetListEntryGroupExternalReferenceCode",
					companyGroup.getExternalReferenceCode()
				).put(
					"assetListEntryId",
					String.valueOf(assetListEntry.getAssetListEntryId())
				).build(),
				HashMapBuilder.put(
					"assetListEntryId",
					String.valueOf(assetListEntry.getAssetListEntryId())
				).build());
		}
		finally {
			_assetListEntryLocalService.deleteAssetListEntry(assetListEntry);
		}
	}

	@Test
	public void testUpgradeWithNoTypeAssetListSelectionStyle()
		throws Exception {

		testUpgrade(
			HashMapBuilder.put(
				"selectionStyle", "manual"
			).build());
	}

	@Test
	public void testUpgradeWithSameGroupAssetListEntry() throws Exception {
		AssetListEntry assetListEntry = _addAssetListEntry(group);

		testUpgrade(
			HashMapBuilder.put(
				"assetListEntryExternalReferenceCode",
				assetListEntry.getExternalReferenceCode()
			).put(
				"assetListEntryId",
				String.valueOf(assetListEntry.getAssetListEntryId())
			).build(),
			HashMapBuilder.put(
				"assetListEntryId",
				String.valueOf(assetListEntry.getAssetListEntryId())
			).build());
	}

	@Test
	public void testUpgradeWithoutAssetListEntry() throws Exception {
		testUpgrade(
			HashMapBuilder.put(
				"selectionStyle", "asset-list"
			).build());
	}

	@Override
	protected String getPortletId() {
		return AssetPublisherPortletKeys.ASSET_PUBLISHER;
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return _upgradeStepRegistrator;
	}

	@Override
	protected Version getVersion() {
		return _VERSION;
	}

	private AssetListEntry _addAssetListEntry(Group group) throws Exception {
		return _assetListEntryLocalService.addAssetListEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), 0,
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId()));
	}

	private static final Version _VERSION = new Version(1, 0, 6);

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.asset.publisher.web.internal.upgrade.registry.AssetPublisherWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}