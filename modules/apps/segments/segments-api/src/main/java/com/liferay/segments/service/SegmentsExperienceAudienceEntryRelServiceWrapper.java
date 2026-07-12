/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;

/**
 * Provides a wrapper for {@link SegmentsExperienceAudienceEntryRelService}.
 *
 * @author Eduardo Garcia
 * @see SegmentsExperienceAudienceEntryRelService
 * @generated
 */
public class SegmentsExperienceAudienceEntryRelServiceWrapper
	implements SegmentsExperienceAudienceEntryRelService,
			   ServiceWrapper<SegmentsExperienceAudienceEntryRelService> {

	public SegmentsExperienceAudienceEntryRelServiceWrapper() {
		this(null);
	}

	public SegmentsExperienceAudienceEntryRelServiceWrapper(
		SegmentsExperienceAudienceEntryRelService
			segmentsExperienceAudienceEntryRelService) {

		_segmentsExperienceAudienceEntryRelService =
			segmentsExperienceAudienceEntryRelService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _segmentsExperienceAudienceEntryRelService.
			getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<SegmentsExperienceAudienceEntryRel>
			updateSegmentsExperienceAudienceEntryRels(
				long groupId, String[] audienceEntryERCs,
				String segmentsExperienceERC)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsExperienceAudienceEntryRelService.
			updateSegmentsExperienceAudienceEntryRels(
				groupId, audienceEntryERCs, segmentsExperienceERC);
	}

	@Override
	public SegmentsExperienceAudienceEntryRelService getWrappedService() {
		return _segmentsExperienceAudienceEntryRelService;
	}

	@Override
	public void setWrappedService(
		SegmentsExperienceAudienceEntryRelService
			segmentsExperienceAudienceEntryRelService) {

		_segmentsExperienceAudienceEntryRelService =
			segmentsExperienceAudienceEntryRelService;
	}

	private SegmentsExperienceAudienceEntryRelService
		_segmentsExperienceAudienceEntryRelService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1442088335