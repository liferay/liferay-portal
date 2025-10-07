/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the Layout service. Represents a row in the &quot;Layout&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutModel
 * @generated
 */
@ImplementationClassName("com.liferay.portal.model.impl.LayoutImpl")
@ProviderType
public interface Layout extends LayoutModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.LayoutImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<Layout, Long> PLID_ACCESSOR =
		new Accessor<Layout, Long>() {

			@Override
			public Long get(Layout layout) {
				return layout.getPlid();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<Layout> getTypeClass() {
				return Layout.class;
			}

		};
	public static final Accessor<Layout, Long> LAYOUT_ID_ACCESSOR =
		new Accessor<Layout, Long>() {

			@Override
			public Long get(Layout layout) {
				return layout.getLayoutId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<Layout> getTypeClass() {
				return Layout.class;
			}

		};

	public Layout fetchDraftLayout();

	/**
	 * Returns all layouts that are direct or indirect children of the current
	 * layout.
	 *
	 * @return the layouts that are direct or indirect children of the current
	 layout
	 */
	public java.util.List<Layout> getAllChildren();

	/**
	 * Returns the ID of the topmost parent layout (e.g. n-th parent layout) of
	 * the current layout.
	 *
	 * @return the ID of the topmost parent layout of the current layout
	 */
	public long getAncestorLayoutId()
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns the plid of the topmost parent layout (e.g. n-th parent layout)
	 * of the current layout.
	 *
	 * @return the plid of the topmost parent layout of the current layout
	 */
	public long getAncestorPlid()
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns all parent layouts of the current layout. The list is retrieved
	 * recursively with the direct parent layout listed first, and most distant
	 * parent listed last.
	 *
	 * @return the current layout's list of parent layouts
	 */
	public java.util.List<Layout> getAncestors()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getBreadcrumb(java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns all child layouts of the current layout, independent of user
	 * access permissions.
	 *
	 * @return the list of all child layouts
	 */
	public java.util.List<Layout> getChildren();

	/**
	 * Returns all child layouts of the current layout that the user has
	 * permission to access.
	 *
	 * @param permissionChecker the user-specific context to check permissions
	 * @return the list of all child layouts that the user has permission to
	 access
	 */
	public java.util.List<Layout> getChildren(
			com.liferay.portal.kernel.security.permission.PermissionChecker
				permissionChecker)
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns the color scheme that is configured for the current layout, or
	 * the color scheme of the layout set that contains the current layout if no
	 * color scheme is configured.
	 *
	 * @return the color scheme that is configured for the current layout, or
	 the color scheme  of the layout set that contains the current
	 layout if no color scheme is configured
	 */
	public ColorScheme getColorScheme()
		throws com.liferay.portal.kernel.exception.PortalException;

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
	public String getCssText()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getDefaultThemeSetting(
		String key, String device, boolean inheritLookAndFeel);

	public java.util.List<Portlet> getEmbeddedPortlets();

	public java.util.List<Portlet> getEmbeddedPortlets(long groupId);

	public String getFaviconURL();

	/**
	 * Returns the layout's friendly URL for the given locale.
	 *
	 * @param locale the locale that the friendly URL should be retrieved for
	 * @return the layout's friendly URL for the given locale
	 */
	public String getFriendlyURL(java.util.Locale locale);

	/**
	 * Returns the friendly URLs for all configured locales.
	 *
	 * @return the friendly URLs for all configured locales
	 */
	public java.util.Map<java.util.Locale, String> getFriendlyURLMap();

	public String getFriendlyURLsXML();

	/**
	 * Returns the current layout's group.
	 *
	 * <p>
	 * Group is Liferay's technical name for a site.
	 * </p>
	 *
	 * @return the current layout's group
	 */
	public Group getGroup();

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
	public String getHTMLTitle(java.util.Locale locale);

	/**
	 * Returns the current layout's HTML title for the given locale language ID,
	 * or the current layout's name if no HTML title is configured.
	 *
	 * @param localeLanguageId the locale that the HTML title should be
	 retrieved for
	 * @return the current layout's HTML title for the given locale language ID,
	 or the current layout's name if no HTML title is configured
	 */
	public String getHTMLTitle(String localeLanguageId);

	public String getIcon();

	/**
	 * Returns <code>true</code> if the current layout has a configured icon.
	 *
	 * @return <code>true</code> if the current layout has a configured icon;
	 <code>false</code> otherwise
	 */
	public boolean getIconImage();

	/**
	 * Returns the current layout's {@link LayoutSet}.
	 *
	 * @return the current layout's layout set
	 */
	public LayoutSet getLayoutSet();

	public Layout getLayoutSetPrototypeLayout();

	/**
	 * Returns the current layout's {@link LayoutType}.
	 *
	 * @return the current layout's layout type
	 */
	public LayoutType getLayoutType();

	/**
	 * Returns the current layout's linked layout.
	 *
	 * @return the current layout's linked layout, or <code>null</code> if no
	 linked layout could be found
	 */
	public Layout getLinkedToLayout();

	public String getRegularURL(
			jakarta.servlet.http.HttpServletRequest httpServletRequest)
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getResetLayoutURL(
			jakarta.servlet.http.HttpServletRequest httpServletRequest)
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getResetMaxStateURL(
			jakarta.servlet.http.HttpServletRequest httpServletRequest)
		throws com.liferay.portal.kernel.exception.PortalException;

	public Group getScopeGroup()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getTarget();

	/**
	 * Returns the current layout's theme, or the layout set's theme if no
	 * layout theme is configured.
	 *
	 * @return the current layout's theme, or the layout set's theme if no
	 layout theme is configured
	 */
	public Theme getTheme()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getThemeSetting(String key, String device);

	public String getThemeSetting(
		String key, String device, boolean inheritLookAndFeel);

	public com.liferay.portal.kernel.util.UnicodeProperties
		getTypeSettingsProperties();

	public String getTypeSettingsProperty(String key);

	public String getTypeSettingsProperty(String key, String defaultValue);

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
	public boolean hasAncestor(long layoutId)
		throws com.liferay.portal.kernel.exception.PortalException;

	/**
	 * Returns <code>true</code> if the current layout has child layouts.
	 *
	 * @return <code>true</code> if the current layout has child layouts,
	 <code>false</code> otherwise
	 */
	public boolean hasChildren();

	public boolean hasScopeGroup()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean hasSetModifiedDate();

	public boolean includeLayoutContent(
			jakarta.servlet.http.HttpServletRequest httpServletRequest,
			jakarta.servlet.http.HttpServletResponse httpServletResponse)
		throws Exception;

	public boolean isChildSelected(boolean selectable, Layout layout)
		throws com.liferay.portal.kernel.exception.PortalException;

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
	public boolean isContentDisplayPage();

	public boolean isCustomizable();

	public boolean isDraftLayout();

	public boolean isEmbeddedPersonalApplication();

	/**
	 * Returns <code>true</code> if the current layout is the first layout in
	 * its parent's hierarchical list of children layouts.
	 *
	 * @return <code>true</code> if the current layout is the first layout in
	 its parent's hierarchical list of children layouts;
	 <code>false</code> otherwise
	 */
	public boolean isFirstChild();

	/**
	 * Returns <code>true</code> if the current layout is the topmost parent
	 * layout.
	 *
	 * @return <code>true</code> if the current layout is the topmost parent
	 layout; <code>false</code> otherwise
	 */
	public boolean isFirstParent();

	public boolean isIconImage();

	/**
	 * Returns <code>true</code> if the current layout utilizes its {@link
	 * LayoutSet}'s look and feel options (e.g. theme and color scheme).
	 *
	 * @return <code>true</code> if the current layout utilizes its layout set's
	 look and feel options; <code>false</code> otherwise
	 */
	public boolean isInheritLookAndFeel();

	public boolean isLayoutDeleteable();

	/**
	 * Returns <code>true</code> if the current layout is built from a layout
	 * template and still maintains an active connection to it.
	 *
	 * @return <code>true</code> if the current layout is built from a layout
	 template and still maintains an active connection to it;
	 <code>false</code> otherwise
	 */
	public boolean isLayoutPrototypeLinkActive();

	public boolean isLayoutSortable();

	public boolean isLayoutUpdateable();

	public boolean isPortletEmbedded(String portletId, long groupId);

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
	public boolean isPublicLayout();

	public boolean isPublished();

	/**
	 * Returns <code>true</code> if the current layout is the root layout.
	 *
	 * @return <code>true</code> if the current layout is the root layout;
	 <code>false</code> otherwise
	 */
	public boolean isRootLayout();

	public boolean isSelected(
		boolean selectable, Layout layout, long ancestorPlid);

	/**
	 * Returns <code>true</code> if the current layout can hold embedded
	 * portlets.
	 *
	 * @return <code>true</code> if the current layout can hold embedded
	 portlets; <code>false</code> otherwise
	 */
	public boolean isSupportsEmbeddedPortlets();

	public boolean isTypeAssetDisplay();

	public boolean isTypeContent();

	public boolean isTypeControlPanel();

	public boolean isTypeEmbedded();

	public boolean isTypeEmpty();

	public boolean isTypeLinkToLayout();

	public boolean isTypePanel();

	public boolean isTypePortlet();

	public boolean isTypeURL();

	public boolean isTypeUtility();

	public boolean isUnlocked(String mode, long userId);

	public boolean matches(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		String friendlyURL);

	public void setLayoutSet(LayoutSet layoutSet);

	public void setTypeSettingsProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			typeSettingsUnicodeProperties);

}