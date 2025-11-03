/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.headless.admin.site.dto.v1_0.CategoryFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.CheckboxFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.FragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.LengthFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.Scope;
import com.liferay.headless.admin.site.dto.v1_0.SelectFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.TextFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentConfigurationFieldValueTypeUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.LocalizedValueUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "dto.class.name=com.liferay.fragment.util.configuration.FragmentConfigurationField",
	service = DTOConverter.class
)
public class FragmentConfigurationFieldValueDTOConverter
	implements DTOConverter
		<FragmentConfigurationField, FragmentConfigurationFieldValue> {

	@Override
	public String getContentType() {
		return FragmentConfigurationFieldValue.class.getSimpleName();
	}

	@Override
	public FragmentConfigurationFieldValue toDTO(
			DTOConverterContext dtoConverterContext,
			FragmentConfigurationField fragmentConfigurationField)
		throws Exception {

		if (dtoConverterContext == null) {
			return null;
		}

		Object fragmentFragmentConfigurationFieldValue =
			dtoConverterContext.getAttribute(
				"fragmentFragmentConfigurationFieldValue");

		if (fragmentFragmentConfigurationFieldValue == null) {
			return null;
		}

		FragmentConfigurationFieldValue.Type type =
			FragmentConfigurationFieldValueTypeUtil.toExternalType(
				fragmentConfigurationField.getType());

		if (Objects.equals(
				type, FragmentConfigurationFieldValue.Type.CATEGORY)) {

			return _getCategoryFragmentConfigurationFieldValue(
				dtoConverterContext, fragmentConfigurationField,
				(JSONObject)fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(
				type, FragmentConfigurationFieldValue.Type.CHECKBOX)) {

			return _getCheckboxFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(type, FragmentConfigurationFieldValue.Type.LENGTH)) {
			return _getLengthFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(type, FragmentConfigurationFieldValue.Type.SELECT)) {
			return _getSelectFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(type, FragmentConfigurationFieldValue.Type.TEXT)) {
			return _getTextFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				fragmentFragmentConfigurationFieldValue);
		}

		return null;
	}

	private FragmentConfigurationFieldValue
		_getCategoryFragmentConfigurationFieldValue(
			DTOConverterContext dtoConverterContext,
			FragmentConfigurationField fragmentConfigurationField,
			JSONObject jsonObject) {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		CategoryFragmentConfigurationFieldValue
			categoryFragmentConfigurationFieldValue =
				new CategoryFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			categoryFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(
					jsonObject,
					key -> _getCategoryTreeNodeItemExternalReference(
						companyId, jsonObject.getJSONObject(key),
						scopeGroupId)));
		}
		else {
			categoryFragmentConfigurationFieldValue.setValue(
				() -> _getCategoryTreeNodeItemExternalReference(
					companyId, jsonObject, scopeGroupId));
		}

		return categoryFragmentConfigurationFieldValue;
	}

	private ItemExternalReference _getCategoryTreeNodeItemExternalReference(
			long companyId, JSONObject jsonObject, long scopeGroupId)
		throws Exception {

		if (JSONUtil.isEmpty(jsonObject)) {
			return null;
		}

		long categoryTreeNodeId = jsonObject.getLong("categoryTreeNodeId");
		String externalReferenceCode = jsonObject.getString(
			"externalReferenceCode");
		String type = jsonObject.getString("categoryTreeNodeType");

		if (((categoryTreeNodeId == 0) &&
			 Validator.isNull(externalReferenceCode)) ||
			(!Objects.equals(type, "Category") &&
			 !Objects.equals(type, "Vocabulary"))) {

			return null;
		}

		if (categoryTreeNodeId == 0) {
			String className = AssetCategory.class.getName();

			if (Objects.equals(type, "Vocabulary")) {
				className = AssetVocabulary.class.getName();
			}

			return _getItemExternalReference(
				className, externalReferenceCode,
				ItemScopeUtil.getItemScope(
					companyId,
					jsonObject.getString("scopeExternalReferenceCode"),
					scopeGroupId));
		}

		if (Objects.equals(type, "Category")) {
			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchAssetCategory(
					categoryTreeNodeId);

			if (assetCategory == null) {
				return _getItemExternalReference(
					AssetCategory.class.getName(), externalReferenceCode,
					ItemScopeUtil.getItemScope(
						companyId,
						jsonObject.getString("scopeExternalReferenceCode"),
						scopeGroupId));
			}

			return _getItemExternalReference(
				AssetCategory.class.getName(),
				assetCategory.getExternalReferenceCode(),
				ItemScopeUtil.getItemScope(
					assetCategory.getGroupId(), scopeGroupId));
		}

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(
				categoryTreeNodeId);

		if (assetVocabulary == null) {
			return _getItemExternalReference(
				AssetVocabulary.class.getName(), externalReferenceCode,
				ItemScopeUtil.getItemScope(
					companyId,
					jsonObject.getString("scopeExternalReferenceCode"),
					scopeGroupId));
		}

		return _getItemExternalReference(
			AssetVocabulary.class.getName(),
			assetVocabulary.getExternalReferenceCode(),
			ItemScopeUtil.getItemScope(
				assetVocabulary.getGroupId(), scopeGroupId));
	}

	private FragmentConfigurationFieldValue
		_getCheckboxFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			Object fragmentFragmentConfigurationFieldValue) {

		CheckboxFragmentConfigurationFieldValue
			checkboxFragmentConfigurationFieldValue =
				new CheckboxFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			JSONObject jsonObject =
				(JSONObject)fragmentFragmentConfigurationFieldValue;

			checkboxFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(
					jsonObject, key -> jsonObject.getBoolean(key)));
		}
		else {
			checkboxFragmentConfigurationFieldValue.setValue(
				() -> GetterUtil.getBoolean(
					fragmentFragmentConfigurationFieldValue));
		}

		return checkboxFragmentConfigurationFieldValue;
	}

	private ItemExternalReference _getItemExternalReference(
		String className, String externalReferenceCode, Scope scope) {

		ItemExternalReference itemExternalReference =
			new ItemExternalReference();

		itemExternalReference.setClassName(() -> className);
		itemExternalReference.setExternalReferenceCode(
			() -> externalReferenceCode);
		itemExternalReference.setScope(() -> scope);

		return itemExternalReference;
	}

	private FragmentConfigurationFieldValue
		_getLengthFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			Object fragmentFragmentConfigurationFieldValue) {

		LengthFragmentConfigurationFieldValue
			lengthFragmentConfigurationFieldValue =
				new LengthFragmentConfigurationFieldValue() {
					{
						setType(Type.LENGTH);
					}
				};

		if (fragmentConfigurationField.isLocalizable()) {
			JSONObject jsonObject =
				(JSONObject)fragmentFragmentConfigurationFieldValue;

			lengthFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(jsonObject));
		}
		else {
			lengthFragmentConfigurationFieldValue.setValue(
				() -> GetterUtil.getString(
					fragmentFragmentConfigurationFieldValue));
		}

		return lengthFragmentConfigurationFieldValue;
	}

	private FragmentConfigurationFieldValue
		_getSelectFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			Object fragmentFragmentConfigurationFieldValue) {

		SelectFragmentConfigurationFieldValue
			selectFragmentConfigurationFieldValue =
				new SelectFragmentConfigurationFieldValue() {
					{
						setType(Type.SELECT);
					}
				};

		if (fragmentConfigurationField.isLocalizable()) {
			JSONObject jsonObject =
				(JSONObject)fragmentFragmentConfigurationFieldValue;

			selectFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(jsonObject));
		}
		else {
			selectFragmentConfigurationFieldValue.setValue(
				() -> GetterUtil.getString(
					fragmentFragmentConfigurationFieldValue));
		}

		return selectFragmentConfigurationFieldValue;
	}

	private FragmentConfigurationFieldValue
		_getTextFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			Object fragmentFragmentConfigurationFieldValue) {

		TextFragmentConfigurationFieldValue
			textFragmentConfigurationFieldValue =
				new TextFragmentConfigurationFieldValue() {
					{
						setType(Type.TEXT);
					}
				};

		if (fragmentConfigurationField.isLocalizable()) {
			JSONObject jsonObject =
				(JSONObject)fragmentFragmentConfigurationFieldValue;

			textFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(jsonObject));
		}
		else {
			textFragmentConfigurationFieldValue.setValue(
				() -> GetterUtil.getString(
					fragmentFragmentConfigurationFieldValue));
		}

		return textFragmentConfigurationFieldValue;
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

}