/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.field.reader.InfoItemFieldReader;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jodd.net.MimeTypes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class FileEntryInfoItemFieldValuesProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testFileEntryInfoItemFieldReader() throws PortalException {
		Bundle bundle = FrameworkUtil.getBundle(InfoItemFieldReader.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<InfoItemFieldReader> serviceRegistration =
			bundleContext.registerService(
				InfoItemFieldReader.class,
				new TestFileEntryInfoItemFieldReader(), null);

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			MimeTypes.MIME_APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		InfoItemFieldValues infoItemFieldValues =
			_infoItemFieldValuesProvider.getInfoItemFieldValues(fileEntry);

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(
				TestFileEntryInfoItemFieldReader._INFO_FIELD_NAME);

		Assert.assertEquals(
			TestFileEntryInfoItemFieldReader._INFO_FIELD_VALUE,
			infoFieldValue.getValue());

		serviceRegistration.unregister();
	}

	@Test
	public void testGetInfoItemFieldValues() throws PortalException {
		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			MimeTypes.MIME_APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		InfoItemFieldValues infoItemFieldValues =
			_infoItemFieldValuesProvider.getInfoItemFieldValues(fileEntry);

		InfoFieldValue<Object> createDateInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("createDate");

		Assert.assertEquals(
			fileEntry.getCreateDate(), createDateInfoFieldValue.getValue());

		InfoFieldValue<Object> modifiedDateInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("modifiedDate");

		Assert.assertEquals(
			fileEntry.getModifiedDate(), modifiedDateInfoFieldValue.getValue());

		InfoFieldValue<Object> publishDateInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("publishDate");

		Assert.assertEquals(
			fileEntry.getModifiedDate(), publishDateInfoFieldValue.getValue());
	}

	@Inject
	private DLAppService _dlAppService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.info.item.provider.FileEntryInfoItemFieldValuesProvider"
	)
	private InfoItemFieldValuesProvider _infoItemFieldValuesProvider;

	private static class TestFileEntryInfoItemFieldReader
		implements InfoItemFieldReader<FileEntry> {

		@Override
		public InfoField getInfoField() {
			return InfoField.builder(
			).infoFieldType(
				TextInfoFieldType.INSTANCE
			).namespace(
				FileEntry.class.getSimpleName()
			).name(
				_INFO_FIELD_NAME
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					getClass(), RandomTestUtil.randomString())
			).build();
		}

		@Override
		public Object getValue(FileEntry fileEntry) {
			return _INFO_FIELD_VALUE;
		}

		private static final String _INFO_FIELD_NAME =
			RandomTestUtil.randomString();

		private static final String _INFO_FIELD_VALUE =
			RandomTestUtil.randomString();

	}

}