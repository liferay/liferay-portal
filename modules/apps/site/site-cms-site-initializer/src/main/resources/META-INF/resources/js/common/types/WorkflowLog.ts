/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface WorkflowLog {
	auditPerson: {
		additionalName: string;
		contentType: string;
		familyName: string;
		givenName: string;
		id: number;
		image: string;
		name: string;
		profileURL: string;
	};
	dateCreated: string;
	id: number;
	person: {
		additionalName: string;
		contentType: string;
		familyName: string;
		givenName: string;
		id: number;
		image: string;
		name: string;
		profileURL: string;
	};
	role: {
		id: number;
	};
	state: string;
	stateLabel: string;
	type: string;
	workflowTaskId: number;
}
