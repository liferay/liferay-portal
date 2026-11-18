jest.mock('shared/pages/ErrorPage', () => () => 'ErrorPage');

import * as API from 'shared/api';
import React from 'react';
import SegmentLinkRedirect from '../SegmentLinkRedirect';
import {act, fireEvent, render, screen} from '@testing-library/react';
import {
	Link,
	MemoryRouter,
	Route,
	Routes as RouterRoutes,
} from 'react-router-dom';

jest.unmock('react-dom');

const renderAt = (path: string) =>
	render(
		<MemoryRouter initialEntries={[path]}>
			<Link to="/workspace/23/123/sites">{'Sites'}</Link>

			<RouterRoutes>
				<Route path="workspace/:groupId/*">
					<Route
						element={
							<SegmentLinkRedirect>
								{'segment profile'}
							</SegmentLinkRedirect>
						}
						path=":channelId?/contacts/segments/:id/*"
					/>
				</Route>

				<Route
					element={<div>{'segment profile of channel 123'}</div>}
					path="workspace/:groupId/123/contacts/segments/:id"
				/>

				<Route
					element={<div>{'sites page'}</div>}
					path="workspace/:groupId/123/sites"
				/>
			</RouterRoutes>
		</MemoryRouter>
	);

describe('SegmentLinkRedirect', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders the segment when the URL has a channel', () => {
		renderAt('/workspace/23/789/contacts/segments/456');

		expect(screen.getByText('segment profile')).toBeInTheDocument();
		expect(API.individualSegment.fetch).not.toHaveBeenCalled();
	});

	it('replaces a link without a channel with the channel of the segment', async () => {
		(API.individualSegment.fetch as jest.Mock).mockReturnValueOnce(
			Promise.resolve({channelId: '123', id: '456'})
		);

		renderAt('/workspace/23/contacts/segments/456');

		expect(
			await screen.findByText('segment profile of channel 123')
		).toBeInTheDocument();
		expect(API.individualSegment.fetch).toHaveBeenCalledWith({
			groupId: '23',
			segmentId: '456',
		});
	});

	it('leaves the segment subpages alone', () => {
		renderAt('/workspace/23/contacts/segments/456/membership');

		expect(screen.getByText('segment profile')).toBeInTheDocument();
		expect(API.individualSegment.fetch).not.toHaveBeenCalled();
	});

	it('renders the error page when the segment cannot be loaded', async () => {
		(API.individualSegment.fetch as jest.Mock).mockReturnValueOnce(
			Promise.reject({})
		);

		renderAt('/workspace/23/contacts/segments/456');

		expect(await screen.findByText('ErrorPage')).toBeInTheDocument();
	});

	it('renders the error page for a segment without a channel', async () => {
		(API.individualSegment.fetch as jest.Mock).mockReturnValueOnce(
			Promise.resolve({channelId: null, id: '456'})
		);

		renderAt('/workspace/23/contacts/segments/456');

		expect(await screen.findByText('ErrorPage')).toBeInTheDocument();
	});

	it('stays on the page the user moved to while the segment loads', async () => {
		let resolveSegment: (segment: {
			channelId: string;
			id: string;
		}) => void = () => undefined;

		(API.individualSegment.fetch as jest.Mock).mockReturnValueOnce(
			new Promise((resolve) => {
				resolveSegment = resolve;
			})
		);

		renderAt('/workspace/23/contacts/segments/456');

		fireEvent.click(screen.getByText(/^sites$/i));

		await act(async () => {
			resolveSegment({channelId: '123', id: '456'});
		});

		expect(screen.getByText('sites page')).toBeInTheDocument();
		expect(
			screen.queryByText('segment profile of channel 123')
		).not.toBeInTheDocument();
	});
});
