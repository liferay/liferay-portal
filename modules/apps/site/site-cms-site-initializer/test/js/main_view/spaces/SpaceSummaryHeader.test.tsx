/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import manageConnectedSitesAction from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/manageConnectedSitesAction';
import manageMembersAction from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/manageMembersAction';
import SpaceSummaryHeader, {
	SpaceSummaryHeaderActions,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceSummaryHeader';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/manageMembersAction',
	() => jest.fn()
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/manageConnectedSitesAction',
	() => jest.fn()
);

describe('SpaceSummaryHeader', () => {
	const defaultProps = {
		label: 'View All',
		title: 'Recent Content',
		url: '',
	};

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders a title and a link when a url is provided', () => {
		render(<SpaceSummaryHeader {...defaultProps} url="/some-url" />);

		expect(
			screen.getByRole('heading', {name: defaultProps.title})
		).toBeInTheDocument();

		const link = screen.getByRole('link', {name: defaultProps.label});
		expect(link).toBeInTheDocument();
		expect(link).toHaveAttribute('href', '/some-url');
		expect(screen.queryByRole('button')).not.toBeInTheDocument();
	});

	it('renders a button instead of a link when modal props are provided and url is null', () => {
		const props = {
			...defaultProps,
			spaceModalProps: {
				action: SpaceSummaryHeaderActions.OPEN_MEMBERS_MODAL,
				assetLibraryCreatorUserId: '1',
				assetLibraryId: '2',
				externalReferenceCode: '3',
			},
		};

		render(<SpaceSummaryHeader {...props} />);

		expect(
			screen.getByRole('heading', {name: defaultProps.title})
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: defaultProps.label})
		).toBeInTheDocument();
		expect(screen.queryByRole('link')).not.toBeInTheDocument();
	});

	describe('manageMembersAction', () => {
		it.each([
			[false, undefined],
			[false, false],
			[true, true],
		])(
			'is called with hasAssignMembersPermission=%s when permissions.hasAssignMembersPermission is %s',
			async (
				expectedHasAssignMembersPermission,
				hasAssignMembersPermission
			) => {
				const spaceModalProps = {
					action: SpaceSummaryHeaderActions.OPEN_MEMBERS_MODAL,
					assetLibraryCreatorUserId: '123',
					externalReferenceCode: '789',
				};

				const props = {
					...defaultProps,
					permissions:
						hasAssignMembersPermission !== undefined
							? {
									hasAssignMembersPermission,
									hasConnectSitesPermission: false,
								}
							: undefined,
					spaceModalProps,
				};

				render(<SpaceSummaryHeader {...props} />);

				const button = screen.getByRole('button', {
					name: defaultProps.label,
				});

				await userEvent.click(button);

				expect(manageMembersAction).toHaveBeenCalledTimes(1);
				expect(manageMembersAction).toHaveBeenCalledWith(
					{
						assetLibraryCreatorUserId:
							spaceModalProps.assetLibraryCreatorUserId,
						externalReferenceCode:
							spaceModalProps.externalReferenceCode,
						hasAssignMembersPermission:
							expectedHasAssignMembersPermission,
						title: defaultProps.title,
					},
					expect.any(Function)
				);
			}
		);
	});

	describe('manageConnectedSitesAction', () => {
		it.each([
			[false, undefined],
			[false, false],
			[true, true],
		])(
			'is called with hasConnectSitesPermission=%s when permissions.hasConnectSitesPermission is %s',
			async (
				expectedHasConnectSitesPermission,
				hasConnectSitesPermission
			) => {
				const spaceModalProps = {
					action: SpaceSummaryHeaderActions.OPEN_SITES_MODAL,
					assetLibraryCreatorUserId: '123',
					assetLibraryId: '456',
					externalReferenceCode: '789',
				};

				const props = {
					...defaultProps,
					permissions:
						hasConnectSitesPermission !== undefined
							? {
									hasAssignMembersPermission: false,
									hasConnectSitesPermission,
								}
							: undefined,
					spaceModalProps,
				};

				render(<SpaceSummaryHeader {...props} />);

				const button = screen.getByRole('button', {
					name: defaultProps.label,
				});

				await userEvent.click(button);

				expect(manageConnectedSitesAction).toHaveBeenCalledTimes(1);
				expect(manageConnectedSitesAction).toHaveBeenCalledWith(
					{
						externalReferenceCode:
							spaceModalProps.externalReferenceCode,
						hasConnectSitesPermission:
							expectedHasConnectSitesPermission,
					},
					expect.any(Function)
				);
			}
		);
	});

	it('does not call manageMembersAction if action is not "open-members-modal"', async () => {
		const props = {
			...defaultProps,
			spaceModalProps: {
				action: 'some-other-action' as any,
				assetLibraryCreatorUserId: '1',
				externalReferenceCode: '3',
			},
		};

		render(<SpaceSummaryHeader {...props} />);

		const button = screen.getByRole('button', {name: defaultProps.label});
		await userEvent.click(button);

		expect(manageMembersAction).not.toHaveBeenCalled();
	});
});
