/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.search.experiences.constants.SXPActionKeys;
import com.liferay.search.experiences.constants.SXPConstants;
import com.liferay.search.experiences.exception.SXPElementReadOnlyException;
import com.liferay.search.experiences.model.SXPElement;
import com.liferay.search.experiences.service.base.SXPElementServiceBaseImpl;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false,
	property = {
		"json.web.service.context.name=sxp",
		"json.web.service.context.path=SXPElement"
	},
	service = AopService.class
)
public class SXPElementServiceImpl extends SXPElementServiceBaseImpl {

	@Override
	public SXPElement addSXPElement(
			String externalReferenceCode, Map<Locale, String> descriptionMap,
			String elementDefinitionJSON, String fallbackDescription,
			String fallbackTitle, boolean readOnly, String schemaVersion,
			Map<Locale, String> titleMap, int type,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null, SXPActionKeys.ADD_SXP_ELEMENT);

		return sxpElementLocalService.addSXPElement(
			externalReferenceCode, getUserId(), descriptionMap,
			elementDefinitionJSON, fallbackDescription, fallbackTitle, readOnly,
			schemaVersion, titleMap, type, serviceContext);
	}

	@Override
	public SXPElement deleteSXPElement(long sxpElementId)
		throws PortalException {

		_sxpElementModelResourcePermission.check(
			getPermissionChecker(), sxpElementId, ActionKeys.DELETE);

		SXPElement sxpElement = sxpElementPersistence.findByPrimaryKey(
			sxpElementId);

		if (sxpElement.isReadOnly()) {
			throw new SXPElementReadOnlyException(
				StringBundler.concat(
					"Search experiences element ", sxpElementId,
					" is read-only"));
		}

		return sxpElementLocalService.deleteSXPElement(sxpElement);
	}

	@Override
	public SXPElement fetchSXPElement(long sxpElementId)
		throws PortalException {

		SXPElement sxpElement = sxpElementPersistence.fetchByPrimaryKey(
			sxpElementId);

		if (sxpElement != null) {
			_sxpElementModelResourcePermission.check(
				getPermissionChecker(), sxpElement, ActionKeys.VIEW);
		}

		return sxpElement;
	}

	@Override
	public SXPElement fetchSXPElementByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		SXPElement sxpElement = sxpElementPersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (sxpElement != null) {
			_sxpElementModelResourcePermission.check(
				getPermissionChecker(), sxpElement, ActionKeys.VIEW);
		}

		return sxpElement;
	}

	@Override
	public SXPElement getSXPElement(long sxpElementId) throws PortalException {
		SXPElement sxpElement = sxpElementPersistence.findByPrimaryKey(
			sxpElementId);

		_sxpElementModelResourcePermission.check(
			getPermissionChecker(), sxpElement, ActionKeys.VIEW);

		return sxpElement;
	}

	@Override
	public SXPElement getSXPElementByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		SXPElement sxpElement = sxpElementPersistence.findByERC_C(
			externalReferenceCode, companyId);

		_sxpElementModelResourcePermission.check(
			getPermissionChecker(), sxpElement, ActionKeys.VIEW);

		return sxpElement;
	}

	@Override
	public SXPElement updateSXPElement(
			String externalReferenceCode, long sxpElementId,
			Map<Locale, String> descriptionMap, String elementDefinitionJSON,
			String schemaVersion, boolean hidden, Map<Locale, String> titleMap,
			ServiceContext serviceContext)
		throws PortalException {

		SXPElement sxpElement = sxpElementPersistence.findByPrimaryKey(
			sxpElementId);

		if (sxpElement.isReadOnly()) {
			throw new SXPElementReadOnlyException(
				StringBundler.concat(
					"Search experiences element ", sxpElementId,
					" is read-only"));
		}

		_sxpElementModelResourcePermission.check(
			getPermissionChecker(), sxpElementId, ActionKeys.UPDATE);

		return sxpElementLocalService.updateSXPElement(
			externalReferenceCode, getUserId(), sxpElementId, descriptionMap,
			elementDefinitionJSON, hidden, schemaVersion, titleMap,
			serviceContext);
	}

	@Reference(target = "(resource.name=" + SXPConstants.RESOURCE_NAME + ")")
	private volatile PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.search.experiences.model.SXPElement)"
	)
	private volatile ModelResourcePermission<SXPElement>
		_sxpElementModelResourcePermission;

}