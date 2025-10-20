/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.upgrade.registry;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.knowledge.base.internal.upgrade.v3_0_0.util.KBArticleTable;
import com.liferay.knowledge.base.internal.upgrade.v3_0_0.util.KBCommentTable;
import com.liferay.knowledge.base.internal.upgrade.v3_0_0.util.KBFolderTable;
import com.liferay.knowledge.base.internal.upgrade.v3_0_0.util.KBTemplateTable;
import com.liferay.knowledge.base.internal.upgrade.v4_4_0.KBGroupServiceConfigurationUpgradeProcess;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.ViewCountUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = UpgradeStepRegistrator.class)
public class KnowledgeBaseServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"0.0.1", "0.0.2",
			new com.liferay.knowledge.base.internal.upgrade.v1_0_0.
				RatingsEntryUpgradeProcess());

		registry.register(
			"0.0.2", "1.0.0",
			new com.liferay.knowledge.base.internal.upgrade.v1_0_0.
				RatingsStatsUpgradeProcess());

		registry.register(
			"1.0.0", "1.0.1",
			new com.liferay.knowledge.base.internal.upgrade.v1_1_0.
				ClassNameUpgradeProcess());

		registry.register(
			"1.0.1", "1.0.2",
			new com.liferay.knowledge.base.internal.upgrade.v1_1_0.
				ExpandoTableUpgradeProcess());

		registry.register(
			"1.0.2", "1.0.3",
			new com.liferay.knowledge.base.internal.upgrade.v1_1_0.
				KBArticleUpgradeProcess(_store));

		registry.register(
			"1.0.3", "1.0.4",
			new com.liferay.knowledge.base.internal.upgrade.v1_1_0.
				KBCommentUpgradeProcess());

		registry.register(
			"1.0.4", "1.0.5",
			new com.liferay.knowledge.base.internal.upgrade.v1_1_0.
				KBTemplateUpgradeProcess());

		registry.register(
			"1.0.5", "1.0.6",
			new com.liferay.knowledge.base.internal.upgrade.v1_1_0.
				ResourceActionUpgradeProcess());

		registry.register(
			"1.0.6", "1.0.7",
			new com.liferay.knowledge.base.internal.upgrade.v1_1_0.
				ResourcePermissionUpgradeProcess());

		registry.register(
			"1.0.7", "1.1.0",
			new com.liferay.knowledge.base.internal.upgrade.v1_1_0.
				UpgradePortletPreferences());

		registry.register(
			"1.1.0", "1.2.0",
			UpgradeProcessFactory.dropColumns("KBArticle", "kbTemplateId"),
			UpgradeProcessFactory.dropTables("KBStructure"),
			UpgradeProcessFactory.dropColumns(
				"KBTemplate", "engineType", "cacheable"));

		registry.register(
			"1.2.0", "1.2.1",
			new com.liferay.knowledge.base.internal.upgrade.v1_3_0.
				KBAttachmentsUpgradeProcess(
					_companyLocalService, _portletFileRepository, _store));

		registry.register(
			"1.2.1", "1.3.0",
			new com.liferay.knowledge.base.internal.upgrade.v1_3_0.
				UpgradePortletPreferences());

		registry.register(
			"1.3.0", "1.3.1",
			new com.liferay.knowledge.base.internal.upgrade.v1_3_1.
				KBArticleUpgradeProcess());

		registry.register(
			"1.3.1", "1.3.1.step-1",
			new com.liferay.knowledge.base.internal.upgrade.v1_3_2.
				KBArticleUpgradeProcess());

		registry.register(
			"1.3.1.step-1", "1.3.2",
			new com.liferay.knowledge.base.internal.upgrade.v1_3_2.
				KBFolderUpgradeProcess());

		registry.register(
			"1.3.2", "1.3.3",
			new com.liferay.knowledge.base.internal.upgrade.v1_3_3.
				KBFolderUpgradeProcess());

		registry.register(
			"1.3.3", "1.3.3.step-1",
			UpgradeProcessFactory.addColumns(
				"KBArticle", "sourceURL STRING null"));

		registry.register(
			"1.3.3.step-1", "1.3.3.step-2",
			new com.liferay.knowledge.base.internal.upgrade.v1_3_4.
				KBCommentUpgradeProcess());

		registry.register(
			"1.3.3.step-2", "1.3.3.step-3",
			new com.liferay.knowledge.base.internal.upgrade.v1_3_4.
				ResourceActionUpgradeProcess());

		registry.register(
			"1.3.3.step-3", "1.3.4",
			new com.liferay.knowledge.base.internal.upgrade.v1_3_4.
				UpgradePortletPreferences());

		registry.register(
			"1.3.4", "1.3.5",
			new com.liferay.knowledge.base.internal.upgrade.v1_3_5.
				UpgradeLastPublishDate());

		registry.register(
			"1.3.5", "1.3.6",
			new com.liferay.knowledge.base.internal.upgrade.v2_0_0.
				UpgradeClassNames());

		registry.register(
			"1.3.6", "1.3.7",
			new com.liferay.knowledge.base.internal.upgrade.v2_0_0.
				KBCommentUpgradeProcess());

		registry.register(
			"1.3.7", "2.0.0",
			new com.liferay.knowledge.base.internal.upgrade.v2_0_0.
				UpgradeRepository());

		registry.register(
			"2.0.0", "2.0.1",
			new com.liferay.knowledge.base.internal.upgrade.v2_0_1.
				UpgradePortletSettings(_settingsLocatorHelper));

		registry.register(
			"2.0.1", "2.0.2",
			new com.liferay.knowledge.base.internal.upgrade.v2_0_2.
				KBArticleUpgradeProcess());

		registry.register(
			"2.0.2", "3.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {
					KBArticleTable.class, KBCommentTable.class,
					KBFolderTable.class, KBTemplateTable.class
				}));

		registry.register(
			"3.0.0", "3.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"KBArticle", "KBComment", "KBFolder", "KBTemplate"
					};
				}

			});

		registry.register(
			"3.1.0", "4.0.0",
			new ViewCountUpgradeProcess(
				"KBArticle", KBArticle.class, "kbArticleId", "viewCount"));

		registry.register("4.0.0", "4.1.0", new DummyUpgradeStep());

		registry.register(
			"4.1.0", "4.2.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"KBArticle", "KBFolder"};
				}

			});

		registry.register(
			"4.2.0", "4.3.0",
			UpgradeProcessFactory.addColumns(
				"KBArticle", "expirationDate DATE null",
				"reviewDate DATE null"));

		registry.register(
			"4.3.0", "4.4.0",
			new KBGroupServiceConfigurationUpgradeProcess(_configurationAdmin));

		registry.register(
			"4.4.0", "4.5.0",
			new CTModelUpgradeProcess(
				"KBArticle", "KBComment", "KBFolder", "KBTemplate"));

		registry.register(
			"4.5.0", "4.6.0",
			new com.liferay.knowledge.base.internal.upgrade.v4_6_0.
				KBArticleUpgradeProcess());

		registry.register(
			"4.6.0", "4.7.0",
			new com.liferay.knowledge.base.internal.upgrade.v4_7_0.
				KBFolderUpgradeProcess());
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.view.count.service)(&(release.schema.version>=1.0.0)))"
	)
	private Release _release;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

	@Reference(target = "(default=true)")
	private Store _store;

}