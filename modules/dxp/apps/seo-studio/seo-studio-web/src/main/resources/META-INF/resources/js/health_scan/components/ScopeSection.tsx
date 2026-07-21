/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayList from '@clayui/list';
import ClayPanel from '@clayui/panel';
import React from 'react';

import {ENGINE_DESCRIPTORS} from '../constants';
import {EngineConfig, EngineKey, HealthScanConfig} from '../types';
import EngineSection from './EngineSection';

interface Props {
	engines: HealthScanConfig['engines'];
	onChange: (key: EngineKey, config: EngineConfig) => void;
}

export default function ScopeSection({engines, onChange}: Props) {
	return (
		<div className="sheet-section">
			<ClayPanel
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('scope')}
				displayType="unstyled"
			>
				<ClayList className="list-group-flush">
					{ENGINE_DESCRIPTORS.map(({key, label}) => (
						<EngineSection
							config={engines[key]}
							engineKey={key}
							idPrefix={`seo-studio-${key}`}
							key={key}
							label={label}
							onChange={(config) => onChange(key, config)}
						/>
					))}
				</ClayList>
			</ClayPanel>
		</div>
	);
}
