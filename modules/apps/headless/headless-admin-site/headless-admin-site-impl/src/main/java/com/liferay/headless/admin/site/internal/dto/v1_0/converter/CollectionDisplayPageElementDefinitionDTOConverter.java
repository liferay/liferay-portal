/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.CollectionDisplayListStyle;
import com.liferay.headless.admin.site.dto.v1_0.CollectionDisplayPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.CollectionDisplayViewport;
import com.liferay.headless.admin.site.dto.v1_0.CollectionDisplayViewportDefinition;
import com.liferay.headless.admin.site.dto.v1_0.CollectionSettings;
import com.liferay.headless.admin.site.dto.v1_0.EmptyCollectionConfig;
import com.liferay.headless.admin.site.dto.v1_0.ListStyle;
import com.liferay.headless.admin.site.dto.v1_0.ListStyleDefinition;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.TemplateListStyle;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CollectionDisplayListStyleUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CollectionUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ViewportIdUtil;
import com.liferay.layout.converter.AlignConverter;
import com.liferay.layout.converter.FlexWrapConverter;
import com.liferay.layout.converter.JustifyConverter;
import com.liferay.layout.converter.VerticalAlignmentConverter;
import com.liferay.layout.util.CollectionPaginationUtil;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.collection.EmptyCollectionOptions;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem"
	},
	service = DTOConverter.class
)
public class CollectionDisplayPageElementDefinitionDTOConverter
	implements DTOConverter
		<CollectionStyledLayoutStructureItem,
		 CollectionDisplayPageElementDefinition> {

	@Override
	public String getContentType() {
		return CollectionDisplayPageElementDefinition.class.getSimpleName();
	}

	@Override
	public CollectionDisplayPageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem)
		throws Exception {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		CollectionDisplayPageElementDefinition
			collectionDisplayPageElementDefinition =
				new CollectionDisplayPageElementDefinition();

		collectionDisplayPageElementDefinition.setCollectionDisplayListStyle(
			() -> _toCollectionDisplayListStyle(
				collectionStyledLayoutStructureItem));
		collectionDisplayPageElementDefinition.setCollectionDisplayViewports(
			() -> _toCollectionDisplayViewports(
				collectionStyledLayoutStructureItem));
		collectionDisplayPageElementDefinition.setCollectionSettings(
			() -> _toCollectionSettings(
				collectionStyledLayoutStructureItem, companyId, scopeGroupId));
		collectionDisplayPageElementDefinition.setDisplayAllItems(
			collectionStyledLayoutStructureItem::isDisplayAllItems);
		collectionDisplayPageElementDefinition.setDisplayAllPages(
			collectionStyledLayoutStructureItem::isDisplayAllPages);
		collectionDisplayPageElementDefinition.setEmptyCollectionConfig(
			() -> _toEmptyCollectionOption(
				collectionStyledLayoutStructureItem));
		collectionDisplayPageElementDefinition.setName(
			collectionStyledLayoutStructureItem::getName);
		collectionDisplayPageElementDefinition.setNumberOfItems(
			collectionStyledLayoutStructureItem::getNumberOfItems);
		collectionDisplayPageElementDefinition.setNumberOfItemsPerPage(
			collectionStyledLayoutStructureItem::getNumberOfItemsPerPage);
		collectionDisplayPageElementDefinition.setNumberOfPages(
			collectionStyledLayoutStructureItem::getNumberOfPages);
		collectionDisplayPageElementDefinition.setPaginationType(
			() -> {
				String paginationType =
					collectionStyledLayoutStructureItem.getPaginationType();

				if (Validator.isNull(paginationType)) {
					return null;
				}

				if (StringUtil.equalsIgnoreCase(
						paginationType,
						CollectionPaginationUtil.PAGINATION_TYPE_REGULAR)) {

					paginationType =
						CollectionPaginationUtil.PAGINATION_TYPE_NUMERIC;
				}

				return _internalToExternalValuesMap.get(paginationType);
			});
		collectionDisplayPageElementDefinition.setType(
			() -> PageElementDefinition.Type.COLLECTION_DISPLAY);

		return collectionDisplayPageElementDefinition;
	}

	private JSONObject _getViewportJSONObject(
		CollectionDisplayViewport.Id collectionDisplayViewportId,
		JSONObject jsonObject) {

		if (Objects.equals(
				collectionDisplayViewportId,
				CollectionDisplayViewport.Id.DESKTOP)) {

			return jsonObject;
		}

		String viewportId = ViewportIdUtil.toInternalValue(
			collectionDisplayViewportId.getValue());

		if (!jsonObject.has(viewportId)) {
			return null;
		}

		return jsonObject.getJSONObject(viewportId);
	}

	private Map<String, Object> _toCollectionConfig(
		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem) {

		JSONObject collectionJSONObject =
			collectionStyledLayoutStructureItem.getCollectionJSONObject();

		if (collectionJSONObject == null) {
			return null;
		}

		JSONObject configJSONObject = collectionJSONObject.getJSONObject(
			"config");

		if (configJSONObject == null) {
			return null;
		}

		return (Map<String, Object>)_jsonFactory.looseDeserialize(
			configJSONObject.toString());
	}

	private CollectionDisplayListStyle _toCollectionDisplayListStyle(
		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem) {

		String listStyle = collectionStyledLayoutStructureItem.getListStyle();

		if (Validator.isNull(listStyle) ||
			Validator.isNotNull(
				CollectionDisplayListStyleUtil.toExternalValue(listStyle))) {

			return _toListStyle(collectionStyledLayoutStructureItem);
		}

		return _toTemplateListStyle(collectionStyledLayoutStructureItem);
	}

	private CollectionDisplayViewport _toCollectionDisplayViewport(
		CollectionDisplayViewport.Id collectionDisplayViewportId,
		JSONObject jsonObject) {

		JSONObject viewportJSONObject = _getViewportJSONObject(
			collectionDisplayViewportId, jsonObject);

		if (JSONUtil.isEmpty(viewportJSONObject)) {
			return null;
		}

		CollectionDisplayViewportDefinition
			collectionDisplayViewportDefinition =
				_toCollectionDisplayViewportDefinition(viewportJSONObject);

		if (collectionDisplayViewportDefinition == null) {
			return null;
		}

		CollectionDisplayViewport collectionDisplayViewport =
			new CollectionDisplayViewport();

		collectionDisplayViewport.setCollectionDisplayViewportDefinition(
			() -> collectionDisplayViewportDefinition);
		collectionDisplayViewport.setId(() -> collectionDisplayViewportId);

		return collectionDisplayViewport;
	}

	private CollectionDisplayViewportDefinition
		_toCollectionDisplayViewportDefinition(
			JSONObject collectionDisplayViewportConfigurationJSONObject) {

		String align =
			collectionDisplayViewportConfigurationJSONObject.getString(
				"align", null);
		String flexWrap =
			collectionDisplayViewportConfigurationJSONObject.getString(
				"flexWrap", null);
		String justify =
			collectionDisplayViewportConfigurationJSONObject.getString(
				"justify", null);
		String numberOfColumns =
			collectionDisplayViewportConfigurationJSONObject.getString(
				"numberOfColumns", null);

		if ((align == null) && (flexWrap == null) && (justify == null) &&
			(numberOfColumns == null) &&
			JSONUtil.isEmpty(
				collectionDisplayViewportConfigurationJSONObject.getJSONObject(
					"styles"))) {

			return null;
		}

		CollectionDisplayViewportDefinition
			collectionDisplayViewportDefinition =
				new CollectionDisplayViewportDefinition();

		collectionDisplayViewportDefinition.setAlign(
			() -> {
				if (Validator.isNull(align)) {
					return null;
				}

				return CollectionDisplayViewportDefinition.Align.create(
					AlignConverter.convertToExternalValue(align));
			});
		collectionDisplayViewportDefinition.setFlexWrap(
			() -> {
				if (Validator.isNull(flexWrap)) {
					return null;
				}

				return CollectionDisplayViewportDefinition.FlexWrap.create(
					FlexWrapConverter.convertToExternalValue(flexWrap));
			});
		collectionDisplayViewportDefinition.setHidden(
			() -> _toHidden(
				collectionDisplayViewportConfigurationJSONObject.getJSONObject(
					"styles")));
		collectionDisplayViewportDefinition.setJustify(
			() -> {
				if (Validator.isNull(justify)) {
					return null;
				}

				return CollectionDisplayViewportDefinition.Justify.create(
					JustifyConverter.convertToExternalValue(justify));
			});
		collectionDisplayViewportDefinition.setNumberOfColumns(
			() -> {
				if (Validator.isNull(numberOfColumns)) {
					return null;
				}

				return collectionDisplayViewportConfigurationJSONObject.getInt(
					"numberOfColumns");
			});

		return collectionDisplayViewportDefinition;
	}

	private CollectionDisplayViewport[] _toCollectionDisplayViewports(
		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem) {

		List<CollectionDisplayViewport> collectionDisplayViewports =
			new ArrayList<>() {
				{
					CollectionDisplayViewport collectionDisplayViewport =
						_toCollectionDisplayViewport(
							CollectionDisplayViewport.Id.DESKTOP,
							collectionStyledLayoutStructureItem.
								getItemConfigJSONObject());

					if (collectionDisplayViewport != null) {
						add(collectionDisplayViewport);
					}

					collectionDisplayViewport = _toCollectionDisplayViewport(
						CollectionDisplayViewport.Id.LANDSCAPE_MOBILE,
						collectionStyledLayoutStructureItem.
							getItemConfigJSONObject());

					if (collectionDisplayViewport != null) {
						add(collectionDisplayViewport);
					}

					collectionDisplayViewport = _toCollectionDisplayViewport(
						CollectionDisplayViewport.Id.PORTRAIT_MOBILE,
						collectionStyledLayoutStructureItem.
							getItemConfigJSONObject());

					if (collectionDisplayViewport != null) {
						add(collectionDisplayViewport);
					}

					collectionDisplayViewport = _toCollectionDisplayViewport(
						CollectionDisplayViewport.Id.TABLET,
						collectionStyledLayoutStructureItem.
							getItemConfigJSONObject());

					if (collectionDisplayViewport != null) {
						add(collectionDisplayViewport);
					}
				}
			};

		return collectionDisplayViewports.toArray(
			new CollectionDisplayViewport[0]);
	}

	private CollectionSettings _toCollectionSettings(
		CollectionStyledLayoutStructureItem collectionStyledLayoutStructureItem,
		long companyId, long scopeGroupId) {

		return new CollectionSettings() {
			{
				setCollectionConfig(
					() -> _toCollectionConfig(
						collectionStyledLayoutStructureItem));
				setCollectionReference(
					() -> CollectionUtil.getCollectionReference(
						companyId,
						collectionStyledLayoutStructureItem.
							getCollectionJSONObject(),
						scopeGroupId));
			}
		};
	}

	private EmptyCollectionConfig _toEmptyCollectionOption(
		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem) {

		EmptyCollectionOptions emptyCollectionOptions =
			collectionStyledLayoutStructureItem.getEmptyCollectionOptions();

		if (emptyCollectionOptions == null) {
			return null;
		}

		return new EmptyCollectionConfig() {
			{
				setDisplayMessage(emptyCollectionOptions::isDisplayMessage);
				setMessage_i18n(emptyCollectionOptions::getMessage);
			}
		};
	}

	private boolean _toHidden(JSONObject stylesJSONObject) {
		if (JSONUtil.isEmpty(stylesJSONObject)) {
			return false;
		}

		String display = stylesJSONObject.getString("display", null);

		if ((display == null) || !StringUtil.equals(display, "none")) {
			return false;
		}

		return true;
	}

	private ListStyle _toListStyle(
		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem) {

		ListStyle listStyle = new ListStyle();

		listStyle.setCollectionDisplayListStyleType(
			CollectionDisplayListStyle.CollectionDisplayListStyleType.
				LIST_STYLE);
		listStyle.setListStyleDefinition(
			() -> _toListStyleDefinition(collectionStyledLayoutStructureItem));
		listStyle.setListStyleType(
			() -> {
				String collectionStyledLayoutStructureItemListStyle =
					StringPool.BLANK;

				if (Validator.isNotNull(
						collectionStyledLayoutStructureItem.getListStyle())) {

					collectionStyledLayoutStructureItemListStyle =
						collectionStyledLayoutStructureItem.getListStyle();
				}

				return ListStyle.ListStyleType.create(
					CollectionDisplayListStyleUtil.toExternalValue(
						collectionStyledLayoutStructureItemListStyle));
			});

		return listStyle;
	}

	private ListStyleDefinition _toListStyleDefinition(
		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem) {

		return new ListStyleDefinition() {
			{
				setGutters(collectionStyledLayoutStructureItem::isGutters);
				setVerticalAlignment(
					() -> {
						String verticalAlignment =
							collectionStyledLayoutStructureItem.
								getVerticalAlignment();

						if (Validator.isNull(verticalAlignment)) {
							return null;
						}

						return ListStyleDefinition.VerticalAlignment.create(
							VerticalAlignmentConverter.convertToExternalValue(
								verticalAlignment));
					});
			}
		};
	}

	private TemplateListStyle _toTemplateListStyle(
		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem) {

		TemplateListStyle templateListStyle = new TemplateListStyle();

		templateListStyle.setCollectionDisplayListStyleType(
			CollectionDisplayListStyle.CollectionDisplayListStyleType.TEMPLATE);
		templateListStyle.setListItemStyleClassName(
			collectionStyledLayoutStructureItem::getListItemStyle);
		templateListStyle.setListStyleClassName(
			collectionStyledLayoutStructureItem::getListStyle);
		templateListStyle.setTemplateKey(
			collectionStyledLayoutStructureItem::getTemplateKey);

		return templateListStyle;
	}

	private static final Map
		<String, CollectionDisplayPageElementDefinition.PaginationType>
			_internalToExternalValuesMap = HashMapBuilder.put(
				CollectionPaginationUtil.PAGINATION_TYPE_NONE,
				CollectionDisplayPageElementDefinition.PaginationType.NONE
			).put(
				CollectionPaginationUtil.PAGINATION_TYPE_NUMERIC,
				CollectionDisplayPageElementDefinition.PaginationType.NUMERIC
			).put(
				CollectionPaginationUtil.PAGINATION_TYPE_SIMPLE,
				CollectionDisplayPageElementDefinition.PaginationType.SIMPLE
			).build();

	@Reference
	private JSONFactory _jsonFactory;

}