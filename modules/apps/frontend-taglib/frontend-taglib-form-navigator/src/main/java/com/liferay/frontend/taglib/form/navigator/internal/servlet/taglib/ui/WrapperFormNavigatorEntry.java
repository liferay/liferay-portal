/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.form.navigator.internal.servlet.taglib.ui;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.portal.kernel.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class WrapperFormNavigatorEntry<T> implements FormNavigatorEntry<T> {

	public WrapperFormNavigatorEntry(
		com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorEntry<T>
			formNavigatorEntry) {

		_formNavigatorEntry = formNavigatorEntry;
	}

	@Override
	public String getCategoryKey() {
		return _formNavigatorEntry.getCategoryKey();
	}

	@Override
	public String getFormNavigatorId() {
		return _formNavigatorEntry.getFormNavigatorId();
	}

	@Override
	public String getKey() {
		return _formNavigatorEntry.getKey();
	}

	@Override
	public String getLabel(Locale locale) {
		return _formNavigatorEntry.getLabel(locale);
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		_formNavigatorEntry.include(httpServletRequest, httpServletResponse);
	}

	@Override
	public boolean isVisible(User user, T formModelBean) {
		return _formNavigatorEntry.isVisible(user, formModelBean);
	}

	private final com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorEntry
		<T> _formNavigatorEntry;

}