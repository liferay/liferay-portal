/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.editor.configuration;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Map;

/**
 * Provides an interface for setting the editor's configuration. Editor options
 * can be set using {@link EditorOptionsContributor}.
 *
 * <p>
 * Implementations of this interface are typically specific to a particular
 * editor, since the configuration JSON object updated here can differ from
 * those of other editors.
 * </p>
 *
 * <p>
 * Implementations of this class must be OSGi components that are registered in
 * the OSGi Registry.
 * </p>
 *
 * <p>
 * The configuration can be targeted to specific editors, based on three
 * criteria: portlet name, editor config key, and editor name. These criteria
 * can be defined as OSGi properties with the following names:
 * </p>
 *
 * <ul>
 * <li>
 * <code>jakarta.portlet.name</code>: The portlet name. If specified, the
 * configuration populated in the JSON object is applied to every editor used
 * in that portlet.
 * </li>
 * <li>
 * <code>editor.config.key</code>: The key used to identify the editor (the
 * <code>input-editor</code> taglib tag's <code>configKey</code> attribute
 * value). If specified, the configuration populated in the JSON object is
 * applied to every editor with the specified <code>configKey</code>.
 * </li>
 * <li>
 * <code>editor.name</code>: The name of the editor (the
 * <code>input-editor</code> taglib tag's <code>editorName</code> attribute
 * value: <code>ckeditor</code>, <code>ckeditor_bbcode</code>,
 * <code>alloyeditor</code>, etc.). If specified, the configuration populated in
 * the JSON object is applied to every editor with that name.
 * </li>
 * </ul>
 *
 * <p>
 * In case there's more than one configuration, they're prioritized by the
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
 * If there are multiple configurations with the same criteria elements, they're
 * prioritized by service rank.
 * </p>
 *
 * @author Sergio González
 */
public interface EditorConfigContributor {

	/**
	 * Updates the original configuration JSON object with some new
	 * configuration. It can even update or delete the original configuration,
	 * or any other configuration introduced by any other {@link
	 * EditorConfigContributor}.
	 *
	 * <p>
	 * The configuration object contains the configuration to be directly used
	 * by the editor. The configuration JSON object might, therefore, differ
	 * from other editors.
	 * </p>
	 *
	 * @param jsonObject the original JSON object composed of the entire
	 *        configuration set by {@link EditorConfigContributor} modules
	 * @param inputEditorTaglibAttributes the attributes specified to the input
	 *        taglib tag that renders the editor
	 * @param themeDisplay the theme display
	 */
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory);

}