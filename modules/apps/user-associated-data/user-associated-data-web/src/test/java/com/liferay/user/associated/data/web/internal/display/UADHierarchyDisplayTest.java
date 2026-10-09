/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.display;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.display.UADHierarchyDeclaration;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class UADHierarchyDisplayTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		DummyEntryUADDisplay dummyEntryUADDisplay = new DummyEntryUADDisplay(
			_dummyEntryService);
		DummyContainerUADDisplay dummyContainerUADDisplay =
			new DummyContainerUADDisplay(_dummyContainerService);

		UADHierarchyDeclaration uadHierarchyDeclaration =
			new DummyUADHierarchyDeclaration(
				dummyEntryUADDisplay, dummyContainerUADDisplay);

		_uadHierarchyDisplay = new UADHierarchyDisplay(
			uadHierarchyDeclaration,
			new DummyUADRegistry(dummyEntryUADDisplay));

		_folderA = _dummyContainerService.create("dummyContainerA", _USER_ID);

		_dummyEntryService.create(
			"dummyEntryAA", _USER_ID_OTHER, _folderA.getId());
		_dummyEntryService.create("dummyEntryAB", _USER_ID, _folderA.getId());

		DummyContainer folderAA = _dummyContainerService.create(
			"dummyContainerAA", _USER_ID_OTHER, _folderA.getId());
		DummyContainer folderAB = _dummyContainerService.create(
			"dummyContainerAB", _USER_ID, _folderA.getId());

		_dummyEntryService.create(
			"dummyEntryAAA", _USER_ID_OTHER, folderAA.getId());
		_dummyEntryService.create(
			"dummyEntryABA", _USER_ID_OTHER, folderAB.getId());
		_dummyEntryService.create("dummyEntryAAB", _USER_ID, folderAA.getId());
		_dummyEntryService.create("dummyEntryABB", _USER_ID, folderAB.getId());

		_userFolderAndItemCountMap.put(_folderA.getId(), 3);
		_userFolderAndItemCountMap.put(folderAA.getId(), 1);
		_userFolderAndItemCountMap.put(folderAB.getId(), 1);

		DummyContainer folderB = _dummyContainerService.create(
			"dummyContainerB", _USER_ID);

		_dummyEntryService.create(
			"dummyEntryBA", _USER_ID_OTHER, folderB.getId());
		_dummyEntryService.create("dummyEntryBB", _USER_ID, folderB.getId());
		_dummyEntryService.create("dummyEntryBC", _USER_ID, folderB.getId());

		DummyContainer folderBA = _dummyContainerService.create(
			"dummyContainerBA", _USER_ID_OTHER, folderB.getId());
		DummyContainer folderBB = _dummyContainerService.create(
			"dummyContainerBA", _USER_ID, folderB.getId());

		_dummyEntryService.create(
			"dummyContainerBAA", _USER_ID_OTHER, folderBA.getId());
		_dummyEntryService.create(
			"dummyContainerBAB", _USER_ID, folderBA.getId());

		_userFolderAndItemCountMap.put(folderB.getId(), 4);
		_userFolderAndItemCountMap.put(folderBA.getId(), 1);
		_userFolderAndItemCountMap.put(folderBB.getId(), 0);

		DummyContainer folderC = _dummyContainerService.create(
			"dummyContainerC", _USER_ID_OTHER);

		_userFolderAndItemCountMap.put(folderC.getId(), 0);

		_dummyEntryService.create(
			"rootEntry", _USER_ID, DummyService.DEFAULT_CONTAINER_ID);

		_userFolderAndItemCountMap.put(DummyService.DEFAULT_CONTAINER_ID, 3);
	}

	@Test
	public void testCountAll() {
		Assert.assertEquals(11, _uadHierarchyDisplay.countAll(_USER_ID));
		Assert.assertEquals(8, _uadHierarchyDisplay.countAll(_USER_ID_OTHER));
	}

	@Test
	public void testFieldValueCount() throws Exception {
		List<Object> items = _uadHierarchyDisplay.search(
			DummyContainer.class.getName(), DummyService.DEFAULT_CONTAINER_ID,
			_USER_ID, null, "", null, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		for (Object item : items) {
			Map<String, Object> fieldValues =
				_uadHierarchyDisplay.getFieldValues(
					item, LocaleUtil.getDefault());

			String uuid = (String)fieldValues.get("uuid");

			Assert.assertNotNull(uuid);

			if (!StringUtil.equals(uuid, _folderA.getUuid())) {
				continue;
			}

			Long count = (Long)fieldValues.get("count");

			Assert.assertEquals(4, count.intValue());
		}
	}

	@Test
	public void testSearch() throws Exception {
		for (DummyContainer dummyContainer :
				_dummyContainerService.getEntities()) {

			List<Object> items = _uadHierarchyDisplay.search(
				DummyContainer.class.getName(), dummyContainer.getId(),
				_USER_ID, null, "", "name", "asc", QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

			Assert.assertEquals(
				items.toString(),
				(int)_userFolderAndItemCountMap.get(dummyContainer.getId()),
				items.size());
		}
	}

	private static final long _USER_ID = 100;

	private static final long _USER_ID_OTHER = 200;

	private final DummyService<DummyContainer> _dummyContainerService =
		new DummyService<>(200, DummyContainer::new);
	private final DummyService<DummyEntry> _dummyEntryService =
		new DummyService<>(100, DummyEntry::new);
	private DummyContainer _folderA;
	private UADHierarchyDisplay _uadHierarchyDisplay;
	private final Map<Long, Integer> _userFolderAndItemCountMap =
		new HashMap<>();

	private class DummyUADRegistry extends UADRegistry {

		public DummyUADRegistry(UADDisplay<?> uadDisplay) {
			_uadDisplay = uadDisplay;
		}

		@Override
		public UADDisplay<?> getUADDisplay(String key) {
			return _uadDisplay;
		}

		@Override
		public UADDisplay<?> getUADDisplayByObject(Object object) {
			return _uadDisplay;
		}

		@Override
		public Collection<UADDisplay<?>> getUADDisplays() {
			return Collections.singletonList(_uadDisplay);
		}

		private final UADDisplay<?> _uadDisplay;

	}

}