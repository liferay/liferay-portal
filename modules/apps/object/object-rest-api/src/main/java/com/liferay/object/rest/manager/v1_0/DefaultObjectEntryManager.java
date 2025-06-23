/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.manager.v1_0;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Feliphe Marinho
 */
@ProviderType
public interface DefaultObjectEntryManager extends ObjectEntryManager {

	public ObjectEntry addObjectRelationshipMappingTableValues(
			DTOConverterContext dtoConverterContext,
			ObjectRelationship objectRelationship, long primaryKey1,
			long primaryKey2)
		throws Exception;

	public Object addSystemObjectRelationshipMappingTableValues(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, long primaryKey1,
			long primaryKey2)
		throws Exception;

	public ObjectEntry copyObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId, int version)
		throws Exception;

	public ObjectEntry copyObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			int version)
		throws Exception;

	public void deleteObjectEntry(
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception;

	public void deleteObjectEntryByVersion(
			ObjectDefinition objectDefinition, long objectEntryId, int version)
		throws Exception;

	public void deleteObjectEntryByVersion(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			int version)
		throws Exception;

	public void disassociateRelatedModels(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, long primaryKey,
			ObjectDefinition relatedObjectDefinition, long userId)
		throws Exception;

	public void executeObjectAction(
			DTOConverterContext dtoConverterContext, String objectActionName,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception;

	public void executeObjectAction(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, String objectActionName,
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception;

	public ObjectEntry expireObjectEntry(
			DTOConverterContext dtoConverterContext, long objectEntryId)
		throws Exception;

	public ObjectEntry expireObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId, int version)
		throws Exception;

	public ObjectEntry expireObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			int version)
		throws Exception;

	public ObjectEntry fetchObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception;

	public ObjectEntry fetchRelatedManyToOneObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, Long objectEntryId,
			String objectRelationshipName)
		throws Exception;

	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			Expression filterExpression, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception;

	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			Filter filter, Pagination pagination, String search, Sort[] sorts)
		throws Exception;

	public ObjectEntry getObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception;

	public ObjectEntry getObjectEntryByVersion(
			DTOConverterContext dtoConverterContext, Long objectEntryId,
			int version)
		throws Exception;

	public ObjectEntry getObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			int version)
		throws Exception;

	public Page<ObjectEntry> getObjectEntryRelatedObjectEntries(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, Long objectEntryId,
			String objectRelationshipName, Pagination pagination)
		throws Exception;

	public Page<Object> getRelatedSystemObjectEntries(
			ObjectDefinition objectDefinition, Long objectEntryId,
			String objectRelationshipName, Pagination pagination)
		throws Exception;

	public Object getSystemObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long primaryKey)
		throws Exception;

	public Page<ObjectEntry> getVersionedObjectEntries(
			DTOConverterContext dtoConverterContext, long objectEntryId,
			Pagination pagination)
		throws Exception;

	public Page<ObjectEntry> getVersionedObjectEntries(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			Pagination pagination)
		throws Exception;

	public ObjectEntry partialUpdateObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId,
			ObjectEntry objectEntry)
		throws Exception;

	public ObjectEntry restoreObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId, int version)
		throws Exception;

	public ObjectEntry restoreObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			int version)
		throws Exception;

	public ObjectEntry updateObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId,
			ObjectEntry objectEntry)
		throws Exception;

	public void validateObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			List<String> objectValidationRuleExternalReferenceCodes,
			String scopeKey)
		throws Exception;

}