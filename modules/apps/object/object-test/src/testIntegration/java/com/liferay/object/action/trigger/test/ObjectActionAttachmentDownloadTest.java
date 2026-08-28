/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.action.trigger.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.test.util.BaseWebServerTestCase;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
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

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ObjectActionAttachmentDownloadTest extends BaseWebServerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_atomicInteger = new AtomicInteger();

		_objectField = ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
			ObjectFieldConstants.DB_TYPE_LONG, true, false, null, "File",
			"file",
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS
				).value(
					"txt"
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_FILE_SOURCE
				).value(
					ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
				).value(
					"100"
				).build()),
			false);

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			Collections.singletonList(_objectField),
			ObjectDefinitionConstants.SCOPE_COMPANY,
			TestPropsValues.getUserId());

		_serviceRegistration = _registerObjectActionExecutor();

		_objectAction = _objectActionLocalService.addObjectAction(
			null, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			Collections.<Locale, String>emptyMap(),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			RandomTestUtil.randomString(), _KEY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ATTACHMENT_DOWNLOAD,
			UnicodePropertiesBuilder.create(
				true
			).build(),
			false);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		super.tearDown();
	}

	@Test
	public void testRunsForItsOwnAttachment() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		MockHttpServletResponse mockHttpServletResponse = _download(
			_getFileEntry(objectEntry),
			_objectDefinition.getExternalReferenceCode(),
			objectEntry.getExternalReferenceCode());

		Assert.assertEquals(200, mockHttpServletResponse.getStatus());

		Assert.assertEquals(1, _atomicInteger.get());
	}

	@Test
	public void testRunsOncePerDownload() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		FileEntry fileEntry = _getFileEntry(objectEntry);

		_download(
			fileEntry, _objectDefinition.getExternalReferenceCode(),
			objectEntry.getExternalReferenceCode());

		Assert.assertEquals(1, _atomicInteger.get());

		// The action runs only once per set of object entry IDs, which a thread
		// local tracks by action ID. A request filter clears these records
		// between requests, but this test calls the servlet directly, so
		// remove the record here to make the second download a new request.

		Map<Long, Set<Long>> objectEntryIdsMap =
			ObjectActionThreadLocal.getObjectEntryIdsMap();

		objectEntryIdsMap.remove(_objectAction.getObjectActionId());

		_download(
			fileEntry, _objectDefinition.getExternalReferenceCode(),
			objectEntry.getExternalReferenceCode());

		Assert.assertEquals(2, _atomicInteger.get());
	}

	@Test
	public void testSkipsUnknownObjectDefinitionExternalReferenceCode()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		MockHttpServletResponse mockHttpServletResponse = _download(
			_getFileEntry(objectEntry), RandomTestUtil.randomString(),
			objectEntry.getExternalReferenceCode());

		Assert.assertEquals(200, mockHttpServletResponse.getStatus());

		Assert.assertEquals(0, _atomicInteger.get());
	}

	@Test
	public void testSkipsWhenNoFileIsServed() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		FileEntry fileEntry = _getFileEntry(objectEntry);

		fileEntry.setUuid(RandomTestUtil.randomString());

		MockHttpServletResponse mockHttpServletResponse = _download(
			fileEntry, _objectDefinition.getExternalReferenceCode(),
			objectEntry.getExternalReferenceCode());

		Assert.assertEquals(404, mockHttpServletResponse.getStatus());

		Assert.assertEquals(0, _atomicInteger.get());
	}

	@Test
	public void testSkipsWithoutObjectDefinitionExternalReferenceCode()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		MockHttpServletResponse mockHttpServletResponse = _download(
			_getFileEntry(objectEntry), null,
			objectEntry.getExternalReferenceCode());

		Assert.assertEquals(200, mockHttpServletResponse.getStatus());

		Assert.assertEquals(0, _atomicInteger.get());
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		FileEntry fileEntry = dlAppService.addFileEntry(
			null, group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK,
			new ByteArrayInputStream(RandomTestUtil.randomBytes()), 0, null,
			null, null, serviceContext);

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.US),
			HashMapBuilder.<String, Serializable>put(
				"file", fileEntry.getFileEntryId()
			).build(),
			serviceContext);
	}

	private MockHttpServletResponse _download(
			FileEntry fileEntry, String objectDefinitionExternalReferenceCode,
			String objectEntryExternalReferenceCode)
		throws Exception {

		String path = StringBundler.concat(
			StringPool.SLASH, fileEntry.getGroupId(), StringPool.SLASH,
			fileEntry.getFolderId(), StringPool.SLASH, fileEntry.getFileName(),
			StringPool.SLASH, fileEntry.getUuid());

		return service(
			"GET", path, Collections.emptyMap(),
			HashMapBuilder.put(
				"download", "true"
			).put(
				"objectDefinitionExternalReferenceCode",
				() -> objectDefinitionExternalReferenceCode
			).put(
				"objectEntryExternalReferenceCode",
				objectEntryExternalReferenceCode
			).build(),
			TestPropsValues.getUser(), null);
	}

	private FileEntry _getFileEntry(ObjectEntry objectEntry) throws Exception {
		Map<String, Serializable> values = objectEntry.getValues();

		long fileEntryId = (Long)values.get("file");

		return dlAppService.getFileEntry(fileEntryId);
	}

	private ServiceRegistration<ObjectActionExecutor>
		_registerObjectActionExecutor() {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectActionAttachmentDownloadTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			ObjectActionExecutor.class,
			new ObjectActionExecutor() {

				@Override
				public void execute(
					long companyId, long objectActionId,
					UnicodeProperties parametersUnicodeProperties,
					JSONObject payloadJSONObject, long userId) {

					_atomicInteger.incrementAndGet();
				}

				@Override
				public String getKey() {
					return _KEY;
				}

			},
			null);
	}

	private static final String _KEY = RandomTestUtil.randomString();

	private AtomicInteger _atomicInteger;
	private ObjectAction _objectAction;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ObjectField _objectField;
	private ServiceRegistration<ObjectActionExecutor> _serviceRegistration;

}