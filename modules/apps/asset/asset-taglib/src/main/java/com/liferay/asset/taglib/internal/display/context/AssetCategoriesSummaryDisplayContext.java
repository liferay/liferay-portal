/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.internal.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class AssetCategoriesSummaryDisplayContext {

	public AssetCategoriesSummaryDisplayContext(
		HttpServletRequest httpServletRequest) {

		_assetCategories = (List<AssetCategory>)httpServletRequest.getAttribute(
			"liferay-asset:asset-categories-summary:assetCategories");
		_className = (String)httpServletRequest.getAttribute(
			"liferay-asset:asset-categories-summary:className");
		_classPK = GetterUtil.getLong(
			(String)httpServletRequest.getAttribute(
				"liferay-asset:asset-categories-summary:classPK"));
		_displayStyle = GetterUtil.getString(
			(String)httpServletRequest.getAttribute(
				"liferay-asset:asset-categories-summary:displayStyle"),
			"default");
		_paramName = GetterUtil.getString(
			(String)httpServletRequest.getAttribute(
				"liferay-asset:asset-categories-summary:paramName"),
			"categoryId");
		_portletURL = (PortletURL)httpServletRequest.getAttribute(
			"liferay-asset:asset-categories-summary:portletURL");
		_visibleTypes = (int[])httpServletRequest.getAttribute(
			"liferay-asset:asset-categories-summary:visibleTypes");
	}

	public String buildCategoryPath(
			AssetCategory category, ThemeDisplay themeDisplay)
		throws PortalException {

		List<AssetCategory> ancestorCategories = category.getAncestors();

		if (ancestorCategories.isEmpty()) {
			return HtmlUtil.escape(category.getTitle(themeDisplay.getLocale()));
		}

		Collections.reverse(ancestorCategories);

		StringBundler sb = new StringBundler(
			(ancestorCategories.size() * 2) + 1);

		for (AssetCategory ancestorCategory : ancestorCategories) {
			sb.append(
				HtmlUtil.escape(
					ancestorCategory.getTitle(themeDisplay.getLocale())));
			sb.append(" &raquo; ");
		}

		sb.append(HtmlUtil.escape(category.getTitle(themeDisplay.getLocale())));

		return sb.toString();
	}

	public List<AssetCategory> filterCategories(
		List<AssetCategory> categories, AssetVocabulary vocabulary) {

		List<AssetCategory> filteredCategories = new ArrayList<>();

		for (AssetCategory category : categories) {
			if ((category.getVocabularyId() != vocabulary.getVocabularyId()) ||
				!ArrayUtil.contains(
					_visibleTypes, vocabulary.getVisibilityType())) {

				continue;
			}

			filteredCategories.add(category);
		}

		return filteredCategories;
	}

	public List<AssetCategory> getCategories() throws PortalException {
		if (ListUtil.isEmpty(_assetCategories)) {
			return AssetCategoryServiceUtil.getCategories(
				getClassName(), getClassPK());
		}

		return _assetCategories;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getDisplayStyle() {
		return _displayStyle;
	}

	public String getParamName() {
		return _paramName;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public List<AssetVocabulary> getVocabularies(long scopeGroupId)
		throws PortalException {

		long groupId = scopeGroupId;

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			getClassName(), getClassPK());

		if (assetEntry != null) {
			groupId = assetEntry.getGroupId();
		}

		return AssetVocabularyServiceUtil.getGroupVocabularies(
			_getCurrentAndAncestorSiteAndDepotGroupIds(groupId));
	}

	private long[] _getCurrentAndAncestorSiteAndDepotGroupIds(long groupId)
		throws PortalException {

		long[] currentAndAncestorSiteAndDepotGroupIds =
			SiteConnectedGroupGroupProviderUtil.
				getCurrentAndAncestorSiteAndDepotGroupIds(groupId);

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
			groupId);

		if ((depotEntry == null) ||
			(depotEntry.getType() != DepotConstants.TYPE_SPACE)) {

			return currentAndAncestorSiteAndDepotGroupIds;
		}

		Group cmsGroup = GroupLocalServiceUtil.fetchGroup(
			depotEntry.getCompanyId(), GroupConstants.CMS);

		if (cmsGroup == null) {
			return currentAndAncestorSiteAndDepotGroupIds;
		}

		return ArrayUtil.append(
			currentAndAncestorSiteAndDepotGroupIds, cmsGroup.getGroupId());
	}

	private final List<AssetCategory> _assetCategories;
	private final String _className;
	private final long _classPK;
	private final String _displayStyle;
	private final String _paramName;
	private final PortletURL _portletURL;
	private final int[] _visibleTypes;

}