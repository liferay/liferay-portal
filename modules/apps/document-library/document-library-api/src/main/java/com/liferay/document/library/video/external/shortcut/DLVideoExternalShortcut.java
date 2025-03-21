/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.external.shortcut;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Provides an external video shortcut to be used by the document library to
 * link to and render videos hosted in external services.
 *
 * <p>
 * Implementations of this interface will be returned by an implementation of
 * {@link
 * com.liferay.document.library.video.external.shortcut.provider.DLVideoExternalShortcutProvider}
 * </p>
 *
 * @author Alejandro Tardín
 * @review
 */
public interface DLVideoExternalShortcut {

	/**
	 * Returns the description of the external video (if any).
	 *
	 * <p>
	 * It will be used to fill the document's description field when saving it
	 * to the document library.
	 * </p>
	 *
	 * @return the description
	 * @review
	 */
	public default String getDescription() {
		return null;
	}

	/**
	 * Returns the thumbnail URL of the external video (if any).
	 *
	 * <p>
	 * It will be used inside the Documents and Media UI (cards, lists, etc...).
	 * </p>
	 *
	 * @return the thumbnail URL.
	 * @review
	 */
	public default String getThumbnailURL() {
		return null;
	}

	/**
	 * Returns the title of the external video (if any).
	 *
	 * <p>
	 * It will be used to fill the document's title field when saving it to the
	 * document library.
	 * </p>
	 *
	 * @return the title
	 * @review
	 */
	public default String getTitle() {
		return null;
	}

	/**
	 * Returns the URL of the external video.
	 *
	 * @return the URL
	 * @review
	 */
	public String getURL();

	/**
	 * Returns a snippet of HTML that will be used to embed the video inside a
	 * specific content.
	 *
	 * <p>
	 * This will typically render an <code>iframe</code> or a <code>video</code>
	 * element.
	 * </p>
	 *
	 * @return the HTML
	 * @review
	 */
	public String renderHTML(HttpServletRequest httpServletRequest);

}