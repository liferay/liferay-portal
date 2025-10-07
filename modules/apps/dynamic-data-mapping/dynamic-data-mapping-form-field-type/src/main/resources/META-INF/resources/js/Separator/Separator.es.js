/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import React, {useEffect, useRef} from 'react';

const Separator = ({name, style, ...otherProps}) => {
	const elRef = useRef(null);

	useEffect(() => {
		if (elRef.current) {

			// The style is a string, to avoid creating a normalizer to generate compatibility
			// with React, we can just add the raw value in the attribute, we don't need to
			// worry about XSS here because it won't go to the server just for printing
			// on the screen.

			elRef.current.setAttribute('style', style);
		}
	}, [style]);

	return (
		<FieldBase
			{...otherProps}
			editOnlyInDefaultLanguage={false}
			name={name}
		>
			<hr className="separator" ref={elRef} />

			<input name={name} type="hidden" />
		</FieldBase>
	);
};

export default Separator;
