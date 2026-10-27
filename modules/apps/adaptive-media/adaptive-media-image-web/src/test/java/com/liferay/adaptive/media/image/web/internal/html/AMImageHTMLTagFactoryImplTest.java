/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.web.internal.html;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.media.query.Condition;
import com.liferay.adaptive.media.image.media.query.MediaQuery;
import com.liferay.adaptive.media.image.media.query.MediaQueryProvider;
import com.liferay.adaptive.media.image.url.AMImageURLFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.URI;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alejandro Tardín
 */
public class AMImageHTMLTagFactoryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws PortalException {
		ReflectionTestUtil.setFieldValue(
			_amImageHTMLTagFactoryImpl, "_amImageConfigurationHelper",
			_amImageConfigurationHelper);
		ReflectionTestUtil.setFieldValue(
			_amImageHTMLTagFactoryImpl, "_amImageURLFactory",
			_amImageURLFactory);
		ReflectionTestUtil.setFieldValue(
			_amImageHTMLTagFactoryImpl, "_mediaQueryProvider",
			_mediaQueryProvider);

		Mockito.when(
			_fileEntry.getFileEntryId()
		).thenReturn(
			1234L
		);

		Mockito.when(
			_fileEntry.getFileVersion()
		).thenReturn(
			_fileVersion
		);
	}

	@Test
	public void testCreatesAPictureTag() throws Exception {
		_addMediaQueries(_createMediaQuery(1989, "adaptiveURL"));

		String originalImgTag =
			"<img src=\"originalURL\" data-fileentryid=\"1234\"/>";

		StringBundler expectedSB = new StringBundler(5);

		expectedSB.append("<picture data-fileentryid=\"1234\">");
		expectedSB.append("<source media=\"(max-width:1989px)\" ");
		expectedSB.append("srcset=\"adaptiveURL\" />");
		expectedSB.append(originalImgTag);
		expectedSB.append("</picture>");

		String pictureTag = _amImageHTMLTagFactoryImpl.create(
			originalImgTag, _fileEntry);

		Assert.assertEquals(expectedSB.toString(), pictureTag);
	}

	@Test
	public void testCreatesAPictureTagWithSeveralMediaQueries()
		throws Exception {

		_addMediaQueries(
			_createMediaQuery(1986, "adaptiveURL1"),
			_createMediaQuery(1989, "adaptiveURL2"));

		String originalImgTag =
			"<img src=\"originalURL\" data-fileentryid=\"1234\"/>";

		StringBundler expectedSB = new StringBundler(7);

		expectedSB.append("<picture data-fileentryid=\"1234\">");
		expectedSB.append("<source media=\"(max-width:1986px)\" ");
		expectedSB.append("srcset=\"adaptiveURL1\" />");
		expectedSB.append("<source media=\"(max-width:1989px)\" ");
		expectedSB.append("srcset=\"adaptiveURL2\" />");
		expectedSB.append(originalImgTag);
		expectedSB.append("</picture>");

		String pictureTag = _amImageHTMLTagFactoryImpl.create(
			originalImgTag, _fileEntry);

		Assert.assertEquals(expectedSB.toString(), pictureTag);
	}

	@Test
	public void testCreatesAPictureTagWithViewportSources() throws Exception {
		_addMediaQueries(_createMediaQuery(1989, "adaptiveURL"));
		_addViewportConfiguration("desktopUUID", "desktopURL");
		_addViewportConfiguration("landscapeUUID", "landscapeURL");
		_addViewportConfiguration("portraitUUID", "portraitURL");
		_addViewportConfiguration("tabletUUID", "tabletURL");

		String originalImgTag = StringBundler.concat(
			"<img data-desktop-configuration=\"desktopUUID\" ",
			"data-fileentryid=\"1234\" ",
			"data-landscapemobile-configuration=\"landscapeUUID\" ",
			"data-portraitmobile-configuration=\"portraitUUID\" ",
			"data-tablet-configuration=\"tabletUUID\" src=\"originalURL\"/>");

		StringBundler expectedSB = new StringBundler(13);

		expectedSB.append("<picture data-fileentryid=\"1234\">");
		expectedSB.append("<source media=\"(max-width:991px) and ");
		expectedSB.append("(min-width:768px)\" srcset=\"tabletURL\" />");
		expectedSB.append("<source media=\"(max-width:575px) and ");
		expectedSB.append("(min-width:240px)\" srcset=\"portraitURL\" />");
		expectedSB.append("<source media=\"(max-width:767px) and ");
		expectedSB.append("(min-width:576px)\" srcset=\"landscapeURL\" />");
		expectedSB.append("<source media=\"(min-width:960px)\" ");
		expectedSB.append("srcset=\"desktopURL\" />");
		expectedSB.append("<source media=\"(max-width:1989px)\" ");
		expectedSB.append("srcset=\"adaptiveURL\" />");
		expectedSB.append(originalImgTag);
		expectedSB.append("</picture>");

		String pictureTag = _amImageHTMLTagFactoryImpl.create(
			originalImgTag, _fileEntry);

		Assert.assertEquals(expectedSB.toString(), pictureTag);
	}

	@Test
	public void testIgnoresAnUnknownViewportConfiguration() throws Exception {
		_addMediaQueries(_createMediaQuery(1989, "adaptiveURL"));

		String originalImgTag = StringBundler.concat(
			"<img data-fileentryid=\"1234\" data-tablet-configuration=\"",
			RandomTestUtil.randomString(), "\" src=\"originalURL\"/>");

		StringBundler expectedSB = new StringBundler(5);

		expectedSB.append("<picture data-fileentryid=\"1234\">");
		expectedSB.append("<source media=\"(max-width:1989px)\" ");
		expectedSB.append("srcset=\"adaptiveURL\" />");
		expectedSB.append(originalImgTag);
		expectedSB.append("</picture>");

		String pictureTag = _amImageHTMLTagFactoryImpl.create(
			originalImgTag, _fileEntry);

		Assert.assertEquals(expectedSB.toString(), pictureTag);
	}

	@Test
	public void testNoSourceIsCreatedIfNoConditionIsPresent() throws Exception {
		_addMediaQueries(
			new MediaQuery(
				Collections.emptyList(), RandomTestUtil.randomString()));

		String originalImgTag =
			"<img src=\"originalURL\" data-fileentryid=\"1234\"/>";

		String pictureTag = _amImageHTMLTagFactoryImpl.create(
			originalImgTag, _fileEntry);

		Assert.assertEquals(
			StringBundler.concat(
				"<picture data-fileentryid=\"1234\">", originalImgTag,
				"</picture>"),
			pictureTag);
	}

	@Test
	public void testReturnsTheOriginalImgTagIfThereAreNoMediaQueries()
		throws Exception {

		_addMediaQueries();

		String originalImgTag =
			"<img src=\"originalURL\" data-fileentryid=\"1234\"/>";

		String pictureTag = _amImageHTMLTagFactoryImpl.create(
			originalImgTag, _fileEntry);

		Assert.assertSame(originalImgTag, pictureTag);
	}

	@Test
	public void testSupportsImageTagsWithNewLineCharacters() throws Exception {
		_addMediaQueries(_createMediaQuery(1989, "adaptiveURL"));

		StringBundler originalSB = new StringBundler(2);

		originalSB.append("<img data-fileentryid=\"1234\" \n");
		originalSB.append("src=\"adaptable\"/>");

		StringBundler expectedSB = new StringBundler(5);

		expectedSB.append("<picture data-fileentryid=\"1234\">");
		expectedSB.append("<source media=\"(max-width:1989px)\" ");
		expectedSB.append("srcset=\"adaptiveURL\" />");
		expectedSB.append(originalSB.toString());
		expectedSB.append("</picture>");

		String pictureTag = _amImageHTMLTagFactoryImpl.create(
			originalSB.toString(), _fileEntry);

		Assert.assertEquals(expectedSB.toString(), pictureTag);
	}

	@Test
	public void testTheAttributeIsCaseInsensitive() throws Exception {
		_addMediaQueries(_createMediaQuery(1989, "adaptiveURL"));

		String originalImgTag =
			"<img src=\"originalURL\" datA-fileENTryID=\"1234\"/>";

		StringBundler expectedSB = new StringBundler(5);

		expectedSB.append("<picture data-fileentryid=\"1234\">");
		expectedSB.append("<source media=\"(max-width:1989px)\" ");
		expectedSB.append("srcset=\"adaptiveURL\" />");
		expectedSB.append(originalImgTag);
		expectedSB.append("</picture>");

		String pictureTag = _amImageHTMLTagFactoryImpl.create(
			originalImgTag, _fileEntry);

		Assert.assertEquals(expectedSB.toString(), pictureTag);
	}

	private void _addMediaQueries(MediaQuery... mediaQueries) throws Exception {
		Mockito.when(
			_mediaQueryProvider.getMediaQueries(_fileEntry)
		).thenReturn(
			Arrays.asList(mediaQueries)
		);
	}

	private void _addViewportConfiguration(String uuid, String url)
		throws Exception {

		AMImageConfigurationEntry amImageConfigurationEntry = Mockito.mock(
			AMImageConfigurationEntry.class);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.eq(uuid))
		).thenReturn(
			amImageConfigurationEntry
		);

		Mockito.when(
			_amImageURLFactory.createFileEntryURL(
				_fileVersion, amImageConfigurationEntry)
		).thenReturn(
			URI.create(url)
		);
	}

	private MediaQuery _createMediaQuery(int width, String url)
		throws Exception {

		return new MediaQuery(
			Arrays.asList(new Condition("max-width", width + "px")), url);
	}

	private final AMImageConfigurationHelper _amImageConfigurationHelper =
		Mockito.mock(AMImageConfigurationHelper.class);
	private final AMImageHTMLTagFactoryImpl _amImageHTMLTagFactoryImpl =
		new AMImageHTMLTagFactoryImpl();
	private final AMImageURLFactory _amImageURLFactory = Mockito.mock(
		AMImageURLFactory.class);
	private final FileEntry _fileEntry = Mockito.mock(FileEntry.class);
	private final FileVersion _fileVersion = Mockito.mock(FileVersion.class);
	private final MediaQueryProvider _mediaQueryProvider = Mockito.mock(
		MediaQueryProvider.class);

}