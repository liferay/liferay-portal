/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.processor;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Generates a particular type of media.
 *
 * <p>
 * The type parameter <code>M</code> specifies the model used by the processor
 * to generate the media. The type parameter <code>T</code> restricts the valid
 * {@link com.liferay.adaptive.media.AMAttribute} set available.
 * </p>
 *
 * @author Adolfo Pérez
 */
@ProviderType
public interface AMProcessor<M> {

	public void process(FileVersion fileVersion, String configurationEntryUuid)
		throws PortalException;

	/**
	 * Generates the media for the model. Some implementations might not
	 * generate any media for the model.
	 *
	 * @param  model the model for which media is generated
	 * @throws PortalException if an error occurred while calling any Liferay
	 *         services
	 */
	public void process(M model) throws PortalException;

}