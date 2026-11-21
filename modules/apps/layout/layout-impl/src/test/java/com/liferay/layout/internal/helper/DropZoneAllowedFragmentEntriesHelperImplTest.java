/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.helper;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class DropZoneAllowedFragmentEntriesHelperImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_dropZoneAllowedFragmentEntriesHelperImpl,
			"_fragmentCollectionContributorRegistry",
			_fragmentCollectionContributorRegistry);
		ReflectionTestUtil.setFieldValue(
			_dropZoneAllowedFragmentEntriesHelperImpl,
			"_fragmentEntryLocalService", _fragmentEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_dropZoneAllowedFragmentEntriesHelperImpl,
			"_fragmentRendererRegistry", _fragmentRendererRegistry);
		ReflectionTestUtil.setFieldValue(
			_dropZoneAllowedFragmentEntriesHelperImpl, "_groupLocalService",
			_groupLocalService);

		Mockito.reset(
			_fragmentCollectionContributorRegistry, _fragmentEntryLocalService,
			_fragmentRendererRegistry, _groupLocalService);

		_designLibraryUtilMockedStatic.reset();
		_scopeUtilMockedStatic.reset();
	}

	@Test
	@TestInfo("LPD-108786")
	public void testGetFragmentEntryKeys() throws Exception {
		_testGetFragmentEntryKeys(
			_getDropZoneLayoutStructureItem(JSONUtil.putAll()),
			Collections.emptyList());
		_testGetFragmentEntryKeys(
			_getDropZoneLayoutStructureItem(null), Collections.emptyList());

		String fragmentEntryRendererKey = RandomTestUtil.randomString();

		_testGetFragmentEntryKeys(
			_getDropZoneLayoutStructureItem(
				JSONUtil.putAll(
					JSONUtil.put(
						"fragmentEntryRendererKey", fragmentEntryRendererKey))),
			Arrays.asList(fragmentEntryRendererKey));

		String fragmentEntryScopeERC = RandomTestUtil.randomString();
		long fragmentEntryScopeGroupId = RandomTestUtil.randomLong();

		FragmentEntry fragmentEntry = _mockFragmentEntryByERC(
			fragmentEntryScopeERC, fragmentEntryScopeGroupId);

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			_getDropZoneLayoutStructureItem(
				JSONUtil.putAll(
					JSONUtil.put(
						"fragmentEntryERC",
						fragmentEntry.getExternalReferenceCode()
					).put(
						"fragmentEntryScopeERC", fragmentEntryScopeERC
					)));

		_testGetFragmentEntryKeys(
			dropZoneLayoutStructureItem,
			Arrays.asList(fragmentEntry.getFragmentEntryKey()));

		_scopeUtilMockedStatic.when(
			() -> ScopeUtil.getItemGroupId(
				_COMPANY_ID, fragmentEntryScopeERC, _SCOPE_GROUP_ID)
		).thenReturn(
			null
		);

		_testGetFragmentEntryKeys(
			dropZoneLayoutStructureItem, Collections.emptyList());

		_scopeUtilMockedStatic.when(
			() -> ScopeUtil.getItemGroupId(
				_COMPANY_ID, fragmentEntryScopeERC, _SCOPE_GROUP_ID)
		).thenReturn(
			fragmentEntryScopeGroupId
		);

		Mockito.when(
			_fragmentEntryLocalService.
				fetchFragmentEntryByExternalReferenceCode(
					fragmentEntry.getExternalReferenceCode(),
					fragmentEntryScopeGroupId)
		).thenReturn(
			null
		);

		_testGetFragmentEntryKeys(
			_getDropZoneLayoutStructureItem(
				JSONUtil.putAll(
					JSONUtil.put(
						"fragmentEntryScopeERC",
						RandomTestUtil.randomString()))),
			Collections.emptyList());
		_testGetFragmentEntryKeys(
			dropZoneLayoutStructureItem, Collections.emptyList());
	}

	@Test
	@TestInfo("LPD-108786")
	public void testIsAllowedFragmentEntryKey() {
		String fragmentEntryRendererKey = RandomTestUtil.randomString();

		_testIsAllowedFragmentEntryKey(
			_getDropZoneLayoutStructureItem(JSONUtil.putAll()), true,
			fragmentEntryRendererKey);
		_testIsAllowedFragmentEntryKey(null, true, fragmentEntryRendererKey);

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			_getDropZoneLayoutStructureItem(
				JSONUtil.putAll(
					JSONUtil.put(
						"fragmentEntryRendererKey", fragmentEntryRendererKey)));

		_testIsAllowedFragmentEntryKey(
			dropZoneLayoutStructureItem, false, fragmentEntryRendererKey);
		_testIsAllowedFragmentEntryKey(
			dropZoneLayoutStructureItem, true, RandomTestUtil.randomString());

		dropZoneLayoutStructureItem = _getDropZoneLayoutStructureItem(
			JSONUtil.putAll());

		dropZoneLayoutStructureItem.setAllowNewFragmentEntries(false);

		_testIsAllowedFragmentEntryKey(
			dropZoneLayoutStructureItem, false, fragmentEntryRendererKey);

		dropZoneLayoutStructureItem = _getDropZoneLayoutStructureItem(
			JSONUtil.putAll(
				JSONUtil.put(
					"fragmentEntryRendererKey", fragmentEntryRendererKey)));

		dropZoneLayoutStructureItem.setAllowNewFragmentEntries(false);

		_testIsAllowedFragmentEntryKey(
			dropZoneLayoutStructureItem, false, RandomTestUtil.randomString());
		_testIsAllowedFragmentEntryKey(
			dropZoneLayoutStructureItem, true, fragmentEntryRendererKey);
	}

	@Test
	@TestInfo("LPD-108786")
	public void testNormalizeItemConfigJSONObject() throws Exception {
		JSONObject itemConfigJSONObject = JSONUtil.put(
			"allowNewFragmentEntries", true);

		Assert.assertSame(
			itemConfigJSONObject,
			_dropZoneAllowedFragmentEntriesHelperImpl.
				normalizeItemConfigJSONObject(
					_COMPANY_ID, itemConfigJSONObject, _SCOPE_GROUP_ID));

		Group companyGroup = _mockCompanyGroup();

		FragmentEntry companyGroupFragmentEntry = _mockFragmentEntry(
			companyGroup.getGroupId());

		String contributedFragmentEntryKey = RandomTestUtil.randomString();

		Mockito.when(
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				contributedFragmentEntryKey)
		).thenReturn(
			Mockito.mock(FragmentEntry.class)
		);

		FragmentEntry fragmentEntry = _mockFragmentEntry(_SCOPE_GROUP_ID);

		String fragmentRendererKey = RandomTestUtil.randomString();

		Mockito.when(
			_fragmentRendererRegistry.getFragmentRenderer(fragmentRendererKey)
		).thenReturn(
			Mockito.mock(FragmentRenderer.class)
		);

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.fetchConnectedDesignLibraryGroupIds(
				_SCOPE_GROUP_ID)
		).thenReturn(
			new long[0]
		);

		_scopeUtilMockedStatic.when(
			() -> ScopeUtil.getItemScopeExternalReferenceCode(
				Mockito.anyLong(), Mockito.anyLong())
		).thenAnswer(
			invocationOnMock -> {
				Long itemGroupId = invocationOnMock.getArgument(0, Long.class);

				if (itemGroupId == companyGroup.getGroupId()) {
					return companyGroup.getExternalReferenceCode();
				}

				return null;
			}
		);

		itemConfigJSONObject = JSONUtil.put(
			"fragmentEntryKeys",
			JSONUtil.putAll(
				contributedFragmentEntryKey,
				companyGroupFragmentEntry.getFragmentEntryKey(),
				fragmentEntry.getFragmentEntryKey(), fragmentRendererKey,
				RandomTestUtil.randomString()));

		JSONObject normalizedItemConfigJSONObject =
			_dropZoneAllowedFragmentEntriesHelperImpl.
				normalizeItemConfigJSONObject(
					_COMPANY_ID, itemConfigJSONObject, _SCOPE_GROUP_ID);

		JSONArray fragmentEntriesJSONArray =
			normalizedItemConfigJSONObject.getJSONArray("fragmentEntries");

		Assert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"fragmentEntryRendererKey", contributedFragmentEntryKey),
				JSONUtil.put(
					"fragmentEntryERC",
					companyGroupFragmentEntry.getExternalReferenceCode()
				).put(
					"fragmentEntryScopeERC",
					companyGroup.getExternalReferenceCode()
				),
				JSONUtil.put(
					"fragmentEntryERC",
					fragmentEntry.getExternalReferenceCode()),
				JSONUtil.put("fragmentEntryRendererKey", fragmentRendererKey)
			).toString(),
			fragmentEntriesJSONArray.toString());

		Assert.assertFalse(
			normalizedItemConfigJSONObject.has("fragmentEntryKeys"));
	}

	private DropZoneLayoutStructureItem _getDropZoneLayoutStructureItem(
		JSONArray fragmentEntriesJSONArray) {

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			new DropZoneLayoutStructureItem(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

		if (fragmentEntriesJSONArray != null) {
			dropZoneLayoutStructureItem.setFragmentEntriesJSONArray(
				fragmentEntriesJSONArray);
		}

		return dropZoneLayoutStructureItem;
	}

	private Group _mockCompanyGroup() throws Exception {
		Group companyGroup = Mockito.mock(Group.class);

		Mockito.when(
			companyGroup.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			companyGroup.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_groupLocalService.getCompanyGroup(_COMPANY_ID)
		).thenReturn(
			companyGroup
		);

		return companyGroup;
	}

	private FragmentEntry _mockFragmentEntry(long groupId) {
		FragmentEntry fragmentEntry = Mockito.mock(FragmentEntry.class);

		Mockito.when(
			fragmentEntry.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			fragmentEntry.getFragmentEntryKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			fragmentEntry.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			_fragmentEntryLocalService.fetchFragmentEntry(
				groupId, fragmentEntry.getFragmentEntryKey())
		).thenReturn(
			fragmentEntry
		);

		return fragmentEntry;
	}

	private FragmentEntry _mockFragmentEntryByERC(
		String fragmentEntryScopeERC, long fragmentEntryScopeGroupId) {

		_scopeUtilMockedStatic.when(
			() -> ScopeUtil.getItemGroupId(
				_COMPANY_ID, fragmentEntryScopeERC, _SCOPE_GROUP_ID)
		).thenReturn(
			fragmentEntryScopeGroupId
		);

		FragmentEntry fragmentEntry = Mockito.mock(FragmentEntry.class);

		Mockito.when(
			fragmentEntry.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			fragmentEntry.getFragmentEntryKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_fragmentEntryLocalService.
				fetchFragmentEntryByExternalReferenceCode(
					fragmentEntry.getExternalReferenceCode(),
					fragmentEntryScopeGroupId)
		).thenReturn(
			fragmentEntry
		);

		return fragmentEntry;
	}

	private void _testGetFragmentEntryKeys(
		DropZoneLayoutStructureItem dropZoneLayoutStructureItem,
		List<String> expectedFragmentEntryKeys) {

		Assert.assertEquals(
			expectedFragmentEntryKeys,
			_dropZoneAllowedFragmentEntriesHelperImpl.getFragmentEntryKeys(
				_COMPANY_ID, dropZoneLayoutStructureItem, _SCOPE_GROUP_ID));
	}

	private void _testIsAllowedFragmentEntryKey(
		DropZoneLayoutStructureItem dropZoneLayoutStructureItem,
		boolean expected, String fragmentEntryKey) {

		Assert.assertEquals(
			expected,
			_dropZoneAllowedFragmentEntriesHelperImpl.isAllowedFragmentEntryKey(
				_COMPANY_ID, dropZoneLayoutStructureItem, fragmentEntryKey,
				_SCOPE_GROUP_ID));
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _SCOPE_GROUP_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private static final MockedStatic<ScopeUtil> _scopeUtilMockedStatic =
		Mockito.mockStatic(ScopeUtil.class);

	private final DropZoneAllowedFragmentEntriesHelperImpl
		_dropZoneAllowedFragmentEntriesHelperImpl =
			new DropZoneAllowedFragmentEntriesHelperImpl();
	private final FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry = Mockito.mock(
			FragmentCollectionContributorRegistry.class);
	private final FragmentEntryLocalService _fragmentEntryLocalService =
		Mockito.mock(FragmentEntryLocalService.class);
	private final FragmentRendererRegistry _fragmentRendererRegistry =
		Mockito.mock(FragmentRendererRegistry.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);

}