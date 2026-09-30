/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert, {DisplayType} from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {preventIframeNavigation} from '@liferay/layout-js-components-web';
import React from 'react';

import PreviewSelectors from './PreviewSelectors';
import useIframeLoad from './useIframeLoad';
import usePreviewState from './usePreviewState';

export default function PreviewBody({
	getPreviewDataURL,
	isContentEdited,
	languageId,
}: {
	getPreviewDataURL: string;
	isContentEdited: boolean;
	languageId: Liferay.Language.Locale;
}) {
	const {
		displayPageTemplates,
		isDisplayPageTemplatesListEmpty,
		isExternalURL,
		previewURL,
		setExternalURL,
		...selectorProps
	} = usePreviewState(getPreviewDataURL, languageId);

	const {
		handleIframeLoad,
		iframeError,
		iframeKey,
		isIframeLoading,
		reloadIframe,
	} = useIframeLoad(previewURL, isExternalURL);

	const previewAlert = getPreviewAlert({
		iframeError,
		isDisplayPageTemplatesListEmpty,
	});

	return (
		<>
			<div className="border-bottom c-gap-3 d-flex flex-wrap mb-0 p-3">
				<PreviewSelectors
					{...selectorProps}
					displayPageTemplates={displayPageTemplates}
					isExternalURL={isExternalURL}
					onBlurExternalURLInput={setExternalURL}
					onReloadExternalURLInput={reloadIframe}
					previewURL={previewURL}
					showPreviewInNewTabLink
				/>
			</div>

			{isContentEdited && previewURL ? (
				<ClayAlert
					displayType="info"
					title={Liferay.Language.get('info')}
					variant="stripe"
				>
					{Liferay.Language.get(
						'you-have-unsaved-changes.-save-as-draft-or-publish-to-update-this-preview'
					)}
				</ClayAlert>
			) : null}

			{previewAlert ? (
				<ClayAlert
					className="m-3"
					displayType={previewAlert.displayType}
					title={previewAlert.title}
				>
					{previewAlert.message}

					{previewAlert.Footer ? (
						<previewAlert.Footer onClick={reloadIframe} />
					) : null}
				</ClayAlert>
			) : (
				<div className="align-items-center content-editor__preview__content d-flex position-relative">
					{previewURL ? (
						<>
							<iframe
								className="border-0 d-block h-100 w-100"
								key={iframeKey}
								onLoad={(event) => {
									handleIframeLoad();
									preventIframeNavigation(event);
								}}
								src={previewURL}
								title={Liferay.Language.get('preview')}
							/>

							{isIframeLoading ? (
								<div className="align-items-center bg-white d-flex h-100 position-absolute w-100">
									<ClayLoadingIndicator
										displayType="primary"
										shape="squares"
										size="lg"
									/>
								</div>
							) : null}
						</>
					) : (
						<ClayEmptyState
							className="mt-0"
							description={Liferay.Language.get(
								'select-a-channel-and-save-as-draft-or-publish-to-see-your-changes-here'
							)}
							imgSrc={`${Liferay.ThemeDisplay.getPathContext()}/o/fragment-collection-contributor-inputs/drag_drop_illustration.svg`}
							imgSrcReducedMotion={`${Liferay.ThemeDisplay.getPathContext()}/o/fragment-collection-contributor-inputs/drag_drop_illustration.svg`}
							small
							title={Liferay.Language.get('nothing-to-show-yet')}
						/>
					)}
				</div>
			)}
		</>
	);
}

function getPreviewAlert({
	iframeError,
	isDisplayPageTemplatesListEmpty,
}: {
	iframeError: boolean;
	isDisplayPageTemplatesListEmpty: boolean;
}): {
	Footer?: React.ComponentType<{onClick: () => void}>;
	displayType: DisplayType;
	message: string;
	title: string;
} | null {
	if (iframeError) {
		return {
			Footer: ({onClick}: {onClick: () => void}) => (
				<ClayAlert.Footer>
					<ClayButton displayType="warning" onClick={onClick} small>
						<ClayIcon className="c-mr-2" symbol="reload" />

						{Liferay.Language.get('refresh')}
					</ClayButton>
				</ClayAlert.Footer>
			),
			displayType: 'warning',
			message: Liferay.Language.get('we-could-not-load-the-preview'),
			title: Liferay.Language.get('warning'),
		};
	}

	if (isDisplayPageTemplatesListEmpty) {
		return {
			displayType: 'info',
			message: Liferay.Language.get(
				'no-display-page-templates-are-available-for-preview-in-this-channel'
			),
			title: Liferay.Language.get('info'),
		};
	}

	return null;
}
