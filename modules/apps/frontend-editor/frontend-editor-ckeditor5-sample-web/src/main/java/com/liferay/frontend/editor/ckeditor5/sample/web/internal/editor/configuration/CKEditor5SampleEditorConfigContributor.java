/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor5.sample.web.internal.editor.configuration;

import com.liferay.frontend.editor.ckeditor5.sample.web.internal.constants.CKEditor5SamplePortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * Asks every editor in the sample portlet but one to load the standard plugins
 * rather than their premium counterparts.
 *
 * <p>
 * Which of the two an editor loads is otherwise decided by the license key,
 * which leaves each of them at the mercy of how the installation happens to be
 * licensed. Pinning the choice keeps these editors loading the same plugins
 * everywhere, so that a test covering a standard plugin never quietly
 * exercises the premium one instead.
 * </p>
 *
 * <p>
 * The editor behind the "React + CET Premium" tab is deliberately left out, so
 * that it keeps following the license and gives the premium plugins somewhere
 * to be exercised.
 * </p>
 *
 * @author Miguel Arroyo
 */
@Component(
	property = {
		"editor.config.key=contentEditor", "editor.config.key=defaultEditor",
		"editor.config.key=inputOnlyEditor",
		"editor.config.key=sampleAdvancedClassicEditor",
		"editor.config.key=sampleBalloonEditor",
		"editor.config.key=sampleBasicClassicEditor",
		"editor.config.key=sampleReactCKEditor5ClassicEditor",
		"jakarta.portlet.name=" + CKEditor5SamplePortletKeys.CKEDITOR_5_SAMPLE
	},
	service = EditorConfigContributor.class
)
public class CKEditor5SampleEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		jsonObject.put(
			"showPasteFromOfficeEnhanced", false
		).put(
			"showSourceEditingEnhanced", false
		);
	}

}