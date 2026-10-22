/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.internal.increment;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.increment.Increment;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;

import java.util.Arrays;

/**
 * @author Zsolt Berentey
 */
public class BufferedIncreasableEntry<K, T>
	extends IncreasableEntry<K, Increment<T>> {

	public BufferedIncreasableEntry(
		AopMethodInvocation aopMethodInvocation, Object[] arguments, K key,
		Increment<T> value) {

		super(key, value);

		_aopMethodInvocation = aopMethodInvocation;
		_arguments = arguments;

		_companyId = CompanyThreadLocal.getCompanyId();
		_ctCollectionId = CTCollectionThreadLocal.getCTCollectionId();
	}

	@Override
	public BufferedIncreasableEntry<K, T> increase(Increment<T> deltaValue) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_companyId, _ctCollectionId)) {

			return new BufferedIncreasableEntry<>(
				_aopMethodInvocation, _arguments, key,
				value.increaseForNew(deltaValue.getValue()));
		}
	}

	public void proceed() throws Throwable {
		if ((_companyId != CompanyConstants.SYSTEM) &&
			(CompanyLocalServiceUtil.fetchCompany(_companyId) == null)) {

			return;
		}

		_arguments[_arguments.length - 1] = getValue().getValue();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_companyId, _ctCollectionId)) {

			_aopMethodInvocation.proceed(_arguments);
		}
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			_aopMethodInvocation, StringPool.OPEN_PARENTHESIS,
			Arrays.toString(_arguments), StringPool.CLOSE_PARENTHESIS);
	}

	private final AopMethodInvocation _aopMethodInvocation;
	private final Object[] _arguments;
	private final long _companyId;
	private final long _ctCollectionId;

}