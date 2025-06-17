/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.service.JournalFolderServiceUtil;
import com.liferay.journal.util.comparator.FolderArticleArticleIdComparator;
import com.liferay.journal.util.comparator.FolderArticleModifiedDateComparator;
import com.liferay.journal.util.comparator.FolderArticleTitleComparator;
import com.liferay.journal.web.internal.asset.model.JournalArticleAssetRenderer;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.dao.search.JournalRowChecker;
import com.liferay.journal.web.internal.item.selector.JournalArticleItemSelectorView;
import com.liferay.journal.web.internal.util.JournalSearcherUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryBuilder;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class JournalArticleItemSelectorViewDisplayContext {

	public JournalArticleItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest,
		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
		String itemSelectedEventName,
		JournalArticleItemSelectorView journalArticleItemSelectorView,
		JournalWebConfiguration journalWebConfiguration, Portal portal,
		PortletURL portletURL,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService, boolean search,
		StagingGroupHelper stagingGroupHelper) {

		_httpServletRequest = httpServletRequest;
		_infoItemItemSelectorCriterion = infoItemItemSelectorCriterion;
		_itemSelectedEventName = itemSelectedEventName;
		_journalArticleItemSelectorView = journalArticleItemSelectorView;
		_journalWebConfiguration = journalWebConfiguration;
		_portal = portal;
		_portletURL = portletURL;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
		_search = search;
		_stagingGroupHelper = stagingGroupHelper;

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
			_httpServletRequest, JournalPortletKeys.JOURNAL,
			"item-selector-display-style", "descriptive");

		return _displayStyle;
	}

	public String getGroupCssIcon(long groupId) throws PortalException {
		Group group = GroupServiceUtil.getGroup(groupId);

		return group.getIconCssClass();
	}

	public String getGroupLabel(long groupId, Locale locale)
		throws PortalException {

		Group group = GroupServiceUtil.getGroup(groupId);

		return group.getDescriptiveName(locale);
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public JournalArticle getLatestArticle(JournalArticle journalArticle) {
		JournalArticle latestArticle =
			JournalArticleLocalServiceUtil.fetchLatestArticle(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				WorkflowConstants.STATUS_ANY);

		if (latestArticle != null) {
			return latestArticle;
		}

		return journalArticle;
	}

	public String getPayload(JournalArticle journalArticle)
		throws PortalException {

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			JournalArticle.class.getName(),
			JournalArticleAssetRenderer.getClassPK(journalArticle));

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
			journalArticle.getDDMStructureId());

		return JSONUtil.put(
			"assetEntryId", String.valueOf(assetEntry.getEntryId())
		).put(
			"assetType",
			() -> {
				AssetRendererFactory<?> assetRendererFactory =
					AssetRendererFactoryRegistryUtil.
						getAssetRendererFactoryByClassName(
							JournalArticle.class.getName());

				if (!assetRendererFactory.isSupportsClassTypes()) {
					return assetRendererFactory.getTypeName(
						_themeDisplay.getLocale(), assetEntry.getClassTypeId());
				}

				ClassTypeReader classTypeReader =
					assetRendererFactory.getClassTypeReader();

				ClassType classType = classTypeReader.getClassType(
					assetEntry.getClassTypeId(), _themeDisplay.getLocale());

				return classType.getName();
			}
		).put(
			"className", JournalArticle.class.getName()
		).put(
			"classNameId", _getJournalArticleClassNameId()
		).put(
			"classPK", journalArticle.getResourcePrimKey()
		).put(
			"classTypeId", _getClassTypeId(ddmStructure)
		).put(
			"groupDescriptiveName",
			() -> {
				Group group = GroupLocalServiceUtil.fetchGroup(
					assetEntry.getGroupId());

				return group.getDescriptiveName(_themeDisplay.getLocale());
			}
		).put(
			"subtype", _getSubtype(ddmStructure)
		).put(
			"title", journalArticle.getTitle(_themeDisplay.getLocale(), true)
		).put(
			"titleMap", journalArticle.getTitleMap()
		).put(
			"type",
			ResourceActionsUtil.getModelResource(
				_themeDisplay.getLocale(), JournalArticle.class.getName())
		).toString();
	}

	public List<BreadcrumbEntry> getPortletBreadcrumbEntries() {
		JournalFolder folder = _getFolder();

		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(
						_httpServletRequest, "sites-and-libraries"));
				breadcrumbEntry.setURL(
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"showGroupSelector", true
					).buildString());
			}
		).add(
			breadcrumbEntry -> {
				Group group = GroupLocalServiceUtil.getGroup(_getGroupId());

				breadcrumbEntry.setTitle(
					group.getDescriptiveName(_themeDisplay.getLocale()));

				breadcrumbEntry.setURL(
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"folderId",
						JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID
					).buildString());
			}
		).addAll(
			() -> folder != null,
			() -> {
				List<JournalFolder> ancestorFolders = folder.getAncestors();

				Collections.reverse(ancestorFolders);

				return TransformUtil.transform(
					ancestorFolders,
					ancestorFolder -> BreadcrumbEntryBuilder.setTitle(
						ancestorFolder.getName()
					).setURL(
						PortletURLBuilder.create(
							getPortletURL()
						).setParameter(
							"folderId", ancestorFolder.getFolderId()
						).buildString()
					).build());
			}
		).add(
			() -> folder != null,
			breadcrumbEntry -> {
				JournalFolder unescapedFolder = folder.toUnescapedModel();

				breadcrumbEntry.setTitle(unescapedFolder.getName());
			}
		).build();
	}

	public PortletURL getPortletURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(
				_portletURL,
				_portal.getLiferayPortletResponse(_portletResponse))
		).setParameter(
			"displayStyle", getDisplayStyle()
		).setParameter(
			"groupType", "site"
		).setParameter(
			"scopeGroupType",
			ParamUtil.getBoolean(_httpServletRequest, "scopeGroupType")
		).setParameter(
			"selectedTab", _getTitle(_httpServletRequest.getLocale())
		).buildPortletURL();
	}

	public String getReturnType() {
		return InfoItemItemSelectorReturnType.class.getName();
	}

	public SearchContainer<?> getSearchContainer() throws Exception {
		if (_articleSearchContainer != null) {
			return _articleSearchContainer;
		}

		PortletURL portletURL = PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"folderId", _getFolderId()
		).setParameter(
			"scope", _getScopeFilter()
		).buildPortletURL();

		SearchContainer<Object> articleAndFolderSearchContainer =
			new SearchContainer<>(_portletRequest, portletURL, null, null);

		if (_infoItemItemSelectorCriterion.isMultiSelection()) {
			JournalRowChecker journalRowChecker = new JournalRowChecker(
				JournalArticleLocalServiceUtil.fetchLatestArticle(
					_infoItemItemSelectorCriterion.getRefererClassPK()),
				_portletResponse);

			journalRowChecker.setRememberCheckBoxStateURLRegex(
				StringBundler.concat(
					"^(?!.*", _portletResponse.getNamespace(),
					"redirect).*(folderId=", _getFolderId(), ")"));

			articleAndFolderSearchContainer.setRowChecker(journalRowChecker);
		}

		articleAndFolderSearchContainer.setOrderByCol(_getOrderByCol());
		articleAndFolderSearchContainer.setOrderByType(_getOrderByType());

		if (isSearch()) {
			List<Long> folderIds = new ArrayList<>(1);

			if (_getFolderId() !=
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				folderIds.add(_getFolderId());
			}

			SearchResponse searchResponse =
				JournalSearcherUtil.searchJournalArticlesAndJournalFolders(
					searchContext -> {
						try {
							_populateSearchContext(
								folderIds,
								articleAndFolderSearchContainer.getStart(),
								articleAndFolderSearchContainer.getEnd(),
								searchContext);
						}
						catch (PortalException portalException) {
							throw new RuntimeException(portalException);
						}
					});

			articleAndFolderSearchContainer.setResultsAndTotal(
				() -> JournalSearcherUtil.transformJournalArticleAndFolders(
					searchResponse.getDocuments71()),
				searchResponse.getTotalHits());

			_articleSearchContainer = articleAndFolderSearchContainer;

			return _articleSearchContainer;
		}

		articleAndFolderSearchContainer.setResultsAndTotal(
			() -> {
				OrderByComparator<Object> folderOrderByComparator = null;

				boolean orderByAsc = false;

				if (Objects.equals(_getOrderByType(), "asc")) {
					orderByAsc = true;
				}

				if (Objects.equals(_getOrderByCol(), "id")) {
					folderOrderByComparator =
						FolderArticleArticleIdComparator.getInstance(
							orderByAsc);
				}
				else if (Objects.equals(_getOrderByCol(), "modified-date")) {
					folderOrderByComparator =
						FolderArticleModifiedDateComparator.getInstance(
							orderByAsc);
				}
				else if (Objects.equals(_getOrderByCol(), "title")) {
					folderOrderByComparator =
						FolderArticleTitleComparator.getInstance(orderByAsc);
				}

				return JournalFolderServiceUtil.getFoldersAndArticles(
					_getGroupId(), 0, _getFolderId(), _getDDMStructureId(),
					_infoItemItemSelectorCriterion.getStatus(),
					_themeDisplay.getLocale(),
					new int[] {WorkflowConstants.STATUS_EXPIRED},
					articleAndFolderSearchContainer.getStart(),
					articleAndFolderSearchContainer.getEnd(),
					folderOrderByComparator);
			},
			JournalFolderServiceUtil.getFoldersAndArticlesCount(
				_getGroupId(), 0, _getFolderId(), _getDDMStructureId(),
				new int[] {WorkflowConstants.STATUS_EXPIRED},
				_infoItemItemSelectorCriterion.getStatus()));

		_articleSearchContainer = articleAndFolderSearchContainer;

		return _articleSearchContainer;
	}

	public int getStatus() {
		return _infoItemItemSelectorCriterion.getStatus();
	}

	public boolean hasGuestViewPermission(JournalArticle journalArticle)
		throws PortalException {

		if (_guestRole == null) {
			_guestRole = _roleLocalService.getRole(
				journalArticle.getCompanyId(), RoleConstants.GUEST);
		}

		return _resourcePermissionLocalService.hasResourcePermission(
			journalArticle.getCompanyId(), JournalArticle.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(journalArticle.getResourcePrimKey()),
			_guestRole.getRoleId(), ActionKeys.VIEW);
	}

	public boolean isMultiSelection() {
		return _infoItemItemSelectorCriterion.isMultiSelection();
	}

	public boolean isRefererArticle(JournalArticle journalArticle) {
		if (_infoItemItemSelectorCriterion.getRefererClassPK() ==
				journalArticle.getResourcePrimKey()) {

			return true;
		}

		return false;
	}

	public boolean isSearch() {
		if (_isEverywhereScopeFilter()) {
			return true;
		}

		return _search;
	}

	public boolean isSearchEverywhere() {
		if (_searchEverywhere != null) {
			return _searchEverywhere;
		}

		if (Objects.equals(
				ParamUtil.getString(_httpServletRequest, "scope"),
				"everywhere")) {

			_searchEverywhere = true;
		}
		else {
			_searchEverywhere = false;
		}

		return _searchEverywhere;
	}

	public boolean isShowBreadcrumb() {
		Group group = _themeDisplay.getScopeGroup();

		return !group.isLayout();
	}

	public boolean showArticleId() {
		if (!_journalWebConfiguration.journalArticleForceAutogenerateId() ||
			_journalWebConfiguration.journalArticleShowId()) {

			return true;
		}

		return false;
	}

	private long _getClassTypeId(DDMStructure ddmStructure) {
		if (ddmStructure == null) {
			return 0;
		}

		return ddmStructure.getStructureId();
	}

	private long _getDDMStructureId() {
		if (_ddmStructureId != null) {
			return _ddmStructureId;
		}

		DDMStructure ddmStructure = null;

		long ddmStructureId = ParamUtil.getLong(
			_httpServletRequest, "ddmStructureId",
			GetterUtil.getLong(
				_infoItemItemSelectorCriterion.getItemSubtype()));

		if (ddmStructureId > 0) {
			ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
				ddmStructureId);
		}

		if ((ddmStructure == null) &&
			Validator.isNotNull(
				_infoItemItemSelectorCriterion.getItemSubtype())) {

			ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
				_getGroupId(), _getJournalArticleClassNameId(),
				_infoItemItemSelectorCriterion.getItemSubtype(), true);
		}

		if (ddmStructure != null) {
			_ddmStructureId = ddmStructure.getStructureId();
		}
		else {
			_ddmStructureId = 0L;
		}

		return _ddmStructureId;
	}

	private JournalFolder _getFolder() {
		if (_folder != null) {
			return _folder;
		}

		_folder = JournalFolderLocalServiceUtil.fetchFolder(
			ParamUtil.getLong(_httpServletRequest, "folderId"));

		return _folder;
	}

	private long _getFolderId() {
		if (_folderId != null) {
			return _folderId;
		}

		_folderId = BeanParamUtil.getLong(
			_getFolder(), _httpServletRequest, "folderId",
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return _folderId;
	}

	private long _getGroupId() {
		return ParamUtil.getLong(
			_portletRequest, "groupId", _getStagingAwareGroupId());
	}

	private long[] _getGroupIds() throws PortalException {
		if (_isEverywhereScopeFilter()) {
			return SiteConnectedGroupGroupProviderUtil.
				getCurrentAndAncestorSiteAndDepotGroupIds(
					_getStagingAwareGroupId());
		}

		return new long[] {_getStagingAwareGroupId()};
	}

	private long _getJournalArticleClassNameId() {
		if (_journalArticleClassNameId != null) {
			return _journalArticleClassNameId;
		}

		_journalArticleClassNameId = _portal.getClassNameId(
			JournalArticle.class.getName());

		return _journalArticleClassNameId;
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
			_httpServletRequest, JournalPortletKeys.JOURNAL,
			"item-selector-order-by-col", defaultOrderByCol);

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (_orderByType != null) {
			return _orderByType;
		}

		if (Objects.equals(_getOrderByCol(), "relevance")) {
			return "desc";
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, JournalPortletKeys.JOURNAL,
			"item-selector-order-by-type", "asc");

		return _orderByType;
	}

	private String _getScopeFilter() {
		if (_scope != null) {
			return _scope;
		}

		_scope = ParamUtil.getString(_httpServletRequest, "scope");

		return _scope;
	}

	private long _getStagingAwareGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = _stagingGroupHelper.getStagedPortletGroupId(
			_themeDisplay.getScopeGroupId(), JournalPortletKeys.JOURNAL);

		return _groupId;
	}

	private String _getSubtype(DDMStructure ddmStructure)
		throws PortalException {

		if (ddmStructure == null) {
			return StringPool.BLANK;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				JournalArticle.class.getName());

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		ClassType classType = classTypeReader.getClassType(
			ddmStructure.getStructureId(), _themeDisplay.getLocale());

		return classType.getName();
	}

	private String _getTitle(Locale locale) {
		return _journalArticleItemSelectorView.getTitle(locale);
	}

	private boolean _isEverywhereScopeFilter() {
		return Objects.equals(
			ParamUtil.getString(_httpServletRequest, "scope"), "everywhere");
	}

	private void _populateSearchContext(
			List<Long> folderIds, int start, int end,
			SearchContext searchContext)
		throws PortalException {

		searchContext.setAndSearch(false);
		searchContext.setAttribute(
			Field.CLASS_NAME_ID, JournalArticleConstants.CLASS_NAME_ID_DEFAULT);
		searchContext.setAttribute(
			Field.STATUS, _infoItemItemSelectorCriterion.getStatus());
		searchContext.setAttribute("head", Boolean.TRUE);
		searchContext.setAttribute("latest", Boolean.TRUE);
		searchContext.setAttribute("showNonindexable", Boolean.TRUE);

		if (_getDDMStructureId() > 0) {
			searchContext.setClassTypeIds(new long[] {_getDDMStructureId()});
		}

		searchContext.setCompanyId(_themeDisplay.getCompanyId());
		searchContext.setEnd(end);
		searchContext.setFolderIds(folderIds);
		searchContext.setGroupIds(_getGroupIds());
		searchContext.setIncludeInternalAssetCategories(true);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		boolean orderByAsc = false;

		if (Objects.equals(_getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		Sort sort = null;

		if (Objects.equals(_getOrderByCol(), "id")) {
			sort = new Sort(
				Field.getSortableFieldName(Field.ARTICLE_ID), Sort.STRING_TYPE,
				!orderByAsc);
		}
		else if (Objects.equals(_getOrderByCol(), "modified-date")) {
			sort = new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, !orderByAsc);
		}
		else if (Objects.equals(_getOrderByCol(), "relevance")) {
			sort = new Sort(null, Sort.SCORE_TYPE, false);
		}
		else if (Objects.equals(_getOrderByCol(), "title")) {
			sort = new Sort(
				Field.getSortableFieldName(
					"localized_title_" + _themeDisplay.getLanguageId()),
				!orderByAsc);
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			searchContext.setAttribute(Field.ARTICLE_ID, keywords);
			searchContext.setAttribute(Field.CONTENT, keywords);
			searchContext.setAttribute(Field.DESCRIPTION, keywords);
			searchContext.setAttribute(Field.TITLE, keywords);
			searchContext.setAttribute(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"expandoAttributes", keywords
				).put(
					"keywords", keywords
				).build());
			searchContext.setKeywords(keywords);
		}
		else {
			searchContext.setAttribute(
				SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH,
				Boolean.TRUE);
		}
	}

	private SearchContainer<?> _articleSearchContainer;
	private Long _ddmStructureId;
	private String _displayStyle;
	private JournalFolder _folder;
	private Long _folderId;
	private Long _groupId;
	private Role _guestRole;
	private final HttpServletRequest _httpServletRequest;
	private final InfoItemItemSelectorCriterion _infoItemItemSelectorCriterion;
	private final String _itemSelectedEventName;
	private Long _journalArticleClassNameId;
	private final JournalArticleItemSelectorView
		_journalArticleItemSelectorView;
	private final JournalWebConfiguration _journalWebConfiguration;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final Portal _portal;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;
	private final PortletURL _portletURL;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;
	private String _scope;
	private final boolean _search;
	private Boolean _searchEverywhere;
	private final StagingGroupHelper _stagingGroupHelper;
	private final ThemeDisplay _themeDisplay;

}