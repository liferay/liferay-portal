/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.model.impl.ClassNameImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class MVCCModelTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testScratchClone() {
		ClassName className = new ClassNameImpl();

		long classNameId = RandomTestUtil.randomLong();

		className.setClassNameId(classNameId);

		long mvccVersion = RandomTestUtil.randomLong();

		className.setMvccVersion(mvccVersion);

		String value = RandomTestUtil.randomString();

		className.setValue(value);

		ClassName scratchClassName = MVCCModel.scratchClone(className);

		Assert.assertNotSame(className, scratchClassName);
		Assert.assertEquals(classNameId, scratchClassName.getClassNameId());
		Assert.assertEquals(mvccVersion - 1, scratchClassName.getMvccVersion());
		Assert.assertEquals(value, scratchClassName.getValue());

		Assert.assertEquals(mvccVersion, className.getMvccVersion());
	}

}