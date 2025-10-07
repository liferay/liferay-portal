/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.graphql.dto;

import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.lang.reflect.Method;

import java.util.Collections;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Javier de Arcos
 */
@ProviderType
public interface GraphQLDTOContributor<D, R> {

	public R createDTO(D dto, DTOConverterContext dtoConverterContext)
		throws Exception;

	public boolean deleteDTO(DTOConverterContext dtoConverterContext, long id)
		throws Exception;

	public String getApplicationName();

	public long getCompanyId();

	public R getDTO(DTOConverterContext dtoConverterContext, long id)
		throws Exception;

	public Page<R> getDTOs(
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			Filter filter, Pagination pagination, String search, Sort[] sorts)
		throws Exception;

	public EntityModel getEntityModel();

	public List<GraphQLDTOProperty> getGraphQLDTOProperties();

	public String getIdName();

	public default List<GraphQLDTOProperty>
		getRelationshipGraphQLDTOProperties() {

		return Collections.emptyList();
	}

	public default <T> T getRelationshipValue(
			DTOConverterContext dtoConverterContext, long id,
			Class<T> relationshipClass, String relationshipName)
		throws Exception {

		return null;
	}

	public Class<?> getResourceClass(Operation operation);

	public Method getResourceMethod(Operation operation);

	public String getResourceName();

	public String getTypeName();

	public boolean hasScope();

	public R updateDTO(D dto, DTOConverterContext dtoConverterContext, long id)
		throws Exception;

	public enum Operation {

		CREATE, DELETE, GET, GET_RELATIONSHIP, LIST, UPDATE

	}

}