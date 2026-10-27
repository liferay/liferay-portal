/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;

/**
 * @author Georgel Pop
 */
public class DesignLibraryTestUtil {

	public static Group addDesignLibraryGroup(long groupId) throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));

		return depotEntry.getGroup();
	}

	public static void assertDesignLibraryPermissionActionHrefs(
		Map<String, Map<String, String>> actions) {

		Map<String, String> getAction = actions.get("get");

		String getHref = getAction.get("href");

		Assert.assertTrue(getHref.contains("/design-libraries/"));

		Map<String, String> replaceAction = actions.get("replace");

		String replaceHref = replaceAction.get("href");

		Assert.assertTrue(replaceHref.contains("/design-libraries/"));
	}

}