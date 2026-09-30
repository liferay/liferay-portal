/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.FrontendTokenMapping;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.LanguageResources;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Pavel Savinov
 */
public class CommonStylesUtil {

	public static List<String> getAvailableStyleNames() {
		if (_availableStyleNames != null) {
			return _availableStyleNames;
		}

		List<String> availableStyleNames = new ArrayList<>();

		JSONArray jsonArray = getCommonStylesJSONArray();

		Iterator<JSONObject> iterator = jsonArray.iterator();

		iterator.forEachRemaining(
			jsonObject -> {
				JSONArray stylesJSONArray = jsonObject.getJSONArray("styles");

				Iterator<JSONObject> stylesIterator =
					stylesJSONArray.iterator();

				stylesIterator.forEachRemaining(
					styleJSONObject -> availableStyleNames.add(
						styleJSONObject.getString("name")));
			});

		Collections.sort(availableStyleNames);

		_availableStyleNames = availableStyleNames;

		return _availableStyleNames;
	}

	public static String getCSS(
		Layout layout, LayoutStructure layoutStructure,
		boolean styleBookEntryPreview) {

		StringBundler sb = new StringBundler();

		sb.append(".lfr-layout-structure-item-container {padding: 0;} ");
		sb.append(".lfr-layout-structure-item-row {overflow: hidden;} ");
		sb.append(".portlet-borderless .portlet-content {padding: 0;} ");
		sb.append("[data-lfr-editable-type=\"rich-text\"] > p:only-child ");
		sb.append("{margin-bottom:0;}");

		JSONObject frontendTokensJSONObject = _getFrontendTokensJSONObject(
			layout, styleBookEntryPreview);

		List<LayoutStructureItem> layoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		for (ViewportSize viewportSize : _sortedViewportSizes) {
			StringBundler cssSB = new StringBundler();

			for (LayoutStructureItem layoutStructureItem :
					layoutStructureItems) {

				if (!(layoutStructureItem instanceof
						StyledLayoutStructureItem)) {

					continue;
				}

				StyledLayoutStructureItem styledLayoutStructureItem =
					(StyledLayoutStructureItem)layoutStructureItem;

				cssSB.append(
					_getLayoutStructureItemCSS(
						frontendTokensJSONObject, styledLayoutStructureItem,
						viewportSize));

				String customCSS = _getCustomCSS(
					styledLayoutStructureItem, viewportSize);

				if (Validator.isNotNull(customCSS)) {
					cssSB.append(
						StringUtil.replace(
							customCSS, "[$FRAGMENT_CLASS$]",
							styledLayoutStructureItem.getUniqueCssClass()));
				}
			}

			if (cssSB.length() == 0) {
				continue;
			}

			if (Objects.equals(viewportSize, ViewportSize.DESKTOP)) {
				sb.append(cssSB);
			}
			else {
				sb.append("@media screen and (max-width: ");
				sb.append(viewportSize.getMaxWidth());
				sb.append("px) {");
				sb.append(cssSB);
				sb.append(StringPool.CLOSE_CURLY_BRACE);
			}
		}

		return sb.toString();
	}

	public static String getCSSTemplate(String propertyKey) {
		if (_cssTemplates != null) {
			return _cssTemplates.get(propertyKey);
		}

		_loadCSSTemplates();

		return _cssTemplates.get(propertyKey);
	}

	public static JSONArray getCommonStylesJSONArray() {
		try {
			return getCommonStylesJSONArray(
				LanguageResources.getResourceBundle(LocaleUtil.getDefault()));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static JSONArray getCommonStylesJSONArray(
			ResourceBundle resourceBundle)
		throws Exception {

		JSONArray commonStylesJSONArray = null;

		if (resourceBundle != null) {
			commonStylesJSONArray = _commonStyles.get(
				resourceBundle.getLocale());
		}

		if (commonStylesJSONArray != null) {
			return commonStylesJSONArray;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			new String(
				FileUtil.getBytes(
					CommonStylesUtil.class, "common-styles.json")));

		Iterator<JSONObject> jsonArrayIterator = jsonArray.iterator();

		while (jsonArrayIterator.hasNext()) {
			JSONObject jsonObject = jsonArrayIterator.next();

			jsonObject.put(
				"label",
				LanguageUtil.get(
					resourceBundle, jsonObject.getString("label")));

			JSONArray stylesJSONArray = jsonObject.getJSONArray("styles");

			Iterator<JSONObject> stylesJSONArrayIterator =
				stylesJSONArray.iterator();

			while (stylesJSONArrayIterator.hasNext()) {
				JSONObject styleJSONObject = stylesJSONArrayIterator.next();

				styleJSONObject.put(
					"label",
					LanguageUtil.get(
						resourceBundle, styleJSONObject.getString("label")));

				JSONObject typeOptionsJSONObject =
					styleJSONObject.getJSONObject("typeOptions");

				if (typeOptionsJSONObject == null) {
					continue;
				}

				JSONArray validValuesJSONArray =
					typeOptionsJSONObject.getJSONArray("validValues");

				if (validValuesJSONArray == null) {
					continue;
				}

				Iterator<JSONObject> validValuesJSONArrayIterator =
					validValuesJSONArray.iterator();

				while (validValuesJSONArrayIterator.hasNext()) {
					JSONObject validValueJSONObject =
						validValuesJSONArrayIterator.next();

					String label = validValueJSONObject.getString("label");

					validValueJSONObject.put(
						"label", LanguageUtil.get(resourceBundle, label));
				}
			}
		}

		if (resourceBundle != null) {
			_commonStyles.put(resourceBundle.getLocale(), jsonArray);
		}

		return jsonArray;
	}

	public static Object getDefaultStyleValue(String name) {
		if (_defaultValues != null) {
			return _defaultValues.get(name);
		}

		Map<String, Object> defaultValues = getDefaultStyleValues();

		return defaultValues.get(name);
	}

	public static Map<String, Object> getDefaultStyleValues() {
		if (_defaultValues != null) {
			return _defaultValues;
		}

		Map<String, Object> defaultValues = new HashMap<>();

		JSONArray jsonArray = getCommonStylesJSONArray();

		Iterator<JSONObject> iterator = jsonArray.iterator();

		iterator.forEachRemaining(
			jsonObject -> {
				JSONArray stylesJSONArray = jsonObject.getJSONArray("styles");

				Iterator<JSONObject> stylesIterator =
					stylesJSONArray.iterator();

				stylesIterator.forEachRemaining(
					styleJSONObject -> defaultValues.put(
						styleJSONObject.getString("name"),
						styleJSONObject.get("defaultValue")));
			});

		_defaultValues = defaultValues;

		return _defaultValues;
	}

	public static List<String> getResponsiveStyleNames() {
		if (_responsiveStyleNames != null) {
			return _responsiveStyleNames;
		}

		List<String> responsiveStyleNames = new ArrayList<>();

		for (String availableStyleName : getAvailableStyleNames()) {
			if (isResponsive(availableStyleName)) {
				responsiveStyleNames.add(availableStyleName);
			}
		}

		_responsiveStyleNames = responsiveStyleNames;

		return _responsiveStyleNames;
	}

	public static String getResponsiveTemplate(String propertyKey) {
		if (_responsiveTemplates != null) {
			return _responsiveTemplates.get(propertyKey);
		}

		_loadResponsiveTemplates();

		return _responsiveTemplates.get(propertyKey);
	}

	public static boolean isResponsive(String propertyKey) {
		if (_responsiveTemplates != null) {
			return Validator.isNotNull(_responsiveTemplates.get(propertyKey));
		}

		_loadResponsiveTemplates();

		return Validator.isNotNull(_responsiveTemplates.get(propertyKey));
	}

	private static JSONObject _createJSONObject(String json) {
		try {
			return JSONFactoryUtil.createJSONObject(json);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

	private static String _getCustomCSS(
		StyledLayoutStructureItem styledLayoutStructureItem,
		ViewportSize viewportSize) {

		if (Objects.equals(viewportSize, ViewportSize.DESKTOP)) {
			return styledLayoutStructureItem.getCustomCSS();
		}

		Map<String, String> customCSSViewports =
			styledLayoutStructureItem.getCustomCSSViewports();

		return customCSSViewports.get(viewportSize.getViewportSizeId());
	}

	private static JSONObject _getCustomFrontendTokensJSONObject(
		JSONObject frontendTokenValuesJSONObject) {

		JSONObject customFrontendTokensJSONObject =
			JSONFactoryUtil.createJSONObject();

		String prefix =
			StyleBookConstants.CUSTOM_FRONTEND_TOKEN_DEFINITION_ID +
				StringPool.COLON;

		for (String key : frontendTokenValuesJSONObject.keySet()) {
			if (!key.startsWith(prefix)) {
				continue;
			}

			JSONObject valueJSONObject =
				frontendTokenValuesJSONObject.getJSONObject(key);

			if (valueJSONObject == null) {
				continue;
			}

			String cssVariable = valueJSONObject.getString(
				"cssVariableMapping");

			if (Validator.isNull(cssVariable)) {
				continue;
			}

			customFrontendTokensJSONObject.put(
				key.substring(prefix.length()),
				JSONUtil.put(
					FrontendTokenMapping.TYPE_CSS_VARIABLE, cssVariable));
		}

		return customFrontendTokensJSONObject;
	}

	private static JSONObject _getFrontendTokensJSONObject(
		Layout layout, boolean styleBookEntryPreview) {

		StyleBookEntry styleBookEntry = null;

		if (!styleBookEntryPreview) {
			styleBookEntry = DefaultStyleBookEntryUtil.getDefaultStyleBookEntry(
				layout);
		}

		JSONObject frontendTokenValuesJSONObject =
			JSONFactoryUtil.createJSONObject();

		if (styleBookEntry != null) {
			frontendTokenValuesJSONObject = _createJSONObject(
				styleBookEntry.getFrontendTokensValues());
		}

		return _mergeFrontendTokensJSONObjects(
			_getThemeFrontendTokensJSONObject(
				frontendTokenValuesJSONObject, layout),
			_getCustomFrontendTokensJSONObject(frontendTokenValuesJSONObject));
	}

	private static String _getLayoutStructureItemCSS(
		JSONObject frontendTokensJSONObject,
		StyledLayoutStructureItem styledLayoutStructureItem,
		ViewportSize viewportSize) {

		JSONObject stylesJSONObject = _getStylesJSONObject(
			styledLayoutStructureItem.getItemConfigJSONObject(), viewportSize);

		if (stylesJSONObject.length() == 0) {
			return StringPool.BLANK;
		}

		List<String> availableStyles = ListUtil.filter(
			getAvailableStyleNames(),
			styleName -> _includeStyles(
				styledLayoutStructureItem, styleName,
				stylesJSONObject.getString(styleName), viewportSize));

		if (ListUtil.isEmpty(availableStyles)) {
			return StringPool.BLANK;
		}

		StringBundler cssSB = new StringBundler(
			(availableStyles.size() * 2) + 4);

		cssSB.append(".lfr-layout-structure-item-");
		cssSB.append(styledLayoutStructureItem.getItemId());
		cssSB.append(" {\n");

		for (String styleName : availableStyles) {
			String value = stylesJSONObject.getString(styleName);

			cssSB.append(
				StringUtil.replace(
					getCSSTemplate(styleName), "{value}",
					_getStyleValue(
						frontendTokensJSONObject, styledLayoutStructureItem,
						styleName, value)));

			cssSB.append(StringPool.NEW_LINE);
		}

		cssSB.append("}\n");

		return cssSB.toString();
	}

	private static String _getStyleFromStyleBookEntry(
		JSONObject frontendTokensJSONObject, String styleValue) {

		JSONObject styleValueJSONObject =
			frontendTokensJSONObject.getJSONObject(styleValue);

		if (styleValueJSONObject == null) {
			return styleValue;
		}

		String cssVariable = styleValueJSONObject.getString(
			FrontendTokenMapping.TYPE_CSS_VARIABLE);

		return "var(--" + HtmlUtil.escapeCSS(cssVariable) + ")";
	}

	private static String _getStyleValue(
		JSONObject frontendTokensJSONObject,
		StyledLayoutStructureItem styledLayoutStructureItem, String styleName,
		String value) {

		if (styleName.startsWith("margin") || styleName.startsWith("padding")) {
			StringBundler sb = new StringBundler(5);

			String spacingValue = _spacings.get(value);

			if (Validator.isNotNull(spacingValue)) {
				sb.append("var(--spacer-");
				sb.append(value);
				sb.append(StringPool.COMMA);
				sb.append(spacingValue);
				sb.append("rem)");
			}
			else {
				sb.append(value);
			}

			return sb.toString();
		}

		if (Objects.equals(styleName, "backgroundImage")) {
			return "var(--lfr-background-image-" +
				styledLayoutStructureItem.getItemId() +
					StringPool.CLOSE_PARENTHESIS;
		}

		if (Objects.equals(styleName, "opacity")) {
			return String.valueOf(GetterUtil.getInteger(value, 100) / 100.0);
		}

		return _getStyleFromStyleBookEntry(frontendTokensJSONObject, value);
	}

	private static JSONObject _getStylesJSONObject(
		JSONObject itemConfigJSONObject, ViewportSize viewportSize) {

		if (Objects.equals(viewportSize, ViewportSize.DESKTOP)) {
			return itemConfigJSONObject.getJSONObject("styles");
		}

		JSONObject viewportJSONObject = itemConfigJSONObject.getJSONObject(
			viewportSize.getViewportSizeId());

		if (viewportJSONObject != null) {
			JSONObject jsonObject = viewportJSONObject.getJSONObject("styles");

			if (jsonObject != null) {
				return jsonObject;
			}
		}

		return JSONFactoryUtil.createJSONObject();
	}

	private static JSONObject _getThemeFrontendTokensJSONObject(
		JSONObject frontendTokenValuesJSONObject, Layout layout) {

		JSONObject frontendTokensJSONObject =
			JSONFactoryUtil.createJSONObject();

		Group group = GroupLocalServiceUtil.fetchGroup(layout.getGroupId());

		if (group == null) {
			return frontendTokensJSONObject;
		}

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			_frontendTokenDefinitionRegistrySnapshot.get();

		FrontendTokenDefinition frontendTokenDefinition =
			frontendTokenDefinitionRegistry.getFrontendTokenDefinition(layout);

		if (frontendTokenDefinition == null) {
			return frontendTokensJSONObject;
		}

		Collection<FrontendToken> frontendTokens =
			frontendTokenDefinition.getFrontendTokens();

		for (FrontendToken frontendToken : frontendTokens) {
			List<FrontendTokenMapping> frontendTokenMappings = new ArrayList<>(
				frontendToken.getFrontendTokenMappings(
					FrontendTokenMapping.TYPE_CSS_VARIABLE));

			if (ListUtil.isEmpty(frontendTokenMappings)) {
				continue;
			}

			String value = String.valueOf(
				frontendToken.<Object>getDefaultValue());

			JSONObject valueJSONObject =
				frontendTokenValuesJSONObject.getJSONObject(
					frontendToken.getName());

			if (valueJSONObject != null) {
				value = valueJSONObject.getString("value");
			}

			frontendTokensJSONObject.put(
				frontendToken.getName(),
				JSONUtil.put(
					FrontendTokenMapping.TYPE_CSS_VARIABLE,
					() -> {
						FrontendTokenMapping frontendTokenMapping =
							frontendTokenMappings.get(0);

						return frontendTokenMapping.getValue();
					}
				).put(
					"value", value
				));
		}

		return frontendTokensJSONObject;
	}

	private static boolean _includeStyles(
		StyledLayoutStructureItem styledLayoutStructureItem, String styleName,
		String value, ViewportSize viewportSize) {

		if (Validator.isNull(value) ||
			(Objects.equals(value, getDefaultStyleValue(styleName)) &&
			 Objects.equals(viewportSize, ViewportSize.DESKTOP))) {

			return false;
		}

		if (!(styledLayoutStructureItem instanceof
				ContainerStyledLayoutStructureItem)) {

			return true;
		}

		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem =
			(ContainerStyledLayoutStructureItem)styledLayoutStructureItem;

		if (!Objects.equals(
				containerStyledLayoutStructureItem.getWidthType(), "fixed")) {

			return true;
		}

		if (Objects.equals(styleName, "marginLeft") ||
			Objects.equals(styleName, "marginRight")) {

			return false;
		}

		return true;
	}

	private static void _loadCSSTemplates() {
		Map<String, String> cssTemplates = new HashMap<>();

		JSONArray jsonArray = getCommonStylesJSONArray();

		Iterator<JSONObject> iterator = jsonArray.iterator();

		iterator.forEachRemaining(
			jsonObject -> {
				JSONArray stylesJSONArray = jsonObject.getJSONArray("styles");

				Iterator<JSONObject> stylesIterator =
					stylesJSONArray.iterator();

				stylesIterator.forEachRemaining(
					styleJSONObject -> cssTemplates.put(
						styleJSONObject.getString("name"),
						styleJSONObject.getString(
							"cssTemplate", StringPool.BLANK)));
			});

		_cssTemplates = cssTemplates;
	}

	private static void _loadResponsiveTemplates() {
		Map<String, String> responsiveTemplates = new HashMap<>();

		JSONArray jsonArray = getCommonStylesJSONArray();

		Iterator<JSONObject> iterator = jsonArray.iterator();

		iterator.forEachRemaining(
			jsonObject -> {
				JSONArray stylesJSONArray = jsonObject.getJSONArray("styles");

				Iterator<JSONObject> stylesIterator =
					stylesJSONArray.iterator();

				stylesIterator.forEachRemaining(
					styleJSONObject -> {
						boolean responsive = styleJSONObject.getBoolean(
							"responsive");

						if (responsive) {
							responsiveTemplates.put(
								styleJSONObject.getString("name"),
								styleJSONObject.getString(
									"responsiveTemplate", StringPool.BLANK));
						}
					});
			});

		_responsiveTemplates = responsiveTemplates;
	}

	private static JSONObject _mergeFrontendTokensJSONObjects(
		JSONObject frontendTokensJSONObject1,
		JSONObject frontendTokensJSONObject2) {

		try {
			return JSONUtil.merge(
				frontendTokensJSONObject1, frontendTokensJSONObject2);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return frontendTokensJSONObject1;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommonStylesUtil.class);

	private static List<String> _availableStyleNames;
	private static final Map<Locale, JSONArray> _commonStyles = new HashMap<>();
	private static Map<String, String> _cssTemplates;
	private static Map<String, Object> _defaultValues;
	private static final Snapshot<FrontendTokenDefinitionRegistry>
		_frontendTokenDefinitionRegistrySnapshot = new Snapshot<>(
			CommonStylesUtil.class, FrontendTokenDefinitionRegistry.class);
	private static List<String> _responsiveStyleNames;
	private static Map<String, String> _responsiveTemplates;
	private static final ViewportSize[] _sortedViewportSizes =
		ViewportSize.values();
	private static final Map<String, String> _spacings = HashMapBuilder.put(
		"0", "0"
	).put(
		"1", "0.25"
	).put(
		"2", "0.5"
	).put(
		"3", "1"
	).put(
		"4", "1.5"
	).put(
		"5", "3"
	).put(
		"6", "4.5"
	).put(
		"7", "6"
	).put(
		"8", "7.5"
	).put(
		"9", "9"
	).put(
		"10", "10"
	).build();

	static {
		Arrays.sort(
			_sortedViewportSizes,
			Comparator.comparingInt(ViewportSize::getOrder));
	}

}