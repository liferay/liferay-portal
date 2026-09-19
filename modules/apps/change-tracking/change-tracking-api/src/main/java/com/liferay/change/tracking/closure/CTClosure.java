/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.closure;

import java.util.List;
import java.util.Map;

/**
 * @author Preston Crary
 */
public interface CTClosure {

	public long getCTCollectionId();

	public Map<Long, List<Long>> getChildPKsMap(long classNameId, long classPK);

	public Map<Long, List<Long>> getParentPKsMap(
		long classNameId, long classPK);

	public Map<Long, List<Long>> getRootPKsMap();

}