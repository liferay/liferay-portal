/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ItemWithStickerRenderer from '../../../../src/main/resources/META-INF/resources/js/props_transformer/cell_renderers/ItemWithStickerRenderer';

describe('ItemWithStickerRenderer', () => {
	it('renders the label alongside its sticker content', () => {
		render(
			<ItemWithStickerRenderer
				label="Marketing"
				stickerContent={<span>sticker</span>}
			/>
		);

		expect(screen.getByText('Marketing')).toBeInTheDocument();
		expect(screen.getByText('sticker')).toBeInTheDocument();
	});

	it('renders an optional suffix when provided', () => {
		render(
			<ItemWithStickerRenderer
				label="Jane Doe"
				stickerContent={<span>sticker</span>}
				suffix={<span>(owner)</span>}
			/>
		);

		expect(screen.getByText('(owner)')).toBeInTheDocument();
	});
});
