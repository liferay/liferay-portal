/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.test.util;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public abstract class BaseTableReferenceDefinitionTestCase {

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDiscardCTEntry() throws Exception {
		_ctCollection = _ctCollectionService.addCTCollection(
			null, 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			CTModel<?> ctModel = addCTModel();

			int count = _ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection.getCtCollectionId());

			Assert.assertTrue(count > 0);

			long modelClassNameId = _classNameLocalService.getClassNameId(
				ctModel.getModelClass());

			CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(
				_ctCollection.getCtCollectionId(), modelClassNameId,
				ctModel.getPrimaryKey());

			Assert.assertNotNull(ctEntry);

			Map<Long, List<CTEntry>> discardCTEntries =
				_ctCollectionLocalService.getRelatedCTEntriesMap(
					_ctCollection.getCtCollectionId(), modelClassNameId,
					ctModel.getPrimaryKey());

			List<CTEntry> ctEntries = discardCTEntries.get(modelClassNameId);

			Assert.assertTrue(
				ctEntries.toString(), ctEntries.contains(ctEntry));

			_ctCollectionService.discardCTEntry(
				_ctCollection.getCtCollectionId(),
				_classNameLocalService.getClassNameId(ctModel.getModelClass()),
				ctModel.getPrimaryKey());

			count = _ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection.getCtCollectionId());

			Assert.assertEquals(0, count);
		}
	}

	protected abstract CTModel<?> addCTModel() throws Exception;

	@DeleteAfterTestRun
	protected Group group;

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTCollectionService _ctCollectionService;

	@Inject
	private static CTEntryLocalService _ctEntryLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

}