/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.configuration.servlet;

import com.liferay.portal.kernel.service.configuration.ServiceComponentConfiguration;

import jakarta.servlet.ServletContext;

import java.io.InputStream;

/**
 * @author Miguel Pastor
 */
public class ServletServiceContextComponentConfiguration
	implements ServiceComponentConfiguration {

	public ServletServiceContextComponentConfiguration(
		ServletContext servletContext) {

		_servletContext = servletContext;
	}

	@Override
	public InputStream getHibernateInputStream() {
		return _servletContext.getResourceAsStream(
			"/WEB-INF/classes/META-INF/portlet-hbm.xml");
	}

	@Override
	public InputStream getModelHintsExtInputStream() {
		return _servletContext.getResourceAsStream(
			"/WEB-INF/classes/META-INF/portlet-model-hints-ext.xml");
	}

	@Override
	public InputStream getModelHintsInputStream() {
		return _servletContext.getResourceAsStream(
			"/WEB-INF/classes/META-INF/portlet-model-hints.xml");
	}

	@Override
	public String getServletContextName() {
		return _servletContext.getServletContextName();
	}

	@Override
	public InputStream getSQLIndexesInputStream() {
		return _servletContext.getResourceAsStream("/WEB-INF/sql/indexes.sql");
	}

	@Override
	public InputStream getSQLSequencesInputStream() {
		return _servletContext.getResourceAsStream(
			"/WEB-INF/sql/sequences.sql");
	}

	@Override
	public InputStream getSQLTablesInputStream() {
		return _servletContext.getResourceAsStream("/WEB-INF/sql/tables.sql");
	}

	private final ServletContext _servletContext;

}