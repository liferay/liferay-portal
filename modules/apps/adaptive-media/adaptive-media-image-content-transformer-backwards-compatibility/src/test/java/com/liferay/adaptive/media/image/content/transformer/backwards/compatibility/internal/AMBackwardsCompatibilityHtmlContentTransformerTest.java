/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.content.transformer.backwards.compatibility.internal;

import com.liferay.adaptive.media.image.content.transformer.backwards.compatibility.internal.configuration.AMBackwardsCompatibilityHtmlContentTransformerConfiguration;
import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class AMBackwardsCompatibilityHtmlContentTransformerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_amImageHTMLTagFactory.create(
				Mockito.anyString(), Mockito.any(FileEntry.class))
		).thenReturn(
			"[REPLACED]"
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			_fileEntry
		);

		Mockito.when(
			_fileEntry.getMimeType()
		).thenReturn(
			ContentTypes.IMAGE_JPEG
		);

		Mockito.when(
			_fileEntryFriendlyURLResolver.resolveFriendlyURL(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_fileEntry
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_group
		);

		_enableContentTransformer(true);

		ReflectionTestUtil.setFieldValue(
			_contentTransformer, "_amImageHTMLTagFactory",
			_amImageHTMLTagFactory);
		ReflectionTestUtil.setFieldValue(
			_contentTransformer, "_amImageMimeTypeProvider",
			_amImageMimeTypeProvider);
		ReflectionTestUtil.setFieldValue(
			_contentTransformer, "_dlAppLocalService", _dlAppLocalService);
		ReflectionTestUtil.setFieldValue(
			_contentTransformer, "_fileEntryFriendlyURLResolver",
			_fileEntryFriendlyURLResolver);
		ReflectionTestUtil.setFieldValue(
			_contentTransformer, "_groupLocalService", _groupLocalService);
	}

	@Test
	public void testDoesNothingWhenDisabled() throws Exception {
		_enableContentTransformer(false);

		Assert.assertEquals(
			_CONTENT_WITH_IMAGE_FRIENDLY_URL,
			_contentTransformer.transform(_CONTENT_WITH_IMAGE_FRIENDLY_URL));
	}

	@Test
	public void testLeavesPictureTagsAsIs() throws Exception {
		Assert.assertEquals(
			_PICTURE_TAG, _contentTransformer.transform(_PICTURE_TAG));
	}

	@Test
	public void testReplacesFriendlyURLImageTagsWithDoubleQuotes()
		throws Exception {

		Assert.assertEquals(
			_CONTENT_PREFIX + "[REPLACED]" + _CONTENT_SUFFIX,
			_contentTransformer.transform(_CONTENT_WITH_IMAGE_FRIENDLY_URL));
	}

	@Test
	public void testReplacesImageTagsOutsidePictureTag() throws Exception {
		Assert.assertEquals(
			StringBundler.concat(
				_CONTENT_PREFIX, "[REPLACED]", _PICTURE_TAG, _CONTENT_SUFFIX),
			_contentTransformer.transform(_CONTENT_WITH_IMAGE_AND_PICTURE));
		Assert.assertEquals(
			StringBundler.concat(
				_CONTENT_PREFIX, _PICTURE_TAG, "[REPLACED]", _CONTENT_SUFFIX),
			_contentTransformer.transform(_CONTENT_WITH_PICTURE_AND_IMAGE));
	}

	@Test
	public void testReplacesImageTagsWithDoubleQuotes() throws Exception {
		Assert.assertEquals(
			_CONTENT_PREFIX + "[REPLACED]" + _CONTENT_SUFFIX,
			_contentTransformer.transform(
				_CONTENT_WITH_IMAGE_AND_DOUBLE_QUOTES));
	}

	@Test
	public void testReplacesImageTagsWithLegacyContent() throws Exception {
		Mockito.when(
			_dlAppLocalService.fetchFileEntry(20138, 0, "sample.jpg")
		).thenReturn(
			_fileEntry
		);

		Assert.assertEquals(
			_CONTENT_PREFIX + "[REPLACED]" + _CONTENT_SUFFIX,
			_contentTransformer.transform(
				_LEGACY_CONTENT_WITH_IMAGE_AND_SINGLE_QUOTES));
	}

	@Test(timeout = 1000)
	public void testReplacesImageTagsWithLongTitleWithSpaces()
		throws Exception {

		Assert.assertEquals(
			_CONTENT_PREFIX + "[REPLACED]" + _CONTENT_SUFFIX,
			_contentTransformer.transform(
				_CONTENT_WITH_IMAGE_AND_LONG_TITLE_WITH_SPACES));
	}

	@Test
	public void testReplacesImageTagsWithQueryParameters() throws Exception {
		Assert.assertEquals(
			_CONTENT_PREFIX + "[REPLACED]" + _CONTENT_SUFFIX,
			_contentTransformer.transform(
				_CONTENT_WITH_IMAGE_AND_QUERY_PARAMETERS));
	}

	@Test
	public void testReplacesImageTagsWithSingleQuotes() throws Exception {
		Assert.assertEquals(
			_CONTENT_PREFIX + "[REPLACED]" + _CONTENT_SUFFIX,
			_contentTransformer.transform(
				_CONTENT_WITH_IMAGE_AND_SINGLE_QUOTES));
	}

	@Test
	public void testReturnsBlankForBlankContent() throws Exception {
		Assert.assertEquals(
			StringPool.BLANK, _contentTransformer.transform(StringPool.BLANK));
	}

	@Test
	public void testReturnsNullForNullContent() throws Exception {
		Assert.assertNull(_contentTransformer.transform(null));
	}

	@Test
	public void testReturnsTheSameHTMLIfNoImagesArePresent() throws Exception {
		String content = RandomTestUtil.randomString();

		Assert.assertEquals(content, _contentTransformer.transform(content));
	}

	@Test
	public void testSupportsImageTagsWithNewLineCharacters() throws Exception {
		Assert.assertEquals(
			_CONTENT_PREFIX + "[REPLACED]" + _CONTENT_SUFFIX,
			_contentTransformer.transform(_CONTENT_WITH_IMAGE_AND_NEWLINES));
	}

	private void _enableContentTransformer(boolean enabled) {
		ReflectionTestUtil.setFieldValue(
			_contentTransformer,
			"_amBackwardsCompatibilityHtmlContentTransformerConfiguration",
			new AMBackwardsCompatibilityHtmlContentTransformerConfiguration() {

				@Override
				public boolean enabled() {
					return enabled;
				}

			});
	}

	private static final String _CONTENT_PREFIX = "<p>Prefix";

	private static final String _CONTENT_SUFFIX = "Suffix</p>";

	private static final String _CONTENT_WITH_IMAGE_AND_DOUBLE_QUOTES =
		StringBundler.concat(
			_CONTENT_PREFIX, "<img src=\"/documents/20138/0/sample.jpg",
			"/1710bfe2-2b7c-1f69-f8b7-23ff6bd5dd4b?t=1506075653544\" />",
			_CONTENT_SUFFIX);

	private static final String _CONTENT_WITH_IMAGE_AND_LONG_TITLE_WITH_SPACES =
		StringBundler.concat(
			_CONTENT_PREFIX, "<img src=\"/documents/20138/0/sample.jpg",
			"/1710bfe2-2b7c-1f69-f8b7-23ff6bd5dd4b?t=1506075653544\" ",
			"title=\"1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 ",
			"9 0 1 2 3 4 5 \" />", _CONTENT_SUFFIX);

	private static final String _CONTENT_WITH_IMAGE_AND_NEWLINES =
		StringBundler.concat(
			_CONTENT_PREFIX, "<img\nsrc=\"/documents/20138/0/sample.jpg",
			"/1710bfe2-2b7c-1f69-f8b7-23ff6bd5dd4b?t=1506075653544\"\n />",
			_CONTENT_SUFFIX);

	private static final String _CONTENT_WITH_IMAGE_AND_PICTURE =
		StringBundler.concat(
			_CONTENT_PREFIX, "<img src='/documents/d/site_name/sample' />",
			AMBackwardsCompatibilityHtmlContentTransformerTest._PICTURE_TAG,
			_CONTENT_SUFFIX);

	private static final String _CONTENT_WITH_IMAGE_AND_QUERY_PARAMETERS =
		StringBundler.concat(
			_CONTENT_PREFIX, "<img src=\"/documents/20117/32920/sample.jpg",
			"/f095aa50-7c0c-ae36-05b6-94a5270085c8?version=1.0&t=",
			"1724834658363&imageThumbnail=1\" />", _CONTENT_SUFFIX);

	private static final String _CONTENT_WITH_IMAGE_AND_SINGLE_QUOTES =
		StringBundler.concat(
			_CONTENT_PREFIX, "<img src='/documents/20138/0/sample.jpg",
			"/1710bfe2-2b7c-1f69-f8b7-23ff6bd5dd4b?t=1506075653544' />",
			_CONTENT_SUFFIX);

	private static final String _CONTENT_WITH_IMAGE_FRIENDLY_URL =
		StringBundler.concat(
			_CONTENT_PREFIX, "<img src=\"/documents/d/site_name/sample\" />",
			_CONTENT_SUFFIX);

	private static final String _CONTENT_WITH_PICTURE_AND_IMAGE =
		StringBundler.concat(
			_CONTENT_PREFIX,
			AMBackwardsCompatibilityHtmlContentTransformerTest._PICTURE_TAG,
			"<img src='/documents/d/site_name/sample' />", _CONTENT_SUFFIX);

	private static final String _LEGACY_CONTENT_WITH_IMAGE_AND_SINGLE_QUOTES =
		StringBundler.concat(
			_CONTENT_PREFIX, "<img src='/documents/20138/0/sample.jpg?t=",
			"1506075653544' />", _CONTENT_SUFFIX);

	private static final String _PICTURE_TAG =
		"<picture><img src='/documents/d/site_name/sample' /></picture>";

	private final AMImageHTMLTagFactory _amImageHTMLTagFactory = Mockito.mock(
		AMImageHTMLTagFactory.class);
	private final AMImageMimeTypeProvider _amImageMimeTypeProvider =
		Mockito.mock(AMImageMimeTypeProvider.class);
	private final AMBackwardsCompatibilityHtmlContentTransformer
		_contentTransformer =
			new AMBackwardsCompatibilityHtmlContentTransformer();
	private final DLAppLocalService _dlAppLocalService = Mockito.mock(
		DLAppLocalService.class);
	private final FileEntry _fileEntry = Mockito.mock(FileEntry.class);
	private final FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver =
		Mockito.mock(FileEntryFriendlyURLResolver.class);
	private final Group _group = Mockito.mock(Group.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);

}