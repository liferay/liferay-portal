/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.parsers.bbcode.BBCodeTranslatorUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletLayoutFinderRegistryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Julio Camarero
 * @author Juan Fernández
 * @author Sergio González
 */
public class MBMessageAssetRenderer
	extends BaseJSPAssetRenderer<MBMessage> implements TrashRenderer {

	public MBMessageAssetRenderer(
		DiscussionPermission discussionPermission, HtmlParser htmlParser,
		MBMessage message,
		ModelResourcePermission<MBMessage> messageModelResourcePermission) {

		_discussionPermission = discussionPermission;
		_htmlParser = htmlParser;
		_message = message;
		_messageModelResourcePermission = messageModelResourcePermission;
	}

	@Override
	public MBMessage getAssetObject() {
		return _message;
	}

	@Override
	public String getClassName() {
		return MBMessage.class.getName();
	}

	@Override
	public long getClassPK() {
		return _message.getMessageId();
	}

	@Override
	public long getGroupId() {
		return _message.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/message_boards/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<MBMessage> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public String getSearchSummary(Locale locale) {
		if (_message.isFormatBBCode()) {
			return _htmlParser.extractText(
				BBCodeTranslatorUtil.getHTML(_message.getBody()));
		}

		return getSummary(null, null);
	}

	@Override
	public int getStatus() {
		return _message.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String summary = _message.getBody();

		if (Validator.isNotNull(summary)) {
			return _htmlParser.render(HtmlUtil.stripHtml(summary));
		}

		return summary;
	}

	@Override
	public String getTitle(Locale locale) {
		return _message.getSubject();
	}

	@Override
	public String getType() {
		return MBMessageAssetRendererFactory.TYPE;
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchGroup(_message.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, group, MBPortletKeys.MESSAGE_BOARDS, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/message_boards/edit_message"
		).setParameter(
			"messageId", _message.getMessageId()
		).buildPortletURL();
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws Exception {

		AssetRendererFactory<MBMessage> assetRendererFactory =
			getAssetRendererFactory();

		return PortletURLBuilder.create(
			assetRendererFactory.getURLView(liferayPortletResponse, windowState)
		).setMVCRenderCommandName(
			"/message_boards/view_message"
		).setParameter(
			"messageId", _message.getMessageId()
		).setWindowState(
			windowState
		).buildString();
	}

	@Override
	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

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
						new ClassPKInfoItemIdentifier(getClassPK())),
					themeDisplay);

			if (Validator.isNotNull(friendlyURL)) {
				return friendlyURL;
			}
		}

		if (!_hasViewInContextGroupLayout(
				_message.getGroupId(), themeDisplay)) {

			return null;
		}

		return getURLViewInContext(
			themeDisplay, noSuchEntryRedirect, "/message_boards/find_message",
			"messageId", _message.getMessageId());
	}

	@Override
	public long getUserId() {
		if (_message.isAnonymous()) {
			return 0;
		}

		return _message.getUserId();
	}

	@Override
	public String getUserName() {
		if (_message.isAnonymous()) {
			return LanguageUtil.get(
				LocaleThreadLocal.getDefaultLocale(), "anonymous");
		}

		return _message.getUserName();
	}

	@Override
	public String getUuid() {
		return _message.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		if (_message.isDiscussion()) {
			return _discussionPermission.hasPermission(
				permissionChecker, _message.getMessageId(), ActionKeys.UPDATE);
		}

		return _messageModelResourcePermission.contains(
			permissionChecker, _message, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		if (_message.isDiscussion()) {
			return _discussionPermission.hasPermission(
				permissionChecker, _message.getMessageId(), ActionKeys.VIEW);
		}

		return _messageModelResourcePermission.contains(
			permissionChecker, _message, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_MESSAGE, _message);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isPrintable() {
		return true;
	}

	public void setAssetDisplayPageFriendlyURLProvider(
		AssetDisplayPageFriendlyURLProvider
			assetDisplayPageFriendlyURLProvider) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
	}

	private boolean _hasViewInContextGroupLayout(
		long groupId, ThemeDisplay themeDisplay) {

		try {
			Layout layout = themeDisplay.getLayout();

			if (layout.isTypeControlPanel()) {
				return true;
			}

			PortletLayoutFinder portletLayoutFinder =
				PortletLayoutFinderRegistryUtil.getPortletLayoutFinder(
					getClassName());

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
		MBMessageAssetRenderer.class);

	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final DiscussionPermission _discussionPermission;
	private final HtmlParser _htmlParser;
	private final MBMessage _message;
	private final ModelResourcePermission<MBMessage>
		_messageModelResourcePermission;

}