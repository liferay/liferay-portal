/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.set.scheduler;

import com.liferay.portal.kernel.exception.PortalException;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Jose Luis Navarro
 */
@ProviderType
public interface LaunchSetScheduler {

	public Date fetchScheduledDate(long launchSetId) throws PortalException;

	public void schedulePublish(long launchSetId, long userId, Date date)
		throws PortalException;

	public void unschedulePublish(long launchSetId) throws PortalException;

}