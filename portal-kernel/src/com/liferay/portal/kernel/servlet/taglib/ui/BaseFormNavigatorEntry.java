/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.taglib.ui;

import com.liferay.portal.kernel.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author     Sergio González
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.form.navigator.BaseFormNavigatorEntry}
 */
@Deprecated
public abstract class BaseFormNavigatorEntry<T>
	implements FormNavigatorEntry<T> {

	@Override
	public abstract String getCategoryKey();

	@Override
	public abstract String getFormNavigatorId();

	@Override
	public abstract String getKey();

	@Override
	public abstract String getLabel(Locale locale);

	@Override
	public abstract void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException;

	@Override
	public boolean isVisible(User user, T formModelBean) {
		return true;
	}

}