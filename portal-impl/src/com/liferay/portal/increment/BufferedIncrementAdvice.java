/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.increment;

import com.liferay.portal.internal.increment.BufferedIncreasableEntry;
import com.liferay.portal.internal.increment.BufferedIncrementProcessor;
import com.liferay.portal.internal.increment.BufferedIncrementProcessorUtil;
import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.increment.BufferedIncrement;
import com.liferay.portal.kernel.increment.Increment;
import com.liferay.portal.kernel.increment.IncrementFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

/**
 * @author Zsolt Berentey
 * @author Shuyang Zhou
 */
public class BufferedIncrementAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Object target, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		BufferedIncrement bufferedIncrement =
			(BufferedIncrement)annotations.get(BufferedIncrement.class);

		if (bufferedIncrement == null) {
			return null;
		}

		BufferedIncrementProcessor bufferedIncrementProcessor =
			BufferedIncrementProcessorUtil.getBufferedIncrementProcessor(
				bufferedIncrement.configuration());

		return new BufferedIncrementContext(
			bufferedIncrementProcessor, bufferedIncrement.incrementClass());
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Object before(
		AopMethodInvocation aopMethodInvocation, Object[] arguments) {

		BufferedIncrementContext bufferedIncrementContext =
			aopMethodInvocation.getAdviceMethodContext();

		BufferedIncrementProcessor bufferedIncrementProcessor =
			bufferedIncrementContext._bufferedIncrementProcessor;
		Class<? extends Increment<?>> incrementClass =
			bufferedIncrementContext._incrementClass;

		Object value = arguments[arguments.length - 1];

		CacheKeyGenerator cacheKeyGenerator =
			CacheKeyGeneratorUtil.getCacheKeyGenerator(
				BufferedIncrementAdvice.class.getName());

		for (int i = 0; i < (arguments.length - 1); i++) {
			cacheKeyGenerator.append(StringUtil.toHexString(arguments[i]));
		}

		Serializable batchKey = cacheKeyGenerator.finish();

		try {
			Increment<?> increment = IncrementFactory.createIncrement(
				incrementClass, value);

			BufferedIncreasableEntry bufferedIncreasableEntry =
				new BufferedIncreasableEntry(
					aopMethodInvocation, arguments, batchKey, increment);

			TransactionCallbackUtil.registerCommitCallback(
				() -> {
					bufferedIncrementProcessor.process(
						bufferedIncreasableEntry);

					return null;
				});
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to increment", exception);
			}
		}

		return nullResult;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BufferedIncrementAdvice.class);

	private static class BufferedIncrementContext {

		private BufferedIncrementContext(
			BufferedIncrementProcessor bufferedIncrementProcessor,
			Class<? extends Increment<?>> incrementClass) {

			_bufferedIncrementProcessor = bufferedIncrementProcessor;
			_incrementClass = incrementClass;
		}

		private final BufferedIncrementProcessor _bufferedIncrementProcessor;
		private final Class<? extends Increment<?>> _incrementClass;

	}

}