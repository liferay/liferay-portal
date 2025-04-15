/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine;

import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.batch.engine.strategy.BatchEngineImportStrategy;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.odata.entity.EntityModel;

import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Ivica Cardic
 */
@ProviderType
public interface BatchEngineTaskItemDelegate<T> {

	public void create(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception;

	public void delete(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception;

	public Set<String> getAvailableCreateStrategies();

	public Set<String> getAvailableUpdateStrategies();

	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception;

	public default Class<T> getItemClass() {
		return null;
	}

	public boolean hasCreateStrategy(String createStrategy);

	public boolean hasUpdateStrategy(String updateStrategy);

	public Page<T> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception;

	public void setBatchEngineImportStrategy(
		BatchEngineImportStrategy batchEngineImportStrategy);

	public void setContextCompany(Company contextCompany);

	public void setContextUriInfo(UriInfo uriInfo);

	public void setContextUser(User contextUser);

	public void setLanguageId(String languageId);

	public void update(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception;

}