/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.display.template;

import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	property = "jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
	service = TemplateHandler.class
)
public class IGPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return FileEntry.class.getName();
	}

	@Override
	public Map<String, Object> getCustomContextObjects() {
		Map<String, Object> contextObjects = new HashMap<>();

		try {
			contextObjects.put("dlUtil", DLUtil.getDL());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		return contextObjects;
	}

	@Override
	public String getName(Locale locale) {
		String portletTitle = _portal.getPortletTitle(
			DLPortletKeys.MEDIA_GALLERY_DISPLAY,
			ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass()));

		return _language.format(locale, "x-template", portletTitle, false);
	}

	@Override
	public String getResourceName() {
		return DLPortletKeys.DOCUMENT_LIBRARY;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups =
			super.getTemplateVariableGroups(classPK, language, locale);

		String[] restrictedVariables = getRestrictedVariables(language);

		TemplateVariableGroup documentUtilTemplateVariableGroup =
			new TemplateVariableGroup("document-util", restrictedVariables);

		documentUtilTemplateVariableGroup.addVariable(
			"dl-util", DLUtil.class, "dlUtil");

		templateVariableGroups.put(
			"document-util", documentUtilTemplateVariableGroup);

		TemplateVariableGroup documentServicesTemplateVariableGroup =
			new TemplateVariableGroup("document-services", restrictedVariables);

		documentServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		documentServicesTemplateVariableGroup.addServiceLocatorVariables(
			DLAppLocalService.class, DLAppService.class,
			DLFileEntryTypeLocalService.class, DLFileEntryTypeService.class);

		templateVariableGroups.put(
			documentServicesTemplateVariableGroup.getLabel(),
			documentServicesTemplateVariableGroup);

		TemplateVariableGroup fieldsTemplateVariableGroup =
			templateVariableGroups.get("fields");

		fieldsTemplateVariableGroup.empty();

		fieldsTemplateVariableGroup.addCollectionVariable(
			"documents", List.class, PortletDisplayTemplateConstants.ENTRIES,
			"document", FileEntry.class, "curFileEntry", "title");

		return templateVariableGroups;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);
	}

	@Override
	protected String getTemplatesConfigPath() {
		return _dlConfiguration.displayTemplatesConfig();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IGPortletDisplayTemplateHandler.class);

	private volatile DLConfiguration _dlConfiguration;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}