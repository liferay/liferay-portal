/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.taglib.servlet.taglib;

import com.liferay.frontend.editor.EditorRenderer;
import com.liferay.frontend.editor.taglib.internal.EditorRendererUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;

/**
 * @author Iván Zaera Avellón
 */
public class ResourcesTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		EditorRenderer editorRenderer = _getEditorProvider();

		setAttributeNamespace(
			editorRenderer.getAttributeNamespace() + StringPool.COLON);

		return super.doStartTag();
	}

	public String getEditorName() {
		return _editorName;
	}

	public String getInlineEditSaveURL() {
		return _inlineEditSaveURL;
	}

	public boolean isInlineEdit() {
		return _inlineEdit;
	}

	public void setEditorName(String editorName) {
		_editorName = editorName;
	}

	public void setInlineEdit(boolean inlineEdit) {
		_inlineEdit = inlineEdit;
	}

	public void setInlineEditSaveURL(String inlineEditSaveURL) {
		_inlineEditSaveURL = inlineEditSaveURL;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_editorName = null;
		_inlineEdit = false;
		_inlineEditSaveURL = null;
	}

	@Override
	protected String getPage() {
		EditorRenderer editorRenderer = _getEditorProvider();

		return editorRenderer.getResourcesJspPath();
	}

	@Override
	protected void includePage(
			String page, HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		setServletContext(
			PortalWebResourcesUtil.getServletContext(_getEditorResourceType()));

		super.includePage(page, httpServletResponse);
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		setNamespacedAttribute(httpServletRequest, "editorName", _editorName);
		setNamespacedAttribute(httpServletRequest, "inlineEdit", _inlineEdit);
		setNamespacedAttribute(
			httpServletRequest, "inlineEditSaveURL", _inlineEditSaveURL);
	}

	private EditorRenderer _getEditorProvider() {
		EditorRenderer editorRenderer = EditorRendererUtil.getEditorRenderer(
			_editorName);

		if (editorRenderer == null) {
			throw new IllegalStateException(
				"No editor renderer found for " + _editorName);
		}

		return editorRenderer;
	}

	private String _getEditorResourceType() {
		EditorRenderer editorRenderer = _getEditorProvider();

		return editorRenderer.getResourceType();
	}

	private String _editorName;
	private boolean _inlineEdit;
	private String _inlineEditSaveURL;

}