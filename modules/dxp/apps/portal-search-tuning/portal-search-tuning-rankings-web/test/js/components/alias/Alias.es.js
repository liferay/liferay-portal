/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import Alias from '../../../../src/main/resources/META-INF/resources/js/components/alias/Alias.es';

import '@testing-library/jest-dom';

describe('Alias', () => {
	it('has a list of aliases available', () => {
		const {container} = render(
			<Alias keywords={['one', 'two', 'three']} onChange={jest.fn()} />
		);

		const tagsElement = container.querySelectorAll('.label-item-expand');

		expect(tagsElement[0]).toHaveTextContent('one');
		expect(tagsElement[1]).toHaveTextContent('two');
		expect(tagsElement[2]).toHaveTextContent('three');
	});

	it('prompts to input an alias', () => {
		const {getByText} = render(
			<Alias keywords={['one', 'two', 'three']} onChange={jest.fn()} />
		);

		expect(getByText('add-an-alias-instruction')).toBeInTheDocument();
	});

	it('updates the input value', () => {
		const {getByLabelText} = render(
			<Alias keywords={['one']} onChange={jest.fn()} />
		);

		const input = getByLabelText('aliases');

		fireEvent.change(input, {target: {value: 'test'}});

		expect(input.getAttribute('value')).toEqual('test');
	});
});
