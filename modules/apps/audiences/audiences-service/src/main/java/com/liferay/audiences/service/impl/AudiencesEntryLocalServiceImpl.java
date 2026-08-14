/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.impl;

import com.liferay.audiences.criteria.AudiencesCriteria;
import com.liferay.audiences.criteria.AudiencesCriteriaProvider;
import com.liferay.audiences.criteria.AudiencesCriteriaType;
import com.liferay.audiences.exception.AudiencesEntryAttributeException;
import com.liferay.audiences.exception.AudiencesEntryJSONException;
import com.liferay.audiences.exception.AudiencesEntryNameException;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.base.AudiencesEntryLocalServiceBaseImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.json.validator.JSONValidator;
import com.liferay.portal.json.validator.JSONValidatorException;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

		User user = _userLocalService.getUser(userId);

		_validate(user.getCompanyId(), json, name);

		AudiencesEntry audiencesEntry = audiencesEntryPersistence.create(
			counterLocalService.increment());

		audiencesEntry.setExternalReferenceCode(externalReferenceCode);

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

		AudiencesEntry audiencesEntry =
			audiencesEntryPersistence.findByPrimaryKey(audiencesEntryId);

		_validate(audiencesEntry.getCompanyId(), json, name);

		audiencesEntry.setExternalReferenceCode(externalReferenceCode);

		User user = _userLocalService.getUser(userId);

		audiencesEntry.setUserId(user.getUserId());
		audiencesEntry.setUserName(user.getFullName());

		audiencesEntry.setJSON(json);
		audiencesEntry.setName(name);

		return audiencesEntryPersistence.update(audiencesEntry);
	}

	private void _validate(long companyId, String json, String name)
		throws PortalException {

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

		_validateAttributes(companyId, json);
	}

	private void _validateAttributes(
			JSONObject jsonObject, Set<String> validAttributes)
		throws PortalException {

		JSONArray rulesJSONArray = jsonObject.getJSONArray("rules");

		if (rulesJSONArray == null) {
			String attribute = jsonObject.getString("attribute");

			if (attribute.startsWith("custom:") &&
				!validAttributes.contains(attribute)) {

				throw new AudiencesEntryAttributeException(
					StringBundler.concat(
						"Attribute \"", attribute,
						"\" is not a valid custom attribute"));
			}

			return;
		}

		for (int i = 0; i < rulesJSONArray.length(); i++) {
			_validateAttributes(
				rulesJSONArray.getJSONObject(i), validAttributes);
		}
	}

	private void _validateAttributes(long companyId, String json)
		throws PortalException {

		if (Validator.isNull(json)) {
			return;
		}

		Set<String> validAttributes = new HashSet<>();

		for (AudiencesCriteriaType audiencesCriteriaType :
				_audiencesCriteriaProvider.getAudiencesCriteriaTypes(
					companyId, LocaleUtil.getSiteDefault())) {

			for (AudiencesCriteria audiencesCriteria :
					audiencesCriteriaType.getAudiencesCriterias()) {

				validAttributes.add(audiencesCriteria.getKey());
			}
		}

		_validateAttributes(
			_jsonFactory.createJSONObject(json), validAttributes);
	}

	private static final JSONValidator _criteriaJSONValidator =
		new JSONValidator(
			AudiencesEntryLocalServiceImpl.class.getResource(
				"dependencies/audiences-criteria-json-schema.json"));

	@Reference
	private AudiencesCriteriaProvider _audiencesCriteriaProvider;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private UserLocalService _userLocalService;

}