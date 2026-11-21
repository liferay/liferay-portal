/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import '@testing-library/jest-dom';

import autoFields from '../../src/main/resources/META-INF/resources/auto_fields/auto_fields';

function renderAutoFields() {
	document.body.innerHTML = `
		<form>
			<fieldset id="rowsId">
				<div class="lfr-form-row">
					<div class="row-fields">
						<input id="field_0" name="field_0" type="text" value="Row 1" />
					</div>
				</div>
				<div class="lfr-form-row">
					<div class="row-fields">
						<input id="field_1" name="field_1" type="text" value="Row 2" />
					</div>
				</div>
			</fieldset>
		</form>
	`;

	autoFields({
		contentBox: 'fieldset#rowsId',
		namespace: '',
	});
}

function getRow(value) {
	return screen.getByDisplayValue(value).closest('.lfr-form-row');
}

describe('AutoFields', () => {
	const languageGet = Liferay.Language.get.getMockImplementation();

	beforeEach(() => {
		Liferay.Language.get.mockImplementation((key) =>
			key === 'undo-x' ? 'Undo {0}' : languageGet(key)
		);
	});

	afterEach(() => {
		Liferay.Language.get.mockImplementation(languageGet);
	});

	it('marks a deleted row as hidden', async () => {
		renderAutoFields();

		await userEvent.click(screen.getAllByTitle('remove')[0]);

		expect(getRow('Row 1')).toHaveAttribute('hidden');
		expect(getRow('Row 2')).not.toHaveAttribute('hidden');
	});

	it('unmarks a deleted row when the deletion is undone', async () => {
		renderAutoFields();

		await userEvent.click(screen.getAllByTitle('remove')[0]);

		await userEvent.click(screen.getByRole('link', {name: 'Undo (1)'}));

		expect(getRow('Row 1')).not.toHaveAttribute('hidden');
	});
});
