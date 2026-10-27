/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.util;

import com.liferay.headless.delivery.dto.v1_0.Rating;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.ratings.kernel.model.RatingsEntry;

import java.util.Map;

/**
 * @author Javier Gamarra
 */
public class RatingUtil {

	public static Rating toRating(
		Map<String, Map<String, String>> actions, Portal portal,
		RatingsEntry ratingsEntry, UserLocalService userLocalService) {

		Rating rating = new Rating();

		rating.setActions(() -> actions);
		rating.setBestRating(() -> 1D);
		rating.setCreator(
			() -> CreatorUtil.toCreator(
				null, portal,
				userLocalService.fetchUser(ratingsEntry.getUserId())));
		rating.setDateCreated(ratingsEntry::getCreateDate);
		rating.setDateModified(ratingsEntry::getModifiedDate);
		rating.setId(ratingsEntry::getEntryId);
		rating.setRatingValue(ratingsEntry::getScore);
		rating.setWorstRating(() -> 0D);

		return rating;
	}

}