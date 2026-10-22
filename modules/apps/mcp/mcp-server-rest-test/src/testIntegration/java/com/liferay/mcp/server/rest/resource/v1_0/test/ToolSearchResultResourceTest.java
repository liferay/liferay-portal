/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.client.dto.v1_0.Prerequisite;
import com.liferay.mcp.server.rest.client.dto.v1_0.ToolSearchResult;
import com.liferay.mcp.server.rest.client.http.HttpInvoker;
import com.liferay.mcp.server.rest.client.pagination.Page;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@FeatureFlag("LPD-63311")
@RunWith(Arquillian.class)
public class ToolSearchResultResourceTest
	extends BaseToolSearchResultResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		MCPServerTestUtil.processBatchEngineUnits();
	}

	@Override
	@Test
	public void testGetToolSearchToolSearchResultsPage() throws Exception {
		_testGetToolSearchToolSearchResultsPageWhenSearchIsTooLong();
		_testGetToolSearchToolSearchResultsPageWithMaxResultsCount();
		_testGetToolSearchToolSearchResultsPageWithNothingRelevant();
		_testGetToolSearchToolSearchResultsPageWithObjectDefinition();
		_testGetToolSearchToolSearchResultsPageWithPrerequisites();
		_testGetToolSearchToolSearchResultsPageWithScope();
		_testGetToolSearchToolSearchResultsPageWithToolSearchProfile();
	}

	private List<ToolSearchResult> _getToolSearchResults(
			boolean includeRequiredInputSchema, String search)
		throws Exception {

		Page<ToolSearchResult> toolSearchResultsPage =
			toolSearchResultResource.getToolSearchToolSearchResultsPage(
				includeRequiredInputSchema, search);

		return new ArrayList<>(toolSearchResultsPage.getItems());
	}

	private String _mergeToolNames(List<ToolSearchResult> toolSearchResults) {
		return StringUtil.merge(
			ListUtil.toList(toolSearchResults, ToolSearchResult::getToolName),
			StringPool.SPACE);
	}

	private void _testGetToolSearchToolSearchResultsPageWhenSearchIsTooLong()
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			toolSearchResultResource.
				getToolSearchToolSearchResultsPageHttpResponse(
					false,
					StringUtil.merge(
						Collections.nCopies(30, "create a blog posting"),
						StringPool.SPACE));

		Assert.assertEquals(400, httpResponse.getStatusCode());

		String content = httpResponse.getContent();

		Assert.assertTrue(content.contains("one action at a time"));
	}

	private void _testGetToolSearchToolSearchResultsPageWithMaxResultsCount()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						"com.liferay.mcp.server.rest.internal.configuration." +
							"MCPServerConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).put(
							"searchToolMaxResultsCount", 3
						).build())) {

			List<ToolSearchResult> toolSearchResults = _getToolSearchResults(
				false, "create a site");

			Assert.assertEquals(
				_mergeToolNames(toolSearchResults), 3,
				toolSearchResults.size());
		}
	}

	private void _testGetToolSearchToolSearchResultsPageWithNothingRelevant()
		throws Exception {

		List<ToolSearchResult> toolSearchResults = _getToolSearchResults(
			false, "zyxwvu qponml kjihgf");

		Assert.assertTrue(toolSearchResults.isEmpty());
	}

	private void _testGetToolSearchToolSearchResultsPageWithObjectDefinition()
		throws Exception {

		String suffix = StringUtil.upperCaseFirstLetter(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		String name = "PetStore" + suffix;

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(name);

		objectDefinition =
			ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		String search = "create a pet store " + StringUtil.toLowerCase(suffix);

		List<ToolSearchResult> toolSearchResults = _getToolSearchResults(
			false, search);

		ToolSearchResult toolSearchResult = toolSearchResults.getFirst();

		Assert.assertEquals(
			_mergeToolNames(toolSearchResults), "post" + name,
			toolSearchResult.getToolName());
		Assert.assertTrue(
			StringUtil.startsWith(toolSearchResult.getToolSetName(), "c-"));

		ObjectDefinitionLocalServiceUtil.deleteObjectDefinition(
			objectDefinition);

		for (ToolSearchResult remainingToolSearchResult :
				_getToolSearchResults(false, search)) {

			Assert.assertNotEquals(
				"post" + name, remainingToolSearchResult.getToolName());
		}
	}

	private void _testGetToolSearchToolSearchResultsPageWithPrerequisites()
		throws Exception {

		List<ToolSearchResult> toolSearchResults = _getToolSearchResults(
			true, "create a web content article");

		ToolSearchResult toolSearchResult = toolSearchResults.getFirst();

		Assert.assertEquals(
			_mergeToolNames(toolSearchResults), "postSiteStructuredContent",
			toolSearchResult.getToolName());

		Map<String, String> prerequisiteToolNames = new HashMap<>();

		for (Prerequisite prerequisite : toolSearchResult.getPrerequisites()) {
			prerequisiteToolNames.put(
				prerequisite.getParameter(), prerequisite.getToolName());
		}

		Assert.assertEquals(
			prerequisiteToolNames.toString(), "getSiteContentStructuresPage",
			prerequisiteToolNames.get("contentStructureId"));
		Assert.assertEquals(
			prerequisiteToolNames.toString(), "getSitesPage",
			prerequisiteToolNames.get("siteId"));

		Assert.assertNotNull(
			toolSearchResult.getToolName(),
			toolSearchResult.getRequiredInputSchema());

		ToolSearchResult secondToolSearchResult = toolSearchResults.get(1);

		Assert.assertNull(
			secondToolSearchResult.getToolName(),
			secondToolSearchResult.getPrerequisites());
		Assert.assertNull(
			secondToolSearchResult.getToolName(),
			secondToolSearchResult.getRequiredInputSchema());
	}

	private void _testGetToolSearchToolSearchResultsPageWithScope()
		throws Exception {

		List<ToolSearchResult> toolSearchResults = _getToolSearchResults(
			false, "create a blog posting in a site");

		ToolSearchResult toolSearchResult = toolSearchResults.getFirst();

		Assert.assertEquals(
			_mergeToolNames(toolSearchResults), "postSiteBlogPosting",
			toolSearchResult.getToolName());
		Assert.assertEquals(
			"headless-delivery-v1.0", toolSearchResult.getToolSetName());
	}

	private void _testGetToolSearchToolSearchResultsPageWithToolSearchProfile()
		throws Exception {

		ObjectEntry mcpServerProfileObjectEntry =
			MCPServerTestUtil.fetchMCPServerProfileObjectEntry("default");

		List<String> toolNames = MCPServerTestUtil.getMCPServerProfileToolNames(
			mcpServerProfileObjectEntry);

		Assert.assertFalse(
			toolNames.contains("getToolSearchToolSearchResultsPage"));

		mcpServerProfileObjectEntry =
			MCPServerTestUtil.fetchMCPServerProfileObjectEntry("tool-search");

		toolNames = MCPServerTestUtil.getMCPServerProfileToolNames(
			mcpServerProfileObjectEntry);

		Assert.assertTrue(
			toolNames.contains("getToolSearchToolSearchResultsPage"));
		Assert.assertTrue(
			toolNames.contains("postToolSetToolSetNameToolInvoke"));
	}

}