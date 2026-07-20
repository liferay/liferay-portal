/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.object.action.executor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.seo.studio.web.internal.test.BaseTestCase;

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
	extends BaseTestCase {

	@Test
	public void testExecute() throws Exception {
		seoStudioDomainObjectEntry = addSEOStudioDomainObjectEntry(
			false, RandomTestUtil.randomString(), null);

		_updateSEOStudioDomainObjectEntry(true);

		Map<String, Serializable> values = objectEntryLocalService.getValues(
			seoStudioDomainObjectEntry.getObjectEntryId());

		Date nextScanDate = (Date)values.get("nextScanDate");

		Assert.assertTrue(nextScanDate.after(new Date()));

		ObjectActionThreadLocal.clearObjectEntryIdsMap();

		_updateSEOStudioDomainObjectEntry(false);

		values = objectEntryLocalService.getValues(
			seoStudioDomainObjectEntry.getObjectEntryId());

		Assert.assertNull(values.get("nextScanDate"));
	}

	private void _updateSEOStudioDomainObjectEntry(boolean autoScanEnabled)
		throws Exception {

		partialUpdateObjectEntry(
			seoStudioDomainObjectEntry,
			HashMapBuilder.<String, Serializable>put(
				"autoScanEnabled", autoScanEnabled
			).build());
	}

}