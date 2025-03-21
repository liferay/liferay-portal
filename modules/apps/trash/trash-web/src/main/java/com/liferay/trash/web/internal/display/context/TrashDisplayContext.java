/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemListBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryBuilder;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashPortletKeys;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashEntryList;
import com.liferay.trash.service.TrashEntryLocalServiceUtil;
import com.liferay.trash.service.TrashEntryServiceUtil;
import com.liferay.trash.util.comparator.EntryCreateDateComparator;
import com.liferay.trash.web.internal.constants.TrashWebKeys;
import com.liferay.trash.web.internal.search.EntrySearch;
import com.liferay.trash.web.internal.search.EntrySearchTerms;
import com.liferay.trash.web.internal.servlet.taglib.util.TrashEntryActionDropdownItemsProvider;
import com.liferay.trash.web.internal.servlet.taglib.util.TrashViewContentActionDropdownItemsProvider;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Provides utility methods moved from the Recycle Bin portlet's JSP files to
 * reduce the complexity of the views.
 *
 * @author Jürgen Kappler
 */
public class TrashDisplayContext {

	public TrashDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_trashHelper = (TrashHelper)httpServletRequest.getAttribute(
			TrashWebKeys.TRASH_HELPER);
	}

	public List<BreadcrumbEntry> getBaseModelBreadcrumbEntries()
		throws Exception {

		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(_httpServletRequest, "recycle-bin"));
				breadcrumbEntry.setURL(
					String.valueOf(_liferayPortletResponse.createRenderURL()));
			}
		).addAll(
			_getBreadcrumbEntries(
				getClassName(), getClassPK(), "classPK",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/view_content.jsp"
				).setParameter(
					"classNameId",
					() -> {
						TrashHandler trashHandler = getTrashHandler();

						String trashHandlerContainerModelClassName =
							trashHandler.getContainerModelClassName(
								getClassPK());

						return PortalUtil.getClassNameId(
							trashHandlerContainerModelClassName);
					}
				).buildPortletURL(),
				true)
		).build();
	}

	public String getClassName() {
		TrashEntry trashEntry = getTrashEntry();

		if (trashEntry != null) {
			return trashEntry.getClassName();
		}

		String className = StringPool.BLANK;

		long classNameId = getClassNameId();

		if (classNameId > 0) {
			className = PortalUtil.getClassName(getClassNameId());
		}

		return className;
	}

	public long getClassNameId() {
		TrashEntry trashEntry = getTrashEntry();

		if (trashEntry != null) {
			return trashEntry.getClassNameId();
		}

		return ParamUtil.getLong(_httpServletRequest, "classNameId");
	}

	public long getClassPK() {
		TrashEntry trashEntry = getTrashEntry();

		if (trashEntry != null) {
			return trashEntry.getClassPK();
		}

		return ParamUtil.getLong(_httpServletRequest, "classPK");
	}

	public List<BreadcrumbEntry> getContainerModelBreadcrumbEntries(
		String className, long classPK, PortletURL containerModelURL) {

		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				TrashHandler trashHandler =
					TrashHandlerRegistryUtil.getTrashHandler(className);

				breadcrumbEntry.setTitle(
					LanguageUtil.get(
						themeDisplay.getLocale(),
						trashHandler.getRootContainerModelName()));

				if (classPK != 0) {
					containerModelURL.setParameter("containerModelId", "0");

					breadcrumbEntry.setURL(containerModelURL.toString());
				}
			}
		).addAll(
			() -> classPK != 0,
			() -> _getBreadcrumbEntries(
				className, classPK, "containerModelId", containerModelURL,
				false)
		).build();
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest, TrashPortletKeys.TRASH, "list");

		return _displayStyle;
	}

	public EntrySearch getEntrySearch() throws PortalException {
		if (_entrySearch != null) {
			return _entrySearch;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		EntrySearch entrySearch = new EntrySearch(
			_liferayPortletRequest, getPortletURL());

		entrySearch.setOrderByCol(getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		entrySearch.setOrderByComparator(
			EntryCreateDateComparator.getInstance(orderByAsc));
		entrySearch.setOrderByType(getOrderByType());

		EntrySearchTerms searchTerms =
			(EntrySearchTerms)entrySearch.getSearchTerms();

		if (isSearch()) {
			Sort sort = new Sort();

			if (Objects.equals(entrySearch.getOrderByCol(), "removed-date")) {
				sort.setFieldName(Field.REMOVED_DATE);
				sort.setType(Sort.LONG_TYPE);
			}
			else {
				sort.setType(Sort.SCORE_TYPE);
			}

			sort.setReverse(
				!StringUtil.equalsIgnoreCase(
					entrySearch.getOrderByType(), "asc"));

			entrySearch.setResultsAndTotal(
				TrashEntryLocalServiceUtil.searchTrashEntries(
					themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
					themeDisplay.getUserId(), searchTerms.getKeywords(),
					entrySearch.getStart(), entrySearch.getEnd(), sort));
		}
		else {
			TrashEntryList trashEntryList = null;

			if (Objects.equals(getNavigation(), "all")) {
				trashEntryList = TrashEntryServiceUtil.getEntries(
					themeDisplay.getScopeGroupId(), entrySearch.getStart(),
					entrySearch.getEnd(), entrySearch.getOrderByComparator());
			}
			else {
				trashEntryList = TrashEntryServiceUtil.getEntries(
					themeDisplay.getScopeGroupId(), getNavigation(),
					entrySearch.getStart(), entrySearch.getEnd(),
					entrySearch.getOrderByComparator());
			}

			entrySearch.setResultsAndTotal(
				trashEntryList::getOriginalTrashEntries,
				trashEntryList.getCount());

			_approximate = trashEntryList.isApproximate();
		}

		if ((entrySearch.getTotal() == 0) &&
			Validator.isNotNull(searchTerms.getKeywords())) {

			entrySearch.setEmptyResultsMessage(
				LanguageUtil.format(
					_httpServletRequest,
					"no-entries-were-found-that-matched-the-keywords-x",
					"<strong>" + HtmlUtil.escape(searchTerms.getKeywords()) +
						"</strong>",
					false));
		}

		EmptyOnClickRowChecker emptyOnClickRowChecker =
			new EmptyOnClickRowChecker(_liferayPortletResponse);

		emptyOnClickRowChecker.setRememberCheckBoxStateURLRegex(
			"^(?!.*" + _liferayPortletResponse.getNamespace() +
				"redirect).*^(?!.*/entry/)");

		entrySearch.setRowChecker(emptyOnClickRowChecker);

		_entrySearch = entrySearch;

		return _entrySearch;
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public String getLastElementBreadcrumbTitle(
		List<BreadcrumbEntry> breadcrumbEntries) {

		BreadcrumbEntry breadcrumbEntry = breadcrumbEntries.get(
			breadcrumbEntries.size() - 1);

		return breadcrumbEntry.getTitle();
	}

	public String getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "all");

		return _navigation;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, TrashPortletKeys.TRASH,
			isSearch() ? "relevance" : "removed-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, TrashPortletKeys.TRASH, "asc");

		return _orderByType;
	}

	public List<BreadcrumbEntry> getPortletBreadcrumbEntries() {
		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(_httpServletRequest, "recycle-bin"));
				breadcrumbEntry.setURL(String.valueOf(getPortletURL()));
			}
		).build();
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		long trashEntryId = getTrashEntryId();

		if (trashEntryId > 0) {
			portletURL.setParameter("mvcPath", "/view_content.jsp");
			portletURL.setParameter(
				"trashEntryId", String.valueOf(trashEntryId));
		}

		String displayStyle = getDisplayStyle();

		if (Validator.isNotNull(displayStyle)) {
			portletURL.setParameter("displayStyle", displayStyle);
		}

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String navigation = getNavigation();

		if (Validator.isNotNull(navigation)) {
			portletURL.setParameter("navigation", navigation);
		}

		return portletURL;
	}

	public List<TabsItem> getTabsItems() {
		return TabsItemListBuilder.add(
			tabsItem -> {
				tabsItem.setActive(true);
				tabsItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "details"));
			}
		).build();
	}

	public SearchContainer<TrashedModel> getTrashContainerSearchContainer()
		throws PortalException {

		if (_trashContainerSearchContainer != null) {
			return _trashContainerSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletURL iteratorURL = PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/view_content.jsp"
		).setParameter(
			"classNameId", getClassNameId()
		).setParameter(
			"classPK", getClassPK()
		).buildPortletURL();

		SearchContainer<TrashedModel> searchContainer = new SearchContainer(
			_liferayPortletRequest, iteratorURL, null,
			LanguageUtil.format(
				_httpServletRequest, "this-x-does-not-contain-an-entry",
				ResourceActionsUtil.getModelResource(
					themeDisplay.getLocale(), getClassName()),
				false));

		searchContainer.setDeltaConfigurable(false);

		TrashHandler trashHandler = getTrashHandler();

		searchContainer.setResultsAndTotal(
			() -> trashHandler.getTrashModelTrashedModels(
				getClassPK(), searchContainer.getStart(),
				searchContainer.getEnd(),
				searchContainer.getOrderByComparator()),
			trashHandler.getTrashModelsCount(getClassPK()));

		_trashContainerSearchContainer = searchContainer;

		return _trashContainerSearchContainer;
	}

	public TrashEntry getTrashEntry() {
		if (_trashEntry != null) {
			return _trashEntry;
		}

		long trashEntryId = ParamUtil.getLong(
			_httpServletRequest, "trashEntryId");

		long classNameId = ParamUtil.getLong(
			_httpServletRequest, "classNameId");
		long classPK = ParamUtil.getLong(_httpServletRequest, "classPK");

		if (trashEntryId > 0) {
			_trashEntry = TrashEntryLocalServiceUtil.fetchEntry(trashEntryId);
		}
		else if ((classNameId > 0) && (classPK > 0)) {
			String className = PortalUtil.getClassName(classNameId);

			if (Validator.isNotNull(className)) {
				_trashEntry = TrashEntryLocalServiceUtil.fetchEntry(
					className, classPK);
			}
		}

		return _trashEntry;
	}

	public List<DropdownItem> getTrashEntryActionDropdownItems(
			TrashEntry trashEntry)
		throws Exception {

		TrashEntryActionDropdownItemsProvider
			trashEntryActionDropdownItemsProvider =
				new TrashEntryActionDropdownItemsProvider(
					_liferayPortletRequest, _liferayPortletResponse,
					trashEntry);

		return trashEntryActionDropdownItemsProvider.getActionDropdownItems();
	}

	public long getTrashEntryId() {
		TrashEntry trashEntry = getTrashEntry();

		if (trashEntry != null) {
			return trashEntry.getEntryId();
		}

		return 0;
	}

	public TrashHandler getTrashHandler() {
		if (_trashHandler != null) {
			return _trashHandler;
		}

		_trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			getClassName());

		return _trashHandler;
	}

	public TrashRenderer getTrashRenderer() throws PortalException {
		if (_trashRenderer != null) {
			return _trashRenderer;
		}

		TrashHandler trashHandler = getTrashHandler();

		long classPK = getClassPK();

		if ((classPK > 0) && (trashHandler != null)) {
			_trashRenderer = trashHandler.getTrashRenderer(getClassPK());
		}

		return _trashRenderer;
	}

	public List<DropdownItem> getTrashViewContentActionDropdownItems(
			String className, long classPK)
		throws Exception {

		TrashViewContentActionDropdownItemsProvider
			trashViewContentActionDropdownItemsProvider =
				new TrashViewContentActionDropdownItemsProvider(
					_liferayPortletRequest, _liferayPortletResponse, className,
					classPK);

		return trashViewContentActionDropdownItemsProvider.
			getActionDropdownItems();
	}

	public String getViewContentRedirectURL() throws PortalException {
		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		TrashHandler trashHandler = getTrashHandler();

		ContainerModel parentContainerModel =
			trashHandler.getParentContainerModel(getClassPK());

		PortletURL redirectURL = _liferayPortletResponse.createRenderURL();

		if ((parentContainerModel != null) && (getClassNameId() > 0)) {
			String parentContainerModelClassName =
				parentContainerModel.getModelClassName();

			redirectURL.setParameter("mvcPath", "/view_content.jsp");
			redirectURL.setParameter(
				"classNameId",
				String.valueOf(
					PortalUtil.getClassNameId(parentContainerModelClassName)));
			redirectURL.setParameter(
				"classPK",
				String.valueOf(parentContainerModel.getContainerModelId()));
		}

		return redirectURL.toString();
	}

	public boolean isApproximate() {
		return _approximate;
	}

	public boolean isDescriptiveView() {
		if (Objects.equals(getDisplayStyle(), "descriptive")) {
			return true;
		}

		return false;
	}

	public boolean isIconView() {
		if (Objects.equals(getDisplayStyle(), "icon")) {
			return true;
		}

		return false;
	}

	public boolean isListView() {
		if (Objects.equals(getDisplayStyle(), "list")) {
			return true;
		}

		return false;
	}

	public boolean isSearch() {
		if (Validator.isNotNull(getKeywords())) {
			return true;
		}

		return false;
	}

	private List<BreadcrumbEntry> _getBreadcrumbEntries(
		String className, long classPK, String paramName,
		PortletURL containerModelURL, boolean checkInTrashContainers) {

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			className);

		return BreadcrumbEntryListBuilder.addAll(
			() -> {
				PortletURL portletURL = PortletURLUtil.clone(
					containerModelURL, _liferayPortletResponse);

				List<ContainerModel> containerModels =
					trashHandler.getParentContainerModels(classPK);

				Collections.reverse(containerModels);

				return TransformUtil.transform(
					containerModels,
					containerModel -> {
						TrashHandler containerModelTrashHandler =
							TrashHandlerRegistryUtil.getTrashHandler(
								containerModel.getModelClassName());

						if (checkInTrashContainers &&
							!containerModelTrashHandler.isInTrash(
								containerModel.getContainerModelId())) {

							return null;
						}

						return BreadcrumbEntryBuilder.setTitle(
							() -> {
								String name =
									containerModel.getContainerModelName();

								if (containerModelTrashHandler.isInTrash(
										containerModel.getContainerModelId())) {

									return _trashHelper.getOriginalTitle(name);
								}

								return name;
							}
						).setURL(
							PortletURLBuilder.create(
								portletURL
							).setParameter(
								paramName, containerModel.getContainerModelId()
							).buildString()
						).build();
					});
			}
		).add(
			breadcrumbEntry -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				TrashRenderer trashRenderer = trashHandler.getTrashRenderer(
					classPK);

				breadcrumbEntry.setTitle(
					trashRenderer.getTitle(themeDisplay.getLocale()));
			}
		).build();
	}

	private boolean _approximate;
	private String _displayStyle;
	private EntrySearch _entrySearch;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private SearchContainer<TrashedModel> _trashContainerSearchContainer;
	private TrashEntry _trashEntry;
	private TrashHandler _trashHandler;
	private final TrashHelper _trashHelper;
	private TrashRenderer _trashRenderer;

}