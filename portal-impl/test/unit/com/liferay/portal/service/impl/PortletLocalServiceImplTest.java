/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.internal.cache.DummyPortalCache;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.BaseFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletFriendlyURLMapperMatch;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class PortletLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetPortletFriendlyURLMapperMatch() {
		String mapping = RandomTestUtil.randomString();

		Portlet portlet = new PortletImpl() {

			@Override
			public FriendlyURLMapper getFriendlyURLMapperInstance() {
				return new BaseFriendlyURLMapper() {

					@Override
					public String buildPath(
						LiferayPortletURL liferayPortletURL) {

						return null;
					}

					@Override
					public String getMapping() {
						return mapping;
					}

					@Override
					public boolean isCheckMappingWithPrefix() {
						return false;
					}

					@Override
					public void populateParams(
						String friendlyURLPath,
						Map<String, String[]> parameterMap,
						Map<String, Object> requestContext) {
					}

				};
			}

		};

		AtomicInteger atomicInteger = new AtomicInteger();

		PortletLocalServiceImpl portletLocalServiceImpl =
			new PortletLocalServiceImpl() {

				@Override
				public List<Portlet> getFriendlyURLMapperPortlets() {
					atomicInteger.incrementAndGet();

					return List.of(portlet);
				}

			};

		Map<String, PortletFriendlyURLMapperMatch> cacheMap = new HashMap<>();

		PortalCache<String, PortletFriendlyURLMapperMatch> portalCache =
			new DummyPortalCache<>(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()) {

				@Override
				public PortletFriendlyURLMapperMatch get(String key) {
					return cacheMap.get(key);
				}

				@Override
				public void put(
					String key, PortletFriendlyURLMapperMatch value) {

					cacheMap.put(key, value);
				}

			};

		ReflectionTestUtil.setFieldValue(
			portletLocalServiceImpl,
			"_portletFriendlyURLMapperMatchPortalCache", portalCache);

		String url = "/web/guest/" + mapping;

		PortletFriendlyURLMapperMatch portletFriendlyURLMapperMatch =
			portletLocalServiceImpl.getPortletFriendlyURLMapperMatch(url);

		Assert.assertSame(portletFriendlyURLMapperMatch, cacheMap.get(url));

		Assert.assertEquals(cacheMap.toString(), 1, cacheMap.size());

		Assert.assertSame(
			portletFriendlyURLMapperMatch,
			portletLocalServiceImpl.getPortletFriendlyURLMapperMatch(url));

		Assert.assertEquals(1, atomicInteger.get());
	}

}