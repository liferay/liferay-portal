/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service;

import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link KBArticleService}.
 *
 * @author Brian Wing Shun Chan
 * @see KBArticleService
 * @generated
 */
public class KBArticleServiceWrapper
	implements KBArticleService, ServiceWrapper<KBArticleService> {

	public KBArticleServiceWrapper() {
		this(null);
	}

	public KBArticleServiceWrapper(KBArticleService kbArticleService) {
		_kbArticleService = kbArticleService;
	}

	@Override
	public KBArticle addKBArticle(
			String externalReferenceCode, String portletId,
			long parentResourceClassNameId, long parentResourcePrimKey,
			String title, String urlTitle, String content, String description,
			String[] sections, String sourceURL, java.util.Date displayDate,
			java.util.Date expirationDate, java.util.Date reviewDate,
			String[] selectedFileNames,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.addKBArticle(
			externalReferenceCode, portletId, parentResourceClassNameId,
			parentResourcePrimKey, title, urlTitle, content, description,
			sections, sourceURL, displayDate, expirationDate, reviewDate,
			selectedFileNames, serviceContext);
	}

	@Override
	public int addKBArticlesMarkdown(
			long groupId, long parentKBFolderId, String fileName,
			boolean prioritizeByNumericalPrefix,
			java.io.InputStream inputStream,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.addKBArticlesMarkdown(
			groupId, parentKBFolderId, fileName, prioritizeByNumericalPrefix,
			inputStream, serviceContext);
	}

	@Override
	public void addTempAttachment(
			long groupId, long resourcePrimKey, String fileName,
			String tempFolderName, java.io.InputStream inputStream,
			String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.addTempAttachment(
			groupId, resourcePrimKey, fileName, tempFolderName, inputStream,
			mimeType);
	}

	@Override
	public int countKBArticlesByKeywords(
		long groupId, String keywords, int status) {

		return _kbArticleService.countKBArticlesByKeywords(
			groupId, keywords, status);
	}

	@Override
	public KBArticle deleteKBArticle(long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.deleteKBArticle(resourcePrimKey);
	}

	@Override
	public void deleteKBArticleAttachment(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.deleteKBArticleAttachment(fileEntryId);
	}

	@Override
	public void deleteKBArticles(long groupId, long[] resourcePrimKeys)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.deleteKBArticles(groupId, resourcePrimKeys);
	}

	@Override
	public void deleteTempAttachment(
			long groupId, long resourcePrimKey, String fileName,
			String tempFolderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.deleteTempAttachment(
			groupId, resourcePrimKey, fileName, tempFolderName);
	}

	@Override
	public KBArticle expireKBArticle(
			long resourcePrimKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.expireKBArticle(
			resourcePrimKey, serviceContext);
	}

	@Override
	public KBArticle fetchFirstChildKBArticle(
		long groupId, long parentResourcePrimKey) {

		return _kbArticleService.fetchFirstChildKBArticle(
			groupId, parentResourcePrimKey);
	}

	@Override
	public KBArticle fetchFirstChildKBArticle(
		long groupId, long parentResourcePrimKey, int status) {

		return _kbArticleService.fetchFirstChildKBArticle(
			groupId, parentResourcePrimKey, status);
	}

	@Override
	public KBArticle fetchKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.fetchKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle);
	}

	@Override
	public KBArticle fetchLatestKBArticle(long resourcePrimKey, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.fetchLatestKBArticle(resourcePrimKey, status);
	}

	@Override
	public KBArticle fetchLatestKBArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.fetchLatestKBArticleByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	@Override
	public KBArticle fetchLatestKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.fetchLatestKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle, status);
	}

	@Override
	public com.liferay.portal.kernel.lock.Lock forceLockKBArticle(
			long groupId, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.forceLockKBArticle(groupId, resourcePrimKey);
	}

	@Override
	public java.util.List<KBArticle> getAllDescendantKBArticles(
			long groupId, long resourcePrimKey, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getAllDescendantKBArticles(
			groupId, resourcePrimKey, status, orderByComparator);
	}

	@Override
	public java.util.List<KBArticle> getGroupKBArticles(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator) {

		return _kbArticleService.getGroupKBArticles(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public int getGroupKBArticlesCount(long groupId, int status) {
		return _kbArticleService.getGroupKBArticlesCount(groupId, status);
	}

	@Override
	public String getGroupKBArticlesRSS(
			int status, int max, String type, double version,
			String displayStyle,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getGroupKBArticlesRSS(
			status, max, type, version, displayStyle, themeDisplay);
	}

	@Override
	public KBArticle getKBArticle(long resourcePrimKey, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getKBArticle(resourcePrimKey, version);
	}

	@Override
	public java.util.List<KBArticle> getKBArticleAndAllDescendantKBArticles(
			long resourcePrimKey, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getKBArticleAndAllDescendantKBArticles(
			resourcePrimKey, status, orderByComparator);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			getKBArticleAttachment(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getKBArticleAttachment(fileEntryId);
	}

	@Override
	public String getKBArticleRSS(
			long resourcePrimKey, int status, int max, String type,
			double version, String displayStyle,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getKBArticleRSS(
			resourcePrimKey, status, max, type, version, displayStyle,
			themeDisplay);
	}

	@Override
	public java.util.List<KBArticle> getKBArticles(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator) {

		return _kbArticleService.getKBArticles(
			groupId, parentResourcePrimKey, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<KBArticle> getKBArticles(
		long groupId, long[] resourcePrimKeys, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator) {

		return _kbArticleService.getKBArticles(
			groupId, resourcePrimKeys, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<KBArticle> getKBArticles(
		long groupId, long[] resourcePrimKeys, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator) {

		return _kbArticleService.getKBArticles(
			groupId, resourcePrimKeys, status, orderByComparator);
	}

	@Override
	public java.util.List<KBArticle> getKBArticlesByKeywords(
		long groupId, String keywords, int status, int start, int end) {

		return _kbArticleService.getKBArticlesByKeywords(
			groupId, keywords, status, start, end);
	}

	@Override
	public int getKBArticlesCount(
		long groupId, long parentResourcePrimKey, int status) {

		return _kbArticleService.getKBArticlesCount(
			groupId, parentResourcePrimKey, status);
	}

	@Override
	public int getKBArticlesCount(
		long groupId, long[] resourcePrimKeys, int status) {

		return _kbArticleService.getKBArticlesCount(
			groupId, resourcePrimKeys, status);
	}

	@Override
	public com.liferay.knowledge.base.model.KBArticleSearchDisplay
			getKBArticleSearchDisplay(
				long groupId, String title, String content, int status,
				java.util.Date startDate, java.util.Date endDate,
				boolean andOperator, int[] curStartValues, int cur, int delta,
				com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
					orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getKBArticleSearchDisplay(
			groupId, title, content, status, startDate, endDate, andOperator,
			curStartValues, cur, delta, orderByComparator);
	}

	@Override
	public java.util.List<KBArticle> getKBArticleVersions(
		long groupId, long resourcePrimKey, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator) {

		return _kbArticleService.getKBArticleVersions(
			groupId, resourcePrimKey, status, start, end, orderByComparator);
	}

	@Override
	public int getKBArticleVersionsCount(
		long groupId, long resourcePrimKey, int status) {

		return _kbArticleService.getKBArticleVersionsCount(
			groupId, resourcePrimKey, status);
	}

	@Override
	public KBArticle getLatestKBArticle(long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getLatestKBArticle(resourcePrimKey);
	}

	@Override
	public KBArticle getLatestKBArticle(long resourcePrimKey, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getLatestKBArticle(resourcePrimKey, status);
	}

	@Override
	public KBArticle getLatestKBArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getLatestKBArticleByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kbArticleService.getOSGiServiceIdentifier();
	}

	@Override
	public KBArticle[] getPreviousAndNextKBArticles(long kbArticleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getPreviousAndNextKBArticles(kbArticleId);
	}

	@Override
	public java.util.List<KBArticle> getSectionsKBArticles(
		long groupId, String[] sections, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator) {

		return _kbArticleService.getSectionsKBArticles(
			groupId, sections, status, start, end, orderByComparator);
	}

	@Override
	public int getSectionsKBArticlesCount(
		long groupId, String[] sections, int status) {

		return _kbArticleService.getSectionsKBArticlesCount(
			groupId, sections, status);
	}

	@Override
	public String[] getTempAttachmentNames(long groupId, String tempFolderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.getTempAttachmentNames(
			groupId, tempFolderName);
	}

	@Override
	public com.liferay.portal.kernel.lock.Lock lockKBArticle(
			long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.lockKBArticle(resourcePrimKey);
	}

	@Override
	public void moveKBArticle(
			long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey, double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.moveKBArticle(
			resourcePrimKey, parentResourceClassNameId, parentResourcePrimKey,
			priority);
	}

	@Override
	public KBArticle moveKBArticleToTrash(long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.moveKBArticleToTrash(resourcePrimKey);
	}

	@Override
	public KBArticle revertKBArticle(
			long resourcePrimKey, int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.revertKBArticle(
			resourcePrimKey, version, serviceContext);
	}

	@Override
	public void subscribeGroupKBArticles(long groupId, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.subscribeGroupKBArticles(groupId, portletId);
	}

	@Override
	public void subscribeKBArticle(long groupId, long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.subscribeKBArticle(groupId, resourcePrimKey);
	}

	@Override
	public void unlockKBArticle(long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.unlockKBArticle(resourcePrimKey);
	}

	@Override
	public void unsubscribeGroupKBArticles(long groupId, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.unsubscribeGroupKBArticles(groupId, portletId);
	}

	@Override
	public void unsubscribeKBArticle(long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.unsubscribeKBArticle(resourcePrimKey);
	}

	@Override
	public KBArticle updateAndUnlockKBArticle(
			long resourcePrimKey, String title, String content,
			String description, String[] sections, String sourceURL,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate, String[] selectedFileNames,
			long[] removeFileEntryIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.updateAndUnlockKBArticle(
			resourcePrimKey, title, content, description, sections, sourceURL,
			displayDate, expirationDate, reviewDate, selectedFileNames,
			removeFileEntryIds, serviceContext);
	}

	@Override
	public KBArticle updateKBArticle(
			long resourcePrimKey, String title, String content,
			String description, String[] sections, String sourceURL,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate, String[] selectedFileNames,
			long[] removeFileEntryIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbArticleService.updateKBArticle(
			resourcePrimKey, title, content, description, sections, sourceURL,
			displayDate, expirationDate, reviewDate, selectedFileNames,
			removeFileEntryIds, serviceContext);
	}

	@Override
	public void updateKBArticlesPriorities(
			long groupId,
			java.util.Map<Long, Double> resourcePrimKeyToPriorityMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbArticleService.updateKBArticlesPriorities(
			groupId, resourcePrimKeyToPriorityMap);
	}

	@Override
	public KBArticleService getWrappedService() {
		return _kbArticleService;
	}

	@Override
	public void setWrappedService(KBArticleService kbArticleService) {
		_kbArticleService = kbArticleService;
	}

	private KBArticleService _kbArticleService;

}
// LIFERAY-SERVICE-BUILDER-HASH:211734032