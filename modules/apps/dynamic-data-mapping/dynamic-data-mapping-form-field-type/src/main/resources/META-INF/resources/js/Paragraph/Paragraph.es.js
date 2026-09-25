/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import 'ckeditor5/ckeditor5.css';

import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import React from 'react';

const Paragraph = ({name, text, ...otherProps}) => (
	<FieldBase {...otherProps} name={name} text={text}>
		<div
			className="form-group liferay-ddm-form-field-paragraph"
			data-field-name={name}
		>
			<div
				className="ck-content liferay-ddm-form-field-paragraph-text"
				dangerouslySetInnerHTML={{
					__html: text,
				}}
			/>
		</div>
	</FieldBase>
);

export default Paragraph;
