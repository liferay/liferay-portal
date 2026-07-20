/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v1_1_0;

import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.internal.upgrade.helper.JournalArticleImageUpgradeHelper;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PortalUtil;

/**
 * @author Eudaldo Alonso
 */
public class ImageTypeContentUpgradeProcess extends UpgradeProcess {

	public ImageTypeContentUpgradeProcess(
		ImageLocalService imageLocalService,
		JournalArticleImageUpgradeHelper journalArticleImageUpgradeHelper,
		PortletFileRepository portletFileRepository) {

		_imageLocalService = imageLocalService;
		_journalArticleImageUpgradeHelper = journalArticleImageUpgradeHelper;
		_portletFileRepository = portletFileRepository;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_copyJournalArticleImagesToJournalRepository();
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropTables("JournalArticleImage")
		};
	}

	private void _copyJournalArticleImagesToJournalRepository()
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			processConcurrently(
				StringBundler.concat(
					"select JournalArticleImage.articleImageId, ",
					"JournalArticleImage.groupId, ",
					"JournalArticleImage.companyId, ",
					"JournalArticle.resourcePrimKey, JournalArticle.userId ",
					"from JournalArticleImage inner join JournalArticle on ",
					"(JournalArticle.groupId = JournalArticleImage.groupId ",
					"and JournalArticle.articleId = ",
					"JournalArticleImage.articleId and JournalArticle.version ",
					"= JournalArticleImage.version)"),
				resultSet -> {
					long articleImageId = resultSet.getLong(1);
					long groupId = resultSet.getLong(2);
					long companyId = resultSet.getLong(3);
					long resourcePrimKey = resultSet.getLong(4);

					long userId = PortalUtil.getValidUserId(
						companyId, resultSet.getLong(5));

					long folderId =
						_journalArticleImageUpgradeHelper.getFolderId(
							userId, groupId, resourcePrimKey);

					return new Object[] {
						articleImageId, groupId, resourcePrimKey, userId,
						folderId
					};
				},
				values -> {
					long articleImageId = (Long)values[0];
					long groupId = (Long)values[1];
					long folderId = (Long)values[4];

					String fileName = String.valueOf(articleImageId);

					FileEntry fileEntry =
						_portletFileRepository.fetchPortletFileEntry(
							groupId, folderId, fileName);

					if (fileEntry != null) {
						return;
					}

					long resourcePrimKey = (Long)values[2];
					long userId = (Long)values[3];

					try {
						Image image = _imageLocalService.fetchImage(
							articleImageId);

						if (image == null) {
							return;
						}

						String mimeType = MimeTypesUtil.getContentType(
							fileName + StringPool.PERIOD + image.getType());

						_portletFileRepository.addPortletFileEntry(
							groupId, userId, JournalArticle.class.getName(),
							resourcePrimKey, JournalConstants.SERVICE_NAME,
							folderId, image.getTextObj(), fileName, mimeType,
							false);

						_imageLocalService.deleteImage(image.getImageId());
					}
					catch (Exception exception) {
						_log.error(
							"Unable to add the journal article image " +
								fileName + " into the file repository",
							exception);

						throw exception;
					}
				},
				null);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImageTypeContentUpgradeProcess.class);

	private final ImageLocalService _imageLocalService;
	private final JournalArticleImageUpgradeHelper
		_journalArticleImageUpgradeHelper;
	private final PortletFileRepository _portletFileRepository;

}