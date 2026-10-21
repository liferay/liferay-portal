/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index.util;

import com.liferay.mcp.server.rest.internal.search.index.MCPTool;
import com.liferay.mcp.server.rest.internal.search.index.constants.MCPToolConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Petteri Karttunen
 */
public class MCPToolFactoryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetMCPTools() {
		MCPTool mcpTool = _getMCPTool(
			"post",
			_getOperationJSONObject(
				false, "BlogPosting", "postSiteBlogPosting"
			).put(
				"parameters", JSONUtil.putAll(JSONUtil.put("name", "siteId"))
			).put(
				"requestBody",
				JSONUtil.put(
					"content",
					JSONUtil.put(
						"application/json",
						JSONUtil.put(
							"schema",
							JSONUtil.put(
								"properties",
								JSONUtil.put(
									"articleBody",
									JSONUtil.put("type", "string")
								).put(
									"headline", JSONUtil.put("type", "string")
								)
							).put(
								"type", "object"
							))))
			).put(
				"responses",
				JSONUtil.put(
					"200",
					JSONUtil.put(
						"content",
						JSONUtil.put(
							"application/json",
							JSONUtil.put(
								"schema",
								JSONUtil.put(
									"properties",
									JSONUtil.put(
										"headline",
										JSONUtil.put("type", "string")
									).put(
										"id", JSONUtil.put("type", "integer")
									)
								).put(
									"type", "object"
								)))))
			),
			"/sites/{siteId}/blog-postings");

		Assert.assertNull(mcpTool.getCollection());
		Assert.assertEquals("BlogPosting", mcpTool.getEntityName());

		String[] expansions = mcpTool.getExpansions();

		_assertExpansion(expansions, "create blog posting in a site");

		Set<String> primaryVerbs = IntentUtil.getPrimaryVerbs(
			IntentUtil.INTENT_CREATE);

		Assert.assertEquals(
			Arrays.toString(expansions), primaryVerbs.size(),
			expansions.length);

		Assert.assertEquals(IntentUtil.INTENT_CREATE, mcpTool.getIntent());
		Assert.assertEquals("post", mcpTool.getMethod());
		Assert.assertEquals(StringPool.BLANK, mcpTool.getOperationVariant());
		Assert.assertArrayEquals(
			new String[] {"siteId"}, mcpTool.getParameterNames());
		Assert.assertEquals(
			"/o/test/sites/{siteId}/blog-postings", mcpTool.getPath());
		Assert.assertArrayEquals(
			new String[0], mcpTool.getRequiredPropertyCollections());
		Assert.assertArrayEquals(
			new String[] {"articleBody", "headline", "id"},
			mcpTool.getSchemaPropertyNames());
		Assert.assertEquals(
			"test-v1.0#postSiteBlogPosting", mcpTool.getToolKey());
		Assert.assertEquals("postSiteBlogPosting", mcpTool.getToolName());
		Assert.assertEquals("test-v1.0", mcpTool.getToolSetName());
		Assert.assertFalse(mcpTool.isDeprecated());
	}

	@Test
	public void testGetMCPToolsWithBatchTool() {
		String[] expansions = _getExpansions(
			false, "BlogPosting", "post", "/sites/{siteId}/blog-postings/batch",
			"postSiteBlogPostingBatch");

		_assertExpansion(expansions, "batch add blog postings in a site");

		for (String expansion : expansions) {
			Assert.assertTrue(
				expansion, StringUtil.startsWith(expansion, "batch "));
		}
	}

	@Test
	public void testGetMCPToolsWithByPhraseBeforeEntity() {
		_assertExpansion(
			_getExpansions(
				true, "ChildComment", "get",
				"/by-external-reference-code/{externalReferenceCode}/comments" +
					"/{commentExternalReferenceCode}/child-comments",
				"getByExternalReferenceCodeCommentChildCommentsPage"),
			"browse child comments by comment external reference code");
	}

	@Test
	public void testGetMCPToolsWithCollectionNounAfterEntity() {
		_assertExpansion(
			_getExpansions(
				true, "Catalog", "get", "/catalog/{catalogId}/permissions",
				"getCatalogPermissionsPage"),
			"browse catalog permissions by catalog id");
	}

	@Test
	public void testGetMCPToolsWithCollectionTool() {
		MCPTool mcpTool = _getMCPTool(
			"get",
			_getOperationJSONObject(
				true, "BlogPosting", "getSiteBlogPostingsPage"),
			"/sites/{siteId}/blog-postings");

		String[] expansions = mcpTool.getExpansions();

		_assertExpansion(expansions, "get all blog postings in a site");
		_assertExpansion(expansions, "list blog postings in a site");

		Assert.assertEquals("blogpostings", mcpTool.getCollection());
		Assert.assertEquals(IntentUtil.INTENT_LIST, mcpTool.getIntent());
	}

	@Test
	public void testGetMCPToolsWithDescription() {
		Assert.assertEquals(
			"Creates a blog posting. The site must exist.",
			_getDescription("The site must exist.", "Creates a blog posting"));
		Assert.assertEquals(
			"Creates a blog posting. The site must exist.",
			_getDescription("The site must exist.", "Creates a blog posting."));
		Assert.assertEquals(
			"The site must exist.",
			_getDescription("The site must exist.", StringPool.BLANK));
	}

	@Test
	public void testGetMCPToolsWithEntityAsParent() {
		String[] expansions = _getExpansions(
			false, "Site", "get",
			"/sites/by-external-reference-code/{externalReferenceCode}",
			"getSiteByExternalReferenceCode");

		_assertExpansion(expansions, "fetch site by external reference code");

		for (String expansion : expansions) {
			Assert.assertFalse(expansion, expansion.contains("in a site"));
		}
	}

	@Test
	public void testGetMCPToolsWithEntityEndingInPage() {
		_assertExpansion(
			_getExpansions(
				false, "WikiPage", "get", "/wiki-pages/{wikiPageId}",
				"getWikiPage"),
			"fetch wiki page by wiki page id");
		_assertExpansion(
			_getExpansions(
				false, "WikiPage", "post",
				"/wiki-nodes/{wikiNodeId}/wiki-pages", "postWikiNodeWikiPage"),
			"add wiki page in a wiki node");
	}

	@Test
	public void testGetMCPToolsWithEntityEndingInSS() {
		_assertExpansion(
			_getExpansions(
				true, "ExportProcess", "get",
				"/sites/{siteId}/export-processes",
				"getSiteExportProcessesPage"),
			"browse export processes in a site");
	}

	@Test
	public void testGetMCPToolsWithExportBatchTool() {
		MCPTool mcpTool = _getMCPTool(
			"post",
			_getOperationJSONObject(
				false, "LanguageOverride",
				"postLanguageOverridesPageExportBatch"),
			"/language-overrides/export-batch");

		Assert.assertArrayEquals(
			new String[] {"export language overrides"},
			mcpTool.getExpansions());
		Assert.assertEquals("export", mcpTool.getIntent());
		Assert.assertEquals(
			MCPToolConstants.OPERATION_VARIANT_EXPORT,
			mcpTool.getOperationVariant());

		_assertExpansion(
			_getExpansions(
				false, "PlacedOrderItem", "post",
				"/placed-orders/{placedOrderId}/placed-order-items" +
					"/export-batch",
				"postPlacedOrderPlacedOrderItemsPageExportBatch"),
			"export placed order items by placed order id");
	}

	@Test
	public void testGetMCPToolsWithModifierAfterEntity() {
		_assertExpansion(
			_getExpansions(
				true, "Document", "get",
				"/sites/{siteId}/documents/rated-by-me",
				"getSiteDocumentsRatedByMePage"),
			"browse documents in a site rated by me");
		_assertExpansion(
			_getExpansions(
				true, "WarehouseItem", "get", "/warehouseItems/updated",
				"getWarehouseItemsUpdatedPage"),
			"browse warehouse items updated");
	}

	@Test
	public void testGetMCPToolsWithOperationVariant() {
		Assert.assertEquals(
			StringPool.BLANK,
			_getOperationVariant(
				true, "ProcessMetric", "/processes/{processId}/metrics",
				"getProcessMetricsPage"));
		Assert.assertEquals(
			StringPool.BLANK,
			_getOperationVariant(
				true, "BlogPosting", "/sites/{siteId}/blog-postings",
				"getSiteBlogPostingsPage"));
		Assert.assertEquals(
			MCPToolConstants.OPERATION_VARIANT_NESTED,
			_getOperationVariant(
				false, "BlogPosting",
				"/sites/by-external-reference-code" +
					"/{siteExternalReferenceCode}/blog-postings" +
						"/by-external-reference-code/{externalReferenceCode}",
				"getSiteByExternalReferenceCodeBlogPostingByExternal" +
					"ReferenceCode"));
		Assert.assertEquals(
			MCPToolConstants.OPERATION_VARIANT_TRAVERSAL,
			_getOperationVariant(
				true, "Attachment", "/products/{id}/images",
				"getProductIdImagesPage"));
		Assert.assertEquals(
			MCPToolConstants.OPERATION_VARIANT_TRAVERSAL,
			_getOperationVariant(
				true, "Assignee",
				"/workflow-tasks/{workflowTaskId}/assignable-users",
				"getWorkflowTaskAssignableUsersPage"));
	}

	@Test
	public void testGetMCPToolsWithParameterBeforeEntity() {
		_assertExpansion(
			_getExpansions(
				false, "Attachment", "post", "/products/{id}/images",
				"postProductIdImage"),
			"add image in a product by id");
		_assertExpansion(
			_getExpansions(
				true, "Attachment", "get", "/products/{id}/images",
				"getProductIdImagesPage"),
			"browse images in a product by id");
	}

	@Test
	public void testGetMCPToolsWithParameterBetweenEntityWords() {
		_assertExpansion(
			_getExpansions(
				true, "OptionValue", "get", "/options/{id}/optionValues",
				"getOptionIdOptionValuesPage"),
			"browse option values by id");
	}

	@Test
	public void testGetMCPToolsWithParameterWordInsideAnotherWord() {
		_assertExpansion(
			_getExpansions(
				false, "Provider", "get", "/providers/{id}", "getProvider"),
			"fetch provider by id");
		_assertExpansion(
			_getExpansions(
				false, "Comment", "get", "/videos/{videoId}/comments/{id}",
				"getVideoComment"),
			"fetch comment in a video by id");
	}

	@Test
	public void testGetMCPToolsWithParentInEntityName() {
		_assertExpansion(
			_getExpansions(
				true, "ProcessMetric", "get", "/processes/{processId}/metrics",
				"getProcessMetricsPage"),
			"browse process metrics by process id");
	}

	@Test
	public void testGetMCPToolsWithParentReachedByExternalReferenceCode() {
		_assertExpansion(
			_getExpansions(
				true, "BlogPosting", "get",
				"/sites/by-external-reference-code" +
					"/{siteExternalReferenceCode}/blog-postings",
				"getSiteBlogPostingsPage"),
			"browse blog postings in a site");
	}

	@Test
	public void testGetMCPToolsWithParentStartingWithU() {
		_assertExpansion(
			_getExpansions(
				true, "Phone", "get", "/user-accounts/{userAccountId}/phones",
				"getUserAccountPhonesPage"),
			"browse phones in a user account");
	}

	@Test
	public void testGetMCPToolsWithParentStartingWithVowel() {
		_assertExpansion(
			_getExpansions(
				true, "Document", "get",
				"/asset-libraries/{assetLibraryId}/documents",
				"getAssetLibraryDocumentsPage"),
			"browse documents in an asset library");
	}

	@Test
	public void testGetMCPToolsWithPathParameterInToolName() {
		_assertExpansion(
			_getExpansions(
				false, "BlogPosting", "get",
				"/sites/{siteId}/blog-postings/by-external-reference-code" +
					"/{externalReferenceCode}",
				"getSiteBlogPostingByExternalReferenceCode"),
			"fetch blog posting in a site by external reference code");
	}

	@Test
	public void testGetMCPToolsWithQualifierAfterCollection() {
		MCPTool mcpTool = _getMCPTool(
			"get",
			_getOperationJSONObject(true, "Keyword", "getKeywordsRankedPage"),
			"/keywords/ranked");

		Assert.assertNull(mcpTool.getCollection());
	}

	@Test
	public void testGetMCPToolsWithRequiredPropertyCollections() {
		MCPTool mcpTool = _getMCPTool(
			"post",
			_getOperationJSONObject(
				false, "StructuredContent", "postStructuredContent"
			).put(
				"requestBody",
				JSONUtil.put(
					"content",
					JSONUtil.put(
						"application/json",
						JSONUtil.put(
							"schema",
							JSONUtil.put(
								"properties",
								JSONUtil.put(
									"contentStructureId",
									JSONUtil.put("type", "integer")
								).put(
									"id", JSONUtil.put("type", "integer")
								).put(
									"siteId", JSONUtil.put("type", "integer")
								).put(
									"taxonomyCategoryIds",
									JSONUtil.put(
										"items", JSONUtil.put("type", "integer")
									).put(
										"type", "array"
									)
								).put(
									"title", JSONUtil.put("type", "string")
								)
							).put(
								"required",
								JSONUtil.putAll(
									"contentStructureId", "id", "siteId",
									"taxonomyCategoryIds", "title")
							).put(
								"type", "object"
							))))
			),
			"/structured-contents");

		Assert.assertEquals(
			Arrays.asList(
				"contentStructureId#contentstructures", "siteId#sites",
				"taxonomyCategoryIds#taxonomycategories"),
			Arrays.asList(mcpTool.getRequiredPropertyCollections()));
	}

	@Test
	public void testGetMCPToolsWithTagAbsentFromToolName() {
		_assertExpansion(
			_getExpansions(
				false, "Organization", "delete",
				"/organizations/{organizationId}/user-accounts" +
					"/by-email-address/{emailAddress}",
				"deleteUserAccountByEmailAddress"),
			"delete user account by email address");
		_assertExpansion(
			_getExpansions(
				false, "ObjectEntry", "get",
				"/by-external-reference-code/{externalReferenceCode}",
				"getByExternalReferenceCode"),
			"fetch object entry by external reference code");
		_assertExpansion(
			_getExpansions(
				true, "Assignee", "get",
				"/workflow-tasks/{workflowTaskId}/assignable-users",
				"getWorkflowTaskAssignableUsersPage"),
			"browse assignable users in a workflow task");
	}

	@Test
	public void testGetMCPToolsWithToolAction() {
		MCPTool mcpTool = _getMCPTool(
			"put",
			_getOperationJSONObject(
				false, "StructuredContent", "putStructuredContentSubscribe"),
			"/structured-contents/{structuredContentId}/subscribe");

		_assertExpansion(
			mcpTool.getExpansions(),
			"subscribe structured content by structured content id");

		Assert.assertEquals("subscribe", mcpTool.getIntent());
	}

	@Test
	public void testGetMCPToolsWithToolActionAfterToolNameStemEndingInPage() {
		_assertExpansion(
			_getExpansions(
				false, "Subscription", "put",
				"/wiki-pages/{wikiPageId}/subscribe", "putWikiPageSubscribe"),
			"subscribe wiki page by wiki page id");
	}

	@Test
	public void testGetMCPToolsWithToolActionInParent() {
		_assertExpansion(
			_getExpansions(
				false, "BlogPosting", "put",
				"/sites/{siteId}/blog-postings/subscribe",
				"putSiteBlogPostingSubscribe"),
			"subscribe blog postings in a site");
	}

	@Test
	public void testGetMCPToolsWithoutEntity() {
		Assert.assertEquals(
			0,
			_getExpansions(false, "Site", "head", "/sites", "headSite").length);
		Assert.assertEquals(
			0,
			_getExpansions(
				false, StringPool.BLANK, "post", "/sites", "postSite").length);
	}

	private void _assertExpansion(String[] expansions, String expected) {
		Assert.assertTrue(
			Arrays.toString(expansions),
			ArrayUtil.contains(expansions, expected));
	}

	private String _getDescription(String description, String summary) {
		MCPTool mcpTool = _getMCPTool(
			"post",
			_getOperationJSONObject(
				false, "BlogPosting", "postSiteBlogPosting"
			).put(
				"description", description
			).put(
				"summary", summary
			),
			"/sites/{siteId}/blog-postings");

		return mcpTool.getDescription();
	}

	private String[] _getExpansions(
		boolean collection, String entityName, String method, String path,
		String toolName) {

		MCPTool mcpTool = _getMCPTool(
			method, _getOperationJSONObject(collection, entityName, toolName),
			path);

		return mcpTool.getExpansions();
	}

	private MCPTool _getMCPTool(
		String method, JSONObject operationJSONObject, String path) {

		List<MCPTool> mcpTools = MCPToolFactoryUtil.getMCPTools(
			"/test",
			JSONUtil.put(
				"paths",
				JSONUtil.put(path, JSONUtil.put(method, operationJSONObject))),
			"test-v1.0");

		Assert.assertEquals(mcpTools.toString(), 1, mcpTools.size());

		return mcpTools.get(0);
	}

	private JSONObject _getOperationJSONObject(
		boolean collection, String entityName, String toolName) {

		JSONObject schemaJSONObject = JSONUtil.put("type", "object");

		if (collection) {
			schemaJSONObject = JSONUtil.put(
				"items", JSONUtil.put("type", "object")
			).put(
				"type", "array"
			);
		}

		JSONObject operationJSONObject = JSONUtil.put(
			"operationId", toolName
		).put(
			"responses",
			JSONUtil.put(
				"200",
				JSONUtil.put(
					"content",
					JSONUtil.put(
						"application/json",
						JSONUtil.put("schema", schemaJSONObject))))
		);

		if (Validator.isNotNull(entityName)) {
			operationJSONObject.put("tags", JSONUtil.putAll(entityName));
		}

		return operationJSONObject;
	}

	private String _getOperationVariant(
		boolean collection, String entityName, String path, String toolName) {

		MCPTool mcpTool = _getMCPTool(
			"get", _getOperationJSONObject(collection, entityName, toolName),
			path);

		return mcpTool.getOperationVariant();
	}

}