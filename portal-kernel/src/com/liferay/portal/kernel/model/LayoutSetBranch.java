/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the LayoutSetBranch service. Represents a row in the &quot;LayoutSetBranch&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetBranchModel
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
@ImplementationClassName("com.liferay.portal.model.impl.LayoutSetBranchImpl")
@ProviderType
public interface LayoutSetBranch extends LayoutSetBranchModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.LayoutSetBranchImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<LayoutSetBranch, Long>
		LAYOUT_SET_BRANCH_ID_ACCESSOR = new Accessor<LayoutSetBranch, Long>() {

			@Override
			public Long get(LayoutSetBranch layoutSetBranch) {
				return layoutSetBranch.getLayoutSetBranchId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<LayoutSetBranch> getTypeClass() {
				return LayoutSetBranch.class;
			}

		};

	public ColorScheme getColorScheme();

	public Group getGroup()
		throws com.liferay.portal.kernel.exception.PortalException;

	public LayoutSet getLayoutSet();

	public long getLiveLogoId();

	public boolean getLogo();

	public com.liferay.portal.kernel.util.UnicodeProperties
		getSettingsProperties();

	public String getSettingsProperty(String key);

	public Theme getTheme();

	public String getThemeSetting(String key, String device);

	public boolean isLayoutSetPrototypeLinkActive();

	public boolean isLogo();

	public void setSettingsProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			settingsUnicodeProperties);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1647337816