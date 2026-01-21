/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.iframe.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Juergen Kappler
 */
@ExtendedObjectClassDefinition(
	category = "display-content",
	scope = ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE
)
@Meta.OCD(
	id = "com.liferay.iframe.web.internal.configuration.IFramePortletInstanceConfiguration",
	localization = "content/Language",
	name = "iframe-portlet-instance-configuration-name"
)
public interface IFramePortletInstanceConfiguration {

	@Meta.AD(name = "alt", required = false)
	public String alt();

	@Meta.AD(deflt = "false", name = "authenticate", required = false)
	public boolean auth();

	@Meta.AD(deflt = "basic", name = "authentication-type", required = false)
	public String authType();

	@Meta.AD(name = "password", required = false)
	public String basicPassword();

	@Meta.AD(name = "user-name", required = false)
	public String basicUserName();

	@Meta.AD(deflt = "0", name = "border", required = false)
	public String border();

	@Meta.AD(deflt = "#000000", name = "bordercolor", required = false)
	public String bordercolor();

	@Meta.AD(deflt = "true", name = "dynamic-url", required = false)
	public boolean dynamicUrlEnabled();

	@Meta.AD(deflt = "post", name = "form-method", required = false)
	public String formMethod();

	@Meta.AD(name = "password", required = false)
	public String formPassword();

	@Meta.AD(name = "user-name", required = false)
	public String formUserName();

	@Meta.AD(deflt = "0", name = "frameborder", required = false)
	public String frameborder();

	@Meta.AD(deflt = "600", name = "height-maximized", required = false)
	public String heightMaximized();

	@Meta.AD(deflt = "600", name = "height-normal", required = false)
	public String heightNormal();

	@Meta.AD(
		deflt = "var1=hello|var2=world", name = "hidden-variables",
		required = false
	)
	public String[] hiddenVariables();

	@Meta.AD(deflt = "0", name = "hspace", required = false)
	public String hspace();

	@Meta.AD(name = "longdesc", required = false)
	public String longdesc();

	@Meta.AD(name = "password-field", required = false)
	public String passwordField();

	@Meta.AD(name = "relative-to-context-path", required = false)
	public boolean relative();

	@Meta.AD(deflt = "true", name = "resize-automatically", required = false)
	public boolean resizeAutomatically();

	@Meta.AD(deflt = "auto", name = "scrolling", required = false)
	public String scrolling();

	@Meta.AD(name = "source-url", required = false)
	public String src();

	@Meta.AD(name = "title", required = false)
	public String title();

	@Meta.AD(name = "user-name-field", required = false)
	public String userNameField();

	@Meta.AD(deflt = "0", name = "vspace", required = false)
	public String vspace();

	@Meta.AD(deflt = "100%", name = "width", required = false)
	public String width();

}