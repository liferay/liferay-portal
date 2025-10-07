/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.liferayrepository;

import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.kernel.repository.capabilities.BulkOperationCapability;
import com.liferay.portal.kernel.repository.capabilities.CommentCapability;
import com.liferay.portal.kernel.repository.capabilities.DynamicCapability;
import com.liferay.portal.kernel.repository.capabilities.FileEntryTypeCapability;
import com.liferay.portal.kernel.repository.capabilities.PortalCapabilityLocator;
import com.liferay.portal.kernel.repository.capabilities.ProcessorCapability;
import com.liferay.portal.kernel.repository.capabilities.RelatedModelCapability;
import com.liferay.portal.kernel.repository.capabilities.ThumbnailCapability;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.capabilities.WorkflowCapability;
import com.liferay.portal.kernel.repository.model.FileContentReference;
import com.liferay.portal.kernel.repository.model.ModelValidator;
import com.liferay.portal.kernel.repository.registry.BaseRepositoryDefiner;
import com.liferay.portal.kernel.repository.registry.CapabilityRegistry;
import com.liferay.portal.kernel.repository.registry.RepositoryDefiner;
import com.liferay.portal.kernel.repository.registry.RepositoryFactoryRegistry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.util.function.BiFunction;

/**
 * @author Adolfo Pérez
 */
public class LiferayRepositoryDefiner extends BaseRepositoryDefiner {

	public static final String CLASS_NAME = LiferayRepository.class.getName();

	public static BiFunction
		<PortalCapabilityLocator, RepositoryFactory, RepositoryDefiner>
			getFactoryBiFunction() {

		return LiferayRepositoryDefiner::new;
	}

	public LiferayRepositoryDefiner(
		PortalCapabilityLocator portalCapabilityLocator,
		RepositoryFactory repositoryFactory) {

		_portalCapabilityLocator = portalCapabilityLocator;
		_repositoryFactory = new LiferayRepositoryFactoryWrapper(
			repositoryFactory);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public boolean isExternalRepository() {
		return false;
	}

	@Override
	public void registerCapabilities(
		CapabilityRegistry<DocumentRepository> capabilityRegistry) {

		DocumentRepository documentRepository = capabilityRegistry.getTarget();

		capabilityRegistry.addExportedCapability(
			BulkOperationCapability.class,
			_portalCapabilityLocator.getBulkOperationCapability(
				documentRepository));

		if (PropsValues.DL_FILE_ENTRY_COMMENTS_ENABLED) {
			capabilityRegistry.addExportedCapability(
				CommentCapability.class,
				_portalCapabilityLocator.getCommentCapability(
					documentRepository));
		}

		capabilityRegistry.addExportedCapability(
			FileEntryTypeCapability.class,
			_portalCapabilityLocator.getFileEntryTypeCapability());
		capabilityRegistry.addExportedCapability(
			RelatedModelCapability.class,
			_portalCapabilityLocator.getRelatedModelCapability(
				documentRepository));
		capabilityRegistry.addExportedCapability(
			ThumbnailCapability.class,
			_portalCapabilityLocator.getThumbnailCapability(
				documentRepository));
		capabilityRegistry.addExportedCapability(
			TrashCapability.class,
			_portalCapabilityLocator.getTrashCapability(documentRepository));
		capabilityRegistry.addExportedCapability(
			WorkflowCapability.class,
			_portalCapabilityLocator.getWorkflowCapability(
				documentRepository, WorkflowCapability.OperationMode.FULL));

		capabilityRegistry.addSupportedCapability(
			DynamicCapability.class,
			_portalCapabilityLocator.getDynamicCapability(
				documentRepository, getClassName()));
		capabilityRegistry.addSupportedCapability(
			ProcessorCapability.class,
			_portalCapabilityLocator.getProcessorCapability(
				documentRepository,
				ProcessorCapability.ResourceGenerationStrategy.REUSE));
	}

	@Override
	public void registerRepositoryFactory(
		RepositoryFactoryRegistry repositoryFactoryRegistry) {

		repositoryFactoryRegistry.setRepositoryFactory(_repositoryFactory);
	}

	private static long _getCompanyId(long groupId) {
		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			return CompanyThreadLocal.getCompanyId();
		}

		return group.getCompanyId();
	}

	private final PortalCapabilityLocator _portalCapabilityLocator;
	private RepositoryFactory _repositoryFactory;

	private static class LiferayRepositoryFactoryWrapper
		implements RepositoryFactory {

		public LiferayRepositoryFactoryWrapper(
			RepositoryFactory repositoryFactory) {

			_repositoryFactory = repositoryFactory;
		}

		@Override
		public LocalRepository createLocalRepository(long repositoryId)
			throws PortalException {

			return new ModelValidatorLocalRepositoryWrapper(
				_repositoryFactory.createLocalRepository(repositoryId),
				_modelValidator);
		}

		@Override
		public Repository createRepository(long repositoryId)
			throws PortalException {

			return new ModelValidatorRepositoryWrapper(
				_repositoryFactory.createRepository(repositoryId),
				_modelValidator);
		}

		private static final ModelValidator<FileContentReference>
			_modelValidator = fileContentReference -> {
				if (Validator.isNotNull(
						fileContentReference.getSourceFileName())) {

					DLValidatorUtil.validateFileName(
						fileContentReference.getSourceFileName());

					if (fileContentReference.getFileEntryId() == 0) {
						DLValidatorUtil.validateFileExtension(
							fileContentReference.getSourceFileName());

						DLValidatorUtil.validateSourceFileExtension(
							fileContentReference.getExtension(),
							fileContentReference.getSourceFileName());
					}

					if (fileContentReference.getSize() != 0) {
						DLValidatorUtil.validateFileMimeType(
							_getCompanyId(fileContentReference.getGroupId()),
							fileContentReference.getMimeType());
					}
				}

				DLValidatorUtil.validateFileSize(
					fileContentReference.getGroupId(),
					fileContentReference.getSourceFileName(),
					fileContentReference.getMimeType(),
					fileContentReference.getSize());
			};

		private final RepositoryFactory _repositoryFactory;

	}

}