/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormView as DataEngineFormView} from 'data-engine-js-components-web';
import React from 'react';

import DefaultPageHeader from './components/DefaultPageHeader';

const FormView: React.FC<{children?: React.ReactNode | undefined} & IProps> = ({
	description,
	portletNamespace,
	title,
	...otherProps
}) => {
	return (
		<>
			{title && (
				<DefaultPageHeader
					description={description}
					hideBackButton
					portletNamespace={portletNamespace}
					title={title}
				/>
			)}

			<DataEngineFormView
				{...otherProps}
				portletNamespace={portletNamespace}
				title={title}
			/>
		</>
	);
};

export default FormView;

interface IProps {
	description?: string;
	portletNamespace?: string;
	title?: string;
}
