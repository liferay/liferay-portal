/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.media;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@ProviderType
public interface CommerceMediaResolver {

	public String getDefaultURL(long groupId);

	public String getDownloadURL(
			long commerceAccountId, long cpAttachmentFileEntryId)
		throws PortalException;

	public String getDownloadVirtualOrderItemURL(
		long commerceVirtualOrderItemId, long fileEntryId);

	public String getDownloadVirtualProductSampleURL(
			String className, long classPK, long commerceAccountId,
			long fileEntryId)
		throws PortalException;

	public String getDownloadVirtualProductURL(
			String className, long classPK, long commerceAccountId,
			long fileEntryId)
		throws PortalException;

	public String getThumbnailURL(
			long commerceAccountId, long cpAttachmentFileEntryId)
		throws PortalException;

	public String getThumbnailURL(
			long commerceAccountId, long cpAttachmentFileEntryId,
			boolean secure)
		throws PortalException;

	public String getURL(long commerceAccountId, long cpAttachmentFileEntryId)
		throws PortalException;

	public String getURL(
			long commerceAccountId, long cpAttachmentFileEntryId,
			boolean download, boolean thumbnail)
		throws PortalException;

	public String getURL(
			long commerceAccountId, long cpAttachmentFileEntryId,
			boolean download, boolean thumbnail, boolean secure)
		throws PortalException;

}