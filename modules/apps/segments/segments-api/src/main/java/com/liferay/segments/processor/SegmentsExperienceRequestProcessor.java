/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.processor;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides methods for processing {@link
 * com.liferay.segments.model.SegmentsExperiment SegmentsExperiment}s.
 *
 * @author Eduardo García
 * @review
 */
@ProviderType
public interface SegmentsExperienceRequestProcessor {

	/**
	 * Returns the processed IDs of segments experiences that will be actually
	 * applied in the current request.
	 *
	 * @param  httpServletRequest the servlet request
	 * @param  httpServletResponse the servlet response
	 * @param  groupId the primary key of the group
	 * @param  plid the primary key of the layout
	 * @param  segmentsExperienceIds the primary keys of the user's active
	 *         segment experiences
	 * @return the processed IDs of segments experiences that will be actually
	 *         applied in the current request
	 * @throws PortalException if a portal exception occurred
	 */
	public long[] getSegmentsExperienceIds(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long groupId, long plid,
			long[] segmentsExperienceIds)
		throws PortalException;

	/**
	 * Returns the processed IDs of segments experiences that will be actually
	 * applied in the current request.
	 *
	 * @param  httpServletRequest the servlet request
	 * @param  httpServletResponse the servlet response
	 * @param  groupId the primary key of the group
	 * @param  plid the primary key of the layout
	 * @param  segmentsEntryIds the primary keys of the user's active segment
	 *         entries
	 * @param  segmentsExperienceIds the primary keys of the user's active
	 *         segment experiences
	 * @return the processed IDs of segments experiences that will be actually
	 *         applied in the current request
	 * @throws PortalException if a portal exception occurred
	 */
	public long[] getSegmentsExperienceIds(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long groupId, long plid,
			long[] segmentsEntryIds, long[] segmentsExperienceIds)
		throws PortalException;

}