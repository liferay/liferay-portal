/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.CollectionItemLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.CollectionLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.ColumnLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.ContainerLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.DropZoneLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.FormLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.FormRelationshipLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.FormStepContainerLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.FormStepLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.FragmentDropZoneLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.FragmentLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.LayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.RootLayoutStructureItemMapper;
import com.liferay.headless.delivery.internal.dto.v1_0.mapper.RowLayoutStructureItemMapper;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.exporter.PortletPermissionsExporter;
import com.liferay.layout.exporter.PortletPreferencesPortletConfigurationExporter;
import com.liferay.layout.util.structure.CollectionItemLayoutStructureItem;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FormRelationshipStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStepContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStepLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RootLayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 * @author Javier de Arcos
 */
@Component(
	property = "dto.class.name=com.liferay.layout.util.structure.LayoutStructureItem",
	service = DTOConverter.class
)
public class PageElementDTOConverter
	implements DTOConverter<LayoutStructureItem, PageElement> {

	@Override
	public String getContentType() {
		return PageElement.class.getSimpleName();
	}

	@Override
	public PageElement toDTO(
			DTOConverterContext dtoConverterContext,
			LayoutStructureItem layoutStructureItem)
		throws Exception {

		Object groupIdObject = dtoConverterContext.getAttribute("groupId");

		if (groupIdObject == null) {
			throw new IllegalArgumentException(
				"Group ID is not defined for layout structure item " +
					layoutStructureItem.getItemId());
		}

		LayoutStructure layoutStructure =
			(LayoutStructure)dtoConverterContext.getAttribute(
				"layoutStructure");

		if (layoutStructure == null) {
			throw new IllegalArgumentException(
				"Layout structure is not defined for layout structure item " +
					layoutStructureItem.getItemId());
		}

		long groupId = GetterUtil.getLong(groupIdObject);
		boolean saveInlineContent = GetterUtil.getBoolean(
			dtoConverterContext.getAttribute("saveInlineContent"), true);
		boolean saveMappingConfiguration = GetterUtil.getBoolean(
			dtoConverterContext.getAttribute("saveMappingConfiguration"), true);

		return _toPageElement(
			groupId, layoutStructure, layoutStructureItem, saveInlineContent,
			saveMappingConfiguration);
	}

	@Activate
	protected void activate() {
		_layoutStructureItemMappers.put(
			CollectionItemLayoutStructureItem.class,
			new CollectionItemLayoutStructureItemMapper());
		_layoutStructureItemMappers.put(
			CollectionStyledLayoutStructureItem.class,
			new CollectionLayoutStructureItemMapper(
				_infoItemServiceRegistry, _portal));
		_layoutStructureItemMappers.put(
			ColumnLayoutStructureItem.class,
			new ColumnLayoutStructureItemMapper());
		_layoutStructureItemMappers.put(
			ContainerStyledLayoutStructureItem.class,
			new ContainerLayoutStructureItemMapper(
				_infoItemServiceRegistry, _portal));
		_layoutStructureItemMappers.put(
			DropZoneLayoutStructureItem.class,
			new DropZoneLayoutStructureItemMapper());
		_layoutStructureItemMappers.put(
			FormRelationshipStyledLayoutStructureItem.class,
			new FormRelationshipLayoutStructureItemMapper(
				_infoItemServiceRegistry, _portal));
		_layoutStructureItemMappers.put(
			FormStepContainerStyledLayoutStructureItem.class,
			new FormStepContainerLayoutStructureItemMapper(
				_infoItemServiceRegistry, _portal));
		_layoutStructureItemMappers.put(
			FormStepLayoutStructureItem.class,
			new FormStepLayoutStructureItemMapper());
		_layoutStructureItemMappers.put(
			FormStyledLayoutStructureItem.class,
			new FormLayoutStructureItemMapper(
				_infoItemServiceRegistry, _portal));
		_layoutStructureItemMappers.put(
			FragmentDropZoneLayoutStructureItem.class,
			new FragmentDropZoneLayoutStructureItemMapper());
		_layoutStructureItemMappers.put(
			FragmentStyledLayoutStructureItem.class,
			new FragmentLayoutStructureItemMapper(
				_fragmentCollectionContributorRegistry,
				_fragmentEntryConfigurationParser,
				_fragmentEntryLinkLocalService, _fragmentEntryLocalService,
				_groupLocalService, _infoItemServiceRegistry, _jsonFactory,
				_layoutLocalService, _portal, _portletLocalService,
				_portletPermissionsExporter,
				_portletPreferencesPortletConfigurationExporter,
				_portletRegistry));
		_layoutStructureItemMappers.put(
			RootLayoutStructureItem.class, new RootLayoutStructureItemMapper());
		_layoutStructureItemMappers.put(
			RowStyledLayoutStructureItem.class,
			new RowLayoutStructureItemMapper(
				_infoItemServiceRegistry, _portal));
	}

	private PageElement _toPageElement(
		long groupId, LayoutStructure layoutStructure,
		LayoutStructureItem layoutStructureItem, boolean saveInlineContent,
		boolean saveMappingConfiguration) {

		List<PageElement> pageElements = new ArrayList<>();

		List<String> childrenItemIds = layoutStructureItem.getChildrenItemIds();

		for (String childItemId : childrenItemIds) {
			LayoutStructureItem childLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(childItemId);

			List<String> grandChildrenItemIds =
				childLayoutStructureItem.getChildrenItemIds();

			if (grandChildrenItemIds.isEmpty()) {
				pageElements.add(
					_toPageElement(
						groupId, childLayoutStructureItem, saveInlineContent,
						saveMappingConfiguration));
			}
			else {
				pageElements.add(
					_toPageElement(
						groupId, layoutStructure, childLayoutStructureItem,
						saveInlineContent, saveMappingConfiguration));
			}
		}

		PageElement pageElement = _toPageElement(
			groupId, layoutStructureItem, saveInlineContent,
			saveMappingConfiguration);

		if ((pageElement != null) && !pageElements.isEmpty()) {
			pageElement.setPageElements(
				() -> pageElements.toArray(new PageElement[0]));
		}

		return pageElement;
	}

	private PageElement _toPageElement(
		long groupId, LayoutStructureItem layoutStructureItem,
		boolean saveInlineContent, boolean saveMappingConfiguration) {

		LayoutStructureItemMapper layoutStructureItemMapper =
			_layoutStructureItemMappers.get(layoutStructureItem.getClass());

		if (layoutStructureItemMapper == null) {
			return null;
		}

		return layoutStructureItemMapper.getPageElement(
			groupId, layoutStructureItem, saveInlineContent,
			saveMappingConfiguration);
	}

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	private final Map<Class<?>, LayoutStructureItemMapper>
		_layoutStructureItemMappers = new HashMap<>();

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPermissionsExporter _portletPermissionsExporter;

	@Reference
	private PortletPreferencesPortletConfigurationExporter
		_portletPreferencesPortletConfigurationExporter;

	@Reference
	private PortletRegistry _portletRegistry;

}