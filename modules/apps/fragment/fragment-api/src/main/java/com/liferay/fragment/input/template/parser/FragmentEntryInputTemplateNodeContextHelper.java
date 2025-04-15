/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.input.template.parser;

import aQute.bnd.annotation.ProviderType;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.info.form.InfoForm;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

/**
 * @author Víctor Galán
 */
@ProviderType
public interface FragmentEntryInputTemplateNodeContextHelper {

	public InputTemplateNode toInputTemplateNode(
		Map<String, Serializable> attributes, String defaultInputLabel,
		FragmentEntryLink fragmentEntryLink,
		HttpServletRequest httpServletRequest, InfoForm infoForm,
		Locale locale);

}