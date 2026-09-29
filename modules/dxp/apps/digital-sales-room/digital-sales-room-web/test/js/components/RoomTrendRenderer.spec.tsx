/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {ROOM_TREND_OPTIONS} from '../../../src/main/resources/META-INF/resources/js/common/utils/roomTrend';
import RoomTrendRenderer from '../../../src/main/resources/META-INF/resources/js/components/props_transformer/cell_renderers/RoomTrendRenderer';

describe('RoomTrendRenderer', () => {
	it.each(ROOM_TREND_OPTIONS.map((option) => [option.status, option.label]))(
		'renders trend %i with the "%s" label',
		(status, label) => {
			render(<RoomTrendRenderer value={status as number} />);

			expect(screen.getByText(label as string)).toBeInTheDocument();
		}
	);

	it('falls back to the first trend for an out-of-range value', () => {
		render(<RoomTrendRenderer value={99} />);

		expect(
			screen.getByText(ROOM_TREND_OPTIONS[0].label)
		).toBeInTheDocument();
	});

	it.each([[null], [undefined]])(
		'renders a fallback when the value is %s',
		(value) => {
			render(<RoomTrendRenderer value={value} />);

			expect(screen.getByText('--')).toBeInTheDocument();
		}
	);
});
