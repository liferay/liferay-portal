/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.portlet.action;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.templateparser.TemplateNode;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.template.engine.TemplateContextHelper;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	property = {
		"jakarta.portlet.name=" + NotificationPortletKeys.NOTIFICATION_TEMPLATES,
		"mvc.command.name=/notification_templates/notification_template_ftl_elements"
	},
	service = MVCResourceCommand.class
)
public class NotificationTemplateFTLElementsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				ParamUtil.getLong(resourceRequest, "objectDefinitionId"));

		if (objectDefinition == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		Locale locale = _portal.getLocale(resourceRequest);

		_fillTemplateContextTemplateVariables(jsonArray, locale);

		_fillObjectDefinitionTemplateVariables(
			jsonArray, locale, objectDefinition, resourceRequest);

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonArray);
	}

	private void _fillObjectDefinitionTemplateVariables(
			JSONArray jsonArray, Locale locale,
			ObjectDefinition objectDefinition, ResourceRequest resourceRequest)
		throws Exception {

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, objectDefinition.getClassName());

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			StringPool.BLANK, _portal.getScopeGroupId(resourceRequest));

		for (InfoFieldSetEntry infoFieldSetEntry :
				infoForm.getInfoFieldSetEntries()) {

			if (!(infoFieldSetEntry instanceof InfoFieldSet)) {
				continue;
			}

			InfoFieldSet infoFieldSet = (InfoFieldSet)infoFieldSetEntry;

			TemplateVariableGroup templateVariableGroup =
				new TemplateVariableGroup(infoFieldSet.getLabel(locale));

			for (InfoField<?> infoField : infoFieldSet.getAllInfoFields()) {
				if (!StringUtil.startsWith(
						infoField.getUniqueId(),
						PortletDisplayTemplate.DISPLAY_STYLE_PREFIX)) {

					InfoFieldType infoFieldType = infoField.getInfoFieldType();

					templateVariableGroup.addFieldVariable(
						infoField.getLabel(locale), TemplateNode.class,
						infoField.getUniqueId(), infoField.getLabel(locale),
						infoFieldType.getName(), infoField.isMultivalued(),
						null);
				}
			}

			jsonArray.put(
				_getTemplateVariableGroupJSONObject(
					true, locale, templateVariableGroup));
		}
	}

	private void _fillTemplateContextTemplateVariables(
			JSONArray jsonArray, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroupsMap =
			TemplateContextHelper.getTemplateVariableGroups(
				_classNameLocalService.getClassNameId(
					NotificationTemplate.class.getName()),
				0L, TemplateConstants.LANG_TYPE_FTL, locale);

		for (TemplateVariableGroup templateVariableGroup :
				templateVariableGroupsMap.values()) {

			jsonArray.put(
				_getTemplateVariableGroupJSONObject(
					false, locale, templateVariableGroup));
		}
	}

	private JSONObject _getTemplateVariableGroupJSONObject(
			boolean infoField, Locale locale,
			TemplateVariableGroup templateVariableGroup)
		throws Exception {

		JSONObject templateVariableGroupJSONObject =
			_jsonFactory.createJSONObject(
				_jsonFactory.looseSerializeDeep(templateVariableGroup));

		JSONArray jsonArray = (JSONArray)templateVariableGroupJSONObject.get(
			"templateVariableDefinitions");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = (JSONObject)jsonArray.get(i);

			jsonObject.put(
				"content",
				() -> {
					String content = (String)jsonObject.get("name");

					if (infoField) {
						if (content.contains(StringPool.POUND)) {
							content = StringBundler.concat(
								".data_model[\"", content, "\"]");
						}

						content = StringBundler.concat(
							"${", content, ".getData()}");
					}
					else {
						content = StringBundler.concat("${", content, "}");
					}

					return content;
				}
			).put(
				"helpText",
				_language.get(locale, (String)jsonObject.get("help"))
			).put(
				"label", _language.get(locale, (String)jsonObject.get("label"))
			);
		}

		templateVariableGroupJSONObject.put(
			"items",
			templateVariableGroupJSONObject.get("templateVariableDefinitions"));

		templateVariableGroupJSONObject.remove("templateVariableDefinitions");

		return templateVariableGroupJSONObject;
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private Portal _portal;

}