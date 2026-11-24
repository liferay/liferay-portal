/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.internal.validator.comparator;

import com.liferay.commerce.discount.validator.CommerceDiscountValidator;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.Serializable;

import java.util.Comparator;

import org.osgi.framework.ServiceReference;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceDiscountValidatorServiceReferenceComparator
	implements Comparator<ServiceReference<CommerceDiscountValidator>>,
			   Serializable {

	public CommerceDiscountValidatorServiceReferenceComparator() {
		this(true);
	}

	public CommerceDiscountValidatorServiceReferenceComparator(
		boolean ascending) {

		_ascending = ascending;
	}

	@Override
	public int compare(
		ServiceReference<CommerceDiscountValidator> serviceReference1,
		ServiceReference<CommerceDiscountValidator> serviceReference2) {

		int priority1 = GetterUtil.getInteger(
			serviceReference1.getProperty(
				"commerce.discount.validator.priority"),
			Integer.MAX_VALUE);
		int priority2 = GetterUtil.getInteger(
			serviceReference2.getProperty(
				"commerce.discount.validator.priority"),
			Integer.MAX_VALUE);

		int value = Integer.compare(priority1, priority2);

		if (_ascending) {
			return value;
		}

		return Math.negateExact(value);
	}

	public boolean isAscending() {
		return _ascending;
	}

	private final boolean _ascending;

}