/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.upgrade.registry;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.journal.content.web.internal.upgrade.v1_0_0.UpgradePortletId;
import com.liferay.journal.content.web.internal.upgrade.v1_0_0.UpgradePortletPreferences;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = UpgradeStepRegistrator.class)
public class JournalContentWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0", new UpgradePortletId(),
			new UpgradePortletPreferences());

		registry.register(
			"1.0.0", "1.1.0",
			new com.liferay.journal.content.web.internal.upgrade.v1_1_0.
				UpgradePortletPreferences(
					_groupLocalService, _journalArticleLocalService, _language,
					_layoutLocalService, _portal));

		registry.register(
			"1.1.0", "1.1.1",
			new com.liferay.journal.content.web.internal.upgrade.v1_1_1.
				UpgradePortletPreferences(
					_classNameLocalService.getClassNameId(DDMStructure.class),
					_ddmTemplateLocalService, _groupLocalService,
					_journalArticleLocalService, _layoutLocalService, _portal));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}