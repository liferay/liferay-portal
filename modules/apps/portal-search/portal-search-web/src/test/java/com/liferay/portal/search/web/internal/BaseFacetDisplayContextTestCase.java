/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FacetDisplayContext;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Amanda Costa, Joshua Cords
 */
public abstract class BaseFacetDisplayContextTestCase {

	@BeforeClass
	public static void setUpClass() {
		configurationProviderUtilMockedStatic = Mockito.mockStatic(
			ConfigurationProviderUtil.class);
	}

	@AfterClass
	public static void tearDownClass() {
		configurationProviderUtilMockedStatic.close();
	}

	public FacetDisplayContext createFacetDisplayContext(String parameterValue)
		throws Exception {

		return null;
	}

	public FacetDisplayContext createFacetDisplayContext(
			String parameterValue, String order)
		throws Exception {

		return null;
	}

	public String getFacetDisplayContextParameterValue() {
		return StringPool.BLANK;
	}

	@Before
	public void setUp() throws Exception {
		Mockito.doReturn(
			facetCollector
		).when(
			facet
		).getFacetCollector();
	}

	@Test
	public void testEmptySearchResults() throws Exception {
		String parameterValue = getFacetDisplayContextParameterValue();

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			parameterValue);

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 0, bucketDisplayContexts.size());

		if (parameterValue == null) {
			parameterValue = StringPool.BLANK;
		}

		Assert.assertEquals(
			parameterValue, facetDisplayContext.getParameterValue());
		Assert.assertTrue(facetDisplayContext.isNothingSelected());
		Assert.assertTrue(facetDisplayContext.isRenderNothing());
	}

	@Test
	public void testEmptySearchResultsWithPreviousSelection() throws Exception {
		String term = createTerm();

		setUpAsset(term);

		String filterValue = getFilterValue(term);

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			filterValue);

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 1, bucketDisplayContexts.size());

		BucketDisplayContext bucketDisplayContext = bucketDisplayContexts.get(
			0);

		Assert.assertEquals(term, bucketDisplayContext.getBucketText());
		Assert.assertEquals(filterValue, bucketDisplayContext.getFilterValue());
		Assert.assertEquals(0, bucketDisplayContext.getFrequency());
		Assert.assertTrue(bucketDisplayContext.isSelected());
		Assert.assertTrue(bucketDisplayContext.isFrequencyVisible());

		Assert.assertEquals(
			filterValue, facetDisplayContext.getParameterValue());
		Assert.assertFalse(facetDisplayContext.isNothingSelected());
		Assert.assertFalse(facetDisplayContext.isRenderNothing());
	}

	@Test
	public void testGetDisplayStyleGroup() throws Exception {
		Group group1 = getGroup();

		setUpGroupLocalServiceUtil(group1);

		setUpPortletDisplayStyleGroupExternalReferenceCode(null);

		Group group2 = getGroup();

		_assertDisplayContext(group2);

		groupLocalServiceUtilMockedStatic.verifyNoInteractions();
	}

	@Test
	public void testGetDisplayStyleGroupWithConfiguration() throws Exception {
		Group group = getGroup();

		setUpGroupLocalServiceUtil(group);
		setUpPortletDisplayStyleGroupExternalReferenceCode(
			group.getExternalReferenceCode());

		_assertDisplayContext(group);

		groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), 0L),
			Mockito.times(1));
	}

	@Test
	public void testOneTerm() throws Exception {
		String term = createTerm();

		setUpAsset(term);

		String filterValue = getFilterValue(term);

		int frequency = RandomTestUtil.randomInt();

		setUpTermCollectors(
			facetCollector,
			Collections.singletonList(
				createTermCollector(filterValue, frequency)));

		String parameterValue = getFacetDisplayContextParameterValue();

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			parameterValue);

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 1, bucketDisplayContexts.size());

		BucketDisplayContext bucketDisplayContext = bucketDisplayContexts.get(
			0);

		Assert.assertEquals(term, bucketDisplayContext.getBucketText());
		Assert.assertEquals(filterValue, bucketDisplayContext.getFilterValue());
		Assert.assertEquals(frequency, bucketDisplayContext.getFrequency());
		Assert.assertTrue(bucketDisplayContext.isFrequencyVisible());
		Assert.assertFalse(bucketDisplayContext.isSelected());

		Assert.assertEquals(
			parameterValue, facetDisplayContext.getParameterValue());
		Assert.assertTrue(facetDisplayContext.isNothingSelected());
		Assert.assertFalse(facetDisplayContext.isRenderNothing());
	}

	@Test
	public void testOneTermWithPreviousSelection() throws Exception {
		String term = RandomTestUtil.randomString();

		setUpAsset(term);

		int frequency = RandomTestUtil.randomInt();

		String filterValue = getFilterValue(term);

		setUpTermCollectors(
			facetCollector,
			Collections.singletonList(
				createTermCollector(filterValue, frequency)));

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			filterValue);

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 1, bucketDisplayContexts.size());

		BucketDisplayContext bucketDisplayContext = bucketDisplayContexts.get(
			0);

		Assert.assertEquals(term, bucketDisplayContext.getBucketText());
		Assert.assertEquals(filterValue, bucketDisplayContext.getFilterValue());
		Assert.assertEquals(frequency, bucketDisplayContext.getFrequency());
		Assert.assertTrue(bucketDisplayContext.isSelected());
		Assert.assertTrue(bucketDisplayContext.isFrequencyVisible());

		Assert.assertEquals(
			filterValue, facetDisplayContext.getParameterValue());
		Assert.assertFalse(facetDisplayContext.isNothingSelected());
		Assert.assertFalse(facetDisplayContext.isRenderNothing());
	}

	@Test
	public void testOrderByTermFrequencyAscending() throws Exception {
		testOrderBy(
			expectedFrequenciesFrequencyAscending,
			expectedTermsFrequencyAscending, new int[] {6, 5, 5, 4},
			"count:asc", new String[] {"Burro", "Caballo", "Árbol", "Abeja"});
	}

	@Test
	public void testOrderByTermFrequencyDescending() throws Exception {
		testOrderBy(
			expectedFrequenciesFrequencyDescending,
			expectedTermsFrequencyDescending, new int[] {4, 5, 5, 6},
			"count:desc", new String[] {"Abeja", "Caballo", "Árbol", "Burro"});
	}

	@Test
	public void testOrderByTermValueAscending() throws Exception {
		testOrderBy(
			expectedFrequenciesValueAscending, expectedTermsValueAscending,
			new int[] {2, 3, 4, 5}, "key:asc",
			new String[] {"Árbol", "Abeja", "Árbol", "Burro"});
	}

	@Test
	public void testOrderByTermValueDescending() throws Exception {
		testOrderBy(
			expectedFrequenciesValueDescending, expectedTermsValueDescending,
			new int[] {2, 3, 4, 5}, "key:desc",
			new String[] {"Árbol", "Abeja", "Árbol", "Burro"});
	}

	protected static String buildExpectedNameFrequencyString(
		String[] expectedDisplayText, int[] expectedFrequencies) {

		StringBundler sb = new StringBundler(expectedDisplayText.length * 4);

		for (int i = 0; i < expectedDisplayText.length; i++) {
			sb.append(expectedDisplayText[i]);
			sb.append(StringPool.COLON);
			sb.append(expectedFrequencies[i]);
			sb.append(StringPool.PIPE);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	protected static TermCollector createTermCollector(
		String term, int frequency) {

		TermCollector termCollector = Mockito.mock(TermCollector.class);

		Mockito.when(
			termCollector.getFrequency()
		).thenReturn(
			frequency
		);

		Mockito.when(
			termCollector.getTerm()
		).thenReturn(
			term
		);

		return termCollector;
	}

	protected static HttpServletRequest getHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.doReturn(
			getThemeDisplay(null)
		).when(
			httpServletRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);

		return httpServletRequest;
	}

	protected static PortletDisplay getPortletDisplay() {
		PortletDisplay portletDisplay = Mockito.mock(PortletDisplay.class);

		Mockito.when(
			portletDisplay.getPortletResource()
		).thenReturn(
			"test"
		);

		return portletDisplay;
	}

	protected static RenderRequest getRenderRequest()
		throws ConfigurationException {

		return getRenderRequest(null);
	}

	protected static RenderRequest getRenderRequest(Group group)
		throws ConfigurationException {

		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.doReturn(
			getThemeDisplay(group)
		).when(
			renderRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);

		return renderRequest;
	}

	protected static List<TermCollector> getTermCollectors(
		String[] terms, int[] frequencies) {

		List<TermCollector> termCollectors = new ArrayList<>();

		for (int i = 0; i < terms.length; i++) {
			termCollectors.add(createTermCollector(terms[i], frequencies[i]));
		}

		return termCollectors;
	}

	protected static ThemeDisplay getThemeDisplay(Group group) {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.doReturn(
			LocaleUtil.getDefault()
		).when(
			themeDisplay
		).getLocale();

		Mockito.doReturn(
			getPortletDisplay()
		).when(
			themeDisplay
		).getPortletDisplay();

		if (group != null) {
			Mockito.doReturn(
				group
			).when(
				themeDisplay
			).getScopeGroup();
		}

		return themeDisplay;
	}

	protected static void setUpTermCollectors(
		FacetCollector facetCollector, List<TermCollector> termCollectors) {

		for (TermCollector termCollector : termCollectors) {
			Mockito.when(
				facetCollector.getTermCollector(termCollector.getTerm())
			).thenReturn(
				termCollector
			);
		}

		Mockito.doReturn(
			termCollectors
		).when(
			facetCollector
		).getTermCollectors();
	}

	protected void assertFacetOrder(
		List<BucketDisplayContext> bucketDisplayContexts,
		String[] expectedDisplayText, int[] expectedFrequencies) {

		String expectedResult = buildExpectedNameFrequencyString(
			expectedDisplayText, expectedFrequencies);

		String actualNameFrequencyString = buildNameFrequencyString(
			bucketDisplayContexts);

		Assert.assertEquals(expectedResult, actualNameFrequencyString);
	}

	protected String buildNameFrequencyString(
		List<BucketDisplayContext> bucketDisplayContexts) {

		StringBundler sb = new StringBundler(bucketDisplayContexts.size() * 4);

		for (BucketDisplayContext bucketDisplayContext :
				bucketDisplayContexts) {

			sb.append(bucketDisplayContext.getBucketText());
			sb.append(StringPool.COLON);
			sb.append(bucketDisplayContext.getFrequency());
			sb.append(StringPool.PIPE);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	protected String createTerm() {
		return RandomTestUtil.randomString();
	}

	protected abstract FacetDisplayContext getFacetDisplayContext(Group group)
		throws Exception;

	protected String getFilterValue(String term) {
		return term;
	}

	protected Group getGroup() {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		return group;
	}

	protected void setUpAsset(String term) throws Exception {
	}

	protected void setUpGroupLocalServiceUtil(Group group) throws Exception {
		groupLocalServiceUtilMockedStatic.reset();

		Mockito.when(
			GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), 0L)
		).thenReturn(
			group
		);
	}

	protected abstract void setUpPortletDisplayStyleGroupExternalReferenceCode(
		String externalReferenceCode);

	protected void testOrderBy(
			int[] expectedFrequencies, String[] expectedTerms,
			int[] frequencies, String order, String[] terms)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	protected static MockedStatic<ConfigurationProviderUtil>
		configurationProviderUtilMockedStatic;
	protected static final MockedStatic<GroupLocalServiceUtil>
		groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

	protected int[] expectedFrequenciesFrequencyAscending = {4, 5, 5, 6};
	protected int[] expectedFrequenciesFrequencyDescending = {6, 5, 5, 4};
	protected int[] expectedFrequenciesValueAscending = {3, 4, 2, 5};
	protected int[] expectedFrequenciesValueDescending = {5, 4, 2, 3};
	protected String[] expectedTermsFrequencyAscending = {
		"Abeja", "Árbol", "Caballo", "Burro"
	};
	protected String[] expectedTermsFrequencyDescending = {
		"Burro", "Árbol", "Caballo", "Abeja"
	};
	protected String[] expectedTermsValueAscending = {
		"Abeja", "Árbol", "Árbol", "Burro"
	};
	protected String[] expectedTermsValueDescending = {
		"Burro", "Árbol", "Árbol", "Abeja"
	};
	protected final Facet facet = Mockito.mock(Facet.class);
	protected final FacetCollector facetCollector = Mockito.mock(
		FacetCollector.class);

	private void _assertDisplayContext(Group group) throws Exception {
		FacetDisplayContext facetDisplayContext = getFacetDisplayContext(group);

		Assert.assertEquals(
			group.getGroupId(), facetDisplayContext.getDisplayStyleGroupId());
	}

}