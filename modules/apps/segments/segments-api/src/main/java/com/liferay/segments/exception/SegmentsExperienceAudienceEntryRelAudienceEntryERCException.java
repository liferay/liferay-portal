/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Eduardo García
 */
public class SegmentsExperienceAudienceEntryRelAudienceEntryERCException
	extends PortalException {

	public SegmentsExperienceAudienceEntryRelAudienceEntryERCException() {
	}

	public SegmentsExperienceAudienceEntryRelAudienceEntryERCException(
		String msg) {

		super(msg);
	}

	public SegmentsExperienceAudienceEntryRelAudienceEntryERCException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public SegmentsExperienceAudienceEntryRelAudienceEntryERCException(
		Throwable throwable) {

		super(throwable);
	}

}