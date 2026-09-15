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
 * This class is a wrapper for {@link LayoutSetBranch}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetBranch
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutSetBranchWrapper
	extends BaseModelWrapper<LayoutSetBranch>
	implements LayoutSetBranch, ModelWrapper<LayoutSetBranch> {

	public LayoutSetBranchWrapper(LayoutSetBranch layoutSetBranch) {
		super(layoutSetBranch);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("layoutSetBranchId", getLayoutSetBranchId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("privateLayout", isPrivateLayout());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("master", isMaster());
		attributes.put("logoId", getLogoId());
		attributes.put("themeId", getThemeId());
		attributes.put("colorSchemeId", getColorSchemeId());
		attributes.put("css", getCss());
		attributes.put("settings", getSettings());
		attributes.put("layoutSetPrototypeUuid", getLayoutSetPrototypeUuid());
		attributes.put(
			"layoutSetPrototypeLinkEnabled", isLayoutSetPrototypeLinkEnabled());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long layoutSetBranchId = (Long)attributes.get("layoutSetBranchId");

		if (layoutSetBranchId != null) {
			setLayoutSetBranchId(layoutSetBranchId);
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

		Boolean privateLayout = (Boolean)attributes.get("privateLayout");

		if (privateLayout != null) {
			setPrivateLayout(privateLayout);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		Boolean master = (Boolean)attributes.get("master");

		if (master != null) {
			setMaster(master);
		}

		Long logoId = (Long)attributes.get("logoId");

		if (logoId != null) {
			setLogoId(logoId);
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

		String settings = (String)attributes.get("settings");

		if (settings != null) {
			setSettings(settings);
		}

		String layoutSetPrototypeUuid = (String)attributes.get(
			"layoutSetPrototypeUuid");

		if (layoutSetPrototypeUuid != null) {
			setLayoutSetPrototypeUuid(layoutSetPrototypeUuid);
		}

		Boolean layoutSetPrototypeLinkEnabled = (Boolean)attributes.get(
			"layoutSetPrototypeLinkEnabled");

		if (layoutSetPrototypeLinkEnabled != null) {
			setLayoutSetPrototypeLinkEnabled(layoutSetPrototypeLinkEnabled);
		}
	}

	@Override
	public LayoutSetBranch cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public ColorScheme getColorScheme() {
		return model.getColorScheme();
	}

	/**
	 * Returns the color scheme ID of this layout set branch.
	 *
	 * @return the color scheme ID of this layout set branch
	 */
	@Override
	public String getColorSchemeId() {
		return model.getColorSchemeId();
	}

	/**
	 * Returns the company ID of this layout set branch.
	 *
	 * @return the company ID of this layout set branch
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this layout set branch.
	 *
	 * @return the create date of this layout set branch
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the css of this layout set branch.
	 *
	 * @return the css of this layout set branch
	 */
	@Override
	public String getCss() {
		return model.getCss();
	}

	/**
	 * Returns the description of this layout set branch.
	 *
	 * @return the description of this layout set branch
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	@Override
	public Group getGroup()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getGroup();
	}

	/**
	 * Returns the group ID of this layout set branch.
	 *
	 * @return the group ID of this layout set branch
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	@Override
	public LayoutSet getLayoutSet() {
		return model.getLayoutSet();
	}

	/**
	 * Returns the layout set branch ID of this layout set branch.
	 *
	 * @return the layout set branch ID of this layout set branch
	 */
	@Override
	public long getLayoutSetBranchId() {
		return model.getLayoutSetBranchId();
	}

	/**
	 * Returns the layout set prototype link enabled of this layout set branch.
	 *
	 * @return the layout set prototype link enabled of this layout set branch
	 */
	@Override
	public boolean getLayoutSetPrototypeLinkEnabled() {
		return model.getLayoutSetPrototypeLinkEnabled();
	}

	/**
	 * Returns the layout set prototype uuid of this layout set branch.
	 *
	 * @return the layout set prototype uuid of this layout set branch
	 */
	@Override
	public String getLayoutSetPrototypeUuid() {
		return model.getLayoutSetPrototypeUuid();
	}

	@Override
	public long getLiveLogoId() {
		return model.getLiveLogoId();
	}

	@Override
	public boolean getLogo() {
		return model.getLogo();
	}

	/**
	 * Returns the logo ID of this layout set branch.
	 *
	 * @return the logo ID of this layout set branch
	 */
	@Override
	public long getLogoId() {
		return model.getLogoId();
	}

	/**
	 * Returns the master of this layout set branch.
	 *
	 * @return the master of this layout set branch
	 */
	@Override
	public boolean getMaster() {
		return model.getMaster();
	}

	/**
	 * Returns the modified date of this layout set branch.
	 *
	 * @return the modified date of this layout set branch
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this layout set branch.
	 *
	 * @return the mvcc version of this layout set branch
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this layout set branch.
	 *
	 * @return the name of this layout set branch
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this layout set branch.
	 *
	 * @return the primary key of this layout set branch
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the private layout of this layout set branch.
	 *
	 * @return the private layout of this layout set branch
	 */
	@Override
	public boolean getPrivateLayout() {
		return model.getPrivateLayout();
	}

	/**
	 * Returns the settings of this layout set branch.
	 *
	 * @return the settings of this layout set branch
	 */
	@Override
	public String getSettings() {
		return model.getSettings();
	}

	@Override
	public com.liferay.portal.kernel.util.UnicodeProperties
		getSettingsProperties() {

		return model.getSettingsProperties();
	}

	@Override
	public String getSettingsProperty(String key) {
		return model.getSettingsProperty(key);
	}

	@Override
	public Theme getTheme() {
		return model.getTheme();
	}

	/**
	 * Returns the theme ID of this layout set branch.
	 *
	 * @return the theme ID of this layout set branch
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
	 * Returns the user ID of this layout set branch.
	 *
	 * @return the user ID of this layout set branch
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this layout set branch.
	 *
	 * @return the user name of this layout set branch
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this layout set branch.
	 *
	 * @return the user uuid of this layout set branch
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public boolean isLayoutSetPrototypeLinkActive() {
		return model.isLayoutSetPrototypeLinkActive();
	}

	/**
	 * Returns <code>true</code> if this layout set branch is layout set prototype link enabled.
	 *
	 * @return <code>true</code> if this layout set branch is layout set prototype link enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isLayoutSetPrototypeLinkEnabled() {
		return model.isLayoutSetPrototypeLinkEnabled();
	}

	@Override
	public boolean isLogo() {
		return model.isLogo();
	}

	/**
	 * Returns <code>true</code> if this layout set branch is master.
	 *
	 * @return <code>true</code> if this layout set branch is master; <code>false</code> otherwise
	 */
	@Override
	public boolean isMaster() {
		return model.isMaster();
	}

	/**
	 * Returns <code>true</code> if this layout set branch is private layout.
	 *
	 * @return <code>true</code> if this layout set branch is private layout; <code>false</code> otherwise
	 */
	@Override
	public boolean isPrivateLayout() {
		return model.isPrivateLayout();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the color scheme ID of this layout set branch.
	 *
	 * @param colorSchemeId the color scheme ID of this layout set branch
	 */
	@Override
	public void setColorSchemeId(String colorSchemeId) {
		model.setColorSchemeId(colorSchemeId);
	}

	/**
	 * Sets the company ID of this layout set branch.
	 *
	 * @param companyId the company ID of this layout set branch
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this layout set branch.
	 *
	 * @param createDate the create date of this layout set branch
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the css of this layout set branch.
	 *
	 * @param css the css of this layout set branch
	 */
	@Override
	public void setCss(String css) {
		model.setCss(css);
	}

	/**
	 * Sets the description of this layout set branch.
	 *
	 * @param description the description of this layout set branch
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the group ID of this layout set branch.
	 *
	 * @param groupId the group ID of this layout set branch
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the layout set branch ID of this layout set branch.
	 *
	 * @param layoutSetBranchId the layout set branch ID of this layout set branch
	 */
	@Override
	public void setLayoutSetBranchId(long layoutSetBranchId) {
		model.setLayoutSetBranchId(layoutSetBranchId);
	}

	/**
	 * Sets whether this layout set branch is layout set prototype link enabled.
	 *
	 * @param layoutSetPrototypeLinkEnabled the layout set prototype link enabled of this layout set branch
	 */
	@Override
	public void setLayoutSetPrototypeLinkEnabled(
		boolean layoutSetPrototypeLinkEnabled) {

		model.setLayoutSetPrototypeLinkEnabled(layoutSetPrototypeLinkEnabled);
	}

	/**
	 * Sets the layout set prototype uuid of this layout set branch.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid of this layout set branch
	 */
	@Override
	public void setLayoutSetPrototypeUuid(String layoutSetPrototypeUuid) {
		model.setLayoutSetPrototypeUuid(layoutSetPrototypeUuid);
	}

	/**
	 * Sets the logo ID of this layout set branch.
	 *
	 * @param logoId the logo ID of this layout set branch
	 */
	@Override
	public void setLogoId(long logoId) {
		model.setLogoId(logoId);
	}

	/**
	 * Sets whether this layout set branch is master.
	 *
	 * @param master the master of this layout set branch
	 */
	@Override
	public void setMaster(boolean master) {
		model.setMaster(master);
	}

	/**
	 * Sets the modified date of this layout set branch.
	 *
	 * @param modifiedDate the modified date of this layout set branch
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this layout set branch.
	 *
	 * @param mvccVersion the mvcc version of this layout set branch
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this layout set branch.
	 *
	 * @param name the name of this layout set branch
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this layout set branch.
	 *
	 * @param primaryKey the primary key of this layout set branch
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets whether this layout set branch is private layout.
	 *
	 * @param privateLayout the private layout of this layout set branch
	 */
	@Override
	public void setPrivateLayout(boolean privateLayout) {
		model.setPrivateLayout(privateLayout);
	}

	/**
	 * Sets the settings of this layout set branch.
	 *
	 * @param settings the settings of this layout set branch
	 */
	@Override
	public void setSettings(String settings) {
		model.setSettings(settings);
	}

	@Override
	public void setSettingsProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			settingsUnicodeProperties) {

		model.setSettingsProperties(settingsUnicodeProperties);
	}

	/**
	 * Sets the theme ID of this layout set branch.
	 *
	 * @param themeId the theme ID of this layout set branch
	 */
	@Override
	public void setThemeId(String themeId) {
		model.setThemeId(themeId);
	}

	/**
	 * Sets the user ID of this layout set branch.
	 *
	 * @param userId the user ID of this layout set branch
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this layout set branch.
	 *
	 * @param userName the user name of this layout set branch
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this layout set branch.
	 *
	 * @param userUuid the user uuid of this layout set branch
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
	protected LayoutSetBranchWrapper wrap(LayoutSetBranch layoutSetBranch) {
		return new LayoutSetBranchWrapper(layoutSetBranch);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-39543053