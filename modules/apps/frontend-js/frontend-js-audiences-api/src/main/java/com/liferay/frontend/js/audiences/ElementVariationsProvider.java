/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.audiences;

/**
 * @author Iván Zaera Avellón
 */
public interface ElementVariationsProvider {

	public ElementVariations getElementVariations(
		long plid, long segmentsExperienceId);

}