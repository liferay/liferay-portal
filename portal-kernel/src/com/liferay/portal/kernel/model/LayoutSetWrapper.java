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
 * This class is a wrapper for {@link LayoutSet}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSet
 * @generated
 */
public class LayoutSetWrapper
	extends BaseModelWrapper<LayoutSet>
	implements LayoutSet, ModelWrapper<LayoutSet> {

	public LayoutSetWrapper(LayoutSet layoutSet) {
		super(layoutSet);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("layoutSetId", getLayoutSetId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("privateLayout", isPrivateLayout());
		attributes.put("logoId", getLogoId());
		attributes.put("themeId", getThemeId());
		attributes.put("colorSchemeId", getColorSchemeId());
		attributes.put("faviconFileEntryId", getFaviconFileEntryId());
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

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		Long layoutSetId = (Long)attributes.get("layoutSetId");

		if (layoutSetId != null) {
			setLayoutSetId(layoutSetId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
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

		Long faviconFileEntryId = (Long)attributes.get("faviconFileEntryId");

		if (faviconFileEntryId != null) {
			setFaviconFileEntryId(faviconFileEntryId);
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
	public LayoutSet cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the layout set's color scheme.
	 *
	 * <p>
	 * Just like themes, color schemes can be configured on the layout set
	 * level. The layout set's color scheme can be overridden on the layout
	 * level.
	 * </p>
	 *
	 * @return the layout set's color scheme
	 */
	@Override
	public ColorScheme getColorScheme() {
		return model.getColorScheme();
	}

	/**
	 * Returns the color scheme ID of this layout set.
	 *
	 * @return the color scheme ID of this layout set
	 */
	@Override
	public String getColorSchemeId() {
		return model.getColorSchemeId();
	}

	@Override
	public String getCompanyFallbackVirtualHostname() {
		return model.getCompanyFallbackVirtualHostname();
	}

	/**
	 * Returns the company ID of this layout set.
	 *
	 * @return the company ID of this layout set
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this layout set.
	 *
	 * @return the create date of this layout set
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the css of this layout set.
	 *
	 * @return the css of this layout set
	 */
	@Override
	public String getCss() {
		return model.getCss();
	}

	/**
	 * Returns the ct collection ID of this layout set.
	 *
	 * @return the ct collection ID of this layout set
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the favicon file entry ID of this layout set.
	 *
	 * @return the favicon file entry ID of this layout set
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
	 * Returns the layout set's group.
	 *
	 * @return the layout set's group
	 */
	@Override
	public Group getGroup()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getGroup();
	}

	/**
	 * Returns the group ID of this layout set.
	 *
	 * @return the group ID of this layout set
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the layout set ID of this layout set.
	 *
	 * @return the layout set ID of this layout set
	 */
	@Override
	public long getLayoutSetId() {
		return model.getLayoutSetId();
	}

	/**
	 * Returns the layout set prototype's ID, or <code>0</code> if it has no
	 * layout set prototype.
	 *
	 * <p>
	 * Prototype is Liferay's technical name for a site template.
	 * </p>
	 *
	 * @return the layout set prototype's ID, or <code>0</code> if it has no
	 layout set prototype
	 */
	@Override
	public long getLayoutSetPrototypeId()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getLayoutSetPrototypeId();
	}

	/**
	 * Returns the layout set prototype link enabled of this layout set.
	 *
	 * @return the layout set prototype link enabled of this layout set
	 */
	@Override
	public boolean getLayoutSetPrototypeLinkEnabled() {
		return model.getLayoutSetPrototypeLinkEnabled();
	}

	/**
	 * Returns the layout set prototype uuid of this layout set.
	 *
	 * @return the layout set prototype uuid of this layout set
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
	 * Returns the logo ID of this layout set.
	 *
	 * @return the logo ID of this layout set
	 */
	@Override
	public long getLogoId() {
		return model.getLogoId();
	}

	@Override
	public java.util.List<Layout> getMergeFailFriendlyURLLayouts() {
		return model.getMergeFailFriendlyURLLayouts();
	}

	/**
	 * Returns the modified date of this layout set.
	 *
	 * @return the modified date of this layout set
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this layout set.
	 *
	 * @return the mvcc version of this layout set
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	@Override
	public int getPageCount() {
		return model.getPageCount();
	}

	/**
	 * Returns the primary key of this layout set.
	 *
	 * @return the primary key of this layout set
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the private layout of this layout set.
	 *
	 * @return the private layout of this layout set
	 */
	@Override
	public boolean getPrivateLayout() {
		return model.getPrivateLayout();
	}

	/**
	 * Returns the settings of this layout set.
	 *
	 * @return the settings of this layout set
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
	 * Returns the theme ID of this layout set.
	 *
	 * @return the theme ID of this layout set
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
	 * Returns the names of the layout set's virtual hosts.
	 *
	 * <p>
	 * When accessing a layout set that has a virtual host, the URL elements
	 * "/web/sitename" or "/group/sitename" can be omitted.
	 * </p>
	 *
	 * @return a map from the layout set's virtual host names to the language
	 ids configured for them. If the virtual host is configured
	 for the default language, it will map to the empty string instead
	 of a language id. If the layout set has no virtual hosts
	 configured, the returned map will be empty.
	 */
	@Override
	public java.util.NavigableMap<String, String> getVirtualHostnames() {
		return model.getVirtualHostnames();
	}

	@Override
	public boolean hasSetModifiedDate() {
		return model.hasSetModifiedDate();
	}

	@Override
	public boolean isLayoutSetPrototypeLinkActive() {
		return model.isLayoutSetPrototypeLinkActive();
	}

	/**
	 * Returns <code>true</code> if this layout set is layout set prototype link enabled.
	 *
	 * @return <code>true</code> if this layout set is layout set prototype link enabled; <code>false</code> otherwise
	 */
	@Override
	public boolean isLayoutSetPrototypeLinkEnabled() {
		return model.isLayoutSetPrototypeLinkEnabled();
	}

	@Override
	public boolean isLayoutSetPrototypeUpdateable() {
		return model.isLayoutSetPrototypeUpdateable();
	}

	@Override
	public boolean isLayoutSetReadyForPropagation() {
		return model.isLayoutSetReadyForPropagation();
	}

	@Override
	public boolean isLogo() {
		return model.isLogo();
	}

	/**
	 * Returns <code>true</code> if this layout set is private layout.
	 *
	 * @return <code>true</code> if this layout set is private layout; <code>false</code> otherwise
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
	 * Sets the color scheme ID of this layout set.
	 *
	 * @param colorSchemeId the color scheme ID of this layout set
	 */
	@Override
	public void setColorSchemeId(String colorSchemeId) {
		model.setColorSchemeId(colorSchemeId);
	}

	@Override
	public void setCompanyFallbackVirtualHostname(
		String companyFallbackVirtualHostname) {

		model.setCompanyFallbackVirtualHostname(companyFallbackVirtualHostname);
	}

	/**
	 * Sets the company ID of this layout set.
	 *
	 * @param companyId the company ID of this layout set
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this layout set.
	 *
	 * @param createDate the create date of this layout set
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the css of this layout set.
	 *
	 * @param css the css of this layout set
	 */
	@Override
	public void setCss(String css) {
		model.setCss(css);
	}

	/**
	 * Sets the ct collection ID of this layout set.
	 *
	 * @param ctCollectionId the ct collection ID of this layout set
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the favicon file entry ID of this layout set.
	 *
	 * @param faviconFileEntryId the favicon file entry ID of this layout set
	 */
	@Override
	public void setFaviconFileEntryId(long faviconFileEntryId) {
		model.setFaviconFileEntryId(faviconFileEntryId);
	}

	/**
	 * Sets the group ID of this layout set.
	 *
	 * @param groupId the group ID of this layout set
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the layout set ID of this layout set.
	 *
	 * @param layoutSetId the layout set ID of this layout set
	 */
	@Override
	public void setLayoutSetId(long layoutSetId) {
		model.setLayoutSetId(layoutSetId);
	}

	/**
	 * Sets whether this layout set is layout set prototype link enabled.
	 *
	 * @param layoutSetPrototypeLinkEnabled the layout set prototype link enabled of this layout set
	 */
	@Override
	public void setLayoutSetPrototypeLinkEnabled(
		boolean layoutSetPrototypeLinkEnabled) {

		model.setLayoutSetPrototypeLinkEnabled(layoutSetPrototypeLinkEnabled);
	}

	/**
	 * Sets the layout set prototype uuid of this layout set.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid of this layout set
	 */
	@Override
	public void setLayoutSetPrototypeUuid(String layoutSetPrototypeUuid) {
		model.setLayoutSetPrototypeUuid(layoutSetPrototypeUuid);
	}

	/**
	 * Sets the logo ID of this layout set.
	 *
	 * @param logoId the logo ID of this layout set
	 */
	@Override
	public void setLogoId(long logoId) {
		model.setLogoId(logoId);
	}

	/**
	 * Sets the modified date of this layout set.
	 *
	 * @param modifiedDate the modified date of this layout set
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this layout set.
	 *
	 * @param mvccVersion the mvcc version of this layout set
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this layout set.
	 *
	 * @param primaryKey the primary key of this layout set
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets whether this layout set is private layout.
	 *
	 * @param privateLayout the private layout of this layout set
	 */
	@Override
	public void setPrivateLayout(boolean privateLayout) {
		model.setPrivateLayout(privateLayout);
	}

	/**
	 * Sets the settings of this layout set.
	 *
	 * @param settings the settings of this layout set
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
	 * Sets the theme ID of this layout set.
	 *
	 * @param themeId the theme ID of this layout set
	 */
	@Override
	public void setThemeId(String themeId) {
		model.setThemeId(themeId);
	}

	/**
	 * Sets the names of the layout set's virtual host name and language IDs.
	 *
	 * @param virtualHostnames the map of the layout set's virtual host name and
	 language IDs
	 * @see #getVirtualHostnames()
	 */
	@Override
	public void setVirtualHostnames(
		java.util.NavigableMap<String, String> virtualHostnames) {

		model.setVirtualHostnames(virtualHostnames);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<LayoutSet, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<LayoutSet, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected LayoutSetWrapper wrap(LayoutSet layoutSet) {
		return new LayoutSetWrapper(layoutSet);
	}

}