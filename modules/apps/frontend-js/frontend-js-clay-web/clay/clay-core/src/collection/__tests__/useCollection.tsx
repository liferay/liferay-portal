/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayPortal} from '@clayui/shared';
import {cleanup, render, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useRef, useState} from 'react';

import {Collection} from '../Collection';

global.ResizeObserver = require('resize-observer-polyfill');

function VirtualizedDropdown({items}: {items: Array<string>}) {
	const parentRef = useRef<HTMLDivElement>(null);
	const [isOpen, setIsOpen] = useState(false);

	return (
		<>
			<button onClick={() => setIsOpen(true)} type="button">
				Open
			</button>

			{isOpen && (
				<ClayPortal>
					<div
						ref={parentRef}
						style={{height: 200, overflow: 'auto'}}
					>
						<Collection
							as="ul"
							items={items}
							parentRef={parentRef}
							virtualize
						>
							{(item: string) => (
								<li key={item} role="option">
									{item}
								</li>
							)}
						</Collection>
					</div>
				</ClayPortal>
			)}
		</>
	);
}

describe('useCollection', () => {
	afterEach(cleanup);

	it('observes virtualized items on first mount even when the host container is initially detached from the document (LPD-87534)', async () => {

		// Without the deferred measurement in `useCollection`, item refs fire
		// while the portal container is still detached from the document, so
		// `_measureElement` exits early on `!node.isConnected`,
		// `ResizeObserver.observe` is never called for the items, and the
		// virtualizer stays at the `estimateSize` fallback — which is what
		// causes long labels to overlap on first open.

		const observeSpy = jest.spyOn(
			(global as any).ResizeObserver.prototype,
			'observe'
		);

		const items = ['one', 'two', 'three'];

		const {getByText} = render(<VirtualizedDropdown items={items} />);

		userEvent.click(getByText('Open'));

		await waitFor(() => {
			const observedItems = observeSpy.mock.calls
				.map((call) => call[0] as HTMLElement)
				.filter((node) => node?.dataset?.index !== undefined);

			expect(observedItems).toHaveLength(items.length);
		});

		observeSpy.mockRestore();
	});
});
