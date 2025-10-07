/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SVGProps} from 'react';
import {JSX} from 'react/jsx-runtime';

const ActivationIconGray = (
	props: JSX.IntrinsicAttributes & SVGProps<SVGSVGElement>
) => (
	<svg
		fill="none"
		height="16"
		viewBox="0 0 16 16"
		width="16"
		xmlns="http://www.w3.org/2000/svg"
		{...props}
	>
		<mask
			height="16"
			id="mask0_248_6345"
			maskUnits="userSpaceOnUse"
			style={{maskType: 'alpha'}}
			width="16"
			x="0"
			y="0"
		>
			<path
				d="M13.2406 2.3375L7.94375 8.52812L5.70625 6.2875C4.79688 5.38438 3.38437 6.8 4.29375 7.70312L7.29375 10.7063C7.475 10.8875 7.725 11 8 11C8.30313 11 8.57501 10.8625 8.75938 10.65L14.7594 3.6375C15.5906 2.66875 14.0719 1.37813 13.2406 2.3375Z"
				fill="#6B6C7E"
			/>

			<path
				d="M8 16C3.5875 16 0 12.4125 0 8C0 3.5875 3.5875 0 8 0C8.85312 0 9.69375 0.134375 10.4969 0.396875C11.7688 0.8125 11.1219 2.70625 9.875 2.29688C9.27188 2.1 8.64062 2 8 2C4.69063 2 2 4.69063 2 8C2 11.3094 4.69063 14 8 14C11.3094 14 14 11.3094 14 8C14 7.77187 13.9875 7.55 13.9625 7.33125C13.8844 5.975 15.7875 5.8375 15.95 7.1125C15.9812 7.40313 16 7.7 16 8.00313C16 12.4125 12.4125 16 8 16Z"
				fill="#6B6C7E"
			/>
		</mask>

		<g mask="url(#mask0_248_6345)">
			<rect fill="#282934" height="16" width="16" />
		</g>
	</svg>
);

export {ActivationIconGray};
