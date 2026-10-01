/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
public interface PersistedModelLocalService {

	@Transactional(enabled = false)
	public default PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public default <T> T dslQuery(DSLQuery dslQuery) {
		return dslQuery(dslQuery, true);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public default <T> T dslQuery(DSLQuery dslQuery, boolean useFinderCache) {
		BasePersistence<?> basePersistence = getBasePersistence();

		return (T)basePersistence.dslQuery(dslQuery, useFinderCache);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public default int dslQueryCount(DSLQuery dslQuery) {
		return dslQueryCount(dslQuery, true);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public default int dslQueryCount(
		DSLQuery dslQuery, boolean useFinderCache) {

		Long count = dslQuery(dslQuery, useFinderCache);

		return count.intValue();
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public default PersistedModel fetchPersistedModel(
		Serializable primaryKeyObj) {

		BasePersistence<?> basePersistence = getBasePersistence();

		return (PersistedModel)basePersistence.fetchByPrimaryKey(primaryKeyObj);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public default <T extends PersistedModel> Map<Serializable, T>
		fetchPersistedModels(Set<Serializable> primaryKeys) {

		BasePersistence<?> basePersistence = getBasePersistence();

		return (Map<Serializable, T>)basePersistence.fetchByPrimaryKeys(
			primaryKeys);
	}

	@Transactional(enabled = false)
	public default BasePersistence<?> getBasePersistence() {
		throw new UnsupportedOperationException();
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

}