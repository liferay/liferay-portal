/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';
import {questionsTest} from './fixtures/questionsTest';

const test = mergeTests(questionsTest);

test(
	'The user can create a question with the maximum of five tags and cannot exceed it.',
	{tag: '@LPD-93524'},
	async ({apiHelpers, page, questionsTopicsPage, site, widgetPagePage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await widgetPagePage.addPortlet('Questions');

		// Seed a topic through the API

		const topicName = getRandomString().replace(/-/g, '');

		await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
			siteId: site.id,
			title: topicName,
		});

		await page.reload();

		// A question can be created with five tags

		const questionTitle = getRandomString();

		await questionsTopicsPage.goToTopic(topicName);

		await page.getByRole('button', {name: 'Ask Question'}).click();
		await page
			.getByPlaceholder('What is your question?')
			.fill(questionTitle);
		await page.getByLabel('Source').click();
		await page
			.getByLabel(/Rich Text Editor/)
			.getByRole('textbox')
			.fill(getRandomString());
		await page.getByLabel('Source').click();

		const tagsInput = page.getByRole('combobox', {name: 'Tags'});

		for (let i = 0; i < 5; i++) {
			await tagsInput.fill(getRandomString());
			await tagsInput.press('Enter');
		}

		await page.getByLabel('Post Your Question').click();

		await expect(
			page.getByRole('link', {name: questionTitle})
		).toBeVisible();

		// A sixth tag is rejected as invalid

		await page.getByRole('button', {name: 'Ask Question'}).click();

		for (let i = 0; i < 6; i++) {
			await tagsInput.fill(getRandomString());
			await tagsInput.press('Enter');
		}

		await expect(page.getByText('This is an invalid tag.')).toBeVisible();
	}
);

test(
	'The user is notified about new questions with a followed tag until unsubscribing from it.',
	{tag: '@LPD-93524'},
	async ({
		apiHelpers,
		page,
		questionsPage,
		questionsTopicsPage,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await widgetPagePage.addPortlet('Questions');

		// Seed a topic and a tagged question through the API

		const tagName = getRandomString().replace(/-/g, '');
		const topicName = getRandomString().replace(/-/g, '');

		const messageBoardSection =
			await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
				siteId: site.id,
				title: topicName,
			});

		await apiHelpers.headlessDelivery.postMessageBoardSectionMessageBoardThread(
			{
				articleBody: getRandomString(),
				headline: getRandomString(),
				keywords: [tagName],
				messageBoardSectionId: messageBoardSection.id,
			}
		);

		// The subscriber follows the tag

		const administratorRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		const subscriber = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToRole(
			administratorRole.externalReferenceCode,
			subscriber.id
		);

		userData[subscriber.alternateName] = {
			name: subscriber.givenName,
			password: 'test',
			surname: subscriber.familyName,
		};

		await performUserSwitchViaApi(page, subscriber.alternateName);

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await page.locator('.navbar').getByText('Tags', {exact: true}).click();

		await page.getByLabel('Subscribe', {exact: true}).click();

		await expect(
			page.getByLabel('Subscribed', {exact: true})
		).toBeVisible();

		// The subscriber is notified when another user posts with the tag

		const secondQuestionTitle = getRandomString();

		await performUserSwitchViaApi(page, 'test');

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await questionsTopicsPage.goToTopic(topicName);
		await questionsPage.askQuestion(
			getRandomString(),
			secondQuestionTitle,
			[tagName]
		);

		await performUserSwitchViaApi(page, subscriber.alternateName);

		await expect(async () => {
			await page.reload();

			await expect(
				page.locator('a.panel-notifications-count')
			).toHaveText('1', {timeout: 5000});
		}).toPass();

		// No new notification arrives after unsubscribing from the tag

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await page.locator('.navbar').getByText('Tags', {exact: true}).click();

		await page.getByLabel('Subscribed', {exact: true}).click();

		await expect(page.getByLabel('Subscribe', {exact: true})).toBeVisible();

		const thirdQuestionTitle = getRandomString();

		await performUserSwitchViaApi(page, 'test');

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await questionsTopicsPage.goToTopic(topicName);
		await questionsPage.askQuestion(getRandomString(), thirdQuestionTitle, [
			tagName,
		]);

		await performUserSwitchViaApi(page, subscriber.alternateName);

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await questionsTopicsPage.goToTopic(topicName);

		await expect(
			page.getByRole('link', {name: thirdQuestionTitle})
		).toBeVisible();

		await page.reload();

		await expect(page.locator('a.panel-notifications-count')).toHaveText(
			'1'
		);
	}
);

test(
	'This is a test for LPS-119780. Question tags can be searched, sorted and opened in the Tags tab.',
	{tag: ['@LPS-119780', '@LPD-93524']},
	async ({apiHelpers, page, site, widgetPagePage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await widgetPagePage.addPortlet('Questions');

		// Seed a topic and three tagged questions through the API

		const firstQuestionTitle = getRandomString();
		const firstTagName = getRandomString().replace(/-/g, '');
		const secondTagName = getRandomString().replace(/-/g, '');
		const topicName = getRandomString().replace(/-/g, '');

		const messageBoardSection =
			await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
				siteId: site.id,
				title: topicName,
			});

		await apiHelpers.headlessDelivery.postMessageBoardSectionMessageBoardThread(
			{
				articleBody: getRandomString(),
				headline: firstQuestionTitle,
				keywords: [firstTagName],
				messageBoardSectionId: messageBoardSection.id,
			}
		);
		await apiHelpers.headlessDelivery.postMessageBoardSectionMessageBoardThread(
			{
				articleBody: getRandomString(),
				headline: getRandomString(),
				keywords: [firstTagName],
				messageBoardSectionId: messageBoardSection.id,
			}
		);
		await apiHelpers.headlessDelivery.postMessageBoardSectionMessageBoardThread(
			{
				articleBody: getRandomString(),
				headline: getRandomString(),
				keywords: [secondTagName],
				messageBoardSectionId: messageBoardSection.id,
			}
		);

		await page.reload();

		// The tags are ordered by number of usages by default

		await page.locator('.navbar').getByText('Tags', {exact: true}).click();

		const tagCards = page.locator('div.question-tags');

		await expect(tagCards.nth(0)).toContainText(firstTagName);
		await expect(tagCards.nth(0)).toContainText('Used 2 Times');
		await expect(tagCards.nth(1)).toContainText(secondTagName);
		await expect(tagCards.nth(1)).toContainText('Used 1 Times');

		// The tags can be searched

		const searchField = page.getByPlaceholder('Search', {exact: true});

		await searchField.fill(firstTagName);

		await expect(tagCards.filter({hasText: secondTagName})).toBeHidden();
		await expect(tagCards.filter({hasText: firstTagName})).toBeVisible();

		await page.getByLabel('Clear', {exact: true}).click();

		await expect(tagCards.filter({hasText: secondTagName})).toBeVisible();

		// The tags can be sorted by creation date and number of usages

		const orderBySelect = page.locator('select[id*="tagsOrderBy"]');

		await orderBySelect.selectOption({label: 'Latest Created'});

		await expect(tagCards.nth(0)).toContainText(secondTagName);
		await expect(tagCards.nth(1)).toContainText(firstTagName);

		await orderBySelect.selectOption({label: 'Number of Usages'});

		await expect(tagCards.nth(0)).toContainText(firstTagName);
		await expect(tagCards.nth(1)).toContainText(secondTagName);

		// A tag opens the questions that use it

		await tagCards.filter({hasText: firstTagName}).click();

		await expect(
			page.getByRole('link', {name: firstQuestionTitle})
		).toBeVisible();
	}
);

test(
	'This is a test for LPS-137561 and LPS-137633. JavaScript in topic names, tags and user names is not executed.',
	{tag: ['@LPS-137561', '@LPS-137633']},
	async ({apiHelpers, page, questionsTopicsPage, site, widgetPagePage}) => {
		let alertFired = false;

		page.on('dialog', async (dialog) => {
			if (dialog.type() === 'alert') {
				alertFired = true;
			}

			await dialog.dismiss();
		});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await widgetPagePage.addPortlet('Questions');

		// A topic name with JavaScript is rejected

		await page.getByText('New Topic', {exact: true}).click();
		await page
			.getByPlaceholder('Please enter a valid topic name.')
			.fill('<script>alert(123);</script>');
		await page.getByLabel('Create').click();

		await expect(
			page
				.getByText(/cannot contain the following invalid characters/)
				.first()
		).toBeVisible();

		// A tag with JavaScript is rejected

		const topicName = getRandomString().replace(/-/g, '');

		await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
			siteId: site.id,
			title: topicName,
		});

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);

		await questionsTopicsPage.goToTopic(topicName);

		await page.getByRole('button', {name: 'Ask Question'}).click();
		await page
			.getByPlaceholder('What is your question?')
			.fill(getRandomString());
		await page.getByLabel('Source').click();
		await page
			.getByLabel(/Rich Text Editor/)
			.getByRole('textbox')
			.fill(getRandomString());
		await page.getByLabel('Source').click();

		const tagsInput = page.getByRole('combobox', {name: 'Tags'});

		await tagsInput.fill('<script>alert(123);</script>');
		await tagsInput.press('Enter');

		await page.getByLabel('Post Your Question').click();

		// The question is rejected and the form stays open

		await expect(page.getByLabel('Post Your Question')).toBeVisible();

		// JavaScript in the user name is not executed when viewing a question

		const administratorRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		const author = await apiHelpers.headlessAdminUser.postUserAccount({
			familyName: "Test<script>alert('hello!')</script>",
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			administratorRole.externalReferenceCode,
			author.id
		);

		userData[author.alternateName] = {
			name: author.givenName,
			password: 'test',
			surname: author.familyName,
		};

		const questionTitle = getRandomString();

		await performUserSwitchViaApi(page, author.alternateName);

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);

		await questionsTopicsPage.goToTopic(topicName);

		await page.getByRole('button', {name: 'Ask Question'}).click();
		await page
			.getByPlaceholder('What is your question?')
			.fill(questionTitle);
		await page.getByLabel('Source').click();
		await page
			.getByLabel(/Rich Text Editor/)
			.getByRole('textbox')
			.fill(getRandomString());
		await page.getByLabel('Source').click();
		await page.getByLabel('Post Your Question').click();

		await page.getByRole('link', {name: questionTitle}).click();

		await expect(page.getByText(questionTitle).first()).toBeVisible();

		expect(alertFired).toBe(false);
	}
);
