/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.test.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalServiceUtil;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

/**
 * @author Kyle Miho
 */
public class AssetListTestUtil {

	public static AssetListEntry addAssetListEntry(long groupId)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return AssetListEntryLocalServiceUtil.addAssetListEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_MANUAL, serviceContext);
	}

	public static AssetListEntry addAssetListEntry(long groupId, int type)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return AssetListEntryLocalServiceUtil.addAssetListEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(), type, serviceContext);
	}

	public static AssetListEntry addAssetListEntry(long groupId, String title)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return AssetListEntryLocalServiceUtil.addAssetListEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			title, AssetListEntryTypeConstants.TYPE_MANUAL, serviceContext);
	}

	public static AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long groupId, AssetEntry assetEntry, AssetListEntry assetListEntry,
			long segmentsEntryId)
		throws PortalException {

		return AssetListEntryAssetEntryRelLocalServiceUtil.
			addAssetListEntryAssetEntryRel(
				assetListEntry.getAssetListEntryId(), assetEntry.getEntryId(),
				segmentsEntryId,
				ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long groupId, AssetEntry assetEntry, AssetListEntry assetListEntry,
			long segmentsEntryId, int position)
		throws PortalException {

		return AssetListEntryAssetEntryRelLocalServiceUtil.
			addAssetListEntryAssetEntryRel(
				assetListEntry.getAssetListEntryId(), assetEntry.getEntryId(),
				segmentsEntryId, position,
				ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static AssetListEntrySegmentsEntryRel
			addAssetListEntrySegmentsEntryRel(
				long groupId, AssetListEntry assetListEntry)
		throws PortalException {

		return addAssetListEntrySegmentsEntryRel(
			groupId, assetListEntry, RandomTestUtil.nextLong());
	}

	public static AssetListEntrySegmentsEntryRel
			addAssetListEntrySegmentsEntryRel(
				long groupId, AssetListEntry assetListEntry,
				long segmentsEntryId)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return AssetListEntrySegmentsEntryRelLocalServiceUtil.
			addAssetListEntrySegmentsEntryRel(
				TestPropsValues.getUserId(), groupId,
				assetListEntry.getAssetListEntryId(), segmentsEntryId,
				UnicodePropertiesBuilder.put(
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				).buildString(),
				serviceContext);
	}

	public static AssetListEntrySegmentsEntryRel
			addAssetListEntrySegmentsEntryRel(
				long groupId, AssetListEntry assetListEntry,
				long segmentsEntryId, String typeSettings)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return AssetListEntrySegmentsEntryRelLocalServiceUtil.
			addAssetListEntrySegmentsEntryRel(
				TestPropsValues.getUserId(), groupId,
				assetListEntry.getAssetListEntryId(), segmentsEntryId,
				typeSettings, serviceContext);
	}

}