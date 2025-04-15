/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.util;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Miguel Pastor
 * @author Carlos Sierra Andrés
 */
public class CacheFileNameGenerator {

	public static String getCacheFileName(
		HttpServletRequest httpServletRequest, String cacheName) {

		CacheKeyGenerator cacheKeyGenerator =
			CacheKeyGeneratorUtil.getCacheKeyGenerator(cacheName);

		cacheKeyGenerator.append(
			HttpComponentsUtil.getProtocol(httpServletRequest.isSecure()));
		cacheKeyGenerator.append(StringPool.UNDERLINE);
		cacheKeyGenerator.append(httpServletRequest.getRequestURI());

		StringBundler queryStringSB = new StringBundler(
			_cacheFileNameContributors.size() * 4);

		for (CacheFileNameContributor cacheFileNameContributor :
				_cacheFileNameContributors) {

			String value = cacheFileNameContributor.getParameterValue(
				httpServletRequest);

			if (value == null) {
				continue;
			}

			queryStringSB.append(StringPool.UNDERLINE);
			queryStringSB.append(cacheFileNameContributor.getParameterName());
			queryStringSB.append(StringPool.UNDERLINE);
			queryStringSB.append(value);
		}

		cacheKeyGenerator.append(
			DigesterUtil.digestBase64(
				Digester.SHA_256, queryStringSB.toString()));

		return _sterilizeFileName(String.valueOf(cacheKeyGenerator.finish()));
	}

	private static String _sterilizeFileName(String fileName) {
		return StringUtil.replace(
			fileName,
			new char[] {
				CharPool.SLASH, CharPool.BACK_SLASH, CharPool.PLUS,
				CharPool.EQUAL
			},
			new char[] {
				CharPool.UNDERLINE, CharPool.UNDERLINE, CharPool.DASH,
				CharPool.UNDERLINE
			});
	}

	private static final ServiceTrackerList<CacheFileNameContributor>
		_cacheFileNameContributors = ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(),
			CacheFileNameContributor.class);

}