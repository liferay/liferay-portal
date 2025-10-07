/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.util.configuration;

import com.liferay.fragment.constants.FragmentConfigurationFieldDataType;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.fragment.util.configuration.FragmentEntryMenuDisplayConfiguration;
import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.FrontendTokenMapping;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.layout.list.retriever.DefaultLayoutListRetrieverContext;
import com.liferay.layout.list.retriever.LayoutListRetriever;
import com.liferay.layout.list.retriever.LayoutListRetrieverRegistry;
import com.liferay.layout.list.retriever.ListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReferenceFactory;
import com.liferay.layout.list.retriever.ListObjectReferenceFactoryRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.navigation.taglib.servlet.taglib.util.NavItemUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = FragmentEntryConfigurationParser.class)
public class FragmentEntryConfigurationParserImpl
	implements FragmentEntryConfigurationParser {

	@Override
	public JSONObject getConfigurationDefaultValuesJSONObject(
		JSONObject configurationJSONObject) {

		List<FragmentConfigurationField> fragmentConfigurationFields =
			getFragmentConfigurationFields(configurationJSONObject);

		JSONObject defaultValuesJSONObject = _jsonFactory.createJSONObject();

		for (FragmentConfigurationField fragmentConfigurationField :
				fragmentConfigurationFields) {

			defaultValuesJSONObject.put(
				fragmentConfigurationField.getName(),
				_getFieldValue(
					fragmentConfigurationField,
					LocaleUtil.getMostRelevantLocale(), null));
		}

		return defaultValuesJSONObject;
	}

	@Override
	public Object getConfigurationFieldValue(
		JSONObject editableValuesJSONObject, String fieldName,
		FragmentConfigurationFieldDataType fragmentConfigurationFieldDataType) {

		if (editableValuesJSONObject == null) {
			return null;
		}

		JSONObject configurationValuesJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		if (configurationValuesJSONObject == null) {
			return null;
		}

		return _getFieldValue(
			fragmentConfigurationFieldDataType,
			configurationValuesJSONObject.getString(fieldName));
	}

	@Override
	public JSONObject getConfigurationJSONObject(
			JSONObject configurationJSONObject,
			JSONObject editableValuesJSONObject, Locale locale)
		throws JSONException {

		JSONObject configurationDefaultValuesJSONObject =
			getConfigurationDefaultValuesJSONObject(configurationJSONObject);

		if (configurationDefaultValuesJSONObject == null) {
			return _jsonFactory.createJSONObject();
		}

		JSONObject configurationValuesJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		if (configurationValuesJSONObject == null) {
			return configurationDefaultValuesJSONObject;
		}

		List<FragmentConfigurationField> fragmentConfigurationFields =
			getFragmentConfigurationFields(configurationJSONObject);

		for (FragmentConfigurationField fragmentConfigurationField :
				fragmentConfigurationFields) {

			String name = fragmentConfigurationField.getName();

			Object object = configurationValuesJSONObject.get(name);

			if (object == null) {
				continue;
			}

			configurationDefaultValuesJSONObject.put(
				name,
				_getFieldValue(
					fragmentConfigurationField, locale,
					configurationValuesJSONObject.getString(name)));
		}

		return configurationDefaultValuesJSONObject;
	}

	@Override
	public Map<String, Object> getContextObjects(
		JSONObject configurationValuesJSONObject,
		JSONObject configurationJSONObject, Object displayObject,
		long[] segmentsEntryIds) {

		HashMap<String, Object> contextObjects = new HashMap<>();

		List<FragmentConfigurationField> fragmentConfigurationFields =
			getFragmentConfigurationFields(configurationJSONObject);

		for (FragmentConfigurationField fragmentConfigurationField :
				fragmentConfigurationFields) {

			String name = fragmentConfigurationField.getName();

			if (StringUtil.equalsIgnoreCase(
					fragmentConfigurationField.getType(), "itemSelector")) {

				Object contextObject = displayObject;

				if (displayObject == null) {
					contextObject = _getInfoDisplayObjectEntry(
						configurationValuesJSONObject.getString(name));
				}

				if (contextObject != null) {
					contextObjects.put(
						name + _CONTEXT_OBJECT_SUFFIX, contextObject);
				}

				continue;
			}

			if (StringUtil.equalsIgnoreCase(
					fragmentConfigurationField.getType(),
					"collectionSelector")) {

				Object contextListObject = _getInfoListObjectEntry(
					configurationValuesJSONObject.getString(name),
					segmentsEntryIds,
					fragmentConfigurationField.getTypeOptionsJSONObject());

				if (contextListObject != null) {
					contextObjects.put(
						name + _CONTEXT_OBJECT_LIST_SUFFIX, contextListObject);
				}
			}

			if (StringUtil.equalsIgnoreCase(
					fragmentConfigurationField.getType(),
					"navigationMenuSelector")) {

				Object contextObject = _getNavItemsContextObject(
					configurationValuesJSONObject.getString(name));

				if (contextObject != null) {
					contextObjects.put(
						name + _CONTEXT_OBJECT_SUFFIX, contextObject);
				}
			}
		}

		return contextObjects;
	}

	@Override
	public Object getFieldValue(
		JSONObject editableValuesJSONObject,
		FragmentConfigurationField fragmentConfigurationField, Locale locale) {

		if (editableValuesJSONObject == null) {
			return fragmentConfigurationField.getDefaultValue();
		}

		JSONObject configurationValuesJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		if (configurationValuesJSONObject == null) {
			return fragmentConfigurationField.getDefaultValue();
		}

		return _getFieldValue(
			fragmentConfigurationField, locale,
			configurationValuesJSONObject.getString(
				fragmentConfigurationField.getName(), null));
	}

	@Override
	public Object getFieldValue(
		JSONObject configurationJSONObject, JSONObject editableValuesJSONObject,
		Locale locale, String name) {

		if (editableValuesJSONObject == null) {
			return null;
		}

		JSONObject configurationValuesJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		if (configurationValuesJSONObject == null) {
			return null;
		}

		List<FragmentConfigurationField> fragmentConfigurationFields =
			getFragmentConfigurationFields(configurationJSONObject);

		for (FragmentConfigurationField fragmentConfigurationField :
				fragmentConfigurationFields) {

			if (!Objects.equals(fragmentConfigurationField.getName(), name)) {
				continue;
			}

			return _getFieldValue(
				fragmentConfigurationField, locale,
				configurationValuesJSONObject.getString(name));
		}

		return null;
	}

	@Override
	public List<FragmentConfigurationField> getFragmentConfigurationFields(
		JSONObject configurationJSONObject) {

		JSONArray fieldSetsJSONArray = _getFieldSetsJSONArray(
			configurationJSONObject);

		if (fieldSetsJSONArray == null) {
			return Collections.emptyList();
		}

		List<FragmentConfigurationField> fragmentConfigurationFields =
			new ArrayList<>();

		Iterator<JSONObject> iterator1 = fieldSetsJSONArray.iterator();

		iterator1.forEachRemaining(
			fieldSetJSONObject -> {
				JSONArray fieldSetFieldsJSONArray =
					fieldSetJSONObject.getJSONArray("fields");

				Iterator<JSONObject> iterator2 =
					fieldSetFieldsJSONArray.iterator();

				iterator2.forEachRemaining(
					fieldSetFieldsJSONObject -> fragmentConfigurationFields.add(
						new FragmentConfigurationField(
							fieldSetFieldsJSONObject)));
			});

		return fragmentConfigurationFields;
	}

	@Override
	public JSONObject translateConfiguration(
		JSONObject jsonObject, ResourceBundle resourceBundle) {

		if (jsonObject == null) {
			return null;
		}

		JSONArray fieldSetsJSONArray = jsonObject.getJSONArray("fieldSets");

		if (fieldSetsJSONArray == null) {
			return null;
		}

		Iterator<JSONObject> iterator = fieldSetsJSONArray.iterator();

		iterator.forEachRemaining(
			fieldSetJSONObject -> {
				String fieldSetLabel = fieldSetJSONObject.getString("label");

				fieldSetJSONObject.put(
					"label",
					_language.get(
						resourceBundle, fieldSetLabel, fieldSetLabel));

				JSONArray fieldsJSONArray = fieldSetJSONObject.getJSONArray(
					"fields");

				Iterator<JSONObject> fieldsIterator =
					fieldsJSONArray.iterator();

				fieldsIterator.forEachRemaining(
					fieldJSONObject -> _translateConfigurationField(
						fieldJSONObject, resourceBundle));
			});

		return jsonObject;
	}

	private String _getColorPickerCssVariable(String fieldValue) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if ((serviceContext == null) || Validator.isNull(fieldValue)) {
			return fieldValue;
		}

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (themeDisplay == null) {
			return fieldValue;
		}

		FrontendTokenDefinition frontendTokenDefinition = null;

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-30204")) {

			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					themeDisplay.getLayout());
		}
		else {
			Group group = themeDisplay.getScopeGroup();

			frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					_layoutSetLocalService.fetchLayoutSet(
						themeDisplay.getSiteGroupId(),
						group.isLayoutSetPrototype()));
		}

		if (frontendTokenDefinition == null) {
			return fieldValue;
		}

		Collection<FrontendToken> frontendTokens =
			frontendTokenDefinition.getFrontendTokens();

		for (FrontendToken frontendToken : frontendTokens) {
			JSONObject jsonObject = frontendToken.getJSONObject(
				LocaleUtil.getMostRelevantLocale());

			if (!Objects.equals(jsonObject.getString("name"), fieldValue)) {
				continue;
			}

			List<FrontendTokenMapping> frontendTokenMappings = new ArrayList<>(
				frontendToken.getFrontendTokenMappings(
					FrontendTokenMapping.TYPE_CSS_VARIABLE));

			if (frontendTokenMappings.isEmpty()) {
				return fieldValue;
			}

			FrontendTokenMapping frontendTokenMapping =
				frontendTokenMappings.get(0);

			return "var(--" + frontendTokenMapping.getValue() + ")";
		}

		return fieldValue;
	}

	private JSONArray _getFieldSetsJSONArray(
		JSONObject configurationJSONObject) {

		if (configurationJSONObject == null) {
			return null;
		}

		return configurationJSONObject.getJSONArray("fieldSets");
	}

	private Object _getFieldValue(
		FragmentConfigurationField fragmentConfigurationField, Locale locale,
		String value) {

		String parsedValue = GetterUtil.getString(value);

		if (fragmentConfigurationField.isLocalizable() &&
			JSONUtil.isJSONObject(parsedValue)) {

			try {
				JSONObject valueJSONObject = _jsonFactory.createJSONObject(
					parsedValue);

				parsedValue = valueJSONObject.getString(
					LocaleUtil.toLanguageId(locale),
					valueJSONObject.getString(
						LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
						fragmentConfigurationField.getDefaultValue()));
			}
			catch (JSONException jsonException) {
				_log.error(
					"Unable to parse configuration value JSON", jsonException);
			}
		}
		else if (value == null) {
			parsedValue = fragmentConfigurationField.getDefaultValue();
		}

		if (StringUtil.equalsIgnoreCase(
				fragmentConfigurationField.getType(), "checkbox")) {

			return _getFieldValue(
				FragmentConfigurationFieldDataType.BOOLEAN, parsedValue);
		}
		else if (StringUtil.equalsIgnoreCase(
					fragmentConfigurationField.getType(),
					"collectionSelector")) {

			return _getInfoListObjectEntryJSONObject(parsedValue);
		}
		else if (StringUtil.equalsIgnoreCase(
					fragmentConfigurationField.getType(), "colorPalette")) {

			JSONObject jsonObject = (JSONObject)_getFieldValue(
				FragmentConfigurationFieldDataType.OBJECT, parsedValue);

			if ((jsonObject != null) && jsonObject.isNull("color") &&
				!jsonObject.isNull("cssClass")) {

				jsonObject.put("color", jsonObject.getString("cssClass"));
			}

			return jsonObject;
		}
		else if (StringUtil.equalsIgnoreCase(
					fragmentConfigurationField.getType(), "colorPicker")) {

			String fieldValue = (String)_getFieldValue(
				FragmentConfigurationFieldDataType.STRING, parsedValue);

			return _getColorPickerCssVariable(fieldValue);
		}
		else if (StringUtil.equalsIgnoreCase(
					fragmentConfigurationField.getType(), "itemSelector")) {

			return _getInfoDisplayObjectEntryJSONObject(parsedValue);
		}
		else if (StringUtil.equalsIgnoreCase(
					fragmentConfigurationField.getType(), "length") ||
				 StringUtil.equalsIgnoreCase(
					 fragmentConfigurationField.getType(), "select") ||
				 StringUtil.equalsIgnoreCase(
					 fragmentConfigurationField.getType(), "text")) {

			FragmentConfigurationFieldDataType
				fragmentConfigurationFieldDataType =
					fragmentConfigurationField.
						getFragmentConfigurationFieldDataType();

			if (fragmentConfigurationFieldDataType == null) {
				fragmentConfigurationFieldDataType =
					FragmentConfigurationFieldDataType.STRING;
			}

			return _getFieldValue(
				fragmentConfigurationFieldDataType, parsedValue);
		}
		else if (StringUtil.equalsIgnoreCase(
					fragmentConfigurationField.getType(), "url")) {

			return _getURLValue(parsedValue);
		}

		return _getFieldValue(
			FragmentConfigurationFieldDataType.STRING, parsedValue);
	}

	private Object _getFieldValue(
		FragmentConfigurationFieldDataType fragmentConfigurationFieldDataType,
		String value) {

		if (fragmentConfigurationFieldDataType ==
				FragmentConfigurationFieldDataType.ARRAY) {

			try {
				return _jsonFactory.createJSONArray(value);
			}
			catch (JSONException jsonException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to parse configuration JSON: " + value,
						jsonException);
				}
			}
		}
		else if (fragmentConfigurationFieldDataType ==
					FragmentConfigurationFieldDataType.BOOLEAN) {

			return GetterUtil.getBoolean(value);
		}
		else if (fragmentConfigurationFieldDataType ==
					FragmentConfigurationFieldDataType.DOUBLE) {

			return GetterUtil.getDouble(value);
		}
		else if (fragmentConfigurationFieldDataType ==
					FragmentConfigurationFieldDataType.INTEGER) {

			return GetterUtil.getInteger(value);
		}
		else if (fragmentConfigurationFieldDataType ==
					FragmentConfigurationFieldDataType.OBJECT) {

			try {
				return _jsonFactory.createJSONObject(value);
			}
			catch (JSONException jsonException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to parse configuration JSON: " + value,
						jsonException);
				}
			}
		}
		else if (fragmentConfigurationFieldDataType ==
					FragmentConfigurationFieldDataType.STRING) {

			return value;
		}

		return null;
	}

	private Object _getInfoDisplayObjectEntry(String value) {
		if (Validator.isNull(value)) {
			return null;
		}

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(value);

			InfoItemObjectProvider<?> infoItemObjectProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					jsonObject.getString("className"),
					ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

			if (infoItemObjectProvider == null) {
				return null;
			}

			return infoItemObjectProvider.getInfoItem(
				new ClassPKInfoItemIdentifier(jsonObject.getLong("classPK")));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get entry: " + value, exception);
			}
		}

		return null;
	}

	private JSONObject _getInfoDisplayObjectEntryJSONObject(String value) {
		try {
			if (Validator.isNull(value) ||
				Objects.equals(value, _jsonFactory.getNullJSON())) {

				return _jsonFactory.createJSONObject();
			}

			JSONObject configurationValueJSONObject =
				_jsonFactory.createJSONObject(value);

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				_jsonFactory.looseSerialize(_getInfoDisplayObjectEntry(value)));

			jsonObject.put(
				"className", configurationValueJSONObject.getString("className")
			).put(
				"classNameId",
				configurationValueJSONObject.getLong("classNameId")
			).put(
				"classPK", configurationValueJSONObject.getLong("classPK")
			).put(
				"externalReferenceCode",
				configurationValueJSONObject.getString("externalReferenceCode")
			).put(
				"template", configurationValueJSONObject.get("template")
			).put(
				"title", configurationValueJSONObject.getString("title")
			);

			return jsonObject;
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to serialize info display object entry to JSON: " +
						value,
					jsonException);
			}
		}

		return null;
	}

	private Object _getInfoListObjectEntry(
		String value, long[] segmentsEntryIds,
		JSONObject typeOptionsJSONObject) {

		if (Validator.isNull(value)) {
			return Collections.emptyList();
		}

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(value);

			if (jsonObject.length() <= 0) {
				return Collections.emptyList();
			}

			String type = jsonObject.getString("type");

			LayoutListRetriever<?, ListObjectReference> layoutListRetriever =
				(LayoutListRetriever<?, ListObjectReference>)
					_layoutListRetrieverRegistry.getLayoutListRetriever(type);

			if (layoutListRetriever == null) {
				return Collections.emptyList();
			}

			ListObjectReferenceFactory<?> listObjectReferenceFactory =
				_listObjectReferenceFactoryRegistry.getListObjectReference(
					type);

			if (listObjectReferenceFactory == null) {
				return Collections.emptyList();
			}

			DefaultLayoutListRetrieverContext
				defaultLayoutListRetrieverContext =
					new DefaultLayoutListRetrieverContext();

			if (typeOptionsJSONObject != null) {
				int numberOfItems = typeOptionsJSONObject.getInt(
					"numberOfItems", 0);

				if (numberOfItems > 0) {
					defaultLayoutListRetrieverContext.setPagination(
						Pagination.of(numberOfItems, 0));
				}
			}

			defaultLayoutListRetrieverContext.setSegmentsEntryIds(
				segmentsEntryIds);

			InfoPage<?> infoPage = layoutListRetriever.getInfoPage(
				listObjectReferenceFactory.getListObjectReference(jsonObject),
				defaultLayoutListRetrieverContext);

			return infoPage.getPageItems();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get collection: " + value, exception);
			}
		}

		return Collections.emptyList();
	}

	private JSONObject _getInfoListObjectEntryJSONObject(String value) {
		if (Validator.isNull(value)) {
			return _jsonFactory.createJSONObject();
		}

		try {
			return _jsonFactory.createJSONObject(value);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to serialize info list object entry to JSON: " +
						value,
					jsonException);
			}
		}

		return null;
	}

	private Object _getNavItemsContextObject(String value) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		FragmentEntryMenuDisplayConfiguration
			fragmentEntryMenuDisplayConfiguration =
				new FragmentEntryMenuDisplayConfiguration(value);

		return NavItemUtil.getNavigationMenuContext(
			1, "auto", serviceContext.getRequest(),
			fragmentEntryMenuDisplayConfiguration.getNavigationMenuMode(),
			false, fragmentEntryMenuDisplayConfiguration.getRootItemId(),
			fragmentEntryMenuDisplayConfiguration.getRootItemLevel(),
			fragmentEntryMenuDisplayConfiguration.getRootItemType(),
			fragmentEntryMenuDisplayConfiguration.getSiteNavigationMenuId(
				serviceContext.getScopeGroupId()));
	}

	private Object _getURLValue(String value) {
		JSONObject jsonObject = (JSONObject)_getFieldValue(
			FragmentConfigurationFieldDataType.OBJECT, value);

		JSONObject layoutJSONObject = jsonObject.getJSONObject("layout");

		if (layoutJSONObject == null) {
			return jsonObject.getString("href");
		}

		long groupId = layoutJSONObject.getLong("groupId");
		boolean privateLayout = layoutJSONObject.getBoolean("privateLayout");
		long layoutId = layoutJSONObject.getLong("layoutId");

		Layout layout = _layoutLocalService.fetchLayout(
			groupId, privateLayout, layoutId);

		if (layout == null) {
			return StringPool.POUND;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		try {
			return _portal.getLayoutFullURL(
				layout, serviceContext.getThemeDisplay());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private void _translateConfigurationField(
		JSONObject fieldJSONObject, ResourceBundle resourceBundle) {

		String fieldDescription = fieldJSONObject.getString("description");

		fieldJSONObject.put(
			"description",
			_language.get(resourceBundle, fieldDescription, fieldDescription));

		String fieldLabel = fieldJSONObject.getString("label");

		fieldJSONObject.put(
			"label", _language.get(resourceBundle, fieldLabel, fieldLabel));

		String type = fieldJSONObject.getString("type");

		if (!Objects.equals(type, "select") && !Objects.equals(type, "text")) {
			return;
		}

		if (fieldJSONObject.getBoolean("localizable")) {
			String defaultValue = fieldJSONObject.getString("defaultValue");

			fieldJSONObject.put(
				"defaultValue",
				_language.get(resourceBundle, defaultValue, defaultValue));
		}

		JSONObject typeOptionsJSONObject = fieldJSONObject.getJSONObject(
			"typeOptions");

		if (typeOptionsJSONObject == null) {
			return;
		}

		if (Objects.equals(type, "select")) {
			JSONArray validValuesJSONArray = typeOptionsJSONObject.getJSONArray(
				"validValues");

			Iterator<JSONObject> validValuesIterator =
				validValuesJSONArray.iterator();

			validValuesIterator.forEachRemaining(
				validValueJSONObject -> {
					String value = validValueJSONObject.getString("value");

					String label = validValueJSONObject.getString(
						"label", value);

					validValueJSONObject.put(
						"label", _language.get(resourceBundle, label, label));
				});
		}
		else {
			JSONObject validationJSONObject =
				typeOptionsJSONObject.getJSONObject("validation");

			if ((validationJSONObject != null) &&
				validationJSONObject.has("errorMessage")) {

				String errorMessage = validationJSONObject.getString(
					"errorMessage");

				validationJSONObject.put(
					"errorMessage",
					_language.get(resourceBundle, errorMessage, errorMessage));
			}
		}
	}

	private static final String _CONTEXT_OBJECT_LIST_SUFFIX = "ObjectList";

	private static final String _CONTEXT_OBJECT_SUFFIX = "Object";

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryConfigurationParserImpl.class);

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutListRetrieverRegistry _layoutListRetrieverRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private ListObjectReferenceFactoryRegistry
		_listObjectReferenceFactoryRegistry;

	@Reference
	private Portal _portal;

}