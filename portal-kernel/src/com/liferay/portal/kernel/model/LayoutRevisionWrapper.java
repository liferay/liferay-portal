/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link LayoutRevision}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutRevision
 * @generated
 */
public class LayoutRevisionWrapper
	extends BaseModelWrapper<LayoutRevision>
	implements LayoutRevision, ModelWrapper<LayoutRevision> {

	public LayoutRevisionWrapper(LayoutRevision layoutRevision) {
		super(layoutRevision);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("layoutRevisionId", getLayoutRevisionId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("layoutSetBranchId", getLayoutSetBranchId());
		attributes.put("layoutBranchId", getLayoutBranchId());
		attributes.put("parentLayoutRevisionId", getParentLayoutRevisionId());
		attributes.put("head", isHead());
		attributes.put("major", isMajor());
		attributes.put("plid", getPlid());
		attributes.put("privateLayout", isPrivateLayout());
		attributes.put("name", getName());
		attributes.put("title", getTitle());
		attributes.put("description", getDescription());
		attributes.put("keywords", getKeywords());
		attributes.put("robots", getRobots());
		attributes.put("typeSettings", getTypeSettings());
		attributes.put("iconImageId", getIconImageId());
		attributes.put("themeId", getThemeId());
		attributes.put("colorSchemeId", getColorSchemeId());
		attributes.put("css", getCss());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long layoutRevisionId = (Long)attributes.get("layoutRevisionId");

		if (layoutRevisionId != null) {
			setLayoutRevisionId(layoutRevisionId);
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

		Long layoutSetBranchId = (Long)attributes.get("layoutSetBranchId");

		if (layoutSetBranchId != null) {
			setLayoutSetBranchId(layoutSetBranchId);
		}

		Long layoutBranchId = (Long)attributes.get("layoutBranchId");

		if (layoutBranchId != null) {
			setLayoutBranchId(layoutBranchId);
		}

		Long parentLayoutRevisionId = (Long)attributes.get(
			"parentLayoutRevisionId");

		if (parentLayoutRevisionId != null) {
			setParentLayoutRevisionId(parentLayoutRevisionId);
		}

		Boolean head = (Boolean)attributes.get("head");

		if (head != null) {
			setHead(head);
		}

		Boolean major = (Boolean)attributes.get("major");

		if (major != null) {
			setMajor(major);
		}

		Long plid = (Long)attributes.get("plid");

		if (plid != null) {
			setPlid(plid);
		}

		Boolean privateLayout = (Boolean)attributes.get("privateLayout");

		if (privateLayout != null) {
			setPrivateLayout(privateLayout);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String title = (String)attributes.get("title");

		if (title != null) {
			setTitle(title);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String keywords = (String)attributes.get("keywords");

		if (keywords != null) {
			setKeywords(keywords);
		}

		String robots = (String)attributes.get("robots");

		if (robots != null) {
			setRobots(robots);
		}

		String typeSettings = (String)attributes.get("typeSettings");

		if (typeSettings != null) {
			setTypeSettings(typeSettings);
		}

		Long iconImageId = (Long)attributes.get("iconImageId");

		if (iconImageId != null) {
			setIconImageId(iconImageId);
		}

		String themeId = (String)attributes.get("themeId");

		if (themeId != null) {
			setThemeId(themeId);
		}

		String colorSchemeId = (String)attributes.get("colorSchemeId");

		if (colorSchemeId != null) {
			setColorSchemeId(colorSchemeId);
		}

		String css = (String)attributes.get("css");

		if (css != null) {
			setCss(css);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		Long statusByUserId = (Long)attributes.get("statusByUserId");

		if (statusByUserId != null) {
			setStatusByUserId(statusByUserId);
		}

		String statusByUserName = (String)attributes.get("statusByUserName");

		if (statusByUserName != null) {
			setStatusByUserName(statusByUserName);
		}

		Date statusDate = (Date)attributes.get("statusDate");

		if (statusDate != null) {
			setStatusDate(statusDate);
		}
	}

	@Override
	public LayoutRevision cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	@Override
	public String getBreadcrumb(java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getBreadcrumb(locale);
	}

	@Override
	public java.util.List<LayoutRevision> getChildren() {
		return model.getChildren();
	}

	@Override
	public ColorScheme getColorScheme()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getColorScheme();
	}

	/**
	 * Returns the color scheme ID of this layout revision.
	 *
	 * @return the color scheme ID of this layout revision
	 */
	@Override
	public String getColorSchemeId() {
		return model.getColorSchemeId();
	}

	/**
	 * Returns the company ID of this layout revision.
	 *
	 * @return the company ID of this layout revision
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this layout revision.
	 *
	 * @return the create date of this layout revision
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the css of this layout revision.
	 *
	 * @return the css of this layout revision
	 */
	@Override
	public String getCss() {
		return model.getCss();
	}

	@Override
	public String getCssText()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCssText();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	/**
	 * Returns the description of this layout revision.
	 *
	 * @return the description of this layout revision
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this layout revision in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this layout revision
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this layout revision in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this layout revision. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this layout revision in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this layout revision
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this layout revision in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this layout revision
	 */
	@Override
	public String getDescription(String languageId, boolean useDefault) {
		return model.getDescription(languageId, useDefault);
	}

	@Override
	public String getDescriptionCurrentLanguageId() {
		return model.getDescriptionCurrentLanguageId();
	}

	@Override
	public String getDescriptionCurrentValue() {
		return model.getDescriptionCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized descriptions of this layout revision.
	 *
	 * @return the locales and localized descriptions of this layout revision
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	@Override
	public Group getGroup() {
		return model.getGroup();
	}

	/**
	 * Returns the group ID of this layout revision.
	 *
	 * @return the group ID of this layout revision
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the head of this layout revision.
	 *
	 * @return the head of this layout revision
	 */
	@Override
	public boolean getHead() {
		return model.getHead();
	}

	@Override
	public String getHTMLTitle(java.util.Locale locale) {
		return model.getHTMLTitle(locale);
	}

	@Override
	public String getHTMLTitle(String localeLanguageId) {
		return model.getHTMLTitle(localeLanguageId);
	}

	@Override
	public boolean getIconImage() {
		return model.getIconImage();
	}

	/**
	 * Returns the icon image ID of this layout revision.
	 *
	 * @return the icon image ID of this layout revision
	 */
	@Override
	public long getIconImageId() {
		return model.getIconImageId();
	}

	/**
	 * Returns the keywords of this layout revision.
	 *
	 * @return the keywords of this layout revision
	 */
	@Override
	public String getKeywords() {
		return model.getKeywords();
	}

	/**
	 * Returns the localized keywords of this layout revision in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized keywords of this layout revision
	 */
	@Override
	public String getKeywords(java.util.Locale locale) {
		return model.getKeywords(locale);
	}

	/**
	 * Returns the localized keywords of this layout revision in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized keywords of this layout revision. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getKeywords(java.util.Locale locale, boolean useDefault) {
		return model.getKeywords(locale, useDefault);
	}

	/**
	 * Returns the localized keywords of this layout revision in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized keywords of this layout revision
	 */
	@Override
	public String getKeywords(String languageId) {
		return model.getKeywords(languageId);
	}

	/**
	 * Returns the localized keywords of this layout revision in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized keywords of this layout revision
	 */
	@Override
	public String getKeywords(String languageId, boolean useDefault) {
		return model.getKeywords(languageId, useDefault);
	}

	@Override
	public String getKeywordsCurrentLanguageId() {
		return model.getKeywordsCurrentLanguageId();
	}

	@Override
	public String getKeywordsCurrentValue() {
		return model.getKeywordsCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized keywordses of this layout revision.
	 *
	 * @return the locales and localized keywordses of this layout revision
	 */
	@Override
	public Map<java.util.Locale, String> getKeywordsMap() {
		return model.getKeywordsMap();
	}

	@Override
	public LayoutBranch getLayoutBranch()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getLayoutBranch();
	}

	/**
	 * Returns the layout branch ID of this layout revision.
	 *
	 * @return the layout branch ID of this layout revision
	 */
	@Override
	public long getLayoutBranchId() {
		return model.getLayoutBranchId();
	}

	/**
	 * Returns the layout revision ID of this layout revision.
	 *
	 * @return the layout revision ID of this layout revision
	 */
	@Override
	public long getLayoutRevisionId() {
		return model.getLayoutRevisionId();
	}

	@Override
	public LayoutSet getLayoutSet()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getLayoutSet();
	}

	/**
	 * Returns the layout set branch ID of this layout revision.
	 *
	 * @return the layout set branch ID of this layout revision
	 */
	@Override
	public long getLayoutSetBranchId() {
		return model.getLayoutSetBranchId();
	}

	/**
	 * Returns the major of this layout revision.
	 *
	 * @return the major of this layout revision
	 */
	@Override
	public boolean getMajor() {
		return model.getMajor();
	}

	/**
	 * Returns the modified date of this layout revision.
	 *
	 * @return the modified date of this layout revision
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this layout revision.
	 *
	 * @return the mvcc version of this layout revision
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this layout revision.
	 *
	 * @return the name of this layout revision
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this layout revision in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this layout revision
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this layout revision in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this layout revision. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this layout revision in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this layout revision
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this layout revision in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this layout revision
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
	 * Returns a map of the locales and localized names of this layout revision.
	 *
	 * @return the locales and localized names of this layout revision
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the parent layout revision ID of this layout revision.
	 *
	 * @return the parent layout revision ID of this layout revision
	 */
	@Override
	public long getParentLayoutRevisionId() {
		return model.getParentLayoutRevisionId();
	}

	/**
	 * Returns the plid of this layout revision.
	 *
	 * @return the plid of this layout revision
	 */
	@Override
	public long getPlid() {
		return model.getPlid();
	}

	/**
	 * Returns the primary key of this layout revision.
	 *
	 * @return the primary key of this layout revision
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the private layout of this layout revision.
	 *
	 * @return the private layout of this layout revision
	 */
	@Override
	public boolean getPrivateLayout() {
		return model.getPrivateLayout();
	}

	@Override
	public String getRegularURL(
			jakarta.servlet.http.HttpServletRequest httpServletRequest)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getRegularURL(httpServletRequest);
	}

	/**
	 * Returns the robots of this layout revision.
	 *
	 * @return the robots of this layout revision
	 */
	@Override
	public String getRobots() {
		return model.getRobots();
	}

	/**
	 * Returns the localized robots of this layout revision in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized robots of this layout revision
	 */
	@Override
	public String getRobots(java.util.Locale locale) {
		return model.getRobots(locale);
	}

	/**
	 * Returns the localized robots of this layout revision in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized robots of this layout revision. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getRobots(java.util.Locale locale, boolean useDefault) {
		return model.getRobots(locale, useDefault);
	}

	/**
	 * Returns the localized robots of this layout revision in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized robots of this layout revision
	 */
	@Override
	public String getRobots(String languageId) {
		return model.getRobots(languageId);
	}

	/**
	 * Returns the localized robots of this layout revision in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized robots of this layout revision
	 */
	@Override
	public String getRobots(String languageId, boolean useDefault) {
		return model.getRobots(languageId, useDefault);
	}

	@Override
	public String getRobotsCurrentLanguageId() {
		return model.getRobotsCurrentLanguageId();
	}

	@Override
	public String getRobotsCurrentValue() {
		return model.getRobotsCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized robotses of this layout revision.
	 *
	 * @return the locales and localized robotses of this layout revision
	 */
	@Override
	public Map<java.util.Locale, String> getRobotsMap() {
		return model.getRobotsMap();
	}

	/**
	 * Returns the status of this layout revision.
	 *
	 * @return the status of this layout revision
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this layout revision.
	 *
	 * @return the status by user ID of this layout revision
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this layout revision.
	 *
	 * @return the status by user name of this layout revision
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this layout revision.
	 *
	 * @return the status by user uuid of this layout revision
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this layout revision.
	 *
	 * @return the status date of this layout revision
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	@Override
	public String getTarget() {
		return model.getTarget();
	}

	@Override
	public Theme getTheme()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getTheme();
	}

	/**
	 * Returns the theme ID of this layout revision.
	 *
	 * @return the theme ID of this layout revision
	 */
	@Override
	public String getThemeId() {
		return model.getThemeId();
	}

	@Override
	public String getThemeSetting(String key, String device) {
		return model.getThemeSetting(key, device);
	}

	/**
	 * Returns the title of this layout revision.
	 *
	 * @return the title of this layout revision
	 */
	@Override
	public String getTitle() {
		return model.getTitle();
	}

	/**
	 * Returns the localized title of this layout revision in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized title of this layout revision
	 */
	@Override
	public String getTitle(java.util.Locale locale) {
		return model.getTitle(locale);
	}

	/**
	 * Returns the localized title of this layout revision in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this layout revision. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getTitle(java.util.Locale locale, boolean useDefault) {
		return model.getTitle(locale, useDefault);
	}

	/**
	 * Returns the localized title of this layout revision in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized title of this layout revision
	 */
	@Override
	public String getTitle(String languageId) {
		return model.getTitle(languageId);
	}

	/**
	 * Returns the localized title of this layout revision in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this layout revision
	 */
	@Override
	public String getTitle(String languageId, boolean useDefault) {
		return model.getTitle(languageId, useDefault);
	}

	@Override
	public String getTitleCurrentLanguageId() {
		return model.getTitleCurrentLanguageId();
	}

	@Override
	public String getTitleCurrentValue() {
		return model.getTitleCurrentValue();
	}

	/**
	 * Returns a map of the locales and localized titles of this layout revision.
	 *
	 * @return the locales and localized titles of this layout revision
	 */
	@Override
	public Map<java.util.Locale, String> getTitleMap() {
		return model.getTitleMap();
	}

	/**
	 * Returns the type settings of this layout revision.
	 *
	 * @return the type settings of this layout revision
	 */
	@Override
	public String getTypeSettings() {
		return model.getTypeSettings();
	}

	@Override
	public com.liferay.portal.kernel.util.UnicodeProperties
		getTypeSettingsProperties() {

		return model.getTypeSettingsProperties();
	}

	@Override
	public String getTypeSettingsProperty(String key) {
		return model.getTypeSettingsProperty(key);
	}

	@Override
	public String getTypeSettingsProperty(String key, String defaultValue) {
		return model.getTypeSettingsProperty(key, defaultValue);
	}

	/**
	 * Returns the user ID of this layout revision.
	 *
	 * @return the user ID of this layout revision
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this layout revision.
	 *
	 * @return the user name of this layout revision
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this layout revision.
	 *
	 * @return the user uuid of this layout revision
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public boolean hasChildren() {
		return model.hasChildren();
	}

	/**
	 * Returns <code>true</code> if this layout revision is approved.
	 *
	 * @return <code>true</code> if this layout revision is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	@Override
	public boolean isContentDisplayPage() {
		return model.isContentDisplayPage();
	}

	@Override
	public boolean isCustomizable()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.isCustomizable();
	}

	/**
	 * Returns <code>true</code> if this layout revision is denied.
	 *
	 * @return <code>true</code> if this layout revision is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this layout revision is a draft.
	 *
	 * @return <code>true</code> if this layout revision is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this layout revision is expired.
	 *
	 * @return <code>true</code> if this layout revision is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this layout revision is head.
	 *
	 * @return <code>true</code> if this layout revision is head; <code>false</code> otherwise
	 */
	@Override
	public boolean isHead() {
		return model.isHead();
	}

	@Override
	public boolean isIconImage() {
		return model.isIconImage();
	}

	/**
	 * Returns <code>true</code> if this layout revision is inactive.
	 *
	 * @return <code>true</code> if this layout revision is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this layout revision is incomplete.
	 *
	 * @return <code>true</code> if this layout revision is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	@Override
	public boolean isInheritLookAndFeel() {
		return model.isInheritLookAndFeel();
	}

	/**
	 * Returns <code>true</code> if this layout revision is major.
	 *
	 * @return <code>true</code> if this layout revision is major; <code>false</code> otherwise
	 */
	@Override
	public boolean isMajor() {
		return model.isMajor();
	}

	/**
	 * Returns <code>true</code> if this layout revision is pending.
	 *
	 * @return <code>true</code> if this layout revision is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this layout revision is private layout.
	 *
	 * @return <code>true</code> if this layout revision is private layout; <code>false</code> otherwise
	 */
	@Override
	public boolean isPrivateLayout() {
		return model.isPrivateLayout();
	}

	/**
	 * Returns <code>true</code> if this layout revision is scheduled.
	 *
	 * @return <code>true</code> if this layout revision is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
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
	 * Sets the color scheme ID of this layout revision.
	 *
	 * @param colorSchemeId the color scheme ID of this layout revision
	 */
	@Override
	public void setColorSchemeId(String colorSchemeId) {
		model.setColorSchemeId(colorSchemeId);
	}

	/**
	 * Sets the company ID of this layout revision.
	 *
	 * @param companyId the company ID of this layout revision
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this layout revision.
	 *
	 * @param createDate the create date of this layout revision
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the css of this layout revision.
	 *
	 * @param css the css of this layout revision
	 */
	@Override
	public void setCss(String css) {
		model.setCss(css);
	}

	/**
	 * Sets the description of this layout revision.
	 *
	 * @param description the description of this layout revision
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this layout revision in the language.
	 *
	 * @param description the localized description of this layout revision
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this layout revision in the language, and sets the default locale.
	 *
	 * @param description the localized description of this layout revision
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescription(
		String description, java.util.Locale locale,
		java.util.Locale defaultLocale) {

		model.setDescription(description, locale, defaultLocale);
	}

	@Override
	public void setDescriptionCurrentLanguageId(String languageId) {
		model.setDescriptionCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized descriptions of this layout revision from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this layout revision
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this layout revision from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this layout revision
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the group ID of this layout revision.
	 *
	 * @param groupId the group ID of this layout revision
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets whether this layout revision is head.
	 *
	 * @param head the head of this layout revision
	 */
	@Override
	public void setHead(boolean head) {
		model.setHead(head);
	}

	/**
	 * Sets the icon image ID of this layout revision.
	 *
	 * @param iconImageId the icon image ID of this layout revision
	 */
	@Override
	public void setIconImageId(long iconImageId) {
		model.setIconImageId(iconImageId);
	}

	/**
	 * Sets the keywords of this layout revision.
	 *
	 * @param keywords the keywords of this layout revision
	 */
	@Override
	public void setKeywords(String keywords) {
		model.setKeywords(keywords);
	}

	/**
	 * Sets the localized keywords of this layout revision in the language.
	 *
	 * @param keywords the localized keywords of this layout revision
	 * @param locale the locale of the language
	 */
	@Override
	public void setKeywords(String keywords, java.util.Locale locale) {
		model.setKeywords(keywords, locale);
	}

	/**
	 * Sets the localized keywords of this layout revision in the language, and sets the default locale.
	 *
	 * @param keywords the localized keywords of this layout revision
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setKeywords(
		String keywords, java.util.Locale locale,
		java.util.Locale defaultLocale) {

		model.setKeywords(keywords, locale, defaultLocale);
	}

	@Override
	public void setKeywordsCurrentLanguageId(String languageId) {
		model.setKeywordsCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized keywordses of this layout revision from the map of locales and localized keywordses.
	 *
	 * @param keywordsMap the locales and localized keywordses of this layout revision
	 */
	@Override
	public void setKeywordsMap(Map<java.util.Locale, String> keywordsMap) {
		model.setKeywordsMap(keywordsMap);
	}

	/**
	 * Sets the localized keywordses of this layout revision from the map of locales and localized keywordses, and sets the default locale.
	 *
	 * @param keywordsMap the locales and localized keywordses of this layout revision
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setKeywordsMap(
		Map<java.util.Locale, String> keywordsMap,
		java.util.Locale defaultLocale) {

		model.setKeywordsMap(keywordsMap, defaultLocale);
	}

	/**
	 * Sets the layout branch ID of this layout revision.
	 *
	 * @param layoutBranchId the layout branch ID of this layout revision
	 */
	@Override
	public void setLayoutBranchId(long layoutBranchId) {
		model.setLayoutBranchId(layoutBranchId);
	}

	/**
	 * Sets the layout revision ID of this layout revision.
	 *
	 * @param layoutRevisionId the layout revision ID of this layout revision
	 */
	@Override
	public void setLayoutRevisionId(long layoutRevisionId) {
		model.setLayoutRevisionId(layoutRevisionId);
	}

	/**
	 * Sets the layout set branch ID of this layout revision.
	 *
	 * @param layoutSetBranchId the layout set branch ID of this layout revision
	 */
	@Override
	public void setLayoutSetBranchId(long layoutSetBranchId) {
		model.setLayoutSetBranchId(layoutSetBranchId);
	}

	/**
	 * Sets whether this layout revision is major.
	 *
	 * @param major the major of this layout revision
	 */
	@Override
	public void setMajor(boolean major) {
		model.setMajor(major);
	}

	/**
	 * Sets the modified date of this layout revision.
	 *
	 * @param modifiedDate the modified date of this layout revision
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this layout revision.
	 *
	 * @param mvccVersion the mvcc version of this layout revision
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this layout revision.
	 *
	 * @param name the name of this layout revision
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this layout revision in the language.
	 *
	 * @param name the localized name of this layout revision
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this layout revision in the language, and sets the default locale.
	 *
	 * @param name the localized name of this layout revision
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
	 * Sets the localized names of this layout revision from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this layout revision
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this layout revision from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this layout revision
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the parent layout revision ID of this layout revision.
	 *
	 * @param parentLayoutRevisionId the parent layout revision ID of this layout revision
	 */
	@Override
	public void setParentLayoutRevisionId(long parentLayoutRevisionId) {
		model.setParentLayoutRevisionId(parentLayoutRevisionId);
	}

	/**
	 * Sets the plid of this layout revision.
	 *
	 * @param plid the plid of this layout revision
	 */
	@Override
	public void setPlid(long plid) {
		model.setPlid(plid);
	}

	/**
	 * Sets the primary key of this layout revision.
	 *
	 * @param primaryKey the primary key of this layout revision
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets whether this layout revision is private layout.
	 *
	 * @param privateLayout the private layout of this layout revision
	 */
	@Override
	public void setPrivateLayout(boolean privateLayout) {
		model.setPrivateLayout(privateLayout);
	}

	/**
	 * Sets the robots of this layout revision.
	 *
	 * @param robots the robots of this layout revision
	 */
	@Override
	public void setRobots(String robots) {
		model.setRobots(robots);
	}

	/**
	 * Sets the localized robots of this layout revision in the language.
	 *
	 * @param robots the localized robots of this layout revision
	 * @param locale the locale of the language
	 */
	@Override
	public void setRobots(String robots, java.util.Locale locale) {
		model.setRobots(robots, locale);
	}

	/**
	 * Sets the localized robots of this layout revision in the language, and sets the default locale.
	 *
	 * @param robots the localized robots of this layout revision
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setRobots(
		String robots, java.util.Locale locale,
		java.util.Locale defaultLocale) {

		model.setRobots(robots, locale, defaultLocale);
	}

	@Override
	public void setRobotsCurrentLanguageId(String languageId) {
		model.setRobotsCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized robotses of this layout revision from the map of locales and localized robotses.
	 *
	 * @param robotsMap the locales and localized robotses of this layout revision
	 */
	@Override
	public void setRobotsMap(Map<java.util.Locale, String> robotsMap) {
		model.setRobotsMap(robotsMap);
	}

	/**
	 * Sets the localized robotses of this layout revision from the map of locales and localized robotses, and sets the default locale.
	 *
	 * @param robotsMap the locales and localized robotses of this layout revision
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setRobotsMap(
		Map<java.util.Locale, String> robotsMap,
		java.util.Locale defaultLocale) {

		model.setRobotsMap(robotsMap, defaultLocale);
	}

	/**
	 * Sets the status of this layout revision.
	 *
	 * @param status the status of this layout revision
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this layout revision.
	 *
	 * @param statusByUserId the status by user ID of this layout revision
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this layout revision.
	 *
	 * @param statusByUserName the status by user name of this layout revision
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this layout revision.
	 *
	 * @param statusByUserUuid the status by user uuid of this layout revision
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this layout revision.
	 *
	 * @param statusDate the status date of this layout revision
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the theme ID of this layout revision.
	 *
	 * @param themeId the theme ID of this layout revision
	 */
	@Override
	public void setThemeId(String themeId) {
		model.setThemeId(themeId);
	}

	/**
	 * Sets the title of this layout revision.
	 *
	 * @param title the title of this layout revision
	 */
	@Override
	public void setTitle(String title) {
		model.setTitle(title);
	}

	/**
	 * Sets the localized title of this layout revision in the language.
	 *
	 * @param title the localized title of this layout revision
	 * @param locale the locale of the language
	 */
	@Override
	public void setTitle(String title, java.util.Locale locale) {
		model.setTitle(title, locale);
	}

	/**
	 * Sets the localized title of this layout revision in the language, and sets the default locale.
	 *
	 * @param title the localized title of this layout revision
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitle(
		String title, java.util.Locale locale, java.util.Locale defaultLocale) {

		model.setTitle(title, locale, defaultLocale);
	}

	@Override
	public void setTitleCurrentLanguageId(String languageId) {
		model.setTitleCurrentLanguageId(languageId);
	}

	/**
	 * Sets the localized titles of this layout revision from the map of locales and localized titles.
	 *
	 * @param titleMap the locales and localized titles of this layout revision
	 */
	@Override
	public void setTitleMap(Map<java.util.Locale, String> titleMap) {
		model.setTitleMap(titleMap);
	}

	/**
	 * Sets the localized titles of this layout revision from the map of locales and localized titles, and sets the default locale.
	 *
	 * @param titleMap the locales and localized titles of this layout revision
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitleMap(
		Map<java.util.Locale, String> titleMap,
		java.util.Locale defaultLocale) {

		model.setTitleMap(titleMap, defaultLocale);
	}

	/**
	 * Sets the type settings of this layout revision.
	 *
	 * @param typeSettings the type settings of this layout revision
	 */
	@Override
	public void setTypeSettings(String typeSettings) {
		model.setTypeSettings(typeSettings);
	}

	@Override
	public void setTypeSettingsProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			typeSettingsUnicodeProperties) {

		model.setTypeSettingsProperties(typeSettingsUnicodeProperties);
	}

	/**
	 * Sets the user ID of this layout revision.
	 *
	 * @param userId the user ID of this layout revision
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this layout revision.
	 *
	 * @param userName the user name of this layout revision
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this layout revision.
	 *
	 * @param userUuid the user uuid of this layout revision
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected LayoutRevisionWrapper wrap(LayoutRevision layoutRevision) {
		return new LayoutRevisionWrapper(layoutRevision);
	}

}