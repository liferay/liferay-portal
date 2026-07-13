/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link LayoutPageTemplateStructureRelElementVariation}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariation
 * @generated
 */
public class LayoutPageTemplateStructureRelElementVariationWrapper
	extends BaseModelWrapper<LayoutPageTemplateStructureRelElementVariation>
	implements LayoutPageTemplateStructureRelElementVariation,
			   ModelWrapper<LayoutPageTemplateStructureRelElementVariation> {

	public LayoutPageTemplateStructureRelElementVariationWrapper(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		super(layoutPageTemplateStructureRelElementVariation);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put(
			"layoutPageTemplateStructureRelElementVariationId",
			getLayoutPageTemplateStructureRelElementVariationId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("active", isActive());
		attributes.put("hide", getHide());
		attributes.put("html", getHtml());
		attributes.put("js", getJs());
		attributes.put("name", getName());
		attributes.put("plid", getPlid());
		attributes.put("segmentsExperienceERC", getSegmentsExperienceERC());
		attributes.put("targetElement", getTargetElement());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
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

		Long layoutPageTemplateStructureRelElementVariationId =
			(Long)attributes.get(
				"layoutPageTemplateStructureRelElementVariationId");

		if (layoutPageTemplateStructureRelElementVariationId != null) {
			setLayoutPageTemplateStructureRelElementVariationId(
				layoutPageTemplateStructureRelElementVariationId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
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

		Boolean active = (Boolean)attributes.get("active");

		if (active != null) {
			setActive(active);
		}

		String hide = (String)attributes.get("hide");

		if (hide != null) {
			setHide(hide);
		}

		String html = (String)attributes.get("html");

		if (html != null) {
			setHtml(html);
		}

		String js = (String)attributes.get("js");

		if (js != null) {
			setJs(js);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Long plid = (Long)attributes.get("plid");

		if (plid != null) {
			setPlid(plid);
		}

		String segmentsExperienceERC = (String)attributes.get(
			"segmentsExperienceERC");

		if (segmentsExperienceERC != null) {
			setSegmentsExperienceERC(segmentsExperienceERC);
		}

		String targetElement = (String)attributes.get("targetElement");

		if (targetElement != null) {
			setTargetElement(targetElement);
		}
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation
		cloneWithOriginalValues() {

		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the active of this layout page template structure rel element variation.
	 *
	 * @return the active of this layout page template structure rel element variation
	 */
	@Override
	public boolean getActive() {
		return model.getActive();
	}

	@Override
	public java.util.List<String> getAudienceEntryERCs() {
		return model.getAudienceEntryERCs();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	/**
	 * Returns the company ID of this layout page template structure rel element variation.
	 *
	 * @return the company ID of this layout page template structure rel element variation
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this layout page template structure rel element variation.
	 *
	 * @return the create date of this layout page template structure rel element variation
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this layout page template structure rel element variation.
	 *
	 * @return the ct collection ID of this layout page template structure rel element variation
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	/**
	 * Returns the external reference code of this layout page template structure rel element variation.
	 *
	 * @return the external reference code of this layout page template structure rel element variation
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this layout page template structure rel element variation.
	 *
	 * @return the group ID of this layout page template structure rel element variation
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the hide of this layout page template structure rel element variation.
	 *
	 * @return the hide of this layout page template structure rel element variation
	 */
	@Override
	public String getHide() {
		return model.getHide();
	}

	/**
	 * Returns the html of this layout page template structure rel element variation.
	 *
	 * @return the html of this layout page template structure rel element variation
	 */
	@Override
	public String getHtml() {
		return model.getHtml();
	}

	/**
	 * Returns the localized html of this layout page template structure rel element variation in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized html of this layout page template structure rel element variation
	 */
	@Override
	public String getHtml(java.util.Locale locale) {
		return model.getHtml(locale);
	}

	/**
	 * Returns the localized html of this layout page template structure rel element variation in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized html of this layout page template structure rel element variation. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getHtml(java.util.Locale locale, boolean useDefault) {
		return model.getHtml(locale, useDefault);
	}

	/**
	 * Returns the localized html of this layout page template structure rel element variation in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized html of this layout page template structure rel element variation
	 */
	@Override
	public String getHtml(String languageId) {
		return model.getHtml(languageId);
	}

	/**
	 * Returns the localized html of this layout page template structure rel element variation in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized html of this layout page template structure rel element variation
	 */
	@Override
	public String getHtml(String languageId, boolean useDefault) {
		return model.getHtml(languageId, useDefault);
	}

	@Override
	public String getHtmlCurrentLanguageId() {
		return model.getHtmlCurrentLanguageId();
	}

	@Override
	public String getHtmlCurrentValue() {
		return model.getHtmlCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized htmls of this layout page template structure rel element variation.
	 *
	 * @return the locales and localized htmls of this layout page template structure rel element variation
	 */
	@Override
	public Map<java.util.Locale, String> getHtmlMap() {
		return model.getHtmlMap();
	}

	/**
	 * Returns the js of this layout page template structure rel element variation.
	 *
	 * @return the js of this layout page template structure rel element variation
	 */
	@Override
	public String getJs() {
		return model.getJs();
	}

	/**
	 * Returns the localized js of this layout page template structure rel element variation in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized js of this layout page template structure rel element variation
	 */
	@Override
	public String getJs(java.util.Locale locale) {
		return model.getJs(locale);
	}

	/**
	 * Returns the localized js of this layout page template structure rel element variation in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized js of this layout page template structure rel element variation. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getJs(java.util.Locale locale, boolean useDefault) {
		return model.getJs(locale, useDefault);
	}

	/**
	 * Returns the localized js of this layout page template structure rel element variation in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized js of this layout page template structure rel element variation
	 */
	@Override
	public String getJs(String languageId) {
		return model.getJs(languageId);
	}

	/**
	 * Returns the localized js of this layout page template structure rel element variation in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized js of this layout page template structure rel element variation
	 */
	@Override
	public String getJs(String languageId, boolean useDefault) {
		return model.getJs(languageId, useDefault);
	}

	@Override
	public String getJsCurrentLanguageId() {
		return model.getJsCurrentLanguageId();
	}

	@Override
	public String getJsCurrentValue() {
		return model.getJsCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized jses of this layout page template structure rel element variation.
	 *
	 * @return the locales and localized jses of this layout page template structure rel element variation
	 */
	@Override
	public Map<java.util.Locale, String> getJsMap() {
		return model.getJsMap();
	}

	/**
	 * Returns the layout page template structure rel element variation ID of this layout page template structure rel element variation.
	 *
	 * @return the layout page template structure rel element variation ID of this layout page template structure rel element variation
	 */
	@Override
	public long getLayoutPageTemplateStructureRelElementVariationId() {
		return model.getLayoutPageTemplateStructureRelElementVariationId();
	}

	/**
	 * Returns the modified date of this layout page template structure rel element variation.
	 *
	 * @return the modified date of this layout page template structure rel element variation
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this layout page template structure rel element variation.
	 *
	 * @return the mvcc version of this layout page template structure rel element variation
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this layout page template structure rel element variation.
	 *
	 * @return the name of this layout page template structure rel element variation
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the plid of this layout page template structure rel element variation.
	 *
	 * @return the plid of this layout page template structure rel element variation
	 */
	@Override
	public long getPlid() {
		return model.getPlid();
	}

	/**
	 * Returns the primary key of this layout page template structure rel element variation.
	 *
	 * @return the primary key of this layout page template structure rel element variation
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the segments experience erc of this layout page template structure rel element variation.
	 *
	 * @return the segments experience erc of this layout page template structure rel element variation
	 */
	@Override
	public String getSegmentsExperienceERC() {
		return model.getSegmentsExperienceERC();
	}

	/**
	 * Returns the target element of this layout page template structure rel element variation.
	 *
	 * @return the target element of this layout page template structure rel element variation
	 */
	@Override
	public String getTargetElement() {
		return model.getTargetElement();
	}

	/**
	 * Returns the user ID of this layout page template structure rel element variation.
	 *
	 * @return the user ID of this layout page template structure rel element variation
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this layout page template structure rel element variation.
	 *
	 * @return the user name of this layout page template structure rel element variation
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this layout page template structure rel element variation.
	 *
	 * @return the user uuid of this layout page template structure rel element variation
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this layout page template structure rel element variation.
	 *
	 * @return the uuid of this layout page template structure rel element variation
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this layout page template structure rel element variation is active.
	 *
	 * @return <code>true</code> if this layout page template structure rel element variation is active; <code>false</code> otherwise
	 */
	@Override
	public boolean isActive() {
		return model.isActive();
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
	 * Sets whether this layout page template structure rel element variation is active.
	 *
	 * @param active the active of this layout page template structure rel element variation
	 */
	@Override
	public void setActive(boolean active) {
		model.setActive(active);
	}

	/**
	 * Sets the company ID of this layout page template structure rel element variation.
	 *
	 * @param companyId the company ID of this layout page template structure rel element variation
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this layout page template structure rel element variation.
	 *
	 * @param createDate the create date of this layout page template structure rel element variation
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this layout page template structure rel element variation.
	 *
	 * @param ctCollectionId the ct collection ID of this layout page template structure rel element variation
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the external reference code of this layout page template structure rel element variation.
	 *
	 * @param externalReferenceCode the external reference code of this layout page template structure rel element variation
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this layout page template structure rel element variation.
	 *
	 * @param groupId the group ID of this layout page template structure rel element variation
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the hide of this layout page template structure rel element variation.
	 *
	 * @param hide the hide of this layout page template structure rel element variation
	 */
	@Override
	public void setHide(String hide) {
		model.setHide(hide);
	}

	/**
	 * Sets the html of this layout page template structure rel element variation.
	 *
	 * @param html the html of this layout page template structure rel element variation
	 */
	@Override
	public void setHtml(String html) {
		model.setHtml(html);
	}

	/**
	 * Sets the localized html of this layout page template structure rel element variation in the language.
	 *
	 * @param html the localized html of this layout page template structure rel element variation
	 * @param locale the locale of the language
	 */
	@Override
	public void setHtml(String html, java.util.Locale locale) {
		model.setHtml(html, locale);
	}

	/**
	 * Sets the localized html of this layout page template structure rel element variation in the language, and sets the default locale.
	 *
	 * @param html the localized html of this layout page template structure rel element variation
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setHtml(
		String html, java.util.Locale locale, java.util.Locale defaultLocale) {

		model.setHtml(html, locale, defaultLocale);
	}

	@Override
	public void setHtmlCurrentLanguageId(String languageId) {
		model.setHtmlCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized htmls of this layout page template structure rel element variation from the map of locales and localized htmls.
	 *
	 * @param htmlMap the locales and localized htmls of this layout page template structure rel element variation
	 */
	@Override
	public void setHtmlMap(Map<java.util.Locale, String> htmlMap) {
		model.setHtmlMap(htmlMap);
	}

	/**
	 * Sets the localized htmls of this layout page template structure rel element variation from the map of locales and localized htmls, and sets the default locale.
	 *
	 * @param htmlMap the locales and localized htmls of this layout page template structure rel element variation
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setHtmlMap(
		Map<java.util.Locale, String> htmlMap, java.util.Locale defaultLocale) {

		model.setHtmlMap(htmlMap, defaultLocale);
	}

	/**
	 * Sets the js of this layout page template structure rel element variation.
	 *
	 * @param js the js of this layout page template structure rel element variation
	 */
	@Override
	public void setJs(String js) {
		model.setJs(js);
	}

	/**
	 * Sets the localized js of this layout page template structure rel element variation in the language.
	 *
	 * @param js the localized js of this layout page template structure rel element variation
	 * @param locale the locale of the language
	 */
	@Override
	public void setJs(String js, java.util.Locale locale) {
		model.setJs(js, locale);
	}

	/**
	 * Sets the localized js of this layout page template structure rel element variation in the language, and sets the default locale.
	 *
	 * @param js the localized js of this layout page template structure rel element variation
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setJs(
		String js, java.util.Locale locale, java.util.Locale defaultLocale) {

		model.setJs(js, locale, defaultLocale);
	}

	@Override
	public void setJsCurrentLanguageId(String languageId) {
		model.setJsCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized jses of this layout page template structure rel element variation from the map of locales and localized jses.
	 *
	 * @param jsMap the locales and localized jses of this layout page template structure rel element variation
	 */
	@Override
	public void setJsMap(Map<java.util.Locale, String> jsMap) {
		model.setJsMap(jsMap);
	}

	/**
	 * Sets the localized jses of this layout page template structure rel element variation from the map of locales and localized jses, and sets the default locale.
	 *
	 * @param jsMap the locales and localized jses of this layout page template structure rel element variation
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setJsMap(
		Map<java.util.Locale, String> jsMap, java.util.Locale defaultLocale) {

		model.setJsMap(jsMap, defaultLocale);
	}

	/**
	 * Sets the layout page template structure rel element variation ID of this layout page template structure rel element variation.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the layout page template structure rel element variation ID of this layout page template structure rel element variation
	 */
	@Override
	public void setLayoutPageTemplateStructureRelElementVariationId(
		long layoutPageTemplateStructureRelElementVariationId) {

		model.setLayoutPageTemplateStructureRelElementVariationId(
			layoutPageTemplateStructureRelElementVariationId);
	}

	/**
	 * Sets the modified date of this layout page template structure rel element variation.
	 *
	 * @param modifiedDate the modified date of this layout page template structure rel element variation
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this layout page template structure rel element variation.
	 *
	 * @param mvccVersion the mvcc version of this layout page template structure rel element variation
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this layout page template structure rel element variation.
	 *
	 * @param name the name of this layout page template structure rel element variation
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the plid of this layout page template structure rel element variation.
	 *
	 * @param plid the plid of this layout page template structure rel element variation
	 */
	@Override
	public void setPlid(long plid) {
		model.setPlid(plid);
	}

	/**
	 * Sets the primary key of this layout page template structure rel element variation.
	 *
	 * @param primaryKey the primary key of this layout page template structure rel element variation
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the segments experience erc of this layout page template structure rel element variation.
	 *
	 * @param segmentsExperienceERC the segments experience erc of this layout page template structure rel element variation
	 */
	@Override
	public void setSegmentsExperienceERC(String segmentsExperienceERC) {
		model.setSegmentsExperienceERC(segmentsExperienceERC);
	}

	/**
	 * Sets the target element of this layout page template structure rel element variation.
	 *
	 * @param targetElement the target element of this layout page template structure rel element variation
	 */
	@Override
	public void setTargetElement(String targetElement) {
		model.setTargetElement(targetElement);
	}

	/**
	 * Sets the user ID of this layout page template structure rel element variation.
	 *
	 * @param userId the user ID of this layout page template structure rel element variation
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this layout page template structure rel element variation.
	 *
	 * @param userName the user name of this layout page template structure rel element variation
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this layout page template structure rel element variation.
	 *
	 * @param userUuid the user uuid of this layout page template structure rel element variation
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this layout page template structure rel element variation.
	 *
	 * @param uuid the uuid of this layout page template structure rel element variation
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
	public Map
		<String,
		 Function<LayoutPageTemplateStructureRelElementVariation, Object>>
			getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map
		<String,
		 BiConsumer<LayoutPageTemplateStructureRelElementVariation, Object>>
			getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected LayoutPageTemplateStructureRelElementVariationWrapper wrap(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		return new LayoutPageTemplateStructureRelElementVariationWrapper(
			layoutPageTemplateStructureRelElementVariation);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1474912710