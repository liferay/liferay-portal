/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer;

import com.liferay.headless.admin.site.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.HtmlProperties;
import com.liferay.headless.admin.site.dto.v1_0.Layout;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentLinkUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutStructureUtil;
import com.liferay.layout.converter.AlignConverter;
import com.liferay.layout.converter.ContentDisplayConverter;
import com.liferay.layout.converter.ContentVisibilityConverter;
import com.liferay.layout.converter.FlexWrapConverter;
import com.liferay.layout.converter.HtmlTagConverter;
import com.liferay.layout.converter.JustifyConverter;
import com.liferay.layout.converter.WidthTypeConverter;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * @author Eudaldo Alonso
 */
public class ContainerLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement)
		throws Exception {

		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem =
			(ContainerStyledLayoutStructureItem)
				layoutStructure.addContainerStyledLayoutStructureItem(
					pageElement.getExternalReferenceCode(),
					LayoutStructureUtil.getParentExternalReferenceCode(
						pageElement, layoutStructure),
					pageElement.getPosition());

		ContainerPageElementDefinition containerPageElementDefinition =
			(ContainerPageElementDefinition)
				pageElement.getPageElementDefinition();

		if (containerPageElementDefinition == null) {
			return containerStyledLayoutStructureItem;
		}

		String contentVisibility =
			containerPageElementDefinition.getContentVisibilityAsString();

		if (contentVisibility != null) {
			containerStyledLayoutStructureItem.setContentVisibility(
				ContentVisibilityConverter.convertToInternalValue(
					contentVisibility));
		}
		else {
			containerStyledLayoutStructureItem.setContentVisibility(null);
		}

		containerStyledLayoutStructureItem.setCssClasses(
			_getCssClasses(containerPageElementDefinition.getCssClasses()));
		containerStyledLayoutStructureItem.setCustomCSS(
			containerPageElementDefinition.getCustomCSS());

		HtmlProperties htmlProperties =
			containerPageElementDefinition.getHtmlProperties();

		if (htmlProperties != null) {
			containerStyledLayoutStructureItem.setHtmlTag(
				HtmlTagConverter.convertToInternalValue(
					htmlProperties.getHtmlTagAsString()));
		}
		else {
			containerStyledLayoutStructureItem.setHtmlTag(null);
		}

		containerStyledLayoutStructureItem.setIndexed(
			GetterUtil.getBoolean(containerPageElementDefinition.getIndexed()));

		Layout layout = containerPageElementDefinition.getLayout();

		if (layout != null) {
			String align = layout.getAlignAsString();

			if (align != null) {
				containerStyledLayoutStructureItem.setAlign(
					AlignConverter.convertToInternalValue(align));
			}

			String contentDisplay = layout.getContentDisplayAsString();

			if (contentDisplay != null) {
				containerStyledLayoutStructureItem.setContentDisplay(
					ContentDisplayConverter.convertToInternalValue(
						contentDisplay));
			}

			String flexWrap = layout.getFlexWrapAsString();

			if (flexWrap != null) {
				containerStyledLayoutStructureItem.setFlexWrap(
					FlexWrapConverter.convertToInternalValue(flexWrap));
			}

			String justify = layout.getJustifyAsString();

			if (justify != null) {
				containerStyledLayoutStructureItem.setJustify(
					JustifyConverter.convertToInternalValue(justify));
			}

			String widthType = layout.getWidthTypeAsString();

			if (widthType != null) {
				containerStyledLayoutStructureItem.setWidthType(
					WidthTypeConverter.convertToInternalValue(widthType));
			}
		}
		else {
			containerStyledLayoutStructureItem.setAlign(null);
			containerStyledLayoutStructureItem.setContentDisplay(null);
			containerStyledLayoutStructureItem.setFlexWrap(null);
			containerStyledLayoutStructureItem.setJustify(null);
			containerStyledLayoutStructureItem.setWidthType(null);
		}

		containerStyledLayoutStructureItem.setName(
			containerPageElementDefinition.getName());

		JSONObject fragmentViewportsJSONObject =
			FragmentViewportUtil.toFragmentViewportsJSONObject(
				containerPageElementDefinition.getFragmentViewports());

		if (fragmentViewportsJSONObject != null) {
			containerStyledLayoutStructureItem.updateItemConfig(
				fragmentViewportsJSONObject);
		}

		JSONObject fragmentLinkJSONObject = FragmentLinkUtil.toJSONObject(
			containerPageElementDefinition.getFragmentLink(),
			layoutStructureItemImporterContext.getInfoItemServiceRegistry(),
			layoutStructureItemImporterContext.getGroupId());

		if (fragmentLinkJSONObject != null) {
			containerStyledLayoutStructureItem.updateItemConfig(
				fragmentLinkJSONObject);
		}
		else {
			containerStyledLayoutStructureItem.updateItemConfig(
				JSONUtil.put("link", JSONFactoryUtil.createJSONObject()));
		}

		return containerStyledLayoutStructureItem;
	}

	private LinkedHashSet<String> _getCssClasses(String[] cssClasses) {
		if (cssClasses == null) {
			return null;
		}

		return new LinkedHashSet<>(Arrays.asList(cssClasses));
	}

}