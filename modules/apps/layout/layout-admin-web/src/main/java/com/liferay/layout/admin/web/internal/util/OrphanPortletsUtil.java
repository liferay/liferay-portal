/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.util;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelModel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Layout;
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
			_getPortletPreferences(layout);

		if (portletPreferencesList.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> explicitlyAddedPortletIds = _getExplicitlyAddedPortletIds(
			layout, portletRegistry);

		List<Portlet> orphanPortlets = new ArrayList<>();

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			Portlet portlet = _fetchOrphanPortlet(
				layout.getCompanyId(), explicitlyAddedPortletIds,
				portletPreferences.getPortletId());

			if (portlet != null) {
				orphanPortlets.add(portlet);
			}
		}

		return orphanPortlets;
	}

	public static boolean hasOrphanPortlets(
		Layout layout, PortletRegistry portletRegistry) {

		if (!layout.isSupportsEmbeddedPortlets()) {
			return false;
		}

		List<PortletPreferences> portletPreferencesList =
			_getPortletPreferences(layout);

		if (portletPreferencesList.isEmpty()) {
			return false;
		}

		List<String> explicitlyAddedPortletIds = _getExplicitlyAddedPortletIds(
			layout, portletRegistry);

		return ListUtil.exists(
			portletPreferencesList,
			portletPreferences -> {
				Portlet portlet = _fetchOrphanPortlet(
					layout.getCompanyId(), explicitlyAddedPortletIds,
					portletPreferences.getPortletId());

				if (portlet == null) {
					return false;
				}

				return true;
			});
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

		if (layout.isTypeContent()) {
			return _getTypeContentExplicitlyAddedPortletIds(
				layout, portletRegistry);
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		return TransformUtil.transform(
			layoutTypePortlet.getExplicitlyAddedPortlets(),
			Portlet::getPortletId);
	}

	private static List<PortletPreferences> _getPortletPreferences(
		Layout layout) {

		return PortletPreferencesLocalServiceUtil.getPortletPreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid());
	}

	private static List<String> _getTypeContentExplicitlyAddedPortletIds(
		Layout layout, PortletRegistry portletRegistry) {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			LayoutPageTemplateStructureLocalServiceUtil.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		if (layoutPageTemplateStructure == null) {
			return Collections.emptyList();
		}

		long[] segmentsExperienceIds = TransformUtil.transformToLongArray(
			LayoutPageTemplateStructureRelLocalServiceUtil.
				getLayoutPageTemplateStructureRels(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId()),
			LayoutPageTemplateStructureRelModel::getSegmentsExperienceId);

		List<String> portletIds = new ArrayList<>();

		for (FragmentEntryLink fragmentEntryLink :
				FragmentEntryLinkLocalServiceUtil.
					getFragmentEntryLinksBySegmentsExperienceId(
						layout.getGroupId(), segmentsExperienceIds,
						layout.getPlid(), false)) {

			portletIds.addAll(
				portletRegistry.getFragmentEntryLinkPortletIds(
					fragmentEntryLink));
		}

		return portletIds;
	}

}