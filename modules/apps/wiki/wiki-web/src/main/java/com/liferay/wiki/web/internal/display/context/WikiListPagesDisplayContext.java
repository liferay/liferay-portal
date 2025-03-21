/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.display.context;

import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.configuration.WikiGroupServiceOverriddenConfiguration;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageResource;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.service.WikiPageResourceLocalServiceUtil;
import com.liferay.wiki.service.WikiPageServiceUtil;
import com.liferay.wiki.util.comparator.PageVersionComparator;
import com.liferay.wiki.web.internal.constants.WikiUIItemKeys;
import com.liferay.wiki.web.internal.display.context.helper.WikiRequestHelper;
import com.liferay.wiki.web.internal.security.permission.resource.WikiNodePermission;
import com.liferay.wiki.web.internal.security.permission.resource.WikiPagePermission;
import com.liferay.wiki.web.internal.util.WikiPortletUtil;
import com.liferay.wiki.web.internal.util.WikiWebComponentProvider;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Iván Zaera
 */
public class WikiListPagesDisplayContext {

	public WikiListPagesDisplayContext(
		HttpServletRequest httpServletRequest, TrashHelper trashHelper,
		WikiNode wikiNode) {

		_httpServletRequest = httpServletRequest;
		_trashHelper = trashHelper;
		_wikiNode = wikiNode;

		_wikiRequestHelper = new WikiRequestHelper(httpServletRequest);
	}

	public List<DropdownItem> getActionDropdownItems(WikiPage wikiPage) {
		return DropdownItemListBuilder.add(
			() -> WikiPagePermission.contains(
				_wikiRequestHelper.getPermissionChecker(), wikiPage,
				ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_wikiRequestHelper.getLiferayPortletResponse()
					).setMVCRenderCommandName(
						"/wiki/edit_page"
					).setParameter(
						"nodeId", wikiPage.getNodeId()
					).setParameter(
						"title", HtmlUtil.unescape(wikiPage.getTitle())
					).buildString());
				dropdownItem.setIcon("pencil");
				dropdownItem.setKey(WikiUIItemKeys.EDIT);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			}
		).add(
			() -> WikiPagePermission.contains(
				_wikiRequestHelper.getPermissionChecker(), wikiPage,
				ActionKeys.PERMISSIONS),
			dropdownItem -> {
				dropdownItem.putData("action", "permissions");
				dropdownItem.putData(
					"permissionsURL",
					PermissionsURLTag.doTag(
						null, WikiPage.class.getName(), wikiPage.getTitle(),
						null, String.valueOf(wikiPage.getResourcePrimKey()),
						LiferayWindowState.POP_UP.toString(), null,
						_httpServletRequest));
				dropdownItem.setIcon("password-policies");
				dropdownItem.setKey(WikiUIItemKeys.PERMISSIONS);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "permissions"));
			}
		).add(
			() -> _isCopyPasteEnabled(wikiPage),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_wikiRequestHelper.getLiferayPortletResponse()
					).setMVCRenderCommandName(
						"/wiki/edit_page"
					).setRedirect(
						_wikiRequestHelper.getCurrentURL()
					).setParameter(
						"editTitle", "1"
					).setParameter(
						"nodeId", wikiPage.getNodeId()
					).setParameter(
						"templateNodeId", wikiPage.getNodeId()
					).setParameter(
						"templateTitle", HtmlUtil.unescape(wikiPage.getTitle())
					).setParameter(
						"title", StringPool.BLANK
					).buildString());
				dropdownItem.setIcon("copy");
				dropdownItem.setKey(WikiUIItemKeys.COPY);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "make-a-copy"));
			}
		).add(
			() -> _isCopyPasteEnabled(wikiPage),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_wikiRequestHelper.getLiferayPortletResponse()
					).setMVCRenderCommandName(
						"/wiki/move_page"
					).setRedirect(
						_wikiRequestHelper.getCurrentURL()
					).setParameter(
						"nodeId", wikiPage.getNodeId()
					).setParameter(
						"title", HtmlUtil.unescape(wikiPage.getTitle())
					).buildString());
				dropdownItem.setIcon("move-folder");
				dropdownItem.setKey(WikiUIItemKeys.MOVE);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "move"));
			}
		).add(
			() ->
				Validator.isNotNull(wikiPage.getContent()) &&
				WikiNodePermission.contains(
					_wikiRequestHelper.getPermissionChecker(),
					wikiPage.getNodeId(), ActionKeys.ADD_PAGE),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_wikiRequestHelper.getLiferayPortletResponse()
					).setMVCRenderCommandName(
						"/wiki/edit_page"
					).setRedirect(
						_wikiRequestHelper.getCurrentURL()
					).setParameter(
						"editTitle", "1"
					).setParameter(
						"nodeId", wikiPage.getNodeId()
					).setParameter(
						"parentTitle", wikiPage.getTitle()
					).setParameter(
						"title", StringPool.BLANK
					).buildString());
				dropdownItem.setIcon("wiki-page");
				dropdownItem.setKey(WikiUIItemKeys.ADD_CHILD_PAGE);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "add-child-page"));
			}
		).add(
			() -> _hasSubscribePermission(wikiPage) && _isSubscribed(wikiPage),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createActionURL(
						_wikiRequestHelper.getLiferayPortletResponse()
					).setActionName(
						"/wiki/edit_page"
					).setCMD(
						Constants.UNSUBSCRIBE
					).setRedirect(
						_wikiRequestHelper.getCurrentURL()
					).setParameter(
						"nodeId", wikiPage.getNodeId()
					).setParameter(
						"title", HtmlUtil.unescape(wikiPage.getTitle())
					).buildString());
				dropdownItem.setIcon("bell-off");
				dropdownItem.setKey(WikiUIItemKeys.UNSUBSCRIBE);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "unsubscribe"));
			}
		).add(
			() -> _hasSubscribePermission(wikiPage) && !_isSubscribed(wikiPage),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createActionURL(
						_wikiRequestHelper.getLiferayPortletResponse()
					).setActionName(
						"/wiki/edit_page"
					).setCMD(
						Constants.SUBSCRIBE
					).setRedirect(
						_wikiRequestHelper.getCurrentURL()
					).setParameter(
						"nodeId", wikiPage.getNodeId()
					).setParameter(
						"title", HtmlUtil.unescape(wikiPage.getTitle())
					).buildString());
				dropdownItem.setIcon("bell-on");
				dropdownItem.setKey(WikiUIItemKeys.SUBSCRIBE);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "subscribe"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "print");
				dropdownItem.putData(
					"printURL",
					PortletURLBuilder.createRenderURL(
						_wikiRequestHelper.getLiferayPortletResponse()
					).setMVCRenderCommandName(
						"/wiki/view"
					).setParameter(
						"nodeName",
						() -> {
							WikiNode wikiNode = wikiPage.getNode();

							return wikiNode.getName();
						}
					).setParameter(
						"title", wikiPage.getTitle()
					).setParameter(
						"viewMode", Constants.PRINT
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setIcon("print");
				dropdownItem.setKey(WikiUIItemKeys.PRINT);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "print"));
			}
		).add(
			() -> WikiPagePermission.contains(
				_wikiRequestHelper.getPermissionChecker(), wikiPage,
				ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.putData("action", "delete");
				dropdownItem.putData(
					"deleteURL",
					PortletURLBuilder.createActionURL(
						_wikiRequestHelper.getLiferayPortletResponse()
					).setActionName(
						"/wiki/edit_page"
					).setCMD(
						() -> {
							if (!wikiPage.isDraft() &&
								_trashHelper.isTrashEnabled(
									_wikiRequestHelper.getScopeGroupId())) {

								return Constants.MOVE_TO_TRASH;
							}

							return Constants.DELETE;
						}
					).setRedirect(
						_wikiRequestHelper.getCurrentURL()
					).setParameter(
						"nodeId", wikiPage.getNodeId()
					).setParameter(
						"title", HtmlUtil.unescape(wikiPage.getTitle())
					).buildString());
				dropdownItem.putData(
					"trashEnabled",
					String.valueOf(
						!wikiPage.isDraft() &&
						_trashHelper.isTrashEnabled(
							_wikiRequestHelper.getScopeGroupId())));
				dropdownItem.setIcon("trash");
				dropdownItem.setKey(WikiUIItemKeys.DELETE);

				String label = "delete";

				if (wikiPage.isDraft()) {
					label = "discard-draft";
				}

				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, label));
			}
		).build();
	}

	public String getEmptyResultsMessage() {
		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			return LanguageUtil.format(
				_httpServletRequest,
				"no-pages-were-found-that-matched-the-keywords-x",
				"<strong>" + HtmlUtil.escape(keywords) + "</strong>", false);
		}

		String navigation = ParamUtil.getString(
			_httpServletRequest, "navigation");

		if (navigation.equals("categorized-pages")) {
			return "there-are-no-pages-with-this-category";
		}
		else if (navigation.equals("draft-pages")) {
			return "there-are-no-drafts";
		}
		else if (navigation.equals("frontpage")) {
			WikiWebComponentProvider wikiWebComponentProvider =
				WikiWebComponentProvider.getWikiWebComponentProvider();

			WikiGroupServiceConfiguration wikiGroupServiceConfiguration =
				wikiWebComponentProvider.getWikiGroupServiceConfiguration();

			return LanguageUtil.format(
				_httpServletRequest, "there-is-no-x",
				new String[] {wikiGroupServiceConfiguration.frontPageName()},
				false);
		}
		else if (navigation.equals("incoming-links")) {
			return "there-are-no-pages-that-link-to-this-page";
		}
		else if (navigation.equals("orphan-pages")) {
			return "there-are-no-orphan-pages";
		}
		else if (navigation.equals("outgoing-links")) {
			return "this-page-has-no-links";
		}
		else if (navigation.equals("pending-pages")) {
			return "there-are-no-pages-submitted-by-you-pending-approval";
		}
		else if (navigation.equals("recent-changes")) {
			return "there-are-no-recent-changes";
		}
		else if (navigation.equals("tagged-pages")) {
			return "there-are-no-pages-with-this-tag";
		}

		return "there-are-no-pages";
	}

	public void populateResultsAndTotal(
			SearchContainer<WikiPage> searchContainer)
		throws PortalException {

		WikiPage page = (WikiPage)_httpServletRequest.getAttribute(
			WikiWebKeys.WIKI_PAGE);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "all-pages");

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			Indexer<WikiPage> indexer = IndexerRegistryUtil.getIndexer(
				WikiPage.class);

			SearchContext searchContext = SearchContextFactory.getInstance(
				_httpServletRequest);

			searchContext.setAttribute("paginationType", "more");
			searchContext.setEnd(searchContainer.getEnd());
			searchContext.setIncludeAttachments(true);
			searchContext.setIncludeDiscussions(true);
			searchContext.setIncludeInternalAssetCategories(true);
			searchContext.setKeywords(keywords);
			searchContext.setNodeIds(new long[] {_wikiNode.getNodeId()});
			searchContext.setStart(searchContainer.getStart());

			Hits hits = indexer.search(searchContext);

			List<WikiPage> pages = TransformUtil.transform(
				SearchResultUtil.getSearchResults(
					hits, themeDisplay.getLocale()),
				searchResult -> WikiPageLocalServiceUtil.getPage(
					searchResult.getClassPK()));

			searchContainer.setResultsAndTotal(() -> pages, hits.getLength());
		}
		else if (navigation.equals("all-pages")) {
			searchContainer.setResultsAndTotal(
				() -> {
					PermissionChecker permissionChecker =
						_wikiRequestHelper.getPermissionChecker();

					return TransformUtil.transform(
						WikiPageServiceUtil.getPages(
							themeDisplay.getScopeGroupId(),
							_wikiNode.getNodeId(), true,
							themeDisplay.getUserId(), true,
							WorkflowConstants.STATUS_APPROVED,
							searchContainer.getStart(),
							searchContainer.getEnd(),
							WikiPortletUtil.getPageOrderByComparator(
								searchContainer.getOrderByCol(),
								searchContainer.getOrderByType())),
						curPage -> {
							boolean contentReviewer =
								permissionChecker.isContentReviewer(
									_wikiRequestHelper.getCompanyId(),
									_wikiRequestHelper.getScopeGroupId());

							if (!contentReviewer &&
								!WikiPagePermission.contains(
									permissionChecker, curPage,
									ActionKeys.UPDATE)) {

								return curPage;
							}

							WikiPage lastPage = null;

							try {
								lastPage = WikiPageLocalServiceUtil.getPage(
									curPage.getResourcePrimKey(), false);
							}
							catch (PortalException portalException) {

								// LPS-52675

								if (_log.isDebugEnabled()) {
									_log.debug(portalException);
								}
							}

							if ((lastPage != null) &&
								(curPage.getVersion() <
									lastPage.getVersion()) &&
								(contentReviewer ||
								 (lastPage.getStatusByUserId() ==
									 permissionChecker.getUserId()))) {

								return lastPage;
							}

							return curPage;
						});
				},
				WikiPageServiceUtil.getPagesCount(
					themeDisplay.getScopeGroupId(), _wikiNode.getNodeId(), true,
					themeDisplay.getUserId(), true,
					WorkflowConstants.STATUS_APPROVED));
		}
		else if (navigation.equals("categorized-pages") ||
				 navigation.equals("tagged-pages")) {

			AssetEntryQuery assetEntryQuery = new AssetEntryQuery(
				WikiPage.class.getName(), searchContainer);

			assetEntryQuery.setEnablePermissions(true);

			searchContainer.setResultsAndTotal(
				() -> {
					assetEntryQuery.setEnd(searchContainer.getEnd());
					assetEntryQuery.setStart(searchContainer.getStart());

					return TransformUtil.transform(
						AssetEntryServiceUtil.getEntries(assetEntryQuery),
						assetEntry -> {
							WikiPageResource pageResource =
								WikiPageResourceLocalServiceUtil.
									getPageResource(assetEntry.getClassPK());

							return WikiPageLocalServiceUtil.getPage(
								pageResource.getNodeId(),
								pageResource.getTitle());
						});
				},
				AssetEntryServiceUtil.getEntriesCount(assetEntryQuery));
		}
		else if (navigation.equals("draft-pages") ||
				 navigation.equals("pending-pages")) {

			if (!themeDisplay.isSignedIn()) {
				return;
			}

			long draftUserId = themeDisplay.getUserId();

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			if (permissionChecker.isContentReviewer(
					themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId())) {

				draftUserId = 0;
			}

			long wikiPageDraftUserId = draftUserId;

			int status = WorkflowConstants.STATUS_DRAFT;

			if (navigation.equals("pending-pages")) {
				status = WorkflowConstants.STATUS_PENDING;
			}

			int wikiPageStatus = status;

			if (wikiPageDraftUserId == 0) {
				searchContainer.setResultsAndTotal(
					() -> WikiPageServiceUtil.getPages(
						themeDisplay.getScopeGroupId(), wikiPageDraftUserId,
						_wikiNode.getNodeId(), wikiPageStatus,
						searchContainer.getStart(), searchContainer.getEnd()),
					WikiPageServiceUtil.getPagesCount(
						themeDisplay.getScopeGroupId(), wikiPageDraftUserId,
						_wikiNode.getNodeId(), wikiPageStatus));
			}
			else {
				searchContainer.setResultsAndTotal(
					WikiPageLocalServiceUtil.getPages(
						themeDisplay.getScopeGroupId(), _wikiNode.getNodeId(),
						wikiPageStatus, wikiPageDraftUserId));
			}
		}
		else if (navigation.equals("frontpage")) {
			WikiWebComponentProvider wikiWebComponentProvider =
				WikiWebComponentProvider.getWikiWebComponentProvider();

			WikiGroupServiceConfiguration wikiGroupServiceConfiguration =
				wikiWebComponentProvider.getWikiGroupServiceConfiguration();

			searchContainer.setResultsAndTotal(
				() -> ListUtil.fromArray(
					WikiPageServiceUtil.getPage(
						themeDisplay.getScopeGroupId(), _wikiNode.getNodeId(),
						wikiGroupServiceConfiguration.frontPageName())),
				1);
		}
		else if (navigation.equals("history")) {
			if (_hasViewPendingStatusPermission(page)) {
				searchContainer.setResultsAndTotal(
					() -> WikiPageLocalServiceUtil.getPages(
						page.getNodeId(), page.getTitle(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS,
						PageVersionComparator.getInstance(false)),
					WikiPageLocalServiceUtil.getPagesCount(
						page.getNodeId(), page.getTitle()));
			}
			else {
				searchContainer.setResultsAndTotal(
					WikiPageLocalServiceUtil.getPages(
						page.getResourcePrimKey(), page.getNodeId(),
						WorkflowConstants.STATUS_APPROVED));
			}
		}
		else if (navigation.equals("incoming-links")) {
			searchContainer.setResultsAndTotal(
				WikiPageLocalServiceUtil.getIncomingLinks(
					page.getNodeId(), page.getTitle()));
		}
		else if (navigation.equals("orphan-pages")) {
			searchContainer.setResultsAndTotal(
				WikiPageServiceUtil.getOrphans(_wikiNode));
		}
		else if (navigation.equals("outgoing-links")) {
			searchContainer.setResultsAndTotal(
				WikiPageLocalServiceUtil.getOutgoingLinks(
					page.getNodeId(), page.getTitle()));
		}
		else if (navigation.equals("recent-changes")) {
			searchContainer.setResultsAndTotal(
				() -> WikiPageServiceUtil.getRecentChanges(
					themeDisplay.getScopeGroupId(), _wikiNode.getNodeId(),
					searchContainer.getStart(), searchContainer.getEnd()),
				WikiPageServiceUtil.getRecentChangesCount(
					themeDisplay.getScopeGroupId(), _wikiNode.getNodeId()));
		}
	}

	private boolean _hasSubscribePermission(WikiPage wikiPage)
		throws PortalException {

		if (Objects.isNull(
				_httpServletRequest.getAttribute(
					WebKeys.SEARCH_CONTAINER_RESULT_ROW))) {

			return false;
		}

		WikiGroupServiceOverriddenConfiguration
			wikiGroupServiceOverriddenConfiguration =
				_wikiRequestHelper.getWikiGroupServiceOverriddenConfiguration();

		if (!WikiPagePermission.contains(
				_wikiRequestHelper.getPermissionChecker(), wikiPage,
				ActionKeys.SUBSCRIBE) ||
			(!wikiGroupServiceOverriddenConfiguration.emailPageAddedEnabled() &&
			 !wikiGroupServiceOverriddenConfiguration.
				 emailPageUpdatedEnabled())) {

			return false;
		}

		return true;
	}

	private boolean _hasViewPendingStatusPermission(WikiPage wikiPage)
		throws PortalException {

		PermissionChecker permissionChecker =
			_wikiRequestHelper.getPermissionChecker();

		if (permissionChecker.isContentReviewer(
				_wikiRequestHelper.getCompanyId(),
				_wikiRequestHelper.getScopeGroupId())) {

			return true;
		}

		WikiPage lastWikiPage = WikiPageLocalServiceUtil.getPage(
			wikiPage.getResourcePrimKey(), false);

		if ((wikiPage.getVersion() >= lastWikiPage.getVersion()) ||
			(permissionChecker.getUserId() ==
				lastWikiPage.getStatusByUserId())) {

			return true;
		}

		return false;
	}

	private boolean _isCopyPasteEnabled(WikiPage wikiPage)
		throws PortalException {

		if (!WikiPagePermission.contains(
				_wikiRequestHelper.getPermissionChecker(), wikiPage,
				ActionKeys.UPDATE) ||
			!WikiNodePermission.contains(
				_wikiRequestHelper.getPermissionChecker(), wikiPage.getNodeId(),
				ActionKeys.ADD_PAGE)) {

			return false;
		}

		return true;
	}

	private Boolean _isSubscribed(WikiPage wikiPage) {
		User user = _wikiRequestHelper.getUser();

		return SubscriptionLocalServiceUtil.isSubscribed(
			user.getCompanyId(), user.getUserId(), WikiPage.class.getName(),
			wikiPage.getResourcePrimKey());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiListPagesDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final TrashHelper _trashHelper;
	private final WikiNode _wikiNode;
	private final WikiRequestHelper _wikiRequestHelper;

}