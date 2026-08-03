/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.constants.JiraConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Pedro Malta
 */
public class JiraTicketResolverUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_languageUtilMockedStatic.when(
			() -> LanguageUtil.format(
				Mockito.any(Locale.class), Mockito.anyString(),
				Mockito.<Object>any())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);
	}

	@After
	public void tearDown() {
		_jiraUtilMockedStatic.close();
		_languageUtilMockedStatic.close();
	}

	@Test
	public void testGetRelatedLPDKeysDedupesSameKey() {
		JSONObject issueJSONObject = _createIssueJSONObject(
			_createIssueLinksJSONArray(
				_createIssueLinkJSONObject(
					JiraConstants.LINK_TYPE_RELATIONSHIP, "LPD-9", "LPD-9"),
				_createIssueLinkJSONObject(
					JiraConstants.LINK_TYPE_RELATIONSHIP, "LPD-9", null)));

		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			issueJSONObject);

		Assert.assertEquals(lpdKeys.toString(), 1, lpdKeys.size());
		Assert.assertEquals("LPD-9", lpdKeys.get(0));
	}

	@Test
	public void testGetRelatedLPDKeysReturnsKeysInLinkOrder() {
		JSONObject issueJSONObject = _createIssueJSONObject(
			_createIssueLinksJSONArray(
				_createIssueLinkJSONObject(
					JiraConstants.LINK_TYPE_RELATIONSHIP, "LPD-1", null),
				_createIssueLinkJSONObject(
					JiraConstants.LINK_TYPE_RELATIONSHIP, null, "LPD-2")));

		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			issueJSONObject);

		Assert.assertEquals(lpdKeys.toString(), 2, lpdKeys.size());
		Assert.assertEquals("LPD-1", lpdKeys.get(0));
		Assert.assertEquals("LPD-2", lpdKeys.get(1));
	}

	@Test
	public void testGetRelatedLPDKeysWhenFieldsIsMissing() {
		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			JSONFactoryUtil.createJSONObject());

		Assert.assertTrue(lpdKeys.isEmpty());
	}

	@Test
	public void testGetRelatedLPDKeysWhenInwardIssueIsLPD() {
		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			_createIssueJSONObject(
				_createIssueLinksJSONArray(
					_createIssueLinkJSONObject(
						JiraConstants.LINK_TYPE_RELATIONSHIP, "LPD-100",
						null))));

		Assert.assertEquals(lpdKeys.toString(), 1, lpdKeys.size());
		Assert.assertEquals("LPD-100", lpdKeys.get(0));
	}

	@Test
	public void testGetRelatedLPDKeysWhenIssueLinksIsEmpty() {
		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			_createIssueJSONObject(JSONFactoryUtil.createJSONArray()));

		Assert.assertTrue(lpdKeys.isEmpty());
	}

	@Test
	public void testGetRelatedLPDKeysWhenIssueLinksIsMissing() {
		JSONObject issueJSONObject = JSONUtil.put(
			"fields", JSONFactoryUtil.createJSONObject());

		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			issueJSONObject);

		Assert.assertTrue(lpdKeys.isEmpty());
	}

	@Test
	public void testGetRelatedLPDKeysWhenLinkedIssueKeyIsMissing() {
		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			_createIssueJSONObject(
				_createIssueLinksJSONArray(
					JSONUtil.put(
						"inwardIssue", JSONFactoryUtil.createJSONObject()
					).put(
						"type",
						JSONUtil.put(
							"name", JiraConstants.LINK_TYPE_RELATIONSHIP)
					))));

		Assert.assertTrue(lpdKeys.isEmpty());
	}

	@Test
	public void testGetRelatedLPDKeysWhenLinkedIssueKeyIsNotLPD() {
		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			_createIssueJSONObject(
				_createIssueLinksJSONArray(
					_createIssueLinkJSONObject(
						JiraConstants.LINK_TYPE_RELATIONSHIP, "LPE-5", null))));

		Assert.assertTrue(lpdKeys.isEmpty());
	}

	@Test
	public void testGetRelatedLPDKeysWhenLinkEntryIsNull() {
		JSONArray issueLinksJSONArray = JSONUtil.putAll(
			0,
			_createIssueLinkJSONObject(
				JiraConstants.LINK_TYPE_RELATIONSHIP, "LPD-7", null));

		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			_createIssueJSONObject(issueLinksJSONArray));

		Assert.assertEquals(lpdKeys.toString(), 1, lpdKeys.size());
		Assert.assertEquals("LPD-7", lpdKeys.get(0));
	}

	@Test
	public void testGetRelatedLPDKeysWhenLinkTypeIsMissing() {
		JSONObject linkJSONObject = JSONUtil.put(
			"inwardIssue", _createLinkedIssueJSONObject("LPD-1"));

		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			_createIssueJSONObject(_createIssueLinksJSONArray(linkJSONObject)));

		Assert.assertTrue(lpdKeys.isEmpty());
	}

	@Test
	public void testGetRelatedLPDKeysWhenLinkTypeIsNotRelationship() {
		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			_createIssueJSONObject(
				_createIssueLinksJSONArray(
					_createIssueLinkJSONObject("Blocks", "LPD-1", null))));

		Assert.assertTrue(lpdKeys.isEmpty());
	}

	@Test
	public void testGetRelatedLPDKeysWhenOutwardIssueIsLPD() {
		List<String> lpdKeys = JiraTicketResolverUtil.getRelatedLPDKeys(
			_createIssueJSONObject(
				_createIssueLinksJSONArray(
					_createIssueLinkJSONObject(
						JiraConstants.LINK_TYPE_RELATIONSHIP, null,
						"LPD-200"))));

		Assert.assertEquals(lpdKeys.toString(), 1, lpdKeys.size());
		Assert.assertEquals("LPD-200", lpdKeys.get(0));
	}

	@Test
	public void testResolveTicketsDedupesDuplicateTickets() throws Exception {
		List<String> resolvedTickets = JiraTicketResolverUtil.resolveTickets(
			_patcherConfiguration, "LPD-1,LPD-1");

		Assert.assertEquals(
			resolvedTickets.toString(), 1, resolvedTickets.size());
		Assert.assertEquals("LPD-1", resolvedTickets.get(0));
	}

	@Test
	public void testResolveTicketsPassesThroughTicketsWithoutLPE()
		throws Exception {

		List<String> resolvedTickets = JiraTicketResolverUtil.resolveTickets(
			_patcherConfiguration, "LPD-1,LPS-2");

		Assert.assertEquals(
			resolvedTickets.toString(), 2, resolvedTickets.size());
		Assert.assertEquals("LPD-1", resolvedTickets.get(0));
		Assert.assertEquals("LPS-2", resolvedTickets.get(1));
	}

	@Test
	public void testResolveTicketsResolvesLPEToSingleLPD() throws Exception {
		_jiraUtilMockedStatic.when(
			() -> JiraUtil.getIssueJSONObject(
				Mockito.any(PatcherConfiguration.class), Mockito.eq("LPE-123"))
		).thenReturn(
			_createIssueJSONObject(
				_createIssueLinksJSONArray(
					_createIssueLinkJSONObject(
						JiraConstants.LINK_TYPE_RELATIONSHIP, "LPD-500", null)))
		);

		List<String> resolvedTickets = JiraTicketResolverUtil.resolveTickets(
			_patcherConfiguration, "LPE-123");

		Assert.assertEquals(
			resolvedTickets.toString(), 1, resolvedTickets.size());
		Assert.assertEquals("LPD-500", resolvedTickets.get(0));
	}

	@Test
	public void testResolveTicketsThrowsWhenMultipleRelatedLPDsAreFound()
		throws Exception {

		_jiraUtilMockedStatic.when(
			() -> JiraUtil.getIssueJSONObject(
				Mockito.any(PatcherConfiguration.class), Mockito.eq("LPE-1"))
		).thenReturn(
			_createIssueJSONObject(
				_createIssueLinksJSONArray(
					_createIssueLinkJSONObject(
						JiraConstants.LINK_TYPE_RELATIONSHIP, "LPD-1", null),
					_createIssueLinkJSONObject(
						JiraConstants.LINK_TYPE_RELATIONSHIP, "LPD-2", null)))
		);

		try {
			JiraTicketResolverUtil.resolveTickets(
				_patcherConfiguration, "LPE-1");

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertEquals(
				"multiple-related-lpd-tickets-were-found-for-x",
				portalException.getMessage());
		}
	}

	@Test
	public void testResolveTicketsThrowsWhenNoRelatedLPDIsFound()
		throws Exception {

		_jiraUtilMockedStatic.when(
			() -> JiraUtil.getIssueJSONObject(
				Mockito.any(PatcherConfiguration.class), Mockito.eq("LPE-404"))
		).thenReturn(
			_createIssueJSONObject(JSONFactoryUtil.createJSONArray())
		);

		try {
			JiraTicketResolverUtil.resolveTickets(
				_patcherConfiguration, "LPE-404");

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertEquals(
				"no-related-lpd-ticket-were-found-for-x",
				portalException.getMessage());
		}
	}

	@Test
	public void testResolveTicketsTrimsAndSkipsBlanks() throws Exception {
		List<String> resolvedTickets = JiraTicketResolverUtil.resolveTickets(
			_patcherConfiguration, " LPD-1 , , LPD-2 ,");

		Assert.assertEquals(
			resolvedTickets.toString(), 2, resolvedTickets.size());
		Assert.assertEquals("LPD-1", resolvedTickets.get(0));
		Assert.assertEquals("LPD-2", resolvedTickets.get(1));
	}

	private JSONObject _createIssueJSONObject(JSONArray issueLinksJSONArray) {
		return JSONUtil.put(
			"fields", JSONUtil.put("issuelinks", issueLinksJSONArray));
	}

	private JSONObject _createIssueLinkJSONObject(
		String typeName, String inwardKey, String outwardKey) {

		JSONObject linkJSONObject = JSONUtil.put(
			"type", JSONUtil.put("name", typeName));

		if (inwardKey != null) {
			linkJSONObject.put(
				"inwardIssue", _createLinkedIssueJSONObject(inwardKey));
		}

		if (outwardKey != null) {
			linkJSONObject.put(
				"outwardIssue", _createLinkedIssueJSONObject(outwardKey));
		}

		return linkJSONObject;
	}

	private JSONArray _createIssueLinksJSONArray(
		JSONObject... issueLinkJSONObjects) {

		JSONArray issueLinksJSONArray = JSONFactoryUtil.createJSONArray();

		for (JSONObject issueLinkJSONObject : issueLinkJSONObjects) {
			issueLinksJSONArray.put(issueLinkJSONObject);
		}

		return issueLinksJSONArray;
	}

	private JSONObject _createLinkedIssueJSONObject(String key) {
		return JSONUtil.put("key", key);
	}

	private final MockedStatic<JiraUtil> _jiraUtilMockedStatic =
		Mockito.mockStatic(JiraUtil.class);
	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final PatcherConfiguration _patcherConfiguration = Mockito.mock(
		PatcherConfiguration.class);

}