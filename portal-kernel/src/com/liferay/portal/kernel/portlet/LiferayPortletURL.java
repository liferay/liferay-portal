/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceURL;

import java.io.Serializable;

import java.util.Set;
import java.util.function.BiConsumer;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Represents a URL pointing to a portlet.
 *
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface LiferayPortletURL
	extends PortletURL, ResourceURL, Serializable {

	/**
	 * Adds a parameter that is included in the friendly URL path and does not
	 * need to appear in the query string.
	 *
	 * @param name the name of the parameter
	 */
	public void addParameterIncludedInPath(String name);

	/**
	 * Returns the portlet lifecycle of this URL's target portlet.
	 *
	 * @return the portlet lifecycle of this URL's target portlet
	 * @see    #setLifecycle(String)
	 */
	public String getLifecycle();

	/**
	 * @param      name the name of the URL parameter
	 * @return     the first value of the URL parameter
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             jakarta.portlet.PortletParameters#getValue(String)}
	 */
	@Deprecated
	public String getParameter(String name);

	/**
	 * Returns the parameters that are included in the friendly URL path and do
	 * not need to appear in the query string.
	 *
	 * @return the names of the parameters that are included in the friendly URL
	 *         path and do not need to appear in the query string
	 */
	public Set<String> getParametersIncludedInPath();

	public long getPlid();

	/**
	 * Returns the ID of this URL's target portlet.
	 *
	 * @return the ID of this URL's target portlet
	 */
	public String getPortletId();

	public Set<String> getRemovedParameterNames();

	/**
	 * Returns the ID of this URL's target resource.
	 *
	 * @return the ID of this URL's target resource
	 */
	public String getResourceID();

	/**
	 * Returns <code>true</code> if this URL is an anchor pointing to the
	 * specified portlet on the page.
	 *
	 * @return whether this URL is an anchor pointing to the specified portlet
	 *         on the page
	 * @see    #setAnchor(boolean)
	 */
	public boolean isAnchor();

	/**
	 * Returns <code>true</code> if the render parameters in the current request
	 * should be copied to this URL.
	 *
	 * @return whether the render parameters in the current request should be
	 *         copied to this URL
	 * @see    #setCopyCurrentRenderParameters(boolean)
	 */
	public boolean isCopyCurrentRenderParameters();

	/**
	 * Returns <code>true</code> if this URL should be encrypted.
	 *
	 * @return <code>true</code> if this URL should be encrypted;
	 *         <code>false</code> otherwise
	 * @see    #setEncrypt(boolean)
	 */
	public boolean isEncrypt();

	/**
	 * Returns <code>true</code> if this URL should be XML escaped.
	 *
	 * @return <code>true</code> if this URL should be XML escaped;
	 *         <code>false</code> otherwise
	 * @see    #setEscapeXml(boolean)
	 */
	public boolean isEscapeXml();

	/**
	 * Returns <code>true</code> if the parameter is included in the friendly
	 * URL path.
	 *
	 * @param  name the name of the parameter to check for inclusion in the path
	 * @return whether the parameter is included in the friendly URL path
	 * @see    #addParameterIncludedInPath(String)
	 */
	public boolean isParameterIncludedInPath(String name);

	/**
	 * Returns <code>true</code> if this URL is secure (https).
	 *
	 * @return <code>true</code> if this URL is secure; <code>false</code>
	 *         otherwise
	 */
	public boolean isSecure();

	/**
	 * Sets whether this URL is an anchor pointing to the specified portlet on
	 * the page.
	 *
	 * <p>
	 * An anchor URL will cause the user's browser to automatically jump down to
	 * the specified portlet after the page loads, avoiding the need to scroll.
	 * </p>
	 *
	 * @param anchor whether this URL is an anchor pointing to the specified
	 *        portlet on the page
	 */
	public void setAnchor(boolean anchor);

	/**
	 * Sets whether the render parameters in the current request should be
	 * copied to this URL.
	 *
	 * <p>
	 * New parameters set on this URL will appear before the copied render
	 * parameters.
	 * </p>
	 *
	 * @param copyCurrentRenderParameters whether the render parameters in the
	 *        current request should be copied to this URL
	 */
	public void setCopyCurrentRenderParameters(
		boolean copyCurrentRenderParameters);

	public void setDoAsGroupId(long doAsGroupId);

	/**
	 * Sets the ID of the user to impersonate.
	 *
	 * <p>
	 * When a page is accessed while impersonating a user, it will appear
	 * exactly as it would to that user.
	 * </p>
	 *
	 * @param doAsUserId the ID of the user to impersonate in the portlet this
	 *        URL points to
	 */
	public void setDoAsUserId(long doAsUserId);

	/**
	 * Sets the language ID of the user to impersonate. This will only have an
	 * effect when a user is being impersonated via {@link
	 * #setDoAsUserId(long)}.
	 *
	 * <p>
	 * The language set here will override the impersonated user's default
	 * language.
	 * </p>
	 *
	 * @param doAsUserLanguageId the language ID of the user to impersonate
	 */
	public void setDoAsUserLanguageId(String doAsUserLanguageId);

	/**
	 * Sets whether this URL should be encrypted.
	 *
	 * <p>
	 * In an encrypted URL, the value of every parameter will be encrypted using
	 * the company's key. This allows sensitive information to be placed in the
	 * URL without being vulnerable to snooping.
	 * </p>
	 *
	 * <p>
	 * Note that this is not the same as making a URL {@link #setSecure(boolean)
	 * secure}.
	 * </p>
	 */
	public void setEncrypt(boolean encrypt);

	/**
	 * Sets whether this URL should be XML escaped.
	 *
	 * <p>
	 * If a URL is XML escaped, it will automatically have special characters
	 * escaped when it is converted to a string or written to a {@link
	 * java.io.Writer}.
	 * </p>
	 *
	 * @param escapeXml whether this URL should be XML escaped
	 */
	public void setEscapeXml(boolean escapeXml);

	/**
	 * Sets the portlet lifecycle of this URL's target portlet.
	 *
	 * <p>
	 * Valid lifecycles are:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * {@link jakarta.portlet.PortletRequest#ACTION_PHASE}
	 * </li>
	 * <li>
	 * {@link jakarta.portlet.PortletRequest#RENDER_PHASE}
	 * </li>
	 * <li>
	 * {@link jakarta.portlet.PortletRequest#RESOURCE_PHASE}
	 * </li>
	 * </ul>
	 *
	 * @param lifecycle the portlet lifecycle
	 */
	public void setLifecycle(String lifecycle);

	/**
	 * @param      name the name of the URL parameter
	 * @param      value the value of the URL parameter
	 * @param      append whether the new value should be appended to any
	 *             existing values for the parameter. If <code>append</code> is
	 *             <code>false</code> any existing values will be overwritten
	 *             with the new value.
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             jakarta.portlet.MutablePortletParameters#setValue(String,
	 *             String)}  Sets the URL parameter to the value
	 */
	@Deprecated
	public void setParameter(String name, String value, boolean append);

	/**
	 * @param      name the name of the URL parameter
	 * @param      values the values of the URL parameter
	 * @param      append whether the new values should be appended to any
	 *             existing values for the parameter. If <code>append</code> is
	 *             <code>false</code> any existing values will be overwritten
	 *             with the new values.
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             jakarta.portlet.MutablePortletParameters#setValues(String,
	 *             String...)}  Sets the URL parameter the values
	 */
	@Deprecated
	public void setParameter(String name, String[] values, boolean append);

	/**
	 * Sets the portlet layout ID.
	 *
	 * @param plid the portlet layout ID
	 */
	public void setPlid(long plid);

	/**
	 * Sets the ID of the target portlet.
	 */
	public void setPortletId(String portletId);

	public void setRefererGroupId(long refererGroupId);

	/**
	 * Sets the referer layout ID.
	 *
	 * @param refererPlid the referer layout ID
	 */
	public void setRefererPlid(long refererPlid);

	public void setRemovedParameterNames(Set<String> removedParamNames);

	/**
	 * Sets whether this portlet restores to the current view when toggling
	 * between maximized and normal states.
	 *
	 * @param windowStateRestoreCurrentView whether this portlet restores to the
	 *        current view when toggling between maximized and normal states
	 */
	public void setWindowStateRestoreCurrentView(
		boolean windowStateRestoreCurrentView);

	public void visitReservedParameters(BiConsumer<String, String> biConsumer);

}