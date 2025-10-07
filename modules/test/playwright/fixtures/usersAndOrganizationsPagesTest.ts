/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {UserLocaleOptionsPage} from '../pages/portal-user-locale-options-web/UserLocaleOptionsPage';
import {SiteConfigurationDetailsPage} from '../pages/site-admin-web/SiteConfigurationDetailsPage';
import {SiteSettingsPage} from '../pages/site-admin-web/SiteSettingsPage';
import {ExportUserDataPage} from '../pages/user-associated-data-web/ExportUserDataPage';
import {PersonalDataErasurePage} from '../pages/user-associated-data-web/PersonalDataErasurePage';
import {UserAssociatedDataAnnouncementPage} from '../pages/user-associated-data-web/announcement-web/UserAssociatedAnnouncementPage';
import {UserAssociatedDataBlogPage} from '../pages/user-associated-data-web/blog-web/UserAssociatedDataBlogPage';
import {UserAssociatedDataDocumentLibraryPage} from '../pages/user-associated-data-web/document-library-web/UserAssociatedDataDocumentLibraryPage';
import {UserAssociatedDataEditDocumentPage} from '../pages/user-associated-data-web/document-library-web/UserAssociatedDataEditDocumentPage';
import {UserAssociatedDataFormPage} from '../pages/user-associated-data-web/form-web/UserAssociatedDataFormPage';
import {UserAssociatedDataJournalPage} from '../pages/user-associated-data-web/journal-article-web/UserAssociatedDataJournalPage';
import {UserAssociatedDataEditMessageBoardThreadPage} from '../pages/user-associated-data-web/message-board-web/UserAssociatedDataEditMessageBoardThreadPage';
import {UserAssociatedDataMessageBoardPage} from '../pages/user-associated-data-web/message-board-web/UserAssociatedDataMessageBoardPage';
import {UserAssociatedDataMessageBoardWidgetPage} from '../pages/user-associated-data-web/message-board-web/UserAssociatedDataMessageBoardWidgetPage';
import {UserAssociatedDataSiteStagingPage} from '../pages/user-associated-data-web/site-staging-web/UserAssociatedDataSiteStagingPage';
import {AssignUsersPage} from '../pages/users-admin-web/AssignUsersPage';
import {EditOrganizationPage} from '../pages/users-admin-web/EditOrganizationPage';
import {EditUserPage} from '../pages/users-admin-web/EditUserPage';
import {OrganizationUsersPage} from '../pages/users-admin-web/OrganizationUsersPage';
import {SMTPMockServerPage} from '../pages/users-admin-web/SMTPMockServerPage';
import {ServiceAccountsPage} from '../pages/users-admin-web/ServiceAccountsPage';
import {TeamsPage} from '../pages/users-admin-web/TeamsPage';
import {UserPersonalSitePage} from '../pages/users-admin-web/UserPersonalSitePage';
import {UsersAndOrganizationsPage} from '../pages/users-admin-web/UsersAndOrganizationsPage';
import {DocumentLibraryPage} from '../pages/users-admin-web/document-library-web/DocumentLibraryPage';
import {SiteMembershipsPage} from '../pages/users-admin-web/site-admin-web/SiteMembershipsPage';
import {TagsEditPage} from '../tests/asset-tags-admin-web/main/pages/TagsEditPage';
import {NotificationsPage} from '../tests/notifications-web/main/pages/NotificationsPage';

const usersAndOrganizationsPagesTest = test.extend<{
	assignUsersPage: AssignUsersPage;
	editOrganizationPage: EditOrganizationPage;
	editUserPage: EditUserPage;
	exportUserDataPage: ExportUserDataPage;
	notificationsPage: NotificationsPage;
	organizationUsersPage: OrganizationUsersPage;
	personalDataErasurePage: PersonalDataErasurePage;
	serviceAccountsPage: ServiceAccountsPage;
	siteConfigurationDetailsPage: SiteConfigurationDetailsPage;
	siteMembershipsPage: SiteMembershipsPage;
	siteSettingsPage: SiteSettingsPage;
	smtpMockServerPage: SMTPMockServerPage;
	tagsEditPage: TagsEditPage;
	teamsPage: TeamsPage;
	userAssociatedDataAnnouncementPage: UserAssociatedDataAnnouncementPage;
	userAssociatedDataBlogPage: UserAssociatedDataBlogPage;
	userAssociatedDataDocumentLibraryPage: UserAssociatedDataDocumentLibraryPage;
	userAssociatedDataEditDocumentPage: UserAssociatedDataEditDocumentPage;
	userAssociatedDataEditMessageBoardThreadPage: UserAssociatedDataEditMessageBoardThreadPage;
	userAssociatedDataFormPage: UserAssociatedDataFormPage;
	userAssociatedDataJournalPage: UserAssociatedDataJournalPage;
	userAssociatedDataMessageBoardPage: UserAssociatedDataMessageBoardPage;
	userAssociatedDataMessageBoardWidgetPage: UserAssociatedDataMessageBoardWidgetPage;
	userAssociatedDataSiteStagingPage: UserAssociatedDataSiteStagingPage;
	userDocumentLibraryPage: DocumentLibraryPage;
	userLocaleOptionsPage: UserLocaleOptionsPage;
	userPersonalSitePage: UserPersonalSitePage;
	usersAndOrganizationsPage: UsersAndOrganizationsPage;
}>({
	assignUsersPage: async ({page}, use) => {
		await use(new AssignUsersPage(page));
	},
	editOrganizationPage: async ({page}, use) => {
		await use(new EditOrganizationPage(page));
	},
	editUserPage: async ({page}, use) => {
		await use(new EditUserPage(page));
	},
	exportUserDataPage: async ({page}, use) => {
		await use(new ExportUserDataPage(page));
	},
	notificationsPage: async ({page}, use) => {
		await use(new NotificationsPage(page));
	},
	organizationUsersPage: async ({page}, use) => {
		await use(new OrganizationUsersPage(page));
	},
	personalDataErasurePage: async ({page}, use) => {
		await use(new PersonalDataErasurePage(page));
	},
	serviceAccountsPage: async ({page}, use) => {
		await use(new ServiceAccountsPage(page));
	},
	siteConfigurationDetailsPage: async ({page}, use) => {
		await use(new SiteConfigurationDetailsPage(page));
	},
	siteMembershipsPage: async ({page}, use) => {
		await use(new SiteMembershipsPage(page));
	},
	siteSettingsPage: async ({page}, use) => {
		await use(new SiteSettingsPage(page));
	},
	smtpMockServerPage: async ({page}, use) => {
		await use(new SMTPMockServerPage(page));
	},
	tagsEditPage: async ({page}, use) => {
		await use(new TagsEditPage(page));
	},
	teamsPage: async ({page}, use) => {
		await use(new TeamsPage(page));
	},
	userAssociatedDataAnnouncementPage: async ({page}, use) => {
		await use(new UserAssociatedDataAnnouncementPage(page));
	},
	userAssociatedDataBlogPage: async ({page}, use) => {
		await use(new UserAssociatedDataBlogPage(page));
	},
	userAssociatedDataDocumentLibraryPage: async ({page}, use) => {
		await use(new UserAssociatedDataDocumentLibraryPage(page));
	},
	userAssociatedDataEditDocumentPage: async ({page}, use) => {
		await use(new UserAssociatedDataEditDocumentPage(page));
	},
	userAssociatedDataEditMessageBoardThreadPage: async ({page}, use) => {
		await use(new UserAssociatedDataEditMessageBoardThreadPage(page));
	},
	userAssociatedDataFormPage: async ({page}, use) => {
		await use(new UserAssociatedDataFormPage(page));
	},
	userAssociatedDataJournalPage: async ({page}, use) => {
		await use(new UserAssociatedDataJournalPage(page));
	},
	userAssociatedDataMessageBoardPage: async ({page}, use) => {
		await use(new UserAssociatedDataMessageBoardPage(page));
	},
	userAssociatedDataMessageBoardWidgetPage: async ({page}, use) => {
		await use(new UserAssociatedDataMessageBoardWidgetPage(page));
	},
	userAssociatedDataSiteStagingPage: async ({page}, use) => {
		await use(new UserAssociatedDataSiteStagingPage(page));
	},
	userDocumentLibraryPage: async ({page}, use) => {
		await use(new DocumentLibraryPage(page));
	},
	userLocaleOptionsPage: async ({page}, use) => {
		await use(new UserLocaleOptionsPage(page));
	},
	userPersonalSitePage: async ({page}, use) => {
		await use(new UserPersonalSitePage(page));
	},
	usersAndOrganizationsPage: async ({page}, use) => {
		await use(new UsersAndOrganizationsPage(page));
	},
});

export {usersAndOrganizationsPagesTest};
