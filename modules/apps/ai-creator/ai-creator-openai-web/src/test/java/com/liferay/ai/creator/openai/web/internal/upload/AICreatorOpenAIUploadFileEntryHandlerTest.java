/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.upload;

import com.liferay.ai.creator.openai.web.internal.util.AICreatorOpenAIGenerationUtil;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import javax.crypto.spec.SecretKeySpec;

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
		_fileUtilMockedStatic.when(
			() -> FileUtil.createTempFile(Mockito.any(InputStream.class))
		).thenAnswer(
			invocationOnMock -> {
				InputStream inputStream = invocationOnMock.getArgument(
					0, InputStream.class);

				_atomicReference.set(inputStream.readAllBytes());

				return _createTempFile(new byte[0]);
			}
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
				Mockito.anyLong(), Mockito.anyString(), Mockito.eq("image/png"),
				Mockito.anyString(), Mockito.anyString(),
				Mockito.nullable(String.class), Mockito.nullable(String.class),
				Mockito.any(File.class), Mockito.nullable(Date.class),
				Mockito.nullable(Date.class), Mockito.nullable(Date.class),
				Mockito.any(ServiceContext.class))
		).thenReturn(
			fileEntry
		);

		AICreatorOpenAIUploadFileEntryHandler
			aiCreatorOpenAIUploadFileEntryHandler =
				new AICreatorOpenAIUploadFileEntryHandler(dlAppService);

		byte[] bytes = ArrayUtil.append(
			new byte[] {
				(byte)0x89, 'P', 'N', 'G', '\r', '\n', (byte)0x1A, '\n'
			},
			RandomTestUtil.randomBytes());
		long expirationTime = System.currentTimeMillis() + Time.HOUR;

		Assert.assertSame(
			fileEntry,
			aiCreatorOpenAIUploadFileEntryHandler.upload(
				_mockUploadPortletRequest(
					bytes, expirationTime,
					AICreatorOpenAIGenerationUtil.sign(
						bytes, _company, expirationTime, _USER_ID))));

		Assert.assertArrayEquals(bytes, _atomicReference.get());
	}

	@Test
	public void testUploadWithInvalidSignature() throws Exception {
		byte[] bytes = RandomTestUtil.randomBytes();
		long expirationTime = System.currentTimeMillis() + Time.HOUR;

		_testUploadWithInvalidSignature(
			bytes, expirationTime,
			AICreatorOpenAIGenerationUtil.sign(
				RandomTestUtil.randomBytes(), _company, expirationTime,
				_USER_ID));
		_testUploadWithInvalidSignature(bytes, expirationTime, null);
	}

	@Test
	public void testUploadWithoutImage() throws Exception {
		DLAppService dlAppService = Mockito.mock(DLAppService.class);

		AICreatorOpenAIUploadFileEntryHandler
			aiCreatorOpenAIUploadFileEntryHandler =
				new AICreatorOpenAIUploadFileEntryHandler(dlAppService);

		Assert.assertNull(
			aiCreatorOpenAIUploadFileEntryHandler.upload(
				Mockito.mock(UploadPortletRequest.class)));

		Mockito.verifyNoInteractions(dlAppService);
	}

	private static Company _mockCompany() {
		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			company.getKeyObj()
		).thenReturn(
			new SecretKeySpec(RandomTestUtil.randomBytes(), "AES")
		);

		return company;
	}

	private File _createTempFile(byte[] bytes) throws Exception {
		Path path = Files.createTempFile(null, null);

		File file = path.toFile();

		file.deleteOnExit();

		Files.write(path, bytes);

		return file;
	}

	private UploadPortletRequest _mockUploadPortletRequest(
			byte[] bytes, long expirationTime, String signature)
		throws Exception {

		UploadPortletRequest uploadPortletRequest = Mockito.mock(
			UploadPortletRequest.class);

		PortletRequest portletRequest = Mockito.mock(PortletRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompany()
		).thenReturn(
			_company
		);

		Mockito.when(
			themeDisplay.getUserId()
		).thenReturn(
			_USER_ID
		);

		Mockito.when(
			portletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			portletRequest.getParameter("expirationTime")
		).thenReturn(
			String.valueOf(expirationTime)
		);

		Mockito.when(
			portletRequest.getParameter("signature")
		).thenReturn(
			signature
		);

		Mockito.when(
			uploadPortletRequest.getFile("image")
		).thenReturn(
			_createTempFile(bytes)
		);

		Mockito.when(
			uploadPortletRequest.getPortletRequest()
		).thenReturn(
			portletRequest
		);

		return uploadPortletRequest;
	}

	private void _testUploadWithInvalidSignature(
			byte[] bytes, long expirationTime, String signature)
		throws Exception {

		DLAppService dlAppService = Mockito.mock(DLAppService.class);

		AICreatorOpenAIUploadFileEntryHandler
			aiCreatorOpenAIUploadFileEntryHandler =
				new AICreatorOpenAIUploadFileEntryHandler(dlAppService);

		try {
			aiCreatorOpenAIUploadFileEntryHandler.upload(
				_mockUploadPortletRequest(bytes, expirationTime, signature));

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		Mockito.verifyNoInteractions(dlAppService);
	}

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private static final AtomicReference<byte[]> _atomicReference =
		new AtomicReference<>();
	private static final Company _company = _mockCompany();
	private static final MockedStatic<FileUtil> _fileUtilMockedStatic =
		Mockito.mockStatic(FileUtil.class);
	private static final MockedStatic<MimeTypesUtil>
		_mimeTypesUtilMockedStatic = Mockito.mockStatic(MimeTypesUtil.class);
	private static final MockedStatic<ServiceContextFactory>
		_serviceContextFactoryMockedStatic = Mockito.mockStatic(
			ServiceContextFactory.class);

}