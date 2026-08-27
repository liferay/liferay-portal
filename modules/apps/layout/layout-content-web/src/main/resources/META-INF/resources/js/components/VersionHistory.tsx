/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {
	hideProductMenuIfPresent,
	openConfirmModal,
	useMediaQuery,
} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {Config, initializeConfig} from '../config';
import PageVersionService from '../services/PageVersionService';
import {PageVersion} from '../types/PageVersion';
import {getVersionData} from '../utils/getVersionData';
import PagePreview from './PagePreview';
import ResponsivePanel from './ResponsivePanel';
import Toolbar from './Toolbar';
import VersionList from './VersionList';

import '../../css/VersionHistory.scss';

const CURRENT_KEY = 'current';

const LARGE_MEDIA_QUERY = '(min-width: 992px)';

interface Props {
	config: Config;
}

export default function VersionHistory({config}: Props) {
	initializeConfig(config);

	const [isPanelOpen, setIsPanelOpen] = useState(false);
	const [search, setSearch] = useState('');
	const [selectedKey, setSelectedKey] = useState<string>();

	const [versions, setVersions] = useState<PageVersion[] | null>(null);

	const [currentExperienceERC, setCurrentExperienceERC] = useState(
		config.availableSegmentsExperiences[0]?.segmentsExperienceERC
	);
	const [currentLanguageId, setCurrentLanguageId] = useState(
		config.defaultLanguageId
	);

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

	const handleDelete = async (version: PageVersion) => {
		if (!version.actions?.delete) {
			return;
		}

		const confirmed = await openConfirmModal({
			buttonLabel: Liferay.Language.get('delete'),
			center: true,
			status: 'danger',
			text: Liferay.Language.get('delete-page-version-confirmation'),
			title: Liferay.Language.get('delete-version'),
		});

		if (!confirmed) {
			return;
		}

		const {error} = await PageVersionService.deletePageVersion(
			version.actions.delete.href
		);

		if (error) {
			openToast({message: error, type: 'danger'});

			return;
		}

		openToast({
			message: sub(Liferay.Language.get('x-was-deleted-successfully'), [
				version.name,
			]),
			type: 'success',
		});

		const nextVersions = versions?.filter(
			({externalReferenceCode}) =>
				externalReferenceCode !== version.externalReferenceCode
		);

		setVersions(nextVersions ?? null);

		if (selectedKey === version.externalReferenceCode) {
			setSelectedKey(CURRENT_KEY);
		}
	};

	const handleRestore = async (version: PageVersion) => {
		if (!version.actions?.restore) {
			return;
		}

		if (config.layout.status === 'draft') {
			const confirmed = await openConfirmModal({
				buttonLabel: Liferay.Language.get('restore'),
				center: true,
				status: 'warning',
				text: Liferay.Language.get(
					'you-are-about-to-restore-an-older-version-of-the-page.-all-your-unsaved-changes-will-be-lost'
				),
				title: Liferay.Language.get('restore-version'),
			});

			if (!confirmed) {
				return;
			}
		}

		const {error} = await PageVersionService.restorePageVersion(
			version.actions.restore.href
		);

		if (error) {
			openToast({message: error, type: 'danger'});

			return;
		}

		window.location.reload();
	};

	const selectedVersion = versions?.find(
		({externalReferenceCode}) => externalReferenceCode === selectedKey
	);

	const {experiences, languages, selectedExperience, selectedLanguageId} =
		getVersionData({
			currentExperienceERC,
			currentLanguageId,
			version: selectedVersion,
		});

	const keywords = search.trim().toLowerCase();

	const matches = (...names: Array<string | undefined>) =>
		names.some((name) => name?.toLowerCase().includes(keywords));

	const items = versions && [
		...(matches(config.layout.name)
			? [{key: CURRENT_KEY, ...config.layout}]
			: []),
		...versions
			.filter(({creator, name, version}) =>
				matches(name, creator?.name, String(version))
			)
			.map((version) => ({
				key: version.externalReferenceCode,
				name: version.name,
				status: version.status,
				version,
			})),
	];

	return (
		<>
			<Toolbar
				availableLanguages={languages}
				experiences={experiences}
				isSidePanelOpen={isPanelOpen || isScreenLarge}
				onChangeExperience={setCurrentExperienceERC}
				onChangeLanguage={setCurrentLanguageId}
				openSidePanel={() => setIsPanelOpen(true)}
				selectedExperience={selectedExperience}
				selectedLanguageId={selectedLanguageId}
			/>

			<ResponsivePanel
				onOpenChange={setIsPanelOpen}
				onSearch={setSearch}
				open={isPanelOpen || isScreenLarge}
			>
				{items ? (
					<VersionList
						items={items}
						onDelete={handleDelete}
						onRestore={handleRestore}
						onSelect={setSelectedKey}
						searching={Boolean(keywords)}
						selectedKey={selectedKey}
					/>
				) : (
					<ClayLoadingIndicator
						displayType="secondary"
						size="sm"
						title={Liferay.Language.get('loading')}
					/>
				)}
			</ResponsivePanel>

			<PagePreview
				experienceERC={selectedExperience?.segmentsExperienceERC}
				experienceId={selectedExperience?.segmentsExperienceId}
				languageId={selectedLanguageId}
				versionERC={selectedVersion?.externalReferenceCode}
			/>
		</>
	);
}
