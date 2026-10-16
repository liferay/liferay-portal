/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {WorkflowPage} from '../../../pages/portal-workflow-web/WorkflowPage';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {changeXliffModelId, translateXliff} from '../../../utils/xliff';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {translationPagesTest} from './fixtures/translationPagesTest';
import {WebContentTranslationPage} from './pages/WebContentTranslationPage';
import {
	expectNotTranslated,
	expectTranslated,
	getTranslations,
	readXliff,
	writeFiles,
	writeZip,
} from './utils/translationFiles';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	translationPagesTest,
	workflowPagesTest
);

const BASE = {
	content: 'WC WebContent Content',
	description: 'WC WebContent Description',
};

const SPANISH = {
	content: 'WC WebContent Contenido',
	description: 'WC WebContent Descripción',
	title: 'WC WebContent Título',
};

async function addAdministratorUser(apiHelpers: ApiHelpers) {
	const administratorRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	await apiHelpers.headlessAdminUser.assignUserToRole(
		administratorRole.externalReferenceCode,
		user.id
	);

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	return user;
}

async function addWebContent(
	apiHelpers: ApiHelpers,
	site: Site,
	title: string
) {
	await apiHelpers.jsonWebServicesJournal.addWebContent({
		content: BASE.content,
		ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
		descriptionMap: {en_US: BASE.description},
		groupId: site.id,
		titleMap: {en_US: title},
	});
}

async function enableTranslationWorkflow(
	site: Site,
	workflowPage: WorkflowPage
) {
	await workflowPage.goto(site.friendlyUrlPath);

	await workflowPage.changeWorkflow('Translation', 'Single Approver');
}

async function submitSpanishTranslation(
	site: Site,
	title: string,
	webContentTranslationPage: WebContentTranslationPage
) {
	await webContentTranslationPage.open(site, title);

	await webContentTranslationPage.changeTargetLocale('es-ES');

	await webContentTranslationPage.translateFields(SPANISH);

	await webContentTranslationPage.submitForWorkflow();
}

test(
	'Approving a pending translation applies it to the web content',
	{tag: '@LPD-96445'},
	async ({
		apiHelpers,
		journalEditArticlePage,
		journalPage,
		site,
		webContentTranslationPage,
		workflowPage,
		workflowTasksPage,
	}) => {
		const title = getRandomString();

		await addWebContent(apiHelpers, site, title);

		await enableTranslationWorkflow(site, workflowPage);

		// Submit a Spanish translation for review

		await submitSpanishTranslation(site, title, webContentTranslationPage);

		// Approve the pending translation task

		await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

		await workflowTasksPage.assignToMe(`Translation of ${title} to es-ES`);

		await workflowTasksPage.approve(`Translation of ${title} to es-ES`);

		// The approved translation is applied to the web content

		await journalPage.goto(site.friendlyUrlPath);

		await journalEditArticlePage.editArticle(title);

		await journalEditArticlePage.changeLanguage('es_ES');

		await expect(journalEditArticlePage.titleInput).toHaveValue(
			SPANISH.title
		);
	}
);

test(
	'Rejecting a pending translation leaves the web content untranslated',
	{tag: '@LPD-96445'},
	async ({
		apiHelpers,
		journalEditArticlePage,
		journalPage,
		site,
		webContentTranslationPage,
		workflowPage,
		workflowTasksPage,
	}) => {
		const title = getRandomString();

		await addWebContent(apiHelpers, site, title);

		await enableTranslationWorkflow(site, workflowPage);

		await submitSpanishTranslation(site, title, webContentTranslationPage);

		// Reject the pending translation task

		await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

		await workflowTasksPage.assignToMe(`Translation of ${title} to es-ES`);

		await workflowTasksPage.reject(`Translation of ${title} to es-ES`);

		// The rejected translation is not applied to the web content

		await journalPage.goto(site.friendlyUrlPath);

		await journalEditArticlePage.editArticle(title);

		await journalEditArticlePage.changeLanguage('es_ES');

		await expect(journalEditArticlePage.titleInput).not.toHaveValue(
			SPANISH.title
		);
	}
);

test(
	'A rejected translation can be resubmitted and then approved',
	{tag: '@LPD-96445'},
	async ({
		apiHelpers,
		site,
		webContentTranslationPage,
		workflowPage,
		workflowTasksPage,
	}) => {
		const title = getRandomString();

		await addWebContent(apiHelpers, site, title);

		await enableTranslationWorkflow(site, workflowPage);

		await submitSpanishTranslation(site, title, webContentTranslationPage);

		// Reject the pending translation task

		await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

		await workflowTasksPage.assignToMe(`Translation of ${title} to es-ES`);

		await workflowTasksPage.reject(`Translation of ${title} to es-ES`);

		// Resubmit the rejected translation for review

		await workflowTasksPage.resubmit(
			`Translation of ${title} to es-ES`,
			site.friendlyUrlPath
		);

		// Approve the resubmitted translation task

		await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

		await workflowTasksPage.assignToMe(`Translation of ${title} to es-ES`);

		await workflowTasksPage.approve(`Translation of ${title} to es-ES`);

		// The web content shows the approved translation

		await webContentTranslationPage.open(site, title);

		await webContentTranslationPage.changeTargetLocale('es-ES');

		await webContentTranslationPage.assertTargetFields(SPANISH);
	}
);

test(
	'A pending translation can be deleted',
	{tag: '@LPD-96445'},
	async ({
		apiHelpers,
		site,
		translationsAdminPage,
		webContentTranslationPage,
		workflowPage,
	}) => {
		const title = getRandomString();

		const entry = `Translation of ${title} to es-ES`;

		await addWebContent(apiHelpers, site, title);

		await enableTranslationWorkflow(site, workflowPage);

		await submitSpanishTranslation(site, title, webContentTranslationPage);

		// Delete the pending translation entry from the Translations app

		await translationsAdminPage.goto(site);

		await translationsAdminPage.assertEntry({
			language: 'es-ES',
			status: 'Pending',
			title: entry,
		});

		await translationsAdminPage.deleteEntry(entry);

		await translationsAdminPage.assertNoEntry(entry);

		// The web content keeps its base language values

		await webContentTranslationPage.open(site, title);

		await webContentTranslationPage.changeTargetLocale('es-ES');

		await webContentTranslationPage.assertTargetFields({...BASE, title});
	}
);

test(
	'Submitting a translation is blocked for a locale another user already submitted',
	{tag: '@LPD-96445'},
	async ({
		apiHelpers,
		page,
		site,
		webContentTranslationPage,
		workflowPage,
	}) => {
		const title = getRandomString();

		await addWebContent(apiHelpers, site, title);

		await enableTranslationWorkflow(site, workflowPage);

		// Submit a Spanish translation as the default admin

		await submitSpanishTranslation(site, title, webContentTranslationPage);

		// A second user cannot submit a translation for the same locale

		const user = await addAdministratorUser(apiHelpers);

		await performUserSwitch(page, user.alternateName);

		await webContentTranslationPage.open(site, title);

		await webContentTranslationPage.changeTargetLocale('es-ES');

		await expect(
			webContentTranslationPage.submitForWorkflowButton
		).toBeDisabled();

		await performUserSwitch(page, 'test');
	}
);

test(
	'Importing translations with workflow sends each valid file for review',
	{tag: '@LPD-110482'},
	async ({
		apiHelpers,
		exportImportTranslationPage,
		page,
		site,
		translationsAdminPage,
		workflowPage,
		workflowTasksPage,
	}) => {
		const title = getRandomString();

		const base = {...BASE, title};

		const french = {
			content: 'ContenuWeb',
			description: 'Ladescription',
			title: 'TitreducontenuWeb',
		};

		const spanish = {
			content: 'Contenidoweb',
			description: 'Descripcion',
			title: 'Titulodelcontenidoweb',
		};

		await addWebContent(apiHelpers, site, title);

		await enableTranslationWorkflow(site, workflowPage);

		// Translate French and Spanish, and point the German file to another model

		const zipPath = await exportImportTranslationPage.exportArticle(
			site.friendlyUrlPath,
			title,
			['French (France)', 'German (Germany)', 'Spanish (Spain)']
		);

		const translatedZipPath = await writeZip(
			`${title}-en_US_translated.zip`,
			{
				[`${title}-en_US-de_DE.xlf`]: changeXliffModelId(
					translateXliff(
						await readXliff(zipPath, title, 'de_DE'),
						getTranslations(base, {
							content: 'Internetinhalt',
							description: 'Beschreibung',
							title: 'TiteldesWebinhalts',
						})
					)
				),
				[`${title}-en_US-es_ES.xlf`]: translateXliff(
					await readXliff(zipPath, title, 'es_ES'),
					getTranslations(base, spanish)
				),
				[`${title}-en_US-fr_FR.xlf`]: translateXliff(
					await readXliff(zipPath, title, 'fr_FR'),
					getTranslations(base, french)
				),
			}
		);

		// Submit the zip for workflow

		await exportImportTranslationPage.gotoImport(
			site.friendlyUrlPath,
			title
		);

		await exportImportTranslationPage.upload([translatedZipPath]);

		await exportImportTranslationPage.submit('Submit for Workflow');

		await expect(exportImportTranslationPage.successTitle).toHaveText(
			'2 Files Sent for Publication'
		);

		await expect(exportImportTranslationPage.failureTitle).toHaveText(
			'1 file could not be published.'
		);

		await expect(
			exportImportTranslationPage.resultItem(`${title}-en_US-de_DE.xlf`)
		).toContainText(
			'The translation file does not correspond to this web content.'
		);

		// Only the valid files have a pending entry and nothing is applied yet

		await translationsAdminPage.goto(site);

		for (const language of ['es-ES', 'fr-FR']) {
			await translationsAdminPage.assertEntry({
				language,
				status: 'Pending',
				title: `Translation of ${title} to ${language}`,
			});
		}

		await expect(
			page.getByRole('row').filter({hasText: `Translation of ${title}`})
		).toHaveCount(2);

		await translationsAdminPage.assertNoEntry(
			`Translation of ${title} to de-DE`
		);

		await expectNotTranslated(apiHelpers, page, site, title, 'es-ES');
		await expectNotTranslated(apiHelpers, page, site, title, 'fr-FR');

		// Approve both pending entries

		for (const language of ['es-ES', 'fr-FR']) {
			await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

			await workflowTasksPage.assignToMe(
				`Translation of ${title} to ${language}`
			);

			await workflowTasksPage.approve(
				`Translation of ${title} to ${language}`
			);
		}

		// The approved translations are applied to the web content

		await translationsAdminPage.goto(site);

		for (const language of ['es-ES', 'fr-FR']) {
			await translationsAdminPage.assertEntry({
				language,
				status: 'Approved',
				title: `Translation of ${title} to ${language}`,
			});
		}

		await expectTranslated(apiHelpers, page, site, title, 'es-ES', spanish);
		await expectTranslated(apiHelpers, page, site, title, 'fr-FR', french);
		await expectNotTranslated(apiHelpers, page, site, title, 'de-DE');
	}
);

test(
	'A rejected translation import can be imported again as a draft and resubmitted',
	{tag: '@LPD-110482'},
	async ({
		apiHelpers,
		exportImportTranslationPage,
		page,
		site,
		translationsAdminPage,
		workflowPage,
		workflowTasksPage,
	}) => {
		const title = getRandomString();

		const base = {...BASE, title};

		const entry = `Translation of ${title} to fr-FR`;

		const revisedFrench = {
			content: 'Contenu Web',
			description: 'La description',
			title: 'Titre du contenu Web',
		};

		await addWebContent(apiHelpers, site, title);

		await enableTranslationWorkflow(site, workflowPage);

		const zipPath = await exportImportTranslationPage.exportArticle(
			site.friendlyUrlPath,
			title,
			['French (France)']
		);

		const xliff = await readXliff(zipPath, title, 'fr_FR');

		const [filePath] = writeFiles({
			[`${title}-en_US-fr_FR.xlf`]: translateXliff(
				xliff,
				getTranslations(base, {
					content: 'ContenuWeb',
					description: 'Ladescription',
					title: 'TitreducontenuWeb',
				})
			),
		});

		const [revisedFilePath] = writeFiles({
			[`${title}-en_US-fr_FR.xlf`]: translateXliff(
				xliff,
				getTranslations(base, revisedFrench)
			),
		});

		// Submit the import for workflow and reject it

		await exportImportTranslationPage.gotoImport(
			site.friendlyUrlPath,
			title
		);

		await exportImportTranslationPage.upload([filePath]);

		await exportImportTranslationPage.submit('Submit for Workflow');

		await expect(exportImportTranslationPage.successTitle).toHaveText(
			'1 File Sent for Publication'
		);

		await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

		await workflowTasksPage.assignToMe(entry);

		await workflowTasksPage.reject(entry);

		await translationsAdminPage.goto(site);

		await translationsAdminPage.assertEntry({
			language: 'fr-FR',
			status: 'Pending',
			title: entry,
		});

		// Import a revised file as a draft while the entry is pending

		await exportImportTranslationPage.gotoImport(
			site.friendlyUrlPath,
			title
		);

		await exportImportTranslationPage.upload([revisedFilePath]);

		await expect(
			exportImportTranslationPage.importToolbar.getByRole('button', {
				name: 'Submit for Workflow',
			})
		).toBeDisabled();

		await exportImportTranslationPage.submit('Save as Draft');

		await expect(exportImportTranslationPage.successTitle).toHaveText(
			'1 File Saved as Draft'
		);

		// Resubmit and approve the entry

		await workflowTasksPage.resubmit(entry, site.friendlyUrlPath);

		await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

		await workflowTasksPage.assignToMe(entry);

		await workflowTasksPage.approve(entry);

		// The revised translation is applied to the web content

		await translationsAdminPage.goto(site);

		await translationsAdminPage.assertEntry({
			language: 'fr-FR',
			status: 'Approved',
			title: entry,
		});

		await expectTranslated(
			apiHelpers,
			page,
			site,
			title,
			'fr-FR',
			revisedFrench
		);
	}
);
