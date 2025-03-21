/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.template;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.dynamic.data.mapping.template.BaseDDMTemplateHandler;
import com.liferay.dynamic.data.mapping.template.DDMTemplateVariableCodeHandler;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.constants.JournalStructureConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalContent;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableCodeHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
	service = TemplateHandler.class
)
public class JournalTemplateHandler extends BaseDDMTemplateHandler {

	@Override
	public String getClassName() {
		return JournalArticle.class.getName();
	}

	@Override
	public Map<String, Object> getCustomContextObjects() {
		Map<String, Object> contextObjects = new HashMap<>();

		try {
			contextObjects.put("journalContent", _journalContent);

			// Deprecated

			contextObjects.put("journalContentUtil", _journalContent);
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		return contextObjects;
	}

	@Override
	public String getName(Locale locale) {
		String portletTitle = _portal.getPortletTitle(
			JournalPortletKeys.JOURNAL, locale);

		return _language.format(locale, "x-template", portletTitle, false);
	}

	@Override
	public String getResourceName() {
		return "com.liferay.journal.template";
	}

	@Override
	public String getTemplatesHelpPath(String language) {
		return "com/liferay/journal/web/portlet/template/dependencies" +
			"/template.ftl";
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups =
			super.getTemplateVariableGroups(classPK, language, locale);

		TemplateVariableGroup fieldsTemplateVariableGroup =
			templateVariableGroups.get("fields");

		if (fieldsTemplateVariableGroup != null) {
			fieldsTemplateVariableGroup.addVariable(
				"friendly-url", String.class, "friendlyURL");
		}

		String[] restrictedVariables = getRestrictedVariables(language);

		templateVariableGroups.put(
			"journal-reserved",
			_getJournalReservedTemplateVariableGroup(restrictedVariables));

		TemplateVariableGroup journalUtilTemplateVariableGroup =
			new TemplateVariableGroup("journal-util", restrictedVariables);

		journalUtilTemplateVariableGroup.addVariable(
			"journal-content", JournalContent.class, "journalContent");

		templateVariableGroups.put(
			"journal-util", journalUtilTemplateVariableGroup);

		TemplateVariableGroup journalServicesTemplateVariableGroup =
			new TemplateVariableGroup("journal-services", restrictedVariables);

		journalServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		journalServicesTemplateVariableGroup.addServiceLocatorVariables(
			JournalArticleLocalService.class, JournalArticleService.class,
			DDMStructureLocalService.class, DDMStructureService.class,
			DDMTemplateLocalService.class, DDMTemplateService.class);

		templateVariableGroups.put(
			journalServicesTemplateVariableGroup.getLabel(),
			journalServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Override
	protected TemplateVariableGroup getStructureFieldsTemplateVariableGroup(
			long ddmStructureId, Locale locale)
		throws PortalException {

		if (ddmStructureId <= 0) {
			return null;
		}

		TemplateVariableGroup templateVariableGroup = new TemplateVariableGroup(
			"fields");

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			ddmStructureId);

		DDMForm fullHierarchyDDMForm = ddmStructure.getFullHierarchyDDMForm();

		Map<String, String> fieldNameVariableNameMap = new LinkedHashMap<>();

		for (DDMFormField ddmFormField :
				fullHierarchyDDMForm.getDDMFormFields()) {

			fieldNameVariableNameMap.put(
				ddmFormField.getName(), ddmFormField.getFieldReference());

			collectNestedFieldNameVariableName(
				ddmFormField, fieldNameVariableNameMap);
		}

		for (Map.Entry<String, String> fieldNameVariableName :
				fieldNameVariableNameMap.entrySet()) {

			String fieldName = fieldNameVariableName.getKey();

			String dataType = ddmStructure.getFieldDataType(fieldName);

			DDMFormField ddmFormField = ddmStructure.getDDMFormField(fieldName);

			if (Objects.equals(
					ddmFormField.getType(),
					DDMFormFieldTypeConstants.FIELDSET)) {

				dataType = DDMFormFieldTypeConstants.FIELDSET;
			}

			if (Validator.isNull(dataType)) {
				continue;
			}

			if (Objects.equals(
					ddmStructure.getFieldType(fieldName),
					DDMFormFieldTypeConstants.CHECKBOX_MULTIPLE)) {

				DDMFormFieldOptions ddmFormFieldOptions =
					(DDMFormFieldOptions)ddmFormField.getProperty("options");

				Map<String, LocalizedValue> options =
					ddmFormFieldOptions.getOptions();

				if (options.size() == 1) {
					dataType = "boolean";
				}
			}

			String label = ddmStructure.getFieldLabel(fieldName, locale);
			String tip = ddmStructure.getFieldTip(fieldName, locale);
			boolean repeatable = ddmStructure.getFieldRepeatable(fieldName);

			templateVariableGroup.addFieldVariable(
				label, getFieldVariableClass(),
				fieldNameVariableName.getValue(), tip, dataType, repeatable,
				getTemplateVariableCodeHandler());
		}

		return templateVariableGroup;
	}

	@Override
	protected TemplateVariableCodeHandler getTemplateVariableCodeHandler() {
		return _templateVariableCodeHandler;
	}

	private TemplateVariableGroup _getJournalReservedTemplateVariableGroup(
		String[] restrictedVariables) {

		TemplateVariableGroup journalReservedTemplateVariableGroup =
			new TemplateVariableGroup("journal", restrictedVariables);

		journalReservedTemplateVariableGroup.addVariable(
			"article-id", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_ID, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"author-email-address", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_AUTHOR_EMAIL_ADDRESS,
			"data", StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"author-id", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_AUTHOR_ID, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"author-job-title", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_AUTHOR_JOB_TITLE, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"author-name", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_AUTHOR_NAME, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"comments", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_AUTHOR_COMMENTS, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"create-date", Date.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_CREATE_DATE, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"description", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_DESCRIPTION, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"display-date", Date.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_DISPLAY_DATE, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"external-reference-code", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_EXTERNAL_REFERENCE_CODE,
			"data", StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"id", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_ID_, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"modified-date", Date.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_MODIFIED_DATE, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"resource-prim-key", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_RESOURCE_PRIM_KEY,
			"data", StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"small-image-url", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_SMALL_IMAGE_URL, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"tags", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_ASSET_TAG_NAMES, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"title", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_TITLE, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"url-title", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_URL_TITLE, "data",
			StringPool.BLANK);
		journalReservedTemplateVariableGroup.addVariable(
			"version", String.class, "reserved-article",
			JournalStructureConstants.RESERVED_ARTICLE_VERSION, "data",
			StringPool.BLANK);

		journalReservedTemplateVariableGroup.setAutocompleteEnabled(false);

		return journalReservedTemplateVariableGroup;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalTemplateHandler.class);

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private final TemplateVariableCodeHandler _templateVariableCodeHandler =
		new DDMTemplateVariableCodeHandler(
			JournalTemplateHandler.class.getClassLoader(),
			"com/liferay/journal/web/portlet/template/dependencies/",
			SetUtil.fromArray(
				"boolean", "date", "document-library", "fieldset",
				"geolocation", "image", "journal-article", "link-to-page"));

}