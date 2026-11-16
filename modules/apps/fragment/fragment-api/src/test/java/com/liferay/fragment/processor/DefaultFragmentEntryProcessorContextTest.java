/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.processor;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class DefaultFragmentEntryProcessorContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PortalUtil portalUtil = new PortalUtil();

		AtomicInteger atomicInteger = new AtomicInteger();

		portalUtil.setPortal(
			(Portal)ProxyUtil.newProxyInstance(
				Portal.class.getClassLoader(), new Class<?>[] {Portal.class},
				(proxy, method, arguments) -> {
					if ((arguments == null) &&
						Objects.equals(
							method.getName(), "getUniqueElementId")) {

						return String.valueOf(atomicInteger.incrementAndGet());
					}

					throw new UnsupportedOperationException(method.getName());
				}));
	}

	@Test
	public void testDefaults() {
		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				_createDefaultFragmentEntryProcessorContext();

		Assert.assertNull(
			defaultFragmentEntryProcessorContext.getAttribute("name"));

		Map<String, Serializable> attributes =
			defaultFragmentEntryProcessorContext.getAttributes();

		attributes.put("name", "value");

		Assert.assertSame(
			attributes, defaultFragmentEntryProcessorContext.getAttributes());

		Assert.assertEquals(
			"value", defaultFragmentEntryProcessorContext.getAttribute("name"));

		String fragmentElementId =
			defaultFragmentEntryProcessorContext.getFragmentElementId();

		Assert.assertTrue(fragmentElementId.startsWith("fragment-"));
		Assert.assertEquals(
			fragmentElementId,
			defaultFragmentEntryProcessorContext.getFragmentElementId());

		DefaultFragmentEntryProcessorContext
			otherDefaultFragmentEntryProcessorContext =
				_createDefaultFragmentEntryProcessorContext();

		Assert.assertNotEquals(
			fragmentElementId,
			otherDefaultFragmentEntryProcessorContext.getFragmentElementId());

		Assert.assertArrayEquals(
			new long[0],
			defaultFragmentEntryProcessorContext.getSegmentsEntryIds());
	}

	private DefaultFragmentEntryProcessorContext
		_createDefaultFragmentEntryProcessorContext() {

		return new DefaultFragmentEntryProcessorContext(
			RandomTestUtil.randomLong(), null, null, LocaleUtil.US,
			FragmentEntryLinkConstants.VIEW, RandomTestUtil.randomLong());
	}

}