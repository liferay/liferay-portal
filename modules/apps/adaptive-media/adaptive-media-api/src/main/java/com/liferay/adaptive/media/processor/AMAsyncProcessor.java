/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.processor;

import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Generates a specific type of media asynchronously.
 *
 * <p>
 * This processor delegates the generation of the media in {@link AMProcessor}
 * by invoking it in an asynchronous manner.
 * </p>
 *
 * <p>
 * The type parameter <code>M</code> specifies the model used by the processor
 * to generate the media. The type parameter <code>T</code> restricts the valid
 * {@link com.liferay.adaptive.media.AMAttribute} set available.
 * </p>
 *
 * @author Sergio González
 */
@ProviderType
public interface AMAsyncProcessor<M, T> {

	/**
	 * Asynchronously generates the media for the model.
	 *
	 * @param  model the model for which media is generated
	 * @param  modelId the model's ID
	 * @throws PortalException if an error occurred while calling any Liferay
	 *         services
	 */
	public void triggerProcess(M model, String modelId) throws PortalException;

}