/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.display.context;

import com.liferay.dynamic.data.mapping.template.DDMTemplateVariableCodeHandler;
import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.template.TemplateVariableCodeHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.templateparser.TemplateNode;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalServiceUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 * @author Lourdes Fernández Besada
 */
public class InformationTemplatesEditDDMTemplateDisplayContext
	extends EditDDMTemplateDisplayContext {

	public InformationTemplatesEditDDMTemplateDisplayContext(
		InfoItemServiceRegistry infoItemServiceRegistry,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(liferayPortletRequest, liferayPortletResponse);

		_infoItemServiceRegistry = infoItemServiceRegistry;

		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getTemplateSubtypeLabel() {
		TemplateEntry templateEntry = _getTemplateEntry();

		if (Validator.isNull(templateEntry.getInfoItemFormVariationKey())) {
			return StringPool.BLANK;
		}

		InfoItemFormVariationsProvider infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				templateEntry.getInfoItemClassName());

		if (infoItemFormVariationsProvider == null) {
			return StringPool.BLANK;
		}

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.getInfoItemFormVariation(
				_themeDisplay.getScopeGroupId(),
				templateEntry.getInfoItemFormVariationKey());

		if (infoItemFormVariation == null) {
			return StringPool.BLANK;
		}

		String label = infoItemFormVariation.getLabel(
			_themeDisplay.getLocale());

		if (label == null) {
			return StringPool.BLANK;
		}

		return label;
	}

	@Override
	public String getTemplateTypeLabel() {
		TemplateEntry templateEntry = _getTemplateEntry();

		String defaultValue = ResourceActionsUtil.getModelResource(
			_themeDisplay.getLocale(), templateEntry.getInfoItemClassName());

		InfoItemDetailsProvider infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class,
				templateEntry.getInfoItemClassName());

		if (infoItemDetailsProvider == null) {
			return defaultValue;
		}

		InfoItemClassDetails infoItemClassDetails =
			infoItemDetailsProvider.getInfoItemClassDetails();

		if (infoItemClassDetails == null) {
			return defaultValue;
		}

		String label = infoItemClassDetails.getLabel(_themeDisplay.getLocale());

		if (label == null) {
			return defaultValue;
		}

		return label;
	}

	@Override
	public String getUpdateDDMTemplateURL() {
		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			"/template/update_template_entry"
		).setTabs1(
			getTabs1()
		).setParameter(
			"templateEntryId", getTemplateEntryId()
		).buildString();
	}

	@Override
	protected long getTemplateHandlerClassNameId() {
		return PortalUtil.getClassNameId(InfoItemFormProvider.class);
	}

	@Override
	protected Collection<TemplateVariableGroup> getTemplateVariableGroups()
		throws Exception {

		TemplateEntry templateEntry = _getTemplateEntry();

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class,
				templateEntry.getInfoItemClassName());

		if (infoItemFormProvider == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get info item form provider for class name " +
						templateEntry.getInfoItemClassName());
			}

			return super.getTemplateVariableGroups();
		}

		InfoForm infoForm = null;

		try {
			infoForm = infoItemFormProvider.getInfoForm(
				templateEntry.getInfoItemFormVariationKey(),
				_themeDisplay.getScopeGroupId());
		}
		catch (NoSuchFormVariationException noSuchFormVariationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get form variation with key " +
						templateEntry.getInfoItemFormVariationKey(),
					noSuchFormVariationException);
			}

			return super.getTemplateVariableGroups();
		}

		List<TemplateVariableGroup> templateVariableGroups = new LinkedList<>(
			super.getTemplateVariableGroups());

		for (InfoFieldSetEntry infoFieldSetEntry :
				infoForm.getInfoFieldSetEntries()) {

			if (!(infoFieldSetEntry instanceof InfoFieldSet)) {
				continue;
			}

			InfoFieldSet infoFieldSet = (InfoFieldSet)infoFieldSetEntry;

			TemplateVariableGroup templateVariableGroup =
				new TemplateVariableGroup(
					infoFieldSet.getLabel(_themeDisplay.getLocale()));

			for (InfoField<?> infoField : infoFieldSet.getAllInfoFields()) {
				if (StringUtil.startsWith(
						infoField.getUniqueId(),
						PortletDisplayTemplate.DISPLAY_STYLE_PREFIX)) {

					continue;
				}

				InfoFieldType infoFieldType = infoField.getInfoFieldType();

				templateVariableGroup.addFieldVariable(
					infoField.getLabel(_themeDisplay.getLocale()),
					TemplateNode.class, infoField.getUniqueId(),
					infoField.getLabel(_themeDisplay.getLocale()),
					infoFieldType.getName(),
					infoField.isMultivalued() || infoField.isRepeatable(),
					_templateVariableCodeHandler);
			}

			templateVariableGroups.add(templateVariableGroup);
		}

		return templateVariableGroups;
	}

	private TemplateEntry _getTemplateEntry() {
		if (_templateEntry != null) {
			return _templateEntry;
		}

		_templateEntry = TemplateEntryLocalServiceUtil.fetchTemplateEntry(
			getTemplateEntryId());

		return _templateEntry;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InformationTemplatesEditDDMTemplateDisplayContext.class);

	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private TemplateEntry _templateEntry;
	private final TemplateVariableCodeHandler _templateVariableCodeHandler =
		new DDMTemplateVariableCodeHandler(
			InformationTemplatesTemplateDisplayContext.class.getClassLoader(),
			"com/liferay/template/web/internal/portlet/template/dependencies/",
			SetUtil.fromArray("boolean", "image"));
	private final ThemeDisplay _themeDisplay;

}