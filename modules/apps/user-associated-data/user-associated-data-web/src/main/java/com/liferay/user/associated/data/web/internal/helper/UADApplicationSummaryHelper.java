/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.helper;

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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = UADApplicationSummaryHelper.class)
public class UADApplicationSummaryHelper {

	public List<UADAnonymizer<?>> getApplicationUADAnonymizers(
		String applicationKey) {

		List<UADAnonymizer<?>> uadAnonymizers = new ArrayList<>();

		for (UADDisplay<?> uadDisplay :
				_uadRegistry.getApplicationUADDisplays(applicationKey)) {

			uadAnonymizers.add(
				_uadRegistry.getUADAnonymizer(uadDisplay.getTypeKey()));
		}

		return uadAnonymizers;
	}

	public String getDefaultUADRegistryKey(String applicationKey) {
		List<UADDisplay<?>> uadDisplays;

		if (applicationKey.equals("all-applications")) {
			uadDisplays = ListUtil.fromCollection(
				_uadRegistry.getUADDisplays());
		}
		else {
			uadDisplays = _uadRegistry.getApplicationUADDisplays(
				applicationKey);
		}

		UADDisplay<?> uadDisplay = uadDisplays.get(0);

		if (uadDisplay == null) {
			return null;
		}

		return uadDisplay.getTypeKey();
	}

	public int getTotalNonreviewableUADEntitiesCount(long userId) {
		return _getNonreviewableUADEntitiesCount(
			_uadRegistry.getNonreviewableUADAnonymizers(), userId);
	}

	public int getTotalReviewableUADEntitiesCount(long userId) {
		return _getReviewableUADEntitiesCount(
			_uadRegistry.getUADDisplays(), userId);
	}

	public UADApplicationSummaryDisplay getUADApplicationSummaryDisplay(
		String applicationKey, List<UADDisplay<?>> uadDisplays, long userId,
		long[] groupIds) {

		UADApplicationSummaryDisplay uadApplicationSummaryDisplay =
			new UADApplicationSummaryDisplay();

		uadApplicationSummaryDisplay.setCount(
			_getReviewableUADEntitiesCount(uadDisplays, userId, groupIds));
		uadApplicationSummaryDisplay.setApplicationKey(applicationKey);

		return uadApplicationSummaryDisplay;
	}

	public List<UADApplicationSummaryDisplay> getUADApplicationSummaryDisplays(
		long userId, long[] groupIds) {

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
				_uadRegistry.getApplicationUADDisplaysKeySet()) {

			List<UADDisplay<?>> applicationUADDisplays = new ArrayList<>();

			for (UADDisplay<?> uadDisplay :
					_uadRegistry.getApplicationUADDisplays(applicationKey)) {

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

	private int _getNonreviewableUADEntitiesCount(
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

	private int _getReviewableUADEntitiesCount(
		Collection<UADDisplay<?>> uadDisplays, long userId) {

		int sum = 0;

		for (UADDisplay<?> uadDisplay : uadDisplays) {
			int userIds = (int)uadDisplay.count(userId);

			sum += userIds;
		}

		return sum;
	}

	private int _getReviewableUADEntitiesCount(
		List<UADDisplay<?>> uadDisplays, long userId, long[] groupIds) {

		int sum = 0;

		for (UADDisplay<?> uadDisplay : uadDisplays) {
			int userIds = (int)uadDisplay.searchCount(userId, groupIds, null);

			sum += userIds;
		}

		return sum;
	}

	@Reference
	private UADRegistry _uadRegistry;

}