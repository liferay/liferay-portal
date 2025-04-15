/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.manager;

import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class SegmentsExperienceManager {

	public SegmentsExperienceManager(
		SegmentsExperienceLocalService segmentsExperienceLocalService) {

		_segmentsExperienceLocalService = segmentsExperienceLocalService;
	}

	public long getSegmentsExperienceId(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return _segmentsExperienceLocalService.
				fetchDefaultSegmentsExperienceId(
					ParamUtil.getLong(httpServletRequest, "plid"));
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		long segmentsExperienceId = ParamUtil.getLong(
			PortalUtil.getOriginalServletRequest(httpServletRequest),
			"segmentsExperienceId", -1);

		if ((segmentsExperienceId != -1) &&
			permissionChecker.isGroupAdmin(themeDisplay.getScopeGroupId())) {

			return segmentsExperienceId;
		}

		long[] segmentsExperienceIds = GetterUtil.getLongValues(
			httpServletRequest.getAttribute(
				SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS));

		if (ArrayUtil.isNotEmpty(segmentsExperienceIds)) {
			return segmentsExperienceIds[0];
		}

		return _segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
			themeDisplay.getPlid());
	}

	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;

}