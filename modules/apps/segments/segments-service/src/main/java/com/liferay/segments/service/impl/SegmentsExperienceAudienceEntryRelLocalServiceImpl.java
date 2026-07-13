/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.exception.SegmentsExperienceAudienceEntryRelAudienceEntryERCException;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;
import com.liferay.segments.service.base.SegmentsExperienceAudienceEntryRelLocalServiceBaseImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "model.class.name=com.liferay.segments.model.SegmentsExperienceAudienceEntryRel",
	service = AopService.class
)
public class SegmentsExperienceAudienceEntryRelLocalServiceImpl
	extends SegmentsExperienceAudienceEntryRelLocalServiceBaseImpl {

	@Override
	public void deleteSegmentsExperienceAudienceEntryRelsByAudienceEntryERC(
		long companyId, String audienceEntryERC) {

		segmentsExperienceAudienceEntryRelPersistence.removeByC_AEERC(
			companyId, audienceEntryERC);
	}

	@Override
	public void
		deleteSegmentsExperienceAudienceEntryRelsBySegmentsExperienceERC(
			long groupId, String segmentsExperienceERC) {

		segmentsExperienceAudienceEntryRelPersistence.removeByG_SEERC(
			groupId, segmentsExperienceERC);
	}

	@Override
	public List<SegmentsExperienceAudienceEntryRel>
		getSegmentsExperienceAudienceEntryRels(
			long groupId, String segmentsExperienceERC) {

		return segmentsExperienceAudienceEntryRelPersistence.findByG_SEERC(
			groupId, segmentsExperienceERC);
	}

	@Override
	public List<SegmentsExperienceAudienceEntryRel>
			updateSegmentsExperienceAudienceEntryRels(
				long userId, long groupId, String[] audienceEntryERCs,
				String segmentsExperienceERC)
		throws PortalException {

		_validate(audienceEntryERCs);

		deleteSegmentsExperienceAudienceEntryRelsBySegmentsExperienceERC(
			groupId, segmentsExperienceERC);

		segmentsExperienceAudienceEntryRelPersistence.flush();

		List<SegmentsExperienceAudienceEntryRel>
			segmentsExperienceAudienceEntryRels = new ArrayList<>();

		User user = _userLocalService.getUser(userId);

		for (int i = 0; i < audienceEntryERCs.length; i++) {
			SegmentsExperienceAudienceEntryRel
				segmentsExperienceAudienceEntryRel =
					segmentsExperienceAudienceEntryRelPersistence.create(
						counterLocalService.increment());

			segmentsExperienceAudienceEntryRel.setGroupId(groupId);
			segmentsExperienceAudienceEntryRel.setCompanyId(
				user.getCompanyId());
			segmentsExperienceAudienceEntryRel.setUserId(user.getUserId());
			segmentsExperienceAudienceEntryRel.setUserName(user.getFullName());
			segmentsExperienceAudienceEntryRel.setAudienceEntryERC(
				audienceEntryERCs[i]);
			segmentsExperienceAudienceEntryRel.setPriority(
				audienceEntryERCs.length - i);
			segmentsExperienceAudienceEntryRel.setSegmentsExperienceERC(
				segmentsExperienceERC);

			segmentsExperienceAudienceEntryRels.add(
				segmentsExperienceAudienceEntryRelPersistence.update(
					segmentsExperienceAudienceEntryRel));
		}

		return segmentsExperienceAudienceEntryRels;
	}

	private void _validate(String[] audienceEntryERCs) throws PortalException {
		Set<String> audienceEntryERCsSet = new HashSet<>();

		for (String audienceEntryERC : audienceEntryERCs) {
			if (Validator.isNull(audienceEntryERC)) {
				throw new SegmentsExperienceAudienceEntryRelAudienceEntryERCException(
					"Audience entry external reference codes are required");
			}

			if (!audienceEntryERCsSet.add(audienceEntryERC)) {
				throw new SegmentsExperienceAudienceEntryRelAudienceEntryERCException(
					"Duplicate audience entry external reference code " +
						audienceEntryERC);
			}
		}
	}

	@Reference
	private UserLocalService _userLocalService;

}