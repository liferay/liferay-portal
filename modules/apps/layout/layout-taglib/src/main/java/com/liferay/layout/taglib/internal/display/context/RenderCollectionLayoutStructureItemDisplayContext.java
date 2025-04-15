/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.display.context;

import com.liferay.fragment.collection.filter.constants.FragmentCollectionFilterConstants;
import com.liferay.fragment.constants.FragmentConfigurationFieldDataType;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.collection.provider.RepeatableFieldInfoItemCollectionProvider;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.RepeatableInfoFieldValue;
import com.liferay.info.filter.InfoFilter;
import com.liferay.info.filter.InfoFilterProvider;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.list.renderer.InfoListRenderer;
import com.liferay.info.list.renderer.InfoListRendererRegistry;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.info.search.InfoSearchClassMapperRegistryUtil;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.list.permission.provider.LayoutListPermissionProvider;
import com.liferay.layout.list.permission.provider.LayoutListPermissionProviderRegistry;
import com.liferay.layout.list.retriever.DefaultLayoutListRetrieverContext;
import com.liferay.layout.list.retriever.LayoutListRetriever;
import com.liferay.layout.list.retriever.LayoutListRetrieverRegistry;
import com.liferay.layout.list.retriever.ListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReferenceFactory;
import com.liferay.layout.list.retriever.ListObjectReferenceFactoryRegistry;
import com.liferay.layout.list.retriever.SegmentsEntryLayoutListRetriever;
import com.liferay.layout.taglib.internal.servlet.ServletContextUtil;
import com.liferay.layout.util.CollectionPaginationUtil;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class RenderCollectionLayoutStructureItemDisplayContext {

	public static final String PAGE_NUMBER_PARAM_PREFIX = "page_number_";

	public RenderCollectionLayoutStructureItemDisplayContext(
		CollectionStyledLayoutStructureItem collectionStyledLayoutStructureItem,
		HttpServletRequest httpServletRequest) {

		_collectionStyledLayoutStructureItem =
			collectionStyledLayoutStructureItem;
		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public int getActivePage() {
		if (_activePage != null) {
			return _activePage;
		}

		_activePage = ParamUtil.getInteger(
			PortalUtil.getOriginalServletRequest(_httpServletRequest),
			PAGE_NUMBER_PARAM_PREFIX +
				_collectionStyledLayoutStructureItem.getItemId(),
			1);

		int numberOfPages =
			_collectionStyledLayoutStructureItem.getNumberOfPages();

		if (!_collectionStyledLayoutStructureItem.isDisplayAllPages() &&
			(_activePage > numberOfPages)) {

			_activePage = numberOfPages - 1;
		}

		return _activePage;
	}

	public List<Object> getCollection() {
		InfoPage<?> infoPage = _getInfoPage();

		return (List<Object>)infoPage.getPageItems();
	}

	public String getCollectionItemType() {
		if (_collectionItemType != null) {
			return _collectionItemType;
		}

		JSONObject collectionJSONObject =
			_collectionStyledLayoutStructureItem.getCollectionJSONObject();

		String collectionItemType = StringPool.BLANK;

		if ((collectionJSONObject != null) &&
			collectionJSONObject.has("itemType")) {

			collectionItemType = collectionJSONObject.getString("itemType");
		}

		if (Objects.equals(
				collectionJSONObject.getString("key"),
				RepeatableFieldInfoItemCollectionProvider.class.getName())) {

			collectionItemType = RepeatableInfoFieldValue.class.getName();
		}

		_collectionItemType = collectionItemType;

		return _collectionItemType;
	}

	public LayoutDisplayPageProvider<?>
		getCollectionLayoutDisplayPageProvider() {

		JSONObject collectionJSONObject =
			_collectionStyledLayoutStructureItem.getCollectionJSONObject();

		if ((collectionJSONObject == null) ||
			(collectionJSONObject.length() <= 0)) {

			return null;
		}

		ListObjectReference listObjectReference = getListObjectReference();

		if (listObjectReference == null) {
			return null;
		}

		LayoutDisplayPageProviderRegistry layoutDisplayPageProviderRegistry =
			ServletContextUtil.getLayoutDisplayPageProviderRegistry();

		return layoutDisplayPageProviderRegistry.
			getLayoutDisplayPageProviderByClassName(
				InfoSearchClassMapperRegistryUtil.getClassName(
					listObjectReference.getItemType()));
	}

	public InfoListRenderer<?> getInfoListRenderer() {
		if (Validator.isNull(
				_collectionStyledLayoutStructureItem.getListStyle())) {

			return null;
		}

		InfoListRendererRegistry infoListRendererRegistry =
			ServletContextUtil.getInfoListRendererRegistry();

		return infoListRendererRegistry.getInfoListRenderer(
			_collectionStyledLayoutStructureItem.getListStyle());
	}

	public ListObjectReference getListObjectReference() {
		if (_listObjectReference != null) {
			return _listObjectReference;
		}

		JSONObject collectionJSONObject =
			_collectionStyledLayoutStructureItem.getCollectionJSONObject();

		if ((collectionJSONObject == null) ||
			(collectionJSONObject.length() <= 0)) {

			return null;
		}

		LayoutListRetrieverRegistry layoutListRetrieverRegistry =
			ServletContextUtil.getLayoutListRetrieverRegistry();

		String type = collectionJSONObject.getString("type");

		LayoutListRetriever<?, ?> layoutListRetriever =
			layoutListRetrieverRegistry.getLayoutListRetriever(type);

		if (layoutListRetriever == null) {
			return null;
		}

		ListObjectReferenceFactoryRegistry listObjectReferenceFactoryRegistry =
			ServletContextUtil.getListObjectReferenceFactoryRegistry();

		ListObjectReferenceFactory<?> listObjectReferenceFactory =
			listObjectReferenceFactoryRegistry.getListObjectReference(type);

		if (listObjectReferenceFactory == null) {
			return null;
		}

		_listObjectReference =
			listObjectReferenceFactory.getListObjectReference(
				collectionJSONObject);

		return _listObjectReference;
	}

	public int getMaxNumberOfItemsPerPage() {
		if (_maxNumberOfItemsPerPage != null) {
			return _maxNumberOfItemsPerPage;
		}

		_maxNumberOfItemsPerPage = Math.min(
			_getCollectionCount(), _getNumberOfItemsPerPage());

		return _maxNumberOfItemsPerPage;
	}

	public int getNumberOfItemsToDisplay() {
		if (_numberOfItemsToDisplay != null) {
			return _numberOfItemsToDisplay;
		}

		int numberOfItemsToDisplay = getTotalNumberOfItems();

		if (CollectionPaginationUtil.isPaginationEnabled(
				_collectionStyledLayoutStructureItem.getPaginationType())) {

			numberOfItemsToDisplay = Math.min(
				numberOfItemsToDisplay, _getNumberOfItemsPerPage());
		}

		_numberOfItemsToDisplay = numberOfItemsToDisplay;

		return _numberOfItemsToDisplay;
	}

	public int getNumberOfPages() {
		if (_numberOfPages != null) {
			return _numberOfPages;
		}

		int numberOfItemsPerPage = _getNumberOfItemsPerPage();

		int maxNumberOfItems = _getCollectionCount();

		if (_collectionStyledLayoutStructureItem.getNumberOfPages() > 0) {
			maxNumberOfItems = Math.min(
				_getCollectionCount(),
				_collectionStyledLayoutStructureItem.getNumberOfPages() *
					numberOfItemsPerPage);
		}

		if (_collectionStyledLayoutStructureItem.isDisplayAllPages()) {
			maxNumberOfItems = _getCollectionCount();
		}

		_numberOfPages = (int)Math.ceil(
			(double)maxNumberOfItems / numberOfItemsPerPage);

		return _numberOfPages;
	}

	public int getNumberOfRows() {
		if (_numberOfRows != null) {
			return _numberOfRows;
		}

		_numberOfRows = (int)Math.ceil(
			(double)getMaxNumberOfItemsPerPage() /
				_collectionStyledLayoutStructureItem.getNumberOfColumns());

		int numberOfItemsToDisplay = getTotalNumberOfItems();

		if (CollectionPaginationUtil.isPaginationEnabled(
				_collectionStyledLayoutStructureItem.getPaginationType())) {

			numberOfItemsToDisplay = Math.min(
				numberOfItemsToDisplay, _getNumberOfItemsPerPage());
		}

		_numberOfRows = (int)Math.ceil(
			(double)numberOfItemsToDisplay /
				_collectionStyledLayoutStructureItem.getNumberOfColumns());

		return _numberOfRows;
	}

	public int getTotalNumberOfItems() {
		if (_totalNumberOfItems != null) {
			return _totalNumberOfItems;
		}

		_totalNumberOfItems = CollectionPaginationUtil.getTotalNumberOfItems(
			_getCollectionCount(),
			_collectionStyledLayoutStructureItem.isDisplayAllPages(),
			_collectionStyledLayoutStructureItem.isDisplayAllItems(),
			_collectionStyledLayoutStructureItem.getNumberOfItems(),
			_collectionStyledLayoutStructureItem.getNumberOfItemsPerPage(),
			_collectionStyledLayoutStructureItem.getNumberOfPages(),
			_collectionStyledLayoutStructureItem.getPaginationType());

		return _totalNumberOfItems;
	}

	public boolean hasViewPermission() {
		ListObjectReference listObjectReference = getListObjectReference();

		if (listObjectReference == null) {
			return true;
		}

		return _hasViewPermission(listObjectReference);
	}

	private long[] _filterSegmentsEntryIds(
		LayoutListRetriever<?, ListObjectReference> layoutListRetriever,
		ListObjectReference listObjectReference, long[] segmentsEntryIds) {

		if (!(layoutListRetriever instanceof
				SegmentsEntryLayoutListRetriever)) {

			return segmentsEntryIds;
		}

		long segmentsExperienceId = ParamUtil.getLong(
			_httpServletRequest, "segmentsExperienceId", -1);

		if (segmentsExperienceId <= 0) {
			return segmentsEntryIds;
		}

		SegmentsExperience segmentsExperience =
			SegmentsExperienceLocalServiceUtil.fetchSegmentsExperience(
				segmentsExperienceId);

		if (segmentsExperience == null) {
			return segmentsEntryIds;
		}

		SegmentsEntryLayoutListRetriever<ListObjectReference>
			segmentsEntryLayoutListRetriever =
				(SegmentsEntryLayoutListRetriever<ListObjectReference>)
					layoutListRetriever;

		if (segmentsEntryLayoutListRetriever.hasSegmentsEntryVariation(
				listObjectReference, segmentsExperience.getSegmentsEntryId())) {

			return new long[] {segmentsExperience.getSegmentsEntryId()};
		}

		return new long[] {
			segmentsEntryLayoutListRetriever.getDefaultVariationSegmentsEntryId(
				listObjectReference)
		};
	}

	private int _getCollectionCount() {
		InfoPage<?> infoPage = _getInfoPage();

		return infoPage.getTotalCount();
	}

	private Map<String, String[]> _getConfiguration() {
		if (_configuration != null) {
			return _configuration;
		}

		Map<String, String[]> configuration = new HashMap<>();

		JSONObject collectionJSONObject =
			_collectionStyledLayoutStructureItem.getCollectionJSONObject();

		String fieldName = collectionJSONObject.getString("fieldName");

		if (Validator.isNotNull(fieldName)) {
			configuration.put("fieldNames", new String[] {fieldName});
		}

		JSONObject configurationJSONObject = collectionJSONObject.getJSONObject(
			"config");

		if (configurationJSONObject == null) {
			return configuration;
		}

		for (String key : configurationJSONObject.keySet()) {
			List<String> values = new ArrayList<>();

			Object object = configurationJSONObject.get(key);

			if (object instanceof JSONArray) {
				JSONArray jsonArray = configurationJSONObject.getJSONArray(key);

				for (int i = 0; i < jsonArray.length(); i++) {
					values.add(jsonArray.getString(i));
				}
			}
			else {
				values.add(String.valueOf(object));
			}

			configuration.put(key, values.toArray(new String[0]));
		}

		_configuration = configuration;

		return _configuration;
	}

	private Object _getContextObject() {
		if (_contextObject != null) {
			return _contextObject;
		}

		Object infoItem = _httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		InfoItemReference infoItemReference =
			(InfoItemReference)_httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_REFERENCE);

		if (infoItemReference == null) {
			_contextObject = infoItem;

			return _contextObject;
		}

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		InfoItemServiceRegistry infoItemServiceRegistry =
			ServletContextUtil.getInfoItemServiceRegistry();

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, infoItemReference.getClassName(),
				infoItemIdentifier.getInfoItemServiceFilter());

		try {
			Object object = infoItemObjectProvider.getInfoItem(
				infoItemIdentifier);

			if (object != null) {
				_contextObject = object;

				return _contextObject;
			}
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchInfoItemException);
			}
		}

		_contextObject = infoItem;

		return _contextObject;
	}

	private Map<String, String[]> _getFilterValues() {
		Map<String, String[]> filterValues = new HashMap<>();

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(_httpServletRequest);

		Map<String, String[]> parameterMap =
			originalHttpServletRequest.getParameterMap();

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			ServletContextUtil.getFragmentEntryConfigurationParser();

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String parameterName = entry.getKey();

			if (!parameterName.startsWith(
					FragmentCollectionFilterConstants.FILTER_PREFIX) ||
				ArrayUtil.isEmpty(entry.getValue())) {

				continue;
			}

			List<String> parameterNameParts = StringUtil.split(
				parameterName, CharPool.UNDERLINE);

			if (parameterNameParts.size() != 3) {
				continue;
			}

			FragmentEntryLink fragmentEntryLink =
				FragmentEntryLinkLocalServiceUtil.fetchFragmentEntryLink(
					GetterUtil.getLong(parameterNameParts.get(2)));

			if (fragmentEntryLink == null) {
				continue;
			}

			JSONArray targetCollectionsJSONArray =
				(JSONArray)
					fragmentEntryConfigurationParser.getConfigurationFieldValue(
						fragmentEntryLink.getEditableValues(),
						"targetCollections",
						FragmentConfigurationFieldDataType.ARRAY);

			if ((targetCollectionsJSONArray != null) &&
				JSONUtil.hasValue(
					targetCollectionsJSONArray,
					_collectionStyledLayoutStructureItem.getItemId())) {

				filterValues.put(
					parameterName.replaceFirst(
						FragmentCollectionFilterConstants.FILTER_PREFIX,
						StringPool.BLANK),
					entry.getValue());
			}
		}

		return filterValues;
	}

	private Map<String, InfoFilter> _getInfoFilters(
		LayoutListRetriever<?, ListObjectReference> layoutListRetriever,
		ListObjectReference listObjectReference) {

		if (_infoFilters != null) {
			return _infoFilters;
		}

		Map<String, InfoFilter> infoFilters = new HashMap<>();

		InfoItemServiceRegistry infoItemServiceRegistry =
			ServletContextUtil.getInfoItemServiceRegistry();

		Map<String, String[]> filterValues = _getFilterValues();

		for (InfoFilter infoFilter :
				layoutListRetriever.getSupportedInfoFilters(
					listObjectReference)) {

			Class<?> clazz = infoFilter.getClass();

			InfoFilterProvider<?> infoFilterProvider =
				infoItemServiceRegistry.getFirstInfoItemService(
					InfoFilterProvider.class, clazz.getName());

			infoFilters.put(
				clazz.getName(), infoFilterProvider.create(filterValues));
		}

		_infoFilters = infoFilters;

		return _infoFilters;
	}

	private InfoPage<?> _getInfoPage() {
		if (_infoPage != null) {
			return _infoPage;
		}

		LayoutListRetriever<?, ListObjectReference> layoutListRetriever =
			_getLayoutListRetriever();
		ListObjectReference listObjectReference = getListObjectReference();

		if ((layoutListRetriever == null) || (listObjectReference == null) ||
			!_hasViewPermission(listObjectReference)) {

			_infoPage = InfoPage.of(
				Collections.emptyList(), Pagination.of(0, 0), 0);

			return _infoPage;
		}

		DefaultLayoutListRetrieverContext defaultLayoutListRetrieverContext =
			new DefaultLayoutListRetrieverContext();

		defaultLayoutListRetrieverContext.setConfiguration(_getConfiguration());
		defaultLayoutListRetrieverContext.setContextObject(_getContextObject());
		defaultLayoutListRetrieverContext.setInfoFilters(
			_getInfoFilters(layoutListRetriever, listObjectReference));
		defaultLayoutListRetrieverContext.setPagination(
			CollectionPaginationUtil.getPagination(
				getActivePage(),
				_collectionStyledLayoutStructureItem.isDisplayAllItems(),
				_collectionStyledLayoutStructureItem.getNumberOfItems(),
				_collectionStyledLayoutStructureItem.getNumberOfItemsPerPage(),
				_collectionStyledLayoutStructureItem.getPaginationType()));
		defaultLayoutListRetrieverContext.setSegmentsEntryIds(
			_getSegmentsEntryIds(layoutListRetriever, listObjectReference));

		_infoPage = layoutListRetriever.getInfoPage(
			listObjectReference, defaultLayoutListRetrieverContext);

		return _infoPage;
	}

	private LayoutListRetriever<?, ListObjectReference>
		_getLayoutListRetriever() {

		JSONObject collectionJSONObject =
			_collectionStyledLayoutStructureItem.getCollectionJSONObject();

		if ((collectionJSONObject == null) ||
			(collectionJSONObject.length() <= 0)) {

			return null;
		}

		LayoutListRetrieverRegistry layoutListRetrieverRegistry =
			ServletContextUtil.getLayoutListRetrieverRegistry();

		return (LayoutListRetriever<?, ListObjectReference>)
			layoutListRetrieverRegistry.getLayoutListRetriever(
				collectionJSONObject.getString("type"));
	}

	private int _getNumberOfItemsPerPage() {
		if (_numberOfItemsPerPage != null) {
			return _numberOfItemsPerPage;
		}

		int numberOfItemsPerPage =
			_collectionStyledLayoutStructureItem.getNumberOfItemsPerPage();

		if ((numberOfItemsPerPage <= 0) ||
			(numberOfItemsPerPage >
				PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA)) {

			numberOfItemsPerPage = PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA;
		}

		_numberOfItemsPerPage = numberOfItemsPerPage;

		return _numberOfItemsPerPage;
	}

	private long[] _getSegmentsEntryIds(
		LayoutListRetriever<?, ListObjectReference> layoutListRetriever,
		ListObjectReference listObjectReference) {

		if (_segmentsEntryIds != null) {
			return _segmentsEntryIds;
		}

		SegmentsEntryRetriever segmentsEntryRetriever =
			ServletContextUtil.getSegmentsEntryRetriever();

		RequestContextMapper requestContextMapper =
			ServletContextUtil.getRequestContextMapper();

		_segmentsEntryIds = segmentsEntryRetriever.getSegmentsEntryIds(
			_themeDisplay.getScopeGroupId(), _themeDisplay.getUserId(),
			requestContextMapper.map(_httpServletRequest), new long[0]);

		_segmentsEntryIds = _filterSegmentsEntryIds(
			layoutListRetriever, listObjectReference, _segmentsEntryIds);

		return _segmentsEntryIds;
	}

	private boolean _hasViewPermission(
		ListObjectReference listObjectReference) {

		if (_hasViewPermission != null) {
			return _hasViewPermission;
		}

		_hasViewPermission = true;

		LayoutListPermissionProviderRegistry
			layoutListPermissionProviderRegistry =
				ServletContextUtil.getLayoutListPermissionProviderRegistry();

		Class<? extends ListObjectReference> listObjectReferenceClass =
			listObjectReference.getClass();

		LayoutListPermissionProvider<ListObjectReference>
			layoutListPermissionProvider =
				(LayoutListPermissionProvider<ListObjectReference>)
					layoutListPermissionProviderRegistry.
						getLayoutListPermissionProvider(
							listObjectReferenceClass.getName());

		if (layoutListPermissionProvider == null) {
			return _hasViewPermission;
		}

		_hasViewPermission = layoutListPermissionProvider.hasPermission(
			_themeDisplay.getPermissionChecker(), listObjectReference,
			ActionKeys.VIEW);

		return _hasViewPermission;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RenderCollectionLayoutStructureItemDisplayContext.class);

	private Integer _activePage;
	private String _collectionItemType;
	private final CollectionStyledLayoutStructureItem
		_collectionStyledLayoutStructureItem;
	private Map<String, String[]> _configuration;
	private Object _contextObject;
	private Boolean _hasViewPermission;
	private final HttpServletRequest _httpServletRequest;
	private Map<String, InfoFilter> _infoFilters;
	private InfoPage<?> _infoPage;
	private ListObjectReference _listObjectReference;
	private Integer _maxNumberOfItemsPerPage;
	private Integer _numberOfItemsPerPage;
	private Integer _numberOfItemsToDisplay;
	private Integer _numberOfPages;
	private Integer _numberOfRows;
	private long[] _segmentsEntryIds;
	private final ThemeDisplay _themeDisplay;
	private Integer _totalNumberOfItems;

}