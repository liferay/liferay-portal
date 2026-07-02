/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jonathan McCann
 */
@FeatureFlag("LPD-44511")
@RunWith(Arquillian.class)
public class ComputeSEOStudioDomainNextScanDateObjectActionExecutorTest
	extends BaseObjectActionExecutorTestCase {

	@Test
	public void testExecute() throws Exception {
		seoStudioDomainObjectEntry = _addSEOStudioDomainObjectEntry();

		_updateSEOStudioDomainObjectEntry(true);

		Map<String, Serializable> values = objectEntryLocalService.getValues(
			seoStudioDomainObjectEntry.getObjectEntryId());

		Date nextScanDate = (Date)values.get("nextScanDate");

		Assert.assertTrue(nextScanDate.after(new Date()));
	}

	@Test
	public void testExecuteWithAutoScanDisabled() throws Exception {
		seoStudioDomainObjectEntry = _addSEOStudioDomainObjectEntry();

		_updateSEOStudioDomainObjectEntry(false);

		Map<String, Serializable> values = objectEntryLocalService.getValues(
			seoStudioDomainObjectEntry.getObjectEntryId());

		Assert.assertNull(values.get("nextScanDate"));
	}

	private ObjectEntry _addSEOStudioDomainObjectEntry() throws Exception {
		return addObjectEntry(
			seoStudioDomainObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"hostname", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioDomains_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioInstanceToSEOStudioDomains_seoStudioInstanceId",
				seoStudioInstanceObjectEntry.getObjectEntryId()
			).build());
	}

	private void _updateSEOStudioDomainObjectEntry(boolean autoScanEnabled)
		throws Exception {

		objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(),
			seoStudioDomainObjectEntry.getObjectEntryId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"autoScanEnabled", autoScanEnabled
			).put(
				"scanFrequency", "daily"
			).put(
				"scanTime", "09:00"
			).build(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

}