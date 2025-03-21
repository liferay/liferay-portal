/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.microblogs.constants.MicroblogsPortletKeys;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.service.MicroblogsEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(
	property = "jakarta.portlet.name=" + MicroblogsPortletKeys.MICROBLOGS,
	service = AssetRendererFactory.class
)
public class MicroblogsEntryAssetRendererFactory
	extends BaseAssetRendererFactory<MicroblogsEntry> {

	public static final String CLASS_NAME = MicroblogsEntry.class.getName();

	public static final String TYPE = "microblogs";

	public MicroblogsEntryAssetRendererFactory() {
		setClassName(CLASS_NAME);
		setPortletId(MicroblogsPortletKeys.MICROBLOGS);
		setSelectable(false);
	}

	@Override
	public AssetRenderer<MicroblogsEntry> getAssetRenderer(
			long classPK, int type)
		throws PortalException {

		MicroblogsEntryAssetRenderer microblogsEntryAssetRenderer =
			new MicroblogsEntryAssetRenderer(
				_microblogsEntryLocalService.getMicroblogsEntry(classPK),
				_microblogsEntryModelResourcePermission);

		microblogsEntryAssetRenderer.setServletContext(_servletContext);

		return microblogsEntryAssetRenderer;
	}

	@Override
	public String getIconCssClass() {
		return "comments";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _microblogsEntryModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	@Reference
	private MicroblogsEntryLocalService _microblogsEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.microblogs.model.MicroblogsEntry)"
	)
	private ModelResourcePermission<MicroblogsEntry>
		_microblogsEntryModelResourcePermission;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.microblogs.web)")
	private ServletContext _servletContext;

}