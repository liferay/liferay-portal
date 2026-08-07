/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {
	hideProductMenuIfPresent,
	useMediaQuery,
} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import {Config, initializeConfig} from '../config';
import PageVersionService from '../services/PageVersionService';
import {PageVersion} from '../types/PageVersion';
import ResponsivePanel from './ResponsivePanel';
import Toolbar from './Toolbar';
import VersionList from './VersionList';

import '../../css/VersionHistory.scss';

const LARGE_MEDIA_QUERY = '(min-width: 992px)';

interface Props {
	config: Config;
}

export default function VersionHistory({config}: Props) {
	initializeConfig(config);

	const [isPanelOpen, setIsPanelOpen] = useState(false);
	const [search, setSearch] = useState('');

	const [versions, setVersions] = useState<PageVersion[] | null>(null);

	const isScreenLarge = useMediaQuery(LARGE_MEDIA_QUERY);

	useEffect(() => {
		hideProductMenuIfPresent({onHide: () => setIsPanelOpen(true)});
	}, []);

	useEffect(() => {
		const controller = new AbortController();

		const loadVersions = async () => {
			const {data, error} = await PageVersionService.getPageVersions(
				controller.signal
			);

			if (controller.signal.aborted) {
				return;
			}

			if (error) {
				openToast({message: error, type: 'danger'});
			}

			setVersions(data?.items ?? []);
		};

		loadVersions();

		return () => controller.abort();
	}, []);

	const keywords = search.trim().toLowerCase();

	const matches = (...names: Array<string | undefined>) =>
		names.some((name) => name?.toLowerCase().includes(keywords));

	return (
		<>
			<Toolbar
				isSidePanelOpen={isPanelOpen || isScreenLarge}
				openSidePanel={() => setIsPanelOpen(true)}
			/>

			<ResponsivePanel
				onOpenChange={setIsPanelOpen}
				onSearch={setSearch}
				open={isPanelOpen || isScreenLarge}
			>
				{versions ? (
					<VersionList
						layout={
							matches(config.layout.name)
								? config.layout
								: undefined
						}
						searching={Boolean(keywords)}
						versions={versions.filter(({creator, name}) =>
							matches(name, creator?.name)
						)}
					/>
				) : (
					<ClayLoadingIndicator
						displayType="secondary"
						size="sm"
						title={Liferay.Language.get('loading')}
					/>
				)}
			</ResponsivePanel>
		</>
	);
}
