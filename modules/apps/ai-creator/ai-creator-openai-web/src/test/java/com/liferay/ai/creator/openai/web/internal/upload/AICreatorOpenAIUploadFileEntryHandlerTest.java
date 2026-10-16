/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.upload;

import com.liferay.ai.creator.openai.web.internal.constants.AICreatorOpenAIWebKeys;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class AICreatorOpenAIUploadFileEntryHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_fileUtilMockedStatic.close();
		_mimeTypesUtilMockedStatic.close();
		_serviceContextFactoryMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		Path path = Files.createTempFile(null, null);

		File file = path.toFile();

		file.deleteOnExit();

		_fileUtilMockedStatic.when(
			() -> FileUtil.createTempFile(Mockito.any(InputStream.class))
		).thenReturn(
			file
		);

		_serviceContextFactoryMockedStatic.when(
			() -> ServiceContextFactory.getInstance(
				Mockito.anyString(), Mockito.any(PortletRequest.class))
		).thenReturn(
			new ServiceContext()
		);
	}

	@Test
	public void testUpload() throws Exception {
		DLAppService dlAppService = Mockito.mock(DLAppService.class);
		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			dlAppService.addFileEntry(
				Mockito.nullable(String.class), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyString(),
				Mockito.nullable(String.class), Mockito.anyString(),
				Mockito.anyString(), Mockito.nullable(String.class),
				Mockito.nullable(String.class), Mockito.any(File.class),
				Mockito.nullable(Date.class), Mockito.nullable(Date.class),
				Mockito.nullable(Date.class), Mockito.any(ServiceContext.class))
		).thenReturn(
			fileEntry
		);

		AICreatorOpenAIUploadFileEntryHandler
			aiCreatorOpenAIUploadFileEntryHandler =
				new AICreatorOpenAIUploadFileEntryHandler(dlAppService);
		String urlPath = _getURLPath();

		Assert.assertSame(
			fileEntry,
			aiCreatorOpenAIUploadFileEntryHandler.upload(
				_mockUploadPortletRequest(
					SetUtil.fromArray(urlPath), urlPath)));
	}

	@Test
	public void testUploadWithDataURL() throws Exception {
		byte[] bytes = ArrayUtil.append(
			new byte[] {
				(byte)0x89, 'P', 'N', 'G', '\r', '\n', (byte)0x1A, '\n'
			},
			RandomTestUtil.randomBytes());

		AtomicReference<byte[]> atomicReference = new AtomicReference<>();

		_fileUtilMockedStatic.when(
			() -> FileUtil.createTempFile(Mockito.any(InputStream.class))
		).thenAnswer(
			invocationOnMock -> {
				InputStream inputStream = invocationOnMock.getArgument(
					0, InputStream.class);

				atomicReference.set(inputStream.readAllBytes());

				Path path = Files.createTempFile(null, null);

				File file = path.toFile();

				file.deleteOnExit();

				return file;
			}
		);

		DLAppService dlAppService = Mockito.mock(DLAppService.class);

		AICreatorOpenAIUploadFileEntryHandler
			aiCreatorOpenAIUploadFileEntryHandler =
				new AICreatorOpenAIUploadFileEntryHandler(dlAppService);

		String urlPath = "data:image/png;base64," + Base64.encode(bytes);

		aiCreatorOpenAIUploadFileEntryHandler.upload(
			_mockUploadPortletRequest(SetUtil.fromArray(urlPath), urlPath));

		Assert.assertArrayEquals(bytes, atomicReference.get());

		Mockito.verify(
			dlAppService
		).addFileEntry(
			Mockito.nullable(String.class), Mockito.anyLong(),
			Mockito.anyLong(), Mockito.anyString(), Mockito.eq("image/png"),
			Mockito.anyString(), Mockito.anyString(),
			Mockito.nullable(String.class), Mockito.nullable(String.class),
			Mockito.any(File.class), Mockito.nullable(Date.class),
			Mockito.nullable(Date.class), Mockito.nullable(Date.class),
			Mockito.any(ServiceContext.class)
		);
	}

	@Test
	public void testUploadWithInvalidURLPath() throws Exception {
		_testUploadWithInvalidURLPath(
			SetUtil.fromArray(RandomTestUtil.randomString()));
		_testUploadWithInvalidURLPath(null);
	}

	private String _getURLPath() throws Exception {
		Path path = Files.createTempFile(null, null);

		File file = path.toFile();

		file.deleteOnExit();

		Files.write(path, RandomTestUtil.randomBytes());

		return String.valueOf(path.toUri());
	}

	private PortletSession _mockPortletSession(Set<String> generations) {
		PortletSession portletSession = Mockito.mock(PortletSession.class);

		Mockito.when(
			portletSession.getAttribute(
				AICreatorOpenAIWebKeys.AI_CREATOR_OPENAI_GENERATIONS)
		).thenReturn(
			generations
		);

		return portletSession;
	}

	private UploadPortletRequest _mockUploadPortletRequest(
		Set<String> generations, String urlPath) {

		PortletRequest portletRequest = Mockito.mock(PortletRequest.class);

		Mockito.when(
			portletRequest.getParameter("urlPath")
		).thenReturn(
			urlPath
		);

		PortletSession portletSession = _mockPortletSession(generations);

		Mockito.when(
			portletRequest.getPortletSession()
		).thenReturn(
			portletSession
		);

		UploadPortletRequest uploadPortletRequest = Mockito.mock(
			UploadPortletRequest.class);

		Mockito.when(
			uploadPortletRequest.getPortletRequest()
		).thenReturn(
			portletRequest
		);

		return uploadPortletRequest;
	}

	private void _testUploadWithInvalidURLPath(Set<String> generations)
		throws Exception {

		DLAppService dlAppService = Mockito.mock(DLAppService.class);

		AICreatorOpenAIUploadFileEntryHandler
			aiCreatorOpenAIUploadFileEntryHandler =
				new AICreatorOpenAIUploadFileEntryHandler(dlAppService);

		try {
			aiCreatorOpenAIUploadFileEntryHandler.upload(
				_mockUploadPortletRequest(generations, _getURLPath()));

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		Mockito.verifyNoInteractions(dlAppService);
	}

	private static final MockedStatic<FileUtil> _fileUtilMockedStatic =
		Mockito.mockStatic(FileUtil.class);
	private static final MockedStatic<MimeTypesUtil>
		_mimeTypesUtilMockedStatic = Mockito.mockStatic(MimeTypesUtil.class);
	private static final MockedStatic<ServiceContextFactory>
		_serviceContextFactoryMockedStatic = Mockito.mockStatic(
			ServiceContextFactory.class);

}