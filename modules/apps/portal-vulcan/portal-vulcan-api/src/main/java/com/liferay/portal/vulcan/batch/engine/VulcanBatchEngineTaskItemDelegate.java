/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.batch.engine;

import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Javier Gamarra
 */
@ProviderType
public interface VulcanBatchEngineTaskItemDelegate<T> {

	public void create(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception;

	public void delete(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception;

	public default Set<String> getAvailableCreateStrategies() {
		return null;
	}

	public default Set<String> getAvailableUpdateStrategies() {
		return null;
	}

	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception;

	public default Class<?> getResourceClass() {
		return getClass();
	}

	public default String getResourceName() {
		return null;
	}

	public default String getVersion() {
		return "v1.0";
	}

	public Page<T> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception;

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<T>, UnsafeFunction<T, T, Exception>, Exception>
				contextBatchUnsafeBiConsumer);

	public void setContextCompany(Company contextCompany);

	public void setContextUriInfo(UriInfo uriInfo);

	public void setContextUser(User contextUser);

	public void setGroupLocalService(GroupLocalService groupLocalService);

	public void setLanguageId(String languageId);

	public void update(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception;

}