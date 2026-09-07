/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.admin.site.dto.v1_0.CollectionDisplayPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.CollectionItemPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.DropZonePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FormContainerPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FormRelationshipPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FormStepContainerPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FormStepPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentDropZonePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.GridPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.ModulePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.WidgetInstancePageElementDefinition;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.InfoFormUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FormRelationshipStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStepContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = DTOConverter.class)
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

		PageElementDefinition pageElementDefinition = _getPageElementDefinition(
			dtoConverterContext, layoutStructureItem);

		if (pageElementDefinition == null) {
			return null;
		}

		PageElement pageElement = new PageElement();

		pageElement.setExternalReferenceCode(layoutStructureItem::getItemId);
		pageElement.setPageElementDefinition(() -> pageElementDefinition);
		pageElement.setPageElements(
			() -> _getPageElements(
				dtoConverterContext, layoutStructure, layoutStructureItem));
		pageElement.setParentExternalReferenceCode(
			() -> {
				if (Objects.equals(
						layoutStructure.getMainItemId(),
						layoutStructureItem.getParentItemId())) {

					return StringPool.BLANK;
				}

				return layoutStructureItem.getParentItemId();
			});
		pageElement.setPosition(
			() -> {
				LayoutStructureItem parentLayoutStructureItem =
					layoutStructure.getLayoutStructureItem(
						layoutStructureItem.getParentItemId());

				List<String> childrenItemIds =
					parentLayoutStructureItem.getChildrenItemIds();

				return childrenItemIds.indexOf(layoutStructureItem.getItemId());
			});

		return pageElement;
	}

	private PageElementDefinition _getPageElementDefinition(
			DTOConverterContext dtoConverterContext,
			LayoutStructureItem layoutStructureItem)
		throws Exception {

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_COLLECTION)) {

			return _collectionDisplayPageElementDefinitionDTOConverter.toDTO(
				dtoConverterContext,
				(CollectionStyledLayoutStructureItem)layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_COLLECTION_ITEM)) {

			return new CollectionItemPageElementDefinition() {
				{
					setType(() -> Type.COLLECTION_ITEM);
				}
			};
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_COLUMN)) {

			return _modulePageElementDefinitionDTOConverter.toDTO(
				dtoConverterContext,
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

			return _dropZonePageElementDefinitionDTOConverter.toDTO(
				dtoConverterContext,
				(DropZoneLayoutStructureItem)layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FORM)) {

			return _formContainerPageElementDefinitionDTOConverter.toDTO(
				dtoConverterContext,
				(FormStyledLayoutStructureItem)layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FORM_RELATIONSHIP)) {

			return _formRelationshipPageElementDefinitionDTOConverter.toDTO(
				dtoConverterContext,
				(FormRelationshipStyledLayoutStructureItem)layoutStructureItem);
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
				dtoConverterContext,
				(FormStepContainerStyledLayoutStructureItem)
					layoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FRAGMENT)) {

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			if (_isWidgetInstance(fragmentStyledLayoutStructureItem)) {
				return _widgetInstancePageElementDefinitionDTOConverter.toDTO(
					dtoConverterContext, fragmentStyledLayoutStructureItem);
			}

			return _fragmentInstancePageElementDefinitionDTOConverter.toDTO(
				dtoConverterContext, fragmentStyledLayoutStructureItem);
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FRAGMENT_DROP_ZONE)) {

			return _fragmentDropZonePageElementDefinitionDTOConverter.toDTO(
				dtoConverterContext,
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

			return _gridPageElementDefinitionDTOConverter.toDTO(
				dtoConverterContext,
				(RowStyledLayoutStructureItem)layoutStructureItem);
		}

		throw new UnsupportedOperationException();
	}

	private PageElement[] _getPageElements(
		DTOConverterContext dtoConverterContext,
		LayoutStructure layoutStructure,
		LayoutStructureItem layoutStructureItem) {

		if (!(layoutStructureItem instanceof
				CollectionStyledLayoutStructureItem)) {

			return _getPageElements(
				dtoConverterContext, layoutStructureItem.getChildrenItemIds(),
				layoutStructure);
		}

		return _getPageElements(
			DTOConverterContextUtil.getDTOConverterContext(
				dtoConverterContext.isAcceptAllLanguages(),
				HashMapBuilder.<String, Object>put(
					"collectionInfoForm",
					() -> {
						Long scopeGroupId =
							(Long)dtoConverterContext.getAttribute(
								"scopeGroupId");

						if (scopeGroupId == null) {
							throw new UnsupportedOperationException();
						}

						return InfoFormUtil.getCollectionInfoForm(
							(CollectionStyledLayoutStructureItem)
								layoutStructureItem,
							scopeGroupId);
					}
				).putAll(
					dtoConverterContext.getAttributes()
				).build(),
				dtoConverterContext.getDTOConverterRegistry(),
				dtoConverterContext.getHttpServletRequest(),
				dtoConverterContext.getId(), dtoConverterContext.getLocale(),
				dtoConverterContext.getUriInfo(),
				dtoConverterContext.getUser()),
			layoutStructureItem.getChildrenItemIds(), layoutStructure);
	}

	private PageElement[] _getPageElements(
		DTOConverterContext dtoConverterContext, List<String> itemIds,
		LayoutStructure layoutStructure) {

		return TransformUtil.transformToArray(
			itemIds,
			itemId -> toDTO(
				dtoConverterContext,
				layoutStructure.getLayoutStructureItem(itemId)),
			PageElement.class);
	}

	private boolean _isWidgetInstance(
		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem) {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		if (fragmentEntryLink == null) {
			return false;
		}

		JSONObject jsonObject = fragmentEntryLink.getEditableValuesJSONObject();

		if (JSONUtil.isEmpty(jsonObject) || !jsonObject.has("portletId")) {
			return false;
		}

		return true;
	}

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.CollectionDisplayPageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<CollectionStyledLayoutStructureItem,
		 CollectionDisplayPageElementDefinition>
			_collectionDisplayPageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.ContainerPageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<ContainerStyledLayoutStructureItem, ContainerPageElementDefinition>
			_containerPageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.DropZonePageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<DropZoneLayoutStructureItem, DropZonePageElementDefinition>
			_dropZonePageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.FormContainerPageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<FormStyledLayoutStructureItem, FormContainerPageElementDefinition>
			_formContainerPageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.FormRelationshipPageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<FormRelationshipStyledLayoutStructureItem,
		 FormRelationshipPageElementDefinition>
			_formRelationshipPageElementDefinitionDTOConverter;

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

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.FragmentInstancePageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<FragmentStyledLayoutStructureItem, PageElementDefinition>
			_fragmentInstancePageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.GridPageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<RowStyledLayoutStructureItem, GridPageElementDefinition>
			_gridPageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.ModulePageElementDefinitionDTOConverter)"
	)
	private DTOConverter<ColumnLayoutStructureItem, ModulePageElementDefinition>
		_modulePageElementDefinitionDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.WidgetInstancePageElementDefinitionDTOConverter)"
	)
	private DTOConverter
		<FragmentStyledLayoutStructureItem, WidgetInstancePageElementDefinition>
			_widgetInstancePageElementDefinitionDTOConverter;

}