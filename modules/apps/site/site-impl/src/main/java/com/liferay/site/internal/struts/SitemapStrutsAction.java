/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.struts;

import com.liferay.portal.kernel.exception.NoSuchLayoutSetException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.manager.SitemapManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(property = "path=/portal/sitemap", service = StrutsAction.class)
public class SitemapStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

			LayoutSet layoutSet = null;

			if (groupId > 0) {
				Group group = _groupLocalService.getGroup(groupId);

				if (group.isStagingGroup()) {
					groupId = group.getLiveGroupId();
				}

				layoutSet = _layoutSetLocalService.getLayoutSet(groupId, false);
			}
			else {
				String host = _portal.getHost(httpServletRequest);

				host = StringUtil.toLowerCase(host);
				host = host.trim();

				VirtualHost virtualHost =
					_virtualHostLocalService.fetchVirtualHost(host);

				if ((virtualHost != null) &&
					(virtualHost.getLayoutSetId() != 0)) {

					layoutSet = _layoutSetLocalService.getLayoutSet(
						virtualHost.getLayoutSetId());

					Group group = layoutSet.getGroup();

					if (group.isStagingGroup()) {
						GroupPermissionUtil.check(
							themeDisplay.getPermissionChecker(),
							group.getGroupId(), ActionKeys.VIEW_STAGING);
					}
				}
				else {
					String groupName =
						PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME;

					if (Validator.isNull(groupName)) {
						groupName = GroupConstants.GUEST;
					}

					Group group = _groupLocalService.getGroup(
						themeDisplay.getCompanyId(), groupName);

					layoutSet = _layoutSetLocalService.getLayoutSet(
						group.getGroupId(), false);
				}
			}

			Group currentGroup = _groupLocalService.getGroup(
				layoutSet.getGroupId());

			if (!currentGroup.isActive()) {
				throw new NoSuchLayoutSetException();
			}

			InputStream inputStream = _sitemapManager.getSitemapInputStream(
				ParamUtil.getString(httpServletRequest, "assetTypeKey"),
				ParamUtil.getString(httpServletRequest, "layoutUuid"),
				layoutSet.getGroupId(),
				ParamUtil.getInteger(httpServletRequest, "page", 1),
				layoutSet.isPrivateLayout(), themeDisplay);

			if (inputStream == null) {
				httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

				return null;
			}

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse, null, inputStream,
				ContentTypes.TEXT_XML_UTF8);
		}
		catch (NoSuchLayoutSetException noSuchLayoutSetException) {
			_portal.sendError(
				HttpServletResponse.SC_NOT_FOUND, noSuchLayoutSetException,
				httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			_portal.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception,
				httpServletRequest, httpServletResponse);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SitemapStrutsAction.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SitemapManager _sitemapManager;

	@Reference
	private VirtualHostLocalService _virtualHostLocalService;

}