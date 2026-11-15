/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.scaler;

/**
 * Tracks the image scalers and returns the one used for a specific MIME type.
 *
 * <p>
 * Every image scaler registered in OSGi with the <code>mime.type</code>
 * property is tracked. Multiple image scalers can be registered for the same
 * MIME type, but the tracker returns only one.
 * </p>
 *
 * <p>
 * The image scalers are grouped by <code>mime.type</code> and ordered by the
 * <code>service.ranking</code> property. The image scaler returns the first
 * image scaler of the group whose {@link AMImageScaler#isEnabled()} method
 * returns <code>true</code>.
 * </p>
 *
 * <p>
 * If no enabled image scalers are registered for the specific MIME type, it
 * returns the default image scaler.
 * </p>
 *
 * @author Sergio González
 */
public interface AMImageScalerRegistry {

	/**
	 * Returns the enabled image scaler for the specific MIME type.
	 *
	 * @param  mimeType the MIME type
	 * @return the image scaler
	 */
	public AMImageScaler getAMImageScaler(String mimeType);

}