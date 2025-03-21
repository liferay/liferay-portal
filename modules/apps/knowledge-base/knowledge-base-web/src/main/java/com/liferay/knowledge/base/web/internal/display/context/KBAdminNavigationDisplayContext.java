/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemBuilder;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.knowledge.base.service.KBArticleServiceUtil;
import com.liferay.knowledge.base.service.KBFolderLocalServiceUtil;
import com.liferay.knowledge.base.service.KBFolderServiceUtil;
import com.liferay.knowledge.base.service.KBTemplateServiceUtil;
import com.liferay.knowledge.base.util.comparator.KBArticleTitleComparator;
import com.liferay.knowledge.base.util.comparator.KBObjectsPriorityComparator;
import com.liferay.knowledge.base.util.comparator.KBTemplateTitleComparator;
import com.liferay.knowledge.base.web.internal.display.context.helper.KBArticleURLHelper;
import com.liferay.knowledge.base.web.internal.security.permission.resource.AdminPermission;
import com.liferay.knowledge.base.web.internal.util.KBDropdownItemsProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.LiferayPortletUtil;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Sergio González
 */
public class KBAdminNavigationDisplayContext {

	public KBAdminNavigationDisplayContext(
			HttpServletRequest httpServletRequest, RenderRequest renderRequest,
			RenderResponse renderResponse, TrashHelper trashHelper)
		throws PortalException {

		_httpServletRequest = httpServletRequest;

		_kbArticleURLHelper = new KBArticleURLHelper(
			renderRequest, renderResponse);
		_liferayPortletResponse = LiferayPortletUtil.getLiferayPortletResponse(
			renderResponse);
		_selectedItemAncestorIds = _getSelectedItemAncestorIds();
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_kbDropdownItemsProvider = new KBDropdownItemsProvider(
			PortalUtil.getLiferayPortletRequest(
				(PortletRequest)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_REQUEST)),
			_liferayPortletResponse, trashHelper);
	}

	public List<NavigationItem> getInfoPanelNavigationItems() {
		return ListUtil.fromArray(
			NavigationItemBuilder.setActive(
				true
			).setHref(
				_themeDisplay.getURLCurrent()
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "details")
			).build());
	}

	public JSONArray getKBFolderDataJSONArray() throws PortalException {
		return JSONUtil.put(
			JSONUtil.put(
				"actions",
				_kbDropdownItemsProvider.getKBFolderDropdownItems(null)
			).put(
				"children",
				_getKBFolderDataJSONArray(
					KBFolderConstants.DEFAULT_PARENT_FOLDER_ID)
			).put(
				"classNameId",
				PortalUtil.getClassNameId(KBFolderConstants.getClassName())
			).put(
				"href",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).buildString()
			).put(
				"id", KBFolderConstants.DEFAULT_PARENT_FOLDER_ID
			).put(
				"name", _themeDisplay.translate("home")
			).put(
				"type", KBFolder.class.getSimpleName()
			));
	}

	public long getMoveParentKBObjectId() throws PortalException {
		long moveKBObjectId = ParamUtil.getLong(
			_httpServletRequest, "moveKBObjectId");

		String moveKBObjectClassName = ParamUtil.getString(
			_httpServletRequest, "moveKBObjectClassName");

		if (moveKBObjectClassName.equals(KBFolder.class.getSimpleName())) {
			KBFolder kbFolder = KBFolderLocalServiceUtil.getKBFolder(
				moveKBObjectId);

			return kbFolder.getParentKBFolderId();
		}

		int kbObjectVersion = ParamUtil.getInteger(
			_httpServletRequest, "kbObjectVersion", -1);

		KBArticle kbArticle = KBArticleLocalServiceUtil.getKBArticle(
			moveKBObjectId, kbObjectVersion);

		return kbArticle.getParentResourcePrimKey();
	}

	public List<JSONObject> getVerticalNavigationJSONObjects()
		throws PortalException {

		List<JSONObject> verticalNavigationItems = new ArrayList<>();

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		String mvcPath = ParamUtil.getString(_httpServletRequest, "mvcPath");
		String mvcRenderCommandName = ParamUtil.getString(
			_httpServletRequest, "mvcRenderCommandName");

		if (PortletPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), _themeDisplay.getPlid(),
				portletDisplay.getId(), KBActionKeys.VIEW)) {

			boolean active = false;
			JSONArray navigationItemsJSONArray = null;

			if (!mvcPath.equals("/admin/view_kb_suggestions.jsp") &&
				!mvcPath.equals("/admin/view_kb_template.jsp") &&
				!mvcRenderCommandName.equals(
					"/knowledge_base/view_kb_templates")) {

				active = true;
				navigationItemsJSONArray = getKBFolderDataJSONArray();
			}

			verticalNavigationItems.add(
				JSONUtil.put(
					"active", active
				).put(
					"href",
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCPath(
						"/admin/view.jsp"
					).buildString()
				).put(
					"icon", "pages-tree"
				).put(
					"key", "article"
				).put(
					"navigationItems", navigationItemsJSONArray
				).put(
					"selectedItemId", _getSelectedItemId()
				).put(
					"title",
					LanguageUtil.get(
						_httpServletRequest, "folders-and-articles")
				));
		}

		if (AdminPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				KBActionKeys.VIEW_KB_TEMPLATES)) {

			boolean active = false;
			JSONArray navigationItemsJSONArray = null;

			if (mvcPath.equals("/admin/view_kb_template.jsp") ||
				mvcRenderCommandName.equals(
					"/knowledge_base/view_kb_templates")) {

				active = true;
				navigationItemsJSONArray =
					_getKBTemplatesNavigationItemsJSONArray();
			}

			verticalNavigationItems.add(
				JSONUtil.put(
					"active", active
				).put(
					"href",
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCRenderCommandName(
						"/knowledge_base/view_kb_templates"
					).buildString()
				).put(
					"icon", "page-template"
				).put(
					"key", "template"
				).put(
					"navigationItems", navigationItemsJSONArray
				).put(
					"selectedItemId", _getSelectedItemId()
				).put(
					"title", LanguageUtil.get(_httpServletRequest, "templates")
				));
		}

		if (AdminPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				KBActionKeys.VIEW_SUGGESTIONS)) {

			boolean active = false;

			if (mvcPath.equals("/admin/view_kb_suggestions.jsp")) {
				active = true;
			}

			verticalNavigationItems.add(
				JSONUtil.put(
					"active", active
				).put(
					"href",
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCPath(
						"/admin/view_kb_suggestions.jsp"
					).buildString()
				).put(
					"icon", "message"
				).put(
					"key", "suggestion"
				).put(
					"title",
					LanguageUtil.get(_httpServletRequest, "suggestions")
				));
		}

		return verticalNavigationItems;
	}

	public boolean isProductMenuOpen() {
		String productMenuState = SessionClicks.get(
			_httpServletRequest,
			"com.liferay.product.navigation.product.menu.web_productMenuState",
			"closed");

		return Objects.equals(productMenuState, "open");
	}

	private JSONArray _getChildKBArticlesJSONArray(KBArticle parentKBArticle)
		throws PortalException {

		JSONArray childrenJSONArray = JSONFactoryUtil.createJSONArray();

		long moveKBObjectId = ParamUtil.getLong(
			_httpServletRequest, "moveKBObjectId");

		List<KBArticle> kbArticles = KBArticleServiceUtil.getKBArticles(
			parentKBArticle.getGroupId(), parentKBArticle.getResourcePrimKey(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, WorkflowConstants.STATUS_ANY,
			KBArticleTitleComparator.getInstance(true));

		for (KBArticle kbArticle : kbArticles) {
			if (moveKBObjectId == kbArticle.getResourcePrimKey()) {
				continue;
			}

			childrenJSONArray.put(
				JSONUtil.put(
					"actions",
					_kbDropdownItemsProvider.getKBArticleDropdownItems(
						kbArticle, _selectedItemAncestorIds)
				).put(
					"children", _getChildKBArticlesJSONArray(kbArticle)
				).put(
					"classNameId", kbArticle.getClassNameId()
				).put(
					"href",
					_kbArticleURLHelper.createViewWithRedirectURL(
						kbArticle,
						PortalUtil.getCurrentURL(_httpServletRequest))
				).put(
					"id", kbArticle.getResourcePrimKey()
				).put(
					"name", kbArticle.getTitle()
				).put(
					"type", KBArticle.class.getSimpleName()
				));
		}

		return childrenJSONArray;
	}

	private KBFolder _getKBFolder(long kbFolderId) throws PortalException {
		if (kbFolderId != KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return KBFolderServiceUtil.getKBFolder(kbFolderId);
		}

		return null;
	}

	private JSONArray _getKBFolderDataJSONArray(long parentFolderId)
		throws PortalException {

		JSONArray childrenJSONArray = JSONFactoryUtil.createJSONArray();

		long moveKBObjectId = ParamUtil.getLong(
			_httpServletRequest, "moveKBObjectId");

		List<Object> kbObjects = KBFolderServiceUtil.getKBFoldersAndKBArticles(
			_themeDisplay.getScopeGroupId(), parentFolderId,
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			KBObjectsPriorityComparator.getInstance(true));

		for (Object kbObject : kbObjects) {
			if (kbObject instanceof KBFolder) {
				KBFolder kbFolder = (KBFolder)kbObject;

				if (moveKBObjectId == kbFolder.getKbFolderId()) {
					continue;
				}

				childrenJSONArray.put(
					JSONUtil.put(
						"actions",
						_kbDropdownItemsProvider.getKBFolderDropdownItems(
							kbFolder, _selectedItemAncestorIds)
					).put(
						"children",
						_getKBFolderDataJSONArray(kbFolder.getKbFolderId())
					).put(
						"classNameId", kbFolder.getClassNameId()
					).put(
						"href",
						PortletURLBuilder.createRenderURL(
							_liferayPortletResponse
						).setMVCPath(
							"/admin/view_kb_folders.jsp"
						).setParameter(
							"parentResourceClassNameId",
							kbFolder.getClassNameId()
						).setParameter(
							"parentResourcePrimKey", kbFolder.getKbFolderId()
						).setParameter(
							"selectedItemId", kbFolder.getKbFolderId()
						).buildString()
					).put(
						"id", kbFolder.getKbFolderId()
					).put(
						"name", kbFolder.getName()
					).put(
						"type", KBFolder.class.getSimpleName()
					));
			}
			else {
				KBArticle kbArticle = (KBArticle)kbObject;

				if (moveKBObjectId == kbArticle.getResourcePrimKey()) {
					continue;
				}

				childrenJSONArray.put(
					JSONUtil.put(
						"actions",
						_kbDropdownItemsProvider.getKBArticleDropdownItems(
							kbArticle, _selectedItemAncestorIds)
					).put(
						"children", _getChildKBArticlesJSONArray(kbArticle)
					).put(
						"classNameId", kbArticle.getClassNameId()
					).put(
						"href",
						_kbArticleURLHelper.createViewWithRedirectURL(
							kbArticle,
							PortalUtil.getCurrentURL(_httpServletRequest))
					).put(
						"id", kbArticle.getResourcePrimKey()
					).put(
						"name", kbArticle.getTitle()
					).put(
						"type", KBArticle.class.getSimpleName()
					));
			}
		}

		return childrenJSONArray;
	}

	private JSONArray _getKBTemplateChildrenJSONArray() {
		JSONArray navigationItemsJSONArray = JSONFactoryUtil.createJSONArray();

		List<KBTemplate> kbTemplates =
			KBTemplateServiceUtil.getGroupKBTemplates(
				_themeDisplay.getScopeGroupId(), QueryUtil.ALL_POS,
				WorkflowConstants.STATUS_ANY,
				KBTemplateTitleComparator.getInstance(true));

		for (KBTemplate kbTemplate : kbTemplates) {
			navigationItemsJSONArray.put(
				JSONUtil.put(
					"actions",
					_kbDropdownItemsProvider.getKBTemplateDropdownItems(
						kbTemplate)
				).put(
					"href",
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCPath(
						"/admin/common/edit_kb_template.jsp"
					).setRedirect(
						PortalUtil.getCurrentURL(_httpServletRequest)
					).setParameter(
						"kbTemplateId", kbTemplate.getKbTemplateId()
					).setParameter(
						"selectedItemId", kbTemplate.getPrimaryKey()
					).buildString()
				).put(
					"id", kbTemplate.getPrimaryKey()
				).put(
					"name", kbTemplate.getTitle()
				).put(
					"type", "template"
				));
		}

		return navigationItemsJSONArray;
	}

	private JSONArray _getKBTemplatesNavigationItemsJSONArray() {
		return JSONUtil.put(
			JSONUtil.put(
				"actions",
				_kbDropdownItemsProvider.getKBFolderDropdownItems(null)
			).put(
				"children", _getKBTemplateChildrenJSONArray()
			).put(
				"href",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/knowledge_base/view_kb_templates"
				).buildString()
			).put(
				"id", KBFolderConstants.DEFAULT_PARENT_FOLDER_ID
			).put(
				"name", _themeDisplay.translate("home")
			).put(
				"type", KBFolder.class.getSimpleName()
			));
	}

	private List<Long> _getSelectedItemAncestorIds() throws PortalException {
		if (_getSelectedItemId() ==
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			return Collections.emptyList();
		}

		List<Long> selectedItemAncestorIds = new ArrayList<>();

		Long kbFolderId = null;

		if (_isKBArticleSelected()) {
			KBArticle kbArticle = _getSelectedKBArticle();

			if (kbArticle != null) {
				selectedItemAncestorIds.addAll(
					kbArticle.getAncestorResourcePrimaryKeys());
				kbFolderId = kbArticle.getKbFolderId();
			}
		}

		if (kbFolderId == null) {
			kbFolderId = ParamUtil.getLong(
				_httpServletRequest, "parentResourcePrimKey",
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		}

		KBFolder kbFolder = _getKBFolder(kbFolderId);

		if (kbFolder != null) {
			selectedItemAncestorIds.addAll(kbFolder.getAncestorKBFolderIds());
		}

		return selectedItemAncestorIds;
	}

	private long _getSelectedItemId() {
		return ParamUtil.getLong(
			_httpServletRequest, "selectedItemId",
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	private KBArticle _getSelectedKBArticle() throws PortalException {
		long resourcePrimKey = ParamUtil.getLong(
			_httpServletRequest, "resourcePrimKey",
			KBArticleConstants.DEFAULT_PARENT_RESOURCE_PRIM_KEY);

		if (resourcePrimKey ==
				KBArticleConstants.DEFAULT_PARENT_RESOURCE_PRIM_KEY) {

			return null;
		}

		return KBArticleServiceUtil.getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);
	}

	private boolean _isKBArticleSelected() {
		long kbArticleClassNameId = PortalUtil.getClassNameId(
			KBArticleConstants.getClassName());

		long resourceClassNameId = ParamUtil.getLong(
			_httpServletRequest, "resourceClassNameId");

		if (resourceClassNameId == kbArticleClassNameId) {
			return true;
		}

		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private final KBArticleURLHelper _kbArticleURLHelper;
	private final KBDropdownItemsProvider _kbDropdownItemsProvider;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final List<Long> _selectedItemAncestorIds;
	private final ThemeDisplay _themeDisplay;

}