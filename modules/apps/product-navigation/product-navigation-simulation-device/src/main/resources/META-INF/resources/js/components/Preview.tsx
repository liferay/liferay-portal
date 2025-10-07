/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {useEventListener} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {debounce} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {SIZES, ScreenSize, Size} from '../constants/sizes';
import {useCustomSize} from '../contexts/CustomSizeContext';

interface IPreviewProps {
	activeSize: Size;
	open: boolean;
	previewRef: React.RefObject<HTMLDivElement>;
}

export default function Preview({activeSize, open, previewRef}: IPreviewProps) {
	const [segmentMessage, setSegmentMessage] = useState<string | null>(null);
	const [size, setSize] = useState<ScreenSize | undefined>(
		activeSize.screenSize
	);
	const customSize = useCustomSize();

	const previewWrapperRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		Liferay.component('SimulationPreview', {
			setMessage: setSegmentMessage,
		});

		return () => {
			Liferay.destroyComponent('SimulationPreview');
		};
	}, []);

	const updatePreview = useCallback(() => {
		if (!open || !previewWrapperRef.current) {
			return;
		}

		setSize(
			activeSize.id === SIZES.autosize.id
				? {
						height:
							previewWrapperRef.current.getBoundingClientRect()
								.height - 6,
						width: previewWrapperRef.current.getBoundingClientRect()
							.width,
					}
				: activeSize.screenSize
		);
	}, [activeSize.id, activeSize.screenSize, open]);

	useEffect(() => {
		updatePreview();
	}, [activeSize, updatePreview]);

	const handleWindowResize = debounce(() => {
		updatePreview();
	}, 250);

	// @ts-ignore

	useEventListener('resize', handleWindowResize, false, window);

	const handleIframeLoad = (
		event: React.SyntheticEvent<HTMLIFrameElement>
	) => {
		const iframe = event.target as HTMLIFrameElement;

		const iframeWin = iframe.contentWindow;
		const iframeDoc = iframeWin?.document;

		iframeDoc?.addEventListener('click', (event) => {
			const target = event.target as HTMLElement;

			const link = target.closest('a');

			if (link && link.href) {
				event.preventDefault();
			}
		});

		(iframeWin as any)?.Liferay.on('beforeNavigate', (event: any) => {
			event.preventDefault();
			event.originalEvent.preventDefault();
		});
	};

	if (!open) {
		return null;
	}

	return (
		<div
			className="d-flex flex-column simulation-preview"
			ref={previewWrapperRef}
		>
			{segmentMessage && (
				<ClayAlert
					className="c-m-3"
					displayType="info"
					title={`${Liferay.Language.get('info')}:`}
				>
					{segmentMessage}
				</ClayAlert>
			)}

			<div
				className={classNames(
					'device position-absolute align-self-center',
					activeSize.cssClass,
					{
						'device--with-alert': segmentMessage,
						'resizable': activeSize.id === SIZES.custom.id,
					}
				)}
				ref={previewRef}
				style={activeSize.id === SIZES.custom.id ? customSize : size}
			>
				<iframe
					className="border-0 h-100 w-100"
					onLoad={handleIframeLoad}
					src={createIframeURL()}
					title={Liferay.Language.get('simulation-preview')}
				/>
			</div>
		</div>
	);
}

Preview.propTypes = {
	activeSize: PropTypes.object.isRequired,
	previewRef: PropTypes.object.isRequired,
};

function createIframeURL() {
	const url = new URL(location.href);
	const searchParams = new URLSearchParams(url.search);

	if (searchParams.has('segmentsExperienceId')) {
		searchParams.delete('segmentsExperienceId');
	}

	searchParams.append('p_l_mode', 'preview');

	return `${url.origin}${url.pathname}?${searchParams.toString()}`;
}
