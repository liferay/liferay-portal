/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer.structure.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.headless.delivery.dto.v1_0.ContextReference;
import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.layout.internal.importer.LayoutStructureItemImporterContext;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.util.LayoutPageTemplateEntryUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pavel Savinov
 */
public abstract class BaseLayoutStructureItemImporter {

	public JSONObject getLayoutFromItemReferenceJSONObject(
		Map<String, Object> itemReferenceMap,
		LayoutStructureItemImporterContext layoutStructureItemImporterContext) {

		String friendlyURL = null;
		Boolean privatePage = null;
		String siteKey = null;

		List<Map<String, String>> fields =
			(List<Map<String, String>>)itemReferenceMap.get("fields");

		for (Map<String, String> field : fields) {
			String key = field.get("fieldName");

			if (Objects.equals(key, "friendlyURL")) {
				friendlyURL = field.get("fieldValue");
			}
			else if (Objects.equals(key, "privatePage")) {
				privatePage = Boolean.valueOf(field.get("fieldValue"));
			}
			else if (Objects.equals(key, "siteKey")) {
				siteKey = field.get("fieldValue");
			}
		}

		if ((friendlyURL == null) || (privatePage == null)) {
			return null;
		}

		Layout currentLayout = layoutStructureItemImporterContext.getLayout();

		long groupId = currentLayout.getGroupId();

		if (Validator.isNotNull(siteKey)) {
			GroupLocalService groupLocalService =
				layoutStructureItemImporterContext.getGroupLocalService();

			Group group = groupLocalService.fetchGroup(
				currentLayout.getCompanyId(), siteKey);

			if (group == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to process mapping because group ", siteKey,
							" does not exist"));
				}

				return null;
			}

			groupId = group.getGroupId();
		}

		LayoutLocalService layoutLocalService =
			layoutStructureItemImporterContext.getLayoutLocalService();

		Layout layout = layoutLocalService.fetchLayoutByFriendlyURL(
			groupId, privatePage, friendlyURL);

		return _getLayoutJSONObject("friendlyURL", friendlyURL, layout);
	}

	public JSONObject toDisplayPageFormSubmissionResultJSONObject(
		Map<String, Object> formSuccessSubmissionResultMap,
		LayoutStructureItemImporterContext layoutStructureItemImporterContext) {

		if (GetterUtil.getBoolean(
				formSuccessSubmissionResultMap.get("defaultDisplayPage"))) {

			return _getDefaultDisplayPageJSONObject();
		}

		Map<String, Object> mapping =
			(Map<String, Object>)formSuccessSubmissionResultMap.get("mapping");

		Map<String, Object> itemReferenceMap = (Map<String, Object>)mapping.get(
			"itemReference");

		if (MapUtil.isEmpty(itemReferenceMap)) {
			return _getDefaultDisplayPageJSONObject();
		}

		String externalReferenceCode = null;

		List<Map<String, String>> fields =
			(List<Map<String, String>>)itemReferenceMap.get("fields");

		for (Map<String, String> field : fields) {
			if (Objects.equals(
					field.get("fieldName"), "externalReferenceCode")) {

				externalReferenceCode = field.get("fieldValue");

				break;
			}
		}

		Layout layout = layoutStructureItemImporterContext.getLayout();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode, layout.getGroupId());

		if (layoutPageTemplateEntry != null) {
			return JSONUtil.put(
				"displayPage",
				StringBundler.concat(
					LayoutPageTemplateEntry.class.getSimpleName(),
					StringPool.UNDERLINE,
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId())
			).put(
				"type", "displayPage"
			);
		}

		return _getDefaultDisplayPageJSONObject();
	}

	protected Map<String, Object> getDefinitionMap(Object definition)
		throws Exception {

		Map<String, Object> definitionMap = null;

		if (definition instanceof Map) {
			definitionMap = (Map<String, Object>)definition;
		}
		else {
			definitionMap = _objectMapper.readValue(
				definition.toString(), Map.class);
		}

		return definitionMap;
	}

	protected Object getLocalizedValue(Map<String, Object> map) {
		Map<String, Object> localizedValuesMap = (Map<String, Object>)map.get(
			"value_i18n");

		if (localizedValuesMap != null) {
			JSONObject localizedValueJSONObject =
				JSONFactoryUtil.createJSONObject();

			for (Map.Entry<String, Object> entry :
					localizedValuesMap.entrySet()) {

				localizedValueJSONObject.put(entry.getKey(), entry.getValue());
			}

			return localizedValueJSONObject;
		}

		return map.get("value");
	}

	protected void processMapping(
		JSONObject jsonObject,
		LayoutStructureItemImporterContext layoutStructureItemImporterContext,
		Map<String, Object> map) {

		if (map == null) {
			return;
		}

		String fieldKey = (String)map.get("fieldKey");

		Map<String, Object> itemReferenceMap = (Map<String, Object>)map.get(
			"itemReference");

		if (itemReferenceMap == null) {
			return;
		}

		String contextSource = (String)itemReferenceMap.get("contextSource");

		if (Objects.equals(
				ContextReference.ContextSource.COLLECTION_ITEM.getValue(),
				contextSource)) {

			jsonObject.put("collectionFieldId", fieldKey);

			return;
		}

		if (Objects.equals(
				ContextReference.ContextSource.DISPLAY_PAGE_ITEM.getValue(),
				contextSource)) {

			if (_isValidInfoField(
					fieldKey, layoutStructureItemImporterContext)) {

				jsonObject.put("mappedField", fieldKey);
			}

			return;
		}

		if (Validator.isNotNull(fieldKey)) {
			jsonObject.put("fieldId", fieldKey);
		}

		String className = (String)itemReferenceMap.get("className");

		if (Objects.equals(className, Layout.class.getName()) &&
			Objects.equals(itemReferenceMap.get("fieldName"), "plid")) {

			String fieldValue = (String)itemReferenceMap.get("fieldValue");

			LayoutLocalService layoutLocalService =
				layoutStructureItemImporterContext.getLayoutLocalService();

			Layout layout = layoutLocalService.fetchLayout(
				GetterUtil.getLong(fieldValue));

			jsonObject.put(
				"layout", _getLayoutJSONObject("PLID", fieldValue, layout));

			return;
		}

		if (Objects.equals(className, Layout.class.getName()) &&
			itemReferenceMap.containsKey("fields")) {

			jsonObject.put(
				"layout",
				getLayoutFromItemReferenceJSONObject(
					itemReferenceMap, layoutStructureItemImporterContext));
		}

		String classNameId = null;

		try {
			classNameId = String.valueOf(PortalUtil.getClassNameId(className));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to process mapping because class name ID could " +
						"not be obtained for class name " + className,
					exception);
			}

			return;
		}

		String classPK = String.valueOf(itemReferenceMap.get("classPK"));

		if (Validator.isNotNull(classNameId) && Validator.isNotNull(classPK)) {
			jsonObject.put(
				"classNameId", classNameId
			).put(
				"classPK", classPK
			);
		}
	}

	protected JSONObject toFragmentViewportStylesJSONObject(
		Map<String, Object> fragmentViewport) {

		if (MapUtil.isEmpty(fragmentViewport)) {
			return JSONFactoryUtil.createJSONObject();
		}

		Map<String, Object> fragmentViewportStyle =
			(Map<String, Object>)fragmentViewport.get("fragmentViewportStyle");

		if (MapUtil.isEmpty(fragmentViewportStyle)) {
			return JSONFactoryUtil.createJSONObject();
		}

		return JSONUtil.put(
			"styles",
			JSONUtil.put(
				"backgroundColor", fragmentViewportStyle.get("backgroundColor")
			).put(
				"borderColor", fragmentViewportStyle.get("borderColor")
			).put(
				"borderRadius", fragmentViewportStyle.get("borderRadius")
			).put(
				"borderWidth", fragmentViewportStyle.get("borderWidth")
			).put(
				"display",
				() -> {
					Object hidden = fragmentViewportStyle.get("hidden");

					if (hidden == null) {
						return null;
					}

					if (GetterUtil.getBoolean(hidden)) {
						return "none";
					}

					return "block";
				}
			).put(
				"fontFamily", fragmentViewportStyle.get("fontFamily")
			).put(
				"fontSize", fragmentViewportStyle.get("fontSize")
			).put(
				"fontWeight", fragmentViewportStyle.get("fontWeight")
			).put(
				"height", fragmentViewportStyle.get("height")
			).put(
				"marginBottom", fragmentViewportStyle.get("marginBottom")
			).put(
				"marginLeft", fragmentViewportStyle.get("marginLeft")
			).put(
				"marginRight", fragmentViewportStyle.get("marginRight")
			).put(
				"marginTop", fragmentViewportStyle.get("marginTop")
			).put(
				"maxHeight", fragmentViewportStyle.get("maxHeight")
			).put(
				"maxWidth", fragmentViewportStyle.get("maxWidth")
			).put(
				"minHeight", fragmentViewportStyle.get("minHeight")
			).put(
				"minWidth", fragmentViewportStyle.get("minWidth")
			).put(
				"opacity", fragmentViewportStyle.get("opacity")
			).put(
				"overflow", fragmentViewportStyle.get("overflow")
			).put(
				"paddingBottom", fragmentViewportStyle.get("paddingBottom")
			).put(
				"paddingLeft", fragmentViewportStyle.get("paddingLeft")
			).put(
				"paddingRight", fragmentViewportStyle.get("paddingRight")
			).put(
				"paddingTop", fragmentViewportStyle.get("paddingTop")
			).put(
				"shadow", fragmentViewportStyle.get("shadow")
			).put(
				"textAlign", fragmentViewportStyle.get("textAlign")
			).put(
				"textColor", fragmentViewportStyle.get("textColor")
			).put(
				"width", fragmentViewportStyle.get("width")
			));
	}

	protected JSONObject toStylesJSONObject(
		LayoutStructureItemImporterContext layoutStructureItemImporterContext,
		Map<String, Object> styles) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (MapUtil.isEmpty(styles)) {
			return jsonObject;
		}

		jsonObject.put("backgroundColor", styles.get("backgroundColor"));

		if (styles.containsKey("backgroundFragmentImage") ||
			styles.containsKey("backgroundImage")) {

			JSONObject backgroundImageJSONObject =
				JSONFactoryUtil.createJSONObject();

			Map<String, Object> childStyleMap = (Map<String, Object>)styles.get(
				"backgroundFragmentImage");

			if (MapUtil.isEmpty(childStyleMap)) {
				childStyleMap = (Map<String, Object>)styles.get(
					"backgroundImage");
			}

			if (MapUtil.isNotEmpty(childStyleMap)) {
				Map<String, Object> titleMap =
					(Map<String, Object>)childStyleMap.get("title");

				if (titleMap != null) {
					backgroundImageJSONObject.put(
						"title", getLocalizedValue(titleMap));
				}

				Map<String, Object> urlMap =
					(Map<String, Object>)childStyleMap.get("url");

				if (urlMap != null) {
					backgroundImageJSONObject.put(
						"url", getLocalizedValue(urlMap));

					processMapping(
						backgroundImageJSONObject,
						layoutStructureItemImporterContext,
						(Map<String, Object>)urlMap.get("mapping"));
				}

				jsonObject.put("backgroundImage", backgroundImageJSONObject);
			}
		}

		Object borderColor = styles.get("borderColor");

		if (borderColor instanceof String) {
			borderColor = _colors.getOrDefault(
				borderColor, (String)borderColor);
		}

		String borderRadius = GetterUtil.getString(
			styles.get("borderRadius"), null);

		boolean hidden = GetterUtil.getBoolean(styles.get("hidden"));

		if (hidden) {
			jsonObject.put("display", "none");
		}

		Object shadow = styles.getOrDefault("boxShadow", styles.get("shadow"));

		String textAlign = GetterUtil.getString(styles.get("textAlign"), null);

		if (Validator.isNull(textAlign)) {
			for (String alignKey : _ALIGN_KEYS) {
				textAlign = GetterUtil.getString(styles.get(alignKey), null);

				if (textAlign != null) {
					break;
				}
			}
		}

		Object textColor = styles.get("textColor");

		if (textColor instanceof String) {
			textColor = _colors.getOrDefault(textColor, (String)textColor);
		}

		return jsonObject.put(
			"borderColor", borderColor
		).put(
			"borderRadius",
			_borderRadiuses.getOrDefault(borderRadius, borderRadius)
		).put(
			"borderWidth", styles.get("borderWidth")
		).put(
			"fontFamily", styles.get("fontFamily")
		).put(
			"fontSize", styles.get("fontSize")
		).put(
			"fontWeight", styles.get("fontWeight")
		).put(
			"height", styles.get("height")
		).put(
			"marginBottom", styles.get("marginBottom")
		).put(
			"marginLeft", styles.get("marginLeft")
		).put(
			"marginRight", styles.get("marginRight")
		).put(
			"marginTop", styles.get("marginTop")
		).put(
			"maxHeight", styles.get("maxHeight")
		).put(
			"maxWidth", styles.get("maxWidth")
		).put(
			"minHeight", styles.get("minHeight")
		).put(
			"minWidth", styles.get("minWidth")
		).put(
			"opacity", styles.get("opacity")
		).put(
			"overflow", styles.get("overflow")
		).put(
			"paddingBottom", styles.get("paddingBottom")
		).put(
			"paddingLeft", styles.get("paddingLeft")
		).put(
			"paddingRight", styles.get("paddingRight")
		).put(
			"paddingTop", styles.get("paddingTop")
		).put(
			"shadow",
			_shadows.getOrDefault(shadow, GetterUtil.getString(shadow, null))
		).put(
			"textAlign", textAlign
		).put(
			"textColor", textColor
		).put(
			"width", styles.get("width")
		);
	}

	private JSONObject _getDefaultDisplayPageJSONObject() {
		return JSONUtil.put(
			"displayPage", ObjectEntry.class.getSimpleName() + "_displayPageURL"
		).put(
			"type", "displayPage"
		);
	}

	private JSONObject _getLayoutJSONObject(
		String fieldKey, String fieldValue, Layout layout) {

		if (layout == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to process mapping because layout could not ",
						"be obtained for ", fieldKey, " ", fieldValue));
			}

			return JSONFactoryUtil.createJSONObject();
		}

		return JSONUtil.put(
			"groupId", String.valueOf(layout.getGroupId())
		).put(
			"id", layout.getUuid()
		).put(
			"layoutId", String.valueOf(layout.getLayoutId())
		).put(
			"layoutUuid", layout.getUuid()
		).put(
			"privateLayout", layout.isPrivateLayout()
		).put(
			"title", layout.getName(LocaleUtil.getMostRelevantLocale())
		).put(
			"value", layout.getFriendlyURL()
		);
	}

	private boolean _isValidInfoField(
		String fieldKey,
		LayoutStructureItemImporterContext layoutStructureItemImporterContext) {

		Layout layout = layoutStructureItemImporterContext.getLayout();

		if (!layout.isTypeAssetDisplay()) {
			return false;
		}

		if (layout.isDraftLayout()) {
			LayoutLocalService layoutLocalService =
				layoutStructureItemImporterContext.getLayoutLocalService();

			layout = layoutLocalService.fetchLayout(layout.getClassPK());
		}

		if (layout == null) {
			return false;
		}

		LayoutPageTemplateEntryLocalService
			layoutPageTemplateEntryLocalService =
				layoutStructureItemImporterContext.
					getLayoutPageTemplateEntryLocalService();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry == null) {
			return false;
		}

		InfoItemServiceRegistry infoItemServiceRegistry =
			layoutStructureItemImporterContext.getInfoItemServiceRegistry();

		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry =
			layoutStructureItemImporterContext.
				getInfoSearchClassMapperRegistry();

		InfoItemFormProvider<Object> infoItemFormProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class,
				infoSearchClassMapperRegistry.getClassName(
					PortalUtil.fetchClassName(
						layoutPageTemplateEntry.getClassNameId())));

		if (infoItemFormProvider == null) {
			return false;
		}

		try {
			InfoForm infoForm = infoItemFormProvider.getInfoForm(
				String.valueOf(
					LayoutPageTemplateEntryUtil.getClassTypeId(
						layoutPageTemplateEntry)),
				layout.getGroupId());

			InfoField<?> infoField = infoForm.getInfoField(fieldKey);

			if (infoField != null) {
				return true;
			}
		}
		catch (NoSuchFormVariationException noSuchFormVariationException) {
			if (_log.isWarnEnabled()) {
				_log.warn(noSuchFormVariationException);
			}
		}

		return false;
	}

	private static final String[] _ALIGN_KEYS = {
		"buttonAlign", "contentAlign", "imageAlign", "textAlign"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		BaseLayoutStructureItemImporter.class);

	private static final Map<String, String> _borderRadiuses =
		HashMapBuilder.put(
			"lg", "0.375rem"
		).put(
			"none", StringPool.BLANK
		).put(
			"sm", "0.1875rem"
		).build();
	private static final Map<String, String> _colors = HashMapBuilder.put(
		"danger", "#DA1414"
	).put(
		"dark", "#272833"
	).put(
		"gray-dark", "#393A4A"
	).put(
		"info", "#2E5AAC"
	).put(
		"light", "#F1F2F5"
	).put(
		"lighter", "#F7F8F9"
	).put(
		"primary", "#0B5FFF"
	).put(
		"secondary", "#6B6C7E"
	).put(
		"success", "#287D3C"
	).put(
		"warning", "#B95000"
	).put(
		"white", "#FFFFFF"
	).build();
	private static final ObjectMapper _objectMapper = new ObjectMapper();
	private static final Map<String, String> _shadows = HashMapBuilder.put(
		"lg", "0 1rem 3rem rgba(0, 0, 0, .175)"
	).put(
		"sm", "0 .125rem .25rem rgba(0, 0, 0, .075)"
	).build();

}