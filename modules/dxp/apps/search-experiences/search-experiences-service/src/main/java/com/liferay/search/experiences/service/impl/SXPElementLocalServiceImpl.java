/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.search.experiences.exception.SXPElementTitleException;
import com.liferay.search.experiences.model.SXPElement;
import com.liferay.search.experiences.service.base.SXPElementLocalServiceBaseImpl;
import com.liferay.search.experiences.validator.SXPElementValidator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false,
	property = "model.class.name=com.liferay.search.experiences.model.SXPElement",
	service = AopService.class
)
public class SXPElementLocalServiceImpl extends SXPElementLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public SXPElement addSXPElement(
			String externalReferenceCode, long userId,
			Map<Locale, String> descriptionMap, String elementDefinitionJSON,
			String fallbackDescription, String fallbackTitle, boolean readOnly,
			String schemaVersion, Map<Locale, String> titleMap, int type,
			ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNull(fallbackDescription)) {
			fallbackDescription = descriptionMap.get(LocaleUtil.getDefault());
		}

		if (Validator.isNull(fallbackTitle)) {
			fallbackTitle = titleMap.get(LocaleUtil.getDefault());
		}

		_validate(titleMap, type, serviceContext);

		SXPElement sxpElement = createSXPElement(
			counterLocalService.increment(SXPElement.class.getName()));

		sxpElement.setExternalReferenceCode(externalReferenceCode);

		User user = _userLocalService.getUser(userId);

		sxpElement.setCompanyId(user.getCompanyId());
		sxpElement.setUserId(user.getUserId());
		sxpElement.setUserName(user.getFullName());

		sxpElement.setDescriptionMap(descriptionMap);
		sxpElement.setElementDefinitionJSON(elementDefinitionJSON);
		sxpElement.setFallbackDescription(fallbackDescription);
		sxpElement.setFallbackTitle(fallbackTitle);
		sxpElement.setHidden(false);
		sxpElement.setReadOnly(readOnly);
		sxpElement.setSchemaVersion(schemaVersion);
		sxpElement.setTitleMap(titleMap);
		sxpElement.setType(type);
		sxpElement.setVersion(
			String.format(
				"%.1f",
				GetterUtil.getFloat(sxpElement.getVersion(), 0.9F) + 0.1));

		sxpElement = sxpElementPersistence.update(sxpElement);

		_resourceLocalService.addModelResources(sxpElement, serviceContext);

		return sxpElement;
	}

	@Override
	public void deleteCompanySXPElements(long companyId)
		throws PortalException {

		List<SXPElement> sxpElements = sxpElementPersistence.findByCompanyId(
			companyId);

		for (SXPElement sxpElement : sxpElements) {
			sxpElementLocalService.deleteSXPElement(sxpElement);
		}
	}

	@Override
	public SXPElement deleteSXPElement(long sxpElementId)
		throws PortalException {

		SXPElement sxpElement = sxpElementPersistence.findByPrimaryKey(
			sxpElementId);

		return deleteSXPElement(sxpElement);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SXPElement deleteSXPElement(SXPElement sxpElement)
		throws PortalException {

		sxpElement = sxpElementPersistence.remove(sxpElement);

		_resourceLocalService.deleteResource(
			sxpElement, ResourceConstants.SCOPE_INDIVIDUAL);

		return sxpElement;
	}

	@Override
	public List<SXPElement> getSXPElements(long companyId, boolean readOnly) {
		return sxpElementPersistence.findByC_R(companyId, readOnly);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public SXPElement updateSXPElement(
			String externalReferenceCode, long userId, long sxpElementId,
			Map<Locale, String> descriptionMap, String elementDefinitionJSON,
			boolean hidden, String schemaVersion, Map<Locale, String> titleMap,
			ServiceContext serviceContext)
		throws PortalException {

		SXPElement sxpElement = getSXPElement(sxpElementId);

		_validate(titleMap, sxpElement.getType(), serviceContext);

		sxpElement.setExternalReferenceCode(externalReferenceCode);
		sxpElement.setDescriptionMap(descriptionMap);
		sxpElement.setElementDefinitionJSON(elementDefinitionJSON);
		sxpElement.setHidden(hidden);
		sxpElement.setSchemaVersion(schemaVersion);
		sxpElement.setTitleMap(titleMap);
		sxpElement.setVersion(
			String.format(
				"%.1f",
				GetterUtil.getFloat(sxpElement.getVersion(), 0.9F) + 0.1));

		return updateSXPElement(sxpElement);
	}

	private void _validate(
			Map<Locale, String> titleMap, int type,
			ServiceContext serviceContext)
		throws SXPElementTitleException {

		if (GetterUtil.getBoolean(
				serviceContext.getAttribute(
					SXPElementLocalServiceImpl.class.getName() + "#_validate"),
				true)) {

			_sxpElementValidator.validate(titleMap, type);
		}
	}

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SXPElementValidator _sxpElementValidator;

	@Reference
	private UserLocalService _userLocalService;

}