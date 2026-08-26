/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal;

import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.store.service.CTSContentLocalService;
import com.liferay.data.cleanup.DataCleanup;
import com.liferay.data.cleanup.DataCleanupAdapter;
import com.liferay.data.cleanup.internal.upgrade.AmazonRankingsUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.ChatUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.CurrencyConverterUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.DLPreviewCTSContentDataUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.DictionaryUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.DirectoryUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.DocumentLibraryFileRankServiceUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.ExpiredJournalArticleUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.FrontendImageEditorUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.GoogleMapsUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.HTMLPreviewUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.HelloVelocityUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.InvitationUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.LayoutClassedModelUsageOrphanDataUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.LoanCalculatorUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.MailReaderUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.NetworkUtilitiesUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.OAuthUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.OpenSocialUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.OutdatedPublishedCTCollectionUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.PasswordGeneratorUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.PortalSecurityWedeployAuthUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.PublishedCTSContentDataUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.QuickNoteUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.RecentDocumentsUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.ShoppingUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.SocialActivityUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.SocialGroupStatisticsUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.SocialPrivateMessagingUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.SocialRequestsUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.SocialUserStatisticsUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.SoftwareCatalogUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.SyncUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.TranslatorUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.TwitterUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.UnitConverterUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.UpgradeHelloWorld;
import com.liferay.data.cleanup.internal.upgrade.WeatherUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.WebFormUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.WebProxyUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.WidgetLayoutTypeSettingsUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.WysiwygUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.XSLContentUpgradeProcess;
import com.liferay.data.cleanup.internal.upgrade.YoutubeUpgradeProcess;
import com.liferay.data.cleanup.internal.verify.ClassNamePostUpgradeDataCleanupProcess;
import com.liferay.data.cleanup.internal.verify.PortletPreferencesPostUpgradeDataCleanupProcess;
import com.liferay.data.cleanup.internal.verify.PostUpgradeDataCleanupProcess;
import com.liferay.data.cleanup.internal.verify.ResourceActionPostUpgradeDataCleanupProcess;
import com.liferay.data.cleanup.internal.verify.ResourcePermissionPostupgradeDataCleanupProcess;
import com.liferay.data.cleanup.internal.verify.ServiceComponentPostUpgradeDataCleanupProcess;
import com.liferay.data.cleanup.util.DataCleanupUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.manager.ContentManager;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.upgrade.data.cleanup.AnalyticsMessageDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.CompanyDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.ConfigurationDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.ContactDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.CounterDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DDMDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DDMStorageLinkDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DLFileEntryDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DataCleanupPreupgradeProcessSuite;
import com.liferay.portal.upgrade.data.cleanup.GroupDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.IllegalCharactersContentDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.JournalDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.LayoutDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.PortalPreferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.QuartzJobDetailsDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.RoleDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.UserDataCleanupPreupgradeProcess;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mariano Álvaro Sáiz
 */
@Component(service = {})
public class DataCleanupRegistrator {

	@Activate
	protected void activate() throws Exception {
		_registerModuleDataCleanups();
		_registerSystemDataCleanups();
	}

	@Deactivate
	protected void deactivate() throws Exception {
		for (DataCleanup dataCleanup : _dataCleanups) {
			DataCleanupUtil.unregisterDataCleanup(dataCleanup);
		}
	}

	private String _getBundleSymbolicName(Class<?> clazz) {
		Bundle bundle = FrameworkUtil.getBundle(clazz);

		if (bundle == null) {
			return null;
		}

		return bundle.getSymbolicName();
	}

	private String _getDataCleanupLabel(
		DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess) {

		return _dataCleanupLabels.get(dataCleanupPreupgradeProcess.getClass());
	}

	private void _registerDataCleanup(DataCleanup dataCleanup) {
		_dataCleanups.add(dataCleanup);

		DataCleanupUtil.registerDataCleanup(dataCleanup);
	}

	private void _registerModuleDataCleanups() {
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-amazon-rankings-module-data",
				"com.liferay.amazon.rankings.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new AmazonRankingsUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-chat-module-data", "com.liferay.chat.service",
				DataCleanup.MODULE_DATA_CLEANUP, new ChatUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-currency-converter-module-data",
				"com.liferay.currency.converter.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new CurrencyConverterUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-dictionary-module-data",
				"com.liferay.dictionary.web", DataCleanup.MODULE_DATA_CLEANUP,
				new DictionaryUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-directory-module-data",
				"com.liferay.directory.web", DataCleanup.MODULE_DATA_CLEANUP,
				new DirectoryUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-document-library-file-rank-module-data",
				"com.liferay.document.library.file.rank.service",
				DataCleanup.MODULE_DATA_CLEANUP,
				new DocumentLibraryFileRankServiceUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-frontend-image-editor-module-data",
				"com.liferay.frontend.image.editor.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new FrontendImageEditorUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-google-maps-module-data",
				"com.liferay.google.maps.web", DataCleanup.MODULE_DATA_CLEANUP,
				new GoogleMapsUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-hello-velocity-module-data",
				"com.liferay.hello.velocity.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new HelloVelocityUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-hello-world-module-data",
				"com.liferay.hello.world.web", DataCleanup.MODULE_DATA_CLEANUP,
				new UpgradeHelloWorld()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-html-preview-module-data",
				"com.liferay.html.preview.service",
				DataCleanup.MODULE_DATA_CLEANUP,
				new HTMLPreviewUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-invitation-module-data",
				"com.liferay.invitation.web", DataCleanup.MODULE_DATA_CLEANUP,
				new InvitationUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-loan-calculator-module-data",
				"com.liferay.loan.calculator.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new LoanCalculatorUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-mail-reader-module-data",
				"com.liferay.mail.reader.service",
				DataCleanup.MODULE_DATA_CLEANUP,
				new MailReaderUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-network-utilities-module-data",
				"com.liferay.network.utilities.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new NetworkUtilitiesUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-oauth-module-data", "com.liferay.oauth.service",
				DataCleanup.MODULE_DATA_CLEANUP, new OAuthUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-password-generator-module-data",
				"com.liferay.password.generator.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new PasswordGeneratorUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-portal-security-wedeploy-auth-module-data",
				"com.liferay.portal.security.wedeploy.auth.service",
				DataCleanup.MODULE_DATA_CLEANUP,
				new PortalSecurityWedeployAuthUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-quick-note-module-data",
				"com.liferay.quick.note.web", DataCleanup.MODULE_DATA_CLEANUP,
				new QuickNoteUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-recent-documents-module-data",
				"com.liferay.recent.documents.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new RecentDocumentsUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-shopping-module-data", "com.liferay.shopping.service",
				DataCleanup.MODULE_DATA_CLEANUP,
				new ShoppingUpgradeProcess(_imageLocalService)));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-social-activity-module-data",
				"com.liferay.social.activity.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new SocialActivityUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-social-group-statistics-module-data",
				"com.liferay.social.group.statistics.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new SocialGroupStatisticsUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-social-private-messaging-module-data",
				"com.liferay.social.privatemessaging.service",
				DataCleanup.MODULE_DATA_CLEANUP,
				new SocialPrivateMessagingUpgradeProcess(
					_mbThreadLocalService)));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-social-requests-module-data",
				"com.liferay.social.requests.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new SocialRequestsUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-social-user-statistics-module-data",
				"com.liferay.social.user.statistics.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new SocialUserStatisticsUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-software-catalog-module-data",
				"com.liferay.softwarecatalog.service",
				DataCleanup.MODULE_DATA_CLEANUP,
				new SoftwareCatalogUpgradeProcess(
					_imageLocalService, _mbMessageLocalService,
					_ratingsStatsLocalService, _subscriptionLocalService)));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-sync-module-data", "com.liferay.sync.service",
				DataCleanup.MODULE_DATA_CLEANUP, new SyncUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-translator-module-data",
				"com.liferay.translator.web", DataCleanup.MODULE_DATA_CLEANUP,
				new TranslatorUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-twitter-module-data", "com.liferay.twitter.service",
				DataCleanup.MODULE_DATA_CLEANUP, new TwitterUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help",
				"clean-up-unit-converter-module-data",
				"com.liferay.unit.converter.web",
				DataCleanup.MODULE_DATA_CLEANUP,
				new UnitConverterUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-weather-module-data",
				"com.liferay.weather.web", DataCleanup.MODULE_DATA_CLEANUP,
				new WeatherUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-web-form-module-data",
				"com.liferay.web.form.web", DataCleanup.MODULE_DATA_CLEANUP,
				new WebFormUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-web-proxy-module-data",
				"com.liferay.web.proxy.web", DataCleanup.MODULE_DATA_CLEANUP,
				new WebProxyUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-wysiwyg-module-data",
				"com.liferay.wysiwyg.web", DataCleanup.MODULE_DATA_CLEANUP,
				new WysiwygUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-xsl-content-module-data",
				"com.liferay.xsl.content.web", DataCleanup.MODULE_DATA_CLEANUP,
				new XSLContentUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-help", "clean-up-youtube-module-data",
				"com.liferay.youtube.web", DataCleanup.MODULE_DATA_CLEANUP,
				new YoutubeUpgradeProcess()));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"clean-up-module-data-and-tables-help",
				"clean-up-open-social-module-data", "opensocial-portlet",
				DataCleanup.MODULE_DATA_CLEANUP,
				new OpenSocialUpgradeProcess(_expandoTableLocalService)));
	}

	private void _registerSystemDataCleanups() {
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-class-name-orphan-data",
				_getBundleSymbolicName(
					ClassNamePostUpgradeDataCleanupProcess.class),
				DataCleanup.SYSTEM_DATA_CLEANUP,
				new VerifyProcess() {

					@Override
					protected void doVerify() throws Exception {
						PostUpgradeDataCleanupProcess
							postUpgradeDataCleanupProcess =
								new ClassNamePostUpgradeDataCleanupProcess(
									_classNameLocalService,
									_companyLocalService, connection,
									_objectDefinitionLocalService);

						postUpgradeDataCleanupProcess.cleanUp();
					}

				}));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-dl-preview-cts-content-data",
				"com.liferay.change.tracking.service",
				DataCleanup.SYSTEM_DATA_CLEANUP,
				new DLPreviewCTSContentDataUpgradeProcess(
					_ctCollectionLocalService, _ctEntryLocalService, _portal)));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-expired-journal-articles",
				"com.liferay.journal.service", DataCleanup.SYSTEM_DATA_CLEANUP,
				new ExpiredJournalArticleUpgradeProcess(
					_journalArticleLocalService)));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-layout-classed-model-usage-orphan-data",
				"com.liferay.layout.service", DataCleanup.SYSTEM_DATA_CLEANUP,
				new LayoutClassedModelUsageOrphanDataUpgradeProcess(
					_classNameLocalService, _contentManager,
					_ctCollectionLocalService, _fragmentEntryLinkLocalService,
					_layoutClassedModelUsageLocalService,
					_layoutPageTemplateStructureLocalService,
					_layoutPageTemplateStructureRelLocalService)));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-portlet-preferences-orphan-data",
				_getBundleSymbolicName(
					ClassNamePostUpgradeDataCleanupProcess.class),
				DataCleanup.SYSTEM_DATA_CLEANUP,
				new VerifyProcess() {

					@Override
					protected void doVerify() throws Exception {
						PostUpgradeDataCleanupProcess
							postUpgradeDataCleanupProcess =
								new PortletPreferencesPostUpgradeDataCleanupProcess(
									connection, true, _portletLocalService);

						postUpgradeDataCleanupProcess.cleanUp();
					}

				}));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-publications-older-than-6-months",
				"com.liferay.change.tracking.service",
				DataCleanup.SYSTEM_DATA_CLEANUP,
				new OutdatedPublishedCTCollectionUpgradeProcess(
					_ctCollectionLocalService)));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-published-cts-content-data",
				"com.liferay.change.tracking.store.service",
				DataCleanup.SYSTEM_DATA_CLEANUP,
				new PublishedCTSContentDataUpgradeProcess(
					_ctsContentLocalService, _portal)));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-resource-action-orphan-data",
				_getBundleSymbolicName(
					ClassNamePostUpgradeDataCleanupProcess.class),
				DataCleanup.SYSTEM_DATA_CLEANUP,
				new VerifyProcess() {

					@Override
					protected void doVerify() throws Exception {
						PostUpgradeDataCleanupProcess
							postUpgradeDataCleanupProcess =
								new ResourceActionPostUpgradeDataCleanupProcess(
									_companyLocalService, connection,
									_objectDefinitionLocalService,
									_resourceActionLocalService);

						postUpgradeDataCleanupProcess.cleanUp();
					}

				}));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-resource-permission-orphan-data",
				_getBundleSymbolicName(
					ClassNamePostUpgradeDataCleanupProcess.class),
				DataCleanup.SYSTEM_DATA_CLEANUP,
				new VerifyProcess() {

					@Override
					protected void doVerify() throws Exception {
						PostUpgradeDataCleanupProcess
							postUpgradeDataCleanupProcess =
								new ResourcePermissionPostupgradeDataCleanupProcess(
									connection,
									_resourcePermissionLocalService);

						postUpgradeDataCleanupProcess.cleanUp();
					}

				}));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-service-component-orphan-data",
				_getBundleSymbolicName(
					ClassNamePostUpgradeDataCleanupProcess.class),
				DataCleanup.SYSTEM_DATA_CLEANUP,
				new VerifyProcess() {

					@Override
					protected void doVerify() throws Exception {
						PostUpgradeDataCleanupProcess
							postUpgradeDataCleanupProcess =
								new ServiceComponentPostUpgradeDataCleanupProcess(
									connection, _serviceComponentLocalService);

						postUpgradeDataCleanupProcess.cleanUp();
					}

				}));
		_registerDataCleanup(
			DataCleanupAdapter.create(
				"remove-widget-layout-type-settings",
				"com.liferay.layout.service", DataCleanup.SYSTEM_DATA_CLEANUP,
				new WidgetLayoutTypeSettingsUpgradeProcess(
					_layoutLocalService)));

		DataCleanupPreupgradeProcessSuite dataCleanupPreupgradeProcessSuite =
			new DataCleanupPreupgradeProcessSuite();

		for (DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess :
				dataCleanupPreupgradeProcessSuite.
					getSortedDataCleanupPreupgradeProcesses()) {

			String dataCleanupLabel = _getDataCleanupLabel(
				dataCleanupPreupgradeProcess);

			if (dataCleanupLabel == null) {
				continue;
			}

			_registerDataCleanup(
				DataCleanupAdapter.create(
					dataCleanupLabel,
					ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
					DataCleanup.SYSTEM_DATA_CLEANUP,
					dataCleanupPreupgradeProcess));
		}
	}

	private static final Map<Class<?>, String> _dataCleanupLabels =
		HashMapBuilder.<Class<?>, String>put(
			AnalyticsMessageDataCleanupPreupgradeProcess.class,
			"remove-analytics-message-data"
		).put(
			CompanyDataCleanupPreupgradeProcess.class,
			"remove-company-orphan-data"
		).put(
			ConfigurationDataCleanupPreupgradeProcess.class,
			"remove-configuration-orphan-data"
		).put(
			ContactDataCleanupPreupgradeProcess.class,
			"remove-contact-orphan-data"
		).put(
			CounterDataCleanupPreupgradeProcess.class, "fix-counter-values"
		).put(
			DDMDataCleanupPreupgradeProcess.class, "remove-ddm-orphan-data"
		).put(
			DDMStorageLinkDataCleanupPreupgradeProcess.class,
			"remove-ddm-storage-link-orphan-data"
		).put(
			DLFileEntryDataCleanupPreupgradeProcess.class,
			"remove-dl-file-entry-orphan-data"
		).put(
			GroupDataCleanupPreupgradeProcess.class, "remove-group-orphan-data"
		).put(
			IllegalCharactersContentDataCleanupPreupgradeProcess.class,
			"remove-illegal-characters-content-data"
		).put(
			JournalDataCleanupPreupgradeProcess.class,
			"remove-journal-orphan-data"
		).put(
			LayoutDataCleanupPreupgradeProcess.class,
			"remove-layout-orphan-data"
		).put(
			PortalPreferencesDataCleanupPreupgradeProcess.class,
			"remove-portal-preferences-orphan-data"
		).put(
			QuartzJobDetailsDataCleanupPreupgradeProcess.class,
			"remove-quartz-job-details-data"
		).put(
			RoleDataCleanupPreupgradeProcess.class, "remove-role-orphan-data"
		).put(
			UserDataCleanupPreupgradeProcess.class, "remove-user-orphan-data"
		).build();

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ContentManager _contentManager;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTSContentLocalService _ctsContentLocalService;

	private final List<DataCleanup> _dataCleanups = new ArrayList<>();

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private ServiceComponentLocalService _serviceComponentLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

}