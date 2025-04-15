/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.template;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.staging.bar.web.internal.util.StagingBarControlMenuUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_THEME,
	service = TemplateContextContributor.class
)
public class StagingBarTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (StagingBarControlMenuUtil.isShow(httpServletRequest)) {
				StringBundler sb = new StringBundler(3);

				sb.append(
					GetterUtil.getString(contextObjects.get("bodyCssClass")));

				Layout layout = themeDisplay.getLayout();

				if (!layout.isSystem() || layout.isTypeControlPanel() ||
					!Objects.equals(
						layout.getFriendlyURL(),
						PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL)) {

					sb.append(" has-staging-bar");
				}

				Group group = themeDisplay.getScopeGroup();

				if (group.isStagingGroup()) {
					sb.append(" staging local-staging");
				}
				else if (themeDisplay.isShowStagingIcon() &&
						 group.hasStagingGroup()) {

					sb.append(" live-view");
				}
				else if (themeDisplay.isShowStagingIcon() &&
						 group.isStagedRemotely()) {

					sb.append(" staging remote-staging");
				}

				contextObjects.put("bodyCssClass", sb.toString());
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		contextObjects.put("show_staging", themeDisplay.isShowStagingIcon());

		if (themeDisplay.isShowStagingIcon()) {
			contextObjects.put(
				"staging_text", _language.get(httpServletRequest, "staging"));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagingBarTemplateContextContributor.class);

	@Reference
	private Language _language;

}