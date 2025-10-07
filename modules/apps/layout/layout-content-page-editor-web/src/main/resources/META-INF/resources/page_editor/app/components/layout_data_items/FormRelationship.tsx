/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {FormRelationshipLayoutDataItem} from '../../../types/layout_data/FormRelationshipLayoutDataItem';
import {Container} from '../../js-index';

const FormRelationship = React.forwardRef<
	HTMLDivElement,
	{children: React.ReactNode; item: FormRelationshipLayoutDataItem}
>((props, ref) => <Container {...props} ref={ref} />);

export default FormRelationship;
