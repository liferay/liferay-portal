/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.internal.http.DSHttp;
import com.liferay.digital.signature.manager.DSDocumentManager;
import com.liferay.petra.string.StringBundler;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = DSDocumentManager.class)
public class DSDocumentManagerImpl implements DSDocumentManager {

	@Override
	public byte[] getDSDocumentAsBytes(
		long companyId, long groupId, String dsDocumentId,
		String dsEnvelopeId) {

		return _dsHttp.getAsBytes(
			companyId, groupId,
			StringBundler.concat(
				"envelopes/", dsEnvelopeId, "/documents/", dsDocumentId));
	}

	@Override
	public byte[] getDSDocumentsAsBytes(
		long companyId, long groupId, String dsEnvelopeId) {

		return _dsHttp.getAsBytes(
			companyId, groupId,
			StringBundler.concat(
				"envelopes/", dsEnvelopeId,
				"/documents/archive?escape_non_ascii_filenames=true",
				"&include=document,summary,voice_print&language=en"));
	}

	@Reference
	private DSHttp _dsHttp;

}