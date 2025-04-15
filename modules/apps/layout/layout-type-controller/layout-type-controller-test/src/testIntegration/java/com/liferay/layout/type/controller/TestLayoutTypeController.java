/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Phil Jones
 */
@Component(
	property = {
		"layout.type=testLayoutTypeController",
		"service.ranking:Integer=" + Integer.MAX_VALUE
	},
	service = LayoutTypeController.class
)
public class TestLayoutTypeController implements LayoutTypeController {

	@Override
	public String[] getConfigurationActionDelete() {
		return null;
	}

	@Override
	public String[] getConfigurationActionUpdate() {
		return null;
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	public String getURL() {
		return null;
	}

	@Override
	public String includeEditContent(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Layout layout) {

		return null;
	}

	@Override
	public boolean includeLayoutContent(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Layout layout) {

		return false;
	}

	@Override
	public boolean isBrowsable() {
		return true;
	}

	@Override
	public boolean isCheckLayoutViewPermission() {
		return true;
	}

	@Override
	public boolean isFirstPageable() {
		return false;
	}

	@Override
	public boolean isFullPageDisplayable() {
		return false;
	}

	@Override
	public boolean isInstanceable() {
		return true;
	}

	@Override
	public boolean isParentable() {
		return false;
	}

	@Override
	public boolean isSitemapable() {
		return false;
	}

	@Override
	public boolean isURLFriendliable() {
		return false;
	}

	@Override
	public boolean matches(
		HttpServletRequest httpServletRequest, String friendlyURL,
		Layout layout) {

		return false;
	}

}