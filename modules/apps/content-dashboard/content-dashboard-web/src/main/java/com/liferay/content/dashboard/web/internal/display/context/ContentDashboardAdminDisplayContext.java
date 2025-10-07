/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.content.dashboard.info.item.ClassNameClassPKInfoItemIdentifier;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtypeFactoryRegistry;
import com.liferay.content.dashboard.web.internal.item.selector.ContentDashboardItemSubtypeItemSelectorCriterion;
import com.liferay.content.dashboard.web.internal.item.type.ContentDashboardItemSubtypeUtil;
import com.liferay.content.dashboard.web.internal.model.AssetVocabularyMetric;
import com.liferay.content.dashboard.web.internal.servlet.taglib.util.ContentDashboardDropdownItemsProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.learn.LearnMessage;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.GenericUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.users.admin.item.selector.UserItemSelectorCriterion;

import jakarta.portlet.ActionURL;
import jakarta.portlet.ResourceURL;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Cristina González
 */
public class ContentDashboardAdminDisplayContext {

	public ContentDashboardAdminDisplayContext(
		List<AssetVocabulary> assetVocabularies,
		AssetVocabularyMetric assetVocabularyMetric,
		ContentDashboardDropdownItemsProvider
			contentDashboardDropdownItemsProvider,
		ContentDashboardItemSubtypeFactoryRegistry
			contentDashboardItemSubtypeFactoryRegistry,
		ItemSelector itemSelector, String languageDirection,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, Portal portal,
		ResourceBundle resourceBundle,
		SearchContainer<ContentDashboardItem<?>> searchContainer) {

		_assetVocabularies = assetVocabularies;
		_assetVocabularyMetric = assetVocabularyMetric;
		_contentDashboardDropdownItemsProvider =
			contentDashboardDropdownItemsProvider;
		_contentDashboardItemSubtypeFactoryRegistry =
			contentDashboardItemSubtypeFactoryRegistry;
		_itemSelector = itemSelector;
		_languageDirection = languageDirection;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_portal = portal;
		_resourceBundle = resourceBundle;
		_searchContainer = searchContainer;
	}

	public List<Long> getAssetCategoryIds() {
		if (_assetCategoryIds != null) {
			return _assetCategoryIds;
		}

		_assetCategoryIds = Arrays.asList(
			ArrayUtil.toLongArray(
				ParamUtil.getLongValues(
					_liferayPortletRequest, "assetCategoryId")));

		return _assetCategoryIds;
	}

	public List<String> getAssetCategoryTitles(
		ContentDashboardItem<?> contentDashboardItem, long assetVocabularyId) {

		Locale locale = _portal.getLocale(_liferayPortletRequest);

		return ListUtil.toList(
			contentDashboardItem.getAssetCategories(assetVocabularyId),
			assetCategory -> assetCategory.getTitle(locale));
	}

	public Set<String> getAssetTagIds() {
		if (_assetTagIds != null) {
			return _assetTagIds;
		}

		_assetTagIds = new HashSet(
			Arrays.asList(
				ArrayUtil.toStringArray(
					ParamUtil.getStringValues(
						_liferayPortletRequest, "assetTagId"))));

		return _assetTagIds;
	}

	public List<AssetVocabulary> getAssetVocabularies() {
		return _assetVocabularies;
	}

	public String getAuditGraphTitle() {
		List<String> vocabularyNames =
			_assetVocabularyMetric.getVocabularyNames();

		if (vocabularyNames.size() == 2) {
			return ResourceBundleUtil.getString(
				_resourceBundle, "content-per-x-and-x", vocabularyNames.get(0),
				vocabularyNames.get(1));
		}
		else if (vocabularyNames.size() == 1) {
			return ResourceBundleUtil.getString(
				_resourceBundle, "content-per-x", vocabularyNames.get(0));
		}

		return ResourceBundleUtil.getString(_resourceBundle, "content");
	}

	public List<Long> getAuthorIds() {
		if (_authorIds != null) {
			return _authorIds;
		}

		_authorIds = Arrays.asList(
			ArrayUtil.toLongArray(
				ParamUtil.getLongValues(_liferayPortletRequest, "authorIds")));

		return _authorIds;
	}

	public String getAuthorItemSelectorURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_liferayPortletRequest);

		UserItemSelectorCriterion userItemSelectorCriterion =
			new UserItemSelectorCriterion();

		userItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.singletonList(new UUIDItemSelectorReturnType()));

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory,
				_liferayPortletResponse.getNamespace() + "selectedAuthorItem",
				userItemSelectorCriterion)
		).setParameter(
			"checkedUserIds", StringUtil.merge(getAuthorIds())
		).setParameter(
			"checkedUserIdsEnabled", Boolean.TRUE
		).buildString();
	}

	public long getClassPK(InfoItemReference infoItemReference) {
		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return 0;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();

		return classPKInfoItemIdentifier.getClassPK();
	}

	public String getContentDashboardItemSubtypeItemSelectorURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_liferayPortletRequest);

		ContentDashboardItemSubtypeItemSelectorCriterion
			contentDashboardItemSubtypeItemSelectorCriterion =
				new ContentDashboardItemSubtypeItemSelectorCriterion();

		contentDashboardItemSubtypeItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(
				Collections.singletonList(new UUIDItemSelectorReturnType()));

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory,
				_liferayPortletResponse.getNamespace() +
					"selectedContentDashboardItemSubtype",
				contentDashboardItemSubtypeItemSelectorCriterion)
		).setParameter(
			"checkedContentDashboardItemSubtypesPayload",
			() -> TransformUtil.transformToArray(
				getContentDashboardItemSubtypes(),
				contentDashboardItemSubtype -> {
					InfoItemReference infoItemReference =
						contentDashboardItemSubtype.getInfoItemReference();

					long classPK = 0;

					InfoItemIdentifier infoItemIdentifier =
						infoItemReference.getInfoItemIdentifier();

					if (infoItemIdentifier instanceof
							ClassNameClassPKInfoItemIdentifier) {

						ClassNameClassPKInfoItemIdentifier
							classNameClassPKInfoItemIdentifier =
								(ClassNameClassPKInfoItemIdentifier)
									infoItemIdentifier;

						classPK =
							classNameClassPKInfoItemIdentifier.getClassPK();
					}

					Class<?> genericClass = GenericUtil.getGenericClass(
						contentDashboardItemSubtype);

					return JSONUtil.put(
						"className", infoItemReference.getClassName()
					).put(
						"classPK", classPK
					).put(
						"entryClassName", genericClass.getName()
					).toString();
				},
				String.class)
		).buildString();
	}

	public List<? extends ContentDashboardItemSubtype>
		getContentDashboardItemSubtypes() {

		if (_contentDashboardItemSubtypes != null) {
			return _contentDashboardItemSubtypes;
		}

		String[] contentDashboardItemSubtypePayloads =
			ParamUtil.getParameterValues(
				_liferayPortletRequest, "contentDashboardItemSubtypePayload",
				new String[0], false);

		if (ArrayUtil.isEmpty(contentDashboardItemSubtypePayloads)) {
			_contentDashboardItemSubtypes = Collections.emptyList();
		}
		else {
			_contentDashboardItemSubtypes = TransformUtil.transformToList(
				contentDashboardItemSubtypePayloads,
				contentDashboardItemSubtypePayload ->
					ContentDashboardItemSubtypeUtil.
						toContentDashboardItemSubtype(
							_contentDashboardItemSubtypeFactoryRegistry,
							contentDashboardItemSubtypePayload));
		}

		return _contentDashboardItemSubtypes;
	}

	public String getContentPerformanceDataFetchURL(
		InfoItemReference infoItemReference) {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return null;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)infoItemIdentifier;

		return ResourceURLBuilder.createResourceURL(
			_liferayPortletResponse
		).setBackURL(
			_portal.getCurrentURL(_liferayPortletRequest)
		).setParameter(
			"className", infoItemReference.getClassName()
		).setParameter(
			"classPK", String.valueOf(classPKInfoItemIdentifier.getClassPK())
		).setResourceID(
			"/content_dashboard/get_content_performance_info"
		).buildString();
	}

	public Map<String, Object> getData() {
		if (_data != null) {
			return _data;
		}

		_data = HashMapBuilder.<String, Object>put(
			"context", _getContext()
		).put(
			"props", _getProps()
		).build();

		return _data;
	}

	public String getDateType() {
		if (_dateType != null) {
			return _dateType;
		}

		_dateType = ParamUtil.getString(_liferayPortletRequest, "dateType");

		return _dateType;
	}

	public List<DropdownItem> getDropdownItems(
		ContentDashboardItem contentDashboardItem) {

		return _contentDashboardDropdownItemsProvider.getDropdownItems(
			contentDashboardItem);
	}

	public String getEndDateString() {
		if (_endDateString != null) {
			return _endDateString;
		}

		_endDateString = ParamUtil.getString(_liferayPortletRequest, "endDate");

		return _endDateString;
	}

	public String getPanelState() {
		return SessionClicks.get(
			_portal.getHttpServletRequest(_liferayPortletRequest),
			"com.liferay.content.dashboard.web_panelState", "closed");
	}

	public String getPortletDisplayId() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.getId();
	}

	public String getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/content_dashboard/edit_content_dashboard_configuration"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getReviewDateString() {
		if (_reviewDateString != null) {
			return _reviewDateString;
		}

		_reviewDateString = ParamUtil.getString(
			_liferayPortletRequest, "reviewDate");

		return _reviewDateString;
	}

	public long getScopeId() {
		if (_scopeId > 0) {
			return _scopeId;
		}

		_scopeId = ParamUtil.getLong(_liferayPortletRequest, "scopeId");

		return _scopeId;
	}

	public String getScopeIdItemSelectorURL() throws PortalException {
		GroupItemSelectorCriterion groupItemSelectorCriterion =
			new GroupItemSelectorCriterion();

		groupItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());
		groupItemSelectorCriterion.setIncludeAllVisibleGroups(true);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					_liferayPortletRequest),
				_liferayPortletResponse.getNamespace() + "selectedScopeIdItem",
				groupItemSelectorCriterion));
	}

	public SearchContainer<ContentDashboardItem<?>> getSearchContainer() {
		return _searchContainer;
	}

	public String getSelectedItemFetchURL(
		ContentDashboardItem contentDashboardItem) {

		InfoItemReference infoItemReference =
			contentDashboardItem.getInfoItemReference();

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof
				ClassNameClassPKInfoItemIdentifier)) {

			return null;
		}

		ClassNameClassPKInfoItemIdentifier classNameClassPKInfoItemIdentifier =
			(ClassNameClassPKInfoItemIdentifier)infoItemIdentifier;

		long classPK = classNameClassPKInfoItemIdentifier.getClassPK();

		ResourceURL resourceURL = _liferayPortletResponse.createResourceURL();

		resourceURL.setParameter(
			"backURL", _portal.getCurrentURL(_liferayPortletRequest));

		resourceURL.setParameter("className", infoItemReference.getClassName());
		resourceURL.setParameter("classPK", String.valueOf(classPK));

		resourceURL.setResourceID(
			"/content_dashboard/get_content_dashboard_item_info");

		return resourceURL.toString();
	}

	public String getSelectedItemRowId() {
		return SessionClicks.get(
			_portal.getHttpServletRequest(_liferayPortletRequest),
			"com.liferay.content.dashboard.web_selectedItemRowId",
			StringPool.BLANK);
	}

	public String getStartDateString() {
		if (_startDateString != null) {
			return _startDateString;
		}

		_startDateString = ParamUtil.getString(
			_liferayPortletRequest, "startDate");

		return _startDateString;
	}

	public Integer getStatus() {
		if (_status != null) {
			return _status;
		}

		_status = ParamUtil.getInteger(
			_liferayPortletRequest, "status", WorkflowConstants.STATUS_ANY);

		return _status;
	}

	public ActionURL getSwapConfigurationURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/content_dashboard/swap_content_dashboard_configuration"
		).buildActionURL();
	}

	public long getUserId() {
		if (_userId > 0) {
			return _userId;
		}

		_userId = _portal.getUserId(_liferayPortletRequest);

		return _userId;
	}

	public HashMap<String, Object> getXlsProps() {
		return HashMapBuilder.<String, Object>put(
			"fileURL",
			() -> ResourceURLBuilder.createResourceURL(
				_liferayPortletResponse
			).setBackURL(
				_portal.getCurrentURL(_liferayPortletRequest)
			).setResourceID(
				"/content_dashboard/get_content_dashboard_items_xls"
			).buildString()
		).put(
			"total", _searchContainer.getTotal()
		).build();
	}

	public boolean isSwapConfigurationEnabled() {
		if (_swapConfigurationEnabled != null) {
			return _swapConfigurationEnabled;
		}

		List<String> vocabularyNames =
			_assetVocabularyMetric.getVocabularyNames();

		if (vocabularyNames.size() == 2) {
			_swapConfigurationEnabled = true;
		}
		else {
			_swapConfigurationEnabled = false;
		}

		return _swapConfigurationEnabled;
	}

	public String toString(Date date) {
		Instant instant = date.toInstant();

		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

		return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	private Map<String, Object> _getContext() {
		return HashMapBuilder.<String, Object>put(
			"languageDirection", _languageDirection
		).put(
			"namespace", _liferayPortletResponse.getNamespace()
		).build();
	}

	private Map<String, Object> _getProps() {
		return HashMapBuilder.<String, Object>put(
			"learnHowLink",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_liferayPortletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				LearnMessage learnMessage = LearnMessageUtil.getLearnMessage(
					"general", themeDisplay.getLanguageId(), "asset-taglib");

				return JSONUtil.put(
					"message", learnMessage.getMessage()
				).put(
					"url", learnMessage.getURL()
				);
			}
		).put(
			"vocabularies", _assetVocabularyMetric.toJSONArray()
		).build();
	}

	private List<Long> _assetCategoryIds;
	private Set<String> _assetTagIds;
	private final List<AssetVocabulary> _assetVocabularies;
	private final AssetVocabularyMetric _assetVocabularyMetric;
	private List<Long> _authorIds;
	private final ContentDashboardDropdownItemsProvider
		_contentDashboardDropdownItemsProvider;
	private final ContentDashboardItemSubtypeFactoryRegistry
		_contentDashboardItemSubtypeFactoryRegistry;
	private List<ContentDashboardItemSubtype> _contentDashboardItemSubtypes;
	private Map<String, Object> _data;
	private String _dateType;
	private String _endDateString;
	private final ItemSelector _itemSelector;
	private final String _languageDirection;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final Portal _portal;
	private final ResourceBundle _resourceBundle;
	private String _reviewDateString;
	private long _scopeId;
	private final SearchContainer<ContentDashboardItem<?>> _searchContainer;
	private String _startDateString;
	private Integer _status;
	private Boolean _swapConfigurationEnabled;
	private long _userId;

}