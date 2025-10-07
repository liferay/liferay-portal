/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the LayoutSet service. Represents a row in the &quot;LayoutSet&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetModel
 * @generated
 */
@ImplementationClassName("com.liferay.portal.model.impl.LayoutSetImpl")
@ProviderType
public interface LayoutSet extends LayoutSetModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.LayoutSetImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<LayoutSet, Long> LAYOUT_SET_ID_ACCESSOR =
		new Accessor<LayoutSet, Long>() {

			@Override
			public Long get(LayoutSet layoutSet) {
				return layoutSet.getLayoutSetId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<LayoutSet> getTypeClass() {
				return LayoutSet.class;
			}

		};

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
	public ColorScheme getColorScheme();

	public String getCompanyFallbackVirtualHostname();

	public String getFaviconURL();

	/**
	 * Returns the layout set's group.
	 *
	 * @return the layout set's group
	 */
	public Group getGroup()
		throws com.liferay.portal.kernel.exception.PortalException;

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
	public long getLayoutSetPrototypeId()
		throws com.liferay.portal.kernel.exception.PortalException;

	public long getLiveLogoId();

	public boolean getLogo();

	public java.util.List<Layout> getMergeFailFriendlyURLLayouts();

	public int getPageCount();

	public com.liferay.portal.kernel.util.UnicodeProperties
		getSettingsProperties();

	public String getSettingsProperty(String key);

	public Theme getTheme();

	public String getThemeSetting(String key, String device);

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
	public java.util.NavigableMap<String, String> getVirtualHostnames();

	public boolean hasSetModifiedDate();

	public boolean isLayoutSetPrototypeLinkActive();

	public boolean isLayoutSetPrototypeUpdateable();

	public boolean isLayoutSetReadyForPropagation();

	public boolean isLogo();

	public void setCompanyFallbackVirtualHostname(
		String companyFallbackVirtualHostname);

	public void setSettingsProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			settingsUnicodeProperties);

	/**
	 * Sets the names of the layout set's virtual host name and language IDs.
	 *
	 * @param virtualHostnames the map of the layout set's virtual host name and
	 language IDs
	 * @see #getVirtualHostnames()
	 */
	public void setVirtualHostnames(
		java.util.NavigableMap<String, String> virtualHostnames);

}