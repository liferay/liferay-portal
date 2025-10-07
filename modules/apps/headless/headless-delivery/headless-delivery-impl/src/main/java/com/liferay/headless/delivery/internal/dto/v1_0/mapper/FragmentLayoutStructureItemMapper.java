/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.mapper;

import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.headless.delivery.dto.v1_0.FragmentStyle;
import com.liferay.headless.delivery.dto.v1_0.FragmentViewport;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.headless.delivery.dto.v1_0.PageWidgetInstanceDefinition;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.util.StyledLayoutStructureItemUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.exporter.PortletPermissionsExporter;
import com.liferay.layout.exporter.PortletPreferencesPortletConfigurationExporter;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Jürgen Kappler
 */
public class FragmentLayoutStructureItemMapper
	extends BaseStyledLayoutStructureItemMapper {

	public FragmentLayoutStructureItemMapper(
		FragmentCollectionContributorRegistry
			fragmentCollectionContributorRegistry,
		FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		FragmentEntryLocalService fragmentEntryLocalService,
		GroupLocalService groupLocalService,
		InfoItemServiceRegistry infoItemServiceRegistry,
		JSONFactory jsonFactory, LayoutLocalService layoutLocalService,
		Portal portal, PortletLocalService portletLocalService,
		PortletPermissionsExporter portletPermissionsExporter,
		PortletPreferencesPortletConfigurationExporter
			portletPreferencesPortletConfigurationExporter,
		PortletRegistry portletRegistry) {

		super(infoItemServiceRegistry, portal);

		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_jsonFactory = jsonFactory;

		_widgetInstanceMapper = new WidgetInstanceMapper(
			layoutLocalService, portletLocalService, portletPermissionsExporter,
			portletPreferencesPortletConfigurationExporter);

		_pageFragmentInstanceDefinitionMapper =
			new PageFragmentInstanceDefinitionMapper(
				fragmentCollectionContributorRegistry,
				fragmentEntryConfigurationParser,
				_fragmentEntryLinkLocalService, fragmentEntryLocalService,
				groupLocalService, infoItemServiceRegistry, _jsonFactory,
				portal, portletRegistry, _widgetInstanceMapper);
	}

	@Override
	public PageElement getPageElement(
		long groupId, LayoutStructureItem layoutStructureItem,
		boolean saveInlineContent, boolean saveMappingConfiguration) {

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)layoutStructureItem;

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		if (fragmentEntryLink == null) {
			return null;
		}

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		if (editableValuesJSONObject == null) {
			return null;
		}

		String portletId = editableValuesJSONObject.getString("portletId");

		if (Validator.isNull(portletId)) {
			return new PageElement() {
				{
					setDefinition(
						() -> {
							JSONObject itemConfigJSONObject =
								fragmentStyledLayoutStructureItem.
									getItemConfigJSONObject();

							return _pageFragmentInstanceDefinitionMapper.
								getPageFragmentInstanceDefinition(
									fragmentStyledLayoutStructureItem,
									toFragmentStyle(
										itemConfigJSONObject.getJSONObject(
											"styles"),
										saveMappingConfiguration),
									getFragmentViewPorts(itemConfigJSONObject),
									saveInlineContent,
									saveMappingConfiguration);
						});
					setId(layoutStructureItem::getItemId);
					setType(() -> Type.FRAGMENT);
				}
			};
		}

		String instanceId = editableValuesJSONObject.getString("instanceId");

		return new PageElement() {
			{
				setDefinition(
					() -> {
						JSONObject itemConfigJSONObject =
							fragmentStyledLayoutStructureItem.
								getItemConfigJSONObject();

						return _toPageWidgetInstanceDefinition(
							fragmentEntryLink,
							fragmentStyledLayoutStructureItem,
							itemConfigJSONObject.getString("name", null),
							toFragmentStyle(
								itemConfigJSONObject.getJSONObject("styles"),
								saveMappingConfiguration),
							getFragmentViewPorts(
								itemConfigJSONObject.getJSONObject("style")),
							PortletIdCodec.encode(portletId, instanceId));
					});
				setId(layoutStructureItem::getItemId);
				setType(() -> Type.WIDGET);
			}
		};
	}

	private PageWidgetInstanceDefinition _toPageWidgetInstanceDefinition(
		FragmentEntryLink fragmentEntryLink,
		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem,
		String nameValue,
		FragmentStyle pageWidgetInstanceDefinitionFragmentStyle,
		FragmentViewport[] pageWidgetInstanceDefinitionFragmentViewports,
		String portletId) {

		if (Validator.isNull(portletId)) {
			return null;
		}

		return new PageWidgetInstanceDefinition() {
			{
				setCssClasses(
					() -> StyledLayoutStructureItemUtil.getCssClasses(
						fragmentStyledLayoutStructureItem));
				setCustomCSS(
					() -> StyledLayoutStructureItemUtil.getCustomCSS(
						fragmentStyledLayoutStructureItem));
				setCustomCSSViewports(
					() -> StyledLayoutStructureItemUtil.getCustomCSSViewports(
						fragmentStyledLayoutStructureItem));
				setFragmentStyle(
					() -> pageWidgetInstanceDefinitionFragmentStyle);
				setFragmentViewports(
					() -> pageWidgetInstanceDefinitionFragmentViewports);
				setName(() -> nameValue);
				setWidgetInstance(
					() -> _widgetInstanceMapper.getWidgetInstance(
						fragmentEntryLink, portletId));
			}
		};
	}

	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final JSONFactory _jsonFactory;
	private final PageFragmentInstanceDefinitionMapper
		_pageFragmentInstanceDefinitionMapper;
	private final WidgetInstanceMapper _widgetInstanceMapper;

}