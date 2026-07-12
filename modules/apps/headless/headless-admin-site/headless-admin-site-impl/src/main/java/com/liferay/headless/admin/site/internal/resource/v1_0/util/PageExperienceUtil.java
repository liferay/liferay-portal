/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.exception.DefaultSegmentsExperienceSegmentException;
import com.liferay.segments.exception.DuplicateSegmentsExperienceKeyException;
import com.liferay.segments.model.SegmentsExperience;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class PageExperienceUtil {

	public static PageExperience getDefaultPageExperience(
		PageExperience[] pageExperiences) {

		if (ArrayUtil.isEmpty(pageExperiences)) {
			throw new IllegalArgumentException("A page experience is required");
		}

		for (PageExperience pageExperience : pageExperiences) {
			if (Objects.equals(
					pageExperience.getKey(),
					SegmentsExperienceConstants.KEY_DEFAULT)) {

				return pageExperience;
			}
		}

		throw new IllegalArgumentException(
			"A default page experience is required");
	}

	public static void validatePageExperiences(
			SegmentsExperience defaultSegmentsExperience,
			PageExperience[] pageExperiences)
		throws PortalException {

		if (defaultSegmentsExperience == null) {
			throw new IllegalArgumentException(
				"The default page experience does not exist");
		}

		if (ArrayUtil.isEmpty(pageExperiences)) {
			throw new IllegalArgumentException("A page experience is required");
		}

		Set<String> pageExperienceKeys = new HashSet<>(pageExperiences.length);

		PageExperience defaultPageExperience = null;

		for (PageExperience pageExperience : pageExperiences) {
			if (Validator.isNull(pageExperience.getKey())) {
				throw new IllegalArgumentException(
					"A page experience key is required");
			}

			if (!pageExperienceKeys.add(pageExperience.getKey())) {
				throw new DuplicateSegmentsExperienceKeyException(
					pageExperience.getKey());
			}

			if (Objects.equals(
					pageExperience.getKey(),
					SegmentsExperienceConstants.KEY_DEFAULT)) {

				defaultPageExperience = pageExperience;
			}
		}

		if (defaultPageExperience == null) {
			throw new IllegalArgumentException(
				"A default page experience is required");
		}

		if ((defaultPageExperience.getPriority() != null) &&
			(defaultPageExperience.getPriority() != 0)) {

			throw new IllegalArgumentException(
				"The default page experience must have a priority of 0");
		}

		if (!StringUtil.equals(
				defaultSegmentsExperience.getExternalReferenceCode(),
				defaultPageExperience.getExternalReferenceCode())) {

			throw new IllegalArgumentException(
				"The external reference code does not match the target " +
					"page's experience external reference code");
		}

		if (defaultPageExperience.getSegmentItemExternalReference() != null) {
			throw new DefaultSegmentsExperienceSegmentException();
		}
	}

}