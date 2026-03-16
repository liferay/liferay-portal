/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the Group service. Represents a row in the &quot;Group_&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see GroupModel
 * @generated
 */
@ImplementationClassName("com.liferay.portal.model.impl.GroupImpl")
@ProviderType
public interface Group extends GroupModel, PersistedModel, TreeModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.GroupImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<Group, Long> GROUP_ID_ACCESSOR =
		new Accessor<Group, Long>() {

			@Override
			public Long get(Group group) {
				return group.getGroupId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<Group> getTypeClass() {
				return Group.class;
			}

		};

	public void clearStagingGroup();

	public java.util.List<Group> getAncestors();

	public java.util.List<Group> getChildren(boolean site);

	public java.util.List<Group> getChildrenWithLayouts(
		boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	public int getChildrenWithLayoutsCount(boolean site);

	public long getDefaultPrivatePlid();

	public long getDefaultPublicPlid();

	public java.util.List<Group> getDescendants(boolean site);

	@com.liferay.portal.kernel.json.JSON
	public String getDescriptiveName()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getDescriptiveName(java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.Map<java.util.Locale, String> getDescriptiveNameMap()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getDisplayURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay);

	public String getDisplayURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay,
		boolean privateLayout);

	public String getDisplayURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay,
		boolean privateLayout, boolean controlPanel);

	public String getIconCssClass();

	public String getIconURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay);

	public String getLayoutRootNodeName(
		boolean privateLayout, java.util.Locale locale);

	public Group getLiveGroup();

	public String getLiveParentTypeSettingsProperty(String key);

	public String getLogoURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay,
		boolean useDefault);

	public long getMaintenanceUtilityPageEntryId();

	public long getOrganizationId();

	public Group getParentGroup();

	public com.liferay.portal.kernel.util.UnicodeProperties
		getParentLiveGroupTypeSettingsProperties();

	public String getPathFriendlyURL(
		boolean privateLayout,
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay);

	public LayoutSet getPrivateLayoutSet();

	public int getPrivateLayoutsPageCount();

	public LayoutSet getPublicLayoutSet();

	public int getPublicLayoutsPageCount();

	public long getRemoteLiveGroupId();

	public String getScopeDescriptiveName(
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getScopeLabel(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay);

	public String getScopeSimpleName(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay);

	public Group getStagingGroup();

	public String getTypeLabel();

	public com.liferay.portal.kernel.util.UnicodeProperties
		getTypeSettingsProperties();

	public String getTypeSettingsProperty(String key);

	public String getUnambiguousName(String name, java.util.Locale locale);

	public boolean hasAncestor(long groupId);

	public boolean hasLocalOrRemoteStagingGroup();

	public boolean hasPrivateLayouts();

	public boolean hasPublicLayouts();

	public boolean hasRemoteStagingGroup();

	public boolean hasStagingGroup();

	public boolean isCMS();

	public boolean isCompany();

	public boolean isCompanyStagingGroup();

	public boolean isContentSharingWithChildrenEnabled();

	public boolean isControlPanel();

	public boolean isDepot();

	public boolean isGuest();

	public boolean isInStagingPortlet(String portletId);

	public boolean isLayout();

	public boolean isLayoutPrototype();

	public boolean isLayoutSetPrototype();

	public boolean isLimitedToParentSiteMembers();

	public boolean isMaintenanceMode();

	public boolean isOrganization();

	public boolean isPrivateLayoutsEnabled();

	public boolean isRegularSite();

	public boolean isRoot();

	public boolean isShowSite(
			com.liferay.portal.kernel.security.permission.PermissionChecker
				permissionChecker,
			boolean privateSite)
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean isStaged();

	public boolean isStagedPortlet(String portletId);

	public boolean isStagedRemotely();

	public boolean isStagingGroup();

	public boolean isUser();

	public boolean isUserGroup();

	public boolean isUserPersonalSite();

	public void setTypeSettingsProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			typeSettingsUnicodeProperties);

}