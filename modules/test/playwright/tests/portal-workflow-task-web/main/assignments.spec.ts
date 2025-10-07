/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import {userPersonalBarPagesTest} from '../../../fixtures/userPersonalBarPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	performUserSwitch,
	userData,
} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export const test = mergeTests(
	apiHelpersTest,
	blogsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest,
	userPersonalBarPagesTest,
	workflowPagesTest
);

let assetType: string;
let blogTitle: string;
let demoUserId: number;
let roleId: number;
let workflowDefinitionId: number;
let workflowDefinitionName: string;
let workflowXMLDefinition: string;

test.afterEach(
	async ({
		apiHelpers,
		blogsPage,
		configurationTabPage,
		processBuilderPage,
	}) => {
		if (assetType && workflowDefinitionName) {
			await processBuilderPage.goto();

			await configurationTabPage.goTo();

			await configurationTabPage.unassignWorkflowFromAssetType(assetType);
		}

		if (blogTitle) {
			await blogsPage.goto();
			await blogsPage.deleteAllBlogEntries();
		}

		if (roleId && demoUserId) {
			await apiHelpers.headlessAdminUser.deleteRoleUserAccountAssociation(
				roleId,
				demoUserId
			);
		}

		if (workflowDefinitionId) {
			await apiHelpers.headlessAdminWorkflow.deleteWorkflowDefinition(
				workflowDefinitionId
			);
		}

		assetType = null;
		blogTitle = null;
		demoUserId = null;
		roleId = null;
		workflowDefinitionId = null;
		workflowDefinitionName = null;
		workflowXMLDefinition = null;
	}
);

test('send user back to my workflow tasks page after assign another user to review', async ({
	apiHelpers,
	blogsEditBlogEntryPage,
	blogsPage,
	configurationTabPage,
	diagramViewPage,
	page,
	processBuilderPage,
	workflowTaskDetailsPage,
	workflowTasksPage,
}) => {
	const user = await apiHelpers.headlessAdminUser.postUserAccount({
		familyName: '<script>alert(0);</script>',
	});

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: '&lt;script&gt;alert(0);&lt;/script&gt;',
	};

	const role =
		await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	await performLogout(page);

	await performLogin(page, user.alternateName);

	workflowDefinitionName = 'Workflow Definition' + getRandomString();

	workflowXMLDefinition = readFileSync(
		__dirname +
			'/dependencies/administrator-role-assignments-workflow-definition.xml',
		'utf-8'
	);

	const workflowDefinition =
		await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionSave(
			workflowDefinitionName,
			{content: workflowXMLDefinition}
		);

	workflowDefinitionId = workflowDefinition.id;

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.publishWorkflowDefinition();

	await diagramViewPage.goBack();

	await configurationTabPage.goTo();

	assetType = 'Blogs Entry';

	await configurationTabPage.assignWorkflowToAssetType(
		workflowDefinitionName,
		assetType
	);

	await blogsPage.goto();

	await blogsPage.goToCreateBlogEntry();

	blogTitle = 'Blog Title' + getRandomInt();

	await blogsEditBlogEntryPage.editBlogEntry({
		content: 'Blog content.',
		submitToWorkflow: true,
		title: blogTitle,
	});

	await performLogout(page);

	await performLogin(page, 'test');

	page.on('dialog', async (dialog) => {
		dialog.accept();

		expect(dialog.message(), 'This alert should not be shown').toBeNull();
	});

	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTaskDetailsPage.selectAsset(blogTitle);

	await page.waitForTimeout(3000);

	await workflowTaskDetailsPage.reviewActionMenu.click();

	await workflowTaskDetailsPage.assignToMenuItem.click();

	await page.waitForLoadState('networkidle');

	await workflowTaskDetailsPage.selectAssignee(user.id.toString());

	await workflowTaskDetailsPage.assigneeDoneButton.click();

	await expect(workflowTasksPage.assignedToMyRolesLink).toBeVisible();
});

test('logged user must be able to see workflow task at least from a read-only perspective', async ({
	apiHelpers,
	configurationTabPage,
	diagramViewPage,
	messageBoardsEditThreadPage,
	messageBoardsWidgetPage,
	page,
	processBuilderPage,
	site,
	userPersonalBarPage,
	workflowTaskDetailsPage,
	workflowTasksPage,
}) => {
	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.company.admin@liferay.com'
		);

	demoUserId = user.id;

	const defaultUser =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'test@liferay.com'
		);

	const messageBoardWidget = getWidgetDefinition({
		id: getRandomString(),
		widgetName: 'com_liferay_message_boards_web_portlet_MBPortlet',
	});

	await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([messageBoardWidget]),
		siteId: site.id,
		title: getRandomString(),
	});

	const messageBoardPage =
		await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	workflowDefinitionName = 'MBWorkflowDefinition' + getRandomInt();
	workflowXMLDefinition = readFileSync(
		__dirname +
			'/dependencies/administrator-role-assignments-workflow-definition.xml',
		'utf-8'
	);

	const workflowDefinition =
		await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionSave(
			workflowDefinitionName,
			{content: workflowXMLDefinition}
		);

	workflowDefinitionId = workflowDefinition.id;

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.publishWorkflowDefinition();

	await configurationTabPage.goTo();

	await configurationTabPage.assignWorkflowToAssetType(
		workflowDefinitionName,
		'Message Boards Message'
	);

	await performUserSwitch(page, user.alternateName);

	const threadTitle = 'ThreadTitle' + getRandomInt();

	await page.goto(
		`/web${site.friendlyUrlPath}${messageBoardPage.friendlyURL}`
	);

	await messageBoardsEditThreadPage.publishNewThreadForWorkflow(
		threadTitle,
		'ThreadContent' + getRandomInt()
	);

	await performUserSwitch(page, defaultUser.alternateName);

	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTasksPage.assignToMe(threadTitle);

	await workflowTasksPage.reject(threadTitle);

	await performUserSwitch(page, user.alternateName);

	await userPersonalBarPage.notificationBadge.click();

	await page
		.getByRole('link', {
			name: `Your submission was rejected by ${defaultUser.name}, please modify and resubmit.`,
		})
		.first()
		.click();

	await workflowTaskDetailsPage.commentsButton.click();

	await workflowTaskDetailsPage.subscribeButton.click();

	await performUserSwitch(page, defaultUser.alternateName);

	await workflowTasksPage.goto();

	await workflowTaskDetailsPage.writeTaskComment(
		threadTitle,
		getRandomString()
	);

	await performUserSwitch(page, user.alternateName);

	await userPersonalBarPage.notificationBadge.click();

	await page
		.getByRole('link', {
			name: `${defaultUser.name} added a new comment to ${threadTitle}.`,
		})
		.click();

	await expect(workflowTaskDetailsPage.previewMessageBoards).toBeVisible();
	await expect(workflowTaskDetailsPage.reviewActionMenu).toBeHidden();

	await performUserSwitch(page, defaultUser.alternateName);
});

test('approve or reject modal appear even after doing a comment on the comments section', async ({
	blogsEditBlogEntryPage,
	blogsPage,
	configurationTabPage,
	page,
	workflowTaskDetailsPage,
	workflowTasksPage,
}) => {
	await configurationTabPage.goTo();

	workflowDefinitionName = 'Single Approver';

	assetType = 'Blogs Entry';

	await configurationTabPage.assignWorkflowToAssetType(
		workflowDefinitionName,
		assetType
	);

	await blogsPage.goto();

	await blogsPage.goToCreateBlogEntry();

	blogTitle = 'Blog Title' + getRandomInt();

	await blogsEditBlogEntryPage.editBlogEntry({
		content: 'Blog content.',
		submitToWorkflow: true,
		title: blogTitle,
	});

	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTasksPage.assignToMe(blogTitle);

	await workflowTaskDetailsPage.selectAsset(blogTitle);

	await page.waitForLoadState('networkidle');

	await workflowTaskDetailsPage.addComment('This is a comment');

	await page.waitForLoadState('networkidle');

	await workflowTaskDetailsPage.reviewActionMenu.click();

	await workflowTaskDetailsPage.approveMenuItem.click();

	await expect(page.getByRole('heading', {name: 'Approve'})).toBeVisible();

	await workflowTaskDetailsPage.cancelButton.click();

	await workflowTaskDetailsPage.reviewActionMenu.click();

	await workflowTaskDetailsPage.rejectMenuItem.click();

	await expect(page.getByRole('heading', {name: 'Reject'})).toBeVisible();
});

test('verify that the user can order the results inside Assigned to My Roles by Due Date', async ({
	apiHelpers,
	page,
	site,
	workflowTasksPage,
}) => {
	await test.step('assign the "Single Approver" workflow to Web Content Article', async () => {
		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.workflow}`
		);

		await page.waitForLoadState('networkidle');

		await page
			.getByRole('row', {name: 'Web Content Article'})
			.getByRole('button', {name: 'Edit'})
			.click();

		await page.getByRole('combobox').selectOption('Single Approver@1');

		await page.getByRole('button', {name: 'Save'}).click();
	});

	let webContent1;
	let webContent2;

	await test.step('create web contents', async () => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		webContent1 = await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: 'Web content 1'},
		});

		apiHelpers.data.push({
			id: `${site.id}_${webContent1.articleId}`,
			type: 'webContent',
		});

		webContent2 = await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: 'Web content 2'},
		});

		apiHelpers.data.push({
			id: `${site.id}_${webContent2.articleId}`,
			type: 'webContent',
		});
	});

	await test.step('update web content due dates and verify that entries are correctly ordered by date', async () => {
		await workflowTasksPage.goToAssignedToMyRoles();

		await workflowTasksPage.updateDueDate(webContent1.title, '10/02');

		await workflowTasksPage.updateDueDate(webContent2.title, '09/01');

		await page.getByLabel('Order').click();

		await page.getByRole('menuitem', {name: 'Due Date'}).click();

		await page.waitForLoadState('networkidle');

		const rowWebContent1 = page.getByRole('row', {name: webContent1.title});

		const rowWebContent2 = page.getByRole('row', {name: webContent2.title});

		const webContent1Index = await rowWebContent1.evaluate((row) =>
			Array.from(row.parentElement!.children).indexOf(row)
		);
		const webContent2Index = await rowWebContent2.evaluate((row) =>
			Array.from(row.parentElement!.children).indexOf(row)
		);

		expect(webContent2Index).toBeLessThan(webContent1Index);
	});
});
