/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util.comparator;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import java.io.Serializable;

import java.text.Collator;

import java.util.Comparator;
import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletTitleComparator
	implements Comparator<Portlet>, Serializable {

	public PortletTitleComparator(Locale locale) {
		this(null, locale);
	}

	public PortletTitleComparator(
		ServletContext servletContext, Locale locale) {

		this(servletContext, locale, true);
	}

	public PortletTitleComparator(
		ServletContext servletContext, Locale locale, boolean ascending) {

		_servletContext = servletContext;
		_locale = locale;
		_ascending = ascending;

		_collator = CollatorUtil.getInstance(locale);
	}

	@Override
	public int compare(Portlet portlet1, Portlet portlet2) {
		String portletTitle1 = StringPool.BLANK;
		String portletTitle2 = StringPool.BLANK;

		if (_servletContext != null) {
			portletTitle1 = PortalUtil.getPortletTitle(
				portlet1, _servletContext, _locale);
			portletTitle2 = PortalUtil.getPortletTitle(
				portlet2, _servletContext, _locale);
		}
		else {
			portletTitle1 = PortalUtil.getPortletTitle(portlet1, _locale);
			portletTitle2 = PortalUtil.getPortletTitle(portlet2, _locale);
		}

		if (Validator.isNull(portletTitle1) &&
			Validator.isNull(portletTitle2)) {

			return 0;
		}

		if (Validator.isNull(portletTitle1)) {
			return 1;
		}

		if (Validator.isNull(portletTitle2)) {
			return -1;
		}

		int value = _collator.compare(portletTitle1, portletTitle2);

		if (_ascending) {
			return value;
		}

		return -value;
	}

	private final boolean _ascending;
	private final Collator _collator;
	private final Locale _locale;
	private final ServletContext _servletContext;

}