/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.util;

import jakarta.portlet.MimeResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.framework.Bundle;

/**
 * @author Ryan Park
 */
public interface AppDisplay extends Comparable<AppDisplay> {

	public static final String APP_TITLE_UNCATEGORIZED = "Independent Modules";

	public void addBundle(Bundle bundle);

	public List<Bundle> getBundles();

	public String getDescription();

	public String getDisplaySuiteTitle();

	public String getDisplayTitle();

	public String getDisplayURL(MimeResponse mimeResponse);

	public String getIconURL(HttpServletRequest httpServletRequest);

	public int getState();

	public String getStoreURL(HttpServletRequest httpServletRequest);

	public String getTitle();

	public String getVersion();

	public boolean isRequired();

}