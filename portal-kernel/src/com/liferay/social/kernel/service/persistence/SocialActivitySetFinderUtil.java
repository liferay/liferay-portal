/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SocialActivitySetFinderUtil {

	public static int countByOrganizationId(long organizationId) {
		return getFinder().countByOrganizationId(organizationId);
	}

	public static int countByRelationType(long userId, int type) {
		return getFinder().countByRelationType(userId, type);
	}

	public static int countByUser(long userId) {
		return getFinder().countByUser(userId);
	}

	public static int countByUserGroups(long userId) {
		return getFinder().countByUserGroups(userId);
	}

	public static java.util.List
		<com.liferay.social.kernel.model.SocialActivitySet>
			findByOrganizationId(long organizationId, int start, int end) {

		return getFinder().findByOrganizationId(organizationId, start, end);
	}

	public static java.util.List
		<com.liferay.social.kernel.model.SocialActivitySet> findByRelationType(
			long userId, int type, int start, int end) {

		return getFinder().findByRelationType(userId, type, start, end);
	}

	public static java.util.List
		<com.liferay.social.kernel.model.SocialActivitySet> findByUser(
			long userId, int start, int end) {

		return getFinder().findByUser(userId, start, end);
	}

	public static java.util.List
		<com.liferay.social.kernel.model.SocialActivitySet> findByUserGroups(
			long userId, int start, int end) {

		return getFinder().findByUserGroups(userId, start, end);
	}

	public static SocialActivitySetFinder getFinder() {
		if (_finder == null) {
			_finder = (SocialActivitySetFinder)PortalBeanLocatorUtil.locate(
				SocialActivitySetFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(SocialActivitySetFinder finder) {
		_finder = finder;
	}

	private static SocialActivitySetFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2144153862