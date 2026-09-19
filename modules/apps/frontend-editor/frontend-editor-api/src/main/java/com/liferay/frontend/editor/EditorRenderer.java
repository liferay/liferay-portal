/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor;

/**
 * @author Iván Zaera Avellón
 */
public interface EditorRenderer {

	public String getAttributeNamespace();

	public String[] getJavaScriptModules();

	public String getJspPath();

	public String getResourceType();

	public String getResourcesJspPath();

}