/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.model.listener;

import com.liferay.layout.content.page.editor.web.internal.cache.ElementVariationsCache;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationLocalService;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = ModelListener.class)
public class
	LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelListener
		extends BaseModelListener
			<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel> {

	@Override
	public void onAfterRemove(
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation =
				_layoutPageTemplateStructureRelElementVariationLocalService.
					fetchLayoutPageTemplateStructureRelElementVariationByExternalReferenceCode(
						layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
							getLayoutPageTemplateStructureRelElementVariationERC(),
						layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
							getGroupId());

		if (layoutPageTemplateStructureRelElementVariation == null) {
			return;
		}

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.
				fetchSegmentsExperienceByExternalReferenceCode(
					layoutPageTemplateStructureRelElementVariation.
						getSegmentsExperienceERC(),
					layoutPageTemplateStructureRelElementVariation.
						getGroupId());

		if (segmentsExperience == null) {
			return;
		}

		_elementVariationsCache.removeElementVariations(
			layoutPageTemplateStructureRelElementVariation.getPlid(),
			segmentsExperience.getSegmentsExperienceId());
	}

	@Reference
	private ElementVariationsCache _elementVariationsCache;

	@Reference
	private LayoutPageTemplateStructureRelElementVariationLocalService
		_layoutPageTemplateStructureRelElementVariationLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}