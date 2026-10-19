/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FieldValueMessageBalloon from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/FieldValueMessageBalloon';

describe('FieldValueMessageBalloon', () => {
	it('drops scripts and event handlers from a rich text value', () => {
		const {container} = render(
			<FieldValueMessageBalloon
				onApply={jest.fn()}
				onRegenerate={jest.fn()}
				richTextFieldNames={['content']}
				values={{
					content:
						'<p onclick="alert(1)">Safe</p><script>alert(1)</script><img onerror="alert(1)" src="x">',
				}}
			/>
		);

		expect(screen.getByText('Safe')).toBeInTheDocument();
		expect(container.innerHTML).not.toContain('alert(1)');
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<FieldValueMessageBalloon
				onApply={jest.fn()}
				onRegenerate={jest.fn()}
				richTextFieldNames={['content']}
				values={{
					content: '<p><strong>Live Energy.</strong> An app.</p>',
				}}
			/>
		);

		await checkAccessibility({context: container});
	});

	it('renders a plain text value as is', () => {
		render(
			<FieldValueMessageBalloon
				onApply={jest.fn()}
				onRegenerate={jest.fn()}
				richTextFieldNames={[]}
				values={{title: 'Fish & Chips'}}
			/>
		);

		expect(screen.getByText('Fish & Chips')).toBeInTheDocument();
	});

	it('renders a rich text value as formatted text instead of HTML tags', () => {
		render(
			<FieldValueMessageBalloon
				onApply={jest.fn()}
				onRegenerate={jest.fn()}
				richTextFieldNames={['content']}
				values={{
					content:
						'<p><strong>Live Energy.</strong> An easy app.</p><p>Second paragraph.</p>',
				}}
			/>
		);

		expect(screen.getByText('Live Energy.').tagName).toBe('STRONG');
		expect(screen.getByText('Second paragraph.').tagName).toBe('P');
		expect(screen.queryByText(/<p>|<strong>/)).toBeNull();
	});

	it('renders each value in its own element', () => {
		render(
			<FieldValueMessageBalloon
				onApply={jest.fn()}
				onRegenerate={jest.fn()}
				richTextFieldNames={['content']}
				values={{
					content: '<p>An easy app.</p>',
					title: 'Live Energy',
				}}
			/>
		);

		expect(screen.getByText('Live Energy')).not.toContainElement(
			screen.getByText('An easy app.')
		);
	});

	it('renders HTML in a plain text value literally', () => {
		render(
			<FieldValueMessageBalloon
				onApply={jest.fn()}
				onRegenerate={jest.fn()}
				richTextFieldNames={['content']}
				values={{title: '<b>AT&amp;T</b> news'}}
			/>
		);

		expect(screen.getByText('<b>AT&amp;T</b> news')).toBeInTheDocument();
	});
});
