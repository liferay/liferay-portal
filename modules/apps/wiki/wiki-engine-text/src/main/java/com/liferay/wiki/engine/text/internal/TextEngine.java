/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.engine.text.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.wiki.engine.BaseWikiEngine;
import com.liferay.wiki.engine.WikiEngine;
import com.liferay.wiki.model.WikiPage;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = WikiEngine.class)
public class TextEngine extends BaseWikiEngine {

	@Override
	public String convert(
		WikiPage page, PortletURL viewPageURL, PortletURL editPageURL,
		String attachmentURLPrefix) {

		if (page.getContent() == null) {
			return StringPool.BLANK;
		}

		return "<pre>" + page.getContent() + "</pre>";
	}

	@Override
	public String getEditorName() {
		return null;
	}

	@Override
	public String getFormat() {
		return "plain_text";
	}

	@Override
	public String getHelpURL() {
		return null;
	}

	@Override
	public Map<String, Boolean> getOutgoingLinks(WikiPage page) {
		return Collections.emptyMap();
	}

	@Override
	protected ServletContext getEditPageServletContext() {
		return _servletContext;
	}

	@Override
	protected ServletContext getHelpPageServletContext() {
		return null;
	}

	@Reference(target = "(osgi.web.symbolicname=com.liferay.wiki.engine.text)")
	private ServletContext _servletContext;

}