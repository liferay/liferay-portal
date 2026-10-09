/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.validation;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.exception.DuplicateFolderNameException;
import com.liferay.journal.exception.InvalidDDMStructureException;
import com.liferay.journal.exception.InvalidFolderException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.service.persistence.JournalFolderPersistence;
import com.liferay.journal.util.JournalValidator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.validation.ModelValidationResults;
import com.liferay.portal.validation.ModelValidator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalFolder",
	service = ModelValidator.class
)
public class JournalFolderModelValidator
	implements ModelValidator<JournalFolder> {

	public void validateArticleDDMStructures(
			long folderId, long[] ddmStructureIds)
		throws PortalException {

		if (ArrayUtil.isEmpty(ddmStructureIds)) {
			return;
		}

		JournalFolder folder = _journalFolderPersistence.findByPrimaryKey(
			folderId);

		for (JournalArticle article :
				_journalArticleLocalService.getArticles(
					folder.getGroupId(), folderId)) {

			if (!ArrayUtil.contains(
					ddmStructureIds, article.getDDMStructureId())) {

				throw new InvalidDDMStructureException(
					"Invalid DDM structure " + article.getDDMStructureId());
			}
		}

		List<JournalFolder> folders = _journalFolderPersistence.findByG_P(
			folder.getGroupId(), folder.getFolderId());

		if (folders.isEmpty()) {
			return;
		}

		for (JournalFolder curFolder : folders) {
			if (curFolder.getRestrictionType() ==
					JournalFolderConstants.
						RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW) {

				continue;
			}

			validateArticleDDMStructures(
				curFolder.getFolderId(), ddmStructureIds);
		}
	}

	public void validateFolder(
			long folderId, long groupId, long parentFolderId, String name)
		throws PortalException {

		_journalValidator.validateFolderName(name);

		JournalFolder folder = _journalFolderPersistence.fetchByG_P_N(
			groupId, parentFolderId, name);

		if ((folder != null) && (folder.getFolderId() != folderId)) {
			throw new DuplicateFolderNameException(name);
		}
	}

	public void validateFolderDDMStructures(long folderId, long parentFolderId)
		throws PortalException {

		JournalFolder folder = _journalFolderLocalService.fetchFolder(folderId);

		int restrictionType =
			JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW;

		JournalFolder parentFolder = _journalFolderLocalService.fetchFolder(
			parentFolderId);

		if (parentFolder != null) {
			restrictionType = parentFolder.getRestrictionType();
		}

		validateArticleDDMStructures(
			folderId,
			TransformUtil.transformToLongArray(
				_journalFolderLocalService.getDDMStructures(
					_portal.getCurrentAndAncestorSiteGroupIds(
						folder.getGroupId()),
					parentFolderId, restrictionType),
				DDMStructure::getStructureId));
	}

	@Override
	public ModelValidationResults validateModel(JournalFolder folder) {
		long[] ddmStructureIds = null;

		try {
			ddmStructureIds = TransformUtil.transformToLongArray(
				_journalFolderLocalService.getDDMStructures(
					new long[] {folder.getGroupId()}, folder.getFolderId(),
					folder.getRestrictionType()),
				DDMStructure::getStructureId);
		}
		catch (PortalException portalException) {
			ModelValidationResults.FailureBuilder failureBuilder =
				ModelValidationResults.failure();

			return failureBuilder.exceptionFailure(
				"Unable to retrieve folder structures for validation: " +
					portalException.getMessage(),
				portalException
			).getResults();
		}

		long folderId = folder.getFolderId();

		try {
			validateArticleDDMStructures(folderId, ddmStructureIds);

			validateFolder(
				folderId, folder.getGroupId(), folder.getParentFolderId(),
				folder.getName());
		}
		catch (PortalException portalException) {
			ModelValidationResults.FailureBuilder failureBuilder =
				ModelValidationResults.failure();

			return failureBuilder.exceptionFailure(
				portalException.getMessage(), portalException
			).getResults();
		}

		return ModelValidationResults.success();
	}

	public void validateParentFolder(JournalFolder folder, long parentFolderId)
		throws PortalException {

		if (parentFolderId == JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		if (folder.getFolderId() == parentFolderId) {
			throw new InvalidFolderException(
				folder, InvalidFolderException.CANNOT_MOVE_INTO_ITSELF);
		}

		JournalFolder parentFolder =
			_journalFolderPersistence.fetchByPrimaryKey(parentFolderId);

		if (parentFolder == null) {
			throw new InvalidFolderException(
				InvalidFolderException.PARENT_FOLDER_DOES_NOT_EXIST);
		}

		if (folder.getGroupId() != parentFolder.getGroupId()) {
			throw new InvalidFolderException(
				InvalidFolderException.INVALID_GROUP);
		}

		List<Long> subfolderIds = new ArrayList<>();

		_journalFolderLocalService.getSubfolderIds(
			subfolderIds, folder.getGroupId(), folder.getFolderId());

		if (subfolderIds.contains(parentFolderId)) {
			throw new InvalidFolderException(
				folder, InvalidFolderException.CANNOT_MOVE_INTO_CHILD_FOLDER);
		}
	}

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference
	private JournalFolderPersistence _journalFolderPersistence;

	@Reference
	private JournalValidator _journalValidator;

	@Reference
	private Portal _portal;

}