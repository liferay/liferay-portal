/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.BadRequestException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Daniel Raposo
 */
public class LayoutUtil {

	public static Layout fetchPortletLayout(
			Group group, String portletId, String sitePageExternalReferenceCode)
		throws PortalException {

		if (Validator.isBlank(portletId)) {
			if (Validator.isNotNull(sitePageExternalReferenceCode)) {
				throw new BadRequestException(
					"A site page external reference code requires a portlet " +
						"ID");
			}

			return null;
		}

		if (Validator.isBlank(sitePageExternalReferenceCode)) {
			return LayoutLocalServiceUtil.getLayout(
				PortalUtil.getControlPanelPlid(group.getCompanyId()));
		}

		return LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode, group.getGroupId());
	}

	public static long[] getLayoutIds(
		List<Layout> layouts, Map<String, String[]> parameterMap) {

		if (!MapUtil.getBoolean(
				parameterMap,
				PortletDataHandlerUtil.PORTLET_DATA_HANDLER_NAME_LAYOUT_SET) ||
			!MapUtil.getBoolean(
				parameterMap, PortletDataHandlerUtil.CONTROL_NAME_PAGES)) {

			return new long[0];
		}

		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection =
			ParameterMapUtil.getPortletDataHandlerTreeSelection(
				PortletDataHandlerUtil.CONTROL_NAME_PAGES, parameterMap);

		Set<String> layoutExternalReferenceCodes = new HashSet<>(
			TransformUtil.transform(layouts, Layout::getExternalReferenceCode));

		Set<String> excludedItemExternalReferenceCodes =
			_getExternalReferenceCodes(
				portletDataHandlerTreeSelection.
					getExcludedItemExternalReferenceCodes(),
				layoutExternalReferenceCodes);
		Set<String> excludedSubtreeExternalReferenceCodes =
			_getExternalReferenceCodes(
				portletDataHandlerTreeSelection.
					getExcludedSubtreeExternalReferenceCodes(),
				layoutExternalReferenceCodes);
		Set<String> itemExternalReferenceCodes = _getExternalReferenceCodes(
			portletDataHandlerTreeSelection.getItemExternalReferenceCodes(),
			layoutExternalReferenceCodes);
		Set<String> subtreeExternalReferenceCodes = _getExternalReferenceCodes(
			portletDataHandlerTreeSelection.getSubtreeExternalReferenceCodes(),
			layoutExternalReferenceCodes);

		Map<Long, Layout> layoutsMap = new HashMap<>();

		for (Layout layout : layouts) {
			layoutsMap.put(layout.getLayoutId(), layout);
		}

		return ArrayUtil.toLongArray(
			TransformUtil.transform(
				layouts,
				layout -> {
					if (!_isSelected(
							excludedItemExternalReferenceCodes,
							excludedSubtreeExternalReferenceCodes,
							itemExternalReferenceCodes, layout, layoutsMap,
							portletDataHandlerTreeSelection.getSelectAll(),
							subtreeExternalReferenceCodes)) {

						return null;
					}

					return layout.getLayoutId();
				}));
	}

	public static Layout getPortletLayout(
			Group group, String portletId, String sitePageExternalReferenceCode)
		throws PortalException {

		Layout layout = fetchPortletLayout(
			group, portletId, sitePageExternalReferenceCode);

		if ((layout == null) &&
			Validator.isNotNull(sitePageExternalReferenceCode)) {

			throw new BadRequestException(
				"No page exists with external reference code \"" +
					sitePageExternalReferenceCode + "\"");
		}

		return layout;
	}

	public static boolean isPrivateLayout(Map<String, String[]> parameterMap) {
		return Objects.equals(
			MapUtil.getString(
				parameterMap, PortletDataHandlerUtil.CONTROL_NAME_VISIBILITY),
			PortletDataHandlerUtil.CHOICE_NAME_PRIVATE_PAGES);
	}

	private static Set<String> _getExternalReferenceCodes(
		String[] externalReferenceCodes,
		Set<String> layoutExternalReferenceCodes) {

		Set<String> externalReferenceCodesSet = new HashSet<>();

		for (String externalReferenceCode :
				GetterUtil.getStringValues(externalReferenceCodes)) {

			if (!layoutExternalReferenceCodes.contains(externalReferenceCode)) {
				throw new BadRequestException(
					"No page exists with external reference code \"" +
						externalReferenceCode + "\"");
			}

			externalReferenceCodesSet.add(externalReferenceCode);
		}

		return externalReferenceCodesSet;
	}

	private static boolean _isSelected(
		Set<String> excludedItemExternalReferenceCodes,
		Set<String> excludedSubtreeExternalReferenceCodes,
		Set<String> itemExternalReferenceCodes, Layout layout,
		Map<Long, Layout> layoutsMap, boolean selectAll,
		Set<String> subtreeExternalReferenceCodes) {

		if (itemExternalReferenceCodes.contains(
				layout.getExternalReferenceCode())) {

			return true;
		}

		if (excludedItemExternalReferenceCodes.contains(
				layout.getExternalReferenceCode())) {

			return false;
		}

		while (layout != null) {
			if (subtreeExternalReferenceCodes.contains(
					layout.getExternalReferenceCode())) {

				return true;
			}

			if (excludedSubtreeExternalReferenceCodes.contains(
					layout.getExternalReferenceCode())) {

				return false;
			}

			layout = layoutsMap.get(layout.getParentLayoutId());
		}

		return selectAll;
	}

}