/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test.util;

import com.liferay.analytics.test.util.AnalyticsCloudHttpServer;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;

/**
 * @author Leslie Wong
 */
public class DepotEntryTestUtil {

	public static DepotEntry addDepotEntry(long groupId) throws Exception {
		return DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	public static void assertGroupIds(
		List<DepotEntry> depotEntries, String url) {

		Assert.assertEquals(
			StringUtil.merge(
				TransformUtil.transformToArray(
					depotEntries, DepotEntry::getGroupId, Long.class),
				StringPool.COMMA),
			URLCodec.decodeURL(
				HttpComponentsUtil.getParameter(url, "groupIds", false)));
	}

	public static void assertNoRequest(
			AnalyticsCloudHttpServer analyticsCloudHttpServer,
			DepotEntry[] depotEntries,
			UnsafeConsumer<Long[], Exception> unsafeConsumer)
		throws Exception {

		Long[] depotEntryIds = null;

		if (depotEntries != null) {
			depotEntryIds = TransformUtil.transform(
				depotEntries, DepotEntry::getDepotEntryId, Long.class);
		}

		unsafeConsumer.accept(depotEntryIds);

		Assert.assertNull(analyticsCloudHttpServer.getLocation());
	}

	public static <T> T withDepotEntryMemberUser(
			DepotEntry depotEntry, UnsafeSupplier<T, Exception> unsafeSupplier)
		throws Exception {

		return _withUser(
			depotEntry.getCompanyId(), unsafeSupplier,
			UserTestUtil.addUser(depotEntry.getGroupId()));
	}

	public static <T> T withDepotEntryNonmemberUser(
			DepotEntry depotEntry, UnsafeSupplier<T, Exception> unsafeSupplier)
		throws Exception {

		return _withUser(
			depotEntry.getCompanyId(), unsafeSupplier, UserTestUtil.addUser());
	}

	private static <T> T _withUser(
			long companyId, UnsafeSupplier<T, Exception> unsafeSupplier,
			User user)
		throws Exception {

		try {
			UserTestUtil.setUser(user);

			return unsafeSupplier.get();
		}
		finally {
			UserTestUtil.setUser(UserTestUtil.getAdminUser(companyId));

			UserLocalServiceUtil.deleteUser(user);
		}
	}

}