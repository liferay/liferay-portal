/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.filter.InfoItemServiceFilter;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Lourdes Fernández Besada
 */
public class ObjectEntryInfoItemObjectProviderTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_objectEntryUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_groupLocalService = Mockito.mock(GroupLocalService.class);

		_group = Mockito.mock(Group.class);

		Mockito.when(
			_groupLocalService.fetchGroup(Mockito.anyLong())
		).thenReturn(
			_group
		);

		_objectDefinition = Mockito.mock(ObjectDefinition.class);
		_objectEntryLocalService = Mockito.mock(ObjectEntryLocalService.class);

		_objectEntryManager = Mockito.mock(ObjectEntryManager.class);

		_objectEntryManagerRegistry = Mockito.mock(
			ObjectEntryManagerRegistry.class);

		Mockito.when(
			_objectEntryManagerRegistry.getObjectEntryManager(null)
		).thenReturn(
			_objectEntryManager
		);

		_objectEntryUtilMockedStatic.reset();

		_userLocalService = Mockito.mock(UserLocalService.class);
	}

	@Test
	public void testGetInfoItemLiferayObjectEntry() throws Exception {
		long classPK = RandomTestUtil.randomLong();
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(classPK)
		).thenReturn(
			objectEntry
		);

		Assert.assertEquals(
			objectEntry,
			_assertGetInfoItem(new ClassPKInfoItemIdentifier(classPK)));

		Mockito.verifyNoInteractions(_objectEntryManagerRegistry);
	}

	@Test
	public void testGetInfoItemLiferayObjectEntryNoSuchInfoItemException() {
		long classPK = RandomTestUtil.randomLong();

		_assertGetInfoItemNoSuchInfoItemException(
			new ClassPKInfoItemIdentifier(classPK),
			"Unable to get object entry " + classPK);

		Mockito.verify(
			_objectEntryLocalService
		).fetchObjectEntry(
			classPK
		);

		Mockito.verifyNoInteractions(_objectEntryManagerRegistry);
	}

	@Test
	public void testGetInfoItemProxyObjectEntry() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		_setUpProxyObjectEntry(externalReferenceCode, objectEntry);

		Assert.assertEquals(
			objectEntry,
			_assertGetInfoItem(
				new ERCInfoItemIdentifier(externalReferenceCode)));

		Mockito.verifyNoInteractions(_objectEntryLocalService);
	}

	@Test
	public void testGetInfoItemProxyObjectEntryInfoItemIdentifierCachedInObjectEntriesAttribute()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ERCInfoItemIdentifier ercInfoItemIdentifier = new ERCInfoItemIdentifier(
			externalReferenceCode);

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		_setUpProxyObjectEntry(externalReferenceCode, objectEntry);

		_assertGetInfoItemProxyObjectEntryCached(
			ercInfoItemIdentifier,
			_getHttpServletRequest(
				HashMapBuilder.<String, Object>put(
					_OBJECT_ENTRIES,
					HashMapBuilder.<InfoItemIdentifier, ObjectEntry>put(
						ercInfoItemIdentifier, objectEntry
					).build()
				).build()),
			objectEntry);
	}

	@Test
	public void testGetInfoItemProxyObjectEntryInfoItemIdentifierNotCachedInObjectEntriesAttribute()
		throws Exception {

		Map<String, Object> attributes = new HashMap<>();
		String externalReferenceCode = RandomTestUtil.randomString();
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		_assertGetInfoItemProxyObjectEntry(
			attributes, new ERCInfoItemIdentifier(externalReferenceCode),
			_getHttpServletRequest(attributes), objectEntry,
			_setUpProxyObjectEntry(externalReferenceCode, objectEntry));
	}

	@Test
	public void testGetInfoItemProxyObjectEntryNoSuchInfoItemException()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_assertGetInfoItemNoSuchInfoItemException(
			new ERCInfoItemIdentifier(externalReferenceCode),
			"Unable to get object entry " + externalReferenceCode);

		Mockito.verifyNoInteractions(_objectEntryLocalService);
		Mockito.verify(
			_objectEntryManager
		).getObjectEntry(
			Mockito.eq(0L), Mockito.any(DefaultDTOConverterContext.class),
			Mockito.eq(externalReferenceCode), Mockito.eq(_objectDefinition),
			Mockito.eq(null)
		);
	}

	@Test
	public void testGetInfoItemProxyObjectEntryNullHttpServletRequest()
		throws Exception {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		String externalReferenceCode = RandomTestUtil.randomString();

		ERCInfoItemIdentifier ercInfoItemIdentifier = new ERCInfoItemIdentifier(
			externalReferenceCode);

		com.liferay.object.rest.dto.v1_0.ObjectEntry proxyObjectEntry =
			_setUpProxyObjectEntry(externalReferenceCode, objectEntry);

		Assert.assertEquals(
			objectEntry, _assertGetInfoItem(ercInfoItemIdentifier));

		Mockito.verifyNoInteractions(_objectEntryLocalService);
		Mockito.verify(
			_objectEntryManager
		).getObjectEntry(
			Mockito.eq(0L), Mockito.any(DefaultDTOConverterContext.class),
			Mockito.eq(ercInfoItemIdentifier.getExternalReferenceCode()),
			Mockito.eq(_objectDefinition), Mockito.eq(null)
		);

		_objectEntryUtilMockedStatic.verify(
			() -> ObjectEntryUtil.toObjectEntry(0L, proxyObjectEntry));
	}

	@Test
	public void testGetInfoItemUnsoportedInfoItemIdentifierNoSuchInfoItemException() {
		InfoItemIdentifier infoItemIdentifier = new InfoItemIdentifier() {

			@Override
			public InfoItemServiceFilter getInfoItemServiceFilter() {
				return null;
			}

			@Override
			public String getVersion() {
				return null;
			}

			@Override
			public void setVersion(String version) {
			}

		};

		_assertGetInfoItemNoSuchInfoItemException(
			infoItemIdentifier,
			"Unsupported info item identifier " + infoItemIdentifier);

		Mockito.verifyNoInteractions(_objectEntryLocalService);
		Mockito.verifyNoInteractions(_objectEntryManagerRegistry);
	}

	private ObjectEntry _assertGetInfoItem(
			InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		return _assertGetInfoItem(infoItemIdentifier, null);
	}

	private ObjectEntry _assertGetInfoItem(
			InfoItemIdentifier infoItemIdentifier,
			HttpServletRequest httpServletRequest)
		throws NoSuchInfoItemException {

		ObjectEntryInfoItemObjectProvider objectEntryInfoItemObjectProvider =
			new ObjectEntryInfoItemObjectProvider(
				_groupLocalService, _objectDefinition, _objectEntryLocalService,
				_objectEntryManagerRegistry, _userLocalService);

		try {
			_pushServiceContext(httpServletRequest);

			return objectEntryInfoItemObjectProvider.getInfoItem(
				infoItemIdentifier);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private void _assertGetInfoItemNoSuchInfoItemException(
		InfoItemIdentifier infoItemIdentifier, String message) {

		try {
			_assertGetInfoItem(infoItemIdentifier);

			Assert.fail();
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			Assert.assertEquals(message, noSuchInfoItemException.getMessage());
		}
	}

	private void _assertGetInfoItemProxyObjectEntry(
			Map<String, Object> attributes,
			ERCInfoItemIdentifier ercInfoItemIdentifier,
			HttpServletRequest httpServletRequest, ObjectEntry objectEntry,
			com.liferay.object.rest.dto.v1_0.ObjectEntry proxyObjectEntry)
		throws Exception {

		Assert.assertEquals(
			objectEntry,
			_assertGetInfoItem(ercInfoItemIdentifier, httpServletRequest));

		Mockito.verifyNoInteractions(_objectEntryLocalService);
		Mockito.verify(
			_objectEntryManager
		).getObjectEntry(
			Mockito.eq(0L), Mockito.any(DefaultDTOConverterContext.class),
			Mockito.eq(ercInfoItemIdentifier.getExternalReferenceCode()),
			Mockito.eq(_objectDefinition), Mockito.eq(null)
		);

		Mockito.verify(
			httpServletRequest
		).getAttribute(
			_OBJECT_ENTRIES
		);

		_objectEntryUtilMockedStatic.verify(
			() -> ObjectEntryUtil.toObjectEntry(0L, proxyObjectEntry));

		_assertObjectEntriesAttribute(
			attributes, ercInfoItemIdentifier, objectEntry);
	}

	private void _assertGetInfoItemProxyObjectEntryCached(
			ERCInfoItemIdentifier ercInfoItemIdentifier,
			HttpServletRequest httpServletRequest, ObjectEntry objectEntry)
		throws Exception {

		Assert.assertEquals(
			objectEntry,
			_assertGetInfoItem(ercInfoItemIdentifier, httpServletRequest));

		Mockito.verifyNoInteractions(_objectEntryLocalService);
		Mockito.verifyNoInteractions(_objectEntryManager);

		_objectEntryUtilMockedStatic.verifyNoInteractions();

		Mockito.verify(
			httpServletRequest
		).getAttribute(
			_OBJECT_ENTRIES
		);
	}

	private void _assertObjectEntriesAttribute(
		Map<String, Object> attributes,
		ERCInfoItemIdentifier infoItemIdentifier, ObjectEntry objectEntry) {

		Object object = attributes.get(_OBJECT_ENTRIES);

		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof Map);

		Map<InfoItemIdentifier, ObjectEntry> objectEntries =
			(Map<InfoItemIdentifier, ObjectEntry>)object;

		Assert.assertEquals(
			MapUtil.toString(objectEntries), 1, objectEntries.size());

		ObjectEntry cachedObjectEntry = objectEntries.get(infoItemIdentifier);

		Assert.assertNotNull(cachedObjectEntry);
		Assert.assertEquals(objectEntry, cachedObjectEntry);
	}

	private HttpServletRequest _getHttpServletRequest(
		Map<String, Object> attributes) {

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(Mockito.anyString())
		).thenAnswer(
			new Answer<Object>() {

				@Override
				public Object answer(InvocationOnMock invocationOnMock)
					throws Throwable {

					return attributes.get(
						invocationOnMock.getArgument(0, String.class));
				}

			}
		);

		Mockito.doAnswer(
			invocation -> {
				attributes.put(
					invocation.getArgument(0, String.class),
					invocation.getArgument(1));

				return null;
			}
		).when(
			httpServletRequest
		).setAttribute(
			Mockito.anyString(), Mockito.any(Object.class)
		);

		return httpServletRequest;
	}

	private void _pushServiceContext(HttpServletRequest httpServletRequest) {
		ServiceContext serviceContext = Mockito.mock(ServiceContext.class);

		Mockito.when(
			serviceContext.getRequest()
		).thenReturn(
			httpServletRequest
		);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	private com.liferay.object.rest.dto.v1_0.ObjectEntry _setUpProxyObjectEntry(
			String externalReferenceCode, ObjectEntry objectEntry)
		throws Exception {

		com.liferay.object.rest.dto.v1_0.ObjectEntry proxyObjectEntry =
			Mockito.mock(com.liferay.object.rest.dto.v1_0.ObjectEntry.class);

		Mockito.when(
			_objectEntryManager.getObjectEntry(
				Mockito.eq(0L), Mockito.any(DefaultDTOConverterContext.class),
				Mockito.eq(externalReferenceCode),
				Mockito.eq(_objectDefinition), Mockito.eq(null))
		).thenReturn(
			proxyObjectEntry
		);

		_objectEntryUtilMockedStatic.when(
			() -> ObjectEntryUtil.toObjectEntry(0L, proxyObjectEntry)
		).thenReturn(
			objectEntry
		);

		return proxyObjectEntry;
	}

	private static final String _OBJECT_ENTRIES =
		ObjectEntryInfoItemObjectProvider.class.getName() + "#OBJECT_ENTRIES";

	private static final MockedStatic<ObjectEntryUtil>
		_objectEntryUtilMockedStatic = Mockito.mockStatic(
			ObjectEntryUtil.class);

	private Group _group;
	private GroupLocalService _groupLocalService;
	private ObjectDefinition _objectDefinition;
	private ObjectEntryLocalService _objectEntryLocalService;
	private ObjectEntryManager _objectEntryManager;
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private UserLocalService _userLocalService;

}