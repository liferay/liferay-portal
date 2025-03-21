/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.service.KBArticleServiceUtil;
import com.liferay.knowledge.base.service.KBFolderServiceUtil;
import com.liferay.knowledge.base.service.KBTemplateServiceUtil;
import com.liferay.knowledge.base.web.internal.KBUtil;
import com.liferay.knowledge.base.web.internal.search.EntriesChecker;
import com.liferay.knowledge.base.web.internal.search.KBObjectsSearch;
import com.liferay.knowledge.base.web.internal.security.permission.resource.AdminPermission;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBArticlePermission;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBFolderPermission;
import com.liferay.knowledge.base.web.internal.util.comparator.KBOrderByComparatorAdapter;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class KBAdminManagementToolbarDisplayContext {

	public KBAdminManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			RenderRequest renderRequest, RenderResponse renderResponse,
			PortletConfig portletConfig, TrashHelper trashHelper)
		throws PortalException, PortletException {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_portletConfig = portletConfig;
		_trashHelper = trashHelper;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_createSearchContainer();
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteEntries");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public List<String> getAvailableActions(KBArticle kbArticle)
		throws PortalException {

		if (KBArticlePermission.contains(
				_themeDisplay.getPermissionChecker(), kbArticle,
				ActionKeys.DELETE)) {

			return Collections.singletonList("deleteEntries");
		}

		return Collections.emptyList();
	}

	public List<String> getAvailableActions(KBFolder kbFolder)
		throws PortalException {

		if (KBFolderPermission.contains(
				_themeDisplay.getPermissionChecker(), kbFolder,
				ActionKeys.DELETE)) {

			return Collections.singletonList("deleteEntries");
		}

		return Collections.emptyList();
	}

	public CreationMenu getCreationMenu() throws PortalException {
		CreationMenu creationMenu = new CreationMenu();

		long parentResourcePrimKey = ParamUtil.getLong(
			_httpServletRequest, "parentResourcePrimKey",
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		if (_hasAddKBFolderPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						PortletURLBuilder.createRenderURL(
							_liferayPortletResponse
						).setMVCPath(
							"/admin/common/edit_kb_folder.jsp"
						).setRedirect(
							PortalUtil.getCurrentURL(_httpServletRequest)
						).setParameter(
							"parentResourceClassNameId",
							PortalUtil.getClassNameId(
								KBFolderConstants.getClassName())
						).setParameter(
							"parentResourcePrimKey", parentResourcePrimKey
						).buildPortletURL());
					dropdownItem.setIcon("folder");
					dropdownItem.setLabel(
						LanguageUtil.get(_httpServletRequest, "folder"));
				});
		}

		long kbFolderClassNameId = PortalUtil.getClassNameId(
			KBFolderConstants.getClassName());

		long parentResourceClassNameId = ParamUtil.getLong(
			_httpServletRequest, "parentResourceClassNameId",
			kbFolderClassNameId);

		if (_hasAddKBArticlePermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						PortletURLBuilder.createRenderURL(
							_liferayPortletResponse
						).setMVCPath(
							"/admin/common/edit_kb_article.jsp"
						).setRedirect(
							PortalUtil.getCurrentURL(_httpServletRequest)
						).setParameter(
							"parentResourceClassNameId",
							parentResourceClassNameId
						).setParameter(
							"parentResourcePrimKey", parentResourcePrimKey
						).buildPortletURL());
					dropdownItem.setIcon("document-text");
					dropdownItem.setLabel(
						LanguageUtil.get(_httpServletRequest, "basic-article"));
				});

			List<KBTemplate> kbTemplates =
				KBTemplateServiceUtil.getGroupKBTemplates(
					_themeDisplay.getScopeGroupId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS,
					OrderByComparatorFactoryUtil.create(
						"KBTemplate", "title", false));

			if (!kbTemplates.isEmpty()) {
				for (KBTemplate kbTemplate : kbTemplates) {
					creationMenu.addDropdownItem(
						dropdownItem -> {
							dropdownItem.setHref(
								PortletURLBuilder.createRenderURL(
									_liferayPortletResponse
								).setMVCPath(
									"/admin/common/edit_kb_article.jsp"
								).setRedirect(
									PortalUtil.getCurrentURL(
										_httpServletRequest)
								).setParameter(
									"kbTemplateId", kbTemplate.getKbTemplateId()
								).setParameter(
									"parentResourceClassNameId",
									parentResourceClassNameId
								).setParameter(
									"parentResourcePrimKey",
									parentResourcePrimKey
								).buildPortletURL());
							dropdownItem.setIcon("document-text");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									HtmlUtil.escape(kbTemplate.getTitle())));
						});
				}
			}
		}

		if ((parentResourceClassNameId == kbFolderClassNameId) &&
			AdminPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), KBActionKeys.ADD_KB_ARTICLE)) {

			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						PortletURLBuilder.createRenderURL(
							_liferayPortletResponse
						).setMVCPath(
							"/admin/import.jsp"
						).setRedirect(
							PortalUtil.getCurrentURL(_httpServletRequest)
						).setParameter(
							"parentKBFolderId", parentResourcePrimKey
						).buildPortletURL());
					dropdownItem.setIcon("import");
					dropdownItem.setLabel(
						LanguageUtil.get(_httpServletRequest, "import"));
				});
		}

		if (creationMenu.isEmpty()) {
			return null;
		}

		return creationMenu;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
			"entries-display-style", "descriptive", true);

		return _displayStyle;
	}

	public List<DropdownItem> getEmptyStateActionDropdownItems() {
		long kbFolderClassNameId = PortalUtil.getClassNameId(
			KBFolderConstants.getClassName());

		long parentResourceClassNameId = ParamUtil.getLong(
			_httpServletRequest, "parentResourceClassNameId",
			kbFolderClassNameId);

		long parentResourcePrimKey = ParamUtil.getLong(
			_httpServletRequest, "parentResourcePrimKey",
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return DropdownItemListBuilder.add(
			() -> _hasAddKBFolderPermission(),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCPath(
						"/admin/common/edit_kb_folder.jsp"
					).setRedirect(
						PortalUtil.getCurrentURL(_httpServletRequest)
					).setParameter(
						"parentResourceClassNameId",
						PortalUtil.getClassNameId(
							KBFolderConstants.getClassName())
					).setParameter(
						"parentResourcePrimKey", parentResourcePrimKey
					).buildPortletURL());
				dropdownItem.setIcon("folder");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "folder"));
			}
		).add(
			() -> _hasAddKBArticlePermission(),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCPath(
						"/admin/common/edit_kb_article.jsp"
					).setRedirect(
						PortalUtil.getCurrentURL(_httpServletRequest)
					).setParameter(
						"parentResourceClassNameId", parentResourceClassNameId
					).setParameter(
						"parentResourcePrimKey", parentResourcePrimKey
					).buildPortletURL());
				dropdownItem.setIcon("document-text");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "basic-article"));
			}
		).add(
			() ->
				(parentResourceClassNameId == kbFolderClassNameId) &&
				AdminPermission.contains(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(),
					KBActionKeys.ADD_KB_ARTICLE),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCPath(
						"/admin/import.jsp"
					).setRedirect(
						PortalUtil.getCurrentURL(_httpServletRequest)
					).setParameter(
						"parentKBFolderId", parentResourcePrimKey
					).buildPortletURL());
				dropdownItem.setIcon("import");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "import"));
			}
		).build();
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return new DropdownItemList() {
			{
				Map<String, String> orderColumnsMap = HashMapBuilder.put(
					"modified-date", "modified-date"
				).put(
					"priority", "priority"
				).put(
					"title", "title"
				).put(
					"view-count", "view-count"
				).build();

				String[] orderColumns = {
					"priority", "modified-date", "title", "view-count"
				};

				for (String orderByCol : orderColumns) {
					add(
						dropdownItem -> {
							dropdownItem.setActive(
								orderByCol.equals(_getOrderByCol()));
							dropdownItem.setHref(
								_getCurrentSortingURL(), "orderByCol",
								orderByCol);
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									orderColumnsMap.get(orderByCol)));
						});
				}
			}
		};
	}

	public String getOrderByType() {
		return _searchContainer.getOrderByType();
	}

	public SearchContainer<Object> getSearchContainer() {
		return _searchContainer;
	}

	public PortletURL getSearchURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/knowledge_base/search"
		).setRedirect(
			_getRedirect()
		).buildPortletURL();
	}

	public PortletURL getSortingURL() throws PortletException {
		return PortletURLBuilder.create(
			_getCurrentSortingURL()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildPortletURL();
	}

	public int getTotal() {
		return _searchContainer.getTotal();
	}

	public List<ViewTypeItem> getViewTypeItems() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/admin/view.jsp"
		).setParameter(
			"parentResourcePrimKey",
			ParamUtil.getLong(
				_httpServletRequest, "parentResourcePrimKey",
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID)
		).buildPortletURL();

		return new ViewTypeItemList(portletURL, getDisplayStyle()) {
			{
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	public boolean isDisabled() {
		return !_searchContainer.hasResults();
	}

	public boolean isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	public boolean isShowInfoButton() {
		return !isSearch();
	}

	public boolean isTrashEnabled() throws PortalException {
		return _trashHelper.isTrashEnabled(_themeDisplay.getScopeGroupId());
	}

	private SearchContainer<Object> _createSearchContainer()
		throws PortalException, PortletException {

		long kbFolderClassNameId = PortalUtil.getClassNameId(
			KBFolderConstants.getClassName());

		long parentResourceClassNameId = ParamUtil.getLong(
			_httpServletRequest, "parentResourceClassNameId",
			kbFolderClassNameId);

		long parentResourcePrimKey = ParamUtil.getLong(
			_httpServletRequest, "parentResourcePrimKey",
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_searchContainer = new KBObjectsSearch(
			_renderRequest,
			PortletURLUtil.clone(
				PortletURLUtil.getCurrent(
					_liferayPortletRequest, _liferayPortletResponse),
				_renderResponse));

		boolean kbFolderView = false;

		if (parentResourceClassNameId == kbFolderClassNameId) {
			kbFolderView = true;
		}

		String keywords = _getKeywords();

		if (Validator.isNotNull(keywords)) {
			OrderByComparator<KBArticle> kbArticleOrderByComparator =
				KBUtil.getKBArticleOrderByComparator(
					_searchContainer.getOrderByCol(),
					_searchContainer.getOrderByType());

			_searchContainer.setOrderByComparator(
				new KBOrderByComparatorAdapter<>(kbArticleOrderByComparator));

			_searchContainer.setResultsAndTotal(
				() -> new ArrayList<>(
					KBArticleServiceUtil.getKBArticlesByKeywords(
						_themeDisplay.getScopeGroupId(), keywords,
						WorkflowConstants.STATUS_ANY,
						_searchContainer.getStart(),
						_searchContainer.getEnd())),
				KBArticleServiceUtil.countKBArticlesByKeywords(
					_themeDisplay.getScopeGroupId(), keywords,
					WorkflowConstants.STATUS_ANY));
		}
		else if (kbFolderView) {
			_searchContainer.setResultsAndTotal(
				() -> KBFolderServiceUtil.getKBFoldersAndKBArticles(
					_themeDisplay.getScopeGroupId(), parentResourcePrimKey,
					WorkflowConstants.STATUS_ANY, _searchContainer.getStart(),
					_searchContainer.getEnd(),
					KBUtil.getKBObjectsOrderByComparator(
						_searchContainer.getOrderByCol(),
						_searchContainer.getOrderByType())),
				KBFolderServiceUtil.getKBFoldersAndKBArticlesCount(
					_themeDisplay.getScopeGroupId(), parentResourcePrimKey,
					WorkflowConstants.STATUS_ANY));
		}
		else {
			OrderByComparator<KBArticle> kbArticleOrderByComparator =
				KBUtil.getKBArticleOrderByComparator(
					_searchContainer.getOrderByCol(),
					_searchContainer.getOrderByType());

			_searchContainer.setOrderByComparator(
				new KBOrderByComparatorAdapter<>(kbArticleOrderByComparator));

			_searchContainer.setResultsAndTotal(
				() -> new ArrayList<>(
					KBArticleServiceUtil.getKBArticles(
						_themeDisplay.getScopeGroupId(), parentResourcePrimKey,
						WorkflowConstants.STATUS_ANY,
						_searchContainer.getStart(), _searchContainer.getEnd(),
						kbArticleOrderByComparator)),
				KBArticleServiceUtil.getKBArticlesCount(
					_themeDisplay.getScopeGroupId(), parentResourcePrimKey,
					WorkflowConstants.STATUS_ANY));
		}

		EntriesChecker entriesChecker = new EntriesChecker(
			_liferayPortletRequest, _liferayPortletResponse);

		entriesChecker.setRememberCheckBoxState(false);

		_searchContainer.setRowChecker(entriesChecker);

		return _searchContainer;
	}

	private PortletURL _getCurrentSortingURL() throws PortletException {
		PortletURL sortingURL = PortletURLUtil.clone(
			PortletURLUtil.getCurrent(
				_liferayPortletRequest, _liferayPortletResponse),
			_liferayPortletResponse);

		if (Validator.isNull(_getKeywords())) {
			return sortingURL;
		}

		return PortletURLBuilder.create(
			sortingURL
		).setKeywords(
			_getKeywords()
		).buildPortletURL();
	}

	private String _getKeywords() {
		return ParamUtil.getString(_httpServletRequest, "keywords");
	}

	private String _getOrderByCol() {
		return _searchContainer.getOrderByCol();
	}

	private String _getRedirect() {
		return PortalUtil.escapeRedirect(
			ParamUtil.getString(
				_httpServletRequest, "redirect",
				PortalUtil.getCurrentURL(_httpServletRequest)));
	}

	private boolean _hasAddKBArticlePermission() throws PortalException {
		long kbFolderClassNameId = PortalUtil.getClassNameId(
			KBFolderConstants.getClassName());

		long parentResourceClassNameId = ParamUtil.getLong(
			_httpServletRequest, "parentResourceClassNameId",
			kbFolderClassNameId);

		if (parentResourceClassNameId == kbFolderClassNameId) {
			long parentResourcePrimKey = ParamUtil.getLong(
				_httpServletRequest, "parentResourcePrimKey",
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			return KBFolderPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), parentResourcePrimKey,
				KBActionKeys.ADD_KB_ARTICLE);
		}

		return AdminPermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(), KBActionKeys.ADD_KB_ARTICLE);
	}

	private boolean _hasAddKBFolderPermission() throws PortalException {
		long kbFolderClassNameId = PortalUtil.getClassNameId(
			KBFolderConstants.getClassName());

		long parentResourceClassNameId = ParamUtil.getLong(
			_httpServletRequest, "parentResourceClassNameId",
			kbFolderClassNameId);

		if (parentResourceClassNameId != kbFolderClassNameId) {
			return false;
		}

		long parentResourcePrimKey = ParamUtil.getLong(
			_httpServletRequest, "parentResourcePrimKey",
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return KBFolderPermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(), parentResourcePrimKey,
			KBActionKeys.ADD_KB_FOLDER);
	}

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final PortletConfig _portletConfig;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<Object> _searchContainer;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}