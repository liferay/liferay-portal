/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {LanguagePicker, Option, Picker} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import ClayPopover from '@clayui/popover';
import {dateUtils, sub} from 'frontend-js-web';
import React, {Key, useEffect, useMemo, useRef, useState} from 'react';

import '../../../css/components/CompareVersionsModal.scss';
import StatusLabel from '../../common/components/StatusLabel';
import {IAssetObjectEntry} from '../../common/types/AssetType';
import {getImage} from '../../common/utils/getImage';
import VersionService from '../info_panel/services/VersionService';
import {VIEW_CONTENT_VERSION_URL} from '../info_panel/util/constants';
import {
	DiffType,
	Diffs,
	injectContentDiffs,
	useVersionDiffs,
} from './useVersionDiffs';

interface CompareVersionsModalContentProps {
	apiURL: string;
	availableLanguageIds: string[];
	closeModal: () => void;
	defaultLanguageId: string;
	initialVersion: number;
	objectEntryId: number;
}

type VersionItem = IAssetObjectEntry;

type VersionsState =
	| {status: 'error' | 'loading'}
	| {items: VersionItem[]; status: 'loaded'};

function getVersionLabel(version: number) {
	return sub(Liferay.Language.get('version-x'), [version]);
}

function getIframeLiferay(iframe: HTMLIFrameElement | null) {
	const contentWindow = iframe?.contentWindow as
		| (Window & {Liferay: typeof Liferay})
		| null
		| undefined;

	return contentWindow?.Liferay;
}

function getVersionItem(items: VersionItem[], version: number | null) {
	return items.find((item) => getVersionNumber(item) === version);
}

function getVersionNumber(item: VersionItem) {
	return item.systemProperties.version.number;
}

export default function CompareVersionsModalContent({
	apiURL,
	availableLanguageIds,
	closeModal,
	defaultLanguageId,
	initialVersion,
	objectEntryId,
}: CompareVersionsModalContentProps) {
	const [languageId, setLanguageId] = useState<string>(() => {
		const currentLanguageId = Liferay.ThemeDisplay.getLanguageId();

		return availableLanguageIds.includes(currentLanguageId)
			? currentLanguageId
			: defaultLanguageId;
	});
	const [sourceVersion, setSourceVersion] = useState<number | null>(null);
	const [targetVersion, setTargetVersion] = useState<number | null>(null);
	const [versionsState, setVersionsState] = useState<VersionsState>({
		status: 'loading',
	});

	const locales = useMemo(
		() =>
			availableLanguageIds.map((availableLanguageId) => {
				const label = availableLanguageId.replace('_', '-');

				return {
					id: availableLanguageId as Liferay.Language.Locale,
					label,
					symbol: label.toLowerCase(),
				};
			}),
		[availableLanguageIds]
	);

	useEffect(() => {
		let stale = false;

		const getVersions = async () => {
			const {data, error} = await VersionService.getObjectEntryVersions(
				apiURL,
				{page: 1, pageSize: -1, sort: 'version:desc'}
			);

			if (stale) {
				return;
			}

			if (error !== null) {
				setVersionsState({status: 'error'});

				return;
			}

			const items: VersionItem[] = data.items;

			setSourceVersion(initialVersion);
			setVersionsState({items, status: 'loaded'});
		};

		getVersions();

		return () => {
			stale = true;
		};
	}, [apiURL, initialVersion]);

	const diffs = useVersionDiffs({
		languageId,
		objectEntryId,
		sourceVersion,
		targetVersion,
	});

	return (
		<>
			<ClayModal.Header withTitle={false}>
				<ClayModal.TitleSection>
					<ClayModal.Title>
						{Liferay.Language.get('compare-versions')}
					</ClayModal.Title>
				</ClayModal.TitleSection>

				<LanguagePicker
					classNamesTrigger="ml-auto mr-3"
					locales={locales}
					onSelectedLocaleChange={(id: Key) =>
						setLanguageId(id as string)
					}
					selectedLocaleId={languageId}
					small
				/>

				<DiffKeyPopover />

				<ClayButton
					aria-label={Liferay.Language.get('close')}
					className="close"
					displayType="unstyled"
					onClick={closeModal}
				>
					<ClayIcon symbol="times" />
				</ClayButton>
			</ClayModal.Header>

			<ClayModal.Body className="d-flex flex-column p-0">
				{versionsState.status === 'loading' ? (
					<ClayLoadingIndicator className="my-5" />
				) : null}

				{versionsState.status === 'error' ? (
					<ClayAlert className="m-3" displayType="danger">
						{Liferay.Language.get('an-unexpected-error-occurred')}
					</ClayAlert>
				) : null}

				{versionsState.status === 'loaded' ? (
					<div className="cms-compare-versions-panes d-flex flex-column flex-grow-1 flex-md-row">
						<CompareVersionPane
							diffType="removals"
							diffs={diffs?.source ?? null}
							excludedVersion={targetVersion}
							languageId={languageId}
							objectEntryId={objectEntryId}
							onVersionChange={setSourceVersion}
							selectedVersion={sourceVersion}
							versions={versionsState.items}
						/>

						<CompareVersionPane
							diffType="additions"
							diffs={diffs?.target ?? null}
							excludedVersion={sourceVersion}
							languageId={languageId}
							objectEntryId={objectEntryId}
							onVersionChange={setTargetVersion}
							selectedVersion={targetVersion}
							versions={versionsState.items}
						/>
					</div>
				) : null}
			</ClayModal.Body>
		</>
	);
}

function DiffKeyPopover() {
	const [show, setShow] = useState(false);

	return (
		<ClayPopover
			alignPosition="bottom-right"
			closeOnClickOutside
			disableScroll
			header={Liferay.Language.get('key')}
			onShowChange={setShow}
			show={show}
			trigger={
				<ClayButtonWithIcon
					aria-expanded={show}
					className="mr-3 text-secondary"
					displayType="unstyled"
					size="sm"
					symbol="question-circle-full"
					title={Liferay.Language.get('compare-versions-key-help')}
				/>
			}
		>
			<div className="c-gap-2 d-flex flex-wrap">
				<span className="cms-compare-versions-key-added px-2">
					{Liferay.Language.get('added')}
				</span>

				<span className="cms-compare-versions-key-deleted px-2">
					{Liferay.Language.get('deleted')}
				</span>

				<span className="cms-compare-versions-key-format-changes px-2">
					{Liferay.Language.get('format-changes')}
				</span>
			</div>
		</ClayPopover>
	);
}

function CompareVersionPane({
	diffType,
	diffs,
	excludedVersion,
	languageId,
	objectEntryId,
	onVersionChange,
	selectedVersion,
	versions,
}: {
	diffType: DiffType;
	diffs: Diffs | null;
	excludedVersion: number | null;
	languageId: string;
	objectEntryId: number;
	onVersionChange: (version: number) => void;
	selectedVersion: number | null;
	versions: VersionItem[];
}) {
	const iframeRef = useRef<HTMLIFrameElement>(null);

	const [iframeStatus, setIframeStatus] = useState<'loaded' | 'loading'>(
		'loading'
	);

	useEffect(() => {
		if (iframeStatus === 'loaded' && iframeRef.current) {
			injectContentDiffs(diffs, diffType, iframeRef.current);
		}
	}, [diffs, diffType, iframeStatus]);

	useEffect(() => {
		if (iframeStatus !== 'loaded') {
			return;
		}

		const iframeLiferay = getIframeLiferay(iframeRef.current);

		iframeLiferay?.fire('localizationSelect:localeChanged', {languageId});
	}, [iframeStatus, languageId]);

	if (selectedVersion === null) {
		const emptyStateImage = getImage('compare_versions_empty_state.svg');

		return (
			<div className="cms-compare-versions-pane d-flex flex-column">
				<ClayEmptyState
					className="justify-content-center"
					description={Liferay.Language.get(
						'choose-a-target-version-to-start-the-comparison'
					)}
					imgSrc={emptyStateImage}
					imgSrcReducedMotion={emptyStateImage}
					small
					title={Liferay.Language.get(
						'select-a-version-for-comparison'
					)}
				>
					<VersionPicker
						excludedVersion={excludedVersion}
						onVersionChange={onVersionChange}
						selectedVersion={selectedVersion}
						versions={versions}
					/>
				</ClayEmptyState>
			</div>
		);
	}

	const selectedItem = getVersionItem(versions, selectedVersion);

	return (
		<div className="cms-compare-versions-pane d-flex flex-column">
			<div className="align-items-center c-gap-3 d-flex p-3">
				<VersionPicker
					excludedVersion={excludedVersion}
					onVersionChange={(version) => {
						setIframeStatus('loading');
						onVersionChange(version);
					}}
					selectedVersion={selectedVersion}
					versions={versions}
				/>

				{selectedItem ? (
					<>
						<span className="ml-auto text-3 text-secondary text-truncate">
							{sub(Liferay.Language.get('modified-by-x'), [
								selectedItem.creator.name,
							])}

							{` ${dateUtils.format(
								new Date(selectedItem.dateModified),
								'P p'
							)}`}
						</span>

						<StatusLabel label={selectedItem.status.label} />
					</>
				) : null}
			</div>

			<div className="cms-compare-versions-pane-content d-flex flex-column flex-grow-1 mx-2">
				{iframeStatus === 'loading' ? (
					<ClayLoadingIndicator className="my-5" />
				) : null}

				<iframe
					className="border-0 flex-grow-1 w-100"
					onLoad={() => setIframeStatus('loaded')}
					ref={iframeRef}
					src={`${VIEW_CONTENT_VERSION_URL}/compare_content_item?objectEntryId=${objectEntryId}&p_p_state=pop_up&version=${selectedVersion}`}
					title={getVersionLabel(selectedVersion)}
				/>
			</div>
		</div>
	);
}

const VersionPickerTrigger = React.forwardRef<HTMLButtonElement, any>(
	({children, ...otherProps}, ref) => (
		<button
			{...otherProps}
			className="form-control form-control-select form-control-select-secondary form-control-sm w-auto"
			ref={ref}
			type="button"
		>
			{children}
		</button>
	)
);

function VersionPicker({
	excludedVersion,
	onVersionChange,
	selectedVersion,
	versions,
}: {
	excludedVersion: number | null;
	onVersionChange: (version: number) => void;
	selectedVersion: number | null;
	versions: VersionItem[];
}) {
	const items = versions
		.filter((item) => getVersionNumber(item) !== excludedVersion)
		.map((item) => ({
			label: getVersionLabel(getVersionNumber(item)),
			value: String(getVersionNumber(item)),
		}));

	return (
		<Picker
			aria-label={
				selectedVersion === null
					? Liferay.Language.get('select-a-version-for-comparison')
					: sub(
							Liferay.Language.get(
								'select-a-version.-current-version-x'
							),
							selectedVersion
						)
			}
			as={VersionPickerTrigger}
			items={items}
			onSelectionChange={(key) => onVersionChange(Number(key))}
			placeholder={`--${Liferay.Language.get('not-selected')}--`}
			selectedKey={
				selectedVersion === null ? undefined : String(selectedVersion)
			}
		>
			{(item: {label: string; value: string}) => (
				<Option key={item.value}>{item.label}</Option>
			)}
		</Picker>
	);
}
