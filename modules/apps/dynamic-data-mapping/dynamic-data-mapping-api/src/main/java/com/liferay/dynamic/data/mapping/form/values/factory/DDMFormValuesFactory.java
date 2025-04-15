/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.values.factory;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marcellus Tavares
 */
public interface DDMFormValuesFactory {

	public DDMFormValues create(
		HttpServletRequest httpServletRequest, DDMForm ddmForm);

	public DDMFormValues create(PortletRequest portletRequest, DDMForm ddmForm);

}