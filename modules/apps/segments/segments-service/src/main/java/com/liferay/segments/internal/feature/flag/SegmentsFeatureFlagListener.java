/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.feature.flag;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(
	property = "feature.flag.key=LPD-78863", service = FeatureFlagListener.class
)
public class SegmentsFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if (companyId != CompanyConstants.SYSTEM) {
			return;
		}

		// A listener receives the current value whenever it registers, which
		// happens at least once while the portal is starting up, and the
		// provider redelivers the current value without a change. The
		// segments entries and segments experiences already hold the value
		// that was last applied to them, so only a change in the value calls
		// for rewriting them

		Boolean lastEnabled = _lastEnabledAtomicReference.getAndSet(enabled);

		if ((lastEnabled == null) || (lastEnabled == enabled)) {
			return;
		}

		_updateSegments(enabled);
	}

	private void _updateSegments(boolean enabled) {
		_companyLocalService.forEachCompany(
			company -> {
				long[] groupIds = TransformUtil.transformToLongArray(
					_groupLocalService.getGroups(
						company.getCompanyId(),
						GroupConstants.ANY_PARENT_GROUP_ID, true),
					GroupModel::getGroupId);

				// An empty group ID array contributes no SQL fragment to the
				// arrayable finders below, which would leave them scoped by
				// nothing but the active and source columns and matching
				// every row in the database

				if (groupIds.length == 0) {
					return;
				}

				_updateSegmentsEntries(enabled, groupIds);
				_updateSegmentsExperiences(enabled, groupIds);
			});
	}

	private void _updateSegmentsEntries(boolean active, long[] groupIds) {
		List<SegmentsEntry> segmentsEntries =
			_segmentsEntryLocalService.getSegmentsEntries(
				groupIds, !active,
				new String[] {
					SegmentsEntryConstants.SOURCE_DEFAULT,
					SegmentsEntryConstants.SOURCE_REFERRED
				});

		for (SegmentsEntry segmentsEntry : segmentsEntries) {
			segmentsEntry.setActive(active);

			_segmentsEntryLocalService.updateSegmentsEntry(segmentsEntry);
		}
	}

	private void _updateSegmentsExperiences(boolean active, long[] groupIds) {
		List<SegmentsExperience> segmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				groupIds, !active);

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

			segmentsExperience.setActive(active);

			_segmentsExperienceLocalService.updateSegmentsExperience(
				segmentsExperience);
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	private final AtomicReference<Boolean> _lastEnabledAtomicReference =
		new AtomicReference<>();

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}