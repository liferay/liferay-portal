/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.media.query;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.finder.AMQuery;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.finder.AMImageQueryBuilder;
import com.liferay.adaptive.media.image.internal.configuration.AMImageAttributeMapping;
import com.liferay.adaptive.media.image.internal.finder.AMImageQueryBuilderImpl;
import com.liferay.adaptive.media.image.internal.processor.AMImage;
import com.liferay.adaptive.media.image.media.query.Condition;
import com.liferay.adaptive.media.image.media.query.MediaQuery;
import com.liferay.adaptive.media.image.processor.AMImageAttribute;
import com.liferay.adaptive.media.image.url.AMImageURLFactory;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.URI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
public class MediaQueryProviderImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws PortalException {
		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_fileEntry.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_fileEntry.getFileVersion()
		).thenReturn(
			_fileVersion
		);

		ReflectionTestUtil.setFieldValue(
			_mediaQueryProviderImpl, "_amImageConfigurationHelper",
			_amImageConfigurationHelper);
		ReflectionTestUtil.setFieldValue(
			_mediaQueryProviderImpl, "_amImageFinder", _amImageFinder);
		ReflectionTestUtil.setFieldValue(
			_mediaQueryProviderImpl, "_amImageURLFactory", _amImageURLFactory);
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();

			_serviceRegistration = null;
		}
	}

	@Test
	public void testCreatesAMediaQuery() throws Exception {
		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry("uuid", 800, 1989, "adaptiveURL"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 1, mediaQueries.size());

		MediaQuery mediaQuery = mediaQueries.get(0);

		Assert.assertEquals("adaptiveURL", mediaQuery.getSrc());

		List<Condition> conditions = mediaQuery.getConditions();

		Assert.assertEquals(conditions.toString(), 1, conditions.size());

		_assertCondition(conditions.get(0), "max-width", "1989px");
	}

	@Test
	public void testCreatesSeveralMediaQueries() throws Exception {
		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid1", 800, 1986, "adaptiveURL1"),
			_createAMImageConfigurationEntry(
				"uuid2", 800, 1989, "adaptiveURL2"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals("adaptiveURL1", mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "1986px");

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		Assert.assertEquals("adaptiveURL2", mediaQuery2.getSrc());

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1989px");
		_assertCondition(conditions2.get(1), "min-width", "1986px");
	}

	@Test
	public void testCreatesSeveralMediaQueriesSortedByWidth() throws Exception {
		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid2", 800, 1989, "adaptiveURL2"),
			_createAMImageConfigurationEntry(
				"uuid1", 800, 1986, "adaptiveURL1"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals("adaptiveURL1", mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "1986px");

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		Assert.assertEquals("adaptiveURL2", mediaQuery2.getSrc());

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1989px");
		_assertCondition(conditions2.get(1), "min-width", "1986px");
	}

	@Test
	public void testFiltersOutAdaptiveMediasWithNoWidth() throws Exception {
		_configureFileEntryPermission(true);

		int auto = 0;

		_addConfigs(
			_createAMImageConfigurationEntry(
				"normal", 2048, 1024, StringPool.BLANK),
			_createAMImageConfigurationEntry(
				"wauto", 900, auto, StringPool.BLANK));

		_addAdaptiveMedias(
			_fileEntry, _createAdaptiveMedia("normal", 1334, 750, "normalURL"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 1, mediaQueries.size());

		_assertMediaQuery(mediaQueries.get(0), "normalURL", 750);
	}

	@Test
	public void testHDMediaQueriesApplies() throws Exception {
		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid1", 450, 800, "http://small.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid2", 900, 1600, "http://small.hd.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid3", 1900, 2500, "http://big.adaptive.com"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 3, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals(
			"http://small.adaptive.com, http://small.hd.adaptive.com 2x",
			mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "800px");

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		Assert.assertEquals(
			"http://small.hd.adaptive.com", mediaQuery2.getSrc());

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1600px");
		_assertCondition(conditions2.get(1), "min-width", "800px");

		MediaQuery mediaQuery3 = mediaQueries.get(2);

		Assert.assertEquals("http://big.adaptive.com", mediaQuery3.getSrc());

		List<Condition> conditions3 = mediaQuery3.getConditions();

		Assert.assertEquals(conditions3.toString(), 2, conditions3.size());

		_assertCondition(conditions3.get(0), "max-width", "2500px");
		_assertCondition(conditions3.get(1), "min-width", "1600px");
	}

	@Test
	public void testHDMediaQueryAppliesWhenHeightHas1PXLessThanExpected()
		throws Exception {

		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid1", 450, 800, "http://small.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid2", 899, 1600, "http://small.hd.adaptive.com"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals(
			"http://small.adaptive.com, http://small.hd.adaptive.com 2x",
			mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "800px");

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		Assert.assertEquals(
			"http://small.hd.adaptive.com", mediaQuery2.getSrc());

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1600px");
		_assertCondition(conditions2.get(1), "min-width", "800px");
	}

	@Test
	public void testHDMediaQueryAppliesWhenHeightHas1PXMoreThanExpected()
		throws Exception {

		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid1", 450, 800, "http://small.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid2", 901, 1600, "http://small.hd.adaptive.com"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals(
			"http://small.adaptive.com, http://small.hd.adaptive.com 2x",
			mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "800px");

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		Assert.assertEquals(
			"http://small.hd.adaptive.com", mediaQuery2.getSrc());

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1600px");
		_assertCondition(conditions2.get(1), "min-width", "800px");
	}

	@Test
	public void testHDMediaQueryAppliesWhenWidthHas1PXLessThanExpected()
		throws Exception {

		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid1", 450, 800, "http://small.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid2", 900, 1599, "http://small.hd.adaptive.com"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals(
			"http://small.adaptive.com, http://small.hd.adaptive.com 2x",
			mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "800px");

		Condition condition = conditions1.get(0);

		Assert.assertEquals("max-width", condition.getAttribute());
		Assert.assertEquals("800px", condition.getValue());

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(
			"http://small.hd.adaptive.com", mediaQuery2.getSrc());

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1599px");
		_assertCondition(conditions2.get(1), "min-width", "800px");
	}

	@Test
	public void testHDMediaQueryAppliesWhenWidthHas1PXMoreThanExpected()
		throws Exception {

		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid", 450, 800, "http://small.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid", 900, 1601, "http://small.hd.adaptive.com"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals(
			"http://small.adaptive.com, http://small.hd.adaptive.com 2x",
			mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "800px");

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		Assert.assertEquals(
			"http://small.hd.adaptive.com", mediaQuery2.getSrc());

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1601px");
		_assertCondition(conditions2.get(1), "min-width", "800px");
	}

	@Test
	public void testHDMediaQueryNotAppliesWhenHeightHas2PXLessThanExpected()
		throws Exception {

		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid", 450, 800, "http://small.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid", 898, 1600, "http://small.hd.adaptive.com"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals("http://small.adaptive.com", mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "800px");

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		Assert.assertEquals(
			"http://small.hd.adaptive.com", mediaQuery2.getSrc());

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1600px");
		_assertCondition(conditions2.get(1), "min-width", "800px");
	}

	@Test
	public void testHDMediaQueryNotAppliesWhenHeightHas2PXMoreThanExpected()
		throws Exception {

		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid", 450, 800, "http://small.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid", 902, 1600, "http://small.hd.adaptive.com"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals("http://small.adaptive.com", mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "800px");

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		Assert.assertEquals(
			"http://small.hd.adaptive.com", mediaQuery2.getSrc());

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1600px");
		_assertCondition(conditions2.get(1), "min-width", "800px");
	}

	@Test
	public void testHDMediaQueryNotAppliesWhenWidthHas2PXLessThanExpected()
		throws Exception {

		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid", 450, 800, "http://small.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid", 900, 1598, "http://small.hd.adaptive.com"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals("http://small.adaptive.com", mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "800px");

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		Assert.assertEquals(
			"http://small.hd.adaptive.com", mediaQuery2.getSrc());

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1598px");
		_assertCondition(conditions2.get(1), "min-width", "800px");
	}

	@Test
	public void testHDMediaQueryNotAppliesWhenWidthHas2PXMoreThanExpected()
		throws Exception {

		_configureFileEntryPermission(true);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid", 450, 800, "http://small.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid", 900, 1602, "http://small.hd.adaptive.com"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 2, mediaQueries.size());

		MediaQuery mediaQuery1 = mediaQueries.get(0);

		Assert.assertEquals("http://small.adaptive.com", mediaQuery1.getSrc());

		List<Condition> conditions1 = mediaQuery1.getConditions();

		Assert.assertEquals(conditions1.toString(), 1, conditions1.size());

		_assertCondition(conditions1.get(0), "max-width", "800px");

		MediaQuery mediaQuery2 = mediaQueries.get(1);

		Assert.assertEquals(
			"http://small.hd.adaptive.com", mediaQuery2.getSrc());

		List<Condition> conditions2 = mediaQuery2.getConditions();

		Assert.assertEquals(conditions2.toString(), 2, conditions2.size());

		_assertCondition(conditions2.get(0), "max-width", "1602px");
		_assertCondition(conditions2.get(1), "min-width", "800px");
	}

	@Test
	public void testReturnsNoMediaQueriesIfThereAreNoConfigs()
		throws Exception {

		_configureFileEntryPermission(true);

		_addConfigs();

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 0, mediaQueries.size());
	}

	@Test
	public void testReturnsNoMediaQueriesIfThereAreNoDownloadPermission()
		throws Exception {

		_configureFileEntryPermission(false);

		_addConfigs(
			_createAMImageConfigurationEntry(
				"uuid", 450, 800, "http://small.adaptive.com"),
			_createAMImageConfigurationEntry(
				"uuid", 900, 1601, "http://small.hd.adaptive.com"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 0, mediaQueries.size());
	}

	@Test
	public void testUsesTheValuesFromConfigIfNoAdaptiveMediasArePresent()
		throws Exception {

		_configureFileEntryPermission(true);

		int auto = 0;

		_addConfigs(
			_createAMImageConfigurationEntry("hauto", auto, 600, "hautoURL"),
			_createAMImageConfigurationEntry("low", 300, 300, "lowURL"),
			_createAMImageConfigurationEntry("normal", 2048, 1024, "normalURL"),
			_createAMImageConfigurationEntry("wauto", 900, auto, "wautoURL"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 3, mediaQueries.size());

		_assertMediaQuery(mediaQueries.get(0), "lowURL", 300);
		_assertMediaQuery(mediaQueries.get(1), "hautoURL", 300, 600);
		_assertMediaQuery(mediaQueries.get(2), "normalURL", 600, 1024);
	}

	@Test
	public void testUsesTheValuesFromTheAdaptiveMediasIfPresent()
		throws Exception {

		_configureFileEntryPermission(true);

		int auto = 0;

		_addConfigs(
			_createAMImageConfigurationEntry(
				"hauto", auto, 600, StringPool.BLANK),
			_createAMImageConfigurationEntry("low", 300, 300, StringPool.BLANK),
			_createAMImageConfigurationEntry(
				"normal", 2048, 1024, StringPool.BLANK),
			_createAMImageConfigurationEntry(
				"wauto", 900, auto, StringPool.BLANK));

		_addAdaptiveMedias(
			_fileEntry, _createAdaptiveMedia("low", 300, 169, "lowURL"),
			_createAdaptiveMedia("wauto", 900, 506, "wautoURL"),
			_createAdaptiveMedia("hauto", 1067, 600, "hautoURL"),
			_createAdaptiveMedia("normal", 1334, 750, "normalURL"));

		List<MediaQuery> mediaQueries = _mediaQueryProviderImpl.getMediaQueries(
			_fileEntry);

		Assert.assertEquals(mediaQueries.toString(), 4, mediaQueries.size());

		_assertMediaQuery(mediaQueries.get(0), "lowURL", 169);
		_assertMediaQuery(mediaQueries.get(1), "wautoURL", 169, 506);
		_assertMediaQuery(mediaQueries.get(2), "hautoURL", 506, 600);
		_assertMediaQuery(mediaQueries.get(3), "normalURL", 600, 750);
	}

	public class MockModelResourcePermission
		implements ModelResourcePermission<FileEntry> {

		public MockModelResourcePermission(boolean downloadPermission) {
			_downloadPermission = downloadPermission;
		}

		@Override
		public void check(
				PermissionChecker permissionChecker, FileEntry fileEntry,
				String actionId)
			throws PortalException {

			if (!_downloadPermission) {
				throw new PrincipalException.MustHavePermission(0L, actionId);
			}
		}

		@Override
		public void check(
				PermissionChecker permissionChecker, long primaryKey,
				String actionId)
			throws PortalException {

			if (!_downloadPermission) {
				throw new PrincipalException.MustHavePermission(0L, actionId);
			}
		}

		@Override
		public boolean contains(
				PermissionChecker permissionChecker, FileEntry fileEntry,
				String actionId)
			throws PortalException {

			return false;
		}

		@Override
		public boolean contains(
				PermissionChecker permissionChecker, long primaryKey,
				String actionId)
			throws PortalException {

			return false;
		}

		@Override
		public String getModelName() {
			return null;
		}

		@Override
		public PortletResourcePermission getPortletResourcePermission() {
			return null;
		}

		private final boolean _downloadPermission;

	}

	private void _addAdaptiveMedias(
			FileEntry fileEntry,
			AdaptiveMedia<AMProcessor<FileVersion>>... adaptiveMedias)
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> {
				Function<AMImageQueryBuilder, AMQuery<?, ?>>
					amImageQueryBuilderFunction = invocation.getArgument(
						0, Function.class);

				AMImageQueryBuilderImpl amImageQueryBuilderImpl =
					new AMImageQueryBuilderImpl();

				AMQuery<?, ?> amQuery = amImageQueryBuilderFunction.apply(
					amImageQueryBuilderImpl);

				if (!AMImageQueryBuilderImpl.AM_QUERY.equals(amQuery)) {
					return Collections.emptyList();
				}

				for (AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia :
						adaptiveMedias) {

					String configurationUuid = adaptiveMedia.getValue(
						AMAttribute.getConfigurationUuidAMAttribute());

					if (Objects.equals(
							fileEntry.getFileVersion(),
							amImageQueryBuilderImpl.getFileVersion()) &&
						configurationUuid.equals(
							amImageQueryBuilderImpl.getConfigurationUuid())) {

						return Collections.singletonList(adaptiveMedia);
					}
				}

				return Collections.emptyList();
			}
		);
	}

	private void _addConfigs(
			AMImageConfigurationEntry... amImageConfigurationEntries)
		throws Exception {

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				_COMPANY_ID)
		).thenReturn(
			Arrays.asList(amImageConfigurationEntries)
		);
	}

	private void _assertCondition(
		Condition condition, String attribute, String value) {

		Assert.assertEquals(attribute, condition.getAttribute());
		Assert.assertEquals(value, condition.getValue());
	}

	private void _assertMediaQuery(
		MediaQuery mediaQuery, String url, int maxWidth) {

		Assert.assertEquals(url, mediaQuery.getSrc());

		List<Condition> conditions = mediaQuery.getConditions();

		Assert.assertEquals(conditions.toString(), 1, conditions.size());

		_assertCondition(conditions.get(0), "max-width", maxWidth + "px");
	}

	private void _assertMediaQuery(
		MediaQuery mediaQuery, String url, int minWidth, int maxWidth) {

		Assert.assertEquals(url, mediaQuery.getSrc());

		List<Condition> conditions = mediaQuery.getConditions();

		Assert.assertEquals(conditions.toString(), 2, conditions.size());

		_assertCondition(conditions.get(0), "max-width", maxWidth + "px");
		_assertCondition(conditions.get(1), "min-width", minWidth + "px");
	}

	private void _configureFileEntryPermission(boolean downloadPermission) {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			ModelResourcePermission.class,
			new MockModelResourcePermission(downloadPermission),
			MapUtil.singletonDictionary(
				"model.class.name",
				"com.liferay.portal.kernel.repository.model.FileEntry"));
	}

	private AdaptiveMedia<AMProcessor<FileVersion>> _createAdaptiveMedia(
			String amImageConfigurationEntryUuid, int height, int width,
			String url)
		throws Exception {

		Map<String, String> properties = HashMapBuilder.put(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT.getName(),
			String.valueOf(height)
		).put(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH.getName(),
			String.valueOf(width)
		).put(
			() -> {
				AMAttribute<?, ?> amAttribute =
					AMAttribute.getConfigurationUuidAMAttribute();

				return amAttribute.getName();
			},
			amImageConfigurationEntryUuid
		).build();

		return new AMImage(
			() -> null, AMImageAttributeMapping.fromProperties(properties),
			URI.create(url));
	}

	private AMImageConfigurationEntry _createAMImageConfigurationEntry(
			final String uuid, final int height, final int width, String url)
		throws Exception {

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntry() {

				@Override
				public String getDescription() {
					return StringPool.BLANK;
				}

				@Override
				public String getName() {
					return uuid;
				}

				@Override
				public Map<String, String> getProperties() {
					return HashMapBuilder.put(
						"max-height", String.valueOf(height)
					).put(
						"max-width", String.valueOf(width)
					).build();
				}

				@Override
				public String getUUID() {
					return uuid;
				}

				@Override
				public boolean isEnabled() {
					return true;
				}

			};

		Mockito.when(
			_amImageURLFactory.createFileEntryURL(
				_fileEntry.getFileVersion(), amImageConfigurationEntry)
		).thenReturn(
			URI.create(url)
		);

		return amImageConfigurationEntry;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final AMImageConfigurationHelper _amImageConfigurationHelper =
		Mockito.mock(AMImageConfigurationHelper.class);
	private final AMImageFinder _amImageFinder = Mockito.mock(
		AMImageFinder.class);
	private final AMImageURLFactory _amImageURLFactory = Mockito.mock(
		AMImageURLFactory.class);
	private final FileEntry _fileEntry = Mockito.mock(FileEntry.class);
	private final FileVersion _fileVersion = Mockito.mock(FileVersion.class);
	private final MediaQueryProviderImpl _mediaQueryProviderImpl =
		new MediaQueryProviderImpl();
	private ServiceRegistration<?> _serviceRegistration;

}