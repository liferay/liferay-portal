/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Kenji Heigel
 */
public interface WeightedItem {

	public long getOverheadWeight();

	public long getSharedWeight();

	public String getSharedWeightName();

	public long getWeight();

}