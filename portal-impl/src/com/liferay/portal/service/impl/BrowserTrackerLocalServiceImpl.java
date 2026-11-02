/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.model.BrowserTracker;
import com.liferay.portal.service.base.BrowserTrackerLocalServiceBaseImpl;

/**
 * @author Brian Wing Shun Chan
 */
public class BrowserTrackerLocalServiceImpl
	extends BrowserTrackerLocalServiceBaseImpl {

	@Override
	public void deleteUserBrowserTracker(long userId) {
		BrowserTracker browserTracker = browserTrackerPersistence.fetchByUserId(
			userId);

		if (browserTracker != null) {
			browserTrackerPersistence.remove(browserTracker);
		}
	}

}