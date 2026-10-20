/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.upgrade.registry;

import com.liferay.content.marketing.platform.web.internal.upgrade.v2_0_0.CMPDescriptionObjectFieldUpgradeProcess;
import com.liferay.content.marketing.platform.web.internal.upgrade.v2_1_0.CMPDescriptionDisplayPageTemplateUpgradeProcess;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Díaz
 */
@Component(service = UpgradeStepRegistrator.class)
public class CMPSiteInitializerUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"1.0.0", "2.0.0",
			new CMPDescriptionObjectFieldUpgradeProcess(
				_companyLocalService, _objectDefinitionLocalService,
				_objectFieldLocalService));

		registry.register(
			"2.0.0", "2.1.0",
			new CMPDescriptionDisplayPageTemplateUpgradeProcess(
				_classNameLocalService, _companyLocalService,
				_fragmentEntryLinkLocalService, _groupLocalService,
				_layoutLocalService, _layoutPageTemplateEntryLocalService,
				_objectDefinitionLocalService));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}