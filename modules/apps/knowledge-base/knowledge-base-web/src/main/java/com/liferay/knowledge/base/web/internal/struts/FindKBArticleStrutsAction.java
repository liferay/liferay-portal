/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.struts;

import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.knowledge.base.util.AdminHelper;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBArticlePermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 */
@Component(
	property = "path=/knowledge_base/find_kb_article",
	service = StrutsAction.class
)
public class FindKBArticleStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String backURL = ParamUtil.getString(
			httpServletRequest, "p_l_back_url");
		long plid = ParamUtil.getLong(httpServletRequest, "plid");
		long resourcePrimKey = ParamUtil.getLong(
			httpServletRequest, "resourcePrimKey");
		int status = ParamUtil.getInteger(
			httpServletRequest, "status", WorkflowConstants.STATUS_APPROVED);
		boolean maximized = ParamUtil.getBoolean(
			httpServletRequest, "maximized");

		KBArticle kbArticle = _getKBArticle(resourcePrimKey, status);

		if (!_isValidPlid(plid)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			plid = themeDisplay.getPlid();
		}

		PortletURL portletURL = null;

		if ((kbArticle == null) ||
			(status != WorkflowConstants.STATUS_APPROVED)) {

			portletURL = _getDynamicPortletURL(
				plid, status, httpServletRequest);
		}

		if (portletURL == null) {
			portletURL = _getKBArticleURL(
				plid, false, kbArticle, httpServletRequest);
		}

		if (portletURL == null) {
			portletURL = _getKBArticleURL(
				plid, true, kbArticle, httpServletRequest);
		}

		if (portletURL == null) {
			portletURL = _getDynamicPortletURL(
				plid, status, httpServletRequest);
		}

		if (Validator.isNotNull(backURL)) {
			portletURL.setParameter("backURL", backURL);
		}

		if (maximized) {
			portletURL.setPortletMode(PortletMode.VIEW);
			portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
		}

		httpServletResponse.sendRedirect(portletURL.toString());

		return null;
	}

	private List<Layout> _getCandidateLayouts(
			long plid, boolean privateLayout, KBArticle kbArticle,
			ThemeDisplay themeDisplay)
		throws Exception {

		List<Layout> candidateLayouts = new ArrayList<>();

		Group group = _groupLocalService.getGroup(kbArticle.getGroupId());

		if (group.isLayout()) {
			Layout layout = _layoutLocalService.getLayout(group.getClassPK());

			candidateLayouts.add(layout);

			group = layout.getGroup();
		}

		candidateLayouts.addAll(
			_layoutLocalService.getLayouts(
				group.getGroupId(), privateLayout,
				LayoutConstants.TYPE_PORTLET));

		PortletLayoutFinder.Result result = _portletLayoutFinder.find(
			themeDisplay, kbArticle.getGroupId());

		Layout layout = _layoutLocalService.getLayout(result.getPlid());

		if (layout == null) {
			layout = _layoutLocalService.getLayout(plid);
		}

		if ((layout.getGroupId() == kbArticle.getGroupId()) &&
			layout.isTypePortlet()) {

			candidateLayouts.remove(layout);
			candidateLayouts.add(0, layout);
		}

		return candidateLayouts;
	}

	private PortletURL _getDynamicPortletURL(
			long plid, int status, HttpServletRequest httpServletRequest)
		throws Exception {

		String portletId = _getPortletId(plid);

		PortletURL portletURL = _getKBArticleURL(
			plid, portletId, null, httpServletRequest);

		if (status != WorkflowConstants.STATUS_APPROVED) {
			portletURL.setParameter("status", String.valueOf(status));
		}

		if (_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED) {
			portletURL.setParameter(
				"p_p_auth",
				AuthTokenUtil.getToken(httpServletRequest, plid, portletId));
		}

		portletURL.setPortletMode(PortletMode.VIEW);

		if (Objects.equals(
				portletId,
				KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE)) {

			portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
		}

		return portletURL;
	}

	private KBArticle _getKBArticle(long resourcePrimKey, int status)
		throws Exception {

		KBArticle kbArticle = _kbArticleLocalService.fetchLatestKBArticle(
			resourcePrimKey, status);

		if ((kbArticle == null) ||
			!KBArticlePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), kbArticle,
				KBActionKeys.VIEW)) {

			return null;
		}

		return kbArticle;
	}

	private PortletURL _getKBArticleURL(
			long plid, boolean privateLayout, KBArticle kbArticle,
			HttpServletRequest httpServletRequest)
		throws Exception {

		PortletURL firstMatchPortletURL = null;

		List<Layout> layouts = _getCandidateLayouts(
			plid, privateLayout, kbArticle,
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY));

		for (Layout layout : layouts) {
			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			List<Portlet> portlets =
				layoutTypePortlet.getAllNonembeddedPortlets();

			for (Portlet portlet : portlets) {
				String rootPortletId = PortletIdCodec.decodePortletName(
					portlet.getPortletId());

				if (rootPortletId.equals(
						KBPortletKeys.KNOWLEDGE_BASE_DISPLAY)) {

					PortletPreferences portletPreferences =
						PortletPreferencesFactoryUtil.getPortletSetup(
							layout, portlet.getPortletId(), StringPool.BLANK);

					long kbFolderClassNameId = _portal.getClassNameId(
						KBFolderConstants.getClassName());

					long resourceClassNameId = GetterUtil.getLong(
						portletPreferences.getValue(
							"resourceClassNameId", null),
						kbFolderClassNameId);

					long resourcePrimKey = GetterUtil.getLong(
						portletPreferences.getValue("resourcePrimKey", null),
						KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

					if (resourceClassNameId == kbFolderClassNameId) {
						if (_isParentFolder(
								resourcePrimKey, kbArticle.getKbFolderId())) {

							return _getKBArticleURL(
								layout.getPlid(), portlet.getPortletId(),
								kbArticle, httpServletRequest);
						}
					}
					else if (resourcePrimKey ==
								kbArticle.getResourcePrimKey()) {

						return _getKBArticleURL(
							layout.getPlid(), portlet.getPortletId(), kbArticle,
							httpServletRequest);
					}

					if (firstMatchPortletURL == null) {
						firstMatchPortletURL = _getKBArticleURL(
							layout.getPlid(), portlet.getPortletId(), kbArticle,
							httpServletRequest);
					}
				}

				if (rootPortletId.equals(
						KBPortletKeys.KNOWLEDGE_BASE_SECTION)) {

					KBArticle rootKBArticle =
						_kbArticleLocalService.fetchLatestKBArticle(
							kbArticle.getRootResourcePrimKey(),
							WorkflowConstants.STATUS_APPROVED);

					if (rootKBArticle == null) {
						continue;
					}

					PortletPreferences portletPreferences =
						PortletPreferencesFactoryUtil.getPortletSetup(
							layout, portlet.getPortletId(), StringPool.BLANK);

					String[] kbArticlesSections = portletPreferences.getValues(
						"kbArticlesSections", new String[0]);

					String[] sections = _adminHelper.unescapeSections(
						rootKBArticle.getSections());

					for (String section : sections) {
						if (!ArrayUtil.contains(kbArticlesSections, section)) {
							continue;
						}

						return _getKBArticleURL(
							layout.getPlid(), portlet.getPortletId(), kbArticle,
							httpServletRequest);
					}
				}

				if (!rootPortletId.equals(
						KBPortletKeys.KNOWLEDGE_BASE_ARTICLE)) {

					continue;
				}

				PortletPreferences portletPreferences =
					PortletPreferencesFactoryUtil.getPortletSetup(
						layout, portlet.getPortletId(), StringPool.BLANK);

				long resourcePrimKey = GetterUtil.getLong(
					portletPreferences.getValue("resourcePrimKey", null));

				KBArticle selKBArticle =
					_kbArticleLocalService.fetchLatestKBArticle(
						resourcePrimKey, WorkflowConstants.STATUS_APPROVED);

				if (selKBArticle == null) {
					continue;
				}

				long rootResourcePrimKey = kbArticle.getRootResourcePrimKey();
				long selRootResourcePrimKey =
					selKBArticle.getRootResourcePrimKey();

				if (rootResourcePrimKey == selRootResourcePrimKey) {
					return _getKBArticleURL(
						layout.getPlid(), portlet.getPortletId(), kbArticle,
						httpServletRequest);
				}

				if (firstMatchPortletURL == null) {
					firstMatchPortletURL = _getKBArticleURL(
						layout.getPlid(), portlet.getPortletId(), kbArticle,
						httpServletRequest);
				}
			}
		}

		return firstMatchPortletURL;
	}

	private PortletURL _getKBArticleURL(
			long plid, String portletId, KBArticle kbArticle,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest, portletId, plid,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/knowledge_base/view_kb_article"
		).setParameter(
			"resourcePrimKey",
			_getResourcePrimKey(
				ParamUtil.getLong(httpServletRequest, "resourcePrimKey"),
				kbArticle),
			false
		).setParameter(
			"urlTitle", _getUrlTitle(kbArticle), false
		).setParameter(
			"kbFolderUrlTitle", _getKBFolderUrlTitle(kbArticle), false
		).setPortletMode(
			PortletMode.VIEW
		).setWindowState(
			_getWindowState(portletId)
		).buildRenderURL();
	}

	private String _getKBFolderUrlTitle(KBArticle kbArticle) throws Exception {
		if ((kbArticle == null) || Validator.isNull(kbArticle.getUrlTitle()) ||
			(kbArticle.getKbFolderId() ==
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			return null;
		}

		KBFolder kbFolder = _kbFolderLocalService.getKBFolder(
			kbArticle.getKbFolderId());

		return kbFolder.getUrlTitle();
	}

	private String _getPortletId(long plid) throws Exception {
		Layout layout = _layoutLocalService.getLayout(plid);

		long selPlid = _portal.getPlidFromPortletId(
			layout.getGroupId(), KBPortletKeys.KNOWLEDGE_BASE_DISPLAY);

		if (selPlid != LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {
			return KBPortletKeys.KNOWLEDGE_BASE_DISPLAY;
		}

		if (layout.isTypeControlPanel()) {
			return KBPortletKeys.KNOWLEDGE_BASE_ADMIN;
		}

		return KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE;
	}

	private Long _getResourcePrimKey(
		long resourcePrimKey, KBArticle kbArticle) {

		if ((kbArticle == null) || Validator.isNull(kbArticle.getUrlTitle())) {
			return resourcePrimKey;
		}

		return null;
	}

	private String _getUrlTitle(KBArticle kbArticle) {
		if ((kbArticle != null) &&
			Validator.isNotNull(kbArticle.getUrlTitle())) {

			return kbArticle.getUrlTitle();
		}

		return null;
	}

	private WindowState _getWindowState(String portletId) {
		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		if (rootPortletId.equals(KBPortletKeys.KNOWLEDGE_BASE_SECTION)) {
			return LiferayWindowState.MAXIMIZED;
		}

		return LiferayWindowState.NORMAL;
	}

	private boolean _isParentFolder(long resourcePrimKey, long kbFolderId)
		throws Exception {

		if (resourcePrimKey == kbFolderId) {
			return true;
		}

		while (kbFolderId != KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			if (resourcePrimKey == kbFolderId) {
				return true;
			}

			KBFolder kbFolder = _kbFolderLocalService.getKBFolder(kbFolderId);

			kbFolderId = kbFolder.getParentKBFolderId();
		}

		return false;
	}

	private boolean _isValidPlid(long plid) throws Exception {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return false;
		}

		return true;
	}

	private static final boolean _PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(
				PropsKeys.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED));

	@Reference
	private AdminHelper _adminHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference
	private KBFolderLocalService _kbFolderLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBArticle)"
	)
	private PortletLayoutFinder _portletLayoutFinder;

}