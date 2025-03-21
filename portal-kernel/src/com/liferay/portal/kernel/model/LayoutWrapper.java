/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link Layout}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Layout
 * @generated
 */
public class LayoutWrapper
	extends BaseModelWrapper<Layout> implements Layout, ModelWrapper<Layout> {

	public LayoutWrapper(Layout layout) {
		super(layout);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("plid", getPlid());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("parentPlid", getParentPlid());
		attributes.put("privateLayout", isPrivateLayout());
		attributes.put("layoutId", getLayoutId());
		attributes.put("parentLayoutId", getParentLayoutId());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put("name", getName());
		attributes.put("title", getTitle());
		attributes.put("description", getDescription());
		attributes.put("keywords", getKeywords());
		attributes.put("robots", getRobots());
		attributes.put("type", getType());
		attributes.put("typeSettings", getTypeSettings());
		attributes.put("hidden", isHidden());
		attributes.put("system", isSystem());
		attributes.put("friendlyURL", getFriendlyURL());
		attributes.put("iconImageId", getIconImageId());
		attributes.put("themeId", getThemeId());
		attributes.put("colorSchemeId", getColorSchemeId());
		attributes.put("styleBookEntryId", getStyleBookEntryId());
		attributes.put("css", getCss());
		attributes.put("priority", getPriority());
		attributes.put("faviconFileEntryId", getFaviconFileEntryId());
		attributes.put("masterLayoutPlid", getMasterLayoutPlid());
		attributes.put("layoutPrototypeUuid", getLayoutPrototypeUuid());
		attributes.put(
			"layoutPrototypeLinkEnabled", isLayoutPrototypeLinkEnabled());
		attributes.put(
			"sourcePrototypeLayoutUuid", getSourcePrototypeLayoutUuid());
		attributes.put("publishDate", getPublishDate());
		attributes.put("lastPublishDate", getLastPublishDate());
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

		Long plid = (Long)attributes.get("plid");

		if (plid != null) {
			setPlid(plid);
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

		Long parentPlid = (Long)attributes.get("parentPlid");

		if (parentPlid != null) {
			setParentPlid(parentPlid);
		}

		Boolean privateLayout = (Boolean)attributes.get("privateLayout");

		if (privateLayout != null) {
			setPrivateLayout(privateLayout);
		}

		Long layoutId = (Long)attributes.get("layoutId");

		if (layoutId != null) {
			setLayoutId(layoutId);
		}

		Long parentLayoutId = (Long)attributes.get("parentLayoutId");

		if (parentLayoutId != null) {
			setParentLayoutId(parentLayoutId);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classPK = (Long)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
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

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		String typeSettings = (String)attributes.get("typeSettings");

		if (typeSettings != null) {
			setTypeSettings(typeSettings);
		}

		Boolean hidden = (Boolean)attributes.get("hidden");

		if (hidden != null) {
			setHidden(hidden);
		}

		Boolean system = (Boolean)attributes.get("system");

		if (system != null) {
			setSystem(system);
		}

		String friendlyURL = (String)attributes.get("friendlyURL");

		if (friendlyURL != null) {
			setFriendlyURL(friendlyURL);
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

		Long styleBookEntryId = (Long)attributes.get("styleBookEntryId");

		if (styleBookEntryId != null) {
			setStyleBookEntryId(styleBookEntryId);
		}

		String css = (String)attributes.get("css");

		if (css != null) {
			setCss(css);
		}

		Integer priority = (Integer)attributes.get("priority");

		if (priority != null) {
			setPriority(priority);
		}

		Long faviconFileEntryId = (Long)attributes.get("faviconFileEntryId");

		if (faviconFileEntryId != null) {
			setFaviconFileEntryId(faviconFileEntryId);
		}

		Long masterLayoutPlid = (Long)attributes.get("masterLayoutPlid");

		if (masterLayoutPlid != null) {
			setMasterLayoutPlid(masterLayoutPlid);
		}

		String layoutPrototypeUuid = (String)attributes.get(
			"layoutPrototypeUuid");

		if (layoutPrototypeUuid != null) {
			setLayoutPrototypeUuid(layoutPrototypeUuid);
		}

		Boolean layoutPrototypeLinkEnabled = (Boolean)attributes.get(
			"layoutPrototypeLinkEnabled");

		if (layoutPrototypeLinkEnabled != null) {
			setLayoutPrototypeLinkEnabled(layoutPrototypeLinkEnabled);
		}

		String sourcePrototypeLayoutUuid = (String)attributes.get(
			"sourcePrototypeLayoutUuid");

		if (sourcePrototypeLayoutUuid != null) {
			setSourcePrototypeLayoutUuid(sourcePrototypeLayoutUuid);
		}

		Date publishDate = (Date)attributes.get("publishDate");

		if (publishDate != null) {
			setPublishDate(publishDate);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
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
	public Layout cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public Layout fetchDraftLayout() {
		return model.fetchDraftLayout();
	}

	/**
	 * Returns all layouts that are direct or indirect children of the current
	 * layout.
	 *
	 * @return the layouts that are direct or indirect children of the current
	 layout
	 */
	@Override
	public java.util.List<Layout> getAllChildren() {
		return model.getAllChildren();
	}

	/**
	 * Returns the ID of the topmost parent layout (e.g. n-th parent layout) of
	 * the current layout.
	 *
	 * @return the ID of the topmost parent layout of the current layout
	 */
	@Override
	public long getAncestorLayoutId()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getAncestorLayoutId();
	}

	/**
	 * Returns the plid of the topmost parent layout (e.g. n-th parent layout)
	 * of the current layout.
	 *
	 * @return the plid of the topmost parent layout of the current layout
	 */
	@Override
	public long getAncestorPlid()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getAncestorPlid();
	}

	/**
	 * Returns all parent layouts of the current layout. The list is retrieved
	 * recursively with the direct parent layout listed first, and most distant
	 * parent listed last.
	 *
	 * @return the current layout's list of parent layouts
	 */
	@Override
	public java.util.List<Layout> getAncestors()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getAncestors();
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

	/**
	 * Returns all child layouts of the current layout, independent of user
	 * access permissions.
	 *
	 * @return the list of all child layouts
	 */
	@Override
	public java.util.List<Layout> getChildren() {
		return model.getChildren();
	}

	/**
	 * Returns all child layouts of the current layout that the user has
	 * permission to access.
	 *
	 * @param permissionChecker the user-specific context to check permissions
	 * @return the list of all child layouts that the user has permission to
	 access
	 */
	@Override
	public java.util.List<Layout> getChildren(
			com.liferay.portal.kernel.security.permission.PermissionChecker
				permissionChecker)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getChildren(permissionChecker);
	}

	/**
	 * Returns the fully qualified class name of this layout.
	 *
	 * @return the fully qualified class name of this layout
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this layout.
	 *
	 * @return the class name ID of this layout
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class pk of this layout.
	 *
	 * @return the class pk of this layout
	 */
	@Override
	public long getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the color scheme that is configured for the current layout, or
	 * the color scheme of the layout set that contains the current layout if no
	 * color scheme is configured.
	 *
	 * @return the color scheme that is configured for the current layout, or
	 the color scheme  of the layout set that contains the current
	 layout if no color scheme is configured
	 */
	@Override
	public ColorScheme getColorScheme()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getColorScheme();
	}

	/**
	 * Returns the color scheme ID of this layout.
	 *
	 * @return the color scheme ID of this layout
	 */
	@Override
	public String getColorSchemeId() {
		return model.getColorSchemeId();
	}

	/**
	 * Returns the company ID of this layout.
	 *
	 * @return the company ID of this layout
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this layout.
	 *
	 * @return the create date of this layout
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the css of this layout.
	 *
	 * @return the css of this layout
	 */
	@Override
	public String getCss() {
		return model.getCss();
	}

	/**
	 * Returns the CSS text for the current layout, or for the layout set if no
	 * CSS text is configured in the current layout.
	 *
	 * <p>
	 * Layouts and layout sets can configure CSS that is applied in addition to
	 * the theme's CSS.
	 * </p>
	 *
	 * @return the CSS text for the current layout, or for the layout set if no
	 CSS text is configured in the current layout
	 */
	@Override
	public String getCssText()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCssText();
	}

	/**
	 * Returns the ct collection ID of this layout.
	 *
	 * @return the ct collection ID of this layout
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	@Override
	public String getDefaultLanguageId() {
		return model.getDefaultLanguageId();
	}

	@Override
	public String getDefaultThemeSetting(
		String key, String device, boolean inheritLookAndFeel) {

		return model.getDefaultThemeSetting(key, device, inheritLookAndFeel);
	}

	/**
	 * Returns the description of this layout.
	 *
	 * @return the description of this layout
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this layout in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this layout
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this layout in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this layout. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this layout in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this layout
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this layout in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this layout
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
	 * Returns a map of the locales and localized descriptions of this layout.
	 *
	 * @return the locales and localized descriptions of this layout
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	@Override
	public java.util.List<Portlet> getEmbeddedPortlets() {
		return model.getEmbeddedPortlets();
	}

	@Override
	public java.util.List<Portlet> getEmbeddedPortlets(long groupId) {
		return model.getEmbeddedPortlets(groupId);
	}

	/**
	 * Returns the external reference code of this layout.
	 *
	 * @return the external reference code of this layout
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the favicon file entry ID of this layout.
	 *
	 * @return the favicon file entry ID of this layout
	 */
	@Override
	public long getFaviconFileEntryId() {
		return model.getFaviconFileEntryId();
	}

	@Override
	public String getFaviconURL() {
		return model.getFaviconURL();
	}

	/**
	 * Returns the friendly url of this layout.
	 *
	 * @return the friendly url of this layout
	 */
	@Override
	public String getFriendlyURL() {
		return model.getFriendlyURL();
	}

	/**
	 * Returns the layout's friendly URL for the given locale.
	 *
	 * @param locale the locale that the friendly URL should be retrieved for
	 * @return the layout's friendly URL for the given locale
	 */
	@Override
	public String getFriendlyURL(java.util.Locale locale) {
		return model.getFriendlyURL(locale);
	}

	/**
	 * Returns the friendly URLs for all configured locales.
	 *
	 * @return the friendly URLs for all configured locales
	 */
	@Override
	public Map<java.util.Locale, String> getFriendlyURLMap() {
		return model.getFriendlyURLMap();
	}

	@Override
	public String getFriendlyURLsXML() {
		return model.getFriendlyURLsXML();
	}

	/**
	 * Returns the current layout's group.
	 *
	 * <p>
	 * Group is Liferay's technical name for a site.
	 * </p>
	 *
	 * @return the current layout's group
	 */
	@Override
	public Group getGroup() {
		return model.getGroup();
	}

	/**
	 * Returns the group ID of this layout.
	 *
	 * @return the group ID of this layout
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the hidden of this layout.
	 *
	 * @return the hidden of this layout
	 */
	@Override
	public boolean getHidden() {
		return model.getHidden();
	}

	/**
	 * Returns the current layout's HTML title for the given locale, or the
	 * current layout's name for the given locale if no HTML title is
	 * configured.
	 *
	 * @param locale the locale that the HTML title should be retrieved for
	 * @return the current layout's HTML title for the given locale, or the
	 current layout's name for the given locale if no HTML title is
	 configured
	 */
	@Override
	public String getHTMLTitle(java.util.Locale locale) {
		return model.getHTMLTitle(locale);
	}

	/**
	 * Returns the current layout's HTML title for the given locale language ID,
	 * or the current layout's name if no HTML title is configured.
	 *
	 * @param localeLanguageId the locale that the HTML title should be
	 retrieved for
	 * @return the current layout's HTML title for the given locale language ID,
	 or the current layout's name if no HTML title is configured
	 */
	@Override
	public String getHTMLTitle(String localeLanguageId) {
		return model.getHTMLTitle(localeLanguageId);
	}

	@Override
	public String getIcon() {
		return model.getIcon();
	}

	/**
	 * Returns <code>true</code> if the current layout has a configured icon.
	 *
	 * @return <code>true</code> if the current layout has a configured icon;
	 <code>false</code> otherwise
	 */
	@Override
	public boolean getIconImage() {
		return model.getIconImage();
	}

	/**
	 * Returns the icon image ID of this layout.
	 *
	 * @return the icon image ID of this layout
	 */
	@Override
	public long getIconImageId() {
		return model.getIconImageId();
	}

	/**
	 * Returns the keywords of this layout.
	 *
	 * @return the keywords of this layout
	 */
	@Override
	public String getKeywords() {
		return model.getKeywords();
	}

	/**
	 * Returns the localized keywords of this layout in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized keywords of this layout
	 */
	@Override
	public String getKeywords(java.util.Locale locale) {
		return model.getKeywords(locale);
	}

	/**
	 * Returns the localized keywords of this layout in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized keywords of this layout. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getKeywords(java.util.Locale locale, boolean useDefault) {
		return model.getKeywords(locale, useDefault);
	}

	/**
	 * Returns the localized keywords of this layout in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized keywords of this layout
	 */
	@Override
	public String getKeywords(String languageId) {
		return model.getKeywords(languageId);
	}

	/**
	 * Returns the localized keywords of this layout in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized keywords of this layout
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
	 * Returns a map of the locales and localized keywordses of this layout.
	 *
	 * @return the locales and localized keywordses of this layout
	 */
	@Override
	public Map<java.util.Locale, String> getKeywordsMap() {
		return model.getKeywordsMap();
	}

	/**
	 * Returns the last publish date of this layout.
	 *
	 * @return the last publish date of this layout
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the layout ID of this layout.
	 *
	 * @return the layout ID of this layout
	 */
	@Override
	public long getLayoutId() {
		return model.getLayoutId();
	}

	/**
	 * Returns the layout prototype link enabled of this layout.
	 *
	 * @return the layout prototype link enabled of this layout
	 */
	@Override
	public boolean getLayoutPrototypeLinkEnabled() {
		return model.getLayoutPrototypeLinkEnabled();
	}

	/**
	 * Returns the layout prototype uuid of this layout.
	 *
	 * @return the layout prototype uuid of this layout
	 */
	@Override
	public String getLayoutPrototypeUuid() {
		return model.getLayoutPrototypeUuid();
	}

	/**
	 * Returns the current layout's {@link LayoutSet}.
	 *
	 * @return the current layout's layout set
	 */
	@Override
	public LayoutSet getLayoutSet() {
		return model.getLayoutSet();
	}

	@Override
	public Layout getLayoutSetPrototypeLayout() {
		return model.getLayoutSetPrototypeLayout();
	}

	/**
	 * Returns the current layout's {@link LayoutType}.
	 *
	 * @return the current layout's layout type
	 */
	@Override
	public LayoutType getLayoutType() {
		return model.getLayoutType();
	}

	/**
	 * Returns the current layout's linked layout.
	 *
	 * @return the current layout's linked layout, or <code>null</code> if no
	 linked layout could be found
	 */
	@Override
	public Layout getLinkedToLayout() {
		return model.getLinkedToLayout();
	}

	/**
	 * Returns the master layout plid of this layout.
	 *
	 * @return the master layout plid of this layout
	 */
	@Override
	public long getMasterLayoutPlid() {
		return model.getMasterLayoutPlid();
	}

	/**
	 * Returns the modified date of this layout.
	 *
	 * @return the modified date of this layout
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this layout.
	 *
	 * @return the mvcc version of this layout
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this layout.
	 *
	 * @return the name of this layout
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this layout in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this layout
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this layout in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this layout. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this layout in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this layout
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this layout in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this layout
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
	 * Returns a map of the locales and localized names of this layout.
	 *
	 * @return the locales and localized names of this layout
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	/**
	 * Returns the parent layout ID of this layout.
	 *
	 * @return the parent layout ID of this layout
	 */
	@Override
	public long getParentLayoutId() {
		return model.getParentLayoutId();
	}

	/**
	 * Returns the parent plid of this layout.
	 *
	 * @return the parent plid of this layout
	 */
	@Override
	public long getParentPlid() {
		return model.getParentPlid();
	}

	/**
	 * Returns the plid of this layout.
	 *
	 * @return the plid of this layout
	 */
	@Override
	public long getPlid() {
		return model.getPlid();
	}

	/**
	 * Returns the primary key of this layout.
	 *
	 * @return the primary key of this layout
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the priority of this layout.
	 *
	 * @return the priority of this layout
	 */
	@Override
	public int getPriority() {
		return model.getPriority();
	}

	/**
	 * Returns the private layout of this layout.
	 *
	 * @return the private layout of this layout
	 */
	@Override
	public boolean getPrivateLayout() {
		return model.getPrivateLayout();
	}

	/**
	 * Returns the publish date of this layout.
	 *
	 * @return the publish date of this layout
	 */
	@Override
	public Date getPublishDate() {
		return model.getPublishDate();
	}

	@Override
	public String getRegularURL(
			jakarta.servlet.http.HttpServletRequest httpServletRequest)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getRegularURL(httpServletRequest);
	}

	@Override
	public String getResetLayoutURL(
			jakarta.servlet.http.HttpServletRequest httpServletRequest)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getResetLayoutURL(httpServletRequest);
	}

	@Override
	public String getResetMaxStateURL(
			jakarta.servlet.http.HttpServletRequest httpServletRequest)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getResetMaxStateURL(httpServletRequest);
	}

	/**
	 * Returns the robots of this layout.
	 *
	 * @return the robots of this layout
	 */
	@Override
	public String getRobots() {
		return model.getRobots();
	}

	/**
	 * Returns the localized robots of this layout in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized robots of this layout
	 */
	@Override
	public String getRobots(java.util.Locale locale) {
		return model.getRobots(locale);
	}

	/**
	 * Returns the localized robots of this layout in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized robots of this layout. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getRobots(java.util.Locale locale, boolean useDefault) {
		return model.getRobots(locale, useDefault);
	}

	/**
	 * Returns the localized robots of this layout in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized robots of this layout
	 */
	@Override
	public String getRobots(String languageId) {
		return model.getRobots(languageId);
	}

	/**
	 * Returns the localized robots of this layout in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized robots of this layout
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
	 * Returns a map of the locales and localized robotses of this layout.
	 *
	 * @return the locales and localized robotses of this layout
	 */
	@Override
	public Map<java.util.Locale, String> getRobotsMap() {
		return model.getRobotsMap();
	}

	@Override
	public Group getScopeGroup()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getScopeGroup();
	}

	/**
	 * Returns the source prototype layout uuid of this layout.
	 *
	 * @return the source prototype layout uuid of this layout
	 */
	@Override
	public String getSourcePrototypeLayoutUuid() {
		return model.getSourcePrototypeLayoutUuid();
	}

	/**
	 * Returns the status of this layout.
	 *
	 * @return the status of this layout
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this layout.
	 *
	 * @return the status by user ID of this layout
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this layout.
	 *
	 * @return the status by user name of this layout
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this layout.
	 *
	 * @return the status by user uuid of this layout
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this layout.
	 *
	 * @return the status date of this layout
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the style book entry ID of this layout.
	 *
	 * @return the style book entry ID of this layout
	 */
	@Override
	public long getStyleBookEntryId() {
		return model.getStyleBookEntryId();
	}

	/**
	 * Returns the system of this layout.
	 *
	 * @return the system of this layout
	 */
	@Override
	public boolean getSystem() {
		return model.getSystem();
	}

	@Override
	public String getTarget() {
		return model.getTarget();
	}

	/**
	 * Returns the current layout's theme, or the layout set's theme if no
	 * layout theme is configured.
	 *
	 * @return the current layout's theme, or the layout set's theme if no
	 layout theme is configured
	 */
	@Override
	public Theme getTheme()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getTheme();
	}

	/**
	 * Returns the theme ID of this layout.
	 *
	 * @return the theme ID of this layout
	 */
	@Override
	public String getThemeId() {
		return model.getThemeId();
	}

	@Override
	public String getThemeSetting(String key, String device) {
		return model.getThemeSetting(key, device);
	}

	@Override
	public String getThemeSetting(
		String key, String device, boolean inheritLookAndFeel) {

		return model.getThemeSetting(key, device, inheritLookAndFeel);
	}

	/**
	 * Returns the title of this layout.
	 *
	 * @return the title of this layout
	 */
	@Override
	public String getTitle() {
		return model.getTitle();
	}

	/**
	 * Returns the localized title of this layout in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized title of this layout
	 */
	@Override
	public String getTitle(java.util.Locale locale) {
		return model.getTitle(locale);
	}

	/**
	 * Returns the localized title of this layout in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this layout. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getTitle(java.util.Locale locale, boolean useDefault) {
		return model.getTitle(locale, useDefault);
	}

	/**
	 * Returns the localized title of this layout in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized title of this layout
	 */
	@Override
	public String getTitle(String languageId) {
		return model.getTitle(languageId);
	}

	/**
	 * Returns the localized title of this layout in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized title of this layout
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
	 * Returns a map of the locales and localized titles of this layout.
	 *
	 * @return the locales and localized titles of this layout
	 */
	@Override
	public Map<java.util.Locale, String> getTitleMap() {
		return model.getTitleMap();
	}

	/**
	 * Returns the type of this layout.
	 *
	 * @return the type of this layout
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	/**
	 * Returns the type settings of this layout.
	 *
	 * @return the type settings of this layout
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
	 * Returns the user ID of this layout.
	 *
	 * @return the user ID of this layout
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this layout.
	 *
	 * @return the user name of this layout
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this layout.
	 *
	 * @return the user uuid of this layout
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this layout.
	 *
	 * @return the uuid of this layout
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if the given layout ID matches one of the
	 * current layout's hierarchical parents.
	 *
	 * @param layoutId the layout ID to search for in the current layout's
	 parent list
	 * @return <code>true</code> if the given layout ID matches one of the
	 current layout's hierarchical parents; <code>false</code>
	 otherwise
	 */
	@Override
	public boolean hasAncestor(long layoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.hasAncestor(layoutId);
	}

	/**
	 * Returns <code>true</code> if the current layout has child layouts.
	 *
	 * @return <code>true</code> if the current layout has child layouts,
	 <code>false</code> otherwise
	 */
	@Override
	public boolean hasChildren() {
		return model.hasChildren();
	}

	@Override
	public boolean hasScopeGroup()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.hasScopeGroup();
	}

	@Override
	public boolean hasSetModifiedDate() {
		return model.hasSetModifiedDate();
	}

	@Override
	public boolean includeLayoutContent(
			jakarta.servlet.http.HttpServletRequest httpServletRequest,
			jakarta.servlet.http.HttpServletResponse httpServletResponse)
		throws Exception {

		return model.includeLayoutContent(
			httpServletRequest, httpServletResponse);
	}

	/**
	 * Returns <code>true</code> if this layout is approved.
	 *
	 * @return <code>true</code> if this layout is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	@Override
	public boolean isChildSelected(boolean selectable, Layout layout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.isChildSelected(selectable, layout);
	}

	/**
	 * Returns <code>true</code> if the current layout can be used as a content
	 * display page.
	 *
	 * <p>
	 * A content display page must have an Asset Publisher portlet that is
	 * configured as the default Asset Publisher for the layout.
	 * </p>
	 *
	 * @return <code>true</code> if the current layout can be used as a content
	 display page; <code>false</code> otherwise
	 */
	@Override
	public boolean isContentDisplayPage() {
		return model.isContentDisplayPage();
	}

	@Override
	public boolean isCustomizable() {
		return model.isCustomizable();
	}

	/**
	 * Returns <code>true</code> if this layout is denied.
	 *
	 * @return <code>true</code> if this layout is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this layout is a draft.
	 *
	 * @return <code>true</code> if this layout is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	@Override
	public boolean isDraftLayout() {
		return model.isDraftLayout();
	}

	@Override
	public boolean isEmbeddedPersonalApplication() {
		return model.isEmbeddedPersonalApplication();
	}

	/**
	 * Returns <code>true</code> if this layout is expired.
	 *
	 * @return <code>true</code> if this layout is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if the current layout is the first layout in
	 * its parent's hierarchical list of children layouts.
	 *
	 * @return <code>true</code> if the current layout is the first layout in
	 its parent's hierarchical list of children layouts;
	 <code>false</code> otherwise
	 */
	@Override
	public boolean isFirstChild() {
		return model.isFirstChild();
	}

	/**
	 * Returns <code>true</code> if the current layout is the topmost parent
	 * layout.
	 *
	 * @return <code>true</code> if the current layout is the topmost parent
	 layout; <code>false</code> otherwise
	 */
	@Override
	public boolean isFirstParent() {
		return model.isFirstParent();
	}

	/**
	 * Returns <code>true</code> if this layout is hidden.
	 *
	 * @return <code>true</code> if this layout is hidden; <code>false</code> otherwise
	 */
	@Override
	public boolean isHidden() {
		return model.isHidden();
	}

	@Override
	public boolean isIconImage() {
		return model.isIconImage();
	}

	/**
	 * Returns <code>true</code> if this layout is inactive.
	 *
	 * @return <code>true</code> if this layout is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this layout is incomplete.
	 *
	 * @return <code>true</code> if this layout is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if the current layout utilizes its {@link
	 * LayoutSet}'s look and feel options (e.g. theme and color scheme).
	 *
	 * @return <code>true</code> if the current layout utilizes its layout set's
	 look and feel options; <code>false</code> otherwise
	 */
	@Override
	public boolean isInheritLookAndFeel() {
		return model.isInheritLookAndFeel();
	}

	@Override
	public boolean isLayoutDeleteable() {
		return model.isLayoutDeleteable();
	}

	/**
	 * Returns <code>true</code> if the current layout is built from a layout
	 * template and still maintains an active connection to it.
	 *
	 * @return <code>true</code> if the current layout is built from a layout
	 template and still maintains an active connection to it;
	 <code>false</code> otherwise
	 */
	@Override
	public boolean isLayoutPrototypeLinkActive() {
		return model.isLayoutPrototypeLinkActive();
	}

	/**
	 * Returns <code>true</code> if this layout is layout prototype link enabled.
	 *
	 * @return <code>true</code> if this layout is layout prototype link enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isLayoutPrototypeLinkEnabled() {
		return model.isLayoutPrototypeLinkEnabled();
	}

	@Override
	public boolean isLayoutSortable() {
		return model.isLayoutSortable();
	}

	@Override
	public boolean isLayoutUpdateable() {
		return model.isLayoutUpdateable();
	}

	/**
	 * Returns <code>true</code> if this layout is pending.
	 *
	 * @return <code>true</code> if this layout is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	@Override
	public boolean isPortletEmbedded(String portletId, long groupId) {
		return model.isPortletEmbedded(portletId, groupId);
	}

	/**
	 * Returns <code>true</code> if this layout is private layout.
	 *
	 * @return <code>true</code> if this layout is private layout; <code>false</code> otherwise
	 */
	@Override
	public boolean isPrivateLayout() {
		return model.isPrivateLayout();
	}

	/**
	 * Returns <code>true</code> if the current layout is part of the public
	 * {@link LayoutSet}.
	 *
	 * <p>
	 * Note, the returned value reflects the layout's default access options,
	 * not its access permissions.
	 * </p>
	 *
	 * @return <code>true</code> if the current layout is part of the public
	 layout set; <code>false</code> otherwise
	 */
	@Override
	public boolean isPublicLayout() {
		return model.isPublicLayout();
	}

	@Override
	public boolean isPublished() {
		return model.isPublished();
	}

	/**
	 * Returns <code>true</code> if the current layout is the root layout.
	 *
	 * @return <code>true</code> if the current layout is the root layout;
	 <code>false</code> otherwise
	 */
	@Override
	public boolean isRootLayout() {
		return model.isRootLayout();
	}

	/**
	 * Returns <code>true</code> if this layout is scheduled.
	 *
	 * @return <code>true</code> if this layout is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
	}

	@Override
	public boolean isSelected(
		boolean selectable, Layout layout, long ancestorPlid) {

		return model.isSelected(selectable, layout, ancestorPlid);
	}

	/**
	 * Returns <code>true</code> if the current layout can hold embedded
	 * portlets.
	 *
	 * @return <code>true</code> if the current layout can hold embedded
	 portlets; <code>false</code> otherwise
	 */
	@Override
	public boolean isSupportsEmbeddedPortlets() {
		return model.isSupportsEmbeddedPortlets();
	}

	/**
	 * Returns <code>true</code> if this layout is system.
	 *
	 * @return <code>true</code> if this layout is system; <code>false</code> otherwise
	 */
	@Override
	public boolean isSystem() {
		return model.isSystem();
	}

	@Override
	public boolean isTypeAssetDisplay() {
		return model.isTypeAssetDisplay();
	}

	@Override
	public boolean isTypeContent() {
		return model.isTypeContent();
	}

	@Override
	public boolean isTypeControlPanel() {
		return model.isTypeControlPanel();
	}

	@Override
	public boolean isTypeEmbedded() {
		return model.isTypeEmbedded();
	}

	@Override
	public boolean isTypeLinkToLayout() {
		return model.isTypeLinkToLayout();
	}

	@Override
	public boolean isTypePanel() {
		return model.isTypePanel();
	}

	@Override
	public boolean isTypePortlet() {
		return model.isTypePortlet();
	}

	@Override
	public boolean isTypeURL() {
		return model.isTypeURL();
	}

	@Override
	public boolean isTypeUtility() {
		return model.isTypeUtility();
	}

	@Override
	public boolean isUnlocked(String mode, long userId) {
		return model.isUnlocked(mode, userId);
	}

	@Override
	public boolean matches(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		String friendlyURL) {

		return model.matches(httpServletRequest, friendlyURL);
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

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this layout.
	 *
	 * @param classNameId the class name ID of this layout
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this layout.
	 *
	 * @param classPK the class pk of this layout
	 */
	@Override
	public void setClassPK(long classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the color scheme ID of this layout.
	 *
	 * @param colorSchemeId the color scheme ID of this layout
	 */
	@Override
	public void setColorSchemeId(String colorSchemeId) {
		model.setColorSchemeId(colorSchemeId);
	}

	/**
	 * Sets the company ID of this layout.
	 *
	 * @param companyId the company ID of this layout
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this layout.
	 *
	 * @param createDate the create date of this layout
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the css of this layout.
	 *
	 * @param css the css of this layout
	 */
	@Override
	public void setCss(String css) {
		model.setCss(css);
	}

	/**
	 * Sets the ct collection ID of this layout.
	 *
	 * @param ctCollectionId the ct collection ID of this layout
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the description of this layout.
	 *
	 * @param description the description of this layout
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this layout in the language.
	 *
	 * @param description the localized description of this layout
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this layout in the language, and sets the default locale.
	 *
	 * @param description the localized description of this layout
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
	 * Sets the localized descriptions of this layout from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this layout
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this layout from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this layout
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the external reference code of this layout.
	 *
	 * @param externalReferenceCode the external reference code of this layout
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the favicon file entry ID of this layout.
	 *
	 * @param faviconFileEntryId the favicon file entry ID of this layout
	 */
	@Override
	public void setFaviconFileEntryId(long faviconFileEntryId) {
		model.setFaviconFileEntryId(faviconFileEntryId);
	}

	/**
	 * Sets the friendly url of this layout.
	 *
	 * @param friendlyURL the friendly url of this layout
	 */
	@Override
	public void setFriendlyURL(String friendlyURL) {
		model.setFriendlyURL(friendlyURL);
	}

	/**
	 * Sets the group ID of this layout.
	 *
	 * @param groupId the group ID of this layout
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets whether this layout is hidden.
	 *
	 * @param hidden the hidden of this layout
	 */
	@Override
	public void setHidden(boolean hidden) {
		model.setHidden(hidden);
	}

	/**
	 * Sets the icon image ID of this layout.
	 *
	 * @param iconImageId the icon image ID of this layout
	 */
	@Override
	public void setIconImageId(long iconImageId) {
		model.setIconImageId(iconImageId);
	}

	/**
	 * Sets the keywords of this layout.
	 *
	 * @param keywords the keywords of this layout
	 */
	@Override
	public void setKeywords(String keywords) {
		model.setKeywords(keywords);
	}

	/**
	 * Sets the localized keywords of this layout in the language.
	 *
	 * @param keywords the localized keywords of this layout
	 * @param locale the locale of the language
	 */
	@Override
	public void setKeywords(String keywords, java.util.Locale locale) {
		model.setKeywords(keywords, locale);
	}

	/**
	 * Sets the localized keywords of this layout in the language, and sets the default locale.
	 *
	 * @param keywords the localized keywords of this layout
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
	 * Sets the localized keywordses of this layout from the map of locales and localized keywordses.
	 *
	 * @param keywordsMap the locales and localized keywordses of this layout
	 */
	@Override
	public void setKeywordsMap(Map<java.util.Locale, String> keywordsMap) {
		model.setKeywordsMap(keywordsMap);
	}

	/**
	 * Sets the localized keywordses of this layout from the map of locales and localized keywordses, and sets the default locale.
	 *
	 * @param keywordsMap the locales and localized keywordses of this layout
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setKeywordsMap(
		Map<java.util.Locale, String> keywordsMap,
		java.util.Locale defaultLocale) {

		model.setKeywordsMap(keywordsMap, defaultLocale);
	}

	/**
	 * Sets the last publish date of this layout.
	 *
	 * @param lastPublishDate the last publish date of this layout
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the layout ID of this layout.
	 *
	 * @param layoutId the layout ID of this layout
	 */
	@Override
	public void setLayoutId(long layoutId) {
		model.setLayoutId(layoutId);
	}

	/**
	 * Sets whether this layout is layout prototype link enabled.
	 *
	 * @param layoutPrototypeLinkEnabled the layout prototype link enabled of this layout
	 */
	@Override
	public void setLayoutPrototypeLinkEnabled(
		boolean layoutPrototypeLinkEnabled) {

		model.setLayoutPrototypeLinkEnabled(layoutPrototypeLinkEnabled);
	}

	/**
	 * Sets the layout prototype uuid of this layout.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid of this layout
	 */
	@Override
	public void setLayoutPrototypeUuid(String layoutPrototypeUuid) {
		model.setLayoutPrototypeUuid(layoutPrototypeUuid);
	}

	@Override
	public void setLayoutSet(LayoutSet layoutSet) {
		model.setLayoutSet(layoutSet);
	}

	/**
	 * Sets the master layout plid of this layout.
	 *
	 * @param masterLayoutPlid the master layout plid of this layout
	 */
	@Override
	public void setMasterLayoutPlid(long masterLayoutPlid) {
		model.setMasterLayoutPlid(masterLayoutPlid);
	}

	/**
	 * Sets the modified date of this layout.
	 *
	 * @param modifiedDate the modified date of this layout
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this layout.
	 *
	 * @param mvccVersion the mvcc version of this layout
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this layout.
	 *
	 * @param name the name of this layout
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this layout in the language.
	 *
	 * @param name the localized name of this layout
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this layout in the language, and sets the default locale.
	 *
	 * @param name the localized name of this layout
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
	 * Sets the localized names of this layout from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this layout
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this layout from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this layout
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the parent layout ID of this layout.
	 *
	 * @param parentLayoutId the parent layout ID of this layout
	 */
	@Override
	public void setParentLayoutId(long parentLayoutId) {
		model.setParentLayoutId(parentLayoutId);
	}

	/**
	 * Sets the parent plid of this layout.
	 *
	 * @param parentPlid the parent plid of this layout
	 */
	@Override
	public void setParentPlid(long parentPlid) {
		model.setParentPlid(parentPlid);
	}

	/**
	 * Sets the plid of this layout.
	 *
	 * @param plid the plid of this layout
	 */
	@Override
	public void setPlid(long plid) {
		model.setPlid(plid);
	}

	/**
	 * Sets the primary key of this layout.
	 *
	 * @param primaryKey the primary key of this layout
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the priority of this layout.
	 *
	 * @param priority the priority of this layout
	 */
	@Override
	public void setPriority(int priority) {
		model.setPriority(priority);
	}

	/**
	 * Sets whether this layout is private layout.
	 *
	 * @param privateLayout the private layout of this layout
	 */
	@Override
	public void setPrivateLayout(boolean privateLayout) {
		model.setPrivateLayout(privateLayout);
	}

	/**
	 * Sets the publish date of this layout.
	 *
	 * @param publishDate the publish date of this layout
	 */
	@Override
	public void setPublishDate(Date publishDate) {
		model.setPublishDate(publishDate);
	}

	/**
	 * Sets the robots of this layout.
	 *
	 * @param robots the robots of this layout
	 */
	@Override
	public void setRobots(String robots) {
		model.setRobots(robots);
	}

	/**
	 * Sets the localized robots of this layout in the language.
	 *
	 * @param robots the localized robots of this layout
	 * @param locale the locale of the language
	 */
	@Override
	public void setRobots(String robots, java.util.Locale locale) {
		model.setRobots(robots, locale);
	}

	/**
	 * Sets the localized robots of this layout in the language, and sets the default locale.
	 *
	 * @param robots the localized robots of this layout
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
	 * Sets the localized robotses of this layout from the map of locales and localized robotses.
	 *
	 * @param robotsMap the locales and localized robotses of this layout
	 */
	@Override
	public void setRobotsMap(Map<java.util.Locale, String> robotsMap) {
		model.setRobotsMap(robotsMap);
	}

	/**
	 * Sets the localized robotses of this layout from the map of locales and localized robotses, and sets the default locale.
	 *
	 * @param robotsMap the locales and localized robotses of this layout
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setRobotsMap(
		Map<java.util.Locale, String> robotsMap,
		java.util.Locale defaultLocale) {

		model.setRobotsMap(robotsMap, defaultLocale);
	}

	/**
	 * Sets the source prototype layout uuid of this layout.
	 *
	 * @param sourcePrototypeLayoutUuid the source prototype layout uuid of this layout
	 */
	@Override
	public void setSourcePrototypeLayoutUuid(String sourcePrototypeLayoutUuid) {
		model.setSourcePrototypeLayoutUuid(sourcePrototypeLayoutUuid);
	}

	/**
	 * Sets the status of this layout.
	 *
	 * @param status the status of this layout
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this layout.
	 *
	 * @param statusByUserId the status by user ID of this layout
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this layout.
	 *
	 * @param statusByUserName the status by user name of this layout
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this layout.
	 *
	 * @param statusByUserUuid the status by user uuid of this layout
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this layout.
	 *
	 * @param statusDate the status date of this layout
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the style book entry ID of this layout.
	 *
	 * @param styleBookEntryId the style book entry ID of this layout
	 */
	@Override
	public void setStyleBookEntryId(long styleBookEntryId) {
		model.setStyleBookEntryId(styleBookEntryId);
	}

	/**
	 * Sets whether this layout is system.
	 *
	 * @param system the system of this layout
	 */
	@Override
	public void setSystem(boolean system) {
		model.setSystem(system);
	}

	/**
	 * Sets the theme ID of this layout.
	 *
	 * @param themeId the theme ID of this layout
	 */
	@Override
	public void setThemeId(String themeId) {
		model.setThemeId(themeId);
	}

	/**
	 * Sets the title of this layout.
	 *
	 * @param title the title of this layout
	 */
	@Override
	public void setTitle(String title) {
		model.setTitle(title);
	}

	/**
	 * Sets the localized title of this layout in the language.
	 *
	 * @param title the localized title of this layout
	 * @param locale the locale of the language
	 */
	@Override
	public void setTitle(String title, java.util.Locale locale) {
		model.setTitle(title, locale);
	}

	/**
	 * Sets the localized title of this layout in the language, and sets the default locale.
	 *
	 * @param title the localized title of this layout
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
	 * Sets the localized titles of this layout from the map of locales and localized titles.
	 *
	 * @param titleMap the locales and localized titles of this layout
	 */
	@Override
	public void setTitleMap(Map<java.util.Locale, String> titleMap) {
		model.setTitleMap(titleMap);
	}

	/**
	 * Sets the localized titles of this layout from the map of locales and localized titles, and sets the default locale.
	 *
	 * @param titleMap the locales and localized titles of this layout
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setTitleMap(
		Map<java.util.Locale, String> titleMap,
		java.util.Locale defaultLocale) {

		model.setTitleMap(titleMap, defaultLocale);
	}

	/**
	 * Sets the type of this layout.
	 *
	 * @param type the type of this layout
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	/**
	 * Sets the type settings of this layout.
	 *
	 * @param typeSettings the type settings of this layout
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
	 * Sets the user ID of this layout.
	 *
	 * @param userId the user ID of this layout
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this layout.
	 *
	 * @param userName the user name of this layout
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this layout.
	 *
	 * @param userUuid the user uuid of this layout
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this layout.
	 *
	 * @param uuid the uuid of this layout
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
	public Map<String, Function<Layout, Object>> getAttributeGetterFunctions() {
		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<Layout, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected LayoutWrapper wrap(Layout layout) {
		return new LayoutWrapper(layout);
	}

}