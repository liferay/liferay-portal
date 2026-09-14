/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {memo} from 'react';

import {EditorSection} from '../chrome/EditorSection';
import {useEditorId} from '../chrome/instance';
import {FilterDefs} from '../imaging/FilterDefs';
import {LoadedImage} from '../imaging/loadImage';
import {EditorAction} from '../state/editorReducer';
import {DEFAULT_ADJUSTMENTS, FilterPreset} from '../state/types';
import {PresetGallery} from './PresetGallery';

const FILTER_LABELS: Record<FilterPreset, string> = {
	bleach: Liferay.Language.get('bleach'),
	cool: Liferay.Language.get('cool'),
	crossprocess: Liferay.Language.get('cross-process'),
	cyanotype: Liferay.Language.get('cyanotype'),
	fade: Liferay.Language.get('fade'),
	grayscale: Liferay.Language.get('grayscale'),
	invert: Liferay.Language.get('inverted'),
	matte: Liferay.Language.get('matte'),
	noir: Liferay.Language.get('noir'),
	none: Liferay.Language.get('none'),
	polaroid: Liferay.Language.get('instant'),
	posterize: Liferay.Language.get('posterize'),
	sepia: Liferay.Language.get('sepia'),
	solarize: Liferay.Language.get('solarize'),
	splittone: Liferay.Language.get('split-tone'),
	tealorange: Liferay.Language.get('teal-and-orange'),
	technicolor: Liferay.Language.get('technicolor'),
	vintage: Liferay.Language.get('vintage'),
	vivid: Liferay.Language.get('vivid'),
	warm: Liferay.Language.get('warm'),
};

interface Props {
	dispatch: (action: EditorAction) => void;
	filter: FilterPreset;
	image: LoadedImage;
	onAnnounce: (message: string) => void;

	presets: FilterPreset[];
}

function FilterGalleryCards({
	dispatch,
	filter,
	image,
	onAnnounce,
	presets,
}: Props) {
	const eid = useEditorId();

	return (
		<EditorSection
			title={Liferay.Language.get('filters')}
			titleId={eid('filters-panel-title')}
		>
			<PresetGallery
				idPrefix={eid('filter')}
				items={presets}
				label={(preset) => FILTER_LABELS[preset]}
				legend={Liferay.Language.get('filters')}
				onSelect={(preset) => {
					dispatch({filter: preset, type: 'set-filter'});

					onAnnounce(
						sub(
							Liferay.Language.get('filter-set-to-x'),
							FILTER_LABELS[preset]
						)
					);
				}}
				preview={(preset) => (
					<svg
						aria-hidden="true"
						className="editor-preset-thumb"
						height={48}
						viewBox="0 0 72 48"
						width={72}
					>
						<defs>
							<FilterDefs
								adjustments={DEFAULT_ADJUSTMENTS}
								filter={preset}
								id={eid(`filter-thumb-${preset}`)}
							/>
						</defs>

						<image
							filter={
								preset === 'none'
									? undefined
									: `url(#${eid(`filter-thumb-${preset}`)})`
							}
							height={48}
							href={image.thumbUrl}
							preserveAspectRatio="xMidYMid slice"
							width={72}
						/>
					</svg>
				)}
				selected={filter}
			/>
		</EditorSection>
	);
}

export const FilterGallery = memo(FilterGalleryCards);
