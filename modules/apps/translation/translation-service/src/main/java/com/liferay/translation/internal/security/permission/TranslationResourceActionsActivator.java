/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.security.permission;

import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia Garcia
 */
@Component(service = {})
public class TranslationResourceActionsActivator {

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		String xml = StringUtil.read(
			TranslationResourceActionsActivator.class.getClassLoader(),
			"/com/liferay/translation/internal/security/permission" +
				"/dependencies/resource-actions.xml.tpl");

		String[] languageIds = ArrayUtil.sortedUnique(PropsValues.LOCALES);

		for (int i = 0; i < languageIds.length; i++) {
			_resourceActions.populateModelResources(
				SAXReaderUtil.read(
					StringUtil.replace(
						StringUtil.replace(
							xml, "[$LANGUAGE_ID$]", languageIds[i]),
						"[$WEIGHT$]", String.valueOf(i))));
		}
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private ResourceActions _resourceActions;

}