/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React, {useState} from 'react';

import '@testing-library/jest-dom';

import {
	EditorInstanceProvider,
	nextEditorInstancePrefix,
	useEditorId,
} from '../../src/main/resources/META-INF/resources/js/chrome/instance';

function Field({label}: {label: string}) {
	const eid = useEditorId();

	return (
		<>
			<label htmlFor={eid('field')}>{label}</label>

			<input id={eid('field')} />
		</>
	);
}

function Editor({label}: {label: string}) {
	const [prefix] = useState(nextEditorInstancePrefix);

	return (
		<EditorInstanceProvider value={prefix}>
			<Field label={label} />
		</EditorInstanceProvider>
	);
}

describe('per-instance ids', () => {
	it('keeps two editors on the same page apart and their labels sound', () => {
		render(
			<>
				<Editor label="first" />
				<Editor label="second" />
			</>
		);

		const first = screen.getByLabelText('first');
		const second = screen.getByLabelText('second');

		expect(first.id).toMatch(/^aie\d+-field$/);
		expect(second.id).toMatch(/^aie\d+-field$/);
		expect(first.id).not.toBe(second.id);
	});
});
