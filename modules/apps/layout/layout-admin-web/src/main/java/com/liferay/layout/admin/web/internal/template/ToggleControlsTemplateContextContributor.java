/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.template;

import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_THEME,
	service = TemplateContextContributor.class
)
public class ToggleControlsTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		String liferayToggleControls = "visible";

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent() &&
			!layout.isTypeControlPanel()) {

			liferayToggleControls = SessionClicks.get(
				httpServletRequest,
				"com.liferay.frontend.js.web_toggleControls", "visible");
		}

		_prepare(contextObjects, themeDisplay, liferayToggleControls);
	}

	private void _prepare(
		Map<String, Object> contextObjects, ThemeDisplay themeDisplay,
		String liferayToggleControls) {

		String cssClass = GetterUtil.getString(
			contextObjects.get("bodyCssClass"));

		if (Objects.equals(liferayToggleControls, "visible")) {
			cssClass += " controls-visible";
		}
		else {
			cssClass += " controls-hidden";
		}

		contextObjects.put("bodyCssClass", cssClass);

		contextObjects.put("liferay_toggle_controls", liferayToggleControls);
		contextObjects.put("show_toggle_controls", themeDisplay.isSignedIn());

		if (themeDisplay.isSignedIn()) {
			contextObjects.put(
				"toggle_controls_text",
				new UnsafeSupplierValue<>(
					() -> _language.get(
						themeDisplay.getLocale(), "toggle-controls")));

			contextObjects.put("toggle_controls_url", "javascript:void(0);");
		}
	}

	@Reference
	private Language _language;

}