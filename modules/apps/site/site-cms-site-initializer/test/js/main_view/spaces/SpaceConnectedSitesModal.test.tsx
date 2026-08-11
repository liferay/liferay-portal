/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import ConnectedSiteService from '../../../../src/main/resources/META-INF/resources/js/common/services/ConnectedSiteService';
import {Site} from '../../../../src/main/resources/META-INF/resources/js/common/types/Site';
import {SiteTemplate} from '../../../../src/main/resources/META-INF/resources/js/common/types/SiteTemplate';
import SpaceConnectedSitesModal from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceConnectedSitesModal';
import {mockFetch} from '../../__mocks__/frontend-js-web';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/ConnectedSiteService'
);

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

const mockGetConnectedSitesFromSpace =
	ConnectedSiteService.getConnectedSitesFromSpace as jest.MockedFunction<
		typeof ConnectedSiteService.getConnectedSitesFromSpace
	>;

const mockConnectSiteToSpace =
	ConnectedSiteService.connectSiteToSpace as jest.MockedFunction<
		typeof ConnectedSiteService.connectSiteToSpace
	>;

const mockDisconnectSiteFromSpace =
	ConnectedSiteService.disconnectSiteFromSpace as jest.MockedFunction<
		typeof ConnectedSiteService.disconnectSiteFromSpace
	>;

const mockedOpenToast = openToast as jest.Mock;

const mockConnectedSites: Site[] = [
	{
		descriptiveName: 'Connected Site 1',
		externalReferenceCode: '1',
		id: '1',
		logo: 'logo1.png',
		name: 'Guest',
		searchable: true,
	},
	{
		descriptiveName: 'Connected Site 2',
		externalReferenceCode: '2',
		id: '2',
		logo: 'logo2.png',
		name: 'Connected Site 2',
		searchable: false,
	},
];

const mockUnconnectedSite: Site = {
	descriptiveName: 'Unconnected Site 3',
	externalReferenceCode: '3',
	id: '3',
	logo: 'logo3.png',
	name: 'Unconnected Site 3',
	searchable: true,
};

const mockConnectedSiteTemplate: Site = {
	descriptiveName: 'Connected Template',
	externalReferenceCode: '101',
	id: '101',
	logo: '',
	name: 'Connected Template',
	searchable: false,
	type: 'SiteTemplate',
};

const mockUnconnectedSiteTemplate: SiteTemplate = {
	id: '102',
	name: 'Unconnected Template',
	siteExternalReferenceCode: '102-erc',
};

const mockAutocompleteFetch = ({
	siteTemplates = [],
	sites = [],
}: {
	siteTemplates?: SiteTemplate[];
	sites?: Site[];
} = {}) => {
	const itemsByPathname: Record<string, Site[] | SiteTemplate[]> = {
		'/o/headless-admin-site/v1.0/site-templates': siteTemplates,
		'/o/headless-admin-site/v1.0/sites': sites,
	};

	mockFetch.mockImplementation(async (...args: unknown[]) => {
		const url = String(args[0]);
		const items = itemsByPathname[new URL(url).pathname];

		if (!items) {
			throw new Error(`Unexpected autocomplete request: ${url}`);
		}

		return {
			headers: new Headers([['Content-Type', 'application/json']]),
			json: async () => ({items}),
		} as Response;
	});
};

const DEFAULT_PROPS = {
	externalReferenceCode: 'ERC',
	hasConnectSitesPermission: true,
};

const errorMessage = 'Connection failed';

const SPECIAL_CHARS_NAME = 'Marketing & Sales/EMEA';

const renderComponent = (props = DEFAULT_PROPS) => {
	return render(<SpaceConnectedSitesModal {...props} />);
};

const assertErrorToast = async (message = errorMessage) => {
	await waitFor(() => {
		expect(mockedOpenToast).toHaveBeenCalledTimes(1);

		expect(mockedOpenToast).toHaveBeenCalledWith({
			message,
			type: 'danger',
		});
	});
};

const assertSuccessToast = async (message: string) => {
	await waitFor(() => {
		expect(mockedOpenToast).toHaveBeenCalledTimes(1);

		expect(mockedOpenToast).toHaveBeenCalledWith({
			message,
			type: 'success',
		});
	});
};

const waitForComponentRendering = async () => {
	const connectedSite = await screen.findByText('Connected Site 1');

	expect(connectedSite).toBeInTheDocument();
};

describe('SpaceConnectedSitesModal', () => {
	const {ResizeObserver: ResizeObserverOriginal} = window;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	beforeEach(() => {
		jest.clearAllMocks();

		mockAutocompleteFetch();

		mockGetConnectedSitesFromSpace.mockResolvedValue({
			data: {items: mockConnectedSites},
			error: null,
		});

		mockConnectSiteToSpace.mockImplementation(
			async (_externalReferenceCode, siteErc, body) => ({
				data: {
					descriptiveName: mockUnconnectedSite.descriptiveName,
					externalReferenceCode: siteErc,
					id: siteErc,
					logo: mockUnconnectedSite.logo,
					name: mockUnconnectedSite.name,
					searchable: body?.searchable ?? false,
				},
				error: null,
			})
		);
		mockDisconnectSiteFromSpace.mockResolvedValue({
			data: null,
			error: null,
		});
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserverOriginal;
		jest.restoreAllMocks();
	});

	afterEach(() => {
		jest.restoreAllMocks();
		mockedOpenToast.mockClear();
	});

	it('checks the accessibility of the modal', async () => {
		const {container} = renderComponent();

		await act(async () => {
			await checkAccessibility({bestPractices: true, context: container});
		});
	});

	it('renders the modal header and the description', async () => {
		renderComponent();

		expect(screen.getByText('all-sites')).toBeInTheDocument();
		expect(
			screen.getByText('connect-sites-and-site-templates-to-this-space')
		).toBeInTheDocument();

		await waitFor(() => {
			expect(mockGetConnectedSitesFromSpace).toHaveBeenCalledWith(
				DEFAULT_PROPS.externalReferenceCode
			);
		});

		expect(await screen.findByText('Connected Site 1')).toBeInTheDocument();
		expect(await screen.findByText('Connected Site 2')).toBeInTheDocument();
	});

	it('displays an empty state message when no sites or templates are connected', async () => {
		mockGetConnectedSitesFromSpace.mockResolvedValue({
			data: {items: []},
			error: null,
		});

		renderComponent();

		expect(
			await screen.findByText('no-sites-are-connected-yet')
		).toBeInTheDocument();
	});

	it('renders connected site templates with a "Site Template" suffix', async () => {
		mockGetConnectedSitesFromSpace.mockResolvedValue({
			data: {items: [mockConnectedSiteTemplate]},
			error: null,
		});

		renderComponent();

		expect(
			await screen.findByText(
				`${mockConnectedSiteTemplate.descriptiveName} (site-template)`
			)
		).toBeInTheDocument();

		const templateRow = screen
			.getByText(
				`${mockConnectedSiteTemplate.descriptiveName} (site-template)`
			)
			.closest('li')!;

		expect(
			within(templateRow).getByText(/searchable-content: no/)
		).toBeInTheDocument();
	});

	describe('when hasConnectSitesPermission is true', () => {
		it('allows connecting a new site', async () => {
			mockAutocompleteFetch({
				sites: [...mockConnectedSites, mockUnconnectedSite],
			});

			renderComponent();
			await waitForComponentRendering();

			await userEvent.click(screen.getByPlaceholderText('select-a-site'));

			await waitFor(() => {
				expect(
					screen.getByRole('option', {
						name: mockUnconnectedSite.descriptiveName,
					})
				).toBeInTheDocument();
			});

			await userEvent.click(
				screen.getByRole('option', {
					name: mockUnconnectedSite.descriptiveName,
				})
			);

			await userEvent.click(
				screen.getByRole('button', {name: 'connect'})
			);

			await waitFor(() => {
				expect(mockConnectSiteToSpace).toHaveBeenCalledWith(
					DEFAULT_PROPS.externalReferenceCode,
					mockUnconnectedSite.externalReferenceCode
				);
			});

			await assertSuccessToast(
				'site-x-was-successfully-connected-to-the-space'
			);

			expect(
				screen.getByText(mockUnconnectedSite.descriptiveName)
			).toBeInTheDocument();
		});

		it('renders the site logo in the autocomplete options', async () => {
			mockAutocompleteFetch({sites: [mockUnconnectedSite]});

			renderComponent();
			await waitForComponentRendering();

			await userEvent.click(screen.getByPlaceholderText('select-a-site'));

			const option = await screen.findByRole('option', {
				name: mockUnconnectedSite.descriptiveName,
			});

			expect(option.querySelector('img')).toHaveAttribute(
				'src',
				mockUnconnectedSite.logo
			);
		});

		it('renders a site name containing HTML characters as typed', async () => {
			mockAutocompleteFetch({
				sites: [
					{
						...mockUnconnectedSite,
						descriptiveName: SPECIAL_CHARS_NAME,
					},
				],
			});

			renderComponent();
			await waitForComponentRendering();

			await userEvent.click(screen.getByPlaceholderText('select-a-site'));

			expect(
				await screen.findByRole('option', {name: SPECIAL_CHARS_NAME})
			).toBeInTheDocument();
		});

		it('renders a site template name containing HTML characters as typed', async () => {
			mockAutocompleteFetch({
				siteTemplates: [
					{...mockUnconnectedSiteTemplate, name: SPECIAL_CHARS_NAME},
				],
			});

			renderComponent();
			await waitForComponentRendering();

			await userEvent.click(screen.getByLabelText('sites'));

			await userEvent.click(
				screen.getByRole('option', {name: 'site-templates'})
			);

			await userEvent.click(
				screen.getByPlaceholderText('select-a-site-template')
			);

			expect(
				await screen.findByRole('option', {name: SPECIAL_CHARS_NAME})
			).toBeInTheDocument();
		});

		it('shows an error toast if connecting a site fails', async () => {
			mockAutocompleteFetch({
				sites: [...mockConnectedSites, mockUnconnectedSite],
			});

			mockConnectSiteToSpace.mockResolvedValue({
				data: null,
				error: errorMessage,
			});

			renderComponent();
			await waitForComponentRendering();

			await userEvent.click(screen.getByPlaceholderText('select-a-site'));

			await waitFor(() => {
				expect(
					screen.getByRole('option', {
						name: mockUnconnectedSite.descriptiveName,
					})
				).toBeInTheDocument();
			});

			await userEvent.click(
				screen.getByRole('option', {
					name: mockUnconnectedSite.descriptiveName,
				})
			);

			await userEvent.click(
				screen.getByRole('button', {name: 'connect'})
			);

			await assertErrorToast();
		});

		it('allows disconnecting a site', async () => {
			renderComponent();
			await waitForComponentRendering();

			const site1Row = (
				await screen.findByText('Connected Site 1')
			).closest('li')!;
			const actionsButton = within(site1Row).getByRole('button', {
				name: 'site-actions',
			});

			await userEvent.click(actionsButton);
			await userEvent.click(
				await screen.findByRole('menuitem', {name: 'disconnect'})
			);

			await waitFor(() => {
				expect(mockDisconnectSiteFromSpace).toHaveBeenCalledWith(
					DEFAULT_PROPS.externalReferenceCode,
					'1'
				);
			});

			await assertSuccessToast(
				'site-x-was-successfully-disconnected-from-the-space'
			);

			expect(
				screen.queryByText('Connected Site 1')
			).not.toBeInTheDocument();
		});

		it('shows an error toast if disconnecting a site fails', async () => {
			mockDisconnectSiteFromSpace.mockResolvedValue({
				data: null,
				error: errorMessage,
			});

			renderComponent();
			await waitForComponentRendering();

			const site1Row = (
				await screen.findByText('Connected Site 1')
			).closest('li')!;
			const actionsButton = within(site1Row).getByRole('button', {
				name: 'site-actions',
			});

			await userEvent.click(actionsButton);
			await userEvent.click(
				await screen.findByRole('menuitem', {name: 'disconnect'})
			);

			await assertErrorToast();
		});

		it('allows changing a site to be unsearchable', async () => {
			renderComponent();
			await waitForComponentRendering();

			const site1Row = screen
				.getByText('Connected Site 1')
				.closest('li')!;

			expect(
				within(site1Row).getByText(/searchable-content: yes/)
			).toBeInTheDocument();

			const actionsButton = within(site1Row).getByRole('button', {
				name: 'site-actions',
			});
			await userEvent.click(actionsButton);
			await userEvent.click(
				await screen.findByRole('menuitem', {name: 'make-unsearchable'})
			);

			await waitFor(() => {
				expect(mockConnectSiteToSpace).toHaveBeenCalledWith(
					DEFAULT_PROPS.externalReferenceCode,
					'1',
					{searchable: false}
				);
			});

			await waitFor(() => {
				expect(
					within(site1Row).getByText(/searchable-content: no/)
				).toBeInTheDocument();
			});
		});

		it('shows an error toast if changing a site to be unsearchable fails', async () => {
			mockConnectSiteToSpace.mockResolvedValue({
				data: null,
				error: errorMessage,
			});

			renderComponent();
			await waitForComponentRendering();

			const site1Row = screen
				.getByText('Connected Site 1')
				.closest('li')!;

			expect(
				within(site1Row).getByText(/searchable-content: yes/)
			).toBeInTheDocument();

			const actionsButton = within(site1Row).getByRole('button', {
				name: 'site-actions',
			});
			await userEvent.click(actionsButton);
			await userEvent.click(
				await screen.findByRole('menuitem', {name: 'make-unsearchable'})
			);

			await assertErrorToast();
		});

		it('disable connect to site button when site already selected', async () => {
			mockAutocompleteFetch({sites: [...mockConnectedSites]});

			renderComponent();
			await waitForComponentRendering();

			await userEvent.click(screen.getByPlaceholderText('select-a-site'));

			await waitFor(() => {
				expect(
					screen.getByRole('option', {
						name: mockConnectedSites[0].descriptiveName,
					})
				).toBeInTheDocument();
			});

			await userEvent.click(
				screen.getByRole('option', {
					name: mockConnectedSites[0].descriptiveName,
				})
			);

			expect(
				screen.getByRole('button', {name: 'connect'})
			).toBeDisabled();
		});

		it('excludes already-connected sites from the autocomplete request', async () => {
			mockAutocompleteFetch();

			renderComponent();
			await waitForComponentRendering();

			await userEvent.click(screen.getByPlaceholderText('select-a-site'));

			await waitFor(() => {
				const sitesURLs = mockFetch.mock.calls
					.map((call: unknown[]) => String(call[0]))
					.filter((url) => url.includes('/sites'));

				expect(
					sitesURLs.some(
						(url) =>
							url.includes('excludedExternalReferenceCodes=1') &&
							url.includes('excludedExternalReferenceCodes=2')
					)
				).toBe(true);
			});
		});

		it('excludes already-connected site templates from the autocomplete request', async () => {
			mockGetConnectedSitesFromSpace.mockResolvedValue({
				data: {items: [mockConnectedSiteTemplate]},
				error: null,
			});

			mockAutocompleteFetch();

			renderComponent();

			await screen.findByText(
				`${mockConnectedSiteTemplate.descriptiveName} (site-template)`
			);

			await userEvent.click(screen.getByLabelText('sites'));

			await userEvent.click(
				screen.getByRole('option', {name: 'site-templates'})
			);

			await userEvent.click(
				screen.getByPlaceholderText('select-a-site-template')
			);

			await waitFor(() => {
				const siteTemplatesURLs = mockFetch.mock.calls
					.map((call: unknown[]) => String(call[0]))
					.filter((url) => url.includes('/site-templates'));

				expect(
					siteTemplatesURLs.some((url) =>
						url.includes(
							`excludedSiteExternalReferenceCodes=${mockConnectedSiteTemplate.externalReferenceCode}`
						)
					)
				).toBe(true);
			});
		});

		it('switches the autocomplete placeholder when toggled to site templates', async () => {
			renderComponent();
			await waitForComponentRendering();

			expect(
				screen.getByPlaceholderText('select-a-site')
			).toBeInTheDocument();

			await userEvent.click(screen.getByLabelText('sites'));

			await userEvent.click(
				screen.getByRole('option', {name: 'site-templates'})
			);

			expect(
				screen.getByPlaceholderText('select-a-site-template')
			).toBeInTheDocument();
			expect(
				screen.queryByPlaceholderText('select-a-site')
			).not.toBeInTheDocument();
		});

		it('allows connecting a new site template', async () => {
			mockAutocompleteFetch({
				siteTemplates: [mockUnconnectedSiteTemplate],
			});

			mockConnectSiteToSpace.mockResolvedValue({
				data: {
					descriptiveName: mockUnconnectedSiteTemplate.name,
					externalReferenceCode:
						mockUnconnectedSiteTemplate.siteExternalReferenceCode!,
					id: mockUnconnectedSiteTemplate.id,
					logo: '',
					name: mockUnconnectedSiteTemplate.name,
					searchable: false,
					type: 'SiteTemplate',
				},
				error: null,
			});

			renderComponent();
			await waitForComponentRendering();

			await userEvent.click(screen.getByLabelText('sites'));

			await userEvent.click(
				screen.getByRole('option', {name: 'site-templates'})
			);

			await userEvent.click(
				screen.getByPlaceholderText('select-a-site-template')
			);

			await waitFor(() => {
				expect(
					screen.getByRole('option', {
						name: mockUnconnectedSiteTemplate.name,
					})
				).toBeInTheDocument();
			});

			await userEvent.click(
				screen.getByRole('option', {
					name: mockUnconnectedSiteTemplate.name,
				})
			);

			await userEvent.click(
				screen.getByRole('button', {name: 'connect'})
			);

			await waitFor(() => {
				expect(mockConnectSiteToSpace).toHaveBeenCalledWith(
					DEFAULT_PROPS.externalReferenceCode,
					mockUnconnectedSiteTemplate.siteExternalReferenceCode,
					{}
				);
			});

			await assertSuccessToast(
				'site-template-x-was-successfully-connected-to-the-space'
			);

			expect(
				screen.getByText(
					`${mockUnconnectedSiteTemplate.name} (site-template)`
				)
			).toBeInTheDocument();
		});

		it('shows an error toast if connecting a site template fails', async () => {
			mockAutocompleteFetch({
				siteTemplates: [mockUnconnectedSiteTemplate],
			});

			mockConnectSiteToSpace.mockResolvedValue({
				data: null,
				error: errorMessage,
			});

			renderComponent();
			await waitForComponentRendering();

			await userEvent.click(screen.getByLabelText('sites'));

			await userEvent.click(
				screen.getByRole('option', {name: 'site-templates'})
			);

			await userEvent.click(
				screen.getByPlaceholderText('select-a-site-template')
			);

			await waitFor(() => {
				expect(
					screen.getByRole('option', {
						name: mockUnconnectedSiteTemplate.name,
					})
				).toBeInTheDocument();
			});

			await userEvent.click(
				screen.getByRole('option', {
					name: mockUnconnectedSiteTemplate.name,
				})
			);

			await userEvent.click(
				screen.getByRole('button', {name: 'connect'})
			);

			await assertErrorToast();
		});

		it('disables the connect button when a site template is already connected', async () => {
			mockGetConnectedSitesFromSpace.mockResolvedValue({
				data: {items: [mockConnectedSiteTemplate]},
				error: null,
			});

			const alreadyConnectedTemplate: SiteTemplate = {
				id: mockConnectedSiteTemplate.id,
				name: mockConnectedSiteTemplate.name,
				siteExternalReferenceCode:
					mockConnectedSiteTemplate.externalReferenceCode,
			};

			mockAutocompleteFetch({
				siteTemplates: [alreadyConnectedTemplate],
			});

			renderComponent();

			expect(
				await screen.findByText(
					`${mockConnectedSiteTemplate.descriptiveName} (site-template)`
				)
			).toBeInTheDocument();

			await userEvent.click(screen.getByLabelText('sites'));

			await userEvent.click(
				screen.getByRole('option', {name: 'site-templates'})
			);

			await userEvent.click(
				screen.getByPlaceholderText('select-a-site-template')
			);

			await waitFor(() => {
				expect(
					screen.getByRole('option', {
						name: alreadyConnectedTemplate.name,
					})
				).toBeInTheDocument();
			});

			await userEvent.click(
				screen.getByRole('option', {
					name: alreadyConnectedTemplate.name,
				})
			);

			expect(
				screen.getByRole('button', {name: 'connect'})
			).toBeDisabled();
		});

		it('allows disconnecting a site template', async () => {
			mockGetConnectedSitesFromSpace.mockResolvedValue({
				data: {items: [mockConnectedSiteTemplate]},
				error: null,
			});

			renderComponent();

			const templateRow = (
				await screen.findByText(
					`${mockConnectedSiteTemplate.descriptiveName} (site-template)`
				)
			).closest('li')!;
			const actionsButton = within(templateRow).getByRole('button', {
				name: 'site-actions',
			});

			await userEvent.click(actionsButton);

			expect(
				await screen.findByRole('menuitem', {name: 'make-searchable'})
			).toBeInTheDocument();

			await userEvent.click(
				screen.getByRole('menuitem', {name: 'disconnect'})
			);

			await waitFor(() => {
				expect(mockDisconnectSiteFromSpace).toHaveBeenCalledWith(
					DEFAULT_PROPS.externalReferenceCode,
					mockConnectedSiteTemplate.externalReferenceCode
				);
			});

			await assertSuccessToast(
				'site-template-x-was-successfully-disconnected-from-the-space'
			);

			expect(
				screen.queryByText(
					`${mockConnectedSiteTemplate.descriptiveName} (site-template)`
				)
			).not.toBeInTheDocument();
		});
	});

	describe('without connect permissions', () => {
		const propsWithoutPermission = {
			...DEFAULT_PROPS,
			hasConnectSitesPermission: false,
		};

		it('does not render the site selector', async () => {
			renderComponent(propsWithoutPermission);
			await waitForComponentRendering();

			expect(
				screen.queryByPlaceholderText('select-a-site')
			).not.toBeInTheDocument();
			expect(
				screen.queryByRole('button', {name: 'connect'})
			).not.toBeInTheDocument();
		});

		it('does not render site actions', async () => {
			renderComponent(propsWithoutPermission);
			await waitForComponentRendering();

			const site1Row = screen
				.getByText('Connected Site 1')
				.closest('li')!;
			expect(
				within(site1Row).queryByRole('button', {name: 'site-actions'})
			).not.toBeInTheDocument();
		});
	});
});
