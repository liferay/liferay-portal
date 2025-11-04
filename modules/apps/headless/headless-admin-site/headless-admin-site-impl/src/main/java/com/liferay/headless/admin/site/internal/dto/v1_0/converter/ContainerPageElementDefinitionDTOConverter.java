/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.HtmlProperties;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ContainerLayoutUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentLinkUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.converter.ContentVisibilityConverter;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Map;
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

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		ContainerPageElementDefinition containerPageElementDefinition =
			new ContainerPageElementDefinition();

		containerPageElementDefinition.setContentVisibility(
			() -> {
				String contentVisibility =
					containerStyledLayoutStructureItem.getContentVisibility();

				if (Validator.isNull(contentVisibility)) {
					return null;
				}

				return ContainerPageElementDefinition.ContentVisibility.create(
					ContentVisibilityConverter.convertToExternalValue(
						contentVisibility));
			});
		containerPageElementDefinition.setCssClasses(
			() -> {
				Set<String> cssClasses =
					containerStyledLayoutStructureItem.getCssClasses();

				if (SetUtil.isEmpty(cssClasses)) {
					return null;
				}

				return ArrayUtil.toStringArray(cssClasses);
			});
		containerPageElementDefinition.setCustomCSS(
			() -> {
				String customCSS =
					containerStyledLayoutStructureItem.getCustomCSS();

				if (Validator.isNotNull(customCSS)) {
					return customCSS;
				}

				return null;
			});
		containerPageElementDefinition.setFragmentLink(
			() -> FragmentLinkUtil.toFragmentLink(
				companyId, _infoItemServiceRegistry,
				containerStyledLayoutStructureItem.getLinkJSONObject(),
				scopeGroupId));
		containerPageElementDefinition.setFragmentViewports(
			() -> FragmentViewportUtil.toFragmentViewports(
				containerStyledLayoutStructureItem.getItemConfigJSONObject()));
		containerPageElementDefinition.setHtmlProperties(
			() -> _toHtmlProperties(containerStyledLayoutStructureItem));
		containerPageElementDefinition.setIndexed(
			containerStyledLayoutStructureItem::isIndexed);
		containerPageElementDefinition.setLayout(
			() -> ContainerLayoutUtil.toLayout(
				containerStyledLayoutStructureItem.getItemConfigJSONObject()));
		containerPageElementDefinition.setName(
			containerStyledLayoutStructureItem::getName);

		return containerPageElementDefinition;
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