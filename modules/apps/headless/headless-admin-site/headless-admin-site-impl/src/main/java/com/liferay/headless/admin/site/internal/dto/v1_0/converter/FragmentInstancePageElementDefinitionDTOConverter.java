/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.fragment.collection.filter.FragmentCollectionFilterRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.renderer.constants.FragmentRendererConstants;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.headless.admin.site.dto.v1_0.BasicFragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.DefaultFragmentReference;
import com.liferay.headless.admin.site.dto.v1_0.FormFragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.FragmentInstance;
import com.liferay.headless.admin.site.dto.v1_0.FragmentItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CollectionFilterConfigurationUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentEditableElementUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ImageValueUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.LocalizedValueUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.WidgetInstanceUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = DTOConverter.class)
public class FragmentInstancePageElementDefinitionDTOConverter
	implements DTOConverter
		<FragmentStyledLayoutStructureItem, PageElementDefinition> {

	@Override
	public String getContentType() {
		return PageElementDefinition.class.getSimpleName();
	}

	@Override
	public PageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem)
		throws Exception {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		long fragmentEntryLinkId =
			fragmentStyledLayoutStructureItem.getFragmentEntryLinkId();

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLinkId);

		if (fragmentEntryLink == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No fragment entry link exists with ID " +
						fragmentEntryLinkId);
			}

			return null;
		}

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		JSONObject freeMarkerJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		if (fragmentEntryLink.isTypeComponent()) {
			return new BasicFragmentInstancePageElementDefinition() {
				{
					setFragmentInstance(
						() -> _getFragmentInstance(
							companyId, dtoConverterContext, fragmentEntryLink,
							fragmentStyledLayoutStructureItem,
							freeMarkerJSONObject, scopeGroupId,
							dtoConverterContext.getUser()));
					setType(Type.BASIC_FRAGMENT);
				}
			};
		}

		return new FormFragmentInstancePageElementDefinition() {
			{
				setFieldKey(
					() -> GetterUtil.getString(
						_getStringValue(freeMarkerJSONObject, "inputFieldId"),
						null));
				setFragmentInstance(
					() -> _getFragmentInstance(
						companyId, dtoConverterContext, fragmentEntryLink,
						fragmentStyledLayoutStructureItem, freeMarkerJSONObject,
						scopeGroupId, dtoConverterContext.getUser()));
				setHelpText_i18n(
					() -> _getI18nMap(freeMarkerJSONObject, "inputHelpText"));
				setLabel_i18n(
					() -> _getI18nMap(freeMarkerJSONObject, "inputLabel"));
				setMarkAsRequired(
					() -> _getBooleanValue(
						freeMarkerJSONObject, "inputRequired"));
				setReadOnlyField(
					() -> _getBooleanValue(
						freeMarkerJSONObject, "inputReadOnly"));
				setShowHelpText(
					() -> _getBooleanValue(
						freeMarkerJSONObject, "inputShowHelpText"));
				setShowLabel(
					() -> _getBooleanValue(
						freeMarkerJSONObject, "inputShowLabel"));
				setType(Type.FORM_FRAGMENT);
			}
		};
	}

	private Boolean _getBooleanValue(JSONObject jsonObject, String key) {
		if ((jsonObject == null) || !jsonObject.has(key)) {
			return null;
		}

		return jsonObject.getBoolean(key);
	}

	private String _getDraftFragmentInstanceExternalReferenceCode(
		FragmentEntryLink fragmentEntryLink) {

		String originalFragmentEntryLinkERC =
			fragmentEntryLink.getOriginalFragmentEntryLinkERC();

		if (Validator.isNull(originalFragmentEntryLinkERC)) {
			return null;
		}

		FragmentEntryLink originalFragmentEntryLink =
			_fragmentEntryLinkLocalService.
				fetchFragmentEntryLinkByExternalReferenceCode(
					originalFragmentEntryLinkERC,
					fragmentEntryLink.getGroupId());

		if (originalFragmentEntryLink == null) {
			return null;
		}

		return originalFragmentEntryLink.getExternalReferenceCode();
	}

	private DTOConverterContext _getDTOConverterContext(
		long companyId, long scopeGroupId) {

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(null, null, null, null, null);

		dtoConverterContext.setAttribute("companyId", companyId);
		dtoConverterContext.setAttribute("scopeGroupId", scopeGroupId);

		return dtoConverterContext;
	}

	private Map<String, FragmentConfigurationFieldValue>
			_getFragmentConfigurationFieldValues(
				FragmentEntryLink fragmentEntryLink,
				JSONObject freeMarkerJSONObject)
		throws Exception {

		if (freeMarkerJSONObject == null) {
			return Collections.emptyMap();
		}

		JSONObject configurationJSONObject =
			fragmentEntryLink.getConfigurationJSONObject();

		if (Objects.equals(
				fragmentEntryLink.getRendererKey(),
				FragmentRendererConstants.
					FRAGMENT_RENDERER_CLASS_NAME_COLLECTION_FILTER)) {

			String filterKey = GetterUtil.getString(
				freeMarkerJSONObject.getString("filterKey"));

			configurationJSONObject =
				CollectionFilterConfigurationUtil.getConfigurationJSONObject(
					_fragmentCollectionFilterRegistry, filterKey);
		}

		if (configurationJSONObject == null) {
			return Collections.emptyMap();
		}

		Map<String, FragmentConfigurationFieldValue> map = new HashMap<>();

		DTOConverterContext dtoConverterContext = _getDTOConverterContext(
			fragmentEntryLink.getCompanyId(), fragmentEntryLink.getGroupId());

		for (FragmentConfigurationField fragmentConfigurationField :
				_fragmentEntryConfigurationParser.
					getFragmentConfigurationFields(configurationJSONObject)) {

			if (!freeMarkerJSONObject.has(
					fragmentConfigurationField.getName())) {

				continue;
			}

			dtoConverterContext.setAttribute(
				"fragmentFragmentConfigurationFieldValue",
				freeMarkerJSONObject.get(fragmentConfigurationField.getName()));

			map.put(
				fragmentConfigurationField.getName(),
				_configurationFieldValueDTOConverter.toDTO(
					dtoConverterContext, fragmentConfigurationField));
		}

		return map;
	}

	private FragmentInstance _getFragmentInstance(
		long companyId, DTOConverterContext dtoConverterContext,
		FragmentEntryLink fragmentEntryLink,
		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem,
		JSONObject freeMarkerJSONObject, long scopeGroupId, User user) {

		return new FragmentInstance() {
			{
				setBackgroundImageValue(
					() -> ImageValueUtil.toBackgroundImageValue(
						companyId, dtoConverterContext,
						_infoItemServiceRegistry,
						fragmentStyledLayoutStructureItem.
							getBackgroundImageJSONObject(),
						scopeGroupId));
				setConfiguration(fragmentEntryLink::getConfiguration);
				setCss(fragmentEntryLink::getCss);
				setCssClasses(
					() -> {
						Set<String> cssClasses =
							fragmentStyledLayoutStructureItem.getCssClasses();

						if (SetUtil.isEmpty(cssClasses)) {
							return null;
						}

						return ArrayUtil.toStringArray(cssClasses);
					});
				setDatePropagated(fragmentEntryLink::getLastPropagationDate);
				setDraftFragmentInstanceExternalReferenceCode(
					() -> _getDraftFragmentInstanceExternalReferenceCode(
						fragmentEntryLink));
				setFragmentConfigurationFieldValues(
					() -> _getFragmentConfigurationFieldValues(
						fragmentEntryLink, freeMarkerJSONObject));
				setFragmentEditableElements(
					() ->
						FragmentEditableElementUtil.getFragmentEditableElements(
							companyId, dtoConverterContext, fragmentEntryLink,
							_fragmentEntryProcessorRegistry,
							_infoItemServiceRegistry, scopeGroupId, user));
				setFragmentInstanceExternalReferenceCode(
					fragmentEntryLink::getExternalReferenceCode);
				setFragmentReference(
					() -> {
						if (Validator.isNull(
								fragmentEntryLink.getFragmentEntryERC()) &&
							Validator.isNull(
								fragmentEntryLink.getRendererKey())) {

							return null;
						}

						if (Validator.isNotNull(
								fragmentEntryLink.getFragmentEntryERC())) {

							return new FragmentItemExternalReference() {
								{
									setExternalReferenceCode(
										fragmentEntryLink::getFragmentEntryERC);
									setFragmentReferenceType(
										() ->
											FragmentReferenceType.
												FRAGMENT_ITEM_EXTERNAL_REFERENCE);
									setScope(
										() -> ItemScopeUtil.getItemScope(
											fragmentEntryLink.getCompanyId(),
											fragmentEntryLink.
												getFragmentEntryScopeERC(),
											fragmentEntryLink.getGroupId()));
								}
							};
						}

						return new DefaultFragmentReference() {
							{
								setDefaultFragmentKey(
									fragmentEntryLink::getRendererKey);
								setFragmentReferenceType(
									() ->
										FragmentReferenceType.
											DEFAULT_FRAGMENT_REFERENCE);
							}
						};
					});
				setFragmentViewports(
					() -> FragmentViewportUtil.toFragmentViewports(
						fragmentStyledLayoutStructureItem.
							getItemConfigJSONObject()));
				setHtml(fragmentEntryLink::getHtml);
				setIndexed(fragmentStyledLayoutStructureItem::isIndexed);
				setJs(fragmentEntryLink::getJs);
				setName(fragmentStyledLayoutStructureItem::getName);
				setNamespace(fragmentEntryLink::getNamespace);
				setUuid(fragmentEntryLink::getUuid);
				setWidgetInstances(
					() -> _getWidgetInstances(fragmentEntryLink));
			}
		};
	}

	private Map<String, String> _getI18nMap(JSONObject jsonObject, String key) {
		if ((jsonObject == null) || !jsonObject.has(key)) {
			return null;
		}

		Map<String, String> i18nMap = null;

		Object value = jsonObject.get(key);

		if (value instanceof JSONObject) {
			JSONObject valueJSONObject = (JSONObject)value;

			i18nMap = LocalizedValueUtil.toLocalizedValues(
				valueJSONObject,
				languageId -> valueJSONObject.getString(languageId));
		}

		if (MapUtil.isEmpty(i18nMap)) {
			return null;
		}

		return i18nMap;
	}

	private String _getStringValue(JSONObject jsonObject, String key) {
		if ((jsonObject == null) || !jsonObject.has(key)) {
			return null;
		}

		return jsonObject.getString(key);
	}

	private WidgetInstance[] _getWidgetInstances(
		FragmentEntryLink fragmentEntryLink) {

		List<String> fragmentEntryLinkPortletIds =
			_portletRegistry.getFragmentEntryLinkPortletIds(fragmentEntryLink);

		if (ListUtil.isEmpty(fragmentEntryLinkPortletIds)) {
			return null;
		}

		List<WidgetInstance> widgetInstances = new ArrayList<>();

		for (String fragmentEntryLinkPortletId : fragmentEntryLinkPortletIds) {
			widgetInstances.add(
				WidgetInstanceUtil.getWidgetInstance(
					PortletIdCodec.decodeInstanceId(fragmentEntryLinkPortletId),
					fragmentEntryLink.getPlid(), fragmentEntryLinkPortletId));
		}

		return widgetInstances.toArray(new WidgetInstance[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentInstancePageElementDefinitionDTOConverter.class);

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.FragmentConfigurationFieldValueDTOConverter)"
	)
	private DTOConverter
		<FragmentConfigurationField, FragmentConfigurationFieldValue>
			_configurationFieldValueDTOConverter;

	@Reference
	private FragmentCollectionFilterRegistry _fragmentCollectionFilterRegistry;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private PortletRegistry _portletRegistry;

}