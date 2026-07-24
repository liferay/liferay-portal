/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor, within} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import AIAssistantSidebar from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/shells/AIAssistantSidebar';

function createBodyNode() {
	const bodyNode = document.createElement('div');

	bodyNode.textContent = 'Sidebar content';

	return bodyNode;
}

function renderSidebar(
	props: Partial<React.ComponentProps<typeof AIAssistantSidebar>> = {}
) {
	return render(
		<AIAssistantSidebar
			active
			bodyNode={createBodyNode()}
			onOpenChange={() => {}}
			open
			{...props}
		/>
	);
}

describe('AIAssistantSidebar', () => {
	let wrapper: HTMLDivElement;

	beforeEach(() => {

		// Clay's SidePanel derives its mobile behavior (focus trap that
		// aria-hides the rest of the page) from the body width, which is
		// always 0 in jsdom; report a desktop width instead.

		Object.defineProperty(document.body, 'clientWidth', {
			configurable: true,
			value: 1440,
		});

		wrapper = document.createElement('div');
		wrapper.id = 'wrapper';

		document.body.appendChild(wrapper);
	});

	afterEach(() => {
		wrapper.remove();
	});

	it('adopts the body node inside a labelled complementary region', () => {
		renderSidebar();

		expect(
			within(
				screen.getByRole('complementary', {name: 'ai-assistant'})
			).getByText('Sidebar content')
		).toBeInTheDocument();
	});

	it('has no accessibility violations while open', async () => {
		renderSidebar();

		await checkAccessibility({
			bestPractices: true,
			context: screen.getByRole('complementary', {
				name: 'ai-assistant',
			}),
		});
	});

	it('leaves the page container alone in the overlay behavior', () => {
		renderSidebar({behavior: 'overlay'});

		expect(wrapper).not.toHaveClass('ai-assistant-sidebar-push');
		expect(wrapper).not.toHaveClass('c-slideout-container');
	});

	it('pushes the page container while open', async () => {
		const bodyNode = createBodyNode();

		const {rerender} = render(
			<AIAssistantSidebar
				active
				bodyNode={bodyNode}
				onOpenChange={() => {}}
				open
			/>
		);

		expect(wrapper).toHaveClass('ai-assistant-sidebar-push');

		rerender(
			<AIAssistantSidebar
				active
				bodyNode={bodyNode}
				onOpenChange={() => {}}
				open={false}
			/>
		);

		await waitFor(() =>
			expect(wrapper).not.toHaveClass('ai-assistant-sidebar-push')
		);
	});
});
