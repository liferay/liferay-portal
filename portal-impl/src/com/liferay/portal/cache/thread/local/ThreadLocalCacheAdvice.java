/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.thread.local;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCache;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.preview.PreviewableResolverUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

/**
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 */
public class ThreadLocalCacheAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Object target, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		return annotations.get(ThreadLocalCachable.class);
	}

	@Override
	public Object invoke(
			AopMethodInvocation aopMethodInvocation, Object[] arguments)
		throws Throwable {

		ThreadLocalCachable threadLocalCachable =
			aopMethodInvocation.getAdviceMethodContext();

		ThreadLocalCache<Object> threadLocalCache =
			ThreadLocalCacheManager.getThreadLocalCache(
				threadLocalCachable.scope(), aopMethodInvocation.getMethod());

		String cacheKey = _getCacheKey(arguments);

		Object value = threadLocalCache.get(cacheKey);

		if (value != null) {
			if (value == nullResult) {
				return null;
			}

			return value;
		}

		Object result = aopMethodInvocation.proceed(arguments);

		if (result == null) {
			threadLocalCache.put(cacheKey, nullResult);
		}
		else {
			threadLocalCache.put(cacheKey, result);
		}

		return result;
	}

	private String _getCacheKey(Object[] arguments) {
		Long previewId = PreviewableResolverUtil.getPreviewId();

		if ((previewId == null) && (arguments.length == 1)) {
			return StringUtil.toHexString(arguments[0]);
		}

		StringBundler sb = new StringBundler(
			(previewId == null) ? (arguments.length * 2) - 1 :
				(arguments.length * 2) + 1);

		if (previewId != null) {
			sb.append(StringUtil.toHexString(previewId));
			sb.append(StringPool.POUND);
		}

		for (int i = 0; i < arguments.length; i++) {
			sb.append(StringUtil.toHexString(arguments[i]));

			if ((i + 1) < arguments.length) {
				sb.append(StringPool.POUND);
			}
		}

		return sb.toString();
	}

}