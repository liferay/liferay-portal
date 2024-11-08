/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.related.models;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public interface ObjectRelatedModelsProvider<T extends BaseModel<T>> {

	public void deleteRelatedModel(
			long userId, long groupId, long objectRelationshipId,
			long primaryKey, String deletionType)
		throws PortalException;

	public void disassociateRelatedModels(
			long userId, long objectRelationshipId, long primaryKey1,
			long primaryKey2)
		throws PortalException;

	public default T fetchRelatedModel(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	public String getClassName();

	public long getCompanyId();

	public String getObjectRelationshipType();

	public List<T> getRelatedModels(
			long groupId, long objectRelationshipId, long primaryKey,
			String search, int start, int end)
		throws PortalException;

	public int getRelatedModelsCount(
			long groupId, long objectRelationshipId, long primaryKey,
			String search)
		throws PortalException;

	public default List<T> getUnrelatedModels(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search,
			int start, int end)
		throws PortalException {

		return new ArrayList<>();
	}

	public default int getUnrelatedModelsCount(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search)
		throws PortalException {

		return 0;
	}

}