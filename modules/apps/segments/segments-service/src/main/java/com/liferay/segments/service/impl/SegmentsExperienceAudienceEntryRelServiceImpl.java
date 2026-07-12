/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.base.SegmentsExperienceAudienceEntryRelServiceBaseImpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"json.web.service.context.name=segments",
		"json.web.service.context.path=SegmentsExperienceAudienceEntryRel"
	},
	service = AopService.class
)
public class SegmentsExperienceAudienceEntryRelServiceImpl
	extends SegmentsExperienceAudienceEntryRelServiceBaseImpl {

	@Override
	public List<SegmentsExperienceAudienceEntryRel>
			updateSegmentsExperienceAudienceEntryRels(
				long groupId, String[] audienceEntryERCs,
				String segmentsExperienceERC)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.
				getSegmentsExperienceByExternalReferenceCode(
					segmentsExperienceERC, groupId);

		_layoutModelResourcePermission.check(
			getPermissionChecker(), segmentsExperience.getPlid(),
			ActionKeys.UPDATE);

		return segmentsExperienceAudienceEntryRelLocalService.
			updateSegmentsExperienceAudienceEntryRels(
				getUserId(), groupId, audienceEntryERCs, segmentsExperienceERC);
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Layout)"
	)
	private ModelResourcePermission<Layout> _layoutModelResourcePermission;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}