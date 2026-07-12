/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;

import java.util.List;

/**
 * Provides the remote service utility for SegmentsExperienceAudienceEntryRel. This utility wraps
 * <code>com.liferay.segments.service.impl.SegmentsExperienceAudienceEntryRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Eduardo Garcia
 * @see SegmentsExperienceAudienceEntryRelService
 * @generated
 */
public class SegmentsExperienceAudienceEntryRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.segments.service.impl.SegmentsExperienceAudienceEntryRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<SegmentsExperienceAudienceEntryRel>
			updateSegmentsExperienceAudienceEntryRels(
				long groupId, String[] audienceEntryERCs,
				String segmentsExperienceERC)
		throws PortalException {

		return getService().updateSegmentsExperienceAudienceEntryRels(
			groupId, audienceEntryERCs, segmentsExperienceERC);
	}

	public static SegmentsExperienceAudienceEntryRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SegmentsExperienceAudienceEntryRelService>
		_serviceSnapshot = new Snapshot<>(
			SegmentsExperienceAudienceEntryRelServiceUtil.class,
			SegmentsExperienceAudienceEntryRelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1699283665