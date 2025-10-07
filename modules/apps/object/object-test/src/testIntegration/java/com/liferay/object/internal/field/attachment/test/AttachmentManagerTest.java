/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.attachment.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class AttachmentManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinition = _addObjectDefinition("txt, png");

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId());

		_objectField = _objectFieldLocalService.getObjectField(
			_objectDefinition.getObjectDefinitionId(), "attachment");
	}

	@Test
	public void testGetOrAddFileEntry() throws Exception {
		FileEntry tempFileEntry = _addTempFileEntry(
			RandomTestUtil.randomString(), ".txt",
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			_objectDefinition);

		Folder folder = tempFileEntry.getFolder();

		FileEntry fileEntry = _attachmentManager.getOrAddFileEntry(
			_objectField.getCompanyId(),
			tempFileEntry.getExternalReferenceCode(),
			StreamUtil.toByteArray(tempFileEntry.getContentStream()),
			tempFileEntry.getFileName(), folder.getExternalReferenceCode(),
			TestPropsValues.getGroupId(), _objectField.getObjectFieldId(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			tempFileEntry.getExternalReferenceCode(),
			fileEntry.getExternalReferenceCode());

		String externalReferenceCode = RandomTestUtil.randomString();

		fileEntry = _attachmentManager.getOrAddFileEntry(
			_objectField.getCompanyId(), externalReferenceCode,
			StreamUtil.toByteArray(tempFileEntry.getContentStream()),
			tempFileEntry.getFileName(), folder.getExternalReferenceCode(),
			TestPropsValues.getGroupId(), _objectField.getObjectFieldId(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			externalReferenceCode, fileEntry.getExternalReferenceCode());

		try {
			tempFileEntry = _addTempFileEntry(
				RandomTestUtil.randomString(), ".bmp",
				RandomTestUtil.randomString(), ContentTypes.IMAGE_BMP,
				_objectDefinition);

			folder = tempFileEntry.getFolder();

			_attachmentManager.getOrAddFileEntry(
				_objectField.getCompanyId(), RandomTestUtil.randomString(),
				StreamUtil.toByteArray(tempFileEntry.getContentStream()),
				tempFileEntry.getFileName(), folder.getExternalReferenceCode(),
				TestPropsValues.getGroupId(), _objectField.getObjectFieldId(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (FileExtensionException fileExtensionException) {
			Assert.assertNotNull(fileExtensionException);
		}

		ObjectDefinition objectDefinition = _addObjectDefinition("*");

		tempFileEntry = _addTempFileEntry(
			RandomTestUtil.randomString(), ".bmp",
			RandomTestUtil.randomString(), ContentTypes.IMAGE_BMP,
			objectDefinition);

		folder = tempFileEntry.getFolder();

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), "attachment");

		externalReferenceCode = RandomTestUtil.randomString();

		fileEntry = _attachmentManager.getOrAddFileEntry(
			objectField.getCompanyId(), externalReferenceCode,
			StreamUtil.toByteArray(tempFileEntry.getContentStream()),
			tempFileEntry.getFileName(), folder.getExternalReferenceCode(),
			TestPropsValues.getGroupId(), objectField.getObjectFieldId(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			externalReferenceCode, fileEntry.getExternalReferenceCode());

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					DLConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"fileExtensions", new String[] {".doc"}
					).build())) {

			tempFileEntry = _addTempFileEntry(
				RandomTestUtil.randomString(), ".txt",
				RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
				_objectDefinition);

			folder = tempFileEntry.getFolder();

			_attachmentManager.getOrAddFileEntry(
				_objectField.getCompanyId(), RandomTestUtil.randomString(),
				StreamUtil.toByteArray(tempFileEntry.getContentStream()),
				tempFileEntry.getFileName(), folder.getExternalReferenceCode(),
				TestPropsValues.getGroupId(), _objectField.getObjectFieldId(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (FileExtensionException fileExtensionException) {
			Assert.assertNotNull(fileExtensionException);
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DLFileEntryMimeTypeConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"fileMimeTypes", new String[] {"text/html"}
						).build())) {

			tempFileEntry = _addTempFileEntry(
				RandomTestUtil.randomString(), ".txt",
				RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
				_objectDefinition);

			folder = tempFileEntry.getFolder();

			_attachmentManager.getOrAddFileEntry(
				_objectField.getCompanyId(), RandomTestUtil.randomString(),
				StreamUtil.toByteArray(tempFileEntry.getContentStream()),
				tempFileEntry.getFileName(), folder.getExternalReferenceCode(),
				TestPropsValues.getGroupId(), _objectField.getObjectFieldId(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (FileMimeTypeException fileMimeTypeException) {
			Assert.assertNotNull(fileMimeTypeException);
		}

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.document.library.internal.configuration." +
						"DLSizeLimitConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"mimeTypeSizeLimit", new String[] {"text/plain:1"}
					).build())) {

			tempFileEntry = _addTempFileEntry(
				RandomTestUtil.randomString(1000), ".txt",
				RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
				_objectDefinition);

			folder = tempFileEntry.getFolder();

			_attachmentManager.getOrAddFileEntry(
				_objectField.getCompanyId(), RandomTestUtil.randomString(),
				StreamUtil.toByteArray(tempFileEntry.getContentStream()),
				tempFileEntry.getFileName(), folder.getExternalReferenceCode(),
				TestPropsValues.getGroupId(), _objectField.getObjectFieldId(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (FileSizeException fileSizeException) {
			Assert.assertNotNull(fileSizeException);
		}
	}

	private ObjectDefinition _addObjectDefinition(String acceptedFileExtensions)
		throws Exception {

		return ObjectDefinitionTestUtil.addCustomObjectDefinition(
			false,
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					RandomTestUtil.randomString(), "attachment",
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS
						).value(
							acceptedFileExtensions
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE
						).value(
							ObjectFieldSettingConstants.VALUE_USER_COMPUTER
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
						).value(
							"100"
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
					ObjectFieldConstants.DB_TYPE_INTEGER, true, false, null,
					RandomTestUtil.randomString(), "integer", false)));
	}

	private FileEntry _addTempFileEntry(
			String content, String extension, String fileName, String mimeType,
			ObjectDefinition objectDefinition)
		throws Exception {

		return TempFileEntryUtil.addTempFileEntry(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName(fileName + extension),
			FileUtil.createTempFile(content.getBytes()), mimeType);
	}

	@Inject
	private AttachmentManager _attachmentManager;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectField _objectField;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

}