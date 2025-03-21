/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.editor.configuration;

import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Map;

/**
 * Provides an interface for setting the editor's options. Editor configuration
 * can be set using {@link EditorConfigContributor}.
 *
 * <p>
 * This interface facilitates configuring options at a higher level than using
 * {@link EditorConfigContributor}. The former requires knowledge about the
 * editor internals, while the latter is more versatile in terms of
 * customization.
 * </p>
 *
 * <p>
 * The options specified through this interface are not tied to any particular
 * editor implementation. A {@link EditorConfigTransformer} transforms these
 * high level options to a low level configuration object.
 * </p>
 *
 * <p>
 * Implementations must be OSGi components that are registered in the OSGi
 * Registry.
 * </p>
 *
 * <p>
 * The options can be targeted for specific editors, based on three criteria:
 * portlet name, editor config key, and editor name. These criteria can be
 * defined as OSGi properties with the following names:
 * </p>
 *
 * <ul>
 * <li>
 * <code>jakarta.portlet.name</code>: The portlet name. If specified, the
 * options are applied to every editor used in that portlet.
 * </li>
 * <li>
 * <code>editor.config.key</code>: The key used to identify the editor (the
 * <code>input-editor</code> taglib tag's <code>configKey</code> attribute
 * value). If specified, the options are applied to every editor with the
 * specified <code>configKey</code>.
 * </li>
 * <li>
 * <code>editor.name</code>: The name of the editor (the
 * <code>input-editor</code> taglib tag's <code>editorName</code> attribute
 * value: <code>ckeditor</code>, <code>ckeditor_bbcode</code>,
 * <code>alloyeditor</code>, etc.). If specified, the options are applied to
 * every editor with that name.
 * </li>
 * </ul>
 *
 * <p>
 * In case there's more than one options contributor, they're prioritized by the
 * following criteria combinations (the lower the criteria's number, the higher
 * it's prioritized):
 * </p>
 *
 * <ol>
 * <li>
 * portlet name, editor config key, editor name
 * </li>
 * <li>
 * portlet name, editor config key
 * </li>
 * <li>
 * editor config key, editor name
 * </li>
 * <li>
 * portlet name, editor name
 * </li>
 * <li>
 * editor config key
 * </li>
 * <li>
 * portlet name
 * </li>
 * <li>
 * editor name
 * </li>
 * <li>
 * empty
 * </li>
 * </ol>
 *
 * <p>
 * If there are multiple contributors with the same criteria elements, they're
 * prioritized by service rank.
 * </p>
 *
 * @author Sergio González
 */
public interface EditorOptionsContributor {

	/**
	 * Updates the original {@link EditorOptions} object with new options. It
	 * can even update or delete the original options, or any other options
	 * introduced by any other {@link EditorOptionsContributor}.
	 *
	 * <p>
	 * The editor options object contains the options to be transformed by the
	 * Editor Config Transformer to a configuration JSON object.
	 * </p>
	 *
	 * @param editorOptions the original {@link EditorOptions} object containing
	 *        the options set by {@link EditorOptionsContributor} modules
	 * @param inputEditorTaglibAttributes the attributes specified to the input
	 *        taglib tag that renders the editor
	 * @param themeDisplay the theme display
	 */
	public void populateEditorOptions(
		EditorOptions editorOptions,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory);

}