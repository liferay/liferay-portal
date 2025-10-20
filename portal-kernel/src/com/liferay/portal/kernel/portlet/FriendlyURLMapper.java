/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;

import java.util.Map;

/**
 * Provides friendly URL mapping for a portlet by converting portlet URL
 * parameters to friendly URL paths and back.
 *
 * <p>
 * Never implement this interface directly, subclass {@link
 * BaseFriendlyURLMapper} to ensure forward compatibility.
 * </p>
 *
 * @author Brian Myunghun Kim
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @see    BaseFriendlyURLMapper
 * @see    DefaultFriendlyURLMapper
 * @see    com.liferay.portlet.PortletURLImpl
 */
public interface FriendlyURLMapper {

	/**
	 * Generates a friendly URL path from the portlet URL object.
	 *
	 * @param  liferayPortletURL the portlet URL object to generate a friendly
	 *         URL for
	 * @return the generated friendly URL, or <code>null</code> if one cannot be
	 *         built. Returning <code>null</code> will cause a normal portlet
	 *         URL to be generated.
	 */
	public String buildPath(LiferayPortletURL liferayPortletURL);

	/**
	 * Returns the friendly URL mapping for this portlet.
	 *
	 * <p>
	 * The friendly URL mapping is used by Liferay to identify the portlet a
	 * friendly URL refers to. It generally appears directly after the
	 * <code>/-/</code> in the URL.
	 * </p>
	 *
	 * <p>
	 * For instance, the blogs portlet mapping is &quot;blogs&quot;. This
	 * produces friendly URLs similar to
	 * <code>http://www.liferay.com/web/guest/blog/-/blogs/example-post</code>
	 * </p>
	 *
	 * @return the friendly URL mapping for this portlet, not including any
	 *         leading or trailing slashes
	 */
	public String getMapping();

	/**
	 * Returns the ID of this portlet
	 *
	 * @return the ID of this portlet, not including the instance ID
	 */
	public String getPortletId();

	/**
	 * Returns the router for this friendly URL mapper
	 *
	 * @return the router, or <code>null</code> if one has not been set
	 */
	public Router getRouter();

	/**
	 * Returns <code>true</code> if the friendly URLs for this mapper should
	 * include the friendly URL separator.
	 *
	 * <p>
	 * Typically, friendly URLs will include the separator &quot;/-/&quot;
	 * before the friendly URL mapping. If this method returns
	 * <code>false</code>, a single slash will be used instead.
	 * </p>
	 *
	 * <p>
	 * This method is called by {@link com.liferay.portal.util.PortalImpl} when
	 * a friendly URL is processed.
	 * </p>
	 *
	 * <p>
	 * <b>It is strongly recommended that this method always return
	 * <code>true</code></b>.
	 * </p>
	 *
	 * @return <code>true</code> if the &quot;/-/&quot; separator should be
	 *         included in friendly URLs, or <code>false</code> if only a
	 *         &quot;/&quot; should be used
	 */
	public boolean isCheckMappingWithPrefix();

	public void init(Portlet portlet);

	/**
	 * Populates the parameter map with values parsed from the friendly URL
	 * path.
	 *
	 * <p>
	 * This method is called by {@link com.liferay.portal.util.PortalImpl} when
	 * a friendly URL is processed.
	 * </p>
	 *
	 * @param friendlyURLPath the friendly URL path, including a leading slash
	 *        and the friendly URL mapping. For example:
	 *        <code>/blogs/example-post</code>
	 * @param parameterMap the parameter map. Entries added to this map must be
	 *        namespaced.
	 * @param requestContext the request context
	 * @see   BaseFriendlyURLMapper#addParameter(Map, String, String)
	 * @see   BaseFriendlyURLMapper#addParameter(String, Map, String, String)
	 */
	public void populateParams(
		String friendlyURLPath, Map<String, String[]> parameterMap,
		Map<String, Object> requestContext);

	/**
	 * Returns <code>true</code> if this portlet is instanceable.
	 *
	 * <p>
	 * The value returned from this method has no effect on whether a portlet is
	 * instanceable, it is a helper method used to determine if the instance ID
	 * should be included in the URL.
	 * </p>
	 *
	 * @return <code>true</code> if the portlet is instanceable;
	 *         <code>false</code> otherwise
	 */
	public boolean isPortletInstanceable();

	public void setFriendlyURLRoutes(String friendlyURLRoutes);

}