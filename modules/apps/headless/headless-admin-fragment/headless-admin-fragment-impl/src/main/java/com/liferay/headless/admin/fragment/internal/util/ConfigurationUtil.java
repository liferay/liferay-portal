/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManagerUtil;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.headless.admin.fragment.dto.v1_0.CategoryFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.CategoryTreeNodeSelectorField;
import com.liferay.headless.admin.fragment.dto.v1_0.CheckboxField;
import com.liferay.headless.admin.fragment.dto.v1_0.CheckboxFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.CheckboxTypeOptions;
import com.liferay.headless.admin.fragment.dto.v1_0.CollectionFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.CollectionSelectorField;
import com.liferay.headless.admin.fragment.dto.v1_0.CollectionSelectorTypeOptions;
import com.liferay.headless.admin.fragment.dto.v1_0.ColorPaletteField;
import com.liferay.headless.admin.fragment.dto.v1_0.ColorPaletteFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.ColorPickerField;
import com.liferay.headless.admin.fragment.dto.v1_0.ColorPickerFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.Configuration;
import com.liferay.headless.admin.fragment.dto.v1_0.Dependency;
import com.liferay.headless.admin.fragment.dto.v1_0.Field;
import com.liferay.headless.admin.fragment.dto.v1_0.FieldSet;
import com.liferay.headless.admin.fragment.dto.v1_0.ItemFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.ItemSelectorField;
import com.liferay.headless.admin.fragment.dto.v1_0.ItemSelectorTypeOptions;
import com.liferay.headless.admin.fragment.dto.v1_0.LengthField;
import com.liferay.headless.admin.fragment.dto.v1_0.LengthFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.NavigationMenuFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.NavigationMenuSelectorField;
import com.liferay.headless.admin.fragment.dto.v1_0.NumberValidation;
import com.liferay.headless.admin.fragment.dto.v1_0.PatternValidation;
import com.liferay.headless.admin.fragment.dto.v1_0.SelectField;
import com.liferay.headless.admin.fragment.dto.v1_0.SelectFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.SelectTypeOptions;
import com.liferay.headless.admin.fragment.dto.v1_0.TargetCollectionDisplayField;
import com.liferay.headless.admin.fragment.dto.v1_0.TargetCollectionDisplayFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.TargetCollectionDisplayTypeOptions;
import com.liferay.headless.admin.fragment.dto.v1_0.TextField;
import com.liferay.headless.admin.fragment.dto.v1_0.TextFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.TextTypeOptions;
import com.liferay.headless.admin.fragment.dto.v1_0.TextValidation;
import com.liferay.headless.admin.fragment.dto.v1_0.TypeOptions;
import com.liferay.headless.admin.fragment.dto.v1_0.URLField;
import com.liferay.headless.admin.fragment.dto.v1_0.URLFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.ValidValue;
import com.liferay.headless.admin.fragment.dto.v1_0.Validation;
import com.liferay.headless.admin.fragment.dto.v1_0.VideoFragmentConfigurationFieldDefaultValue;
import com.liferay.headless.admin.fragment.dto.v1_0.VideoSelectorField;
import com.liferay.headless.admin.site.dto.v1_0.CategoryFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.ClassNameReference;
import com.liferay.headless.admin.site.dto.v1_0.CollectionFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.CollectionItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.CollectionReference;
import com.liferay.headless.admin.site.dto.v1_0.ColorPaletteValue;
import com.liferay.headless.admin.site.dto.v1_0.ContextualMenuNavigationMenuValue;
import com.liferay.headless.admin.site.dto.v1_0.FragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.HrefURLValue;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.ItemFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.ItemValue;
import com.liferay.headless.admin.site.dto.v1_0.NavigationMenuFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.NavigationMenuValue;
import com.liferay.headless.admin.site.dto.v1_0.RepeatableFieldsCollectionProviderReference;
import com.liferay.headless.admin.site.dto.v1_0.SiteMenuNavigationMenuValue;
import com.liferay.headless.admin.site.dto.v1_0.SitePageURLValue;
import com.liferay.headless.admin.site.dto.v1_0.SitePagesNavigationMenuValue;
import com.liferay.headless.admin.site.dto.v1_0.URLFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.URLValue;
import com.liferay.headless.admin.site.dto.v1_0.VideoValue;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.collection.provider.RepeatableFieldInfoItemCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.RepeatableFieldsInfoItemFormProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.item.selector.criteria.VideoEmbeddableHTMLItemSelectorReturnType;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
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
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.scope.Scope;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalServiceUtil;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.util.SiteNavigationMenuItemTypeRegistryUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 */
public class ConfigurationUtil {

	public static Configuration toConfiguration(
			DTOConverter
				<FragmentConfigurationField, FragmentConfigurationFieldValue>
					fragmentConfigurationFieldValueDTOConverter,
			FragmentEntry fragmentEntry)
		throws Exception {

		String configurationJSON = fragmentEntry.getConfiguration();

		if (Validator.isNull(configurationJSON)) {
			return null;
		}

		JSONObject configurationJSONObject = null;

		try {
			configurationJSONObject = JSONFactoryUtil.createJSONObject(
				configurationJSON);
		}
		catch (JSONException jsonException) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Fragment entry with ID ",
					fragmentEntry.getFragmentEntryId(),
					" has an approved configuration that is not valid JSON"),
				jsonException);
		}

		JSONArray fieldSetsJSONArray = configurationJSONObject.getJSONArray(
			"fieldSets");

		if (fieldSetsJSONArray == null) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Fragment entry with ID ",
					fragmentEntry.getFragmentEntryId(),
					" has an approved configuration without field sets"));
		}

		Configuration configuration = new Configuration();

		FieldSet[] fieldSets = JSONUtil.toArray(
			fieldSetsJSONArray,
			fieldSetJSONObject -> _toFieldSet(
				fieldSetJSONObject, fragmentConfigurationFieldValueDTOConverter,
				fragmentEntry),
			FieldSet.class);

		configuration.setFieldSets(() -> fieldSets);

		return configuration;
	}

	public static String toConfigurationJSON(
			Configuration configuration, long groupId,
			InfoItemServiceRegistry infoItemServiceRegistry)
		throws Exception {

		if (configuration == null) {
			return null;
		}

		JSONObject configurationJSONObject = JSONUtil.put(
			"fieldSets",
			JSONUtil.toJSONArray(
				configuration.getFieldSets(),
				fieldSet -> _toFieldSetJSONObject(
					fieldSet, groupId, infoItemServiceRegistry)));

		return configurationJSONObject.toString();
	}

	private static Long _getClassPK(
		String className, String externalReferenceCode, long groupId,
		InfoItemServiceRegistry infoItemServiceRegistry,
		String scopeExternalReferenceCode) {

		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		if ((infoItemDetailsProvider == null) ||
			(infoItemObjectProvider == null)) {

			return null;
		}

		try {
			Object infoItem = infoItemObjectProvider.getInfoItem(
				groupId,
				new ERCInfoItemIdentifier(
					externalReferenceCode, scopeExternalReferenceCode));

			if (infoItem == null) {
				_logOptionalReference(
					className, externalReferenceCode, groupId,
					scopeExternalReferenceCode);

				return null;
			}

			InfoItemDetails infoItemDetails =
				infoItemDetailsProvider.getInfoItemDetails(
					groupId, ClassPKInfoItemIdentifier.class, infoItem);

			if (infoItemDetails == null) {
				return null;
			}

			InfoItemReference infoItemReference =
				infoItemDetails.getInfoItemReference();

			if (infoItemReference == null) {
				return null;
			}

			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					infoItemReference.getInfoItemIdentifier();

			return classPKInfoItemIdentifier.getClassPK();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			_logOptionalReference(
				className, externalReferenceCode, groupId,
				scopeExternalReferenceCode);
		}

		return null;
	}

	private static JSONObject _getDefaultValueJSONObject(
		JSONObject fieldJSONObject) {

		Object defaultValue = fieldJSONObject.opt("defaultValue");

		if (defaultValue instanceof JSONObject) {
			return (JSONObject)defaultValue;
		}

		if (!(defaultValue instanceof String)) {
			return null;
		}

		try {
			return JSONFactoryUtil.createJSONObject((String)defaultValue);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	private static InfoCollectionProvider _getInfoCollectionProvider(
		String className, InfoItemServiceRegistry infoItemServiceRegistry) {

		InfoCollectionProvider infoCollectionProvider =
			infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class, className);

		if (infoCollectionProvider == null) {
			infoCollectionProvider = infoItemServiceRegistry.getInfoItemService(
				RelatedInfoItemCollectionProvider.class, className);
		}

		return infoCollectionProvider;
	}

	private static InfoItemFormVariation _getInfoItemFormVariation(
		String className, long groupId,
		InfoItemServiceRegistry infoItemServiceRegistry,
		ItemExternalReference itemExternalReference) {

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, className);

		if (infoItemFormVariationsProvider == null) {
			_logOptionalReference(
				className, itemExternalReference.getExternalReferenceCode(),
				groupId,
				_getScopeExternalReferenceCode(
					itemExternalReference.getScope()));

			return null;
		}

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.
				getInfoItemFormVariationByExternalReferenceCode(
					itemExternalReference.getExternalReferenceCode(), groupId);

		if (infoItemFormVariation == null) {
			_logOptionalReference(
				infoItemFormVariationsProvider.
					getInfoItemFormVariationClassName(),
				itemExternalReference.getExternalReferenceCode(), groupId,
				_getScopeExternalReferenceCode(
					itemExternalReference.getScope()));
		}

		return infoItemFormVariation;
	}

	private static String _getRepeatableFieldsTitle(
		String className, String fieldName,
		InfoItemFormVariation infoItemFormVariation,
		InfoItemServiceRegistry infoItemServiceRegistry,
		ItemExternalReference itemExternalReference) {

		if (infoItemFormVariation == null) {
			return null;
		}

		RepeatableFieldsInfoItemFormProvider<?>
			repeatableFieldsInfoItemFormProvider =
				infoItemServiceRegistry.getFirstInfoItemService(
					RepeatableFieldsInfoItemFormProvider.class, className);

		if (repeatableFieldsInfoItemFormProvider == null) {
			_logOptionalReference(
				RepeatableFieldsInfoItemFormProvider.class, className);

			return null;
		}

		try {
			InfoForm infoForm =
				repeatableFieldsInfoItemFormProvider.
					getRepeatableFieldsInfoForm(infoItemFormVariation.getKey());

			if (infoForm == null) {
				_logOptionalReference(
					InfoForm.class, infoItemFormVariation.getKey());

				return null;
			}

			InfoField infoField = infoForm.getInfoField(fieldName);

			if (infoField == null) {
				_logOptionalReference(InfoField.class, fieldName);

				return null;
			}

			if (infoField.isRepeatable()) {
				return infoField.getLabel(LocaleUtil.getDefault());
			}
		}
		catch (NoSuchFormVariationException noSuchFormVariationException) {
			_logOptionalReference(
				InfoForm.class,
				itemExternalReference.getExternalReferenceCode());

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFormVariationException);
			}
		}

		return null;
	}

	private static String _getScopeExternalReferenceCode(Scope scope) {
		if (scope == null) {
			return null;
		}

		return scope.getExternalReferenceCode();
	}

	private static String _getSitePagesTitle(
			long groupId, boolean privateLayout)
		throws PortalException {

		if (privateLayout) {
			return LanguageUtil.get(
				LocaleUtil.getMostRelevantLocale(), "private-pages-hierarchy");
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (group.isPrivateLayoutsEnabled()) {
			return LanguageUtil.get(
				LocaleUtil.getMostRelevantLocale(), "public-pages-hierarchy");
		}

		return LanguageUtil.get(
			LocaleUtil.getMostRelevantLocale(), "pages-hierarchy");
	}

	private static void _logOptionalReference(
		Class<?> modelClass, String modelExternalReferenceCode) {

		if (_log.isWarnEnabled()) {
			StringBundler sb = new StringBundler(6);

			sb.append("Optional reference generated for missing ");
			sb.append(modelClass.getSimpleName());
			sb.append(" with external reference code ");
			sb.append(modelExternalReferenceCode);
			sb.append(" and company ID ");
			sb.append(CompanyThreadLocal.getCompanyId());

			_log.warn(sb.toString());
		}
	}

	private static void _logOptionalReference(
		String className, String externalReferenceCode, long groupId,
		String scopeExternalReferenceCode) {

		if (_log.isWarnEnabled()) {
			StringBundler sb = new StringBundler(7);

			sb.append("Optional reference generated for missing entity with ");
			sb.append("class name ");
			sb.append(className);
			sb.append(", external reference code ");
			sb.append(externalReferenceCode);

			if (Validator.isNotNull(scopeExternalReferenceCode)) {
				sb.append(", and scope external reference code ");
				sb.append(scopeExternalReferenceCode);
			}
			else {
				sb.append(", and null scope with current scope ID ");
				sb.append(groupId);
			}

			_log.warn(sb.toString());
		}

		EmptyModelManagerUtil.reportMissingReference(
			className, externalReferenceCode, groupId);
	}

	private static Boolean _toBoolean(
		CheckboxFragmentConfigurationFieldDefaultValue
			checkboxFragmentConfigurationFieldDefaultValue) {

		if (checkboxFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		return checkboxFragmentConfigurationFieldDefaultValue.getValue();
	}

	private static Boolean _toBoolean(JSONObject fieldJSONObject) {
		if (!fieldJSONObject.has("defaultValue")) {
			return null;
		}

		return fieldJSONObject.getBoolean("defaultValue");
	}

	private static CategoryFragmentConfigurationFieldDefaultValue
		_toCategoryFragmentConfigurationFieldDefaultValue(
			ItemExternalReference itemExternalReference) {

		if (itemExternalReference == null) {
			return null;
		}

		return new CategoryFragmentConfigurationFieldDefaultValue() {
			{
				setValue(() -> itemExternalReference);
			}
		};
	}

	private static JSONObject _toCategoryTreeNodeJSONObject(
			CategoryFragmentConfigurationFieldDefaultValue
				categoryFragmentConfigurationFieldDefaultValue,
			long groupId)
		throws PortalException {

		if (categoryFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		ItemExternalReference itemExternalReference =
			categoryFragmentConfigurationFieldDefaultValue.getValue();

		if (itemExternalReference == null) {
			return null;
		}

		String categoryTreeNodeType = "Category";

		if (Objects.equals(
				itemExternalReference.getClassName(),
				AssetVocabulary.class.getName())) {

			categoryTreeNodeType = "Vocabulary";
		}

		String scopeExternalReferenceCode =
			ScopeUtil.getItemScopeExternalReferenceCode(
				_getScopeExternalReferenceCode(
					itemExternalReference.getScope()),
				groupId);

		JSONObject categoryTreeNodeJSONObject = JSONUtil.put(
			"categoryTreeNodeType", categoryTreeNodeType
		).put(
			"externalReferenceCode",
			itemExternalReference.getExternalReferenceCode()
		).put(
			"scopeExternalReferenceCode", scopeExternalReferenceCode
		);

		Long itemGroupId = ScopeUtil.getItemGroupId(
			CompanyThreadLocal.getCompanyId(), scopeExternalReferenceCode,
			groupId);

		if (itemGroupId == null) {
			_logOptionalReference(
				itemExternalReference.getClassName(),
				itemExternalReference.getExternalReferenceCode(), groupId,
				scopeExternalReferenceCode);

			return categoryTreeNodeJSONObject;
		}

		if (Objects.equals(categoryTreeNodeType, "Vocabulary")) {
			AssetVocabulary assetVocabulary =
				AssetVocabularyLocalServiceUtil.
					fetchAssetVocabularyByExternalReferenceCode(
						itemExternalReference.getExternalReferenceCode(),
						itemGroupId);

			if (assetVocabulary == null) {
				_logOptionalReference(
					itemExternalReference.getClassName(),
					itemExternalReference.getExternalReferenceCode(), groupId,
					scopeExternalReferenceCode);

				return categoryTreeNodeJSONObject;
			}

			return categoryTreeNodeJSONObject.put(
				"categoryTreeNodeId",
				String.valueOf(assetVocabulary.getVocabularyId())
			).put(
				"title",
				assetVocabulary.getTitle(LocaleUtil.getMostRelevantLocale())
			);
		}

		AssetCategory assetCategory =
			AssetCategoryLocalServiceUtil.
				fetchAssetCategoryByExternalReferenceCode(
					itemExternalReference.getExternalReferenceCode(),
					itemGroupId);

		if (assetCategory == null) {
			_logOptionalReference(
				itemExternalReference.getClassName(),
				itemExternalReference.getExternalReferenceCode(), groupId,
				scopeExternalReferenceCode);

			return categoryTreeNodeJSONObject;
		}

		return categoryTreeNodeJSONObject.put(
			"categoryTreeNodeId", String.valueOf(assetCategory.getCategoryId())
		).put(
			"title", assetCategory.getName()
		);
	}

	private static CheckboxFragmentConfigurationFieldDefaultValue
		_toCheckboxFragmentConfigurationFieldDefaultValue(Boolean value) {

		if (value == null) {
			return null;
		}

		CheckboxFragmentConfigurationFieldDefaultValue
			checkboxFragmentConfigurationFieldDefaultValue =
				new CheckboxFragmentConfigurationFieldDefaultValue();

		checkboxFragmentConfigurationFieldDefaultValue.setValue(() -> value);

		return checkboxFragmentConfigurationFieldDefaultValue;
	}

	private static CheckboxTypeOptions _toCheckboxTypeOptions(
		JSONObject typeOptionsJSONObject) {

		if (typeOptionsJSONObject == null) {
			return null;
		}

		return new CheckboxTypeOptions() {
			{
				setDependency(() -> _toDependencyMap(typeOptionsJSONObject));
				setDisplayType(
					() -> CheckboxTypeOptions.DisplayType.create(
						typeOptionsJSONObject.getString("displayType")));
			}
		};
	}

	private static JSONObject _toClassNameReferenceJSONObject(
		ClassNameReference classNameReference,
		InfoItemServiceRegistry infoItemServiceRegistry) {

		String className = classNameReference.getClassName();

		if (Validator.isNull(className)) {
			return null;
		}

		InfoCollectionProvider infoCollectionProvider =
			_getInfoCollectionProvider(className, infoItemServiceRegistry);

		if (infoCollectionProvider == null) {
			_logOptionalReference(InfoCollectionProvider.class, className);

			return JSONUtil.put(
				"key", className
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			);
		}

		return JSONUtil.put(
			"itemSubtype",
			() -> {
				if (!(infoCollectionProvider instanceof
						SingleFormVariationInfoCollectionProvider)) {

					return null;
				}

				SingleFormVariationInfoCollectionProvider<?>
					singleFormVariationInfoCollectionProvider =
						(SingleFormVariationInfoCollectionProvider<?>)
							infoCollectionProvider;

				return singleFormVariationInfoCollectionProvider.
					getFormVariationKey();
			}
		).put(
			"itemType", infoCollectionProvider.getCollectionItemClassName()
		).put(
			"key", infoCollectionProvider.getKey()
		).put(
			"title",
			() -> infoCollectionProvider.getLabel(LocaleUtil.getDefault())
		).put(
			"type", InfoListProviderItemSelectorReturnType.class.getName()
		);
	}

	private static CollectionFragmentConfigurationFieldDefaultValue
		_toCollectionFragmentConfigurationFieldDefaultValue(
			CollectionReference collectionReference) {

		if (collectionReference == null) {
			return null;
		}

		return new CollectionFragmentConfigurationFieldDefaultValue() {
			{
				setValue(() -> collectionReference);
			}
		};
	}

	private static JSONObject _toCollectionItemExternalReferenceJSONObject(
			CollectionItemExternalReference collectionItemExternalReference,
			long groupId)
		throws PortalException {

		if (Validator.isNull(
				collectionItemExternalReference.getExternalReferenceCode())) {

			return null;
		}

		String scopeExternalReferenceCode =
			ScopeUtil.getItemScopeExternalReferenceCode(
				_getScopeExternalReferenceCode(
					collectionItemExternalReference.getScope()),
				groupId);

		JSONObject collectionJSONObject = JSONUtil.put(
			"externalReferenceCode",
			collectionItemExternalReference.getExternalReferenceCode()
		).put(
			"scopeExternalReferenceCode", scopeExternalReferenceCode
		).put(
			"type", InfoListItemSelectorReturnType.class.getName()
		);

		Long itemGroupId = ScopeUtil.getItemGroupId(
			CompanyThreadLocal.getCompanyId(), scopeExternalReferenceCode,
			groupId);

		if (itemGroupId == null) {
			_logOptionalReference(
				AssetListEntry.class.getName(),
				collectionItemExternalReference.getExternalReferenceCode(),
				groupId, scopeExternalReferenceCode);

			return collectionJSONObject;
		}

		AssetListEntry assetListEntry =
			AssetListEntryLocalServiceUtil.
				fetchAssetListEntryByExternalReferenceCode(
					collectionItemExternalReference.getExternalReferenceCode(),
					itemGroupId);

		if (assetListEntry == null) {
			_logOptionalReference(
				AssetListEntry.class.getName(),
				collectionItemExternalReference.getExternalReferenceCode(),
				groupId, scopeExternalReferenceCode);

			return collectionJSONObject;
		}

		return collectionJSONObject.put(
			"classNameId",
			String.valueOf(PortalUtil.getClassNameId(AssetListEntry.class))
		).put(
			"classPK", String.valueOf(assetListEntry.getAssetListEntryId())
		).put(
			"itemSubtype", assetListEntry.getAssetEntrySubtype()
		).put(
			"itemType", assetListEntry.getAssetEntryType()
		).put(
			"title", assetListEntry.getTitle()
		);
	}

	private static JSONObject _toCollectionJSONObject(
			CollectionFragmentConfigurationFieldDefaultValue
				collectionFragmentConfigurationFieldDefaultValue,
			long groupId, InfoItemServiceRegistry infoItemServiceRegistry)
		throws PortalException {

		if (collectionFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		CollectionReference collectionReference =
			collectionFragmentConfigurationFieldDefaultValue.getValue();

		if (collectionReference instanceof
				ClassNameReference classNameReference) {

			return _toClassNameReferenceJSONObject(
				classNameReference, infoItemServiceRegistry);
		}

		if (collectionReference instanceof
				CollectionItemExternalReference
					collectionItemExternalReference) {

			return _toCollectionItemExternalReferenceJSONObject(
				collectionItemExternalReference, groupId);
		}

		if (collectionReference instanceof
				RepeatableFieldsCollectionProviderReference
					repeatableFieldsCollectionProviderReference) {

			return _toRepeatableFieldsCollectionProviderReferenceJSONObject(
				groupId, infoItemServiceRegistry,
				repeatableFieldsCollectionProviderReference);
		}

		return null;
	}

	private static CollectionReference _toCollectionReference(
		JSONObject fieldJSONObject,
		DTOConverter
			<FragmentConfigurationField, FragmentConfigurationFieldValue>
				fragmentConfigurationFieldValueDTOConverter,
		FragmentEntry fragmentEntry) {

		FragmentConfigurationFieldValue fragmentConfigurationFieldValue =
			_toFragmentConfigurationFieldValue(
				fieldJSONObject, fragmentConfigurationFieldValueDTOConverter,
				fragmentEntry);

		if (!(fragmentConfigurationFieldValue instanceof
				CollectionFragmentConfigurationFieldValue
					collectionFragmentConfigurationFieldValue)) {

			return null;
		}

		return collectionFragmentConfigurationFieldValue.getValue();
	}

	private static CollectionSelectorTypeOptions
		_toCollectionSelectorTypeOptions(JSONObject typeOptionsJSONObject) {

		if (typeOptionsJSONObject == null) {
			return null;
		}

		return new CollectionSelectorTypeOptions() {
			{
				setDependency(() -> _toDependencyMap(typeOptionsJSONObject));
				setItemSubtype(
					() -> typeOptionsJSONObject.getString("itemSubtype", null));
				setItemType(
					() -> typeOptionsJSONObject.getString("itemType", null));
				setNumberOfItems(
					() -> {
						if (!typeOptionsJSONObject.has("numberOfItems")) {
							return null;
						}

						return typeOptionsJSONObject.getInt("numberOfItems");
					});
			}
		};
	}

	private static ColorPaletteFragmentConfigurationFieldDefaultValue
		_toColorPaletteFragmentConfigurationFieldDefaultValue(
			ColorPaletteValue colorPaletteValue) {

		if (colorPaletteValue == null) {
			return null;
		}

		return new ColorPaletteFragmentConfigurationFieldDefaultValue() {
			{
				setValue(() -> colorPaletteValue);
			}
		};
	}

	private static JSONObject _toColorPaletteJSONObject(
		ColorPaletteFragmentConfigurationFieldDefaultValue
			colorPaletteFragmentConfigurationFieldDefaultValue) {

		if (colorPaletteFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		ColorPaletteValue colorPaletteValue =
			colorPaletteFragmentConfigurationFieldDefaultValue.getValue();

		if (colorPaletteValue == null) {
			return null;
		}

		JSONObject colorPaletteJSONObject = JSONUtil.put(
			"color", colorPaletteValue.getColor()
		).put(
			"cssClass", colorPaletteValue.getCssClass()
		).put(
			"rgbValue", colorPaletteValue.getRgbValue()
		);

		if (colorPaletteJSONObject.length() == 0) {
			return null;
		}

		return colorPaletteJSONObject;
	}

	private static ColorPaletteValue _toColorPaletteValue(
		JSONObject defaultValueJSONObject) {

		if (JSONUtil.isEmpty(defaultValueJSONObject)) {
			return null;
		}

		return new ColorPaletteValue() {
			{
				setColor(() -> defaultValueJSONObject.getString("color", null));
				setCssClass(
					() -> defaultValueJSONObject.getString("cssClass", null));
				setRgbValue(
					() -> defaultValueJSONObject.getString("rgbValue", null));
			}
		};
	}

	private static ColorPickerFragmentConfigurationFieldDefaultValue
		_toColorPickerFragmentConfigurationFieldDefaultValue(String value) {

		if (value == null) {
			return null;
		}

		ColorPickerFragmentConfigurationFieldDefaultValue
			colorPickerFragmentConfigurationFieldDefaultValue =
				new ColorPickerFragmentConfigurationFieldDefaultValue();

		colorPickerFragmentConfigurationFieldDefaultValue.setValue(() -> value);

		return colorPickerFragmentConfigurationFieldDefaultValue;
	}

	private static JSONObject _toContextualMenuJSONObject(
		ContextualMenuNavigationMenuValue contextualMenuNavigationMenuValue) {

		String contextualMenu = _toInternalContextualMenuType(
			contextualMenuNavigationMenuValue.getContextualMenuType());

		if (contextualMenu == null) {
			return null;
		}

		return JSONUtil.put(
			"contextualMenu", contextualMenu
		).put(
			"title",
			LanguageUtil.get(LocaleUtil.getMostRelevantLocale(), contextualMenu)
		);
	}

	private static Dependency _toDependency(JSONObject dependencyJSONObject) {
		return new Dependency() {
			{
				setType(
					() -> Dependency.Type.create(
						dependencyJSONObject.getString("type")));
				setValue(() -> dependencyJSONObject.getString("value"));
			}
		};
	}

	private static JSONObject _toDependencyJSONObject(
		Map<String, Dependency> dependencyMap) {

		if (dependencyMap == null) {
			return null;
		}

		JSONObject dependencyJSONObject = JSONFactoryUtil.createJSONObject();

		for (Map.Entry<String, Dependency> entry : dependencyMap.entrySet()) {
			Dependency dependency = entry.getValue();

			dependencyJSONObject.put(
				entry.getKey(),
				JSONUtil.put(
					"type", String.valueOf(dependency.getType())
				).put(
					"value", dependency.getValue()
				));
		}

		return dependencyJSONObject;
	}

	private static Map<String, Dependency> _toDependencyMap(
		JSONObject typeOptionsJSONObject) {

		JSONObject dependencyJSONObject = typeOptionsJSONObject.getJSONObject(
			"dependency");

		if (dependencyJSONObject == null) {
			return null;
		}

		Map<String, Dependency> dependencyMap = new HashMap<>();

		for (String key : dependencyJSONObject.keySet()) {
			JSONObject entryJSONObject = dependencyJSONObject.getJSONObject(
				key);

			if (entryJSONObject == null) {
				continue;
			}

			dependencyMap.put(key, _toDependency(entryJSONObject));
		}

		if (dependencyMap.isEmpty()) {
			return null;
		}

		return dependencyMap;
	}

	private static Field _toField(
		JSONObject fieldJSONObject,
		DTOConverter
			<FragmentConfigurationField, FragmentConfigurationFieldValue>
				fragmentConfigurationFieldValueDTOConverter,
		FragmentEntry fragmentEntry) {

		if (fieldJSONObject == null) {
			return null;
		}

		String type = fieldJSONObject.getString("type");

		Field field = null;

		if (Objects.equals(type, "categoryTreeNodeSelector")) {
			field = new CategoryTreeNodeSelectorField() {
				{
					setDefaultValue(
						() -> _toCategoryFragmentConfigurationFieldDefaultValue(
							_toItemExternalReference(
								fieldJSONObject,
								fragmentConfigurationFieldValueDTOConverter,
								fragmentEntry)));
					setTypeOptions(
						() -> _toTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "checkbox")) {
			field = new CheckboxField() {
				{
					setDefaultValue(
						() -> _toCheckboxFragmentConfigurationFieldDefaultValue(
							_toBoolean(fieldJSONObject)));
					setTypeOptions(
						() -> _toCheckboxTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "collectionSelector")) {
			field = new CollectionSelectorField() {
				{
					setDefaultValue(
						() ->
							_toCollectionFragmentConfigurationFieldDefaultValue(
								_toCollectionReference(
									fieldJSONObject,
									fragmentConfigurationFieldValueDTOConverter,
									fragmentEntry)));
					setTypeOptions(
						() -> _toCollectionSelectorTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "colorPalette")) {
			field = new ColorPaletteField() {
				{
					setDefaultValue(
						() ->
							_toColorPaletteFragmentConfigurationFieldDefaultValue(
								_toColorPaletteValue(
									_getDefaultValueJSONObject(
										fieldJSONObject))));
					setTypeOptions(
						() -> _toTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "colorPicker")) {
			field = new ColorPickerField() {
				{
					setDefaultValue(
						() ->
							_toColorPickerFragmentConfigurationFieldDefaultValue(
								fieldJSONObject.getString(
									"defaultValue", null)));
					setTypeOptions(
						() -> _toTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "itemSelector")) {
			field = new ItemSelectorField() {
				{
					setDefaultValue(
						() -> _toItemFragmentConfigurationFieldDefaultValue(
							_toItemValue(
								fieldJSONObject,
								fragmentConfigurationFieldValueDTOConverter,
								fragmentEntry)));
					setTypeOptions(
						() -> _toItemSelectorTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "length")) {
			field = new LengthField() {
				{
					setDefaultValue(
						() -> _toLengthFragmentConfigurationFieldDefaultValue(
							fieldJSONObject.getString("defaultValue", null)));
					setTypeOptions(
						() -> _toTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "navigationMenuSelector")) {
			field = new NavigationMenuSelectorField() {
				{
					setDefaultValue(
						() ->
							_toNavigationMenuFragmentConfigurationFieldDefaultValue(
								_toNavigationMenuValue(
									fieldJSONObject,
									fragmentConfigurationFieldValueDTOConverter,
									fragmentEntry)));
					setTypeOptions(
						() -> _toTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "select")) {
			field = new SelectField() {
				{
					setDefaultValue(
						() -> _toSelectFragmentConfigurationFieldDefaultValue(
							fieldJSONObject.getString("defaultValue", null)));
					setTypeOptions(
						() -> _toSelectTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "targetCollectionDisplay")) {
			field = new TargetCollectionDisplayField() {
				{
					setDefaultValue(
						() ->
							_toTargetCollectionDisplayFragmentConfigurationFieldDefaultValue(
								fieldJSONObject.getJSONArray("defaultValue")));
					setTypeOptions(
						() -> _toTargetCollectionDisplayTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "text")) {
			field = new TextField() {
				{
					setDefaultValue(
						() -> _toTextFragmentConfigurationFieldDefaultValue(
							fieldJSONObject.getString("defaultValue", null)));
					setTypeOptions(
						() -> _toTextTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "url")) {
			field = new URLField() {
				{
					setDefaultValue(
						() -> _toURLFragmentConfigurationFieldDefaultValue(
							_toURLValue(
								fieldJSONObject,
								fragmentConfigurationFieldValueDTOConverter,
								fragmentEntry)));
					setTypeOptions(
						() -> _toTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else if (Objects.equals(type, "videoSelector")) {
			field = new VideoSelectorField() {
				{
					setDefaultValue(
						() -> _toVideoFragmentConfigurationFieldDefaultValue(
							_toVideoValue(
								_getDefaultValueJSONObject(fieldJSONObject))));
					setTypeOptions(
						() -> _toTypeOptions(
							fieldJSONObject.getJSONObject("typeOptions")));
				}
			};
		}
		else {
			throw new IllegalStateException(
				StringBundler.concat(
					"Fragment entry with ID ",
					fragmentEntry.getFragmentEntryId(),
					" has an approved configuration with a field of unknown ",
					"type ", type));
		}

		field.setDataType(
			() -> Field.DataType.create(fieldJSONObject.getString("dataType")));
		field.setDescription(
			() -> fieldJSONObject.getString("description", null));
		field.setLabel(() -> fieldJSONObject.getString("label", null));
		field.setLocalizable(
			() -> {
				if (!fieldJSONObject.has("localizable")) {
					return null;
				}

				return fieldJSONObject.getBoolean("localizable");
			});
		field.setName(() -> fieldJSONObject.getString("name"));
		field.setType(() -> Field.Type.create(type));

		return field;
	}

	private static JSONObject _toFieldJSONObject(
			Field field, long groupId,
			InfoItemServiceRegistry infoItemServiceRegistry)
		throws PortalException {

		JSONObject fieldJSONObject = JSONFactoryUtil.createJSONObject();

		if (field.getDataType() != null) {
			fieldJSONObject.put(
				"dataType", String.valueOf(field.getDataType()));
		}

		fieldJSONObject.put(
			"description", field.getDescription()
		).put(
			"label", field.getLabel()
		).put(
			"localizable", field.getLocalizable()
		).put(
			"name", field.getName()
		).put(
			"type", String.valueOf(field.getType())
		);

		if (field instanceof
				CategoryTreeNodeSelectorField categoryTreeNodeSelectorField) {

			fieldJSONObject.put(
				"defaultValue",
				_toCategoryTreeNodeJSONObject(
					categoryTreeNodeSelectorField.getDefaultValue(), groupId)
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(
					categoryTreeNodeSelectorField.getTypeOptions())
			);
		}
		else if (field instanceof CheckboxField checkboxField) {
			fieldJSONObject.put(
				"defaultValue", _toBoolean(checkboxField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(checkboxField.getTypeOptions())
			);
		}
		else if (field instanceof
					CollectionSelectorField collectionSelectorField) {

			fieldJSONObject.put(
				"defaultValue",
				_toCollectionJSONObject(
					collectionSelectorField.getDefaultValue(), groupId,
					infoItemServiceRegistry)
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(
					collectionSelectorField.getTypeOptions())
			);
		}
		else if (field instanceof ColorPaletteField colorPaletteField) {
			fieldJSONObject.put(
				"defaultValue",
				_toColorPaletteJSONObject(colorPaletteField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(colorPaletteField.getTypeOptions())
			);
		}
		else if (field instanceof ColorPickerField colorPickerField) {
			fieldJSONObject.put(
				"defaultValue", _toString(colorPickerField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(colorPickerField.getTypeOptions())
			);
		}
		else if (field instanceof ItemSelectorField itemSelectorField) {
			fieldJSONObject.put(
				"defaultValue",
				_toItemJSONObject(
					groupId, infoItemServiceRegistry,
					itemSelectorField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(itemSelectorField.getTypeOptions())
			);
		}
		else if (field instanceof LengthField lengthField) {
			fieldJSONObject.put(
				"defaultValue", _toString(lengthField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(lengthField.getTypeOptions())
			);
		}
		else if (field instanceof
					NavigationMenuSelectorField navigationMenuSelectorField) {

			fieldJSONObject.put(
				"defaultValue",
				_toNavigationMenuJSONObject(
					groupId, navigationMenuSelectorField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(
					navigationMenuSelectorField.getTypeOptions())
			);
		}
		else if (field instanceof SelectField selectField) {
			fieldJSONObject.put(
				"defaultValue", _toString(selectField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(selectField.getTypeOptions())
			);
		}
		else if (field instanceof
					TargetCollectionDisplayField targetCollectionDisplayField) {

			fieldJSONObject.put(
				"defaultValue",
				_toJSONArray(targetCollectionDisplayField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(
					targetCollectionDisplayField.getTypeOptions())
			);
		}
		else if (field instanceof TextField textField) {
			fieldJSONObject.put(
				"defaultValue", _toString(textField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(textField.getTypeOptions())
			);
		}
		else if (field instanceof URLField urlField) {
			fieldJSONObject.put(
				"defaultValue",
				_toURLJSONObject(groupId, urlField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(urlField.getTypeOptions())
			);
		}
		else if (field instanceof VideoSelectorField videoSelectorField) {
			fieldJSONObject.put(
				"defaultValue",
				_toVideoJSONObject(videoSelectorField.getDefaultValue())
			).put(
				"typeOptions",
				_toTypeOptionsJSONObject(videoSelectorField.getTypeOptions())
			);
		}

		return fieldJSONObject;
	}

	private static FieldSet _toFieldSet(
			JSONObject fieldSetJSONObject,
			DTOConverter
				<FragmentConfigurationField, FragmentConfigurationFieldValue>
					fragmentConfigurationFieldValueDTOConverter,
			FragmentEntry fragmentEntry)
		throws Exception {

		if (fieldSetJSONObject == null) {
			return null;
		}

		FieldSet fieldSet = new FieldSet();

		fieldSet.setConfigurationRole(
			() -> FieldSet.ConfigurationRole.create(
				fieldSetJSONObject.getString("configurationRole")));
		fieldSet.setCustomComponentModule(
			() -> fieldSetJSONObject.getString("customComponentModule", null));

		Field[] fields = JSONUtil.toArray(
			fieldSetJSONObject.getJSONArray("fields"),
			fieldJSONObject -> _toField(
				fieldJSONObject, fragmentConfigurationFieldValueDTOConverter,
				fragmentEntry),
			Field.class);

		fieldSet.setFields(() -> fields);

		fieldSet.setLabel(() -> fieldSetJSONObject.getString("label", null));

		return fieldSet;
	}

	private static JSONObject _toFieldSetJSONObject(
			FieldSet fieldSet, long groupId,
			InfoItemServiceRegistry infoItemServiceRegistry)
		throws Exception {

		JSONObject fieldSetJSONObject = JSONFactoryUtil.createJSONObject();

		if (fieldSet.getConfigurationRole() != null) {
			fieldSetJSONObject.put(
				"configurationRole",
				String.valueOf(fieldSet.getConfigurationRole()));
		}

		fieldSetJSONObject.put(
			"customComponentModule", fieldSet.getCustomComponentModule()
		).put(
			"fields",
			JSONUtil.toJSONArray(
				fieldSet.getFields(),
				field -> _toFieldJSONObject(
					field, groupId, infoItemServiceRegistry))
		).put(
			"label", fieldSet.getLabel()
		);

		return fieldSetJSONObject;
	}

	private static FragmentConfigurationFieldValue
		_toFragmentConfigurationFieldValue(
			JSONObject fieldJSONObject,
			DTOConverter
				<FragmentConfigurationField, FragmentConfigurationFieldValue>
					fragmentConfigurationFieldValueDTOConverter,
			FragmentEntry fragmentEntry) {

		JSONObject defaultValueJSONObject = _getDefaultValueJSONObject(
			fieldJSONObject);

		if (defaultValueJSONObject == null) {
			return null;
		}

		try {
			return fragmentConfigurationFieldValueDTOConverter.toDTO(
				new DefaultDTOConverterContext(
					false, null,
					HashMapBuilder.<String, Object>put(
						"companyId", fragmentEntry.getCompanyId()
					).put(
						"fragmentFragmentConfigurationFieldValue",
						defaultValueJSONObject
					).put(
						"scopeGroupId", fragmentEntry.getGroupId()
					).build(),
					null, null, null, null, null, null),
				new FragmentConfigurationField(
					fieldJSONObject.getString("name"),
					fieldJSONObject.getString("dataType"), null, false,
					fieldJSONObject.getString("type")));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private static String _toInternalContextualMenuType(
		ContextualMenuNavigationMenuValue.ContextualMenuType
			contextualMenuType) {

		if (Objects.equals(
				contextualMenuType,
				ContextualMenuNavigationMenuValue.ContextualMenuType.
					CHILDREN)) {

			return "children";
		}

		if (Objects.equals(
				contextualMenuType,
				ContextualMenuNavigationMenuValue.ContextualMenuType.
					PARENT_AND_ITS_SIBLINGS)) {

			return "parent-and-its-siblings";
		}

		if (Objects.equals(
				contextualMenuType,
				ContextualMenuNavigationMenuValue.ContextualMenuType.
					SELF_AND_SIBLINGS)) {

			return "self-and-siblings";
		}

		return null;
	}

	private static ItemExternalReference _toItemExternalReference(
		JSONObject fieldJSONObject,
		DTOConverter
			<FragmentConfigurationField, FragmentConfigurationFieldValue>
				fragmentConfigurationFieldValueDTOConverter,
		FragmentEntry fragmentEntry) {

		FragmentConfigurationFieldValue fragmentConfigurationFieldValue =
			_toFragmentConfigurationFieldValue(
				fieldJSONObject, fragmentConfigurationFieldValueDTOConverter,
				fragmentEntry);

		if (!(fragmentConfigurationFieldValue instanceof
				CategoryFragmentConfigurationFieldValue
					categoryFragmentConfigurationFieldValue)) {

			return null;
		}

		ItemExternalReference itemExternalReference =
			categoryFragmentConfigurationFieldValue.getValue();

		if ((itemExternalReference == null) ||
			Validator.isNull(
				itemExternalReference.getExternalReferenceCode())) {

			return null;
		}

		return itemExternalReference;
	}

	private static ItemFragmentConfigurationFieldDefaultValue
		_toItemFragmentConfigurationFieldDefaultValue(ItemValue itemValue) {

		if (itemValue == null) {
			return null;
		}

		return new ItemFragmentConfigurationFieldDefaultValue() {
			{
				setValue(() -> itemValue);
			}
		};
	}

	private static JSONObject _toItemJSONObject(
			long groupId, InfoItemServiceRegistry infoItemServiceRegistry,
			ItemFragmentConfigurationFieldDefaultValue
				itemFragmentConfigurationFieldDefaultValue)
		throws PortalException {

		if (itemFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		ItemValue itemValue =
			itemFragmentConfigurationFieldDefaultValue.getValue();

		if ((itemValue == null) ||
			(itemValue.getItemExternalReference() == null)) {

			return null;
		}

		ItemExternalReference itemExternalReference =
			itemValue.getItemExternalReference();

		String className = itemExternalReference.getClassName();

		String scopeExternalReferenceCode =
			ScopeUtil.getItemScopeExternalReferenceCode(
				_getScopeExternalReferenceCode(
					itemExternalReference.getScope()),
				groupId);

		return JSONUtil.put(
			"className", className
		).put(
			"classNameId", String.valueOf(PortalUtil.getClassNameId(className))
		).put(
			"classPK",
			() -> {
				Long classPK = _getClassPK(
					className, itemExternalReference.getExternalReferenceCode(),
					groupId, infoItemServiceRegistry,
					scopeExternalReferenceCode);

				if (classPK == null) {
					return null;
				}

				return String.valueOf(classPK);
			}
		).put(
			"externalReferenceCode",
			itemExternalReference.getExternalReferenceCode()
		).put(
			"scopeExternalReferenceCode", scopeExternalReferenceCode
		);
	}

	private static ItemSelectorTypeOptions _toItemSelectorTypeOptions(
		JSONObject typeOptionsJSONObject) {

		if (typeOptionsJSONObject == null) {
			return null;
		}

		return new ItemSelectorTypeOptions() {
			{
				setClassName(
					() -> typeOptionsJSONObject.getString("className", null));
				setDependency(() -> _toDependencyMap(typeOptionsJSONObject));
				setEnableSelectTemplate(
					() -> {
						if (!typeOptionsJSONObject.has(
								"enableSelectTemplate")) {

							return null;
						}

						return typeOptionsJSONObject.getBoolean(
							"enableSelectTemplate");
					});
				setItemSubtype(
					() -> typeOptionsJSONObject.getString("itemSubtype", null));
				setItemType(
					() -> typeOptionsJSONObject.getString("itemType", null));
				setMimeTypes(
					() -> {
						JSONArray mimeTypesJSONArray =
							typeOptionsJSONObject.getJSONArray("mimeTypes");

						if (mimeTypesJSONArray == null) {
							return null;
						}

						return JSONUtil.toStringArray(mimeTypesJSONArray);
					});
			}
		};
	}

	private static ItemValue _toItemValue(
		JSONObject fieldJSONObject,
		DTOConverter
			<FragmentConfigurationField, FragmentConfigurationFieldValue>
				fragmentConfigurationFieldValueDTOConverter,
		FragmentEntry fragmentEntry) {

		FragmentConfigurationFieldValue fragmentConfigurationFieldValue =
			_toFragmentConfigurationFieldValue(
				fieldJSONObject, fragmentConfigurationFieldValueDTOConverter,
				fragmentEntry);

		if (!(fragmentConfigurationFieldValue instanceof
				ItemFragmentConfigurationFieldValue
					itemFragmentConfigurationFieldValue)) {

			return null;
		}

		ItemValue itemValue = itemFragmentConfigurationFieldValue.getValue();

		if (itemValue == null) {
			return null;
		}

		ItemExternalReference itemExternalReference =
			itemValue.getItemExternalReference();

		if ((itemExternalReference == null) ||
			Validator.isNull(
				itemExternalReference.getExternalReferenceCode())) {

			return null;
		}

		return itemValue;
	}

	private static JSONArray _toJSONArray(
		TargetCollectionDisplayFragmentConfigurationFieldDefaultValue
			targetCollectionDisplayFragmentConfigurationFieldDefaultValue) {

		if (targetCollectionDisplayFragmentConfigurationFieldDefaultValue ==
				null) {

			return null;
		}

		String[] values =
			targetCollectionDisplayFragmentConfigurationFieldDefaultValue.
				getValue();

		if (values == null) {
			return null;
		}

		return JSONFactoryUtil.createJSONArray(values);
	}

	private static JSONObject _toLayoutJSONObject(
			long groupId, ItemExternalReference itemExternalReference)
		throws PortalException {

		String scopeExternalReferenceCode =
			ScopeUtil.getItemScopeExternalReferenceCode(
				_getScopeExternalReferenceCode(
					itemExternalReference.getScope()),
				groupId);

		JSONObject layoutJSONObject = JSONUtil.put(
			"externalReferenceCode",
			itemExternalReference.getExternalReferenceCode()
		).put(
			"scopeExternalReferenceCode", scopeExternalReferenceCode
		);

		Long itemGroupId = ScopeUtil.getItemGroupId(
			CompanyThreadLocal.getCompanyId(), scopeExternalReferenceCode,
			groupId);

		if (itemGroupId == null) {
			_logOptionalReference(
				Layout.class.getName(),
				itemExternalReference.getExternalReferenceCode(), groupId,
				scopeExternalReferenceCode);

			return layoutJSONObject;
		}

		Layout layout =
			LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				itemExternalReference.getExternalReferenceCode(), itemGroupId);

		if (layout == null) {
			_logOptionalReference(
				Layout.class.getName(),
				itemExternalReference.getExternalReferenceCode(), groupId,
				scopeExternalReferenceCode);

			return layoutJSONObject;
		}

		return layoutJSONObject.put(
			"groupId", String.valueOf(layout.getGroupId())
		).put(
			"layoutId", String.valueOf(layout.getLayoutId())
		).put(
			"layoutUuid", layout.getUuid()
		).put(
			"privateLayout", layout.isPrivateLayout()
		).put(
			"title", layout.getName(LocaleUtil.getMostRelevantLocale())
		);
	}

	private static LengthFragmentConfigurationFieldDefaultValue
		_toLengthFragmentConfigurationFieldDefaultValue(String value) {

		if (value == null) {
			return null;
		}

		LengthFragmentConfigurationFieldDefaultValue
			lengthFragmentConfigurationFieldDefaultValue =
				new LengthFragmentConfigurationFieldDefaultValue();

		lengthFragmentConfigurationFieldDefaultValue.setValue(() -> value);

		return lengthFragmentConfigurationFieldDefaultValue;
	}

	private static NavigationMenuFragmentConfigurationFieldDefaultValue
		_toNavigationMenuFragmentConfigurationFieldDefaultValue(
			NavigationMenuValue navigationMenuValue) {

		if (navigationMenuValue == null) {
			return null;
		}

		return new NavigationMenuFragmentConfigurationFieldDefaultValue() {
			{
				setValue(() -> navigationMenuValue);
			}
		};
	}

	private static JSONObject _toNavigationMenuJSONObject(
			long groupId,
			NavigationMenuFragmentConfigurationFieldDefaultValue
				navigationMenuFragmentConfigurationFieldDefaultValue)
		throws PortalException {

		if (navigationMenuFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		NavigationMenuValue navigationMenuValue =
			navigationMenuFragmentConfigurationFieldDefaultValue.getValue();

		if (navigationMenuValue instanceof
				ContextualMenuNavigationMenuValue
					contextualMenuNavigationMenuValue) {

			return _toContextualMenuJSONObject(
				contextualMenuNavigationMenuValue);
		}

		if (navigationMenuValue instanceof
				SiteMenuNavigationMenuValue siteMenuNavigationMenuValue) {

			return _toSiteMenuJSONObject(groupId, siteMenuNavigationMenuValue);
		}

		if (navigationMenuValue instanceof
				SitePagesNavigationMenuValue sitePagesNavigationMenuValue) {

			return _toSitePagesJSONObject(
				groupId, sitePagesNavigationMenuValue);
		}

		return null;
	}

	private static NavigationMenuValue _toNavigationMenuValue(
		JSONObject fieldJSONObject,
		DTOConverter
			<FragmentConfigurationField, FragmentConfigurationFieldValue>
				fragmentConfigurationFieldValueDTOConverter,
		FragmentEntry fragmentEntry) {

		FragmentConfigurationFieldValue fragmentConfigurationFieldValue =
			_toFragmentConfigurationFieldValue(
				fieldJSONObject, fragmentConfigurationFieldValueDTOConverter,
				fragmentEntry);

		if (!(fragmentConfigurationFieldValue instanceof
				NavigationMenuFragmentConfigurationFieldValue
					navigationMenuFragmentConfigurationFieldValue)) {

			return null;
		}

		NavigationMenuValue navigationMenuValue =
			navigationMenuFragmentConfigurationFieldValue.getValue();

		if (!(navigationMenuValue instanceof
				SiteMenuNavigationMenuValue siteMenuNavigationMenuValue)) {

			return navigationMenuValue;
		}

		ItemExternalReference itemExternalReference =
			siteMenuNavigationMenuValue.
				getNavigationMenuItemExternalReference();

		if ((itemExternalReference == null) ||
			Validator.isNull(
				itemExternalReference.getExternalReferenceCode())) {

			return null;
		}

		return navigationMenuValue;
	}

	private static JSONObject
		_toRepeatableFieldsCollectionProviderReferenceJSONObject(
			long groupId, InfoItemServiceRegistry infoItemServiceRegistry,
			RepeatableFieldsCollectionProviderReference
				repeatableFieldsCollectionProviderReference) {

		String className =
			repeatableFieldsCollectionProviderReference.getClassName();

		if (Validator.isNull(className)) {
			return null;
		}

		ItemExternalReference subTypeExternalReference =
			repeatableFieldsCollectionProviderReference.
				getSubTypeExternalReference();

		if ((subTypeExternalReference == null) ||
			(subTypeExternalReference.getExternalReferenceCode() == null)) {

			return null;
		}

		InfoItemFormVariation infoItemFormVariation = _getInfoItemFormVariation(
			className, groupId, infoItemServiceRegistry,
			subTypeExternalReference);

		return JSONUtil.put(
			"fieldName",
			repeatableFieldsCollectionProviderReference.getFieldName()
		).put(
			"itemSubtypeKey",
			subTypeExternalReference.getExternalReferenceCode()
		).put(
			"itemType", className
		).put(
			"key", RepeatableFieldInfoItemCollectionProvider.class.getName()
		).put(
			"title",
			_getRepeatableFieldsTitle(
				className,
				repeatableFieldsCollectionProviderReference.getFieldName(),
				infoItemFormVariation, infoItemServiceRegistry,
				subTypeExternalReference)
		).put(
			"type", InfoListProviderItemSelectorReturnType.class.getName()
		);
	}

	private static SelectFragmentConfigurationFieldDefaultValue
		_toSelectFragmentConfigurationFieldDefaultValue(String value) {

		if (value == null) {
			return null;
		}

		SelectFragmentConfigurationFieldDefaultValue
			selectFragmentConfigurationFieldDefaultValue =
				new SelectFragmentConfigurationFieldDefaultValue();

		selectFragmentConfigurationFieldDefaultValue.setValue(() -> value);

		return selectFragmentConfigurationFieldDefaultValue;
	}

	private static SelectTypeOptions _toSelectTypeOptions(
		JSONObject typeOptionsJSONObject) {

		if (typeOptionsJSONObject == null) {
			return null;
		}

		return new SelectTypeOptions() {
			{
				setDependency(() -> _toDependencyMap(typeOptionsJSONObject));
				setValidValues(
					() -> {
						JSONArray validValuesJSONArray =
							typeOptionsJSONObject.getJSONArray("validValues");

						if (validValuesJSONArray == null) {
							return null;
						}

						return JSONUtil.toArray(
							validValuesJSONArray,
							validValueJSONObject -> _toValidValue(
								validValueJSONObject),
							ValidValue.class);
					});
			}
		};
	}

	private static JSONObject _toSiteMenuJSONObject(
			long groupId,
			SiteMenuNavigationMenuValue siteMenuNavigationMenuValue)
		throws PortalException {

		ItemExternalReference itemExternalReference =
			siteMenuNavigationMenuValue.
				getNavigationMenuItemExternalReference();

		if (itemExternalReference == null) {
			return null;
		}

		String parentMenuItemExternalReferenceCode =
			siteMenuNavigationMenuValue.
				getParentMenuItemExternalReferenceCode();
		String scopeExternalReferenceCode =
			ScopeUtil.getItemScopeExternalReferenceCode(
				_getScopeExternalReferenceCode(
					itemExternalReference.getScope()),
				groupId);

		JSONObject siteMenuJSONObject = JSONUtil.put(
			"parentSiteNavigationMenuItemExternalReferenceCode",
			parentMenuItemExternalReferenceCode
		).put(
			"siteNavigationMenuExternalReferenceCode",
			itemExternalReference.getExternalReferenceCode()
		).put(
			"siteNavigationMenuScopeExternalReferenceCode",
			scopeExternalReferenceCode
		);

		Long itemGroupId = ScopeUtil.getItemGroupId(
			CompanyThreadLocal.getCompanyId(), scopeExternalReferenceCode,
			groupId);

		if (itemGroupId == null) {
			_logOptionalReference(
				SiteNavigationMenu.class.getName(),
				itemExternalReference.getExternalReferenceCode(), groupId,
				scopeExternalReferenceCode);

			return siteMenuJSONObject;
		}

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuLocalServiceUtil.
				fetchSiteNavigationMenuByExternalReferenceCode(
					itemExternalReference.getExternalReferenceCode(),
					itemGroupId);

		if (siteNavigationMenu == null) {
			_logOptionalReference(
				SiteNavigationMenu.class.getName(),
				itemExternalReference.getExternalReferenceCode(), groupId,
				scopeExternalReferenceCode);

			return siteMenuJSONObject;
		}

		siteMenuJSONObject.put(
			"siteNavigationMenuId",
			String.valueOf(siteNavigationMenu.getSiteNavigationMenuId())
		).put(
			"title", siteNavigationMenu.getName()
		);

		if (Validator.isNull(parentMenuItemExternalReferenceCode)) {
			return siteMenuJSONObject;
		}

		SiteNavigationMenuItem siteNavigationMenuItem =
			SiteNavigationMenuItemLocalServiceUtil.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					parentMenuItemExternalReferenceCode, itemGroupId);

		if (siteNavigationMenuItem == null) {
			_logOptionalReference(
				SiteNavigationMenuItem.class.getName(),
				parentMenuItemExternalReferenceCode, groupId,
				scopeExternalReferenceCode);

			return siteMenuJSONObject;
		}

		siteMenuJSONObject.put(
			"parentSiteNavigationMenuItemId",
			String.valueOf(
				siteNavigationMenuItem.getSiteNavigationMenuItemId()));

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			SiteNavigationMenuItemTypeRegistryUtil.
				getSiteNavigationMenuItemType(siteNavigationMenuItem);

		if (siteNavigationMenuItemType == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No site navigation menu item type is registered for " +
						siteNavigationMenuItem.getType());
			}

			return siteMenuJSONObject;
		}

		return siteMenuJSONObject.put(
			"title",
			siteNavigationMenuItemType.getTitle(
				siteNavigationMenuItem, LocaleUtil.getMostRelevantLocale()));
	}

	private static JSONObject _toSitePagesJSONObject(
			long groupId,
			SitePagesNavigationMenuValue sitePagesNavigationMenuValue)
		throws PortalException {

		String parentSitePageExternalReferenceCode =
			sitePagesNavigationMenuValue.
				getParentSitePageExternalReferenceCode();

		boolean privateLayout = Objects.equals(
			sitePagesNavigationMenuValue.getPageSetType(),
			SitePagesNavigationMenuValue.PageSetType.PRIVATE_PAGES);

		JSONObject sitePagesJSONObject = JSONUtil.put(
			"parentSiteNavigationMenuItemExternalReferenceCode",
			parentSitePageExternalReferenceCode
		).put(
			"privateLayout", privateLayout
		);

		if (Validator.isNull(parentSitePageExternalReferenceCode)) {
			return sitePagesJSONObject.put(
				"title", _getSitePagesTitle(groupId, privateLayout));
		}

		Layout layout =
			LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				parentSitePageExternalReferenceCode, groupId);

		if (layout == null) {
			_logOptionalReference(
				Layout.class.getName(), parentSitePageExternalReferenceCode,
				groupId, null);

			return sitePagesJSONObject.put(
				"title", _getSitePagesTitle(groupId, privateLayout));
		}

		return sitePagesJSONObject.put(
			"parentSiteNavigationMenuItemId", String.valueOf(layout.getPlid())
		).put(
			"title", layout.getName(LocaleUtil.getMostRelevantLocale())
		);
	}

	private static String _toString(
		ColorPickerFragmentConfigurationFieldDefaultValue
			colorPickerFragmentConfigurationFieldDefaultValue) {

		if (colorPickerFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		return colorPickerFragmentConfigurationFieldDefaultValue.getValue();
	}

	private static String _toString(
		LengthFragmentConfigurationFieldDefaultValue
			lengthFragmentConfigurationFieldDefaultValue) {

		if (lengthFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		return lengthFragmentConfigurationFieldDefaultValue.getValue();
	}

	private static String _toString(
		SelectFragmentConfigurationFieldDefaultValue
			selectFragmentConfigurationFieldDefaultValue) {

		if (selectFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		return selectFragmentConfigurationFieldDefaultValue.getValue();
	}

	private static String _toString(
		TextFragmentConfigurationFieldDefaultValue
			textFragmentConfigurationFieldDefaultValue) {

		if (textFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		return textFragmentConfigurationFieldDefaultValue.getValue();
	}

	private static TargetCollectionDisplayFragmentConfigurationFieldDefaultValue
		_toTargetCollectionDisplayFragmentConfigurationFieldDefaultValue(
			JSONArray jsonArray) {

		if (jsonArray == null) {
			return null;
		}

		return new TargetCollectionDisplayFragmentConfigurationFieldDefaultValue() {
			{
				setValue(() -> JSONUtil.toStringArray(jsonArray));
			}
		};
	}

	private static TargetCollectionDisplayTypeOptions
		_toTargetCollectionDisplayTypeOptions(
			JSONObject typeOptionsJSONObject) {

		if (typeOptionsJSONObject == null) {
			return null;
		}

		return new TargetCollectionDisplayTypeOptions() {
			{
				setDependency(() -> _toDependencyMap(typeOptionsJSONObject));
				setEnableCompatibleCollections(
					() -> {
						if (!typeOptionsJSONObject.has(
								"enableCompatibleCollections")) {

							return null;
						}

						return typeOptionsJSONObject.getBoolean(
							"enableCompatibleCollections");
					});
			}
		};
	}

	private static TextFragmentConfigurationFieldDefaultValue
		_toTextFragmentConfigurationFieldDefaultValue(String value) {

		if (value == null) {
			return null;
		}

		TextFragmentConfigurationFieldDefaultValue
			textFragmentConfigurationFieldDefaultValue =
				new TextFragmentConfigurationFieldDefaultValue();

		textFragmentConfigurationFieldDefaultValue.setValue(() -> value);

		return textFragmentConfigurationFieldDefaultValue;
	}

	private static TextTypeOptions _toTextTypeOptions(
		JSONObject typeOptionsJSONObject) {

		if (typeOptionsJSONObject == null) {
			return null;
		}

		return new TextTypeOptions() {
			{
				setDependency(() -> _toDependencyMap(typeOptionsJSONObject));
				setPlaceholder(
					() -> typeOptionsJSONObject.getString("placeholder", null));
				setValidation(
					() -> _toValidation(
						typeOptionsJSONObject.getJSONObject("validation")));
			}
		};
	}

	private static TypeOptions _toTypeOptions(
		JSONObject typeOptionsJSONObject) {

		if (typeOptionsJSONObject == null) {
			return null;
		}

		Map<String, Dependency> dependencyMap = _toDependencyMap(
			typeOptionsJSONObject);

		if (dependencyMap == null) {
			return null;
		}

		return new TypeOptions() {
			{
				setDependency(() -> dependencyMap);
			}
		};
	}

	private static JSONObject _toTypeOptionsJSONObject(
		CheckboxTypeOptions checkboxTypeOptions) {

		if (checkboxTypeOptions == null) {
			return null;
		}

		JSONObject typeOptionsJSONObject = JSONUtil.put(
			"dependency",
			_toDependencyJSONObject(checkboxTypeOptions.getDependency()));

		if (checkboxTypeOptions.getDisplayType() != null) {
			typeOptionsJSONObject.put(
				"displayType",
				String.valueOf(checkboxTypeOptions.getDisplayType()));
		}

		if (typeOptionsJSONObject.length() == 0) {
			return null;
		}

		return typeOptionsJSONObject;
	}

	private static JSONObject _toTypeOptionsJSONObject(
		CollectionSelectorTypeOptions collectionSelectorTypeOptions) {

		if (collectionSelectorTypeOptions == null) {
			return null;
		}

		JSONObject typeOptionsJSONObject = JSONUtil.put(
			"dependency",
			_toDependencyJSONObject(
				collectionSelectorTypeOptions.getDependency())
		).put(
			"itemSubtype", collectionSelectorTypeOptions.getItemSubtype()
		).put(
			"itemType", collectionSelectorTypeOptions.getItemType()
		).put(
			"numberOfItems", collectionSelectorTypeOptions.getNumberOfItems()
		);

		if (typeOptionsJSONObject.length() == 0) {
			return null;
		}

		return typeOptionsJSONObject;
	}

	private static JSONObject _toTypeOptionsJSONObject(
		ItemSelectorTypeOptions itemSelectorTypeOptions) {

		if (itemSelectorTypeOptions == null) {
			return null;
		}

		JSONObject typeOptionsJSONObject = JSONUtil.put(
			"className", itemSelectorTypeOptions.getClassName()
		).put(
			"dependency",
			_toDependencyJSONObject(itemSelectorTypeOptions.getDependency())
		).put(
			"enableSelectTemplate",
			itemSelectorTypeOptions.getEnableSelectTemplate()
		).put(
			"itemSubtype", itemSelectorTypeOptions.getItemSubtype()
		).put(
			"itemType", itemSelectorTypeOptions.getItemType()
		);

		String[] mimeTypes = itemSelectorTypeOptions.getMimeTypes();

		if (ArrayUtil.isNotEmpty(mimeTypes)) {
			typeOptionsJSONObject.put(
				"mimeTypes", JSONFactoryUtil.createJSONArray(mimeTypes));
		}

		if (typeOptionsJSONObject.length() == 0) {
			return null;
		}

		return typeOptionsJSONObject;
	}

	private static JSONObject _toTypeOptionsJSONObject(
		SelectTypeOptions selectTypeOptions) {

		if (selectTypeOptions == null) {
			return null;
		}

		JSONObject typeOptionsJSONObject = JSONUtil.put(
			"dependency",
			_toDependencyJSONObject(selectTypeOptions.getDependency()));

		ValidValue[] validValues = selectTypeOptions.getValidValues();

		if (ArrayUtil.isNotEmpty(validValues)) {
			typeOptionsJSONObject.put(
				"validValues",
				JSONUtil.toJSONArray(
					validValues,
					validValue -> _toValidValueJSONObject(validValue), _log));
		}

		if (typeOptionsJSONObject.length() == 0) {
			return null;
		}

		return typeOptionsJSONObject;
	}

	private static JSONObject _toTypeOptionsJSONObject(
		TargetCollectionDisplayTypeOptions targetCollectionDisplayTypeOptions) {

		if (targetCollectionDisplayTypeOptions == null) {
			return null;
		}

		JSONObject typeOptionsJSONObject = JSONUtil.put(
			"dependency",
			_toDependencyJSONObject(
				targetCollectionDisplayTypeOptions.getDependency())
		).put(
			"enableCompatibleCollections",
			targetCollectionDisplayTypeOptions.getEnableCompatibleCollections()
		);

		if (typeOptionsJSONObject.length() == 0) {
			return null;
		}

		return typeOptionsJSONObject;
	}

	private static JSONObject _toTypeOptionsJSONObject(
		TextTypeOptions textTypeOptions) {

		if (textTypeOptions == null) {
			return null;
		}

		JSONObject typeOptionsJSONObject = JSONUtil.put(
			"dependency",
			_toDependencyJSONObject(textTypeOptions.getDependency())
		).put(
			"placeholder", textTypeOptions.getPlaceholder()
		).put(
			"validation",
			_toValidationJSONObject(textTypeOptions.getValidation())
		);

		if (typeOptionsJSONObject.length() == 0) {
			return null;
		}

		return typeOptionsJSONObject;
	}

	private static JSONObject _toTypeOptionsJSONObject(
		TypeOptions typeOptions) {

		if (typeOptions == null) {
			return null;
		}

		JSONObject dependencyJSONObject = _toDependencyJSONObject(
			typeOptions.getDependency());

		if (dependencyJSONObject == null) {
			return null;
		}

		return JSONUtil.put("dependency", dependencyJSONObject);
	}

	private static URLFragmentConfigurationFieldDefaultValue
		_toURLFragmentConfigurationFieldDefaultValue(URLValue urlValue) {

		if (urlValue == null) {
			return null;
		}

		return new URLFragmentConfigurationFieldDefaultValue() {
			{
				setValue(() -> urlValue);
			}
		};
	}

	private static JSONObject _toURLJSONObject(
			long groupId,
			URLFragmentConfigurationFieldDefaultValue
				urlFragmentConfigurationFieldDefaultValue)
		throws PortalException {

		if (urlFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		URLValue urlValue =
			urlFragmentConfigurationFieldDefaultValue.getValue();

		if (urlValue instanceof HrefURLValue hrefURLValue) {
			return JSONUtil.put("href", hrefURLValue.getHref());
		}

		if (!(urlValue instanceof SitePageURLValue sitePageURLValue) ||
			(sitePageURLValue.getSitePageItemExternalReference() == null)) {

			return null;
		}

		return JSONUtil.put(
			"layout",
			_toLayoutJSONObject(
				groupId, sitePageURLValue.getSitePageItemExternalReference()));
	}

	private static URLValue _toURLValue(
		JSONObject fieldJSONObject,
		DTOConverter
			<FragmentConfigurationField, FragmentConfigurationFieldValue>
				fragmentConfigurationFieldValueDTOConverter,
		FragmentEntry fragmentEntry) {

		FragmentConfigurationFieldValue fragmentConfigurationFieldValue =
			_toFragmentConfigurationFieldValue(
				fieldJSONObject, fragmentConfigurationFieldValueDTOConverter,
				fragmentEntry);

		if (!(fragmentConfigurationFieldValue instanceof
				URLFragmentConfigurationFieldValue
					urlFragmentConfigurationFieldValue)) {

			return null;
		}

		URLValue urlValue = urlFragmentConfigurationFieldValue.getValue();

		if (!(urlValue instanceof SitePageURLValue sitePageURLValue)) {
			return urlValue;
		}

		ItemExternalReference itemExternalReference =
			sitePageURLValue.getSitePageItemExternalReference();

		if ((itemExternalReference == null) ||
			Validator.isNull(
				itemExternalReference.getExternalReferenceCode())) {

			return null;
		}

		return urlValue;
	}

	private static ValidValue _toValidValue(JSONObject validValueJSONObject) {
		if (validValueJSONObject == null) {
			return null;
		}

		return new ValidValue() {
			{
				setLabel(() -> validValueJSONObject.getString("label", null));
				setValue(() -> validValueJSONObject.getString("value", null));
			}
		};
	}

	private static JSONObject _toValidValueJSONObject(ValidValue validValue) {
		return JSONUtil.put(
			"label", validValue.getLabel()
		).put(
			"value", validValue.getValue()
		);
	}

	private static Validation _toValidation(JSONObject validationJSONObject) {
		if (validationJSONObject == null) {
			return null;
		}

		String type = validationJSONObject.getString("type");

		Validation validation = null;

		if (Objects.equals(type, "number")) {
			validation = new NumberValidation() {
				{
					setMax(
						() -> {
							if (!validationJSONObject.has("max")) {
								return null;
							}

							return validationJSONObject.getLong("max");
						});
					setMin(
						() -> {
							if (!validationJSONObject.has("min")) {
								return null;
							}

							return validationJSONObject.getLong("min");
						});
				}
			};
		}
		else if (Objects.equals(type, "pattern")) {
			validation = new PatternValidation() {
				{
					setRegexp(
						() -> validationJSONObject.getString("regexp", null));
				}
			};
		}
		else if (Objects.equals(type, "email") ||
				 Objects.equals(type, "text") || Objects.equals(type, "url")) {

			validation = new TextValidation() {
				{
					setMaxLength(
						() -> {
							if (!validationJSONObject.has("maxLength")) {
								return null;
							}

							return validationJSONObject.getLong("maxLength");
						});
					setMinLength(
						() -> {
							if (!validationJSONObject.has("minLength")) {
								return null;
							}

							return validationJSONObject.getLong("minLength");
						});
				}
			};
		}

		if (validation == null) {
			return null;
		}

		validation.setErrorMessage(
			() -> validationJSONObject.getString("errorMessage", null));
		validation.setRequired(
			() -> {
				if (!validationJSONObject.has("required")) {
					return null;
				}

				return validationJSONObject.getBoolean("required");
			});
		validation.setType(() -> Validation.Type.create(type));

		return validation;
	}

	private static JSONObject _toValidationJSONObject(Validation validation) {
		if (validation == null) {
			return null;
		}

		JSONObject validationJSONObject = JSONUtil.put(
			"errorMessage", validation.getErrorMessage()
		).put(
			"required", validation.getRequired()
		).put(
			"type", validation.getTypeAsString()
		);

		if (validation instanceof NumberValidation numberValidation) {
			return validationJSONObject.put(
				"max", numberValidation.getMax()
			).put(
				"min", numberValidation.getMin()
			);
		}

		if (validation instanceof PatternValidation patternValidation) {
			return validationJSONObject.put(
				"regexp", patternValidation.getRegexp());
		}

		if (validation instanceof TextValidation textValidation) {
			return validationJSONObject.put(
				"maxLength", textValidation.getMaxLength()
			).put(
				"minLength", textValidation.getMinLength()
			);
		}

		return validationJSONObject;
	}

	private static VideoFragmentConfigurationFieldDefaultValue
		_toVideoFragmentConfigurationFieldDefaultValue(VideoValue videoValue) {

		if (videoValue == null) {
			return null;
		}

		return new VideoFragmentConfigurationFieldDefaultValue() {
			{
				setValue(() -> videoValue);
			}
		};
	}

	private static JSONObject _toVideoJSONObject(
		VideoFragmentConfigurationFieldDefaultValue
			videoFragmentConfigurationFieldDefaultValue) {

		if (videoFragmentConfigurationFieldDefaultValue == null) {
			return null;
		}

		VideoValue videoValue =
			videoFragmentConfigurationFieldDefaultValue.getValue();

		if ((videoValue == null) || Validator.isNull(videoValue.getHtml())) {
			return null;
		}

		return JSONUtil.put(
			"html", videoValue.getHtml()
		).put(
			"title", videoValue.getTitle()
		).put(
			"type", VideoEmbeddableHTMLItemSelectorReturnType.class.getName()
		);
	}

	private static VideoValue _toVideoValue(JSONObject defaultValueJSONObject) {
		if ((defaultValueJSONObject == null) ||
			(!defaultValueJSONObject.has("html") &&
			 !defaultValueJSONObject.has("title"))) {

			return null;
		}

		return new VideoValue() {
			{
				setHtml(() -> defaultValueJSONObject.getString("html", null));
				setTitle(() -> defaultValueJSONObject.getString("title", null));
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationUtil.class);

}