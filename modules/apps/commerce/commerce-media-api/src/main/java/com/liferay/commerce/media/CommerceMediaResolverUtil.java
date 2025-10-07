/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.media;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
public class CommerceMediaResolverUtil {

	public static String getDefaultURL(long groupId) {
		CommerceMediaResolver commerceMediaResolver =
			_serviceTracker.getService();

		return commerceMediaResolver.getDefaultURL(groupId);
	}

	public static String getDownloadURL(
			long commerceAccountId, long cpAttachmentFileEntryId)
		throws PortalException {

		CommerceMediaResolver commerceMediaResolver =
			_serviceTracker.getService();

		return commerceMediaResolver.getDownloadURL(
			commerceAccountId, cpAttachmentFileEntryId);
	}

	public static String getThumbnailURL(
			long commerceAccountId, long cpAttachmentFileEntryId)
		throws PortalException {

		return getThumbnailURL(
			commerceAccountId, cpAttachmentFileEntryId, true);
	}

	public static String getThumbnailURL(
			long commerceAccountId, long cpAttachmentFileEntryId,
			boolean secure)
		throws PortalException {

		CommerceMediaResolver commerceMediaResolver =
			_serviceTracker.getService();

		return commerceMediaResolver.getThumbnailURL(
			commerceAccountId, cpAttachmentFileEntryId, secure);
	}

	public static String getURL(
			long commerceAccountId, long cpAttachmentFileEntryId)
		throws PortalException {

		CommerceMediaResolver commerceMediaResolver =
			_serviceTracker.getService();

		return commerceMediaResolver.getURL(
			commerceAccountId, cpAttachmentFileEntryId);
	}

	private static final ServiceTracker<?, CommerceMediaResolver>
		_serviceTracker = ServiceTrackerFactory.open(
			FrameworkUtil.getBundle(CommerceMediaResolverUtil.class),
			CommerceMediaResolver.class);

}