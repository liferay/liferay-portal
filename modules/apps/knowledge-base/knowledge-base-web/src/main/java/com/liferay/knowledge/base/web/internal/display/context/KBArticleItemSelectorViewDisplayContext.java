/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.knowledge.base.service.KBArticleServiceUtil;
import com.liferay.knowledge.base.service.KBFolderLocalServiceUtil;
import com.liferay.knowledge.base.service.KBFolderServiceUtil;
import com.liferay.knowledge.base.util.comparator.KBObjectsModifiedDateComparator;
import com.liferay.knowledge.base.util.comparator.KBObjectsPriorityComparator;
import com.liferay.knowledge.base.util.comparator.KBObjectsTitleComparator;
import com.liferay.knowledge.base.util.comparator.KBObjectsViewCountComparator;
import com.liferay.knowledge.base.web.internal.search.KBSearcher;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryBuilder;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Alicia García
 */
public class KBArticleItemSelectorViewDisplayContext {

	public KBArticleItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest,
		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
		String itemSelectedEventName, PortletURL portletURL, boolean search) {

		_httpServletRequest = httpServletRequest;
		_infoItemItemSelectorCriterion = infoItemItemSelectorCriterion;
		_itemSelectedEventName = itemSelectedEventName;
		_portletURL = portletURL;
		_search = search;

		_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
		_portletResponse = (RenderResponse)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest, KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
			"item-selector-display-style", "descriptive");

		return _displayStyle;
	}

	public String getKBArticleDataReturnType() {
		return InfoItemItemSelectorReturnType.class.getName();
	}

	public String getKBArticleDataValue(KBArticle kbArticle) {
		return JSONUtil.put(
			"className", KBArticle.class.getName()
		).put(
			"classNameId", PortalUtil.getClassNameId(KBArticle.class.getName())
		).put(
			"classPK", kbArticle.getResourcePrimKey()
		).put(
			"externalReferenceCode", kbArticle.getExternalReferenceCode()
		).put(
			"title", kbArticle.getTitle()
		).put(
			"type",
			ResourceActionsUtil.getModelResource(
				_themeDisplay.getLocale(), KBArticle.class.getName())
		).toString();
	}

	public String getKBArticleRowURL(KBArticle kbArticle)
		throws PortletException {

		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"groupId", kbArticle.getGroupId()
		).setParameter(
			"kbFolderId", kbArticle.getResourcePrimKey()
		).setParameter(
			"parentResourceClassNameId", kbArticle.getClassNameId()
		).setParameter(
			"parentResourcePrimKey", kbArticle.getResourcePrimKey()
		).buildString();
	}

	public String getKBFolderRowURL(long groupId, long kbFolderId)
		throws PortletException {

		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"groupId", groupId
		).setParameter(
			"kbFolderId", kbFolderId
		).buildString();
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public KBArticle getLatestArticle(KBArticle kbArticle) {
		KBArticle latestArticle =
			KBArticleLocalServiceUtil.fetchLatestKBArticle(
				kbArticle.getGroupId(), kbArticle.getResourcePrimKey());

		if (latestArticle != null) {
			return latestArticle;
		}

		return kbArticle;
	}

	public List<BreadcrumbEntry> getPortletBreadcrumbEntries()
		throws Exception {

		KBFolder kbFolder = _getKBFolder();

		return BreadcrumbEntryListBuilder.add(
			BreadcrumbEntryBuilder.setTitle(
				LanguageUtil.get(_httpServletRequest, "sites-and-libraries")
			).setURL(
				PortletURLBuilder.create(
					getPortletURL()
				).setParameter(
					"groupType", "site"
				).setParameter(
					"showGroupSelector", true
				).buildString()
			).build()
		).add(
			breadcrumbEntry -> {
				Group group = GroupLocalServiceUtil.getGroup(_getGroupId());

				breadcrumbEntry.setTitle(
					group.getDescriptiveName(_themeDisplay.getLocale()));

				breadcrumbEntry.setURL(
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"kbFolderId", KBFolderConstants.DEFAULT_PARENT_FOLDER_ID
					).buildString());
			}
		).addAll(
			() -> _getParentResourcePrimKey() != 0,
			() -> {
				KBArticle kbArticle = KBArticleServiceUtil.getLatestKBArticle(
					_getParentResourcePrimKey(), WorkflowConstants.STATUS_ANY);

				List<BreadcrumbEntry> breadcrumbEntries =
					_getFolderBreadcrumbEntry(
						kbArticle.getKbFolderId(),
						PortletURLBuilder.create(
							getPortletURL()
						).setParameter(
							"kbFolderId",
							KBFolderConstants.DEFAULT_PARENT_FOLDER_ID
						).buildPortletURL());

				breadcrumbEntries.add(
					BreadcrumbEntryBuilder.setTitle(
						LanguageUtil.get(
							_httpServletRequest, kbArticle.getTitle())
					).setURL(
						PortletURLBuilder.create(
							getPortletURL()
						).setParameter(
							"kbFolderId", kbArticle.getKbFolderId()
						).buildString()
					).build());

				return breadcrumbEntries;
			}
		).addAll(
			() -> (_getParentResourcePrimKey() == 0) && (kbFolder != null),
			() -> _getFolderBreadcrumbEntry(
				kbFolder.getKbFolderId(),
				PortletURLBuilder.create(
					getPortletURL()
				).setParameter(
					"kbFolderId", KBFolderConstants.DEFAULT_PARENT_FOLDER_ID
				).buildPortletURL())
		).build();
	}

	public PortletURL getPortletURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(
				_portletURL,
				PortalUtil.getLiferayPortletResponse(_portletResponse))
		).setParameter(
			"displayStyle", getDisplayStyle()
		).buildPortletURL();
	}

	public SearchContainer<?> getSearchContainer() throws Exception {
		if (_articleSearchContainer != null) {
			return _articleSearchContainer;
		}

		PortletURL portletURL = PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"kbFolderId", _getKBFolderId()
		).buildPortletURL();

		SearchContainer<Object> articleAndFolderSearchContainer =
			new SearchContainer<>(_portletRequest, portletURL, null, null);

		articleAndFolderSearchContainer.setOrderByCol(_getOrderByCol());
		articleAndFolderSearchContainer.setOrderByType(_getOrderByType());

		if (isSearch()) {
			List<Long> kbFolderIds = new ArrayList<>(1);

			if (_getKBFolderId() !=
					KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				kbFolderIds.add(_getKBFolderId());
			}

			boolean orderByAsc = false;

			if (Objects.equals(_getOrderByType(), "asc")) {
				orderByAsc = true;
			}

			Sort sort = null;

			if (Objects.equals(_getOrderByCol(), "id")) {
				sort = new Sort(
					Field.getSortableFieldName("kbArticleId"), Sort.STRING_TYPE,
					!orderByAsc);
			}
			else if (Objects.equals(_getOrderByCol(), "modified-date")) {
				sort = new Sort(
					Field.MODIFIED_DATE, Sort.LONG_TYPE, !orderByAsc);
			}
			else if (Objects.equals(_getOrderByCol(), "relevance")) {
				sort = new Sort(null, Sort.SCORE_TYPE, false);
			}
			else if (Objects.equals(_getOrderByCol(), "title")) {
				sort = new Sort(Field.TITLE, Sort.STRING_TYPE, !orderByAsc);
			}

			Indexer<?> indexer = KBSearcher.getInstance();

			SearchContext searchContext = buildSearchContext(
				kbFolderIds, articleAndFolderSearchContainer.getStart(),
				articleAndFolderSearchContainer.getEnd(), sort);

			Hits hits = indexer.search(searchContext);

			List<Object> results = new ArrayList<>();

			Document[] documents = hits.getDocs();

			for (Document document : documents) {
				String className = document.get(Field.ENTRY_CLASS_NAME);
				long classPK = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				if (className.equals(KBArticle.class.getName())) {
					results.add(
						KBArticleLocalServiceUtil.fetchLatestKBArticle(
							classPK, WorkflowConstants.STATUS_APPROVED));
				}
				else if (className.equals(KBFolder.class.getName())) {
					results.add(KBFolderLocalServiceUtil.getKBFolder(classPK));
				}
			}

			articleAndFolderSearchContainer.setResultsAndTotal(
				() -> results, hits.getLength());
		}
		else {
			articleAndFolderSearchContainer.setResultsAndTotal(
				() -> {
					OrderByComparator<Object> kbObjectOrderByComparator = null;

					boolean orderByAsc = false;

					if (Objects.equals(_getOrderByType(), "asc")) {
						orderByAsc = true;
					}

					if (Objects.equals(_getOrderByCol(), "priority")) {
						kbObjectOrderByComparator =
							KBObjectsPriorityComparator.getInstance(orderByAsc);
					}
					else if (Objects.equals(
								_getOrderByCol(), "modified-date")) {

						kbObjectOrderByComparator =
							new KBObjectsModifiedDateComparator<>(orderByAsc);
					}
					else if (Objects.equals(_getOrderByCol(), "title")) {
						kbObjectOrderByComparator =
							new KBObjectsTitleComparator<>(orderByAsc);
					}
					else if (Objects.equals(_getOrderByCol(), "view-count")) {
						kbObjectOrderByComparator =
							KBObjectsViewCountComparator.getInstance(
								orderByAsc);
					}

					return KBFolderServiceUtil.getKBFoldersAndKBArticles(
						_getGroupId(), _getKBFolderId(),
						_infoItemItemSelectorCriterion.getStatus(),
						articleAndFolderSearchContainer.getStart(),
						articleAndFolderSearchContainer.getEnd(),
						kbObjectOrderByComparator);
				},
				KBFolderServiceUtil.getKBFoldersAndKBArticlesCount(
					_getGroupId(), _getKBFolderId(),
					_infoItemItemSelectorCriterion.getStatus()));
		}

		_articleSearchContainer = articleAndFolderSearchContainer;

		return _articleSearchContainer;
	}

	public boolean isSearch() {
		return _search;
	}

	protected SearchContext buildSearchContext(
		List<Long> kbFolderIds, int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(false);
		searchContext.setAttribute(Field.CONTENT, getKeywords());
		searchContext.setAttribute(Field.DESCRIPTION, getKeywords());
		searchContext.setAttribute(
			Field.STATUS, _infoItemItemSelectorCriterion.getStatus());
		searchContext.setAttribute(Field.TITLE, getKeywords());
		searchContext.setAttribute("head", Boolean.TRUE);
		searchContext.setAttribute("kbArticleId", getKeywords());
		searchContext.setAttribute("latest", Boolean.TRUE);
		searchContext.setAttribute(
			"params",
			LinkedHashMapBuilder.<String, Object>put(
				"expandoAttributes", getKeywords()
			).put(
				"keywords", getKeywords()
			).build());
		searchContext.setAttribute("showNonindexable", Boolean.TRUE);
		searchContext.setCompanyId(_themeDisplay.getCompanyId());
		searchContext.setEnd(end);
		searchContext.setFolderIds(kbFolderIds);
		searchContext.setGroupIds(_getGroupIds());
		searchContext.setIncludeInternalAssetCategories(true);
		searchContext.setKeywords(getKeywords());

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		return searchContext;
	}

	private List<BreadcrumbEntry> _getFolderBreadcrumbEntry(
			long resourcePrimKey, PortletURL portletURL)
		throws Exception {

		List<BreadcrumbEntry> breadcrumbEntries = new ArrayList<>();

		if (resourcePrimKey == KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			BreadcrumbEntry folderBreadcrumbEntry = new BreadcrumbEntry();

			folderBreadcrumbEntry.setTitle(
				LanguageUtil.get(_httpServletRequest, "home"));
			folderBreadcrumbEntry.setURL(portletURL.toString());

			breadcrumbEntries.add(folderBreadcrumbEntry);

			return breadcrumbEntries;
		}

		KBFolder kbFolder = KBFolderServiceUtil.getKBFolder(resourcePrimKey);

		breadcrumbEntries.addAll(
			_getFolderBreadcrumbEntry(
				kbFolder.getParentKBFolderId(), portletURL));

		BreadcrumbEntry folderBreadcrumbEntry = new BreadcrumbEntry();

		KBFolder unescapedFolder = kbFolder.toUnescapedModel();

		folderBreadcrumbEntry.setTitle(unescapedFolder.getName());

		portletURL.setParameter(
			"kbFolderId", String.valueOf(kbFolder.getKbFolderId()));

		folderBreadcrumbEntry.setURL(portletURL.toString());

		breadcrumbEntries.add(folderBreadcrumbEntry);

		return breadcrumbEntries;
	}

	private long _getGroupId() {
		return ParamUtil.getLong(
			_portletRequest, "groupId", _themeDisplay.getScopeGroupId());
	}

	private long[] _getGroupIds() {
		return PortalUtil.getCurrentAndAncestorSiteGroupIds(
			_themeDisplay.getScopeGroupId());
	}

	private KBFolder _getKBFolder() {
		if (_kbFolder != null) {
			return _kbFolder;
		}

		_kbFolder = KBFolderLocalServiceUtil.fetchKBFolder(
			ParamUtil.getLong(_httpServletRequest, "kbFolderId"));

		return _kbFolder;
	}

	private long _getKBFolderId() {
		if (_kbFolderId != null) {
			return _kbFolderId;
		}

		_kbFolderId = BeanParamUtil.getLong(
			_getKBFolder(), _httpServletRequest, "kbFolderId",
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return _kbFolderId;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		String defaultOrderByCol = "modified-date";

		if (isSearch()) {
			defaultOrderByCol = "relevance";
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
			"item-selector-order-by-col", defaultOrderByCol);

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (_orderByType != null) {
			return _orderByType;
		}

		if (Objects.equals(_getOrderByCol(), "priority")) {
			return "desc";
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
			"item-selector-order-by-type", "asc");

		return _orderByType;
	}

	private long _getParentResourcePrimKey() {
		if (_parentResourcePrimKey != null) {
			return _parentResourcePrimKey;
		}

		_parentResourcePrimKey = ParamUtil.getLong(
			_httpServletRequest, "parentResourcePrimKey");

		return _parentResourcePrimKey;
	}

	private SearchContainer<?> _articleSearchContainer;
	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final InfoItemItemSelectorCriterion _infoItemItemSelectorCriterion;
	private final String _itemSelectedEventName;
	private KBFolder _kbFolder;
	private Long _kbFolderId;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private Long _parentResourcePrimKey;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;
	private final PortletURL _portletURL;
	private final boolean _search;
	private final ThemeDisplay _themeDisplay;

}