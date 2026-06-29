/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.model.listener;

import com.liferay.analytics.settings.rest.dto.v1_0.Channel;
import com.liferay.analytics.settings.rest.dto.v1_0.DataSource;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.resource.v1_0.ChannelResource;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.site.dsr.site.initializer.constants.DSRFolderConstants;
import com.liferay.site.dsr.site.initializer.util.DSRRoomUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Stefano Motta
 */
public class ObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Mockito.when(
			_channelResourceBuilder.build()
		).thenReturn(
			_channelResource
		);

		Mockito.when(
			_channelResourceBuilder.checkPermissions(false)
		).thenReturn(
			_channelResourceBuilder
		);

		Mockito.when(
			_channelResourceBuilder.user(Mockito.any(User.class))
		).thenReturn(
			_channelResourceBuilder
		);

		Mockito.when(
			_channelResourceFactory.create()
		).thenReturn(
			_channelResourceBuilder
		);

		Mockito.when(
			_userLocalService.getUser(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(User.class)
		);
	}

	@Test
	public void testCopyFileEntries() throws Exception {
		Mockito.when(
			_group.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			_dlFolderLocalService.fetchDLFolderByExternalReferenceCode(
				DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
				_GROUP_ID)
		).thenReturn(
			_dlFolder
		);

		long folderId = RandomTestUtil.randomLong();

		Mockito.when(
			_dlFolder.getFolderId()
		).thenReturn(
			folderId
		);

		long fileEntryId1 = RandomTestUtil.randomLong();
		String fileEntryTitle1 = RandomTestUtil.randomString();

		FileEntry fileEntry1 = Mockito.mock(FileEntry.class);

		Mockito.when(
			fileEntry1.getTitle()
		).thenReturn(
			fileEntryTitle1
		);

		Mockito.when(
			_dlAppService.getFileEntry(fileEntryId1)
		).thenReturn(
			fileEntry1
		);

		long fileEntryId2 = RandomTestUtil.randomLong();

		FileEntry fileEntry2 = Mockito.mock(FileEntry.class);

		Mockito.when(
			fileEntry2.getTitle()
		).thenReturn(
			fileEntryTitle1
		);

		Mockito.when(
			_dlAppService.getFileEntry(fileEntryId2)
		).thenReturn(
			fileEntry2
		);

		long fileEntryId3 = RandomTestUtil.randomLong();

		FileEntry fileEntry3 = Mockito.mock(FileEntry.class);

		Mockito.when(
			fileEntry3.getFileEntryId()
		).thenReturn(
			fileEntryId3
		);

		Mockito.when(
			fileEntry3.getTitle()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_dlAppService.getFileEntries(_GROUP_ID, folderId)
		).thenReturn(
			Arrays.asList(fileEntry2, fileEntry3)
		);

		ReflectionTestUtil.invoke(
			_objectEntryModelListener, "_copyFileEntries",
			new Class<?>[] {long[].class, Group.class},
			new long[] {fileEntryId1}, _group);

		Mockito.verify(
			_dlAppService, Mockito.times(1)
		).deleteFileEntry(
			Mockito.anyLong()
		);

		Mockito.verify(
			_dlAppService
		).deleteFileEntry(
			fileEntryId3
		);

		Mockito.verify(
			_dlAppService, Mockito.never()
		).copyFileEntry(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.anyLong(), Mockito.any(), Mockito.any()
		);
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getObjectDefinition()
		).thenReturn(
			_objectDefinition
		);

		Mockito.when(
			_objectDefinition.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		ReflectionTestUtil.invoke(
			_objectEntryModelListener, "_onAfterUpdate",
			new Class<?>[] {ObjectEntry.class, ObjectEntry.class}, objectEntry,
			objectEntry);

		Mockito.verifyNoInteractions(_groupLocalService);

		String className = RandomTestUtil.randomString();
		String friendlyURL = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		long objectEntryId = RandomTestUtil.randomLong();

		objectEntry = _mockRoomObjectEntry(
			className, friendlyURL, name, objectEntryId);

		ReflectionTestUtil.invoke(
			_objectEntryModelListener, "_onAfterUpdate",
			new Class<?>[] {ObjectEntry.class, ObjectEntry.class},
			_mockRoomObjectEntry(className, friendlyURL, name, objectEntryId),
			objectEntry);

		Mockito.verifyNoInteractions(_groupLocalService);

		ObjectEntry originalObjectEntry = _mockRoomObjectEntry(
			className, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), objectEntryId);

		long classNameId = RandomTestUtil.randomLong();

		Mockito.when(
			_classNameLocalService.getClassNameId(className)
		).thenReturn(
			classNameId
		);

		Mockito.when(
			_groupLocalService.fetchGroup(
				_COMPANY_ID, classNameId, objectEntryId)
		).thenReturn(
			_group
		);

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), RandomTestUtil.randomString()
		).build();

		Mockito.when(
			_group.getNameMap()
		).thenReturn(
			nameMap
		);

		Mockito.when(
			_group.getFriendlyURL()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		ReflectionTestUtil.invoke(
			_objectEntryModelListener, "_onAfterUpdate",
			new Class<?>[] {ObjectEntry.class, ObjectEntry.class},
			originalObjectEntry, objectEntry);

		Mockito.verify(
			_groupLocalService, Mockito.times(1)
		).updateGroup(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.same(nameMap),
			Mockito.any(), Mockito.anyInt(), Mockito.any(),
			Mockito.anyBoolean(), Mockito.anyInt(),
			Mockito.eq("/" + friendlyURL), Mockito.anyBoolean(),
			Mockito.anyBoolean(), Mockito.any(ServiceContext.class)
		);

		Assert.assertEquals(name, nameMap.get(LocaleUtil.getDefault()));

		Mockito.when(
			_group.getFriendlyURL()
		).thenReturn(
			"/" + friendlyURL
		);

		ReflectionTestUtil.invoke(
			_objectEntryModelListener, "_onAfterUpdate",
			new Class<?>[] {ObjectEntry.class, ObjectEntry.class},
			originalObjectEntry, objectEntry);

		Mockito.verify(
			_groupLocalService, Mockito.times(1)
		).updateGroup(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.any(), Mockito.any(),
			Mockito.anyInt(), Mockito.any(), Mockito.anyBoolean(),
			Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean(),
			Mockito.anyBoolean(), Mockito.any(ServiceContext.class)
		);
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		ObjectEntry objectEntry1 = _mockRoomObjectEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong());
		ObjectEntry objectEntry2 = _mockRoomObjectEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong());

		try (MockedStatic<DSRRoomUtil> dsrRoomUtilMockedStatic =
				Mockito.mockStatic(DSRRoomUtil.class)) {

			ReflectionTestUtil.invoke(
				_objectEntryModelListener, "_onBeforeUpdate",
				new Class<?>[] {ObjectEntry.class, ObjectEntry.class},
				objectEntry1, objectEntry2);

			dsrRoomUtilMockedStatic.verify(
				() -> DSRRoomUtil.checkPermission(
					objectEntry1, null, ActionKeys.UPDATE));
		}

		String name = RandomTestUtil.randomString();

		ObjectEntry objectEntry3 = _mockRoomObjectEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), name,
			RandomTestUtil.randomLong());

		Mockito.when(
			objectEntry3.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"archiveDate", RandomTestUtil.randomString()
			).put(
				"name", name
			).put(
				"roomStatus", WorkflowConstants.STATUS_APPROVED
			).build()
		);

		ObjectEntry objectEntry4 = _mockRoomObjectEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), name,
			RandomTestUtil.randomLong());

		Mockito.when(
			objectEntry4.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"archiveDate", RandomTestUtil.randomString()
			).put(
				"name", name
			).put(
				"roomStatus", WorkflowConstants.STATUS_INACTIVE
			).build()
		);

		try (MockedStatic<DSRRoomUtil> dsrRoomUtilMockedStatic =
				Mockito.mockStatic(DSRRoomUtil.class)) {

			ReflectionTestUtil.invoke(
				_objectEntryModelListener, "_onBeforeUpdate",
				new Class<?>[] {ObjectEntry.class, ObjectEntry.class},
				objectEntry3, objectEntry4);

			dsrRoomUtilMockedStatic.verifyNoInteractions();
		}
	}

	@Test
	public void testPatchAnalyticsChannel() throws Exception {
		Mockito.when(
			_analyticsSettingsManager.isAnalyticsEnabled(_COMPANY_ID)
		).thenReturn(
			false
		);

		ReflectionTestUtil.invoke(
			_objectEntryModelListener, "_patchAnalyticsChannel",
			new Class<?>[] {long.class, ObjectDefinition.class, long.class},
			_COMPANY_ID, _objectDefinition, _USER_ID);

		Mockito.verifyNoInteractions(_channelResourceFactory);

		Mockito.clearInvocations(_channelResource);

		Mockito.when(
			_analyticsSettingsManager.isAnalyticsEnabled(_COMPANY_ID)
		).thenReturn(
			true
		);

		Mockito.when(
			_channelResource.getChannelsPage(
				Mockito.eq("DSR"), Mockito.any(), Mockito.any())
		).thenReturn(
			Page.of(Collections.emptyList())
		);

		Mockito.when(
			_channelResource.postChannel(Mockito.any(Channel.class))
		).thenReturn(
			new Channel()
		);

		ReflectionTestUtil.invoke(
			_objectEntryModelListener, "_patchAnalyticsChannel",
			new Class<?>[] {long.class, ObjectDefinition.class, long.class},
			_COMPANY_ID, _objectDefinition, _USER_ID);

		ArgumentCaptor<Channel> argumentCaptor = ArgumentCaptor.forClass(
			Channel.class);

		Mockito.verify(
			_channelResource
		).postChannel(
			argumentCaptor.capture()
		);

		Channel channel = argumentCaptor.getValue();

		Assert.assertEquals("DSR", channel.getName());

		Mockito.clearInvocations(_channelResource);

		channel.setChannelId(_CHANNEL_ID);

		Mockito.when(
			_channelResource.getChannelsPage(
				Mockito.eq("DSR"), Mockito.any(), Mockito.any())
		).thenReturn(
			Page.of(Collections.singletonList(channel))
		);

		String className = RandomTestUtil.randomString();

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			className
		);

		Mockito.when(
			_group.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			_groupLocalService.getGroups(_COMPANY_ID, className, 0L)
		).thenReturn(
			Collections.singletonList(_group)
		);

		ReflectionTestUtil.invoke(
			_objectEntryModelListener, "_patchAnalyticsChannel",
			new Class<?>[] {long.class, ObjectDefinition.class, long.class},
			_COMPANY_ID, _objectDefinition, _USER_ID);

		argumentCaptor = ArgumentCaptor.forClass(Channel.class);

		Mockito.verify(
			_channelResource
		).patchChannel(
			argumentCaptor.capture()
		);

		channel = argumentCaptor.getValue();

		Assert.assertEquals(_CHANNEL_ID, channel.getChannelId());

		DataSource[] dataSources = channel.getDataSources();

		Assert.assertEquals(
			Arrays.toString(dataSources), 1, dataSources.length);
		Assert.assertArrayEquals(
			new Long[] {_GROUP_ID}, dataSources[0].getSiteIds());
	}

	private ObjectEntry _mockRoomObjectEntry(
		String className, String friendlyURL, String name, long objectEntryId) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			objectEntry.getObjectEntryId()
		).thenReturn(
			objectEntryId
		);

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			className
		);

		Mockito.when(
			_objectDefinition.getExternalReferenceCode()
		).thenReturn(
			"L_DSR_ROOM"
		);

		Mockito.when(
			objectEntry.getObjectDefinition()
		).thenReturn(
			_objectDefinition
		);

		Mockito.when(
			objectEntry.getUserId()
		).thenReturn(
			_USER_ID
		);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"friendlyURL", friendlyURL
			).put(
				"name", name
			).build()
		);

		return objectEntry;
	}

	private static final String _CHANNEL_ID = RandomTestUtil.randomString();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private final AnalyticsSettingsManager _analyticsSettingsManager =
		Mockito.mock(AnalyticsSettingsManager.class);
	private final ChannelResource _channelResource = Mockito.mock(
		ChannelResource.class);
	private final ChannelResource.Builder _channelResourceBuilder =
		Mockito.mock(ChannelResource.Builder.class);
	private final ChannelResource.Factory _channelResourceFactory =
		Mockito.mock(ChannelResource.Factory.class);
	private final ClassNameLocalService _classNameLocalService = Mockito.mock(
		ClassNameLocalService.class);
	private final DLAppService _dlAppService = Mockito.mock(DLAppService.class);
	private final DLFolder _dlFolder = Mockito.mock(DLFolder.class);
	private final DLFolderLocalService _dlFolderLocalService = Mockito.mock(
		DLFolderLocalService.class);
	private final Group _group = Mockito.mock(Group.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);

	@InjectMocks
	private ObjectEntryModelListener _objectEntryModelListener;

	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}