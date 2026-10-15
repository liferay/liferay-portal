/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import, @liferay/no-extraneous-dependencies
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import React from 'react';

import WritingAssistantActions from '../../../../src/main/resources/META-INF/resources/js/WritingAssistant/components/WritingAssistantActions';

function renderWritingAssistantActions() {
	return render(
		<WritingAssistantActions
			containerRef={document.body}
			handleActionClick={jest.fn()}
			hideBalloon={jest.fn()}
		/>
	);
}

describe('WritingAssistantActions', () => {
	it('is accessible', async () => {
		const {container} = renderWritingAssistantActions();

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('offers the AI writing tools', () => {
		renderWritingAssistantActions();

		expect(
			screen.getByRole('button', {name: 'fix-spelling-and-grammar'})
		).toBeEnabled();
		expect(
			screen.getByRole('button', {name: 'improve-writing'})
		).toBeEnabled();
	});
});
