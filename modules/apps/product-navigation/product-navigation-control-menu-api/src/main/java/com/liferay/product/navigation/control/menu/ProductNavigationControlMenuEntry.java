/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.Map;

/**
 * Provides an interface that defines entries to be used by a
 * <code>product-navigation:control-menu</code> tag instance to render a new
 * Control Menu entry. Control Menu entries are included within Control Menu
 * categories defined by {@link ProductNavigationControlMenuCategory}
 * implementations.
 *
 * <p>
 * Implementations must be registered in the OSGi Registry. The order of Control
 * Menu entries inside a category is determined by the
 * <code>product.navigation.control.menu.entry.order</code> property value. The
 * Control Menu category used to display that entry is determined by the
 * <code>product.navigation.control.menu.category.key</code> property value.
 * </p>
 *
 * @author Julio Camarero
 */
public interface ProductNavigationControlMenuEntry {

	/**
	 * Returns the data to be injected as the <code>data</code> attribute of the
	 * <code>liferay-ui:icon</code> tag instance for the Control Menu entry.
	 *
	 * @param  httpServletRequest the request that renders the Control Menu
	 *         entry
	 * @return the <code>data</code> attribute of the
	 *         <code>liferay-ui:icon</code> tag instance for the Control Menu
	 *         entry
	 */
	public Map<String, Object> getData(HttpServletRequest httpServletRequest);

	/**
	 * Returns the icon name to be injected as the <code>icon</code> attribute
	 * of the <code>liferay-ui:icon</code> tag instance for the Control Menu
	 * entry.
	 *
	 * @param  httpServletRequest the request that renders the Control Menu
	 *         entry
	 * @return the <code>icon</code> attribute of the
	 *         <code>liferay-ui:icon</code> tag instance for the Control Menu
	 *         entry
	 */
	public String getIcon(HttpServletRequest httpServletRequest);

	/**
	 * Returns the icon CSS class to be injected as the
	 * <code>iconCssClass</code> attribute of the <code>liferay-ui:icon</code>
	 * tag instance for the Control Menu entry.
	 *
	 * @param  httpServletRequest the request that renders the Control Menu
	 *         entry
	 * @return the <code>iconCssClass</code> attribute of the
	 *         <code>liferay-ui:icon</code> tag instance for the Control Menu
	 *         entry
	 */
	public String getIconCssClass(HttpServletRequest httpServletRequest);

	/**
	 * Returns the Control Menu entry's key. This key must be unique in the
	 * scope of the Control Menu entry selector.
	 *
	 * @return the Control Menu entry's key
	 */
	public String getKey();

	/**
	 * Returns the label that is displayed in the user interface when the
	 * Control Menu entry is included in the tag instance.
	 *
	 * @param  locale the label's retrieved locale
	 * @return the Control Menu entry's label
	 */
	public String getLabel(Locale locale);

	/**
	 * Returns the link CSS class to be injected as the
	 * <code>linkCssClass</code> attribute of the <code>liferay-ui:icon</code>
	 * tag instance for the Control Menu entry.
	 *
	 * @param  httpServletRequest the request that renders the Control Menu
	 *         entry
	 * @return the <code>linkCssClass</code> attribute of the
	 *         <code>liferay-ui:icon</code> tag instance for the Control Menu
	 *         entry
	 */
	public String getLinkCssClass(HttpServletRequest httpServletRequest);

	/**
	 * Returns the markup view string to be injected as the
	 * <code>markupView</code> attribute of the <code>liferay-ui:icon</code> tag
	 * instance for the Control Menu entry.
	 *
	 * @param  httpServletRequest the request that renders the Control Menu
	 *         entry
	 * @return the <code>markupView</code> attribute of the
	 *         <code>liferay-ui:icon</code> tag instance for the Control Menu
	 *         entry
	 */
	public String getMarkupView(HttpServletRequest httpServletRequest);

	/**
	 * Returns the URL to be injected as the <code>url</code> attribute of the
	 * <code>liferay-ui:icon</code> tag instance for the Control Menu entry.
	 *
	 * @param  httpServletRequest the request that renders the Control Menu
	 *         entry
	 * @return the <code>url</code> attribute of the
	 *         <code>liferay-ui:icon</code> tag instance for the Control Menu
	 *         entry
	 */
	public String getURL(HttpServletRequest httpServletRequest);

	/**
	 * Returns <code>true</code> if the Control Menu entry body's HTML should be
	 * rendered.
	 *
	 * @param  httpServletRequest the request that renders the Control Menu
	 *         entry
	 * @param  httpServletResponse the response that renders the Control Menu
	 *         entry
	 * @return <code>true</code> if the Control Menu entry body's HTML should be
	 *         rendered; <code>false</code> otherwise
	 * @throws IOException if an IO exception occurred
	 */
	public boolean includeBody(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException;

	/**
	 * Returns <code>true</code> if the Control Menu entry icon's HTML should be
	 * rendered.
	 *
	 * @param  httpServletRequest the request that renders the Control Menu
	 *         entry
	 * @param  httpServletResponse the response that renders the Control Menu
	 *         entry
	 * @return <code>true</code> if the Control Menu entry icon's HTML should be
	 *         rendered; <code>false</code> otherwise
	 * @throws IOException if an IO exception occurred
	 */
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException;

	public boolean isPanelStateOpen(
		HttpServletRequest httpServletRequest, String key);

	public default boolean isRelevant(HttpServletRequest httpServletRequest) {
		return true;
	}

	/**
	 * Returns <code>true</code> if the Control Menu entry should be displayed
	 * in the request's context.
	 *
	 * @param  httpServletRequest the request that renders the Control Menu
	 *         entry
	 * @return <code>true</code> if the Control Menu entry should be displayed
	 *         in the request's context; <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException;

	/**
	 * Returns <code>true</code> if the Control Menu entry should be opened in a
	 * dialog window.
	 *
	 * @return <code>true</code> if the Control Menu entry should be opened in a
	 *         dialog window; <code>false</code> if it should open in the
	 *         current window
	 */
	public boolean isUseDialog();

	public void setPanelState(
		HttpServletRequest httpServletRequest, String key, String panelState);

}