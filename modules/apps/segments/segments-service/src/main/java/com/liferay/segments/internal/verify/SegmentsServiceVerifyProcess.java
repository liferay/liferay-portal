/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.verify;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = {"initial.deployment=true", "run.on.portal.upgrade=true"},
	service = VerifyProcess.class
)
public class SegmentsServiceVerifyProcess extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {

		// Data created before the services started resolving the governed
		// state at the write can contradict the feature flag. Deactivation is
		// the only direction reconciled here, because reactivating without a
		// flag change would discard deliberate deactivations, while inactive
		// rows under an enabled flag cost nothing

		if (FeatureFlagManagerUtil.isEnabled(
				CompanyConstants.SYSTEM, "LPD-78863")) {

			return;
		}

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_companyLocalService.forEachCompanyId(
				companyId -> _deactivateSegments(companyId));
		}
	}

	private void _deactivateSegments(long companyId) {
		long[] groupIds = TransformUtil.transformToLongArray(
			_groupLocalService.getGroups(
				companyId, GroupConstants.ANY_PARENT_GROUP_ID, true),
			GroupModel::getGroupId);

		// An empty group ID array contributes no SQL fragment to the
		// arrayable finders below, which would leave them scoped by nothing
		// but the active and source columns and matching every row in the
		// database

		if (groupIds.length == 0) {
			return;
		}

		List<SegmentsEntry> segmentsEntries =
			_segmentsEntryLocalService.getSegmentsEntries(
				groupIds, true,
				new String[] {
					SegmentsEntryConstants.SOURCE_DEFAULT,
					SegmentsEntryConstants.SOURCE_REFERRED
				});

		for (SegmentsEntry segmentsEntry : segmentsEntries) {
			segmentsEntry.setActive(false);

			_segmentsEntryLocalService.updateSegmentsEntry(segmentsEntry);
		}

		List<SegmentsExperience> segmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				groupIds, true);

		for (SegmentsExperience segmentsExperience : segmentsExperiences) {
			SegmentsEntry segmentsEntry =
				_segmentsEntryLocalService.fetchSegmentsEntry(
					segmentsExperience.getSegmentsEntryId());

			if ((segmentsEntry == null) ||
				Objects.equals(
					segmentsEntry.getSource(),
					SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND)) {

				continue;
			}

			segmentsExperience.setActive(false);

			_segmentsExperienceLocalService.updateSegmentsExperience(
				segmentsExperience);
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}