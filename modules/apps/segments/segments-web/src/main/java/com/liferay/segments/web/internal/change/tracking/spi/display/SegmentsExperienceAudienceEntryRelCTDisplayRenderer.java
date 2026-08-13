/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.change.tracking.spi.display;

import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class SegmentsExperienceAudienceEntryRelCTDisplayRenderer
	extends BaseCTDisplayRenderer<SegmentsExperienceAudienceEntryRel> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			SegmentsExperienceAudienceEntryRel
				segmentsExperienceAudienceEntryRel)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.
				fetchSegmentsExperienceByExternalReferenceCode(
					segmentsExperienceAudienceEntryRel.
						getSegmentsExperienceERC(),
					segmentsExperienceAudienceEntryRel.getGroupId());

		if (segmentsExperience == null) {
			return null;
		}

		Layout layout = _layoutLocalService.fetchLayout(
			segmentsExperience.getPlid());

		if (layout == null) {
			return null;
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			draftLayout = layout;
		}

		return HttpComponentsUtil.addParameters(
			_portal.getLayoutFullURL(
				draftLayout,
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY)),
			"p_l_back_url", _portal.getCurrentURL(httpServletRequest),
			"p_l_mode", Constants.EDIT);
	}

	@Override
	public Class<SegmentsExperienceAudienceEntryRel> getModelClass() {
		return SegmentsExperienceAudienceEntryRel.class;
	}

	@Override
	public String getTitle(
		Locale locale,
		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel) {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.
				fetchSegmentsExperienceByExternalReferenceCode(
					segmentsExperienceAudienceEntryRel.
						getSegmentsExperienceERC(),
					segmentsExperienceAudienceEntryRel.getGroupId());

		String segmentsExperienceName =
			segmentsExperienceAudienceEntryRel.getSegmentsExperienceERC();

		if (segmentsExperience != null) {
			segmentsExperienceName = segmentsExperience.getName(locale);
		}

		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.
				fetchAudiencesEntryByExternalReferenceCode(
					segmentsExperienceAudienceEntryRel.getAudienceEntryERC(),
					segmentsExperienceAudienceEntryRel.getCompanyId());

		String audiencesEntryName =
			segmentsExperienceAudienceEntryRel.getAudienceEntryERC();

		if (audiencesEntry != null) {
			audiencesEntryName = audiencesEntry.getName();
		}

		return StringBundler.concat(
			segmentsExperienceName, " - ", audiencesEntryName);
	}

	@Reference
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}