/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.frontend.taglib.form.navigator;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.journal.configuration.JournalFileUploadsConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.journal.configuration.JournalFileUploadsConfiguration",
	property = "form.navigator.entry.order:Integer=70",
	service = FormNavigatorEntry.class
)
public class JournalSmallImageFormNavigatorEntry
	extends BaseJournalFormNavigatorEntry {

	@Override
	public String getKey() {
		return "featured-image";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			JournalFileUploadsConfiguration.class.getName(),
			_journalFileUploadsConfiguration);

		super.include(httpServletRequest, httpServletResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_journalFileUploadsConfiguration = ConfigurableUtil.createConfigurable(
			JournalFileUploadsConfiguration.class, properties);
	}

	@Override
	protected String getJspPath() {
		return "/article/small_image.jsp";
	}

	private volatile JournalFileUploadsConfiguration
		_journalFileUploadsConfiguration;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.journal.web)")
	private ServletContext _servletContext;

}