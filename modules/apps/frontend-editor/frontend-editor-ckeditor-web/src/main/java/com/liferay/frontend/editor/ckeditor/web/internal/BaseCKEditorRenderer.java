/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor.web.internal;

import com.liferay.frontend.editor.EditorRenderer;
import com.liferay.frontend.editor.ckeditor.web.internal.constants.CKEditorConstants;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;

/**
 * @author João Victor Alves
 */
public abstract class BaseCKEditorRenderer implements EditorRenderer {

	@Override
	public String getAttributeNamespace() {
		return CKEditorConstants.ATTRIBUTE_NAMESPACE;
	}

	@Override
	public String[] getJavaScriptModules() {
		return new String[0];
	}

	@Override
	public String getResourceType() {
		return PortalWebResourceConstants.RESOURCE_TYPE_EDITOR_CKEDITOR;
	}

	@Override
	public String getResourcesJspPath() {
		return null;
	}

}