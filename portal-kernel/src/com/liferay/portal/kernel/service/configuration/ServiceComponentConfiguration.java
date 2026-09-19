/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.configuration;

import java.io.InputStream;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Miguel Pastor
 */
@ProviderType
public interface ServiceComponentConfiguration {

	public InputStream getHibernateInputStream();

	public InputStream getModelHintsExtInputStream();

	public InputStream getModelHintsInputStream();

	public InputStream getSQLIndexesInputStream();

	public InputStream getSQLSequencesInputStream();

	public InputStream getSQLTablesInputStream();

	public String getServletContextName();

}