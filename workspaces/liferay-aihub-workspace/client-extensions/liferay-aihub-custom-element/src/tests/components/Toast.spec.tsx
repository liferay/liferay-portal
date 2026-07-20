/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render} from '@testing-library/react';
import React from 'react';
import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';

import Toast from '../../components/Toast';

describe('Toast', () => {
	beforeEach(() => {
		vi.useFakeTimers();
	});

	afterEach(() => {
		vi.useRealTimers();
	});

	it('dismisses after 4 seconds', () => {
		const onDismiss = vi.fn();

		render(<Toast message="Saved" onDismiss={onDismiss} />);

		expect(onDismiss).not.toHaveBeenCalled();

		act(() => vi.advanceTimersByTime(4000));

		expect(onDismiss).toHaveBeenCalledTimes(1);
	});

	it('does not reset the timer when re-rendered with a new onDismiss reference', () => {
		const onDismiss = vi.fn();

		const {rerender} = render(
			<Toast message="Saved" onDismiss={() => onDismiss()} />
		);

		act(() => vi.advanceTimersByTime(3000));

		// A parent re-render passes a brand-new inline callback while the
		// message is unchanged; the pending timer must survive.

		rerender(<Toast message="Saved" onDismiss={() => onDismiss()} />);

		act(() => vi.advanceTimersByTime(1000));

		expect(onDismiss).toHaveBeenCalledTimes(1);
	});

	it('invokes the latest onDismiss callback', () => {
		const stale = vi.fn();
		const latest = vi.fn();

		const {rerender} = render(<Toast message="Saved" onDismiss={stale} />);

		rerender(<Toast message="Saved" onDismiss={latest} />);

		act(() => vi.advanceTimersByTime(4000));

		expect(stale).not.toHaveBeenCalled();
		expect(latest).toHaveBeenCalledTimes(1);
	});
});
