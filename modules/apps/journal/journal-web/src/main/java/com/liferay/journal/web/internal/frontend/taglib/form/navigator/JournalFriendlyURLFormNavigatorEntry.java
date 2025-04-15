/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.frontend.taglib.form.navigator;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.User;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "form.navigator.entry.order:Integer=50",
	service = FormNavigatorEntry.class
)
public class JournalFriendlyURLFormNavigatorEntry
	extends BaseJournalFormNavigatorEntry {

	@Override
	public String getKey() {
		return "friendly-url";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isVisible(User user, JournalArticle article) {
		return false;
	}

	@Override
	protected String getJspPath() {
		return "/article/friendly_url.jsp";
	}

	@Reference(target = "(osgi.web.symbolicname=com.liferay.journal.web)")
	private ServletContext _servletContext;

}