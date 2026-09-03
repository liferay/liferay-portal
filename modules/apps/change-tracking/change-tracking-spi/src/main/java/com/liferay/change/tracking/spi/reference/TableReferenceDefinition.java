/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.spi.reference;

import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Describes parent and child relationships for a given table using joins.
 * Implementations are required for complete change tracking integration of a
 * service builder service that has a base persistence.
 *
 * @author Preston Crary
 */
public interface TableReferenceDefinitionXYTestChange<T extends Table<T>> {

	/**
	 * Defines child rows using inner joins on the table.
	 *
	 * A child row is defined as a row required by the parent table to function
	 * correctly. Typically,
	 * children have their parents' primary keys contained in one of the child's
	 * columns. {@link com.liferay.asset.kernel.model.AssetEntry} and {@link
	 * com.liferay.portal.kernel.model.ResourcePermission} are common children
	 * of many
	 * tables.
	 *
	 * @param childTableReferenceInfoBuilder the builder object used to define
	 *        child relationships for this table reference definition
	 */
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<T> childTableReferenceInfoBuilder);

	/**
	 * Defines parent rows using inner joins on the table.
	 *
	 * A parent row is defined as a row that triggers its children for deletion
	 * when it is deleted.
	 * Typically, children have their parents' primary keys contained in one of
	 * the child's columns. {@link com.liferay.portal.kernel.model.Company} and
	 * {@link com.liferay.portal.kernel.model.Group} are common parents of many
	 * tables.
	 *
	 * @param parentTableReferenceInfoBuilder the builder object used to define
	 *        parent relationships for this table reference definition
	 */
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<T> parentTableReferenceInfoBuilder);

	/**
	 * Returns the base persistence for the table described by this table
	 * reference definition.
	 *
	 * @return the base persistence for this table reference definition
	 */
	public BasePersistence<?> getBasePersistence();

	/**
	 * Returns the table being described by this table reference definition.
	 *
	 * @return the table being described by this table reference definition
	 */
	public T getTable();

}