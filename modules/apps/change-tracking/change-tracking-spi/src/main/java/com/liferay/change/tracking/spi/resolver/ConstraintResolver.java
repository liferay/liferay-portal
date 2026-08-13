/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.spi.resolver;

import com.liferay.change.tracking.spi.resolver.context.ConstraintResolverContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.change.tracking.CTModel;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Constraint resolvers are used to automatically resolve unique database
 * constraints between different change tracking collections, or to describe the
 * conflict for users attempting to publish.
 *
 * @author Preston Crary
 */
public interface ConstraintResolver<T extends CTModel<T>> {

	/**
	 * Returns the language key that describes the conflict.
	 *
	 * @return the language key that describes the conflict
	 */
	public String getConflictDescriptionKey();

	/**
	 * Returns the model class for this constraint resolver.
	 *
	 * @return the model class for this constraint resolver
	 */
	public Class<T> getModelClass();

	/**
	 * Returns the language key for steps taken (or steps needed) to resolve the
	 * unique constraint conflict.
	 *
	 * @return the language key
	 */
	public String getResolutionDescriptionKey();

	/**
	 * Returns the resource bundle to use for the conflict description and
	 * resolution description keys.
	 *
	 * @param  locale the locale for the resource bundle
	 * @return the resource bundle
	 * @see    #getConflictDescriptionKey()
	 * @see    #getResolutionDescriptionKey()
	 */
	public ResourceBundle getResourceBundle(Locale locale);

	/**
	 * Returns an array of column names for the unique index, excluding the
	 * <code>ctCollectionId</code> column.
	 *
	 * @return the unique index column names
	 * @see    com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence#getUniqueIndexColumnNames(
	 *         )
	 */
	public String[] getUniqueIndexColumnNames();

	/**
	 * Returns <code>true</code> if modified models are checked for unique
	 * index conflicts, in addition to added models.
	 *
	 * Only added models are checked by default. Enable this for models whose
	 * unique index covers a column that is changed after the model is added,
	 * such as an ordering position. Enabling it runs one additional query per
	 * modified model, so leave it disabled unless the model needs it.
	 *
	 * @return <code>true</code> if modified models are checked for unique
	 *         index conflicts; <code>false</code> otherwise
	 */
	public default boolean isModificationConflictCheckEnabled() {
		return false;
	}

	/**
	 * Called when publishing would cause a constraint violation with both rows
	 * involved accessible from the context.
	 *
	 * It is not required to resolve the
	 * conflict if the goal is only to describe it for the user. This method is
	 * always called from the source model's change tracking collection.
	 *
	 * @param constraintResolverContext the context used in automatically
	 *        resolving constraint conflicts between the source and target
	 *        models
	 */
	public void resolveConflict(
			ConstraintResolverContext<T> constraintResolverContext)
		throws PortalException;

}