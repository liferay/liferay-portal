/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.admin.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagServiceUtil;
import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.asset.util.comparator.AssetTagAssetCountComparator;
import com.liferay.portlet.asset.util.comparator.AssetTagNameComparator;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Juergen Kappler
 */
public class AssetTagsDisplayContext {

	public AssetTagsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getAssetTagActionDropdownItems(AssetTag tag) {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						this::isShowTagsActions,
						dropdownItem -> {
							dropdownItem.setHref(
								PortletURLBuilder.createRenderURL(
									_renderResponse
								).setMVCPath(
									"/edit_tag.jsp"
								).setParameter(
									"tagId", tag.getTagId()
								).buildString());
							dropdownItem.setIcon("pencil");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "edit"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						this::isShowTagsActions,
						dropdownItem -> {
							dropdownItem.setHref(
								PortletURLBuilder.createRenderURL(
									_renderResponse
								).setMVCPath(
									"/merge_tag.jsp"
								).setParameter(
									"mergeTagIds", tag.getTagId()
								).buildString());
							dropdownItem.setIcon("merge");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "merge"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData("action", "deleteTag");
							dropdownItem.putData(
								"deleteTagURL",
								PortletURLBuilder.createActionURL(
									_renderResponse
								).setActionName(
									"deleteTag"
								).setRedirect(
									_themeDisplay.getURLCurrent()
								).setParameter(
									"tagId", tag.getTagId()
								).buildString());
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "delete"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public String getAssetTitle() {
		AssetTag tag = getTag();

		if (tag == null) {
			return LanguageUtil.get(_httpServletRequest, "new-tag");
		}

		return tag.getName();
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest, AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
			"list");

		return _displayStyle;
	}

	public long getFullTagsCount(AssetTag tag) {
		return AssetEntryLocalServiceUtil.getAssetTagAssetEntriesCount(
			tag.getTagId());
	}

	public String getKeywords() {
		if (Validator.isNotNull(_keywords)) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords", null);

		return _keywords;
	}

	public List<String> getMergeTagNames() {
		if (_mergeTagNames != null) {
			return _mergeTagNames;
		}

		long[] mergeTagIds = StringUtil.split(
			ParamUtil.getString(_renderRequest, "mergeTagIds"), 0L);

		List<String> mergeTagNames = new ArrayList<>();

		for (long mergeTagId : mergeTagIds) {
			AssetTag tag = AssetTagLocalServiceUtil.fetchAssetTag(mergeTagId);

			if (tag == null) {
				continue;
			}

			mergeTagNames.add(tag.getName());
		}

		_mergeTagNames = mergeTagNames;

		return _mergeTagNames;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
			"name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
			"asc");

		return _orderByType;
	}

	public AssetTag getTag() {
		if (_tag != null) {
			return _tag;
		}

		long tagId = getTagId();

		AssetTag tag = null;

		if (tagId > 0) {
			tag = AssetTagLocalServiceUtil.fetchAssetTag(tagId);
		}

		_tag = tag;

		return _tag;
	}

	public Long getTagId() {
		if (_tagId != null) {
			return _tagId;
		}

		_tagId = ParamUtil.getLong(_httpServletRequest, "tagId");

		CTTimelineUtil.setCTTimelineKeys(
			_httpServletRequest, AssetTag.class, _tagId);

		return _tagId;
	}

	public SearchContainer<AssetTag> getTagsSearchContainer()
		throws PortalException {

		if (_tagsSearchContainer != null) {
			return _tagsSearchContainer;
		}

		SearchContainer<AssetTag> tagsSearchContainer = new SearchContainer<>(
			_renderRequest, _renderResponse.createRenderURL(), null,
			"there-are-no-tags");

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			Sort sort = null;

			String orderByCol = getOrderByCol();

			if (orderByCol.equals("name")) {
				sort = SortFactoryUtil.getSort(
					AssetTag.class, Sort.STRING_TYPE, Field.NAME,
					getOrderByType());
			}
			else if (orderByCol.equals("usages")) {
				sort = SortFactoryUtil.getSort(
					AssetTag.class, Sort.INT_TYPE, "assetCount_Number",
					getOrderByType());
			}

			tagsSearchContainer.setResultsAndTotal(
				AssetTagLocalServiceUtil.searchTags(
					new long[] {_themeDisplay.getScopeGroupId()}, keywords,
					tagsSearchContainer.getStart(),
					tagsSearchContainer.getEnd(), sort));
		}
		else {
			String orderByCol = getOrderByCol();

			tagsSearchContainer.setOrderByCol(orderByCol);

			OrderByComparator<AssetTag> orderByComparator = null;

			boolean orderByAsc = false;

			String orderByType = getOrderByType();

			if (orderByType.equals("asc")) {
				orderByAsc = true;
			}

			if (orderByCol.equals("name")) {
				orderByComparator = new AssetTagNameComparator(orderByAsc);
			}
			else if (orderByCol.equals("usages")) {
				orderByComparator = AssetTagAssetCountComparator.getInstance(
					orderByAsc);
			}

			tagsSearchContainer.setOrderByComparator(orderByComparator);
			tagsSearchContainer.setOrderByType(orderByType);

			long scopeGroupId = _themeDisplay.getScopeGroupId();

			tagsSearchContainer.setResultsAndTotal(
				() -> AssetTagServiceUtil.getTags(
					scopeGroupId, StringPool.BLANK,
					tagsSearchContainer.getStart(),
					tagsSearchContainer.getEnd(),
					tagsSearchContainer.getOrderByComparator()),
				AssetTagServiceUtil.getTagsCount(scopeGroupId, keywords));
		}

		tagsSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_tagsSearchContainer = tagsSearchContainer;

		return _tagsSearchContainer;
	}

	public boolean isShowTagsActions() {
		if (_showTagsActions != null) {
			return _showTagsActions;
		}

		boolean showTagsActions = true;

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		Group group = _themeDisplay.getScopeGroup();

		if (stagingGroupHelper.isLocalLiveGroup(group) ||
			stagingGroupHelper.isRemoteLiveGroup(group)) {

			showTagsActions = false;
		}

		_showTagsActions = showTagsActions;

		return _showTagsActions;
	}

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private List<String> _mergeTagNames;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Boolean _showTagsActions;
	private AssetTag _tag;
	private Long _tagId;
	private SearchContainer<AssetTag> _tagsSearchContainer;
	private final ThemeDisplay _themeDisplay;

}