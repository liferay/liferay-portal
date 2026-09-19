/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.suggestions.display.context;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.internal.suggestions.display.context.builder.SuggestionsPortletDisplayContextBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.AdditionalAnswers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Adam Brandizzi
 * @author André de Oliveira
 */
public class SuggestionsPortletDisplayContextBuilderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpHtml();

		_setUpPortalUtil();

		_setUpDisplayContextBuilder();
	}

	@After
	public void tearDown() {
		_htmlUtilMockedStatic.close();
	}

	@Test
	public void testGetRelatedQueriesSuggestions() {
		List<SuggestionDisplayContext> suggestionDisplayContexts =
			buildRelatedQueriesSuggestions(Arrays.asList("alpha"));

		_assertSuggestion(
			"[<strong>alpha</strong>] | q=alpha",
			suggestionDisplayContexts.get(0));
	}

	@Test
	public void testGetRelatedQueriesSuggestionsEmptyByDefault() {
		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		List<SuggestionDisplayContext> suggestionDisplayContexts =
			suggestionsPortletDisplayContext.getRelatedQueriesSuggestions();

		Assert.assertTrue(suggestionDisplayContexts.isEmpty());
	}

	@Test
	public void testGetRelatedQueriesSuggestionsMultiple() {
		_setUpSearchedKeywords("q", "a b");

		List<SuggestionDisplayContext> suggestionDisplayContexts =
			buildRelatedQueriesSuggestions(Arrays.asList("a C", "a b C"));

		Assert.assertEquals(
			suggestionDisplayContexts.toString(), 2,
			suggestionDisplayContexts.size());

		_assertSuggestion(
			"a [<strong>C</strong>] | q=a+C", suggestionDisplayContexts.get(0));

		_assertSuggestion(
			"a b [<strong>C</strong>] | q=a+b+C",
			suggestionDisplayContexts.get(1));
	}

	@Test
	public void testGetRelatedSuggestionsWithEmptyList() {
		_setUpSearchedKeywords("q", "a b");

		List<SuggestionDisplayContext> suggestionDisplayContexts =
			buildRelatedQueriesSuggestions(Collections.emptyList());

		Assert.assertTrue(suggestionDisplayContexts.isEmpty());
	}

	@Test
	public void testGetRelatedSuggestionsWithKeywordsAsSuggestions() {
		_setUpSearchedKeywords("q", "a b");

		List<SuggestionDisplayContext> suggestionDisplayContexts =
			buildRelatedQueriesSuggestions(Arrays.asList("a b", "a b C"));

		Assert.assertEquals(
			suggestionDisplayContexts.toString(), 1,
			suggestionDisplayContexts.size());

		_assertSuggestion(
			"a b [<strong>C</strong>] | q=a+b+C",
			suggestionDisplayContexts.get(0));
	}

	@Test
	public void testGetRelatedSuggestionsWithNullSuggestions() {
		_setUpSearchedKeywords("q", "a b");

		List<SuggestionDisplayContext> suggestionDisplayContexts =
			buildRelatedQueriesSuggestions(Arrays.asList(null, "", "a b C"));

		Assert.assertEquals(
			suggestionDisplayContexts.toString(), 1,
			suggestionDisplayContexts.size());

		_assertSuggestion(
			"a b [<strong>C</strong>] | q=a+b+C",
			suggestionDisplayContexts.get(0));
	}

	@Test
	public void testGetSpellCheckSuggestion() {
		SuggestionDisplayContext suggestionDisplayContext =
			buildSpellCheckSuggestion("alpha");

		_assertSuggestion(
			"[<strong>alpha</strong>] | q=alpha", suggestionDisplayContext);
	}

	@Test
	public void testGetSpellCheckSuggestionEqualsToKeywords() {
		_setUpSearchedKeywords("q", "a b");

		Assert.assertNull(buildSpellCheckSuggestion("a b"));
	}

	@Test
	public void testGetSpellCheckSuggestionFormatted() {
		_setUpSearchedKeywords("q", "a b");

		SuggestionDisplayContext suggestionDisplayContext =
			buildSpellCheckSuggestion("a C");

		_assertSuggestion(
			"a [<strong>C</strong>] | q=a+C", suggestionDisplayContext);
	}

	@Test
	public void testGetSpellCheckSuggestionWithEmptySuggestion() {
		_setUpSearchedKeywords("q", "a b");

		Assert.assertNull(buildSpellCheckSuggestion(""));
	}

	@Test
	public void testGetSpellCheckSuggestionWithNullSuggestion() {
		_setUpSearchedKeywords("q", "a b");

		Assert.assertNull(buildSpellCheckSuggestion(null));
	}

	@Test
	public void testGetSpellCheckSuggestionsNullByDefault() {
		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertNull(
			suggestionsPortletDisplayContext.getSpellCheckSuggestion());
	}

	@Test
	public void testHasRelatedQueriesSuggestionsFalseByDefault() {
		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertFalse(
			suggestionsPortletDisplayContext.hasRelatedQueriesSuggestions());
	}

	@Test
	public void testHasRelatedSuggestionsFalseWithDisabledAndNonemptyList() {
		_displayContextBuilder.setRelatedQueriesSuggestions(
			Arrays.asList(RandomTestUtil.randomString()));
		_displayContextBuilder.setRelatedQueriesSuggestionsEnabled(false);

		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertFalse(
			suggestionsPortletDisplayContext.hasRelatedQueriesSuggestions());
	}

	@Test
	public void testHasRelatedSuggestionsFalseWithEnabledAndEmptyList() {
		_displayContextBuilder.setRelatedQueriesSuggestions(
			Collections.emptyList());
		_displayContextBuilder.setRelatedQueriesSuggestionsEnabled(true);

		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertFalse(
			suggestionsPortletDisplayContext.hasRelatedQueriesSuggestions());
	}

	@Test
	public void testHasRelatedSuggestionsTrueWithEnabledAndNonemptyList() {
		_displayContextBuilder.setRelatedQueriesSuggestions(
			Arrays.asList(RandomTestUtil.randomString()));
		_displayContextBuilder.setRelatedQueriesSuggestionsEnabled(true);

		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertTrue(
			suggestionsPortletDisplayContext.hasRelatedQueriesSuggestions());
	}

	@Test
	public void testHasSpellCheckSuggestionFalseByDefault() {
		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertFalse(
			suggestionsPortletDisplayContext.hasSpellCheckSuggestion());
	}

	@Test
	public void testHasSpellCheckSuggestionsFalseWithDisabledAndSuggestion() {
		_displayContextBuilder.setSpellCheckSuggestion(
			RandomTestUtil.randomString());
		_displayContextBuilder.setSpellCheckSuggestionEnabled(false);

		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertFalse(
			suggestionsPortletDisplayContext.hasSpellCheckSuggestion());
	}

	@Test
	public void testHasSpellCheckSuggestionsFalseWithEnabledAndNull() {
		_displayContextBuilder.setSpellCheckSuggestion(null);
		_displayContextBuilder.setSpellCheckSuggestionEnabled(true);

		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertFalse(
			suggestionsPortletDisplayContext.hasSpellCheckSuggestion());
	}

	@Test
	public void testHasSpellCheckSuggestionsTrueWithEnabledAndNonemptyList() {
		_displayContextBuilder.setSpellCheckSuggestion(
			RandomTestUtil.randomString());
		_displayContextBuilder.setSpellCheckSuggestionEnabled(true);

		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertTrue(
			suggestionsPortletDisplayContext.hasSpellCheckSuggestion());
	}

	@Test
	public void testIsRelatedSuggestions() {
		_displayContextBuilder.setRelatedQueriesSuggestions(
			Arrays.asList(RandomTestUtil.randomString()));
		_displayContextBuilder.setRelatedQueriesSuggestionsEnabled(true);

		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertTrue(
			suggestionsPortletDisplayContext.
				isRelatedQueriesSuggestionsEnabled());
	}

	@Test
	public void testIsRelatedSuggestionsFalseByDefault() {
		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertFalse(
			suggestionsPortletDisplayContext.
				isRelatedQueriesSuggestionsEnabled());
	}

	@Test
	public void testIsSpellCheckSuggestionEnabled() {
		_displayContextBuilder.setSpellCheckSuggestionEnabled(true);

		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertTrue(
			suggestionsPortletDisplayContext.isSpellCheckSuggestionEnabled());
	}

	@Test
	public void testIsSpellCheckSuggestionEnabledFalseByDefault() {
		SuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			_displayContextBuilder.build();

		Assert.assertFalse(
			suggestionsPortletDisplayContext.isSpellCheckSuggestionEnabled());
	}

	protected List<SuggestionDisplayContext> buildRelatedQueriesSuggestions(
		List<String> suggestions) {

		_displayContextBuilder.setRelatedQueriesSuggestions(suggestions);
		_displayContextBuilder.setRelatedQueriesSuggestionsEnabled(true);

		SuggestionsPortletDisplayContext displayContext =
			_displayContextBuilder.build();

		return displayContext.getRelatedQueriesSuggestions();
	}

	protected SuggestionDisplayContext buildSpellCheckSuggestion(
		String spellCheckSuggestion) {

		_displayContextBuilder.setSpellCheckSuggestion(spellCheckSuggestion);
		_displayContextBuilder.setSpellCheckSuggestionEnabled(true);

		SuggestionsPortletDisplayContext displayContext =
			_displayContextBuilder.build();

		return displayContext.getSpellCheckSuggestion();
	}

	protected Portal portal = Mockito.mock(Portal.class);

	private void _assertSuggestion(
		String expected, SuggestionDisplayContext suggestionDisplayContext) {

		String[] parts = StringUtil.split(expected, CharPool.PIPE);

		String suggestedKeywordsFormatted = _formatSimplifiedSuggestedKeywords(
			StringUtil.trim(parts[0]));

		String fullURL = _toFullURL(StringUtil.trim(parts[1]));

		Assert.assertEquals(
			suggestedKeywordsFormatted + " | " + fullURL,
			suggestionDisplayContext.getSuggestedKeywordsFormatted() + " | " +
				suggestionDisplayContext.getURL());
	}

	private String _formatKeyword(String keyword) {
		if (keyword.charAt(0) == CharPool.OPEN_BRACKET) {
			return "<span class=\"changed-keyword\">" +
				keyword.substring(1, keyword.length() - 1) + "</span>";
		}

		return "<span class=\"unchanged-keyword\">" + keyword + "</span>";
	}

	private String _formatSimplifiedSuggestedKeywords(String simplifiedQuery) {
		String[] simplifiedQueryParts = StringUtil.split(
			simplifiedQuery, CharPool.SPACE);

		StringBundler sb = new StringBundler(simplifiedQueryParts.length * 2);

		for (String simplifiedQueryPart : simplifiedQueryParts) {
			sb.append(_formatKeyword(simplifiedQueryPart));
			sb.append(CharPool.SPACE);
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private void _setUpDisplayContextBuilder() {
		_displayContextBuilder = new SuggestionsPortletDisplayContextBuilder();

		_setUpSearchedKeywords("q", "X");
	}

	private void _setUpHtml() {
		_htmlUtilMockedStatic = Mockito.mockStatic(HtmlUtil.class);

		_htmlUtilMockedStatic.when(
			() -> HtmlUtil.escape(Mockito.anyString())
		).thenAnswer(
			AdditionalAnswers.returnsFirstArg()
		);
	}

	private void _setUpPortalUtil() {
		Mockito.doAnswer(
			invocation -> new String[] {
				invocation.getArgument(0, String.class), StringPool.BLANK
			}
		).when(
			portal
		).stripURLAnchor(
			Mockito.anyString(), Mockito.anyString()
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portal);
	}

	private void _setUpSearchedKeywords(
		String keywordsParameterName, String keywords) {

		_displayContextBuilder.setKeywords(keywords);
		_displayContextBuilder.setKeywordsParameterName(keywordsParameterName);
		_displayContextBuilder.setSearchURL(
			StringBundler.concat(
				_URL_PREFIX, keywordsParameterName, StringPool.EQUAL,
				keywords));
	}

	private String _toFullURL(String parameters) {
		return _URL_PREFIX + parameters;
	}

	private static final String _URL_PREFIX = "http://localhost:8080/?";

	private SuggestionsPortletDisplayContextBuilder _displayContextBuilder;
	private MockedStatic<HtmlUtil> _htmlUtilMockedStatic;

}