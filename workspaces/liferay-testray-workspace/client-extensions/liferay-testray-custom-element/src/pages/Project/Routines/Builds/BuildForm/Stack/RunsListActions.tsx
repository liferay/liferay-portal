/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayLayout from '@clayui/layout';

import i18n from '../../../../../../i18n';
import {Fields} from './RunsList';

import type {UseFieldArrayAppend} from 'react-hook-form';

type RunsListActionsProps = {
	append: UseFieldArrayAppend<any>;
	field: Fields;
	index: number;
	remove: (index: number) => void;
};

const RunsListActions: React.FC<RunsListActionsProps> = ({
	append,
	field,
	index,
	remove,
}) => {
	return (
		<ClayLayout.Col className="d-flex justify-content-end">
			{!field.disabled && (
				<ClayButtonWithIcon
					aria-label={i18n.translate('add')}
					displayType="secondary"
					onClick={() => append({...field, runId: undefined})}
					symbol="plus"
				/>
			)}

			<ClayButtonWithIcon
				aria-label={i18n.translate('delete')}
				className="ml-1"
				displayType="secondary"
				onClick={() => remove(index)}
				symbol="hr"
			/>
		</ClayLayout.Col>
	);
};

export default RunsListActions;
