/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link Group}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Group
 * @generated
 */
public class GroupWrapper
	extends BaseModelWrapper<Group> implements Group, ModelWrapper<Group> {

	public GroupWrapper(Group group) {
		super(group);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("creatorUserId", getCreatorUserId());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put("parentGroupId", getParentGroupId());
		attributes.put("liveGroupId", getLiveGroupId());
		attributes.put("treePath", getTreePath());
		attributes.put("groupKey", getGroupKey());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("type", getType());
		attributes.put("typeSettings", getTypeSettings());
		attributes.put("manualMembership", isManualMembership());
		attributes.put("membershipRestriction", getMembershipRestriction());
		attributes.put("friendlyURL", getFriendlyURL());
		attributes.put("site", isSite());
		attributes.put("remoteStagingGroupCount", getRemoteStagingGroupCount());
		attributes.put("inheritContent", isInheritContent());
		attributes.put("active", isActive());

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

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long creatorUserId = (Long)attributes.get("creatorUserId");

		if (creatorUserId != null) {
			setCreatorUserId(creatorUserId);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classPK = (Long)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
		}

		Long parentGroupId = (Long)attributes.get("parentGroupId");

		if (parentGroupId != null) {
			setParentGroupId(parentGroupId);
		}

		Long liveGroupId = (Long)attributes.get("liveGroupId");

		if (liveGroupId != null) {
			setLiveGroupId(liveGroupId);
		}

		String treePath = (String)attributes.get("treePath");

		if (treePath != null) {
			setTreePath(treePath);
		}

		String groupKey = (String)attributes.get("groupKey");

		if (groupKey != null) {
			setGroupKey(groupKey);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		String typeSettings = (String)attributes.get("typeSettings");

		if (typeSettings != null) {
			setTypeSettings(typeSettings);
		}

		Boolean manualMembership = (Boolean)attributes.get("manualMembership");

		if (manualMembership != null) {
			setManualMembership(manualMembership);
		}

		Integer membershipRestriction = (Integer)attributes.get(
			"membershipRestriction");

		if (membershipRestriction != null) {
			setMembershipRestriction(membershipRestriction);
		}

		String friendlyURL = (String)attributes.get("friendlyURL");

		if (friendlyURL != null) {
			setFriendlyURL(friendlyURL);
		}

		Boolean site = (Boolean)attributes.get("site");

		if (site != null) {
			setSite(site);
		}

		Integer remoteStagingGroupCount = (Integer)attributes.get(
			"remoteStagingGroupCount");

		if (remoteStagingGroupCount != null) {
			setRemoteStagingGroupCount(remoteStagingGroupCount);
		}

		Boolean inheritContent = (Boolean)attributes.get("inheritContent");

		if (inheritContent != null) {
			setInheritContent(inheritContent);
		}

		Boolean active = (Boolean)attributes.get("active");

		if (active != null) {
			setActive(active);
		}
	}

	@Override
	public String buildTreePath()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.buildTreePath();
	}

	@Override
	public void clearStagingGroup() {
		model.clearStagingGroup();
	}

	@Override
	public Group cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the active of this group.
	 *
	 * @return the active of this group
	 */
	@Override
	public boolean getActive() {
		return model.getActive();
	}

	@Override
	public java.util.List<Group> getAncestors() {
		return model.getAncestors();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return model.getAvailableLanguageIds();
	}

	@Override
	public java.util.List<Group> getChildren(boolean site) {
		return model.getChildren(site);
	}

	@Override
	public java.util.List<Group> getChildrenWithLayouts(
		boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return model.getChildrenWithLayouts(
			site, start, end, orderByComparator);
	}

	@Override
	public int getChildrenWithLayoutsCount(boolean site) {
		return model.getChildrenWithLayoutsCount(site);
	}

	/**
	 * Returns the fully qualified class name of this group.
	 *
	 * @return the fully qualified class name of this group
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this group.
	 *
	 * @return the class name ID of this group
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class pk of this group.
	 *
	 * @return the class pk of this group
	 */
	@Override
	public long getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the company ID of this group.
	 *
	 * @return the company ID of this group
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the creator user ID of this group.
	 *
	 * @return the creator user ID of this group
	 */
	@Override
	public long getCreatorUserId() {
		return model.getCreatorUserId();
	}

	/**
	 * Returns the creator user uuid of this group.
	 *
	 * @return the creator user uuid of this group
	 */
	@Override
	public String getCreatorUserUuid() {
		return model.getCreatorUserUuid();
	}

	/**
	 * Returns the ct collection ID of this group.
	 *
	 * @return the ct collection ID of this group
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
	public long getDefaultPrivatePlid() {
		return model.getDefaultPrivatePlid();
	}

	@Override
	public long getDefaultPublicPlid() {
		return model.getDefaultPublicPlid();
	}

	@Override
	public java.util.List<Group> getDescendants(boolean site) {
		return model.getDescendants(site);
	}

	/**
	 * Returns the description of this group.
	 *
	 * @return the description of this group
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the localized description of this group in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized description of this group
	 */
	@Override
	public String getDescription(java.util.Locale locale) {
		return model.getDescription(locale);
	}

	/**
	 * Returns the localized description of this group in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this group. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getDescription(java.util.Locale locale, boolean useDefault) {
		return model.getDescription(locale, useDefault);
	}

	/**
	 * Returns the localized description of this group in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized description of this group
	 */
	@Override
	public String getDescription(String languageId) {
		return model.getDescription(languageId);
	}

	/**
	 * Returns the localized description of this group in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized description of this group
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
	 * Returns a map of the locales and localized descriptions of this group.
	 *
	 * @return the locales and localized descriptions of this group
	 */
	@Override
	public Map<java.util.Locale, String> getDescriptionMap() {
		return model.getDescriptionMap();
	}

	@Override
	public String getDescriptiveName()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDescriptiveName();
	}

	@Override
	public String getDescriptiveName(java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDescriptiveName(locale);
	}

	@Override
	public Map<java.util.Locale, String> getDescriptiveNameMap()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDescriptiveNameMap();
	}

	@Override
	public String getDisplayURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		return model.getDisplayURL(themeDisplay);
	}

	@Override
	public String getDisplayURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay,
		boolean privateLayout) {

		return model.getDisplayURL(themeDisplay, privateLayout);
	}

	@Override
	public String getDisplayURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay,
		boolean privateLayout, boolean controlPanel) {

		return model.getDisplayURL(themeDisplay, privateLayout, controlPanel);
	}

	/**
	 * Returns the external reference code of this group.
	 *
	 * @return the external reference code of this group
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the friendly url of this group.
	 *
	 * @return the friendly url of this group
	 */
	@Override
	public String getFriendlyURL() {
		return model.getFriendlyURL();
	}

	/**
	 * Returns the group ID of this group.
	 *
	 * @return the group ID of this group
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the group key of this group.
	 *
	 * @return the group key of this group
	 */
	@Override
	public String getGroupKey() {
		return model.getGroupKey();
	}

	@Override
	public String getIconCssClass() {
		return model.getIconCssClass();
	}

	@Override
	public String getIconURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		return model.getIconURL(themeDisplay);
	}

	/**
	 * Returns the inherit content of this group.
	 *
	 * @return the inherit content of this group
	 */
	@Override
	public boolean getInheritContent() {
		return model.getInheritContent();
	}

	@Override
	public String getLayoutRootNodeName(
		boolean privateLayout, java.util.Locale locale) {

		return model.getLayoutRootNodeName(privateLayout, locale);
	}

	@Override
	public Group getLiveGroup() {
		return model.getLiveGroup();
	}

	/**
	 * Returns the live group ID of this group.
	 *
	 * @return the live group ID of this group
	 */
	@Override
	public long getLiveGroupId() {
		return model.getLiveGroupId();
	}

	@Override
	public String getLiveParentTypeSettingsProperty(String key) {
		return model.getLiveParentTypeSettingsProperty(key);
	}

	@Override
	public String getLogoURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay,
		boolean useDefault) {

		return model.getLogoURL(themeDisplay, useDefault);
	}

	@Override
	public long getMaintenanceUtilityPageEntryId() {
		return model.getMaintenanceUtilityPageEntryId();
	}

	/**
	 * Returns the manual membership of this group.
	 *
	 * @return the manual membership of this group
	 */
	@Override
	public boolean getManualMembership() {
		return model.getManualMembership();
	}

	/**
	 * Returns the membership restriction of this group.
	 *
	 * @return the membership restriction of this group
	 */
	@Override
	public int getMembershipRestriction() {
		return model.getMembershipRestriction();
	}

	/**
	 * Returns the modified date of this group.
	 *
	 * @return the modified date of this group
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this group.
	 *
	 * @return the mvcc version of this group
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this group.
	 *
	 * @return the name of this group
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the localized name of this group in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this group
	 */
	@Override
	public String getName(java.util.Locale locale) {
		return model.getName(locale);
	}

	/**
	 * Returns the localized name of this group in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this group. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@Override
	public String getName(java.util.Locale locale, boolean useDefault) {
		return model.getName(locale, useDefault);
	}

	/**
	 * Returns the localized name of this group in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this group
	 */
	@Override
	public String getName(String languageId) {
		return model.getName(languageId);
	}

	/**
	 * Returns the localized name of this group in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this group
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
	 * Returns a map of the locales and localized names of this group.
	 *
	 * @return the locales and localized names of this group
	 */
	@Override
	public Map<java.util.Locale, String> getNameMap() {
		return model.getNameMap();
	}

	@Override
	public long getOrganizationId() {
		return model.getOrganizationId();
	}

	@Override
	public Group getParentGroup() {
		return model.getParentGroup();
	}

	/**
	 * Returns the parent group ID of this group.
	 *
	 * @return the parent group ID of this group
	 */
	@Override
	public long getParentGroupId() {
		return model.getParentGroupId();
	}

	@Override
	public com.liferay.portal.kernel.util.UnicodeProperties
		getParentLiveGroupTypeSettingsProperties() {

		return model.getParentLiveGroupTypeSettingsProperties();
	}

	@Override
	public String getPathFriendlyURL(
		boolean privateLayout,
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		return model.getPathFriendlyURL(privateLayout, themeDisplay);
	}

	/**
	 * Returns the primary key of this group.
	 *
	 * @return the primary key of this group
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public LayoutSet getPrivateLayoutSet() {
		return model.getPrivateLayoutSet();
	}

	@Override
	public int getPrivateLayoutsPageCount() {
		return model.getPrivateLayoutsPageCount();
	}

	@Override
	public LayoutSet getPublicLayoutSet() {
		return model.getPublicLayoutSet();
	}

	@Override
	public int getPublicLayoutsPageCount() {
		return model.getPublicLayoutsPageCount();
	}

	@Override
	public long getRemoteLiveGroupId() {
		return model.getRemoteLiveGroupId();
	}

	/**
	 * Returns the remote staging group count of this group.
	 *
	 * @return the remote staging group count of this group
	 */
	@Override
	public int getRemoteStagingGroupCount() {
		return model.getRemoteStagingGroupCount();
	}

	@Override
	public String getScopeDescriptiveName(
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getScopeDescriptiveName(themeDisplay);
	}

	@Override
	public String getScopeLabel(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		return model.getScopeLabel(themeDisplay);
	}

	@Override
	public String getScopeSimpleName(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		return model.getScopeSimpleName(themeDisplay);
	}

	/**
	 * Returns the site of this group.
	 *
	 * @return the site of this group
	 */
	@Override
	public boolean getSite() {
		return model.getSite();
	}

	@Override
	public Group getStagingGroup() {
		return model.getStagingGroup();
	}

	/**
	 * Returns the tree path of this group.
	 *
	 * @return the tree path of this group
	 */
	@Override
	public String getTreePath() {
		return model.getTreePath();
	}

	/**
	 * Returns the type of this group.
	 *
	 * @return the type of this group
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	@Override
	public String getTypeLabel() {
		return model.getTypeLabel();
	}

	/**
	 * Returns the type settings of this group.
	 *
	 * @return the type settings of this group
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
	public String getUnambiguousName(String name, java.util.Locale locale) {
		return model.getUnambiguousName(name, locale);
	}

	/**
	 * Returns the uuid of this group.
	 *
	 * @return the uuid of this group
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public boolean hasAncestor(long groupId) {
		return model.hasAncestor(groupId);
	}

	@Override
	public boolean hasLocalOrRemoteStagingGroup() {
		return model.hasLocalOrRemoteStagingGroup();
	}

	@Override
	public boolean hasPrivateLayouts() {
		return model.hasPrivateLayouts();
	}

	@Override
	public boolean hasPublicLayouts() {
		return model.hasPublicLayouts();
	}

	@Override
	public boolean hasRemoteStagingGroup() {
		return model.hasRemoteStagingGroup();
	}

	@Override
	public boolean hasStagingGroup() {
		return model.hasStagingGroup();
	}

	/**
	 * Returns <code>true</code> if this group is active.
	 *
	 * @return <code>true</code> if this group is active; <code>false</code> otherwise
	 */
	@Override
	public boolean isActive() {
		return model.isActive();
	}

	@Override
	public boolean isCMS() {
		return model.isCMS();
	}

	@Override
	public boolean isCompany() {
		return model.isCompany();
	}

	@Override
	public boolean isCompanyStagingGroup() {
		return model.isCompanyStagingGroup();
	}

	@Override
	public boolean isContentSharingWithChildrenEnabled() {
		return model.isContentSharingWithChildrenEnabled();
	}

	@Override
	public boolean isControlPanel() {
		return model.isControlPanel();
	}

	@Override
	public boolean isDepot() {
		return model.isDepot();
	}

	@Override
	public boolean isGuest() {
		return model.isGuest();
	}

	/**
	 * Returns <code>true</code> if this group is inherit content.
	 *
	 * @return <code>true</code> if this group is inherit content; <code>false</code> otherwise
	 */
	@Override
	public boolean isInheritContent() {
		return model.isInheritContent();
	}

	@Override
	public boolean isInStagingPortlet(String portletId) {
		return model.isInStagingPortlet(portletId);
	}

	@Override
	public boolean isLayout() {
		return model.isLayout();
	}

	@Override
	public boolean isLayoutPrototype() {
		return model.isLayoutPrototype();
	}

	@Override
	public boolean isLayoutSetPrototype() {
		return model.isLayoutSetPrototype();
	}

	@Override
	public boolean isLimitedToParentSiteMembers() {
		return model.isLimitedToParentSiteMembers();
	}

	@Override
	public boolean isMaintenanceMode() {
		return model.isMaintenanceMode();
	}

	/**
	 * Returns <code>true</code> if this group is manual membership.
	 *
	 * @return <code>true</code> if this group is manual membership; <code>false</code> otherwise
	 */
	@Override
	public boolean isManualMembership() {
		return model.isManualMembership();
	}

	@Override
	public boolean isOrganization() {
		return model.isOrganization();
	}

	@Override
	public boolean isPrivateLayoutsEnabled() {
		return model.isPrivateLayoutsEnabled();
	}

	@Override
	public boolean isRegularSite() {
		return model.isRegularSite();
	}

	@Override
	public boolean isRoot() {
		return model.isRoot();
	}

	@Override
	public boolean isShowSite(
			com.liferay.portal.kernel.security.permission.PermissionChecker
				permissionChecker,
			boolean privateSite)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.isShowSite(permissionChecker, privateSite);
	}

	/**
	 * Returns <code>true</code> if this group is site.
	 *
	 * @return <code>true</code> if this group is site; <code>false</code> otherwise
	 */
	@Override
	public boolean isSite() {
		return model.isSite();
	}

	@Override
	public boolean isStaged() {
		return model.isStaged();
	}

	@Override
	public boolean isStagedPortlet(String portletId) {
		return model.isStagedPortlet(portletId);
	}

	@Override
	public boolean isStagedRemotely() {
		return model.isStagedRemotely();
	}

	@Override
	public boolean isStagingGroup() {
		return model.isStagingGroup();
	}

	@Override
	public boolean isUser() {
		return model.isUser();
	}

	@Override
	public boolean isUserGroup() {
		return model.isUserGroup();
	}

	@Override
	public boolean isUserPersonalSite() {
		return model.isUserPersonalSite();
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
	 * Sets whether this group is active.
	 *
	 * @param active the active of this group
	 */
	@Override
	public void setActive(boolean active) {
		model.setActive(active);
	}

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this group.
	 *
	 * @param classNameId the class name ID of this group
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this group.
	 *
	 * @param classPK the class pk of this group
	 */
	@Override
	public void setClassPK(long classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the company ID of this group.
	 *
	 * @param companyId the company ID of this group
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the creator user ID of this group.
	 *
	 * @param creatorUserId the creator user ID of this group
	 */
	@Override
	public void setCreatorUserId(long creatorUserId) {
		model.setCreatorUserId(creatorUserId);
	}

	/**
	 * Sets the creator user uuid of this group.
	 *
	 * @param creatorUserUuid the creator user uuid of this group
	 */
	@Override
	public void setCreatorUserUuid(String creatorUserUuid) {
		model.setCreatorUserUuid(creatorUserUuid);
	}

	/**
	 * Sets the ct collection ID of this group.
	 *
	 * @param ctCollectionId the ct collection ID of this group
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the description of this group.
	 *
	 * @param description the description of this group
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the localized description of this group in the language.
	 *
	 * @param description the localized description of this group
	 * @param locale the locale of the language
	 */
	@Override
	public void setDescription(String description, java.util.Locale locale) {
		model.setDescription(description, locale);
	}

	/**
	 * Sets the localized description of this group in the language, and sets the default locale.
	 *
	 * @param description the localized description of this group
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
	 * Sets the localized descriptions of this group from the map of locales and localized descriptions.
	 *
	 * @param descriptionMap the locales and localized descriptions of this group
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap) {

		model.setDescriptionMap(descriptionMap);
	}

	/**
	 * Sets the localized descriptions of this group from the map of locales and localized descriptions, and sets the default locale.
	 *
	 * @param descriptionMap the locales and localized descriptions of this group
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setDescriptionMap(
		Map<java.util.Locale, String> descriptionMap,
		java.util.Locale defaultLocale) {

		model.setDescriptionMap(descriptionMap, defaultLocale);
	}

	/**
	 * Sets the external reference code of this group.
	 *
	 * @param externalReferenceCode the external reference code of this group
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the friendly url of this group.
	 *
	 * @param friendlyURL the friendly url of this group
	 */
	@Override
	public void setFriendlyURL(String friendlyURL) {
		model.setFriendlyURL(friendlyURL);
	}

	/**
	 * Sets the group ID of this group.
	 *
	 * @param groupId the group ID of this group
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the group key of this group.
	 *
	 * @param groupKey the group key of this group
	 */
	@Override
	public void setGroupKey(String groupKey) {
		model.setGroupKey(groupKey);
	}

	/**
	 * Sets whether this group is inherit content.
	 *
	 * @param inheritContent the inherit content of this group
	 */
	@Override
	public void setInheritContent(boolean inheritContent) {
		model.setInheritContent(inheritContent);
	}

	/**
	 * Sets the live group ID of this group.
	 *
	 * @param liveGroupId the live group ID of this group
	 */
	@Override
	public void setLiveGroupId(long liveGroupId) {
		model.setLiveGroupId(liveGroupId);
	}

	/**
	 * Sets whether this group is manual membership.
	 *
	 * @param manualMembership the manual membership of this group
	 */
	@Override
	public void setManualMembership(boolean manualMembership) {
		model.setManualMembership(manualMembership);
	}

	/**
	 * Sets the membership restriction of this group.
	 *
	 * @param membershipRestriction the membership restriction of this group
	 */
	@Override
	public void setMembershipRestriction(int membershipRestriction) {
		model.setMembershipRestriction(membershipRestriction);
	}

	/**
	 * Sets the modified date of this group.
	 *
	 * @param modifiedDate the modified date of this group
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this group.
	 *
	 * @param mvccVersion the mvcc version of this group
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this group.
	 *
	 * @param name the name of this group
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the localized name of this group in the language.
	 *
	 * @param name the localized name of this group
	 * @param locale the locale of the language
	 */
	@Override
	public void setName(String name, java.util.Locale locale) {
		model.setName(name, locale);
	}

	/**
	 * Sets the localized name of this group in the language, and sets the default locale.
	 *
	 * @param name the localized name of this group
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
	 * Sets the localized names of this group from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this group
	 */
	@Override
	public void setNameMap(Map<java.util.Locale, String> nameMap) {
		model.setNameMap(nameMap);
	}

	/**
	 * Sets the localized names of this group from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this group
	 * @param defaultLocale the default locale
	 */
	@Override
	public void setNameMap(
		Map<java.util.Locale, String> nameMap, java.util.Locale defaultLocale) {

		model.setNameMap(nameMap, defaultLocale);
	}

	/**
	 * Sets the parent group ID of this group.
	 *
	 * @param parentGroupId the parent group ID of this group
	 */
	@Override
	public void setParentGroupId(long parentGroupId) {
		model.setParentGroupId(parentGroupId);
	}

	/**
	 * Sets the primary key of this group.
	 *
	 * @param primaryKey the primary key of this group
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the remote staging group count of this group.
	 *
	 * @param remoteStagingGroupCount the remote staging group count of this group
	 */
	@Override
	public void setRemoteStagingGroupCount(int remoteStagingGroupCount) {
		model.setRemoteStagingGroupCount(remoteStagingGroupCount);
	}

	/**
	 * Sets whether this group is site.
	 *
	 * @param site the site of this group
	 */
	@Override
	public void setSite(boolean site) {
		model.setSite(site);
	}

	/**
	 * Sets the tree path of this group.
	 *
	 * @param treePath the tree path of this group
	 */
	@Override
	public void setTreePath(String treePath) {
		model.setTreePath(treePath);
	}

	/**
	 * Sets the type of this group.
	 *
	 * @param type the type of this group
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the type settings of this group.
	 *
	 * @param typeSettings the type settings of this group
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
	 * Sets the uuid of this group.
	 *
	 * @param uuid the uuid of this group
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
	public void updateTreePath(String treePath) {
		model.updateTreePath(treePath);
	}

	@Override
	public Map<String, Function<Group, Object>> getAttributeGetterFunctions() {
		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<Group, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected GroupWrapper wrap(Group group) {
		return new GroupWrapper(group);
	}

}