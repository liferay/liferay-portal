/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.dto.v1_0.util;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.rest.dto.v1_0.Link;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;

/**
 * @author Carolina Barbosa
 */
public class LinkUtil {

	public static Link toLink(
		DLAppService dlAppService, DLFileEntry dlFileEntry,
		DLURLHelper dlURLHelper, long groupId,
		String objectDefinitionExternalReferenceCode,
		String objectEntryExternalReferenceCode, Portal portal) {

		return new Link() {
			{
				setHref(
					() -> {
						try {
							return ObjectFieldUtil.getAttachmentDownloadURL(
								dlURLHelper,
								dlAppService.getFileEntry(
									dlFileEntry.getFileEntryId()),
								groupId, objectDefinitionExternalReferenceCode,
								objectEntryExternalReferenceCode, null);
						}
						catch (Exception exception) {
							if (_log.isWarnEnabled()) {
								_log.warn(exception);
							}
						}

						return StringBundler.concat(
							portal.getPathContext(), portal.getPathMain(),
							"/portal/login");
					});
				setLabel(dlFileEntry::getFileName);
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(LinkUtil.class);

}