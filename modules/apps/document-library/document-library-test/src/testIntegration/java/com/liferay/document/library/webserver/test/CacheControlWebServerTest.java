/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.webserver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.BaseWebServerTestCase;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class CacheControlWebServerTest extends BaseWebServerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDownload() throws Exception {
		String urlTitle = RandomTestUtil.randomString();

		_addFileEntry(RandomTestUtil.randomString(), urlTitle);

		MockHttpServletResponse mockHttpServletResponse = service(
			HttpMethods.GET, _getFileEntryFriendlyURL(urlTitle),
			Collections.emptyMap(),
			HashMapBuilder.put(
				"download", Boolean.TRUE.toString()
			).build(),
			TestPropsValues.getUser(), null);

		Assert.assertEquals(
			"must-revalidate, max-age=600",
			mockHttpServletResponse.getHeader(HttpHeaders.CACHE_CONTROL));
	}

	@Test
	public void testDownloadWhenCTCollectionIsCheckedOut() throws Exception {
		String urlTitle = RandomTestUtil.randomString();

		_addFileEntry(RandomTestUtil.randomString(), urlTitle);

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, group.getCompanyId(), TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			MockHttpServletResponse mockHttpServletResponse = service(
				HttpMethods.GET, _getFileEntryFriendlyURL(urlTitle),
				Collections.emptyMap(),
				HashMapBuilder.put(
					"download", Boolean.TRUE.toString()
				).build(),
				TestPropsValues.getUser(), null);

			Assert.assertEquals(
				HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE,
				mockHttpServletResponse.getHeader(HttpHeaders.CACHE_CONTROL));
		}
	}

	private FileEntry _addFileEntry(String fileName, String urlTitle)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), urlTitle,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			(byte[])null, null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private String _getFileEntryFriendlyURL(String urlTitle) {
		return String.format(
			"%s%s/%s", FriendlyURLResolverConstants.URL_SEPARATOR_X_FILE_ENTRY,
			group.getFriendlyURL(), urlTitle);
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Inject
	private Portal _portal;

}