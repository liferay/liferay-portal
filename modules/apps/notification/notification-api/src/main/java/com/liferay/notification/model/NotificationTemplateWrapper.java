/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link NotificationTemplate}.
 * </p>
 *
 * @author Gabriel Albuquerque
 * @see NotificationTemplate
 * @generated
 */
public class NotificationTemplateWrapper
	extends BaseModelWrapper<NotificationTemplate>
	implements ModelWrapper<NotificationTemplate>, NotificationTemplate {

	public NotificationTemplateWrapper(
		NotificationTemplate notificationTemplate) {

		super(notificationTemplate);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("notificationTemplateId", getNotificationTemplateId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("objectDefinitionId", getObjectDefinitionId());
		attributes.put("body", getBody());
		attributes.put("description", getDescription());
		attributes.put("editorType", getEditorType());
		attributes.put("name", getName());
		attributes.put("recipientType", getRecipientType());
		attributes.put("subject", getSubject());
		attributes.put("system", isSystem());
		attributes.put("type", getType());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long notificationTemplateId = (Long)attributes.get(
			"notificationTemplateId");

		if (notificationTemplateId != null) {
			setNotificationTemplateId(notificationTemplateId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long objectDefinitionId = (Long)attributes.get("objectDefinitionId");

		if (objectDefinitionId != null) {
			setObjectDefinitionId(objectDefinitionId);
		}

		String body = (String)attributes.get("body");

		if (body != null) {
			setBody(body);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String editorType = (String)attributes.get("editorType");

		if (editorType != null) {
			setEditorType(editorType);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String recipientType = (String)attributes.get("recipientType");

		if (recipientType != null) {
			setRecipientType(recipientType);
		}

		String subject = (String)attributes.get("subject");

		if (subject != null) {
			setSubject(subject);
		}

		Boolean system = (Boolean)attributes.get("system");

		if (system != null) {
			setSystem(system);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}
	}

	@Override
	public NotificationTemplate cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public NotificationRecipient fetchNotificationRecipient() {
		return model.fetchNotificationRecipient();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the body of this notification template.
	 *
	 * @return the body of this notification template
	 */
	@Override
	public String getBody() {
		return model.getBody();
	}

	/**
	 * Returns the localized body of this notification template in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized body of this notification template
	 */
	@Override
	public String getBody(java.util.Locale locale) {
		return model.getBody(locale);
	}

	/**
	 * Returns the localized body of this notification template in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized body of this notification template. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getBody(java.util.Locale locale, boolean useDefault) {
		return model.getBody(locale, useDefault);
	}

	/**
	 * Returns the localized body of this notification template in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized body of this notification template
	 */
	@Override
	public String getBody(String languageId) {
		return model.getBody(languageId);
	}

	/**
	 * Returns the localized body of this notification template in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized body of this notification template
	 */
	@Override
	public String getBody(String languageId, boolean useDefault) {
		return model.getBody(languageId, useDefault);
	}

	@Override
	public String getBodyCurrentLanguageId() {
		return model.getBodyCurrentLanguageId();
	}

	@Override
	public String getBodyCurrentValue() {
		return model.getBodyCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized bodies of this notification template.
	 *
	 * @return the locales and localized bodies of this notification template
	 */
	@Override
	public Map<java.util.Locale, String> getBodyMap() {
		return model.getBodyMap();
	}

	/**
	 * Returns the company ID of this notification template.
	 *
	 * @return the company ID of this notification template
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this notification template.
	 *
	 * @return the create date of this notification template
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	/**
	 * Returns the description of this notification template.
	 *
	 * @return the description of this notification template
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the editor type of this notification template.
	 *
	 * @return the editor type of this notification template
	 */
	@Override
	public String getEditorType() {
		return model.getEditorType();
	}

	/**
	 * Returns the external reference code of this notification template.
	 *
	 * @return the external reference code of this notification template
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the modified date of this notification template.
	 *
	 * @return the modified date of this notification template
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this notification template.
	 *
	 * @return the mvcc version of this notification template
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this notification template.
	 *
	 * @return the name of this notification template
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this notification template in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this notification template
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this notification template in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this notification template. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this notification template in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this notification template
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this notification template in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this notification template
	 */
	@Override
	public String getName(String languageId, boolean useDefault) {
		return model.getName(languageId, useDefault);
	}

	@Override
	public String getNameCurrentLanguageId() {
		return model.getNameCurrentLanguageId();
	}

	@Override
	public String getNameCurrentValue() {
		return model.getNameCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized names of this notification template.
	 *
	 * @return the locales and localized names of this notification template
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	@Override
	public NotificationRecipient getNotificationRecipient()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getNotificationRecipient();
	}

	/**
	 * Returns the notification template ID of this notification template.
	 *
	 * @return the notification template ID of this notification template
	 */
	@Override
	public long getNotificationTemplateId() {
		return model.getNotificationTemplateId();
	}

	/**
	 * Returns the object definition ID of this notification template.
	 *
	 * @return the object definition ID of this notification template
	 */
	@Override
	public long getObjectDefinitionId() {
		return model.getObjectDefinitionId();
	}

	/**
	 * Returns the primary key of this notification template.
	 *
	 * @return the primary key of this notification template
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the recipient type of this notification template.
	 *
	 * @return the recipient type of this notification template
	 */
	@Override
	public String getRecipientType() {
		return model.getRecipientType();
	}

	/**
	 * Returns the subject of this notification template.
	 *
	 * @return the subject of this notification template
	 */
	@Override
	public String getSubject() {
		return model.getSubject();
	}

	/**
	 * Returns the localized subject of this notification template in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized subject of this notification template
	 */
	@Override
	public String getSubject(java.util.Locale locale) {
		return model.getSubject(locale);
	}

	/**
	 * Returns the localized subject of this notification template in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized subject of this notification template. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getSubject(java.util.Locale locale, boolean useDefault) {
		return model.getSubject(locale, useDefault);
	}

	/**
	 * Returns the localized subject of this notification template in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized subject of this notification template
	 */
	@Override
	public String getSubject(String languageId) {
		return model.getSubject(languageId);
	}

	/**
	 * Returns the localized subject of this notification template in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized subject of this notification template
	 */
	@Override
	public String getSubject(String languageId, boolean useDefault) {
		return model.getSubject(languageId, useDefault);
	}

	@Override
	public String getSubjectCurrentLanguageId() {
		return model.getSubjectCurrentLanguageId();
	}

	@Override
	public String getSubjectCurrentValue() {
		return model.getSubjectCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized subjects of this notification template.
	 *
	 * @return the locales and localized subjects of this notification template
	 */
	@Override
	public Map<java.util.Locale, String> getSubjectMap() {
		return model.getSubjectMap();
	}

	/**
	 * Returns the system of this notification template.
	 *
	 * @return the system of this notification template
	 */
	@Override
	public boolean getSystem() {
		return model.getSystem();
	}

	/**
	 * Returns the type of this notification template.
	 *
	 * @return the type of this notification template
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this notification template.
	 *
	 * @return the user ID of this notification template
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this notification template.
	 *
	 * @return the user name of this notification template
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this notification template.
	 *
	 * @return the user uuid of this notification template
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this notification template.
	 *
	 * @return the uuid of this notification template
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this notification template is system.
	 *
	 * @return <code>true</code> if this notification template is system; <code>false</code> otherwise
	 */
	@Override
	public boolean isSystem() {
		return model.isSystem();
	}

	@Override
	public void persist() {
		model.persist();
	}

	@Override
	public void prepareLocalizedFieldsForImport()
		throws com.liferay.portal.kernel.exception.LocaleException {

		model.prepareLocalizedFieldsForImport();
	}

	@Override
	public void prepareLocalizedFieldsForImport(
			java.util.Locale defaultImportLocale)
		throws com.liferay.portal.kernel.exception.LocaleException {

		model.prepareLocalizedFieldsForImport(defaultImportLocale);
	}

	/**
	 * Sets the body of this notification template.
	 *
	 * @param body the body of this notification template
	 */
	@Override
	public void setBody(String body) {
		model.setBody(body);
	}

	/**
	 * Sets the localized body of this notification template in the language.
	 *
	 * @param body the localized body of this notification template
	 * @param locale the locale of the language
	 */
	@Override
	public void setBody(String body, java.util.Locale locale) {
		model.setBody(body, locale);
	}

	/**
	 * Sets the localized body of this notification template in the language, and sets the default locale.
	 *
	 * @param body the localized body of this notification template
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setBody(
		String body, java.util.Locale locale, java.util.Locale defaultLocale) {

		model.setBody(body, locale, defaultLocale);
	}

	@Override
	public void setBodyCurrentLanguageId(String languageId) {
		model.setBodyCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized bodies of this notification template from the map of locales and localized bodies.
	 *
	 * @param bodyMap the locales and localized bodies of this notification template
	 */
	@Override
	public void setBodyMap(Map<java.util.Locale, String> bodyMap) {
		model.setBodyMap(bodyMap);
	}

	/**
	 * Sets the localized bodies of this notification template from the map of locales and localized bodies, and sets the default locale.
	 *
	 * @param bodyMap the locales and localized bodies of this notification template
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setBodyMap(
		Map<java.util.Locale, String> bodyMap, java.util.Locale defaultLocale) {

		model.setBodyMap(bodyMap, defaultLocale);
	}

	/**
	 * Sets the company ID of this notification template.
	 *
	 * @param companyId the company ID of this notification template
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this notification template.
	 *
	 * @param createDate the create date of this notification template
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the description of this notification template.
	 *
	 * @param description the description of this notification template
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the editor type of this notification template.
	 *
	 * @param editorType the editor type of this notification template
	 */
	@Override
	public void setEditorType(String editorType) {
		model.setEditorType(editorType);
	}

	/**
	 * Sets the external reference code of this notification template.
	 *
	 * @param externalReferenceCode the external reference code of this notification template
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the modified date of this notification template.
	 *
	 * @param modifiedDate the modified date of this notification template
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this notification template.
	 *
	 * @param mvccVersion the mvcc version of this notification template
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this notification template.
	 *
	 * @param name the name of this notification template
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this notification template in the language.
	 *
	 * @param name the localized name of this notification template
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this notification template in the language, and sets the default locale.
	 *
	 * @param name the localized name of this notification template
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setName(
		String name, java.util.Locale locale, java.util.Locale defaultLocale) {

		model.setName(name, locale, defaultLocale);
	}

	@Override
	public void setNameCurrentLanguageId(String languageId) {
		model.setNameCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized names of this notification template from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this notification template
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this notification template from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this notification template
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the notification template ID of this notification template.
	 *
	 * @param notificationTemplateId the notification template ID of this notification template
	 */
	@Override
	public void setNotificationTemplateId(long notificationTemplateId) {
		model.setNotificationTemplateId(notificationTemplateId);
	}

	/**
	 * Sets the object definition ID of this notification template.
	 *
	 * @param objectDefinitionId the object definition ID of this notification template
	 */
	@Override
	public void setObjectDefinitionId(long objectDefinitionId) {
		model.setObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Sets the primary key of this notification template.
	 *
	 * @param primaryKey the primary key of this notification template
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the recipient type of this notification template.
	 *
	 * @param recipientType the recipient type of this notification template
	 */
	@Override
	public void setRecipientType(String recipientType) {
		model.setRecipientType(recipientType);
	}

	/**
	 * Sets the subject of this notification template.
	 *
	 * @param subject the subject of this notification template
	 */
	@Override
	public void setSubject(String subject) {
		model.setSubject(subject);
	}

	/**
	 * Sets the localized subject of this notification template in the language.
	 *
	 * @param subject the localized subject of this notification template
	 * @param locale the locale of the language
	 */
	@Override
	public void setSubject(String subject, java.util.Locale locale) {
		model.setSubject(subject, locale);
	}

	/**
	 * Sets the localized subject of this notification template in the language, and sets the default locale.
	 *
	 * @param subject the localized subject of this notification template
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setSubject(
		String subject, java.util.Locale locale,
		java.util.Locale defaultLocale) {

		model.setSubject(subject, locale, defaultLocale);
	}

	@Override
	public void setSubjectCurrentLanguageId(String languageId) {
		model.setSubjectCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized subjects of this notification template from the map of locales and localized subjects.
	 *
	 * @param subjectMap the locales and localized subjects of this notification template
	 */
	@Override
	public void setSubjectMap(Map<java.util.Locale, String> subjectMap) {
		model.setSubjectMap(subjectMap);
	}

	/**
	 * Sets the localized subjects of this notification template from the map of locales and localized subjects, and sets the default locale.
	 *
	 * @param subjectMap the locales and localized subjects of this notification template
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setSubjectMap(
		Map<java.util.Locale, String> subjectMap,
		java.util.Locale defaultLocale) {

		model.setSubjectMap(subjectMap, defaultLocale);
	}

	/**
	 * Sets whether this notification template is system.
	 *
	 * @param system the system of this notification template
	 */
	@Override
	public void setSystem(boolean system) {
		model.setSystem(system);
	}

	/**
	 * Sets the type of this notification template.
	 *
	 * @param type the type of this notification template
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this notification template.
	 *
	 * @param userId the user ID of this notification template
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this notification template.
	 *
	 * @param userName the user name of this notification template
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this notification template.
	 *
	 * @param userUuid the user uuid of this notification template
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this notification template.
	 *
	 * @param uuid the uuid of this notification template
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected NotificationTemplateWrapper wrap(
		NotificationTemplate notificationTemplate) {

		return new NotificationTemplateWrapper(notificationTemplate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:401666449