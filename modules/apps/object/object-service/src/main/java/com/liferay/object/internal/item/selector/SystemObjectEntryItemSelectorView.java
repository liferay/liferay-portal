/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.item.selector;

import com.liferay.info.item.selector.InfoItemSelectorView;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.object.entry.util.ObjectEntryDTOConverterUtil;
import com.liferay.object.entry.util.ObjectEntryValuesUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Gabriel Albuquerque
 * @author Marcela Cunha
 */
public class SystemObjectEntryItemSelectorView
	implements InfoItemSelectorView,
			   ItemSelectorView<InfoItemItemSelectorCriterion> {

	public SystemObjectEntryItemSelectorView(
		DTOConverterRegistry dtoConverterRegistry, ItemSelector itemSelector,
		ItemSelectorViewDescriptorRenderer<InfoItemItemSelectorCriterion>
			itemSelectorViewDescriptorRenderer,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelatedModelsProviderRegistry objectRelatedModelsProviderRegistry,
		Portal portal,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry,
		UserLocalService userLocalService) {

		_dtoConverterRegistry = dtoConverterRegistry;
		_itemSelector = itemSelector;
		_itemSelectorViewDescriptorRenderer =
			itemSelectorViewDescriptorRenderer;
		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelatedModelsProviderRegistry =
			objectRelatedModelsProviderRegistry;
		_portal = portal;
		_systemObjectDefinitionManagerRegistry =
			systemObjectDefinitionManagerRegistry;
		_userLocalService = userLocalService;
	}

	@Override
	public String getClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public Class<InfoItemItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return InfoItemItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _objectDefinition.getPluralLabel(locale);
	}

	@Override
	public boolean isVisible(
		InfoItemItemSelectorCriterion itemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		if (StringUtil.equals(
				_itemSelector.getItemSelectedEventName(
					themeDisplay.getURLCurrent()),
				StringBundler.concat(
					"_",
					ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
					"_selectInfoItem"))) {

			return false;
		}

		return true;
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse, infoItemItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new SystemObjectItemSelectorViewDescriptor(
				_dtoConverterRegistry, (HttpServletRequest)servletRequest,
				infoItemItemSelectorCriterion, _objectDefinition,
				_objectRelatedModelsProviderRegistry, portletURL,
				_systemObjectDefinitionManagerRegistry, _userLocalService));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SystemObjectEntryItemSelectorView.class);

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new InfoItemItemSelectorReturnType());

	private final DTOConverterRegistry _dtoConverterRegistry;
	private final ItemSelector _itemSelector;
	private final ItemSelectorViewDescriptorRenderer
		<InfoItemItemSelectorCriterion> _itemSelectorViewDescriptorRenderer;
	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;
	private final Portal _portal;
	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;
	private final UserLocalService _userLocalService;

	private class SystemObjectEntryItemDescriptor
		implements ItemSelectorViewDescriptor.ItemDescriptor {

		public SystemObjectEntryItemDescriptor(
			BaseModel<?> baseModel, DTOConverterRegistry dtoConverterRegistry,
			HttpServletRequest httpServletRequest,
			SystemObjectDefinitionManagerRegistry
				systemObjectDefinitionManagerRegistry,
			UserLocalService userLocalService) {

			_baseModel = baseModel;
			_dtoConverterRegistry = dtoConverterRegistry;
			_httpServletRequest = httpServletRequest;
			_systemObjectDefinitionManagerRegistry =
				systemObjectDefinitionManagerRegistry;
			_userLocalService = userLocalService;

			_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);
		}

		@Override
		public String getIcon() {
			return null;
		}

		@Override
		public String getImageURL() {
			return null;
		}

		@Override
		public Date getModifiedDate() {
			Map<String, Object> modelAttributes =
				_baseModel.getModelAttributes();

			return (Date)modelAttributes.get("modifiedDate");
		}

		@Override
		public String getPayload() {
			return JSONUtil.put(
				"className", _objectDefinition.getClassName()
			).put(
				"classNameId",
				_portal.getClassNameId(_objectDefinition.getClassName())
			).put(
				"classPK", _baseModel.getPrimaryKeyObj()
			).put(
				"title",
				StringBundler.concat(
					_objectDefinition.getLabel(_themeDisplay.getLocale()),
					StringPool.SPACE, _getTitleFieldValue())
			).toString();
		}

		@Override
		public String getSubtitle(Locale locale) {
			return String.valueOf(_baseModel.getPrimaryKeyObj());
		}

		@Override
		public String getTitle(Locale locale) {
			return _getTitleFieldValue();
		}

		@Override
		public long getUserId() {
			Map<String, Object> modelAttributes =
				_baseModel.getModelAttributes();

			return (Long)modelAttributes.get("userId");
		}

		@Override
		public String getUserName() {
			Map<String, Object> modelAttributes =
				_baseModel.getModelAttributes();

			return _portal.getUserName(
				(Long)modelAttributes.get("userId"), StringPool.BLANK);
		}

		private String _getTitleFieldValue() {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					_objectDefinition.getObjectDefinitionId());

			if (objectDefinition == null) {
				return StringPool.BLANK;
			}

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				objectDefinition.getTitleObjectFieldId());

			if (objectField == null) {
				return StringPool.BLANK;
			}

			Object titleFieldValue = ObjectEntryValuesUtil.getTitleFieldValue(
				objectField.getBusinessType(), _baseModel.getModelAttributes(),
				objectField, _themeDisplay.getUser(),
				ObjectEntryDTOConverterUtil.toValues(
					_baseModel, _dtoConverterRegistry,
					_objectDefinition.getName(),
					_systemObjectDefinitionManagerRegistry,
					_themeDisplay.getUser()));

			if (titleFieldValue == null) {
				return StringPool.BLANK;
			}

			return titleFieldValue.toString();
		}

		private final BaseModel<?> _baseModel;
		private final DTOConverterRegistry _dtoConverterRegistry;
		private final HttpServletRequest _httpServletRequest;
		private final SystemObjectDefinitionManagerRegistry
			_systemObjectDefinitionManagerRegistry;
		private final ThemeDisplay _themeDisplay;
		private final UserLocalService _userLocalService;

	}

	private class SystemObjectItemSelectorViewDescriptor
		implements ItemSelectorViewDescriptor<BaseModel<?>> {

		public SystemObjectItemSelectorViewDescriptor(
			DTOConverterRegistry dtoConverterRegistry,
			HttpServletRequest httpServletRequest,
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
			ObjectDefinition objectDefinition,
			ObjectRelatedModelsProviderRegistry
				objectRelatedModelsProviderRegistry,
			PortletURL portletURL,
			SystemObjectDefinitionManagerRegistry
				systemObjectDefinitionManagerRegistry,
			UserLocalService userLocalService) {

			_dtoConverterRegistry = dtoConverterRegistry;
			_httpServletRequest = httpServletRequest;
			_infoItemItemSelectorCriterion = infoItemItemSelectorCriterion;
			_objectDefinition = objectDefinition;
			_objectRelatedModelsProviderRegistry =
				objectRelatedModelsProviderRegistry;
			_portletURL = portletURL;
			_systemObjectDefinitionManagerRegistry =
				systemObjectDefinitionManagerRegistry;
			_userLocalService = userLocalService;

			_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
			_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);
		}

		@Override
		public String getDefaultDisplayStyle() {
			return "descriptive";
		}

		@Override
		public ItemDescriptor getItemDescriptor(BaseModel<?> baseModel) {
			return new SystemObjectEntryItemDescriptor(
				baseModel, _dtoConverterRegistry, _httpServletRequest,
				_systemObjectDefinitionManagerRegistry, _userLocalService);
		}

		@Override
		public ItemSelectorReturnType getItemSelectorReturnType() {
			return new InfoItemItemSelectorReturnType();
		}

		@Override
		public SearchContainer<BaseModel<?>> getSearchContainer()
			throws PortalException {

			SearchContainer<BaseModel<?>> searchContainer =
				new SearchContainer<>(
					_portletRequest, null, null, "cur",
					ParamUtil.getInteger(_portletRequest, "cur"),
					ParamUtil.getInteger(_portletRequest, "delta"), _portletURL,
					null, "no-entries-were-found");

			searchContainer.setResultsAndTotal(
				ArrayList::new, searchContainer.getEnd());

			String objectRelationshipType = ParamUtil.getString(
				_portletRequest, "objectRelationshipType");

			if (Validator.isNull(objectRelationshipType)) {
				return searchContainer;
			}

			try {
				ObjectRelatedModelsProvider objectRelatedModelsProvider =
					_objectRelatedModelsProviderRegistry.
						getObjectRelatedModelsProvider(
							_objectDefinition.getClassName(),
							CompanyThreadLocal.getCompanyId(),
							objectRelationshipType);

				List<BaseModel<?>> baseModels =
					objectRelatedModelsProvider.getUnrelatedModels(
						_themeDisplay.getCompanyId(),
						_themeDisplay.getScopeGroupId(), _objectDefinition,
						ParamUtil.getLong(_portletRequest, "objectEntryId"),
						ParamUtil.getLong(
							_portletRequest, "objectRelationshipId"),
						null, searchContainer.getStart(),
						searchContainer.getEnd());

				searchContainer.setResultsAndTotal(
					() -> baseModels,
					objectRelatedModelsProvider.getUnrelatedModelsCount(
						_themeDisplay.getCompanyId(),
						_themeDisplay.getScopeGroupId(), _objectDefinition,
						ParamUtil.getLong(_portletRequest, "objectEntryId"),
						ParamUtil.getLong(
							_portletRequest, "objectRelationshipId"),
						null));
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			return searchContainer;
		}

		@Override
		public boolean isMultipleSelection() {
			return _infoItemItemSelectorCriterion.isMultiSelection();
		}

		private final DTOConverterRegistry _dtoConverterRegistry;
		private final HttpServletRequest _httpServletRequest;
		private final InfoItemItemSelectorCriterion
			_infoItemItemSelectorCriterion;
		private final ObjectDefinition _objectDefinition;
		private final ObjectRelatedModelsProviderRegistry
			_objectRelatedModelsProviderRegistry;
		private final PortletRequest _portletRequest;
		private final PortletURL _portletURL;
		private final SystemObjectDefinitionManagerRegistry
			_systemObjectDefinitionManagerRegistry;
		private final ThemeDisplay _themeDisplay;
		private final UserLocalService _userLocalService;

	}

}