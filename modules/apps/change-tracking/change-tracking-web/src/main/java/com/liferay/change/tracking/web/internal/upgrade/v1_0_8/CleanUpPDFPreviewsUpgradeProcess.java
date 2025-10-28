/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.upgrade.v1_0_8;

import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Portal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author David Truong
 */
public class CleanUpPDFPreviewsUpgradeProcess extends UpgradeProcess {

	public CleanUpPDFPreviewsUpgradeProcess(
		CTCollectionLocalService ctCollectionLocalService, Portal portal) {

		_ctCollectionLocalService = ctCollectionLocalService;
		_portal = portal;
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (hasTable("DLFileVersionPreview")) {
			_cleanUpDLFileVersionPreviews();
		}
	}

	private void _cleanUpDLFileVersionPreviews() throws Exception {
		long dlFileVersionPreviewClassNameId = _portal.getClassNameId(
			"com.liferay.document.library.model.DLFileVersionPreview");

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select distinct CTEntry.ctCollectionId, CTEntry.",
					"modelClassNameId, CTEntry.modelClassPK from CTEntry ",
					"inner join DLFileVersionPreview on CTEntry.",
					"modelClassNameId = ? and DLFileVersionPreview.",
					"dlFileVersionPreviewId = CTEntry.modelClassPK inner join ",
					"DLFileEntry on (DLFileVersionPreview.fileEntryId = ",
					"DLFileEntry.fileEntryId and DLFileEntry.ctCollectionId ",
					"!= CTEntry.ctCollectionId)"))) {

			preparedStatement.setLong(1, dlFileVersionPreviewClassNameId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long ctCollectionId = resultSet.getLong("ctCollectionId");
					long classNameId = resultSet.getLong("modelClassNameId");
					long classPK = resultSet.getLong("modelClassPK");

					_ctCollectionLocalService.discardCTEntry(
						ctCollectionId, classNameId, classPK, true);
				}
			}
		}
	}

	private final CTCollectionLocalService _ctCollectionLocalService;
	private final Portal _portal;

}