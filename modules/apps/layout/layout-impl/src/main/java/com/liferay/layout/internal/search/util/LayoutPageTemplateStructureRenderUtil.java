/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.search.util;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.CollectionItemLayoutStructureItem;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutPageTemplateStructureRenderUtil {

	public static String renderLayoutContent(
		FragmentRendererController fragmentRendererController,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		LayoutPageTemplateStructure layoutPageTemplateStructure, Locale locale,
		long segmentsExperienceId) {

		if (fragmentRendererController == null) {
			return StringPool.BLANK;
		}

		String data = layoutPageTemplateStructure.getData(segmentsExperienceId);

		if (Validator.isNull(data)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		LayoutStructure layoutStructure = LayoutStructure.of(data);

		Map<Long, LayoutStructureItem> fragmentEntryLinkIdMap =
			layoutStructure.getFragmentLayoutStructureItems();

		for (LayoutStructureItem layoutStructureItem :
				fragmentEntryLinkIdMap.values()) {

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			if (!fragmentStyledLayoutStructureItem.isIndexed() ||
				_hasNonindexableAncestor(
					fragmentStyledLayoutStructureItem.getItemId(),
					layoutStructure)) {

				continue;
			}

			FragmentEntryLink fragmentEntryLink =
				FragmentEntryLinkLocalServiceUtil.fetchFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			if (fragmentEntryLink == null) {
				return StringPool.BLANK;
			}

			DefaultFragmentRendererContext fragmentRendererContext =
				new DefaultFragmentRendererContext(fragmentEntryLink);

			fragmentRendererContext.setLocale(locale);
			fragmentRendererContext.setMode(FragmentEntryLinkConstants.INDEX);

			sb.append(
				fragmentRendererController.render(
					fragmentRendererContext, httpServletRequest,
					httpServletResponse));
		}

		return sb.toString();
	}

	private static boolean _hasNonindexableAncestor(
		String itemId, LayoutStructure layoutStructure) {

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		LayoutStructureItem parentLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem.getParentItemId());

		if (parentLayoutStructureItem == null) {
			return false;
		}

		if (layoutStructureItem instanceof CollectionItemLayoutStructureItem ||
			layoutStructureItem instanceof
				CollectionStyledLayoutStructureItem) {

			return true;
		}

		if (layoutStructureItem instanceof ContainerStyledLayoutStructureItem) {
			ContainerStyledLayoutStructureItem
				containerStyledLayoutStructureItem =
					(ContainerStyledLayoutStructureItem)layoutStructureItem;

			if (!containerStyledLayoutStructureItem.isIndexed()) {
				return true;
			}
		}
		else if (layoutStructureItem instanceof FormStyledLayoutStructureItem) {
			FormStyledLayoutStructureItem formStyledLayoutStructureItem =
				(FormStyledLayoutStructureItem)layoutStructureItem;

			if (!formStyledLayoutStructureItem.isIndexed()) {
				return true;
			}
		}

		if (Objects.equals(
				parentLayoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_ROOT)) {

			return false;
		}

		return _hasNonindexableAncestor(
			parentLayoutStructureItem.getItemId(), layoutStructure);
	}

}