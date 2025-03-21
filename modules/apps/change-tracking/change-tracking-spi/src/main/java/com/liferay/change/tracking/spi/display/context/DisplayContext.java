/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.spi.display.context;

import com.liferay.portal.kernel.model.BaseModel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The DisplayContext used by {@link
 * com.liferay.change.tracking.spi.display.CTDisplayRenderer} for rendering a
 * model.
 *
 * @author Preston Crary
 * @see    com.liferay.change.tracking.spi.display.CTDisplayRenderer
 */
@ProviderType
public interface DisplayContext<T> {

	/**
	 * Creates a download URL for use while rendering. This is only used for
	 * {@link CTModel}
	 * renderers.
	 *
	 * @param  key to be passed to {@link
	 *         com.liferay.change.tracking.spi.display.CTDisplayRenderer#getDownloadInputStream(
	 *         Object, String)}
	 * @param  size the size of the download in bytes or <code>0</code>
	 * @param  title the title to use for the download
	 * @return the URL string or <code>null</code>
	 */
	public String getDownloadURL(String key, long size, String title);

	/**
	 * Returns the request used for rendering.
	 *
	 * @return the request used for rendering
	 */
	public HttpServletRequest getHttpServletRequest();

	/**
	 * Returns the response used for rendering.
	 *
	 * @return the response used for rendering
	 */
	public HttpServletResponse getHttpServletResponse();

	public Locale getLocale();

	/**
	 * Returns the model to be rendered.
	 *
	 * @return the model to be rendered
	 */
	public T getModel();

	public void render(BaseModel<?> baseModel, Locale locale) throws Exception;

	public String renderPreview(BaseModel<?> baseModel, Locale locale)
		throws Exception;

}