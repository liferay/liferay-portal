/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.item.selector.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalServiceUtil;
import com.liferay.asset.list.service.AssetListEntryServiceUtil;
import com.liferay.asset.list.service.AssetListEntryUsageLocalServiceUtil;
import com.liferay.asset.list.util.AssetListPortletUtil;
import com.liferay.info.collection.provider.item.selector.InfoCollectionProviderItemSelectorCriterion;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class AssetListEntryItemSelectorDisplayContext {

	public AssetListEntryItemSelectorDisplayContext(
		HttpServletRequest httpServletRequest,
		InfoItemServiceRegistry infoItemServiceRegistry,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		Language language, PortletURL portletURL,
		InfoCollectionProviderItemSelectorCriterion
			infoCollectionProviderItemSelectorCriterion) {

		_httpServletRequest = httpServletRequest;
		_infoItemServiceRegistry = infoItemServiceRegistry;
		_infoSearchClassMapperRegistry = infoSearchClassMapperRegistry;
		_language = language;
		_portletURL = portletURL;
		_infoCollectionProviderItemSelectorCriterion =
			infoCollectionProviderItemSelectorCriterion;
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

	public int getAssetListEntryUsageCount(AssetListEntry assetListEntry) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (group.getType() == GroupConstants.TYPE_DEPOT) {
			return AssetListEntryUsageLocalServiceUtil.
				getCompanyAssetListEntryUsagesCount(
					themeDisplay.getCompanyId(),
					PortalUtil.getClassNameId(AssetListEntry.class),
					String.valueOf(assetListEntry.getAssetListEntryId()));
		}

		return AssetListEntryUsageLocalServiceUtil.getAssetListEntryUsagesCount(
			themeDisplay.getScopeGroupId(),
			PortalUtil.getClassNameId(AssetListEntry.class),
			String.valueOf(assetListEntry.getAssetListEntryId()));
	}

	public SearchContainer<AssetListEntry> getSearchContainer() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		SearchContainer<AssetListEntry> searchContainer = new SearchContainer<>(
			portletRequest, _portletURL, null,
			_language.get(
				themeDisplay.getLocale(), "there-are-no-collections"));

		String orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol", "create-date");

		searchContainer.setOrderByCol(orderByCol);

		String orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "asc");

		searchContainer.setOrderByComparator(
			AssetListPortletUtil.getAssetListEntryOrderByComparator(
				orderByCol, orderByType));
		searchContainer.setOrderByType(orderByType);

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (_infoCollectionProviderItemSelectorCriterion.getType() ==
				InfoCollectionProviderItemSelectorCriterion.Type.
					ALL_COLLECTIONS) {

			if (Validator.isNotNull(keywords)) {
				searchContainer.setResultsAndTotal(
					() -> AssetListEntryServiceUtil.getAssetListEntries(
						themeDisplay.getScopeGroupId(), keywords,
						searchContainer.getStart(), searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
					AssetListEntryServiceUtil.getAssetListEntriesCount(
						themeDisplay.getScopeGroupId(), keywords));
			}
			else {
				searchContainer.setResultsAndTotal(
					() -> AssetListEntryServiceUtil.getAssetListEntries(
						themeDisplay.getScopeGroupId(),
						searchContainer.getStart(), searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
					AssetListEntryServiceUtil.getAssetListEntriesCount(
						themeDisplay.getScopeGroupId()));
			}
		}
		else if (_infoCollectionProviderItemSelectorCriterion.getType() ==
					InfoCollectionProviderItemSelectorCriterion.Type.
						SUPPORTED_INFO_FRAMEWORK_COLLECTIONS) {

			String[] classNames = _getInfoItemClassNames();

			if (Validator.isNotNull(keywords)) {
				searchContainer.setResultsAndTotal(
					() -> AssetListEntryServiceUtil.getAssetListEntries(
						new long[] {themeDisplay.getScopeGroupId()}, keywords,
						classNames, searchContainer.getStart(),
						searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
					AssetListEntryServiceUtil.getAssetListEntriesCount(
						new long[] {themeDisplay.getScopeGroupId()}, keywords,
						classNames));
			}
			else {
				searchContainer.setResultsAndTotal(
					() -> AssetListEntryServiceUtil.getAssetListEntries(
						new long[] {themeDisplay.getScopeGroupId()}, classNames,
						searchContainer.getStart(), searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
					AssetListEntryServiceUtil.getAssetListEntriesCount(
						new long[] {themeDisplay.getScopeGroupId()},
						classNames));
			}
		}
		else {
			if (Validator.isNotNull(keywords)) {
				searchContainer.setResultsAndTotal(
					() -> AssetListEntryServiceUtil.getAssetListEntries(
						new long[] {themeDisplay.getScopeGroupId()}, keywords,
						_infoCollectionProviderItemSelectorCriterion.
							getItemSubtype(),
						_infoCollectionProviderItemSelectorCriterion.
							getItemType(),
						searchContainer.getStart(), searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
					AssetListEntryServiceUtil.getAssetListEntriesCount(
						new long[] {themeDisplay.getScopeGroupId()}, keywords,
						_infoCollectionProviderItemSelectorCriterion.
							getItemSubtype(),
						_infoCollectionProviderItemSelectorCriterion.
							getItemType()));
			}
			else {
				searchContainer.setResultsAndTotal(
					() -> AssetListEntryServiceUtil.getAssetListEntries(
						new long[] {themeDisplay.getScopeGroupId()},
						_infoCollectionProviderItemSelectorCriterion.
							getItemSubtype(),
						_infoCollectionProviderItemSelectorCriterion.
							getItemType(),
						searchContainer.getStart(), searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
					AssetListEntryServiceUtil.getAssetListEntriesCount(
						new long[] {themeDisplay.getScopeGroupId()},
						_infoCollectionProviderItemSelectorCriterion.
							getItemSubtype(),
						_infoCollectionProviderItemSelectorCriterion.
							getItemType()));
			}
		}

		return searchContainer;
	}

	public String getSubtype(AssetListEntry assetListEntry) {
		if (Validator.isNull(assetListEntry.getAssetEntrySubtype())) {
			return StringPool.BLANK;
		}

		String subtypeLabel = _getAssetEntrySubtypeSubtypeLabel(assetListEntry);

		if (Validator.isNull(subtypeLabel)) {
			return StringPool.BLANK;
		}

		return subtypeLabel;
	}

	public String getType(AssetListEntry assetListEntry, Locale locale) {
		return ResourceActionsUtil.getModelResource(
			locale, assetListEntry.getAssetEntryType());
	}

	private String _getAssetEntrySubtypeSubtypeLabel(
		AssetListEntry assetListEntry) {

		long classTypeId = GetterUtil.getLong(
			assetListEntry.getAssetEntrySubtype(), -1);

		if (classTypeId < 0) {
			return StringPool.BLANK;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetListEntry.getAssetEntryType());

		if ((assetRendererFactory == null) ||
			!assetRendererFactory.isSupportsClassTypes()) {

			return StringPool.BLANK;
		}

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			ClassType classType = classTypeReader.getClassType(
				classTypeId, themeDisplay.getLocale());

			return classType.getName();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringPool.BLANK;
	}

	private String[] _getInfoItemClassNames() {

		// LPS-166852

		Set<String> infoItemClassNames = new HashSet<>();

		for (String className :
				_infoItemServiceRegistry.getInfoItemClassNames(
					InfoItemFormProvider.class)) {

			infoItemClassNames.add(className);
			infoItemClassNames.add(
				_infoSearchClassMapperRegistry.getSearchClassName(className));
		}

		return ArrayUtil.toStringArray(infoItemClassNames);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListEntryItemSelectorDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final InfoCollectionProviderItemSelectorCriterion
		_infoCollectionProviderItemSelectorCriterion;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;
	private final Language _language;
	private final PortletURL _portletURL;

}