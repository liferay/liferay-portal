/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import React, {useState} from 'react';

import type {ContentSample as ContentSampleType} from '../types/ContentModel';

interface IProps {
	defaultExpanded?: boolean;
	sample: ContentSampleType;
}

export default function ContentSample({defaultExpanded, sample}: IProps) {
	const [expanded, setExpanded] = useState(!!defaultExpanded);

	return (
		<div className="content-site-generator__sample">
			<ClayButton
				aria-expanded={expanded}
				className="content-site-generator__sample-header"
				displayType="unstyled"
				onClick={() => setExpanded(!expanded)}
			>
				<span className="font-weight-semi-bold">{sample.title}</span>

				<ClayIcon
					spritemap={Liferay.Icons.spritemap}
					symbol={expanded ? 'angle-down' : 'angle-right'}
				/>
			</ClayButton>

			{expanded && (
				<div className="content-site-generator__sample-body">
					<dl className="content-site-generator__sample-fields">
						{sample.fields.map((field) => (
							<React.Fragment key={field.label}>
								<dt>{field.label}</dt>

								<dd>{field.value}</dd>
							</React.Fragment>
						))}
					</dl>

					{!!sample.chips.length && (
						<div className="content-site-generator__sample-chips">
							{sample.chips.map((chip) => (
								<ClayLabel displayType="secondary" key={chip}>
									{chip}
								</ClayLabel>
							))}
						</div>
					)}
				</div>
			)}
		</div>
	);
}
