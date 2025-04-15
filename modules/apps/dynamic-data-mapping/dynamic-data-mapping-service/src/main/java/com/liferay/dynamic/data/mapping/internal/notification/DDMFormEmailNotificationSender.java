/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.notification;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.URLTemplateResource;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.mail.internet.InternetAddress;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Writer;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = DDMFormEmailNotificationSender.class)
public class DDMFormEmailNotificationSender {

	public void sendEmailNotification(
		DDMFormInstanceRecord ddmFormInstanceRecord,
		ServiceContext serviceContext) {

		try {
			MailMessage mailMessage = _createMailMessage(
				ddmFormInstanceRecord, serviceContext);

			_mailService.sendEmail(mailMessage);
		}
		catch (Exception exception) {
			_log.error("Unable to send form email", exception);
		}
	}

	protected Map<String, List<DDMFormFieldValue>> getDDMFormFieldValuesMap(
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws PortalException {

		DDMFormValues ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		return ddmFormValues.getDDMFormFieldValuesMap(true);
	}

	protected Map<String, Object> getFieldProperties(
		List<DDMFormFieldValue> ddmFormFieldValues, Locale locale) {

		DDMFormField ddmFormField = _getDDMFormField(ddmFormFieldValues);

		if (Objects.equals(ddmFormField.getType(), "fieldset")) {
			return null;
		}

		if (Objects.equals(ddmFormField.getType(), "paragraph")) {
			return HashMapBuilder.<String, Object>put(
				"label", _getLabel(ddmFormField, locale)
			).put(
				"value", _getParagraphText(ddmFormField, locale)
			).build();
		}

		List<String> renderedDDMFormFieldValues = ListUtil.toList(
			ddmFormFieldValues,
			new Function<DDMFormFieldValue, String>() {

				@Override
				public String apply(DDMFormFieldValue ddmFormFieldValue) {
					return _renderDDMFormFieldValue(ddmFormFieldValue, locale);
				}

			});

		return HashMapBuilder.<String, Object>put(
			"label", _getLabel(ddmFormField, locale)
		).put(
			"value",
			StringUtil.merge(
				renderedDDMFormFieldValues, StringPool.COMMA_AND_SPACE)
		).build();
	}

	protected List<Object> getFields(
		List<String> fieldNames,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
		Locale locale) {

		List<Object> fields = new ArrayList<>();

		for (String fieldName : fieldNames) {
			List<DDMFormFieldValue> ddmFormFieldValues =
				ddmFormFieldValuesMap.get(fieldName);

			if (ddmFormFieldValues == null) {
				continue;
			}

			fields.add(getFieldProperties(ddmFormFieldValues, locale));

			fields.addAll(
				_getNestedFields(
					ddmFormFieldValues, ddmFormFieldValuesMap, locale));
		}

		return fields;
	}

	private MailMessage _createMailMessage(
			DDMFormInstanceRecord ddmFormInstanceRecord,
			ServiceContext serviceContext)
		throws Exception {

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceRecord.getFormInstance();

		InternetAddress fromInternetAddress = new InternetAddress(
			_getEmailFromAddress(ddmFormInstance),
			_getEmailFromName(ddmFormInstance));

		String subject = _getEmailSubject(ddmFormInstance);

		String body = _getEmailBody(
			serviceContext, ddmFormInstance, ddmFormInstanceRecord);

		MailMessage mailMessage = new MailMessage(
			fromInternetAddress, subject, body, true);

		InternetAddress[] toAddresses = InternetAddress.parse(
			_getEmailToAddress(ddmFormInstance));

		mailMessage.setTo(toAddresses);

		return mailMessage;
	}

	private Template _createTemplate(
			ServiceContext serviceContext, DDMFormInstance ddmFormInstance,
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws Exception {

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL,
			_getTemplateResource(_TEMPLATE_PATH), false);

		_populateParameters(
			template, serviceContext, ddmFormInstance, ddmFormInstanceRecord);

		return template;
	}

	private DDMFormField _getDDMFormField(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		return ddmFormFieldValue.getDDMFormField();
	}

	private DDMFormLayout _getDDMFormLayout(DDMFormInstance ddmFormInstance)
		throws Exception {

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		return ddmStructure.getDDMFormLayout();
	}

	private String _getEmailBody(
			ServiceContext serviceContext, DDMFormInstance ddmFormInstance,
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws Exception {

		Template template = _createTemplate(
			serviceContext, ddmFormInstance, ddmFormInstanceRecord);

		return _render(template);
	}

	private String _getEmailFromAddress(DDMFormInstance ddmFormInstance)
		throws Exception {

		DDMFormInstanceSettings formInstancetings =
			ddmFormInstance.getSettingsModel();

		String defaultEmailFromAddress = _prefsProps.getString(
			ddmFormInstance.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);

		return GetterUtil.getString(
			formInstancetings.emailFromAddress(), defaultEmailFromAddress);
	}

	private String _getEmailFromName(DDMFormInstance ddmFormInstance)
		throws Exception {

		DDMFormInstanceSettings formInstancetings =
			ddmFormInstance.getSettingsModel();

		String defaultEmailFromName = _prefsProps.getString(
			ddmFormInstance.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_NAME);

		return GetterUtil.getString(
			formInstancetings.emailFromName(), defaultEmailFromName);
	}

	private String _getEmailSubject(DDMFormInstance ddmFormInstance)
		throws Exception {

		DDMFormInstanceSettings formInstancetings =
			ddmFormInstance.getSettingsModel();

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		DDMForm ddmForm = ddmStructure.getDDMForm();

		Locale locale = ddmForm.getDefaultLocale();

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		String defaultEmailSubject = _language.format(
			resourceBundle, "new-x-form-submitted",
			ddmFormInstance.getName(locale), false);

		return GetterUtil.getString(
			formInstancetings.emailSubject(), defaultEmailSubject);
	}

	private String _getEmailToAddress(DDMFormInstance ddmFormInstance)
		throws Exception {

		String defaultEmailToAddress = StringPool.BLANK;

		DDMFormInstanceSettings formInstancetings =
			ddmFormInstance.getSettingsModel();

		User user = _userLocalService.fetchUser(ddmFormInstance.getUserId());

		if (user != null) {
			defaultEmailToAddress = user.getEmailAddress();
		}

		return GetterUtil.getString(
			formInstancetings.emailToAddress(), defaultEmailToAddress);
	}

	private List<String> _getFieldNames(DDMFormLayoutPage ddmFormLayoutPage) {
		List<String> fieldNames = new ArrayList<>();

		for (DDMFormLayoutRow ddmFormLayoutRow :
				ddmFormLayoutPage.getDDMFormLayoutRows()) {

			for (DDMFormLayoutColumn ddmFormLayoutColumn :
					ddmFormLayoutRow.getDDMFormLayoutColumns()) {

				fieldNames.addAll(ddmFormLayoutColumn.getDDMFormFieldNames());
			}
		}

		return fieldNames;
	}

	private String _getLabel(DDMFormField ddmFormField, Locale locale) {
		LocalizedValue label = ddmFormField.getLabel();

		if (ddmFormField.isRequired()) {
			return label.getString(locale) + StringPool.STAR;
		}

		return label.getString(locale);
	}

	private Locale _getLocale(
			DDMFormInstance ddmFormInstance, ServiceContext serviceContext)
		throws Exception {

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		String languageId = GetterUtil.getString(
			httpServletRequest.getParameter("languageId"),
			ddmFormInstance.getDefaultLanguageId());

		return LocaleUtil.fromLanguageId(languageId);
	}

	private List<Map<String, Object>> _getNestedFields(
		List<DDMFormFieldValue> ddmFormFieldValues,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
		Locale locale) {

		List<Map<String, Object>> nestedFields = new ArrayList<>();

		DDMFormField ddmFormField = _getDDMFormField(ddmFormFieldValues);

		Map<String, DDMFormField> nestedDDMFormFieldsMap =
			ddmFormField.getNestedDDMFormFieldsMap();

		for (String key : nestedDDMFormFieldsMap.keySet()) {
			nestedFields.add(
				getFieldProperties(ddmFormFieldValuesMap.get(key), locale));
		}

		return nestedFields;
	}

	private Map<String, Object> _getPage(
		DDMFormLayoutPage ddmFormLayoutPage,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
		Locale locale) {

		return HashMapBuilder.<String, Object>put(
			"fields",
			getFields(
				_getFieldNames(ddmFormLayoutPage), ddmFormFieldValuesMap,
				locale)
		).put(
			"title",
			() -> {
				LocalizedValue title = ddmFormLayoutPage.getTitle();

				return title.getString(locale);
			}
		).build();
	}

	private List<Object> _getPages(
			DDMFormInstance ddmFormInstance,
			DDMFormInstanceRecord ddmFormInstanceRecord, Locale locale)
		throws Exception {

		List<Object> pages = new ArrayList<>();

		DDMFormLayout ddmFormLayout = _getDDMFormLayout(ddmFormInstance);

		for (DDMFormLayoutPage ddmFormLayoutPage :
				ddmFormLayout.getDDMFormLayoutPages()) {

			pages.add(
				_getPage(
					ddmFormLayoutPage,
					getDDMFormFieldValuesMap(ddmFormInstanceRecord), locale));
		}

		return pages;
	}

	private String _getParagraphText(DDMFormField ddmFormField, Locale locale) {
		LocalizedValue text = (LocalizedValue)ddmFormField.getProperty("text");

		if (text == null) {
			return StringPool.BLANK;
		}

		return _htmlParser.extractText(text.getString(locale));
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private String _getSiteName(long groupId, Locale locale) throws Exception {
		Group siteGroup = _groupLocalService.fetchGroup(groupId);

		if (siteGroup != null) {
			return siteGroup.getDescriptiveName(locale);
		}

		return StringPool.BLANK;
	}

	private TemplateResource _getTemplateResource(String templatePath) {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		URL templateURL = classLoader.getResource(templatePath);

		return new URLTemplateResource(templateURL.getPath(), templateURL);
	}

	private String _getUserName(
		DDMFormInstanceRecord ddmFormInstanceRecord, Locale locale) {

		String userName = ddmFormInstanceRecord.getUserName();

		if (Validator.isNotNull(userName)) {
			return userName;
		}

		return _language.get(_getResourceBundle(locale), "someone");
	}

	private String _getViewFormEntriesURL(
			ServiceContext serviceContext, DDMFormInstance ddmFormInstance)
		throws Exception {

		String portletNamespace = _portal.getPortletNamespace(
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN);

		return _portal.getSiteAdminURL(
			serviceContext.getPortalURL(),
			_groupLocalService.getGroup(ddmFormInstance.getGroupId()),
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
			HashMapBuilder.put(
				portletNamespace.concat("mvcPath"),
				new String[] {"/admin/view_form_instance_records.jsp"}
			).put(
				portletNamespace.concat("formInstanceId"),
				new String[] {
					String.valueOf(ddmFormInstance.getFormInstanceId())
				}
			).build());
	}

	private String _getViewFormURL(
			ServiceContext serviceContext, DDMFormInstance ddmFormInstance,
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws Exception {

		String portletNamespace = _portal.getPortletNamespace(
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN);

		return _portal.getSiteAdminURL(
			serviceContext.getPortalURL(),
			_groupLocalService.getGroup(ddmFormInstance.getGroupId()),
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
			HashMapBuilder.put(
				portletNamespace.concat("mvcPath"),
				new String[] {"/admin/view_form_instance_record.jsp"}
			).put(
				portletNamespace.concat("formInstanceRecordId"),
				new String[] {
					String.valueOf(
						ddmFormInstanceRecord.getFormInstanceRecordId())
				}
			).put(
				portletNamespace.concat("formInstanceId"),
				new String[] {
					String.valueOf(ddmFormInstance.getFormInstanceId())
				}
			).build());
	}

	private void _populateParameters(
			Template template, ServiceContext serviceContext,
			DDMFormInstance ddmFormInstance,
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws Exception {

		Locale locale = _getLocale(ddmFormInstance, serviceContext);

		template.put("formName", ddmFormInstance.getName(locale));

		template.put(
			"pages", _getPages(ddmFormInstance, ddmFormInstanceRecord, locale));
		template.put(
			"siteName", _getSiteName(ddmFormInstance.getGroupId(), locale));
		template.put("userName", _getUserName(ddmFormInstanceRecord, locale));

		template.put(
			"viewFormEntriesURL",
			_getViewFormEntriesURL(serviceContext, ddmFormInstance));
		template.put(
			"viewFormURL",
			_getViewFormURL(
				serviceContext, ddmFormInstance, ddmFormInstanceRecord));
	}

	private String _render(Template template) throws Exception {
		Writer writer = new UnsyncStringWriter();

		template.processTemplate(writer);

		return writer.toString();
	}

	private String _renderDDMFormFieldValue(
		DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		if (ddmFormFieldValue.getValue() == null) {
			return StringPool.BLANK;
		}

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			_ddmFormFieldTypeServicesRegistry.getDDMFormFieldValueRenderer(
				ddmFormFieldValue.getType());

		return ddmFormFieldValueRenderer.render(ddmFormFieldValue, locale);
	}

	private static final String _TEMPLATE_PATH =
		"/META-INF/resources/notification/form_entry_add_body.ftl";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormEmailNotificationSender.class);

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private Language _language;

	@Reference
	private MailService _mailService;

	@Reference
	private Portal _portal;

	@Reference
	private PrefsProps _prefsProps;

	@Reference
	private UserLocalService _userLocalService;

}