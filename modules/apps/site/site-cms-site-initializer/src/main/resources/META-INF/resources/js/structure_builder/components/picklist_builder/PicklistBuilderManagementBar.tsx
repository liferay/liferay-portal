/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from '@liferay/layout-js-components-web';
import {ManagementToolbar, openToast} from 'frontend-js-components-web';
import React, {useMemo} from 'react';

import PicklistService from '../../../services/PicklistService';
import {useStaleCache} from '../../contexts/CacheContext';
import {
	useDeletedOptions,
	useErc,
	useId,
	useName,
	useOptions,
	useSetId,
} from '../../contexts/PicklistBuilderContext';
import focusInvalidElement from '../../utils/focusInvalidElement';
import AsyncButton from '../AsyncButton';
import ManagementBar from '../ManagementBar';

export default function PicklistBuilderManagementBar() {
	const deletedOptions = useDeletedOptions();
	const erc = useErc();
	const id = useId();
	const name = useName();
	const options = useOptions();
	const setId = useSetId();
	const staleCache = useStaleCache();

	const localizedName = useMemo(
		() => name[Liferay.ThemeDisplay.getDefaultLanguageId()],
		[name]
	);

	const onSave = async () => {
		if (deletedOptions) {
			if (
				!(await openConfirmModal({
					buttonLabel: Liferay.Language.get('Save'),
					center: true,
					status: 'danger',
					text: Liferay.Language.get(
						'you-deleted-one-or-more-options-from-the-picklist'
					),
					title: Liferay.Language.get('save-picklist-changes'),
				}))
			) {
				return;
			}
		}

		try {
			if (!localizedName || !erc) {
				focusInvalidElement();

				return;
			}

			const params = {
				erc,
				name,
				options,
			};

			if (!id) {
				const picklist = await PicklistService.createPicklist(params);

				setId(picklist.id);
			}
			else {
				await PicklistService.updatePicklist({...params, id});
			}

			staleCache('picklists');

			openToast({
				message: Liferay.Util.sub(
					Liferay.Language.get('x-was-saved-successfully'),
					localizedName
				),
				type: 'success',
			});
		}
		catch (error) {
			const {message} = error as Error;

			openToast({
				message:
					message ||
					Liferay.Language.get(
						'an-unexpected-error-occurred-while-saving-or-publishing-the-picklist'
					),
				type: 'danger',
			});
		}
	};

	return (
		<ManagementBar title={Liferay.Language.get('new-picklist')}>
			<ManagementToolbar.Item>
				<AsyncButton
					displayType="primary"
					label={Liferay.Language.get('save')}
					onClick={onSave}
				/>
			</ManagementToolbar.Item>
		</ManagementBar>
	);
}
