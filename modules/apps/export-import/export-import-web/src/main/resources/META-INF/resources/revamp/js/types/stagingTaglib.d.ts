/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

declare module 'staging-taglib' {
	import {ComponentType} from 'react';

	export interface ExportReportEntriesModalProps {
		closeModal: () => void;
		filename: string;
		importProcessId: string;
	}

	export const ExportReportEntriesModal: ComponentType<ExportReportEntriesModalProps>;

	export interface PagesTreeProps {
		config: {
			changeItemSelectionURL: string;
			loadMoreItemsURL: string;
			maxPageSize: number;
			namespace: string;
		};
		groupId: string;
		items: unknown[];
		portletNamespace: string;
		privateLayout: boolean;
		selectedLayoutIds: string[];
		treeId: string;
	}

	export const PagesTree: ComponentType<PagesTreeProps>;
}
