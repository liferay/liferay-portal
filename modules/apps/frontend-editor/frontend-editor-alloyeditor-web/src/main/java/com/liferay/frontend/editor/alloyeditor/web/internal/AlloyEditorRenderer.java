/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.alloyeditor.web.internal;

import com.liferay.frontend.editor.EditorRenderer;
import com.liferay.frontend.editor.alloyeditor.web.internal.constants.AlloyEditorConstants;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author João Victor Alves
 */
@Component(
	property = {
		"name=" + AlloyEditorBBCodeEditor.EDITOR_NAME,
		"name=" + AlloyEditorCreoleEditor.EDITOR_NAME,
		"name=" + AlloyEditorEditor.EDITOR_NAME
	},
	service = EditorRenderer.class
)
public class AlloyEditorRenderer implements EditorRenderer {

	@Override
	public String getAttributeNamespace() {
		return AlloyEditorConstants.ATTRIBUTE_NAMESPACE;
	}

	@Override
	public String[] getJavaScriptModules() {
		return new String[] {"liferay-alloy-editor"};
	}

	@Override
	public String getJspPath() {
		return "/alloyeditor.jsp";
	}

	@Override
	public String getResourceType() {
		return PortalWebResourceConstants.RESOURCE_TYPE_EDITOR_ALLOYEDITOR;
	}

	@Override
	public String getResourcesJspPath() {
		return "/resources.jsp";
	}

}