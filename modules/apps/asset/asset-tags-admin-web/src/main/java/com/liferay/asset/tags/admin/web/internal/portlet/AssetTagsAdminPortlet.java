/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.admin.web.internal.portlet;

import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.asset.kernel.exception.AssetTagNameException;
import com.liferay.asset.kernel.exception.DuplicateTagException;
import com.liferay.asset.kernel.exception.NoSuchTagException;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-asset-tag-admin",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.icon=/icons/asset_tag_admin.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Asset Tag Admin",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AssetTagsAdminPortlet extends MVCPortlet {

	public void deleteTag(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteTagIds = null;

		long tagId = ParamUtil.getLong(actionRequest, "tagId");

		if (tagId > 0) {
			deleteTagIds = new long[] {tagId};
		}
		else {
			deleteTagIds = ParamUtil.getLongValues(actionRequest, "rowIds");
		}

		for (long deleteTagId : deleteTagIds) {
			_assetTagService.deleteTag(deleteTagId);
		}
	}

	public void editTag(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long tagId = ParamUtil.getLong(actionRequest, "tagId");

		String name = ParamUtil.getString(actionRequest, "name");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			AssetTag.class.getName(), actionRequest);

		if (tagId <= 0) {

			// Add tag

			_assetTagService.addTag(
				null, serviceContext.getScopeGroupId(), name, serviceContext);
		}
		else {

			// Update tag

			_assetTagService.updateTag(null, tagId, name, serviceContext);
		}
	}

	public void mergeTag(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		String targetTagName = ParamUtil.getString(
			actionRequest, "targetTagName");

		AssetTag targetTag = _assetTagLocalService.fetchTag(
			groupId, targetTagName);

		if (targetTag == null) {
			return;
		}

		String[] mergeTagNames = ParamUtil.getStringValues(
			actionRequest, "mergeTagNames");

		for (String mergeTagName : mergeTagNames) {
			if (targetTagName.equals(mergeTagName)) {
				continue;
			}

			AssetTag mergeTag = _assetTagLocalService.fetchTag(
				groupId, mergeTagName);

			if (mergeTag == null) {
				continue;
			}

			_assetTagService.mergeTags(
				mergeTag.getTagId(), targetTag.getTagId());
		}
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		CTTimelineUtil.setClassName(renderRequest, AssetTag.class);

		if (SessionErrors.contains(
				renderRequest, NoSuchTagException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof AssetTagException ||
			throwable instanceof AssetTagNameException ||
			throwable instanceof DuplicateTagException ||
			throwable instanceof NoSuchTagException ||
			throwable instanceof PrincipalException) {

			return true;
		}

		return false;
	}

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private AssetTagService _assetTagService;

}