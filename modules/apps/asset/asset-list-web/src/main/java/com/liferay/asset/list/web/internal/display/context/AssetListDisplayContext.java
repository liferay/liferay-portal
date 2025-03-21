/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.list.constants.AssetListActionKeys;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalServiceUtil;
import com.liferay.asset.list.service.AssetListEntryServiceUtil;
import com.liferay.asset.list.service.AssetListEntryUsageLocalServiceUtil;
import com.liferay.asset.list.util.AssetListPortletUtil;
import com.liferay.asset.list.web.internal.security.permission.resource.AssetListEntryPermission;
import com.liferay.asset.list.web.internal.security.permission.resource.AssetListPermission;
import com.liferay.asset.list.web.internal.servlet.taglib.util.AssetListEntryActionDropdownItemsProvider;
import com.liferay.asset.util.AssetRendererFactoryClassProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class AssetListDisplayContext {

	public AssetListDisplayContext(
		AssetRendererFactoryClassProvider assetRendererFactoryClassProvider,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_assetRendererFactoryClassProvider = assetRendererFactoryClassProvider;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_liferayPortletRequest = PortalUtil.getLiferayPortletRequest(
			renderRequest);
		_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(
			renderResponse);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems(
		AssetListEntry assetListEntry) {

		AssetListEntryActionDropdownItemsProvider
			assetListEntryActionDropdownItemsProvider =
				new AssetListEntryActionDropdownItemsProvider(
					assetListEntry, _liferayPortletRequest,
					_liferayPortletResponse);

		return assetListEntryActionDropdownItemsProvider.
			getActionDropdownItems();
	}

	public List<DropdownItem> getAddAssetListEntryDropdownItems() {
		return DropdownItemListBuilder.add(
			_getAddAssetListEntryDropdownItemUnsafeConsumer(
				AssetListEntryTypeConstants.TYPE_MANUAL_LABEL,
				"manual-collection", AssetListEntryTypeConstants.TYPE_MANUAL)
		).add(
			_getAddAssetListEntryDropdownItemUnsafeConsumer(
				AssetListEntryTypeConstants.TYPE_DYNAMIC_LABEL,
				"dynamic-collection", AssetListEntryTypeConstants.TYPE_DYNAMIC)
		).build();
	}

	public String getAssetEntrySubtypeLabel(AssetListEntry assetListEntry) {
		String assetEntryTypeLabel = getAssetEntryTypeLabel(assetListEntry);
		String classTypeLabel = getClassTypeLabel(assetListEntry);

		if (Validator.isNull(classTypeLabel) ||
			Objects.equals(StringPool.DASH, classTypeLabel)) {

			return HtmlUtil.escape(assetEntryTypeLabel);
		}

		return HtmlUtil.escape(assetEntryTypeLabel + " - " + classTypeLabel);
	}

	public String getAssetEntryTypeLabel(AssetListEntry assetListEntry) {
		if (Validator.isNotNull(assetListEntry.getAssetEntryType())) {
			return ResourceActionsUtil.getModelResource(
				_themeDisplay.getLocale(), assetListEntry.getAssetEntryType());
		}

		return StringPool.BLANK;
	}

	public int getAssetListEntriesCount() {
		if (_assetListEntriesCount != null) {
			return _assetListEntriesCount;
		}

		_assetListEntriesCount =
			AssetListEntryServiceUtil.getAssetListEntriesCount(
				_themeDisplay.getScopeGroupId());

		return _assetListEntriesCount;
	}

	public SearchContainer<AssetListEntry>
		getAssetListEntriesSearchContainer() {

		if (_assetListEntriesSearchContainer != null) {
			return _assetListEntriesSearchContainer;
		}

		SearchContainer<AssetListEntry> assetListEntriesSearchContainer =
			new SearchContainer<>(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-collections");

		assetListEntriesSearchContainer.setOrderByCol(_getOrderByCol());
		assetListEntriesSearchContainer.setOrderByComparator(
			AssetListPortletUtil.getAssetListEntryOrderByComparator(
				_getOrderByCol(), getOrderByType()));
		assetListEntriesSearchContainer.setOrderByType(getOrderByType());

		if (_isSearch()) {
			assetListEntriesSearchContainer.setResultsAndTotal(
				() -> AssetListEntryServiceUtil.getAssetListEntries(
					_themeDisplay.getScopeGroupId(), _getKeywords(),
					assetListEntriesSearchContainer.getStart(),
					assetListEntriesSearchContainer.getEnd(),
					assetListEntriesSearchContainer.getOrderByComparator()),
				AssetListEntryServiceUtil.getAssetListEntriesCount(
					_themeDisplay.getScopeGroupId(), _getKeywords()));
		}
		else {
			assetListEntriesSearchContainer.setResultsAndTotal(
				() -> AssetListEntryServiceUtil.getAssetListEntries(
					_themeDisplay.getScopeGroupId(),
					assetListEntriesSearchContainer.getStart(),
					assetListEntriesSearchContainer.getEnd(),
					assetListEntriesSearchContainer.getOrderByComparator()),
				getAssetListEntriesCount());
		}

		if (!isLiveGroup()) {
			assetListEntriesSearchContainer.setRowChecker(
				new EmptyOnClickRowChecker(_renderResponse));
		}

		_assetListEntriesSearchContainer = assetListEntriesSearchContainer;

		return _assetListEntriesSearchContainer;
	}

	public AssetListEntry getAssetListEntry() {
		if (_assetListEntry != null) {
			return _assetListEntry;
		}

		_assetListEntry = AssetListEntryLocalServiceUtil.fetchAssetListEntry(
			getAssetListEntryId());

		return _assetListEntry;
	}

	public long getAssetListEntryId() {
		if (_assetListEntryId != null) {
			return _assetListEntryId;
		}

		_assetListEntryId = ParamUtil.getLong(
			_httpServletRequest, "assetListEntryId");

		return _assetListEntryId;
	}

	public int getAssetListEntrySegmentsEntryRelsCount(
		AssetListEntry assetListEntry) {

		int assetListEntrySegmentsEntryRelsCount =
			AssetListEntrySegmentsEntryRelLocalServiceUtil.
				getAssetListEntrySegmentsEntryRelsCount(
					assetListEntry.getAssetListEntryId());

		if (assetListEntrySegmentsEntryRelsCount < 2) {
			return 0;
		}

		return assetListEntrySegmentsEntryRelsCount;
	}

	public String getAssetListEntryTitle() {
		AssetListEntry assetListEntry = getAssetListEntry();

		if (assetListEntry != null) {
			return assetListEntry.getTitle();
		}

		String title = StringPool.BLANK;

		if (getAssetListEntryType() ==
				AssetListEntryTypeConstants.TYPE_DYNAMIC) {

			title = "new-dynamic-collection";
		}
		else if (getAssetListEntryType() ==
					AssetListEntryTypeConstants.TYPE_MANUAL) {

			title = "new-manual-collection";
		}

		return LanguageUtil.get(_httpServletRequest, title);
	}

	public int getAssetListEntryType() {
		if (_assetListEntryType != null) {
			return _assetListEntryType;
		}

		AssetListEntry assetListEntry = getAssetListEntry();

		int assetListEntryType = ParamUtil.getInteger(
			_httpServletRequest, "assetListEntryType");

		if (assetListEntry != null) {
			assetListEntryType = assetListEntry.getType();
		}

		_assetListEntryType = assetListEntryType;

		return _assetListEntryType;
	}

	public String getAssetListEntryTypeLabel(AssetListEntry assetListEntry) {
		if (assetListEntry.getType() ==
				AssetListEntryTypeConstants.TYPE_DYNAMIC) {

			return LanguageUtil.get(
				_themeDisplay.getLocale(), "dynamic-collection");
		}

		return LanguageUtil.get(_themeDisplay.getLocale(), "manual-collection");
	}

	public int getAssetListEntryUsageCount(AssetListEntry assetListEntry) {
		Group group = _themeDisplay.getScopeGroup();

		if (group.getType() == GroupConstants.TYPE_DEPOT) {
			return AssetListEntryUsageLocalServiceUtil.
				getCompanyAssetListEntryUsagesCount(
					_themeDisplay.getCompanyId(),
					PortalUtil.getClassNameId(AssetListEntry.class),
					String.valueOf(assetListEntry.getAssetListEntryId()));
		}

		return AssetListEntryUsageLocalServiceUtil.getAssetListEntryUsagesCount(
			_themeDisplay.getScopeGroupId(),
			PortalUtil.getClassNameId(AssetListEntry.class),
			String.valueOf(assetListEntry.getAssetListEntryId()));
	}

	public String getClassName(AssetRendererFactory<?> assetRendererFactory) {
		Class<? extends AssetRendererFactory<?>> clazz =
			_assetRendererFactoryClassProvider.getClass(assetRendererFactory);

		String className = clazz.getName();

		return className.substring(
			className.lastIndexOf(StringPool.PERIOD) + 1);
	}

	public ClassType getClassType(
		ClassTypeReader classTypeReader, long classTypeId) {

		try {
			return classTypeReader.getClassType(
				classTypeId, _themeDisplay.getLocale());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get class type", portalException);
			}
		}

		return null;
	}

	public String getClassTypeLabel(AssetListEntry assetListEntry) {
		long classTypeId = GetterUtil.getLong(
			assetListEntry.getAssetEntrySubtype(), -1);

		if (classTypeId < 0) {
			return StringPool.DASH;
		}

		String classTypeLabel = StringPool.DASH;

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetListEntry.getAssetEntryType());

		if ((assetRendererFactory != null) &&
			assetRendererFactory.isSupportsClassTypes()) {

			ClassType classType = getClassType(
				assetRendererFactory.getClassTypeReader(), classTypeId);

			if (classType != null) {
				classTypeLabel = classType.getName();
			}
		}

		return classTypeLabel;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest, AssetListPortletKeys.ASSET_LIST, "list");

		return _displayStyle;
	}

	public String getEditURL(AssetListEntry assetListEntry)
		throws PortalException {

		if (isLiveGroup()) {
			return StringPool.BLANK;
		}

		if (AssetListEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), assetListEntry,
				ActionKeys.UPDATE) ||
			AssetListEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), assetListEntry,
				ActionKeys.VIEW)) {

			return PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCPath(
				"/edit_asset_list_entry.jsp"
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).setParameter(
				"assetListEntryId", assetListEntry.getAssetListEntryId()
			).setParameter(
				"backURLTitle",
				() -> {
					PortletDisplay portletDisplay =
						_themeDisplay.getPortletDisplay();

					return portletDisplay.getPortletDisplayName();
				}
			).buildString();
		}

		return StringPool.BLANK;
	}

	public String getEmptyResultMessageDescription() {
		if (isShowAddAssetListEntryAction()) {
			return LanguageUtil.get(
				_httpServletRequest,
				"fortunately-it-is-very-easy-to-add-new-ones");
		}

		return StringPool.BLANK;
	}

	public List<NavigationItem> getNavigationItems(String currentItem) {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(currentItem.equals("collections"));
				navigationItem.setHref(_renderResponse.createRenderURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "collections"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					currentItem.equals("collection-providers"));
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/view_info_collection_providers.jsp");
				navigationItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest, "collection-providers"));
			}
		).build();
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, AssetListPortletKeys.ASSET_LIST, "asc");

		return _orderByType;
	}

	public long getSegmentsEntryId() {
		if (_segmentsEntryId != null) {
			return _segmentsEntryId;
		}

		_segmentsEntryId = ParamUtil.getLong(
			_httpServletRequest, "segmentsEntryId",
			SegmentsEntryConstants.ID_DEFAULT);

		return _segmentsEntryId;
	}

	public boolean isLiveGroup() {
		Group group = _themeDisplay.getScopeGroup();

		if (group.isLayout()) {
			group = group.getParentGroup();
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (stagingGroupHelper.isLiveGroup(group) &&
			stagingGroupHelper.isStagedPortlet(
				group, AssetListPortletKeys.ASSET_LIST)) {

			return true;
		}

		return false;
	}

	public boolean isShowAddAssetListEntryAction() {
		if (isLiveGroup()) {
			return false;
		}

		return AssetListPermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(),
			AssetListActionKeys.ADD_ASSET_LIST_ENTRY);
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAddAssetListEntryDropdownItemUnsafeConsumer(
			String title, String label, int type) {

		return dropdownItem -> {
			dropdownItem.putData("action", "addAssetListEntry");
			dropdownItem.putData(
				"addAssetListEntryURL", _getAddAssetListEntryURL(type));
			dropdownItem.putData("title", _getAddAssetListTitle(title));
			dropdownItem.setLabel(LanguageUtil.get(_httpServletRequest, label));
		};
	}

	private String _getAddAssetListEntryURL(int type) {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/asset_list/add_asset_list_entry"
		).setParameter(
			"backURLTitle",
			() -> {
				PortletDisplay portletDisplay =
					_themeDisplay.getPortletDisplay();

				return portletDisplay.getPortletDisplayName();
			}
		).setParameter(
			"type", type
		).buildString();
	}

	private String _getAddAssetListTitle(String title) {
		return LanguageUtil.format(
			_httpServletRequest, "add-x-collection", title, true);
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, AssetListPortletKeys.ASSET_LIST,
			"create-date");

		return _orderByCol;
	}

	private boolean _isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListDisplayContext.class);

	private Integer _assetListEntriesCount;
	private SearchContainer<AssetListEntry> _assetListEntriesSearchContainer;
	private AssetListEntry _assetListEntry;
	private Long _assetListEntryId;
	private Integer _assetListEntryType;
	private final AssetRendererFactoryClassProvider
		_assetRendererFactoryClassProvider;
	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Long _segmentsEntryId;
	private final ThemeDisplay _themeDisplay;

}