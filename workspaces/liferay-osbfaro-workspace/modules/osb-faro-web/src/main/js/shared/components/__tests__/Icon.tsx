import AC_ICONS from '../ac-icons';
import fs from 'fs';
import Icon, {CLAY_ICONS_SPRITEMAP} from '../Icon';
import path from 'path';
import React from 'react';
import {ClayIconSpriteContext} from '@clayui/icon';
import {cleanup, render} from '@testing-library/react';

jest.unmock('react-dom');

const AC_ICONS_DIR = path.join(__dirname, '..', '..', '..', '..', 'images');

describe('Icon', () => {
	afterEach(cleanup);

	it('should draw an Analytics Cloud icon inline', () => {
		const {container} = render(
			<Icon className="text-secondary" symbol="ac_hidden" />
		);

		const svg = container.querySelector('svg');

		expect(svg).toHaveAttribute('data-file', 'ac_hidden.svg');
		expect(svg).toHaveAttribute('role', 'presentation');
		expect(svg).toHaveClass(
			'lexicon-icon',
			'lexicon-icon-ac_hidden',
			'text-secondary'
		);
		expect(container.querySelector('use')).toBeNull();
	});

	it('should draw a Clay icon from the @clayui/css sprite', () => {
		const {container} = render(
			<ClayIconSpriteContext.Provider value={CLAY_ICONS_SPRITEMAP}>
				<Icon symbol="plus" />
			</ClayIconSpriteContext.Provider>
		);

		expect(CLAY_ICONS_SPRITEMAP).toBe('icons.svg');
		expect(container.querySelector('svg')).toHaveClass('lexicon-icon-plus');
		expect(container.querySelector('use')).toHaveAttribute(
			'href',
			'icons.svg#plus'
		);
	});

	// An icon missing from the registry is not an error: its name falls through
	// to the Clay sprite, which has no such symbol, and the icon renders blank.

	it('should register every Analytics Cloud icon', () => {
		const files = fs
			.readdirSync(AC_ICONS_DIR)
			.filter((file) => file.endsWith('.svg'))
			.map((file) => path.basename(file, '.svg'));

		expect(Object.keys(AC_ICONS).sort()).toEqual(files.sort());
	});
});
