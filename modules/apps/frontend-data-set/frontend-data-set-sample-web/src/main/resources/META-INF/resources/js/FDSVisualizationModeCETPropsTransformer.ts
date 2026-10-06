/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {IFrontendDataSetProps} from '@liferay/frontend-data-set-web';

interface IFDSVisualizationModeCET {
	externalReferenceCode: string;
	icon: string;
	label: string;
	url: string;
}

export default function propsTransformer({
	additionalProps,
	views,
	...otherProps
}: IFrontendDataSetProps & {
	additionalProps?: {fdsVisualizationModeCET?: IFDSVisualizationModeCET};
}): IFrontendDataSetProps {
	const fdsVisualizationModeCET = additionalProps?.fdsVisualizationModeCET;

	if (!fdsVisualizationModeCET) {
		return {...otherProps, views};
	}

	return {
		...otherProps,
		views: [
			...views,
			{
				contentRenderer: 'clientExtension',
				contentRendererClientExtension: true,
				contentRendererModuleURL: `default from ${fdsVisualizationModeCET.url}`,
				label: fdsVisualizationModeCET.label,
				name: `clientExtension-${fdsVisualizationModeCET.externalReferenceCode}`,
				schema: {
					date: 'date',
					description: 'description',
					title: 'title',
				},
				thumbnail: fdsVisualizationModeCET.icon,
			},
		],
	};
}
