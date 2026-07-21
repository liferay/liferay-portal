/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {openConfirmModal, openToast} from 'frontend-js-components-web';
import React from 'react';

import HealthScanConfiguration from '../../../js/health_scan/components/HealthScanConfiguration';

jest.mock('frontend-js-components-web', () => ({
	openConfirmModal: jest.fn(),
	openToast: jest.fn(),
}));

function getSaveAndScanNowButton() {
	return screen.getByRole('button', {name: 'save-and-scan-now'});
}

function getSaveButton() {
	return screen.getByRole('button', {name: 'save'});
}

function toggleCrawler() {
	fireEvent.click(screen.getByLabelText('crawler-insights'));
}

function enableAutoScan() {
	fireEvent.click(screen.getByLabelText(/auto-scan/, {selector: 'input'}));
}

const TIME_ZONES = [
	{label: '(UTC) Greenwich Mean Time', value: 'UTC'},
	{label: '(UTC -05:00) Eastern Standard Time', value: 'America/New_York'},
];

type ConfigurationProps = React.ComponentProps<typeof HealthScanConfiguration>;

function renderConfiguration(props: Partial<ConfigurationProps> = {}) {
	return render(
		<HealthScanConfiguration
			defaultTimeZoneId="UTC"
			domainId={1}
			scanConfig={null}
			schedule={null}
			timeZones={TIME_ZONES}
			{...props}
		/>
	);
}

describe('HealthScanConfiguration', () => {
	beforeEach(() => {
		(openConfirmModal as jest.Mock).mockClear();
		(openToast as jest.Mock).mockClear();

		(Liferay.Util as unknown) = {
			fetch: jest.fn().mockResolvedValue({ok: true}),
		};
	});

	describe('schedule section', () => {
		it('hides the schedule fields when Auto Scan is off by default', () => {
			renderConfiguration();

			expect(
				screen.queryByLabelText('frequency')
			).not.toBeInTheDocument();
			expect(screen.queryByLabelText('time')).not.toBeInTheDocument();
			expect(
				screen.queryByLabelText('time-zone')
			).not.toBeInTheDocument();
		});

		it('shows the schedule fields when Auto Scan is turned on', () => {
			renderConfiguration();

			enableAutoScan();

			expect(screen.getByLabelText('frequency')).toBeInTheDocument();
			expect(screen.getByLabelText('time')).toHaveValue('00:00');
			expect(screen.getByLabelText('time-zone')).toBeInTheDocument();
		});

		it('populates the time zone select with the provided time zones', () => {
			renderConfiguration();

			enableAutoScan();

			expect(
				screen.getByText('(UTC -05:00) Eastern Standard Time')
			).toBeInTheDocument();
		});

		it('reveals the day-of-week selector only for Weekly', () => {
			renderConfiguration();

			enableAutoScan();

			expect(
				screen.queryByLabelText('day-of-week')
			).not.toBeInTheDocument();

			fireEvent.change(screen.getByLabelText('frequency'), {
				target: {value: 'weekly'},
			});

			expect(screen.getByLabelText('day-of-week')).toBeInTheDocument();
			expect(
				screen.queryByLabelText('day-of-month')
			).not.toBeInTheDocument();
		});

		it('reveals the day-of-month selector only for Monthly', () => {
			renderConfiguration();

			enableAutoScan();

			fireEvent.change(screen.getByLabelText('frequency'), {
				target: {value: 'monthly'},
			});

			expect(screen.getByLabelText('day-of-month')).toBeInTheDocument();
			expect(
				screen.queryByLabelText('day-of-week')
			).not.toBeInTheDocument();
		});
	});

	describe('scope section', () => {
		it('renders a toggle for every engine, disabled by default', () => {
			renderConfiguration();

			expect(screen.getByLabelText('crawler-insights')).not.toBeChecked();
			expect(screen.queryAllByLabelText('scope')).toHaveLength(0);
		});

		it("shows an engine's fields when its toggle is on", () => {
			renderConfiguration();

			toggleCrawler();

			expect(screen.getAllByLabelText('scope')).toHaveLength(1);
		});

		it('reveals the Included Path input only for "Included Paths Only"', () => {
			renderConfiguration();

			toggleCrawler();

			expect(
				screen.queryByLabelText(/included-path/, {selector: 'input'})
			).not.toBeInTheDocument();

			fireEvent.change(screen.getAllByLabelText('scope')[0], {
				target: {value: 'includedPathsOnly'},
			});

			expect(
				screen.getByLabelText(/included-path/, {selector: 'input'})
			).toBeInTheDocument();
		});

		it('shows no path input for "Public Sitemap Pages"', () => {
			renderConfiguration();

			toggleCrawler();

			fireEvent.change(screen.getAllByLabelText('scope')[0], {
				target: {value: 'publicSitemapPages'},
			});

			expect(
				screen.queryByLabelText(/included-path/, {selector: 'input'})
			).not.toBeInTheDocument();
			expect(
				screen.queryByLabelText(/excluded-path/, {selector: 'input'})
			).not.toBeInTheDocument();
			expect(getSaveButton()).toBeEnabled();
		});
	});

	describe('crawler configuration', () => {
		it('shows the Max Crawl Depth and Max Duration selects for the crawler engine', () => {
			renderConfiguration();

			toggleCrawler();

			expect(screen.getByLabelText('max-crawl-depth')).toHaveValue('2');
			expect(screen.getByLabelText('max-duration')).toHaveValue('86400');
		});

		it('hides the Max Crawl Depth and Max Duration selects for other engines', () => {
			renderConfiguration();

			fireEvent.click(screen.getByLabelText('ai-generated-insights'));

			expect(
				screen.queryByLabelText('max-crawl-depth')
			).not.toBeInTheDocument();
			expect(
				screen.queryByLabelText('max-duration')
			).not.toBeInTheDocument();
		});

		it('persists the crawler depth and duration on Save', async () => {
			const fetchMock = Liferay.Util.fetch as jest.Mock;

			renderConfiguration({domainId: 42});

			toggleCrawler();

			fireEvent.change(screen.getByLabelText('max-crawl-depth'), {
				target: {value: '8'},
			});
			fireEvent.change(screen.getByLabelText('max-duration'), {
				target: {value: '3600'},
			});
			fireEvent.click(getSaveButton());

			await waitFor(() => expect(fetchMock).toHaveBeenCalledTimes(1));

			const scanConfig = JSON.parse(
				JSON.parse(fetchMock.mock.calls[0][1].body).scanConfig
			);

			expect(scanConfig.engines.crawler.maxCrawlDepth).toBe(8);
			expect(scanConfig.engines.crawler.maxDuration).toBe(3600);
		});
	});

	describe('path validation', () => {
		function selectIncludedPathsOnly() {
			renderConfiguration();

			toggleCrawler();

			fireEvent.change(screen.getAllByLabelText('scope')[0], {
				target: {value: 'includedPathsOnly'},
			});

			return screen.getByLabelText(/included-path/, {selector: 'input'});
		}

		it('shows an inline error and disables Save for an invalid path', () => {
			const input = selectIncludedPathsOnly();

			fireEvent.change(input, {target: {value: 'blog/*'}});

			expect(screen.getByText('invalid-path-format')).toBeInTheDocument();
			expect(getSaveButton()).toBeDisabled();
		});

		it('clears the error and enables Save for a valid path', () => {
			const input = selectIncludedPathsOnly();

			fireEvent.change(input, {target: {value: 'blog/*'}});
			fireEvent.change(input, {target: {value: '/blog/*'}});

			expect(
				screen.queryByText('invalid-path-format')
			).not.toBeInTheDocument();
			expect(getSaveButton()).toBeEnabled();
		});

		it('shows the required message and disables Save while Included Paths is empty', () => {
			const input = selectIncludedPathsOnly();

			expect(input).toHaveValue('');
			expect(
				screen.getByText('this-field-is-required')
			).toBeInTheDocument();
			expect(
				screen.queryByText('invalid-path-format')
			).not.toBeInTheDocument();
			expect(getSaveButton()).toBeDisabled();
		});

		it('requires an Excluded Path and disables Save while it is empty', () => {
			renderConfiguration();

			toggleCrawler();

			fireEvent.change(screen.getAllByLabelText('scope')[0], {
				target: {value: 'excludedPathsOnly'},
			});

			const input = screen.getByLabelText(/excluded-path/, {
				selector: 'input',
			});

			expect(input).toHaveValue('');
			expect(getSaveButton()).toBeDisabled();

			fireEvent.change(input, {target: {value: '/private/*'}});

			expect(getSaveButton()).toBeEnabled();
		});
	});

	describe('save and scan now', () => {
		it('keeps both buttons enabled when no change has been made', () => {
			renderConfiguration();

			expect(getSaveAndScanNowButton()).toBeEnabled();
			expect(getSaveButton()).toBeEnabled();
		});

		it('saves and then creates scans once the action is confirmed', async () => {
			const fetchMock = Liferay.Util.fetch as jest.Mock;

			(openConfirmModal as jest.Mock).mockImplementation(({onConfirm}) =>
				onConfirm(true)
			);

			renderConfiguration({domainId: 42});

			fireEvent.click(getSaveAndScanNowButton());

			await waitFor(() => expect(fetchMock).toHaveBeenCalledTimes(2));

			expect(fetchMock.mock.calls[0][0]).toBe('/o/seo-studio/domains/42');
			expect(fetchMock.mock.calls[0][1].method).toBe('PATCH');
			expect(fetchMock.mock.calls[1][0]).toBe(
				'/o/seo-studio/domains/42/object-actions/createScans'
			);
			expect(fetchMock.mock.calls[1][1].method).toBe('PUT');
		});

		it('shows an error toast when the scan request fails', async () => {
			const fetchMock = Liferay.Util.fetch as jest.Mock;

			fetchMock
				.mockResolvedValueOnce({ok: true})
				.mockResolvedValueOnce({ok: false});

			(openConfirmModal as jest.Mock).mockImplementation(({onConfirm}) =>
				onConfirm(true)
			);

			renderConfiguration({domainId: 42});

			fireEvent.click(getSaveAndScanNowButton());

			await waitFor(() =>
				expect(openToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'danger'})
				)
			);
		});

		it('does nothing when the scan confirmation is dismissed', async () => {
			const fetchMock = Liferay.Util.fetch as jest.Mock;

			(openConfirmModal as jest.Mock).mockImplementation(({onConfirm}) =>
				onConfirm(false)
			);

			renderConfiguration({domainId: 42});

			fireEvent.click(getSaveAndScanNowButton());

			await waitFor(() => expect(openConfirmModal).toHaveBeenCalled());

			expect(fetchMock).not.toHaveBeenCalled();
		});
	});

	describe('persistence', () => {
		it('disables Save when no domain is available', () => {
			renderConfiguration({domainId: null});

			expect(getSaveButton()).toBeDisabled();
		});

		it('persists the configuration to the domain on Save', async () => {
			const fetchMock = Liferay.Util.fetch as jest.Mock;

			renderConfiguration({domainId: 42});

			fireEvent.click(getSaveButton());

			await waitFor(() => expect(fetchMock).toHaveBeenCalledTimes(1));

			const [url, init] = fetchMock.mock.calls[0];

			expect(url).toBe('/o/seo-studio/domains/42');
			expect(init.method).toBe('PATCH');

			const body = JSON.parse(init.body);

			expect(body.autoScanEnabled).toBe(false);
			expect(body.scanFrequency).toBe('daily');
			expect(body.scanTimeZone).toBe('UTC');

			const scanConfig = JSON.parse(body.scanConfig);

			expect(scanConfig.engines.crawler.enabled).toBe(false);

			await waitFor(() =>
				expect(openToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'success'})
				)
			);
		});

		it('shows an error toast when the save request fails', async () => {
			(Liferay.Util.fetch as jest.Mock).mockResolvedValue({ok: false});

			renderConfiguration();

			fireEvent.click(getSaveButton());

			await waitFor(() =>
				expect(openToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'danger'})
				)
			);
		});

		it('seeds the form from the persisted configuration', () => {
			renderConfiguration({
				scanConfig: JSON.stringify({
					engines: {
						crawler: {
							enabled: false,
							excludedPaths: '',
							includedPaths: '',
							maxPagesPerScan: 100,
							rankingMethod: 'topByPageVisit',
							scope: 'allPublishedPages',
						},
					},
				}),
				schedule: {
					autoScanEnabled: true,
					scanDayOfWeek: 'WE',
					scanFrequency: 'weekly',
				},
			});

			expect(screen.getByLabelText('frequency')).toHaveValue('weekly');
			expect(screen.getByLabelText('day-of-week')).toHaveValue('WE');
			expect(
				screen.queryByLabelText('crawler-insights')
			).not.toBeChecked();
		});

		it('keeps the schedule defaults when persisted fields are empty', () => {
			renderConfiguration({
				schedule: {
					autoScanEnabled: true,
					scanDayOfMonth: 0,
					scanDayOfWeek: '',
					scanFrequency: '',
					scanTime: '',
					scanTimeZone: '',
				} as unknown as ConfigurationProps['schedule'],
			});

			expect(screen.getByLabelText('frequency')).toHaveValue('daily');
			expect(screen.getByLabelText('time')).toHaveValue('00:00');
			expect(screen.getByLabelText('time-zone')).toHaveValue('UTC');
		});

		it('persists midnight when Time is cleared', async () => {
			const fetchMock = Liferay.Util.fetch as jest.Mock;

			renderConfiguration({domainId: 42});

			enableAutoScan();

			fireEvent.change(screen.getByLabelText('time'), {
				target: {value: ''},
			});
			fireEvent.click(getSaveButton());

			await waitFor(() => expect(fetchMock).toHaveBeenCalledTimes(1));

			const body = JSON.parse(fetchMock.mock.calls[0][1].body);

			expect(body.scanTime).toBe('00:00');
		});

		it('merges a partial persisted engine over its defaults', () => {
			renderConfiguration({
				scanConfig: JSON.stringify({
					engines: {
						crawler: {
							enabled: true,
						},
					},
				}),
			});

			expect(screen.getByLabelText('crawler-insights')).toBeChecked();
			expect(
				screen.getByLabelText('ai-generated-insights')
			).not.toBeChecked();
			expect(screen.getAllByLabelText('scope')).toHaveLength(1);
			expect(screen.getAllByLabelText('scope')[0]).toHaveValue(
				'allPublishedPages'
			);
		});
	});

	describe('collapsible sections', () => {
		it('toggles a section open and closed from its header', () => {
			renderConfiguration();

			const header = screen.getByRole('button', {name: 'schedule[noun]'});

			expect(header).toHaveAttribute('aria-expanded', 'true');

			fireEvent.click(header);

			expect(header).toHaveAttribute('aria-expanded', 'false');
		});
	});
});
