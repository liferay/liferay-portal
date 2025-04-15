/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saved.content.taglib.servlet.taglib.SavedContentTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(service = FragmentRenderer.class)
public class SaveContentFragmentRenderer extends BaseContentFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "content-display";
	}

	@Override
	public String getIcon() {
		return "bookmarks";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "save-content");
	}

	@Override
	public boolean hasViewPermission(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		return FeatureFlagManagerUtil.isEnabled("LPS-197909");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return FeatureFlagManagerUtil.isEnabled("LPS-197909");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!FeatureFlagManagerUtil.isEnabled("LPS-197909")) {
			return;
		}

		SavedContentTag savedContentTag = new SavedContentTag();

		Tuple displayObjectTuple = getDisplayObjectTuple(
			fragmentRendererContext, httpServletRequest);

		savedContentTag.setClassName(
			GetterUtil.getString(displayObjectTuple.getObject(0)));
		savedContentTag.setClassPK(
			GetterUtil.getLong(displayObjectTuple.getObject(1)));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		savedContentTag.setGroupId(themeDisplay.getScopeGroupId());

		savedContentTag.setInTrash(false);
		savedContentTag.setViewMode(fragmentRendererContext.isViewMode());

		try {
			savedContentTag.doTag(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error("Unable to render saved content fragment", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SaveContentFragmentRenderer.class);

	@Reference
	private Language _language;

}