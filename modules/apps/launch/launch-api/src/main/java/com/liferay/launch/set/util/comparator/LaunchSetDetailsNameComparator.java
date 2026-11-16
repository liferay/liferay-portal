/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.set.util.comparator;

import com.liferay.launch.set.details.LaunchSetDetails;

import java.util.Comparator;

/**
 * @author Lourdes Fernández Besada
 */
public class LaunchSetDetailsNameComparator
	implements Comparator<LaunchSetDetails> {

	public static final Comparator<LaunchSetDetails> INSTANCE =
		new LaunchSetDetailsNameComparator();

	@Override
	public int compare(
		LaunchSetDetails launchSetDetails1,
		LaunchSetDetails launchSetDetails2) {

		String name1 = launchSetDetails1.getName();
		String name2 = launchSetDetails2.getName();

		return name1.compareToIgnoreCase(name2);
	}

	private LaunchSetDetailsNameComparator() {
	}

}