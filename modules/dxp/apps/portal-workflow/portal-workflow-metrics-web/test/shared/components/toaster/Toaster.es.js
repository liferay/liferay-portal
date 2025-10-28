/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import ToasterProvider from '../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
import {useToaster} from '../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/hooks/useToaster.es';

import '@testing-library/jest-dom';

const ComponentMock = () => {
	const toaster = useToaster();

	const add = () => {
		toaster.danger();
		toaster.info('Alert Info', 'Info');
		toaster.success();
		toaster.warning('Alert Warning', 'Warning');
	};

	const clear = () => {
		toaster.clearAll();
	};

	return (
		<>
			<button onClick={add}>add</button>
			<button onClick={clear}>clear</button>
		</>
	);
};

describe('The Toaster component should', () => {
	test('Render all alerts and clear all', () => {
		const {container, getByText} = render(<ComponentMock />, {
			wrapper: ToasterProvider,
		});

		const alertContainer = container.querySelector('.alert-container');
		const addBtn = getByText('add');
		const clearBtn = getByText('clear');

		fireEvent.click(addBtn);

		let alerts = container.querySelectorAll('.alert-dismissible');

		expect(alerts.length).toBe(4);

		fireEvent.click(alerts[0].children[1]);

		alerts = container.querySelectorAll('.alert-dismissible');

		expect(alerts.length).toBe(3);

		fireEvent.click(clearBtn);

		expect(alertContainer.children[0].children.length).toBe(0);
	});
});
