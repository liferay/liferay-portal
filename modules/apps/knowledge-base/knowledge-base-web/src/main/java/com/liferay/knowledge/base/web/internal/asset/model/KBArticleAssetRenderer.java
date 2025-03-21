/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.knowledge.base.web.internal.constants.KBWebKeys;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBArticlePermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletLayoutFinderRegistryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Peter Shin
 */
public class KBArticleAssetRenderer
	extends BaseJSPAssetRenderer<KBArticle> implements TrashRenderer {

	public KBArticleAssetRenderer(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		HtmlParser htmlParser, KBArticle kbArticle, TrashHelper trashHelper) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_htmlParser = htmlParser;
		_kbArticle = kbArticle;
		_trashHelper = trashHelper;
	}

	@Override
	public KBArticle getAssetObject() {
		return _kbArticle;
	}

	@Override
	public String getClassName() {
		return KBArticle.class.getName();
	}

	@Override
	public long getClassPK() {
		return getClassPK(_kbArticle);
	}

	@Override
	public long getGroupId() {
		return _kbArticle.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/admin/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<KBArticle> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public int getStatus() {
		return _kbArticle.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		if (Validator.isNull(_kbArticle.getDescription())) {
			return StringUtil.shorten(
				_htmlParser.extractText(_kbArticle.getContent()), 200);
		}

		return _kbArticle.getDescription();
	}

	@Override
	public String getTitle(Locale locale) {
		if (_trashHelper == null) {
			return _kbArticle.getTitle();
		}

		return _trashHelper.getOriginalTitle(_kbArticle.getTitle());
	}

	@Override
	public String getType() {
		return KBArticleAssetRendererFactory.TYPE;
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, _getGroup(liferayPortletRequest),
				KBPortletKeys.KNOWLEDGE_BASE_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/knowledge_base/edit_kb_article"
		).setParameter(
			"resourcePrimKey", _kbArticle.getResourcePrimKey()
		).buildPortletURL();
	}

	@Override
	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				KBWebKeys.THEME_DISPLAY);

		return getURLViewInContext(themeDisplay, noSuchEntryRedirect);
	}

	@Override
	public String getURLViewInContext(
			ThemeDisplay themeDisplay, String noSuchEntryRedirect)
		throws PortalException {

		if (_assetDisplayPageFriendlyURLProvider != null) {
			String friendlyURL =
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					new InfoItemReference(
						getClassName(),
						new ClassPKInfoItemIdentifier(
							_kbArticle.getKbArticleId())),
					themeDisplay);

			if (Validator.isNotNull(friendlyURL)) {
				return friendlyURL;
			}
		}

		if (!_hasViewInContextGroupLayout(
				themeDisplay, _kbArticle.getGroupId())) {

			return null;
		}

		return KnowledgeBaseUtil.getKBArticleURL(
			themeDisplay.getPlid(), _kbArticle.getResourcePrimKey(),
			_kbArticle.getStatus(), themeDisplay.getPortalURL(), false);
	}

	@Override
	public long getUserId() {
		return _kbArticle.getUserId();
	}

	@Override
	public String getUserName() {
		return _kbArticle.getUserName();
	}

	@Override
	public String getUuid() {
		return _kbArticle.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return KBArticlePermission.contains(
			permissionChecker, _kbArticle, KBActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return KBArticlePermission.contains(
			permissionChecker, _kbArticle, KBActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE, _kbArticle);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isPrintable() {
		return true;
	}

	protected long getClassPK(KBArticle kbArticle) {
		if ((kbArticle.isDraft() || kbArticle.isPending()) &&
			(kbArticle.getVersion() != KBArticleConstants.DEFAULT_VERSION)) {

			return kbArticle.getPrimaryKey();
		}

		return kbArticle.getResourcePrimKey();
	}

	private Group _getGroup(LiferayPortletRequest liferayPortletRequest) {
		Group group = GroupLocalServiceUtil.fetchGroup(_kbArticle.getGroupId());

		if ((group != null) && !group.isCompany()) {
			return group;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getScopeGroup();
	}

	private boolean _hasViewInContextGroupLayout(
		ThemeDisplay themeDisplay, long groupId) {

		try {
			PortletLayoutFinder portletLayoutFinder =
				PortletLayoutFinderRegistryUtil.getPortletLayoutFinder(
					getClassName());

			if (portletLayoutFinder == null) {
				return false;
			}

			PortletLayoutFinder.Result result = portletLayoutFinder.find(
				themeDisplay, groupId);

			if ((result == null) || Validator.isNull(result.getPortletId())) {
				return false;
			}

			return true;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBArticleAssetRenderer.class);

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final HtmlParser _htmlParser;
	private final KBArticle _kbArticle;
	private final TrashHelper _trashHelper;

}