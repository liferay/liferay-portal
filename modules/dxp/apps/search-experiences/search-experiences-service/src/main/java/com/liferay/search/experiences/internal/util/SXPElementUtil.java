/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.search.experiences.internal.model.listener.CompanyModelListener;
import com.liferay.search.experiences.rest.dto.v1_0.SXPElement;
import com.liferay.search.experiences.service.SXPElementLocalService;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Renan Vasconcelos
 */
public class SXPElementUtil {

	public static void addSXPElements(
			Company company, SXPElementLocalService sxpElementLocalService)
		throws PortalException {

		Set<String> externalReferenceCodes = new HashSet<>();

		for (com.liferay.search.experiences.model.SXPElement sxpElement :
				sxpElementLocalService.getSXPElements(
					company.getCompanyId(), true)) {

			externalReferenceCodes.add(sxpElement.getExternalReferenceCode());
		}

		for (SXPElement sxpElement : _getOrCreateSXPElements()) {
			User user = company.getGuestUser();

			sxpElementLocalService.addSXPElement(
				sxpElement.getExternalReferenceCode(), user.getUserId(),
				LocalizedMapUtil.getLocalizedMap(
					sxpElement.getDescription_i18n(), true),
				String.valueOf(sxpElement.getElementDefinition()),
				sxpElement.getDescription_i18n(
				).get(
					LocaleUtil.US.toString()
				),
				sxpElement.getTitle_i18n(
				).get(
					LocaleUtil.US.toString()
				),
				true, _SCHEMA_VERSION,
				LocalizedMapUtil.getLocalizedMap(
					sxpElement.getTitle_i18n(), true),
				0,
				new ServiceContext() {
					{
						setAddGuestPermissions(true);
						setCompanyId(company.getCompanyId());
						setScopeGroupId(company.getGroupId());
						setUserId(user.getUserId());
					}
				});
		}
	}

	private static List<SXPElement> _createSXPElements() {
		try {
			List<SXPElement> sxpElements = new ArrayList<>();

			Bundle bundle = FrameworkUtil.getBundle(CompanyModelListener.class);

			Package pkg = CompanyModelListener.class.getPackage();

			String path = StringUtil.replace(
				pkg.getName(), CharPool.PERIOD, CharPool.SLASH);

			Enumeration<URL> enumeration = bundle.findEntries(
				path.concat("/dependencies"), "*.json", false);

			while (enumeration.hasMoreElements()) {
				sxpElements.add(
					com.liferay.search.experiences.rest.dto.v1_0.util.
						SXPElementUtil.toSXPElement(
							URLUtil.toString(enumeration.nextElement())));
			}

			return sxpElements;
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}
	}

	private static List<SXPElement> _getOrCreateSXPElements() {
		if (_sxpElements == null) {
			_sxpElements = _createSXPElements();
		}

		return _sxpElements;
	}

	private static final String _SCHEMA_VERSION = StringUtil.replace(
		StringUtil.extractFirst(
			StringUtil.extractLast(SXPElement.class.getName(), ".v"),
			CharPool.PERIOD),
		CharPool.UNDERLINE, CharPool.PERIOD);

	private static List<SXPElement> _sxpElements;

}