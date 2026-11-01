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
public class ObjectEntryVersionCreateDateComparatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCompare() {
		ObjectEntryVersion newerObjectEntryVersion = _createObjectEntryVersion(
			2000);
		ObjectEntryVersion olderObjectEntryVersion = _createObjectEntryVersion(
			1000);

		ObjectEntryVersionCreateDateComparator
			ascendingObjectEntryVersionCreateDateComparator =
				ObjectEntryVersionCreateDateComparator.getInstance(true);

		Assert.assertEquals(
			-1,
			Integer.signum(
				ascendingObjectEntryVersionCreateDateComparator.compare(
					olderObjectEntryVersion, newerObjectEntryVersion)));
		Assert.assertEquals(
			1,
			Integer.signum(
				ascendingObjectEntryVersionCreateDateComparator.compare(
					newerObjectEntryVersion, olderObjectEntryVersion)));

		ObjectEntryVersionCreateDateComparator
			descendingObjectEntryVersionCreateDateComparator =
				ObjectEntryVersionCreateDateComparator.getInstance(false);

		Assert.assertEquals(
			1,
			Integer.signum(
				descendingObjectEntryVersionCreateDateComparator.compare(
					olderObjectEntryVersion, newerObjectEntryVersion)));
		Assert.assertEquals(
			-1,
			Integer.signum(
				descendingObjectEntryVersionCreateDateComparator.compare(
					newerObjectEntryVersion, olderObjectEntryVersion)));
	}

	private ObjectEntryVersion _createObjectEntryVersion(long time) {
		return new ObjectEntryVersionWrapper(null) {

			@Override
			public Date getCreateDate() {
				return new Date(time);
			}

		};
	}

}