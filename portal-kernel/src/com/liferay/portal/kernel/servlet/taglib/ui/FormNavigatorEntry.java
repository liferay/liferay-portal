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
 * Provides an interface defining entries that will be used by a specific
 * <code>liferay-ui:form-navigator</code> tag instance to render a new section.
 * Form navigator entries are included within form navigator categories, defined
 * by {@link FormNavigatorCategory} implementations.
 *
 * <p>
 * Implementations must be registered in the OSGi Registry. The order of the
 * form navigator entries inside a category is determined by the service
 * ranking.
 * </p>
 *
 * @author     Sergio González
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry}
 */
@Deprecated
public interface FormNavigatorEntry<T> {

	/**
	 * Returns the category key where the form navigator entry will be included.
	 *
	 * @return the category key where the form navigator entry will be included
	 */
	public String getCategoryKey();

	/**
	 * Returns the form navigator ID where the form navigator entry will be
	 * included. This ID must match the ID attribute of the
	 * <code>liferay-ui:form-navigator</code> tag, where this form navigator
	 * entry is to be included.
	 *
	 * @return the form navigator ID where the form navigator entry will be
	 *         included
	 */
	public String getFormNavigatorId();

	/**
	 * Returns the key for the form navigator entry. This key needs to be unique
	 * in the scope of a category key and form navigator ID.
	 *
	 * @return the key of the form navigator entry
	 */
	public String getKey();

	/**
	 * Returns the label that will be displayed in the user interface when the
	 * form navigator entry is included in the form navigator.
	 *
	 * @param  locale the locale that the label should be retrieved for
	 * @return the label of the form navigator entry
	 */
	public String getLabel(Locale locale);

	/**
	 * Renders the HTML that needs to be displayed when the form navigator entry
	 * is displayed.
	 *
	 * @param  httpServletRequest the request with which the form navigator
	 *         entry is rendered
	 * @param  httpServletResponse the response with which the form navigator
	 *         entry is rendered
	 * @throws IOException if an IO exception occurs
	 */
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException;

	/**
	 * Returns <code>true</code> if the form navigator entry should be
	 * displayed.
	 *
	 * @param  user the user viewing the form navigator entry
	 * @param  formModelBean the bean edited by the form navigator, or
	 *         <code>null</code>
	 * @return <code>true</code> if the form navigator entry should be
	 *         displayed; <code>false</code> otherwise
	 */
	public boolean isVisible(User user, T formModelBean);

}