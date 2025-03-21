/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the LayoutRevision service. Represents a row in the &quot;LayoutRevision&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutRevisionModel
 * @generated
 */
@ImplementationClassName("com.liferay.portal.model.impl.LayoutRevisionImpl")
@ProviderType
public interface LayoutRevision extends LayoutRevisionModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.LayoutRevisionImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<LayoutRevision, Long>
		LAYOUT_REVISION_ID_ACCESSOR = new Accessor<LayoutRevision, Long>() {

			@Override
			public Long get(LayoutRevision layoutRevision) {
				return layoutRevision.getLayoutRevisionId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<LayoutRevision> getTypeClass() {
				return LayoutRevision.class;
			}

		};

	public String getBreadcrumb(java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<LayoutRevision> getChildren();

	public ColorScheme getColorScheme()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getCssText()
		throws com.liferay.portal.kernel.exception.PortalException;

	public Group getGroup();

	public String getHTMLTitle(java.util.Locale locale);

	public String getHTMLTitle(String localeLanguageId);

	public boolean getIconImage();

	public LayoutBranch getLayoutBranch()
		throws com.liferay.portal.kernel.exception.PortalException;

	public LayoutSet getLayoutSet()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getRegularURL(
			jakarta.servlet.http.HttpServletRequest httpServletRequest)
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getTarget();

	public Theme getTheme()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getThemeSetting(String key, String device);

	public com.liferay.portal.kernel.util.UnicodeProperties
		getTypeSettingsProperties();

	public String getTypeSettingsProperty(String key);

	public String getTypeSettingsProperty(String key, String defaultValue);

	public boolean hasChildren();

	public boolean isContentDisplayPage();

	public boolean isCustomizable()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean isIconImage();

	public boolean isInheritLookAndFeel();

	public void setTypeSettingsProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			typeSettingsUnicodeProperties);

}