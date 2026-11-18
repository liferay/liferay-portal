import ClayLink, {ClayLinkContext} from '@clayui/link';
import React from 'react';
import RouterLink from '../RouterLink';
import {MemoryRouter} from 'react-router-dom';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const renderLink = (href: string, ref: React.RefObject<HTMLAnchorElement>) =>
	render(
		<MemoryRouter>
			<ClayLinkContext.Provider value={RouterLink}>
				<ClayLink href={href} ref={ref}>
					{'Sites'}
				</ClayLink>
			</ClayLinkContext.Provider>
		</MemoryRouter>
	);

describe('RouterLink', () => {
	it('forwards the ref of a router link to its anchor', () => {
		const ref = React.createRef<HTMLAnchorElement>();

		renderLink('/workspace/23/sites', ref);

		expect(ref.current).toBe(screen.getByRole('link', {name: /^sites$/i}));
		expect(ref.current).toHaveAttribute('href', '/workspace/23/sites');
	});

	it('forwards the ref of an external link to its anchor', () => {
		const ref = React.createRef<HTMLAnchorElement>();

		renderLink('https://www.liferay.com', ref);

		expect(ref.current).toBe(screen.getByRole('link', {name: /^sites$/i}));
		expect(ref.current).toHaveAttribute('href', 'https://www.liferay.com');
	});
});
