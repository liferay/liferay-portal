/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.web.internal.asset.model;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.comment.web.internal.constants.CommentPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.comment.WorkflowableComment;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Jorge Ferrer
 * @author Sergio González
 */
public class CommentAssetRenderer
	extends BaseJSPAssetRenderer<WorkflowableComment> implements TrashRenderer {

	public CommentAssetRenderer(
		AssetRendererFactory<WorkflowableComment> assetRendererFactory,
		DiscussionPermission discussionPermission, HtmlParser htmlParser,
		WorkflowableComment workflowableComment) {

		_assetRendererFactory = assetRendererFactory;
		_discussionPermission = discussionPermission;
		_htmlParser = htmlParser;
		_workflowableComment = workflowableComment;
	}

	@Override
	public WorkflowableComment getAssetObject() {
		return _workflowableComment;
	}

	@Override
	public AssetRendererFactory<WorkflowableComment> getAssetRendererFactory() {
		return _assetRendererFactory;
	}

	@Override
	public String getClassName() {
		return _workflowableComment.getModelClassName();
	}

	@Override
	public long getClassPK() {
		return _workflowableComment.getCommentId();
	}

	@Override
	public long getGroupId() {
		return _workflowableComment.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/asset/discussion_" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<WorkflowableComment> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public String getSearchSummary(Locale locale) {
		return _htmlParser.extractText(
			_workflowableComment.getTranslatedBody(StringPool.BLANK));
	}

	@Override
	public int getStatus() {
		return _workflowableComment.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _workflowableComment.getBody();
	}

	@Override
	public String getTitle(Locale locale) {
		return StringUtil.shorten(getSearchSummary(locale));
	}

	@Override
	public String getType() {
		return CommentAssetRendererFactory.TYPE;
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchGroup(
			_workflowableComment.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, group, CommentPortletKeys.COMMENT, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/comment/edit_discussion"
		).setParameter(
			"commentId", _workflowableComment.getCommentId()
		).buildPortletURL();
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws Exception {

		AssetRendererFactory<WorkflowableComment> assetRendererFactory =
			getAssetRendererFactory();

		return PortletURLBuilder.create(
			assetRendererFactory.getURLView(liferayPortletResponse, windowState)
		).setMVCPath(
			"/view_comment.jsp"
		).setParameter(
			"commentId", _workflowableComment.getCommentId()
		).setWindowState(
			windowState
		).buildString();
	}

	@Override
	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect)
		throws Exception {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_workflowableComment.getClassName());

		if (assetRendererFactory == null) {
			return null;
		}

		AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(
			_workflowableComment.getClassPK());

		if (assetRenderer == null) {
			return null;
		}

		return assetRenderer.getURLViewInContext(
			liferayPortletRequest, liferayPortletResponse, noSuchEntryRedirect);
	}

	@Override
	public String getURLViewInContext(
			ThemeDisplay themeDisplay, String noSuchEntryRedirect)
		throws Exception {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_workflowableComment.getClassName());

		if (assetRendererFactory == null) {
			return null;
		}

		AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(
			_workflowableComment.getClassPK());

		if (assetRenderer == null) {
			return null;
		}

		return assetRenderer.getURLViewInContext(
			themeDisplay, noSuchEntryRedirect);
	}

	@Override
	public long getUserId() {
		return _workflowableComment.getUserId();
	}

	@Override
	public String getUserName() {
		return _workflowableComment.getUserName();
	}

	@Override
	public String getUuid() {
		return _workflowableComment.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _discussionPermission.hasUpdatePermission(
			permissionChecker, _workflowableComment.getCommentId());
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _discussionPermission.hasPermission(
			permissionChecker, _workflowableComment, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		Comment comment = CommentManagerUtil.fetchComment(
			_workflowableComment.getCommentId());

		httpServletRequest.setAttribute(WebKeys.COMMENT, comment);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isPrintable() {
		return true;
	}

	private final AssetRendererFactory<WorkflowableComment>
		_assetRendererFactory;
	private final DiscussionPermission _discussionPermission;
	private final HtmlParser _htmlParser;
	private final WorkflowableComment _workflowableComment;

}