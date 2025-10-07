/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayCard from '@clayui/card';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import {openModal} from 'frontend-js-components-web';
import {createPortletURL, fetch} from 'frontend-js-web';
import {
	KeyboardEvent,
	MouseEvent,
	default as React,
	useEffect,
	useRef,
	useState,
} from 'react';

interface IProps {
	addLayoutURL: string;
	getLayoutPageTemplateEntryListURL: string;
	layoutPageTemplateEntryId: string;
	modalTitle?: string;
	portletNamespace: string;
	subtitle: string;
	thumbnailURL: string;
	title: string;
}

export default function LayoutPageTemplateEntryCard({
	addLayoutURL,
	getLayoutPageTemplateEntryListURL,
	layoutPageTemplateEntryId,
	modalTitle,
	subtitle,
	thumbnailURL,
	title,
}: IProps) {
	const {
		observer: previewObserver,
		onOpenChange: onPreviewOpenChange,
		open: previewOpen,
	} = useModal();

	const onClick = () => {
		openModal({
			disableAutoClose: true,
			height: '60vh',
			id: 'addLayoutDialog',
			size: 'md',
			title: modalTitle,
			url: addLayoutURL,
		});
	};

	const onKeyDown = (event: KeyboardEvent) => {
		if (event.key === 'Enter' || event.key === 'Space') {
			event.preventDefault();
			event.stopPropagation();
			onClick();
		}
	};

	const onPreviewClick = (event: MouseEvent) => {
		event.stopPropagation();
		onPreviewOpenChange(true);
	};

	const [entryIndex, setEntryIndex] = useState(0);
	const [layoutPageTemplateEntryList, setLayoutPageTemplateEntryList] =
		useState<LayoutPageTemplateEntryList | null>(null);

	const updateEntryIndex = (direction: 'previous' | 'next') => {
		setEntryIndex((previousIndex) => {
			if (!layoutPageTemplateEntryList) {
				return previousIndex;
			}

			if (direction === 'previous') {
				return previousIndex === 0
					? layoutPageTemplateEntryList.length - 1
					: previousIndex - 1;
			}

			return (previousIndex + 1) % layoutPageTemplateEntryList.length;
		});
	};

	return (
		<>
			<div
				className="btn c-p-0 card card-type-asset file-card position-relative"
				onClick={onClick}
				onKeyDown={onKeyDown}
				role="option"
				tabIndex={0}
			>
				<ClayCard.AspectRatio containerAspectRatio="16/9">
					{thumbnailURL ? (
						<img
							alt="thumbnail"
							className="aspect-ratio-item-center-middle aspect-ratio-item-fluid"
							src={thumbnailURL}
						/>
					) : (
						<div className="aspect-ratio-item aspect-ratio-item-center-middle card-type-asset-icon">
							<ClayIcon symbol="page" />
						</div>
					)}
				</ClayCard.AspectRatio>

				<ClayCard.Body className="text-left">
					<ClayCard.Row className="c-gap-2">
						<div className="autofit-col autofit-col-expand autofit-col-shrink">
							<ClayCard.Description
								className="c-mb-0"
								displayType="title"
							>
								{title}
							</ClayCard.Description>

							<ClayCard.Description displayType="subtitle">
								{subtitle}
							</ClayCard.Description>
						</div>

						<div className="autofit-col">
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'preview-page-template'
								)}
								borderless
								displayType="secondary"
								onClick={onPreviewClick}
								onKeyDown={(event) => {
									if (
										event.key === 'Enter' ||
										event.key === 'Space'
									) {
										event.stopPropagation();
										onPreviewOpenChange(true);
									}
								}}
								size="sm"
								symbol="view"
								title={Liferay.Language.get(
									'preview-page-template'
								)}
							/>
						</div>
					</ClayCard.Row>
				</ClayCard.Body>
			</div>

			{previewOpen && (
				<ClayModal
					observer={previewObserver}
					onKeyDown={(event) => {
						if (event.key === 'ArrowLeft') {
							updateEntryIndex('previous');
						}
						else if (event.key === 'ArrowRight') {
							updateEntryIndex('next');
						}
					}}
					size="full-screen"
				>
					<ClayModal.Header>
						{Liferay.Language.get('preview-page-template')}
					</ClayModal.Header>

					<ClayModal.Body className="c-p-0">
						<PreviewModalContent
							addLayoutURL={addLayoutURL}
							entryIndex={entryIndex}
							getLayoutPageTemplateEntryListURL={
								getLayoutPageTemplateEntryListURL
							}
							initialLayoutPageTemplateEntryId={
								layoutPageTemplateEntryId
							}
							layoutPageTemplateEntryList={
								layoutPageTemplateEntryList
							}
							onPreviewOpenChange={onPreviewOpenChange}
							setEntryIndex={setEntryIndex}
							setLayoutPageTemplateEntryList={
								setLayoutPageTemplateEntryList
							}
							updateEntryIndex={updateEntryIndex}
						/>
					</ClayModal.Body>
				</ClayModal>
			)}
		</>
	);
}

type LayoutPageTemplateEntry = {
	layoutPageTemplateEntryId: string;
	name: string;
	previewLayoutURL: string;
};

type LayoutPageTemplateEntryList = LayoutPageTemplateEntry[];

interface IPreviewModalContentProps {
	addLayoutURL: string;
	entryIndex: number;
	getLayoutPageTemplateEntryListURL: string;
	initialLayoutPageTemplateEntryId: string;
	layoutPageTemplateEntryList: LayoutPageTemplateEntry[] | null;
	onPreviewOpenChange: (open: boolean) => void;
	setEntryIndex: (index: number) => void;
	setLayoutPageTemplateEntryList: (
		layoutPageTemplateEntries: LayoutPageTemplateEntry[]
	) => void;
	updateEntryIndex: (direction: 'previous' | 'next') => void;
}

function PreviewModalContent({
	addLayoutURL,
	entryIndex,
	getLayoutPageTemplateEntryListURL,
	initialLayoutPageTemplateEntryId,
	layoutPageTemplateEntryList,
	onPreviewOpenChange,
	setEntryIndex,
	setLayoutPageTemplateEntryList,
	updateEntryIndex,
}: IPreviewModalContentProps) {
	const iframeRef =
		useRef() as React.MutableRefObject<HTMLIFrameElement | null>;

	const layoutPageTemplateEntry = layoutPageTemplateEntryList
		? layoutPageTemplateEntryList[entryIndex]
		: null;

	useEffect(() => {
		fetch(getLayoutPageTemplateEntryListURL)
			.then((response) => response.json())
			.then(
				(
					nextLayoutPageTemplateEntryList: LayoutPageTemplateEntryList
				) => {
					setEntryIndex(
						nextLayoutPageTemplateEntryList.findIndex(
							(entry) =>
								entry.layoutPageTemplateEntryId ===
								initialLayoutPageTemplateEntryId
						)
					);

					setLayoutPageTemplateEntryList(
						nextLayoutPageTemplateEntryList
					);
				}
			)
			.catch((error) => {
				console.error(error);
			});
	}, [
		getLayoutPageTemplateEntryListURL,
		initialLayoutPageTemplateEntryId,
		setEntryIndex,
		setLayoutPageTemplateEntryList,
	]);

	if (!layoutPageTemplateEntryList || !layoutPageTemplateEntry) {
		return null;
	}

	return (
		<div className="bg-dark d-flex flex-column h-100 layout-page-template-entry-preview-modal">
			<div className="bg-white c-p-3 d-flex justify-content-end">
				<ClayButton
					onClick={() => {
						onPreviewOpenChange(false);

						openModal({
							disableAutoClose: true,
							height: '60vh',
							id: 'addLayoutDialog',
							size: 'md',
							title: Liferay.Language.get('add-page'),
							url: createPortletURL(addLayoutURL, {
								layoutPageTemplateEntryId:
									layoutPageTemplateEntry.layoutPageTemplateEntryId,
							}),
						});
					}}
				>
					{Liferay.Language.get('create-page-from-this-template')}
				</ClayButton>
			</div>

			<div className="align-items-center d-flex flex-grow-1 flex-row">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('go-to-previous-template')}
					className="btn-xl c-ml-1 text-white"
					displayType="unstyled"
					onClick={() => updateEntryIndex('previous')}
					symbol="angle-left"
				/>

				<iframe
					className="align-self-stretch border-0 flex-grow-1"
					onLoad={() => {
						const style = {
							cursor: 'not-allowed',
							height: '100%',
							left: '0',
							position: 'fixed',
							top: '0',
							width: '100%',
							zIndex: '100000',
						};

						if (iframeRef.current) {
							const overlay = document.createElement('div');

							const keys = Object.keys(
								style
							) as (keyof typeof style)[];

							keys.forEach((key) => {
								overlay.style[key] = style[key];
							});

							iframeRef.current.removeAttribute('style');
							iframeRef.current.contentDocument?.body.append(
								overlay
							);
						}
					}}
					ref={(ref) => {
						iframeRef.current = ref;

						iframeRef.current?.setAttribute('inert', '');
					}}
					src={layoutPageTemplateEntry.previewLayoutURL}
				/>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('go-to-next-template')}
					className="btn-xl c-mr-1 text-white"
					displayType="unstyled"
					onClick={() => updateEntryIndex('next')}
					symbol="angle-right"
				/>
			</div>

			<header className="bg-secondary-d2 c-p-3 d-flex text-light">
				<p className="c-m-0 flex-grow-1 text-center">
					{layoutPageTemplateEntry.name}
				</p>

				<p className="c-m-0">
					{Liferay.Util.sub(
						Liferay.Language.get('x-of-x'),
						entryIndex + 1,
						layoutPageTemplateEntryList.length
					)}
				</p>
			</header>
		</div>
	);
}
