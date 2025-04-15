/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.processor;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.segments.processor.SegmentsExperienceRequestProcessor;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "segments.experience.request.processor.priority:Integer=0",
	service = SegmentsExperienceRequestProcessor.class
)
public class DefaultSegmentsExperienceRequestProcessor
	implements SegmentsExperienceRequestProcessor {

	@Override
	public long[] getSegmentsExperienceIds(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long groupId, long plid,
			long[] segmentsExperienceIds)
		throws PortalException {

		return TransformUtil.transformToLongArray(
			_segmentsExperienceLocalService.getSegmentsExperiences(
				groupId, plid, true),
			segmentsExperience -> {
				if (segmentsExperience.getPriority() < 0) {
					return null;
				}

				return segmentsExperience.getSegmentsExperienceId();
			});
	}

	@Override
	public long[] getSegmentsExperienceIds(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long groupId, long plid,
			long[] segmentsEntryIds, long[] segmentsExperienceIds)
		throws PortalException {

		return TransformUtil.transformToLongArray(
			_segmentsExperienceLocalService.getSegmentsExperiences(
				groupId, segmentsEntryIds, plid, true),
			segmentsExperience -> {
				if (segmentsExperience.getPriority() < 0) {
					return null;
				}

				return segmentsExperience.getSegmentsExperienceId();
			});
	}

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}