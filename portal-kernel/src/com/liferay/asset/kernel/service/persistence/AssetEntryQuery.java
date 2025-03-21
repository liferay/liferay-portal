/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service.persistence;

import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Juan Fernández
 */
public class AssetEntryQuery {

	public static final String[] ORDER_BY_COLUMNS = {
		"title", "createDate", "modifiedDate", "publishDate", "expirationDate",
		"priority", "viewCount", "ratings", "ratingsTotalScore"
	};

	public static String checkOrderByCol(String orderByCol) {
		if (ArrayUtil.contains(ORDER_BY_COLUMNS, orderByCol) ||
			((orderByCol != null) &&
			 orderByCol.startsWith(
				 DDMStructureManager.STRUCTURE_INDEXER_FIELD_PREFIX))) {

			return orderByCol;
		}

		return ORDER_BY_COLUMNS[2];
	}

	public static String checkOrderByType(String orderByType) {
		if ((orderByType == null) ||
			StringUtil.equalsIgnoreCase(orderByType, "DESC")) {

			return "DESC";
		}

		return "ASC";
	}

	public AssetEntryQuery() {
		Date date = new Date();

		_expirationDate = date;
		_publishDate = date;
	}

	public AssetEntryQuery(AssetEntryQuery assetEntryQuery) {
		setAllCategoryIds(assetEntryQuery.getAllCategoryIds());
		setAllKeywords(assetEntryQuery.getAllKeywords());
		setAllTagIdsArray(assetEntryQuery.getAllTagIdsArray());
		setAndOperator(assetEntryQuery.isAndOperator());
		setAnyCategoryIds(assetEntryQuery.getAnyCategoryIds());
		setAnyKeywords(assetEntryQuery.getAnyKeywords());
		setAnyTagIds(assetEntryQuery.getAnyTagIds());
		setAttributes(assetEntryQuery.getAttributes());
		setClassNameIds(assetEntryQuery.getClassNameIds());
		setClassTypeIds(assetEntryQuery.getClassTypeIds());
		setDescription(assetEntryQuery.getDescription());
		setEnablePermissions(assetEntryQuery.isEnablePermissions());
		setEnd(assetEntryQuery.getEnd());
		setExcludeZeroViewCount(assetEntryQuery.isExcludeZeroViewCount());
		setExpirationDate(assetEntryQuery.getExpirationDate());
		setGroupIds(assetEntryQuery.getGroupIds());
		setKeywords(assetEntryQuery.getKeywords());
		setLayout(assetEntryQuery.getLayout());
		setLinkedAssetEntryIds(assetEntryQuery.getLinkedAssetEntryIds());
		setListable(assetEntryQuery.isListable());
		setNotAllCategoryIds(assetEntryQuery.getNotAllCategoryIds());
		setNotAllKeywords(assetEntryQuery.getNotAllKeywords());
		setNotAllTagIdsArray(assetEntryQuery.getNotAllTagIdsArray());
		setNotAnyCategoryIds(assetEntryQuery.getNotAnyCategoryIds());
		setNotAnyKeywords(assetEntryQuery.getNotAnyKeywords());
		setNotAnyTagIds(assetEntryQuery.getNotAnyTagIds());
		setOrderByCol1(assetEntryQuery.getOrderByCol1());
		setOrderByCol2(assetEntryQuery.getOrderByCol2());
		setOrderByType1(assetEntryQuery.getOrderByType1());
		setOrderByType2(assetEntryQuery.getOrderByType2());
		setPaginationType(assetEntryQuery.getPaginationType());
		setPublishDate(assetEntryQuery.getPublishDate());
		setStart(assetEntryQuery.getStart());
		setTitle(assetEntryQuery.getTitle());
		setUserName(assetEntryQuery.getUserName());
		setVisible(assetEntryQuery.isVisible());
	}

	public AssetEntryQuery(
		long[] classNameIds, SearchContainer<?> searchContainer) {

		this();

		setClassNameIds(classNameIds);
		_start = searchContainer.getStart();
		_end = searchContainer.getEnd();

		if (Validator.isNotNull(searchContainer.getOrderByCol())) {
			setOrderByCol1(searchContainer.getOrderByCol());
			setOrderByType1(searchContainer.getOrderByType());
		}

		PortletRequest portletRequest = searchContainer.getPortletRequest();

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_groupIds = new long[] {themeDisplay.getScopeGroupId()};

		long categoryId = ParamUtil.getLong(portletRequest, "categoryId");

		if (categoryId > 0) {
			_allCategoryIds = new long[] {categoryId};
		}

		String tagName = ParamUtil.getString(portletRequest, "tag");

		if (Validator.isNotNull(tagName)) {
			_allTagIds = AssetTagLocalServiceUtil.getTagIds(
				PortalUtil.getSiteGroupId(themeDisplay.getScopeGroupId()),
				new String[] {tagName});

			if (_allTagIds.length == 0) {
				_allTagIds = new long[] {0};
			}

			_allTagIdsArray = new long[][] {_allTagIds};
		}
	}

	public AssetEntryQuery(
		String className, SearchContainer<?> searchContainer) {

		this(
			new long[] {PortalUtil.getClassNameId(className)}, searchContainer);
	}

	public void addAllTagIdsArray(long[] allTagsIds) {
		if (allTagsIds.length == 0) {
			return;
		}

		_allTagIdsArray = ArrayUtil.append(_allTagIdsArray, allTagsIds);

		_allTagIds = _flattenTagIds(_allTagIdsArray);
	}

	public void addNotAllTagIdsArray(long[] notAllTagsIds) {
		if (notAllTagsIds.length == 0) {
			return;
		}

		_notAllTagIdsArray = ArrayUtil.append(
			_notAllTagIdsArray, notAllTagsIds);

		_notAllTagIds = _flattenTagIds(_notAllTagIdsArray);
	}

	public long[] getAllCategoryIds() {
		return _allCategoryIds;
	}

	public String[] getAllKeywords() {
		return _allKeywords;
	}

	public long[] getAllTagIds() {
		return _allTagIds;
	}

	public long[][] getAllTagIdsArray() {
		return _allTagIdsArray;
	}

	public long[] getAnyCategoryIds() {
		return _anyCategoryIds;
	}

	public String[] getAnyKeywords() {
		return _anyKeywords;
	}

	public long[] getAnyTagIds() {
		return _anyTagIds;
	}

	public Serializable getAttribute(String name) {
		return _attributes.get(name);
	}

	public Map<String, Serializable> getAttributes() {
		return _attributes;
	}

	public long[] getClassNameIds() {
		return _classNameIds;
	}

	public long[] getClassTypeIds() {
		return _classTypeIds;
	}

	public String getDescription() {
		return _description;
	}

	public int getEnd() {
		return _end;
	}

	public Date getExpirationDate() {
		return _expirationDate;
	}

	public long[] getGroupIds() {
		return _groupIds;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getAllKeywords()}
	 */
	@Deprecated
	public String getKeywords() {
		return _keywords;
	}

	public Layout getLayout() {
		return _layout;
	}

	public long[] getLinkedAssetEntryIds() {
		return _linkedAssetEntryIds;
	}

	public long[] getNotAllCategoryIds() {
		return _notAllCategoryIds;
	}

	public String[] getNotAllKeywords() {
		return _notAllKeywords;
	}

	public long[] getNotAllTagIds() {
		return _notAllTagIds;
	}

	public long[][] getNotAllTagIdsArray() {
		return _notAllTagIdsArray;
	}

	public long[] getNotAnyCategoryIds() {
		return _notAnyCategoryIds;
	}

	public String[] getNotAnyKeywords() {
		return _notAnyKeywords;
	}

	public long[] getNotAnyTagIds() {
		return _notAnyTagIds;
	}

	public String getOrderByCol1() {
		return checkOrderByCol(_orderByCol1);
	}

	public String getOrderByCol2() {
		return checkOrderByCol(_orderByCol2);
	}

	public String getOrderByType1() {
		return checkOrderByType(_orderByType1);
	}

	public String getOrderByType2() {
		return checkOrderByType(_orderByType2);
	}

	public String getPaginationType() {
		return _paginationType;
	}

	public Date getPublishDate() {
		return _publishDate;
	}

	public int getStart() {
		return _start;
	}

	public String getTitle() {
		return _title;
	}

	public String getUserName() {
		return _userName;
	}

	public boolean isAndOperator() {
		return _andOperator;
	}

	public boolean isEnablePermissions() {
		return _enablePermissions;
	}

	public boolean isExcludeZeroViewCount() {
		return _excludeZeroViewCount;
	}

	public Boolean isListable() {
		return _listable;
	}

	public Boolean isVisible() {
		return _visible;
	}

	public void setAllCategoryIds(long[] allCategoryIds) {
		_allCategoryIds = allCategoryIds;

		_toString = null;
	}

	public void setAllKeywords(String[] allKeywords) {
		_allKeywords = allKeywords;

		_toString = null;
	}

	public void setAllTagIds(long[] allTagIds) {
		_allTagIds = allTagIds;

		_allTagIdsArray = _expandTagIds(allTagIds);

		_toString = null;
	}

	public void setAllTagIdsArray(long[][] allTagIdsArray) {
		_allTagIdsArray = allTagIdsArray;

		_allTagIds = _flattenTagIds(allTagIdsArray);

		_toString = null;
	}

	public void setAndOperator(boolean andOperator) {
		_andOperator = andOperator;
	}

	public void setAnyCategoryIds(long[] anyCategoryIds) {
		_anyCategoryIds = anyCategoryIds;

		_toString = null;
	}

	public void setAnyKeywords(String[] anyKeywords) {
		_anyKeywords = anyKeywords;

		_toString = null;
	}

	public void setAnyTagIds(long[] anyTagIds) {
		_anyTagIds = anyTagIds;

		_toString = null;
	}

	public void setAttribute(String name, Serializable value) {
		_attributes.put(name, value);
	}

	public void setAttributes(Map<String, Serializable> attributes) {
		if (_attributes == null) {
			_attributes = new HashMap<>();
		}
		else {
			_attributes = attributes;
		}
	}

	public void setClassName(String className) {
		_classNameIds = new long[] {PortalUtil.getClassNameId(className)};

		_toString = null;
	}

	public void setClassNameIds(long[] classNameIds) {
		_classNameIds = classNameIds;

		_toString = null;
	}

	public void setClassTypeIds(long[] classTypeIds) {
		_classTypeIds = classTypeIds;

		_toString = null;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setEnablePermissions(boolean enablePermissions) {
		_enablePermissions = enablePermissions;
	}

	public void setEnd(int end) {
		_end = end;

		_toString = null;
	}

	public void setExcludeZeroViewCount(boolean excludeZeroViewCount) {
		_excludeZeroViewCount = excludeZeroViewCount;

		_toString = null;
	}

	public void setExpirationDate(Date expirationDate) {
		_expirationDate = expirationDate;

		_toString = null;
	}

	public void setGroupIds(long[] groupIds) {
		_groupIds = groupIds;

		_toString = null;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #setAllKeywords(String[])}
	 */
	@Deprecated
	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	public void setLayout(Layout layout) {
		_layout = layout;

		_toString = null;
	}

	public void setLinkedAssetEntryIds(long[] linkedAssetEntryIds) {
		_linkedAssetEntryIds = linkedAssetEntryIds;

		_toString = null;
	}

	public void setListable(Boolean listable) {
		_listable = listable;
	}

	public void setNotAllCategoryIds(long[] notAllCategoryIds) {
		_notAllCategoryIds = notAllCategoryIds;

		_toString = null;
	}

	public void setNotAllKeywords(String[] notAllKeywords) {
		_notAllKeywords = notAllKeywords;

		_toString = null;
	}

	public void setNotAllTagIds(long[] notAllTagIds) {
		_notAllTagIds = notAllTagIds;

		_notAllTagIdsArray = _expandTagIds(notAllTagIds);

		_toString = null;
	}

	public void setNotAllTagIdsArray(long[][] notAllTagIdsArray) {
		_notAllTagIdsArray = notAllTagIdsArray;

		_notAllTagIds = _flattenTagIds(notAllTagIdsArray);

		_toString = null;
	}

	public void setNotAnyCategoryIds(long[] notAnyCategoryIds) {
		_notAnyCategoryIds = notAnyCategoryIds;

		_toString = null;
	}

	public void setNotAnyKeywords(String[] notAnyKeywords) {
		_notAnyKeywords = notAnyKeywords;

		_toString = null;
	}

	public void setNotAnyTagIds(long[] notAnyTagIds) {
		_notAnyTagIds = notAnyTagIds;

		_toString = null;
	}

	public void setOrderByCol1(String orderByCol1) {
		_orderByCol1 = orderByCol1;

		_toString = null;
	}

	public void setOrderByCol2(String orderByCol2) {
		_orderByCol2 = orderByCol2;

		_toString = null;
	}

	public void setOrderByType1(String orderByType1) {
		_orderByType1 = orderByType1;

		_toString = null;
	}

	public void setOrderByType2(String orderByType2) {
		_orderByType2 = orderByType2;

		_toString = null;
	}

	public void setPaginationType(String paginationType) {
		_paginationType = paginationType;

		_toString = null;
	}

	public void setPublishDate(Date publishDate) {
		_publishDate = publishDate;

		_toString = null;
	}

	public void setStart(int start) {
		_start = start;

		_toString = null;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public void setVisible(Boolean visible) {
		_visible = visible;

		_toString = null;
	}

	@Override
	public String toString() {
		if (_toString != null) {
			return _toString;
		}

		StringBundler sb = new StringBundler(69);

		sb.append("{allCategoryIds=");
		sb.append(StringUtil.merge(_allCategoryIds));
		sb.append(", allKeywords=");
		sb.append(StringUtil.merge(_allKeywords));
		sb.append(", allTagIds=");
		sb.append(StringUtil.merge(_allTagIds));
		sb.append(", andOperator=");
		sb.append(_andOperator);
		sb.append(", anyCategoryIds=");
		sb.append(StringUtil.merge(_anyCategoryIds));
		sb.append(", anyKeywords=");
		sb.append(StringUtil.merge(_anyKeywords));
		sb.append(", anyTagIds=");
		sb.append(StringUtil.merge(_anyTagIds));
		sb.append(", classNameIds=");
		sb.append(StringUtil.merge(_classNameIds));
		sb.append(", classTypeIds=");
		sb.append(StringUtil.merge(_classTypeIds));
		sb.append(", description=");
		sb.append(_description);

		if (_layout != null) {
			sb.append(", layout=");
			sb.append(_layout.getPlid());
		}

		sb.append(", end=");
		sb.append(_end);
		sb.append(", excludeZeroViewCount=");
		sb.append(_excludeZeroViewCount);
		sb.append(", expirationDate=");
		sb.append(_expirationDate);
		sb.append(", groupIds=");
		sb.append(StringUtil.merge(_groupIds));
		sb.append(", keywords=");
		sb.append(_keywords);
		sb.append(", linkedAssetEntryIds=");
		sb.append(_linkedAssetEntryIds);
		sb.append(", listable=");
		sb.append(_listable);
		sb.append(", notAllCategoryIds=");
		sb.append(StringUtil.merge(_notAllCategoryIds));
		sb.append(", notAllKeywords=");
		sb.append(StringUtil.merge(_notAllKeywords));
		sb.append(", notAllTagIds=");
		sb.append(StringUtil.merge(_notAllTagIds));
		sb.append(", notAnyCategoryIds=");
		sb.append(StringUtil.merge(_notAnyCategoryIds));
		sb.append(", notAnyKeywords=");
		sb.append(StringUtil.merge(_notAnyKeywords));
		sb.append(", notAnyTagIds=");
		sb.append(StringUtil.merge(_notAnyTagIds));
		sb.append(", orderByCol1=");
		sb.append(_orderByCol1);
		sb.append(", orderByCol2=");
		sb.append(_orderByCol2);
		sb.append(", orderByType1=");
		sb.append(_orderByType1);
		sb.append(", orderByType2=");
		sb.append(_orderByType2);
		sb.append(", paginationType=");
		sb.append(_paginationType);
		sb.append(", publishDate=");
		sb.append(_publishDate);
		sb.append(", start=");
		sb.append(_start);
		sb.append(", title=");
		sb.append(_title);
		sb.append(", userName=");
		sb.append(_userName);
		sb.append(", visible=");
		sb.append(_visible);
		sb.append("}");

		_toString = sb.toString();

		return _toString;
	}

	private long[][] _expandTagIds(long[] tagIds) {
		long[][] tagIdsArray = new long[tagIds.length][1];

		for (int i = 0; i < tagIds.length; i++) {
			tagIdsArray[i][0] = tagIds[i];
		}

		return tagIdsArray;
	}

	private long[] _flattenTagIds(long[][] tagIdsArray) {
		List<Long> tagIdsList = new ArrayList<>();

		for (long[] tagIds : tagIdsArray) {
			for (long tagId : tagIds) {
				tagIdsList.add(tagId);
			}
		}

		return ArrayUtil.toArray(tagIdsList.toArray(new Long[0]));
	}

	private long[] _allCategoryIds = new long[0];
	private String[] _allKeywords = new String[0];
	private long[] _allTagIds = new long[0];
	private long[][] _allTagIdsArray = new long[0][];
	private boolean _andOperator;
	private long[] _anyCategoryIds = new long[0];
	private String[] _anyKeywords = new String[0];
	private long[] _anyTagIds = new long[0];
	private Map<String, Serializable> _attributes = new HashMap<>();
	private long[] _classNameIds = new long[0];
	private long[] _classTypeIds = new long[0];
	private String _description;
	private boolean _enablePermissions;
	private int _end = QueryUtil.ALL_POS;
	private boolean _excludeZeroViewCount;
	private Date _expirationDate;
	private long[] _groupIds = new long[0];
	private String _keywords;
	private Layout _layout;
	private long[] _linkedAssetEntryIds;
	private Boolean _listable = true;
	private long[] _notAllCategoryIds = new long[0];
	private String[] _notAllKeywords = new String[0];
	private long[] _notAllTagIds = new long[0];
	private long[][] _notAllTagIdsArray = new long[0][];
	private long[] _notAnyCategoryIds = new long[0];
	private String[] _notAnyKeywords = new String[0];
	private long[] _notAnyTagIds = new long[0];
	private String _orderByCol1;
	private String _orderByCol2;
	private String _orderByType1;
	private String _orderByType2;
	private String _paginationType;
	private Date _publishDate;
	private int _start = QueryUtil.ALL_POS;
	private String _title;
	private String _toString;
	private String _userName;
	private Boolean _visible = Boolean.TRUE;

}