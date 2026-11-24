/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.exporter;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.user.associated.data.component.UADComponent;

import java.io.File;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Handles converting the user-related type {@code T} entities into a format
 * that can be written to a file and downloaded.
 *
 * @author William Newbury
 */
@ProviderType
public interface UADExporter<T> extends UADComponent<T> {

	/**
	 * Returns the number of type {@code T} entities associated with the user.
	 *
	 * @param  userId the primary key of the user whose data to count
	 * @return the number of entities associated with the user
	 */
	public long count(long userId) throws PortalException;

	/**
	 * Returns a byte array representing the entity, ready to be written to a
	 * file.
	 *
	 * @param  t the type {@code T} entity to convert into a byte array
	 * @return a byte array representing the given entity
	 * @throws PortalException if a portal exception occurred
	 */
	public byte[] export(T t) throws PortalException;

	/**
	 * Returns a file object containing the data from all type {@code T}
	 * entities related to the user.
	 *
	 * @param  userId the primary key of the user whose data to export
	 * @return a file containing the exported data
	 * @throws PortalException if a portal exception occurred
	 */
	public File exportAll(long userId) throws PortalException;

	/**
	 * Returns the number of export data items of type {@code T} entities
	 * associated with the user.
	 *
	 * @param  userId the primary key of the user whose data to count
	 * @return the number of export data items
	 */
	public default long getExportDataCount(long userId) throws PortalException {
		return count(userId);
	}

}