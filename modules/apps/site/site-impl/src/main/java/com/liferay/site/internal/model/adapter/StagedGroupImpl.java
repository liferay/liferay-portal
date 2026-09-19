/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.adapter;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupWrapper;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.model.adapter.StagedGroup;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Daniel Kocsis
 */
public class StagedGroupImpl extends GroupWrapper implements StagedGroup {

	public StagedGroupImpl(Group group) {
		super(group);

		Objects.requireNonNull(
			group, "Unable to create a new staged group for a null group");

		_group = group;
	}

	@Override
	public Object clone() {
		return new StagedGroupImpl((Group)_group.clone());
	}

	@Override
	public String getClassName() {
		return _group.getClassName();
	}

	@Override
	public long getClassNameId() {
		return _group.getClassNameId();
	}

	@Override
	public long getClassPK() {
		return _group.getClassPK();
	}

	@Override
	public long getCompanyId() {
		return _group.getCompanyId();
	}

	@Override
	public Date getCreateDate() {
		return new Date();
	}

	@Override
	public long getCreatorUserId() {
		return _group.getCreatorUserId();
	}

	@Override
	public String getCreatorUserUuid() {
		return _group.getCreatorUserUuid();
	}

	@Override
	public String getDefaultLanguageId() {
		return _group.getDefaultLanguageId();
	}

	@Override
	public long getDefaultPrivatePlid() {
		return _group.getDefaultPrivatePlid();
	}

	@Override
	public long getDefaultPublicPlid() {
		return _group.getDefaultPublicPlid();
	}

	@Override
	public List<Group> getDescendants(boolean site) {
		return _group.getDescendants(site);
	}

	@Override
	public String getDescription() {
		return _group.getDescription();
	}

	@Override
	public String getDescription(Locale locale) {
		return _group.getDescription(locale);
	}

	@Override
	public String getDescription(Locale locale, boolean useDefault) {
		return _group.getDescription(locale, useDefault);
	}

	@Override
	public String getDescription(String languageId) {
		return _group.getDescription(languageId);
	}

	@Override
	public String getDescription(String languageId, boolean useDefault) {
		return _group.getDescription(languageId, useDefault);
	}

	@Override
	public String getDescriptionCurrentLanguageId() {
		return _group.getDescriptionCurrentLanguageId();
	}

	@Override
	public String getDescriptionCurrentValue() {
		return _group.getDescriptionCurrentValue();
	}

	@Override
	public Map<Locale, String> getDescriptionMap() {
		return _group.getDescriptionMap();
	}

	@Override
	public String getDescriptiveName() throws PortalException {
		return _group.getDescriptiveName();
	}

	@Override
	public String getDescriptiveName(Locale locale) throws PortalException {
		return _group.getDescriptiveName(locale);
	}

	@Override
	public String getDisplayURL(ThemeDisplay themeDisplay) {
		return _group.getDisplayURL(themeDisplay);
	}

	@Override
	public String getDisplayURL(
		ThemeDisplay themeDisplay, boolean privateLayout) {

		return _group.getDisplayURL(themeDisplay, privateLayout);
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _group.getExpandoBridge();
	}

	@Override
	public String getFriendlyURL() {
		return _group.getFriendlyURL();
	}

	@Override
	public Group getGroup() {
		return _group;
	}

	@Override
	public long getGroupId() {
		return _group.getGroupId();
	}

	@Override
	public String getGroupKey() {
		return _group.getGroupKey();
	}

	@Override
	public String getIconCssClass() {
		return _group.getIconCssClass();
	}

	@Override
	public String getIconURL(ThemeDisplay themeDisplay) {
		return _group.getIconURL(themeDisplay);
	}

	@Override
	public boolean getInheritContent() {
		return _group.isInheritContent();
	}

	@Override
	public String getLayoutRootNodeName(boolean privateLayout, Locale locale) {
		return _group.getLayoutRootNodeName(privateLayout, locale);
	}

	@Override
	public Group getLiveGroup() {
		return _group.getLiveGroup();
	}

	@Override
	public long getLiveGroupId() {
		return _group.getLiveGroupId();
	}

	@Override
	public String getLiveParentTypeSettingsProperty(String key) {
		return _group.getLiveParentTypeSettingsProperty(key);
	}

	@Override
	public String getLogoURL(ThemeDisplay themeDisplay, boolean useDefault) {
		return _group.getLogoURL(themeDisplay, useDefault);
	}

	@Override
	public boolean getManualMembership() {
		return _group.isManualMembership();
	}

	@Override
	public int getMembershipRestriction() {
		return _group.getMembershipRestriction();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		return _group.getModelAttributes();
	}

	@Override
	public Class<?> getModelClass() {
		return StagedGroup.class;
	}

	@Override
	public String getModelClassName() {
		return StagedGroup.class.getName();
	}

	@Override
	public Date getModifiedDate() {
		return new Date();
	}

	@Override
	public long getMvccVersion() {
		return _group.getMvccVersion();
	}

	@Override
	public String getName() {
		return _group.getName();
	}

	@Override
	public String getName(Locale locale) {
		return _group.getName(locale);
	}

	@Override
	public String getName(Locale locale, boolean useDefault) {
		return _group.getName(locale, useDefault);
	}

	@Override
	public String getName(String languageId) {
		return _group.getName(languageId);
	}

	@Override
	public String getName(String languageId, boolean useDefault) {
		return _group.getName(languageId, useDefault);
	}

	@Override
	public String getNameCurrentLanguageId() {
		return _group.getNameCurrentLanguageId();
	}

	@Override
	public String getNameCurrentValue() {
		return _group.getNameCurrentValue();
	}

	@Override
	public Map<Locale, String> getNameMap() {
		return _group.getNameMap();
	}

	@Override
	public long getOrganizationId() {
		return _group.getOrganizationId();
	}

	@Override
	public Group getParentGroup() {
		return _group.getParentGroup();
	}

	@Override
	public long getParentGroupId() {
		return _group.getParentGroupId();
	}

	@Override
	public UnicodeProperties getParentLiveGroupTypeSettingsProperties() {
		return _group.getParentLiveGroupTypeSettingsProperties();
	}

	@Override
	public String getPathFriendlyURL(
		boolean privateLayout, ThemeDisplay themeDisplay) {

		return _group.getPathFriendlyURL(privateLayout, themeDisplay);
	}

	@Override
	public long getPrimaryKey() {
		return _group.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _group.getPrimaryKeyObj();
	}

	@Override
	public LayoutSet getPrivateLayoutSet() {
		return _group.getPrivateLayoutSet();
	}

	@Override
	public int getPrivateLayoutsPageCount() {
		return _group.getPrivateLayoutsPageCount();
	}

	@Override
	public LayoutSet getPublicLayoutSet() {
		return _group.getPublicLayoutSet();
	}

	@Override
	public int getPublicLayoutsPageCount() {
		return _group.getPublicLayoutsPageCount();
	}

	@Override
	public long getRemoteLiveGroupId() {
		return _group.getRemoteLiveGroupId();
	}

	@Override
	public int getRemoteStagingGroupCount() {
		return _group.getRemoteStagingGroupCount();
	}

	@Override
	public String getScopeDescriptiveName(ThemeDisplay themeDisplay)
		throws PortalException {

		return _group.getScopeDescriptiveName(themeDisplay);
	}

	@Override
	public String getScopeLabel(ThemeDisplay themeDisplay) {
		return _group.getScopeLabel(themeDisplay);
	}

	@Override
	public boolean getSite() {
		return _group.isSite();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(StagedGroup.class);
	}

	@Override
	public Group getStagingGroup() {
		return _group.getStagingGroup();
	}

	@Override
	public String getTreePath() {
		return _group.getTreePath();
	}

	@Override
	public int getType() {
		return _group.getType();
	}

	@Override
	public String getTypeLabel() {
		return _group.getTypeLabel();
	}

	@Override
	public String getTypeSettings() {
		return _group.getTypeSettings();
	}

	@Override
	public UnicodeProperties getTypeSettingsProperties() {
		return _group.getTypeSettingsProperties();
	}

	@Override
	public String getTypeSettingsProperty(String key) {
		return _group.getTypeSettingsProperty(key);
	}

	@Override
	public String getUnambiguousName(String name, Locale locale) {
		return _group.getUnambiguousName(name, locale);
	}

	@Override
	public String getUuid() {
		return _group.getUuid();
	}

	@Override
	public boolean hasAncestor(long groupId) {
		return _group.hasAncestor(groupId);
	}

	@Override
	public boolean hasLocalOrRemoteStagingGroup() {
		return _group.hasLocalOrRemoteStagingGroup();
	}

	@Override
	public boolean hasPrivateLayouts() {
		return _group.hasPrivateLayouts();
	}

	@Override
	public boolean hasPublicLayouts() {
		return _group.hasPublicLayouts();
	}

	@Override
	public boolean hasRemoteStagingGroup() {
		return _group.hasRemoteStagingGroup();
	}

	@Override
	public boolean hasStagingGroup() {
		return _group.hasStagingGroup();
	}

	@Override
	public boolean isActive() {
		return _group.isActive();
	}

	@Override
	public boolean isCachedModel() {
		return _group.isCachedModel();
	}

	@Override
	public boolean isCompany() {
		return _group.isCompany();
	}

	@Override
	public boolean isCompanyStagingGroup() {
		return _group.isCompanyStagingGroup();
	}

	@Override
	public boolean isControlPanel() {
		return _group.isControlPanel();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public boolean isEntityCacheEnabled() {
		return _group.isEntityCacheEnabled();
	}

	@Override
	public boolean isEscapedModel() {
		return _group.isEscapedModel();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public boolean isFinderCacheEnabled() {
		return _group.isFinderCacheEnabled();
	}

	@Override
	public boolean isGuest() {
		return _group.isGuest();
	}

	@Override
	public boolean isInStagingPortlet(String portletId) {
		return _group.isInStagingPortlet(portletId);
	}

	@Override
	public boolean isInheritContent() {
		return _group.isInheritContent();
	}

	@Override
	public boolean isLayout() {
		return _group.isLayout();
	}

	@Override
	public boolean isLayoutPrototype() {
		return _group.isLayoutPrototype();
	}

	@Override
	public boolean isLayoutSetPrototype() {
		return _group.isLayoutSetPrototype();
	}

	@Override
	public boolean isLimitedToParentSiteMembers() {
		return _group.isLimitedToParentSiteMembers();
	}

	@Override
	public boolean isManualMembership() {
		return _group.isManualMembership();
	}

	@Override
	public boolean isNew() {
		return _group.isNew();
	}

	@Override
	public boolean isOrganization() {
		return _group.isOrganization();
	}

	@Override
	public boolean isRegularSite() {
		return _group.isRegularSite();
	}

	@Override
	public boolean isRoot() {
		return _group.isRoot();
	}

	@Override
	public boolean isShowSite(
			PermissionChecker permissionChecker, boolean privateSite)
		throws PortalException {

		return _group.isShowSite(permissionChecker, privateSite);
	}

	@Override
	public boolean isSite() {
		return _group.isSite();
	}

	@Override
	public boolean isStaged() {
		return _group.isStaged();
	}

	@Override
	public boolean isStagedPortlet(String portletId) {
		return _group.isStagedPortlet(portletId);
	}

	@Override
	public boolean isStagedRemotely() {
		return _group.isStagedRemotely();
	}

	@Override
	public boolean isStagingGroup() {
		return _group.isStagingGroup();
	}

	@Override
	public boolean isUser() {
		return _group.isUser();
	}

	@Override
	public boolean isUserGroup() {
		return _group.isUserGroup();
	}

	@Override
	public boolean isUserPersonalSite() {
		return _group.isUserPersonalSite();
	}

	@Override
	public void persist() {
		_group.persist();
	}

	@Override
	public void prepareLocalizedFieldsForImport() throws LocaleException {
		_group.prepareLocalizedFieldsForImport();
	}

	@Override
	public void prepareLocalizedFieldsForImport(Locale defaultImportLocale)
		throws LocaleException {

		_group.prepareLocalizedFieldsForImport(defaultImportLocale);
	}

	@Override
	public void resetOriginalValues() {
		_group.resetOriginalValues();
	}

	@Override
	public void setActive(boolean active) {
		_group.setActive(active);
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_group.setCachedModel(cachedModel);
	}

	@Override
	public void setClassName(String className) {
		_group.setClassName(className);
	}

	@Override
	public void setClassNameId(long classNameId) {
		_group.setClassNameId(classNameId);
	}

	@Override
	public void setClassPK(long classPK) {
		_group.setClassPK(classPK);
	}

	@Override
	public void setCompanyId(long companyId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCreateDate(Date createDate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCreatorUserId(long creatorUserId) {
		_group.setCreatorUserId(creatorUserId);
	}

	@Override
	public void setCreatorUserUuid(String creatorUserUuid) {
		_group.setCreatorUserUuid(creatorUserUuid);
	}

	@Override
	public void setDescription(String description) {
		_group.setDescription(description);
	}

	@Override
	public void setDescription(String description, Locale locale) {
		_group.setDescription(description, locale);
	}

	@Override
	public void setDescription(
		String description, Locale locale, Locale defaultLocale) {

		_group.setDescription(description, locale, defaultLocale);
	}

	@Override
	public void setDescriptionCurrentLanguageId(String languageId) {
		_group.setDescriptionCurrentLanguageId(languageId);
	}

	@Override
	public void setDescriptionMap(Map<Locale, String> descriptionMap) {
		_group.setDescriptionMap(descriptionMap);
	}

	@Override
	public void setDescriptionMap(
		Map<Locale, String> descriptionMap, Locale defaultLocale) {

		_group.setDescriptionMap(descriptionMap, defaultLocale);
	}

	@Override
	public void setExpandoBridgeAttributes(BaseModel<?> baseModel) {
		_group.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		_group.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		_group.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public void setFriendlyURL(String friendlyURL) {
		_group.setFriendlyURL(friendlyURL);
	}

	public void setGroup(Group group) {
		_group = group;
	}

	@Override
	public void setGroupId(long groupId) {
		_group.setGroupId(groupId);
	}

	@Override
	public void setGroupKey(String groupKey) {
		_group.setGroupKey(groupKey);
	}

	@Override
	public void setInheritContent(boolean inheritContent) {
		_group.setInheritContent(inheritContent);
	}

	@Override
	public void setLiveGroupId(long liveGroupId) {
		_group.setLiveGroupId(liveGroupId);
	}

	@Override
	public void setManualMembership(boolean manualMembership) {
		_group.setManualMembership(manualMembership);
	}

	@Override
	public void setMembershipRestriction(int membershipRestriction) {
		_group.setMembershipRestriction(membershipRestriction);
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		_group.setModelAttributes(attributes);
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		_group.setMvccVersion(mvccVersion);
	}

	@Override
	public void setName(String name) {
		_group.setName(name);
	}

	@Override
	public void setName(String name, Locale locale) {
		_group.setName(name, locale);
	}

	@Override
	public void setName(String name, Locale locale, Locale defaultLocale) {
		_group.setName(name, locale, defaultLocale);
	}

	@Override
	public void setNameCurrentLanguageId(String languageId) {
		_group.setNameCurrentLanguageId(languageId);
	}

	@Override
	public void setNameMap(Map<Locale, String> nameMap) {
		_group.setNameMap(nameMap);
	}

	@Override
	public void setNameMap(Map<Locale, String> nameMap, Locale defaultLocale) {
		_group.setNameMap(nameMap, defaultLocale);
	}

	@Override
	public void setNew(boolean n) {
		_group.setNew(n);
	}

	@Override
	public void setParentGroupId(long parentGroupId) {
		_group.setParentGroupId(parentGroupId);
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		_group.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRemoteStagingGroupCount(int remoteStagingGroupCount) {
		_group.setRemoteStagingGroupCount(remoteStagingGroupCount);
	}

	@Override
	public void setSite(boolean site) {
		_group.setSite(site);
	}

	@Override
	public void setTreePath(String treePath) {
		_group.setTreePath(treePath);
	}

	@Override
	public void setType(int type) {
		_group.setType(type);
	}

	@Override
	public void setTypeSettings(String typeSettings) {
		_group.setTypeSettings(typeSettings);
	}

	@Override
	public void setTypeSettingsProperties(
		UnicodeProperties typeSettingsUnicodeProperties) {

		_group.setTypeSettingsProperties(typeSettingsUnicodeProperties);
	}

	@Override
	public void setUuid(String uuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheModel<Group> toCacheModel() {
		return _group.toCacheModel();
	}

	@Override
	public Group toEscapedModel() {
		return _group.toEscapedModel();
	}

	@Override
	public Group toUnescapedModel() {
		return _group.toUnescapedModel();
	}

	@Override
	public void updateTreePath(String treePath) {
		_group.updateTreePath(treePath);
	}

	private Group _group;

}