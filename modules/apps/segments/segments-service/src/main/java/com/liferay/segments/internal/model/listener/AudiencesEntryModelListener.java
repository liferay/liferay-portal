/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.model.listener;

import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.segments.service.SegmentsExperienceAudienceEntryRelLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = ModelListener.class)
public class AudiencesEntryModelListener
	extends BaseModelListener<AudiencesEntry> {

	@Override
	public void onBeforeRemove(AudiencesEntry audiencesEntry) {
		_segmentsExperienceAudienceEntryRelLocalService.
			deleteSegmentsExperienceAudienceEntryRelsByAudienceEntryERC(
				audiencesEntry.getCompanyId(),
				audiencesEntry.getExternalReferenceCode());
	}

	@Reference
	private SegmentsExperienceAudienceEntryRelLocalService
		_segmentsExperienceAudienceEntryRelLocalService;

}