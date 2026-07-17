/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.impl;

import com.liferay.audiences.exception.AudiencesEntryJSONException;
import com.liferay.audiences.exception.AudiencesEntryNameException;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.base.AudiencesEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.json.validator.JSONValidator;
import com.liferay.portal.json.validator.JSONValidatorException;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.audiences.model.AudiencesEntry",
	service = AopService.class
)
public class AudiencesEntryLocalServiceImpl
	extends AudiencesEntryLocalServiceBaseImpl {

	@Override
	public AudiencesEntry addAudiencesEntry(
			String externalReferenceCode, long userId, String json, String name)
		throws PortalException {

		_validate(json, name);

		AudiencesEntry audiencesEntry = audiencesEntryPersistence.create(
			counterLocalService.increment());

		audiencesEntry.setExternalReferenceCode(externalReferenceCode);

		User user = _userLocalService.getUser(userId);

		audiencesEntry.setCompanyId(user.getCompanyId());
		audiencesEntry.setUserId(user.getUserId());
		audiencesEntry.setUserName(user.getFullName());

		audiencesEntry.setJSON(json);
		audiencesEntry.setName(name);

		return audiencesEntryPersistence.update(audiencesEntry);
	}

	@Override
	public AudiencesEntry deleteAudiencesEntry(long audiencesEntryId)
		throws PortalException {

		AudiencesEntry audiencesEntry =
			audiencesEntryPersistence.findByPrimaryKey(audiencesEntryId);

		return audiencesEntryLocalService.deleteAudiencesEntry(audiencesEntry);
	}

	@Override
	public List<AudiencesEntry> getAudiencesEntries(
		long companyId, int start, int end,
		OrderByComparator<AudiencesEntry> orderByComparator) {

		return audiencesEntryPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public List<AudiencesEntry> getAudiencesEntries(
			long companyId, String name, int start, int end,
			OrderByComparator<AudiencesEntry> orderByComparator)
		throws PortalException {

		return audiencesEntryPersistence.findByC_LikeN(
			companyId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], start,
			end, orderByComparator);
	}

	@Override
	public int getAudiencesEntriesCount(long companyId) {
		return audiencesEntryPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getAudiencesEntriesCount(long companyId, String name) {
		return audiencesEntryPersistence.countByC_LikeN(
			companyId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0]);
	}

	@Override
	public AudiencesEntry updateAudiencesEntry(
			String externalReferenceCode, long userId, long audiencesEntryId,
			String json, String name)
		throws PortalException {

		_validate(json, name);

		AudiencesEntry audiencesEntry =
			audiencesEntryPersistence.findByPrimaryKey(audiencesEntryId);

		audiencesEntry.setExternalReferenceCode(externalReferenceCode);

		User user = _userLocalService.getUser(userId);

		audiencesEntry.setUserId(user.getUserId());
		audiencesEntry.setUserName(user.getFullName());

		audiencesEntry.setJSON(json);
		audiencesEntry.setName(name);

		return audiencesEntryPersistence.update(audiencesEntry);
	}

	private void _validate(String json, String name) throws PortalException {
		try {
			_criteriaJSONValidator.validate(json);
		}
		catch (JSONValidatorException jsonValidatorException) {
			throw new AudiencesEntryJSONException(
				jsonValidatorException.getMessage(), jsonValidatorException);
		}

		if (Validator.isNull(name)) {
			throw new AudiencesEntryNameException();
		}
	}

	private static final JSONValidator _criteriaJSONValidator =
		new JSONValidator(
			AudiencesEntryLocalServiceImpl.class.getResource(
				"dependencies/audiences-criteria-json-schema.json"));

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private UserLocalService _userLocalService;

}