/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.CollectionItemPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.CollectionPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.ColumnPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.DropZonePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FormPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FormStepContainerPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FormStepPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentDropZonePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.RowPageElementDefinition;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStepContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
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

		if (dtoConverterContext == null) {
			throw new UnsupportedOperationException();
		}

		LayoutStructure layoutStructure =
			(LayoutStructure)dtoConverterContext.getAttribute(
				LayoutStructure.class.getName());

		if (layoutStructure == null) {
			throw new UnsupportedOperationException();
		}

		return new PageElement() {
			{
				setExternalReferenceCode(layoutStructureItem::getItemId);
				setPageElementDefinition(
					() -> _getPageElementDefinition(
						dtoConverterContext, layoutStructureItem));
				setPageElements(
					() -> _getPageElements(
						dtoConverterContext, layoutStructure,
						layoutStructureItem));
				setParentExternalReferenceCode(
					() -> {
						if (Objects.equals(
								layoutStructure.getMainItemId(),
								layoutStructureItem.getParentItemId())) {

							return StringPool.BLANK;
						}

						return layoutStructureItem.getParentItemId();
					});
				setPosition(
					() -> {
						LayoutStructureItem parentLayoutStructureItem =
							layoutStructure.getLayoutStructureItem(
								layoutStructureItem.getParentItemId());

						List<String> childrenItemIds =
							parentLayoutStructureItem.getChildrenItemIds();

						return childrenItemIds.indexOf(
							layoutStructureItem.getItemId());
					});
			}
		};
	}

	private PageElementDefinition _getPageElementDefinition(
			DTOConverterContext dtoConverterContext,
			LayoutStructureItem layoutStructureItem)
		throws Exception {

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_COLLECTION)) {

			return _collectionPageElementDefinitionDTOConverter.toDTO(
				(CollectionStyledLayoutStructureItem)layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_COLLECTION_ITEM)) {

			CollectionItemPageElementDefinition
				collectionItemPageElementDefinition =
					new CollectionItemPageElementDefinition();

			collectionItemPageElementDefinition.setType(
				PageElementDefinition.Type.COLLECTION_ITEM);

			return collectionItemPageElementDefinition;
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_COLUMN)) {

			return _columnPageElementDefinitionDTOConverter.toDTO(
				(ColumnLayoutStructureItem)layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_CONTAINER)) {

			return _containerPageElementDefinitionDTOConverter.toDTO(
				dtoConverterContext,
				(ContainerStyledLayoutStructureItem)layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_DROP_ZONE)) {

			DropZonePageElementDefinition dropZonePageElementDefinition =
				new DropZonePageElementDefinition();

			dropZonePageElementDefinition.setType(
				PageElementDefinition.Type.DROP_ZONE);

			return dropZonePageElementDefinition;
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FORM)) {

			return _formPageElementDefinitionDTOConverter.toDTO(
				(FormStyledLayoutStructureItem)layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FORM_STEP)) {

			FormStepPageElementDefinition formStepPageElementDefinition =
				new FormStepPageElementDefinition();

			formStepPageElementDefinition.setType(
				PageElementDefinition.Type.FORM_STEP);

			return formStepPageElementDefinition;
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FORM_STEP_CONTAINER)) {

			return _formStepContainerPageElementDefinitionDTOConverter.toDTO(
				(FormStepContainerStyledLayoutStructureItem)
					layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FRAGMENT)) {

			return _fragmentInstancePageElementDefinitionDTOConverter.toDTO(
				(FragmentStyledLayoutStructureItem)layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FRAGMENT_DROP_ZONE)) {

			return _fragmentDropZonePageElementDefinitionDTOConverter.toDTO(
				(FragmentDropZoneLayoutStructureItem)layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_ROOT)) {

			throw new UnsupportedOperationException();
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_ROW)) {

			return _rowPageElementDefinitionDTOConverter.toDTO(
				(RowStyledLayoutStructureItem)layoutStructureItem);
		}

		throw new UnsupportedOperationException();
	}

	private PageElement[] _getPageElements(
		DTOConverterContext dtoConverterContext,
		LayoutStructure layoutStructure,
		LayoutStructureItem layoutStructureItem) {

		return TransformUtil.transformToArray(
			layoutStructureItem.getChildrenItemIds(),
			childrenItemId -> toDTO(
				dtoConverterContext,
				layoutStructure.getLayoutStructureItem(childrenItemId)),
			PageElement.class);
	}

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.CollectionPageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<CollectionStyledLayoutStructureItem, CollectionPageElementDefinition>
			_collectionPageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.ColumnPageElementDefinitionDTOConverter)"
	)
	private DTOConverter<ColumnLayoutStructureItem, ColumnPageElementDefinition>
		_columnPageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.ContainerPageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<ContainerStyledLayoutStructureItem, ContainerPageElementDefinition>
			_containerPageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.FormPageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<FormStyledLayoutStructureItem, FormPageElementDefinition>
			_formPageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.FormStepContainerPageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<FormStepContainerStyledLayoutStructureItem,
		 FormStepContainerPageElementDefinition>
			_formStepContainerPageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.FragmentDropZonePageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<FragmentDropZoneLayoutStructureItem,
		 FragmentDropZonePageElementDefinition>
			_fragmentDropZonePageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.FragmentInstancePageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<FragmentStyledLayoutStructureItem,
		 FragmentInstancePageElementDefinition>
			_fragmentInstancePageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.RowPageElementDefinitionDTOConverter)"
	)
	private DTOConverter<RowStyledLayoutStructureItem, RowPageElementDefinition>
		_rowPageElementDefinitionDTOConverter;

}