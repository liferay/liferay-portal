/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.util.comparator;

import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectEntryVersionWrapper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ObjectEntryVersionVersionComparatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCompare() {
		ObjectEntryVersion newerObjectEntryVersion = _createObjectEntryVersion(
			1000, 2);
		ObjectEntryVersion olderObjectEntryVersion = _createObjectEntryVersion(
			2000, 1);

		ObjectEntryVersionVersionComparator
			ascendingObjectEntryVersionVersionComparator =
				ObjectEntryVersionVersionComparator.getInstance(true);

		Assert.assertEquals(
			-1,
			Integer.signum(
				ascendingObjectEntryVersionVersionComparator.compare(
					olderObjectEntryVersion, newerObjectEntryVersion)));
		Assert.assertEquals(
			1,
			Integer.signum(
				ascendingObjectEntryVersionVersionComparator.compare(
					newerObjectEntryVersion, olderObjectEntryVersion)));

		ObjectEntryVersionVersionComparator
			descendingObjectEntryVersionVersionComparator =
				ObjectEntryVersionVersionComparator.getInstance(false);

		Assert.assertEquals(
			1,
			Integer.signum(
				descendingObjectEntryVersionVersionComparator.compare(
					olderObjectEntryVersion, newerObjectEntryVersion)));
		Assert.assertEquals(
			-1,
			Integer.signum(
				descendingObjectEntryVersionVersionComparator.compare(
					newerObjectEntryVersion, olderObjectEntryVersion)));
	}

	private ObjectEntryVersion _createObjectEntryVersion(
		long createTime, int version) {

		return new ObjectEntryVersionWrapper(null) {

			@Override
			public Date getCreateDate() {
				return new Date(createTime);
			}

			@Override
			public int getVersion() {
				return version;
			}

		};
	}

}