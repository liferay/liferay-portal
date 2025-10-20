/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.servlet;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.rest.util.FilterUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.site.cms.site.initializer.internal.servlet.DownloadObjectEntryFolderServlet",
		"osgi.http.whiteboard.servlet.pattern=/cms/download-folder/*",
		"servlet.init.httpMethods=GET,POST"
	},
	service = Servlet.class
)
public class DownloadObjectEntryFolderServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		_createContext(httpServletRequest, httpServletResponse);

		super.service(httpServletRequest, httpServletResponse);
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			_downloadObjectEntryFolder(httpServletRequest, httpServletResponse);
		}
		catch (PortalException portalException) {
			throw new ServletException(portalException);
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			_downloadBulkAction(httpServletRequest, httpServletResponse);
		}
		catch (PortalException portalException) {
			throw new ServletException(portalException);
		}
	}

	private void _createContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			EventsProcessorUtil.process(
				PropsKeys.SERVLET_SERVICE_EVENTS_PRE,
				PropsValues.SERVLET_SERVICE_EVENTS_PRE, httpServletRequest,
				httpServletResponse);
		}
		catch (ActionException actionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(actionException);
			}
		}
	}

	private void _downloadBulkAction(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException {

		httpServletResponse.addHeader(
			HttpHeaders.CACHE_CONTROL,
			HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);

		JSONObject jsonObject = null;

		try {
			jsonObject = _jsonFactory.createJSONObject(
				StreamUtil.toString(
					httpServletRequest.getInputStream(), StringPool.UTF8));
		}
		catch (JSONException jsonException) {
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			if (_log.isWarnEnabled()) {
				_log.warn(jsonException);
			}

			return;
		}

		if (!StringUtil.equalsIgnoreCase(
				"DownloadBulkAction", jsonObject.getString("type"))) {

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			if (_log.isWarnEnabled()) {
				_log.warn("Type is not \"DownloadBulkAction\"");
			}

			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		if (jsonObject.getBoolean("selectAll")) {
			_selectAllDownload(httpServletRequest, themeDisplay, zipWriter);
		}
		else {
			JSONArray jsonArray = jsonObject.getJSONArray("bulkActionItems");

			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);

				_zipEntry(
					jsonObject.getString("className"),
					jsonObject.getLong("classPK"), themeDisplay, zipWriter);
			}
		}

		try (InputStream inputStream = new FileInputStream(
				zipWriter.getFile())) {

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse,
				"download-" + Time.getTimestamp() + ".zip", inputStream, 0,
				ContentTypes.APPLICATION_ZIP,
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		finally {
			File file = zipWriter.getFile();

			if ((file != null) && file.exists()) {
				file.delete();
			}
		}
	}

	private void _downloadObjectEntryFolder(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException {

		String[] pathArray = StringUtil.split(
			_getObjectEntryFolderPath(httpServletRequest), StringPool.SLASH);

		long classNameId = _portal.getClassNameId(ObjectEntryFolder.class);

		if (classNameId != GetterUtil.getLong(pathArray[0])) {
			throw new RuntimeException();
		}

		long objectEntryFolderId = GetterUtil.getLong(pathArray[1]);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		try {
			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderService.getObjectEntryFolder(
					objectEntryFolderId);

			String zipFileName = objectEntryFolder.getName() + ".zip";

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_zipObjectEntryFolder(
				objectEntryFolder.getGroupId(), objectEntryFolderId,
				objectEntryFolder.getName(), themeDisplay, zipWriter);

			try (InputStream inputStream = new FileInputStream(
					zipWriter.getFile())) {

				httpServletResponse.addHeader(
					HttpHeaders.CACHE_CONTROL,
					HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);

				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse, zipFileName,
					inputStream, 0, ContentTypes.APPLICATION_ZIP,
					HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
			}
		}
		finally {
			File file = zipWriter.getFile();

			if ((file != null) && file.exists()) {
				file.delete();
			}
		}
	}

	private BooleanClause<Query> _getBooleanClause(
		UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
		Filter filter) {

		BooleanQuery booleanQuery = new BooleanQueryImpl() {
			{
				add(new MatchAllQuery(), BooleanClauseOccur.MUST);

				BooleanFilter booleanFilter = new BooleanFilter();

				booleanFilter.add(filter, BooleanClauseOccur.MUST);

				setPreBooleanFilter(booleanFilter);
			}
		};

		try {
			booleanQueryUnsafeConsumer.accept(booleanQuery);

			return BooleanClauseFactoryUtil.create(
				booleanQuery, BooleanClauseOccur.MUST.getName());
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getObjectEntryFolderPath(
		HttpServletRequest httpServletRequest) {

		String requestURI = httpServletRequest.getRequestURI();

		String path =
			httpServletRequest.getContextPath() +
				httpServletRequest.getServletPath();

		return requestURI.substring(path.length() + 1);
	}

	private void _populateSearchContext(
		Filter filter, String search, SearchContext searchContext, User user) {

		int[] statuses = FilterUtil.getStatuses(filter);

		if (ArrayUtil.isNotEmpty(statuses)) {
			searchContext.setAttribute("status", statuses);
		}

		if (filter != null) {
			searchContext.setBooleanClauses(
				new BooleanClause[] {
					_getBooleanClause(
						booleanQuery -> {
						},
						filter)
				});
		}

		searchContext.setCompanyId(user.getCompanyId());
		searchContext.setEnd(QueryUtil.ALL_POS);
		searchContext.setKeywords(search);
		searchContext.setLocale(user.getLocale());
		searchContext.setStart(QueryUtil.ALL_POS);
		searchContext.setTimeZone(user.getTimeZone());
		searchContext.setUserId(user.getUserId());
	}

	private void _selectAllDownload(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
			ZipWriter zipWriter)
		throws IOException, PortalException {

		User user = _portal.getUser(httpServletRequest);

		if (user == null) {
			throw new RuntimeException();
		}

		String search = ParamUtil.getString(httpServletRequest, "search");

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).emptySearchEnabled(
				true
			).withSearchContext(
				searchContext -> _populateSearchContext(
					_toFilter(
						ParamUtil.getString(httpServletRequest, "filter"),
						user.getLocale()),
					search, searchContext, user)
			);

		if (!Validator.isBlank(search)) {
			searchRequestBuilder.queryString(search);
		}

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		SearchHits searchHits = searchResponse.getSearchHits();

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Document document = searchHit.getDocument();

			_zipEntry(
				document.getString("entryClassName"),
				document.getLong("entryClassPK"), themeDisplay, zipWriter);
		}
	}

	private Filter _toFilter(String filterString, Locale locale) {
		try {
			FilterParser filterParser = _filterParserProvider.provide(
				_entityModel);

			com.liferay.portal.odata.filter.Filter oDataFilter =
				new com.liferay.portal.odata.filter.Filter(
					filterParser.parse(filterString));

			return _expressionConvert.convert(
				oDataFilter.getExpression(), locale, _entityModel);
		}
		catch (Exception exception) {
			_log.error("Invalid filter " + filterString, exception);

			return null;
		}
	}

	private void _zipEntry(
			String className, long classPK, ThemeDisplay themeDisplay,
			ZipWriter zipWriter)
		throws IOException, PortalException {

		if (StringUtil.equalsIgnoreCase(
				className, ObjectEntryFolder.class.getName())) {

			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderService.getObjectEntryFolder(classPK);

			_zipObjectEntryFolder(
				objectEntryFolder.getGroupId(), classPK,
				objectEntryFolder.getName(), themeDisplay, zipWriter);
		}
		else {
			_zipObjectEntry(
				_objectEntryLocalService.getObjectEntry(classPK),
				StringPool.SLASH, themeDisplay.getPermissionChecker(),
				zipWriter);
		}
	}

	private void _zipObjectEntry(
			ObjectEntry objectEntry, String path,
			PermissionChecker permissionChecker, ZipWriter zipWriter)
		throws IOException, PortalException {

		Map<String, Serializable> values = objectEntry.getValues();

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectEntry.getObjectDefinitionId());

		String objectFieldName = StringPool.SLASH;

		for (ObjectField objectField : objectFields) {
			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				objectFieldName = objectField.getName();
			}
		}

		Serializable serializable = values.get(objectFieldName);

		long fileEntryId = GetterUtil.getLong(serializable);

		if (fileEntryId == 0) {
			return;
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		if (fileEntry.containsPermission(
				permissionChecker, ActionKeys.DOWNLOAD)) {

			zipWriter.addEntry(
				path + StringPool.SLASH + fileEntry.getFileName(),
				fileEntry.getContentStream());
		}
	}

	private void _zipObjectEntryFolder(
			long groupId, long objectEntryFolderId, String path,
			ThemeDisplay themeDisplay, ZipWriter zipWriter)
		throws IOException, PortalException {

		List<ObjectEntry> objectEntryFolderObjectEntries =
			_objectEntryLocalService.getObjectEntryFolderObjectEntries(
				groupId, objectEntryFolderId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (ObjectEntry objectEntry : objectEntryFolderObjectEntries) {
			_zipObjectEntry(
				objectEntry, path, themeDisplay.getPermissionChecker(),
				zipWriter);
		}

		List<ObjectEntryFolder> objectEntryFolders =
			_objectEntryFolderService.getObjectEntryFolders(
				groupId, themeDisplay.getCompanyId(), objectEntryFolderId,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (ObjectEntryFolder objectEntryFolder : objectEntryFolders) {
			_zipObjectEntryFolder(
				groupId, objectEntryFolder.getObjectEntryFolderId(),
				StringBundler.concat(
					path, StringPool.SLASH, objectEntryFolder.getName()),
				themeDisplay, zipWriter);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DownloadObjectEntryFolderServlet.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference(target = "(entity.model.name=BulkAction)")
	private EntityModel _entityModel;

	@Reference(
		target = "(result.class.name=com.liferay.portal.kernel.search.filter.Filter)"
	)
	private ExpressionConvert<Filter> _expressionConvert;

	@Reference
	private FilterParserProvider _filterParserProvider;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectEntryFolderService _objectEntryFolderService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}