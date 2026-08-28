/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.application;

import java.util.List;

/**
 * @author Alejandro Tardín
 */
public interface HeadlessApplicationProvider {

	public List<Application> getApplications();

	public interface Application {

		public String getBasePath();

		public List<OpenAPIDocument> getOpenAPIDocuments();

		public List<ResourceMethod> getResourceMethods();

	}

	public interface OpenAPIDocument {

		public Application getApplication();

		public Object getContentObject(String serverURL);

		public String getContentString(String serverURL, Type type);

		public String getDescription();

		public String getPath(Type type);

		public String getVersion();

		public enum Type {

			JSON, YAML

		}

	}

	public interface ResourceMethod {

		public String getMethod();

		public String getPath();

		public String[] getProducingMimeTypes();

	}

}