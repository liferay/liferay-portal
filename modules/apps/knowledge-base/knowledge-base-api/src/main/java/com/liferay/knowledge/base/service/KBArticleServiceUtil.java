/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service;

import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for KBArticle. This utility wraps
 * <code>com.liferay.knowledge.base.service.impl.KBArticleServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see KBArticleService
 * @generated
 */
public class KBArticleServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.knowledge.base.service.impl.KBArticleServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static KBArticle addKBArticle(
			String externalReferenceCode, String portletId,
			long parentResourceClassNameId, long parentResourcePrimKey,
			String title, String urlTitle, String content, String description,
			String[] sections, String sourceURL, java.util.Date displayDate,
			java.util.Date expirationDate, java.util.Date reviewDate,
			String[] selectedFileNames,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKBArticle(
			externalReferenceCode, portletId, parentResourceClassNameId,
			parentResourcePrimKey, title, urlTitle, content, description,
			sections, sourceURL, displayDate, expirationDate, reviewDate,
			selectedFileNames, serviceContext);
	}

	public static int addKBArticlesMarkdown(
			long groupId, long parentKBFolderId, String fileName,
			boolean prioritizeByNumericalPrefix, InputStream inputStream,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKBArticlesMarkdown(
			groupId, parentKBFolderId, fileName, prioritizeByNumericalPrefix,
			inputStream, serviceContext);
	}

	public static void addTempAttachment(
			long groupId, long resourcePrimKey, String fileName,
			String tempFolderName, InputStream inputStream, String mimeType)
		throws PortalException {

		getService().addTempAttachment(
			groupId, resourcePrimKey, fileName, tempFolderName, inputStream,
			mimeType);
	}

	public static int countKBArticlesByKeywords(
		long groupId, String keywords, int status) {

		return getService().countKBArticlesByKeywords(
			groupId, keywords, status);
	}

	public static KBArticle deleteKBArticle(long resourcePrimKey)
		throws PortalException {

		return getService().deleteKBArticle(resourcePrimKey);
	}

	public static void deleteKBArticleAttachment(long fileEntryId)
		throws PortalException {

		getService().deleteKBArticleAttachment(fileEntryId);
	}

	public static void deleteKBArticles(long groupId, long[] resourcePrimKeys)
		throws PortalException {

		getService().deleteKBArticles(groupId, resourcePrimKeys);
	}

	public static void deleteTempAttachment(
			long groupId, long resourcePrimKey, String fileName,
			String tempFolderName)
		throws PortalException {

		getService().deleteTempAttachment(
			groupId, resourcePrimKey, fileName, tempFolderName);
	}

	public static KBArticle expireKBArticle(
			long resourcePrimKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().expireKBArticle(resourcePrimKey, serviceContext);
	}

	public static KBArticle fetchFirstChildKBArticle(
		long groupId, long parentResourcePrimKey) {

		return getService().fetchFirstChildKBArticle(
			groupId, parentResourcePrimKey);
	}

	public static KBArticle fetchFirstChildKBArticle(
		long groupId, long parentResourcePrimKey, int status) {

		return getService().fetchFirstChildKBArticle(
			groupId, parentResourcePrimKey, status);
	}

	public static KBArticle fetchKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle)
		throws PortalException {

		return getService().fetchKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle);
	}

	public static KBArticle fetchLatestKBArticle(
			long resourcePrimKey, int status)
		throws PortalException {

		return getService().fetchLatestKBArticle(resourcePrimKey, status);
	}

	public static KBArticle fetchLatestKBArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		return getService().fetchLatestKBArticleByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	public static KBArticle fetchLatestKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle, int status)
		throws PortalException {

		return getService().fetchLatestKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle, status);
	}

	public static com.liferay.portal.kernel.lock.Lock forceLockKBArticle(
			long groupId, long resourcePrimKey)
		throws PortalException {

		return getService().forceLockKBArticle(groupId, resourcePrimKey);
	}

	public static List<KBArticle> getAllDescendantKBArticles(
			long groupId, long resourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws PortalException {

		return getService().getAllDescendantKBArticles(
			groupId, resourcePrimKey, status, orderByComparator);
	}

	public static List<KBArticle> getGroupKBArticles(
		long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getGroupKBArticles(
			groupId, status, start, end, orderByComparator);
	}

	public static int getGroupKBArticlesCount(long groupId, int status) {
		return getService().getGroupKBArticlesCount(groupId, status);
	}

	public static String getGroupKBArticlesRSS(
			int status, int max, String type, double version,
			String displayStyle,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws PortalException {

		return getService().getGroupKBArticlesRSS(
			status, max, type, version, displayStyle, themeDisplay);
	}

	public static KBArticle getKBArticle(long resourcePrimKey, int version)
		throws PortalException {

		return getService().getKBArticle(resourcePrimKey, version);
	}

	public static List<KBArticle> getKBArticleAndAllDescendantKBArticles(
			long resourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws PortalException {

		return getService().getKBArticleAndAllDescendantKBArticles(
			resourcePrimKey, status, orderByComparator);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			getKBArticleAttachment(long fileEntryId)
		throws PortalException {

		return getService().getKBArticleAttachment(fileEntryId);
	}

	public static String getKBArticleRSS(
			long resourcePrimKey, int status, int max, String type,
			double version, String displayStyle,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws PortalException {

		return getService().getKBArticleRSS(
			resourcePrimKey, status, max, type, version, displayStyle,
			themeDisplay);
	}

	public static List<KBArticle> getKBArticles(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getService().getKBArticles(
			groupId, parentResourcePrimKey, status, start, end,
			orderByComparator);
	}

	public static List<KBArticle> getKBArticles(
		long groupId, long[] resourcePrimKeys, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getKBArticles(
			groupId, resourcePrimKeys, status, start, end, orderByComparator);
	}

	public static List<KBArticle> getKBArticles(
		long groupId, long[] resourcePrimKeys, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getKBArticles(
			groupId, resourcePrimKeys, status, orderByComparator);
	}

	public static List<KBArticle> getKBArticlesByKeywords(
		long groupId, String keywords, int status, int start, int end) {

		return getService().getKBArticlesByKeywords(
			groupId, keywords, status, start, end);
	}

	public static int getKBArticlesCount(
		long groupId, long parentResourcePrimKey, int status) {

		return getService().getKBArticlesCount(
			groupId, parentResourcePrimKey, status);
	}

	public static int getKBArticlesCount(
		long groupId, long[] resourcePrimKeys, int status) {

		return getService().getKBArticlesCount(
			groupId, resourcePrimKeys, status);
	}

	public static com.liferay.knowledge.base.model.KBArticleSearchDisplay
			getKBArticleSearchDisplay(
				long groupId, String title, String content, int status,
				java.util.Date startDate, java.util.Date endDate,
				boolean andOperator, int[] curStartValues, int cur, int delta,
				OrderByComparator<KBArticle> orderByComparator)
		throws PortalException {

		return getService().getKBArticleSearchDisplay(
			groupId, title, content, status, startDate, endDate, andOperator,
			curStartValues, cur, delta, orderByComparator);
	}

	public static List<KBArticle> getKBArticleVersions(
		long groupId, long resourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getKBArticleVersions(
			groupId, resourcePrimKey, status, start, end, orderByComparator);
	}

	public static int getKBArticleVersionsCount(
		long groupId, long resourcePrimKey, int status) {

		return getService().getKBArticleVersionsCount(
			groupId, resourcePrimKey, status);
	}

	public static KBArticle getLatestKBArticle(long resourcePrimKey)
		throws PortalException {

		return getService().getLatestKBArticle(resourcePrimKey);
	}

	public static KBArticle getLatestKBArticle(long resourcePrimKey, int status)
		throws PortalException {

		return getService().getLatestKBArticle(resourcePrimKey, status);
	}

	public static KBArticle getLatestKBArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		return getService().getLatestKBArticleByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static KBArticle[] getPreviousAndNextKBArticles(long kbArticleId)
		throws PortalException {

		return getService().getPreviousAndNextKBArticles(kbArticleId);
	}

	public static List<KBArticle> getSectionsKBArticles(
		long groupId, String[] sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getSectionsKBArticles(
			groupId, sections, status, start, end, orderByComparator);
	}

	public static int getSectionsKBArticlesCount(
		long groupId, String[] sections, int status) {

		return getService().getSectionsKBArticlesCount(
			groupId, sections, status);
	}

	public static String[] getTempAttachmentNames(
			long groupId, String tempFolderName)
		throws PortalException {

		return getService().getTempAttachmentNames(groupId, tempFolderName);
	}

	public static com.liferay.portal.kernel.lock.Lock lockKBArticle(
			long resourcePrimKey)
		throws PortalException {

		return getService().lockKBArticle(resourcePrimKey);
	}

	public static void moveKBArticle(
			long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey, double priority)
		throws PortalException {

		getService().moveKBArticle(
			resourcePrimKey, parentResourceClassNameId, parentResourcePrimKey,
			priority);
	}

	public static KBArticle moveKBArticleToTrash(long resourcePrimKey)
		throws PortalException {

		return getService().moveKBArticleToTrash(resourcePrimKey);
	}

	public static KBArticle revertKBArticle(
			long resourcePrimKey, int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().revertKBArticle(
			resourcePrimKey, version, serviceContext);
	}

	public static void subscribeGroupKBArticles(long groupId, String portletId)
		throws PortalException {

		getService().subscribeGroupKBArticles(groupId, portletId);
	}

	public static void subscribeKBArticle(long groupId, long resourcePrimKey)
		throws PortalException {

		getService().subscribeKBArticle(groupId, resourcePrimKey);
	}

	public static void unlockKBArticle(long resourcePrimKey)
		throws PortalException {

		getService().unlockKBArticle(resourcePrimKey);
	}

	public static void unsubscribeGroupKBArticles(
			long groupId, String portletId)
		throws PortalException {

		getService().unsubscribeGroupKBArticles(groupId, portletId);
	}

	public static void unsubscribeKBArticle(long resourcePrimKey)
		throws PortalException {

		getService().unsubscribeKBArticle(resourcePrimKey);
	}

	public static KBArticle updateAndUnlockKBArticle(
			long resourcePrimKey, String title, String content,
			String description, String[] sections, String sourceURL,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate, String[] selectedFileNames,
			long[] removeFileEntryIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAndUnlockKBArticle(
			resourcePrimKey, title, content, description, sections, sourceURL,
			displayDate, expirationDate, reviewDate, selectedFileNames,
			removeFileEntryIds, serviceContext);
	}

	public static KBArticle updateKBArticle(
			long resourcePrimKey, String title, String content,
			String description, String[] sections, String sourceURL,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate, String[] selectedFileNames,
			long[] removeFileEntryIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateKBArticle(
			resourcePrimKey, title, content, description, sections, sourceURL,
			displayDate, expirationDate, reviewDate, selectedFileNames,
			removeFileEntryIds, serviceContext);
	}

	public static void updateKBArticlesPriorities(
			long groupId, Map<Long, Double> resourcePrimKeyToPriorityMap)
		throws PortalException {

		getService().updateKBArticlesPriorities(
			groupId, resourcePrimKeyToPriorityMap);
	}

	public static KBArticleService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KBArticleService> _serviceSnapshot =
		new Snapshot<>(KBArticleServiceUtil.class, KBArticleService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-656320330