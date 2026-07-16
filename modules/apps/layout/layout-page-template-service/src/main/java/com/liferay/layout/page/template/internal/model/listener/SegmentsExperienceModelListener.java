/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.model.listener;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.segments.model.SegmentsExperience;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 */
@Component(service = ModelListener.class)
public class SegmentsExperienceModelListener
	extends BaseModelListener<SegmentsExperience> {

	@Override
	public void onBeforeRemove(SegmentsExperience segmentsExperience)
		throws ModelListenerException {

		try {
			_layoutPageTemplateStructureRelElementVariationLocalService.
				deleteLayoutPageTemplateStructureRelElementVariations(
					segmentsExperience.getPlid(),
					segmentsExperience.getExternalReferenceCode());

			_layoutPageTemplateStructureRelLocalService.
				deleteLayoutPageTemplateStructureRelsBySegmentsExperienceId(
					segmentsExperience.getSegmentsExperienceId());

			for (FragmentEntryLink fragmentEntryLink :
					_fragmentEntryLinkLocalService.
						getFragmentEntryLinksBySegmentsExperienceId(
							segmentsExperience.getGroupId(),
							segmentsExperience.getSegmentsExperienceId(),
							segmentsExperience.getPlid())) {

				_fragmentEntryLinkLocalService.deleteFragmentEntryLink(
					fragmentEntryLink);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference(unbind = "-")
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private LayoutPageTemplateStructureRelElementVariationLocalService
		_layoutPageTemplateStructureRelElementVariationLocalService;

	@Reference(unbind = "-")
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

}