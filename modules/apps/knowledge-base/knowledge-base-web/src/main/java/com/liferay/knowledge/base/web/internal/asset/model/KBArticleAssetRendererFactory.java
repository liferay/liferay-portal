/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 */
@Component(
	property = "jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
	service = AssetRendererFactory.class
)
public class KBArticleAssetRendererFactory
	extends BaseAssetRendererFactory<KBArticle> {

	public static final String TYPE = "article";

	public KBArticleAssetRendererFactory() {
		setLinkable(true);
		setSearchable(true);
	}

	@Override
	public AssetEntry getAssetEntry(KBArticle kbArticle)
		throws PortalException {

		return super.getAssetEntry(getClassName(), kbArticle.getClassPK());
	}

	@Override
	public AssetEntry getAssetEntry(String className, long classPK)
		throws PortalException {

		return getAssetEntry(_getKBArticle(classPK, TYPE_LATEST));
	}

	@Override
	public AssetRenderer<KBArticle> getAssetRenderer(
			KBArticle kbArticle, int type)
		throws PortalException {

		KBArticleAssetRenderer kbArticleAssetRenderer =
			new KBArticleAssetRenderer(
				_assetDisplayPageFriendlyURLProvider, _htmlParser, kbArticle,
				_trashHelper);

		kbArticleAssetRenderer.setAssetRendererType(type);
		kbArticleAssetRenderer.setServletContext(_servletContext);

		return kbArticleAssetRenderer;
	}

	@Override
	public AssetRenderer<KBArticle> getAssetRenderer(long classPK, int type)
		throws PortalException {

		return getAssetRenderer(_getKBArticle(classPK, type), type);
	}

	@Override
	public String getClassName() {
		return KBArticle.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "page";
	}

	@Override
	public String getPortletId() {
		return KBPortletKeys.KNOWLEDGE_BASE_DISPLAY;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public PortletURL getURLAdd(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classTypeId)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				liferayPortletRequest, getGroup(liferayPortletRequest),
				KBPortletKeys.KNOWLEDGE_BASE_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/admin/common/edit_kb_article.jsp"
		).buildPortletURL();
	}

	@Override
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, long groupId, long classTypeId) {

		return _portletResourcePermission.contains(
			permissionChecker, groupId, KBActionKeys.ADD_KB_ARTICLE);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _kbArticleModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private KBArticle _getKBArticle(long classPK, int type)
		throws PortalException {

		KBArticle kbArticle = _kbArticleLocalService.fetchKBArticle(classPK);

		if (kbArticle != null) {
			return kbArticle;
		}

		if (type == TYPE_LATEST_APPROVED) {
			kbArticle = _kbArticleLocalService.fetchLatestKBArticle(
				classPK, WorkflowConstants.STATUS_APPROVED);

			if (kbArticle != null) {
				return kbArticle;
			}

			kbArticle = _kbArticleLocalService.fetchLatestKBArticle(
				classPK, WorkflowConstants.STATUS_EXPIRED);
		}
		else {
			kbArticle = _kbArticleLocalService.fetchLatestKBArticle(
				classPK, WorkflowConstants.STATUS_ANY);
		}

		if (kbArticle != null) {
			return kbArticle;
		}

		return _kbArticleLocalService.getLatestKBArticle(
			classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBArticle)"
	)
	private ModelResourcePermission<KBArticle>
		_kbArticleModelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + KBConstants.RESOURCE_NAME_ADMIN + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.knowledge.base.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private TrashHelper _trashHelper;

}