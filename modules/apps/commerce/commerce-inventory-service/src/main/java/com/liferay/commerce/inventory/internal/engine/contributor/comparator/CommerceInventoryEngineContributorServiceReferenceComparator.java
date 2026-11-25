/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.internal.engine.contributor.comparator;

import com.liferay.commerce.inventory.engine.contributor.CommerceInventoryEngineContributor;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.Serializable;

import java.util.Comparator;

import org.osgi.framework.ServiceReference;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceInventoryEngineContributorServiceReferenceComparator
	implements Comparator<ServiceReference<CommerceInventoryEngineContributor>>,
			   Serializable {

	public CommerceInventoryEngineContributorServiceReferenceComparator() {
		this(true);
	}

	public CommerceInventoryEngineContributorServiceReferenceComparator(
		boolean ascending) {

		_ascending = ascending;
	}

	@Override
	public int compare(
		ServiceReference<CommerceInventoryEngineContributor> serviceReference1,
		ServiceReference<CommerceInventoryEngineContributor>
			serviceReference2) {

		int displayOrder1 = GetterUtil.getInteger(
			serviceReference1.getProperty(
				"commerce.inventory.engine.contributor.order"),
			Integer.MAX_VALUE);
		int displayOrder2 = GetterUtil.getInteger(
			serviceReference2.getProperty(
				"commerce.inventory.engine.contributor.order"),
			Integer.MAX_VALUE);

		int value = Integer.compare(displayOrder1, displayOrder2);

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