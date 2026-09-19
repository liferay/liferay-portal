/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.internal.resource.v1_0;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.search.experiences.blueprint.exception.InvalidElementInstanceException;
import com.liferay.search.experiences.blueprint.exception.InvalidWebCacheItemException;
import com.liferay.search.experiences.blueprint.exception.PrivateIPAddressException;
import com.liferay.search.experiences.blueprint.search.request.enhancer.SXPBlueprintSearchRequestEnhancer;
import com.liferay.search.experiences.exception.SXPExceptionUtil;
import com.liferay.search.experiences.rest.dto.v1_0.DocumentField;
import com.liferay.search.experiences.rest.dto.v1_0.Hit;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.dto.v1_0.SearchHits;
import com.liferay.search.experiences.rest.dto.v1_0.SearchResponse;
import com.liferay.search.experiences.rest.dto.v1_0.util.SXPBlueprintUtil;
import com.liferay.search.experiences.rest.resource.v1_0.SearchResponseResource;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Brian Wing Shun Chan
 * @author André de Oliveira
 */
@Component(
	enabled = false,
	properties = "OSGI-INF/liferay/rest/v1_0/search-response.properties",
	scope = ServiceScope.PROTOTYPE, service = SearchResponseResource.class
)
@CTAware
public class SearchResponseResourceImpl extends BaseSearchResponseResourceImpl {

	@Override
	public SearchResponse postSearch(
			String queryString, Pagination pagination,
			SXPBlueprint sxpBlueprint)
		throws Exception {

		try {
			return search(pagination, queryString, sxpBlueprint);
		}
		catch (RuntimeException runtimeException) {
			if (_isComposite(runtimeException)) {
				throw new RuntimeException(_getTraceString(runtimeException));
			}

			throw runtimeException;
		}
	}

	protected SearchResponse search(
		Pagination pagination, String queryString, SXPBlueprint sxpBlueprint) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				contextCompany.getCompanyId()
			).from(
				pagination.getStartPosition()
			).queryString(
				queryString
			).size(
				pagination.getPageSize()
			).withSearchContext(
				searchContext -> {
					searchContext.setAttribute(
						"search.experiences.ip.address",
						contextHttpServletRequest.getRemoteAddr());
					searchContext.setAttribute(
						"search.experiences.scope.group.id", _getGroupId());
					searchContext.setTimeZone(contextUser.getTimeZone());
					searchContext.setUserId(contextUser.getUserId());
				}
			);

		RuntimeException runtimeException = new RuntimeException();

		try {
			if (sxpBlueprint != null) {
				_sxpBlueprintSearchRequestEnhancer.enhance(
					searchRequestBuilder,
					String.valueOf(SXPBlueprintUtil.unpack(sxpBlueprint)));
			}
		}
		catch (Exception exception) {
			runtimeException.addSuppressed(exception);
		}

		if (!SXPExceptionUtil.hasErrors(runtimeException)) {
			try {
				SearchResponse searchResponse = toSearchResponse(
					_searcher.search(searchRequestBuilder.build()));

				if (ArrayUtil.isNotEmpty(runtimeException.getSuppressed())) {
					if (_log.isWarnEnabled()) {
						_log.warn(runtimeException);
					}

					searchResponse.setErrors(
						() -> _toErrorMaps(runtimeException));
				}

				return searchResponse;
			}
			catch (Exception exception) {
				runtimeException.addSuppressed(exception);
			}
		}

		_log.error(runtimeException);

		return new SearchResponse() {
			{
				setErrors(() -> _toErrorMaps(runtimeException));
			}
		};
	}

	protected SearchResponse toSearchResponse(
			com.liferay.portal.search.searcher.SearchResponse searchResponse)
		throws Exception {

		SearchRequest portalSearchRequest = searchResponse.getRequest();

		return SearchResponse.unsafeToDTO(
			String.valueOf(
				new SearchResponse() {
					{
						setPage(portalSearchRequest::getFrom);
						setPageSize(portalSearchRequest::getSize);
						setRequest(
							() -> _createJSONObject(
								searchResponse.getRequestString()));
						setRequestString(searchResponse::getRequestString);
						setResponse(
							() -> _createJSONObject(
								searchResponse.getResponseString()));
						setResponseString(searchResponse::getResponseString);
						setSearchHits(
							() -> _toSearchHits(
								_getLocale(searchResponse),
								searchResponse.getSearchHits()));
					}
				}));
	}

	private void _addErrorMaps(
		List<Map<String, ?>> maps, Throwable throwable1,
		Map<String, String> baseMap) {

		Map<String, String> errorMap = new LinkedHashMap<>(baseMap);
		Map<String, String> inheritMap = new LinkedHashMap<>(baseMap);

		if (!_isComposite(throwable1)) {
			Class<? extends Throwable> clazz = throwable1.getClass();

			errorMap.put("exceptionClass", clazz.getName());

			errorMap.put("exceptionTrace", _getTraceString(throwable1));
			errorMap.put("msg", throwable1.getMessage());
			errorMap.put("severity", "WARN");

			if (throwable1 instanceof InvalidElementInstanceException) {
				InvalidElementInstanceException
					invalidElementInstanceException =
						(InvalidElementInstanceException)throwable1;

				String sxpElementId =
					"querySXPElement-" +
						invalidElementInstanceException.getIndex();

				errorMap.put("localizedMessage", "Element skipped");
				errorMap.put("sxpElementId", sxpElementId);

				inheritMap.put("sxpElementId", sxpElementId);
			}
			else if (!(throwable1 instanceof PrivateIPAddressException) &&
					 !(throwable1 instanceof InvalidWebCacheItemException)) {

				errorMap.put("localizedMessage", "Error");
				errorMap.put("severity", "ERROR");
			}

			maps.add(errorMap);
		}

		ArrayUtil.isNotEmptyForEach(
			throwable1.getSuppressed(),
			throwable2 -> _addErrorMaps(maps, throwable2, inheritMap));
	}

	private JSONObject _createJSONObject(String string) {
		try {
			return _jsonFactory.createJSONObject(string);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	private AssetRenderer<?> _getAssetRenderer(Map<String, Field> fields) {
		try {
			Field entryClassNameField = fields.get(
				com.liferay.portal.kernel.search.Field.ENTRY_CLASS_NAME);

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						GetterUtil.getString(entryClassNameField.getValue()));

			if (assetRendererFactory == null) {
				return null;
			}

			Field entryClassPKField = fields.get(
				com.liferay.portal.kernel.search.Field.ENTRY_CLASS_PK);

			return assetRendererFactory.getAssetRenderer(
				GetterUtil.getLong(entryClassPKField.getValue()));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	private long _getGroupId() {
		return contextCompany.getGroupId();
	}

	private Locale _getLocale(
		com.liferay.portal.search.searcher.SearchResponse searchResponse) {

		Locale locale = searchResponse.withSearchContextGet(
			SearchContext::getLocale);

		if (locale != null) {
			return locale;
		}

		return contextAcceptLanguage.getPreferredLocale();
	}

	private Float _getScore(float score) {
		if (Float.isNaN(score)) {
			return null;
		}

		return score;
	}

	private String _getTraceString(Throwable throwable) {
		OutputStream outputStream = new ByteArrayOutputStream();

		throwable.printStackTrace(new PrintStream(outputStream));

		return outputStream.toString();
	}

	private boolean _isComposite(Throwable throwable) {
		if ((throwable.getClass() == RuntimeException.class) &&
			Validator.isNull(throwable.getMessage()) &&
			ArrayUtil.isNotEmpty(throwable.getSuppressed())) {

			return true;
		}

		return false;
	}

	private Map<String, DocumentField> _toDocumentFields(
		Document document, Locale locale) {

		Map<String, Field> fields = document.getFields();

		Map<String, DocumentField> documentFields = new LinkedHashMap<>();

		MapUtil.isNotEmptyForEach(
			fields,
			(name, field) -> {
				List<Object> valuesList = field.getValues();

				documentFields.put(
					name,
					new DocumentField() {
						{
							setValues(valuesList::toArray);
						}
					});
			});

		if (MapUtil.isEmpty(fields)) {
			return documentFields;
		}

		AssetRenderer<?> assetRenderer = _getAssetRenderer(fields);

		if (assetRenderer == null) {
			return documentFields;
		}

		documentFields.put(
			"assetSearchSummary",
			new DocumentField() {
				{
					setValues(
						() -> new String[] {
							assetRenderer.getSearchSummary(locale)
						});
				}
			});
		documentFields.put(
			"assetTitle",
			new DocumentField() {
				{
					setValues(
						() -> new String[] {assetRenderer.getTitle(locale)});
				}
			});

		return documentFields;
	}

	private Map<String, ?>[] _toErrorMaps(RuntimeException runtimeException) {
		List<Map<String, ?>> maps = new ArrayList<>();

		_addErrorMaps(maps, runtimeException, Collections.emptyMap());

		return maps.toArray(new Map[0]);
	}

	private Hit[] _toHits(Locale locale, List<SearchHit> searchHits) {
		List<Hit> hits = new ArrayList<>();

		for (SearchHit searchHit : searchHits) {
			hits.add(
				new Hit() {
					{
						setDocumentFields(
							() -> _toDocumentFields(
								searchHit.getDocument(), locale));
						setExplanation(searchHit::getExplanation);
						setId(searchHit::getId);
						setScore(() -> _getScore(searchHit.getScore()));
						setVersion(searchHit::getVersion);
					}
				});
		}

		return hits.toArray(new Hit[0]);
	}

	private SearchHits _toSearchHits(
		Locale locale, com.liferay.portal.search.hits.SearchHits searchHits) {

		return new SearchHits() {
			{
				setHits(() -> _toHits(locale, searchHits.getSearchHits()));
				setMaxScore(() -> _getScore(searchHits.getMaxScore()));
				setTotalHits(searchHits::getTotalHits);
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchResponseResourceImpl.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Searcher _searcher;

	@Reference
	private SXPBlueprintSearchRequestEnhancer
		_sxpBlueprintSearchRequestEnhancer;

}