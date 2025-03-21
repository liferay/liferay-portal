/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.model.listener;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.segments.model.SegmentsExperience;

import jakarta.portlet.PortletPreferences;

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
			_layoutPageTemplateStructureRelLocalService.
				deleteLayoutPageTemplateStructureRelsBySegmentsExperienceId(
					segmentsExperience.getSegmentsExperienceId());

			for (FragmentEntryLink fragmentEntryLink :
					_fragmentEntryLinkLocalService.
						getFragmentEntryLinksBySegmentsExperienceId(
							segmentsExperience.getGroupId(),
							segmentsExperience.getSegmentsExperienceId(),
							segmentsExperience.getPlid())) {

				for (String portletId :
						_portletRegistry.getFragmentEntryLinkPortletIds(
							fragmentEntryLink)) {

					PortletPreferences jxPortletPreferences =
						_portletPreferencesLocalService.fetchPreferences(
							fragmentEntryLink.getCompanyId(),
							PortletKeys.PREFS_OWNER_ID_DEFAULT,
							PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
							fragmentEntryLink.getPlid(), portletId);

					if (jxPortletPreferences != null) {
						_portletPreferencesLocalService.
							deletePortletPreferences(
								PortletKeys.PREFS_OWNER_ID_DEFAULT,
								PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
								fragmentEntryLink.getPlid(), portletId);
					}
				}

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

	@Reference(unbind = "-")
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletRegistry _portletRegistry;

}