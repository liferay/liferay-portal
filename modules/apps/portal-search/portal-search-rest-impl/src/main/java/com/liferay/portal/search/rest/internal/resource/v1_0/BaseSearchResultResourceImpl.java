/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortField;
import com.liferay.portal.odata.sort.SortParser;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.search.rest.dto.v1_0.SearchRequestBody;
import com.liferay.portal.search.rest.dto.v1_0.SearchResult;
import com.liferay.portal.search.rest.resource.v1_0.SearchResultResource;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@jakarta.ws.rs.Path("/v1.0")
public abstract class BaseSearchResultResourceImpl
	implements EntityModelResource, SearchResultResource,
			   VulcanBatchEngineTaskItemDelegate<SearchResult> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/search/v1.0/search'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Use this API to search for matching content, products and other indexed data. Choose GET when query-string parameters are sufficient; choose POST /search when you need request body features such as `facetConfigurations` (faceted aggregations), Search Blueprint context attributes (`search.experiences.*`), or other request-body-only constructs. Refer to https://learn.liferay.com/w/dxp/integration/headless-apis/search-apis/search-api-basics for more information."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Specify the External Reference Code (ERC) of a search blueprint to control the search query and configuration. This is preferred over the blueprint ID. For POST /search requests, use the `search.experiences.blueprint.external.reference.code` request body attribute instead. Search Blueprints is available on DXP Enterprise tier with Liferay Enterprise Search (LES) add-on subscription.",
				example = "PRODUCT_SEARCH",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "blueprintExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Set this to 'true' to return results even if the search parameter is omitted from the request (no search keywords specified). For POST /search requests, use the `search.empty.search` request body attribute instead.",
				example = "true",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "emptySearch"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "A comma separated list of entryClassNames to be searched using the class names of the types to search. Defaults to all searchable types. Out-of-the-box searchable types and their corresponding class names include - Basic Document (CMS) - com.liferay.object.model.ObjectDefinition#Z7P5, Basic Web Content (CMS) - com.liferay.object.model.ObjectDefinition#H4T4, Blog (CMS) - com.liferay.object.model.ObjectDefinition#Y8H4, Blogs Entry - com.liferay.blogs.model.BlogsEntry, Bookmarks Entry - com.liferay.bookmarks.model.BookmarksEntry, Bookmarks Folder - com.liferay.bookmarks.model.BookmarksFolder, Calendar Event - com.liferay.calendar.model.CalendarBooking, Commerce Product - com.liferay.commerce.product.model.CPDefinition, Document - com.liferay.document.library.kernel.model.DLFileEntry, Documents Folder - com.liferay.document.library.kernel.model.DLFolder, Dynamic Data Lists Record - com.liferay.dynamic.data.lists.model.DDLRecord, External Video (CMS) - com.liferay.object.model.ObjectDefinition#S1T1, Form Record - com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord, Knowledge Base Article - com.liferay.knowledge.base.model.KBArticle, Message Boards Message - com.liferay.message.boards.model.MBMessage, Object Entry Folder - com.liferay.object.model.ObjectEntryFolder, Page - com.liferay.portal.kernel.model.Layout, Sharing Entry - com.liferay.sharing.model.SharingEntry, User - com.liferay.portal.kernel.model.User, Web Content Article - com.liferay.journal.model.JournalArticle, and Web Content Folder - com.liferay.journal.model.JournalFolder. Entries marked `(CMS)` are seeded by the Liferay CMS site initializer and may be absent on instances that have not run it. Custom Objects and custom CMS Content Structures follow the class name format com.liferay.object.model.ObjectDefinition#<OBJECT-DEFINITION-CLASS-NAME>, where OBJECT-DEFINITION-CLASS-NAME is the Class Name of the Object Definition (for example, com.liferay.object.model.ObjectDefinition#H4T4 for Basic Web Content (CMS)). To discover available class names at runtime, call GET /o/search-experiences-rest/v1.0/searchable-asset-names (admin permission required).",
				example = "com.liferay.journal.model.JournalArticle,com.liferay.blogs.model.BlogsEntry",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "entryClassNames"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Use this parameter to specify and return only the fields specified in each of the items in the response. When requesting multiple types of items (when parameter entryClassNames is empty or specifies multiple class names), only common fields can be specified, otherwise the API returns an empty object for each result item.",
				example = "title,description,score",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Filters across different fields. Supported fields are creatorId (the creator User's ID), folderId (a folder's ID), groupIds (Site, Asset Library, or Space IDs - corresponding search index field is groupId; only approved items are returned by default), objectDefinitionId (an Object Definition's ID), status (workflow status ID - supported statuses are '-1' = Any, '0' = Approved, '1' = Pending, '2' = Draft, '3' = Expired, '4' = Denied, '5' = Inactive, '6' = Incomplete, '7' = Scheduled, '8' = In Trash, '9' = Empty), taxonomyCategoryIds (Asset category IDs), objectDefinitionExternalReferenceCode (Object Definition External Reference Code), extension (file extension), dateCreated (the date the entry was created in OData Edm.Date format YYYY-MM-DD), dateDisplay (the date the entry was displayed in OData Edm.Date format YYYY-MM-DD), dateExpiration (the date the entry expires in OData Edm.Date format YYYY-MM-DD), dateModified (the date the entry was modified in OData Edm.Date format YYYY-MM-DD), datePublish (the date the entry was published in OData Edm.Date format YYYY-MM-DD), dateReview (the date the entry should be reviewed in OData Edm.Date format YYYY-MM-DD), keywords (Asset tag names), and title (Asset title on the locale specified by the 'Accept-Language' request header). Supported operators are `eq`, `ne`, `gt`, `ge`, `lt`, `le`; multivalue collections use `any()`/`all()` (for example, `groupIds/any(g:g eq 20121)`). Web content articles (JournalArticle) cannot be filtered by status - for those, limit by site using `scope`/`groupIds` instead, which returns only approved items by default. See https://learn.liferay.com/w/dxp/integration/headless-apis/using-liferay-as-a-headless-platform/consuming-apis/api-query-parameters for more information on the standard API query parameters. For more robust filtering options, use Search Blueprints (available on DXP Enterprise tier with Liferay Enterprise Search (LES) add-on subscription), which can encapsulate more filters and operates directly with search index fields. Learn more at https://learn.liferay.com/w/dxp/search/liferay-enterprise-search/search-blueprints.",
				example = "groupIds/any(g:g eq 20121)",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Supports 'embedded' to return the full entity payload for each result, following the entity's own headless API schema. The MCP Server may default to this value.",
				example = "embedded",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "nestedFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Specify which page to return out of all the pages available. Defaults to 1. See https://learn.liferay.com/w/dxp/integration/headless-apis/using-liferay-as-a-headless-platform/consuming-apis/api-query-parameters for more information.",
				example = "1",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Specify how many items you want per page. Defaults to 20. See https://learn.liferay.com/w/dxp/integration/headless-apis/using-liferay-as-a-headless-platform/consuming-apis/api-query-parameters for more information.",
				example = "20",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Comma-separated list of fields to exclude from each result. This is the inverse of the fields parameter. Use either fields (allow list) or restrictFields (block list), not both.",
				example = "actions,embedded",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Specify a list of Site, Asset Library, or Space IDs or External Reference Codes (ERCs) to search. You can mix IDs and ERCs in the same request.",
				example = "20121,L_GUEST",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "scope"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Search by keyword(s).", example = "liferay",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort results by ascending or descending order. Use format `field:asc` or `field:desc`. Supported fields are dateCreated (the date the entry was created in OData Edm.Date format YYYY-MM-DD), dateDisplay (the date the entry was displayed in OData Edm.Date format YYYY-MM-DD), dateExpiration (the date the entry expires in OData Edm.Date format YYYY-MM-DD), dateModified (the date the entry was modified in OData Edm.Date format YYYY-MM-DD), datePublish (the date the entry was published in OData Edm.Date format YYYY-MM-DD), dateReview (the date the entry should be reviewed in OData Edm.Date format YYYY-MM-DD), keywords (Asset tag names - corresponding search index field is assetTagNames.lowercase), and title (Asset title on the locale specified by the 'Accept-Language' request header - corresponding search index field is localized_title_[locale].keyword_lowercase where 'locale' is a language ID like 'en_US'). Defaults to order by relevance score descending. See https://learn.liferay.com/w/dxp/integration/headless-apis/using-liferay-as-a-headless-platform/consuming-apis/api-query-parameters for more information. Note - a search blueprint can also have sort configurations. See https://learn.liferay.com/w/dxp/search/liferay-enterprise-search/search-blueprints/sorting-results-in-a-search-blueprint#search-blueprints-versus-the-headless-api to understand how these blueprint-added sorts interact with the sort parameter if you plan to use both. Search Blueprints is available on DXP Enterprise tier with Liferay Enterprise Search (LES) add-on subscription.",
				example = "dateModified:desc",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "SearchResult")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/search")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<SearchResult> getSearchPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("blueprintExternalReferenceCode")
			String blueprintExternalReferenceCode,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("emptySearch")
			Boolean emptySearch,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("entryClassNames")
			String entryClassNames,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("scope")
			String scope,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/search/v1.0/search' -d $'{"attributes": ___, "facetConfigurations": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Use this API to search for matching content, products and other indexed data. Choose POST over GET when you need request body features such as facets (aggregations) via `facetConfigurations`, Search Blueprint context attributes (`search.experiences.*`), or other request-body-only constructs. Refer to https://learn.liferay.com/w/dxp/integration/headless-apis/search-apis/search-api-basics for more information."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "A comma separated list of entryClassNames to be searched using the class names of the types to search. Defaults to all searchable types. Out-of-the-box searchable types and their corresponding class names include - Basic Document (CMS) - com.liferay.object.model.ObjectDefinition#Z7P5, Basic Web Content (CMS) - com.liferay.object.model.ObjectDefinition#H4T4, Blog (CMS) - com.liferay.object.model.ObjectDefinition#Y8H4, Blogs Entry - com.liferay.blogs.model.BlogsEntry, Bookmarks Entry - com.liferay.bookmarks.model.BookmarksEntry, Bookmarks Folder - com.liferay.bookmarks.model.BookmarksFolder, Calendar Event - com.liferay.calendar.model.CalendarBooking, Commerce Product - com.liferay.commerce.product.model.CPDefinition, Document - com.liferay.document.library.kernel.model.DLFileEntry, Documents Folder - com.liferay.document.library.kernel.model.DLFolder, Dynamic Data Lists Record - com.liferay.dynamic.data.lists.model.DDLRecord, External Video (CMS) - com.liferay.object.model.ObjectDefinition#S1T1, Form Record - com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord, Knowledge Base Article - com.liferay.knowledge.base.model.KBArticle, Message Boards Message - com.liferay.message.boards.model.MBMessage, Object Entry Folder - com.liferay.object.model.ObjectEntryFolder, Page - com.liferay.portal.kernel.model.Layout, Sharing Entry - com.liferay.sharing.model.SharingEntry, User - com.liferay.portal.kernel.model.User, Web Content Article - com.liferay.journal.model.JournalArticle, and Web Content Folder - com.liferay.journal.model.JournalFolder. Entries marked `(CMS)` are seeded by the Liferay CMS site initializer and may be absent on instances that have not run it. Custom Objects and custom CMS Content Structures follow the class name format com.liferay.object.model.ObjectDefinition#<OBJECT-DEFINITION-CLASS-NAME>, where OBJECT-DEFINITION-CLASS-NAME is the Class Name of the Object Definition (for example, com.liferay.object.model.ObjectDefinition#H4T4 for Basic Web Content (CMS)). To discover available class names at runtime, call GET /o/search-experiences-rest/v1.0/searchable-asset-names (admin permission required).",
				example = "com.liferay.journal.model.JournalArticle,com.liferay.blogs.model.BlogsEntry",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "entryClassNames"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Use this parameter to specify and return only the fields specified in each of the items in the response. When requesting multiple types of items (when parameter entryClassNames is empty or specifies multiple class names), only common fields can be specified, otherwise the API returns an empty object for each result item.",
				example = "title,description,score",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Filters across different fields. Supported fields are creatorId (the creator User's ID), folderId (a folder's ID), groupIds (Site, Asset Library, or Space IDs - corresponding search index field is groupId; only approved items are returned by default), objectDefinitionId (an Object Definition's ID), status (workflow status ID - supported statuses are '-1' = Any, '0' = Approved, '1' = Pending, '2' = Draft, '3' = Expired, '4' = Denied, '5' = Inactive, '6' = Incomplete, '7' = Scheduled, '8' = In Trash, '9' = Empty), taxonomyCategoryIds (Asset category IDs), objectDefinitionExternalReferenceCode (Object Definition External Reference Code), extension (file extension), dateCreated (the date the entry was created in OData Edm.Date format YYYY-MM-DD), dateDisplay (the date the entry was displayed in OData Edm.Date format YYYY-MM-DD), dateExpiration (the date the entry expires in OData Edm.Date format YYYY-MM-DD), dateModified (the date the entry was modified in OData Edm.Date format YYYY-MM-DD), datePublish (the date the entry was published in OData Edm.Date format YYYY-MM-DD), dateReview (the date the entry should be reviewed in OData Edm.Date format YYYY-MM-DD), keywords (Asset tag names), and title (Asset title on the locale specified by the 'Accept-Language' request header). Supported operators are `eq`, `ne`, `gt`, `ge`, `lt`, `le`; multivalue collections use `any()`/`all()` (for example, `groupIds/any(g:g eq 20121)`). Web content articles (JournalArticle) cannot be filtered by status - for those, limit by site using `scope`/`groupIds` instead, which returns only approved items by default. See https://learn.liferay.com/w/dxp/integration/headless-apis/using-liferay-as-a-headless-platform/consuming-apis/api-query-parameters for more information on the standard API query parameters. For more robust filtering options, use Search Blueprints (available on DXP Enterprise tier with Liferay Enterprise Search (LES) add-on subscription), which can encapsulate more filters and operates directly with search index fields. Learn more at https://learn.liferay.com/w/dxp/search/liferay-enterprise-search/search-blueprints.",
				example = "groupIds/any(g:g eq 20121)",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Supports 'embedded' to return the full entity payload for each result, following the entity's own headless API schema. The MCP Server may default to this value.",
				example = "embedded",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "nestedFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Specify which page to return out of all the pages available. Defaults to 1. See https://learn.liferay.com/w/dxp/integration/headless-apis/using-liferay-as-a-headless-platform/consuming-apis/api-query-parameters for more information.",
				example = "1",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Specify how many items you want per page. Defaults to 20. See https://learn.liferay.com/w/dxp/integration/headless-apis/using-liferay-as-a-headless-platform/consuming-apis/api-query-parameters for more information.",
				example = "20",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Comma-separated list of fields to exclude from each result. This is the inverse of the fields parameter. Use either fields (allow list) or restrictFields (block list), not both.",
				example = "actions,embedded",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Specify a list of Site, Asset Library, or Space IDs or External Reference Codes (ERCs) to search. You can mix IDs and ERCs in the same request.",
				example = "20121,L_GUEST",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "scope"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Search by keyword(s).", example = "liferay",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort results by ascending or descending order. Use format `field:asc` or `field:desc`. Supported fields are dateCreated (the date the entry was created in OData Edm.Date format YYYY-MM-DD), dateDisplay (the date the entry was displayed in OData Edm.Date format YYYY-MM-DD), dateExpiration (the date the entry expires in OData Edm.Date format YYYY-MM-DD), dateModified (the date the entry was modified in OData Edm.Date format YYYY-MM-DD), datePublish (the date the entry was published in OData Edm.Date format YYYY-MM-DD), dateReview (the date the entry should be reviewed in OData Edm.Date format YYYY-MM-DD), keywords (Asset tag names - corresponding search index field is assetTagNames.lowercase), and title (Asset title on the locale specified by the 'Accept-Language' request header - corresponding search index field is localized_title_[locale].keyword_lowercase where 'locale' is a language ID like 'en_US'). Defaults to order by relevance score descending. See https://learn.liferay.com/w/dxp/integration/headless-apis/using-liferay-as-a-headless-platform/consuming-apis/api-query-parameters for more information. Note - a search blueprint can also have sort configurations. See https://learn.liferay.com/w/dxp/search/liferay-enterprise-search/search-blueprints/sorting-results-in-a-search-blueprint#search-blueprints-versus-the-headless-api to understand how these blueprint-added sorts interact with the sort parameter if you plan to use both. Search Blueprints is available on DXP Enterprise tier with Liferay Enterprise Search (LES) add-on subscription.",
				example = "dateModified:desc",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "SearchResult")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/search")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<SearchResult> postSearchPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("entryClassNames")
			String entryClassNames,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("scope")
			String scope,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts,
			SearchRequestBody searchRequestBody)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<SearchResult> searchResults,
			Map<String, Serializable> parameters)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Override
	public void delete(
			Collection<SearchResult> searchResults,
			Map<String, Serializable> parameters)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	public Set<String> getAvailableCreateStrategies() {
		return SetUtil.fromArray();
	}

	public Set<String> getAvailableUpdateStrategies() {
		return SetUtil.fromArray();
	}

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return getEntityModel(
			new MultivaluedHashMap<String, Object>(multivaluedMap));
	}

	public String getResourceName() {
		return "SearchResult";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<SearchResult> read(
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Override
	public void setLanguageId(String languageId) {
		this.contextAcceptLanguage = new AcceptLanguage() {

			@Override
			public List<Locale> getLocales() {
				return null;
			}

			@Override
			public String getPreferredLanguageId() {
				return languageId;
			}

			@Override
			public Locale getPreferredLocale() {
				return LocaleUtil.fromLanguageId(languageId);
			}

			@Override
			public boolean isAcceptAllLanguages() {
				if (ExportImportThreadLocal.isExportInProcess()) {
					return true;
				}

				return AcceptLanguage.super.isAcceptAllLanguages();
			}

		};
	}

	@Override
	public void update(
			Collection<SearchResult> searchResults,
			Map<String, Serializable> parameters)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<SearchResult>,
			 UnsafeFunction<SearchResult, SearchResult, Exception>, Exception>
				contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<SearchResult>, UnsafeConsumer<SearchResult, Exception>,
			 Exception> contextBatchUnsafeConsumer) {

		this.contextBatchUnsafeConsumer = contextBatchUnsafeConsumer;
	}

	public void setContextCompany(
		com.liferay.portal.kernel.model.Company contextCompany) {

		this.contextCompany = contextCompany;
	}

	public void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {

		this.contextHttpServletRequest = contextHttpServletRequest;
	}

	public void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {

		this.contextHttpServletResponse = contextHttpServletResponse;
	}

	public void setContextUriInfo(UriInfo contextUriInfo) {
		this.contextUriInfo = UriInfoUtil.getVulcanUriInfo(
			getApplicationPath(), contextUriInfo);
	}

	public void setContextUser(
		com.liferay.portal.kernel.model.User contextUser) {

		this.contextUser = contextUser;
	}

	public void setExpressionConvert(
		ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
			expressionConvert) {

		this.expressionConvert = expressionConvert;
	}

	public void setFilterParserProvider(
		FilterParserProvider filterParserProvider) {

		this.filterParserProvider = filterParserProvider;
	}

	public void setGroupLocalService(GroupLocalService groupLocalService) {
		this.groupLocalService = groupLocalService;
	}

	public void setResourceActionLocalService(
		ResourceActionLocalService resourceActionLocalService) {

		this.resourceActionLocalService = resourceActionLocalService;
	}

	public void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService) {

		this.resourcePermissionLocalService = resourcePermissionLocalService;
	}

	public void setRoleLocalService(RoleLocalService roleLocalService) {
		this.roleLocalService = roleLocalService;
	}

	public void setSortParserProvider(SortParserProvider sortParserProvider) {
		this.sortParserProvider = sortParserProvider;
	}

	protected String getApplicationPath() {
		return "search";
	}

	public void setVulcanBatchEngineExportTaskResource(
		VulcanBatchEngineExportTaskResource
			vulcanBatchEngineExportTaskResource) {

		this.vulcanBatchEngineExportTaskResource =
			vulcanBatchEngineExportTaskResource;
	}

	public void setVulcanBatchEngineImportTaskResource(
		VulcanBatchEngineImportTaskResource
			vulcanBatchEngineImportTaskResource) {

		this.vulcanBatchEngineImportTaskResource =
			vulcanBatchEngineImportTaskResource;
	}

	@Override
	public com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString, Map<String, List<String>> multivaluedMap) {

		try {
			EntityModel entityModel = getEntityModel(multivaluedMap);

			FilterParser filterParser = filterParserProvider.provide(
				entityModel);

			com.liferay.portal.odata.filter.Filter oDataFilter =
				new com.liferay.portal.odata.filter.Filter(
					filterParser.parse(filterString));

			return expressionConvert.convert(
				oDataFilter.getExpression(),
				contextAcceptLanguage.getPreferredLocale(), entityModel);
		}
		catch (Exception exception) {
			_log.error("Invalid filter " + filterString, exception);

			return null;
		}
	}

	@Override
	public com.liferay.portal.kernel.search.Sort[] toSorts(String sortString) {
		if (Validator.isNull(sortString)) {
			return null;
		}

		try {
			SortParser sortParser = sortParserProvider.provide(
				getEntityModel(Collections.emptyMap()));

			if (sortParser == null) {
				return null;
			}

			com.liferay.portal.odata.sort.Sort oDataSort =
				new com.liferay.portal.odata.sort.Sort(
					sortParser.parse(sortString));

			List<SortField> sortFields = oDataSort.getSortFields();
			com.liferay.portal.kernel.search.Sort[] sorts =
				new com.liferay.portal.kernel.search.Sort[sortFields.size()];

			for (int i = 0; i < sortFields.size(); i++) {
				SortField sortField = sortFields.get(i);

				sorts[i] = new com.liferay.portal.kernel.search.Sort(
					sortField.getSortableFieldName(
						contextAcceptLanguage.getPreferredLocale()),
					!sortField.isAscending());
			}

			return sorts;
		}
		catch (Exception exception) {
			_log.error("Invalid sort " + sortString, exception);

			return new com.liferay.portal.kernel.search.Sort[0];
		}
	}

	protected Map<String, String> addAction(
		String actionName,
		com.liferay.portal.kernel.model.GroupedModel groupedModel,
		String methodName) {

		return ActionUtil.addAction(
			actionName, getClass(), groupedModel, methodName,
			contextScopeChecker, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, Long id, String methodName, Long ownerId,
		String permissionName, Long siteId) {

		return ActionUtil.addAction(
			actionName, getClass(), id, methodName, contextScopeChecker,
			ownerId, permissionName, siteId, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, Long id, String methodName,
		ModelResourcePermission modelResourcePermission) {

		return ActionUtil.addAction(
			actionName, getClass(), id, methodName, contextScopeChecker,
			modelResourcePermission, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, String methodName, String permissionName,
		Long siteId) {

		return addAction(
			actionName, siteId, methodName, null, permissionName, siteId);
	}

	protected <T, R, E extends Throwable> List<R> transform(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transform(collection, unsafeFunction);
	}

	public static <R, E extends Throwable> R[] transform(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	public static <R, E extends Throwable> R[] transform(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] transform(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] transformToArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transformToArray(
			collection, unsafeFunction, clazz);
	}

	public static <T, E extends Throwable> boolean[] transformToBooleanArray(
		Collection<T> collection,
		UnsafeFunction<T, Boolean, E> unsafeFunction) {

		return TransformUtil.transformToBooleanArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> boolean[] transformToBooleanArray(
		T[] array, UnsafeFunction<T, Boolean, E> unsafeFunction) {

		return TransformUtil.transformToBooleanArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] transformToByteArray(
		Collection<T> collection, UnsafeFunction<T, Byte, E> unsafeFunction) {

		return TransformUtil.transformToByteArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] transformToByteArray(
		T[] array, UnsafeFunction<T, Byte, E> unsafeFunction) {

		return TransformUtil.transformToByteArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> double[] transformToDoubleArray(
		Collection<T> collection, UnsafeFunction<T, Double, E> unsafeFunction) {

		return TransformUtil.transformToDoubleArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> double[] transformToDoubleArray(
		T[] array, UnsafeFunction<T, Double, E> unsafeFunction) {

		return TransformUtil.transformToDoubleArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] transformToFloatArray(
		Collection<T> collection, UnsafeFunction<T, Float, E> unsafeFunction) {

		return TransformUtil.transformToFloatArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] transformToFloatArray(
		T[] array, UnsafeFunction<T, Float, E> unsafeFunction) {

		return TransformUtil.transformToFloatArray(array, unsafeFunction);
	}

	public static <T, R, E extends Throwable> int[] transformToIntArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToIntArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> int[] transformToIntArray(
		T[] array, UnsafeFunction<T, Integer, E> unsafeFunction) {

		return TransformUtil.transformToIntArray(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> transformToList(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> transformToList(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> transformToList(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] transformToLongArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToLongArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> long[] transformToLongArray(
		T[] array, UnsafeFunction<T, Long, E> unsafeFunction) {

		return TransformUtil.transformToLongArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] transformToShortArray(
		Collection<T> collection, UnsafeFunction<T, Short, E> unsafeFunction) {

		return TransformUtil.transformToShortArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] transformToShortArray(
		T[] array, UnsafeFunction<T, Short, E> unsafeFunction) {

		return TransformUtil.transformToShortArray(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransform(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransform(collection, unsafeFunction);
	}

	public static <R, E extends Throwable> R[] unsafeTransform(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	public static <R, E extends Throwable> R[] unsafeTransform(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] unsafeTransform(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] unsafeTransformToArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransformToArray(
			collection, unsafeFunction, clazz);
	}

	public static <T, E extends Throwable> boolean[]
			unsafeTransformToBooleanArray(
				Collection<T> collection,
				UnsafeFunction<T, Boolean, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToBooleanArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> boolean[]
			unsafeTransformToBooleanArray(
				T[] array, UnsafeFunction<T, Boolean, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToBooleanArray(
			array, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] unsafeTransformToByteArray(
			Collection<T> collection, UnsafeFunction<T, Byte, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToByteArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] unsafeTransformToByteArray(
			T[] array, UnsafeFunction<T, Byte, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToByteArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> double[]
			unsafeTransformToDoubleArray(
				Collection<T> collection,
				UnsafeFunction<T, Double, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToDoubleArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> double[]
			unsafeTransformToDoubleArray(
				T[] array, UnsafeFunction<T, Double, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToDoubleArray(
			array, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] unsafeTransformToFloatArray(
			Collection<T> collection,
			UnsafeFunction<T, Float, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToFloatArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] unsafeTransformToFloatArray(
			T[] array, UnsafeFunction<T, Float, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToFloatArray(array, unsafeFunction);
	}

	public static <T, R, E extends Throwable> int[] unsafeTransformToIntArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToIntArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> int[] unsafeTransformToIntArray(
			T[] array, UnsafeFunction<T, Integer, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToIntArray(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransformToList(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] unsafeTransformToLongArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToLongArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> long[] unsafeTransformToLongArray(
			T[] array, UnsafeFunction<T, Long, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToLongArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] unsafeTransformToShortArray(
			Collection<T> collection,
			UnsafeFunction<T, Short, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToShortArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] unsafeTransformToShortArray(
			T[] array, UnsafeFunction<T, Short, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToShortArray(array, unsafeFunction);
	}

	protected AcceptLanguage contextAcceptLanguage;
	protected UnsafeBiConsumer
		<Collection<SearchResult>,
		 UnsafeFunction<SearchResult, SearchResult, Exception>, Exception>
			contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<SearchResult>, UnsafeConsumer<SearchResult, Exception>,
		 Exception> contextBatchUnsafeConsumer;
	protected com.liferay.portal.kernel.model.Company contextCompany;
	protected HttpServletRequest contextHttpServletRequest;
	protected HttpServletResponse contextHttpServletResponse;
	protected Object contextScopeChecker;
	protected UriInfo contextUriInfo;
	protected com.liferay.portal.kernel.model.User contextUser;
	protected ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
		expressionConvert;
	protected FilterParserProvider filterParserProvider;
	protected GroupLocalService groupLocalService;
	protected ResourceActionLocalService resourceActionLocalService;
	protected ResourcePermissionLocalService resourcePermissionLocalService;
	protected RoleLocalService roleLocalService;
	protected SortParserProvider sortParserProvider;
	protected VulcanBatchEngineExportTaskResource
		vulcanBatchEngineExportTaskResource;
	protected VulcanBatchEngineImportTaskResource
		vulcanBatchEngineImportTaskResource;

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseSearchResultResourceImpl.class);

}
// LIFERAY-REST-BUILDER-HASH:-1348333046