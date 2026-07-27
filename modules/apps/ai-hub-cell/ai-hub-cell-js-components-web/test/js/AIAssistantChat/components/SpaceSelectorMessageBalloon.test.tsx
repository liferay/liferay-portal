/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import, @liferay/no-extraneous-dependencies
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import SpaceSelectorMessageBalloon from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/SpaceSelectorMessageBalloon';

const SPACES = [
	{externalReferenceCode: 'MARKETING', id: 1, name: 'Marketing', siteId: 1},
	{externalReferenceCode: 'SALES', id: 2, name: 'Sales', siteId: 2},
];

describe('SpaceSelectorMessageBalloon', () => {
	it('stores the selected space in the context ref and notifies the parent', async () => {
		const contextRef = {current: {}};
		const onSelectSpace = jest.fn();

		render(
			<SpaceSelectorMessageBalloon
				contextRef={contextRef}
				message="Select a Space"
				onSelectSpace={onSelectSpace}
				spaces={SPACES}
			/>
		);

		await userEvent.selectOptions(screen.getByLabelText('space'), '2');

		expect(contextRef.current).toEqual({spaceId: '2'});
		expect(onSelectSpace).toHaveBeenCalledWith({
			externalReferenceCode: 'SALES',
			id: 2,
			name: 'Sales',
			siteId: 2,
		});
	});

	it('disables the dropdown once a space is selected', async () => {
		render(
			<SpaceSelectorMessageBalloon
				contextRef={{current: {}}}
				message="Select a Space"
				onSelectSpace={jest.fn()}
				spaces={SPACES}
			/>
		);

		const select = screen.getByLabelText('space');

		await userEvent.selectOptions(select, '1');

		expect(select).toBeDisabled();
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<SpaceSelectorMessageBalloon
				contextRef={{current: {}}}
				message="Select a Space"
				onSelectSpace={jest.fn()}
				spaces={SPACES}
			/>
		);

		await checkAccessibility({context: container});
	});
});
