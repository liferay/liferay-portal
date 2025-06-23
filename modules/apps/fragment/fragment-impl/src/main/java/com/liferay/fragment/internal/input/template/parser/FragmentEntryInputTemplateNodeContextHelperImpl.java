/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.input.template.parser;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.constants.FragmentConfigurationFieldDataType;
import com.liferay.fragment.input.template.parser.FragmentEntryInputTemplateNodeContextHelper;
import com.liferay.fragment.input.template.parser.InputTemplateNode;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.exception.InfoFormValidationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.DateTimeInfoFieldType;
import com.liferay.info.field.type.FileInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.LongTextInfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.OptionInfoFieldType;
import com.liferay.info.field.type.PicklistMultiselectInfoFieldType;
import com.liferay.info.field.type.PicklistSelectInfoFieldType;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.CustomFileItemSelectorCriterion;
import com.liferay.layout.constants.LayoutWebKeys;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.math.BigDecimal;

import java.text.DateFormat;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentEntryInputTemplateNodeContextHelper.class)
public class FragmentEntryInputTemplateNodeContextHelperImpl
	implements FragmentEntryInputTemplateNodeContextHelper {

	@Override
	public InputTemplateNode toInputTemplateNode(
		Map<String, Serializable> attributes, String defaultInputLabel,
		FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest, InfoForm infoForm,
		Locale locale) {

		String errorMessage = StringPool.BLANK;

		InfoField infoField = null;

		if (infoForm != null) {
			String fieldName = GetterUtil.getString(
				_fragmentEntryConfigurationParser.getFieldValue(
					fragmentEntryLink.getEditableValues(),
					new FragmentConfigurationField(
						"inputFieldId", "string", "", false, "text"),
					locale));

			infoField = infoForm.getInfoField(fieldName);
		}

		if (SessionErrors.contains(
				httpServletRequest, InfoFormException.class)) {

			InfoFormException infoFormException =
				(InfoFormException)SessionErrors.get(
					httpServletRequest, InfoFormException.class);

			if ((infoField != null) &&
				(infoFormException instanceof InfoFormValidationException)) {

				InfoFormValidationException infoFormValidationException =
					(InfoFormValidationException)infoFormException;

				if (Objects.equals(
						infoField.getUniqueId(),
						infoFormValidationException.getInfoFieldUniqueId())) {

					errorMessage =
						infoFormValidationException.getLocalizedMessage(locale);

					SessionErrors.remove(
						httpServletRequest, InfoFormException.class);
				}
			}
			else if (infoFormException instanceof
						InfoFormValidationException.InvalidCaptcha) {

				InfoFormValidationException.InvalidCaptcha
					infoFormValidationExceptionInvalidCaptcha =
						(InfoFormValidationException.InvalidCaptcha)
							infoFormException;

				if (fragmentEntryLink.getFragmentEntryLinkId() ==
						infoFormValidationExceptionInvalidCaptcha.
							getFragmentEntryLinkId()) {

					errorMessage = infoFormException.getLocalizedMessage(
						locale);

					SessionErrors.remove(
						httpServletRequest, InfoFormException.class);
				}
			}
		}

		String inputHelpText = GetterUtil.getString(
			_fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getEditableValues(),
				new FragmentConfigurationField(
					"inputHelpText", "string",
					_language.get(locale, "add-your-help-text-here"), true,
					"text"),
				locale));

		if (infoField != null) {
			defaultInputLabel = infoField.getLabel(locale);
		}

		String inputLabel = _getInputLabel(
			defaultInputLabel, fragmentEntryLink.getEditableValues(), infoField,
			locale);

		boolean localizable = false;
		String name = "name";
		boolean readOnly = false;

		if (infoField != null) {
			name = infoField.getName();
			readOnly = infoField.isReadOnly();
			localizable = infoField.isLocalizable();
		}

		boolean required = false;

		if (((infoField != null) && infoField.isRequired()) ||
			GetterUtil.getBoolean(
				_fragmentEntryConfigurationParser.getFieldValue(
					fragmentEntryLink.getEditableValues(),
					new FragmentConfigurationField(
						"inputRequired", "boolean", "false", false, "checkbox"),
					locale))) {

			required = true;
		}

		boolean inputShowHelpText = GetterUtil.getBoolean(
			_fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getEditableValues(),
				new FragmentConfigurationField(
					"inputShowHelpText", "boolean", "false", false, "checkbox"),
				locale));

		boolean inputShowLabel = GetterUtil.getBoolean(
			_fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getEditableValues(),
				new FragmentConfigurationField(
					"inputShowLabel", "boolean", "true", false, "checkbox"),
				locale));

		if (infoField == null) {
			InputTemplateNode inputTemplateNode = new InputTemplateNode(
				errorMessage, inputHelpText, inputLabel, localizable, name,
				readOnly, required, inputShowHelpText, inputShowLabel, "type",
				StringPool.BLANK, Collections.emptyMap());

			for (Map.Entry<String, Serializable> entry :
					attributes.entrySet()) {

				inputTemplateNode.addAttribute(
					entry.getKey(), entry.getValue());
			}

			return inputTemplateNode;
		}

		InfoFieldType infoFieldType = infoField.getInfoFieldType();

		String label = StringPool.BLANK;
		String value = StringPool.BLANK;

		if (infoFieldType instanceof MultiselectInfoFieldType) {
			List<OptionInfoFieldType> optionInfoFieldTypes =
				(List<OptionInfoFieldType>)infoField.getAttribute(
					MultiselectInfoFieldType.OPTIONS);

			if (optionInfoFieldTypes == null) {
				optionInfoFieldTypes = Collections.emptyList();
			}

			for (OptionInfoFieldType optionInfoFieldType :
					optionInfoFieldTypes) {

				if (optionInfoFieldType.isActive()) {
					label = optionInfoFieldType.getLabel(locale);
					value = optionInfoFieldType.getValue();

					break;
				}
			}
		}
		else if (infoFieldType instanceof SelectInfoFieldType) {
			List<OptionInfoFieldType> optionInfoFieldTypes =
				(List<OptionInfoFieldType>)infoField.getAttribute(
					SelectInfoFieldType.OPTIONS);

			if (optionInfoFieldTypes == null) {
				optionInfoFieldTypes = Collections.emptyList();
			}

			for (OptionInfoFieldType optionInfoFieldType :
					optionInfoFieldTypes) {

				if (optionInfoFieldType.isActive()) {
					label = optionInfoFieldType.getLabel(locale);
					value = optionInfoFieldType.getValue();

					break;
				}
			}
		}

		Map<Locale, String> valueI18n = new HashMap<>();

		Map<String, String> infoFormParameterMap =
			(Map<String, String>)SessionMessages.get(
				httpServletRequest, "infoFormParameterMap");

		if (infoFormParameterMap != null) {
			label = String.valueOf(
				infoFormParameterMap.get(infoField.getName() + "-label"));

			Object infoParameterMapValue = infoFormParameterMap.get(
				infoField.getName());

			if (infoParameterMapValue instanceof Map) {
				Map<Locale, String> map =
					(Map<Locale, String>)infoParameterMapValue;

				value = String.valueOf(map.get(locale));
				valueI18n = map;
			}
			else {
				value = String.valueOf(infoParameterMapValue);
			}
		}
		else {
			Object infoFieldValue = _getValue(
				value, httpServletRequest, infoField, infoForm.getName(),
				locale);

			if (infoFieldValue instanceof KeyValuePair) {
				KeyValuePair keyValuePair = (KeyValuePair)infoFieldValue;

				label = keyValuePair.getValue();
				value = keyValuePair.getKey();
			}
			else if (infoFieldValue instanceof Map) {
				Map<Locale, String> map = (Map<Locale, String>)infoFieldValue;

				value = map.get(locale);
				valueI18n = map;
			}
			else {
				value = String.valueOf(infoFieldValue);
			}
		}

		InputTemplateNode inputTemplateNode = new InputTemplateNode(
			errorMessage, inputHelpText, inputLabel, localizable, name,
			readOnly, required, inputShowHelpText, inputShowLabel,
			infoFieldType.getName(), value, valueI18n);

		_addInputTemplateNodeAttributes(
			attributes, fragmentEntryLink, httpServletRequest, infoField,
			inputTemplateNode, label, locale, value, valueI18n);

		if (!localizable) {
			_addLocalizationOptionsAttributes(
				fragmentEntryLink, httpServletRequest, inputLabel,
				inputTemplateNode, locale);
		}

		return inputTemplateNode;
	}

	private void _addFileInfoFieldTypeInputTemplateNodeAttributes(
		FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest, InfoField infoField,
		InputTemplateNode inputTemplateNode, String value,
		Map<Locale, String> valueI18n) {

		String allowedFileExtensions = (String)infoField.getAttribute(
			FileInfoFieldType.ALLOWED_FILE_EXTENSIONS);

		if (allowedFileExtensions == null) {
			allowedFileExtensions = StringPool.BLANK;
		}

		if (Validator.isNotNull(allowedFileExtensions)) {
			StringBundler sb = new StringBundler();

			String[] allowedFileExtensionsArray = StringUtil.split(
				allowedFileExtensions);

			for (String allowedFileExtension : allowedFileExtensionsArray) {
				sb.append(StringPool.PERIOD);
				sb.append(allowedFileExtension.trim());
				sb.append(StringPool.COMMA);
			}

			sb.setIndex(sb.index() - 1);

			allowedFileExtensions = sb.toString();
		}

		inputTemplateNode.addAttribute(
			"allowedFileExtensions", allowedFileExtensions);

		Long maximumFileSize = (Long)infoField.getAttribute(
			FileInfoFieldType.MAX_FILE_SIZE);

		if (maximumFileSize == null) {
			maximumFileSize = 0L;
		}

		inputTemplateNode.addAttribute("maxFileSize", maximumFileSize);

		FileInfoFieldType.FileSourceType fileSourceType =
			(FileInfoFieldType.FileSourceType)infoField.getAttribute(
				FileInfoFieldType.FILE_SOURCE);

		if (fileSourceType == null) {
			return;
		}

		String fileName = _getFileName(fileSourceType, value);

		if (fileName != null) {
			inputTemplateNode.addAttribute("fileName", fileName);
		}

		Map<String, String> fileNameI18n = new HashMap<>();

		for (Map.Entry<Locale, String> entry : valueI18n.entrySet()) {
			String localizedFileName = _getFileName(
				fileSourceType, entry.getValue());

			if (localizedFileName != null) {
				fileNameI18n.put(
					_language.getLanguageId(entry.getKey()), localizedFileName);
			}
		}

		inputTemplateNode.addAttribute(
			"fileNameI18n", _jsonFactory.createJSONObject(fileNameI18n));

		boolean selectFromDocumentLibrary = false;

		if (fileSourceType ==
				FileInfoFieldType.FileSourceType.DOCUMENTS_AND_MEDIA) {

			selectFromDocumentLibrary = true;
		}

		inputTemplateNode.addAttribute(
			"selectFromDocumentLibrary", selectFromDocumentLibrary);

		if (selectFromDocumentLibrary &&
			Validator.isNotNull(allowedFileExtensions)) {

			CustomFileItemSelectorCriterion customFileItemSelectorCriterion =
				new CustomFileItemSelectorCriterion();

			customFileItemSelectorCriterion.setExtensions(
				StringUtil.split(allowedFileExtensions));
			customFileItemSelectorCriterion.setMaxFileSize(maximumFileSize);

			customFileItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				new FileEntryItemSelectorReturnType());

			inputTemplateNode.addAttribute(
				"selectFromDocumentLibraryURL",
				String.valueOf(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							httpServletRequest),
						fragmentEntryLink.getNamespace() + "selectFileEntry",
						customFileItemSelectorCriterion)));
		}
	}

	private void _addInputTemplateNodeAttributes(
		Map<String, Serializable> attributes,
		FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest, InfoField infoField,
		InputTemplateNode inputTemplateNode, String label, Locale locale,
		String value, Map<Locale, String> valueI18n) {

		if (infoField.getInfoFieldType() instanceof FileInfoFieldType) {
			_addFileInfoFieldTypeInputTemplateNodeAttributes(
				fragmentEntryLink, httpServletRequest, infoField,
				inputTemplateNode, value, valueI18n);
		}
		else if (infoField.getInfoFieldType() instanceof
					LongTextInfoFieldType) {

			_addLongTextInfoFieldTypeInputTemplateNodeAttributes(
				infoField, inputTemplateNode);
		}
		else if (infoField.getInfoFieldType() instanceof
					MultiselectInfoFieldType) {

			_addMultiselectInfoFieldTypeInputTemplateNodeAttributes(
				infoField, inputTemplateNode, locale);
		}
		else if (infoField.getInfoFieldType() instanceof NumberInfoFieldType) {
			_addNumberInfoFieldTypeInputTemplateNodeAttributes(
				infoField, inputTemplateNode);
		}
		else if (infoField.getInfoFieldType() instanceof
					RelationshipInfoFieldType) {

			_addRelationshipInfoFieldTypeInputTemplateNodeAttributes(
				infoField, inputTemplateNode, label, value);
		}
		else if (infoField.getInfoFieldType() instanceof SelectInfoFieldType) {
			_addSelectInfoFieldTypeInputTemplateNodeAttributes(
				infoField, inputTemplateNode, locale, value);
		}
		else if (infoField.getInfoFieldType() instanceof TextInfoFieldType) {
			_addTextInfoFieldTypeInputTemplateNodeAttributes(
				infoField, inputTemplateNode);
		}

		for (Map.Entry<String, Serializable> entry : attributes.entrySet()) {
			inputTemplateNode.addAttribute(entry.getKey(), entry.getValue());
		}
	}

	private void _addLocalizationOptionsAttributes(
		FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest, String inputLabel,
		InputTemplateNode inputTemplateNode, Locale locale) {

		LayoutStructure layoutStructure = null;

		if (httpServletRequest != null) {
			layoutStructure = (LayoutStructure)httpServletRequest.getAttribute(
				LayoutWebKeys.LAYOUT_STRUCTURE);
		}

		if (layoutStructure == null) {
			LayoutPageTemplateStructure layoutPageTemplateStructure =
				_layoutPageTemplateStructureLocalService.
					fetchLayoutPageTemplateStructure(
						fragmentEntryLink.getGroupId(),
						fragmentEntryLink.getPlid());

			layoutStructure = LayoutStructure.of(
				layoutPageTemplateStructure.getData(
					fragmentEntryLink.getSegmentsExperienceId()));
		}

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				fragmentEntryLink.getFragmentEntryLinkId());

		if (layoutStructureItem == null) {
			return;
		}

		LayoutStructureItem ancestorLayoutStructureItem =
			LayoutStructureItemUtil.getAncestor(
				layoutStructureItem.getItemId(),
				LayoutDataItemTypeConstants.TYPE_FORM, layoutStructure);

		if (ancestorLayoutStructureItem == null) {
			return;
		}

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)ancestorLayoutStructureItem;

		JSONObject localizationConfigJSONObject =
			formStyledLayoutStructureItem.getLocalizationConfigJSONObject();

		if (localizationConfigJSONObject == null) {
			inputTemplateNode.addAttribute(
				"unlocalizedFieldsMessage",
				_language.format(
					locale, "x-field-cannot-be-localized", inputLabel));
			inputTemplateNode.addAttribute(
				"unlocalizedFieldsState", "disabled");

			return;
		}

		inputTemplateNode.addAttribute(
			"unlocalizedFieldsState",
			localizationConfigJSONObject.getString(
				"unlocalizedFieldsState", "disabled"));

		JSONObject jsonObject = localizationConfigJSONObject.getJSONObject(
			"unlocalizedFieldsMessage");

		String unlocalizedFieldsMessage = StringPool.BLANK;

		if (jsonObject != null) {
			unlocalizedFieldsMessage = jsonObject.getString(
				_language.getLanguageId(locale),
				jsonObject.getString(
					_language.getLanguageId(LocaleUtil.getSiteDefault())));
		}

		if (Validator.isNull(unlocalizedFieldsMessage)) {
			unlocalizedFieldsMessage = _language.get(
				locale, "this-field-cannot-be-localized");
		}

		inputTemplateNode.addAttribute(
			"unlocalizedFieldsMessage", unlocalizedFieldsMessage);
	}

	private void _addLongTextInfoFieldTypeInputTemplateNodeAttributes(
		InfoField infoField, InputTemplateNode inputTemplateNode) {

		inputTemplateNode.addAttribute(
			"maxLength",
			infoField.getAttribute(LongTextInfoFieldType.MAX_LENGTH));
	}

	private void _addMultiselectInfoFieldTypeInputTemplateNodeAttributes(
		InfoField infoField, InputTemplateNode inputTemplateNode,
		Locale locale) {

		inputTemplateNode.addAttribute(
			"options",
			TransformUtil.transform(
				(List<OptionInfoFieldType>)infoField.getAttribute(
					MultiselectInfoFieldType.OPTIONS),
				optionInfoFieldType -> new InputTemplateNode.Option(
					optionInfoFieldType.getLabel(locale),
					optionInfoFieldType.getValue())));
	}

	private void _addNumberInfoFieldTypeInputTemplateNodeAttributes(
		InfoField infoField, InputTemplateNode inputTemplateNode) {

		String dataType = "integer";

		if (GetterUtil.getBoolean(
				infoField.getAttribute(NumberInfoFieldType.DECIMAL))) {

			dataType = "decimal";

			Integer decimalPartMaxLength = (Integer)infoField.getAttribute(
				NumberInfoFieldType.DECIMAL_PART_MAX_LENGTH);

			if (decimalPartMaxLength != null) {
				inputTemplateNode.addAttribute(
					"step", _getStep(decimalPartMaxLength));
			}
		}

		inputTemplateNode.addAttribute("dataType", dataType);

		BigDecimal maxValue = (BigDecimal)infoField.getAttribute(
			NumberInfoFieldType.MAX_VALUE);

		if (maxValue != null) {
			inputTemplateNode.addAttribute("max", maxValue);
		}

		BigDecimal minValue = (BigDecimal)infoField.getAttribute(
			NumberInfoFieldType.MIN_VALUE);

		if (minValue != null) {
			inputTemplateNode.addAttribute("min", minValue);
		}
	}

	private void _addRelationshipInfoFieldTypeInputTemplateNodeAttributes(
		InfoField infoField, InputTemplateNode inputTemplateNode, String label,
		String value) {

		inputTemplateNode.addAttribute(
			"relationshipLabelFieldName",
			infoField.getAttribute(RelationshipInfoFieldType.LABEL_FIELD_NAME));
		inputTemplateNode.addAttribute(
			"relationshipURL",
			infoField.getAttribute(RelationshipInfoFieldType.URL));
		inputTemplateNode.addAttribute(
			"relationshipValueFieldName",
			infoField.getAttribute(RelationshipInfoFieldType.VALUE_FIELD_NAME));

		if (Validator.isNotNull(label)) {
			inputTemplateNode.addAttribute("selectedOptionLabel", label);
		}

		if (Validator.isNotNull(value)) {
			inputTemplateNode.addAttribute("selectedOptionValue", value);
		}
	}

	private void _addSelectInfoFieldTypeInputTemplateNodeAttributes(
		InfoField infoField, InputTemplateNode inputTemplateNode, Locale locale,
		String value) {

		List<InputTemplateNode.Option> options = new ArrayList<>();

		List<OptionInfoFieldType> optionInfoFieldTypes =
			(List<OptionInfoFieldType>)infoField.getAttribute(
				SelectInfoFieldType.OPTIONS);

		if (optionInfoFieldTypes == null) {
			optionInfoFieldTypes = Collections.emptyList();
		}

		for (OptionInfoFieldType optionInfoFieldType : optionInfoFieldTypes) {
			options.add(
				new InputTemplateNode.Option(
					optionInfoFieldType.getLabel(locale),
					optionInfoFieldType.getValue()));

			if ((value != null) &&
				value.equals(optionInfoFieldType.getValue())) {

				inputTemplateNode.addAttribute(
					"selectedOptionLabel",
					optionInfoFieldType.getLabel(locale));
				inputTemplateNode.addAttribute("selectedOptionValue", value);
			}
		}

		inputTemplateNode.addAttribute("options", options);
	}

	private void _addTextInfoFieldTypeInputTemplateNodeAttributes(
		InfoField infoField, InputTemplateNode inputTemplateNode) {

		inputTemplateNode.addAttribute(
			"maxLength", infoField.getAttribute(TextInfoFieldType.MAX_LENGTH));
	}

	private FileEntry _fetchFileEntry(long fileEntryId) {
		try {
			return _dlAppLocalService.getFileEntry(fileEntryId);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get file entry " + fileEntryId, portalException);
			}

			return null;
		}
	}

	private String _getFileName(
		FileInfoFieldType.FileSourceType fileSourceType, Object value) {

		if (Validator.isNull(value)) {
			return null;
		}

		FileEntry fileEntry = _fetchFileEntry(GetterUtil.getLong(value));

		if (fileSourceType ==
				FileInfoFieldType.FileSourceType.DOCUMENTS_AND_MEDIA) {

			if (fileEntry != null) {
				return fileEntry.getFileName();
			}
		}
		else if (fileSourceType ==
					FileInfoFieldType.FileSourceType.USER_COMPUTER) {

			if (fileEntry != null) {
				return TempFileEntryUtil.getOriginalTempFileName(
					fileEntry.getFileName());
			}
		}

		return null;
	}

	private String _getInputLabel(
		String defaultInputLabel, String editableValues, InfoField<?> infoField,
		Locale locale) {

		String inputLabel = null;

		JSONObject inputLabelJSONObject =
			(JSONObject)
				_fragmentEntryConfigurationParser.getConfigurationFieldValue(
					editableValues, "inputLabel",
					FragmentConfigurationFieldDataType.OBJECT);

		if (inputLabelJSONObject != null) {
			inputLabel = inputLabelJSONObject.getString(
				_language.getLanguageId(locale));
		}

		if (Validator.isNull(inputLabel) && (infoField != null)) {
			InfoLocalizedValue<String> labelInfoLocalizedValue =
				infoField.getLabelInfoLocalizedValue();

			Set<Locale> availableLocales =
				labelInfoLocalizedValue.getAvailableLocales();

			if (availableLocales.contains(locale)) {
				inputLabel = labelInfoLocalizedValue.getValue(locale);
			}
			else {
				inputLabel = inputLabelJSONObject.getString(
					_language.getLanguageId(LocaleUtil.getSiteDefault()));
			}
		}

		if (Validator.isNull(inputLabel) && (infoField != null)) {
			inputLabel = infoField.getLabel(locale);
		}

		if (Validator.isNotNull(inputLabel)) {
			return inputLabel;
		}

		return defaultInputLabel;
	}

	private List<String> _getSelectedOptions(
		List<OptionInfoFieldType> optionInfoFieldTypes,
		List<KeyLocalizedLabelPair> keyLocalizedLabelPairs) {

		List<String> selectedOptions = new ArrayList<>();

		for (KeyLocalizedLabelPair keyLocalizedLabelPair :
				keyLocalizedLabelPairs) {

			for (OptionInfoFieldType optionInfoFieldType :
					optionInfoFieldTypes) {

				if (Objects.equals(
						keyLocalizedLabelPair.getKey(),
						optionInfoFieldType.getValue())) {

					selectedOptions.add(optionInfoFieldType.getValue());
				}
			}
		}

		return selectedOptions;
	}

	private String _getStep(Integer decimalPartMaxLength) {
		if (decimalPartMaxLength == null) {
			return StringPool.BLANK;
		}

		if (decimalPartMaxLength <= 0) {
			return "0";
		}

		return StringBundler.concat(
			"0.",
			StringUtil.merge(
				Collections.nCopies(decimalPartMaxLength - 1, "0"),
				StringPool.BLANK),
			"1");
	}

	private Object _getValue(
		String defaultValue, HttpServletRequest httpServletRequest,
		InfoField infoField, String infoFormName, Locale locale) {

		if (httpServletRequest == null) {
			return defaultValue;
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		if (layoutDisplayPageObjectProvider == null) {
			return defaultValue;
		}

		String className = _infoSearchClassMapperRegistry.getClassName(
			layoutDisplayPageObjectProvider.getClassName());

		if (!Objects.equals(className, infoFormName)) {
			return defaultValue;
		}

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		if (infoItemFieldValuesProvider == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get info item form provider for class " +
						layoutDisplayPageObjectProvider.getClassName());
			}

			return defaultValue;
		}

		InfoItemFieldValues infoItemFieldValues =
			infoItemFieldValuesProvider.getInfoItemFieldValues(
				layoutDisplayPageObjectProvider.getDisplayObject());

		InfoFieldValue<?> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(infoField.getUniqueId());

		if (infoFieldValue == null) {
			return defaultValue;
		}

		return _parseValue(
			defaultValue, infoField, locale, infoFieldValue.getValue());
	}

	private Object _parseValue(
		String defaultValue, InfoField infoField, Locale locale, Object value) {

		if (infoField.isLocalizable() &&
			(value instanceof InfoLocalizedValue)) {

			HashMap<Locale, Object> parsedValues = new HashMap<>();

			InfoLocalizedValue<?> infoLocalizedValue =
				(InfoLocalizedValue<?>)value;

			Map<Locale, Object> values =
				(Map<Locale, Object>)infoLocalizedValue.getValues();

			for (Map.Entry<Locale, Object> entry : values.entrySet()) {
				parsedValues.put(
					entry.getKey(),
					_parseValue(
						defaultValue, infoField, locale, entry.getValue()));
			}

			return parsedValues;
		}

		if (infoField.getInfoFieldType() == DateInfoFieldType.INSTANCE) {
			try {
				DateFormat dateFormat =
					DateFormatFactoryUtil.getSimpleDateFormat(
						"yyyy-MM-dd", locale);

				return dateFormat.format(value);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to parse date from " + value, exception);
				}
			}

			return StringPool.BLANK;
		}

		if (infoField.getInfoFieldType() == DateTimeInfoFieldType.INSTANCE) {
			try {
				DateTimeFormatter dateTimeFormatter =
					DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

				return dateTimeFormatter.format((TemporalAccessor)value);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to parse date from " + value, exception);
				}
			}

			return StringPool.BLANK;
		}

		if (infoField.getInfoFieldType() == MultiselectInfoFieldType.INSTANCE) {
			if (!(value instanceof List)) {
				return defaultValue;
			}

			List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
				(List<KeyLocalizedLabelPair>)value;

			if (ListUtil.isEmpty(keyLocalizedLabelPairs)) {
				return defaultValue;
			}

			List<OptionInfoFieldType> optionInfoFieldTypes =
				(List<OptionInfoFieldType>)infoField.getAttribute(
					MultiselectInfoFieldType.OPTIONS);

			return ListUtil.toString(
				_getSelectedOptions(
					optionInfoFieldTypes, keyLocalizedLabelPairs),
				StringPool.BLANK);
		}

		if ((infoField.getInfoFieldType() == NumberInfoFieldType.INSTANCE) &&
			(value instanceof BigDecimal)) {

			BigDecimal bigDecimal = (BigDecimal)value;

			if (Objects.equals(bigDecimal.signum(), 0)) {
				return "0";
			}
		}

		if (infoField.getInfoFieldType() ==
				RelationshipInfoFieldType.INSTANCE) {

			return value;
		}

		if (infoField.getInfoFieldType() ==
				PicklistMultiselectInfoFieldType.INSTANCE) {

			if (!(value instanceof List)) {
				return defaultValue;
			}

			List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
				(List<KeyLocalizedLabelPair>)value;

			if (ListUtil.isEmpty(keyLocalizedLabelPairs)) {
				return defaultValue;
			}

			List<OptionInfoFieldType> optionInfoFieldTypes =
				(List<OptionInfoFieldType>)infoField.getAttribute(
					PicklistMultiselectInfoFieldType.OPTIONS);

			return ListUtil.toString(
				_getSelectedOptions(
					optionInfoFieldTypes, keyLocalizedLabelPairs),
				StringPool.BLANK);
		}

		if (infoField.getInfoFieldType() ==
				PicklistSelectInfoFieldType.INSTANCE) {

			if (!(value instanceof List)) {
				return defaultValue;
			}

			List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
				(List<KeyLocalizedLabelPair>)value;

			if (ListUtil.isEmpty(keyLocalizedLabelPairs)) {
				return defaultValue;
			}

			List<OptionInfoFieldType> optionInfoFieldTypes =
				(List<OptionInfoFieldType>)infoField.getAttribute(
					PicklistSelectInfoFieldType.OPTIONS);

			return ListUtil.toString(
				_getSelectedOptions(
					optionInfoFieldTypes, keyLocalizedLabelPairs),
				StringPool.BLANK);
		}

		if (infoField.getInfoFieldType() == SelectInfoFieldType.INSTANCE) {
			if (!(value instanceof List)) {
				return defaultValue;
			}

			List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
				(List<KeyLocalizedLabelPair>)value;

			if (ListUtil.isEmpty(keyLocalizedLabelPairs)) {
				return defaultValue;
			}

			List<OptionInfoFieldType> optionInfoFieldTypes =
				(List<OptionInfoFieldType>)infoField.getAttribute(
					SelectInfoFieldType.OPTIONS);

			return ListUtil.toString(
				_getSelectedOptions(
					optionInfoFieldTypes, keyLocalizedLabelPairs),
				StringPool.BLANK);
		}

		if (Validator.isNull(value)) {
			return defaultValue;
		}

		return String.valueOf(value);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryInputTemplateNodeContextHelperImpl.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

}