/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.util;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelModel;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 * @author Balázs Sáfrány-Kovalik
 */
public class OrphanPortletsUtil {

	public static List<Portlet> getOrphanPortlets(
		Layout layout, PortletRegistry portletRegistry) {

		if (!layout.isSupportsEmbeddedPortlets()) {
			return Collections.emptyList();
		}

		List<PortletPreferences> portletPreferencesList =
			PortletPreferencesLocalServiceUtil.getPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid());

		if (portletPreferencesList.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> explicitlyAddedPortletIds = _getExplicitlyAddedPortletIds(
			layout, portletRegistry);

		return TransformUtil.transform(
			portletPreferencesList,
			portletPreferences -> _fetchOrphanPortlet(
				layout.getCompanyId(), explicitlyAddedPortletIds,
				portletPreferences.getPortletId()));
	}

	public static boolean hasOrphanPortlets(
		Layout layout, PortletRegistry portletRegistry) {

		return ListUtil.isNotEmpty(getOrphanPortlets(layout, portletRegistry));
	}

	private static Portlet _fetchOrphanPortlet(
		long companyId, List<String> explicitlyAddedPortletIds,
		String portletId) {

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			companyId, portletId);

		if (portlet.isSystem() ||
			explicitlyAddedPortletIds.contains(portletId)) {

			return null;
		}

		return portlet;
	}

	private static List<String> _getExplicitlyAddedPortletIds(
		Layout layout, PortletRegistry portletRegistry) {

		List<String> portletIds = new ArrayList<>(
			_getMasterLayoutPortletIds(layout, portletRegistry));

		if (layout.isTypeContent()) {
			portletIds.addAll(
				_getFragmentEntryLinkPortletIds(
					layout.getGroupId(), layout.getPlid(), portletRegistry));

			return portletIds;
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		portletIds.addAll(
			TransformUtil.transform(
				layoutTypePortlet.getExplicitlyAddedPortlets(),
				Portlet::getPortletId));

		return portletIds;
	}

	private static List<String> _getFragmentEntryLinkPortletIds(
		long groupId, long plid, PortletRegistry portletRegistry) {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			LayoutPageTemplateStructureLocalServiceUtil.
				fetchLayoutPageTemplateStructure(groupId, plid);

		if (layoutPageTemplateStructure == null) {
			return Collections.emptyList();
		}

		List<String> portletIds = new ArrayList<>();

		long[] segmentsExperienceIds = TransformUtil.transformToLongArray(
			LayoutPageTemplateStructureRelLocalServiceUtil.
				getLayoutPageTemplateStructureRels(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId()),
			LayoutPageTemplateStructureRelModel::getSegmentsExperienceId);

		for (FragmentEntryLink fragmentEntryLink :
				FragmentEntryLinkLocalServiceUtil.
					getFragmentEntryLinksBySegmentsExperienceId(
						groupId, segmentsExperienceIds, plid, false)) {

			portletIds.addAll(
				portletRegistry.getFragmentEntryLinkPortletIds(
					fragmentEntryLink));
		}

		return portletIds;
	}

	private static List<String> _getMasterLayoutPortletIds(
		Layout layout, PortletRegistry portletRegistry) {

		long masterLayoutPlid = layout.getMasterLayoutPlid();

		if (masterLayoutPlid == LayoutConstants.DEFAULT_PLID) {
			return Collections.emptyList();
		}

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(masterLayoutPlid);

		if (masterLayoutPageTemplateEntry == null) {
			return Collections.emptyList();
		}

		return _getFragmentEntryLinkPortletIds(
			masterLayoutPageTemplateEntry.getGroupId(),
			masterLayoutPageTemplateEntry.getPlid(), portletRegistry);
	}

}