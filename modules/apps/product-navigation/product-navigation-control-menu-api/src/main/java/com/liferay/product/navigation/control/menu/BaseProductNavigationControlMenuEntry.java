/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.SessionClicks;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author Julio Camarero
 */
public abstract class BaseProductNavigationControlMenuEntry
	implements ProductNavigationControlMenuEntry {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductNavigationControlMenuEntry)) {
			return false;
		}

		ProductNavigationControlMenuEntry productNavigationControlMenuEntry =
			(ProductNavigationControlMenuEntry)object;

		return Objects.equals(
			getKey(), productNavigationControlMenuEntry.getKey());
	}

	@Override
	public Map<String, Object> getData(HttpServletRequest httpServletRequest) {
		return Collections.emptyMap();
	}

	@Override
	public String getIcon(HttpServletRequest httpServletRequest) {
		return StringPool.BLANK;
	}

	@Override
	public String getIconCssClass(HttpServletRequest httpServletRequest) {
		return StringPool.BLANK;
	}

	@Override
	public String getKey() {
		Class<?> clazz = getClass();

		return clazz.getName();
	}

	@Override
	public String getLinkCssClass(HttpServletRequest httpServletRequest) {
		return StringPool.BLANK;
	}

	@Override
	public String getMarkupView(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, getKey());
	}

	@Override
	public boolean includeBody(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		return false;
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		return false;
	}

	@Override
	public boolean isPanelStateOpen(
		HttpServletRequest httpServletRequest, String key) {

		String panelState = SessionClicks.get(
			httpServletRequest, key, "closed");

		return Objects.equals(panelState, "open");
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		return true;
	}

	@Override
	public boolean isUseDialog() {
		return false;
	}

	@Override
	public void setPanelState(
		HttpServletRequest httpServletRequest, String key, String panelState) {

		SessionClicks.put(httpServletRequest, key, panelState);
	}

}