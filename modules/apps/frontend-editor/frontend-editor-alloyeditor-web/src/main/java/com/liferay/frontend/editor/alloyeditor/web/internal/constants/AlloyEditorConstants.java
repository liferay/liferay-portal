/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.alloyeditor.web.internal.constants;

/**
 * @author Iván Zaera Avellón
 */
public class AlloyEditorConstants {

	/**
	 * The namespace prefix added to the taglib attributes when they are put in
	 * the {@link jakarta.servlet.http.HttpServletRequest}.
	 *
	 * <p>
	 * Do not change this value unless you stop implementing the legacy {@link
	 * com.liferay.portal.kernel.editor.Editor} interface and leave the {@link
	 * com.liferay.frontend.editor.EditorRenderer} alone; otherwise, the former
	 * will fail because it hard codes the use of this namespace.
	 * </p>
	 */
	public static final String ATTRIBUTE_NAMESPACE = "liferay-ui:input-editor";

}