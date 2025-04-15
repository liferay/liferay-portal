/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.template;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.lists.constants.DDLConstants;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.dynamic.data.lists.service.DDLRecordService;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.lists.service.DDLRecordSetService;
import com.liferay.dynamic.data.lists.web.internal.configuration.DDLWebConfigurationKeys;
import com.liferay.dynamic.data.lists.web.internal.configuration.DDLWebConfigurationUtil;
import com.liferay.dynamic.data.lists.web.internal.template.helper.DDLDisplayTemplateHelper;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.template.BaseDDMTemplateHandler;
import com.liferay.dynamic.data.mapping.template.DDMTemplateVariableCodeHandler;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableCodeHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Marcellus Tavares
 */
@Component(
	property = "jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
	service = TemplateHandler.class
)
public class DDLDisplayTemplateHandler extends BaseDDMTemplateHandler {

	@Override
	public String getClassName() {
		return DDLRecordSet.class.getName();
	}

	@Override
	public Map<String, Object> getCustomContextObjects() {
		return HashMapBuilder.<String, Object>put(
			"ddlDisplayTemplateHelper",
			new DDLDisplayTemplateHelper(_dlURLHelper)
		).build();
	}

	@Override
	public String getName(Locale locale) {
		String portletTitle = _portal.getPortletTitle(
			DDLPortletKeys.DYNAMIC_DATA_LISTS,
			ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass()));

		return _language.format(locale, "x-template", portletTitle, false);
	}

	@Override
	public String getResourceName() {
		return "com.liferay.dynamic.data.lists.template";
	}

	@Override
	public String getTemplatesHelpPath(String language) {
		return DDLWebConfigurationUtil.get(
			getTemplatesHelpPropertyKey(), new Filter(language));
	}

	@Override
	public String getTemplatesHelpPropertyKey() {
		return DDLWebConfigurationKeys.DYNAMIC_DATA_LISTS_HELP_TEMPLATE;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups =
			new LinkedHashMap<>();

		addTemplateVariableGroup(
			templateVariableGroups,
			_getDDLUtilVariablesTemplateVariableGroups());
		addTemplateVariableGroup(
			templateVariableGroups, _getDDLVariablesTemplateVariableGroups());
		addTemplateVariableGroup(
			templateVariableGroups, getGeneralVariablesTemplateVariableGroup());

		TemplateVariableGroup structureFieldsTemplateVariableGroup =
			getStructureFieldsTemplateVariableGroup(classPK, locale);

		structureFieldsTemplateVariableGroup.setLabel(
			"data-list-record-fields");

		addTemplateVariableGroup(
			templateVariableGroups, structureFieldsTemplateVariableGroup);

		addTemplateVariableGroup(
			templateVariableGroups, getUtilTemplateVariableGroup());

		TemplateVariableGroup ddlServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"data-list-services", getRestrictedVariables(language));

		ddlServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		ddlServicesTemplateVariableGroup.addServiceLocatorVariables(
			DDLRecordLocalService.class, DDLRecordService.class,
			DDLRecordSetLocalService.class, DDLRecordSetService.class,
			DDMStructureLocalService.class, DDMStructureService.class,
			DDMTemplateLocalService.class, DDMTemplateService.class);

		templateVariableGroups.put(
			ddlServicesTemplateVariableGroup.getLabel(),
			ddlServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Override
	protected Class<?> getFieldVariableClass() {
		return DDMFormFieldValue.class;
	}

	@Override
	protected TemplateVariableCodeHandler getTemplateVariableCodeHandler() {
		return _templateVariableCodeHandler;
	}

	private TemplateVariableGroup _getDDLUtilVariablesTemplateVariableGroups() {
		TemplateVariableGroup ddlUtilTemplateVariableGroup =
			new TemplateVariableGroup("data-list-util");

		ddlUtilTemplateVariableGroup.addVariable(
			"data-list-display-template-helper", DDLDisplayTemplateHelper.class,
			"ddlDisplayTemplateHelper", null);

		return ddlUtilTemplateVariableGroup;
	}

	private TemplateVariableGroup _getDDLVariablesTemplateVariableGroups() {
		TemplateVariableGroup templateVariableGroup = new TemplateVariableGroup(
			"data-list-variables");

		templateVariableGroup.addVariable(
			"data-definition-id", null, DDLConstants.RESERVED_DDM_STRUCTURE_ID);
		templateVariableGroup.addVariable(
			"data-list-description", String.class,
			DDLConstants.RESERVED_RECORD_SET_DESCRIPTION);
		templateVariableGroup.addVariable(
			"data-list-id", null, DDLConstants.RESERVED_RECORD_SET_ID);
		templateVariableGroup.addVariable(
			"data-list-name", String.class,
			DDLConstants.RESERVED_RECORD_SET_NAME);
		templateVariableGroup.addCollectionVariable(
			"data-list-records", List.class, "records", "record",
			DDLRecord.class, "cur_record", null);
		templateVariableGroup.addVariable(
			"template-id", null, DDLConstants.RESERVED_DDM_TEMPLATE_ID);

		return templateVariableGroup;
	}

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.dynamic.data.lists.service)(release.schema.version>=0.0.3))"
	)
	private Release _release;

	private final TemplateVariableCodeHandler _templateVariableCodeHandler =
		new DDMTemplateVariableCodeHandler(
			DDLDisplayTemplateHandler.class.getClassLoader(),
			"com/liferay/dynamic/data/lists/web/internal/template" +
				"/dependencies/",
			SetUtil.fromArray("document-library", "html", "link-to-page"));

}