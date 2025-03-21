/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.url.provider;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;

import jakarta.portlet.PortletURL;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Adolfo Pérez
 */
@ProviderType
public interface TranslationURLProvider {

	public PortletURL getExportTranslationURL(
		long groupId, long classNameId, long classPK,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory);

	public PortletURL getExportTranslationURL(
		long groupId, long classNameId,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory);

	public PortletURL getImportTranslationURL(
			long groupId, long classNameId, long classPK,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory)
		throws PortalException;

	public PortletURL getImportTranslationURL(
			long groupId, long classNameId,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory)
		throws PortalException;

	public PortletURL getTranslateURL(
			long groupId, long classNameId, long classPK,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory)
		throws PortalException;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getTranslateURL(long, long, long,
	 *             RequestBackedPortletURLFactory)}
	 */
	@Deprecated
	public PortletURL getTranslateURL(
		long classNameId, long classPK,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory);

}