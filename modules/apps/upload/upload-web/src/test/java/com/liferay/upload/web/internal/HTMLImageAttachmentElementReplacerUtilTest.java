/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.web.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileEntryWrapper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alejandro Tardín
 */
public class HTMLImageAttachmentElementReplacerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_fileEntry = new FileEntryWrapper(null) {

			@Override
			public long getFileEntryId() {
				return _IMAGE_FILE_ENTRY_ID;
			}

		};

		_portletFileRepository = Mockito.mock(PortletFileRepository.class);

		Mockito.when(
			_portletFileRepository.getPortletFileEntryURL(
				Mockito.nullable(ThemeDisplay.class), Mockito.eq(_fileEntry),
				Mockito.eq(StringPool.BLANK))
		).thenReturn(
			_FILE_ENTRY_IMAGE_URL
		);
	}

	@Test
	public void testKeepsCustomAttributes() throws Exception {
		String originalImgTag = String.format(
			"<img class=\"custom\" data-image-id=\"%s\" src=\"%s\" />",
			_IMAGE_FILE_ENTRY_ID, StringUtil.randomString());
		String expectedImgTag = String.format(
			"<img class=\"custom\" src=\"%s\" data-fileentryid=\"%s\" />",
			_FILE_ENTRY_IMAGE_URL, _IMAGE_FILE_ENTRY_ID);

		String actualTag = HTMLImageAttachmentElementReplacerUtil.replace(
			originalImgTag, _fileEntry, _portletFileRepository);

		Assert.assertEquals(expectedImgTag, actualTag);
	}

	@Test
	public void testRemovesTemporalAttributeAndSetsFinalSrc() throws Exception {
		String originalImgTag = String.format(
			"<img data-image-id=\"%s\" src=\"%s\" />", _IMAGE_FILE_ENTRY_ID,
			StringUtil.randomString());
		String expectedImgTag = String.format(
			"<img src=\"%s\" data-fileentryid=\"%s\" />", _FILE_ENTRY_IMAGE_URL,
			_IMAGE_FILE_ENTRY_ID);

		String actualTag = HTMLImageAttachmentElementReplacerUtil.replace(
			originalImgTag, _fileEntry, _portletFileRepository);

		Assert.assertEquals(expectedImgTag, actualTag);
	}

	private static final String _FILE_ENTRY_IMAGE_URL =
		RandomTestUtil.randomString();

	private static final long _IMAGE_FILE_ENTRY_ID =
		RandomTestUtil.randomLong();

	private FileEntry _fileEntry;
	private PortletFileRepository _portletFileRepository;

}