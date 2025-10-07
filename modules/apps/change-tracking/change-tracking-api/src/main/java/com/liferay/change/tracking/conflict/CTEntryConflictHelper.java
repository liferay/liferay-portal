/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.conflict;

import com.liferay.change.tracking.model.CTEntry;
import com.liferay.portal.kernel.model.change.tracking.CTModel;

/**
 * @author Tina Tian
 */
public interface CTEntryConflictHelper {

	public default String getMissingRequirementTypeName(
		CTEntry ctEntry, long targetCTCollectionId) {

		return null;
	}

	public Class<? extends CTModel<?>> getModelClass();

	public default boolean hasDeletionModificationConflict(
		CTEntry ctEntry, long targetCTCollectionId) {

		return false;
	}

	public default boolean hasModificationConflict(
		CTEntry ctEntry, long targetCTCollectionId) {

		return false;
	}

}