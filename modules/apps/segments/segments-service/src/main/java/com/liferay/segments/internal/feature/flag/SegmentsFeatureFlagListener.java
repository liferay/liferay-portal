/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.feature.flag;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
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
 * @author Thiago Buarque
 */
@Component(
	property = "feature.flag.key=LPD-78863", service = FeatureFlagListener.class
)
public class SegmentsFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		_companyLocalService.forEachCompany(
			company -> {
				for (Group group :
						_groupLocalService.getGroups(
							company.getCompanyId(),
							GroupConstants.ANY_PARENT_GROUP_ID, true)) {

					try {
						_processSegmentsEntries(
							enabled,
							_segmentsEntryLocalService.getSegmentsEntries(
								group.getGroupId(), QueryUtil.ALL_POS,
								QueryUtil.ALL_POS, null));
						_processSegmentsExperiences(
							enabled,
							_segmentsExperienceLocalService.
								getSegmentsExperiences(
									group.getGroupId(), !enabled));
					}
					catch (PortalException portalException) {
						if (_log.isDebugEnabled()) {
							_log.debug(
								"Unable to process group " + group.getGroupId(),
								portalException);
						}
					}
				}
			});
	}

	private void _processSegmentsEntries(
		boolean active, List<SegmentsEntry> segmentsEntries) {

		for (SegmentsEntry segmentsEntry : segmentsEntries) {
			if (Objects.equals(
					segmentsEntry.getSource(),
					SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND)) {

				continue;
			}

			segmentsEntry.setActive(active);

			_segmentsEntryLocalService.updateSegmentsEntry(segmentsEntry);
		}
	}

	private void _processSegmentsExperiences(
		boolean active, List<SegmentsExperience> segmentsExperiences) {

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

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsFeatureFlagListener.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}