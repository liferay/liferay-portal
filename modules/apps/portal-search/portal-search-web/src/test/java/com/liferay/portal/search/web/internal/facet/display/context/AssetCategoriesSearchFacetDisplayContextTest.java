/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.BaseFacetDisplayContextTestCase;
import com.liferay.portal.search.web.internal.category.facet.configuration.CategoryFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.facet.display.context.builder.AssetCategoriesSearchFacetDisplayContextBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.builder.AssetCategoryPermissionChecker;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class AssetCategoriesSearchFacetDisplayContextTest
	extends BaseFacetDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Override
	public FacetDisplayContext createFacetDisplayContext(String parameterValue)
		throws Exception {

		return createFacetDisplayContext(parameterValue, "count:desc");
	}

	@Override
	public FacetDisplayContext createFacetDisplayContext(
			String parameterValue, String order)
		throws ConfigurationException {

		AssetCategoriesSearchFacetDisplayContextBuilder
			assetCategoriesSearchFacetDisplayContextBuilder =
				new AssetCategoriesSearchFacetDisplayContextBuilder(
					Mockito.mock(GroupLocalService.class),
					Mockito.mock(RenderRequest.class));

		assetCategoriesSearchFacetDisplayContextBuilder.
			setAssetCategoryLocalService(_assetCategoryLocalService);
		assetCategoriesSearchFacetDisplayContextBuilder.
			setAssetCategoryPermissionChecker(_assetCategoryPermissionChecker);
		assetCategoriesSearchFacetDisplayContextBuilder.
			setAssetVocabularyLocalService(_assetVocabularyLocalService);
		assetCategoriesSearchFacetDisplayContextBuilder.setDisplayStyle(
			"cloud");
		assetCategoriesSearchFacetDisplayContextBuilder.setFacet(facet);
		assetCategoriesSearchFacetDisplayContextBuilder.setFrequenciesVisible(
			true);
		assetCategoriesSearchFacetDisplayContextBuilder.setFrequencyThreshold(
			0);
		assetCategoriesSearchFacetDisplayContextBuilder.setLocale(
			LocaleUtil.getDefault());
		assetCategoriesSearchFacetDisplayContextBuilder.setMaxTerms(0);
		assetCategoriesSearchFacetDisplayContextBuilder.setOrder(order);
		assetCategoriesSearchFacetDisplayContextBuilder.setParameterName(
			facet.getFieldId());
		assetCategoriesSearchFacetDisplayContextBuilder.setParameterValue(
			parameterValue);
		assetCategoriesSearchFacetDisplayContextBuilder.setPortal(
			_getPortal(null));

		if (_excludedGroupId > 0) {
			assetCategoriesSearchFacetDisplayContextBuilder.setExcludedGroupId(
				_excludedGroupId);
		}

		return assetCategoriesSearchFacetDisplayContextBuilder.build();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		setUpAssetVocabularyLocalService();
		setUpFacet();
	}

	@Test
	public void testExcludedGroup() throws Exception {
		long assetCategoryId = RandomTestUtil.randomLong();

		long groupId = RandomTestUtil.randomLong();

		long stagingGroupId = RandomTestUtil.randomLong();

		createGroup(groupId, stagingGroupId);

		_setUpAssetCategory(assetCategoryId, stagingGroupId);

		_excludedGroupId = stagingGroupId;

		int frequency = RandomTestUtil.randomInt();

		setUpOneTermCollector(assetCategoryId, frequency);

		String facetParam = StringPool.BLANK;

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			facetParam);

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 0, bucketDisplayContexts.size());

		_excludedGroupId = 0;
	}

	@Ignore
	@Override
	@Test
	public void testGetDisplayStyleGroup() {
	}

	@Ignore
	@Override
	@Test
	public void testGetDisplayStyleGroupWithConfiguration() {
	}

	@Test
	public void testGetGroupVocabularyExternalReferenceCodes()
		throws Exception {

		long assetCategoryId = RandomTestUtil.randomLong();
		long groupId = RandomTestUtil.randomLong();

		AssetCategory assetCategory1 = _createAssetCategory(
			assetCategoryId, groupId);

		long assetVocabularyId = RandomTestUtil.randomLong();

		Mockito.when(
			assetCategory1.getVocabularyId()
		).thenReturn(
			assetVocabularyId
		);

		AssetVocabulary assetVocabulary = Mockito.mock(AssetVocabulary.class);

		String assetVocabularyExternalReferenceCode =
			RandomTestUtil.randomString();

		Mockito.when(
			assetVocabulary.getExternalReferenceCode()
		).thenReturn(
			assetVocabularyExternalReferenceCode
		);

		Mockito.when(
			assetVocabulary.getGroupId()
		).thenReturn(
			groupId
		);

		String title = RandomTestUtil.randomString();

		Mockito.when(
			assetVocabulary.getTitle(Mockito.any(Locale.class))
		).thenReturn(
			title
		);

		Mockito.when(
			assetVocabulary.getVocabularyId()
		).thenReturn(
			assetVocabularyId
		);

		AssetVocabularyLocalService assetVocabularyLocalService = Mockito.mock(
			AssetVocabularyLocalService.class);

		Mockito.when(
			assetVocabularyLocalService.fetchAssetVocabulary(assetVocabularyId)
		).thenReturn(
			assetVocabulary
		);

		Group group = Mockito.mock(Group.class);

		String groupExternalReferenceCode = RandomTestUtil.randomString();

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			groupExternalReferenceCode
		);

		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		Mockito.when(
			groupLocalService.fetchGroup(groupId)
		).thenReturn(
			group
		);

		FacetCollector facetCollector = Mockito.mock(FacetCollector.class);

		TermCollector termCollector = Mockito.mock(TermCollector.class);

		Mockito.when(
			termCollector.getFrequency()
		).thenReturn(
			1
		);

		Mockito.when(
			termCollector.getTerm()
		).thenReturn(
			String.valueOf(assetCategoryId)
		);

		Mockito.when(
			facetCollector.getTermCollectors()
		).thenReturn(
			ListUtil.fromArray(termCollector)
		);

		Mockito.when(
			facet.getFacetCollector()
		).thenReturn(
			facetCollector
		);

		Mockito.when(
			facet.getFieldName()
		).thenReturn(
			"assetCategoryIds"
		);

		AssetCategoriesSearchFacetDisplayContextBuilder
			assetCategoriesSearchFacetDisplayContextBuilder =
				new AssetCategoriesSearchFacetDisplayContextBuilder(
					groupLocalService, Mockito.mock(RenderRequest.class));

		assetCategoriesSearchFacetDisplayContextBuilder.
			setAssetCategoryLocalService(_assetCategoryLocalService);
		assetCategoriesSearchFacetDisplayContextBuilder.
			setAssetVocabularyLocalService(assetVocabularyLocalService);
		assetCategoriesSearchFacetDisplayContextBuilder.
			setAssetCategoryPermissionChecker(assetCategory2 -> true);
		assetCategoriesSearchFacetDisplayContextBuilder.setDisplayStyle(
			"cloud");
		assetCategoriesSearchFacetDisplayContextBuilder.setFacet(facet);
		assetCategoriesSearchFacetDisplayContextBuilder.setFrequenciesVisible(
			true);
		assetCategoriesSearchFacetDisplayContextBuilder.setLocale(
			LocaleUtil.US);
		assetCategoriesSearchFacetDisplayContextBuilder.setMaxTerms(10);
		assetCategoriesSearchFacetDisplayContextBuilder.setParameterName(
			"category");
		assetCategoriesSearchFacetDisplayContextBuilder.setPortal(
			_getPortal(null));

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext =
				assetCategoriesSearchFacetDisplayContextBuilder.build();

		Assert.assertEquals(
			ListUtil.fromArray(
				groupExternalReferenceCode + "&&" +
					assetVocabularyExternalReferenceCode),
			assetCategoriesSearchFacetDisplayContext.
				getGroupVocabularyExternalReferenceCodes());
		Assert.assertEquals(
			ListUtil.fromArray(assetVocabularyId),
			assetCategoriesSearchFacetDisplayContext.getVocabularyIds());
		Assert.assertEquals(
			ListUtil.fromArray(title),
			assetCategoriesSearchFacetDisplayContext.getVocabularyNames());
	}

	@Override
	@Test
	public void testOneTerm() throws Exception {
		long assetCategoryId = RandomTestUtil.randomLong();

		_setUpAssetCategory(assetCategoryId, 0);

		int frequency = RandomTestUtil.randomInt();

		setUpOneTermCollector(assetCategoryId, frequency);

		String facetParam = StringPool.BLANK;

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			facetParam);

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 1, bucketDisplayContexts.size());

		BucketDisplayContext bucketDisplayContext = bucketDisplayContexts.get(
			0);

		Assert.assertEquals(
			String.valueOf(assetCategoryId),
			bucketDisplayContext.getBucketText());
		Assert.assertEquals(
			String.valueOf(assetCategoryId),
			bucketDisplayContext.getFilterValue());
		Assert.assertEquals(frequency, bucketDisplayContext.getFrequency());
		Assert.assertTrue(bucketDisplayContext.isFrequencyVisible());
		Assert.assertFalse(bucketDisplayContext.isSelected());

		Assert.assertEquals(
			facetParam, facetDisplayContext.getParameterValue());
		Assert.assertTrue(facetDisplayContext.isNothingSelected());
		Assert.assertFalse(facetDisplayContext.isRenderNothing());
	}

	@Override
	@Test
	public void testOneTermWithPreviousSelection() throws Exception {
		long assetCategoryId = RandomTestUtil.randomLong();

		_setUpAssetCategory(assetCategoryId, 0);

		int frequency = RandomTestUtil.randomInt();

		setUpOneTermCollector(assetCategoryId, frequency);

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			String.valueOf(assetCategoryId));

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 1, bucketDisplayContexts.size());

		BucketDisplayContext bucketDisplayContext = bucketDisplayContexts.get(
			0);

		Assert.assertEquals(
			String.valueOf(assetCategoryId),
			bucketDisplayContext.getBucketText());
		Assert.assertEquals(
			String.valueOf(assetCategoryId),
			bucketDisplayContext.getFilterValue());
		Assert.assertEquals(frequency, bucketDisplayContext.getFrequency());
		Assert.assertTrue(bucketDisplayContext.isFrequencyVisible());
		Assert.assertTrue(bucketDisplayContext.isSelected());

		Assert.assertEquals(
			assetCategoryId,
			GetterUtil.getLong(facetDisplayContext.getParameterValue()));
		Assert.assertFalse(facetDisplayContext.isNothingSelected());
		Assert.assertFalse(facetDisplayContext.isRenderNothing());
	}

	@Override
	@Test
	public void testOrderByTermFrequencyAscending() throws Exception {
		long[] assetCategoryIds = {3L, 4L, 2L, 1L};

		List<TermCollector> termCollectors = _getTermCollectors(
			assetCategoryIds, new int[] {6, 5, 5, 4});

		String[] expectedCategoryIds = {"1", "2", "4", "3"};
		int[] expectedFrequencies = {4, 5, 5, 6};

		_testOrderBy(
			assetCategoryIds, termCollectors, "count:asc", expectedCategoryIds,
			expectedFrequencies);
	}

	@Override
	@Test
	public void testOrderByTermFrequencyDescending() throws Exception {
		long[] assetCategoryIds = {3L, 4L, 2L, 1L};

		List<TermCollector> termCollectors = _getTermCollectors(
			assetCategoryIds, new int[] {6, 5, 5, 4});

		String[] expectedCategoryIds = {"3", "2", "4", "1"};
		int[] expectedFrequencies = {6, 5, 5, 4};

		_testOrderBy(
			assetCategoryIds, termCollectors, "count:desc", expectedCategoryIds,
			expectedFrequencies);
	}

	@Override
	@Test
	public void testOrderByTermValueAscending() throws Exception {
		long[] assetCategoryIds = {2L, 1L, 2L, 3L};

		List<TermCollector> termCollectors = _getTermCollectors(
			assetCategoryIds);

		String[] expectedCategoryIds = {"1", "2", "2", "3"};
		int[] expectedFrequencies = {2, 3, 1, 4};

		_testOrderBy(
			assetCategoryIds, termCollectors, "key:asc", expectedCategoryIds,
			expectedFrequencies);
	}

	@Override
	@Test
	public void testOrderByTermValueDescending() throws Exception {
		long[] assetCategoryIds = {2L, 1L, 2L, 3L};

		List<TermCollector> termCollectors = _getTermCollectors(
			assetCategoryIds);

		String[] expectedCategoryIds = {"3", "2", "2", "1"};
		int[] expectedFrequencies = {4, 3, 1, 2};

		_testOrderBy(
			assetCategoryIds, termCollectors, "key:desc", expectedCategoryIds,
			expectedFrequencies);
	}

	@Test
	public void testSelectionOfNonexistentTerms() throws Exception {
		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			RandomTestUtil.randomString());

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 0, bucketDisplayContexts.size());

		Assert.assertEquals("0", facetDisplayContext.getParameterValue());
		Assert.assertFalse(facetDisplayContext.isNothingSelected());
		Assert.assertFalse(facetDisplayContext.isRenderNothing());
	}

	@Test
	public void testUnauthorized() throws Exception {
		long assetCategoryId = RandomTestUtil.randomLong();

		_setUpAssetCategoryUnauthorized(assetCategoryId);

		int frequency = RandomTestUtil.randomInt();

		setUpOneTermCollector(assetCategoryId, frequency);

		String facetParam = StringPool.BLANK;

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			facetParam);

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 0, bucketDisplayContexts.size());

		Assert.assertEquals(
			facetParam, facetDisplayContext.getParameterValue());
		Assert.assertTrue(facetDisplayContext.isNothingSelected());
		Assert.assertTrue(facetDisplayContext.isRenderNothing());
	}

	@Test
	public void testUnauthorizedWithPreviousSelection() throws Exception {
		long assetCategoryId = RandomTestUtil.randomLong();

		_setUpAssetCategoryUnauthorized(assetCategoryId);

		String facetParam = String.valueOf(assetCategoryId);

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			facetParam);

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 0, bucketDisplayContexts.size());

		Assert.assertEquals(
			facetParam, facetDisplayContext.getParameterValue());
		Assert.assertFalse(facetDisplayContext.isNothingSelected());
		Assert.assertFalse(facetDisplayContext.isRenderNothing());
	}

	protected Group createGroup(long groupId, long stagingGroupId) {
		Group group = Mockito.mock(Group.class);

		Mockito.doReturn(
			groupId
		).when(
			group
		).getGroupId();

		return group;
	}

	@Override
	protected String createTerm() {
		return String.valueOf(RandomTestUtil.randomLong());
	}

	protected TermCollector createTermCollector(
		long assetCategoryId, int frequency) {

		TermCollector termCollector = Mockito.mock(TermCollector.class);

		Mockito.doReturn(
			frequency
		).when(
			termCollector
		).getFrequency();

		if (_isLegacyField()) {
			Mockito.doReturn(
				String.valueOf(assetCategoryId)
			).when(
				termCollector
			).getTerm();
		}
		else {
			Mockito.doReturn(
				"vocabularyId-" + assetCategoryId
			).when(
				termCollector
			).getTerm();
		}

		return termCollector;
	}

	@Override
	protected FacetDisplayContext getFacetDisplayContext(Group group)
		throws Exception {

		AssetCategoriesSearchFacetDisplayContextBuilder
			assetCategoriesSearchFacetDisplayContextBuilder =
				new AssetCategoriesSearchFacetDisplayContextBuilder(
					Mockito.mock(GroupLocalService.class),
					getRenderRequest(group));

		assetCategoriesSearchFacetDisplayContextBuilder.setPortal(
			_getPortal(group));

		return assetCategoriesSearchFacetDisplayContextBuilder.build();
	}

	protected String getFacetFieldName() {
		return "assetVocabularyCategoryIds";
	}

	@Override
	protected void setUpAsset(String assetCategoryId) throws Exception {
		_groupId = RandomTestUtil.randomLong();

		_setUpAssetCategory(GetterUtil.getLong(assetCategoryId), _groupId);
	}

	protected void setUpAssetVocabularyLocalService() {
		AssetVocabulary assetVocabulary = Mockito.mock(AssetVocabulary.class);

		Mockito.doReturn(
			"name"
		).when(
			assetVocabulary
		).getTitle(
			Mockito.any(Locale.class)
		);

		Mockito.doReturn(
			assetVocabulary
		).when(
			_assetVocabularyLocalService
		).fetchAssetVocabulary(
			Mockito.anyLong()
		);
	}

	protected void setUpFacet() throws Exception {
		super.setUp();

		Mockito.doReturn(
			getFacetFieldName()
		).when(
			facet
		).getFieldName();
	}

	protected void setUpOneTermCollector(long assetCategoryId, int frequency) {
		Mockito.doReturn(
			Collections.singletonList(
				createTermCollector(assetCategoryId, frequency))
		).when(
			facetCollector
		).getTermCollectors();
	}

	@Override
	protected void setUpPortletDisplayStyleGroupExternalReferenceCode(
		String externalReferenceCode) {

		CategoryFacetPortletInstanceConfiguration
			categoryFacetPortletInstanceConfiguration = Mockito.mock(
				CategoryFacetPortletInstanceConfiguration.class);

		Mockito.when(
			categoryFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			categoryFacetPortletInstanceConfiguration
		);
	}

	private AssetCategory _createAssetCategory(
		long assetCategoryId, long groupId) {

		AssetCategory assetCategory = Mockito.mock(AssetCategory.class);

		Mockito.doReturn(
			assetCategoryId
		).when(
			assetCategory
		).getCategoryId();

		Mockito.doReturn(
			groupId
		).when(
			assetCategory
		).getGroupId();

		Mockito.doReturn(
			String.valueOf(assetCategoryId)
		).when(
			assetCategory
		).getTitle(
			(Locale)Mockito.any()
		);

		Mockito.doReturn(
			assetCategory
		).when(
			_assetCategoryLocalService
		).fetchAssetCategory(
			assetCategoryId
		);

		return assetCategory;
	}

	private HttpServletRequest _getHttpServletRequest(Group group) {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.doReturn(
			getThemeDisplay(group)
		).when(
			httpServletRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);

		return httpServletRequest;
	}

	private Portal _getPortal(Group group) throws ConfigurationException {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.doReturn(
			_getHttpServletRequest(group)
		).when(
			portal
		).getHttpServletRequest(
			Mockito.any()
		);

		return portal;
	}

	private List<TermCollector> _getTermCollectors(long... assetCategoryIds) {
		int[] frequencies = new int[assetCategoryIds.length];

		for (int i = 0; i < assetCategoryIds.length; i++) {
			frequencies[i] = i + 1;
		}

		return _getTermCollectors(assetCategoryIds, frequencies);
	}

	private List<TermCollector> _getTermCollectors(
		long[] assetCategoryIds, int[] frequencies) {

		List<TermCollector> termCollectors = new ArrayList<>();

		for (int i = 0; i < assetCategoryIds.length; i++) {
			termCollectors.add(
				createTermCollector(assetCategoryIds[i], frequencies[i]));
		}

		return termCollectors;
	}

	private boolean _isLegacyField() {
		String fieldName = getFacetFieldName();

		return fieldName.equals("assetCategoryIds");
	}

	private void _setUpAssetCategory(long assetCategoryId, long groupId) {
		AssetCategory assetCategory = _createAssetCategory(
			assetCategoryId, groupId);

		Mockito.doReturn(
			true
		).when(
			_assetCategoryPermissionChecker
		).hasPermission(
			assetCategory
		);
	}

	private void _setUpAssetCategoryUnauthorized(long assetCategoryId) {
		AssetCategory assetCategory = _createAssetCategory(assetCategoryId, 0);

		Mockito.doReturn(
			false
		).when(
			_assetCategoryPermissionChecker
		).hasPermission(
			assetCategory
		);
	}

	private void _setUpMultipleAssetCategory(long[] assetCategoryId) {
		for (int i = 0; i < assetCategoryId.length; i++) {
			AssetCategory assetCategory = _createAssetCategory(
				assetCategoryId[i], i);

			Mockito.doReturn(
				true
			).when(
				_assetCategoryPermissionChecker
			).hasPermission(
				assetCategory
			);
		}
	}

	private void _testOrderBy(
			long[] assetCategoryIds, List<TermCollector> termCollectors,
			String order, String[] expectedTerms, int[] expectedFrequencies)
		throws Exception {

		_setUpMultipleAssetCategory(assetCategoryIds);

		setUpTermCollectors(facetCollector, termCollectors);

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			StringPool.BLANK, order);

		assertFacetOrder(
			facetDisplayContext.getBucketDisplayContexts(), expectedTerms,
			expectedFrequencies);
	}

	private final AssetCategoryLocalService _assetCategoryLocalService =
		Mockito.mock(AssetCategoryLocalService.class);
	private final AssetCategoryPermissionChecker
		_assetCategoryPermissionChecker = Mockito.mock(
			AssetCategoryPermissionChecker.class);
	private final AssetVocabularyLocalService _assetVocabularyLocalService =
		Mockito.mock(AssetVocabularyLocalService.class);
	private long _excludedGroupId;
	private long _groupId;

}