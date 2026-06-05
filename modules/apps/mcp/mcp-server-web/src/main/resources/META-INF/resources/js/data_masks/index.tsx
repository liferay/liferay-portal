/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import React, {useState} from 'react';

import {ConfirmModal} from '../shared/ConfirmModal';
import {DataMaskForm} from './DataMaskForm';
import {DataMaskList} from './DataMaskList';
import {duplicateDataMask} from './duplicateDataMask';
import {Mode} from './types';
import {useDataMaskDeletion} from './useDataMaskDeletion';

export function DataMasks() {
	const [mode, setMode] = useState<Mode>({kind: 'list'});
	const {modalProps, requestDelete} = useDataMaskDeletion();

	if (mode.kind === 'form') {
		return (
			<DataMaskForm
				dataMask={mode.dataMask}
				onCancel={() => setMode({kind: 'list'})}
				onSaved={(saved) => {
					openToast({
						message: Liferay.Util.sub(
							Liferay.Language.get('x-was-saved-successfully'),
							saved.name
						),
						type: 'success',
					});

					setMode({kind: 'list'});
				}}
				readOnly={mode.readOnly}
			/>
		);
	}

	return (
		<>
			{modalProps && <ConfirmModal {...modalProps} />}

			<DataMaskList
				onCreate={() => setMode({dataMask: null, kind: 'form'})}
				onDelete={requestDelete}
				onDuplicate={duplicateDataMask}
				onEdit={(dataMask) => setMode({dataMask, kind: 'form'})}
				onView={(dataMask) =>
					setMode({dataMask, kind: 'form', readOnly: true})
				}
			/>
		</>
	);
}
