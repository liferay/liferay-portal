/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.util;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetEntryService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.util.AssetEntryResult;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherSelectionStyleConfigurationUtil;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.asset.util.AssetHelper;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.context.RequestContextMapper;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration",
	service = AssetPublisherHelper.class
)
public class AssetPublisherHelperImpl implements AssetPublisherHelper {

	@Override
	public long[] getAssetCategoryIds(PortletPreferences portletPreferences) {
		long[] assetCategoryIds = new long[0];

		for (int i = 0; true; i++) {
			String[] queryValues = portletPreferences.getValues(
				"queryValues" + i, null);

			if (ArrayUtil.isEmpty(queryValues)) {
				break;
			}

			boolean queryContains = GetterUtil.getBoolean(
				portletPreferences.getValue(
					"queryContains" + i, StringPool.BLANK));
			boolean queryAndOperator = GetterUtil.getBoolean(
				portletPreferences.getValue(
					"queryAndOperator" + i, StringPool.BLANK));
			String queryName = portletPreferences.getValue(
				"queryName" + i, StringPool.BLANK);

			if (Objects.equals(queryName, "assetCategories") && queryContains &&
				(queryAndOperator || (queryValues.length == 1))) {

				assetCategoryIds = ArrayUtil.append(
					assetCategoryIds, GetterUtil.getLongValues(queryValues));
			}
		}

		return assetCategoryIds;
	}

	@Override
	public BaseModelSearchResult<AssetEntry> getAssetEntries(
			AssetEntryQuery assetEntryQuery, Layout layout,
			PortletPreferences portletPreferences, String portletName,
			Locale locale, TimeZone timeZone, long companyId, long scopeGroupId,
			long userId, Map<String, Serializable> attributes, int start,
			int end)
		throws Exception {

		if (_isSearchWithIndex(portletName, assetEntryQuery)) {
			return _assetHelper.searchAssetEntries(
				assetEntryQuery,
				_filterAssetCategoryIds(assetEntryQuery, portletPreferences),
				getAssetTagNames(portletPreferences), attributes, companyId,
				assetEntryQuery.getKeywords(), layout, locale, scopeGroupId,
				timeZone, userId, start, end);
		}

		int total = _assetEntryService.getEntriesCount(assetEntryQuery);

		assetEntryQuery.setEnd(end);
		assetEntryQuery.setStart(start);

		List<AssetEntry> assetEntries = _assetEntryService.getEntries(
			assetEntryQuery);

		return new BaseModelSearchResult<>(assetEntries, total);
	}

	@Override
	public List<AssetEntry> getAssetEntries(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences,
			PermissionChecker permissionChecker, long[] groupIds,
			boolean deleteMissingAssetEntries, boolean checkPermission)
		throws Exception {

		return getAssetEntries(
			portletRequest, portletPreferences, permissionChecker, groupIds,
			deleteMissingAssetEntries, checkPermission, false);
	}

	@Override
	public List<AssetEntry> getAssetEntries(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences,
			PermissionChecker permissionChecker, long[] groupIds,
			boolean deleteMissingAssetEntries, boolean checkPermission,
			boolean includeNonvisibleAssets)
		throws Exception {

		return getAssetEntries(
			portletRequest, portletPreferences, permissionChecker, groupIds,
			deleteMissingAssetEntries, checkPermission, includeNonvisibleAssets,
			AssetRendererFactory.TYPE_LATEST_APPROVED);
	}

	@Override
	public List<AssetEntry> getAssetEntries(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences,
			PermissionChecker permissionChecker, long[] groupIds,
			boolean deleteMissingAssetEntries, boolean checkPermission,
			boolean includeNonvisibleAssets, int type)
		throws Exception {

		List<AssetEntry> assetEntries = new ArrayList<>();

		String[] assetEntryXmls = portletPreferences.getValues(
			"assetEntryXml", new String[0]);

		List<String> missingAssetEntryUuids = new ArrayList<>();

		for (String assetEntryXml : assetEntryXmls) {
			Document document = SAXReaderUtil.read(assetEntryXml);

			Element rootElement = document.getRootElement();

			String assetEntryUuid = rootElement.elementText("asset-entry-uuid");

			String assetEntryType = rootElement.elementText("asset-entry-type");

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(assetEntryType);

			String portletId = null;

			if (assetRendererFactory != null) {
				portletId = assetRendererFactory.getPortletId();
			}

			AssetEntry assetEntry = null;

			for (long groupId : groupIds) {
				Group group = _groupLocalService.fetchGroup(groupId);

				if ((portletId != null) && group.isStagingGroup() &&
					!group.isStagedPortlet(portletId)) {

					groupId = group.getLiveGroupId();
				}

				assetEntry = _assetEntryLocalService.fetchEntry(
					groupId, assetEntryUuid);

				if (assetEntry != null) {
					break;
				}
			}

			if (assetEntry == null) {
				if (deleteMissingAssetEntries) {
					missingAssetEntryUuids.add(assetEntryUuid);
				}

				continue;
			}

			if (!assetEntry.isVisible() && !includeNonvisibleAssets) {
				continue;
			}

			assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						assetEntry.getClassName());

			if (!assetRendererFactory.isActive(
					permissionChecker.getCompanyId())) {

				if (deleteMissingAssetEntries) {
					missingAssetEntryUuids.add(assetEntryUuid);
				}

				continue;
			}

			if (checkPermission) {
				AssetRenderer<?> assetRenderer =
					assetRendererFactory.getAssetRenderer(
						assetEntry.getClassPK(), type);

				if (!assetRenderer.isDisplayable() &&
					!includeNonvisibleAssets) {

					continue;
				}
				else if (!assetRenderer.hasViewPermission(permissionChecker)) {
					assetRenderer = assetRendererFactory.getAssetRenderer(
						assetEntry.getClassPK(),
						AssetRendererFactory.TYPE_LATEST_APPROVED);

					if (!assetRenderer.hasViewPermission(permissionChecker)) {
						continue;
					}
				}
			}

			assetEntries.add(assetEntry);
		}

		if (deleteMissingAssetEntries) {
			_removeAndStoreSelection(
				missingAssetEntryUuids, portletPreferences);

			if (!missingAssetEntryUuids.isEmpty()) {
				SessionMessages.add(
					portletRequest, "deletedMissingAssetEntries",
					missingAssetEntryUuids);
			}
		}

		return assetEntries;
	}

	@Override
	public List<AssetEntry> getAssetEntries(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences,
			PermissionChecker permissionChecker, long[] groupIds,
			long[] allCategoryIds, String[] allTagNames,
			boolean deleteMissingAssetEntries, boolean checkPermission)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		AssetListEntry assetListEntry = AssetPublisherUtil.getAssetListEntry(
			true, themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
			portletPreferences);

		if (assetListEntry != null) {
			long[] segmentsEntryIds = _getSegmentsEntryIds(portletRequest);

			String acClientUserId = GetterUtil.getString(
				portletRequest.getAttribute(
					SegmentsWebKeys.SEGMENTS_ANONYMOUS_USER_ID));

			InfoPage<AssetEntry> infoPage =
				_assetListAssetEntryProvider.getAssetEntriesInfoPage(
					assetListEntry, segmentsEntryIds, null, null,
					StringPool.BLANK, acClientUserId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			return (List<AssetEntry>)infoPage.getPageItems();
		}

		List<AssetEntry> assetEntries = getAssetEntries(
			portletRequest, portletPreferences, permissionChecker, groupIds,
			deleteMissingAssetEntries, checkPermission);

		if (assetEntries.isEmpty() ||
			(ArrayUtil.isEmpty(allCategoryIds) &&
			 ArrayUtil.isEmpty(allTagNames))) {

			return assetEntries;
		}

		if (ArrayUtil.isNotEmpty(allCategoryIds)) {
			assetEntries = _filterAssetCategoriesAssetEntries(
				assetEntries, allCategoryIds);
		}

		if (ArrayUtil.isNotEmpty(allTagNames)) {
			assetEntries = _filterAssetTagNamesAssetEntries(
				assetEntries, _normalizeAssetTagNames(allTagNames));
		}

		return assetEntries;
	}

	@Override
	public AssetEntryQuery getAssetEntryQuery(
			PortletPreferences portletPreferences, long groupId, Layout layout,
			long[] overrideAllAssetCategoryIds,
			String[] overrideAllAssetTagNames)
		throws PortalException {

		return getAssetEntryQuery(
			portletPreferences, groupId, layout, overrideAllAssetCategoryIds,
			overrideAllAssetTagNames, null);
	}

	@Override
	public AssetEntryQuery getAssetEntryQuery(
			PortletPreferences portletPreferences, long groupId, Layout layout,
			long[] overrideAllAssetCategoryIds,
			String[] overrideAllAssetTagNames, String[] overrideAllKeywords)
		throws PortalException {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		long[] groupIds = getGroupIds(portletPreferences, groupId, layout);

		_setCategoriesAndTagsAndKeywords(
			assetEntryQuery, portletPreferences, groupIds,
			overrideAllAssetCategoryIds, overrideAllAssetTagNames,
			overrideAllKeywords);

		assetEntryQuery.setAttribute("headOrShowNonindexable", Boolean.TRUE);
		assetEntryQuery.setGroupIds(groupIds);

		boolean anyAssetType = GetterUtil.getBoolean(
			portletPreferences.getValue("anyAssetType", null), true);

		if (!anyAssetType) {
			long[] availableClassNameIds =
				AssetRendererFactoryRegistryUtil.getClassNameIds(
					layout.getCompanyId());

			assetEntryQuery.setClassNameIds(
				getClassNameIds(portletPreferences, availableClassNameIds));
		}

		assetEntryQuery.setClassTypeIds(
			GetterUtil.getLongValues(
				portletPreferences.getValues("classTypeIds", null)));
		assetEntryQuery.setEnablePermissions(
			GetterUtil.getBoolean(
				portletPreferences.getValue("enablePermissions", null)));
		assetEntryQuery.setExcludeZeroViewCount(
			GetterUtil.getBoolean(
				portletPreferences.getValue("excludeZeroViewCount", null)));

		boolean showOnlyLayoutAssets = GetterUtil.getBoolean(
			portletPreferences.getValue("showOnlyLayoutAssets", null));

		if (showOnlyLayoutAssets) {
			assetEntryQuery.setLayout(layout);
		}

		assetEntryQuery.setListable(null);

		String orderByColumn1 = GetterUtil.getString(
			portletPreferences.getValue("orderByColumn1", "modifiedDate"));

		assetEntryQuery.setOrderByCol1(orderByColumn1);

		String orderByColumn2 = GetterUtil.getString(
			portletPreferences.getValue("orderByColumn2", "title"));

		assetEntryQuery.setOrderByCol2(orderByColumn2);

		assetEntryQuery.setOrderByType1(
			GetterUtil.getString(
				portletPreferences.getValue("orderByType1", "DESC")));
		assetEntryQuery.setOrderByType2(
			GetterUtil.getString(
				portletPreferences.getValue("orderByType2", "ASC")));

		return assetEntryQuery;
	}

	@Override
	public List<AssetEntryResult> getAssetEntryResults(
			SearchContainer<AssetEntry> searchContainer,
			AssetEntryQuery assetEntryQuery, Layout layout,
			PortletPreferences portletPreferences, String portletName,
			Locale locale, TimeZone timeZone, long companyId, long scopeGroupId,
			long userId, long[] classNameIds,
			Map<String, Serializable> attributes)
		throws Exception {

		if (!_isShowAssetEntryResults(portletName, assetEntryQuery)) {
			return Collections.emptyList();
		}

		long assetVocabularyId = GetterUtil.getLong(
			portletPreferences.getValue("assetVocabularyId", null));

		if (assetVocabularyId > 0) {
			return _getAssetEntryResultsByVocabulary(
				searchContainer, assetEntryQuery, layout, portletPreferences,
				portletName, locale, timeZone, companyId, scopeGroupId, userId,
				classNameIds, assetVocabularyId, attributes);
		}
		else if (assetVocabularyId <= -1) {
			return _getAssetEntryResultsByClassName(
				searchContainer, assetEntryQuery, layout, portletPreferences,
				portletName, locale, timeZone, companyId, scopeGroupId, userId,
				classNameIds, attributes);
		}

		return _getAssetEntryResultsByDefault(
			searchContainer, assetEntryQuery, layout, portletPreferences,
			portletName, locale, timeZone, companyId, scopeGroupId, userId,
			classNameIds, attributes);
	}

	@Override
	public String getAssetSocialURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, AssetEntry assetEntry) {

		AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

		PortletURL viewFullContentURL = PortletURLBuilder.create(
			getBaseAssetViewURL(
				liferayPortletRequest, liferayPortletResponse, assetRenderer,
				assetEntry)
		).buildPortletURL();

		try {
			String viewURL = assetRenderer.getURLViewInContext(
				liferayPortletRequest, liferayPortletResponse,
				viewFullContentURL.toString());

			if (Validator.isNotNull(viewURL)) {
				return _normalizeURL(viewURL);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return _normalizeURL(viewFullContentURL.toString());
	}

	@Override
	public String[] getAssetTagNames(PortletPreferences portletPreferences) {
		List<String> allAssetTagNames = new ArrayList<>();

		for (int i = 0; true; i++) {
			String[] queryValues = portletPreferences.getValues(
				"queryValues" + i, null);

			if (ArrayUtil.isEmpty(queryValues)) {
				break;
			}

			boolean queryContains = GetterUtil.getBoolean(
				portletPreferences.getValue(
					"queryContains" + i, StringPool.BLANK));
			boolean queryAndOperator = GetterUtil.getBoolean(
				portletPreferences.getValue(
					"queryAndOperator" + i, StringPool.BLANK));
			String queryName = portletPreferences.getValue(
				"queryName" + i, StringPool.BLANK);

			if (Objects.equals(queryName, "assetTags") && queryContains &&
				(queryAndOperator || (queryValues.length == 1))) {

				queryValues = _normalizeAssetTagNames(queryValues);

				Collections.addAll(allAssetTagNames, queryValues);
			}
		}

		return allAssetTagNames.toArray(new String[0]);
	}

	@Override
	public String getAssetViewURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, AssetEntry assetEntry) {

		return getAssetViewURL(
			liferayPortletRequest, liferayPortletResponse, assetEntry, false);
	}

	@Override
	public String getAssetViewURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, AssetEntry assetEntry,
		boolean viewInContext) {

		return getAssetViewURL(
			liferayPortletRequest, liferayPortletResponse,
			assetEntry.getAssetRenderer(), assetEntry, viewInContext);
	}

	@Override
	public String getAssetViewURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		AssetRenderer<?> assetRenderer, AssetEntry assetEntry,
		boolean viewInContext) {

		PortletURL redirectURL = PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setParameter(
			"assetEntryId", assetEntry.getEntryId()
		).setParameter(
			"cur", ParamUtil.getInteger(liferayPortletRequest, "cur")
		).setParameter(
			"delta",
			() -> {
				int delta = ParamUtil.getInteger(
					liferayPortletRequest, "delta");

				if (delta > 0) {
					return delta;
				}

				return null;
			}
		).setParameter(
			"resetCur", ParamUtil.getBoolean(liferayPortletRequest, "resetCur")
		).buildPortletURL();

		PortletURL viewFullContentURL = PortletURLBuilder.create(
			getBaseAssetViewURL(
				liferayPortletRequest, liferayPortletResponse, assetRenderer,
				assetEntry)
		).setRedirect(
			redirectURL
		).buildPortletURL();

		String viewURL = null;

		if (viewInContext) {
			try {
				String noSuchEntryRedirect = viewFullContentURL.toString();

				viewURL = assetRenderer.getURLViewInContext(
					liferayPortletRequest, liferayPortletResponse,
					noSuchEntryRedirect);

				if (Validator.isNotNull(viewURL) &&
					!Objects.equals(viewURL, noSuchEntryRedirect)) {

					String redirect = ParamUtil.getString(
						liferayPortletRequest, "redirect");

					if (Validator.isNull(redirect)) {
						redirect = _portal.getCurrentURL(liferayPortletRequest);
					}

					viewURL = HttpComponentsUtil.setParameter(
						viewURL, "redirect", redirect);
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		if (Validator.isNull(viewURL)) {
			viewURL = viewFullContentURL.toString();
		}

		return viewURL;
	}

	@Override
	public PortletURL getBaseAssetViewURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		AssetRenderer<?> assetRenderer, AssetEntry assetEntry) {

		PortletURL baseAssetViewURL = PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCPath(
			"/view_content.jsp"
		).setParameter(
			"assetEntryId", assetEntry.getEntryId()
		).setParameter(
			"type",
			() -> {
				AssetRendererFactory<?> assetRendererFactory =
					assetRenderer.getAssetRendererFactory();

				return assetRendererFactory.getType();
			}
		).buildPortletURL();

		String urlTitle = assetRenderer.getUrlTitle(
			liferayPortletRequest.getLocale());

		if (Validator.isNotNull(urlTitle)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (assetRenderer.getGroupId() != themeDisplay.getScopeGroupId()) {
				baseAssetViewURL.setParameter(
					"groupId", String.valueOf(assetRenderer.getGroupId()));
			}

			urlTitle = urlTitle.replaceAll(StringPool.SLASH, StringPool.DASH);

			urlTitle = urlTitle.replaceAll(
				StringBundler.concat(
					StringPool.DASH, StringPool.DASH, StringPool.PLUS),
				StringPool.DASH);

			baseAssetViewURL.setParameter("urlTitle", urlTitle);
		}

		return baseAssetViewURL;
	}

	@Override
	public long[] getClassNameIds(
		PortletPreferences portletPreferences, long[] availableClassNameIds) {

		boolean anyAssetType = GetterUtil.getBoolean(
			portletPreferences.getValue(
				"anyAssetType", Boolean.TRUE.toString()));
		String selectionStyle = portletPreferences.getValue(
			"selectionStyle",
			AssetPublisherSelectionStyleConfigurationUtil.
				defaultSelectionStyle());

		if (anyAssetType ||
			selectionStyle.equals(
				AssetPublisherSelectionStyleConstants.TYPE_MANUAL)) {

			return availableClassNameIds;
		}

		long defaultClassNameId = GetterUtil.getLong(
			portletPreferences.getValue("anyAssetType", null));

		if (defaultClassNameId > 0) {
			return new long[] {defaultClassNameId};
		}

		long[] classNameIds = GetterUtil.getLongValues(
			portletPreferences.getValues("classNameIds", null));

		if (ArrayUtil.isNotEmpty(classNameIds)) {
			return classNameIds;
		}

		return availableClassNameIds;
	}

	@Override
	public long getGroupIdFromScopeId(
			String scopeId, long siteGroupId, boolean privateLayout)
		throws PortalException {

		if (scopeId.startsWith(SCOPE_ID_CHILD_GROUP_PREFIX)) {
			String scopeIdSuffix = scopeId.substring(
				SCOPE_ID_CHILD_GROUP_PREFIX.length());

			long childGroupId = GetterUtil.getLong(scopeIdSuffix);

			Group childGroup = _groupLocalService.getGroup(childGroupId);

			if (!childGroup.hasAncestor(siteGroupId)) {
				throw new PrincipalException();
			}

			return childGroupId;
		}
		else if (scopeId.startsWith(SCOPE_ID_GROUP_PREFIX)) {
			String scopeIdSuffix = scopeId.substring(
				SCOPE_ID_GROUP_PREFIX.length());

			if (scopeIdSuffix.equals(GroupConstants.DEFAULT)) {
				return siteGroupId;
			}

			long scopeGroupId = GetterUtil.getLong(scopeIdSuffix);

			Group scopeGroup = _groupLocalService.getGroup(scopeGroupId);

			return scopeGroup.getGroupId();
		}
		else if (scopeId.startsWith(SCOPE_ID_LAYOUT_UUID_PREFIX)) {
			String layoutUuid = scopeId.substring(
				SCOPE_ID_LAYOUT_UUID_PREFIX.length());

			Layout scopeIdLayout =
				_layoutLocalService.getLayoutByUuidAndGroupId(
					layoutUuid, siteGroupId, privateLayout);

			Group scopeIdGroup = _groupLocalService.checkScopeGroup(
				scopeIdLayout, PrincipalThreadLocal.getUserId());

			return scopeIdGroup.getGroupId();
		}
		else if (scopeId.startsWith(SCOPE_ID_LAYOUT_PREFIX)) {

			// Legacy portlet preferences

			String scopeIdSuffix = scopeId.substring(
				SCOPE_ID_LAYOUT_PREFIX.length());

			long scopeIdLayoutId = GetterUtil.getLong(scopeIdSuffix);

			Layout scopeIdLayout = _layoutLocalService.getLayout(
				siteGroupId, privateLayout, scopeIdLayoutId);

			Group scopeIdGroup = scopeIdLayout.getScopeGroup();

			return scopeIdGroup.getGroupId();
		}
		else if (scopeId.startsWith(SCOPE_ID_PARENT_GROUP_PREFIX)) {
			String scopeIdSuffix = scopeId.substring(
				SCOPE_ID_PARENT_GROUP_PREFIX.length());

			long parentGroupId = GetterUtil.getLong(scopeIdSuffix);

			Group parentGroup = _groupLocalService.getGroup(parentGroupId);

			if (!parentGroup.isContentSharingWithChildrenEnabled()) {
				throw new PrincipalException();
			}

			Group group = _groupLocalService.getGroup(siteGroupId);

			if (!group.hasAncestor(parentGroupId)) {
				throw new PrincipalException();
			}

			return parentGroupId;
		}

		throw new IllegalArgumentException("Invalid scope ID " + scopeId);
	}

	@Override
	public long[] getGroupIds(
		PortletPreferences portletPreferences, long scopeGroupId,
		Layout layout) {

		String[] scopeIds = portletPreferences.getValues(
			"scopeIds", new String[] {SCOPE_ID_GROUP_PREFIX + scopeGroupId});

		Set<Long> groupIds = new LinkedHashSet<>();

		for (String scopeId : scopeIds) {
			try {
				long groupId = getGroupIdFromScopeId(
					scopeId, scopeGroupId, layout.isPrivateLayout());

				groupIds.add(groupId);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return ArrayUtil.toLongArray(groupIds);
	}

	@Override
	public InfoPage<AssetEntry> getInfoPage(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences,
			PermissionChecker permissionChecker, long[] groupIds,
			long[] allCategoryIds, String[] allTagNames,
			boolean deleteMissingAssetEntries, boolean checkPermission,
			int start, int end)
		throws Exception {

		List<AssetEntry> assetEntries = getAssetEntries(
			portletRequest, portletPreferences, permissionChecker, groupIds,
			allCategoryIds, allTagNames, deleteMissingAssetEntries,
			checkPermission);

		return InfoPage.of(
			ListUtil.subList(assetEntries, start, end),
			Pagination.of(end, start), assetEntries.size());
	}

	@Override
	public Group getItemSelectorScopeGroup(Group scopeGroup)
		throws PortalException {

		if (!scopeGroup.isLayoutPrototype()) {
			return scopeGroup;
		}

		LayoutPrototype layoutPrototype =
			_layoutPrototypeLocalService.fetchLayoutPrototype(
				scopeGroup.getClassPK());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchFirstLayoutPageTemplateEntry(
					layoutPrototype.getLayoutPrototypeId());

		if ((layoutPageTemplateEntry != null) &&
			(layoutPageTemplateEntry.getGroupId() > 0)) {

			return _groupLocalService.getGroup(
				layoutPageTemplateEntry.getGroupId());
		}

		return scopeGroup;
	}

	@Override
	public String[] getKeywords(PortletPreferences portletPreferences) {
		String[] allKeywords = new String[0];

		for (int i = 0; true; i++) {
			String[] queryValues = portletPreferences.getValues(
				"queryValues" + i, null);

			if (ArrayUtil.isEmpty(queryValues)) {
				break;
			}

			boolean queryContains = GetterUtil.getBoolean(
				portletPreferences.getValue(
					"queryContains" + i, StringPool.BLANK));
			boolean queryAndOperator = GetterUtil.getBoolean(
				portletPreferences.getValue(
					"queryAndOperator" + i, StringPool.BLANK));
			String queryName = portletPreferences.getValue(
				"queryName" + i, StringPool.BLANK);

			if (Objects.equals(queryName, "keywords") && queryContains &&
				(queryAndOperator || (queryValues.length == 1))) {

				allKeywords = queryValues;
			}
		}

		return allKeywords;
	}

	@Override
	public String getScopeId(Group group, long scopeGroupId) {
		String key = null;

		if (group.isLayout()) {
			Layout layout = _layoutLocalService.fetchLayout(group.getClassPK());

			key = SCOPE_ID_LAYOUT_UUID_PREFIX + layout.getUuid();
		}
		else if (group.isLayoutPrototype() ||
				 (group.getGroupId() == scopeGroupId)) {

			key = SCOPE_ID_GROUP_PREFIX + GroupConstants.DEFAULT;
		}
		else {
			Group scopeGroup = _groupLocalService.fetchGroup(scopeGroupId);

			if (scopeGroup.hasAncestor(group.getGroupId()) &&
				group.isContentSharingWithChildrenEnabled()) {

				key = SCOPE_ID_PARENT_GROUP_PREFIX + group.getGroupId();
			}
			else if (group.hasAncestor(scopeGroup.getGroupId())) {
				key = SCOPE_ID_CHILD_GROUP_PREFIX + group.getGroupId();
			}
			else {
				key = SCOPE_ID_GROUP_PREFIX + group.getGroupId();
			}
		}

		return key;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties)
		throws ConfigurationException {

		_assetPublisherWebConfiguration = ConfigurableUtil.createConfigurable(
			AssetPublisherWebConfiguration.class, properties);
	}

	private List<AssetEntry> _filterAssetCategoriesAssetEntries(
		List<AssetEntry> assetEntries, long[] assetCategoryIds) {

		return TransformUtil.transform(
			assetEntries,
			assetEntry -> {
				if (ArrayUtil.containsAll(
						assetEntry.getCategoryIds(), assetCategoryIds)) {

					return assetEntry;
				}

				return null;
			});
	}

	private long[] _filterAssetCategoryIds(
		AssetEntryQuery assetEntryQuery,
		PortletPreferences portletPreferences) {

		long[] filteredAssetCategoryIds = ArrayUtil.filter(
			getAssetCategoryIds(portletPreferences),
			assetCategoryId -> !ArrayUtil.contains(
				assetEntryQuery.getAllCategoryIds(), assetCategoryId));

		return _filterAssetCategoryIds(filteredAssetCategoryIds);
	}

	private long[] _filterAssetCategoryIds(long[] assetCategoryIds) {
		List<Long> filteredAssetCategoryIdsList = new ArrayList<>();

		for (long assetCategoryId : assetCategoryIds) {
			AssetCategory category =
				_assetCategoryLocalService.fetchAssetCategory(assetCategoryId);

			if (category == null) {
				continue;
			}

			filteredAssetCategoryIdsList.add(assetCategoryId);
		}

		return ArrayUtil.toArray(
			filteredAssetCategoryIdsList.toArray(new Long[0]));
	}

	private List<AssetEntry> _filterAssetTagNamesAssetEntries(
		List<AssetEntry> assetEntries, String[] assetTagNames) {

		List<AssetEntry> filteredAssetEntries = new ArrayList<>();

		for (AssetEntry assetEntry : assetEntries) {
			List<AssetTag> assetTags = assetEntry.getTags();

			String[] assetEntryAssetTagNames = new String[assetTags.size()];

			for (int i = 0; i < assetTags.size(); i++) {
				AssetTag assetTag = assetTags.get(i);

				assetEntryAssetTagNames[i] = assetTag.getName();
			}

			if (ArrayUtil.containsAll(assetEntryAssetTagNames, assetTagNames)) {
				filteredAssetEntries.add(assetEntry);
			}
		}

		return filteredAssetEntries;
	}

	private List<AssetEntryResult> _getAssetEntryResultsByClassName(
			SearchContainer<AssetEntry> searchContainer,
			AssetEntryQuery assetEntryQuery, Layout layout,
			PortletPreferences portletPreferences, String portletName,
			Locale locale, TimeZone timeZone, long companyId, long scopeGroupId,
			long userId, long[] classNameIds,
			Map<String, Serializable> attributes)
		throws Exception {

		List<AssetEntryResult> assetEntryResults = new ArrayList<>();

		int end = searchContainer.getEnd();
		int start = searchContainer.getStart();

		int total = 0;

		for (long classNameId : classNameIds) {
			assetEntryQuery.setClassNameIds(new long[] {classNameId});

			BaseModelSearchResult<AssetEntry> baseModelSearchResult =
				getAssetEntries(
					assetEntryQuery, layout, portletPreferences, portletName,
					locale, timeZone, companyId, scopeGroupId, userId,
					attributes, start, end);

			int groupTotal = baseModelSearchResult.getLength();

			total += groupTotal;

			List<AssetEntry> assetEntries =
				baseModelSearchResult.getBaseModels();

			if (!assetEntries.isEmpty() && (start < groupTotal)) {
				AssetRendererFactory<?> groupAssetRendererFactory =
					AssetRendererFactoryRegistryUtil.
						getAssetRendererFactoryByClassNameId(classNameId);

				String title = ResourceActionsUtil.getModelResource(
					locale, groupAssetRendererFactory.getClassName());

				assetEntryResults.add(
					new AssetEntryResult(title, assetEntries));
			}

			if (!portletName.equals(AssetPublisherPortletKeys.RECENT_CONTENT)) {
				if (groupTotal > 0) {
					if ((end > 0) && (end > groupTotal)) {
						end -= groupTotal;
					}
					else {
						end = 0;
					}

					if ((start > 0) && (start > groupTotal)) {
						start -= groupTotal;
					}
					else {
						start = 0;
					}
				}

				assetEntryQuery.setEnd(QueryUtil.ALL_POS);
				assetEntryQuery.setStart(QueryUtil.ALL_POS);
			}
		}

		searchContainer.setResultsAndTotal(searchContainer::getResults, total);

		return assetEntryResults;
	}

	private List<AssetEntryResult> _getAssetEntryResultsByDefault(
			SearchContainer<AssetEntry> searchContainer,
			AssetEntryQuery assetEntryQuery, Layout layout,
			PortletPreferences portletPreferences, String portletName,
			Locale locale, TimeZone timeZone, long companyId, long scopeGroupId,
			long userId, long[] classNameIds,
			Map<String, Serializable> attributes)
		throws Exception {

		List<AssetEntryResult> assetEntryResults = new ArrayList<>();

		int end = searchContainer.getEnd();
		int start = searchContainer.getStart();

		assetEntryQuery.setClassNameIds(classNameIds);

		BaseModelSearchResult<AssetEntry> baseModelSearchResult =
			getAssetEntries(
				assetEntryQuery, layout, portletPreferences, portletName,
				locale, timeZone, companyId, scopeGroupId, userId, attributes,
				start, end);

		int total = baseModelSearchResult.getLength();

		searchContainer.setResultsAndTotal(searchContainer::getResults, total);

		List<AssetEntry> assetEntries = baseModelSearchResult.getBaseModels();

		if (!assetEntries.isEmpty()) {
			assetEntryResults.add(new AssetEntryResult(assetEntries));
		}

		return assetEntryResults;
	}

	private List<AssetEntryResult> _getAssetEntryResultsByVocabulary(
			SearchContainer<AssetEntry> searchContainer,
			AssetEntryQuery assetEntryQuery, Layout layout,
			PortletPreferences portletPreferences, String portletName,
			Locale locale, TimeZone timeZone, long companyId, long scopeGroupId,
			long userId, long[] classNameIds, long assetVocabularyId,
			Map<String, Serializable> attributes)
		throws Exception {

		List<AssetEntryResult> assetEntryResults = new ArrayList<>();

		List<AssetCategory> assetCategories =
			_assetCategoryLocalService.getVocabularyRootCategories(
				assetVocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		assetEntryQuery.setClassNameIds(classNameIds);

		int end = searchContainer.getEnd();
		int start = searchContainer.getStart();

		int total = 0;

		for (AssetCategory assetCategory : assetCategories) {
			long[] oldAllCategoryIds = assetEntryQuery.getAllCategoryIds();

			long[] newAllAssetCategoryIds = ArrayUtil.append(
				oldAllCategoryIds, assetCategory.getCategoryId());

			assetEntryQuery.setAllCategoryIds(newAllAssetCategoryIds);

			BaseModelSearchResult<AssetEntry> baseModelSearchResult =
				getAssetEntries(
					assetEntryQuery, layout, portletPreferences, portletName,
					locale, timeZone, companyId, scopeGroupId, userId,
					attributes, start, end);

			int groupTotal = baseModelSearchResult.getLength();

			total += groupTotal;

			List<AssetEntry> assetEntries =
				baseModelSearchResult.getBaseModels();

			if (!assetEntries.isEmpty() && (start < groupTotal)) {
				assetEntryResults.add(
					new AssetEntryResult(
						assetCategory.getTitle(locale), assetEntries));
			}

			if (groupTotal > 0) {
				if ((end > 0) && (end > groupTotal)) {
					end -= groupTotal;
				}
				else {
					end = 0;
				}

				if ((start > 0) && (start > groupTotal)) {
					start -= groupTotal;
				}
				else {
					start = 0;
				}
			}

			assetEntryQuery.setAllCategoryIds(oldAllCategoryIds);
			assetEntryQuery.setEnd(QueryUtil.ALL_POS);
			assetEntryQuery.setStart(QueryUtil.ALL_POS);
		}

		searchContainer.setResultsAndTotal(searchContainer::getResults, total);

		return assetEntryResults;
	}

	private long[] _getSegmentsEntryIds(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _segmentsEntryRetriever.getSegmentsEntryIds(
			themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
			_requestContextMapper.map(
				_portal.getHttpServletRequest(portletRequest)),
			new long[0]);
	}

	private long[] _getSiteGroupIds(long[] groupIds) {
		Set<Long> siteGroupIds = new LinkedHashSet<>();

		for (long groupId : groupIds) {
			siteGroupIds.add(_portal.getSiteGroupId(groupId));
		}

		return ArrayUtil.toLongArray(siteGroupIds);
	}

	private boolean _isSearchWithIndex(
		String portletName, AssetEntryQuery assetEntryQuery) {

		if (_assetPublisherWebConfiguration.searchWithIndex() &&
			ArrayUtil.isEmpty(assetEntryQuery.getLinkedAssetEntryIds()) &&
			!portletName.equals(
				AssetPublisherPortletKeys.HIGHEST_RATED_ASSETS) &&
			!portletName.equals(AssetPublisherPortletKeys.MOST_VIEWED_ASSETS)) {

			return true;
		}

		return false;
	}

	private boolean _isShowAssetEntryResults(
		String portletName, AssetEntryQuery assetEntryQuery) {

		if (!portletName.equals(AssetPublisherPortletKeys.RELATED_ASSETS) ||
			ArrayUtil.isNotEmpty(assetEntryQuery.getLinkedAssetEntryIds())) {

			return true;
		}

		return false;
	}

	private String[] _normalizeAssetTagNames(String[] assetTagNames) {
		if (ArrayUtil.isEmpty(assetTagNames)) {
			return assetTagNames;
		}

		for (int i = 0; i < assetTagNames.length; i++) {
			assetTagNames[i] = StringUtil.trim(assetTagNames[i]);
		}

		return assetTagNames;
	}

	private String _normalizeURL(String url) {
		int index = url.indexOf(CharPool.QUESTION);

		if (index != -1) {
			url = url.substring(0, index);
		}

		return url;
	}

	private void _removeAndStoreSelection(
			List<String> assetEntryUuids, PortletPreferences portletPreferences)
		throws Exception {

		if (assetEntryUuids.isEmpty()) {
			return;
		}

		List<String> assetEntryXmls = ListUtil.fromArray(
			portletPreferences.getValues("assetEntryXml", new String[0]));

		Iterator<String> iterator = assetEntryXmls.iterator();

		while (iterator.hasNext()) {
			String assetEntryXml = iterator.next();

			Document document = SAXReaderUtil.read(assetEntryXml);

			Element rootElement = document.getRootElement();

			String assetEntryUuid = rootElement.elementText("asset-entry-uuid");

			if (assetEntryUuids.contains(assetEntryUuid)) {
				iterator.remove();
			}
		}

		portletPreferences.setValues(
			"assetEntryXml", assetEntryXmls.toArray(new String[0]));

		portletPreferences.store();
	}

	private void _setCategoriesAndTagsAndKeywords(
		AssetEntryQuery assetEntryQuery, PortletPreferences portletPreferences,
		long[] scopeGroupIds, long[] overrideAllAssetCategoryIds,
		String[] overrideAllAssetTagNames, String[] overrideAllKeywords) {

		long[] allAssetCategoryIds = new long[0];
		long[] anyAssetCategoryIds = new long[0];
		long[] notAllAssetCategoryIds = new long[0];
		long[] notAnyAssetCategoryIds = new long[0];

		String[] allAssetTagNames = new String[0];
		String[] anyAssetTagNames = new String[0];
		String[] notAllAssetTagNames = new String[0];
		String[] notAnyAssetTagNames = new String[0];

		String[] allKeywords = new String[0];
		String[] anyKeywords = new String[0];
		String[] notAllKeywords = new String[0];
		String[] notAnyKeywords = new String[0];

		for (int i = 0; true; i++) {
			String[] queryValues = portletPreferences.getValues(
				"queryValues" + i, null);

			if (ArrayUtil.isEmpty(queryValues)) {
				break;
			}

			boolean queryContains = GetterUtil.getBoolean(
				portletPreferences.getValue(
					"queryContains" + i, StringPool.BLANK));
			boolean queryAndOperator = GetterUtil.getBoolean(
				portletPreferences.getValue(
					"queryAndOperator" + i, StringPool.BLANK));
			String queryName = portletPreferences.getValue(
				"queryName" + i, StringPool.BLANK);

			if (Objects.equals(queryName, "assetCategories")) {
				long[] assetCategoryIds = GetterUtil.getLongValues(queryValues);

				if (queryContains && queryAndOperator) {
					allAssetCategoryIds = assetCategoryIds;
				}
				else if (queryContains && !queryAndOperator) {
					anyAssetCategoryIds = assetCategoryIds;
				}
				else if (!queryContains && queryAndOperator) {
					notAllAssetCategoryIds = assetCategoryIds;
				}
				else {
					notAnyAssetCategoryIds = assetCategoryIds;
				}
			}
			else if (Objects.equals(queryName, "keywords")) {
				if (queryContains && queryAndOperator) {
					allKeywords = queryValues;
				}
				else if (queryContains && !queryAndOperator) {
					anyKeywords = queryValues;
				}
				else if (!queryContains && queryAndOperator) {
					notAllKeywords = queryValues;
				}
				else {
					notAnyKeywords = queryValues;
				}
			}
			else {
				if (queryContains && queryAndOperator) {
					allAssetTagNames = _normalizeAssetTagNames(queryValues);
				}
				else if (queryContains && !queryAndOperator) {
					anyAssetTagNames = _normalizeAssetTagNames(queryValues);
				}
				else if (!queryContains && queryAndOperator) {
					notAllAssetTagNames = _normalizeAssetTagNames(queryValues);
				}
				else {
					notAnyAssetTagNames = _normalizeAssetTagNames(queryValues);
				}
			}
		}

		if (overrideAllAssetCategoryIds != null) {
			allAssetCategoryIds = overrideAllAssetCategoryIds;
		}

		allAssetCategoryIds = _filterAssetCategoryIds(allAssetCategoryIds);

		assetEntryQuery.setAllCategoryIds(allAssetCategoryIds);

		if (overrideAllKeywords != null) {
			allKeywords = overrideAllKeywords;
		}

		assetEntryQuery.setAllKeywords(allKeywords);

		if (overrideAllAssetTagNames != null) {
			allAssetTagNames = _normalizeAssetTagNames(
				overrideAllAssetTagNames);
		}

		long[] siteGroupIds = _getSiteGroupIds(scopeGroupIds);

		for (String assetTagName : allAssetTagNames) {
			long[] allAssetTagIds = _assetTagLocalService.getTagIds(
				assetTagName);

			assetEntryQuery.addAllTagIdsArray(allAssetTagIds);
		}

		anyAssetCategoryIds = _filterAssetCategoryIds(anyAssetCategoryIds);

		assetEntryQuery.setAnyCategoryIds(anyAssetCategoryIds);

		assetEntryQuery.setAnyKeywords(anyKeywords);

		long[] anyAssetTagIds = _assetTagLocalService.getTagIds(
			siteGroupIds, anyAssetTagNames);

		assetEntryQuery.setAnyTagIds(anyAssetTagIds);

		assetEntryQuery.setNotAllCategoryIds(notAllAssetCategoryIds);
		assetEntryQuery.setNotAllKeywords(notAllKeywords);

		for (String assetTagName : notAllAssetTagNames) {
			long[] notAllAssetTagIds = _assetTagLocalService.getTagIds(
				siteGroupIds, assetTagName);

			assetEntryQuery.addNotAllTagIdsArray(notAllAssetTagIds);
		}

		assetEntryQuery.setNotAnyCategoryIds(notAnyAssetCategoryIds);
		assetEntryQuery.setNotAnyKeywords(notAnyKeywords);

		long[] notAnyAssetTagIds = _assetTagLocalService.getTagIds(
			siteGroupIds, notAnyAssetTagNames);

		assetEntryQuery.setNotAnyTagIds(notAnyAssetTagIds);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetPublisherHelperImpl.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetEntryService _assetEntryService;

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	private volatile AssetPublisherWebConfiguration
		_assetPublisherWebConfiguration;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RequestContextMapper _requestContextMapper;

	@Reference
	private SegmentsEntryRetriever _segmentsEntryRetriever;

}