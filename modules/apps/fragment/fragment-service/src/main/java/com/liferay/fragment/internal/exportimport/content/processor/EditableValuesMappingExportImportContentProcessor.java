/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.exportimport.content.processor;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;

import java.util.Iterator;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "content.processor.type=FragmentEntryLinkEditableValues",
	service = ExportImportContentProcessor.class
)
public class EditableValuesMappingExportImportContentProcessor
	implements ExportImportContentProcessor<JSONObject> {

	@Override
	public JSONObject replaceExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			JSONObject editableValuesJSONObject,
			boolean exportReferencedContent, boolean escapeContent)
		throws Exception {

		_replaceAllEditableExportContentReferences(
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR),
			exportReferencedContent, portletDataContext, stagedModel);

		_replaceAllEditableExportContentReferences(
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR),
			exportReferencedContent, portletDataContext, stagedModel);

		return editableValuesJSONObject;
	}

	@Override
	public JSONObject replaceImportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			JSONObject editableValuesJSONObject)
		throws Exception {

		_replaceAllEditableImportContentReferences(
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR),
			portletDataContext);

		_replaceAllEditableImportContentReferences(
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR),
			portletDataContext);

		return editableValuesJSONObject;
	}

	@Override
	public void validateContentReferences(long groupId, JSONObject jsonObject) {
	}

	private void _exportAssetVocabularyReference(
			String mappedField, PortletDataContext portletDataContext,
			StagedModel stagedModel)
		throws Exception {

		long assetVocabularyId = GetterUtil.getLong(
			mappedField.substring(
				_EDITABLE_VALUE_PREFIX_ASSET_VOCABULARY.length()));

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(
				assetVocabularyId);

		if (assetVocabulary != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, stagedModel, assetVocabulary,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _exportLayoutPageTemplateEntryReference(
			String mappedField, PortletDataContext portletDataContext,
			StagedModel stagedModel)
		throws Exception {

		long layoutPageTemplateEntryId = GetterUtil.getLong(
			mappedField.substring(
				_EDITABLE_VALUE_PREFIX_LAYOUT_PAGE_TEMPLATE_ENTRY.length()));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntryId);

		if (layoutPageTemplateEntry != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, stagedModel, layoutPageTemplateEntry,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _exportTemplateReference(
			String mappedField, PortletDataContext portletDataContext,
			StagedModel referrerStagedModel)
		throws Exception {

		StagedModel stagedModel;

		if (mappedField.startsWith(_EDITABLE_VALUE_PREFIX_TEMPLATE)) {
			stagedModel = _templateEntryLocalService.fetchTemplateEntry(
				GetterUtil.getLong(
					mappedField.substring(
						_EDITABLE_VALUE_PREFIX_TEMPLATE.length())));
		}
		else {
			stagedModel = _ddmTemplateLocalService.fetchTemplate(
				portletDataContext.getScopeGroupId(),
				_portal.getClassNameId(DDMStructure.class),
				mappedField.substring(
					PortletDisplayTemplate.DISPLAY_STYLE_PREFIX.length()));
		}

		if (stagedModel != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, referrerStagedModel, stagedModel,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private String _getTemplateEditableFieldValue(
		String mappedField, PortletDataContext portletDataContext) {

		if (mappedField.startsWith(_EDITABLE_VALUE_PREFIX_TEMPLATE)) {
			long templateEntryId = GetterUtil.getLong(
				mappedField.substring(
					_EDITABLE_VALUE_PREFIX_TEMPLATE.length()));

			Map<Long, Long> templateEntryIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					TemplateEntry.class);

			long importedTemplateEntryId = MapUtil.getLong(
				templateEntryIds, templateEntryId, templateEntryId);

			return _EDITABLE_VALUE_PREFIX_TEMPLATE + importedTemplateEntryId;
		}

		String ddmTemplateKey = mappedField.substring(
			PortletDisplayTemplate.DISPLAY_STYLE_PREFIX.length());

		Map<String, String> ddmTemplateKeys =
			(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
				DDMTemplate.class + ".ddmTemplateKey");

		String importedDDMTemplateKey = MapUtil.getString(
			ddmTemplateKeys, ddmTemplateKey, ddmTemplateKey);

		return PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
			importedDDMTemplateKey;
	}

	private void _replaceAllEditableExportContentReferences(
			JSONObject editableValuesJSONObject,
			boolean exportReferencedContent,
			PortletDataContext portletDataContext, StagedModel stagedModel)
		throws Exception {

		if ((editableValuesJSONObject == null) ||
			(editableValuesJSONObject.length() <= 0)) {

			return;
		}

		_replaceMappedFieldExportContentReferences(
			portletDataContext, stagedModel, editableValuesJSONObject,
			exportReferencedContent);

		Iterator<String> editableKeysIterator = editableValuesJSONObject.keys();

		while (editableKeysIterator.hasNext()) {
			String editableKey = editableKeysIterator.next();

			JSONObject editableJSONObject =
				editableValuesJSONObject.getJSONObject(editableKey);

			_replaceAllEditableExportContentReferences(
				editableJSONObject, exportReferencedContent, portletDataContext,
				stagedModel);
		}
	}

	private void _replaceAllEditableImportContentReferences(
		JSONObject editableValuesJSONObject,
		PortletDataContext portletDataContext) {

		if ((editableValuesJSONObject == null) ||
			(editableValuesJSONObject.length() <= 0)) {

			return;
		}

		_replaceMappedFieldImportContentReferences(
			portletDataContext, editableValuesJSONObject);

		Iterator<String> editableKeysIterator = editableValuesJSONObject.keys();

		while (editableKeysIterator.hasNext()) {
			String editableKey = editableKeysIterator.next();

			JSONObject editableJSONObject =
				editableValuesJSONObject.getJSONObject(editableKey);

			_replaceAllEditableImportContentReferences(
				editableJSONObject, portletDataContext);
		}
	}

	private void _replaceMappedFieldExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			JSONObject editableJSONObject, boolean exportReferencedContent)
		throws Exception {

		long classNameId = editableJSONObject.getLong("classNameId");
		long classPK = editableJSONObject.getLong("classPK");
		String collectionFieldId = editableJSONObject.getString(
			"collectionFieldId", null);
		String mappedField = editableJSONObject.getString("mappedField", null);

		if (((classNameId == 0) || (classPK == 0)) &&
			Validator.isNull(collectionFieldId) &&
			Validator.isNull(mappedField)) {

			return;
		}

		mappedField = GetterUtil.getString(
			collectionFieldId,
			GetterUtil.getString(
				mappedField, editableJSONObject.getString("fieldId")));

		if (mappedField.startsWith(_EDITABLE_VALUE_PREFIX_ASSET_VOCABULARY)) {
			_exportAssetVocabularyReference(
				mappedField, portletDataContext, stagedModel);
		}
		else if (mappedField.startsWith(
					PortletDisplayTemplate.DISPLAY_STYLE_PREFIX)) {

			_exportTemplateReference(
				mappedField, portletDataContext, stagedModel);
		}
		else if (mappedField.startsWith(
					_EDITABLE_VALUE_PREFIX_LAYOUT_PAGE_TEMPLATE_ENTRY)) {

			_exportLayoutPageTemplateEntryReference(
				mappedField, portletDataContext, stagedModel);
		}

		if ((classNameId == 0) || (classPK == 0)) {
			return;
		}

		String className = _portal.fetchClassName(classNameId);

		editableJSONObject.put("className", className);

		ExportImportContentProcessorUtil.exportContentReference(
			className, classPK, exportReferencedContent,
			_infoItemServiceRegistry, portletDataContext, stagedModel);
	}

	private void _replaceMappedFieldImportContentReferences(
		PortletDataContext portletDataContext, JSONObject editableJSONObject) {

		String key = "fieldId";

		if (editableJSONObject.has("collectionFieldId")) {
			key = "collectionFieldId";
		}
		else if (editableJSONObject.has("mappedField")) {
			key = "mappedField";
		}

		String mappedField = editableJSONObject.getString(key);

		if (mappedField.startsWith(_EDITABLE_VALUE_PREFIX_ASSET_VOCABULARY)) {
			long assetVocabularyId = GetterUtil.getLong(
				mappedField.substring(
					_EDITABLE_VALUE_PREFIX_ASSET_VOCABULARY.length()));

			Map<Long, Long> assetVocabularyIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					AssetVocabulary.class);

			long importedAssetVocabularyId = MapUtil.getLong(
				assetVocabularyIds, assetVocabularyId, assetVocabularyId);

			editableJSONObject.put(
				key,
				_EDITABLE_VALUE_PREFIX_ASSET_VOCABULARY +
					importedAssetVocabularyId);
		}
		else if (mappedField.startsWith(
					PortletDisplayTemplate.DISPLAY_STYLE_PREFIX)) {

			editableJSONObject.put(
				key,
				_getTemplateEditableFieldValue(
					mappedField, portletDataContext));
		}
		else if (mappedField.startsWith(
					_EDITABLE_VALUE_PREFIX_LAYOUT_PAGE_TEMPLATE_ENTRY)) {

			long layoutPageTemplateEntryId = GetterUtil.getLong(
				mappedField.substring(
					_EDITABLE_VALUE_PREFIX_LAYOUT_PAGE_TEMPLATE_ENTRY.
						length()));

			Map<Long, Long> layoutPageTemplateEntryIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					LayoutPageTemplateEntry.class);

			long importedLayoutPageTemplateEntryId = MapUtil.getLong(
				layoutPageTemplateEntryIds, layoutPageTemplateEntryId,
				layoutPageTemplateEntryId);

			editableJSONObject.put(
				key,
				_EDITABLE_VALUE_PREFIX_LAYOUT_PAGE_TEMPLATE_ENTRY +
					importedLayoutPageTemplateEntryId);
		}

		ExportImportContentProcessorUtil.replaceImportContentReferences(
			editableJSONObject, portletDataContext);

		if (editableJSONObject.has("fileEntryId")) {
			editableJSONObject.put(
				"fileEntryId", editableJSONObject.getLong("classPK"));
		}
	}

	private static final String _EDITABLE_VALUE_PREFIX_ASSET_VOCABULARY =
		AssetVocabulary.class.getSimpleName() + StringPool.UNDERLINE;

	private static final String
		_EDITABLE_VALUE_PREFIX_LAYOUT_PAGE_TEMPLATE_ENTRY =
			LayoutPageTemplateEntry.class.getSimpleName() +
				StringPool.UNDERLINE;

	private static final String _EDITABLE_VALUE_PREFIX_TEMPLATE =
		PortletDisplayTemplate.DISPLAY_STYLE_PREFIX + StringPool.UNDERLINE +
			PortletDisplayTemplate.DISPLAY_STYLE_PREFIX;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private TemplateEntryLocalService _templateEntryLocalService;

}