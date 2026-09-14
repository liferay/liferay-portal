/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {Carousel} from '../../src/main/resources/META-INF/resources/js/panels/Carousel';

function renderCarousel() {
	const result = render(
		<ClayIconSpriteContext.Provider value="/icons.svg">
			<Carousel className="grid" itemCount={3}>
				<span>one</span>

				<span>two</span>

				<span>three</span>
			</Carousel>
		</ClayIconSpriteContext.Provider>
	);

	const track = result.container.querySelector(
		'.editor-carousel-track'
	) as HTMLDivElement;

	return {...result, track};
}

function layOut(
	track: HTMLDivElement,
	{clientWidth, scrollLeft, scrollWidth}: Record<string, number>
) {
	Object.defineProperty(track, 'clientWidth', {
		configurable: true,
		value: clientWidth,
	});
	Object.defineProperty(track, 'scrollWidth', {
		configurable: true,
		value: scrollWidth,
	});
	Object.defineProperty(track, 'scrollLeft', {
		configurable: true,
		value: scrollLeft,
		writable: true,
	});
}

describe('Carousel', () => {
	it('shows no arrows while the track fits', () => {
		const {container} = renderCarousel();

		expect(
			container.querySelectorAll('.editor-carousel-arrow')
		).toHaveLength(0);
	});

	it('offers the arrows once the track overflows, pointer only', () => {
		const {container, track} = renderCarousel();

		track.style.overflowX = 'auto';
		layOut(track, {clientWidth: 200, scrollLeft: 0, scrollWidth: 600});

		fireEvent.scroll(track);

		const arrows = container.querySelectorAll('.editor-carousel-arrow');

		expect(arrows).toHaveLength(2);
		expect(arrows[0]).toBeDisabled();
		expect(arrows[1]).toBeEnabled();
		expect(arrows[0]).toHaveAttribute('aria-hidden', 'true');
		expect(arrows[0]).toHaveAttribute('tabindex', '-1');
	});

	it('scrolls by most of a page and flips the arrows at the end', () => {
		const {container, track} = renderCarousel();

		track.style.overflowX = 'auto';
		layOut(track, {clientWidth: 200, scrollLeft: 0, scrollWidth: 600});
		track.scrollBy = jest.fn();

		fireEvent.scroll(track);
		fireEvent.click(
			container.querySelectorAll('.editor-carousel-arrow')[1]
		);

		expect(track.scrollBy).toHaveBeenCalledWith({
			behavior: 'smooth',
			left: 160,
		});

		layOut(track, {clientWidth: 200, scrollLeft: 400, scrollWidth: 600});
		fireEvent.scroll(track);

		const arrows = container.querySelectorAll('.editor-carousel-arrow');

		expect(arrows[0]).toBeEnabled();
		expect(arrows[1]).toBeDisabled();
	});

	it('jumps instead of gliding when the account prefers reduced motion', () => {
		document.body.classList.add('c-prefers-reduced-motion');

		try {
			const {container, track} = renderCarousel();

			track.style.overflowX = 'auto';
			layOut(track, {clientWidth: 200, scrollLeft: 0, scrollWidth: 600});
			track.scrollBy = jest.fn();

			fireEvent.scroll(track);
			fireEvent.click(
				container.querySelectorAll('.editor-carousel-arrow')[1]
			);

			expect(track.scrollBy).toHaveBeenCalledWith({
				behavior: 'auto',
				left: 160,
			});
		}
		finally {
			document.body.classList.remove('c-prefers-reduced-motion');
		}
	});
});
