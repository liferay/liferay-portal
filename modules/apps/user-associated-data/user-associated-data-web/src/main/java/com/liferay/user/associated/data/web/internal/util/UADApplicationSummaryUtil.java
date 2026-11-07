/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.constants.UADConstants;
import com.liferay.user.associated.data.web.internal.display.UADApplicationSummaryDisplay;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Drew Brokke
 */
public class UADApplicationSummaryUtil {

	public static List<UADAnonymizer<?>> getApplicationUADAnonymizers(
		String applicationKey, UADRegistry uadRegistry) {

		List<UADAnonymizer<?>> uadAnonymizers = new ArrayList<>();

		for (UADDisplay<?> uadDisplay :
				uadRegistry.getApplicationUADDisplays(applicationKey)) {

			uadAnonymizers.add(
				uadRegistry.getUADAnonymizer(uadDisplay.getTypeKey()));
		}

		return uadAnonymizers;
	}

	public static String getDefaultUADRegistryKey(
		String applicationKey, UADRegistry uadRegistry) {

		List<UADDisplay<?>> uadDisplays;

		if (applicationKey.equals("all-applications")) {
			uadDisplays = ListUtil.fromCollection(uadRegistry.getUADDisplays());
		}
		else {
			uadDisplays = uadRegistry.getApplicationUADDisplays(applicationKey);
		}

		UADDisplay<?> uadDisplay = uadDisplays.get(0);

		if (uadDisplay == null) {
			return null;
		}

		return uadDisplay.getTypeKey();
	}

	public static int getTotalNonreviewableUADEntitiesCount(
		UADRegistry uadRegistry, long userId) {

		return _getNonreviewableUADEntitiesCount(
			uadRegistry.getNonreviewableUADAnonymizers(), userId);
	}

	public static int getTotalReviewableUADEntitiesCount(
		UADRegistry uadRegistry, long userId) {

		return _getReviewableUADEntitiesCount(
			uadRegistry.getUADDisplays(), userId);
	}

	public static UADApplicationSummaryDisplay getUADApplicationSummaryDisplay(
		String applicationKey, List<UADDisplay<?>> uadDisplays, long userId,
		long[] groupIds) {

		UADApplicationSummaryDisplay uadApplicationSummaryDisplay =
			new UADApplicationSummaryDisplay();

		uadApplicationSummaryDisplay.setCount(
			_getReviewableUADEntitiesCount(uadDisplays, userId, groupIds));
		uadApplicationSummaryDisplay.setApplicationKey(applicationKey);

		return uadApplicationSummaryDisplay;
	}

	public static List<UADApplicationSummaryDisplay>
		getUADApplicationSummaryDisplays(
			long[] groupIds, UADRegistry uadRegistry, long userId) {

		List<UADApplicationSummaryDisplay> uadApplicationSummaryDisplays =
			new ArrayList<>();

		UADApplicationSummaryDisplay
			allApplicationsUADApplicationSummaryDisplay =
				new UADApplicationSummaryDisplay();

		allApplicationsUADApplicationSummaryDisplay.setApplicationKey(
			UADConstants.ALL_APPLICATIONS);

		List<UADApplicationSummaryDisplay>
			generatedUADApplicationSummaryDisplays = new ArrayList<>();

		int count = 0;

		for (String applicationKey :
				uadRegistry.getApplicationUADDisplaysKeySet()) {

			List<UADDisplay<?>> applicationUADDisplays = new ArrayList<>();

			for (UADDisplay<?> uadDisplay :
					uadRegistry.getApplicationUADDisplays(applicationKey)) {

				if (ArrayUtil.isNotEmpty(groupIds) !=
						uadDisplay.isSiteScoped()) {

					continue;
				}

				applicationUADDisplays.add(uadDisplay);
			}

			if (ListUtil.isEmpty(applicationUADDisplays)) {
				continue;
			}

			UADApplicationSummaryDisplay uadApplicationSummaryDisplay =
				getUADApplicationSummaryDisplay(
					applicationKey, applicationUADDisplays, userId, groupIds);

			generatedUADApplicationSummaryDisplays.add(
				uadApplicationSummaryDisplay);

			count += uadApplicationSummaryDisplay.getCount();
		}

		allApplicationsUADApplicationSummaryDisplay.setCount(count);

		uadApplicationSummaryDisplays.add(
			allApplicationsUADApplicationSummaryDisplay);

		generatedUADApplicationSummaryDisplays.sort(
			(uadApplicationSummaryDisplay, uadApplicationSummaryDisplay2) -> {
				String applicationKey1 =
					uadApplicationSummaryDisplay.getApplicationKey();

				return applicationKey1.compareTo(
					uadApplicationSummaryDisplay2.getApplicationKey());
			});

		uadApplicationSummaryDisplays.addAll(
			generatedUADApplicationSummaryDisplays);

		return uadApplicationSummaryDisplays;
	}

	private static int _getNonreviewableUADEntitiesCount(
		Collection<UADAnonymizer<?>> uadAnonymizers, long userId) {

		int sum = 0;

		for (UADAnonymizer<?> uadAnonymizer : uadAnonymizers) {
			try {
				int userIds = (int)uadAnonymizer.count(userId);

				sum += userIds;
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}
		}

		return sum;
	}

	private static int _getReviewableUADEntitiesCount(
		Collection<UADDisplay<?>> uadDisplays, long userId) {

		int sum = 0;

		for (UADDisplay<?> uadDisplay : uadDisplays) {
			int userIds = (int)uadDisplay.count(userId);

			sum += userIds;
		}

		return sum;
	}

	private static int _getReviewableUADEntitiesCount(
		List<UADDisplay<?>> uadDisplays, long userId, long[] groupIds) {

		int sum = 0;

		for (UADDisplay<?> uadDisplay : uadDisplays) {
			int userIds = (int)uadDisplay.searchCount(userId, groupIds, null);

			sum += userIds;
		}

		return sum;
	}

}