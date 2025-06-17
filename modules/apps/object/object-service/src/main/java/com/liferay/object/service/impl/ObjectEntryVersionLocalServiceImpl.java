/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.configuration.ObjectEntryVersionConfiguration;
import com.liferay.object.entry.util.ObjectEntryDTOConverterUtil;
import com.liferay.object.exception.RequiredObjectEntryVersionException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.service.base.ObjectEntryVersionLocalServiceBaseImpl;
import com.liferay.object.util.comparator.ObjectEntryVersionCreateDateComparator;
import com.liferay.object.util.comparator.ObjectEntryVersionVersionComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectEntryVersion",
	service = AopService.class
)
public class ObjectEntryVersionLocalServiceImpl
	extends ObjectEntryVersionLocalServiceBaseImpl {

	@Override
	public ObjectEntryVersion addObjectEntryVersion(ObjectEntry objectEntry)
		throws PortalException {

		ObjectEntryVersion objectEntryVersion = _updateObjectEntryVersion(
			objectEntry,
			objectEntryVersionPersistence.create(
				counterLocalService.increment()),
			objectEntry.getVersion() + 1);

		if (_exceedsMaximumVersions(objectEntry.getObjectEntryId())) {
			ObjectEntryVersion oldestObjectEntryVersion =
				objectEntryVersionPersistence.findByObjectEntryId_First(
					objectEntry.getObjectEntryId(),
					ObjectEntryVersionCreateDateComparator.getInstance(true));

			if (oldestObjectEntryVersion != null) {
				deleteObjectEntryVersion(
					objectEntry.getObjectEntryId(),
					oldestObjectEntryVersion.getVersion());
			}
		}

		return objectEntryVersion;
	}

	@Override
	public ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException {

		if (getObjectEntryVersionsCount(objectEntryId) == 1) {
			throw new RequiredObjectEntryVersionException.MustHaveOneVersion(
				"At least one version must remain",
				"at-least-one-version-must-remain");
		}

		ObjectEntryVersion objectEntryVersion =
			objectEntryVersionPersistence.fetchByObjectEntryId_First(
				objectEntryId,
				ObjectEntryVersionVersionComparator.getInstance(false));

		if (version == objectEntryVersion.getVersion()) {
			throw new RequiredObjectEntryVersionException.
				MustNotDeleteLatestVersion(
					"The latest version cannot be deleted",
					"the-last-version-cannot-be-deleted");
		}

		objectEntryVersion = objectEntryVersionPersistence.findByOEI_V(
			objectEntryId, version);

		return deleteObjectEntryVersion(objectEntryVersion);
	}

	@Override
	public void deleteObjectEntryVersionByObjectDefinitionId(
		Long objectDefinitionId) {

		objectEntryVersionPersistence.removeByObjectDefinitionId(
			objectDefinitionId);
	}

	@Override
	public void deleteObjectEntryVersions(long objectEntryId) {
		objectEntryVersionPersistence.removeByObjectEntryId(objectEntryId);
	}

	@Override
	public ObjectEntryVersion expireObjectEntryVersion(
			long userId, long objectEntryId, int version)
		throws PortalException {

		ObjectEntryVersion objectEntryVersion =
			objectEntryVersionPersistence.findByOEI_V(objectEntryId, version);

		if (objectEntryVersion.isDraft() || objectEntryVersion.isExpired() ||
			objectEntryVersion.isPending()) {

			return objectEntryVersion;
		}

		Date date = new Date();

		objectEntryVersion.setExpirationDate(date);

		objectEntryVersion.setStatus(WorkflowConstants.STATUS_EXPIRED);

		User user = _userLocalService.getUser(userId);

		objectEntryVersion.setStatusByUserId(user.getUserId());
		objectEntryVersion.setStatusByUserName(user.getFullName());

		objectEntryVersion.setStatusDate(date);

		return objectEntryVersionPersistence.update(objectEntryVersion);
	}

	@Override
	public ObjectEntryVersion getObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException {

		return objectEntryVersionPersistence.findByOEI_V(
			objectEntryId, version);
	}

	@Override
	public List<ObjectEntryVersion> getObjectEntryVersions(long objectEntryId) {
		return objectEntryVersionPersistence.findByObjectEntryId(objectEntryId);
	}

	@Override
	public List<ObjectEntryVersion> getObjectEntryVersions(
		long objectEntryId, int start, int end) {

		return objectEntryVersionPersistence.findByObjectEntryId(
			objectEntryId, start, end);
	}

	@Override
	public int getObjectEntryVersionsCount(long objectEntryId) {
		return objectEntryVersionPersistence.countByObjectEntryId(
			objectEntryId);
	}

	@Override
	public ObjectEntryVersion updateLatestObjectEntryVersion(
			ObjectEntry objectEntry)
		throws PortalException {

		return _updateObjectEntryVersion(
			objectEntry,
			objectEntryVersionPersistence.fetchByObjectEntryId_First(
				objectEntry.getObjectEntryId(),
				ObjectEntryVersionVersionComparator.getInstance(false)),
			objectEntry.getVersion());
	}

	private boolean _exceedsMaximumVersions(long objectEntryId) {
		boolean exceedsMaximumVersions = false;

		int count = getObjectEntryVersionsCount(objectEntryId);

		if (count <= 0) {
			return exceedsMaximumVersions;
		}

		try {
			_objectEntryVersionConfiguration =
				_configurationProvider.getCompanyConfiguration(
					ObjectEntryVersionConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			if (_objectEntryVersionConfiguration == null) {
				_objectEntryVersionConfiguration =
					_configurationProvider.getSystemConfiguration(
						ObjectEntryVersionConfiguration.class);
			}
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}

		int maximumVersionsPerEntry =
			_objectEntryVersionConfiguration.maximumVersionsPerEntry();

		if ((maximumVersionsPerEntry > 0) &&
			(count > maximumVersionsPerEntry)) {

			exceedsMaximumVersions = true;
		}

		return exceedsMaximumVersions;
	}

	private ObjectEntryVersion _updateObjectEntryVersion(
			ObjectEntry objectEntry, ObjectEntryVersion objectEntryVersion,
			int version)
		throws PortalException {

		User user = _userLocalService.getUser(objectEntry.getUserId());

		objectEntryVersion.setUserId(user.getUserId());
		objectEntryVersion.setUserName(user.getFullName());

		objectEntryVersion.setCreateDate(objectEntry.getCreateDate());
		objectEntryVersion.setModifiedDate(objectEntry.getModifiedDate());
		objectEntryVersion.setObjectDefinitionId(
			objectEntry.getObjectDefinitionId());
		objectEntryVersion.setObjectEntryId(objectEntry.getObjectEntryId());

		try {
			objectEntryVersion.setContent(
				ObjectEntryDTOConverterUtil.toDTO(
					_dtoConverterRegistry, _jsonFactory, objectEntry, user));
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}

		if (FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-17564")) {

			objectEntryVersion.setDisplayDate(objectEntry.getDisplayDate());
		}

		Date date = new Date();
		Date expirationDate = objectEntryVersion.getExpirationDate();
		int status = objectEntry.getStatus();

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(expirationDate != null) && expirationDate.before(date)) {

			objectEntryVersion.setExpirationDate(null);
		}

		if ((status == WorkflowConstants.STATUS_EXPIRED) &&
			(expirationDate == null)) {

			objectEntryVersion.setExpirationDate(date);
		}

		if (FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-17564")) {

			objectEntryVersion.setReviewDate(objectEntry.getReviewDate());
		}

		objectEntryVersion.setVersion(version);
		objectEntryVersion.setStatus(status);
		objectEntryVersion.setStatusByUserId(user.getUserId());
		objectEntryVersion.setStatusByUserName(user.getFullName());
		objectEntryVersion.setStatusDate(date);

		return objectEntryVersionPersistence.update(objectEntryVersion);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	private volatile ObjectEntryVersionConfiguration
		_objectEntryVersionConfiguration;

	@Reference
	private UserLocalService _userLocalService;

}