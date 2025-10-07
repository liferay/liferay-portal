/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.HtmlProperties;
import com.liferay.headless.admin.site.dto.v1_0.Layout;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentLinkUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.converter.AlignConverter;
import com.liferay.layout.converter.ContentDisplayConverter;
import com.liferay.layout.converter.ContentVisibilityConverter;
import com.liferay.layout.converter.FlexWrapConverter;
import com.liferay.layout.converter.JustifyConverter;
import com.liferay.layout.util.constants.StyledLayoutStructureConstants;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "dto.class.name=com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem",
	service = DTOConverter.class
)
public class ContainerPageElementDefinitionDTOConverter
	implements DTOConverter
		<ContainerStyledLayoutStructureItem, ContainerPageElementDefinition> {

	@Override
	public String getContentType() {
		return ContainerPageElementDefinition.class.getSimpleName();
	}

	@Override
	public ContainerPageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			ContainerStyledLayoutStructureItem
				containerStyledLayoutStructureItem)
		throws Exception {

		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if (scopeGroupId == null) {
			throw new UnsupportedOperationException();
		}

		return new ContainerPageElementDefinition() {
			{
				setContentVisibility(
					() -> {
						String contentVisibility =
							containerStyledLayoutStructureItem.
								getContentVisibility();

						if (Validator.isNull(contentVisibility)) {
							return null;
						}

						return ContentVisibility.create(
							ContentVisibilityConverter.convertToExternalValue(
								contentVisibility));
					});
				setCssClasses(
					() -> {
						Set<String> cssClasses =
							containerStyledLayoutStructureItem.getCssClasses();

						if (SetUtil.isEmpty(cssClasses)) {
							return null;
						}

						return ArrayUtil.toStringArray(cssClasses);
					});
				setCustomCSS(
					() -> {
						String customCSS =
							containerStyledLayoutStructureItem.getCustomCSS();

						if (Validator.isNotNull(customCSS)) {
							return customCSS;
						}

						return null;
					});
				setFragmentLink(
					() -> FragmentLinkUtil.toFragmentLink(
						_infoItemServiceRegistry,
						containerStyledLayoutStructureItem.getLinkJSONObject(),
						scopeGroupId));
				setFragmentViewports(
					() -> FragmentViewportUtil.toFragmentViewports(
						containerStyledLayoutStructureItem.
							getItemConfigJSONObject()));
				setHtmlProperties(
					() -> _toHtmlProperties(
						containerStyledLayoutStructureItem));
				setIndexed(containerStyledLayoutStructureItem::isIndexed);
				setLayout(() -> _toLayout(containerStyledLayoutStructureItem));
				setName(containerStyledLayoutStructureItem::getName);
				setType(PageElementDefinition.Type.CONTAINER);
			}
		};
	}

	private HtmlProperties _toHtmlProperties(
		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem) {

		if (Validator.isNull(containerStyledLayoutStructureItem.getHtmlTag())) {
			return null;
		}

		return new HtmlProperties() {
			{
				setHtmlTag(
					() -> _internalToExternalValuesMap.get(
						containerStyledLayoutStructureItem.getHtmlTag()));
			}
		};
	}

	private Layout _toLayout(
		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem) {

		JSONObject itemConfigJSONObject =
			containerStyledLayoutStructureItem.getItemConfigJSONObject();

		if ((itemConfigJSONObject.getString("align", null) == null) &&
			(itemConfigJSONObject.getString("contentVisibility", null) ==
				null) &&
			(itemConfigJSONObject.getString("flexWrap", null) == null) &&
			(itemConfigJSONObject.getString("justify", null) == null) &&
			(itemConfigJSONObject.getString("widthType", null) == null)) {

			return null;
		}

		return new Layout() {
			{
				setAlign(
					() -> {
						String align = itemConfigJSONObject.getString(
							"align", null);

						if (Validator.isNull(align)) {
							return null;
						}

						return Align.create(
							AlignConverter.convertToExternalValue(align));
					});
				setContentDisplay(
					() -> {
						String contentDisplay = itemConfigJSONObject.getString(
							"contentDisplay", null);

						if (Validator.isNull(contentDisplay)) {
							return null;
						}

						return ContentDisplay.create(
							ContentDisplayConverter.convertToExternalValue(
								GetterUtil.getString(contentDisplay)));
					});
				setFlexWrap(
					() -> {
						String flexWrap = itemConfigJSONObject.getString(
							"flexWrap", null);

						if (Validator.isNull(flexWrap)) {
							return null;
						}

						return FlexWrap.create(
							FlexWrapConverter.convertToExternalValue(flexWrap));
					});
				setJustify(
					() -> {
						String justify = itemConfigJSONObject.getString(
							"justify", null);

						if (Validator.isNull(justify)) {
							return null;
						}

						return Justify.create(
							JustifyConverter.convertToExternalValue(justify));
					});
				setWidthType(
					() -> {
						String widthType = itemConfigJSONObject.getString(
							"widthType", null);

						if (Validator.isNull(widthType) ||
							Objects.equals(
								widthType,
								StyledLayoutStructureConstants.WIDTH_TYPE)) {

							return null;
						}

						return WidthType.create(
							StringUtil.upperCaseFirstLetter(widthType));
					});
			}
		};
	}

	private static final Map<String, HtmlProperties.HtmlTag>
		_internalToExternalValuesMap = HashMapBuilder.put(
			"article", HtmlProperties.HtmlTag.ARTICLE
		).put(
			"aside", HtmlProperties.HtmlTag.ASIDE
		).put(
			"div", HtmlProperties.HtmlTag.DIV
		).put(
			"footer", HtmlProperties.HtmlTag.FOOTER
		).put(
			"header", HtmlProperties.HtmlTag.HEADER
		).put(
			"nav", HtmlProperties.HtmlTag.NAV
		).put(
			"section", HtmlProperties.HtmlTag.SECTION
		).build();

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}