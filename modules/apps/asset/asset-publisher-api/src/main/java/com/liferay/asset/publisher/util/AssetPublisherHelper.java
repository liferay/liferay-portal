/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.info.pagination.InfoPage;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Eudaldo Alonso
 */
@ProviderType
public interface AssetPublisherHelper {

	public static final String SCOPE_ID_CHILD_GROUP_PREFIX = "ChildGroup_";

	public static final String SCOPE_ID_GROUP_PREFIX = "Group_";

	public static final String SCOPE_ID_LAYOUT_PREFIX = "Layout_";

	public static final String SCOPE_ID_LAYOUT_UUID_PREFIX = "LayoutUuid_";

	public static final String SCOPE_ID_PARENT_GROUP_PREFIX = "ParentGroup_";

	public long[] getAssetCategoryIds(PortletPreferences portletPreferences);

	public BaseModelSearchResult<AssetEntry> getAssetEntries(
			AssetEntryQuery assetEntryQuery, Layout layout,
			PortletPreferences portletPreferences, String portletName,
			Locale locale, TimeZone timeZone, long companyId, long scopeGroupId,
			long userId, Map<String, Serializable> attributes, int start,
			int end)
		throws Exception;

	public List<AssetEntry> getAssetEntries(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences,
			PermissionChecker permissionChecker, long[] groupIds,
			boolean deleteMissingAssetEntries, boolean checkPermission)
		throws Exception;

	public List<AssetEntry> getAssetEntries(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences,
			PermissionChecker permissionChecker, long[] groupIds,
			boolean deleteMissingAssetEntries, boolean checkPermission,
			boolean includeNonvisibleAssets)
		throws Exception;

	public List<AssetEntry> getAssetEntries(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences,
			PermissionChecker permissionChecker, long[] groupIds,
			boolean deleteMissingAssetEntries, boolean checkPermission,
			boolean includeNonvisibleAssets, int type)
		throws Exception;

	public List<AssetEntry> getAssetEntries(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences,
			PermissionChecker permissionChecker, long[] groupIds,
			long[] allCategoryIds, String[] allTagNames,
			boolean deleteMissingAssetEntries, boolean checkPermission)
		throws Exception;

	public AssetEntryQuery getAssetEntryQuery(
			PortletPreferences portletPreferences, long groupId, Layout layout,
			long[] overrideAllAssetCategoryIds,
			String[] overrideAllAssetTagNames)
		throws PortalException;

	public AssetEntryQuery getAssetEntryQuery(
			PortletPreferences portletPreferences, long groupId, Layout layout,
			long[] overrideAllAssetCategoryIds,
			String[] overrideAllAssetTagNames, String[] overrideAllKeywords)
		throws PortalException;

	public List<AssetEntryResult> getAssetEntryResults(
			SearchContainer<AssetEntry> searchContainer,
			AssetEntryQuery assetEntryQuery, Layout layout,
			PortletPreferences portletPreferences, String portletName,
			Locale locale, TimeZone timeZone, long companyId, long scopeGroupId,
			long userId, long[] classNameIds,
			Map<String, Serializable> attributes)
		throws Exception;

	public String getAssetSocialURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, AssetEntry assetEntry);

	public String[] getAssetTagNames(PortletPreferences portletPreferences);

	public String getAssetViewURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, AssetEntry assetEntry);

	public String getAssetViewURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, AssetEntry assetEntry,
		boolean viewInContext);

	public String getAssetViewURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		AssetRenderer<?> assetRenderer, AssetEntry assetEntry,
		boolean viewInContext);

	public PortletURL getBaseAssetViewURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		AssetRenderer<?> assetRenderer, AssetEntry assetEntry);

	public long[] getClassNameIds(
		PortletPreferences portletPreferences, long[] availableClassNameIds);

	public long getGroupIdFromScopeId(
			String scopeId, long siteGroupId, boolean privateLayout)
		throws PortalException;

	public long[] getGroupIds(
		PortletPreferences portletPreferences, long scopeGroupId,
		Layout layout);

	public InfoPage<AssetEntry> getInfoPage(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences,
			PermissionChecker permissionChecker, long[] groupIds,
			long[] allCategoryIds, String[] allTagNames,
			boolean deleteMissingAssetEntries, boolean checkPermission,
			int start, int end)
		throws Exception;

	public Group getItemSelectorScopeGroup(Group scopeGroup) throws Exception;

	public String[] getKeywords(PortletPreferences portletPreferences);

	public String getScopeId(Group group, long scopeGroupId);

}