/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Rest from '../../core/Rest';
import yupSchema from '../../schema/yup';
import {TestrayCaseDetail} from './types';

type CaseDetail = typeof yupSchema.caseDetail.__outputType;

class TestrayCaseDetailImpl extends Rest<CaseDetail, TestrayCaseDetail> {
	constructor() {
		super({
			adapter: ({dueStatus, id, name}) => ({
				dueStatus,
				id,
				name,
			}),
			fields: 'id,name,dueStatus',
			nestedFields: '',
			transformData: (testrayCaseDetail) => {
				return {
					...testrayCaseDetail,
				};
			},
			uri: 'casedetails',
		});
	}
}

export const testrayCaseDetailImpl = new TestrayCaseDetailImpl();
