/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FieldMappingChannelFieldRenderer from '../../../src/main/resources/META-INF/resources/js/cell_renderers/FieldMappingChannelFieldRenderer';

describe('FieldMappingChannelFieldRenderer', () => {
	it('links the channel field to its mapping page', () => {
		render(
			<FieldMappingChannelFieldRenderer
				itemData={{href: '/map-channel-field?channelField=name'}}
				value="Name"
			/>
		);

		expect(screen.getByRole('link', {name: 'Name'})).toHaveAttribute(
			'href',
			'/map-channel-field?channelField=name'
		);
	});
});
