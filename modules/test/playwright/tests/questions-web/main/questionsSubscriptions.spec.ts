/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';
import {questionsTest} from './fixtures/questionsTest';

const test = mergeTests(questionsTest);

test(
	'This is a test for LPS-159138, LPS-159144 and LPS-159160. The user can subscribe to and unsubscribe from a question, a tag and a topic.',
	{tag: ['@LPS-159138', '@LPS-159144', '@LPS-159160']},
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

		// Seed a topic through the API

		const topicName = getRandomString().replace(/-/g, '');

		await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
			siteId: site.id,
			title: topicName,
		});

		// Asking a question subscribes the user to it

		const questionTitle = getRandomString();
		const tagName = getRandomString();

		await page.reload();

		await questionsTopicsPage.goToTopic(topicName);
		await questionsPage.addNewQuestion(
			getRandomString(),
			questionTitle,
			tagName
		);

		await page.locator('.navbar').getByText('Subscriptions').click();

		await expect(
			page.getByRole('link', {name: questionTitle})
		).toBeVisible();

		// The user can unsubscribe from the question

		await page.getByRole('link', {name: questionTitle}).click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByText('Unsubscribe', {exact: true}),
			trigger: page
				.locator(
					'.questions-container button:has(svg.lexicon-icon-ellipsis-v)'
				)
				.first(),
		});

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await page.locator('.navbar').getByText('Subscriptions').click();

		await expect(
			page.getByRole('link', {name: questionTitle})
		).toBeHidden();

		// The user can subscribe to a tag

		await page.locator('.navbar').getByText('Tags', {exact: true}).click();

		await page.getByLabel('Subscribe', {exact: true}).click();

		await page.locator('.navbar').getByText('Subscriptions').click();

		await expect(
			page
				.locator(`a[title*="${tagName}"]`)
				.getByLabel('Subscribed', {exact: true})
		).toBeVisible();

		// The user can unsubscribe from the tag

		await page.locator('a').filter({hasText: 'Tags'}).click();

		await page.getByLabel('Subscribed', {exact: true}).click();

		await page.locator('.navbar').getByText('Subscriptions').click();

		await expect(page.locator(`a[title*="${tagName}"]`)).toBeHidden();

		// The user can subscribe to a topic

		await page
			.locator('.navbar')
			.getByText('Questions', {exact: true})
			.click();
		await questionsTopicsPage.goToTopic(topicName);

		await page.getByLabel('Subscribe', {exact: true}).click();

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await page.locator('.navbar').getByText('Subscriptions').click();

		await expect(page.getByRole('link', {name: topicName})).toBeVisible();

		// The user can unsubscribe from the topic

		await questionsTopicsPage.goToTopic(topicName);

		await page.getByLabel('Subscribed', {exact: true}).click();

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await page.locator('.navbar').getByText('Subscriptions').click();

		await expect(page.getByRole('link', {name: topicName})).toBeHidden();
	}
);

test(
	'This is a test for LPS-159147, LPS-159156 and LPS-159158. The user is notified when a followed topic has a new answer, comment or question.',
	{tag: ['@LPS-159147', '@LPS-159156', '@LPS-159158']},
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

		// Seed a topic and a question through the API

		const topicName = getRandomString().replace(/-/g, '');

		const messageBoardSection =
			await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
				siteId: site.id,
				title: topicName,
			});

		const firstQuestionTitle = getRandomString();

		await apiHelpers.headlessDelivery.postMessageBoardSectionMessageBoardThread(
			{
				articleBody: getRandomString(),
				headline: firstQuestionTitle,
				messageBoardSectionId: messageBoardSection.id,
			}
		);

		// The subscriber follows the topic

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
		await questionsTopicsPage.goToTopic(topicName);

		await page.getByLabel('Subscribe', {exact: true}).click();

		await expect(
			page.getByLabel('Subscribed', {exact: true})
		).toBeVisible();

		// Another user answers the question, asks a question and comments

		await performUserSwitchViaApi(page, 'test');

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await questionsTopicsPage.goToTopic(topicName);
		await page.getByRole('link', {name: firstQuestionTitle}).click();
		await questionsPage.answerQuestion(getRandomString());

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await questionsTopicsPage.goToTopic(topicName);
		await questionsPage.addNewQuestion(
			getRandomString(),
			getRandomString(),
			getRandomString()
		);

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);
		await questionsTopicsPage.goToTopic(topicName);
		await page.getByRole('link', {name: firstQuestionTitle}).click();
		await questionsPage.addComment(getRandomString());

		// The subscriber is notified about the three activities

		await performUserSwitchViaApi(page, subscriber.alternateName);

		await expect(async () => {
			await page.reload();

			await expect(
				page.locator('a.panel-notifications-count')
			).toHaveText('3', {timeout: 5000});
		}).toPass();

		await page.locator('a.panel-notifications-count').click();

		await expect(
			page.getByText('Test Test added a new message boards message.')
		).toHaveCount(3);
	}
);
