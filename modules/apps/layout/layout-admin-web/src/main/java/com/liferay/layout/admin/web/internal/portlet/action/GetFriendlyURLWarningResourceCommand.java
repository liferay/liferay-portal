/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.impl.LayoutLocalServiceHelper;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/get_friendly_url_warning"
	},
	service = MVCResourceCommand.class
)
public class GetFriendlyURLWarningResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPS-174417")) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put("hasWarnings", false));

			return;
		}

		String friendlyURL = ParamUtil.getString(
			resourceRequest, "friendlyURL");

		if (Validator.isNotNull(friendlyURL)) {
			long plid = ParamUtil.getLong(
				resourceRequest, "plid", LayoutConstants.DEFAULT_PLID);

			if (plid == LayoutConstants.DEFAULT_PLID) {
				JSONPortletResponseUtil.writeJSON(
					resourceRequest, resourceResponse,
					JSONUtil.put("hasWarnings", false));

				return;
			}

			Layout layout = _layoutLocalService.getLayout(plid);

			if (!_layoutSetPrototypeHelper.hasDuplicatedFriendlyURLs(
					layout.getUuid(), layout.getGroupId(),
					layout.isPrivateLayout(), friendlyURL)) {

				JSONPortletResponseUtil.writeJSON(
					resourceRequest, resourceResponse,
					JSONUtil.put("hasWarnings", false));

				return;
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"hasWarnings", true
				).put(
					"warningMessage",
					() -> {
						ThemeDisplay themeDisplay =
							(ThemeDisplay)resourceRequest.getAttribute(
								WebKeys.THEME_DISPLAY);

						return _getWarningMessage(
							layout.getGroup(), themeDisplay.getLocale());
					}
				));

			return;
		}

		long groupId = ParamUtil.getLong(resourceRequest, "groupId");
		String name = ParamUtil.getString(resourceRequest, "name");

		if ((groupId == 0) || Validator.isNull(name)) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put("hasWarnings", false));

			return;
		}

		boolean privateLayout = ParamUtil.getBoolean(
			resourceRequest, "privateLayout");

		friendlyURL = StringPool.SLASH.concat(
			_layoutLocalServiceHelper.getFriendlyURL(name));

		if (!_layoutSetPrototypeHelper.hasDuplicatedFriendlyURLs(
				null, groupId, privateLayout, friendlyURL)) {

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put("hasWarnings", false));

			return;
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"hasWarnings", true
			).put(
				"warningMessage",
				() -> {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)resourceRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					return _getWarningMessage(
						_groupLocalService.getGroup(groupId),
						themeDisplay.getLocale());
				}
			));
	}

	private String _getWarningMessage(Group group, Locale locale)
		throws PortalException {

		if (group.isLayoutSetPrototype()) {
			return _language.get(
				locale,
				StringBundler.concat(
					"the-friendly-url-of-the-site-template-page-you-are-",
					"trying-to-save-conflicts-with-some-of-the-own-pages-of-",
					"the-sites-created-from-this-site-template.-are-you-sure-",
					"you-want-to-configure-the-site-template-page-with-this-",
					"friendly-url"));
		}

		return _language.get(
			locale,
			StringBundler.concat(
				"the-friendly-url-of-the-page-you-are-trying-to-save-",
				"conflicts-with-a-friendly-url-of-a-page-in-the-site-",
				"template,-from-which-this-site-was-created.-are-you-sure-you-",
				"want-to-configure-the-page-with-this-friendly-url"));
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutLocalServiceHelper _layoutLocalServiceHelper;

	@Reference
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

}