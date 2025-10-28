/* eslint-disable no-unused-vars */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import {SidebarHeader} from '../../../../src/main/resources/META-INF/resources/js/components/Sidebar';
import BasicInformation from '../../../../src/main/resources/META-INF/resources/js/components/layout_reports/BasicInformation';
import ErrorAlert from '../../../../src/main/resources/META-INF/resources/js/components/layout_reports/ErrorAlert';
import {
	PAGE_SPEED_API_KEY_ERROR_CODE,
	PAGE_SPEED_API_KEY_INVALID_STATUS,
	PAGE_SPEED_SERVER_ERROR_CODE,
} from '../../../../src/main/resources/META-INF/resources/js/constants/googlePageSpeed';
import {StoreContextProvider} from '../../../../src/main/resources/META-INF/resources/js/context/StoreContext';
import {pageURLs} from '../../mocks';

const getErrorAlertComponent = ({data = null, error = null} = {}) => {
	return (
		<StoreContextProvider
			value={{
				data,
				error,
			}}
		>
			<SidebarHeader />

			<BasicInformation
				defaultLanguageId={pageURLs[0].languageId}
				pageURLs={pageURLs}
				selectedLanguageId={pageURLs[0].languageId}
			/>

			<ErrorAlert />
		</StoreContextProvider>
	);
};

function getParentAlert(element) {
	return element.parentElement;
}

describe('Error Alert', () => {
	describe('Unknown error cases', () => {
		it('Renders unknown error alert if no data nor error objects are available', () => {
			const {getByRole, getByText} = render(getErrorAlertComponent());

			expect(
				getByText('an-unexpected-error-occurred')
			).toBeInTheDocument();

			expect(getParentAlert(getByRole('alert')).className).toContain(
				'danger'
			);
		});

		it('Renders unknown error alert if no error is specified', () => {
			const {getByRole, getByText} = render(
				getErrorAlertComponent({
					data: {
						configureGooglePageSpeedURL:
							'a valid configureGooglePageSpeedURL',
					},
				})
			);

			expect(
				getByText('an-unexpected-error-occurred')
			).toBeInTheDocument();

			expect(getParentAlert(getByRole('alert')).className).toContain(
				'danger'
			);
		});
	});

	describe('Invalid API Key cases', () => {
		it('Renders danger alert component with set api key button and show error button if api key is invalid', () => {
			const {getByRole, getByText, queryByText} = render(
				getErrorAlertComponent({
					data: {
						configureGooglePageSpeedURL:
							'a valid configureGooglePageSpeedURL',
					},
					error: {
						code: PAGE_SPEED_API_KEY_ERROR_CODE,
						status: PAGE_SPEED_API_KEY_INVALID_STATUS,
					},
				})
			);

			expect(getByText('the-api-key-is-invalid')).toBeInTheDocument();
			expect(getByText('set-api-key')).toBeInTheDocument();
			expect(getByText('show-details')).toBeInTheDocument();
			expect(
				queryByText(
					'you-can-set-the-api-key-from-site-settings-pages-google-pagespeed-insights'
				)
			).not.toBeInTheDocument();

			expect(getParentAlert(getByRole('alert')).className).toContain(
				'danger'
			);
		});

		it('Renders dange alert component with proper message and without button if user has not enough privileges or no URL is provided', () => {
			const {getByText, queryByText} = render(
				getErrorAlertComponent({
					data: {
						configureGooglePageSpeedURL: null,
					},
					error: {
						code: PAGE_SPEED_API_KEY_ERROR_CODE,
						status: PAGE_SPEED_API_KEY_INVALID_STATUS,
					},
				})
			);

			expect(queryByText('set-api-key')).not.toBeInTheDocument();
			expect(
				getByText(
					'you-can-set-the-api-key-from-site-settings-pages-google-pagespeed-insights'
				)
			).toBeInTheDocument();
		});

		it('Show error details button shows the extended error information and changes button text on click', () => {
			const {getByText, queryByText} = render(
				getErrorAlertComponent({
					data: {
						configureGooglePageSpeedURL:
							'a valid configureGooglePageSpeedURL',
					},
					error: {
						code: PAGE_SPEED_API_KEY_ERROR_CODE,
						status: PAGE_SPEED_API_KEY_INVALID_STATUS,
					},
				})
			);

			let showButton = queryByText('show-details');
			let hideButton = queryByText('hide-details');

			expect(
				queryByText(PAGE_SPEED_API_KEY_INVALID_STATUS)
			).not.toBeInTheDocument();
			expect(showButton).toBeInTheDocument();
			expect(hideButton).not.toBeInTheDocument();

			userEvent.click(showButton);

			showButton = queryByText('show-details');
			hideButton = queryByText('hide-details');

			expect(
				getByText(PAGE_SPEED_API_KEY_INVALID_STATUS)
			).toBeInTheDocument();
			expect(hideButton).toBeInTheDocument();
			expect(showButton).not.toBeInTheDocument();
		});

		it('Renders danger alert component with page language selector', () => {
			const {getByText} = render(
				getErrorAlertComponent({
					data: {
						configureGooglePageSpeedURL:
							'a valid configureGooglePageSpeedURL',
					},
					error: {
						code: PAGE_SPEED_API_KEY_ERROR_CODE,
						status: PAGE_SPEED_API_KEY_INVALID_STATUS,
					},
				})
			);

			expect(getByText(pageURLs[0].languageId)).toBeInTheDocument();
			expect(getByText(pageURLs[0].title)).toBeInTheDocument();
			expect(getByText(pageURLs[0].url)).toBeInTheDocument();
		});
	});

	describe('Server error, no internet connection o local/private pages cases', () => {
		it('Renders warning alert component when a server error code is received with show error details button', () => {
			const {getByRole, getByText, getByTitle} = render(
				getErrorAlertComponent({
					data: {
						configureGooglePageSpeedURL:
							'a valid configureGooglePageSpeedURL',
						privateLayout: false,
						validConnection: true,
					},
					error: {
						code: PAGE_SPEED_SERVER_ERROR_CODE,
					},
				})
			);

			expect(
				getByText('this-page-cannot-be-audited')
			).toBeInTheDocument();

			expect(
				getByText(
					'private-local-or-intranet-pages-cannot-be-audited-as-they-are-not-accessible-from-the-internet'
				)
			).toBeInTheDocument();

			expect(getByText('show-details')).toBeInTheDocument();

			expect(getParentAlert(getByRole('alert')).className).toContain(
				'warning'
			);
		});

		it('Renders warning alert component when a private page is audited, without show-details button', () => {
			const {getByRole, getByText, queryByText} = render(
				getErrorAlertComponent({
					data: {
						configureGooglePageSpeedURL:
							'a valid configureGooglePageSpeedURL',
						privateLayout: true,
						validConnection: true,
					},
					error: null,
				})
			);

			expect(
				getByText('this-page-cannot-be-audited')
			).toBeInTheDocument();
			expect(
				getByText(
					'private-local-or-intranet-pages-cannot-be-audited-as-they-are-not-accessible-from-the-internet'
				)
			).toBeInTheDocument();
			expect(queryByText('show-details')).not.toBeInTheDocument();

			expect(getParentAlert(getByRole('alert')).className).toContain(
				'warning'
			);
		});

		it('Renders warning alert component with page language selector', () => {
			const {getByText} = render(
				getErrorAlertComponent({
					data: {
						configureGooglePageSpeedURL:
							'a valid configureGooglePageSpeedURL',
						privateLayout: false,
						validConnection: true,
					},
					error: {
						code: PAGE_SPEED_SERVER_ERROR_CODE,
					},
				})
			);

			expect(getByText(pageURLs[0].languageId)).toBeInTheDocument();
			expect(getByText(pageURLs[0].title)).toBeInTheDocument();
			expect(getByText(pageURLs[0].url)).toBeInTheDocument();
		});
	});
});
